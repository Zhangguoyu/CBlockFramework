package com.zhangguoyu.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.zhangguoyu.app.CActivity;
import com.zhangguoyu.demo.actionbar.R;

import java.util.ArrayList;
import java.util.List;

public class CActionBarImpl extends CActionBar {
	
	private static final String LOG_TAG = "CActionBarImpl";
	
	private Context mContext = null;
	private CActivity mActivity = null;
	private CTitleActionBarView mTitleActionBarView = null;
    private CMenuBarView mNavigationActionBarView = null;
	private boolean mShowing;
	private TabImpl mSelectedTab = null;
	private List<TabImpl> mTabs = null;

    public static CActionBarImpl newDefault(CActivity activity) {
        return new CActionBarImpl(activity);
    }
	
	CActionBarImpl(CActivity activity) {
		mActivity = activity;
		Window window = activity.getWindow();
		View decor = window.getDecorView();
		mContext = decor.getContext();
		mTitleActionBarView = (CTitleActionBarView) decor.findViewById(R.id.actionbar_id);
        mTitleActionBarView.setOnOptionsBarSelectedListener(mOptionsSelectedListener);
		mNavigationActionBarView = (CMenuBarView) decor.findViewById(R.id.navbar_id);
        mNavigationActionBarView.setOnMenuItemSelectedListener(mNavigationSelectedListener);
		mTabs = new ArrayList<TabImpl>();
	}

	@Override
	public CActionBar setLogoView(View view) {
		mTitleActionBarView.setHomeView(view);
		return this;
	}

	@Override
	public CActionBar setBackButton(View view) {
		mTitleActionBarView.setBackButton(view);
		return this;
	}

	@Override
	public CActionBar setTitleView(View view) {
		mTitleActionBarView.setTitleView(view);
		return this;
	}

	@Override
	public CActionBar setOptionsView(View view) {
		mTitleActionBarView.setMenus(view);
		return this;
	}

	@Override
	public CActionBar setCustomView(View view) {
        mTitleActionBarView.setCustomView(view);
		return this;
	}

	@Override
	public CActionBar setLogo(int resId) {
		ImageView home = new ImageView(mContext);
		home.setImageResource(resId);
		mTitleActionBarView.setHomeView(home);
		return this;
	}

	@Override
	public CActionBar setLogo(Drawable drawable) {
        ImageView home = new ImageView(mContext);
        home.setImageDrawable(drawable);
        mTitleActionBarView.setHomeView(home);
		return this;
	}

	@Override
	public CActionBar setBackDrawable(int resId) {
        ImageView back = new ImageView(mContext);
        back.setImageResource(resId);
        mTitleActionBarView.setBackButton(back);
		return this;
	}

	@Override
	public CActionBar setBackDrawable(Drawable drawable) {
        ImageView back = new ImageView(mContext);
        back.setImageDrawable(drawable);
        mTitleActionBarView.setBackButton(back);
		return this;
	}

	@Override
	public CActionBar setTitle(int resId) {
		CharSequence title = mContext.getText(resId);
		setTitle(title);
		return this;
	}

	@Override
	public CActionBar setTitle(CharSequence title) {
		mTitleActionBarView.setTitle(title);
		return this;
	}

	@Override
	public CActionBar setSubTitle(int resId) {
		CharSequence title = mContext.getText(resId);
		setSubTitle(title);
		return this;
	}

	@Override
	public CActionBar setSubTitle(CharSequence subTitle) {
		mTitleActionBarView.setSubTitle(subTitle);
		return this;
	}
	

	@Override
	public void show() {
		if(isShowing()) {
			return;
		}
		mShowing = true;
		mTitleActionBarView.setVisibility(View.VISIBLE);
        mNavigationActionBarView.setVisibility(View.VISIBLE);
	}

	@Override
	public void hide() {
		if(!isShowing()) {
			return;
		}
		mShowing = false;
		mTitleActionBarView.setVisibility(View.GONE);
        mNavigationActionBarView.setVisibility(View.GONE);
	}

	@Override
	public boolean isShowing() {
		return mShowing;
	}

	@Override
	public CActionBar setBackground(int resId) {
		mTitleActionBarView.setBackgroundResource(resId);
		return this;
	}

	@Override
	public CActionBar setBackground(Drawable drawable) {
		mTitleActionBarView.setBackgroundDrawable(drawable);
		return this;
	}

	@Override
	public CActionBar setDisplayShowBackButtonEnable(boolean showBackButton) {
		mTitleActionBarView.setDisplayShowBackButtonEnable(showBackButton);
		return this;
	}

