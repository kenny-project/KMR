package com.kenny.file.tools;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.framework.log.P;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;

public class ApkTools
{
   public static Drawable geTApkIcon(Context context, String Path)
   {
      // 未安装的程序通过apk文件获取icon
      String apkPath = Path; // apk 文件所在的路径
      String PATH_PackageParser = "android.content.pm.PackageParser";
      String PATH_AssetManager = "android.content.res.AssetManager";
      try
      {
         Class<?> pkgParserCls = Class.forName(PATH_PackageParser);
         Class<?>[] typeArgs =
         { String.class };
         Constructor<?> pkgParserCt = pkgParserCls.getConstructor(typeArgs);
         Object[] valueArgs =
         { apkPath };
         Object pkgParser = pkgParserCt.newInstance(valueArgs);
         DisplayMetrics metrics = new DisplayMetrics();
         metrics.setToDefaults();
         typeArgs = new Class<?>[]
         { File.class, String.class, DisplayMetrics.class, int.class };
         Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
	     "parsePackage", typeArgs);
         valueArgs = new Object[]
         { new File(apkPath), apkPath, metrics, 0 };
         Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
	     valueArgs);
         Field appInfoFld = pkgParserPkg.getClass().getDeclaredField(
	     "applicationInfo");
         ApplicationInfo info = (ApplicationInfo) appInfoFld.get(pkgParserPkg);
         Class<?> assetMagCls = Class.forName(PATH_AssetManager);
         Object assetMag = assetMagCls.newInstance();
         typeArgs = new Class[1];
         typeArgs[0] = String.class;
         Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod(
	     "addAssetPath", typeArgs);
         valueArgs = new Object[1];
         valueArgs[0] = apkPath;
         assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
         Resources res = context.getResources();
         typeArgs = new Class[3];
         typeArgs[0] = assetMag.getClass();
         typeArgs[1] = res.getDisplayMetrics().getClass();
         typeArgs[2] = res.getConfiguration().getClass();
         Constructor<Resources> resCt = Resources.class
	     .getConstructor(typeArgs);
         valueArgs = new Object[3];
         valueArgs[0] = assetMag;
         valueArgs[1] = res.getDisplayMetrics();
         valueArgs[2] = res.getConfiguration();
         res = (Resources) resCt.newInstance(valueArgs);
         if (info != null)
         {
	  if (info.icon != 0)
	  {
	     Drawable icon = res.getDrawable(info.icon);
	     return icon;
	  }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return null;
   }
   
   /*************************************************************************************
    * 显示应用详情
    **************************************************************************************/
   private static final String SCHEME = "package";
   /**
    * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
    */
   private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
   /**
    * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
    */
   private static final String APP_PKG_NAME_22 = "pkg";
   /**
    * InstalledAppDetails所在包名
    */
   private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
   /**
    * InstalledAppDetails类名
    */
   private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
   
   /**
    * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level
    * 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
    * 
    * @param context
    * 
    * @param packageName
    *           应用程序的包名
    */
   public static void showAppDetails(Context context,
         String packageName)
   {
      Intent intent = new Intent();
      final int apiLevel = Build.VERSION.SDK_INT;
      if (apiLevel >= 9)
      { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
         intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
         Uri uri = Uri.fromParts(SCHEME, packageName, null);
         intent.setData(uri);
      }
      else
      { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
        // 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
         final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
	     : APP_PKG_NAME_21);
         intent.setAction(Intent.ACTION_VIEW);
         intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
         intent.putExtra(appPkgName, packageName);
      }
      context.startActivity(intent);
   }
   /**
    * 启动指定包名的APP
    * @param act
    * @param strPackageName
    * @return
    */
   public static boolean StartApk(Activity act, String strPackageName)
   {
      
      try
      {
         PackageManager packageManager = act.getPackageManager();
         Intent intent = new Intent();
         intent = packageManager.getLaunchIntentForPackage(strPackageName);
         act.startActivity(intent);
         return true;
      }
      catch (Exception e)
      {
         P.debug("error", e.getMessage());
         return false;
      }
   }

}