package com.zhangguoyu.app;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.ViewGroup;
import com.zhangguoyu.demo.actionbar.R;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zhangguoyu on 13-5-23.
 */
public class CBlockManager {

    private static final String LOG_TAG = CBlockManager.class.getSimpleName();

    private static class Instance {
        static HashMap<Integer, CBlockManager> MAP = new HashMap<Integer, CBlockManager>();
    }

    private CBlockActivity mActivity = null;
    private ArrayList<CBlockInfo> mBlockInfoList = null;
    private static final HashMap<String, Class<?>> sClassMap = new HashMap<String, Class<?>>();

    private CBlock mMainBlock = null;
    private Bundle mSavedBundle = null;

    private int mCurrentState = CBlock.NO_STATE;
    private SavedState mSavedState = null;
    private ViewGroup mFrame = null;

    public static CBlockManager newInstance(CBlockActivity activity) {
        CBlockManager bm = null;
        if (Instance.MAP.containsKey(activity.hashCode())) {
            final CBlockManager old = Instance.MAP.get(activity.hashCode());
            if (old != null) {
                bm = old;
            }
        }
        if (bm == null) {
            bm = new CBlockManager(activity);
            Instance.MAP.put(activity.hashCode(), bm);
        }
        return bm;
    }

    private CBlockManager(CBlockActivity activity) {
        mActivity = activity;
        mFrame = (ViewGroup) activity.findViewById(R.id.main_frame);
    }