    @Override
    public CActionBar setDisplayShowBackButtonEnableWithAnimation(
            boolean showBackButton, Animation anim) {
        mTitleActionBarView.setDisplayShowBackButtonEnable(showBackButton, anim);
        return this;
    }

    @Override
    public CActionBar setDisplayShowBackButtonEnableWithAnimation(
            boolean showBackButton, int anim) {
        mTitleActionBarView.setDisplayShowBackButtonEnable(showBackButton,
                AnimationUtils.loadAnimation(mContext, anim));
        return null;
    }

    @Override
	public CActionBar setDisplayShowLogoEnable(boolean showHome) {
		mTitleActionBarView.setDisplayShowHomeEnable(showHome);
		return this;
	}

    @Override
    public CActionBar setDisplayShowLogoEnableWithAnimation(
            boolean showLogo, Animation anim) {
        mTitleActionBarView.setDisplayShowHomeEnable(showLogo, anim);
        return this;
    }

    @Override
    public CActionBar setDisplayShowLogoEnableWithAnimation(
            boolean showLogo, int anim) {
        mTitleActionBarView.setDisplayShowHomeEnable(showLogo,
                AnimationUtils.loadAnimation(mContext, anim));
        return this;
    }

    @Override
	public CActionBar setDisplayShowCustomEnable(boolean showCustom) {
		mTitleActionBarView.setDisplayShowCustomEnable(showCustom);
		return this;
	}

    @Override
    public CActionBar setDisplayShowCustomEnableWithAnimation(
            boolean showCustom, Animation anim) {
        mTitleActionBarView.setDisplayShowCustomEnable(showCustom, anim);
        return this;
    }

    @Override
    public CActionBar setDisplayShowCustomEnableWithAnimation(
            boolean showCustom, int anim) {
        mTitleActionBarView.setDisplayShowCustomEnable(showCustom,
                AnimationUtils.loadAnimation(mContext, anim));
        return this;
    }

    @Override
	public CActionBar setDisplayShowTitleEnable(boolean showTitle) {
		mTitleActionBarView.setDisplayShowTitleEnable(showTitle);
		return this;
	}

    @Override
    public CActionBar setDisplayShowTitleEnableWithAnimation(
            boolean showTitle, Animation anim) {
        mTitleActionBarView.setDisplayShowTitleEnable(showTitle, anim);
        return this;
    }

    @Override
    public CActionBar setDisplayShowTitleEnableWithAnimation(
            boolean showTitle, int anim) {
        mTitleActionBarView.setDisplayShowTitleEnable(showTitle,
                AnimationUtils.loadAnimation(mContext, anim));
        return this;
    }

    @Override
	public CTab buildTab() {
		return new TabImpl();
	}
	
	private class TabImpl extends CTab {
		
		private int mPosition = -1;
		private CharSequence mTitle = null;
		private Drawable mIcon = null;
		private View mCustom = null;
		private Object mTag = null;
		private CharSequence mDescription = null;
		private OnTabSelectedListener mListener = null;

		@Override
		public int getPosition() {
			return mPosition;
		}
		
		public CTab setPosition(int position) {
			mPosition = position;
			return this;
		}

		@Override
		public CTab setTitle(int resId) {
			CharSequence title = mContext.getText(resId);
			return setTitle(title);
		}

		@Override
		public CTab setTitle(CharSequence title) {
			mTitle = title;
			if (mPosition > -1) {
				mTitleActionBarView.updateTab(mPosition);
			}
			return this;
		}

		@Override
		public CharSequence getTitle() {
			return mTitle;
		}

		@Override
		public CTab setIcon(int resId) {
			Drawable icon = mContext.getResources().getDrawable(resId);
			return setIcon(icon);
		}

		@Override
		public CTab setIcon(Drawable icon) {
			mIcon = icon;
			if (mPosition > -1) {
				mTitleActionBarView.updateTab(mPosition);
			}
			return this;
		}

		@Override
		public Drawable getIcon() {
			return mIcon;
		}

		@Override
		public void select() {
			if(mPosition > -1) {
				mTitleActionBarView.setTabSelected(mPosition);
				if(mListener != null) {
					mListener.onTabSelected(CActionBarImpl.this, this);
				}
			}
		}

		@Override
		public boolean isSelected() {
			return this == getSelectedTab();
		}

		@Override
		public CTab setTag(Object tag) {
			mTag = tag;
			return this;
		}

		@Override
		public Object getTag() {
			return mTag;
		}

		@Override
		public void setTabListener(OnTabSelectedListener listener) {
			mListener = listener;
		}

		@Override
		public CTab setCustom(View custom) {
			mCustom = custom;
			if (mPosition > -1) {
				mTitleActionBarView.updateTab(mPosition);
			}
			return this;
		}

		@Override
		public View getCustom() {
			return mCustom;
		}

