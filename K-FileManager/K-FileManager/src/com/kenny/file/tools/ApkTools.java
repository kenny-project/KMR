package com.kenny.file.tools;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.framework.log.P;

public class ApkTools
{
	public static class AppInfoData
	{
		private Drawable appIcon=null;
		private String Appname=null;
		private String Apppackage=null;
		private String Appversion=null;
		private int AppversionCode=0;

		public AppInfoData()
		{

		}

		public String getAppversion()
		{
			return Appversion;
		}

		public void setAppversion(String appversion)
		{
			Appversion = appversion;
		}

		public int getAppversionCode()
		{
			return AppversionCode;
		}

		public void setAppversionCode(int appversionCode)
		{
			AppversionCode = appversionCode;
		}

		public Drawable getAppIcon()
		{
			return appIcon;
		}

		public void setAppIcon(Drawable appIcon)
		{
			this.appIcon = appIcon;
		}

		public String getAppname()
		{
			return Appname;
		}

		public void setAppname(String appname)
		{
			Appname = appname;
		}

		public String getApppackage()
		{
			return Apppackage;
		}

		public void setApppackage(String apppackage)
		{
			Apppackage = apppackage;
		}

	}
	public static AppInfoData getApplicationInfo(Context ctx, File apkFile)
	{
//		return getFanSheApplicationInfo(ctx,apkFile);
		AppInfoData infoData = new AppInfoData();
        PackageManager pm = ctx.getPackageManager();     
        PackageInfo info = pm.getPackageArchiveInfo(apkFile.getAbsolutePath(), PackageManager.GET_ACTIVITIES);     
        if(info != null)
        {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.publicSourceDir = apkFile.getAbsolutePath();
            infoData.Appname = pm.getApplicationLabel(appInfo).toString();
            infoData.Apppackage = appInfo.packageName;  //得到安装包名称   
            infoData.Appversion=info.versionName;       //得到版本信息
            infoData.setAppIcon( pm.getApplicationIcon(appInfo));//得到图标信息
        }
        return infoData;
	}
	// 通过反射机制
	// 取得apk安装包的信息
	public static AppInfoData getFanSheApplicationInfo(Context ctx, File apkFile)
	{
		// System.out.println(apkFile.getPath());
		AppInfoData infoData = new AppInfoData();
		String PATH_PackageParser = "android.content.pm.PackageParser";
		String PATH_AssetManager = "android.content.res.AssetManager";
		try
		{
			if (!apkFile.exists())// ||
									// !apkFile.getName().toLowerCase().endsWith(".apk"))
			{
				return null;
			}
			// 反射得到pkgParserCls对象并实例化,有参数
			Class<?> pkgParserCls = Class.forName(PATH_PackageParser);
			// 反射得到assetMagCls对象并实例化,无参
			Class<?> assetMagCls = Class.forName(PATH_AssetManager);

			Class<?>[] typeArgs =
			{ String.class };
			Constructor<?> pkgParserCt = pkgParserCls.getConstructor();//by wmh getConstructor(typeArgs)
			Object[] valueArgs =
			{ apkFile.getPath() };
			Object pkgParser = pkgParserCt.newInstance();//newInstance(valueArgs)

			// 从pkgParserCls类得到parsePackage方法
			DisplayMetrics metrics = new DisplayMetrics();
			metrics.setToDefaults();// 这个是与显示有关的, 这边使用默认
			typeArgs = new Class<?>[]
			{ File.class, String.class, DisplayMetrics.class, int.class };
			Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
					"parsePackage", typeArgs);

			valueArgs = new Object[]
			{ apkFile, apkFile.getPath(), metrics, 0 };
			/**
			 * {@link PackageInfo} flag: return information about the signatures
			 * included in the package.
			 */

			// 执行pkgParser_parsePackageMtd方法并返回
			Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
					valueArgs);

