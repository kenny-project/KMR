package com.kenny.event;

import com.kenny.activity.MainOld;
import com.kenny.struct.AbsEvent;


/**
 * 空操作事件
 * @author chenjiangang
 * 
 * */
public class DoNothingEvent extends AbsEvent
{

	public DoNothingEvent(MainOld main)
	{
		super(main);
	}
	
	public DoNothingEvent()
	{
		super(null);
	}

	@Override
	public void ok()
	{
		// TODO Auto-generated method stub
		
	}
  
}
