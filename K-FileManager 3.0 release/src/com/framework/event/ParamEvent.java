package com.framework.event;

import java.util.Map;


/**
 * 事件基类
 * 
 * @author aimery
 * */
public abstract class ParamEvent extends AbsEvent
{
   protected int Key;
   protected Object Value;
   protected Map map;
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
