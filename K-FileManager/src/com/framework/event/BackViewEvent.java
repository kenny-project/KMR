package com.framework.event;

import com.framework.util.CommLayer;
/**
 * backview event
 * 
 * @author aimery
 * */
public class BackViewEvent extends AbsEvent
{
	int anim=0;
	public BackViewEvent(int anim)
	{
		this.anim = anim;
	}
	public BackViewEvent()
	{
		
	}
	@Override
	public void ok()
	{
		CommLayer.getPMG().backView(anim);
	}
}
