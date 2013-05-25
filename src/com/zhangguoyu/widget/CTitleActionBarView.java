package com.zhangguoyu.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhangguoyu.widget.CActionBar.CTab;
import com.zhangguoyu.demo.actionbar.R;

public class CTitleActionBarView extends LinearLayout {
	
	private View mBackButton = null;
	private View mHomeView = null;
	private View mTitleView = null;
	private ViewGroup mTitleViewContainer = null;
	private View mCustomView = null;
	private LinearLayout mTitleBar = null;
	
	private CTabBarView mTabBar = null;
    private View mOptionsBar = null;
	private TitleViewWrapper mTitleViewWrapper = null;

    private OnBackButtonClickListener mBackButtonListener = new OnBackButtonClickListener();
	
	public CTitleActionBarView(Context context) {
		super(context);
		init();
	}
	
	public CTitleActionBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public CTitleActionBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() {
		setOrientation(LinearLayout.VERTICAL);
	}
	
	public void setBackButton(View view) {
		ensureTitleBarExist();
		if (mBackButton != null) {
			mTitleBar.removeView(mBackButton);
		}
		
		mBackButton = view;
		if(view != null) {
            mBackButton.setOnClickListener(mBackButtonListener);
			mTitleBar.addView(view, 0);
		}
	}
	
	public void setHomeView(View view) {
		ensureTitleBarExist();
		if (mHomeView != null) {
			mTitleBar.removeView(mHomeView);
		}
		
		mHomeView = view;
		
		if(view != null) {
			int pos = 0;
			if(mBackButton != null) {
				pos = 1;
			}
			mTitleBar.addView(view, pos);
		}
	}
	

	public void setCustomView(View view) {
		ensureTitleBarExist();
		if (mCustomView != null) {
			mTitleBar.removeView(mCustomView);
		}
		
		mCustomView = view;
		
		if(view != null) {
			int pos = 0;
			if(mBackButton != null) {
				pos++;
			}
			if(mHomeView != null) {
				pos++;
			}
			mTitleBar.addView(view, pos);
		}
	}
	
	public void setTitleView(View view) {
		ensureTitleBarExist();
		if (mTitleViewContainer == null && view != null) {
			mTitleViewContainer = new FrameLayout(getContext());
			LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			p.gravity = Gravity.CENTER_VERTICAL;
			p.weight = 1.0f;
			int pos = 0;
			if (mBackButton != null) {
				pos++;
			} 
			if (mHomeView != null) {
				pos++;
			}
			if (mCustomView != null) {
				pos++;
			}
			mTitleViewContainer.setBackgroundColor(Color.LTGRAY);
			mTitleBar.addView(mTitleViewContainer, pos, p);
		}
		
		if (mTitleView != null) {
			mTitleViewContainer.removeView(mTitleView);
		}
		
		mTitleView = view;
		
		if(view != null) {
			FrameLayout.LayoutParams tp = new FrameLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			tp.gravity = Gravity.CENTER;
			mTitleViewContainer.addView(mTitleView, tp);
		}
	}

	public void setMenus(View view) {
		ensureTitleBarExist();
		if (mOptionsBar != null) {
			mTitleBar.removeView(mOptionsBar);
		}

        mOptionsBar = view;

		int pos = Math.max(0, mTitleBar.getChildCount());
		if(view != null) {
			LayoutParams p = generateDefaultLayoutParams();
			p.gravity |= Gravity.RIGHT;
			mTitleBar.addView(mOptionsBar, pos);
		}
	}
	
