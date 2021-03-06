package com.zhangguoyu.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.zhangguoyu.app.CBlock;

public class CLinearLayoutBlock extends LinearLayout implements CBlockingView {

    private CBlockingViewHelper mHelper = null;
	
	public CLinearLayoutBlock(Context context) {
		super(context);
        init(context, null);
	}
	
	public CLinearLayoutBlock(Context context, AttributeSet attrs) {
		super(context, attrs);
        init(context, attrs);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public CLinearLayoutBlock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
        init(context, attrs);
	}

    private void init(Context context, AttributeSet attrs) {
        mHelper = new CBlockingViewHelper(this);
        mHelper.init(context, attrs);
    }

    @Override
    public CBlock getBlock() {
        return mHelper.getBlock();
    }

    @Override
    public void bindBlock(Class<?> className, int id, int layoutResId, String tag) {
        mHelper.bindBlock(className, id, layoutResId, tag);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHelper.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHelper.onDetachFromWindow();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return mHelper.onSaveInstanceState(superState);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof CBlockingViewHelper.SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        CBlockingViewHelper.SavedState ss = (CBlockingViewHelper.SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mHelper.onRestoreInstanceState(ss);
    }
}
