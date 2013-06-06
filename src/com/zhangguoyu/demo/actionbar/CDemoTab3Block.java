package com.zhangguoyu.demo.actionbar;

import android.os.Bundle;
import com.zhangguoyu.app.CBlock;
import com.zhangguoyu.widget.CMenu;

/**
 * Created by zhangguoyu on 13-6-3.
 */
public class CDemoTab3Block extends CBlock {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_block_tab3);
    }

    @Override
    public boolean onCreateNavigationMenu(CMenu menu) {
        return true;
    }
}
