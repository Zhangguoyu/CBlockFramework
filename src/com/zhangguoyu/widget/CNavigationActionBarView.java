package com.zhangguoyu.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import com.zhangguoyu.demo.actionbar.R;

/**
 * Created by zhangguoyu on 13-5-17.
 */
public class CNavigationActionBarView extends CMenuBarView {

    private PopupWindow mMenuPanel = null;
    private SubMenuPanelLayout mMenuPanelLayout = null;
    private CMenuItem mPreSelectedItem;


    public CNavigationActionBarView(Context context) {
        super(context);
    }

    public CNavigationActionBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CNavigationActionBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int getDefaultDisplayCountLimits() {
        return 6;
    }

    @Override
    protected void onCreateSubMenuPanel(CMenuItem moreItem) {

        if (mMenuPanel != null && mMenuPanel.isShowing()) {
            if (moreItem.hasSubMenu()) {
                updateMenuItem(moreItem);
            } else {
                mMenuPanel.dismiss();
            }
            return;
        }

        if (moreItem == null || !moreItem.hasSubMenu()) {
            return;
        }

        if(mMenuPanel == null) {
            mMenuPanelLayout = new SubMenuPanelLayout(getContext());
            mMenuPanelLayout.setBackgroundColor(Color.BLUE);

            FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            p.gravity = Gravity.BOTTOM;
            p.bottomMargin = getBottom()-getTop();
            FrameLayout container = new FrameLayout(getContext());
            container.addView(mMenuPanelLayout, p);

            mMenuPanel = new PopupWindow(container, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            //mMenuPanel.setOutsideTouchable(true);
            mMenuPanel.setTouchable(true);
            mMenuPanel.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mMenuPanel.setTouchInterceptor(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    final int menuPanelTop = mMenuPanelLayout.getTop();
                    final Rect r = new Rect();
                    mMenuPanelLayout.getDrawingRect(r);
                    final Rect menuPanelRect = new Rect(
                            r.left, r.top+menuPanelTop,
                            r.right, r.bottom+menuPanelTop);
                    if (!menuPanelRect.contains((int)motionEvent.getX(),
                            (int)motionEvent.getY())) {
                        if (mMenuPanel != null && mMenuPanel.isShowing()) {
                            mMenuPanel.dismiss();
                        }
                    }

                    return false;
                }
            });

        }

        if (!mMenuPanel.isShowing()) {
            updateMenuItem(moreItem);
            mMenuPanel.showAtLocation(this, Gravity.BOTTOM|Gravity.LEFT, 0, 0);
        }
    }

    private void updateMenuItem(CMenuItem item) {

        final CMenuItem curr = getCurrentSelectedItem();
        if (mPreSelectedItem == null || mPreSelectedItem != curr) {
            mMenuPanelLayout.updateByMenuItem((CMenuItemImpl) item);
            mPreSelectedItem = curr;
        }
    }

    private class SubMenuPanelLayout extends FrameLayout {

        private int[] mRowHeight;

        public SubMenuPanelLayout(Context context) {
            super(context);
        }

        void updateByMenuItem(CMenuItemImpl item) {
            removeAllViews();
            if (!item.hasSubMenu()) {
                return;
            }
            CSubMenu menu = item.getSubMenu();
            final int N = menu.getItemCount();
            for (int i=0; i<N; i++) {
                CMenuItemImpl mi = (CMenuItemImpl) menu.getItemAt(i);
                addView(createMenuItemView(mi));
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

            int heightSize = 0;
            int maxHeight = 0;
            final int columnNumber = 4;
            final int count = getChildCount();
            mRowHeight = new int[(int) Math.ceil((float)count/columnNumber)];
            for (int i=0; i<count; i++) {

                if (i % columnNumber == 0) {
                    maxHeight = 0;
                }

                final View child = getChildAt(i);
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
                final int childHeight = child.getMeasuredHeight();
                maxHeight = Math.max(maxHeight, childHeight);

                if (i % columnNumber == columnNumber-1 || i == count-1) {
                    heightSize += maxHeight;
                    mRowHeight[i/columnNumber] = maxHeight;
                }
            }

            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), heightSize);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

            final int widthSpec = getMeasuredWidth();
            final int columnNumber = 4;
            final int childWidthSpec = widthSpec/columnNumber;
            final int count = getChildCount();
            int childLeft;
            int childTop = 0;
            int childRight;
            int childBottom = 0;
            for (int i=0; i<count; i++) {
                final View child = getChildAt(i);
                if (i > 0 && i % columnNumber == 0) {
                    childTop += mRowHeight[i/columnNumber-1];
                }
                childLeft = childWidthSpec * (i % columnNumber);
                childRight = childLeft + childWidthSpec;
                childBottom = childTop + mRowHeight[i/columnNumber];

                child.layout(childLeft, childTop, childRight, childBottom);
            }
        }

    }
}
