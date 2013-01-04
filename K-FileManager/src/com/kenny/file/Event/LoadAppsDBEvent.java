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
import com.kenny.file.tools.SaveData;

/**
 * @author wmh 初始化event
 * */
public class LoadAppsDBEvent extends AbsEvent
{
   private String TAG = "LoadAppsEvent";
   private Activity m_act;
   private INotifyDataSetChanged m_INotify = null;
   private ProgressDialog myDialog = null;
   private boolean bShowDialog = false;
   private int ItemCount = 0;
   private int nFlag = 0;
   private ArrayList<AppBean> beans;
   
   public LoadAppsDBEvent(Activity act, int nFlag, ArrayList<AppBean> beans,
         boolean bShowDialog, INotifyDataSetChanged INotify)
   {
      this.m_act = act;
      this.beans = beans;
      this.nFlag = nFlag;
      this.m_INotify = INotify;
      this.bShowDialog = bShowDialog;
   }
   
   @Override
   public void ok()
   {
      P.debug(TAG, "LoadAppsEvent start");
      List<PackageInfo> packages = m_act.getPackageManager()
	  .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
      // .getInstalledPackages(PackageManager.GET_ACTIVITIES);
      // m_act.getPackageManager().get
      // int ItemCount = SaveData.Read(m_act, "UserAppCount", 0);
      // SaveData.Write(m_act, "UserAppCount", packages.size());
      Dao dao = Dao.getInstance(m_act);
      P.debug(TAG, "dao.AppInfoCount() start");
      ItemCount = dao.AppInfoCount();
      P.debug(TAG, "dao.AppInfoCount() end packages.size()=" + packages.size()
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
	     myDialog = ProgressDialog.show(m_act, "",
		 m_act.getString(R.string.msg_loading_data), true, true);
	     myDialog.setCancelable(false);
	     myDialog.show();
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
   }
   
   // /插入值
   public ArrayList<AppBean> InsertApp(Dao dao, List<PackageInfo> packages)
   {
      PackageManager packageManager = m_act.getPackageManager();
      // List<PackageInfo> packages = m_act.getPackageManager()
      // // .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
      // .getInstalledPackages(PackageManager.GET_ACTIVITIES);
      final ArrayList<AppBean> beans = new ArrayList<AppBean>();
      for (int i = 0; i < packages.size(); i++)
      {
         PackageInfo packageInfo = packages.get(i);
         
         // int flag = packageInfo.applicationInfo.flags
         // & ApplicationInfo.FLAG_SYSTEM;
         // flag: :1:系统 0:用户 直接读取手机内存里面的数据
         // if (1 == flag)
         // {
         // continue;
         // }
         AppBean tmpInfo = new AppBean(packageInfo.applicationInfo.sourceDir,
	     packageInfo.applicationInfo.loadLabel(packageManager).toString());
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
      dao.deleteAppAll();
      for (int i = 0; i < beans.size(); i++)
      {
         dao.saveInfos(beans.get(i));
      }
      dao.closeDb();
      return beans;
   }
}
