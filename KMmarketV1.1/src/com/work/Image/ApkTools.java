package com.work.Image;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
import android.widget.Toast;

import com.framework.log.P;
import com.work.market.util.ZipInstall;

public class ApkTools
{
	public static class AppInfoData
	{
		private Drawable appIcon;
		private String Appname;
		private String Apppackage;

		public AppInfoData()
		{
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

	public static void StartAutoInster(Context context, final String file,
			String fileExt)
	{
		if ("apk".equals(fileExt))
		{
			Uri uri = Uri.fromFile(new File(file));
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(uri,
					"application/vnd.android.package-archive");
			context.startActivity(intent);
		}
		else if ("zip".equals(fileExt))
		{
			Toast.makeText(context, "�����أ��뵽�Ѿ������б�����ֶ���װ", Toast.LENGTH_LONG).show();
		}
	}
	public static void StartInster(Context context, final String file,
			String fileExt)
	{
		if ("apk".equals(fileExt))
		{
			Uri uri = Uri.fromFile(new File(file));
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setDataAndType(uri,
					"application/vnd.android.package-archive");
			context.startActivity(intent);
		}
		else if ("zip".equals(fileExt))
		{
			ZipInstall.InstallZip(context, file);
		}
		else
		{
			Toast.makeText(context, "Ŀǰ����֧�ָø�ʽ��װ", Toast.LENGTH_LONG).show();
		}
	}

	// ͨ������
	// ȡ��apk��װ������Ϣ
	public static AppInfoData getApplicationInfo(Context ctx, File apkFile)
	{
		// System.out.println(apkFile.getPath());
		AppInfoData infoData = new AppInfoData();
		String PATH_PackageParser = "android.content.pm.PackageParser";
		String PATH_AssetManager = "android.content.res.AssetManager";
		try
		{
			if (!apkFile.exists()
					|| !apkFile.getPath().toLowerCase().endsWith(".apk"))
			{
				System.out.println("�ļ�·������ȷ");
				return null;
			}
			// ����õ�pkgParserCls����ʵ����,�в�
			Class<?> pkgParserCls = Class.forName(PATH_PackageParser);
			// ����õ�assetMagCls����ʵ����,�޲�
			Class<?> assetMagCls = Class.forName(PATH_AssetManager);

			Class<?>[] typeArgs =
			{ String.class };
			Constructor<?> pkgParserCt = pkgParserCls.getConstructor(typeArgs);
			Object[] valueArgs =
			{ apkFile.getPath() };
			Object pkgParser = pkgParserCt.newInstance(valueArgs);

			// ��pkgParserCls��õ�parsePackage����
			DisplayMetrics metrics = new DisplayMetrics();
			metrics.setToDefaults();// ���������ʾ�й� ���ʹ��Ĭ��
			typeArgs = new Class<?>[]
			{ File.class, String.class, DisplayMetrics.class, int.class };
			Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
					"parsePackage", typeArgs);

			valueArgs = new Object[]
			{ apkFile, apkFile.getPath(), metrics, 0 };

			// ִ��pkgParser_parsePackageMtd��������
			Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
					valueArgs);

			// �ӷ��صĶ���õ���Ϊ"applicationInfo"���ֶζ�
			if (pkgParserPkg == null)
			{
				return null;
			}
			Field appInfoFld = pkgParserPkg.getClass().getDeclaredField(
					"applicationInfo");

			// �Ӷ�pkgParserPkg"�õ��ֶ�"appInfoFld"��
			if (appInfoFld.get(pkgParserPkg) == null)
			{
				return null;
			}
			ApplicationInfo info = (ApplicationInfo) appInfoFld
					.get(pkgParserPkg);

			Object assetMag = assetMagCls.newInstance();
			// ��assetMagCls��õ�addAssetPath����
			typeArgs = new Class[1];
			typeArgs[0] = String.class;
			Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod(
					"addAssetPath", typeArgs);
			valueArgs = new Object[1];
			valueArgs[0] = apkFile.getPath();
			// ִ��assetMag_addAssetPathMtd����
			assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);

			// �õ�Resources����ʵ����,�в�?
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

