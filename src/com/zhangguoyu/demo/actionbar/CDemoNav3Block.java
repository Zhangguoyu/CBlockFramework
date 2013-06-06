package com.zhangguoyu.demo.actionbar;

import android.os.Bundle;
import com.zhangguoyu.app.CBlock;
import com.zhangguoyu.widget.CMenu;

/**
 * Created by zhangguoyu on 13-6-3.
 */
public class CDemoNav3Block extends CBlock {
    private static final int NAV_6 = 6;
    private static final int NAV_7 = 7;
    private static final int NAV_8 = 8;
    private static final int NAV_9 = 9;
    private static final int NAV_10 = 10;
    private static final int NAV_11 = 11;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_block_nav3);
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
}
