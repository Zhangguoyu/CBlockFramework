package com.zhangguoyu.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.zhangguoyu.demo.actionbar.R;
import com.zhangguoyu.widget.CFrameLayoutBlock;
import com.zhangguoyu.widget.CMenu;
import com.zhangguoyu.widget.CMenuItem;

public class CBlockActivity extends CActivity implements CBlockManager.OnBlockStackChangedListener {

	private static final String BLOCK_MANAGER_TAG = "activity:block_manager";
    private static final String META_DATA_KEY_MANIFEST = "blockManifest";
    private CBlockManager mBlockManager = null;

    private CBlock mCurrentBlock = null;
    private boolean mNullBackStack = true;

    private Handler mHandler = new Handler();
    private BroadcastReceiver mReceiver = new InternalBroadcastReceiver();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        CFrameLayoutBlock root = new CFrameLayoutBlock(getBaseContext());
        root.setId(R.id.main_frame_block);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setContentView(root, lp);

        mBlockManager = CBlockManager.newInstance(this);
        mBlockManager.addBlockStackChangedListener(this);
        getSupportActionBar().setDisplayShowBackButtonEnable(false);

		try {
			ActivityInfo info = getPackageManager().getActivityInfo(
					getComponentName(), PackageManager.GET_META_DATA);
			final Bundle metaData = info.metaData;
            if (metaData != null) {
                int manifestResId = metaData.getInt(META_DATA_KEY_MANIFEST);
                if (manifestResId > 0) {
                    mBlockManager.parseBlockMainfestFromXml(manifestResId);
                }
            }

		} catch (NameNotFoundException e) {
			e.printStackTrace();
        }

        //step 1:Attach to current context
        mBlockManager.attachToActivity();
        //step 2:restore the saved state
        Bundle savedState = null;
        if (savedInstanceState != null) {
            savedState = savedInstanceState.getBundle(BLOCK_MANAGER_TAG);
        }
        if (savedState != null) {
            mBlockManager.restoreState(savedState);
        }
        //step 3:create
        mBlockManager.dispatchCreate();

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                if (intent != null) {
                    CBlockIntent blockIntent = intent.getParcelableExtra(CBlock.INTENT_TAG);
                    if (blockIntent != null) {
                        startBlock(blockIntent);
                    }
                }
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);

	}

    @Override
    protected void onStart() {
        super.onStart();
        mBlockManager.dispatchStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBlockManager.dispatchResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBlockManager.dispatchPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBlockManager.dispatchStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBlockManager.dispatchDestroy();
        mBlockManager.removeBlockStackChangedListener(this);
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mBlockManager.dispatchConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mBlockManager.dispatchLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle saveState = mBlockManager.saveState();
        if (!saveState.isEmpty()) {
            outState.putBundle(BLOCK_MANAGER_TAG, saveState);
        }
    }

    public void startBlock(CBlockIntent intent) {
        startBlockForResult(intent, -1);
    }

    public void startBlockForResult(CBlockIntent intent, int requestCode) {
        mBlockManager.execStartBlockForResult(R.id.main_frame_block, intent, requestCode, false);
    }

    public void startBlockImmediately(CBlockIntent intent) {
        startBlockForResultImmediately(intent, -1);
    }

    public void startBlockForResultImmediately(CBlockIntent intent, int requestCode) {
        mBlockManager.execStartBlockForResult(R.id.main_frame_block, intent, requestCode, true);
    }

    public void finishCurrentBlock() {
        mBlockManager.execFinish();
    }

    public CBlock findBlockById(int id) {
        return null;
    }

    public CBlock findBlockWithTag(String tag) {
        return null;
    }

    @Override
    public void onBackButtonClick() {
        if (mNullBackStack) {
            super.onBackButtonClick();
        } else {
            if (!mBlockManager.dispatchBackButtonClick()) {
                finishCurrentBlock();
            }
        }
    }

    public CBlockManager getBlockManager() {
        return mBlockManager;
    }

    void setCurrentBlock(CBlock current) {
        mCurrentBlock = current;
    }

    public CBlock getCurrentBlock() {
        return mCurrentBlock;
    }

    @Override
    protected void onDispatchCreateOptionsMenu() {
    }

    @Override
    protected void onDispatchCreateNavigationMenu() {
    }

    @Override
    public boolean onCreateOptionsMenu(CMenu menu) {
        return super.onCreateOptionsMenu(menu)
                || mBlockManager.dispatchCreateOptionsMenu(menu);
    }

    @Override
    public boolean onCreateNavigationMenu(CMenu menu) {
        return super.onCreateNavigationMenu(menu)
                || mBlockManager.dispatchCreateNavigationMenu(menu);
    }

    @Override
    public void onOptionsMenuItemSelected(CMenuItem item) {
        super.onOptionsMenuItemSelected(item);
        mBlockManager.dispatchOptionsMenuItemSelected(item);
    }

    @Override
    public void onNavigationMenuItemSelected(CMenuItem item) {
        super.onNavigationMenuItemSelected(item);
        mBlockManager.dispatchNavigationMenuItemSelected(item);
    }


    @Override
    public void onPush(CBlock block, int size) {
        mNullBackStack = false;
        getSupportActionBar().setDisplayShowBackButtonEnable(true);
    }

    @Override
    public void onPop(CBlock block, int size) {
        mNullBackStack = !(size > 0);
        getSupportActionBar().setDisplayShowBackButtonEnable(size>0);
    }

    private final class InternalBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                mBlockManager.dispatchScreenOn();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                mBlockManager.dispatchScreenOff();
            }
        }
    }
}
