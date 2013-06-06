package com.zhangguoyu.demo.actionbar;

import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import com.zhangguoyu.app.CBlock;
import com.zhangguoyu.app.CBlockIntent;
import com.zhangguoyu.app.CPageBlock;

/**
 * Created by zhangguoyu on 13-5-28.
 */
public class CDemoPageBlock extends CPageBlock {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inflateBlockPagesFromXmlRes(R.xml.blocks);
    }

    @Override
    public Animation onCreateExitAnimationForPause(CBlock toBlock) {
        Animation exitTranAnim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -1f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        exitTranAnim.setDuration(300);
        exitTranAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        return exitTranAnim;
    }

    @Override
    public Animation onCreateEnterAnimationForResume(CBlock exitBlock) {
        Animation enterTranAnim = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -1f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f
        );
        enterTranAnim.setDuration(300);
        enterTranAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        return enterTranAnim;
    }
}
