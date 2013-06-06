package com.zhangguoyu.demo.actionbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.zhangguoyu.app.CBlock;
import com.zhangguoyu.app.CBlockIntent;
import com.zhangguoyu.widget.CMenu;

/**
 * Created by zhangguoyu on 13-5-21.
 */
public class CDemoSubBlock5 extends CBlock implements View.OnClickListener {

    private static final String LOG_TAG = CDemoSubBlock5.class.getSimpleName();

    private Button mBtnPop = null;
    private Button mBtnPush = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo4);

        mBtnPop = (Button) findViewById(R.id.pop);
        mBtnPop.setOnClickListener(this);

    }

    @Override
    public boolean onCreateNavigationMenu(CMenu menu) {
        menu.add(R.string.demo_menu);
        menu.add(R.string.demo_menu);
        menu.add(R.string.demo_menu);
        menu.add(R.string.demo_menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.pop:
                setResult(CBlock.RESULT_OK);
                finish();
                break;

        }

    }
}
