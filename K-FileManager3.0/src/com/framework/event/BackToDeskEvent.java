package com.framework.event;


import android.app.Activity;
import android.content.Intent;

/**
 * 返回到桌面，相当于home
 * 
 * @author aimery
 * */
public class BackToDeskEvent extends AbsEvent
{
	private Activity act;

	public BackToDeskEvent(Activity act)
	{
		this.act = act;
	}

	@Override
	public void ok()
	{
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		act.startActivity(intent);
	}

}
