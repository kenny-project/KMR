package com.kenny.Slidingmenu.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.kenny.Slidingmenu.MainUIActivity;
import com.slidingmenu.lib.SlidingMenu;

/**
 * 界面基类
 * 
 * @author kenny
 * */
public abstract class AbsFragmentPage extends Fragment
{
	// **
	// * 词友、群、会话、设置为一级界面，其余都为二级界面
	// * 一级界面再内存中只会存在一个,并且一级界面issave只能为true；
	// * */
	public static final int FIRST_PAGE = 0;// 一级界面，
	public static final int SECOND_PAGE = 1;// 二级级界面
	protected Activity m_act;
	protected boolean bViewCache = false;// 是否缓存View页
	// public View view;
	protected Object viewobj = null;// 上一个界面节点传入的数据结构体
	protected int pageID = 0;// 页面Id号
	public AbsFragmentPage parent;// 父界面指的是从那个界面做的跳转
	protected View mView;
	protected String mTitle;

	@Override
	public void onCreateOptionsMenu(android.view.Menu menu,
			android.view.MenuInflater inflater)
	{
		// TODO Auto-generated method stub
		Log.v("wmh", "fffffffffffffffffffffffff");
		super.onCreateOptionsMenu(menu, inflater);
	}

	/**
	 * 设置下一个节点页面需要传递的数据
	 * 
	 * @param obj
	 *            传递的数据结构
	 * */
	public void setObj(Object obj)
	{
		this.viewobj = obj;
	}

	/**
	 * 获取上一个节点页面传递的数据
	 * 
	 * @return Object 上一个节点页面传递的数据
	 * */
	public Object getObj()
	{
		return viewobj;
	}

	// /**
	// * 添加带有menu的view
	// *
	// * */
	// public void addHaveMenuView(View view, LinearLayout.LayoutParams params,
	// final int pageId)
	// {
	// this.removeAllViews();
	// this.addView(view);
	// }

	public static enum LoadType
	{
		NEED_CREAT_LAYOUT, /** 需要创建layout */
		NOT_CREAT_LAYOUT
		/** 不需要创建layout */
	}
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// if (keyCode == KeyEvent.KEYCODE_BACK)
		// {
		// SysEng.getInstance().addHandlerEvent(new BackViewEvent());
		// return true;
		// } else
		// {
		// return false;
		// }
		return false;
	}

	/**
	 * touch 事件
	 * */
	// public abstract boolean onTouchEvent(MotionEvent event);

	public void OnActivityResult(int requestCode, int resultCode, Intent data)
	{

	};

	/**
	 * 
	 */
	// private int MenuId;
	public boolean onCreateOptionsMenu(MenuInflater inflater, Menu menu)
	{
		// inflater.inflate(R.menu.localpagemenu, menu);
		return false;
	}
	

	public com.actionbarsherlock.app.ActionBar getSupportActionBar()
	{
		if (getActivity() != null)
		{
			if (getActivity() instanceof MainUIActivity)
			{
				MainUIActivity ra = (MainUIActivity) getActivity();
				//return ra.getSupportActionBar();
//						getActionBar();
				return null;
			}
		}
		return null;
	}

	public SlidingMenu getSlidingMenu()
	{
		if (getActivity() != null)
		{
			if (getActivity() instanceof MainUIActivity)
			{
				MainUIActivity ra = (MainUIActivity) getActivity();
				return null;
				//return ra.getSlidingMenu();
			}
		}
		return null;
	}

	// {
	// inflater.inflate(R.menu.localpagemenu, menu);
	// return true;
	// }
	public boolean onOptionsItemSelected(MenuItem item)
	{
		Log.v("wmh", "onOptionsItemSelected");
//		switch (item.getItemId())
//		{
//		case R.id.muSetting:
//			SettingPage.actionSettingPage(m_act);
//			break;
//		case R.id.muAboutDialog:
//			new AboutDialog().showDialog(m_act);
//			break;
//		// case R.id.muFeedback:
//		// UMFeedbackService.openUmengFeedbackSDK(m_act);
//		// break;
//		case R.id.muExit:
//			SysEng.getInstance().addHandlerEvent(new ExitEvent(m_act, false));
//			break;
//		default:
//			return false;
//		}
		return true;
		// return super.onOptionsItemSelected(item);
	}

	public void onOptionsMenuClosed(Menu menu)
	{

		Log.v("wmh", "onOptionsMenuClosed");
		// super.onOptionsMenuClosed(menu);
	}

	public void onPrepareOptionsMenu(Menu menu)
	{
		Log.v("wmh", "onPrepareOptionsMenu");
		// onCreateOptionsMenu(menu);
		// menu.clear();
	}

	/**
	 * 菜单menu end
	 */
	/**
	 * 将指定View加入到当前窗体
	 * 
	 * @param rid
	 */
	protected void setContentView(int rid, LayoutInflater inflater)
	{
		mView = inflater.inflate(rid, null, true);
	}

	// @Override

	/**
	 * 当fragment和activity关联之后，调用这个方法。
	 */
	@Override
	public void onAttach(Activity activity)
	{
		// TODO Auto-generated method stub
		m_act = activity;
		super.onAttach(activity);
	}

	/**
	 * 保存的数据恢复
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	/**
	 * The Fragment's UI is just a simple text view showing its instance number.
	 * 初始化UI
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		if (mView != null && mView.getParent() != null)
		{
			// ((FrameLayout) mView.getParent()).removeAllViews();
		}
		if (mView == null)
		{
			onCreate(inflater, container, savedInstanceState);
		}
		return mView;
	}

	protected void finish()
	{
		//SysEng.getInstance().addHandlerEvent(new BackViewEvent());
	}

	/**
	 * 切换到新FragMent
	 * 
	 * @param fragment
	 */
	protected void switchFragment(Fragment fragment)
	{
		if (getActivity() == null)
			return;
		if (getActivity() instanceof MainUIActivity)
		{
			MainUIActivity ra = (MainUIActivity) getActivity();
			ra.switchContent(fragment);
		}
	}

	/**
	 * 创建
	 */
	public abstract void onCreate(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState);
}
