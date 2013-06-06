package com.zhangguoyu.app;

import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.*;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import com.zhangguoyu.demo.actionbar.R;
import com.zhangguoyu.widget.CBlockingView;
import com.zhangguoyu.widget.CMenu;
import com.zhangguoyu.widget.CMenuItem;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by zhangguoyu on 13-5-23.
 */
public class CBlockManager {

    private static final String LOG_TAG = CBlockManager.class.getSimpleName();
    private static final String STACK_TAG = "manager:back_stack";
    private static final String ACTIVATED_TAG = "manager:activated";

    public interface OnBlockStackChangedListener {

        public void onPush(CBlock block, int size);

        public void onPop(CBlock block, int size);
    }

    private static class Instance {
        static HashMap<Integer, CBlockManager> MAP = new HashMap<Integer, CBlockManager>();
    }

    private CBlockActivity mActivity = null;
    private ArrayList<CBlockInfo> mBlockInfoList = null;
    private CBlockInfo mMainInfo = null;
    private static final HashMap<String, Class<?>> sClassMap = new HashMap<String, Class<?>>();

    private CBlock mActivated = null;
    private ArrayList<OnBlockStackChangedListener> mStackListeners = null;

    private int mCurrentState = CBlock.NO_STATE;
    private ViewGroup mFrame = null;
    private BlockBackStack mBlockBackStack = null;

    private LinkedList<Runnable> mActions = null;

    private Runnable mActionsRunner = new Runnable() {
        @Override
        public void run() {
            execNextActions();
        }
    };

