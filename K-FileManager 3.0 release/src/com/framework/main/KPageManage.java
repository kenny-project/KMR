package com.framework.main;

import com.framework.util.Const;
import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;
import android.widget.LinearLayout.LayoutParams;

/**
 * 界面切换
 * 
 * @author WangMinghui
 * 
 */
public class KPageManage extends PageManage
{
	private ViewFlipper flipper;// 页面切换效果
	protected Activity m_Activity;

	public void Init(Activity m_Activity, ViewFlipper flipper)
	{
		this.flipper = flipper;
		this.m_Activity = m_Activity;
	}

	/**
	 * 切换到下一界面
	 */
	protected void NextPage(View obj, int anim)
	{
		flipper.clearAnimation();
		flipper.addView(obj, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		if (anim == Const.SHOWANIM)
		{
			//TranslateAnimation(int fromXType, float fromXValue, int toXType, float toXValue, int fromYType, float fromYValue, int toYType, float toYValue)
			//Animation left=new TranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta)
			//第一个参数fromXDelta为动画起始时 X坐标上的移动位置   
			//第二个参数toXDelta为动画结束时 X坐标上的移动位置      
			//第三个参数fromYDelta为动画起始时Y坐标上的移动位置     
			//第四个参数toYDelta为动画结束时Y坐标上的移动位置 
			Animation left_in=new TranslateAnimation(400.0f, 0.0f,0.0f,0.0f);
			left_in.setDuration(200);
			this.flipper.startAnimation(left_in);
//			this.flipper.setInAnimation(AnimationUtils.loadAnimation(
//					m_Activity, R.anim.left_in));
//			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(
//					m_Activity, R.anim.left_out));
		}
		this.flipper.showNext();
	}

	protected void BackPage(View obj, int anim)
	{
		this.flipper.clearAnimation();
		if (anim == Const.SHOWANIM)
		{
			Animation left_in=new TranslateAnimation( 0.0f,400.0f,0.0f,0.0f);
			left_in.setDuration(200);
			this.flipper.startAnimation(left_in);
//			this.flipper.setInAnimation(AnimationUtils.loadAnimation(
//					m_Activity, R.anim.right_in));
//			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(
//					m_Activity, R.anim.right_out));
		}
		this.flipper.showPrevious();
		flipper.removeViewAt(flipper.getChildCount() - 1);
	}
}
