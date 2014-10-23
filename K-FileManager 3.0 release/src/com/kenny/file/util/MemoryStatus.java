package com.kenny.file.util;

import android.app.Activity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.framework.log.P;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class MemoryStatus
{
	/**
	 * 获取android当前可用内存大小
	 * 
	 * @param act
	 * @return
	 */
	public static Long getAvailMemory(Activity act)
	{// 获取android当前可用内存大小

		ActivityManager am = (ActivityManager) act
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; 当前系统的可用内存
		return mi.availMem/1024/1024;// 将获取的内存大小规格化
	}

	/**
	 * 获得总内存大小
	 * 
	 * @return
	 */
	public static long getTotalMemory()
	{
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;

		try
		{
			FileReader localFileReader = new FileReader(str1);

			BufferedReader localBufferedReader = new BufferedReader(
					localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

			arrayOfString = str2.split("//s+");
			for (String num : arrayOfString)
			{
				P.v("def", num + "/t");
			}
			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
			localBufferedReader.close();

		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return initial_memory;// Byte转换为KB或者MB，内存大小规格化
	}

	public static void readSystem()
	{
		File root = Environment.getRootDirectory();
		StatFs sf = new StatFs(root.getPath());
		long blockSize = sf.getBlockSize();
		long blockCount = sf.getBlockCount();
		long availCount = sf.getAvailableBlocks();
		Log.d("def", "block大小:" + blockSize + ",block数目:" + blockCount + ",总大小:"
				+ blockSize * blockCount / 1024 + "KB");
		Log.d("def", "可用的block数目：:" + availCount + ",可用大小:" + availCount
				* blockSize / 1024 + "KB");
	}
}
