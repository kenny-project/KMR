package com.kenny.file.util;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.RemoteException;
import com.framework.log.P;
import com.kenny.file.bean.AppBean;
import com.kenny.file.db.Dao;
import com.kenny.file.struct.INotifyDataSetChanged;

// aidl文件形成的Bindler机制服务类
public class PkgSizeObserver extends IPackageStatsObserver.Stub
{
   private INotifyDataSetChanged notify;
   private Method getPackageSizeInfo = null;
   private PackageManager pm = null;
   private Map<String, SoftReference<PackageStats>> packageStatsCache = new HashMap<String, SoftReference<PackageStats>>();
   private static PkgSizeObserver m_pkgSizeObServer = null;
   /***
    * 回调函数，
    * 
    * @param pStatus
    *           ,返回数据封装在PackageStats对象中
    * @param succeeded
    *           代表回调成功
    */
   private Dao dao = null;
   private AppBean mAppBean;
   
   private PkgSizeObserver(Context m_act)
   {
      try
      {
         dao = Dao.getInstance(m_act);
         pm = m_act.getPackageManager();
         // 通过反射机制获得该隐藏函数
         getPackageSizeInfo = pm.getClass().getDeclaredMethod(
	     "getPackageSizeInfo", String.class, IPackageStatsObserver.class);
         // 调用该函数，并且给其分配参数
         // ，待调用流程完成后会回调PkgSizeObserver类的函数
      }
      catch (Exception ex)
      {
         P.e("page", "NoSuchMethodException");
         ex.printStackTrace();
         getPackageSizeInfo = null;
      }
   }
   
   public static PkgSizeObserver getHandler(Context act)
   {
      if (m_pkgSizeObServer == null)
      {
         m_pkgSizeObServer = new PkgSizeObserver(act);
      }
      return m_pkgSizeObServer;
   }
   
   public PackageStats Start(AppBean appBean, INotifyDataSetChanged notify)
   {
      PackageStats result = null;
      this.notify = notify;
      this.mAppBean = appBean;
      String packageName = appBean.getPackageName();
      if (packageStatsCache.containsKey(packageName))// 从列表中获取数据
      {
         SoftReference<PackageStats> softReference = packageStatsCache
	     .get(packageName);
         if (softReference != null)
         {
	  result = softReference.get();
	  if (result != null) { return result; }
         }
      }
      packageStatsCache.remove(result);
      // 使用放射机制得到PackageManager类的隐藏函数getPackageSizeInfo
      try
      {
         
         if (getPackageSizeInfo != null) getPackageSizeInfo.invoke(pm,
	     packageName, this);
         
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }
      
      return null;
   }
   
   
   public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
         throws RemoteException
   {
      if (succeeded)
      {
         packageStatsCache.put(pStats.packageName,
	     new SoftReference<PackageStats>(pStats));
         mAppBean.setCacheSize(pStats.cacheSize); // 缓存大小
         mAppBean.setDataSize(pStats.dataSize); // 数据大小
         mAppBean.setCodeSize(pStats.codeSize); // 应用程序大小
         if (notify != null)
         {
	  notify.NotifyDataSetChanged(1, pStats);
         }
         dao.updataInfos(pStats.packageName, pStats.codeSize, pStats.dataSize,
	     pStats.cacheSize);
      }
   }
}
