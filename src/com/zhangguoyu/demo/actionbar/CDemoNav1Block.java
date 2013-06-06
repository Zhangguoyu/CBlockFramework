package com.zhangguoyu.demo.actionbar;

import android.os.Bundle;
import android.widget.TextView;
import com.zhangguoyu.app.CBlock;
import com.zhangguoyu.app.CBlockIntent;
import com.zhangguoyu.widget.CMenu;
import com.zhangguoyu.widget.CMenuItem;

/**
 * Created by zhangguoyu on 13-6-3.
 */
public class CDemoNav1Block extends CBlock {

    private static final int NAV_6 = 6;
    private static final int NAV_7 = 7;
    private static final int NAV_8 = 8;
    private static final int NAV_9 = 9;
    private static final int NAV_10 = 10;
    private static final int NAV_11 = 11;

    private TextView mTxvDemoContent = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_block_nav1);
        mTxvDemoContent = (TextView) findViewById(R.id.demo_nav1_content);
    }

    @Override
    public void onNewIntent(CBlockIntent intent) {
        super.onNewIntent(intent);
        Bundle arg = getArguments();
        int index = arg.getInt("index");
        mTxvDemoContent.setText("This is Nav " + index);
    }

    @Override
    public boolean onCreateNavigationMenu(CMenu menu) {

        menu.add(0, NAV_6, R.string.demo_nav_6, R.drawable.ic_launcher);
        menu.add(0, NAV_7, R.string.demo_nav_7, R.drawable.ic_launcher);
        menu.add(0, NAV_8, R.string.demo_nav_8, R.drawable.ic_launcher);
        menu.add(0, NAV_9, R.string.demo_nav_9, R.drawable.ic_launcher);
        menu.add(0, NAV_10, R.string.demo_nav_10, R.drawable.ic_launcher);
        menu.add(0, NAV_11, R.string.demo_nav_11, R.drawable.ic_launcher);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(CMenu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsMenuItemSelected(CMenuItem item) {
        return super.onOptionsMenuItemSelected(item);
    }

    @Override
    public boolean onNavigationMenuItemSelected(CMenuItem item) {
        return super.onNavigationMenuItemSelected(item);
    }
}
