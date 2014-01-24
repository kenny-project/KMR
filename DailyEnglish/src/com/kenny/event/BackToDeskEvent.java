package com.kenny.event;


import android.content.Intent;

import com.kenny.activity.MainOld;
import com.kenny.struct.AbsEvent;

/**
 * 返回到桌面，相当于home
 * 
 * @author chenjiangang
 * */
public class BackToDeskEvent extends AbsEvent
{
	public BackToDeskEvent(MainOld act)
	{
		super(act);
	}

	@Override
	public void ok()
	{
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		main.startActivity(intent);
	}

}
