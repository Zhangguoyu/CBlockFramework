package com.zhangguoyu.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;
import com.zhangguoyu.app.CBlock;
import com.zhangguoyu.demo.actionbar.R;

public abstract class CViewGroupBlock extends ViewGroup implements CBlockingView {

    private CBlockingViewHelper mHelper = null;
	
	public CViewGroupBlock(Context context) {
		super(context);
        init(context, null);
	}
	
	public CViewGroupBlock(Context context, AttributeSet attrs) {
		super(context, attrs);
        init(context, attrs);
	}

	public CViewGroupBlock(Context context, AttributeSet attrs, int defStyle) {
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
