package com.framework.log;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/**
 * debug
 * 
 * @author aimery
 * */
public class P
{
   private static boolean isLogShow = true;
   
   public static void Init(Context ctx)
   {
      PackageInfo packageInfo = null;
      try
      {
         packageInfo = ctx.getPackageManager().getPackageInfo(
	     ctx.getPackageName(), 0);
         int flags = packageInfo.applicationInfo.flags;
         //flags=flags & ApplicationInfo.FLAG_DEBUGGABLE;
         //Log.v("wmh", "Init:flags="+flags);
         if ((flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0)
         {
	  // development mode
	  isLogShow=true;
         }
         else
         {
	  // release mode
	  isLogShow=false;
         }
         Log.v("wmh", "Init:isLogShow="+isLogShow);
      }
      catch (NameNotFoundException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }// get package info for your context
      
   }
   
   public static void debug(String s)
   {
      if (isLogShow) Log.v("debug", "" + s);
   }
   
   public static void debug(String tag, String s)
   {
      if (isLogShow) Log.v("" + tag, "" + s);
   }
   
   public static void v(String s)
   {
      if (isLogShow) Log.v("def", "" + s);
   }
   
   public static void v(String tag, String s)
   {
      if (isLogShow) Log.v("" + tag, "" + s);
   }
   public static void d(String tag, String s)
   {
      if (isLogShow) Log.d("" + tag, "" + s);
   }   
   public static void i(String tag, String s)
   {
      if (isLogShow) Log.i("" + tag, "" + s);
   }
   
   public static void e(String tag, String s)
   {
      if (isLogShow) Log.e("" + tag, "" + s);
   }
}