		@Override
		public CTab setContetnDescription(CharSequence description) {
			mDescription = description;
			if (mPosition > -1) {
				mTitleActionBarView.updateTab(mPosition);
			}
			return this;
		}

		@Override
		public CharSequence getContentDescription() {
			return mDescription;
		}
		
	}

	@Override
	public CTab getSelectedTab() {
		return mSelectedTab;
	}

	@Override
	public int getTabCount() {
		return mTabs.size();
	}

	@Override
	public CTab getTabAt(int index) {
		return mTabs.get(index);
	}

	@Override
	public CActionBar addTab(CTab tab) {
		addTab(tab, -1, mTabs.isEmpty());
		return this;
	}

	@Override
	public CActionBar addTab(CTab tab, int position) {
		addTab(tab, position, mTabs.isEmpty());
		return this;
	}

	@Override
	public CActionBar addTab(CTab tab, boolean selected) {
		addTab(tab, -1, selected);
		return this;
	}

	@Override
	public CActionBar addTab(CTab tab, int position, boolean selected) {
		mTitleActionBarView.addTab(tab, position, selected);
		configureTab(tab, position);
		if (selected) {
            selectTab(tab);
        }
		return this;
	}

	@Override
	public void selectTab(CTab tab) {
		if(mTitleActionBarView.getTabCount() > 0) {
			mSelectedTab = (TabImpl) tab;
			tab.select();
		}
	}
	
	private void configureTab(CTab tab, int position) {
		if(position < 0) {
			position = mTabs.size();
		}
        final TabImpl tabi = (TabImpl) tab;

        tabi.setPosition(position);
        mTabs.add(position, tabi);

        final int count = mTabs.size();
        for (int i = position + 1; i < count; i++) {
            mTabs.get(i).setPosition(i);
        }
    }

	@Override
	public CActionBar removeTabAt(int index) {
		return this;
	}

	@Override
	public CActionBar removeTab(CTab tab) {
		return removeTabAt(tab.getPosition());
	}

	@Override
	public CActionBar removeAllTabs() {
        cleanupTabs();
        return this;
    }

    @Override
    public CActionBar setTitleStyle(int titleViewStyle) {
        return null;
    }

    private void cleanupTabs() {
        mTabs.clear();
        mTitleActionBarView.removeAllTabs();
    }

	@Override
	public CActionBar setNavigationBarBackground(Drawable drawable) {
		mNavigationActionBarView.setBackgroundDrawable(drawable);
		return this;
	}

	@Override
	public CActionBar setNavigationBarBackground(int resId) {
		mNavigationActionBarView.setBackgroundResource(resId);
		return this;
	}

    @Override
    public CActionBar setNavigationMenuBackground(Drawable drawable) {
        mNavigationActionBarView.setItemBackground(drawable);
        return this;
    }

    @Override
    public CActionBar setNavigationMenuBackground(int resId) {
        mNavigationActionBarView.setItemBackground(
                mContext.getResources().getDrawable(resId));
        return this;
    }

    @Override
    public CActionBar setNavigationMenuMargin(int margin) {
        mNavigationActionBarView.setItemMargin(margin);
        return this;
    }

    @Override
    public CActionBar setNavigationMenuMarginLeftAndRight(int margin) {
        final int top = mNavigationActionBarView.getItemTopMargin();
        final int bottom = mNavigationActionBarView.getItemBottomMargin();
        mNavigationActionBarView.setItemMargin(margin, top, margin, bottom);
        return this;
    }

    @Override
    public CActionBar setNavigaitonMenuMarginTopAndBottom(int margin) {
        final int left = mNavigationActionBarView.getItemLeftMargin();
        final int right = mNavigationActionBarView.getItemRightMargin();
        mNavigationActionBarView.setItemMargin(left, margin, right, margin);
        return this;
    }

    @Override
    public CActionBar setNavigationMenuMargin(int left, int top, int right, int bottom) {
        mNavigationActionBarView.setItemMargin(left, top, right, bottom);
        return this;
    }

    @Override
    public CActionBar setNavigationMenuTextSize(float size) {
        mNavigationActionBarView.setItemTextSize(size);
        return this;
    }

    @Override
    public CActionBar setNavigationMenuTextColor(ColorStateList color) {
        mNavigationActionBarView.setItemTextColor(color);
        return this;
    }

    @Override
    public CActionBar setNavigationMenuTextColor(int color) {
        mNavigationActionBarView.setItemTextColor(color);
        return this;
    }

    @Override
    public CActionBar setNavigationMenuTextColorResource(int colorResourceId) {
        mNavigationActionBarView.setItemTextColorResource(colorResourceId);
        return this;
    }

