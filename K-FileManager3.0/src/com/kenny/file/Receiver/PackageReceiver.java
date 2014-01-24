package com.kenny.file.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.kenny.file.bean.AppBean;
import com.kenny.file.db.Dao;

public class PackageReceiver extends BroadcastReceiver
{
   @Override
   public void onReceive(Context context, Intent intent)
   {
      try
      {
         // 接收广播：系统启动完成后运行程序
         if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
         {
	  String packageName = intent.getDataString().substring(8);
	  Log.v("wmh", "----------BOOT_COMPLETED----" + packageName);
         }
         // 接收广播：设备上新安装了一个应用程序包后自动启动新安装应用程序。
         else if (intent.getAction().equals(
	     Intent.ACTION_PACKAGE_ADDED))
         {
	  Log.v("wmh", "----------PACKAGE_ADDED----start");
	  String packageName = intent.getDataString().substring(8);
	  int result=AddAppInfo(context, packageName);
	  Log.v("wmh", "----------PACKAGE_ADDED----" + packageName+" result="+result);
         }
         // 接收广播：设备上删除了一个应用程序包。
         else if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED))
         {
	  Log.v("wmh", "----------PACKAGE_REMOVED----start:intent.getDataString()="+intent.getDataString());
	  String packageName = intent.getDataString().substring(8);
	  Dao m_Dao = Dao.getInstance(context);
	  int result = m_Dao.delete(packageName);
	  m_Dao.closeDb();
	  Log.v("wmh", "----------PACKAGE_REMOVED----" + packageName
	        + "result=" + result);
         }
         else if (intent.getAction().equals(Intent.ACTION_DELETE))
         {
	  String packageName = intent.getDataString().substring(8);
	  Log.v("wmh", "----------android.intent.action.DELETE----"
	        + packageName);
	  // PackageManager packageManger = context.getPackageManager();
	  // PackageInfo packageInfo =
	  // packageManger.getPackageInfo(packageName,
	  // 0);
	  // if (packageInfo != null)
	  // {
	  // int tFlags = packageInfo.applicationInfo.flags
	  // & ApplicationInfo.FLAG_SYSTEM;
	  // AppBean tmpInfo = new AppBean(
	  // packageInfo.applicationInfo.sourceDir,
	  // packageInfo.applicationInfo.loadLabel(
	  // context.getPackageManager()).toString());
	  // tmpInfo.setPackageName(packageInfo.packageName);
	  // tmpInfo.setVersionName(packageInfo.versionName);
	  // tmpInfo.setVersionCode(packageInfo.versionCode);
	  // tmpInfo.setFilePath(packageInfo.applicationInfo.sourceDir);
	  // // tmpInfo.setFlags(packageInfo.applicationInfo.flags);
	  // tmpInfo.setFlags(packageInfo.applicationInfo.flags);
	  // int result = SDFile.BackAppFile(tmpInfo.getFilePath(),
	  // Const.szRecyclePath,
	  // tmpInfo.getAppName() + tmpInfo.getVersionName());
	  // P.v("ACTION_DELETE end");
	  // }
	  // else
	  // {
	  // P.v("ACTION_DELETE end 未找到相应文件");
	  // }
         }
         final String BROADCAST = "com.kenny.package.change"; 
         
         Intent intent1 = new Intent(BROADCAST); // 对应setAction() 
          
//         intent1.putExtra("data_title", "来短信啦"); 
//          
//         intent1.putExtra("data_text", "美女你好，晚上可有空"); 
          
         context.sendBroadcast(intent1); 
      }
      catch (Exception e)
      {
         Log.v("wmh", "----------PACKAGE_Error----");
         e.printStackTrace();
      }
   }
   
   private int AddAppInfo(Context context, String packageName)
   {
      PackageManager packageManager = context.getPackageManager();
      PackageInfo packageInfo;
      try
      {
         packageInfo = packageManager.getPackageInfo(packageName, 0);
         AppBean tmpInfo = new AppBean(packageInfo.applicationInfo.sourceDir,
	     packageInfo.applicationInfo.loadLabel(packageManager)
	           .toString());
         tmpInfo.setPackageName(packageInfo.packageName);
         tmpInfo.setVersionName(packageInfo.versionName);
         tmpInfo.setVersionCode(packageInfo.versionCode);
         tmpInfo.setFilePath(packageInfo.applicationInfo.sourceDir);
         // tmpInfo.setFlags(packageInfo.applicationInfo.flags);
         tmpInfo.setFlags(packageInfo.applicationInfo.flags
	     & ApplicationInfo.FLAG_SYSTEM);
         Dao m_Dao = Dao.getInstance(context);
        int result= m_Dao.saveInfos(tmpInfo);
         m_Dao.closeDb();
         return result;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return -1;
      }
   }
}