package com.zhangguoyu.demo.actionbar;

import com.zhangguoyu.app.CBlock;

import android.os.Bundle;
import android.util.Log;
import com.zhangguoyu.widget.CMenu;

public class CDemoBlock extends CBlock {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.demo);
        CBlock block = findBlockById(R.id.demo_sub_block);

	}

    @Override
    public boolean onCreateNavigationMenu(CMenu menu) {
        menu.add(R.string.demo_menu);
        menu.add(R.string.demo_menu);
        menu.add(R.string.demo_menu);
        menu.add(R.string.demo_menu);
        return true;
    }
}
