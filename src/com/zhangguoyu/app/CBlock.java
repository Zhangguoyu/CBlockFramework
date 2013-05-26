package com.zhangguoyu.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.zhangguoyu.widget.CBlockingView;
import com.zhangguoyu.widget.CMenu;
import com.zhangguoyu.widget.CMenuItem;

import java.util.ArrayList;
import java.util.List;

public class CBlock {

    private static final String LOG_TAG = CBlock.class.getSimpleName();
	
	static final int FLAG_STATE_BASE = 0x1;

	static final int FLAG_STATE_ON_ATTACH = FLAG_STATE_BASE;
    static final int FLAG_STATE_ON_ATTACH_MASK = FLAG_STATE_ON_ATTACH;

	static final int FLAG_STATE_ON_CREATE = FLAG_STATE_BASE<<1;
    static final int FLAG_STATE_ON_CREATE_MASK =
            FLAG_STATE_ON_ATTACH_MASK | FLAG_STATE_ON_CREATE;

	static final int FLAG_STATE_ON_CREATE_VIEW = FLAG_STATE_BASE<<2;
    static final int FLAG_STATE_ON_CREATE_VIEW_MASK =
            FLAG_STATE_ON_CREATE_MASK | FLAG_STATE_ON_CREATE_VIEW;

	static final int FLAG_STATE_ON_ACTIVITY_CREATE = FLAG_STATE_BASE<<3;
    static final int FLAG_STATE_ON_ACTIVITY_CREATE_MASK =
            FLAG_STATE_ON_CREATE_VIEW_MASK | FLAG_STATE_ON_ACTIVITY_CREATE;

	static final int FLAG_STATE_ON_VIEW_STATE_RESTORE = FLAG_STATE_BASE<<4;
    static final int FLAG_STATE_ON_VIEW_STATE_RESTORE_MASK =
            FLAG_STATE_ON_ACTIVITY_CREATE_MASK | FLAG_STATE_ON_VIEW_STATE_RESTORE;

	static final int FLAG_STATE_ON_START = FLAG_STATE_BASE<<5;
    static final int FLAG_STATE_ON_STARE_MASK =
            FLAG_STATE_ON_VIEW_STATE_RESTORE_MASK | FLAG_STATE_ON_START;

    static final int FLAG_STATE_ON_RESUME = FLAG_STATE_BASE<<6;
    static final int FLAG_STATE_ON_RESUME_MASK =
            FLAG_STATE_ON_STARE_MASK | FLAG_STATE_ON_RESUME;

	static final int FLAG_STATE_ON_PAUSE = FLAG_STATE_BASE<<7;
    static final int FLAG_STATE_ON_PAUSE_MASK =
            FLAG_STATE_ON_RESUME_MASK | FLAG_STATE_ON_PAUSE;

	static final int FLAG_STATE_ON_STOP = FLAG_STATE_BASE<<8;
    static final int FLAG_STATE_ON_STOP_MASK =
            FLAG_STATE_ON_PAUSE_MASK | FLAG_STATE_ON_STOP;

	static final int FLAG_STATE_ON_DESTROY_VIEW = FLAG_STATE_BASE<<9;
    static final int FLAG_STATE_ON_DESTROY_VIEW_MASK =
            FLAG_STATE_ON_STOP_MASK | FLAG_STATE_ON_DESTROY_VIEW;

	static final int FLAG_STATE_ON_DESTROY = FLAG_STATE_BASE<<10;
    static final int FLAG_STATE_ON_DESTROY_MASK =
            FLAG_STATE_ON_DESTROY_VIEW_MASK | FLAG_STATE_ON_DESTROY;

    static final int FLAG_STATE_ON_DETACH = FLAG_STATE_BASE<<11;
    static final int FLAG_STATE_ON_DETACH_MASK =
            FLAG_STATE_ON_DESTROY_MASK | FLAG_STATE_ON_DETACH;

    public static final int NO_ID = View.NO_ID;

    static final int NO_STATE = 0;
    static final int STATE_INITIALIZING = 1;
    static final int STATE_CREATE = 2;
    static final int STATE_START = 3;
    static final int STATE_RESUME = 4;
	
	private CBlockActivity mActivity = null;
	private boolean mViewInflated = false;
	private ViewGroup mContainer = null;
	private LayoutInflater mLayoutInflater = null;
	private View mContentView = null;
	private int mContentLayoutResId = 0;
	