			// ��ȡapk�ļ�����?
			// AppInfo appInfoData = new AppInfo();
			if (info != null)
			{
				if (info.icon != 0)
				{// ͼƬ���ڣ����ȡ�����Ϣ
					Drawable icon = res.getDrawable(info.icon);// ͼ��
					// appInfoData.appIcon = (icon);
					infoData.setAppIcon(icon);
					// fileBean.setAppIcon(icon);
					// return icon;
				}
				if (info.labelRes != 0)
				{
					String name = (String) res.getText(info.labelRes);// ����
					infoData.setAppname(name);
				}
				else
				{
					infoData.setAppname(apkFile.getName());
					// String apkName=apkFile.getName();
					// appInfoData.setAppname(apkName.substring(0,apkName.lastIndexOf(".")));
				}
				infoData.setApppackage(info.packageName);// ����
				// appInfoData.setApppackage(pkgName);
				return infoData;
			}
			// PackageManager pm = ctx.getPackageManager();
			// PackageInfo packageInfo =
			// pm.getPackageArchiveInfo(apkPath,
			// PackageManager.GET_ACTIVITIES);
			// if (packageInfo != null) {
			// appInfoData.setAppversion(packageInfo.versionName);//�汾?
			// appInfoData.setAppversionCode(packageInfo.versionCode+"");//�汾?
			// }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * ��ȡ�ļ�apk��Ϣ ͼƬ
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
				return null;
			}
			// ����õ�pkgParserCls����ʵ����,�в�?
			Class<?> pkgParserCls = Class.forName(PATH_PackageParser);
			// ����õ�assetMagCls����ʵ����,�޲�
			Class<?> assetMagCls = Class.forName(PATH_AssetManager);

			Class<?>[] typeArgs =
			{ String.class };
			Constructor<?> pkgParserCt = pkgParserCls.getConstructor(typeArgs);
			Object[] valueArgs =
			{ apkFile.getPath() };
			Object pkgParser = pkgParserCt.newInstance(valueArgs);

			// ��pkgParserCls��õ�parsePackage����
			DisplayMetrics metrics = new DisplayMetrics();
			metrics.setToDefaults();// ���������ʾ�й�? ���ʹ��Ĭ��
			typeArgs = new Class<?>[]
			{ File.class, String.class, DisplayMetrics.class, int.class };
			Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
					"parsePackage", typeArgs);

			valueArgs = new Object[]
			{ apkFile, apkFile.getPath(), metrics, 0 };

			// ִ��pkgParser_parsePackageMtd��������?
			Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
					valueArgs);

			// �ӷ��صĶ���õ���Ϊ"applicationInfo"���ֶζ�?
			if (pkgParserPkg == null)
			{
				return null;
			}
			Field appInfoFld = pkgParserPkg.getClass().getDeclaredField(
					"applicationInfo");

			// �Ӷ�?pkgParserPkg"�õ��ֶ�"appInfoFld"��?
			if (appInfoFld.get(pkgParserPkg) == null)
			{
				return null;
			}
			ApplicationInfo info = (ApplicationInfo) appInfoFld
					.get(pkgParserPkg);

			Object assetMag = assetMagCls.newInstance();
			// ��assetMagCls��õ�addAssetPath����
			typeArgs = new Class[1];
			typeArgs[0] = String.class;
			Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod(
					"addAssetPath", typeArgs);
			valueArgs = new Object[1];
			valueArgs[0] = apkFile.getPath();
			// ִ��assetMag_addAssetPathMtd����
			assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);

			// �õ�Resources����ʵ����,�в�?
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

			// ��ȡapk�ļ�����?
			// AppInfo appInfoData = new AppInfo();
			if (info != null)
			{
				if (info.icon != 0)
				{// ͼƬ���ڣ����ȡ�����Ϣ
					Drawable icon = res.getDrawable(info.icon);// ͼ��
					// appInfoData.appIcon = (icon);
					// fileBean.setAppIcon(icon);
					return icon;
				}
				// if (info.labelRes != 0)
				// {
				// String name = (String) res.getText(info.labelRes);// ����
				// // appInfoData.setAppname(neme);
				// } else
				// {
				// String apkName = apkFile.getName();
				// //
				// appInfoData.setAppname(apkName.substring(0,apkName.lastIndexOf(".")));
				// }
				// String pkgName = info.packageName;// ����
				// appInfoData.setApppackage(pkgName);
			}
			// PackageManager pm = ctx.getPackageManager();
			// PackageInfo packageInfo =
			// pm.getPackageArchiveInfo(apkPath,
			// PackageManager.GET_ACTIVITIES);
			// if (packageInfo != null) {
			// appInfoData.setAppversion(packageInfo.versionName);//�汾?
			// appInfoData.setAppversionCode(packageInfo.versionCode+"");//�汾?
			// }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static Drawable geTApkIcon(Context context, String Path)
	{
		// δ��װ�ĳ���ͨ��apk�ļ���ȡicon
		String apkPath = Path; // apk �ļ�?��·?
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/*************************************************************************************
	 * ��ʾӦ������
	 **************************************************************************************/
	private static final String SCHEME = "package";
	/**
	 * ����ϵͳInstalledAppDetails����?��Extra����(����Android 2.1��֮ǰ��?
	 */
	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
	/**
	 * ����ϵͳInstalledAppDetails����?��Extra����(����Android 2.2)
	 */
	private static final String APP_PKG_NAME_22 = "pkg";
	/**
	 * InstalledAppDetails?����
	 */
	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
	/**
	 * InstalledAppDetails����
	 */
	private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

	/**
	 * ����ָ��������APP
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
		}
		catch (Exception e)
		{
			P.debug("error", e.getMessage());
			return false;
		}
	}

}