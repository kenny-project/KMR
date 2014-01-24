package com.kenny.syseng;

import com.kenny.struct.AbsEvent;




/**
 * Event执行状�?回调接口
 * 延时执行的事件�?阻塞执行事件不在监听范围
 * @author aimery
 * */
public interface  EventCallBack
{
	/**
	 * �?��执行
	 * */
	public abstract void exceStart(AbsEvent event);
	/**
	 * 结束执行
	 * @param event
	 * @param times 执行时间
	 * */
	public abstract void exceEnd(AbsEvent event,long times);
}
