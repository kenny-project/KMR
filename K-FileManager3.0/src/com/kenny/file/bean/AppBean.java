package com.kenny.file.bean;

import java.io.File;
import java.text.DecimalFormat;

import com.kenny.file.tools.ApkTools;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class AppBean extends FileBean
{

	private long codeSize = 0;
	private long dataSize = 0;
	private long cacheSize = 0;
	private Drawable mAppIco = null; // 文件图标
	private int id = 0;
	/**
	 * 获得应用程序路径
	 */
	// private String sourceDir = ""; // 获得应用程序路径
	/**
	 * 应用程序名称
	 */
	private String appName = "";
	/**
	 * 应用包名
	 */
	private String packageName = "";
	/**
	 * 应用版本名称
	 */
	private String versionName = "";

	/**
	 * 应用版本号
	 */
	private int versionCode = 0;

	public int getVersionCode()
	{
		return versionCode;
	}

	public void setVersionCode(int versionCode)
	{
		this.versionCode = versionCode;
	}

	public String getPackageName()
	{
		return packageName;
	}

	public void setPackageName(String packageName)
	{
		this.packageName = packageName;
	}

	public String getVersionName()
	{
		try
		{
			if (versionName.length() > 0)
			{
				return versionName;
			} else
			{
				return getDesc();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}

	public void setVersionName(String versionName)
	{
		if (versionName == null)
		{
			this.versionName = "";
		} else
		{
			this.versionName = versionName;
		}
	}

	/**
	 * 标记 系统应用,用户应用
	 */
	private int flags;
	private String szflags = "";
	/**
	 * 文件总大小
	 */
	private String szTotalsize = null; //
	/**
	 * 是否被选择
	 */
	private boolean check = false; // 是否选择

	public Drawable getAppIco()
	{
		return mAppIco;
	}

	public void setAppIco(Drawable mAppIco)
	{
		this.mAppIco = mAppIco;
	}

	public AppBean()
	{
		super(null, null);

	}

	public AppBean(String sourceDir, String appName)
	{

		super(new File(sourceDir), appName);
		this.appName = appName;
		setDirectory(false);

	}

	public AppBean(Context context, int id, String appName, String packageName,
			String versionName, int versionCode, String sourceDir, int flags,
			int codeSize, int dataSize, int cacheSize)
	{
		super(new File(sourceDir), appName);
		this.id = id;
//		try
//		{
//			ApplicationInfo appinfo = ApkTools.getApplicationInfo(context, sourceDir);
//			this.appName = appinfo.name;
//		} catch (Exception e)
//		{
//			// TODO Auto-generated catch block
//			this.appName = appName;
//			e.printStackTrace();
//		}
		this.appName = appName;
		this.packageName = packageName;
		this.versionName = versionName;
		this.versionCode = versionCode;
		this.FilePath = sourceDir;
		this.flags = flags;
		this.codeSize = codeSize;
		this.dataSize = dataSize;
		this.cacheSize = cacheSize;
	}

	private static DecimalFormat myformat = new DecimalFormat("#####0.00");

	public String getAppName()
	{
		return appName;
	}

	public void setAppName(String appName)
	{
		this.appName = appName;
	}

	public int getId()
	{
		return id;
	}

	public String getSTotalsize()
	{
		if (szTotalsize == null)
		{
			double totalsize = getTotalsize();
			if (totalsize > 1024)
			{
				totalsize = totalsize / 1024.0;
				if (totalsize > 1024)
				{
					totalsize = totalsize / 1024.0;
					szTotalsize = myformat.format(totalsize) + "M";
				} else
				{
					szTotalsize = myformat.format(totalsize) + "K";
				}
			} else
			{
				if (totalsize == 0)
				{
					return "未知";
				} else
				{
					szTotalsize = myformat.format(totalsize) + "B";
				}
			}
		}
		return szTotalsize;
	}

	public boolean isChecked()
	{
		return check;
	}

	public void setChecked(boolean check)
	{
		this.check = check;
	}

	public long getTotalsize()
	{
		return cacheSize + dataSize + codeSize;
	}

	public long getCodeSize()
	{
		return codeSize;
	}

	public void setCodeSize(long codeSize)
	{
		this.codeSize = codeSize;
	}

	public long getDataSize()
	{
		return dataSize;
	}

	public void setDataSize(long dataSize)
	{
		this.dataSize = dataSize;
	}

	public long getCacheSize()
	{
		return cacheSize;
	}

	public void setCacheSize(long cacheSize)
	{
		this.cacheSize = cacheSize;
	}

	public String getSFlag()
	{
		return szflags;
	}

	public void setFlags(int flags)
	{
		this.flags = flags;
		switch (flags)
		{
		case ApplicationInfo.FLAG_SYSTEM:
			szflags = "系统应用";
			break;
		case ApplicationInfo.FLAG_ALLOW_CLEAR_USER_DATA:
			szflags = "用户应用";
			break;
		}
		return;
	}

	public int getFlags()
	{
		return flags;
	}
}
