package com.zhangguoyu.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.zhangguoyu.app.CBlock;
import com.zhangguoyu.demo.actionbar.R;

/**
 * Created by zhangguoyu on 13-6-3.
 */
public class CBlockingViewHelper implements CBlockingView {

    private CBlock mBlock;
    private ViewGroup mBlockingView;
    private String mBlockClassName = null;
    private int mBlockId = CBlock.NO_ID;
    private String mBlockTag = null;
    private int mBlockLayoutResId = 0;
    private CBlock mParent = null;

    public CBlockingViewHelper(ViewGroup blockingView) {
        mBlockingView = blockingView;
    }

    public void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CBlock);

            try {
                final String blockClassName = a.getString(R.styleable.CBlock_blockName);
                final Class<?> cls = mBlockingView.getContext().getClassLoader().loadClass(blockClassName);
                final int blockId = a.getResourceId(R.styleable.CBlock_blockId, CBlock.NO_ID);
                final String blockTag = a.getString(R.styleable.CBlock_blockTag);
                final int blockLayoutResId = a.getResourceId(R.styleable.CBlock_blockLayout, 0);
                instantiateBlock(cls, blockId, blockLayoutResId, blockTag);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (a != null) {
                    a.recycle();
                }
            }
        }
    }

    public CBlock getBlock() {
        return mBlock;
    }

    @Override
    public void bindBlock(Class<?> className, int id, int layoutResId, String tag) {
        instantiateBlock(className, id, layoutResId, tag);
    }

    private void instantiateBlock(Class<?> className, int id, int layoutResId, String tag) {
        mBlockId = id;
        mBlockClassName = className.getName();
        mBlockLayoutResId = layoutResId;
        mBlockTag = tag;

        try {
            mBlock = (CBlock) className.newInstance();
            mBlock.setId(id)
                    .setTag(tag)
                    .setContainer(mBlockingView);
//            if (layoutResId > 0) {
//                mBlock.setContentView(layoutResId);
//            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    void onAttachedToWindow() {
        ViewParent parent = mBlockingView.getParent();
        while(parent != null) {
            if (parent instanceof CBlockingView) {
                mParent = ((CBlockingView) parent).getBlock();
                if (mParent != null) {
                    break;
                }
            }

            parent = parent.getParent();
        }

        if (mParent != null && mBlock != null) {
            mBlock.attachToParent(mParent);
        }
    }

    void onDetachFromWindow() {
        if (mBlock != null) {
            mBlock.detachFromParent();
        }
    }

    Parcelable onSaveInstanceState(Parcelable superState) {
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

    void onRestoreInstanceState(Parcelable state) {

        SavedState ss = (SavedState) state;

        if (mBlock == null) {
            try {
                final Class<?> cls = mBlockingView.getContext().getClassLoader()
                        .loadClass(ss.blockClassName);
                instantiateBlock(cls, ss.blockId,
                        ss.blockLayoutResId, ss.blockTag);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        if (mBlock != null) {
            //mBlock.onRestoreInstanceState(ss.blockSavedState);
        }
    }

    public static class SavedState extends View.BaseSavedState {

        String blockClassName;
        int blockId;
        int blockLayoutResId;
        String blockTag;
        Parcelable blockSavedState;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel in) {
            super(in);
            blockId = in.readInt();
            blockLayoutResId = in.readInt();
            blockClassName = in.readString();
            blockTag = in.readString();
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
            parcel.writeString(blockTag);
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
