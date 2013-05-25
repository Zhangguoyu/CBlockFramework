package com.zhangguoyu.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import com.zhangguoyu.app.CBlock;
import com.zhangguoyu.demo.actionbar.R;

public class CFrameLayoutBlock extends FrameLayout implements CBlockingView {

	private CBlock mBlock = null;
    private String mBlockClassName = null;
    private int mBlockId = CBlock.NO_ID;
    private Object mBlockTag = null;
    private int mBlockLayoutResId = 0;
    private CBlock mParent = null;
	
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

		if (attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CBlock);
			final String blockClassName = a.getString(R.styleable.CBlock_blockName);
            final int blockId = a.getResourceId(R.styleable.CBlock_blockId, CBlock.NO_ID);
            final Object blockTag = a.getString(R.styleable.CBlock_blockTag);
            final int blockLayoutResId = a.getResourceId(R.styleable.CBlock_blockLayout, 0);
			instantiateBlock(blockClassName, blockId, blockLayoutResId, blockTag);
			a.recycle();
		}
	}

	@Override
	public CBlock getBlock() {
		return mBlock;
	}

    @Override
    public void bindBlock(String name, int id, int layoutResId, Object tag) {
        instantiateBlock(name, id, layoutResId, tag);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        CBlock parentBlock = null;
        ViewParent parent = getParent();
        while(parent != null) {
            if (!(parent instanceof CBlockingView)) {
                parent = parent.getParent();
                continue;
            }

            mParent = ((CBlockingView) parent).getBlock();
            break;
        }

        if (mParent != null && mBlock != null) {
            mBlock.attachToParent(parentBlock, true);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mBlock != null) {
            mBlock.detachFromParent();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.blockId = mBlockId;
        ss.blockClassName = mBlockClassName;
        ss.blockLayoutResId = mBlockLayoutResId;
        ss.blockTag = mBlockTag;
        if (mBlock != null) {
            //ss.blockSavedState = mBlock.onSaveInstanceState();
        }
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        if (mBlock == null) {
            instantiateBlock(ss.blockClassName, ss.blockId,
                    ss.blockLayoutResId, ss.blockTag);
        }

        if (mBlock != null) {
            //mBlock.onRestoreInstanceState(ss.blockSavedState);
        }

    }

    private void instantiateBlock(String className, int id, int layoutResId, Object tag) {
        mBlockId = id;
        mBlockClassName = className;
        mBlockLayoutResId = layoutResId;
        mBlockTag = tag;

        if (!TextUtils.isEmpty(className)) {
            try {
                Class<?> blockClass = getContext().getClassLoader().loadClass(className);
                mBlock = (CBlock) blockClass.newInstance();
                mBlock.setId(id)
                        .setTag(tag)
                        .setContainer(this);
                if (layoutResId > 0) {
                    mBlock.setContentView(layoutResId);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static class SavedState extends BaseSavedState {

        String blockClassName;
        int blockId;
        int blockLayoutResId;
        Object blockTag;
        Parcelable blockSavedState;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel in) {
            super(in);
            blockId = in.readInt();
            blockLayoutResId = in.readInt();
            blockClassName = in.readString();
            blockTag = in.readValue(getClass().getClassLoader());
            blockSavedState = in.readParcelable(getClass().getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(blockId);
            parcel.writeInt(blockLayoutResId);
            parcel.writeString(blockClassName);
            parcel.writeValue(blockTag);
            parcel.writeParcelable(blockSavedState, i);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
    }
}
