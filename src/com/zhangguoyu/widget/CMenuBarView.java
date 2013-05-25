package com.zhangguoyu.widget;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.zhangguoyu.demo.actionbar.R;


public class CMenuBarView extends CButtonBarView {
	
	public interface OnMenuItemSelectedListener {
		
		public void onMenuItemSelected(CMenuBarView view, CMenuItem menuItem);
		
	}

	private int mCurrentSelected = -1;

    private List<CMenuItemImpl> mItems = null;
//	private List<CMenuItemImpl> mMoreItems = null;
	private List<CMenuItemImpl> mUsedItems = null;

    private MenuItemClickListener mMenuClickListener = null;
    private OnMenuItemSelectedListener mMenuItemSelectedListener = null;

    private CharSequence mMoreTitle = null;
    private Drawable mMoreIconDrawable = null;
    private int mDisplayCountLimits = 5;

    private CMenuItemImpl mCurrentSelectedItem = null;

    private CMenuItemImpl mMoreMenuItem = null;
	
	public CMenuBarView(Context context) {
		super(context);
        init(context, null, 0);
	}
	
	public CMenuBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
        init(context, attrs, 0);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public CMenuBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        init(context, attrs, defStyle);
	}

    private void init(Context context, AttributeSet attrs, int defStyle) {
        if(attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CMenuBarView, defStyle, 0);
            mMoreTitle = a.getText(R.styleable.CMenuBarView_moreTitle);
            mMoreIconDrawable = a.getDrawable(R.styleable.CMenuBarView_moreIcon);
            mDisplayCountLimits = a.getInteger(R.styleable.CMenuBarView_displayCountLimits,
                    getDefaultDisplayCountLimits());
            a.recycle();
        }
    }

    @Override
    protected void onChangeItemTextSize(float size) {
        final int N = getChildCount();
        for (int i=0; i<N; i++) {
            final MenuItemView child = (MenuItemView) getChildAt(i);
            child.mTxvTitle.setTextSize(size);
        }
    }

    @Override
    protected void onChangeItemTextColor(ColorStateList color) {
        final int N = getChildCount();
        for (int i=0; i<N; i++) {
            final MenuItemView child = (MenuItemView) getChildAt(i);
            child.mTxvTitle.setTextColor(color);
        }
    }

    @Override
    protected void onChangeItemTypeface(Typeface tf, int style) {
        final int N = getChildCount();
        for (int i=0; i<N; i++) {
            final MenuItemView child = (MenuItemView) getChildAt(i);
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
            final MenuItemView child = (MenuItemView) getChildAt(i);
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
            final MenuItemView child = (MenuItemView) getChildAt(i);
            child.mTxvTitle.setShadowLayer(radius, dx, dy, color);
        }
    }

    public void setDisplayCountLimits(int limits) {
        if (mDisplayCountLimits == limits) {
            return;
        }

        mDisplayCountLimits = limits;
        if (mItems == null || mItems.isEmpty()) {
            return;
        }

        if (mUsedItems != null) {
            mUsedItems.clear();
        }
//        if (mMoreItems != null) {
//            mMoreItems.clear();
//        }

        final int count = mItems.size();
        for (int i=0; i<count; i++) {
            addMenuItem(mItems.get(i));
        }
    }

    public int getDisplayCountLimits() {
        return mDisplayCountLimits;
    }

    protected int getDefaultDisplayCountLimits() {
        return mDisplayCountLimits;
    }

    public CMenuItem getCurrentSelectedItem() {
        return mCurrentSelectedItem;
    }

    protected void onCreateSubMenuPanel(CMenuItem moreItem) {
    }
	
	public void inflatedByMenu(CMenu menu) {
        removeAllMenuItems();

		final int itemCount = menu.getItemCount();
		if(itemCount <= 0) {
			return;
		}
		for (int i=0; i<itemCount; i++) {
			addMenuItem((CMenuItemImpl) menu.getItemAt(i));
		}
	}
	
	void addMenuItem(CMenuItemImpl item) {
		addMenuItem(item, -1);
	}
	
	void addMenuItem(CMenuItemImpl item, int position) {
        if (mItems == null) {
            mItems = new ArrayList<CMenuItemImpl>();
        }

        if (mUsedItems == null) {
            mUsedItems = new ArrayList<CMenuItemImpl>();
        }

		final int usedSize = mUsedItems.size();
		if(usedSize < mDisplayCountLimits) {

			mUsedItems.add(item);
			final MenuItemView menuItem = createMenuItemView(item);
	        addView(menuItem, position, new LayoutParams(
	                0, LayoutParams.MATCH_PARENT, 1));
		} else {
//			if (mMoreItems == null) {
//				mMoreItems = new ArrayList<CMenuItemImpl>();
//			}

			CMenuItemImpl lastUsedItem = (CMenuItemImpl) mUsedItems.get(usedSize-1);
			if (!lastUsedItem.isMore()) {
				mUsedItems.remove(usedSize - 1);
                mMoreMenuItem = createMoreMenuItem();
                CSubMenuImpl moreSubMenu = new CSubMenuImpl(
                        getContext(), mMoreMenuItem);
                mMoreMenuItem.setSubMenu(moreSubMenu);
                moreSubMenu.add(lastUsedItem.getTitle(), lastUsedItem.getIcon())
                        .setItemId(lastUsedItem.getItemId())
                        .setGroupId(lastUsedItem.getGroupId())
                        .setCustomView(lastUsedItem.getCustomView());

//                mMoreItems.add(lastUsedItem);
                mUsedItems.add(mMoreMenuItem);

				final MenuItemView menuItem = createMenuItemView(mMoreMenuItem);
                removeViewAt(getChildCount()-1);
		        addView(menuItem, position, new LayoutParams(
		                0, LayoutParams.MATCH_PARENT, 1));
			}

//            mMoreItems.add(item);
            mMoreMenuItem.getSubMenu().add(item.getTitle(), item.getIcon())
                    .setItemId(item.getItemId())
                    .setGroupId(item.getGroupId())
                    .setCustomView(item.getCustomView());

		}

        mItems.add(item);
        
    }

    protected CMenuItemImpl createMoreMenuItem() {
        final CharSequence title = mMoreTitle;
        final Drawable icon = mMoreIconDrawable;
        return new CMenuItemImpl(getContext(), 0, 0, title, icon, true);
    }
	
	public void updateMenuItem(int position) {
        ((MenuItemView)getChildAt(position)).update();
    }
	
	public void removeMenuItemAt(int position) {
        final MenuItemView view = (MenuItemView) getChildAt(position);
        final CMenuItemImpl item = view.getMenuItem();
        if (mItems != null && mUsedItems.contains(item)) {
            mItems.remove(item);
        }

        if (mUsedItems != null && mUsedItems.contains(item)) {
            mUsedItems.remove(item);
        }

        removeViewAt(position);
    }

    public void removeAllMenuItems() {
        if (mItems != null) {
            mItems.clear();
        }
        if (mUsedItems != null) {
            mUsedItems.clear();
        }
//        if (mMoreItems != null) {
//            mMoreItems.clear();
//        }
        removeAllViews();
    }
	
	public void selectedMenuItem(int position) {
        final int count = getChildCount();
        if (position < 0 || position >= count) {
            Log.w(this.getClass().getSimpleName(), "The selected position " + position
                    + " is invalid, menu size " + count);
            return;
        }
        mCurrentSelected = position;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final boolean isSelected = i == position;
            if (isSelected) {
                mCurrentSelectedItem = ((MenuItemView) child).getMenuItem();
            }
            child.setSelected(isSelected);
        }

        if (mMenuItemSelectedListener != null) {
            mMenuItemSelectedListener.onMenuItemSelected(CMenuBarView.this, mCurrentSelectedItem);
        }
    }
	
	public int getCurrentSelected() {
		return mCurrentSelected;
	}
	
	public int getMenuItemCount() {
		return getChildCount();
	}

    public void setOnMenuItemSelectedListener(OnMenuItemSelectedListener listener) {
        mMenuItemSelectedListener = listener;
    }
	
	protected MenuItemView createMenuItemView(CMenuItemImpl item) {
        final MenuItemView menuItem = new MenuItemView(getContext());
        menuItem.init(this, item);
        menuItem.setFocusable(true);
        if (mMenuClickListener == null) {
            mMenuClickListener = new MenuItemClickListener();
        }
        menuItem.setOnClickListener(mMenuClickListener);
        return menuItem;
    }
	
	private class MenuItemClickListener implements OnClickListener {
        public void onClick(View view) {
        	MenuItemView itemView = (MenuItemView) view;
        	final CMenuItemImpl item = itemView.getMenuItem();
            mCurrentSelectedItem = item;
            final int tabCount = getChildCount();
            for (int i = 0; i < tabCount; i++) {
                final View child = getChildAt(i);
                child.setSelected(child == view);
            }

            if (mMenuItemSelectedListener != null) {
                mMenuItemSelectedListener.onMenuItemSelected(CMenuBarView.this, item);
            }

            onCreateSubMenuPanel(item);
        }
    }

	private class MenuItemView extends LinearLayout {
		
		private CMenuItemImpl mMenuItem = null;
		TextView mTxvTitle = null;
		ImageView mImgIcon = null;
		private View mCustomView = null;
		private CMenuBarView mParent = null;

		public MenuItemView(Context context) {
			super(context);
			setOrientation(LinearLayout.VERTICAL);
		}
		
		public void init(CMenuBarView parent, CMenuItemImpl tab) {
			mParent = parent;
			mMenuItem = tab;
			update();
		}
		
		public void update() {
			final CMenuItem item = mMenuItem;
			final View custom = item.getCustomView();
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
				
				final Drawable icon = item.getIcon();
                final CharSequence text = item.getTitle();

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

        CMenuItemImpl getMenuItem() {
			return mMenuItem;
		}
		
	}

    static class CMenuImpl implements CMenu {

        private Context mContext;
        private List<CMenuItemImpl> mItems = null;
        private Resources mRes = null;

        CMenuImpl(Context context) {
            mContext = context;
            mRes = context.getResources();
        }

        Context getContext() {
            return mContext;
        }

        @Override
        public CMenuItem add(CharSequence title) {
            return addInternal(0, 0, title, null, false);
        }

        @Override
        public CMenuItem add(int titleResId) {
            return addInternal(0, 0, mRes.getString(titleResId), null, false);
        }

        @Override
        public CMenuItem addIconItem(int iconResId) {
            return addInternal(0, 0, null, mRes.getDrawable(iconResId), false);
        }

        @Override
        public CMenuItem addIconItem(Drawable icon) {
            return addInternal(0, 0, null, icon, false);
        }

        @Override
        public CMenuItem add(CharSequence title, Drawable icon) {
            return addInternal(0, 0, title, icon, false);
        }

        @Override
        public CMenuItem add(int titleResId, int iconResId) {
            return addInternal(0, 0, mRes.getString(titleResId),
                    mRes.getDrawable(iconResId), false);
        }

        @Override
        public CMenuItem add(int groupId, int itemId, CharSequence title) {
            return addInternal(groupId, itemId, title, null, false);
        }

        @Override
        public CMenuItem add(int groupId, int itemId, int titleResId) {
            return addInternal(groupId, itemId, mRes.getString(titleResId), null, false);
        }

        @Override
        public CMenuItem add(int groupId, int itemId, CharSequence title,
                             Drawable icon) {
            return addInternal(groupId, itemId, title, icon, false);
        }

        @Override
        public CMenuItem add(int groupId, int itemId, int titleResId, int iconResId) {
            return addInternal(groupId, itemId, mRes.getString(titleResId),
                    mRes.getDrawable(iconResId), false);
        }

        private CMenuItemImpl addInternal(int groupId, int itemId, CharSequence title,
                                          Drawable icon, boolean isMore) {
            CMenuItemImpl impl = new CMenuItemImpl(mContext,
                    groupId, itemId, title, icon, isMore);

            if (mItems == null) {
                mItems = new ArrayList<CMenuItemImpl>();
            }
            mItems.add(impl);
            return impl;
        }

        @Override
        public CMenuItem findItem(int id) {
            if (mItems != null) {
                final int N = mItems.size();
                for (int i=0; i<N; i++) {
                    final CMenuItem item = mItems.get(i);
                    if (item.getItemId() == id) {
                        return item;
                    } else if (item.hasSubMenu()) {
                        CSubMenu subMenu = item.getSubMenu();
                        CMenuItem subItem = subMenu.findItem(id);
                        if(subItem != null) {
                            return subItem;
                        }
                    }
                }
            }
            return null;
        }

        @Override
        public CMenuItem getItemAt(int index) {
            if (mItems != null && index > -1 && index < mItems.size()) {
                return mItems.get(index);
            }
            return null;
        }

        @Override
        public int getItemCount() {
            if (mItems != null) {
                return mItems.size();
            }
            return 0;
        }

        @Override
        public CSubMenu addSubMenu(CharSequence title) {
            return addSubMenuInternal(0, 0, title, null);
        }

        @Override
        public CSubMenu addSubMenu(int titleResId) {
            return addSubMenuInternal(0, 0, mRes.getString(titleResId), null);
        }

        @Override
        public CSubMenu addSubMenu(CharSequence title, Drawable icon) {
            return addSubMenuInternal(0, 0, title, icon);
        }

        @Override
        public CSubMenu addSubMenu(int titleResId, int iconResId) {
            return addSubMenuInternal(0, 0, mRes.getString(titleResId),
                    mRes.getDrawable(iconResId));
        }

        @Override
        public CSubMenu addSubMenu(int groupId, int itemId, CharSequence title) {
            return addSubMenuInternal(groupId, itemId, title, null);
        }

        @Override
        public CSubMenu addSubMenu(int groupId, int itemId, int titleResId) {
            return addSubMenuInternal(groupId, itemId, mRes.getString(titleResId), null);
        }

        @Override
        public CSubMenu addSubMenu(int groupId, int itemId, CharSequence title,
                                   Drawable icon) {
            return addSubMenuInternal(groupId, itemId, title, icon);
        }

        @Override
        public CSubMenu addSubMenu(int groupId, int itemId, int titleResId,
                                   int iconResId) {
            return addSubMenuInternal(groupId, itemId, mRes.getString(titleResId),
                    mRes.getDrawable(iconResId));
        }

        private CSubMenuImpl addSubMenuInternal(int groupId, int itemId, CharSequence title,
                                                Drawable icon) {
            CMenuItemImpl item = addInternal(groupId, itemId, title, icon, false);
            CSubMenuImpl impl = new CSubMenuImpl(mContext, item);
            item.setSubMenu(impl);
            return impl;
        }

        @Override
        public CMenu removeItem(int itemId) {
            CMenuItem removeItem = null;
            if (mItems != null) {
                final int N = mItems.size();
                for (int i=0; i<N; i++) {
                    final CMenuItem item = mItems.get(i);
                    if (item.getItemId() == itemId) {
                        removeItem = item;
                        break;
                    }
                }
            }

            if (removeItem != null) {
                mItems.remove(removeItem);
            }
            return this;
        }

        @Override
        public CMenu removeGroup(int groupId) {
            List<CMenuItem> removeItems = new ArrayList<CMenuItem>();
            if (mItems != null) {
                final int N = mItems.size();
                for (int i=0; i<N; i++) {
                    final CMenuItem item = mItems.get(i);
                    if (item.getGroupId() == groupId) {
                        removeItems.add(item);
                    }
                }
            }

            if (!removeItems.isEmpty()) {
                mItems.remove(removeItems);
            }

            return this;
        }

        @Override
        public CMenu clear() {
            mItems.clear();
            mItems = null;
            return this;
        }

        @Override
        public void show() {

        }

        @Override
        public void hide() {

        }
    }

    static class CSubMenuImpl extends CMenuImpl implements CSubMenu {

        private CMenuItemImpl mItemImpl = null;

        CSubMenuImpl(Context context, CMenuItemImpl item) {
            super(context);
            mItemImpl = item;
        }

        @Override
        public CMenuItem getMenuItem() {
            return mItemImpl;
        }

    }

    static class CMenuItemImpl implements CMenuItem {

        private Context mContext = null;
        private int mGroupId = 0;
        private int mItemId = 0;
        private int mIconResId = 0;
        private Drawable mIcon = null;
        private int mTitleResId = 0;
        private CharSequence mTitle = null;
        private int mCustomLayoutResId = 0;
        private View mCustomView = null;
        private Intent mIntent = null;
        private int mVisibility = View.VISIBLE;
        private boolean mIsEnabled = true;
        private boolean mIsSelected = false;
        private boolean mIsMore = false;
        private CSubMenu mSubMenu = null;
        private LayoutInflater mInflater = null;

        public CMenuItemImpl(Context context, int groupId, int itemId,
                             CharSequence title, Drawable icon, boolean isMore) {
            mContext = context;
            mIsMore = isMore;
            mGroupId = groupId;
            mItemId = itemId;
            mTitle = title;
            mIcon = icon;

            mInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setSubMenu(CSubMenu subMenu) {
            mSubMenu = subMenu;
        }

        @Override
        public CMenuItem setIcon(Drawable icon) {
            mIcon = icon;
            mIconResId = 0;
            return this;
        }

        @Override
        public CMenuItem setIcon(int iconResId) {
            mIconResId = iconResId;
            mIcon = null;
            return this;
        }

        @Override
        public CMenuItem setTitle(CharSequence title) {
            mTitle = title;
            mTitleResId = 0;
            return this;
        }

        @Override
        public CMenuItem setTitle(int titleResId) {
            mTitleResId = titleResId;
            mTitle = null;
            return this;
        }

        @Override
        public CMenuItem setCustomView(int customLayoutResId) {
            mCustomLayoutResId = customLayoutResId;
            return this;
        }

        @Override
        public CMenuItem setCustomView(View customView) {
            mCustomView = customView;
            return this;
        }

        @Override
        public CMenuItem setVisibility(int visibility) {
            mVisibility = visibility;
            return this;
        }

        @Override
        public CMenuItem setEnabled(boolean enabled) {
            mIsEnabled = enabled;
            return this;
        }

        @Override
        public CMenuItem setSelected(boolean selected) {
            mIsSelected = selected;
            return this;
        }

        @Override
        public CMenuItem setIntent(Intent intent) {
            mIntent = intent;
            return this;
        }

        @Override
        public boolean hasSubMenu() {
            return mSubMenu != null && mSubMenu.getItemCount()>0;
        }

        @Override
        public CSubMenu getSubMenu() {
            return mSubMenu;
        }

        @Override
        public Drawable getIcon() {
            if (mIcon == null) {
                if (mIconResId > 0) {
                    mIcon = mContext.getResources().getDrawable(mIconResId);
                }
            }
            return mIcon;
        }

        @Override
        public CharSequence getTitle() {
            if (mTitle == null) {
                if (mTitleResId > 0) {
                    mTitle = mContext.getResources().getText(mTitleResId);
                }
            }
            return mTitle;
        }

        @Override
        public View getCustomView() {
            if (mCustomView == null) {
                if (mCustomLayoutResId > 0) {
                    mCustomView = mInflater.inflate(mCustomLayoutResId, null);
                }
            }
            return mCustomView;
        }

        @Override
        public Intent getIntent() {
            return mIntent;
        }

        @Override
        public int getVisibility() {
            return mVisibility;
        }

        @Override
        public boolean isEnabled() {
            return mIsEnabled;
        }

        @Override
        public boolean isSelected() {
            return mIsSelected;
        }

        boolean isMore() {
            return mIsMore;
        }

        @Override
        public CMenuItem setItemId(int id) {
            mItemId = id;
            return this;
        }

        @Override
        public CMenuItem setGroupId(int groupId) {
            mGroupId = groupId;
            return this;
        }

        @Override
        public int getGroupId() {
            return mGroupId;
        }

        @Override
        public int getItemId() {
            return mItemId;
        }

    }
	
}
