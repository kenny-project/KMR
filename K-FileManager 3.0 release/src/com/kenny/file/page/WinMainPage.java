package com.kenny.file.page;

import com.framework.page.MultiListPage;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;

/**
 * 主界面切换
 * 
 * @author WangMinghui
 * 
 */
public class WinMainPage extends MultiListPage
{
	protected View tools;
	private LocalPage m_localPage = null;
	private NetworkPage m_RemotePage = null;
	private AppsPage m_AppsPage = null;
	private TaskPage m_TaskPage = null;
	private FavoritePage m_FavoritePage = null;
	
	/**
	 * 本地
	 */
	public static final String Local = "Local";
	public static final String Remote = "Remote";
	public static final String Favorite = "Favorite";
	public static final String apps = "apps";
	public static final String task = "task";

	public WinMainPage(Activity context)
	{
		super(context);
		m_localPage = new LocalPage(context);
	}

	
	public void onCreate()
	{
		m_localPage.setObj(this);
		//m_localPage.onCreate();
		AddAbsPage(Local, m_localPage);
		m_RemotePage = new NetworkPage(m_act);
		m_AppsPage = new AppsPage(m_act);
		m_TaskPage = new TaskPage(m_act);
		m_FavoritePage= new FavoritePage(m_act);
		m_RemotePage.setObj(this);
		m_TaskPage.setObj(this);
		m_AppsPage.setObj(this);
		m_FavoritePage.setObj(this);
		AddAbsPage(Remote, m_RemotePage);
		AddAbsPage(apps, m_AppsPage);
		AddAbsPage(task, m_TaskPage);
		AddAbsPage(Favorite, m_FavoritePage);
		SwitchPage(Local);
	}

	
	public void clear()
	{
		// TODO Auto-generated method stub

	}

	
	public boolean onTouchEvent(MotionEvent event)
	{
		// TODO Auto-generated method stub
		return false;
	}

	
	public void onResume()
	{
		// TODO Auto-generated method stub

	}

	
	public void onPause()
	{
		// TODO Auto-generated method stub

	}

	
	public void onDestroy()
	{
		// TODO Auto-generated method stub

	}
}