    @Override
    public CActionBar setNavigationMenuTypeface(Typeface tf) {
        mNavigationActionBarView.setItemTypeface(tf);
        return this;
    }

    @Override
    public CActionBar setNavigationBarStyle(int style) {
        mNavigationActionBarView.setButtonBarStyle(style);
        return this;
    }

    @Override
    public CActionBar setNavigationBarHeight(int height) {
        mNavigationActionBarView.setButtonBarHeight(height);
        return this;
    }

    @Override
    public CActionBar setTabBarBackground(Drawable drawable) {
        mTitleActionBarView.getTabBar().setBackgroundDrawable(drawable);
        return this;
    }

    @Override
    public CActionBar setTabBarBackground(int resId) {
        mTitleActionBarView.getTabBar().setBackgroundResource(resId);
        return this;
    }

    @Override
    public CActionBar setTabMenuBackground(Drawable drawable) {
        mTitleActionBarView.getTabBar().setItemBackground(drawable);
        return this;
    }

    @Override
    public CActionBar setTabMenuBackground(int resId) {
        mTitleActionBarView.getTabBar().setItemBackground(
                mContext.getResources().getDrawable(resId));
        return this;
    }

    @Override
    public CActionBar setTabMenuMargin(int margin) {
        mTitleActionBarView.getTabBar().setItemMargin(margin);
        return this;
    }

    @Override
    public CActionBar setTabMenuMarginLeftAndRight(int margin) {
        final CTabBarView tabBar = mTitleActionBarView.getTabBar();
        final int top = tabBar.getItemTopMargin();
        final int bottom = tabBar.getItemBottomMargin();
        tabBar.setItemMargin(margin, top, margin, bottom);
        return this;
    }

    @Override
    public CActionBar setTabMenuMarginTopAndBottom(int margin) {
        final CTabBarView tabBar = mTitleActionBarView.getTabBar();
        final int left = tabBar.getItemLeftMargin();
        final int right = tabBar.getItemRightMargin();
        tabBar.setItemMargin(left, margin, right, margin);
        return this;
    }

    @Override
    public CActionBar setTabMenuMenuMargin(int left, int top, int right, int bottom) {
        mTitleActionBarView.getTabBar().setItemMargin(left, top, right, bottom);
        return this;
    }

    @Override
    public CActionBar setTabMenuTextSize(float size) {
        mTitleActionBarView.getTabBar().setItemTextSize(size);
        return this;
    }

    @Override
    public CActionBar setTabMenuTextColor(ColorStateList color) {
        mTitleActionBarView.getTabBar().setItemTextColor(color);
        return this;
    }

    @Override
    public CActionBar setTabMenuTextColor(int color) {
        mTitleActionBarView.getTabBar().setItemTextColor(color);
        return this;
    }

    @Override
    public CActionBar setTabMenuTextColorResource(int colorResId) {
        mTitleActionBarView.getTabBar().setItemTextColorResource(colorResId);
        return this;
    }

    @Override
    public CActionBar setTabMenuTypeface(Typeface tf) {
        mTitleActionBarView.getTabBar().setItemTypeface(tf);
        return this;
    }

    @Override
    public CActionBar setTabMenuBarStyle(int style) {
        mTitleActionBarView.getTabBar().setButtonBarStyle(style);
        return this;
    }

    @Override
    public CActionBar setTabBarHeight(int height) {
        mTitleActionBarView.getTabBar().setButtonBarHeight(height);
        return this;
    }

    @Override
    public CActionBar setOnBackButtonClickListener(View.OnClickListener listener) {
        mTitleActionBarView.setOnBackButtonClickListener(listener);
        return this;
    }

    public CMenu newMenu() {
		return new CMenuBarView.CMenuImpl(mContext);
	}
	
	public void inflatedNavigationBarByMenu(CMenu menu) {
		mNavigationActionBarView.inflatedByMenu(menu);
	}

    public void inflatdOptionsBarByMenu(CMenu menu) {
        mTitleActionBarView.inflateOptionsBarByMenu(menu);
    }

    private final CMenuBarView.OnMenuItemSelectedListener mOptionsSelectedListener =
            new CMenuBarView.OnMenuItemSelectedListener() {

        @Override
        public void onMenuItemSelected(CMenuBarView view, CMenuItem menuItem) {
            mActivity.onOptionsMenuItemSelected(menuItem);
        }
    };

    private final CMenuBarView.OnMenuItemSelectedListener mNavigationSelectedListener =
            new CMenuBarView.OnMenuItemSelectedListener() {
        @Override
        public void onMenuItemSelected(CMenuBarView view, CMenuItem menuItem) {
            mActivity.onNavigationMenuItemSelected(menuItem);
        }
    };

}
