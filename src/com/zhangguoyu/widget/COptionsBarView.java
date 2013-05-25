package com.zhangguoyu.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.zhangguoyu.widget.CMenuBarView;
import com.zhangguoyu.widget.CMenuItem;

/**
 * Created by zhangguoyu on 13-5-17.
 */
public class COptionsBarView extends CMenuBarView {

    public COptionsBarView(Context context) {
        super(context);
    }

    public COptionsBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public COptionsBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int getDefaultDisplayCountLimits() {
        return 3;
    }

    @Override
    protected void onCreateSubMenuPanel(CMenuItem moreItem) {
        super.onCreateSubMenuPanel(moreItem);
    }
}
