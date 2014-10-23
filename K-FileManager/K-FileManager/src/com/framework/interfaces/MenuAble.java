package com.framework.interfaces;

import android.view.Menu;
import android.view.MenuItem;

/**
 * 系统菜单支持
 * @author aimery
 * */
public interface MenuAble
{
//	/**
//	 * 非动态构建menu
//	 * */
	public abstract boolean onCreateOptionsMenu(Menu menu);
	/**
	 * 动态构造menu
	 * */
	public abstract boolean onPrepareOptionsMenu(Menu menu);
	/**
	 * menu的选择处理
	 * */
	public abstract boolean onOptionsItemSelected(MenuItem item);
}
