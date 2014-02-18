package com.kenny.Slidingmenu.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.framework.event.AbsEvent;
import com.framework.event.BackViewEvent;
import com.framework.page.AbsFragmentPage;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.Slidingmenu.MainUIActivity;
import com.kenny.file.Activity.SettingPage;
import com.kenny.file.Event.ExitEvent;
import com.kenny.file.dialog.AboutDialog;
import com.slidingmenu.lib.SlidingMenu;

/**
 * 界面基类
 * 
 * @author kenny
 * */
public abstract class ContentFragment extends AbsFragmentPage
{
	protected Activity m_act;
	protected Object viewobj = null;// 上一个界面节点传入的数据结构体
	protected int pageID = 0;// 页面Id号
	public ContentFragment parent;// 父界面指的是从那个界面做的跳转
	protected View mView;
	private String mTitle = null;
	private int mResTitle = 0;

	@Override
	public void onCreateOptionsMenu(android.view.Menu menu,
			android.view.MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);
	}

	public void setTitle(String mTitle)
	{
		this.mTitle = mTitle;
		if (getActivity() == null)
			return;
		if (getActivity() instanceof MainUIActivity)
		{
			MainUIActivity ra = (MainUIActivity) getActivity();
			ra.setTitle(mTitle);
		}
	}

	public void setTitle(int mTitle)
	{
		this.mResTitle = mTitle;
//		if (getSupportActionBar() != null)
//		{
//			getSupportActionBar().setTitle(mResTitle);
//		}
		if (getActivity() == null)
			return;
		if (getActivity() instanceof MainUIActivity)
		{
			MainUIActivity ra = (MainUIActivity) getActivity();
			ra.setTitle(mResTitle);
		}
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

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			ShowMenu();
			return true;
		}
		return super.onKeyDown(keyCode, event);
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

	public boolean onOptionsItemSelected(MenuItem item)
	{
		Log.v("wmh", "onOptionsItemSelected");
		switch (item.getItemId())
		{
		case R.id.muSetting:
//			SettingPage.actionSettingPage();
			switchFragment(new SettingPage());
			break;
		case R.id.muAboutDialog:
			new AboutDialog().showDialog(m_act);
			break;
		case 16908332:
			ShowMenu();
			break;
		// case R.id.muFeedback:
		// UMFeedbackService.openUmengFeedbackSDK(m_act);
		// break;
		case R.id.muExit:
			SysEng.getInstance().addHandlerEvent(new ExitEvent(m_act, false));
			break;
		default:
			return false;
		}
		return true;
		// return super.onOptionsItemSelected(item);
	}

	public void ShowMenu()
	{
		SysEng.getInstance().addHandlerEvent(new AbsEvent()
		{
			@Override
			public void ok()
			{
				if (getSlidingMenu() != null)
					getSlidingMenu().showMenu();
			}
		}, 50);
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
		// if (mView != null && mView.getParent() != null)
		// {
		// ((FrameLayout) mView.getParent()).removeAllViews();
		// }
		if (getSupportActionBar() != null)
		{
			if (mTitle != null)
			{
				getSupportActionBar().setTitle(mTitle);
			} else if (mResTitle != 0)
			{
				getSupportActionBar().setTitle(mResTitle);
			}
		}
		if (getActivity() != null&&getActivity() instanceof MainUIActivity)
		{
			MainUIActivity ra = (MainUIActivity) getActivity();
			if (mTitle != null)
			{
				ra.setTitle(mTitle);
			} else if (mResTitle != 0)
			{
				ra.setTitle(mResTitle);
			}
		}
		if (mView == null)
		{
			onCreate(inflater, container, savedInstanceState);
			// LoadingInit();
		}
		return mView;
	}

	private FrameLayout lframe;

	protected View LoadingView()
	{
		// ---------------正在加载界面 by
		LinearLayout pframe = new LinearLayout(mView.getContext());
		pframe.setId(432);
		pframe.setOrientation(LinearLayout.VERTICAL);
		pframe.setVisibility(View.VISIBLE);
		pframe.setGravity(Gravity.CENTER);

		ProgressBar progress = new ProgressBar(mView.getContext(), null,
				android.R.attr.progressBarStyleLarge);
		pframe.addView(progress, new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		// m_lvMain.addView(pframe, new FrameLayout.LayoutParams(
		// ViewGroup.LayoutParams.FILL_PARENT,
		// ViewGroup.LayoutParams.FILL_PARENT));
		// ------------------------------------------------------------------
		lframe = new FrameLayout(mView.getContext());
		lframe.setId(120);
		lframe.setVisibility(View.GONE);
		TextView tv = new TextView(getActivity());
		tv.setId(15);
		tv.setText("正在加载");
		tv.setGravity(Gravity.CENTER);
		lframe.addView(pframe, new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		lframe.addView(tv, new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		// m_lvMain.addView(lframe, new FrameLayout.LayoutParams(
		// ViewGroup.LayoutParams.FILL_PARENT,
		// ViewGroup.LayoutParams.FILL_PARENT));
		return lframe;
	}

	public void StartLoading()
	{
		if (lframe != null)
			lframe.setVisibility(View.VISIBLE);
	}

	public void StopLoading()
	{
		if (lframe != null)
			lframe.setVisibility(View.GONE);
	}

	protected void finish()
	{
		SysEng.getInstance().addHandlerEvent(new BackViewEvent());
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
	protected void backFragment()
	{
		if (getActivity() == null)
			return;
		if (getActivity() instanceof MainUIActivity)
		{
			MainUIActivity ra = (MainUIActivity) getActivity();
			ra.backFragment();
		}
	}
	/**
	 * 创建
	 */
	public abstract void onCreate(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState);
}
