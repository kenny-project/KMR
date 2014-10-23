package com.framework.interfaces;

/**
 * @author aimery
 * 监听软键盘是否弹出或隐藏
 * */
public interface InputpadShowOrHideListener
{
	/**
	 * 软键盘显示
	 * */
 	public void show();
 	/**
 	 * 软键盘隐藏
 	 * */
	public void hide();
	/**
	 * 屏幕尺寸改变
	 * */
	public void onSizeChanged(int w, int h, int oldw, int oldh);
}
