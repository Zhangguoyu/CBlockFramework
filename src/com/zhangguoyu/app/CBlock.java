package com.zhangguoyu.app;

import android.app.Activity;
import android.content.*;
import android.content.res.Configuration;
import android.os.*;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import com.zhangguoyu.widget.CActionBar;
import com.zhangguoyu.widget.CBlockingView;
import com.zhangguoyu.widget.CMenu;
import com.zhangguoyu.widget.CMenuItem;

import java.util.*;

public class CBlock {

    private static final String LOG_TAG = CBlock.class.getSimpleName();

    private static final HashMap<String, Class<?>> sClass = new HashMap<String, Class<?>>();
    private static final String SUB_TAG = "block:subblock";
    public static final int NO_ID = View.NO_ID;

    static final String INTENT_TAG = "block_intent";
    static final int NO_STATE = 0;
    static final int STATE_INITIALIZING = 1;
    static final int STATE_CREATE = 2;
    static final int STATE_START = 3;
    static final int STATE_RESUME = 4;

    private static final int FLAG_ROOT = 0x1;
    private static final int FLAG_PREPARED = 0x2;
    private static final int FLAG_WAIT_FOR_RESULT = 0x4;
    private static final int FLAG_INFLATED = 0x8;
    private static final int FLAG_DETACHED = 0x10;
    private static final int FLAG_IS_TEMP = 0x20;
    private static final int FLAG_HANDLE_INTERCEPTED = 0x40;
    private static final int FLAG_EXPECT_INTERCEPTE = 0x80;

    public static final int RESULT_OK = 0;
    public static final int RESULT_CANCEL = -1;
    public static final int NO_RESULT = -2;
	
	private CBlockActivity mActivity = null;
	private ViewGroup mContainer = null;
    private int mContainerId = 0;
	private LayoutInflater mLayoutInflater = null;
	private View mContentView = null;
	private int mContentLayoutResId = 0;
    private CharSequence mTitle = null;
	private List<CBlock> mSubBlocks = null;
    private SparseArray<CBlock> mContacts = null;
    private CBlock mParent = null;
    private CBlock mRoot = null;
    private String mTarget = null;
    private int mId = NO_ID;
    private Bundle mArgs = null;
    private SavedState mSavedState = null;
    private SparseArray<Parcelable> mSavedSubBlockState = null;
    private CBlockManager mBlockManager = null;
    private CBlockManager.CBlockInfo mBlockInfo = null;
    private CBlockIntent mBlockIntent = null;
    int mRequestCode;
    private int mFlag = 0;

    CBlockIntent mNewBlockIntent = null;

    private LinkedList<CBlock> mPendingActions = null;
    int mResultCode = NO_RESULT;
    private Bundle mResultArgs = null;

    BlockInterceptor mInterceptor = null;

	private int mStateFlag = NO_STATE;

    public interface BlockInterceptor {

        public boolean onProcess(CBlock src, CBlockIntent intent);

    }

    public static CBlock initWithBlockName(String blockName) {
        if (TextUtils.isEmpty(blockName)) {
            return null;
        }

        CBlock newBlock = null;
        try {
            Class<?> clazz = Class.forName(blockName);
            newBlock = (CBlock) clazz.newInstance();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return newBlock;
    }

    void attachToActivity(CBlockActivity activity) {
        mActivity = activity;
        mBlockManager = activity.getBlockManager();
        mLayoutInflater = activity.getLayoutInflater();
        mStateFlag = CBlock.STATE_INITIALIZING;
        performAttach(activity);
    }

    void detachFromActivity() {
        mStateFlag = CBlock.NO_STATE;
        performDetach();
    }
	
	public void setContentView(int layoutId) {
        if (mContentView != null && mContentLayoutResId == layoutId) {
            return;
        }

		mContentLayoutResId = layoutId;
		if (layoutId > 0 && mLayoutInflater != null) {
			setContentView(mLayoutInflater.inflate(layoutId, null));
		}
	}
	
	public void setContentView(View view) {
		mContentView = view;
        if (view == null) {
            return;
        }
        attachSubBlocksTraversalInView(mContentView);
        ViewGroup contentParent = (ViewGroup) mContentView.getParent();
        if (mContainer != contentParent) {
            if (contentParent != null) {
                contentParent.removeView(mContentView);
            }
            if (mContainer != null) {
                mContainer.addView(mContentView);
            }
        }
        flagInflated(true);
	}

    public CBlockManager getBlockManager() {
        return mBlockManager;
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
        if (mParent != null || parent == null) {
            return;
        }

        parent.addSubBlock(this);

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
            mBlockManager.syncStateToBlock(this, CBlock.STATE_INITIALIZING);
        }

        mRoot = null;
    }

