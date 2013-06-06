package com.zhangguoyu.demo.actionbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.zhangguoyu.app.CBlock;
import com.zhangguoyu.app.CBlockIntent;
import com.zhangguoyu.widget.CMenu;

/**
 * Created by zhangguoyu on 13-6-3.
 */
public class CDemoNav1SubBlockA extends CBlock implements View.OnClickListener {

    private Button mBtnSendTo = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_block_nav1_sub_a);
        mBtnSendTo = (Button) findViewById(R.id.demo_nav1_sub_a_btn);
        mBtnSendTo.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onNewIntent(CBlockIntent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onClick(View view) {
        sendMessageToBlock(new CBlockIntent(R.id.demo_block_nav1_sub_b_id));
    }
}
