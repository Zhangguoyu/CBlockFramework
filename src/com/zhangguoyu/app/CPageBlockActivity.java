package com.zhangguoyu.app;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import com.zhangguoyu.widget.CMenu;

public class CPageBlockActivity extends CBlockActivity {

    private static final String META_DATA_KEY_PAGE_NAME = "pageName";
    private static final String KEY_PAGE = "page_index";

    private CPageBlock mPageBlock = null;
    private int mPageXmlResId = 0;
    private boolean mInflated = false;
    private InternalBlockChangedListener mListener = new InternalBlockChangedListener();

    private class InternalBlockChangedListener implements CPageBlock.OnBlockPageChangedListener {

        CPageBlock.OnBlockPageChangedListener mListener = null;

        @Override
        public void onPageSelected(CPageBlock block, CBlock selected, int position) {
            if (selected != null) {
                setCurrentBlock(selected);
            }
            if (mListener != null) {
                mListener.onPageSelected(block, selected, position);
            }
        }
    }

    public void loadBlocksFromResurce(int blockXmlResId) {
        mPageXmlResId = blockXmlResId;
        if (mPageBlock == null) {
            return;
        }
        if (mPageBlock != null) {
            mInflated = true;
            mPageBlock.inflateBlockPagesFromXmlRes(blockXmlResId);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mPageBlock == null) {
            CBlock curr = getBlockManager().getCurrentBlock();
            if (!(curr instanceof CPageBlock)) {
                throw new RuntimeException();
            }
            mPageBlock = (CPageBlock) curr;
        }
    }

    public void setMute(boolean mute) {
        mPageBlock.setMute(mute);
    }

    public CPageBlock getPageBlock() {
        return mPageBlock;
    }

    public int getBlockPageCount() {
        return mPageBlock.getBlockCount();
    }

    public CBlock getBlockPageAt(int position) {
        return mPageBlock.getBlockAt(position);
    }

    public void setOnBlockPageChangedListener(CPageBlock.OnBlockPageChangedListener listener) {
        mListener.mListener = listener;
    }

    public void setOnViewPageChangedListener(ViewPager.OnPageChangeListener listener) {
        mPageBlock.setOnViewPageChangedListener(listener);
    }

    public CPageBlockActivity addBlockPage(int blockId) {
        addBlockPageAtPosition(blockId, -1);
        return this;
    }

    public CPageBlockActivity addBlockPage(String blockTag) {
        addBlockPageAtPosition(blockTag, -1);
        return this;
    }

    public CPageBlockActivity addBlockPage(Class<? extends CBlock> blockClass) {
        addBlockPageAtPosition(blockClass, -1);
        return this;
    }

    public CPageBlockActivity addBlockPageAtPosition(Class<? extends CBlock> blockClass,
            int blockId, String blockTag, CharSequence blockTitle, int contentViewResId) {
        addBlockPageAtPosition(blockClass, blockId, blockTag, blockTitle, contentViewResId, -1);
        return this;
    }

    public CPageBlockActivity addBlockPageAtPosition(int blockId, int position) {
        mPageBlock.addBlockPageAtPosition(blockId, position);
        return this;
    }

    public CPageBlockActivity addBlockPageAtPosition(String blockTag, int position) {
        mPageBlock.addBlockPageAtPosition(blockTag, position);
        return this;
    }

    public CPageBlockActivity addBlockPageAtPosition(Class<? extends CBlock> blockClass, int position) {
        mPageBlock.addBlockPageAtPosition(blockClass, position);
        return this;
    }

    public CPageBlockActivity addBlockPageAtPosition(Class<? extends CBlock> blockClass,
            int id, String tag, CharSequence title, int contentViewResId, int position) {
        mPageBlock.addBlockPageAtPosition(blockClass, id, tag, title, contentViewResId, position);
        return this;
    }

    public CPageBlockActivity removeBlockPage(int blockId) {
        mPageBlock.removeBlockPage(blockId);
        return this;
    }

    public CPageBlockActivity removeBlockPage(String blockTag) {
        mPageBlock.removeBlockPage(blockTag);

        return this;
    }

    public CPageBlockActivity removeBlockPage(Class<? extends CBlock> blockClass) {
        mPageBlock.removeBlockPage(blockClass);
        return this;
    }

    public CPageBlockActivity removeBlockPageAtPosition(int position) {
        mPageBlock.removeBlockPageAtPosition(position);
        return this;
    }

    public void clear() {
        mPageBlock.clear();
    }

    public void selectPage(int pageNumber) {
        mPageBlock.selectPage(pageNumber);
    }

    public void selectPageSmoothly(int pageNumber) {
        mPageBlock.selectPageSmoothly(pageNumber);
    }

    @Override
    protected void onDispatchCreateOptionsMenu() {
    }

    @Override
    protected void onDispatchCreateNavigationMenu() {
    }

    @Override
    public boolean onCreateOptionsMenu(CMenu menu) {
        CBlock current = mPageBlock.getCurrentBlock();
        if (current != null) {
            return current.onCreateOptionsMenu(menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onCreateNavigationMenu(CMenu menu) {
        CBlock current = mPageBlock.getCurrentBlock();
        if (current != null) {
            return current.onCreateNavigationMenu(menu);
        }
        return super.onCreateNavigationMenu(menu);
    }

    private void dispatchCreateMenu() {
        dispatchCreateOptionsMenu();
        dispatchCreateNavigationMenu();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("CPageBlockActivity", "@@@ onSaveInstanceState " + outState);
        if (mPageBlock != null) {
            outState.putInt(KEY_PAGE, mPageBlock.getCurrentIndex());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (mPageBlock != null && savedInstanceState != null) {
            final int index = savedInstanceState.getInt(KEY_PAGE, 0);
            mPageBlock.selectPage(index);
        }
    }

}
