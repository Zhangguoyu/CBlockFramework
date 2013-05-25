package com.zhangguoyu.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhangguoyu.widget.CActionBar.CTab;
import com.zhangguoyu.demo.actionbar.R;


public class CTabBarView extends CButtonBarView {

	private OnTabClickListener mTabClickListener;
	private int mCurrentSelected = -1;

	public CTabBarView(Context context) {
		super(context);
        init(context, null, R.attr.tabBarStyle);
	}
	
	public CTabBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
        init(context, attrs, R.attr.tabBarStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
    }

    @Override
    protected void onChangeItemTextSize(float size) {
        final int N = getChildCount();
        for (int i=0; i<N; i++) {
            final TabView child = (TabView) getChildAt(i);
            child.mTxvTitle.setTextSize(size);
        }
    }

    @Override
    protected void onChangeItemTextColor(ColorStateList color) {
        final int N = getChildCount();
        for (int i=0; i<N; i++) {
            final TabView child = (TabView) getChildAt(i);
            child.mTxvTitle.setTextColor(color);
        }
    }

    @Override
    protected void onChangeItemTypeface(Typeface tf, int style) {
        final int N = getChildCount();
        for (int i=0; i<N; i++) {
            final TabView child = (TabView) getChildAt(i);
            if(style != -1) {
                child.mTxvTitle.setTypeface(tf);
            } else {
                child.mTxvTitle.setTypeface(tf, style);
            }
        }
    }

    @Override
    protected void onChangeItemMargin(int left, int top, int right, int bottom) {
        final int N = getChildCount();
        for (int i=0; i<N; i++) {
            final View child = getChildAt(i);
            LayoutParams p = (LayoutParams) child.getLayoutParams();
            p.leftMargin = left;
            p.rightMargin = right;
            p.topMargin = top;
            p.bottomMargin = bottom;
            updateViewLayout(child, p);
        }
    }

    @Override
    protected void onChangeItemGravity(int gravity) {
        final int N = getChildCount();
        for (int i=0; i<N; i++) {
            final TabView child = (TabView) getChildAt(i);
            child.mTxvTitle.setGravity(gravity);
        }
    }

    @Override
    public void onChangeItemBackground(Drawable drawable) {
        final int N = getChildCount();
        for (int i=0; i<N; i++) {
            final View child = getChildAt(i);
            child.setBackgroundDrawable(drawable);
        }
    }

    @Override
    public void onChangeItemPadding(int left, int top, int right, int bottom) {
        final int N = getChildCount();
        for (int i=0; i<N; i++) {
            final View child = getChildAt(i);
            child.setPadding(left, top, right, bottom);
        }
    }

    @Override
    public void onChangeItemTextShadow(int color, float dx, float dy, float radius) {
        final int N = getChildCount();
        for (int i=0; i<N; i++) {
            final TabView child = (TabView) getChildAt(i);
            child.mTxvTitle.setShadowLayer(radius, dx, dy, color);
        }
    }

    public void addTab(CTab tab, boolean selected) {
		addTab(tab, -1, selected);
	}
	
	public void addTab(CTab tab, int position, boolean selected) {
        final TabView tabView = createTabView(tab);
        addView(tabView, position, new LayoutParams(
                0, LayoutParams.MATCH_PARENT, 1));
        if (selected) {
            tabView.setSelected(true);
        }
    }
	
	public void updateTab(int position) {
        ((TabView)getChildAt(position)).update();
    }
	
	public void removeTabAt(int position) {
        removeViewAt(position);
    }

    public void removeAllTabs() {
        removeAllViews();
    }
	
	public void setTabSelected(int position) {
        mCurrentSelected = position;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final boolean isSelected = i == position;
            child.setSelected(isSelected);
        }
    }
	
	public int getCurrentSelected() {
		return mCurrentSelected;
	}
	
	public int getTabCount() {
		return getChildCount();
	}
	
	private TabView createTabView(CTab tab) {
        final TabView tabView = new TabView(getContext());
        tabView.init(this, tab);
        tabView.setFocusable(true);
        if (mTabClickListener == null) {
            mTabClickListener = new OnTabClickListener();
        }
        tabView.setOnClickListener(mTabClickListener);
        return tabView;
    }
	
	private class OnTabClickListener implements OnClickListener {
        public void onClick(View view) {
            TabView tabView = (TabView) view;
            tabView.getTab().select();
        }
    }

	private class TabView extends LinearLayout {
		
		private CTab mTab = null;
		private TextView mTxvTitle = null;
		private ImageView mImgIcon = null;
		private View mCustomView = null;
		private CTabBarView mParent = null;

		public TabView(Context context) {
			super(context);
			setOrientation(LinearLayout.VERTICAL);
		}
		
		public void init(CTabBarView parent, CTab tab) {
			mParent = parent;
			mTab = tab;
			update();
		}
		
		public void update() {
			final CTab tab = mTab;
			final View custom = tab.getCustom();
			if (custom != null) {
				final ViewParent customParent = custom.getParent();
                if (customParent != this) {
                    if (customParent != null) {
                    	((ViewGroup) customParent).removeView(custom);
                    }
                    addView(custom);
                }
                mCustomView = custom;
                if (mTxvTitle != null) {
                	mTxvTitle.setVisibility(GONE);
                }
                if (mImgIcon != null) {
                	mImgIcon.setVisibility(GONE);
                	mImgIcon.setImageDrawable(null);
                }
			} else {
				if(mCustomView != null) {
					mParent.removeView(mCustomView);
					mCustomView = null;
				}
				
				final Drawable icon = tab.getIcon();
                final CharSequence text = tab.getTitle();

                if (icon != null) {
                    if (mImgIcon == null) {
                        ImageView iconView = new ImageView(getContext());
                        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT);
                        lp.gravity = Gravity.CENTER;
                        iconView.setLayoutParams(lp);
                        addView(iconView, 0);
                        mImgIcon = iconView;
                    }
                    mImgIcon.setImageDrawable(icon);
                    mImgIcon.setVisibility(VISIBLE);
                } else if (mImgIcon != null) {
                	mImgIcon.setVisibility(GONE);
                	mImgIcon.setImageDrawable(null);
                }
                
                if (mImgIcon != null) {
                	mImgIcon.setContentDescription(tab.getContentDescription());
                }
                
                if (text != null) {
                    if (mTxvTitle == null) {
                        TextView textView = new TextView(getContext());
                        textView.setEllipsize(TruncateAt.END);
                        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT);
                        lp.gravity = Gravity.CENTER;
                        textView.setLayoutParams(lp);
                        textView.setText(text);
                        addView(textView);
                        mTxvTitle = textView;
                    }
                    mTxvTitle.setVisibility(VISIBLE);
                } else if (mTxvTitle != null) {
                	mTxvTitle.setVisibility(GONE);
                	mTxvTitle.setText(null);
                }
			}
		}
		
		CTab getTab() {
			return mTab;
		}
		
	}

}
