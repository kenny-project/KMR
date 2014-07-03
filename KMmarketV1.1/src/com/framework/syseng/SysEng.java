package com.framework.syseng;

import java.util.Timer;
import java.util.Vector;

import android.os.Handler;
import android.os.Message;

import com.framework.log.P;

/**
 * ϵͳ����
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
	 * �ú���,ֻ����UI���߳��е���
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

	public void setHandler(Handler handler)
	{
		this.handler = handler;
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
	 * �����¼�ִ��״?������? ��ʱִ�е��¼�?����ִ���¼����ڼ�����Χ
	 * 
	 * @param listener
	 *            ����? *
	 */
	public void setListener(EventCallBack listener)
	{
		this.listener = listener;
	}

	/** ����? * @param event ?ִ�е���? * */
	public void addEvent(AbsEvent event)
	{
		synchronized (vec)
		{
			vec.add(event);
			vec.notify();
			// P.v("event", "add event unblock " + event.getClass().getName());
		}
	}

	/**
	 * ������̺߳���
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
				// P.v("event", "add event unblock " +
				// event.getClass().getName());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * ��ʱִ�е���? * @param event ?ִ�е�event
	 * 
	 * @param delaytimes
	 *            ��ʱʱ��
	 * */
	public void addEvent(AbsEvent event, long delaytimes)
	{
		timer.schedule(event, delaytimes);
	}

	/**
	 * ��ʱִ�е���? * @param event ?ִ�е�event
	 * 
	 * @param delaytimes
	 *            ��ʱʱ��
	 * */
	public void addHandlerEvent(final AbsEvent event, long delaytimes)
	{
		if (handler == null)
		{
			handler = new Handler();
		}
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
	 * ��ʱִ�е���? * @param event ?ִ�е�event
	 * 
	 * @param delaytimes
	 *            ��ʱʱ��
	 * */
	public void addHandlerEvent(final AbsEvent event)
	{
		if (handler == null)
		{
			handler = new Handler();
		}
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
	 * ����ִ��
	 * 
	 * @param event
	 * 
	 */
	public synchronized void addSynchEvent(AbsEvent event)
	{
		try
		{
			event.ok();
			// P.v("event", "" + event.getClass().getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * �����̨�߳�
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
					// P.v("event", "[exce start]<" + event.getClass().getName()
					// + ">");
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
					// P.v("event", "[exce end]<" + event.getClass().getName()
					// + " excetime=" + (exceendtime - excestarttime) + ">");
					// P.v("event", "    ");
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
