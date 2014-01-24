package com.framework.page;

import java.util.Hashtable;

import com.framework.interfaces.IMultiPage;
import com.framework.log.P;
import android.app.Activity;

/**
 * 多窗体界面的List列表
 * 
 * @author WangMinghui
 * 
 */
public abstract class MultiListPage extends AbsPage implements IMultiPage
{
	private MultiItemPage m_curPage = null;
	protected Hashtable<String, MultiItemPage> pages = new Hashtable<String, MultiItemPage>();

	public MultiListPage(Activity context)
	{
		super(context);
	}

	/**
	 * 获得当前实例的名柄
	 */
	public MultiItemPage CurrentPage()
	{
		return m_curPage;
	}

	public int AddAbsPage(String key, MultiItemPage value)
	{
		pages.put(key, value);
		return 1;
	}

	public int RemoveAbsPage(String key)
	{
		pages.remove(key);
		return 1;
	}

	public int SwitchPage(String key)
	{
		if (m_curPage != null)
		{
			this.removeView(m_curPage);
			m_curPage.onExit();
			//m_curPage.onPause();
		}
		// this.removeAllViews();
		MultiItemPage temp = pages.get(key);
		if (temp != null)
		{
			if(!temp.isCreate())
			{
				temp.onCreate();
				temp.onLoad();
			}
			this.addView(temp);
			m_curPage = temp;
			temp.onReload();
//			temp.getContext().get
//			temp.onCreateOptionsMenu(menu);
			this.postInvalidate();
			return 1;
		} else
		{
			P.v(key + ":未找到相应的窗体");
		}
		return 0;
	}
}