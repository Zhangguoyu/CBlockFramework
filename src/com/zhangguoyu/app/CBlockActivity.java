package com.zhangguoyu.app;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

public class CBlockActivity extends CActivity {

	private static final String BLOCK_TAG = "cn.emoney.level2.block";
    private static final String META_DATA_KEY_MANIFEST = "blockManifest";
    private CBlockManager mBlockManager = null;

    private CBlock mCurrentBlock = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        mBlockManager = CBlockManager.newInstance(this);
        getSupportActionBar().setDisplayShowBackButtonEnable(false);

		try {
			ActivityInfo info = getPackageManager().getActivityInfo(
					getComponentName(), PackageManager.GET_META_DATA);
			final Bundle metaData = info.metaData;
            int manifestResId = metaData.getInt(META_DATA_KEY_MANIFEST);

            mBlockManager.parseBlockMainfestFromXml(manifestResId);

		} catch (NameNotFoundException e) {
			e.printStackTrace();
        }

        mBlockManager.dispatchCreate();
//        if (savedInstanceState != null) {
//            Parcelable state = savedInstanceState.getParcelable(BLOCK_TAG);
//            mBlockManager.restoreState(state);
//        }
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
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable saveState = mBlockManager.saveState();
        if (saveState != null) {
            outState.putParcelable(BLOCK_TAG, saveState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void startBlock(Object tag, Bundle args) {
        CBlockManager.CBlockInfo blockInfo = mBlockManager.findBlockInfoWithTag(tag);
        if (blockInfo == null) {
            throw new CBlockNotFoundException();
        }
        final String className = blockInfo.className;
        if (TextUtils.isEmpty(className)) {
            throw new CBlockNotFoundException();
        }

        try {
            startBlock(getClassLoader().loadClass(className), args);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void startBlock(int id, Bundle args) {

        CBlockManager.CBlockInfo blockInfo = mBlockManager.findBlockInfoById(id);
        if (blockInfo == null) {
            throw new CBlockNotFoundException();
        }

        final String className = blockInfo.className;
        if (TextUtils.isEmpty(className)) {
            throw new CBlockNotFoundException();
        }

        try {
            startBlock(getClassLoader().loadClass(className), args);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void startBlock(Class<?> blockClass, Bundle args) {
        try {
            Object o = blockClass.newInstance();
            if (o != null && (o instanceof CBlock)) {

                final CBlock target = (CBlock) o;
                target.setArguments(args);

            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void finishBlock() {

    }

    @Override
    public void onBackButtonClick() {
        super.onBackButtonClick();

        if (mCurrentBlock != null) {
            mCurrentBlock.onBackButtonClick();
        }
    }

    public CBlockManager getBlockManager() {
        return mBlockManager;
    }

    void setCurrentBlock(CBlock current) {
        mCurrentBlock = current;
    }

    void attachMainBlock(CBlock main) {
        mCurrentBlock = main;
        main.attachToActivity(this);
    }

    public CBlock getCurrentBlock() {
        return mCurrentBlock;
    }

}