	private List<CBlock> mSubBlocks = null;
    private CBlock mParent = null;
    private CBlock mRoot = null;
    private Object mTarget = null;
    private CBlockFragment mFragment = null;
    private int mId = NO_ID;
    private Bundle mSavedInstanceState = null;
    private Bundle mArgs = null;
    private SavedState mSavedState = null;
    private SparseArray<Parcelable> mSavedViewState = null;
    private CBlockManager mBlockManager = null;
	
	private int mStateFlag = NO_STATE;

    void attachToActivity(CBlockActivity activity) {
        mActivity = activity;
        mBlockManager = activity.getBlockManager();
        mLayoutInflater = activity.getLayoutInflater();
        mStateFlag = CBlock.STATE_INITIALIZING;
    }
	
    void attachToFragment(CBlockFragment fragment) {
        mRoot = this;
        mFragment = fragment;
	}
	
	public void setContentView(int layoutId) {
		mContentLayoutResId = layoutId;
		if (layoutId > 0 && mLayoutInflater != null) {
			setContentView(mLayoutInflater.inflate(layoutId, null));
		}
	}
	
	public void setContentView(View view) {
		mContentView = view;
        attachSubBlocksTraversalInView(mContentView);
        if (view != null) {
            ViewGroup contentParent = (ViewGroup) mContentView.getParent();
            if (contentParent != null) {
                contentParent.removeView(mContentView);
            }
            if (mContainer.getChildCount() > 0) {
                mContainer.removeAllViews();
            }
            mContainer.addView(mContentView);
        }
	}

    private void attachSubBlocksTraversalInView(View view) {
        if (view == null) {
            return;
        }

        if (view instanceof CBlockingView) {
            CBlock block = ((CBlockingView) view).getBlock();
            if (block != this && block != null) {
                block.attachToParent(this);
            }
            return;
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            final int count = group.getChildCount();
            for (int i=0; i<count; i++) {
                final View child = group.getChildAt(i);
                attachSubBlocksTraversalInView(child);
            }
        }
    }

    public void attachToParent(CBlock parent) {
        if (parent == null) {
            return;
        }

        parent.addSubBlock(this);
        mParent = parent;
        mRoot = parent.getRoot();

        attachToActivity(parent.getActivity());
        mArgs = parent.getArguments();

        final int parentState = parent.getState();
        if (parentState > NO_STATE && parentState != mStateFlag) {
            mBlockManager.syncStateToBlock(this);
        }

    }

    public void detachFromParent() {
        if (mParent != null) {
            mParent.removeSubBlock(this);
            mParent = null;
        }

        if (mStateFlag > NO_STATE) {
            switch(mStateFlag) {
                case STATE_RESUME:
                    performPause();
                case STATE_START:
                    performStop();
                case STATE_CREATE:
                    performDestroy();
                    performDetach();
            }
        }

        mRoot = null;
    }
	
	void onAttach(CBlockActivity activity) {
		mActivity = activity;
        mStateFlag = STATE_INITIALIZING;
	}
	
