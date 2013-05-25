package com.zhangguoyu.demo.actionbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.zhangguoyu.app.CBlock;

/**
 * Created by Forcs on 13-5-20.
 */
public class CDemoSubBlock extends CBlock implements View.OnClickListener {

    private Button mBtnPush = null;
    private Button mBtnPop = null;

    private int mNumb = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CDemoSubBlock", "@@@ onCreate " + getClass().getName());
        setContentView(R.layout.demo2);

        Bundle args = getArguments();
        if (args != null) {
            mNumb = args.getInt("numb");
        }

        mBtnPush = (Button) findViewById(R.id.push);

        mBtnPush.setText(mBtnPush.getText() + "" + mNumb);

        mBtnPush.setOnClickListener(this);
        mBtnPop = (Button) findViewById(R.id.pop);
        mBtnPop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.push:
                Bundle args = new Bundle();
                args.putInt("numb", ++mNumb);
                startBlock(CDemoBlock.class, args);
                break;
            case R.id.pop:
                finish();
                break;
        }
    }

    @Override
    public void onBackButtonClick() {
        Log.d(CDemoSubBlock.class.getSimpleName(), "@@@ onBackButtonClick");
    }
}
