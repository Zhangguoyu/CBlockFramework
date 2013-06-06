package com.zhangguoyu.widget;

import com.zhangguoyu.app.CBlock;

public interface CBlockingView {
	
	public CBlock getBlock();

    public void bindBlock(Class<?> className, int id, int layoutResId, String tag);

}
