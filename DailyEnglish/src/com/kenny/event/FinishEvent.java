package com.kenny.event;

import android.content.Context;

import com.kenny.activity.MainOld;
import com.kenny.struct.AbsEvent;

public class FinishEvent extends AbsEvent{

	public FinishEvent(MainOld main) {
		super(main);
		// TODO Auto-generated constructor stub
	}
	
	public FinishEvent(Context main){
		super(main);
	}

	@Override
	public void ok() {
		// TODO Auto-generated method stub
//		((KApp)(context.getApplicationContext())).PauseAllDict();
	}
}
