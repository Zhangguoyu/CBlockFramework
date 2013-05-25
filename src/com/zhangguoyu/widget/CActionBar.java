package com.zhangguoyu.widget;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;

public abstract class CActionBar {

    public static final int TITLE_STYLE_SHOW_ONLY_MAIN_TITLE = 1;

    public static final int TITLE_STYLE_SUBTITLED = 2;

    public static final int TITLE_STYLE_CSTOCK = 3;
    /**
     * 设置logo视图
     * @param view
     * @return
     */
	public abstract CActionBar setLogoView(View view);

    /**
     * 设置返回按钮视图
     * @param view
     * @return
     */
	public abstract CActionBar setBackButton(View view);

    /**
     * 设置标题视图
     * @param view
     * @return
     */
	public abstract CActionBar setTitleView(View view);

    /**
     * 设置标题栏右侧选项按钮视图
     * @param view
     * @return
     */
	public abstract CActionBar setOptionsView(View view);

    /**
     * 设置自定义视图，该视图会显示在Logo和标题之间
     * @param view
     * @return
     */
	public abstract CActionBar setCustomView(View view);

    /**
     * 设置logo图片
     * @param resId
     * @return
     */
	public abstract CActionBar setLogo(int resId);

    /**
     * 设置logo图片
     * @param drawable
     * @return
     */
	public abstract CActionBar setLogo(Drawable drawable);

    /**
     * 设置返回按钮图片
     * @param resId
     * @return
     */
	public abstract CActionBar setBackDrawable(int resId);

    /**
     * 设置返回按钮图片
     * @param drawable
     * @return
     */
	public abstract CActionBar setBackDrawable(Drawable drawable);

    /**
     * 设置标题
     * @param resId
     * @return
     */
	public abstract CActionBar setTitle(int resId);

    /**
     * 设置标题
     * @param title
     * @return
     */
	public abstract CActionBar setTitle(CharSequence title);

    /**
     * 设置子标题
     * @param resId
     * @return
     */
	public abstract CActionBar setSubTitle(int resId);

    /**
     * 设置子标题
     * @param subTitle
     * @return
     */
	public abstract CActionBar setSubTitle(CharSequence subTitle);

    /**
     * 显示ActionBar
     */
	public abstract void show();

    /**
     * 隐藏ActionBar
     */
	public abstract void hide();

    /**
     * 判断ActionBar是否显示
     * @return
     */
	public abstract boolean isShowing();

    /**
     * 设置ActionBar背景
     * @param resId
     * @return
     */
	public abstract CActionBar setBackground(int resId);

    /**
     * 设置ActionBar背景
     * @param drawable
     * @return
     */
	public abstract CActionBar setBackground(Drawable drawable);

    /**
     * 设置返回按钮显示或者隐藏
     * @param showBackButton
     * @return
     */
	public abstract CActionBar setDisplayShowBackButtonEnable(boolean showBackButton);

    /**
     * 设置返回按钮伴随着动画显示或者隐藏
     * @param showBackButton
     * @param anim
     * @return
     */
    public abstract CActionBar setDisplayShowBackButtonEnableWithAnimation(boolean showBackButton, Animation anim);

    /**
     * 设置返回按钮伴随着动画显示或者隐藏
     * @param showBackButton
     * @param anim
     * @return
     */
    public abstract CActionBar setDisplayShowBackButtonEnableWithAnimation(boolean showBackButton, int anim);

    /**
     * 设置logo显示或者隐藏
     * @param showLogo
     * @return
     */
	public abstract CActionBar setDisplayShowLogoEnable(boolean showLogo);

    /**
     * 设置logo伴随动画显示或者隐藏
     * @param showLogo
     * @param anim
     * @return
     */
    public abstract CActionBar setDisplayShowLogoEnableWithAnimation(boolean showLogo, Animation anim);

    /**
     * 设置logo伴随动画显示或者隐藏
     * @param showLogo
     * @param anim
     * @return
     */
    public abstract CActionBar setDisplayShowLogoEnableWithAnimation(boolean showLogo, int anim);

    /**
     * 设置自定义view显示或者隐藏
     * @param showCustom
     * @return
     */
	public abstract CActionBar setDisplayShowCustomEnable(boolean showCustom);

    /**
     * 设置自定义view伴随动画显示或者隐藏
     * @param showCustom
     * @param anim
     * @return
     */
    public abstract CActionBar setDisplayShowCustomEnableWithAnimation(boolean showCustom, Animation anim);

    /**
     * 设置自定义view伴随动画显示或者隐藏
     * @param showCustom
     * @param anim
     * @return
     */
    public abstract CActionBar setDisplayShowCustomEnableWithAnimation(boolean showCustom, int anim);

