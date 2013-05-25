package com.zhangguoyu.widget;

import android.graphics.drawable.Drawable;

public interface CMenu {

	public CMenuItem add(CharSequence title);

	public CMenuItem add(int titleResId);

    public CMenuItem addIconItem(int iconResId);

    public CMenuItem addIconItem(Drawable icon);

	public CMenuItem add(CharSequence title, Drawable icon);

	public CMenuItem add(int titleResId, int iconResId);

	public CMenuItem add(int groupId, int itemId, CharSequence title);

	public CMenuItem add(int groupId, int itemId, int titleResId);

	public CMenuItem add(int groupId, int itemId, CharSequence title, Drawable icon);

	public CMenuItem add(int groupId, int itemId, int titleResId, int iconResId);

	public CMenuItem findItem(int id);

	public CMenuItem getItemAt(int index);

	public int getItemCount();

	public CSubMenu addSubMenu(CharSequence title);

	public CSubMenu addSubMenu(int titleResId);

	public CSubMenu addSubMenu(CharSequence title, Drawable icon);

	public CSubMenu addSubMenu(int titleResId, int iconResId);

	public CSubMenu addSubMenu(int groupId, int itemId, CharSequence title);

	public CSubMenu addSubMenu(int groupId, int itemId, int titleResId);

	public CSubMenu addSubMenu(int groupId, int itemId, CharSequence title, Drawable icon);

	public CSubMenu addSubMenu(int groupId, int itemId, int titleResId, int iconResId);

	public CMenu removeItem(int itemId);

	public CMenu removeGroup(int groupId);

	public CMenu clear();

	public void show();

	public void hide();
	
}
