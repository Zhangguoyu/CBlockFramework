package com.zhangguoyu.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.zhangguoyu.widget.CBlockingView;
import com.zhangguoyu.widget.CMenu;
import com.zhangguoyu.widget.CMenuItem;

import java.util.ArrayList;
import java.util.List;

public class CBlock {
	
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
	
	private Activity mActivity = null;
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
	
	private int mStateFlag = 0;

    void attachToActivity(CBlockActivity activity) {
        mActivity = activity;
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
                block.attachToParent(this, false);
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

    public void attachToParent(CBlock parent, boolean syncLifecycle) {
        if (parent == null) {
            return;
        }

        parent.addSubBlock(this);
        mParent = parent;
        mRoot = parent.getRoot();

        mStateFlag = requestCurrentState();
        mActivity = parent.getActivity();
        mSavedInstanceState = parent.getSavedInstanceState();
        mLayoutInflater = parent.getLayoutInflater();
        mArgs = parent.getArguments();

        if (syncLifecycle) {

            if ((mStateFlag & FLAG_STATE_ON_ATTACH) != 0) {
                onAttach(mActivity);
            }

            if ((mStateFlag & FLAG_STATE_ON_CREATE_VIEW) != 0) {
                mContentView = onCreateView(mLayoutInflater, mContainer, mSavedInstanceState);
                if (mContentView != null) {
                    ViewGroup p = (ViewGroup) mContentView.getParent();
                    if (p != null) {
                        p.removeView(mContentView);
                    }
                    if (mContainer.getChildCount() > 0) {
                        mContainer.removeAllViews();
                    }
                    mContainer.addView(mContentView);
                }
            }

            if ((mStateFlag & FLAG_STATE_ON_CREATE) != 0) {
                onCreate(mSavedInstanceState);
            }

            if ((mStateFlag & FLAG_STATE_ON_ACTIVITY_CREATE) != 0) {
                onActivityCreated(mSavedInstanceState);
            }

            if ((mStateFlag & FLAG_STATE_ON_START) != 0) {
                onStart();
            }

            if ((mStateFlag & FLAG_STATE_ON_RESUME) != 0) {
                onResume();
            }

            if ((mStateFlag & FLAG_STATE_ON_PAUSE) != 0) {
                onPause();
            }

            if ((mStateFlag & FLAG_STATE_ON_STOP) != 0) {
                onStop();
            }

            if ((mStateFlag & FLAG_STATE_ON_DESTROY) != 0) {
                onDestroy();
            }

        }

    }

    public void detachFromParent() {
        if (mParent != null) {
            mParent.removeSubBlock(this);
            mParent = null;
        }
        mRoot = null;
    }
	
	void onAttach(Activity activity) {
		mActivity = activity;
//        Log.d("CBlock", "@@@ onAttach " + this);
        mStateFlag |= FLAG_STATE_ON_ATTACH;
	}

    void dispatchOnAttach(Activity activity) {
        onAttach(activity);

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.dispatchOnAttach(activity);
        }
    }
	
	void onActivityCreated(Bundle savedInstanceState) {
        mStateFlag |= FLAG_STATE_ON_ACTIVITY_CREATE;
//        Log.d("CBlock", "@@@ onActivityCreated " + this);
	}

    void dispatchOnActivityCreated(Bundle savedInstanceState) {
        onActivityCreated(savedInstanceState);

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.dispatchOnActivityCreated(savedInstanceState);
        }
    }
	
	public void onCreate(Bundle savedInstanceState) {
        mStateFlag |= FLAG_STATE_ON_CREATE;
//        Log.d("CBlock", "@@@ onCreate " + this);
	}

    public void dispatchOnCreate(Bundle savedInstanceState) {
        onCreate(savedInstanceState);

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.dispatchOnCreate(savedInstanceState);
        }
    }
	
	View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
        mStateFlag |= FLAG_STATE_ON_CREATE_VIEW;
		mViewInflated = true;
        mLayoutInflater = inflater;

//		if (mContentView == null) {
//			if (mContentLayoutResId > 0) {
//				mContentView = mLayoutInflater.inflate(mContentLayoutResId, container, false);
//			}
//		}
//        Log.d("CBlock", "@@@ onCreateView " + this);
		return mContentView;
	}

    View dispatchOnCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = onCreateView(inflater, mContainer, savedInstanceState);

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return view;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.dispatchOnCreateView(inflater, subBlock.getContainerView(), savedInstanceState);
        }

        return view;
    }
	
	public void onDestroy() {
        mStateFlag |= FLAG_STATE_ON_DESTROY;
	}

    public void dispatchOnDestroy() {

        onDestroy();

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.dispatchOnDestroy();
        }
    }
	
	void onDestroyView() {
        mStateFlag |= FLAG_STATE_ON_DESTROY_VIEW;
	}

    void dispatchOnDestroyView() {

        onDestroyView();

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return ;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.dispatchOnDestroyView();
        }
    }
	
	void onDetach() {
        mStateFlag |= FLAG_STATE_ON_DETACH;
	}

    void dispatchOnDetach() {

        onDetach();

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.dispatchOnDetach();
        }
    }
	
	public void onLowMemory() {
	}

    public void dispatchOnLowMemory() {

        onLowMemory();

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.dispatchOnLowMemory();
        }
    }
	
	public void onPause() {
        mStateFlag |= FLAG_STATE_ON_PAUSE;
	}

    public void dispatchOnPause() {

        onPause();

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.dispatchOnPause();
        }
    }
	
	public void onResume() {
        mStateFlag |= FLAG_STATE_ON_RESUME;
//        Log.d("CBlock", "@@@ onResume " + this);
	}

    public void dispatchOnResume() {

        onResume();

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.dispatchOnResume();
        }
    }
	
	public void onStart() {
        mStateFlag |= FLAG_STATE_ON_START;
//        Log.d("CBlock", "@@@ onStart " + this);
	}

    public void dispatchOnStart() {

        onStart();

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.dispatchOnStart();
        }
    }
	
	public void onStop() {
        mStateFlag |= FLAG_STATE_ON_STOP;
	}

    public void dispatchOnStop() {

        onStop();

        if (mSubBlocks == null || mSubBlocks.isEmpty()) {
            return;
        }

        for (CBlock subBlock : mSubBlocks) {
            subBlock.dispatchOnStop();
        }
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

    public Activity getActivity() {
        return mActivity;
    }

    public LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    public Bundle getSavedInstanceState() {
        return mSavedInstanceState;
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

        public SavedState(Parcel in) {
            super();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {

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
