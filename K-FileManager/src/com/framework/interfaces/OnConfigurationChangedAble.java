package com.framework.interfaces;

import android.content.res.Configuration;

/**
 * @author aimery
 * 配置改变，如屏幕切换
 * */
public interface OnConfigurationChangedAble
{
	/**
	 * 配置发生改变，如屏幕切换
	 * */
  public void onConfigurationChanged(Configuration newConfig);
}
