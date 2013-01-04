package com.framework.main;

import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ViewFlipper;

import com.framework.interfaces.IMultiPage;
import com.framework.interfaces.IPageManage;
import com.framework.interfaces.MenuAble;
import com.framework.interfaces.OnConfigurationChangedAble;
import com.framework.interfaces.OnDestroyAble;
import com.framework.interfaces.OnPauseAble;
import com.framework.interfaces.OnRestartAble;
import com.framework.interfaces.OnRestoreInstanceStateAble;
import com.framework.interfaces.OnResumeAble;
import com.framework.interfaces.OnSaveInstanceStateAble;
import com.framework.interfaces.OnStartAble;
import com.framework.interfaces.OnStopAble;
import com.framework.page.AbsPage;

public abstract class PageManage implements IPageManage
{
	private Vector<AbsPage> pagevec = new Vector<AbsPage>();// 页面容器
	private AbsPage currentpage = null;// 当前页面

	/**
	 * 返回指定的实例
	 * 
	 * @param key
	 * @return
	 */
	public AbsPage FindAbsPage(Class<?> cls)
	{
		for (int i = 0; i < pagevec.size(); i++)
		{
			AbsPage temp = pagevec.get(i);
			Class<? extends Object> cls1 = temp.getClass();
			if (cls == cls1)
			{
				return temp;
			}
		}
		return null;
	}

	/**
	 * 页面跳转
	 * 
	 * @param pageid
	 *            页面id
	 * @param animation
	 *            0-不使用动画，1-使用动画
	 * @param obj
	 *            页面数据交换 添加页面到容器
	 * 
	 * @param view
	 *            需要添加的页面
	 * @param anim
	 *            0-不要切换动画 1-需要切换动画
	 * */
	
	public void NextView(AbsPage obj, int anim)
	{
		if (obj != null)
		{
			if (obj.isIssave())
			{
				// 判断是否重复加载
				int flag = 0;
				for (int i = 0; i < pagevec.size(); i++)
				{
					if (obj == pagevec.get(i))
					{
						flag = 1;
					}
				}
				if (flag == 0)
					pagevec.addElement(obj);
			}
			onPause();
			currentpage = obj;
			currentpage.onCreate();
			NextPage(currentpage, anim);
			onResume();
//			for (int i = 0; i < pagevec.size(); i++)
//			{
//				AbsPage vi = pagevec.get(i);
//				P.v("total " + pagevec.size() + " page." + i + "->"
//						+ vi.getClass().getName());
//			}
		}
	}
	/**
	 * 初始化界面动功
	 */
	public abstract void Init(Activity m_Activity, ViewFlipper flipper);

	/**
	 * 切换到下一界面
	 */
	protected abstract void NextPage(View obj, int anim);
	/**
	 * 切换到上一界面
	 * @param obj
	 * @param anim
	 */
	protected abstract void BackPage(View obj, int anim);

	/**
	 * 返回上一层页面
	 * */
	
	public boolean backView(int anim)
	{
		if (pagevec.size() > 1)
		{
			((AbsPage) pagevec.elementAt(pagevec.size() - 1)).clear();
			pagevec.removeElementAt(pagevec.size() - 1);
			
			onPause();
			onDestroy();
			currentpage = (AbsPage) pagevec.elementAt(pagevec.size() - 1);
			onResume();
			BackPage(currentpage, anim);
			return true;
		}
//		else
//		{
//			P.v("back,total." + pagevec.size() + "not back");
//		}
//		for (int i = 0; i < pagevec.size(); i++)
//		{
//			AbsPage vi = (AbsPage) pagevec.elementAt(i);
//			P.v("back,total." + pagevec.size() + " pages." + i + "->"
//					+ vi.getClass().getName());
//		}
		return false;
	}

	public synchronized void postInvalidate()
	{
		AbsPage temp = currentpage;
		if (temp instanceof IMultiPage)
		{
			temp = ((IMultiPage) temp).CurrentPage();
		}
		if (temp != null)
		{
			currentpage.postInvalidate();//
		}
	}

