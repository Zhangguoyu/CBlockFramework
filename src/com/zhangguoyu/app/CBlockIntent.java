package com.zhangguoyu.app;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Created by zhangguoyu on 13-5-30.
 */
public class CBlockIntent implements Parcelable, Cloneable {

    public static final String ACTION_MAIN = "cn.emoney.level2.MAIN";

    private Bundle mArgs = null;
    private int mId = CBlock.NO_ID;
    private String mClazz = null;
    private String mAction = null;

    public CBlockIntent() {}

    public CBlockIntent(Parcel in) {
        mId = in.readInt();
        mClazz = in.readString();
        mArgs = in.readBundle();
        mAction = in.readString();
    }

    public CBlockIntent(CBlockIntent src) {
        if (src != null) {
            mId = src.mId;
            mClazz = src.mClazz;
            mArgs = (Bundle) src.mArgs.clone();
        }
    }

    public CBlockIntent(int id) {
        mId = id;
    }

    public CBlockIntent(String action) {
        mAction = action;
    }

    public CBlockIntent(Class<?> clazz) {
        mClazz = clazz.getName();
    }

    public CBlockIntent(String packageName, String className) {
        mClazz = combine(packageName, className);
    }

    public CBlockIntent(Context context, String className) {
        String pkg = null;
        if (context != null) {
            pkg = context.getPackageName();
        }
        mClazz = combine(pkg, className);
    }

    private String combine(String packageName, String className) {
        if (TextUtils.isEmpty(className)) {
            throw new RuntimeException("ClassName can not be null or empty");
        }

        String clazz = null;

        if (TextUtils.isEmpty(packageName) || className.startsWith(packageName)) {
            clazz = className;
        } else {
            if (packageName.endsWith(".") && className.startsWith(".")) {
                clazz = packageName + className.substring(1);
            } else if (packageName.endsWith(".") || className.startsWith(".")) {
                clazz = packageName + className;
            } else {
                clazz = packageName + "." + className;
            }
        }

        return clazz;
    }

    public CBlockIntent setBlockId(int id) {
        mId = id;
        return this;
    }

    public int getBlockId() {
        return mId;
    }

    public CBlockIntent setBlockClassName(String className) {
        mClazz = className;
        return this;
    }

    public CBlockIntent setBlockClass(Class<?> clazz) {
        mClazz = clazz.getName();
        return this;
    }

    public String getBlockClassName() {
        return mClazz;
    }

    public CBlockIntent setArguments(Bundle args) {
        mArgs = args;
        return this;
    }

    public Bundle getArguments() {
        return mArgs;
    }

    public CBlockIntent setAction(String action) {
        mAction = action;
        return this;
    }

    public String getAction() {
        return mAction;
    }

    @Override
    public Object clone() {
        return new CBlockIntent(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeInt(mId);
        parcel.writeString(mClazz);
        parcel.writeBundle(mArgs);
        parcel.writeString(mAction);
    }

    public static final Creator<CBlockIntent> CREATOR = new Creator<CBlockIntent>() {
        @Override
        public CBlockIntent createFromParcel(Parcel parcel) {
            return new CBlockIntent(parcel);
        }

        @Override
        public CBlockIntent[] newArray(int i) {
            return new CBlockIntent[i];
        }
    };

    @Override
    public String toString() {
        return "CBlockIntent "+hashCode()
                + " {id:" + mId
                + ", action:" + mAction
                + ", class:" + mClazz
                + ", Arguments:" + mArgs + "}";
    }
}
