package com.framework.event;

import java.util.TimerTask;

import com.framework.log.P;

/**
 * 事件基类
 * 
 * @author aimery
 * */
public abstract class AbsEvent extends TimerTask
{
   public abstract void ok();
   protected String tag="Event";
   @Override
   public final void run()
   {
      // TODO Auto-generated method stub
      try
      {
         P.v("event","start Event=" + this.getClass().getName());
         ok();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      P.v("event","end Event=" + this.getClass().getName());
   }
}
