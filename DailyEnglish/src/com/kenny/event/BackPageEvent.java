package com.kenny.event;

import android.content.Context;

import com.kenny.struct.AbsEvent;

/**
 * 返回到上一个page的event
 * 
 * @author chenjiangang
 * */
public class BackPageEvent extends AbsEvent{

	private int num = 1;
	
	public BackPageEvent(Context main) {
		// TODO Auto-generated constructor stub
		super(main);
		this.context = main;
		num = 1;
		
	}


	@Override
	public void ok() {
		// TODO Auto-generated method stub
//		Main.handler.post(new Runnable(){
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				while (num > 0){
//					main.common.getPageManage().backPage();
//					num --;
//				}
//			}
//		});
	}

}
