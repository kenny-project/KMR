package com.work.market.util;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * 动画类
 * @author zhou
 *
 */
public class AnimationUtil {
	
	/**
	 * 左边进入
	 * @return
	 */
	public static Animation inFromLeftAnimation() {
		TranslateAnimation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, -2.0F, Animation.RELATIVE_TO_PARENT, 0.0F,
				Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F);
		animation.setDuration(300L);
		animation.setInterpolator(new AccelerateInterpolator());
		animation.setFillAfter(true);
		return animation;
	}

	/**
	 * 右边进入
	 * @return
	 */
	public static Animation inFromRightAnimation() {
		TranslateAnimation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 2.0F, Animation.RELATIVE_TO_PARENT, 0.0F,
				Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F);
		animation.setDuration(300L);
		animation.setInterpolator(new AccelerateInterpolator());
		animation.setFillAfter(true);
		return animation;
	}
	public static Animation inFromRightAnimation1() {
		TranslateAnimation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 2.0F, Animation.RELATIVE_TO_PARENT, 0.0F,
				Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F);
		animation.setDuration(900L);
		animation.setInterpolator(new AccelerateInterpolator());
		animation.setFillAfter(true);
		return animation;
	}
	
	/**
	 * 底部进入
	 * @return
	 */
	public static Animation inFromBottomAnimation() {
		TranslateAnimation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F,
				Animation.RELATIVE_TO_PARENT, 2.0F, Animation.RELATIVE_TO_PARENT, 0.0F);
		animation.setDuration(200L);
		animation.setInterpolator(new AccelerateInterpolator());
		animation.setFillAfter(true);
		return animation;
	}

	/**
	 * 左边移出
	 * @return
	 */
	public static Animation outToLeftAnimation() {
		TranslateAnimation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, -2.0F, 
				Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F);
		animation.setDuration(300L);
		animation.setInterpolator(new AccelerateInterpolator());
		animation.setFillAfter(true);
		return animation;
	}
	public static Animation outToLeftAnimation1() {
		TranslateAnimation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, -2.0F, 
				Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F);
		animation.setDuration(1L);
		animation.setInterpolator(new AccelerateInterpolator());
		animation.setFillAfter(true);
		return animation;
	}

	/**
	 * 右边移出
	 * @return
	 */
	public static Animation outToRightAnimation() {
		TranslateAnimation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 2.0F, 
				Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F);
		animation.setDuration(300L);
		animation.setInterpolator(new AccelerateInterpolator());
		animation.setFillAfter(true);
		return animation;
	}
	
	/**
	 * 底部移出
	 * @return
	 */
	public static Animation outToBottomAnimation() {
		TranslateAnimation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F, 
				Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 2.0F);
		animation.setDuration(200L);
		animation.setInterpolator(new AccelerateInterpolator());
		animation.setFillAfter(true);
		return animation;
	}
	
	/**
	 * 顶部移出
	 * @return
	 */
	public static Animation outToTopAnimation() {
		TranslateAnimation animation = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, 0.0F, 
				Animation.RELATIVE_TO_PARENT, 0.0F, Animation.RELATIVE_TO_PARENT, -2.0F);
		animation.setDuration(200L);
		animation.setInterpolator(new AccelerateInterpolator());
		animation.setFillAfter(true);
		return animation;
	}

	/**
	 * 旋转摇晃动画
	 * @return
	 */
	public static Animation rotateAnimation() {
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(new ScaleAnimation(1.0F, 0.2F, 1.0F, 1.0F, 
				Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F));
		animationSet.addAnimation(new ScaleAnimation(0.2F, 1.0F, 1.0F, 1.0F, 
				Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F));
		animationSet.setDuration(150L);
		return animationSet;
	}
}