    public void parseBlockMainfestFromXml(int xmlResId) {

        final String TAG_MANIFEST = "manifest";
        final String TAG_BLOCK = "block";

        XmlResourceParser parser = mActivity.getResources().getXml(xmlResId);
        AttributeSet attrs = Xml.asAttributeSet(parser);

        try {
            int eventType = parser.getEventType();
            String tagName = null;
            do {
                if (eventType == XmlPullParser.START_TAG) {
                    tagName = parser.getName();
                    if (tagName.equals(TAG_MANIFEST)) {
                        eventType = parser.next();
                        break;
                    }

                    throw new RuntimeException("Resolve manifest xml "+xmlResId+" exception " + tagName);
                }
                eventType = parser.next();
            } while (eventType != XmlPullParser.END_DOCUMENT);

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        if (tagName.equals(TAG_BLOCK)) {
                            if (mBlockInfoList == null) {
                                mBlockInfoList = new ArrayList<CBlockInfo>();
                            }
                            CBlockInfo info = new CBlockInfo();
                            TypedArray a = mActivity.obtainStyledAttributes(attrs, R.styleable.ManifestBlock);
                            info.id = a.getResourceId(R.styleable.ManifestBlock_id, CBlock.NO_ID);
                            info.layoutResId = a.getResourceId(R.styleable.ManifestBlock_layout, 0);
                            info.tag = a.getString(R.styleable.ManifestBlock_tag);
                            info.className = a.getString(R.styleable.ManifestBlock_name);
                            info.title = a.getText(R.styleable.ManifestBlock_title);
                            info.isMain = a.getBoolean(R.styleable.ManifestBlock_isMain, false);
                            final int orientation = a.getInt(R.styleable.ManifestBlock_orientation, 3);
                            a.recycle();

                            if (info.isMain) {
                                mMainBlock = buildMainBlock(info.className);
                                mActivity.attachMainBlock(mMainBlock);
                                mMainBlock.setContainer(mFrame);
                            }

                            mBlockInfoList.add(info);
                        }

                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public CBlockInfo findBlockInfoWithClassName(String name) {
        if (name == null || mBlockInfoList == null) {
            return null;
        }

        final int N = mBlockInfoList.size();
        for (int i=0; i<N; i++) {
            final CBlockInfo info = mBlockInfoList.get(i);
            if (info != null) {
                final String className = info.className;
                if (TextUtils.isEmpty(className)) {
                    continue;
                }

                if (className.equals(name)) {
                    return info;
                }
            }
        }
        return null;
    }

    public CBlockInfo findBlockInfoById(int id) {
        if (id == CBlock.NO_ID || mBlockInfoList == null) {
            return null;
        }

        final int N = mBlockInfoList.size();
        for (int i=0; i<N; i++) {
            final CBlockInfo info = mBlockInfoList.get(i);
            if (info != null) {
                final int infoId = info.id;
                if (infoId == CBlock.NO_ID) {
                    continue;
                }

                if (infoId == id) {
                    return info;
                }
            }
        }
        return null;
    }

    public CBlockInfo findBlockInfoWithTag(Object tag) {
        if (tag == null || mBlockInfoList == null) {
            return null;
        }

        final int N = mBlockInfoList.size();
        for (int i=0; i<N; i++) {
            final CBlockInfo info = mBlockInfoList.get(i);
            if (info != null) {
                final Object infoTag = info.tag;
                if (infoTag == null) {
                    continue;
                }

                if (infoTag.equals(tag)) {
                    return info;
                }
            }
        }
        return null;
    }

    public CBlock buildMainBlock(String className) {
        CBlock main = null;
        if (!TextUtils.isEmpty(className)) {
            try {
                Class<?> blockClass = sClassMap.get(className);
                if (blockClass == null) {
                    blockClass = mActivity.getClassLoader().loadClass(className);
                    sClassMap.put(className, blockClass);
                }

                main = (CBlock) blockClass.newInstance();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return main;
    }

    public CBlock getMainBlock() {
        return mMainBlock;
    }

    public int getBlockCount() {
        if (mBlockInfoList != null) {
            return mBlockInfoList.size();
        }
        return 0;
    }

    public CBlockInfo getBlockInfoAt(int i) {
        if (mBlockInfoList != null && i < mBlockInfoList.size()
                && i >= 0) {
            return mBlockInfoList.get(i);
        }
        return null;
    }

    public void dispatchCreate() {
        mCurrentState = CBlock.STATE_CREATE;
        syncStateToBlock(mMainBlock);
    }

    public void dispatchStart() {
        mCurrentState = CBlock.STATE_START;
        syncStateToBlock(mMainBlock);
    }

    public void dispatchResume() {
        mCurrentState = CBlock.STATE_RESUME;
        syncStateToBlock(mMainBlock);
    }

    public void dispatchPause() {
        mCurrentState = CBlock.STATE_START;
        syncStateToBlock(mMainBlock);
    }

    public void dispatchStop() {
        mCurrentState = CBlock.STATE_CREATE;
        syncStateToBlock(mMainBlock);
    }

    public void dispatchDestroy() {
        mCurrentState = CBlock.STATE_INITIALIZING;
        syncStateToBlock(mMainBlock);
    }

    public void dispatchSaveInstanceState(Bundle savedState) {
        if (mMainBlock != null) {
            if (mSavedBundle == null) {
                mSavedBundle = new Bundle();
            }

            mMainBlock.dispatchOnSaveInstanceState(mSavedBundle);
        }
    }

    public void dispatchRestoreInstanceState() {
    }

    void restoreState(Parcelable savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        SavedState ss = (SavedState) savedInstanceState;
        dispatchRestoreInstanceState();
    }

    Parcelable saveState() {
        if (mSavedState == null) {
            mSavedState = new SavedState();
        }
        mSavedState.currState = mCurrentState;
        if (mBlockInfoList != null) {
            final int size = mBlockInfoList.size();
            if (size > 0) {
                mSavedState.blockInfoInstanceStates = new Parcelable[size];
                for (int i=0; i<size; i++) {
                    mSavedState.blockInfoInstanceStates[i] = mBlockInfoList.get(i);
                }
            }
        }
        if (mMainBlock != null) {
            if (mSavedState.blockInstanceState == null) {
                mSavedState.blockInstanceState = new Bundle();
            }
            mMainBlock.performSaveInstanceState(mSavedState.blockInstanceState);
        }
        return mSavedState;
    }

    public static class CBlockInfo implements Parcelable {

        public int id = CBlock.NO_ID;
        public CharSequence title = null;
        public int layoutResId = 0;
        public Object tag = null;
        public String className = null;
        public boolean isMain = false;

        public CBlockInfo() {}

        public CBlockInfo(Parcel in) {
            super();
            id = in.readInt();
            title = in.readString();
            layoutResId = in.readInt();
            tag = in.readValue(getClass().getClassLoader());
            className = in.readString();
            isMain = in.readInt()==1;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(id);
            parcel.writeString(title.toString());
            parcel.writeInt(layoutResId);
            parcel.writeValue(tag);
            parcel.writeString(className);
            parcel.writeInt(isMain?1:0);
        }

        public static final Creator<CBlockInfo> CREATOR = new Creator<CBlockInfo>() {
            @Override
            public CBlockInfo createFromParcel(Parcel parcel) {
                return new CBlockInfo(parcel);
            }

            @Override
            public CBlockInfo[] newArray(int i) {
                return new CBlockInfo[i];
            }
        };
    }

    void syncStateToBlock(CBlock blockForSyncing) {
        syncStateToBlock(blockForSyncing, mCurrentState);
    }

    private void syncStateToBlock(CBlock blockForSyncing, int stateSynced) {
        Log.d(LOG_TAG, "@@@ syncStateToBlock " + blockForSyncing.getState() + ", curr state " + stateSynced);
        //block can not be null.
        if (blockForSyncing == null) {
            return;
        }
        //No need to sync，if the state of block equal with the current state of context
        if (blockForSyncing.getState() == stateSynced) {
            return;
        }

        final int blockState = blockForSyncing.getState();
        if (blockState < stateSynced) { //active process
            switch (blockState) {
                case CBlock.NO_STATE:
                case CBlock.STATE_INITIALIZING:
                    if (stateSynced > CBlock.STATE_INITIALIZING) {
                        //handle the attach from the none
                        blockForSyncing.performAttach(mActivity);
                        //handle the create from the attach
                        blockForSyncing.performCreate(mSavedBundle);
                    }
                case CBlock.STATE_CREATE:
                    if (stateSynced > CBlock.STATE_CREATE) {
                        //handle the start from the create
                        blockForSyncing.performStart();
                    }
                case CBlock.STATE_START:
                    if (stateSynced > CBlock.STATE_START) {
                        //handle the resume from the start
                        blockForSyncing.performResume();
                    }
            }
        } else if (blockState > stateSynced) { //negative process
            switch (blockState) {
                case CBlock.STATE_RESUME:
                    if (stateSynced < CBlock.STATE_RESUME) {
                        //handle the pause from the resume
                        blockForSyncing.performPause();
                    }
                case CBlock.STATE_START:
                    if (stateSynced < CBlock.STATE_START) {
                        //handle the stop from the pause
                        blockForSyncing.performStop();
                    }
                case CBlock.STATE_CREATE:
                    if (stateSynced < CBlock.STATE_CREATE) {
                        //handle the destroy from the stop
                        blockForSyncing.performDestroy();
                    }
                case CBlock.STATE_INITIALIZING:
                    if (stateSynced < CBlock.STATE_INITIALIZING) {
                        //handle the detach from the destroy
                        blockForSyncing.performDetach();
                    }
            }
        }

        blockForSyncing.setState(stateSynced);

    }

    public static class SavedState implements Parcelable {

        Parcelable[] blockInfoInstanceStates = null;
        int currState = 0;
        Bundle blockInstanceState = null;

        SavedState() {}

        SavedState(Parcel in) {
            blockInfoInstanceStates = in.readParcelableArray(getClass().getClassLoader());
            currState = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelableArray(blockInfoInstanceStates, i);
            parcel.writeInt(currState);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
    }

}
