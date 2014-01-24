package com.kenny.file.util;

import android.app.Activity;
import android.content.Context;

import com.kenny.file.tools.SaveData;

/**
 * 文件打开限制
 * @author WangMinghui
 * 
 */
public class Config
{
	private static boolean bOpenDefPicFile = true;
	private static boolean bOpenDefAudioFile = true;
	private static boolean bOpenDefTxtFile = true;
	private static boolean bOpenDefZipFile = true;
	
	private static final String strOpenDefPicFile = "OpenDefPicFile";
	private static final String strOpenDefAudioFile = "OpenDefAudioFile";
	private static final String strOpenDefTxtFile = "OpenDefTxtFile";
	private static final String strOpenDefZipFile = "OpenDefZipFile";
	
	public static boolean isOpenDefPicFile()
	{
		
		return bOpenDefPicFile;
	}

	public static void setbOpenDefPicFile(boolean mOpenDefPicFile)
	{
		bOpenDefPicFile = mOpenDefPicFile;
	}

	public static boolean isOpenDefAudioFile()
	{
		return bOpenDefAudioFile;
	}

	public static void setOpenDefAudioFile(boolean mOpenDefAudioFile)
	{
		bOpenDefAudioFile = mOpenDefAudioFile;
	}

	public static boolean isOpenDefTxtFile()
	{
		return bOpenDefTxtFile;
	}

	public static void setOpenDefTxtFile(boolean fileDefTxtFile)
	{
		bOpenDefTxtFile = fileDefTxtFile;
	}

	public static boolean isbOpenDefZipFile()
	{
		return bOpenDefZipFile;
	}

	public static void setbOpenDefZipFile(boolean bOpenDefZipFile)
	{
		Config.bOpenDefZipFile = bOpenDefZipFile;
	}

	/**
	 * 初始化
	 * 
	 * @param inActivity
	 */
	public static void Init(Activity inActivity)
	{
		setOpenDefTxtFile(SaveData.Read(inActivity, strOpenDefTxtFile, true));
		setOpenDefAudioFile(SaveData
				.Read(inActivity, strOpenDefAudioFile, true));// 输入自动化
		setbOpenDefPicFile(SaveData.Read(inActivity, strOpenDefPicFile, true)); // 显示隐含文件
		setbOpenDefZipFile(SaveData.Read(inActivity, strOpenDefZipFile, true)); // 显示隐含文件
	}

	public static void Save(Context inActivity)
	{
		SaveData.Write(inActivity, strOpenDefTxtFile, isOpenDefTxtFile());
		SaveData.Write(inActivity, strOpenDefAudioFile, isOpenDefAudioFile());// 旋转
		SaveData.Write(inActivity, strOpenDefPicFile, isOpenDefPicFile());// 工具栏
		SaveData.Write(inActivity, strOpenDefZipFile, isbOpenDefZipFile());// 工具栏
	}
}
