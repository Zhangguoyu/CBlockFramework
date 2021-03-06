package com.zhangguoyu.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by zhangguoyu on 13-5-23.
 */
public class CTextMenuBar extends CMenuBarView {

    public CTextMenuBar(Context context) {
        super(context);
    }

    public CTextMenuBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CTextMenuBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CTextMenuBar addTextMenu(int titleResId) {
        return addTextMenu(getResources().getText(titleResId));
    }

    public CTextMenuBar addTextMenu(CharSequence title) {
        return addTextMenu(0, title);
    }

    public CTextMenuBar addTextMenu(int itemId, int titleResId) {
        return addTextMenu(itemId, getResources().getText(titleResId));
    }

    public CTextMenuBar addTextMenu(int itemId, CharSequence title) {
        addTextMenuInternal(itemId, title);
        return this;
    }

    private void addTextMenuInternal(int itemId, CharSequence title) {
        CMenuItemImpl impl = new CMenuItemImpl(getContext(), 0, itemId, title, null, false);
        addMenuItem(impl);
    }
}
