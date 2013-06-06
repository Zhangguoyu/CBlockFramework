package com.zhangguoyu.demo.actionbar;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.zhangguoyu.app.CBlockActivity;
import com.zhangguoyu.app.CBlockIntent;
import com.zhangguoyu.app.CPageBlockActivity;
import com.zhangguoyu.widget.CActionBar;
import com.zhangguoyu.widget.CMenu;
import com.zhangguoyu.widget.CMenuItem;

public class MainActivity extends CBlockActivity {

    private static final String LOG_TAG = "MainActivity";

    private static final int NAV_1 = 1;
    private static final int NAV_2 = 2;
    private static final int NAV_3 = 3;
    private static final int NAV_4 = 4;
    private static final int NAV_5 = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

    @Override
    public boolean onCreateNavigationMenu(CMenu menu) {
        menu.add(0, NAV_1, R.string.demo_nav_1, R.drawable.ic_launcher);
        menu.add(0, NAV_2, R.string.demo_nav_2, R.drawable.ic_launcher);
        menu.add(0, NAV_3, R.string.demo_nav_3, R.drawable.ic_launcher);
        menu.add(0, NAV_4, R.string.demo_nav_4, R.drawable.ic_launcher);
        menu.add(0, NAV_5, R.string.demo_nav_5, R.drawable.ic_launcher);
        return super.onCreateNavigationMenu(menu);
    }

    @Override
    public void onNavigationMenuItemSelected(CMenuItem item) {
        super.onNavigationMenuItemSelected(item);
        Bundle arg = new Bundle();
        arg.putInt("index", item.getItemId());
        switch(item.getItemId()) {
            case NAV_1:
                startBlock(new CBlockIntent(R.id.demo_nav1_id).setArguments(arg));
                break;
            case NAV_2:
                startBlock(new CBlockIntent(R.id.demo_nav2_id).setArguments(arg));
                break;
            case NAV_3:
                startBlock(new CBlockIntent(R.id.demo_nav3_id).setArguments(arg));
                break;
            case NAV_4:
                startBlock(new CBlockIntent(R.id.demo_nav4_id).setArguments(arg));
                break;
            case NAV_5:
                startBlock(new CBlockIntent(R.id.demo_nav5_id).setArguments(arg));
                break;
        }
    }

    @Override
    public void onTabSelected(CActionBar.CTab selectedTab) {
        super.onTabSelected(selectedTab);
    }
}
