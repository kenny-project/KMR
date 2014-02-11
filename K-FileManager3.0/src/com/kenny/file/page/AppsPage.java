package com.kenny.file.page;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.log.P;
import com.framework.page.AbsFragmentPage;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.KFileManager.R.color;
import com.kenny.file.Adapter.AppAdapter;
import com.kenny.file.Event.ExitEvent;
import com.kenny.file.Event.LoadAppsDBEvent;
import com.kenny.file.bean.AppBean;
import com.kenny.file.bean.FileBean;
import com.kenny.file.commui.ListHeaderView;
import com.kenny.file.dialog.AppBackUpDialog;
import com.kenny.file.dialog.UnInstallDialog;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.menu.PopAppDialog;
import com.kenny.file.tools.ApkTools;

public class AppsPage extends AbsFragmentPage implements OnItemClickListener,
		OnClickListener, OnItemLongClickListener, INotifyDataSetChanged
{

	private View lyBTools;
	// 当前实例
	private ArrayList<AppBean> mNowList = new ArrayList<AppBean>();
	// 用户安装列表
	private ArrayList<AppBean> mUserAppList = new ArrayList<AppBean>();
	// 系统应用列表
	private ArrayList<AppBean> mSysAppList = new ArrayList<AppBean>();

	private AppAdapter appAdapter;
	private ListView m_Appslist;

	private static final int UserAppFlag = 0; // 用户应用列表内容:
	private static final int SystemAppFlag = 1; // 系统应用列表内容:
	private int AFlag = UserAppFlag; // 列表内容:
	private Button btSelectAll, btSelectVisible, btUnInstall, btBackUp;
	private Button btUserApp, btSystemApp;

	private View FooterView()
	{
		TextView tview = new TextView(m_act);
		tview.setHeight(100);
		tview.setWidth(-1);
		tview.setBackgroundColor(color.green);
		ListHeaderView headerView = new ListHeaderView(m_act, tview);
		return headerView;
	}

	private OnScrollListener m_AppOnScrollListener = new OnScrollListener()
	{
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			switch (scrollState)
			{
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
				P.debug("SCROLL_STATE_FLING");
				if (appAdapter != null)
					appAdapter.setShowLogo(false);
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
				P.debug("SCROLL_STATE_IDLE");
				if (appAdapter != null)
				{
					appAdapter.setShowLogo(true);
					appAdapter.notifyDataSetChanged();
				}
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				P.debug("SCROLL_STATE_TOUCH_SCROLL");
				if (appAdapter != null)
					appAdapter.setShowLogo(false);
				break;
			default:
				break;
			}
		}

		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount)
		{

		}
	};
	private OnScrollListener m_AppFileOnScrollListener = new OnScrollListener()
	{

		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			switch (scrollState)
			{
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
				P.debug("SCROLL_STATE_FLING");
				if (appAdapter != null)
					appAdapter.setShowLogo(false);
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
				P.debug("SCROLL_STATE_IDLE");
				if (appAdapter != null)
				{
					appAdapter.setShowLogo(true);
					appAdapter.notifyDataSetChanged();
				}
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				P.debug("SCROLL_STATE_TOUCH_SCROLL");
				if (appAdapter != null)
					appAdapter.setShowLogo(false);
				break;
			default:
				break;
			}
		}

		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount)
		{

		}
	};

	public AppsPage(int AFlag)
	{
		this.AFlag = AFlag;
	}

	public void onCreate(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		setContentView(R.layout.appspage, inflater);
		lyBTools = (View) mView.findViewById(R.id.lyBTools);
		// 应用
		m_Appslist = (ListView) mView.findViewById(R.id.lvLocallist);
		m_Appslist.setOnScrollListener(m_AppOnScrollListener);
		m_Appslist.setOnItemClickListener(this);
		m_Appslist.addFooterView(FooterView(), null, false);
		m_Appslist.setOnItemLongClickListener(this);
		appAdapter = new AppAdapter(m_act, 1, mNowList);
		m_Appslist.setAdapter(appAdapter);
		// 文件
		btUserApp = (Button) mView.findViewById(R.id.btUserApp);
		btUserApp.setOnClickListener(this);

		btSystemApp = (Button) mView.findViewById(R.id.btSystemApp);
		btSystemApp.setOnClickListener(this);

		btSelectVisible = (Button) mView.findViewById(R.id.btSelectVisible);
		btSelectVisible.setOnClickListener(this);

		btSelectAll = (Button) mView.findViewById(R.id.btSelectAll);
		btSelectAll.setOnClickListener(this);

		btUnInstall = (Button) mView.findViewById(R.id.btUnInstall);
		btUnInstall.setOnClickListener(this);

		btBackUp = (Button) mView.findViewById(R.id.btBackUp);
		btBackUp.setOnClickListener(this);
		
//		if (Theme.getToolsVisible())
//		{
//			lyBTools.setVisibility(View.VISIBLE);
//		} else
//		{
//			lyBTools.setVisibility(View.GONE);
//		}
		registerReceiver();
		appList(AFlag);
	}

	BroadcastReceiver packageReceiver = new BroadcastReceiver()
	{
		public void onReceive(Context ctx, Intent intent)
		{
			Log.v("wmh", "----------APACKAGE_ADDED----start");
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED))
			{
				Log.v("wmh",
						"----------PACKAGE_REMOVED----start:intent.getDataString()="
								+ intent.getDataString());
				String packageName = intent.getDataString().substring(8);
				for (int i = 0; i < mUserAppList.size(); i++)
				{
					if (mUserAppList.get(i).getPackageName()
							.equals(packageName))
					{
						mUserAppList.remove(i);
						appAdapter.notifyDataSetChanged();
						break;
					}
				}
				for (int i = 0; i < mSysAppList.size(); i++)
				{
					if (mSysAppList.get(i).getPackageName().equals(packageName))
					{
						mSysAppList.remove(i);
						appAdapter.notifyDataSetChanged();
						break;
					}
				}
				if (AFlag == UserAppFlag)
				{
					mNowList.clear();
					mNowList.addAll(mUserAppList);
				} else
				{
					mNowList.clear();
					mNowList.addAll(mSysAppList);
				}

				appAdapter.setShowLogo(true);
				appAdapter.notifyDataSetChanged();

			}
		}
	};

	public void registerReceiver()
	{
		P.v("wmh", "App:registerReceiver()");
		IntentFilter filter = new IntentFilter(); // 和广播中Intent的action对应
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		filter.addAction(Intent.ACTION_PACKAGE_ADDED);
		filter.addDataScheme("package");
		m_act.registerReceiver(packageReceiver, filter);// 注册监听函数
	}

	public void onResume()
	{
		P.v("wmh", "App:onResume()");

		super.onResume();
	}

	public void onPause()
	{
		P.v("wmh", "App:onPause()");
		super.onPause();
	}

	/** 注销广播 */

	public void onDestroy()
	{
		P.v("wmh", "App:onDestroy()");
		this.m_act.unregisterReceiver(this.packageReceiver);
		super.onDestroy();
	}
	public boolean onKeyDown(int keyCode, KeyEvent msg)
	{
		// 弹出退出对话框
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			SysEng.getInstance().addHandlerEvent(new ExitEvent(m_act, true));
			return true;
		}
		return super.onKeyDown(keyCode, msg);
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btUserApp:
			appList(UserAppFlag);
			break;
		case R.id.btSystemApp:
			appList(SystemAppFlag);
			break;

		case R.id.btBackUp:
			AppBackUp();
			break;
		case R.id.btUnInstall:
			AllUnInstall();
			break;
		case R.id.btSelectAll:
			SelectAll();
			break;
		case R.id.btSelectVisible:
			Selected(appAdapter.isSelected());
			break;
		}
	}

	private void Selected(boolean nFlag)
	{
		appAdapter.setSelected(!nFlag);
		appAdapter.notifyDataSetChanged();
		if (!nFlag)
		{
			this.btSelectVisible.setText(R.string.cancel);
		} else
		{
			this.btSelectVisible.setText(R.string.btSelect);
		}
	}

	/**
	 * UserAppFlag SystemAppFlag BackUpAppFlag
	 * 
	 * @param flag
	 */
	private void appList(final int flag)
	{
		List<AppBean> tempList = null;
		this.AFlag = flag;
		if (flag == UserAppFlag)
		{
			btUnInstall.setVisibility(View.VISIBLE);
			btBackUp.setVisibility(View.VISIBLE);
			P.debug("appList start");
			if (mUserAppList.size() == 0)
			{
				SysEng.getInstance().addEvent(
						new LoadAppsDBEvent(m_act, UserAppFlag, mUserAppList,
								mView.findViewById(R.id.pbLoading), this));
			} else
			{
				tempList = mUserAppList;
			}
			P.debug("appList end");
			btUserApp.setBackgroundResource(R.drawable.tab2_left_select);
			btSystemApp.setBackgroundResource(R.drawable.tab2_right_unselect);

			btSystemApp.setTextColor(m_act.getResources().getColor(
					R.color.tab_TextColor_selected));
			btUserApp.setTextColor(m_act.getResources().getColor(
					R.color.tab_TextColor_normal));
			m_Appslist.setVisibility(View.VISIBLE);

		} else if (flag == SystemAppFlag)
		{
			P.debug("appList start");
			btUnInstall.setVisibility(View.VISIBLE);
			btBackUp.setVisibility(View.VISIBLE);
			if (mSysAppList.size() == 0)
			{
				SysEng.getInstance().addEvent(
						new LoadAppsDBEvent(m_act, SystemAppFlag, mSysAppList,
								mView.findViewById(R.id.pbLoading), this));
			} else
			{
				tempList = mSysAppList;
			}
			P.debug("appList end");
			btUserApp.setBackgroundResource(R.drawable.tab2_left_unselect);
			btSystemApp.setBackgroundResource(R.drawable.tab2_right_select);
			btUserApp.setTextColor(m_act.getResources().getColor(
					R.color.tab_TextColor_selected));
			btSystemApp.setTextColor(m_act.getResources().getColor(
					R.color.tab_TextColor_normal));
			m_Appslist.setVisibility(View.VISIBLE);
		}
		if (tempList != null)
		{
			mNowList.clear();
			mNowList.addAll(tempList);
			appAdapter.setShowLogo(true);
			appAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 备份已选择的应用
	 */
	private void AppBackUp()
	{
		if (mNowList.size() > 0)
		{
			ArrayList<AppBean> mAppFiles = new ArrayList<AppBean>();
			for (int i = 0; i < mNowList.size(); i++)
			{
				AppBean tmpInfo = mNowList.get(i);
				if (tmpInfo.isChecked())
				{
					mAppFiles.add(tmpInfo);
				}
			}
			if (mAppFiles.size() > 0)
			{
				new AppBackUpDialog().ShowDialog(m_act, mAppFiles);
				return;
			}
		}
		Toast.makeText(
				m_act,
				m_act.getString(R.string.msg_Please_select_backup_program)
						+ "!", Toast.LENGTH_SHORT).show();
	}

	private void AllUnInstall()
	{
		if (mNowList.size() > 0)
		{
			ArrayList<AppBean> mAppFiles = new ArrayList<AppBean>();
			for (int i = 0; i < mNowList.size(); i++)
			{
				AppBean tmpInfo = mNowList.get(i);
				if (tmpInfo.isChecked())
				{
					mAppFiles.add(tmpInfo);
				}
			}
			if (mAppFiles.size() > 0)
			{
				new UnInstallDialog().ShowDialog(m_act, mAppFiles, this);
				return;
			}
		}
		Toast.makeText(
				m_act,
				m_act.getString(R.string.msg_Please_select_uninstall_program)
						+ "!", Toast.LENGTH_SHORT).show();
	}

	private void SelectAll()
	{
		if (mNowList.size() > 0)
		{
			boolean bCheck = !mNowList.get(0).isChecked();
			for (int i = 0; i < mNowList.size(); i++)
			{
				FileBean tmpInfo = mNowList.get(i);
				tmpInfo.setChecked(bCheck);
			}
			appAdapter.notifyDataSetChanged();
		}
	}

	// public boolean onCreateOptionsMenu(Menu menu) {
	// P.debug("onCreateOptionsMenu");
	// return super.onCreateOptionsMenu(menu, R.menu.apppagemenu);
	// }
	//
	// public boolean onPrepareOptionsMenu(Menu menu) {
	// super.onPrepareOptionsMenu(menu);
	// return true;
	// }

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		if (mNowList.size() > position)
		{
			AppBean temp = mNowList.get(position);
			ApkTools.StartApk(m_act, temp.getPackageName());
		} else
		{
			Toast.makeText(m_act,
					m_act.getString(R.string.msg_file_is_not_found),
					Toast.LENGTH_LONG).show();

		}
	}

	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3)
	{
		// MobclickAgent.onEvent(m_act, "AppEvent","ItemLongClick");
		AppBean temp = mNowList.get(arg2);
		switch (AFlag)
		{
		case UserAppFlag:
			temp = mNowList.get(arg2);
			PopAppDialog.ShowApp(m_act, temp);
			break;
		case SystemAppFlag:
			temp = mNowList.get(arg2);
			PopAppDialog.ShowApp(m_act, temp);
			break;
		default:
			return false;
		}
		return true;
	}

	public void NotifyDataSetChanged(int cmd, Object value)
	{
		if (value != null)
		{
			mNowList.clear();
			mNowList.addAll((List<AppBean>) value);
			appAdapter.setShowLogo(true);
			appAdapter.notifyDataSetChanged();
		} else
		{
			mUserAppList.clear();
			mSysAppList.clear();
			appList(AFlag);
		}
	}
}
