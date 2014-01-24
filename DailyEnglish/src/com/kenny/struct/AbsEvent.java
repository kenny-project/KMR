package com.kenny.struct;

import java.util.TimerTask;

import android.content.Context;

import com.kenny.activity.MainOld;

public abstract class AbsEvent extends TimerTask{
	
	public MainOld main;
	public Context context;
	
	public AbsEvent(MainOld main){
		this.main = main;
		if (main != null){
			context = main;
		}
	}
	
	public AbsEvent(Context context){
		this.context = context;
	}

	public abstract void ok();
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ok();
	}
}
