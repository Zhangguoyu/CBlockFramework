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
    private CBlockManager mBlockManger = null;

    private CBlock mCurrentBlock = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        mBlockManger = CBlockManager.newInstance(this);
        getSupportActionBar().setDisplayShowBackButtonEnable(false);

		try {
			ActivityInfo info = getPackageManager().getActivityInfo(
					getComponentName(), PackageManager.GET_META_DATA);
			final Bundle metaData = info.metaData;
            int manifestResId = metaData.getInt(META_DATA_KEY_MANIFEST);

            mBlockManger.parseBlockMainfestFromXml(manifestResId);

		} catch (NameNotFoundException e) {
			e.printStackTrace();
        }

        if (savedInstanceState != null) {
            Parcelable state = savedInstanceState.getParcelable(BLOCK_TAG);
            mBlockManger.restoreState(state);
        }

        mBlockManger.dispatchCreate();
	}

    @Override
    protected void onStart() {
        super.onStart();
        mBlockManger.dispatchStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBlockManger.dispatchResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBlockManger.dispatchPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBlockManger.dispatchStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBlockManger.dispatchDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable saveState = mBlockManger.saveState();
        if (saveState != null) {
            outState.putParcelable(BLOCK_TAG, saveState);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void startBlock(Object tag, Bundle args) {
        CBlockManager.CBlockInfo blockInfo = mBlockManger.findBlockInfoWithTag(tag);
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

        CBlockManager.CBlockInfo blockInfo = mBlockManger.findBlockInfoById(id);
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
        return mBlockManger;
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

	private static class FrameFragment extends CBlockFragment {

		static FrameFragment newInstance() {
			return new FrameFragment();
		}

	}

}
