package com.zhangguoyu.demo.actionbar;

import android.os.Bundle;
import android.view.Menu;

import com.zhangguoyu.app.CPageBlockActivity;
import com.zhangguoyu.widget.CMenu;

public class MainActivity extends CPageBlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadBlocksFromResurce(R.xml.blocks);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

    @Override
    public boolean onCreateOptionsMenu(CMenu menu) {
        menu.add(R.string.demo_menu, R.drawable.ic_launcher);
        menu.add(R.string.demo_menu, R.drawable.ic_launcher);
        menu.add(R.string.demo_menu, R.drawable.ic_launcher);
        return true;
    }

}
