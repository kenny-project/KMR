package com.kenny.file.menu;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.kenny.KFileManager.t.R;

public class MGoneAinm extends Animation {
	private View mV;
	private Animation mA;
	private Context main;
	private View MAnimView;

	/**
	 * @author ZhouKang
	 * @param view 要隐藏的view
	 * @param AnimView 执行动画的view
	 * 
	 * */
	public MGoneAinm(Context context, View view,View AnimView) {
		// TODO Auto-generated constructor stub
		
		main = context;
		mV = view;
		MAnimView = AnimView;
		mA = AnimationUtils.loadAnimation(main, R.anim.up_out);
		
	}
	public void ShowAnim(){
		MAnimView.startAnimation(mA);
		mA.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mV.setVisibility(View.GONE);
			}
		});
	}

}