	/**
	 * *************************************************************************
	 * ******
	 */
	/**
	 * 按键
	 * 
	 * @param keyCode
	 * @param event
	 * @return
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		AbsPage temp = currentpage;
		if (temp instanceof IMultiPage)
		{
			temp = ((IMultiPage) temp).CurrentPage();
		}
		if (temp != null && temp.onKeyDown(keyCode, event))
		{
			return true;
		}
		return false;
	}
	public boolean onCreateOptionsMenu(Menu menu)
	{
		AbsPage temp = currentpage;
		if (temp instanceof IMultiPage)
		{
			temp = ((IMultiPage) temp).CurrentPage();
		}
		if (temp != null && temp instanceof MenuAble)
		{
			return ((MenuAble) temp).onCreateOptionsMenu(menu);
		} else
		{
			return false;
		}	   
	}
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		AbsPage temp = currentpage;
		if (temp instanceof IMultiPage)
		{
			temp = ((IMultiPage) temp).CurrentPage();
		}
		if (temp != null && temp instanceof MenuAble)
		{
			return ((MenuAble) temp).onPrepareOptionsMenu(menu);
		} else
		{
			return false;
		}
	}

	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		AbsPage temp = currentpage;
		if (temp instanceof IMultiPage)
		{
			temp = ((IMultiPage) temp).CurrentPage();
		}
		if (temp != null && temp instanceof MenuAble)
		{
			return ((MenuAble) temp).onOptionsItemSelected(item);
		}
		return false;
	}

	
	public void onRestart()
	{
		AbsPage temp = currentpage;
		if (temp instanceof IMultiPage)
		{
			temp = ((IMultiPage) temp).CurrentPage();
		}
		if (temp != null && temp instanceof OnRestartAble)
		{
			((OnRestartAble) temp).onRestart();
		}
	}

	
	public void onStart()
	{
		AbsPage temp = currentpage;
		if (temp instanceof IMultiPage)
		{
			temp = ((IMultiPage) temp).CurrentPage();
		}
		if (temp != null && temp instanceof OnStartAble)
		{
			((OnStartAble) temp).onStart();
		}
	}

	
	public void onResume()
	{
		AbsPage temp = currentpage;
		if (temp instanceof IMultiPage)
		{
			temp = ((IMultiPage) temp).CurrentPage();
		}
		if (temp != null && temp instanceof OnResumeAble)
		{
			((OnResumeAble) temp).onResume();
		}
	}

	
	public void onPause()
	{
		AbsPage temp = currentpage;
		if (temp instanceof IMultiPage)
		{
			temp = ((IMultiPage) temp).CurrentPage();
		}
		if (temp != null && temp instanceof OnPauseAble)
		{
			((OnPauseAble) temp).onPause();
		}
	}

	
	public void onStop()
	{
		AbsPage temp = currentpage;
		if (temp instanceof IMultiPage)
		{
			temp = ((IMultiPage) temp).CurrentPage();
		}
		if (temp != null && temp instanceof OnStopAble)
		{
			((OnStopAble) temp).onStop();
		}
	}

	
	public void onDestroy()
	{
		AbsPage temp = currentpage;
		if (temp instanceof IMultiPage)
		{
			temp = ((IMultiPage) temp).CurrentPage();
		}
		if (temp != null && temp instanceof OnDestroyAble)
		{
			((OnDestroyAble) temp).onDestroy();
		}
	}

	
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		AbsPage temp = currentpage;
		if (temp instanceof IMultiPage)
		{
			temp = ((IMultiPage) temp).CurrentPage();
		}
		if (temp != null && temp instanceof OnRestoreInstanceStateAble)
		{
			((OnRestoreInstanceStateAble) temp)
					.onRestoreInstanceState(savedInstanceState);
		}
	}

	
	public void onSaveInstanceState(Bundle outState)
	{
		AbsPage temp = currentpage;
		if (temp instanceof IMultiPage)
		{
			temp = ((IMultiPage) temp).CurrentPage();
		}
		if (temp != null && temp instanceof OnSaveInstanceStateAble)
		{
			((OnSaveInstanceStateAble) temp).onSaveInstanceState(outState);
		}
	}

	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		AbsPage temp = currentpage;
		if (temp instanceof IMultiPage)
		{
			temp = ((IMultiPage) temp).CurrentPage();
		}
		temp.OnActivityResult(requestCode, resultCode, data);
	}

	public void onConfigurationChanged(Configuration newConfig)
	{
		AbsPage temp = currentpage;
		if (temp instanceof IMultiPage)
		{
			temp = ((IMultiPage) temp).CurrentPage();
		}
		if (temp != null && temp instanceof OnConfigurationChangedAble)
		{
			((OnConfigurationChangedAble) temp)
					.onConfigurationChanged(newConfig);
		}
	}
}
