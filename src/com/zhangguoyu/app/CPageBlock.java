package com.zhangguoyu.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.zhangguoyu.widget.CBlockPager;

/**
 * Created by zhangguoyu on 13-5-24.
 */
public class CPageBlock extends CBlock {

    private static final String LOG_TAG = "CPageBlock";

    private CBlockPager mPager = null;

    public void addBlock(CBlock block, Bundle args) {

        if (!checkContext()) {
            Log.w(LOG_TAG, "Can not add block, cause context is invalid, " +
                    "it must be a instance of CBlockActivity");
            return;
        }

        if (mPager == null) {
            mPager = new CBlockPager(getContext());
            final CBlockActivity activity = (CBlockActivity) getActivity();
            mPager.setSupportFragmentManager(activity.getSupportFragmentManager());
            setContentView(mPager);
        }

        mPager.addBlock(block, args);

    }

    public void addBlocksFromXml(int xmlResId) {

        if (!checkContext()) {
            Log.w(LOG_TAG, "Can not add block, cause context is invalid, " +
                    "it must be a instance of CBlockActivity");
            return;
        }

        if (mPager == null) {
            mPager = new CBlockPager(getContext());
            final CBlockActivity activity = (CBlockActivity) getActivity();
            mPager.setSupportFragmentManager(activity.getSupportFragmentManager());
            setContentView(mPager);
        }

        mPager.addBlockFromResource(xmlResId);
    }

    public void clear() {
        if (mPager != null) {
            mPager.clearBlocks();
        }
    }

    public void removeBlock(CBlock block) {
        if (mPager != null) {
            mPager.removeBlock(block);
        }
    }

    public void removeAt(int position) {
        if (mPager != null) {
            mPager.removeBlockAt(position);
        }
    }

    public void setCurrentItem(int item) {
        if (mPager != null) {
            mPager.setCurrentItem(item);
        }
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        if (mPager != null) {
            mPager.setCurrentItem(item, smoothScroll);
        }
    }

    public int getCurrentItem() {
        if (mPager == null) {
            return 0;
        }
        return mPager.getCurrentItem();
    }

    private boolean checkContext() {
        final Activity activity = getActivity();
        if (!(activity instanceof CBlockActivity)) {
            return false;
        }

        return true;
    }
}
