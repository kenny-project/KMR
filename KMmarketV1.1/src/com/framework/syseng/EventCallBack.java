package com.framework.syseng;




public interface  EventCallBack
{
	public abstract void exceStart(AbsEvent event);
	public abstract void exceEnd(AbsEvent event,long times);
}
