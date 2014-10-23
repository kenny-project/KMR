package com.kenny.file.Event;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.bean.AppBean;
import com.kenny.file.db.Dao;
import com.kenny.file.struct.INotifyDataSetChanged;
/**
 * @author wmh 初始化event
 * */
public class LoadAppsEvent extends AbsEvent
{
   private String TAG = "LoadAppsEvent";
   private Activity m_act;
   private INotifyDataSetChanged m_INotify = null;
   // private PkgSizeObserver mpkgSizeObserver;
   private ProgressDialog myDialog = null;
   private boolean bShowDialog = false;
   private int ItemCount = 0;
   private int nFlag = 0;
   private ArrayList<AppBean> beans;
   
   public LoadAppsEvent(Activity act, int nFlag, ArrayList<AppBean> beans,
         boolean bShowDialog, INotifyDataSetChanged INotify)
   {
      m_act = act;
      this.beans = beans;
      this.nFlag = nFlag;
      m_INotify = INotify;
      if (bShowDialog)
      {
         myDialog = ProgressDialog.show(m_act, "",
	     m_act.getString(R.string.msg_loading_data), true, true);
         myDialog.setCancelable(false);
         myDialog.show();
      }
   }
   
   /**
    * flag: :1:系统 0:用户 直接读取手机内存里面的数据
    */
   public void UserAppInfo(int flag)
   {
      List<PackageInfo> packages = m_act.getPackageManager()
      // .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
      // .getInstalledPackages(PackageManager.GET_ACTIVITIES);
	  .getInstalledPackages(0);
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
         }
      }
      P.debug(TAG, "LoadAppsEvent end");
      SysEng.getInstance().addHandlerEvent(new AbsEvent()
      {
         @Override
         public void ok()
         {
	  if (myDialog != null)
	  {
	     myDialog.dismiss();
	  }
	  if (m_INotify != null)
	  {
	     m_INotify.NotifyDataSetChanged(1, beans);
	  }
         }
      });
      P.debug(TAG, "LoadAppsEvent end");
   }
   
   @Override
   public void ok()
   {
      UserAppInfo(nFlag);
   }
   
   // 存储在数据库
   public void SysAppInfo()
   {
      final ArrayList<AppBean> beans = new ArrayList<AppBean>();
      List<PackageInfo> packages = m_act.getPackageManager()
      // .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
	  .getInstalledPackages(PackageManager.GET_ACTIVITIES);
      
      P.debug(TAG, "LoadAppsEvent start");
      
      Dao dao = Dao.getInstance(m_act);
      P.debug(TAG, "dao.AppInfoCount() start");
      ItemCount = dao.AppInfoCount();
      P.debug(TAG, "dao.AppInfoCount() end packages.size()=" + packages.size()
	  + "ItemCount=" + ItemCount);
      if (ItemCount == packages.size())
      {
         if (myDialog != null)
         {
	  myDialog.dismiss();
         }
         return;
      }
      
      for (int i = 0; i < packages.size(); i++)
      {
         PackageInfo packageInfo = packages.get(i);
         AppBean tmpInfo = new AppBean(packageInfo.applicationInfo.sourceDir,
	     packageInfo.applicationInfo.loadLabel(m_act.getPackageManager())
	           .toString());
         // tmpInfo.setAppName(packageInfo.applicationInfo
         // .loadLabel(m_act.getPackageManager())
         // .toString());
         tmpInfo.setPackageName(packageInfo.packageName);
         tmpInfo.setVersionName(packageInfo.versionName);
         tmpInfo.setVersionCode(packageInfo.versionCode);
         tmpInfo.setFilePath(packageInfo.applicationInfo.sourceDir);
         // tmpInfo.setFlags(packageInfo.applicationInfo.flags);
         tmpInfo.setFlags(packageInfo.applicationInfo.flags
	     & ApplicationInfo.FLAG_SYSTEM);
         
         // int nSdcard = packageInfo.applicationInfo.flags
         // & ApplicationInfo.FLAG_EXTERNAL_STORAGE;// 这个是关键
         // if (nSdcard != 0)
         // {
         // System.out.println(resolverInfo.activityInfo.name);// 这个是sd卡的应用
         // }
         beans.add(tmpInfo);
      }
      P.debug(TAG, "LoadAppsEvent DB start");
      dao.deleteAppAll();
      for (int i = 0; i < beans.size(); i++)
      {
         dao.saveInfos(beans.get(i));
      }
      dao.closeDb();
      P.debug(TAG, "LoadAppsEvent DB end");
      P.debug(TAG, "LoadAppsEvent end");
      SysEng.getInstance().addHandlerEvent(new AbsEvent()
      {
         @Override
         public void ok()
         {
	  if (myDialog != null)
	  {
	     myDialog.dismiss();
	  }
	  if (m_INotify != null)
	  {
	     m_INotify.NotifyDataSetChanged(1, beans);
	  }
         }
      });
      P.debug(TAG, "LoadAppsEvent end");
   }
   
   // 存储在数据库
   public void AppDBInfo()
   {
      final ArrayList<AppBean> beans = new ArrayList<AppBean>();
      List<PackageInfo> packages = m_act.getPackageManager()
      // .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
	  .getInstalledPackages(PackageManager.GET_ACTIVITIES);
      
      P.debug(TAG, "LoadAppsEvent start");
      
      Dao dao = Dao.getInstance(m_act);
      P.debug(TAG, "dao.AppInfoCount() start");
      ItemCount = dao.AppInfoCount();
      P.debug(TAG, "dao.AppInfoCount() end packages.size()=" + packages.size()
	  + "ItemCount=" + ItemCount);
      if (ItemCount == packages.size())
      {
         if (myDialog != null)
         {
	  myDialog.dismiss();
         }
         return;
      }
      
      for (int i = 0; i < packages.size(); i++)
      {
         PackageInfo packageInfo = packages.get(i);
         AppBean tmpInfo = new AppBean(packageInfo.applicationInfo.sourceDir,
	     packageInfo.applicationInfo.loadLabel(m_act.getPackageManager())
	           .toString());
         // tmpInfo.setAppName(packageInfo.applicationInfo
         // .loadLabel(m_act.getPackageManager())
         // .toString());
         tmpInfo.setPackageName(packageInfo.packageName);
         tmpInfo.setVersionName(packageInfo.versionName);
         tmpInfo.setVersionCode(packageInfo.versionCode);
         tmpInfo.setFilePath(packageInfo.applicationInfo.sourceDir);
         // tmpInfo.setFlags(packageInfo.applicationInfo.flags);
         tmpInfo.setFlags(packageInfo.applicationInfo.flags
	     & ApplicationInfo.FLAG_SYSTEM);
         
         // int nSdcard = packageInfo.applicationInfo.flags
         // & ApplicationInfo.FLAG_EXTERNAL_STORAGE;// 这个是关键
         // if (nSdcard != 0)
         // {
         // System.out.println(resolverInfo.activityInfo.name);// 这个是sd卡的应用
         // }
         beans.add(tmpInfo);
      }
      P.debug(TAG, "LoadAppsEvent DB start");
      dao.deleteAppAll();
      for (int i = 0; i < beans.size(); i++)
      {
         dao.saveInfos(beans.get(i));
      }
      dao.closeDb();
      P.debug(TAG, "LoadAppsEvent DB end");
      P.debug(TAG, "LoadAppsEvent end");
      SysEng.getInstance().addHandlerEvent(new AbsEvent()
      {
         @Override
         public void ok()
         {
	  if (myDialog != null)
	  {
	     myDialog.dismiss();
	  }
	  if (m_INotify != null)
	  {
	     m_INotify.NotifyDataSetChanged(1, beans);
	  }
         }
      });
      P.debug(TAG, "LoadAppsEvent end");
   }
}