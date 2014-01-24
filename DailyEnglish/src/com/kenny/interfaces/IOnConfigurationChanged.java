package com.kenny.interfaces;

import android.content.res.Configuration;

/**
 * 配置改变，如屏幕切换
 * @author chenjiangang
 * */
public interface IOnConfigurationChanged
{
	/**
	 * 配置发生改变，如屏幕切换
	 * */
  public void onConfigurationChanged(Configuration newConfig);
}