			// 从返回的对象得到名为"applicationInfo"的字段对象
			if (pkgParserPkg == null)
			{
				return null;
			}
			Field appInfoFld = pkgParserPkg.getClass().getDeclaredField(
					"applicationInfo");

			// 从对象"pkgParserPkg"得到字段"appInfoFld"的值
			if (appInfoFld.get(pkgParserPkg) == null)
			{
				return null;
			}
			ApplicationInfo info = (ApplicationInfo) appInfoFld
					.get(pkgParserPkg);

			Object assetMag = assetMagCls.newInstance();
			// 从assetMagCls类得到addAssetPath方法
			typeArgs = new Class[1];
			typeArgs[0] = String.class;
			Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod(
					"addAssetPath", typeArgs);
			valueArgs = new Object[1];
			valueArgs[0] = apkFile.getPath();
			// 执行assetMag_addAssetPathMtd方法
			assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);

			// 得到Resources对象并实例化,有参数
			Resources res = ctx.getResources();
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

			// 读取apk文件的信息
			// AppInfo appInfoData = new AppInfo();
			if (info != null)
			{
				if (info.icon != 0)
				{// 图片存在，则读取相关信息
					Drawable icon = res.getDrawable(info.icon);// 图标
					// appInfoData.appIcon = (icon);
					infoData.setAppIcon(icon);
					// fileBean.setAppIcon(icon);
					// return icon;
				}
				if (info.labelRes != 0)
				{
					String name = (String) res.getText(info.labelRes);// 名字
					infoData.setAppname(name);
				}

				// infoData.setApppackage(info.packageName);// 包名
				// appInfoData.setApppackage(pkgName);

				PackageManager pm = ctx.getPackageManager();
				PackageInfo packageInfo = pm.getPackageArchiveInfo(
						apkFile.getAbsolutePath(),
						PackageManager.GET_ACTIVITIES);
				if (packageInfo != null)
				{
					infoData.setAppversion(packageInfo.versionName);// 版本号
					infoData.setAppversionCode(packageInfo.versionCode);// 版本码
				}
				return infoData;
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获得包名是否相同
	 * 
	 * @param ctx
	 * @return
	 * @throws Exception
	 */
	public static boolean isPackageName(Context ctx)
	{
		return ctx.getPackageName().equals("com.kenny.KFileManager");
	}

	/**
	 * 获取文件apk信息 图片
	 * 
	 * @param ctx
	 * @param apkPath
	 * @return
	 */
	public static Drawable getApkFileInfo(Context ctx, File apkFile)
	{
		// System.out.println(apkFile.getPath());
		String PATH_PackageParser = "android.content.pm.PackageParser";
		String PATH_AssetManager = "android.content.res.AssetManager";
		try
		{
			if (!apkFile.exists()
					|| !apkFile.getPath().toLowerCase().endsWith(".apk"))
			{
				System.out.println("文件路径不正确");
				return null;
			}
			// 反射得到pkgParserCls对象并实例化,有参数
			Class<?> pkgParserCls = Class.forName(PATH_PackageParser);
			// 反射得到assetMagCls对象并实例化,无参
			Class<?> assetMagCls = Class.forName(PATH_AssetManager);

			Class<?>[] typeArgs =
			{ String.class };
			Constructor<?> pkgParserCt = pkgParserCls.getConstructor(typeArgs);
			Object[] valueArgs =
			{ apkFile.getPath() };
			Object pkgParser = pkgParserCt.newInstance(valueArgs);

			// 从pkgParserCls类得到parsePackage方法
			DisplayMetrics metrics = new DisplayMetrics();
			metrics.setToDefaults();// 这个是与显示有关的, 这边使用默认
			typeArgs = new Class<?>[]
			{ File.class, String.class, DisplayMetrics.class, int.class };
			Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
					"parsePackage", typeArgs);

			valueArgs = new Object[]
			{ apkFile, apkFile.getPath(), metrics, 0 };

			// 执行pkgParser_parsePackageMtd方法并返回
			Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
					valueArgs);

			// 从返回的对象得到名为"applicationInfo"的字段对象
			if (pkgParserPkg == null)
			{
				return null;
			}
			Field appInfoFld = pkgParserPkg.getClass().getDeclaredField(
					"applicationInfo");

			// 从对象"pkgParserPkg"得到字段"appInfoFld"的值
			if (appInfoFld.get(pkgParserPkg) == null)
			{
				return null;
			}
			ApplicationInfo info = (ApplicationInfo) appInfoFld
					.get(pkgParserPkg);

			Object assetMag = assetMagCls.newInstance();
			// 从assetMagCls类得到addAssetPath方法
			typeArgs = new Class[1];
			typeArgs[0] = String.class;
			Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod(
					"addAssetPath", typeArgs);
			valueArgs = new Object[1];
			valueArgs[0] = apkFile.getPath();
			// 执行assetMag_addAssetPathMtd方法
			assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);

			// 得到Resources对象并实例化,有参数
			Resources res = ctx.getResources();
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

			// 读取apk文件的信息
			// AppInfo appInfoData = new AppInfo();
			if (info != null)
			{
				if (info.icon != 0)
				{// 图片存在，则读取相关信息
					Drawable icon = res.getDrawable(info.icon);// 图标
					// appInfoData.appIcon = (icon);
					// fileBean.setAppIcon(icon);
					return icon;
				}
				// if (info.labelRes != 0)
				// {
				// String name = (String) res.getText(info.labelRes);// 名字
				// // appInfoData.setAppname(neme);
				// } else
				// {
				// String apkName = apkFile.getName();
				// //
				// appInfoData.setAppname(apkName.substring(0,apkName.lastIndexOf(".")));
				// }
				// String pkgName = info.packageName;// 包名
				// appInfoData.setApppackage(pkgName);
			}
			// PackageManager pm = ctx.getPackageManager();
			// PackageInfo packageInfo =
			// pm.getPackageArchiveInfo(apkPath,
			// PackageManager.GET_ACTIVITIES);
			// if (packageInfo != null) {
			// appInfoData.setAppversion(packageInfo.versionName);//版本号
			// appInfoData.setAppversionCode(packageInfo.versionCode+"");//版本码
			// }
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * {@link PackageInfo} flag: return information about the signatures
	 * included in the package.
	 */
	public static final int GET_SIGNATURES = 0x00000040;

	public static Package parsePackage(File sourceFile, String destCodePath,
			DisplayMetrics metrics, int flags)
	{
		String PATH_PackageParser = "android.content.pm.PackageParser";

		try
		{
			// 反射得到pkgParserCls对象并实例化,有参数
			Class<?> pkgParserCls = Class.forName(PATH_PackageParser);
			// 反射得到assetMagCls对象并实例化,无参
			// ------------------构造函数---------
			Class<?>[] typeArgs =
			{ String.class };
			Constructor<?> pkgParserCt = pkgParserCls.getConstructor(typeArgs);
			Object[] valueArgs =
			{ sourceFile.getPath() };
			Object pkgParser = pkgParserCt.newInstance(valueArgs);//
			// ------------------构造函数结束---------

			typeArgs = new Class<?>[]
			{ File.class, String.class, DisplayMetrics.class, int.class };
			Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
					"parsePackage", typeArgs);

			valueArgs = new Object[]
			{ sourceFile, destCodePath, metrics, 0 };

			// 执行pkgParser_parsePackageMtd方法并返回
			Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
					valueArgs);

			return (Package) pkgParserPkg;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public final int addAssetPath(String path)
	{
		String PATH_AssetManager = "android.content.res.AssetManager";
		try
		{
			Class<?> assetMagCls = Class.forName(PATH_AssetManager);
			Object assetMag = assetMagCls.newInstance();

			// 从assetMagCls类得到addAssetPath方法
			Class[] typeArgs = new Class[1];
			typeArgs[0] = String.class;
			Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod(
					"addAssetPath", typeArgs);
			Object[] valueArgs = new Object[1];
			valueArgs[0] = path;
			// 执行assetMag_addAssetPathMtd方法
			assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return 1;
	}

	// /**
	// * 获取签名
	// */
	// public static Drawable getApkFileSignedInfo(Context ctx, File apkFile)
	// {
	//
	// try
	// {
	// if (!apkFile.exists()
	// || !apkFile.getPath().toLowerCase().endsWith(".apk"))
	// {
	// System.out.println("文件路径不正确");
	// return null;
	// }
	//
	// // 从pkgParserCls类得到parsePackage方法
	// DisplayMetrics metrics = new DisplayMetrics();
	// metrics.setToDefaults();// 这个是与显示有关的, 这边使用默认
	//
	// Package
	// pkgParserPkg=parsePackage(apkFile,apkFile.getPath(),metrics,GET_SIGNATURES);
	// // 从返回的对象得到名为"applicationInfo"的字段对象
	// if (pkgParserPkg == null)
	// {
	// return null;
	// }
	// Field appInfoFld = pkgParserPkg.getClass().getDeclaredField(
	// "applicationInfo");
	//
	// // 从对象"pkgParserPkg"得到字段"appInfoFld"的值
	// if (appInfoFld.get(pkgParserPkg) == null)
	// {
	// return null;
	// }
	// ApplicationInfo info = (ApplicationInfo) appInfoFld
	// .get(pkgParserPkg);
	//
	// // 得到Resources对象并实例化,有参数
	// Resources res = ctx.getResources();
	// Class[] typeArgs = new Class[3];
	// typeArgs[0] = assetMag.getClass();
	// typeArgs[1] = res.getDisplayMetrics().getClass();
	// typeArgs[2] = res.getConfiguration().getClass();
	// Constructor<Resources> resCt = Resources.class
	// .getConstructor(typeArgs);
	// Object[] valueArgs = new Object[3];
	// valueArgs[0] = assetMag;
	// valueArgs[1] = res.getDisplayMetrics();
	// valueArgs[2] = res.getConfiguration();
	// res = (Resources) resCt.newInstance(valueArgs);
	//
	// // 读取apk文件的信息
	// // AppInfo appInfoData = new AppInfo();
	// if (info != null)
	// {
	// if (info.icon != 0)
	// {// 图片存在，则读取相关信息
	// Drawable icon = res.getDrawable(info.icon);// 图标
	// return icon;
	// }
	// }
	//
	// } catch (Exception e)
	// {
	// e.printStackTrace();
	// }
	// return null;
	// }

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
			ApplicationInfo info = (ApplicationInfo) appInfoFld
					.get(pkgParserPkg);
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
		} catch (Exception e)
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
	 *            应用程序的包名
	 */
	public static void showAppDetails(Context context, String packageName)
	{
		try
		{
			Intent intent = new Intent();
			final int apiLevel = Build.VERSION.SDK_INT;
			if (apiLevel >= 9)
			{ // 2.3（ApiLevel 9）以上，使用SDK提供的接口
				intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				Uri uri = Uri.fromParts(SCHEME, packageName, null);
				intent.setData(uri);
			} else
			{ // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
				// 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
				final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
						: APP_PKG_NAME_21);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setClassName(APP_DETAILS_PACKAGE_NAME,
						APP_DETAILS_CLASS_NAME);
				intent.putExtra(appPkgName, packageName);
			}
			context.startActivity(intent);
		} catch (Exception e)
		{
			e.printStackTrace();
			Toast.makeText(context, "未找到该应用", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 启动指定包名的APP
	 * 
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
		} catch (Exception e)
		{
			P.debug("error", e.getMessage());
			return false;
		}
	}

}