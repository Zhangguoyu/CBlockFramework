package com.zhangguoyu.app;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;

import android.widget.FrameLayout;
import com.zhangguoyu.widget.CFrameLayoutBlock;
import com.zhangguoyu.widget.CMenu;
import com.zhangguoyu.widget.CMenuItem;

public class CBlockFragment extends Fragment {

    private static final String LOG_TAG = "CBlockFragment";
	
	private CBlock mBlock = null;
    private CFrameLayoutBlock mBlockRootLayout = null;
	
	public void wrap(CBlock block) {
		mBlock = block;
		mBlock.attachToFragment(this);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
        mBlockRootLayout = new CFrameLayoutBlock(activity.getBaseContext());
//        mBlockRootLayout.setBlock(mBlock);
        mBlock.setContainer(mBlockRootLayout);
		mBlock.dispatchOnAttach(activity);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mBlock.dispatchOnActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        mBlock.dispatchOnActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
        mBlock.dispatchOnConfigurationChanged(newConfig);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//mBlock.dispatchOnCreate(savedInstanceState);
        Log.d(LOG_TAG, "@@@ onCreate " + mBlock.getClass().getSimpleName());
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mBlock.dispatchOnCreateView(inflater, mBlockRootLayout, savedInstanceState);
        mBlock.dispatchOnCreate(savedInstanceState);
        View view = mBlock.getContentView();
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            mBlockRootLayout.addView(view, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }
        ViewParent parent = mBlockRootLayout.getParent();
        if (parent != null && parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(mBlockRootLayout);
        }
		return mBlockRootLayout;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//mBlock.dispatchOnDestroy();

        Log.d(LOG_TAG, "@@@ onDestroy " + mBlock.getClass().getSimpleName());
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
        mBlock.dispatchOnDestroy();
		mBlock.dispatchOnDestroyView();
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		mBlock.dispatchOnDetach();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		mBlock.dispatchOnHiddenChanged(hidden);
	}
	
	@Override
	public void onInflate(Activity activity, AttributeSet attrs,
			Bundle savedInstanceState) {
		super.onInflate(activity, attrs, savedInstanceState);
		mBlock.dispatchOnInflate(activity, attrs, savedInstanceState);
	}
	
	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mBlock.dispatchOnLowMemory();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		mBlock.dispatchOnPause();

        Log.d(LOG_TAG, "@@@ onPause " + mBlock.getClass().getSimpleName());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mBlock.dispatchOnResume();

        Log.d(LOG_TAG, "@@@ onResume " + mBlock.getClass().getSimpleName());
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mBlock.dispatchOnSaveInstanceState(outState);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		mBlock.dispatchOnStart();

        Log.d(LOG_TAG, "@@@ onStart " + mBlock.getClass().getSimpleName());
	}
	
	@Override
	public void onStop() {
		super.onStop();
		mBlock.dispatchOnStop();

        Log.d(LOG_TAG, "@@@ onStop " + mBlock.getClass().getSimpleName());
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mBlock.dispatchOnViewCreated(view, savedInstanceState);
	}
	
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		mBlock.dispatchOnViewStateRestored(savedInstanceState);
	}
	
	public boolean onCreateNavigationMenu(CMenu menu) {
		return mBlock.onCreateNavigationMenu(menu);
	}
	
	public boolean onPrepareNavigationMenu(CMenu item) {
		return mBlock.onPrepareNavigationMenu(item);
	}
	
	public void onNavigationMenuItemSelected(CMenuItem item) {
		mBlock.onNavigationMenuItemSelected(item);
	}
	
	public boolean onCreateOptionsMenu(CMenu menu) {
		return mBlock.onCreateOptionsMenu(menu);
	}
	
	public boolean onPrepareOptionsMenu(CMenu menu) {
		return mBlock.onPrepareOptionsMenu(menu);
	}
	
	public void onOptionsMenuItemSelected(CMenuItem item) {
		mBlock.onOptionsMenuItemSelected(item);
	}

    int getCurrentState() {
        return 0;
    }

    public CBlock getBlock() {
        return mBlock;
    }

}