    CBlock setBlockInfo(CBlockManager.CBlockInfo info) {
        mBlockInfo = info;
        return this;
    }

    CBlockManager.CBlockInfo getBlockInfo() {
        return mBlockInfo;
    }

    boolean hasPendingActions() {
        return mPendingActions != null && !mPendingActions.isEmpty();
    }

    void pushPendingAction(CBlock block) {
        if (mPendingActions == null) {
            mPendingActions = new LinkedList<CBlock>();
        }
        mPendingActions.addFirst(block);
    }

    CBlock popPendingAction() {
        CBlock pop = null;
        if (mPendingActions != null) {
            pop = mPendingActions.removeFirst();
            if (mPendingActions.isEmpty()) {
                mPendingActions = null;
            }
        }
        return pop;
    }

    void clearPendingActions() {
        if (mPendingActions != null) {
            mPendingActions.clear();
        }
    }
	
	public void onCreate(Bundle savedInstanceState) {
    }

	public void onStart() {
	}

    public void onResume() {
    }

    public void onPause() {
    }
	
	public void onStop() {
	}

    public void onDestroy() {
    }

    void onDetach() {
    }

    public void onNewIntent(CBlockIntent intent) {
    }

    public void onLowMemory() {
    }

    public void onConfigurationChanged(Configuration newConfig) {
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
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
        mTitle = title;
		if (mActivity != null) {
			mActivity.setTitle(title);
		}
        return this;
	}
	
	public CBlock setTitle(int resId) {
        if (resId > 0) {
            mTitle = getContext().getResources().getText(resId);
        }
		if (mActivity != null) {
			mActivity.setTitle(resId);
		}
        return this;
	}

    public CharSequence getTitle() {
        return mTitle;
    }

	void addSubBlock(CBlock subBlock) {
        if (subBlock == null) {
            throw new IllegalArgumentException("Can not add a null block to the parent");
        }

        if (mSubBlocks == null) {
			mSubBlocks = new ArrayList<CBlock>();
		}
		mSubBlocks.add(subBlock);

        subBlock.mParent = this;
        subBlock.mRoot = mRoot;

	}
	
	void removeSubBlock(CBlock subBlock) {
		if (mSubBlocks == null || mSubBlocks.isEmpty()) {
			return;
		}
		mSubBlocks.remove(subBlock);
	}

    CBlock addBlock(int containerId, CBlock block) {
        mBlockManager.addBlock(block, containerId, this);
        return this;
    }

    CBlock removeBlock(int blockId) {
        mBlockManager.removeBlock(blockId, this);
        return this;
    }

    CBlock replaceBlock(int containerId, CBlock block) {
        mBlockManager.replaceBlock(block, containerId, this);
        return this;
    }

    public boolean dispatchCreateNavigationMenu(CMenu menu) {
        return onCreateNavigationMenu(menu);
    }
	
	public boolean onCreateNavigationMenu(CMenu menu) {
		return false;
	}
	
	public boolean onPrepareNavigationMenu(CMenu item) {
		return false;
	}