    /**
     * 设置标题显示或者隐藏
     * @param showTitle
     * @return
     */
	public abstract CActionBar setDisplayShowTitleEnable(boolean showTitle);

    /**
     * 设置标题伴随动画显示或者隐藏
     * @param showTitle
     * @param anim
     * @return
     */
    public abstract CActionBar setDisplayShowTitleEnableWithAnimation(boolean showTitle, Animation anim);

    /**
     * 设置标题伴随动画显示或者隐藏
     * @param showTitle
     * @param anim
     * @return
     */
    public abstract CActionBar setDisplayShowTitleEnableWithAnimation(boolean showTitle, int anim);

    /**
     * 创建一个tab
     * @return
     */
	public abstract CTab buildTab();

    /**
     * 获取当前选择的tab
     * @return
     */
	public abstract CTab getSelectedTab();

    /**
     * 获取当前Tab的总数
     * @return
     */
	public abstract int getTabCount();

    /**
     * 根据指定的索引index获取tab
     * @param index
     * @return
     */
	public abstract CTab getTabAt(int index);

    /**
     * 添加tab到ActionBar
     * @param tab
     * @return
     */
	public abstract CActionBar addTab(CTab tab);

    /**
     * 根据指定的位置添加tab到ActionBar
     * @param tab
     * @return
     */
	public abstract CActionBar addTab(CTab tab, int position);

    /**
     * 添加tab到ActionBar，并设置是否为选中状态
     * @param tab
     * @return
     */
	public abstract CActionBar addTab(CTab tab, boolean selected);

    /**
     * 根据指定的位置添加tab到ActionBar，并设置是否为选中状态
     * @param tab
     * @return
     */
	public abstract CActionBar addTab(CTab tab, int position, boolean selected);

    /**
     * 选中tab
     * @param tab
     */
	public abstract void selectTab(CTab tab);

    /**
     * 移除指定位置的tab
     * @param index
     * @return
     */
	public abstract CActionBar removeTabAt(int index);

    /**
     * 移除指定的tab
     * @param tab
     * @return
     */
	public abstract CActionBar removeTab(CTab tab);

    /**
     * 移除所有tab
     * @return
     */
	public abstract CActionBar removeAllTabs();
    public abstract CActionBar setTitleStyle(int titleViewStyle);
	
    /**
     * 设置底部导航栏背景
     * @param drawable
     * @return
     */
	public abstract CActionBar setNavigationBarBackground(Drawable drawable);

    /**
     * 设置底部导航栏背景
     * @param resId
     * @return
     */
	public abstract CActionBar setNavigationBarBackground(int resId);
	
	/**
     * 设置导航按钮项的背景
     * @param drawable
     * @return
     */
    public abstract CActionBar setNavigationMenuBackground(Drawable drawable);
	
	/**
     * 设置导航按钮项的背景
     * @param resId
     * @return
     */
    public abstract CActionBar setNavigationMenuBackground(int resId);
	
	/**
     * 设置导航按钮间距
     * @param margin
     * @return
     */
    public abstract CActionBar setNavigationMenuMargin(int margin);
	
	/**
     * 设置导航按钮间距
     * @param margin
     * @return
     */
    public abstract CActionBar setNavigationMenuMarginLeftAndRight(int margin);
	
	/**
     * 设置导航按钮间距
     * @param margin
     * @return
     */
    public abstract CActionBar setNavigaitonMenuMarginTopAndBottom(int margin);
	
	/**
     * 设置导航按钮间距
     * @param margin
     * @return
     */
    public abstract CActionBar setNavigationMenuMargin(int left, int top, int right, int bottom);
	
	/**
     * 设置导航按钮字体大小
     * @param size
     * @return
     */
    public abstract CActionBar setNavigationMenuTextSize(float size);
	
	/**
     * 设置导航字体颜色
     * @param color
     * @return
     */
    public abstract CActionBar setNavigationMenuTextColor(ColorStateList color);
	
	/**
     * 设置导航字体颜色
     * @param color
     * @return
     */
    public abstract CActionBar setNavigationMenuTextColor(int color);

    public abstract CActionBar setNavigationMenuTextColorResource(int colorResourceId);
	
	/**
     * 设置导航按钮的字体样式
     * @param tf
     * @return
     */
    public abstract CActionBar setNavigationMenuTypeface(Typeface tf);
	
	/**
     * 设置导航栏样式
     * 包括：
     * 导航栏背景
     * 导航栏高度
     * 导航栏按钮间距
     * 导航栏按钮背景
     * 导航栏字体大小
     * 导航栏字体颜色
     * 导航栏字体样式
     * 导航栏字体阴影
     *
     * @param style
     * @return
     */
    public abstract CActionBar setNavigationBarStyle(int style);
	
