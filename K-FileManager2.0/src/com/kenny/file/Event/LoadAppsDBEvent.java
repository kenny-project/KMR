package com.kenny.file.Event;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.ProgressBar;

import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.bean.AppBean;
import com.kenny.file.db.Dao;
import com.kenny.file.interfaces.INotifyDataSetChanged;

/**
 * @author wmh 初始化event
 * */
public class LoadAppsDBEvent extends AbsEvent
{
	private String TAG = "LoadAppsEvent";
	private Activity m_act;
	private INotifyDataSetChanged m_INotify = null;
	private ProgressBar myDialog = null;
	private int ItemCount = 0;
	private int nFlag = 0;
	private ArrayList<AppBean> beans;
	private View mView = null;// Loading资源组

	public LoadAppsDBEvent(Activity act, int nFlag, ArrayList<AppBean> beans,
			View mView, INotifyDataSetChanged INotify)
	{
		this.m_act = act;
		this.beans = beans;
		this.nFlag = nFlag;
		this.m_INotify = INotify;
		this.mView = mView;
	}

	@Override
	public void ok()
	{
		P.debug(TAG, "LoadAppsEvent start");
		final List<PackageInfo> packages = m_act.getPackageManager()
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		// .getInstalledPackages(PackageManager.GET_ACTIVITIES);
		// m_act.getPackageManager().get
		// int ItemCount = SaveData.Read(m_act, "UserAppCount", 0);
		// SaveData.Write(m_act, "UserAppCount", packages.size());
		Dao dao = Dao.getInstance(m_act);
		P.debug(TAG, "dao.AppInfoCount() start");
		ItemCount = dao.AppInfoCount();
		P.debug(TAG,
				"dao.AppInfoCount() end packages.size()=" + packages.size()
						+ "ItemCount=" + ItemCount);
		beans.clear();
		if (ItemCount != packages.size())
		{
			// 数据不一致
			SysEng.getInstance().addHandlerEvent(new AbsEvent()
			{
				@Override
				public void ok()
				{
					if (mView != null)
					{
						myDialog = (ProgressBar) mView
								.findViewById(R.id.pbSDFileStatus);
						myDialog.setMax(packages.size());
						myDialog.setProgress(0);
						mView.setVisibility(View.VISIBLE);
					}
				}
			});
			P.debug(TAG, "InsertApp start");
			InsertApp(dao, packages);
			P.debug(TAG, "InsertApp end");
		}
		beans.addAll(dao.getAppInfos(String.valueOf(nFlag)));
		SysEng.getInstance().addHandlerEvent(new AbsEvent()
		{
			@Override
			public void ok()
			{
				if (mView != null)
				{
					mView.setVisibility(View.GONE);
					if (m_INotify != null)
					{
						m_INotify.NotifyDataSetChanged(1, beans);
					}
				}
			}
		});
	}

	// /插入值
	public ArrayList<AppBean> InsertApp(Dao dao, List<PackageInfo> packages)
	{
		PackageManager packageManager = m_act.getPackageManager();
		final ArrayList<AppBean> beans = new ArrayList<AppBean>();
		for (int i = 0; i < packages.size(); i++)
		{
			PackageInfo packageInfo = packages.get(i);
			if (myDialog != null)
				myDialog.incrementProgressBy(1);
			try
			{
				AppBean tmpInfo = new AppBean(
						packageInfo.applicationInfo.sourceDir,
						packageInfo.applicationInfo.loadLabel(packageManager)
								.toString());
				tmpInfo.setPackageName(packageInfo.packageName);
				tmpInfo.setVersionName(packageInfo.versionName);
				tmpInfo.setVersionCode(packageInfo.versionCode);
				tmpInfo.setFilePath(packageInfo.applicationInfo.sourceDir);
				// tmpInfo.setFlags(packageInfo.applicationInfo.flags);
				tmpInfo.setFlags(packageInfo.applicationInfo.flags
						& ApplicationInfo.FLAG_SYSTEM);
				beans.add(tmpInfo);
				//dao.saveInfos(tmpInfo);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		dao.deleteAppAll();
		dao.saveInfos(beans);
		dao.closeDb();
		return beans;
	}
}
