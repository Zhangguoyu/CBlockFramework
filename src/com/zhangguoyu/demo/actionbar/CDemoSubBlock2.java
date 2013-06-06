package com.zhangguoyu.demo.actionbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import com.zhangguoyu.app.CBlock;
import com.zhangguoyu.app.CBlockIntent;
import com.zhangguoyu.widget.CMenu;

/**
 * Created by zhangguoyu on 13-5-21.
 */
public class CDemoSubBlock2 extends CBlock implements View.OnClickListener {

    private static final String LOG_TAG = CDemoSubBlock2.class.getSimpleName();

    private Button mBtnPop = null;
    private Button mBtnPush = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo3);

        mBtnPop = (Button) findViewById(R.id.pop);
        mBtnPop.setOnClickListener(this);
        mBtnPush = (Button) findViewById(R.id.push);
        mBtnPush.setOnClickListener(this);
    }

    @Override
    public Animation onCreateEnterAnimationForPush(CBlock fromBlock) {
        Animation enterTranAnim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        enterTranAnim.setDuration(300);
        enterTranAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        return enterTranAnim;
    }

    @Override
    public Animation onCreateExitAnimationForPop(CBlock toBlock) {
        Animation exitTranAnim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        exitTranAnim.setDuration(300);
        exitTranAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        return exitTranAnim;
    }

    @Override
    public boolean onCreateNavigationMenu(CMenu menu) {
        menu.add(R.string.demo_menu);
        menu.add(R.string.demo_menu);
        menu.add(R.string.demo_menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pop:
                finish();
                break;
            case R.id.push:
                startBlock(new CBlockIntent(R.id.demodemo_id));
                break;
        }

    }
}
