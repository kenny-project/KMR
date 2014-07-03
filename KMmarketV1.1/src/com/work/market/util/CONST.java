package com.work.market.util;

import android.os.Environment;

public class CONST
{
	public static final String mApkTempPaths = Environment
			.getExternalStorageDirectory() + "/baifen/temp/apk/";
	public static final String mZipTempPaths = Environment
			.getExternalStorageDirectory().getAbsolutePath();
	public static final String AppPath = Environment
			.getExternalStorageDirectory() + "/baifen/";
	public static final String DownLoad = AppPath + "dowsload/";
	public static final String ONPATH = Environment
			.getExternalStorageDirectory().toString() + "/baifen/img/";
}
