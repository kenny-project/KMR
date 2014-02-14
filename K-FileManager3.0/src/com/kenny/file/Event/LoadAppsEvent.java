package com.kenny.file.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.kenny.file.bean.AppBean;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.sort.FileSort;

/**
 * @author wmh 初始化event
 * */
public class LoadAppsEvent extends AbsEvent
{
	private String TAG = "LoadAppsEvent";
	private Activity m_act;
	private INotifyDataSetChanged m_INotify = null;
	private int nFlag = 0;
	private ArrayList<AppBean> beans = new ArrayList<AppBean>();

	public LoadAppsEvent(Activity act, int nFlag, INotifyDataSetChanged INotify)
	{
		m_act = act;
		this.nFlag = nFlag;
		m_INotify = INotify;
	}

	/**
	 * flag: :1:系统 0:用户 直接读取手机内存里面的数据
	 */
	public void UserAppInfo(int flag)
	{
		List<PackageInfo> packages = m_act.getPackageManager()
				.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
		P.debug(TAG, "LoadAppsEvent start");
		beans.clear();
		for (int i = 0; i < packages.size(); i++)
		{
			PackageInfo packageInfo = packages.get(i);
			int tFlags = packageInfo.applicationInfo.flags
					& ApplicationInfo.FLAG_SYSTEM;
			if (tFlags == flag)
			{
				AppBean tmpInfo = new AppBean(
						packageInfo.applicationInfo.sourceDir,
						packageInfo.applicationInfo.loadLabel(
								m_act.getPackageManager()).toString());
				tmpInfo.setPackageName(packageInfo.packageName);
				tmpInfo.setVersionName(packageInfo.versionName);
				tmpInfo.setVersionCode(packageInfo.versionCode);
				tmpInfo.setFilePath(packageInfo.applicationInfo.sourceDir);
				// tmpInfo.setFlags(packageInfo.applicationInfo.flags);
				tmpInfo.setFlags(packageInfo.applicationInfo.flags);
				beans.add(tmpInfo);
				if (beans.size() % 30 == 1)
				{
					NotifyDataSetChanged(1, beans);
				}
			}
		}
		Collections.sort(beans, new FileSort());
		NotifyDataSetChanged(1, beans);
	}

	@Override
	public void ok()
	{
		UserAppInfo(nFlag);
	}

	public void NotifyDataSetChanged(final int cmd, final List<AppBean> beans)
	{

		if (m_INotify != null)
		{
			m_INotify.NotifyDataSetChanged(cmd, beans);
		}

	}
}
