package com.zhangguoyu.widget;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.zhangguoyu.app.CBlock;
import com.zhangguoyu.widget.CBlockingViewHelper.SavedState;

public class CFrameLayoutBlock extends FrameLayout implements CBlockingView {

    private CBlockingViewHelper mHelper = null;
	
	public CFrameLayoutBlock(Context context) {
		super(context);
		init(context, null);
	}
	
	public CFrameLayoutBlock(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public CFrameLayoutBlock(Context context, AttributeSet attrs, int defStyle) {
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
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mHelper.onRestoreInstanceState(ss);
    }
}