	private void ensureTitleBarExist() {
		if (mTitleBar != null) {
			return;
		}
		mTitleBar = new LinearLayout(getContext());
		int layoutHeight = getContext().getResources().getDimensionPixelSize(
				R.dimen.actionbar_default_height);
		LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT, layoutHeight);
		addView(mTitleBar, 0, p);
	}
	
	public void addTab(CTab tab) {
		ensureTabBarExist();
		mTabBar.addTab(tab, true);
	}
	
	public void addTab(CTab tab, int position) {
		ensureTabBarExist();
		mTabBar.addTab(tab, position, true);
	}
	
	public void addTab(CTab tab, boolean selected) {
		ensureTabBarExist();
		mTabBar.addTab(tab, selected);
	}
	
	public void addTab(CTab tab, int position, boolean selected) {
		ensureTabBarExist();
		mTabBar.addTab(tab, position, selected);
	}
	
	private void ensureTabBarExist() {
		if(mTabBar != null) {
			return;
		}
        setMenus(new COptionsBarView(getContext()));
		mTabBar = new CTabBarView(getContext());
		LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		addView(mTabBar, p);
	}
	
	public int getTabCount() {
		if(mTabBar == null) {
			return 0;
		} 
		return mTabBar.getTabCount();
	}
	
	public void updateTab(int position) {
		if(mTabBar != null) {
			mTabBar.updateTab(position);
		}
	}
	
	public void removeTabAt(int position) {
		if(mTabBar != null) {
			mTabBar.removeTabAt(position);
		}
	}
	
	public void removeAllTabs() {
		if(mTabBar != null) {
			mTabBar.removeAllTabs();
		}
	}
	
	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		LayoutParams p = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		p.gravity = Gravity.CENTER_VERTICAL;
		return p;
	}
	
	public void setDisplayShowBackButtonEnable(boolean showBackButton) {
		if (mBackButton != null) {
			mBackButton.setVisibility(showBackButton?View.VISIBLE:View.GONE);
		}
	}

    public void setDisplayShowBackButtonEnable(boolean showBackButton, Animation anim) {
        if (mBackButton == null) {
            return;
        }

        final boolean showing = mBackButton.getVisibility() == View.VISIBLE;
        if (showBackButton != showing) {
            mBackButton.clearAnimation();
            if (anim != null) {
                mBackButton.setAnimation(anim);
            }
            mBackButton.setVisibility(showBackButton?View.VISIBLE:View.GONE);
        }
    }

	public void setDisplayShowHomeEnable(boolean showHome) {
		if (mHomeView != null) {
			mHomeView.setVisibility(showHome?View.VISIBLE:View.GONE);
		}
	}

    public void setDisplayShowHomeEnable(boolean showHome, Animation anim) {
        if (mHomeView == null) {
            return;
        }

        final boolean showing = mHomeView.getVisibility() == View.VISIBLE;
        if (showHome != showing) {
            mHomeView.clearAnimation();
            if (anim != null) {
                mHomeView.setAnimation(anim);
            }
            mHomeView.setVisibility(showHome?View.VISIBLE:View.GONE);
        }
    }

	public void setDisplayShowCustomEnable(boolean showCustom) {
		if (mCustomView != null) {
			mCustomView.setVisibility(showCustom?View.VISIBLE:View.GONE);
		}
	}

    public void setDisplayShowCustomEnable(boolean showCustom, Animation anim) {
        if (mCustomView == null) {
            return;
        }

        final boolean showing = mCustomView.getVisibility() == View.VISIBLE;
        if (showCustom != showing) {
            mCustomView.clearAnimation();
            if (anim != null) {
                mCustomView.setAnimation(anim);
            }
            mCustomView.setVisibility(showCustom?View.VISIBLE:View.GONE);
        }
    }

	public void setDisplayShowTitleEnable(boolean showTitle) {
		if (mTitleView != null) {
			mTitleView.setVisibility(showTitle?View.VISIBLE:View.GONE);
		}
	}

    public void setDisplayShowTitleEnable(boolean showTitle, Animation anim) {
        if (mTitleView == null) {
            return;
        }

        final boolean showing = mTitleView.getVisibility() == View.VISIBLE;
        if (showTitle != showing) {
            mTitleView.clearAnimation();
            if (anim != null) {
                mTitleView.setAnimation(anim);
            }
            mTitleView.setVisibility(showTitle?View.VISIBLE:View.GONE);
        }
    }
	
	public void setTitle(CharSequence title) {
		getTitleViewWrapper().setTitle(title);
		addTitleView();
	}
	
	public void setSubTitle(CharSequence subTitle) {
		getTitleViewWrapper().setSubTitle(subTitle);
		addTitleView();
	}
	
	private TitleViewWrapper getTitleViewWrapper() {
		if (mTitleViewWrapper == null) {
			mTitleViewWrapper = new TitleViewWrapper(getContext());
		}
		return mTitleViewWrapper;
	}
	
	private void addTitleView() {
		View titleView = mTitleView;
		if(titleView != null) {
			Object tag = titleView.getTag();
			if(tag != null && !(tag instanceof TitleViewWrapper)) {
				return;
			}
		}
		setTitleView(mTitleViewWrapper.getView());
	}
	
	private class TitleViewWrapper {
		
		private View mView = null;
		
		private TextView mTxvTitle = null;
		
		private TextView mTxvSubTitle = null;

		TitleViewWrapper(Context context) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			mView = inflater.inflate(R.layout.action_bar_title_view, null);
			mView.setTag(this);
			mTxvTitle = (TextView) mView.findViewById(R.id.actionbar_title);
			mTxvSubTitle = (TextView) mView.findViewById(R.id.actionbar_subtitle);
		}
		
		void setTitle(CharSequence title) {
			mTxvTitle.setText(title);
		}
		
		void setSubTitle(CharSequence subTitle) {
			if (mTxvSubTitle.getVisibility() != View.VISIBLE) {
				mTxvSubTitle.setVisibility(View.VISIBLE);
			}
			mTxvSubTitle.setText(subTitle);
		}
		
		View getView() {
			return mView;
		}
	}

	public void setTabSelected(int position) {
		if (mTabBar != null) {
			mTabBar.setTabSelected(position);
		}
	}

    public void inflateOptionsBarByMenu(CMenu menu) {
        if (menu == null && menu.getItemCount() == 0) {
            return;
        }

        if (mOptionsBar != null && (mOptionsBar instanceof CMenuBarView)) {
            ((CMenuBarView) mOptionsBar).inflatedByMenu(menu);
        }
    }

    public void setOnOptionsBarSelectedListener(CMenuBarView.OnMenuItemSelectedListener listener) {
        if (mOptionsBar != null && (mOptionsBar instanceof CMenuBarView)) {
            ((CMenuBarView) mOptionsBar).setOnMenuItemSelectedListener(listener);
        }
    }

    public CTabBarView getTabBar() {
        return mTabBar;
    }

    public void setOnBackButtonClickListener(OnClickListener listener) {
        mBackButtonListener.listener = listener;
    }

    private final class OnBackButtonClickListener implements OnClickListener {

        private OnClickListener listener = null;

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(view);
            }
        }
    }

}
