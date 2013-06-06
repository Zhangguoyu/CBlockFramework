package com.zhangguoyu.demo.actionbar;

import android.os.Bundle;
import android.util.Log;
import com.zhangguoyu.app.CBlock;
import com.zhangguoyu.app.CBlockIntent;
import com.zhangguoyu.widget.*;

/**
 * Created by zhangguoyu on 13-6-3.
 */
public class CDemoMainBlock extends CBlock implements CMenuBarView.OnMenuItemSelectedListener {

    private static final String LOG_TAG = "CDemoMainBlock";

    private static final int TAB_1 = 1;
    private static final int TAB_2 = 2;
    private static final int TAB_3 = 3;
    private static final int TAB_4 = 4;

    private CTextMenuBar mTabBar = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.demo_block_main);

        mTabBar = (CTextMenuBar) findViewById(R.id.demo_text_tab_bar);
        mTabBar.addTextMenu(TAB_1, R.string.demo_tab_0)
                .addTextMenu(TAB_2, R.string.demo_tab_1)
                .addTextMenu(TAB_3, R.string.demo_tab_2)
                .addTextMenu(TAB_4, R.string.demo_tab_3);
        mTabBar.setOnMenuItemSelectedListener(this);
    }

    @Override
    public void onMenuItemSelected(CMenuBarView view, CMenuItem menuItem) {
        Log.d(LOG_TAG, "@@@ onMenuItemSelected " + menuItem.getTitle());
        switch (menuItem.getItemId()) {
            case TAB_1:
                startBlock(new CBlockIntent(R.id.demo_tab1_id));
                break;
            case TAB_2:
                startBlock(new CBlockIntent(R.id.demo_tab2_id));
                break;
            case TAB_3:
                startBlock(new CBlockIntent(R.id.demo_tab3_id));
                break;
            case TAB_4:
                startBlock(new CBlockIntent(R.id.demo_tab4_id));
                break;
        }
    }

    @Override
    public boolean onCreateNavigationMenu(CMenu menu) {
        return true;
    }
}
