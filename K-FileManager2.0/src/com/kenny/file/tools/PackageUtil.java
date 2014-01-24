package com.kenny.file.tools;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.Format.Field;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class PackageUtil
{
	// ApplicationInfo 类，保存了普通应用程序的信息，主要是指Manifest.xml中application标签中的信息
	private List<ApplicationInfo> allAppList;

	public PackageUtil(Context context)
	{
		// 通过包管理器，检索所有的应用程序（包括卸载）与数据目录
		PackageManager pm = context.getApplicationContext().getPackageManager();
		allAppList = pm
				.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		pm.getInstalledPackages(0);
	}

	/**
	 * 通过一个程序名返回该程序的一个ApplicationInfo对象
	 * 
	 * @param name
	 *            程序名
	 * @return ApplicationInfo
	 */
	public ApplicationInfo getApplicationInfo(String appName)
	{
		if (appName == null)
		{
			return null;
		}
		for (ApplicationInfo appinfo : allAppList)
		{
			if (appName.equals(appinfo.processName))
			{
				return appinfo;
			}
		}
		return null;
	}

//	private PackageInfo parsePackage(String archiveFilePath, int flags)
//	{
//
//		PackageParser packageParser = new PackageParser(archiveFilePath);
//		DisplayMetrics metrics = new DisplayMetrics();
//		metrics.setToDefaults();
//		final File sourceFile = new File(archiveFilePath);
//		PackageParser.Package pkg = packageParser.parsePackage(sourceFile,
//				archiveFilePath, metrics, 0);
//		if (pkg == null)
//		{
//			return null;
//		}
//
//		packageParser.collectCertificates(pkg, 0);
//
//		return PackageParser.generatePackageInfo(pkg, null, flags, 0, 0);
//	}

//	private void showUninstallAPKIcon(String apkPath)
//	{
//		String PATH_PackageParser = "android.content.pm.PackageParser";
//		String PATH_AssetManager = "android.content.res.AssetManager";
//		try
//		{
//			// apk包的文件路径
//			// 这是一个Package 解释器, 是隐藏的
//			// 构造函数的参数只有一个, apk文件的路径
//			// PackageParser packageParser = new PackageParser(apkPath);
//			Class pkgParserCls = Class.forName(PATH_PackageParser);
//			Class[] typeArgs = new Class[1];
//			typeArgs[0] = String.class;
//			Constructor pkgParserCt = pkgParserCls.getConstructor(typeArgs);
//			Object[] valueArgs = new Object[1];
//			valueArgs[0] = apkPath;
//			Object pkgParser = pkgParserCt.newInstance(valueArgs);
//			Log.d("ANDROID_LAB", "pkgParser:" + pkgParser.toString());
//			// 这个是与显示有关的, 里面涉及到一些像素显示等等, 我们使用默认的情况
//			DisplayMetrics metrics = new DisplayMetrics();
//			metrics.setToDefaults();
//			// PackageParser.Package mPkgInfo = packageParser.parsePackage(new
//			// File(apkPath), apkPath,
//			// metrics, 0);
//			typeArgs = new Class[4];
//			typeArgs[0] = File.class;
//			typeArgs[1] = String.class;
//			typeArgs[2] = DisplayMetrics.class;
//			typeArgs[3] = Integer.TYPE;
//			Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
//					"parsePackage", typeArgs);
//			valueArgs = new Object[4];
//			valueArgs[0] = new File(apkPath);
//			valueArgs[1] = apkPath;
//			valueArgs[2] = metrics;
//			valueArgs[3] = 0;
//			Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
//					valueArgs);
//			// 应用程序信息包, 这个公开的, 不过有些函数, 变量没公开
//			// ApplicationInfo info = mPkgInfo.applicationInfo;
//			Field appInfoFld = pkgParserPkg.getClass().getDeclaredField(
//					"applicationInfo");
//			ApplicationInfo info = (ApplicationInfo) appInfoFld
//					.get(pkgParserPkg);
//			// uid 输出为"-1"，原因是未安装，系统未分配其Uid。
//			Log.d("ANDROID_LAB", "pkg:" + info.packageName + " uid=" + info.uid);
//			// Resources pRes = getResources();
//			// AssetManager assmgr = new AssetManager();
//			// assmgr.addAssetPath(apkPath);
//			// Resources res = new Resources(assmgr, pRes.getDisplayMetrics(),
//			// pRes.getConfiguration());
//			Class assetMagCls = Class.forName(PATH_AssetManager);
//			Constructor assetMagCt = assetMagCls.getConstructor((Class[]) null);
//			Object assetMag = assetMagCt.newInstance((Object[]) null);
//			typeArgs = new Class[1];
//			typeArgs[0] = String.class;
//			Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod(
//					"addAssetPath", typeArgs);
//			valueArgs = new Object[1];
//			valueArgs[0] = apkPath;
//			assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
//			Resources res = getResources();
//			typeArgs = new Class[3];
//			typeArgs[0] = assetMag.getClass();
//			typeArgs[1] = res.getDisplayMetrics().getClass();
//			typeArgs[2] = res.getConfiguration().getClass();
//			Constructor resCt = Resources.class.getConstructor(typeArgs);
//			valueArgs = new Object[3];
//			valueArgs[0] = assetMag;
//			valueArgs[1] = res.getDisplayMetrics();
//			valueArgs[2] = res.getConfiguration();
//			res = (Resources) resCt.newInstance(valueArgs);
//			CharSequence label = null;
//			if (info.labelRes != 0)
//			{
//				label = res.getText(info.labelRes);
//			}
//			// if (label == null) {
//			// label = (info.nonLocalizedLabel != null) ? info.nonLocalizedLabel
//			// : info.packageName;
//			// }
//			Log.d("ANDROID_LAB", "label=" + label);
//			// 这里就是读取一个apk程序的图标
//			if (info.icon != 0)
//			{
//				Drawable icon = res.getDrawable(info.icon);
////				ImageView image = (ImageView) findViewById(R.id.apkIconBySodino);
////				image.setVisibility(View.VISIBLE);
////				image.setImageDrawable(icon);
//			}
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
}