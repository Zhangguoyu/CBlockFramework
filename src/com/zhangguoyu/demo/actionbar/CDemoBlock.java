package com.zhangguoyu.demo.actionbar;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import com.zhangguoyu.app.CBlock;

import android.os.Bundle;
import android.util.Log;
import com.zhangguoyu.widget.CMenu;

public class CDemoBlock extends CBlock {

    private static final String LOG_TAG = "CDemoBlock";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.demo);
        CBlock block = findBlockById(R.id.demo_sub_block);
	}

    @Override
    public Animation onCreateExitAnimationForPop(CBlock resumeBlock) {
        return super.onCreateExitAnimationForPop(resumeBlock);
    }

    @Override
    public boolean onCreateNavigationMenu(CMenu menu) {
        menu.add(R.string.demo_menu);
        menu.add(R.string.demo_menu);
        menu.add(R.string.demo_menu);
        menu.add(R.string.demo_menu);
        return true;
    }
}
