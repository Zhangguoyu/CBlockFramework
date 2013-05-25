package com.zhangguoyu.widget;

import com.zhangguoyu.app.CBlock;

public interface CBlockingView {
	
	public CBlock getBlock();

    public void bindBlock(String name, int id, int layoutResId, Object tag);

}