    private Handler mHandler = new Handler(Looper.getMainLooper());

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
    }

    void parseBlockMainfestFromXml(int xmlResId) {

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
                            info.action = a.getString(R.styleable.ManifestBlock_action);
                            info.launchMode = a.getInt(R.styleable.ManifestBlock_launchMode, CBlockInfo.NEW_INSTANCE);
                            final int orientation = a.getInt(R.styleable.ManifestBlock_orientation, 3);
                            a.recycle();

                            if (CBlockIntent.ACTION_MAIN.equals(info.action)) {
                                mMainInfo = info;
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

    public CBlockInfo findBlockInfoWithTag(String tag) {
        if (tag == null || mBlockInfoList == null) {
            return null;
        }

        final int N = mBlockInfoList.size();
        for (int i=0; i<N; i++) {
            final CBlockInfo info = mBlockInfoList.get(i);
            if (info != null) {
                final String infoTag = info.tag;
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

    private CBlockInfo findBlockInfoByIntent(CBlockIntent intent) {
        if (intent == null || mBlockInfoList == null) {
            return null;
        }

        final int N = mBlockInfoList.size();
        CBlockInfo target = null;
        for (int i=0; i<N; i++) {
            final CBlockInfo info = mBlockInfoList.get(i);
            if (info == null || TextUtils.isEmpty(info.className)) {
                continue;
            }

            final int infoId = info.id;
            final int intentId = intent.getBlockId();
            if (intentId != CBlock.NO_ID && infoId == intentId) {
                target = info;
                break;
            }

            final String infoAction = info.action;
            final String intentAction = intent.getAction();
            if (!TextUtils.isEmpty(infoAction)
                    && !TextUtils.isEmpty(intentAction)) {
                if (infoAction.equals(intentAction)) {
                    target = info;
                    break;
                }
            }

            final String infoClass = info.className;
            final String intentClass = intent.getBlockClassName();
            if (infoClass.equals(intentClass)){
                target = info;
                break;
            }

        }
        return target;
    }

    void attachToActivity() {
        if (mMainInfo == null) {
            throw new RuntimeException("Can not found the main block!");
        }

        final CBlockInfo mainInfo = mMainInfo;
        mFrame = (ViewGroup) mActivity.findViewById(R.id.main_frame_block);
        if (mFrame instanceof CBlockingView) {
            CBlockingView root = (CBlockingView) mFrame;
            try {
                Class<?> clazz = mActivity.getClassLoader().loadClass(mainInfo.className);
                root.bindBlock(clazz, mainInfo.id, mainInfo.layoutResId, mainInfo.tag);
                mActivated = root.getBlock();
                mActivated.setTitle(mainInfo.title)
                        .setContainerId(mFrame.getId());
                mActivated.flagRoot(true);
                mActivated.setBlockInfo(mainInfo);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return;
        }

        throw new RuntimeException("");
    }

    void execStartBlockForResult(int containerId, CBlockIntent intent,
                                 int requestCode, boolean immediately) {
        synchronized (this) {

            if (!immediately && mActivated != null && mActivated.isPrepared()) {
                throw new RuntimeException("Can not start block " + intent
                        + " during the preparation phase");
            }

            final CBlockInfo info = findBlockInfoByIntent(intent);
            if (info == null || TextUtils.isEmpty(info.className)) {
                throw new CBlockNotFoundException("Can not found block " + intent + "."
                        + "Please check the block has been declared in block manifest XML file");
            }

            final CBlock block = requestBlockFromBackStack(info);
            final CBlock activated = mActivated;
            if (activated != null && requestCode > 0) {
                activated.flagWaitForResult(true);
                block.mRequestCode = requestCode;
            }
            block.setArguments(intent.getArguments());
            block.flagRoot(true);
            block.setContainerId(containerId);
            block.mNewBlockIntent = intent;
            block.flagTemp(activated!=null&&activated.isHandledIntercepted());

            if (activated!=null&&!immediately) {
                activated.flagPrepared(true);
                if (!activated.performPrepareStartBlock(intent)) {
                    activated.flagPrepared(false);
                    activated.flagHandleIntercepted(true);
                    if (activated.dispatchIntercepteProcess(intent)) {
                        activated.pushPendingAction(block);
                    }
                    activated.flagHandleIntercepted(false);
                    return;
                }
                activated.flagPrepared(false);
            }
            pushBlockToBackStack(block);
        }
    }

    private CBlock requestBlockFromBackStack(CBlockInfo info) {
        final int launchMode = info.launchMode;
        CBlock block = null;

        if (mBlockBackStack != null) {
            if (launchMode == CBlockInfo.SINGLE_TASK) {
                mBlockBackStack.moveToTemp(mActivated);
                if (mBlockBackStack.checkBlockAlive(info)) {
                    block = mBlockBackStack.popBlock(info);
                }
            } else if (launchMode == CBlockInfo.SINGLE_INSTANCE) {

                if (mActivated == null || !info.equals(mActivated.getBlockInfo())) {
                    block = mBlockBackStack.requestBlockAlive(info);
                } else {
                    block = mActivated;
                }
            }
        }

        if (block == null) {
            block = createNewInstanceByInfo(info);
        }
        return block;
    }

    void execFinish() {
        if (mActivated != null) {
            popCurrentBlockFromBackStack();
        }
    }

    CBlock buildMainBlock(String className) {
        CBlock main = null;
        if (!TextUtils.isEmpty(className)) {
            try {
                Class<?> blockClass = sClassMap.get(className);
                if (blockClass == null && mActivity != null) {
                    blockClass = mActivity.getClassLoader().loadClass(className);
                    sClassMap.put(className, blockClass);
                }

                if (blockClass != null) {
                    main = (CBlock) blockClass.newInstance();
                }

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

    CBlock createNewInstanceByInfo(CBlockInfo info) {
        if (info == null) {
            return null;
        }

        CBlock block = null;
        final String className = info.className;
        if (!TextUtils.isEmpty(className)) {
            try {
                Class<?> blockClass = sClassMap.get(className);
                if (blockClass == null && mActivity != null) {
                    blockClass = mActivity.getClassLoader().loadClass(className);
                    sClassMap.put(className, blockClass);
                }

                if (blockClass != null) {
                    block = ((CBlock) blockClass.newInstance())
                        .setId(info.id)
                        .setTag(info.tag)
                        .setTitle(info.title)
                        .setBlockInfo(info);
                    if (info.layoutResId > 0) {
                        block.setContentView(info.layoutResId);
                    }

                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return block;
    }

    public CBlock getCurrentBlock() {
        return mActivated;
    }

    public void pushBlockToBackStack(CBlock block) {
        ActionInfo ai = new ActionInfo();
        ai.mOp = Action.OP_PUSH;
        ai.mBlock = block;
        enqueueAction(new Action(ai));
    }

    public CBlock popCurrentBlockFromBackStack() {
        if (mActivated != null) {
            ActionInfo ai = new ActionInfo();
            ai.mOp = Action.OP_POP;
            ai.mBlock = mActivated;
            enqueueAction(new Action(ai));
        }
        return mActivated;
    }

    void dispatchCreate() {
        mCurrentState = CBlock.STATE_CREATE;
        syncStateToBlock(mActivated);
    }

    void dispatchStart() {
        mCurrentState = CBlock.STATE_START;
        syncStateToBlock(mActivated);
    }

    void dispatchResume() {
        mCurrentState = CBlock.STATE_RESUME;
        syncStateToBlock(mActivated);
    }

    void dispatchPause() {
        mCurrentState = CBlock.STATE_START;
        syncStateToBlock(mActivated);
    }

    void dispatchStop() {
        mCurrentState = CBlock.STATE_CREATE;
        syncStateToBlock(mActivated);
    }

    void dispatchDestroy() {
        mCurrentState = CBlock.STATE_INITIALIZING;
        syncStateToBlock(mActivated);
    }

    Bundle saveState() {
        Bundle saveState = new Bundle();
        if (mBlockBackStack != null) {
            final Bundle stackState = mBlockBackStack.saveAllState();
            if (stackState != null) {
                saveState.putBundle(STACK_TAG, stackState);
            }
        }
        dispatchSaveInstanceState(saveState);
        return saveState;
    }

    void dispatchSaveInstanceState(Bundle savedState) {
        if (mActivated != null) {
            savedState.putParcelable(ACTIVATED_TAG, mActivated.saveAllState());
        }
    }

    void restoreState(Bundle savedState) {
        Bundle backStackState = savedState.getBundle(STACK_TAG);
        if (backStackState != null) {
            if (mBlockBackStack == null) {
                mBlockBackStack = new BlockBackStack();
            }
            mBlockBackStack.restoreAllState(backStackState);
        }
        Parcelable activatedState = savedState.getParcelable(ACTIVATED_TAG);
        if (activatedState != null && activatedState instanceof CBlock.SavedState) {
            mActivated = CBlock.instantiate(mActivity, (CBlock.SavedState) activatedState);
        }
    }

    boolean dispatchCreateOptionsMenu(CMenu menu) {
        if (mActivated != null) {
            return mActivated.dispatchCreateOptionsMenu(menu);
        }
        return false;
    }

    boolean dispatchCreateNavigationMenu(CMenu menu) {
        if (mActivated != null) {
            return mActivated.dispatchCreateNavigationMenu(menu);
        }
        return false;
    }

    void dispatchOptionsMenuItemSelected(CMenuItem item) {
        if (mActivated != null) {
            mActivated.dispatchNavigationMenuItemSelected(item);
        }
    }

    void dispatchNavigationMenuItemSelected(CMenuItem item) {
        if (mActivated != null) {
            mActivated.dispatchOptionsMenuItemSelected(item);
        }
    }

    boolean dispatchBackButtonClick() {
        if (mActivated != null) {
            return mActivated.dispatchBackButtonClick();
        }
        return false;
    }

    void dispatchConfigurationChanged(Configuration newConfig) {
        if (mActivated != null) {
            mActivated.dispatchConfigurationChanged(newConfig);
        }
    }

    void dispatchLowMemory() {
        if (mActivated != null) {
            mActivated.dispatchLowMemory();
        }
    }

    void dispatchScreenOn() {
        if (mActivated != null) {
            mActivated.dispatchScreenOn();
        }
    }

    void dispatchScreenOff() {
        if (mActivated != null) {
            mActivated.dispatchScreenOff();
        }
    }

    public void addBlockStackChangedListener(OnBlockStackChangedListener added) {
        if (mStackListeners == null) {
            mStackListeners = new ArrayList<OnBlockStackChangedListener>();
        }
        mStackListeners.add(added);
    }

    public void removeBlockStackChangedListener(OnBlockStackChangedListener removed) {
        if (mStackListeners != null) {
            mStackListeners.remove(removed);
        }
        if (mStackListeners.isEmpty()) {
            mStackListeners = null;
        }
    }

    void addBlock(CBlock child, int containerId, CBlock parent) {
        ActionInfo ai = new ActionInfo();
        ai.mOp = Action.OP_ADD;
        ai.mContainerId = containerId;
        ai.mBlock = child;
        ai.mParent = parent;
        enqueueAction(new Action(ai));
    }

    void removeBlock(int removedBlockId, CBlock parent) {
        ActionInfo ai = new ActionInfo();
        ai.mOp = Action.OP_REMOVE;
        ai.mBlockId = removedBlockId;
        ai.mParent = parent;
        enqueueAction(new Action(ai));
    }

    void replaceBlock(CBlock child, int containerId, CBlock parent) {
        ActionInfo ai = new ActionInfo();
        ai.mOp = Action.OP_REPLACE;
        ai.mContainerId = containerId;
        ai.mBlock = child;
        ai.mParent = parent;
        enqueueAction(new Action(ai));
    }

    void syncStateToBlock(CBlock blockForSyncing) {
        syncStateToBlock(blockForSyncing, mCurrentState);
    }

    void syncStateToBlock(CBlock blockForSyncing, int stateSynced) {
        syncStateToBlock(blockForSyncing, stateSynced, -1);
    }

    void syncStateToBlock(CBlock blockForSyncing, int stateSynced, int cmd) {
        //block can not be null.
        if (blockForSyncing == null) {
            return;
        }
        //No need to sync, if the state of block equal with the current state of context
        if (blockForSyncing.getState() == stateSynced) {
            return;
        }

        final CBlock activated = mActivated;
        final int blockState = blockForSyncing.getState();
        if (blockState < stateSynced) { //active process
            switch (blockState) {
                case CBlock.NO_STATE:
                case CBlock.STATE_INITIALIZING:
                    if (stateSynced > CBlock.STATE_INITIALIZING) {
                        blockForSyncing.attachToActivity(mActivity);
                        ViewGroup container = null;
                        if (mFrame != null) {
                            final int containerId = blockForSyncing.getContainerId();
                            container = (ViewGroup) mFrame.findViewById(containerId);
                            blockForSyncing.setContainer(container);
                        }
                        CBlock.SavedState ss = blockForSyncing.getSavedInstanceState();
                        Bundle sis = ss!=null?ss.savedInstanceState:null;
                        blockForSyncing.performCreate(sis);
                    }
                case CBlock.STATE_CREATE:
                    if (stateSynced > CBlock.STATE_CREATE) {
                        final CBlockInfo info = blockForSyncing.getBlockInfo();
                        if (info != null && info.launchMode == CBlockInfo.SINGLE_INSTANCE) {
                            if (blockForSyncing.getBlockIntent() != null) {
                                blockForSyncing.setBlockIntent(blockForSyncing.mNewBlockIntent)
                                        .performNewIntent(blockForSyncing.mNewBlockIntent);
                            } else {
                                blockForSyncing.setBlockIntent(blockForSyncing.mNewBlockIntent);
                            }
                            blockForSyncing.mNewBlockIntent = null;
                        }
                        blockForSyncing.performStart();
                    }
                case CBlock.STATE_START:
                    if (stateSynced > CBlock.STATE_START) {
                        blockForSyncing.performResume();

                        final CharSequence title =  blockForSyncing.getTitle();
                        if (title != null) {
                            mActivity.setTitle(title);
                        }

                        if (!blockForSyncing.isRoot()) {
                            break;
                        }

                        if (blockForSyncing == mActivated) {
                            mActivity.dispatchCreateOptionsMenu();
                            mActivity.dispatchCreateNavigationMenu();
                            break;
                        }

                        final View enterContentView = blockForSyncing.getContentView();
                        if (enterContentView == null) {
                            break;
                        }

                        final CBlock enterBlock = blockForSyncing;
                        final View exitContentView = activated.getContentView();
                        final CBlock exitBlock = activated;

                        Animation enterAnim = null;
                        Animation exitAnim = null;
                        switch(cmd) {
                            case Action.OP_PUSH:
                                enterAnim = blockForSyncing.onCreateEnterAnimationForPush(activated);
                                exitAnim = activated.onCreateExitAnimationForPause(blockForSyncing);
                                break;
                            case Action.OP_POP:
                                enterAnim = blockForSyncing.onCreateEnterAnimationForResume(activated);
                                exitAnim = activated.onCreateExitAnimationForPop(blockForSyncing);
                                break;
                        }

                        enterContentView.setVisibility(View.VISIBLE);
                        if (enterAnim != null) {
                            enterAnim.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {}
                                @Override
                                public void onAnimationRepeat(Animation animation) {}

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    detachBlockFromWindow(exitBlock);
                                }
                            });

                            enterContentView.startAnimation(enterAnim);
                        }

                        if (exitContentView != null && exitAnim != null) {

                            exitAnim.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {}
                                @Override
                                public void onAnimationRepeat(Animation animation) {}

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    //exitContentView.setVisibility(View.GONE);
                                    detachBlockFromWindow(exitBlock);
                                }

                            });

                            exitContentView.startAnimation(exitAnim);

                        } else {
                            if (enterAnim == null) {
                                detachBlockFromWindow(exitBlock);
                            }
                        }

                        if (activated != null) {
                            final int rc = activated.mResultCode;
                            final Bundle ra = activated.getResultArgs();
                            final int rq = activated.mRequestCode;
                            activated.mResultCode = CBlock.NO_RESULT;
                            if (rc > CBlock.NO_RESULT && enterBlock.isWaitingForResult()) {
                                enterBlock.flagWaitForResult(false);
                                enterBlock.performResumeForResult(activated, rq, rc, ra);
                            }
                        }

                        mActivated = enterBlock;

                        mActivity.dispatchCreateOptionsMenu();
                        mActivity.dispatchCreateNavigationMenu();

                    }
            }
        } else if (blockState > stateSynced) { //negative process
            switch (blockState) {
                case CBlock.STATE_RESUME:
                    if (stateSynced < CBlock.STATE_RESUME) {
                        blockForSyncing.performPause();
                    }
                case CBlock.STATE_START:
                    if (stateSynced < CBlock.STATE_START) {
                        blockForSyncing.performStop();
                    }
                case CBlock.STATE_CREATE:
                    if (stateSynced < CBlock.STATE_CREATE) {
                        blockForSyncing.performDestroy();
                        blockForSyncing.detachFromActivity();
                        blockForSyncing.flagDetached(true);
                    }
            }
        }

        blockForSyncing.dispatchUpdateStateTo(stateSynced);
    }

    private void detachBlockFromWindow(CBlock exitBlock) {
        if (exitBlock.isDetached()) {
            ViewGroup container = exitBlock.getContainer();
            final View contentView = exitBlock.getContentView();
            if (container != null && contentView != null) {
                container.removeView(contentView);
            }
        } else {
            exitBlock.getContentView().setVisibility(View.GONE);
        }
    }

    void enqueueAction(Runnable action) {
        synchronized (this) {
            if (mActions == null) {
                mActions = new LinkedList<Runnable>();
            }

            mActions.offer(action);
            if (mActions.size() == 1) {
                mHandler.removeCallbacks(mActionsRunner);
                mHandler.post(mActionsRunner);
            }
        }
    }

    private void execNextActions() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalThreadStateException();
        }

        if (mActions == null || mActions.isEmpty()) {
            return;
        }

        Runnable action = mActions.poll();
        action.run();

        if (!mActions.isEmpty()) {
            mHandler.removeCallbacks(mActionsRunner);
            mHandler.post(mActionsRunner);
        }
    }

    public class BlockBackStack {

        static final String ALIVE_TAG = "stack:alive";
        static final String TEMP_TAG = "stack:temp";

        LinkedList<CBlock> mAlive = null;
        ArrayList<CBlock> mTemp = null;

        BlockBackStack() {}

        void push(CBlock block) {
            if (mAlive == null) {
                mAlive = new LinkedList<CBlock>();
            }
            mAlive.addFirst(block);
        }

        CBlock pop() {
            if (mAlive != null && !mAlive.isEmpty()) {
                return mAlive.removeFirst();
            }

            return null;
        }

        int size() {
            if (mAlive != null) {
                return mAlive.size();
            }
            return 0;
        }

        boolean isEmpty() {
            return mAlive == null || mAlive.isEmpty();
        }

        void clearTemp() {
            if (mTemp != null) {
                mTemp.clear();
            }
            mTemp = null;
        }

        boolean hasTemp() {
            return mTemp != null && !mTemp.isEmpty();
        }

        void moveToTemp(CBlock block) {
            if (mTemp == null) {
                mTemp = new ArrayList<CBlock>();
            }
            mTemp.add(block);
        }

        int tempSize() {
            if (mTemp != null) {
                return mTemp.size();
            }
            return 0;
        }

        CBlock getTempAt(int index) {
            if (index < 0) {
                return null;
            }
            if (mTemp != null && mTemp.size() > index) {
                return mTemp.get(index);
            }
            return null;
        }

        boolean checkBlockAlive(CBlockInfo info) {
            boolean res = false;
            if (mAlive == null) {
                return false;
            }
            Iterator<CBlock> i = mAlive.iterator();
            while(i.hasNext()) {
                final CBlock b = i.next();
                if (b.getBlockInfo() != null
                        && b.getBlockInfo().equals(info)) {
                    res = true;
                    break;
                }
            }
            return res;
        }

        CBlock requestBlockAlive(CBlockInfo info) {
            CBlock target = null;
            if (mAlive == null) {
                return null;
            }
            Iterator<CBlock> i = mAlive.iterator();
            while(i.hasNext()) {
                final CBlock b = i.next();
                if (b.getBlockInfo() != null
                        && b.getBlockInfo().equals(info)) {
                    target = b;
                    break;
                }
            }
            if (target != null) {
                mAlive.remove(target);
            }
            return target;
        };

        CBlock popBlock(CBlockInfo info) {
            if (mAlive == null) {
                return null;
            }
            CBlock pop = mAlive.removeFirst();
            while (pop != null) {
                pop = pop();

                final CBlockInfo i = pop.getBlockInfo();
                if (i == null || !i.equals(info)) {
                    moveToTemp(pop);
                    continue;
                }

                break;
            }
            return pop;
        }

        Bundle saveAllState() {
            Bundle allState = new Bundle();
            if (mAlive != null && !mAlive.isEmpty()) {
                ArrayList<Parcelable> aliveStates = new ArrayList<Parcelable>();
                Iterator<CBlock> i = mAlive.iterator();
                while (i.hasNext()) {
                    final CBlock b = i.next();
                    if (b.getId() > CBlock.NO_ID) {
                        aliveStates.add(b.saveAllState());
                    }
                }
                allState.putParcelableArrayList(ALIVE_TAG, aliveStates);
            }
            if (mTemp != null && !mTemp.isEmpty()) {
                ArrayList<Parcelable> tempStates = new ArrayList<Parcelable>();
                final int size = mTemp.size();
                for (int i=0; i<size; i++) {
                    final CBlock b = mTemp.get(i);
                    if (b.getId() > CBlock.NO_ID) {
                        tempStates.add(b.saveAllState());
                    }
                }
                allState.putParcelableArrayList(TEMP_TAG, tempStates);
            }
            return allState;
        }

        void restoreAllState(Bundle savedState) {
            ArrayList<Parcelable> aliveStates =
                    savedState.getParcelableArrayList(ALIVE_TAG);
            if (aliveStates != null) {
                final int len = aliveStates.size();
                if (len > 0) {
                    if (mAlive == null) {
                        mAlive = new LinkedList<CBlock>();
                    }
                    for (int i=0; i<len; i++) {
                        CBlock.SavedState blockState =
                                (CBlock.SavedState) aliveStates.get(i);
                        CBlock b = CBlock.instantiate(mActivity, blockState);
                        mAlive.addLast(b);
                        invokeStackListenerPushCallback(b);
                    }
                }
            }
            ArrayList<Parcelable> tempStates =
                    savedState.getParcelableArrayList(TEMP_TAG);
            if (tempStates != null) {
                final int len = tempStates.size();
                if (len > 0) {
                    if (mTemp == null) {
                        mTemp = new ArrayList<CBlock>();
                    }
                    for (int i=0; i<len; i++) {
                        CBlock.SavedState blockState =
                                (CBlock.SavedState) tempStates.get(i);
                        CBlock b = CBlock.instantiate(mActivity, blockState);
                        mTemp.add(b);
                    }
                }
            }
        }
    }

    private static class ActionInfo {
        int mOp;
        int mContainerId;
        int mBlockId;
        CBlock mBlock;
        CBlock mParent;
    }

    private class Action implements Runnable {

        static final int OP_PUSH = 1;
        static final int OP_POP = 2;
        static final int OP_ADD = 3;
        static final int OP_REMOVE = 4;
        static final int OP_REPLACE = 5;

        private ActionInfo mInfo = null;

        Action(ActionInfo info) {
            mInfo = info;
        }

        @Override
        public void run() {
            int stateSynced = 0;
            final ActionInfo ai = mInfo;
            switch(ai.mOp) {
                case OP_PUSH:
                    if (mBlockBackStack == null) {
                        mBlockBackStack = new BlockBackStack();
                    }

                    if (ai.mBlock != mActivated) {
                        mBlockBackStack.push(mActivated);
                        invokeStackListenerPushCallback(ai.mBlock);
                    }
                    stateSynced = mCurrentState;
                    //clear the temp's block if the stack
                    //for storing the temp's block is not empty
                    if (mBlockBackStack.hasTemp()) {
                        final int tempSize = mBlockBackStack.tempSize();
                        for (int i=0; i<tempSize; i++) {
                            final CBlock temp = mBlockBackStack.getTempAt(i);
                            syncStateToBlock(temp, CBlock.STATE_INITIALIZING);
                        }
                        mBlockBackStack.clearTemp();
                    }
                    if (mActivated != null && !mActivated.isDetached()) {
                        syncStateToBlock(mActivated, CBlock.STATE_CREATE);
                    }
                    syncStateToBlock(ai.mBlock, stateSynced, ai.mOp);
                    break;
                case OP_POP:
                    syncStateToBlock(ai.mBlock, CBlock.STATE_INITIALIZING);
                    CBlock currBlock = null;
                    if (mBlockBackStack != null) {
                        currBlock = mBlockBackStack.pop();
                    }
                    if (currBlock != null && currBlock.hasPendingActions()
                            && ai.mBlock.getResultCode() > CBlock.RESULT_CANCEL) {
                        mBlockBackStack.push(currBlock);
                        currBlock = currBlock.popPendingAction();
                        ai.mOp = OP_PUSH;
                    }
                    invokeStackListenerPopCallback(ai.mBlock);

                    if (currBlock != null) {
                        ai.mBlock = currBlock;
                        stateSynced = CBlock.STATE_RESUME;
                    }
                    syncStateToBlock(ai.mBlock, stateSynced, ai.mOp);
                    break;
                case OP_REPLACE:
                    final ViewGroup container = ai.mParent.getContainer();
                    if (container != null && container.getChildCount()>0) {
                        container.removeAllViews();
                    }
                case OP_ADD:
                    if (ai.mContainerId > 0 && ai.mBlock != null) {
                        ai.mBlock.setContainerId(ai.mContainerId);
                        ai.mBlock.attachToParent(ai.mParent);
                    }
                    break;
                case OP_REMOVE:
                    CBlock block = ai.mParent.findBlockById(ai.mBlockId);
                    if (block != null) {
                        block.detachFromParent();
                    }
                    break;
            }

        }
    }

    private void invokeStackListenerPushCallback(CBlock block) {
        if (mStackListeners != null) {
            for (OnBlockStackChangedListener listener : mStackListeners) {
                listener.onPush(block, mBlockBackStack.size());
            }
        }
    }

    private void invokeStackListenerPopCallback(CBlock block) {
        if (mStackListeners != null) {
            for (OnBlockStackChangedListener listener : mStackListeners) {
                listener.onPop(block, mBlockBackStack.size());
            }
        }
    }

    public static class CBlockInfo implements Parcelable {

        public static final int NEW_INSTANCE = 1;
        public static final int SINGLE_INSTANCE = 2;
        public static final int SINGLE_TASK = 3;

        public int id = CBlock.NO_ID;
        public CharSequence title = null;
        public int layoutResId = 0;
        public String tag = null;
        public String className = null;
        public String action = null;
        public int launchMode = NEW_INSTANCE;

        public CBlockInfo() {}

        public CBlockInfo(Parcel in) {
            super();
            id = in.readInt();
            title = in.readString();
            layoutResId = in.readInt();
            tag = in.readString();
            className = in.readString();
            action = in.readString();
            launchMode = in.readInt();
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
            parcel.writeString(tag);
            parcel.writeString(className);
            parcel.writeString(action);
            parcel.writeInt(launchMode);
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

        @Override
        public boolean equals(Object o) {
            if (o instanceof CBlockInfo) {
                final CBlockInfo other = (CBlockInfo) o;
                return className != null && other.className != null
                        && className.equals(other.className);
            }
            return false;
        }
    }

}
