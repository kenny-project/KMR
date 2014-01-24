package com.kenny.syseng;

import java.util.Timer;
import java.util.Vector;

import com.kenny.struct.AbsEvent;
import com.kenny.util.Log;

/**
 * 系统引擎
 * 
 * @author aimery
 * */
public class KSysEng extends Thread
{
	private boolean isrun = true;
	private Vector<AbsEvent> vec = null;
	private Vector<AbsEvent> dlvec = null;

	private Thread thread;
	private Timer timer = null;
	private static KSysEng syseng;
	private EventCallBack listener;
	private long excestarttime, exceendtime;

	protected KSysEng()
	{
		vec = new Vector<AbsEvent>();
		dlvec = new Vector<AbsEvent>();
		timer = new Timer();
	}

	public synchronized static KSysEng getInstance()
	{
		if (syseng == null)
		{
			syseng = new KSysEng();
			syseng.start();
		}
		return syseng;
	}
	DLThread mDLThread = new DLThread();
	/**
	 * 该函数,只能在UI主线程中调用
	 */
	@Override
	public void start()
	{
		if (thread == null)
		{
			thread = new Thread(this);
			thread.start();
			
			mDLThread.start();
			
//			mDLThread = new DLThread();
//			mDLThread.start();
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
		} catch (Exception e)
		{
			Log.v("event", "cancel ex-" + e.toString());
		}
	}

	/**
	 * 设置事件执行状�?监听器�? 延时执行的事件�?阻塞执行事件不在监听范围
	 * 
	 * @param listener
	 *            监听�? *
	 */
	public void setListener(EventCallBack listener)
	{
		this.listener = listener;
	}

	public void addDLEvent(AbsEvent event)
	{
		synchronized (dlvec)
		{
			dlvec.remove(event);
			dlvec.add(event);
			dlvec.notify();
			if(mDLThread==null||!mDLThread.isAlive())
			{
				mDLThread = new DLThread();
				mDLThread.start();
				Log.v("event", "start mDLThread addDLEvent");
			}
			Log.v("event", "add addDLEvent unblock "
					+ event.getClass().getName());
		}
	}

	public boolean IsDLEvent(AbsEvent event)
	{
		synchronized (dlvec)
		{
			boolean result = dlvec.contains(event);
			Log.v("event", "IsDLEvent unblock " + event.getClass().getName());
			return result;
		}
	}

	public class DLThread extends Thread
	{
		@Override
		public void run()
		{
			AbsEvent event = null;
			try
			{
				while (isrun)
				{
					synchronized (dlvec)
					{
						if (dlvec.size() <= 0)
						{
							dlvec.wait();
							continue;
						} else
						{
							event = dlvec.remove(0);
						}
					}
					if (event != null)
					{
						excestarttime = System.currentTimeMillis();
						Log.v("event", "[exce start]<"
								+ event.getClass().getName() + ">");
						if (listener != null)
						{
							listener.exceStart(event);
						}
						event.ok();
						exceendtime = System.currentTimeMillis();
						if (listener != null)
						{
							listener.exceEnd(event,
									(exceendtime - excestarttime));
						}
						Log.v("event", "[exce end]<"
								+ event.getClass().getName() + " excetime="
								+ (exceendtime - excestarttime) + ">");
						Log.v("event", "    ");
					}
				}
			} catch (Exception e)
			{
				e.printStackTrace();
				Log.v("event", "run " + e.toString());
			}
		}
	}

	public void DelDLEvent(AbsEvent event)
	{
		synchronized (dlvec)
		{
			dlvec.remove(event);
			dlvec.notify();
			Log.v("event", "del addDLEvent unblock "
					+ event.getClass().getName());
		}
	}

	/** 非阻�? * @param event �?��执行的事�? * */
	public void addEvent(AbsEvent event)
	{
		synchronized (vec)
		{
			vec.add(event);
			vec.notify();
			Log.v("event", "add event unblock " + event.getClass().getName());
		}
	}

	/**
	 * 延时执行的事�? * @param event �?��执行的event
	 * 
	 * @param delaytimes
	 *            延时时间
	 * */
	public void addEvent(AbsEvent event, long delaytimes)
	{
		timer.schedule(event, delaytimes);
	}

	/**
	 * 阻塞执行
	 * 
	 * @param event
	 * 
	 */
	public synchronized void addSynchEvent(AbsEvent event)
	{
		event.ok();
		Log.v("event", "" + event.getClass().getName());
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
		} catch (InterruptedException e)
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

	@Override
	public void run()
	{
		try
		{
			while (isrun)
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
					Log.v("event", "[exce start]<" + event.getClass().getName()
							+ ">");
					if (listener != null)
					{
						listener.exceStart(event);
					}
					event.ok();
					exceendtime = System.currentTimeMillis();
					if (listener != null)
					{
						listener.exceEnd(event, (exceendtime - excestarttime));
					}
					Log.v("event", "[exce end]<" + event.getClass().getName()
							+ " excetime=" + (exceendtime - excestarttime)
							+ ">");
					Log.v("event", "    ");
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			Log.v("event", "run " + e.toString());
		}
	}
}
