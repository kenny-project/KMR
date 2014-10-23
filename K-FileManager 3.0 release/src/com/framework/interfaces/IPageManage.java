package com.framework.interfaces;

import com.framework.page.AbsPage;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ViewFlipper;

public interface IPageManage
{

	public abstract void Init(Activity m_Activity,ViewFlipper flipper);
	/**
	 * 添加页面到容器
	 * 
	 * @param view
	 *            需要添加的页面
	 * @param anim
	 *            0-不要切换动画 1-需要切换动画
	 * */
	public void NextView(AbsPage obj,int anim);

	/**
	 * 返回上一层页面
	 * */
	public boolean backView(int anim);

	/**
	 * 刷新当前界面
	 */
	public void postInvalidate();

	/**
	 * *************************************************************************
	 * ******
	 */
	/**
	 * 按键
	 * 
	 * @param keyCode
	 * @param event
	 * @return
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event);
	
	public boolean onCreateOptionsMenu(Menu menu);
	
	public boolean onPrepareOptionsMenu(Menu menu);

	public boolean onOptionsItemSelected(MenuItem item);

	public void onStart();

	public void onRestart();

	public void onResume();

	public void onPause();

	public void onStop();

	public void onDestroy();

	public void onRestoreInstanceState(Bundle savedInstanceState);

	public void onSaveInstanceState(Bundle outState);

	public void onActivityResult(int requestCode, int resultCode, Intent data);

	public void onConfigurationChanged(Configuration newConfig);
}