	public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate " + this.getClass().getName());
    }

	public void onStart() {
        Log.d(LOG_TAG, "onStart " + this.getClass().getName());
	}

    public void onResume() {
        Log.d(LOG_TAG, "onResume " + this.getClass().getName());
    }

    public void onPause() {
        Log.d(LOG_TAG, "onPause " + this.getClass().getName());
    }
	
	public void onStop() {
        Log.d(LOG_TAG, "onStop " + this.getClass().getName());
	}

    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy " + this.getClass().getName());
    }

    void onDetach() {
        Log.d(LOG_TAG, "onDetach " + this.getClass().getName());
    }

    public void onLowMemory() {
    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	}

    public void dispatchOnActivityResult(int requestCode, int resultCode, Intent data) {

        onActivityResult(requestCode, resultCode, data);

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.dispatchOnActivityResult(requestCode, resultCode, data);
        }
    }
	
	public void onConfigurationChanged(Configuration newConfig) {
	}

    public void dispatchOnConfigurationChanged(Configuration newConfig) {

        onConfigurationChanged(newConfig);

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.onConfigurationChanged(newConfig);
        }
    }
	
	public void onHiddenChanged(boolean hidden) {
	}

    public void dispatchOnHiddenChanged(boolean hidden) {

        onHiddenChanged(hidden);

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.dispatchOnHiddenChanged(hidden);
        }
    }
	
	public void onInflate(Activity activity, AttributeSet attrs,
			Bundle savedInstanceState) {
	}

    public void dispatchOnInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {

        onInflate(activity, attrs, savedInstanceState);

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.dispatchOnInflate(activity, attrs, savedInstanceState);
        }
    }
	
	public void onSaveInstanceState(Bundle outState) {
	}

    public void dispatchOnSaveInstanceState(Bundle outState) {

        onSaveInstanceState(outState);

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.dispatchOnSaveInstanceState(outState);
        }
    }
	
	public void onViewCreated(View view, Bundle savedInstanceState) {
	}

    public void dispatchOnViewCreated(View view, Bundle savedInstanceState) {

        onViewCreated(mContentView, savedInstanceState);

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.onViewCreated(subBlock.getContentView(), savedInstanceState);
        }
    }
	
	public void onViewStateRestored(Bundle savedInstanceState) {
        mStateFlag |= FLAG_STATE_ON_VIEW_STATE_RESTORE;
	}

    public void dispatchOnViewStateRestored(Bundle savedInstanceState) {

        onViewStateRestored(savedInstanceState);

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.dispatchOnViewStateRestored(savedInstanceState);
        }
    }
	
	public View findViewById(int id) {
		if (mContentView == null) {
			return null;
		}
		return mContentView.findViewById(id);
	}
	
	public View findViewWithTag(Object tag) {
		if (mContentView == null) {
			return null;
		}
		return mContentView.findViewWithTag(tag);
	}
	
	public CBlock setTitle(CharSequence title) {
		if (mActivity != null) {
			mActivity.setTitle(title);
		}
        return this;
	}
	
	public CBlock setTitle(int resId) {
		if (mActivity != null) {
			mActivity.setTitle(resId);
		}
        return this;
	}

	public void addSubBlock(CBlock subBlock) {
        if (mSubBlocks == null) {
			mSubBlocks = new ArrayList<CBlock>();
		}
		mSubBlocks.add(subBlock);
	}
	
	public void removeSubBlock(CBlock subBlock) {
		if (mSubBlocks == null || mSubBlocks.isEmpty()) {
			return;
		}
		mSubBlocks.remove(subBlock);
	}
	
	public void dispatchState(int state) {
		mStateFlag |= state;
	}
	
	public boolean onCreateNavigationMenu(CMenu menu) {
		return false;
	}
	
	public boolean onPrepareNavigationMenu(CMenu item) {
		return false;
	}
	
	public void onNavigationMenuItemSelected(CMenuItem item) {
	}
	
	public boolean onCreateOptionsMenu(CMenu menu) {
		return false;
	}
	
	public boolean onPrepareOptionsMenu(CMenu menu) {
		return false;
	}
	
	public void onOptionsMenuItemSelected(CMenuItem item) {
	}

    public void sendMessageToBlock(Object blockTag, Bundle args) {
        CBlock block = findBlockWithTag(blockTag);
        if (block != null) {
            block.onReceiveMessageFromBlock(mTarget, args);
        }
    }

    public void onReceiveMessageFromBlock(Object fromBlockTag, Bundle args) {
    }

    public void sendMessageToBlock(int blockId, Bundle args) {
        CBlock block = findBlockById(blockId);
        if (block != null) {
            block.onReceiveMessageFromBlock(mId, args);
        }
    }

    public void onReceiveMessageFromBlock(int fromBlockId, Bundle args) {

    }

    public CBlock findBlockById(int id) {
        return findBlockByIdTraversal(id);
    }

    private CBlock findBlockByIdTraversal(long id) {

        if (mId == id) {
            return this;
        }

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return null;
        }

        CBlock tagBlock = null;
        final int N = mSubBlocks.size();
        for (int i=0; i<N; i++) {
            tagBlock = mSubBlocks.get(i);
            tagBlock = tagBlock.findBlockByIdTraversal(id);

            if (tagBlock != null) {
                break;
            }
        }
        return tagBlock;
    }

    public CBlock findBlockWithTag(Object tag) {
        return findBlockWithTagTraversal(tag);
    }

    private CBlock findBlockWithTagTraversal(Object tag) {

        if (mTarget != null && mTarget.equals(tag)) {
            return this;
        }

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return null;
        }

        CBlock tagBlock = null;
        final int N = mSubBlocks.size();
        for (int i=0; i<N; i++) {
            tagBlock = mSubBlocks.get(i);
            tagBlock = tagBlock.findBlockWithTagTraversal(tag);

            if (tagBlock != null) {
                break;
            }
        }

        return tagBlock;
    }

    public CBlock setTag(Object tag) {
        mTarget = tag;
        return  this;
    }

    public Object getTag() {
        return mTarget;
    }

    public CBlock setId(int id) {
        mId = id;
        return this;
    }

    public int getId() {
        return mId;
    }

    public CBlock getRoot() {
        return mRoot;
    }

    public Context getContext() {
        return mActivity.getBaseContext();
    }

    public Context getApplicationContext() {
        return mActivity.getApplicationContext();
    }

    public void setContainer(ViewGroup container) {
        mContainer = container;
    }

    public ViewGroup getContainerView() {
        return mContainer;
    }

    public View getContentView() {
        return mContentView;
    }

    public int requestCurrentState() {
        if (mRoot == this) {
            return mStateFlag;
        }

        if (mFragment != null) {
            return mFragment.getCurrentState();
        } else {
            return mRoot.requestCurrentState();
        }
    }

    public int getState() {
        return mStateFlag;
    }

    public void setState(int state) {
        mStateFlag = state;
    }

    void performSaveInstanceState(Bundle outState) {

        onSaveInstanceState(outState);

        if (mSavedState == null) {
            mSavedState = new SavedState();
        }

        mSavedState.id = mId;
        mSavedState.layoutResId = mContentLayoutResId;
        mSavedState.tag = mTarget;
        mSavedState.className = getClass().getName();
        mSavedState.arguments = mArgs;

        if (mSubBlocks == null) {
            return;
        }

        final int size = mSubBlocks.size();
        if (size > 0) {
            mSavedState.savedChildInstanceStates = new Bundle[size];
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                mSavedState.savedChildInstanceStates[i] = new Bundle();
                sub.performSaveInstanceState(mSavedState.savedChildInstanceStates[i]);
            }
        }

        if (mContainer != null) {
            if (mSavedInstanceState == null) {
                mSavedInstanceState = new Bundle();
            }
            mSavedViewState = new SparseArray<Parcelable>();
            mContainer.saveHierarchyState(mSavedViewState);
            mSavedInstanceState.putSparseParcelableArray("CBlockViewTag", mSavedViewState);
            mSavedState.savedInstanceState = mSavedInstanceState;
        }

        outState.putParcelable("CBlockTag", mSavedState);

    }

    void performRestoreInstanceState(Bundle savedInstanceState) {
        onSaveInstanceState(savedInstanceState);
        SavedState ss = savedInstanceState.getParcelable("CBlockTag");
        if (ss == null) {
            return;
        }

        mSavedState = ss;
        mId = ss.id;
        mContentLayoutResId = ss.layoutResId;
        mTarget = ss.tag;
        mArgs = ss.arguments;
        mStateFlag = ss.currState;

        Bundle[] savedChildInstanceStates = ss.savedChildInstanceStates;
        if (savedChildInstanceStates == null) {
            return;
        }

        final int size = savedChildInstanceStates.length;
        if (size <= 0) {
            return;
        }

        if (mSubBlocks == null) {
            mSubBlocks = new ArrayList<CBlock>();
        }
        for (int i=0; i<size; i++) {
            SavedState childSavedState = savedChildInstanceStates[i].getParcelable("CBlockTag");
            CBlock sub = instantiate(childSavedState);
            mSubBlocks.add(sub);
        }

        mSavedInstanceState = ss.savedInstanceState;
        if (mSavedInstanceState != null) {
            mSavedViewState = mSavedInstanceState.getSparseParcelableArray("CBlockViewTag");
        }
    }

    private CBlock instantiate(SavedState savedState) {
        final String className = savedState.className;
        if (TextUtils.isEmpty(className)) {
            return null;
        }

        CBlock sub = null;
        try {
            sub = (CBlock) getClass().getClassLoader()
                    .loadClass(className).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return sub;
    }

    void performAttach(CBlockActivity activity) {
        onAttach(activity);
        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                sub.performAttach(activity);
            }
        }
    }

    void performCreate(Bundle savedInstanceState) {
        onCreate(savedInstanceState);
        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);

                Bundle subState = null;
                if (savedInstanceState != null) {
                    subState = savedInstanceState.getParcelable(""+sub.getId());
                }
                sub.performCreate(subState);
            }
        }
    }

    void performStart() {
        onStart();
        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                sub.performStart();
            }
        }
    }

    void performResume() {
        onResume();
        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                sub.performResume();
            }
        }
    }

    void performPause() {
        onPause();
        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                sub.performPause();
            }
        }
    }

    void performStop() {
        onStop();
        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                sub.performStop();
            }
        }
    }

    void performDestroy() {
        onDestroy();
        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                sub.performDestroy();
            }
        }
    }

    void performDetach() {
        onDetach();
        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                sub.performDetach();
            }
        }
    }

    public CBlockActivity getActivity() {
        return mActivity;
    }

    public LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    public SavedState getSavedInstanceState() {
        return mSavedState;
    }

    public void preStartBlock(CBlock targetBlock, Bundle args) {
    }

    public void postStartBlock(CBlock targetBlock, Bundle args) {
    }

    public void startBlock(int targetBlockId, Bundle args) {
    }

    public void startBlock(Object targetBlockTag, Bundle args) {
    }

    public void startBlock(Class<?> targetBlockClass, Bundle args) {
        if (mActivity != null && mActivity instanceof CBlockActivity) {
            ((CBlockActivity) mActivity).startBlock(targetBlockClass, args);
        }
    }

    public void startBlock(CBlock targetBlock, Bundle args) {
    }

    public void startBlockForResult(int targetBlockId, Bundle args) {
    }

    public void startBlockForResult(Object targetBlockTag, Bundle args) {
    }

    public void startBlockForResult(Class<?> targetBlockClass, Bundle args) {
    }

    public void startBlockForResult(CBlock targetBlock, Bundle args) {
    }

    public void finish() {
        if (mActivity != null && mActivity instanceof CBlockActivity) {
            ((CBlockActivity) mActivity).finishBlock();
        }
    }

    public void onBackButtonClick() {
        if (mSubBlocks != null) {
            final int N = mSubBlocks.size();
            for (int i=0; i<N; i++) {
                final CBlock sub = mSubBlocks.get(i);
                sub.onBackButtonClick();
            }
        }
    }

    public void setArguments(Bundle args) {
        mArgs = args;
    }

    public Bundle getArguments() {
        return mArgs;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CBlock) {
            final CBlock other = (CBlock) o;
            final Object oTag = other.getTag();
            final int oId = other.getId();
            final String oName = other.getClass().getName();

            return getClass().getName().equals(oName) &&
                    (((mId!=NO_ID) && mId==oId)||
                            (mTarget!=null && mTarget.equals(oTag)));
        }
        return super.equals(o);
    }

    public static class SavedState implements Parcelable {

        Bundle savedInstanceState = null;
        int id = CBlock.NO_ID;
        int layoutResId = View.NO_ID;
        int currState = CBlock.NO_STATE;
        Bundle arguments = null;
        Object tag = null;
        String className = null;
        Bundle[] savedChildInstanceStates = null;
        SparseArray<Parcelable> savedViewState = null;

        public SavedState() {}

        public SavedState(Parcel in) {
            super();
            final ClassLoader l = getClass().getClassLoader();

            savedInstanceState = in.readParcelable(l);
            id = in.readInt();
            layoutResId = in.readInt();
            currState = in.readInt();
            arguments = in.readBundle();
            tag = in.readValue(l);
            className = in.readString();
            savedViewState = in.readSparseArray(l);

            Parcelable[] states = in.readParcelableArray(l);
            if (states != null) {
                final int size = states.length;
                if (size > 0) {
                    savedChildInstanceStates = new Bundle[size];
                }
                for (int i=0; i<size; i++) {
                    savedChildInstanceStates[i] = (Bundle) states[i];
                }
            }
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(savedInstanceState, i);
            parcel.writeInt(id);
            parcel.writeInt(layoutResId);
            parcel.writeInt(currState);
            parcel.writeBundle(arguments);
            parcel.writeValue(tag);
            parcel.writeString(className);
            parcel.writeParcelableArray(savedChildInstanceStates, i);
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
