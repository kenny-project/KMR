package com.work.market.util;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * ������
 * @author zhou
 *
 */
public class AnimationUtil {
	
	/**
	 * ��߽���
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
	 * �ұ߽���
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
	 * �ײ�����
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
	 * ����Ƴ�
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
	 * �ұ��Ƴ�
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
	 * �ײ��Ƴ�
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
	 * �����Ƴ�
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
	 * ��תҡ�ζ���
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