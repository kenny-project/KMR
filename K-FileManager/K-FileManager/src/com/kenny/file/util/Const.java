package com.kenny.file.util;

import java.io.File;

public class Const
{
	public static final String consumer_key = "xcpR3jd2qOXuBm5w";// 快盘操作
	public static final String consumer_secret = "aOzWOcoesds7OOGA";// 快盘操作

	public static final int cmd_LoadSDFile_Start = 1000;
	public static final int cmd_LoadSDFile_Init = cmd_LoadSDFile_Start + 1001;
	public static final int cmd_LoadSDFile_Run = cmd_LoadSDFile_Start + 1002;
	public static final int cmd_LoadSDFile_Error = cmd_LoadSDFile_Start + 1004;
	public static final int cmd_LoadSDFile_Finish = cmd_LoadSDFile_Start + 1003;
	public static final int cmd_LoadSDFile_State = cmd_LoadSDFile_Start + 1005;
	public static final int cmd_LoadSDFile_Change = cmd_LoadSDFile_Start + 1020;
	public static final int cmd_LoadSDFile_Changeed = cmd_LoadSDFile_Start + 1021;// 有结果变化
	public static final int cmd_OpenFile_Dialog_Result = cmd_LoadSDFile_Start + 1022;// 返回结果

	public static final int cmd_KuaiPan_LS = cmd_LoadSDFile_Start + 1006;
	public static final int cmd_KuaiPan_LS_Error = cmd_LoadSDFile_Start + 1007;
	public static final int cmd_KuaiPan_LS_Error_NoNetWork = cmd_LoadSDFile_Start + 1008;
	public static final int cmd_KuaiPan_OAuth_Error = cmd_LoadSDFile_Start + 1009;

	public static final int cmd_KuaiPan_upload_Error = cmd_LoadSDFile_Start + 1;
	public static final int cmd_KuaiPan_upload_Finish = cmd_LoadSDFile_Start + 2;// 上传成功
	public static final int cmd_KuaiPan_uploadfile_error = cmd_LoadSDFile_Start + 3;// 上传文件失败
	public static final int cmd_KuaiPan_AccountInfo = cmd_LoadSDFile_Start + 6;
	public static final int cmdTextEncodeDialog = 1001;

	public static final int cmd_DelFileEvent_Finish = cmd_LoadSDFile_Start + 2001;
	public static final int cmd_CutFileEvent_Finish = cmd_LoadSDFile_Start + 2002;
	public static final int cmd_CreateFileEvent_Finish = cmd_LoadSDFile_Start + 2003;
	public static final int cmd_CreateFolderEvent_Finish = cmd_LoadSDFile_Start + 2004;
	public static final int cmd_RenameFileEvent_Finish = cmd_LoadSDFile_Start + 2005;
	public static final int cmd_ZipFileEvent_Finish = cmd_LoadSDFile_Start + 2006;
	public static final int cmd_PalseFileEvent_Finish = cmd_LoadSDFile_Start + 2007;

	public static final int cmd_APP_UnInstallEvent_Finish = cmd_LoadSDFile_Start + 4001;// 卸载应用成功

	public static final int cmd_Local_ListSort_Finish = cmd_LoadSDFile_Start + 3001;// 文件显示排序完成

	public static final int cmd_Local_List_Refresh = cmd_LoadSDFile_Start + 3002;// 刷新列表

	public static final int cmd_Local_List_Go = cmd_LoadSDFile_Start + 3003;// 获取新列表

	public static final int cmd_Local_List_Selected = cmd_LoadSDFile_Start + 3004;// 选择列表
	public static final int cmd_Local_List_UnSelected = cmd_LoadSDFile_Start + 3005;// 不选择

	public static final String AppName = "K-FileManager";
	public static final String Root = File.separator;

	private static String SDCard = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath();
	public static String szAppPath = SDCard + File.separator + AppName
			+ File.separator;
	public static String szDownLoadPath = SDCard + File.separator + "Download"
			+ File.separator;
	public static String szBackupPath = szAppPath + "Backup" + File.separator;
	public static String szRecyclePath = szAppPath + "Recycle" + File.separator;
	public static String szAppTempPath = szAppPath + "Temp" + File.separator;

	public static String szZipPath = szAppTempPath + "Zip" + File.separator;
	public static String szKuaiPanPath = szAppTempPath + "kuaipan";
	public static String szBaiduYunPath = szAppTempPath + "BaiduYun";
	// +
																	// File.separator;

	// public static String szMusic=szAppPath+"Music"+File.separator;
	// public static String szMusic=szAppPath+"Music"+File.separator;
	// public static String szMusic=szAppPath+"Music"+File.separator;
	// public static String szMusic=szAppPath+"Music"+File.separator;
	// public static String szMusic=szAppPath+"Music"+File.separator;
	// public static String szMusic=szAppPath+"Music"+File.separator;

	public static String getSDCard()
	{
		return SDCard;
	}

	public static void setSDCard(String sdcard)
	{
		SDCard = sdcard;
		szAppPath = SDCard + File.separator + "K-FileManager" + File.separator;
		szDownLoadPath = SDCard + File.separator + "Download" + File.separator;
		szBackupPath = szAppPath + "Backup" + File.separator;
		szRecyclePath = szAppPath + "Recycle" + File.separator;
		szZipPath = szAppPath + "Temp" + File.separator + "Zip"
				+ File.separator;
		szKuaiPanPath = szAppPath + "Temp" + File.separator + "kuaipan";// +
																		// File.separator;
	}

	/**
	 * 设置默认SD卡的路径
	 */
	public static final String strDefSDRootPath = "DefaultSDRootPath";// 设置默认SD卡的路径
	public static final String strDefaultPath = "DefaultPath";// 默认打开路径
	// public static String strLastPathEnable = "LastPathEnable";//最后一次打开的路径
	// public static String strLastPath = "LastPath";//最后一次打开的路径
	public static final String strToolsVisible = "strToolsVisible";// 工具栏是否显示
	public static final String strTabVisible = "strTabVisible"; // 显示TAB栏
	public static final String strShowHideFile = "strShowHideFile"; // 显示掩藏文件
	public static final String strSensorEnable = "AutoSensor";// 动态切换屏幕

	public static final String strEditKuaiPanDialogValue = "AutoSensor";// 昵称名称
}