	/**
     * 设置导航栏高度
     * @param height
     * @return
     */
    public abstract CActionBar setNavigationBarHeight(int height);

    /**
     * 设置tab栏背景
     * @param drawable
     * @return
     */
    public abstract CActionBar setTabBarBackground(Drawable drawable);

    /**
     * 设置tab栏背景
     * @param resId
     * @return
     */
    public abstract CActionBar setTabBarBackground(int resId);

    /**
     * 设置tab按钮项的背景
     * @param drawable
     * @return
     */
    public abstract CActionBar setTabMenuBackground(Drawable drawable);

    /**
     * 设置tab按钮项的背景
     * @param resId
     * @return
     */
    public abstract CActionBar setTabMenuBackground(int resId);

    /**
     * 设置tab按钮间距
     * @param margin
     * @return
     */
    public abstract CActionBar setTabMenuMargin(int margin);

    /**
     * 设置tab按钮项的左右边距
     * @param margin
     * @return
     */
    public abstract CActionBar setTabMenuMarginLeftAndRight(int margin);

    /**
     * 设置tab按钮项的上下边距
     * @param margin
     * @return
     */
    public abstract CActionBar setTabMenuMarginTopAndBottom(int margin);

    /**
     * 设置按钮项四周边距
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    public abstract CActionBar setTabMenuMenuMargin(int left, int top, int right, int bottom);

    /**
     * 设置tab字体大小
     * @param size
     * @return
     */
    public abstract CActionBar setTabMenuTextSize(float size);
	
	/**
     * 设置tab字体颜色
     * @param color
     * @return
     */
    public abstract CActionBar setTabMenuTextColor(ColorStateList color);

    /**
     * 设置tab字体颜色
     * @param color
     * @return
     */
    public abstract CActionBar setTabMenuTextColor(int color);

    public abstract CActionBar setTabMenuTextColorResource(int colorResId);

    /**
     * 设置tab按钮的字体样式
     * @param tf
     * @return
     */
    public abstract CActionBar setTabMenuTypeface(Typeface tf);

    /**
     * 设置tab栏样式
     * 包括：
     * tab栏背景
     * tab栏高度
     * tab栏按钮间距
     * tab栏按钮背景
     * tab栏字体大小
     * tab栏字体颜色
     * tab栏字体样式
     * tab栏字体阴影
     *
     * @param style
     * @return
     */
    public abstract CActionBar setTabMenuBarStyle(int style);

    /**
     * 设置tab栏高度
     * @param height
     * @return
     */
    public abstract CActionBar setTabBarHeight(int height);

    public abstract CActionBar setOnBackButtonClickListener(View.OnClickListener listener);

    /**
     * ActionBar的标签控件
     */
	public static abstract class CTab {

        /**
         * 获取Tab的所在位置
         * @return
         */
		public abstract int getPosition();

        /**
         * 设置Tab的标题
         * @param resId
         * @return
         */
		public abstract CTab setTitle(int resId);

        /**
         * 设置Tab的标题
         * @param title
         * @return
         */
		public abstract CTab setTitle(CharSequence title);

        /**
         * 获取Tab的标题
         * @return
         */
		public abstract CharSequence getTitle();

        /**
         * 设置Tab的ICON
         * @param resId
         * @return
         */
		public abstract CTab setIcon(int resId);

        /**
         * 设置Tab的ICON
         * @param icon
         * @return
         */
		public abstract CTab setIcon(Drawable icon);

        /**
         * 获取Tab的ICON
         * @return
         */
		public abstract Drawable getIcon();

        /**
         * 选中当前Tab
         */
		public abstract void select();

        /**
         * 判断当前Tab是否为选中状态
         * @return
         */
		public abstract boolean isSelected();

        /**
         * 设置Tag，该tag可以作为Tab的唯一标记
         * @param tag
         * @return
         */
		public abstract CTab setTag(Object tag);

        /**
         * 获取Tag
         * @return
         */
		public abstract Object getTag();

        /**
         * 设置Tab的自定义视图
         * @param custom
         * @return
         */
		public abstract CTab setCustom(View custom);

        /**
         * 获取Tab的自定义视图
         * @return
         */
		public abstract View getCustom();

        /**
         * 设置Tab选择监听器
         * @param listener
         */
		public abstract void setTabListener(OnTabSelectedListener listener);
		
		public abstract CTab setContetnDescription(CharSequence description);
		
		public abstract CharSequence getContentDescription();
		
	}

    /**
     * Tab选中监听器
     */
	public interface OnTabSelectedListener {

		public void onTabSelected(CActionBar actionBar, CTab selectedTab);
		
	}

}
