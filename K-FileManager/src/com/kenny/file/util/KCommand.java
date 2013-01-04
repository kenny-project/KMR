package com.kenny.file.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import com.kenny.file.tools.LinuxFileCommand;
import com.kenny.file.tools.SaveData;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.view.WindowManager;
import android.widget.Toast;

public class KCommand
{
   
   public static final boolean bDefToolsVisible = true;// ranking
   // 默认是否显示工具条true:显示
   public static String AppPackageName = null;
   public static Hashtable<String, Integer> facecacheid = new Hashtable<String, Integer>();
   
   public static String getReaderFilter(Context context)
   {
      if (AppPackageName == null)
      {
         AppPackageName = context.getPackageName();
      }
      return AppPackageName + ".web";
   }
   
   /**
    * 复制单个文件
    * 
    * @param oldPath
    *           String 原文件路径 如：c:/fqf.txt
    * @param newPath
    *           String 复制后路径 如：f:/fqf.txt
    * @return boolean
    * @throws IOException
    */
   public static void copyFile(InputStream inStream, String newPath)
         throws IOException
   {
      int bytesum = 0;
      int byteread = 0;
      FileOutputStream fs = new FileOutputStream(newPath);
      byte[] buffer = new byte[1444];
      while ((byteread = inStream.read(buffer)) != -1)
      {
         bytesum += byteread; // 字节数 文件大小
         System.out.println(bytesum);
         fs.write(buffer, 0, byteread);
      }
      inStream.close();
   }
   
