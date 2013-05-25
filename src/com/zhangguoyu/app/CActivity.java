package com.zhangguoyu.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import com.zhangguoyu.widget.*;
import com.zhangguoyu.widget.CActionBar.CTab;
import com.zhangguoyu.demo.actionbar.R;

public class CActivity extends FragmentActivity {
	
	private FrameLayout mMainFrame = null;
	private CActionBarImpl mActionBarImpl = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ensureInflateFrame();
	}
	
	@Override
	public void setContentView(int layoutResID) {
		getLayoutInflater().inflate(layoutResID, mMainFrame);
	}
	
	@Override
	public void setContentView(View view) {
		mMainFrame.addView(view);
	}
	
	@Override
	public void setContentView(View view, LayoutParams params) {
		mMainFrame.addView(view, params);
	}
	
	private void ensureInflateFrame() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.cframe);
		mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
		prepareActionBar();
	}
	
	private void prepareActionBar() {
		mActionBarImpl = CActionBarImpl.newDefault(this);
		Button back = new Button(this);
		back.setText("<");
		mActionBarImpl.setBackButton(back);
		mActionBarImpl.setLogo(R.drawable.ic_launcher);
		mActionBarImpl.setTitle("Demo");
        mActionBarImpl.setOnBackButtonClickListener(new BackButtonClickListener());
		mActionBarImpl.addTab(mActionBarImpl.buildTab().setTitle("Demo"))
			.addTab(mActionBarImpl.buildTab().setTitle("Demo").setIcon(R.drawable.ic_launcher), 0)
			.addTab(mActionBarImpl.buildTab().setTitle("Demo"));

        onDispatchCreateOptionsMenu();
        onDispatchCreateNavigationMenu();
	}
	
	public void onTabSelected(CTab selectedTab) {
	}

    protected void onDispatchCreateOptionsMenu() {
        dispatchCreateOptionsMenu();
    }

    protected void onDispatchCreateNavigationMenu() {
        dispatchCreateNavigationMenu();
    }
	
	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
		mActionBarImpl.setTitle(title);
	}
	
	@Override
	public void setTitle(int titleId) {
		super.setTitle(titleId);
		mActionBarImpl.setTitle(titleId);
	}

    public void dispatchCreateOptionsMenu() {
        CMenu menu = mActionBarImpl.newMenu();
        if (onCreateOptionsMenu(menu)) {
            mActionBarImpl.inflatdOptionsBarByMenu(menu);
        }
    }
	
	public void dispatchCreateNavigationMenu() {
        CMenu menu = mActionBarImpl.newMenu();
		if(onCreateNavigationMenu(menu)) {
			mActionBarImpl.inflatedNavigationBarByMenu(menu);
		}
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

		return true;
	}
	
	public void onOptionsMenuItemSelected(CMenuItem item) {
	}

    public void onBackButtonClick() {
    }

    public CActionBar getSupportActionBar() {
        return mActionBarImpl;
    }

    private final class BackButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            onBackButtonClick();
        }
    }
}
