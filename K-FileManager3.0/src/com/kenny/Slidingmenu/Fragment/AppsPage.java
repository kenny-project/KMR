package com.kenny.Slidingmenu.Fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
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
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Adapter.AppAdapter;
import com.kenny.file.Event.LoadAppsEvent;
import com.kenny.file.bean.AppBean;
import com.kenny.file.bean.FileBean;
import com.kenny.file.dialog.AppBackUpDialog;
import com.kenny.file.dialog.UnInstallDialog;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.menu.PopAppDialog;
import com.kenny.file.tools.ApkTools;
import com.kenny.file.util.Theme;

public class AppsPage extends ContentFragment implements OnItemClickListener,
		OnClickListener, OnItemLongClickListener, INotifyDataSetChanged
{
	// 当前实例
	private ArrayList<AppBean> mNowList = new ArrayList<AppBean>();
	private AppAdapter appAdapter;
	private ListView m_Appslist;

	private static final int UserAppFlag = 0; // 用户应用列表内容:
	private static final int SystemAppFlag = 1; // 系统应用列表内容:
	private int AFlag = UserAppFlag; // 列表内容:
	private Button btSelectAll, btSelectVisible, btUnInstall, btBackUp;
	// private Button btUserApp, btSystemApp;

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

	public AppsPage(int AFlag)
	{
		this.AFlag = AFlag;
	}

	public void onCreate(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		setContentView(R.layout.appspage, inflater);
		// 应用
		m_Appslist = (ListView) mView.findViewById(R.id.lvLocallist);
		m_Appslist.setBackgroundResource(Theme.getBackgroundResource());
		m_Appslist.setOnScrollListener(m_AppOnScrollListener);
		m_Appslist.setOnItemClickListener(this);
		m_Appslist.setOnItemLongClickListener(this);
		appAdapter = new AppAdapter(m_act, 1, mNowList);
		m_Appslist.setAdapter(appAdapter);

		btSelectVisible = (Button) mView.findViewById(R.id.btSelectVisible);
		btSelectVisible.setOnClickListener(this);

		btSelectAll = (Button) mView.findViewById(R.id.btSelectAll);
		btSelectAll.setOnClickListener(this);

		btUnInstall = (Button) mView.findViewById(R.id.btUnInstall);
		btUnInstall.setOnClickListener(this);

		btBackUp = (Button) mView.findViewById(R.id.btBackUp);
		btBackUp.setOnClickListener(this);
		appList(AFlag);
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
		this.AFlag = flag;
		if (flag == UserAppFlag)
		{
			btUnInstall.setVisibility(View.VISIBLE);
			btBackUp.setVisibility(View.VISIBLE);
		} else if (flag == SystemAppFlag)
		{
			btUnInstall.setVisibility(View.VISIBLE);
			btBackUp.setVisibility(View.VISIBLE);
		}
//		SysEng.getInstance().addEvent(new LoadAppsEvent(m_act, flag, this));
		appAdapter.setShowLogo(true);
		appAdapter.notifyDataSetChanged();
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

	public void NotifyDataSetChanged(final int cmd, final Object value)
	{
//		switch (cmd)
//		{
//		case Const.cmd_APP_UnInstallEvent_Finish:
//			SysEng.getInstance()
//					.addEvent(new LoadAppsEvent(m_act, AFlag, this));
//			return;
//		default:
//			break;
//		}
		if (value != null)
		{
			SysEng.getInstance().addHandlerEvent(new AbsEvent()
			{
				@Override
				public void ok()
				{
					mNowList.clear();
					mNowList.addAll((List<AppBean>) value);
					appAdapter.setShowLogo(true);
					appAdapter.notifyDataSetChanged();
				}
			});
		}
	}

	@Override
	public void onResume()
	{
		setTitle(getTitle());
		SysEng.getInstance()
		.addEvent(new LoadAppsEvent(m_act, AFlag, this));
		m_Appslist.setBackgroundResource(Theme.getBackgroundResource());
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(MenuInflater inflater, Menu menu)
	{
		inflater.inflate(R.menu.apppagemenu, menu);
		return true;
	}
}
