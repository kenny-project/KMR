package com.framework.syseng;

import java.util.Timer;
import java.util.Vector;
import android.os.Handler;
import android.os.Message;

import com.framework.callback.EventCallBack;
import com.framework.event.AbsEvent;
import com.framework.log.P;

/**
 * 系统引擎
 * 
 * @author aimery
 * */
public class SysEng extends Thread
{
   private boolean isrun = true;
   private Vector<AbsEvent> vec = null;
   private Thread thread;
   private Timer timer = null;
   private static SysEng syseng;
   private EventCallBack listener;
   private long excestarttime, exceendtime;
   private Handler handler;
   
   protected SysEng()
   {
      vec = new Vector<AbsEvent>();
      timer = new Timer();
   }
   
   public synchronized static SysEng getInstance()
   {
      if (syseng == null)
      {
         syseng = new SysEng();
         syseng.start();
      }
      return syseng;
   }
   
   /**
    * 该函数,只能在UI主线程中调用
    */
   public void start()
   {
      if (thread == null)
      {
         thread = new Thread(this);
         thread.start();
      }
      if (handler == null)
      {
         handler = new Handler();
      }
   }
   
   public void cancel()
   {
      try
      {
         this.isrun = false;
         syseng = null;
         synchronized (vec)
         {
	  vec.removeAllElements();
         }
      }
      catch (Exception e)
      {
         P.v("event", "cancel ex-" + e.toString());
      }
   }
   
   /**
    * 设置事件执行状�?监听器�? 延时执行的事件�?阻塞执行事件不在监听范围
    * 
    * @param listener
    *           监听�? *
    */
   public void setListener(EventCallBack listener)
   {
      this.listener = listener;
   }
   
   /** 非阻�? * @param event �?��执行的事�? * */
   public void addEvent(AbsEvent event)
   {
      synchronized (vec)
      {
         vec.add(event);
         vec.notify();
         P.v("event", "add event unblock " + event.getClass().getName());
      }
   }
   
   /**
    * 加入多线程函数
    * 
    * @param event
    */
   public void addThreadEvent(AbsEvent event)
   {
      synchronized (vec)
      {
         try
         {
	  Thread thread = new Thread(event);
	  thread.setPriority(MIN_PRIORITY);
	  thread.start();
	  // vec.add(event);
	  // vec.notify();
	  P.v("event", "add event unblock " + event.getClass().getName());
         }
         catch (Exception e)
         {
	  e.printStackTrace();
         }
      }
   }
   
   /**
    * 延时执行的事�? * @param event �?��执行的event
    * 
    * @param delaytimes
    *           延时时间
    * */
   public void addEvent(AbsEvent event, long delaytimes)
   {
      timer.schedule(event, delaytimes);
   }
   
   /**
    * 延时执行的事�? * @param event �?��执行的event
    * 
    * @param delaytimes
    *           延时时间
    * */
   public void addHandlerEvent(final AbsEvent event, long delaytimes)
   {
      handler.postDelayed(new Runnable()
      {
         
         public void run()
         {
	  try
	  {
	     event.ok();
	  }
	  catch (Exception e)
	  {
	     e.printStackTrace();
	  }
         }
      }, delaytimes);
   }
   public final boolean sendMessage(Message msg)
   {
      return handler.sendMessage(msg);
   }
   /**
    * 延时执行的事�? * @param event �?��执行的event
    * 
    * @param delaytimes
    *           延时时间
    * */
   public void addHandlerEvent(final AbsEvent event)
   {
      handler.post(new Runnable()
      {
         
         public void run()
         {
	  try
	  {
	     event.ok();
	  }
	  catch (Exception e)
	  {
	     e.printStackTrace();
	  }
         }
      });
   }
   
   /**
    * 阻塞执行
    * 
    * @param event
    * 
    */
   public synchronized void addSynchEvent(AbsEvent event)
   {
      try
      {
         event.ok();
         P.v("event", "" + event.getClass().getName());
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   /**
    * 挂起后台线程
    */
   public void ThreadWait()
   {
      try
      {
         synchronized (vec)
         {
	  vec.wait();
         }
      }
      catch (InterruptedException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   
   public void ThreadNotify()
   {
      synchronized (vec)
      {
         vec.notify();
      }
   }
   
   public void run()
   {
      while (isrun)
      {
         try
         {
	  synchronized (vec)
	  {
	     if (vec.size() <= 0)
	     {
	        vec.wait();
	        continue;
	     }
	  }
	  if (vec.size() > 0)
	  {
	     excestarttime = System.currentTimeMillis();
	     AbsEvent event = vec.remove(0);
	     P.v("event", "[exce start]<" + event.getClass().getName() + ">");
	     if (listener != null)
	     {
	        listener.exceStart(event);
	     }
	     try
	     {
	        event.ok();
	     }
	     catch (Exception e)
	     {
	        e.printStackTrace();
	     }
	     exceendtime = System.currentTimeMillis();
	     if (listener != null)
	     {
	        listener.exceEnd(event, (exceendtime - excestarttime));
	     }
	     P.v("event", "[exce end]<" + event.getClass().getName()
		 + " excetime=" + (exceendtime - excestarttime) + ">");
	     P.v("event", "    ");
	  }
         }
         catch (Exception e)
         {
	  e.printStackTrace();
	  P.v("event", "run " + e.toString());
         }
      }
   }
}