   // 初始化运行程序所需要的文件
   private static boolean ResourceRAWFile(Context context, String szFileName,
         int ResourceID)
   {
      String path = "/data/data/" + context.getPackageName() + "/files/";
      // + szFileName;
      File fileName;
      try
      {
         new File(path).mkdirs();
         path += szFileName;
         fileName = new File(path);
         if (fileName.exists())
         {
	  fileName.delete();
         }
         fileName.createNewFile();
         InputStream inputStream = context.getResources().openRawResource(
	     ResourceID);
         copyFile(inputStream, path);
         return true;
      }
      catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
         fileName = new File(path);
         fileName.delete();
         return false;
      }
   }
   
   // 发送给好友信息
   public static void SendShare(Context context, String Title, String Body)
   {
      Intent it = new Intent(Intent.ACTION_SEND);
      String[] tos =
      { "" };
      it.putExtra(Intent.EXTRA_EMAIL, tos);
      it.putExtra(Intent.EXTRA_SUBJECT, "看到一个很有意思的发给你");
      it.putExtra(Intent.EXTRA_TITLE, "" + Title);
      it.putExtra(Intent.EXTRA_TEXT, "" + Body);
      it.setType("text/plain");
      it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(it);
      return;
   }
   
   public static void KWriteString(Context context, InputStream inStream,
         String filename) throws IOException
   {
      int bytesum = 0;
      int byteread = 0;
      FileOutputStream fs = context.openFileOutput(filename,
	  Context.MODE_WORLD_WRITEABLE);// MODE_WORLD_WRITEABLE
      byte[] buffer = new byte[1444];
      while ((byteread = inStream.read(buffer)) != -1)
      {
         bytesum += byteread; // 字节数 文件大小
         System.out.println(bytesum);
         fs.write(buffer, 0, byteread);
      }
      fs.flush();
      fs.close();
      inStream.close();
   }
   
   // 判断是否有可用的网络连接
   public static boolean isNetConnectNoMsg(Context context)
   {
      try
      {
         /*
          * Check for Internet Connection (Through whichever interface)
          */
         ConnectivityManager connManager = (ConnectivityManager) (context)
	     .getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo netInfo = connManager.getActiveNetworkInfo();
         /******* EMULATOR HACK - false condition needs to be removed *****/
         // if (false && (netInfo == null || !netInfo.isConnected())){
         if ((netInfo == null || netInfo.isConnected() == false))
         {
	  // SendMessage((context), "No Internet Connection");
         }
         else
         {
	  return true;
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return false;
   }
   
   // 检查网络 是否正常
   private boolean checkNet(Context context)
   {
      ConnectivityManager manager = (ConnectivityManager) (context)
	  .getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo netWrokInfo = manager.getActiveNetworkInfo();
      if (netWrokInfo == null || !netWrokInfo.isAvailable())
      {
         Toast.makeText(context, "当前的网络不可用，请开启\n网络", Toast.LENGTH_LONG).show();
         return false;
      }
      else if (netWrokInfo.getTypeName().equals("MOBILE")
	  & netWrokInfo.getExtraInfo().equals("cmwap"))
      {
         Toast.makeText(context, "cmwap网络不可用，请选择cmnet网络", Toast.LENGTH_LONG)
	     .show();
         return false;
      }
      else if (netWrokInfo.getTypeName().equals("MOBILE")
	  & netWrokInfo.getExtraInfo().equals("cmwap"))
      {
         Toast.makeText(context, "cmwap网络不可用，请选择cmnet网络", Toast.LENGTH_LONG)
	     .show();
         return false;
      }
      else
      {
         
         return true;
      }
   }
   
   // 判断是否有可用的网络连接
   public static boolean isNetConnect(Context context)
   {
      try
      {
         ConnectivityManager connManager = (ConnectivityManager) (context)
	     .getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo netInfo = connManager.getActiveNetworkInfo();
         /******* EMULATOR HACK - false condition needs to be removed *****/
         if ((netInfo == null || netInfo.isConnected() == false))
         {
	  // SendMessage((context), "No Internet Connection");
         }
         else if (netInfo.getTypeName().equals("MOBILE")
	     && netInfo.getExtraInfo().equals("cmwap"))
         {
	  Toast.makeText(context, "cmwap网络不可用，请选择cmnet网络", Toast.LENGTH_LONG)
	        .show();
	  return false;
         }
         else
         {
	  return true;
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      Toast.makeText(context, "未找到网络,请检查连接", Toast.LENGTH_SHORT).show();
      return false;
   }
   
   public static boolean isWiFiActive(Context inContext)
   {
      Context context = inContext.getApplicationContext();
      ConnectivityManager connectivity = (ConnectivityManager) context
	  .getSystemService(Context.CONNECTIVITY_SERVICE);
      if (connectivity != null)
      {
         NetworkInfo[] info = connectivity.getAllNetworkInfo();
         if (info != null)
         {
	  for (int i = 0; i < info.length; i++)
	  {
	     if (info[i].getTypeName().equals("WIFI"))
	     {
	        if (info[i].isConnected())
	        {
		 return true;
	        }
	        else
	        {
		 return false;
	        }
	        
	     }
	     
	  }
         }
      }
      return false;
   }
   
   // // 屏幕亮度调节
   // public static void SetScreenBrightness(Activity inActivity)
   // {
   // int value = SaveData.readPreferencesInt(inActivity, "ScreenBrightValue");
   // ;
   // if (value < 0)
   // {
   // value = 5;
   // SaveData.writePreferencesInt(inActivity, "ScreenBrightValue", value);
   // }
   // KCommand.ScreenBrightness(inActivity, value);
   // }
   
   // // 屏幕亮度调节
   // public static void ScreenBrightness(Activity inActivity, int value)
   // {
   // if (value < 1)
   // {
   // value = 1;
   // }
   // else if (value > 10)
   // {
   // value = 10;
   // }
   // WindowManager.LayoutParams lp = inActivity.getWindow().getAttributes();
   // lp.screenBrightness = value / 10.0f;
   // inActivity.getWindow().setAttributes(lp);
   // }
   
   public static boolean GetLightMode(Activity inActivity)
   {
      return SaveData.Read(inActivity, "LightMode", true);// 输入自动化
      
   }
   
   private static String UserName = null;
   
   /**
    * 获取用户名称
    * 
    * @param context
    * @return
    */
   public static String GetUserName(Context context)
   {
      if (UserName == null) UserName = SaveData.Read(context, "UserName", "");
      return UserName;
   }
}