    void dispatchNavigationMenuItemSelected(CMenuItem item) {
        if (onNavigationMenuItemSelected(item)) {
            return;
        }

        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                sub.dispatchNavigationMenuItemSelected(item);
            }
        }
    }
	
	public boolean onNavigationMenuItemSelected(CMenuItem item) {
        return false;
	}

    public boolean dispatchCreateOptionsMenu(CMenu menu) {
        return onCreateOptionsMenu(menu);
    }
	
	public boolean onCreateOptionsMenu(CMenu menu) {
		return false;
	}
	
	public boolean onPrepareOptionsMenu(CMenu menu) {
		return false;
	}

    void dispatchOptionsMenuItemSelected(CMenuItem item) {
        if (onOptionsMenuItemSelected(item)) {
            return;
        }

        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                sub.dispatchOptionsMenuItemSelected(item);
            }
        }
    }
	
	public boolean onOptionsMenuItemSelected(CMenuItem item) {
        return false;
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

    public void sendMessageToBlock(CBlockIntent intent) {
        final int targetId = intent.getBlockId();

        CBlock block = null;
        if (mContacts != null) {
            block = mContacts.get(targetId);
        }

        if (block == null) {
            block = mParent.findBlockById(targetId);
            if (block != null) {
                if (mContacts == null) {
                    mContacts = new SparseArray<CBlock>();
                }
                mContacts.put(targetId, block);
            }
        }
        if (block != null) {
            block.onReceiveMessageFromBlock(this, intent);
        }
    }

    public void onReceiveMessageFromBlock(CBlock from, CBlockIntent intent) {
    }

    public CActionBar getSupportActionBar() {
        if (mActivity != null) {
            return mActivity.getSupportActionBar();
        }
        return null;
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

    public CBlock setTag(String tag) {
        mTarget = tag;
        return  this;
    }

    public String getTag() {
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
        if (mContainer == null) {
            mContainer = container;
        }

        if (!isInflated() && mContentLayoutResId > 0) {
            setContentView(mContentLayoutResId);
        }
    }

    public void setContainerId(int viewId) {
        mContainerId = viewId;
    }

    public int getContainerId() {
        return mContainerId;
    }

    public ViewGroup getContainer() {
        return mContainer;
    }

    public View getContentView() {
        return mContentView;
    }

    public int getState() {
        return mStateFlag;
    }

    public void setState(int state) {
        mStateFlag = state;
    }

    void dispatchUpdateStateTo(int state) {
        setState(state);
        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                sub.dispatchUpdateStateTo(state);
            }
        }
    }

    void performSaveInstanceState(Bundle outState) {
        onSaveInstanceState(outState);

        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            SparseArray<Parcelable> subStates = new SparseArray<Parcelable>();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                if (sub.getId() > NO_ID) {
                    Parcelable subState = sub.saveAllState();
                    subStates.put(sub.getId(), subState);
                }

            }
            if (subStates.size() > 0) {
                outState.putSparseParcelableArray(SUB_TAG, subStates);
            }
        }
    }

    void performRestoreInstanceState(Bundle savedInstanceState) {
        onRestoreInstanceState(savedInstanceState);
    }

    static CBlock instantiate(Context context, SavedState savedState) {
        if (savedState == null) {
            return null;
        }
        final String className = savedState.info.className;
        if (TextUtils.isEmpty(className)) {
            return null;
        }

        CBlock block = null;
        try {
            Class<?> clazz = sClass.get(className);
            if (clazz == null) {
                clazz = context.getClassLoader().loadClass(className);
                sClass.put(className, clazz);
            }
            block = (CBlock) clazz.newInstance();
            block.restoreAllState(savedState);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return block;
    }

    void performAttach(CBlockActivity activity) {
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

                final int sid = sub.getId();
                Parcelable sss = null; //subblock saved state
                if (mSavedSubBlockState != null && sid > NO_ID) {
                    sss = mSavedSubBlockState.get(sid);
                }
                Bundle subState = null;
                if (sss != null && sss instanceof SavedState) {
                    subState = ((SavedState) sss).savedInstanceState;
                }
                sub.performCreate(subState);
                sub.restoreAllState(sss);
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
        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                sub.performDetach();
            }
        }
    }

    void performNewIntent(CBlockIntent intent) {
        onNewIntent(intent);
        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                sub.performNewIntent(intent);
            }
        }
    }

    void flagExceptIntercepte(boolean except) {
        if (except) {
            mFlag |= FLAG_EXPECT_INTERCEPTE;
        } else {
            mFlag &= ~FLAG_EXPECT_INTERCEPTE;
        }
    }

    boolean isExceptIntercepte() {
        return (mFlag & FLAG_EXPECT_INTERCEPTE) != 0;
    }

    void flagRoot(boolean root) {
        if (root) {
            mFlag |= FLAG_ROOT;
        } else {
            mFlag &= ~FLAG_ROOT;
        }
    }

    boolean isRoot() {
        return (mFlag & FLAG_ROOT) != 0;
    }

    void flagPrepared(boolean prepared) {
        if (prepared) {
            mFlag |= FLAG_PREPARED;
        } else {
            mFlag &= ~FLAG_PREPARED;
        }
    }

    boolean isPrepared() {
        return (mFlag & FLAG_PREPARED) != 0;
    }

    void flagWaitForResult(boolean wait) {
        if (wait) {
            mFlag |= FLAG_WAIT_FOR_RESULT;
        } else {
            mFlag &= ~FLAG_WAIT_FOR_RESULT;
        }
    }

    boolean isWaitingForResult() {
        return (mFlag & FLAG_WAIT_FOR_RESULT) != 0;
    }

    void flagDetached(boolean detached) {
        if (detached) {
            mFlag |= FLAG_DETACHED;
        } else {
            mFlag &= FLAG_DETACHED;
        }
    }

    boolean isDetached() {
        return (mFlag & FLAG_DETACHED) != 0;
    }

    void flagInflated(boolean inflated) {
        if (inflated) {
            mFlag |= FLAG_INFLATED;
        } else {
            mFlag &= ~FLAG_INFLATED;
        }
    }

    boolean isInflated () {
        return (mFlag & FLAG_INFLATED) != 0;
    }

    void flagTemp(boolean temp) {
        if (temp) {
            mFlag |= FLAG_IS_TEMP;
        } else {
            mFlag &= ~FLAG_IS_TEMP;
        }
    }

    boolean isTemp() {
        return (mFlag & FLAG_IS_TEMP) != 0;
    }

    void flagHandleIntercepted(boolean handle) {
        if (handle) {
            mFlag |= FLAG_HANDLE_INTERCEPTED;
        } else {
            mFlag &= ~FLAG_HANDLE_INTERCEPTED;
        }
    }

    boolean isHandledIntercepted() {
        return (mFlag & FLAG_HANDLE_INTERCEPTED) != 0;
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

    public void startBlock(CBlockIntent intent) {
        startBlockForResult(intent, -1);
    }

    public void startBlockForResult(CBlockIntent intent, int requestCode) {
        if (mActivity != null) {
            mActivity.startBlockForResult(intent, requestCode);
        }
    }

    public void startBlcokImmediately(CBlockIntent intent) {
        if (mActivity != null) {
            mActivity.startBlockImmediately(intent);
        }
    }

    public void startBlcokForResultImmediately(CBlockIntent intent, int requestCode) {
        if (mActivity != null) {
            mActivity.startBlockForResultImmediately(intent, requestCode);
        }
    }

    public void startActivity(Intent intent) {
        if (mActivity != null) {
            mActivity.startActivity(intent);
        }
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        if (mActivity != null) {
            mActivity.startActivityForResult(intent, requestCode);
        }
    }

    public void startActivityToBlock(Intent intent, CBlockIntent blockIntent) {
        if (intent != null) {
            intent.putExtra(INTENT_TAG, blockIntent);
        }
        startActivity(intent);
    }

    public void onResumeForResult(CBlock from, int requestCode, int resultCode, Bundle resultArgs) {
    }

    public void performResumeForResult(CBlock from, int requestCode,
                                       int resultCode, Bundle resultArgs) {
        onResumeForResult(from, requestCode, resultCode, resultArgs);
        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                sub.performResumeForResult(from, requestCode, resultCode, resultArgs);
            }
        }
    }

    public void finish() {
        if (mActivity != null) {
            mActivity.finishCurrentBlock();
        }
    }

    public void finish(int resultCode) {
        setResult(resultCode);
        finish();
    }

    public void finishActivity() {
        if (mActivity != null) {
            mActivity.finish();
        }
    }

    public CBlock setResult(int resultCode) {
        mResultCode = resultCode;
        return this;
    }

    public CBlock setResult(int resultCode, Bundle args) {
        mResultCode = resultCode;
        mResultArgs = args;
        return this;
    }

    public CBlock setBlockInterceptor(BlockInterceptor interceptor) {
        mInterceptor = interceptor;
        return this;
    }

    public int getResultCode() {
        return mResultCode;
    }

    public Bundle getResultArgs() {
        return mResultArgs;
    }

    boolean dispatchBackButtonClick() {
        if (onBackButtonClick()) {
            return true;
        }

        boolean res = false;
        if (mSubBlocks != null) {
            final int N = mSubBlocks.size();
            for (int i=0; i<N; i++) {
                final CBlock sub = mSubBlocks.get(i);
                res = sub.onBackButtonClick();
                if (res) {
                    break;
                }
            }
        }
        return res;
    }

    public boolean onBackButtonClick() {
        if (isTemp()) {
            finish(RESULT_CANCEL);
            return true;
        }
        return false;
    }

    public CBlock setArguments(Bundle args) {
        mArgs = args;
        return this;
    }

    public Bundle getArguments() {
        return mArgs;
    }

    public CBlock setBlockIntent(CBlockIntent intent) {
        mBlockIntent = intent;
        return this;
    }

    public CBlockIntent getBlockIntent() {
        return mBlockIntent;
    }

    public Animation onCreateEnterAnimationForPush(CBlock fromBlock) {
        return null;
    }

    public Animation onCreateExitAnimationForPause(CBlock toBlock) {
        return null;
    }

    public Animation onCreateEnterAnimationForResume(CBlock exitBlock) {
        return null;
    }

    public Animation onCreateExitAnimationForPop(CBlock resumeBlock) {
        return null;
    }

    public CBlock addBlockingView(int containerId, CBlockingView blockingView) {
        return this;
    }

    public CBlock replaceBlockingView(int containerId, CBlockingView blockingView) {
        return this;
    }

    public CBlock removeBlockingView(CBlockingView blockingView) {
        return this;
    }

    public Intent getIntent() {
        return mActivity.getIntent();
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return mActivity.registerReceiver(receiver, filter);
    }

    public void unregisterReceiver(BroadcastReceiver receiver) {
        mActivity.unregisterReceiver(receiver);
    }

    public ContentResolver getContentResolver() {
        return mActivity.getContentResolver();
    }

    public void startService(Intent serviceIntent) {
        mActivity.startService(serviceIntent);
    }

    public void stopService(Intent name) {
        mActivity.stopService(name);
    }

    public boolean bindService(Intent serviceIntent, ServiceConnection conn, int flag) {
        return mActivity.bindService(serviceIntent, conn, flag);
    }

    public void unbindService(ServiceConnection conn) {
        mActivity.unbindService(conn);
    }

    public boolean onPrepareStartBlock(CBlockIntent intent) {
        return true;
    }

    public boolean performPrepareStartBlock(CBlockIntent intent) {

        boolean goOn = onPrepareStartBlock(intent);
        flagExceptIntercepte(!goOn);

        if (goOn) {
            if (mSubBlocks == null) {
                return goOn;
            }

            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                goOn = sub.performPrepareStartBlock(intent);
                if (!goOn) {
                    break;
                }
            }
        }
        return goOn;
    }

    public boolean dispatchIntercepteProcess(CBlockIntent intent) {

        boolean handle = false;
        if (isExceptIntercepte()) {
            handle = mInterceptor != null && mInterceptor.onProcess(this, intent);
        }

        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                handle = sub.dispatchIntercepteProcess(intent);
                if (handle) {
                    break;
                }
            }
        }

        return handle;
    }

    public void dispatchConfigurationChanged(Configuration newConfig) {
        onConfigurationChanged(newConfig);

        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                sub.dispatchConfigurationChanged(newConfig);
            }
        }
    }

    public void dispatchLowMemory() {
        onLowMemory();

        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                sub.dispatchLowMemory();
            }
        }
    }

    public void onScreenOn() {
    }

    public void dispatchScreenOn() {
        onScreenOn();

        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                sub.dispatchScreenOn();
            }
        }
    }

    public void onScreenOff() {
    }

    public void dispatchScreenOff() {
        onScreenOff();

        if (mSubBlocks != null) {
            final int size = mSubBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock sub = mSubBlocks.get(i);
                sub.dispatchScreenOff();
            }
        }
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

    Parcelable saveAllState() {
        SavedState state = new SavedState();
        state.id = mId;
        state.tag = mTarget;
        state.title = mTitle;
        state.containerId = mContainerId;
        state.contentLayoutResId = mContentLayoutResId;
        state.info = mBlockInfo;
        state.intent = mBlockIntent;
        state.resultCode = mResultCode;
        state.resultArgs = mResultArgs;
        state.flag = mFlag;
        if (mPendingActions != null && !mPendingActions.isEmpty()) {
            final int size = mPendingActions.size();
            state.pendingActions = new Parcelable[size];
            Iterator<CBlock> i = mPendingActions.iterator();
            int index = 0;
            while (i.hasNext()) {
                final CBlock b = i.next();
                state.pendingActions[index] = b.saveAllState();
                index++;
            }
        }
        Bundle instanceState = new Bundle();
        performSaveInstanceState(instanceState);
        if (!instanceState.isEmpty()) {
            state.savedInstanceState = instanceState;
        }
        instanceState = null;
        mSavedState = null;
        return state;
    }

    void restoreAllState(Parcelable savedState) {
        if (!(savedState instanceof SavedState)) {
            return;
        }

        SavedState ss = (SavedState) savedState;
        mId = ss.id;
        mTarget = ss.tag;
        mTitle = ss.title;
        mContainerId = ss.containerId;
        mContentLayoutResId = ss.contentLayoutResId;
        mBlockInfo = ss.info;
        mBlockIntent = ss.intent;
        mResultCode = ss.resultCode;
        mResultArgs = ss.resultArgs;
        mFlag = ss.flag;
        if (mBlockIntent != null) {
            mArgs = mBlockIntent.getArguments();
        }
        Parcelable[] pa = ss.pendingActions;
        if (pa != null) {
            final int size = pa.length;
            if (mPendingActions == null) {
                mPendingActions = new LinkedList<CBlock>();
            } else {
                mPendingActions.clear();
            }
            for (int i=0; i<size; i++) {
                final SavedState pss = (SavedState) pa[i];
                CBlock b = CBlock.instantiate(mActivity, pss);
                mPendingActions.addLast(b);
            }
        }

        Bundle savedInstanceState = ss.savedInstanceState;
        if (savedInstanceState != null) {
            performRestoreInstanceState(savedInstanceState);
            mSavedSubBlockState = savedInstanceState.getSparseParcelableArray(SUB_TAG);
        }

        mSavedState = ss;
    }

    public static class SavedState implements Parcelable {

        CBlockManager.CBlockInfo info = null;
        CBlockIntent intent = intent = null;
        int id = CBlock.NO_ID;
        String tag = null;
        CharSequence title = null;
        int containerId = 0;
        int contentLayoutResId = 0;
        int resultCode = NO_RESULT;
        int flag = 0;
        Bundle resultArgs = null;
        Parcelable[] pendingActions = null;
        Bundle savedInstanceState = null;

        public SavedState() {}

        public SavedState(Parcel in) {
            super();

            id = in.readInt();
            tag = in.readString();
            if (in.readInt() > 0) {
                title = in.readString();
            }
            containerId = in.readInt();
            contentLayoutResId = in.readInt();
            resultCode = in.readInt();
            flag = in.readInt();

            if (in.readInt() > 0) {
                resultArgs = in.readBundle();
            }
            if (in.readInt() > 0) {
                info = in.readParcelable(null);
            }
            if (in.readInt() > 0) {
                intent = in.readParcelable(null);
            }
            if (in.readInt() > 0) {
                pendingActions = in.readParcelableArray(null);
            }

        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flag) {

            parcel.writeInt(id);
            parcel.writeString(tag);
            if (title != null) {
                parcel.writeInt(1);
                parcel.writeString(title.toString());
            } else {
                parcel.writeInt(0);
            }
            parcel.writeInt(containerId);
            parcel.writeInt(contentLayoutResId);
            parcel.writeInt(resultCode);
            parcel.writeInt(flag);

            if (resultArgs != null) {
                parcel.writeInt(1);
                parcel.writeBundle(resultArgs);
            } else {
                parcel.writeInt(0);
            }
            if (info != null) {
                parcel.writeInt(1);
                parcel.writeParcelable(info, flag);
            } else {
                parcel.writeInt(0);
            }
            if (intent != null) {
                parcel.writeInt(1);
                parcel.writeParcelable(intent, flag);
            } else {
                parcel.writeInt(0);
            }
            if (pendingActions == null || pendingActions.length == 0) {
                parcel.writeInt(0);
            } else {
                parcel.writeInt(1);
                parcel.writeParcelableArray(pendingActions, flag);
            }

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
