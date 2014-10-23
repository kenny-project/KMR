package com.framework.event;

import java.util.Hashtable;
import java.util.Map;

import com.kenny.file.bean.NotifParam;

/**
 * 事件基类
 * 
 * @author wangmh
 * */
public abstract class ParamEvent extends AbsEvent
{
//	protected int Key;
//	protected Object Value;
//	protected Map<String, Object> HashMap = new Hashtable<String, Object>();
	public int Key;
	public Object Value;
	public Map<String, Object> HashMap = new Hashtable<String, Object>();
	
	public void Push(String key, Object value)
	{
		if (HashMap.containsKey(key))
		{
			HashMap.remove(key);
		}
		HashMap.put(key, value);
	}

	public Object Pop(String key)
	{
		if (HashMap.containsKey(key))
		{
			return HashMap.get(key);
		} else
		{
			return null;
		}
	}

	public int getKey()
	{
		return Key;
	}

	public void setKey(int key)
	{
		Key = key;
	}

	public Object getValue()
	{
		return Value;
	}

	public void setValue(Object value)
	{
		Value = value;
	}
}
