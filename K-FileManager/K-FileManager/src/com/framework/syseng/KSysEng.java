package com.framework.syseng;

import java.util.Vector;

import android.util.Log;

import com.framework.callback.EventCallBack;
import com.framework.event.AbsEvent;
import com.framework.log.P;

/**
 * 文件下载引擎
 * 
 * @author aimery
 * */
public class KSysEng extends Thread
{
	private boolean isrun = true;

	private Vector<Runnable> dlvec = null;
	private Thread thread;
	private static KSysEng syseng;
	private long excestarttime, exceendtime;

	protected KSysEng()
	{
		dlvec = new Vector<Runnable>();
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

	/**
	 * 该函数,只能在UI主线程中调用
	 */
	public void start()
	{
		if (thread == null)
		{
			thread = new Thread(this);
		}
		thread.start();
	}

	public void cancel()
	{
		try
		{
			this.isrun = false;
			syseng = null;
			synchronized (dlvec)
			{
				dlvec.removeAllElements();
			}
		} catch (Exception e)
		{
			Log.v("event", "cancel ex-" + e.toString());
		}
	}

	public void addDLEvent(Runnable event)
	{
		synchronized (dlvec)
		{
			dlvec.remove(event);
			dlvec.add(event);
			dlvec.notify();
			Log.v("event", "add addDLEvent unblock "
					+ event.getClass().getName());
		}
	}

	public boolean IsDLEvent(Runnable event)
	{
		synchronized (dlvec)
		{
			boolean result = dlvec.contains(event);
			Log.v("event", "IsDLEvent unblock " + event.getClass().getName());
			return result;
		}
	}

	public void run()
	{
		Runnable event = null;

		while (isrun)
		{
			try
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
					P.v("event", "[exce start]<" + event.getClass().getName()
							+ ">");
					event.run();
					P.v("event", "[exce end]<" + event.getClass().getName()
							+ " excetime=" + (exceendtime - excestarttime)
							+ ">");
				}
			} catch (Exception e)
			{
				e.printStackTrace();
				Log.v("event", "run " + e.toString());
			}
		}

		isrun = false;
	}

	public void DelDLEvent(AbsEvent event)
	{
		synchronized (dlvec)
		{
			dlvec.remove(event);
			dlvec.notify();
			P.v("event", "del addDLEvent unblock " + event.getClass().getName());
		}
	}

}
