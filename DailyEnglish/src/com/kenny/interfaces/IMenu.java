package com.kenny.interfaces;

import android.view.Menu;
import android.view.MenuItem;

/**
 * 系统菜单支持
 * @author chenjiangang
 * */
public interface IMenu
{
	/**
	 * 动态构造menu
	 * */
	public abstract boolean onPrepareOptionsMenu(Menu menu);
	/**
	 * menu的选择处理
	 * */
	public abstract boolean onOptionsItemSelected(MenuItem item);
}
