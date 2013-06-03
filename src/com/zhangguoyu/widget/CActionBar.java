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

    public static final int TITLE_EVENT_TYPE_CLICK_MAIN_TITLE = 0;

    public static final int TITLE_EVENT_TYPE_CLICK_SUBTITLE = 1;

    public static final int TITLE_EVENT_TYPE_CLICK_LEFT_NAV = 2;

    public static final int TITLE_EVENT_TYPE_CLICK_RIGHT_NAV = 3;
    /**
     * ����logo��ͼ
     * @param view
     * @return
     */
	public abstract CActionBar setLogoView(View view);

    /**
     * ���÷��ذ�ť��ͼ
     * @param view
     * @return
     */
	public abstract CActionBar setBackButton(View view);

    /**
     * ���ñ�����ͼ
     * @param view
     * @return
     */
	public abstract CActionBar setTitleView(View view);

    /**
     * ���ñ������Ҳ�ѡ�ť��ͼ
     * @param view
     * @return
     */
	public abstract CActionBar setOptionsView(View view);

    /**
     * �����Զ�����ͼ������ͼ����ʾ��Logo�ͱ���֮��
     * @param view
     * @return
     */
	public abstract CActionBar setCustomView(View view);

    /**
     * ����logoͼƬ
     * @param resId
     * @return
     */
	public abstract CActionBar setLogo(int resId);

    /**
     * ����logoͼƬ
     * @param drawable
     * @return
     */
	public abstract CActionBar setLogo(Drawable drawable);

    /**
     * ���÷��ذ�ťͼƬ
     * @param resId
     * @return
     */
	public abstract CActionBar setBackDrawable(int resId);

    /**
     * ���÷��ذ�ťͼƬ
     * @param drawable
     * @return
     */
	public abstract CActionBar setBackDrawable(Drawable drawable);

    /**
     * ���ñ���
     * @param resId
     * @return
     */
	public abstract CActionBar setTitle(int resId);

    /**
     * ���ñ���
     * @param title
     * @return
     */
	public abstract CActionBar setTitle(CharSequence title);

    /**
     * �����ӱ���
     * @param resId
     * @return
     */
	public abstract CActionBar setSubTitle(int resId);

    /**
     * �����ӱ���
     * @param subTitle
     * @return
     */
	public abstract CActionBar setSubTitle(CharSequence subTitle);

    /**
     * ��ʾActionBar
     */
	public abstract void show();

    /**
     * ����ActionBar
     */
	public abstract void hide();

    /**
     * �ж�ActionBar�Ƿ���ʾ
     * @return
     */
	public abstract boolean isShowing();

    /**
     * ����ActionBar����
     * @param resId
     * @return
     */
	public abstract CActionBar setBackground(int resId);

    /**
     * ����ActionBar����
     * @param drawable
     * @return
     */
	public abstract CActionBar setBackground(Drawable drawable);

    /**
     * ���÷��ذ�ť��ʾ��������
     * @param showBackButton
     * @return
     */
	public abstract CActionBar setDisplayShowBackButtonEnable(boolean showBackButton);

    /**
     * ���÷��ذ�ť�����Ŷ�����ʾ��������
     * @param showBackButton
     * @param anim
     * @return
     */
    public abstract CActionBar setDisplayShowBackButtonEnableWithAnimation(boolean showBackButton, Animation anim);

    /**
     * ���÷��ذ�ť�����Ŷ�����ʾ��������
     * @param showBackButton
     * @param anim
     * @return
     */
    public abstract CActionBar setDisplayShowBackButtonEnableWithAnimation(boolean showBackButton, int anim);

    /**
     * ����logo��ʾ��������
     * @param showLogo
     * @return
     */
	public abstract CActionBar setDisplayShowLogoEnable(boolean showLogo);

    /**
     * ����logo���涯����ʾ��������
     * @param showLogo
     * @param anim
     * @return
     */
    public abstract CActionBar setDisplayShowLogoEnableWithAnimation(boolean showLogo, Animation anim);

    /**
     * ����logo���涯����ʾ��������
     * @param showLogo
     * @param anim
     * @return
     */
    public abstract CActionBar setDisplayShowLogoEnableWithAnimation(boolean showLogo, int anim);

    /**
     * �����Զ���view��ʾ��������
     * @param showCustom
     * @return
     */
	public abstract CActionBar setDisplayShowCustomEnable(boolean showCustom);

    /**
     * �����Զ���view���涯����ʾ��������
     * @param showCustom
     * @param anim
     * @return
     */
    public abstract CActionBar setDisplayShowCustomEnableWithAnimation(boolean showCustom, Animation anim);

    /**
     * �����Զ���view���涯����ʾ��������
     * @param showCustom
     * @param anim
     * @return
     */
    public abstract CActionBar setDisplayShowCustomEnableWithAnimation(boolean showCustom, int anim);

    /**
     * ���ñ�����ʾ��������
     * @param showTitle
     * @return
     */
	public abstract CActionBar setDisplayShowTitleEnable(boolean showTitle);

    /**
     * ���ñ�����涯����ʾ��������
     * @param showTitle
     * @param anim
     * @return
     */
    public abstract CActionBar setDisplayShowTitleEnableWithAnimation(boolean showTitle, Animation anim);

    /**
     * ���ñ�����涯����ʾ��������
     * @param showTitle
     * @param anim
     * @return
     */
    public abstract CActionBar setDisplayShowTitleEnableWithAnimation(boolean showTitle, int anim);

    /**
     * ����һ��tab
     * @return
     */
	public abstract CTab buildTab();

    /**
     * ��ȡ��ǰѡ���tab
     * @return
     */
	public abstract CTab getSelectedTab();

    /**
     * ��ȡ��ǰTab������
     * @return
     */
	public abstract int getTabCount();

    /**
     * ���ָ��������index��ȡtab
     * @param index
     * @return
     */
	public abstract CTab getTabAt(int index);

    /**
     * ���tab��ActionBar
     * @param tab
     * @return
     */
	public abstract CActionBar addTab(CTab tab);

    /**
     * ���ָ����λ�����tab��ActionBar
     * @param tab
     * @return
     */
	public abstract CActionBar addTab(CTab tab, int position);

    /**
     * ���tab��ActionBar���������Ƿ�Ϊѡ��״̬
     * @param tab
     * @return
     */
	public abstract CActionBar addTab(CTab tab, boolean selected);

    /**
     * ���ָ����λ�����tab��ActionBar���������Ƿ�Ϊѡ��״̬
     * @param tab
     * @return
     */
	public abstract CActionBar addTab(CTab tab, int position, boolean selected);

    /**
     * ѡ��tab
     * @param tab
     */
	public abstract void selectTab(CTab tab);

    /**
     * �Ƴ�ָ��λ�õ�tab
     * @param index
     * @return
     */
	public abstract CActionBar removeTabAt(int index);

    /**
     * �Ƴ�ָ����tab
     * @param tab
     * @return
     */
	public abstract CActionBar removeTab(CTab tab);

    /**
     * �Ƴ�����tab
     * @return
     */
	public abstract CActionBar removeAllTabs();
    public abstract CActionBar setTitleStyle(int titleViewStyle);
	
    /**
     * ���õײ�����������
     * @param drawable
     * @return
     */
	public abstract CActionBar setNavigationBarBackground(Drawable drawable);

    /**
     * ���õײ�����������
     * @param resId
     * @return
     */
	public abstract CActionBar setNavigationBarBackground(int resId);
	
	/**
     * ���õ�����ť��ı���
     * @param drawable
     * @return
     */
    public abstract CActionBar setNavigationMenuBackground(Drawable drawable);
	
	/**
     * ���õ�����ť��ı���
     * @param resId
     * @return
     */
    public abstract CActionBar setNavigationMenuBackground(int resId);
	
	/**
     * ���õ�����ť���
     * @param margin
     * @return
     */
    public abstract CActionBar setNavigationMenuMargin(int margin);
	
	/**
     * ���õ�����ť���
     * @param margin
     * @return
     */
    public abstract CActionBar setNavigationMenuMarginLeftAndRight(int margin);
	
	/**
     * ���õ�����ť���
     * @param margin
     * @return
     */
    public abstract CActionBar setNavigaitonMenuMarginTopAndBottom(int margin);
	
	/**
     * ���õ�����ť���
     * @param margin
     * @return
     */
    public abstract CActionBar setNavigationMenuMargin(int left, int top, int right, int bottom);
	
	/**
     * ���õ�����ť�����С
     * @param size
     * @return
     */
    public abstract CActionBar setNavigationMenuTextSize(float size);
	
	/**
     * ���õ���������ɫ
     * @param color
     * @return
     */
    public abstract CActionBar setNavigationMenuTextColor(ColorStateList color);
	
	/**
     * ���õ���������ɫ
     * @param color
     * @return
     */
    public abstract CActionBar setNavigationMenuTextColor(int color);

    public abstract CActionBar setNavigationMenuTextColorResource(int colorResourceId);
	
	/**
     * ���õ�����ť��������ʽ
     * @param tf
     * @return
     */
    public abstract CActionBar setNavigationMenuTypeface(Typeface tf);
	
	/**
     * ���õ�������ʽ
     * ������
     * ����������
     * �������߶�
     * ��������ť���
     * ��������ť����
     * �����������С
     * ������������ɫ
     * ������������ʽ
     * ������������Ӱ
     *
     * @param style
     * @return
     */
    public abstract CActionBar setNavigationBarStyle(int style);
	
	/**
     * ���õ������߶�
     * @param height
     * @return
     */
    public abstract CActionBar setNavigationBarHeight(int height);

    /**
     * ����tab������
     * @param drawable
     * @return
     */
    public abstract CActionBar setTabBarBackground(Drawable drawable);

    /**
     * ����tab������
     * @param resId
     * @return
     */
    public abstract CActionBar setTabBarBackground(int resId);

    /**
     * ����tab��ť��ı���
     * @param drawable
     * @return
     */
    public abstract CActionBar setTabMenuBackground(Drawable drawable);

    /**
     * ����tab��ť��ı���
     * @param resId
     * @return
     */
    public abstract CActionBar setTabMenuBackground(int resId);

    /**
     * ����tab��ť���
     * @param margin
     * @return
     */
    public abstract CActionBar setTabMenuMargin(int margin);

    /**
     * ����tab��ť������ұ߾�
     * @param margin
     * @return
     */
    public abstract CActionBar setTabMenuMarginLeftAndRight(int margin);

    /**
     * ����tab��ť������±߾�
     * @param margin
     * @return
     */
    public abstract CActionBar setTabMenuMarginTopAndBottom(int margin);

    /**
     * ���ð�ť�����ܱ߾�
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    public abstract CActionBar setTabMenuMenuMargin(int left, int top, int right, int bottom);

    /**
     * ����tab�����С
     * @param size
     * @return
     */
    public abstract CActionBar setTabMenuTextSize(float size);
	
	/**
     * ����tab������ɫ
     * @param color
     * @return
     */
    public abstract CActionBar setTabMenuTextColor(ColorStateList color);

    /**
     * ����tab������ɫ
     * @param color
     * @return
     */
    public abstract CActionBar setTabMenuTextColor(int color);

    public abstract CActionBar setTabMenuTextColorResource(int colorResId);

    /**
     * ����tab��ť��������ʽ
     * @param tf
     * @return
     */
    public abstract CActionBar setTabMenuTypeface(Typeface tf);

    /**
     * ����tab����ʽ
     * ������
     * tab������
     * tab���߶�
     * tab����ť���
     * tab����ť����
     * tab�������С
     * tab��������ɫ
     * tab��������ʽ
     * tab��������Ӱ
     *
     * @param style
     * @return
     */
    public abstract CActionBar setTabMenuBarStyle(int style);

    /**
     * ����tab���߶�
     * @param height
     * @return
     */
    public abstract CActionBar setTabBarHeight(int height);

    /**
     * ActionBar�ı�ǩ�ؼ�
     */
	public static abstract class CTab {

        /**
         * ��ȡTab������λ��
         * @return
         */
		public abstract int getPosition();

        /**
         * ����Tab�ı���
         * @param resId
         * @return
         */
		public abstract CTab setTitle(int resId);

        /**
         * ����Tab�ı���
         * @param title
         * @return
         */
		public abstract CTab setTitle(CharSequence title);

        /**
         * ��ȡTab�ı���
         * @return
         */
		public abstract CharSequence getTitle();

        /**
         * ����Tab��ICON
         * @param resId
         * @return
         */
		public abstract CTab setIcon(int resId);

        /**
         * ����Tab��ICON
         * @param icon
         * @return
         */
		public abstract CTab setIcon(Drawable icon);

        /**
         * ��ȡTab��ICON
         * @return
         */
		public abstract Drawable getIcon();

        /**
         * ѡ�е�ǰTab
         */
		public abstract void select();

        /**
         * �жϵ�ǰTab�Ƿ�Ϊѡ��״̬
         * @return
         */
		public abstract boolean isSelected();

        /**
         * ����Tag����tag������ΪTab��Ψһ���
         * @param tag
         * @return
         */
		public abstract CTab setTag(Object tag);

        /**
         * ��ȡTag
         * @return
         */
		public abstract Object getTag();

        /**
         * ����Tab���Զ�����ͼ
         * @param custom
         * @return
         */
		public abstract CTab setCustom(View custom);

        /**
         * ��ȡTab���Զ�����ͼ
         * @return
         */
		public abstract View getCustom();

        /**
         * ����Tabѡ�������
         * @param listener
         */
		public abstract void setTabListener(OnTabSelectedListener listener);
		
		public abstract CTab setContetnDescription(CharSequence description);
		
		public abstract CharSequence getContentDescription();
		
	}

    /**
     * Tabѡ�м�����
     */
	public interface OnTabSelectedListener {

		public void onTabSelected(CActionBar actionBar, CTab selectedTab);
		
	}

}
