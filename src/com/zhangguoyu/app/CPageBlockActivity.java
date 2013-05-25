package com.zhangguoyu.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.zhangguoyu.demo.actionbar.R;
import com.zhangguoyu.widget.CBlockPager;
import com.zhangguoyu.widget.CMenu;

public class CPageBlockActivity extends CBlockActivity {

    private CBlockPager mBlockPager = null;
    private BlockPageChangeListener mListener = new BlockPageChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBlockPager = new CBlockPager(getBaseContext());
        mBlockPager.setId(R.id.page_frame_id);
        mBlockPager.setSupportFragmentManager(getSupportFragmentManager());
        mBlockPager.setOnPageChangeListener(mListener);

        FrameFragment frame = FrameFragment.newInstance(mBlockPager);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_frame, frame)
                .commit();
    }

    public void loadBlocksFromResurce(int blockXmlResId) {
        mBlockPager.addBlockFromResource(blockXmlResId);
        dispatchCreateMenu();
    }

    public void addBlockToPage(CBlock block, Bundle args) {
        mBlockPager.addBlock(block, args);
    }

    public void setCurrentItem(int item) {
        mBlockPager.setCurrentItem(item);
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        mBlockPager.setCurrentItem(item, smoothScroll);
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener.customListener = listener;
    }

    public void arrowScroll(int direction) {
        mBlockPager.arrowScroll(direction);
    }

    @Override
    protected void onDispatchCreateOptionsMenu() {
    }

    @Override
    protected void onDispatchCreateNavigationMenu() {
    }

    @Override
    public boolean onCreateOptionsMenu(CMenu menu) {
        CBlock current = getCurrentBlock();
        if (current != null) {
            return current.onCreateOptionsMenu(menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onCreateNavigationMenu(CMenu menu) {
        CBlock current = getCurrentBlock();
        if (current != null) {
            return current.onCreateNavigationMenu(menu);
        }
        return super.onCreateNavigationMenu(menu);
    }

    private CBlockFragment getCurrentFragment() {
        PagerAdapter adapter = mBlockPager.getAdapter();
        if (adapter != null && (adapter instanceof FragmentStatePagerAdapter)) {
            Fragment current = ((FragmentStatePagerAdapter) adapter).getItem(
                    mBlockPager.getCurrentItem());
            if (current instanceof CBlockFragment) {
                return (CBlockFragment) current;
            }
        }
        return null;
    }

    private void findCurrentBlock() {
        CBlockFragment curr = getCurrentFragment();
        if (curr != null) {
            setCurrentBlock(curr.getBlock());
        }
    }

    private void dispatchCreateMenu() {
        findCurrentBlock();
        dispatchCreateOptionsMenu();
        dispatchCreateNavigationMenu();
    }

    private class BlockPageChangeListener implements ViewPager.OnPageChangeListener {

        private ViewPager.OnPageChangeListener customListener = null;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            if (customListener != null) {
                customListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            dispatchCreateMenu();
            if (customListener != null) {
                customListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

            if (customListener != null) {
                customListener.onPageScrollStateChanged(state);
            }
        }
    }

    public static class FrameFragment extends Fragment {

        private static View mContentView = null;

        static FrameFragment newInstance(View contentView) {
            mContentView = contentView;
            return new FrameFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ViewParent p = mContentView.getParent();
            if (p != null && p instanceof ViewGroup) {
                ((ViewGroup) p).removeView(mContentView);
            }
            return mContentView;
        }
    }
}
