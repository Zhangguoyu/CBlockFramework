package com.zhangguoyu.demo.actionbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.zhangguoyu.app.CBlock;
import com.zhangguoyu.app.CBlockIntent;

/**
 * Created by Forcs on 13-5-20.
 */
public class CDemoSubBlock extends CBlock implements View.OnClickListener {

    private static final String LOG_TAG = "CDemoSubBlock";

    private Button mBtnPush = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.demo2);
        setBlockInterceptor(mInterceptor);
        mBtnPush = (Button) findViewById(R.id.push);

        mBtnPush.setText(mBtnPush.getText());
        mBtnPush.setOnClickListener(this);
    }

    @Override
    public boolean onPrepareStartBlock(CBlockIntent intent) {
        //startBlock(CDemoSubBlock5.class, null);
        if (intent.getBlockId() == R.id.demo2_id) {
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.push:
                //startBlock(CDemoSubBlock2.class, null);
                startBlock(new CBlockIntent(R.id.demodemo_id));
                break;
        }
    }

    @Override
    public boolean onBackButtonClick() {
        Log.d(CDemoSubBlock.class.getSimpleName(), "@@@ onBackButtonClick");
        return false;
    }

    private BlockInterceptor mInterceptor = new BlockInterceptor() {
        @Override
        public boolean onProcess(CBlock src, CBlockIntent intent) {
            startBlock(new CBlockIntent(R.id.demo2_id));
            return true;
        }
    };


}
