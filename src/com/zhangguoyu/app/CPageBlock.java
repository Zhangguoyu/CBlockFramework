package com.zhangguoyu.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import com.zhangguoyu.demo.actionbar.R;
import com.zhangguoyu.widget.CMenu;
import com.zhangguoyu.widget.CNestedViewPager;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhangguoyu on 13-5-24.
 */
public class CPageBlock extends CBlock {

    private static final String LOG_TAG = "CPageBlock";

    public interface OnBlockPageChangedListener {

        public void onPageSelected(CPageBlock block, CBlock selected, int position);

    }

    private static final HashMap<String, Class<?>> sClassMap =
            new HashMap<String, Class<?>>();
    private static final String XML_BLOCK_PAGE = "blockpager";
    private static final String XML_BLOCK_ITEM = "page";

    private CNestedViewPager mViewPager = null;
    private InternalBlockPagerAdapter mAdapter = null;

    private ArrayList<CBlock> mBlocks = null;
    private CBlock mActivated = null;
    private CBlockManager mBlockManager = null;

    private int mLastPosition = 0;
    private int mLastOffsetPixel = 0;

    private OnBlockPageChangedListener mBlockPageListener = null;
    private ViewPager.OnPageChangeListener mViewPageListener = null;

    private ViewPager.OnPageChangeListener mPageListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float offset, int offsetPixel) {
            if (mViewPageListener != null) {
                mViewPageListener.onPageScrolled(position, offset, offsetPixel);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mViewPageListener != null) {
                mViewPageListener.onPageSelected(position);
            }

            CBlock selected = null;
            if (mBlocks != null && mBlocks.size() > position && position > -1) {
                selected = mBlocks.get(position);
            }
            mActivated = selected;
            getActivity().dispatchCreateOptionsMenu();
            getActivity().dispatchCreateNavigationMenu();

            if (mBlockPageListener != null) {
                mBlockPageListener.onPageSelected(CPageBlock.this, selected, position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mViewPageListener != null) {
                mViewPageListener.onPageScrollStateChanged(state);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewPager = new CNestedViewPager(getApplicationContext());
        mViewPager.setOffscreenPageLimit(5);
        mAdapter = new InternalBlockPagerAdapter();
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(mPageListener);
        mBlockManager = getBlockManager();

        super.setContentView(mViewPager);
    }

    public void setMute(boolean touchable) {
        if (mViewPager != null) {
            mViewPager.setMute(touchable);
        }
    }

    @Override
    public void setContentView(int layoutId) {
        throw new RuntimeException("Do not call setContentView()!"
                + "You must initialize content view through call "
                + "addBlockPage() or inflateBlockPageFromXmlRes()");

    }

    @Override
    public void setContentView(View view) {
        throw new RuntimeException("Do not call setContentView()!"
                + "You must initialize content view through call "
                + "addBlockPage() or inflateBlockPageFromXmlRes()");
    }

    public void setOnBlockPageChangedListener(OnBlockPageChangedListener listener) {
        mBlockPageListener = listener;
    }

    public void setOnViewPageChangedListener(ViewPager.OnPageChangeListener listener) {
        mViewPageListener = listener;
    }

    public void selectPage(int position) {
        mViewPager.setCurrentItem(position, false);
    }

    public void selectPageSmoothly(int position) {
        mViewPager.setCurrentItem(position);
    }

    public CBlock getCurrentBlock() {
        return mActivated;
    }

    public int getCurrentIndex() {
        return mViewPager.getCurrentItem();
    }

    public CPageBlock addBlockPage(int blockId) {
        addBlockPageAtPosition(blockId, -1);
        return this;
    }

    public CPageBlock addBlockPage(String blockTag) {
        addBlockPageAtPosition(blockTag, -1);
        return this;
    }

    public CPageBlock addBlockPage(Class<? extends CBlock> blockClass) {
        addBlockPageAtPosition(blockClass, -1);
        return this;
    }

    public CPageBlock addBlockPageAtPosition(Class<? extends CBlock> blockClass,
            int blockId, String blockTag, CharSequence blockTitle, int contentViewResId) {
        addBlockPageAtPosition(blockClass, blockId, blockTag, blockTitle, contentViewResId, -1);
        return this;
    }

    public CPageBlock addBlockPageAtPosition(int blockId, int position) {
        CBlockManager.CBlockInfo info = mBlockManager.findBlockInfoById(blockId);
        if (info != null && info.id != CBlock.NO_ID && info.id == blockId) {
            CBlock page = mBlockManager.createNewInstanceByInfo(info);
            if (page != null) {
                addBlockPageInternal(page, position);
            }
        }
        return this;
    }

    public CPageBlock addBlockPageAtPosition(String blockTag, int position) {
        CBlockManager.CBlockInfo info = mBlockManager.findBlockInfoWithTag(blockTag);
        if (info != null && info.tag != null && info.tag.equals(blockTag)) {
            CBlock page = mBlockManager.createNewInstanceByInfo(info);
            if (page != null) {
                addBlockPageInternal(page, position);
            }
        }
        return this;
    }

    public CPageBlock addBlockPageAtPosition(Class<? extends CBlock> blockClass, int position) {
        addBlockPageAtPosition(blockClass, 0, null, null, 0, position);
        return this;
    }

    public CPageBlock addBlockPageAtPosition(Class<? extends CBlock> blockClass,
            int blockId, String blockTag, CharSequence blockTitle, int contentViewResId, int position) {

        if (blockClass != null) {
            try {
                CBlock page = blockClass.newInstance();
                page.setId(blockId)
                        .setTag(blockTag)
                        .setTitle(blockTitle)
                        .setContentView(contentViewResId);

                addBlockPageInternal(page, position);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    private void addBlockPageInternal(CBlock page, int position) {
        if (mBlocks == null) {
            mBlocks = new ArrayList<CBlock>();
        }
        if (position > -1) {
            mBlocks.add(position, page);
        } else {
            mBlocks.add(page);
        }
        page.attachToParent(this);
    }

    public CPageBlock removeBlockPage(int blockId) {
        if (mBlocks != null) {
            CBlock target = null;
            final int size = mBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock block = mBlocks.get(i);
                if (block.getId() == blockId) {
                    target = block;
                    break;
                }
            }
            if (target != null) {
                mBlocks.remove(target);
                target.detachFromParent();
                notifyDataSetChanged();
            }
        }
        return this;
    }

    public CPageBlock removeBlockPage(String blockTag) {
        if (mBlocks != null) {
            CBlock target = null;
            final int size = mBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock block = mBlocks.get(i);
                final Object tag = block.getTag();
                if (tag != null && tag.equals(blockTag)) {
                    target = block;
                    break;
                }
            }
            if (target != null) {
                mBlocks.remove(target);
                target.detachFromParent();
                notifyDataSetChanged();
            }
        }
        return this;
    }

    public CPageBlock removeBlockPage(Class<? extends CBlock> blockClass) {
        if (mBlocks != null) {
            CBlock target = null;
            final int size = mBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock block = mBlocks.get(i);
                final String name = block.getClass().getName();
                if (name.equals(blockClass.getName())) {
                    target = block;
                    break;
                }
            }
            if (target != null) {
                mBlocks.remove(target);
                target.detachFromParent();
                notifyDataSetChanged();
            }
        }
        return this;
    }

    public CPageBlock removeBlockPageAtPosition(int position) {
        if (mBlocks != null && mBlocks.size() > position && position > -1) {
            final CBlock block = mBlocks.get(position);
            block.detachFromParent();
            mBlocks.remove(block);
            notifyDataSetChanged();
        }
        return this;
    }

    public void clear() {
        if (mBlocks != null && !mBlocks.isEmpty()) {
            final int size = mBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock block = mBlocks.get(i);
                block.detachFromParent();
            }
            mBlocks.clear();
            notifyDataSetChanged();
        }
    }

    public void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public CBlock getBlockAt(int position) {
        if (mBlocks != null && position > -1 && mBlocks.size() > position) {
            return mBlocks.get(position);
        }
        return null;
    }

    public int getBlockCount() {
        if (mBlocks != null) {
            return mBlocks.size();
        }
        return 0;
    }

    public void inflateBlockPagesFromXmlRes(int xmlResId) {
        inflateBlockPagesFromXmlResAndSelected(xmlResId, -1);
    }

    public void inflateBlockPagesFromXmlResAndSelected(int xmlResId, int selectedPosition) {
        if (mBlocks == null) {
            mBlocks = new ArrayList<CBlock>();
        } else {
            mBlocks.clear();
        }

        inflateInternal(xmlResId);
        notifyDataSetChanged();

        int currentItem = mViewPager.getCurrentItem();
        final int count = getBlockCount();
        if (currentItem >= count) {
            currentItem = 0;
        }
        selectPageSmoothly(currentItem);
        mActivated = mBlocks.get(currentItem);

        if (selectedPosition > -1) {
            selectPageSmoothly(selectedPosition);
        }
    }

    @Override
    void performStart() {
        onStart();
        if (mActivated != null) {
            mActivated.performStart();
        }
    }

    @Override
    void performResume() {
        onResume();
        if (mActivated != null) {
            mActivated.performResume();
        }
    }

    @Override
    public void performNewIntent(CBlockIntent intent) {
        onNewIntent(intent);
        if (mActivated != null) {
            mActivated.performNewIntent(intent);
        }
    }

    @Override
    void dispatchUpdateStateTo(int state) {
        if (state > CBlock.STATE_CREATE) {
            setState(state);
            if (mActivated != null) {
                mActivated.dispatchUpdateStateTo(state);
            }
        } else {
            super.dispatchUpdateStateTo(state);
        }

    }

    private void inflateInternal(int xmlResId) {
        final Context c = getContext();
        final ClassLoader loader = c.getClassLoader();
        XmlResourceParser parser = c.getResources().getXml(xmlResId);
        AttributeSet attrs = Xml.asAttributeSet(parser);
        try {
            int eventType = parser.getEventType();
            String tagName;
            do {
                if (eventType == XmlPullParser.START_TAG) {
                    tagName = parser.getName();
                    if (tagName.equals(XML_BLOCK_PAGE)) {
                        eventType = parser.next();
                        break;
                    }

                    throw new RuntimeException("Resolve block xml "+xmlResId+" exception " + tagName);
                }
                eventType = parser.next();
            } while (eventType != XmlPullParser.END_DOCUMENT);

            while(eventType != XmlPullParser.END_DOCUMENT) {
                switch(eventType) {
                    case XmlPullParser.START_TAG:
                        tagName = parser.getName();
                        if(tagName.equals(XML_BLOCK_ITEM)) {
                            TypedArray a = c.obtainStyledAttributes(
                                    attrs, R.styleable.CBlock);
                            final String className = a.getString(R.styleable.CBlock_blockName);
                            final String title = a.getString(R.styleable.CBlock_blockTitle);
                            final int layoutResId = a.getResourceId(R.styleable.CBlock_blockLayout, 0);
                            final int blockId = a.getResourceId(R.styleable.CBlock_blockId, CBlock.NO_ID);
                            final String blockTag = a.getString(R.styleable.CBlock_blockTag);

                            Class<?> clazz = sClassMap.get(className);
                            if (clazz == null) {
                                clazz = loader.loadClass(className);
                                sClassMap.put(className, clazz);
                            }
                            CBlock block = (CBlock)clazz.newInstance();
                            block.setId(blockId)
                                    .setTag(blockTag)
                                    .setTitle(title);
                            if (layoutResId > 0) {
                                block.setContentView(layoutResId);
                            }
                            addBlockPageInternal(block, -1);
                            a.recycle();
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean dispatchCreateOptionsMenu(CMenu menu) {
        final CBlock current = getCurrentBlock();
        boolean handled = false;
        if (current != null) {
            handled = current.dispatchCreateOptionsMenu(menu);
        }
        return handled || super.dispatchCreateOptionsMenu(menu);
    }

    @Override
    public boolean dispatchCreateNavigationMenu(CMenu menu) {
        final CBlock current = getCurrentBlock();
        boolean handled = false;
        if (current != null) {
            handled = current.dispatchCreateNavigationMenu(menu);
        }
        return handled || super.dispatchCreateNavigationMenu(menu);
    }

    private class InternalBlockPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (container == null || mBlocks == null || mBlocks.isEmpty()
                    || position >= mBlocks.size()) {
                return super.instantiateItem(container, position);
            }
            CBlock curr = mBlocks.get(position);
            if (curr != mActivated) {
                //mBlockManager.syncStateToBlock(curr, CBlock.STATE_CREATE);
            }
            View contentView = curr.getContentView();
            container.addView(contentView);
            return contentView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (mBlocks == null || mBlocks.isEmpty() || position >= mBlocks.size()) {
                return;
            }
            CBlock curr = mBlocks.get(position);
            View contentView = curr.getContentView();
            if (contentView != null) {
                container.removeView(contentView);
            }
        }

        @Override
        public int getCount() {
            if (mBlocks != null) {
                return mBlocks.size();
            }
            return 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            if (mBlocks == null) {
                return;
            }

            final int size = mBlocks.size();
            for (int i=0; i<size; i++) {
                final CBlock block = mBlocks.get(i);
                if (i == position) {
                    mBlockManager.syncStateToBlock(block);
                    mActivated = block;
                } else {
                    mBlockManager.syncStateToBlock(block, CBlock.STATE_CREATE);
                }
            }
        }
    }
}
