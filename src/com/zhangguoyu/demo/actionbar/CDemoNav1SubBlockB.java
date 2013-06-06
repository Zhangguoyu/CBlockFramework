package com.zhangguoyu.demo.actionbar;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.zhangguoyu.app.CBlock;
import com.zhangguoyu.app.CBlockIntent;
import com.zhangguoyu.widget.CMenu;

/**
 * Created by zhangguoyu on 13-6-3.
 */
public class CDemoNav1SubBlockB extends CBlock {

    private static final String LOG_TAG = "CDemoNav1SubBlockB";

    private TextView mTxvDemoContent = null;
    private int mCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_block_nav1_sub_b);
        mTxvDemoContent = (TextView) findViewById(R.id.demo_nav1_content_sub_b);

        if (savedInstanceState != null) {
            mCount = savedInstanceState.getInt("count");
            mTxvDemoContent.setText("receive from \n Sub A " + mCount);
        }
    }

    @Override
    public void onReceiveMessageFromBlock(CBlock from, CBlockIntent intent) {
        super.onReceiveMessageFromBlock(from, intent);
        Log.d(LOG_TAG, "@@@ onReceiveMessageFromBlock " + from.getId());
        Bundle args = intent.getArguments();
        if (args != null) {
            //xxxx
        }
        mCount++;
        mTxvDemoContent.setText("receive from \n Sub A " + mCount);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("count", mCount);
    }

}
