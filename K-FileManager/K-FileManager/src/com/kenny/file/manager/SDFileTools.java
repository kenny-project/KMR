package com.kenny.file.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.framework.log.P;
import com.kenny.KFileManager.R;
import com.kenny.file.bean.FileBean;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.sort.FileModifiedSort;
import com.kenny.file.sort.FileNameSort;
import com.kenny.file.sort.FileSizeSort;
import com.kenny.file.sort.FileSort;
import com.kenny.file.tools.SaveData;
import com.kenny.file.util.Const;
import com.kenny.file.util.Theme;

public class SDFileTools 
{
	private String mCurrentPath = ""; // 当前目录路径信息
	
	private static SDFileTools m_LocalManage = new SDFileTools();
	private Context context = null;
	private INotifyDataSetChanged m_notifyData = null;
	private String mRootPath = ""; // 当前根结点

	public void setRootPath(String path)
	{
		mRootPath=new File(path).getAbsolutePath();
	}

	public void setNotifyData(INotifyDataSetChanged m_notifyData)
	{
		this.m_notifyData = m_notifyData;
	}

	public void setContext(Context context)
	{
		this.context = context;
	}

	public String getCurrentPath()
	{
		if (mCurrentPath.length() == 0)
		{
			return SaveData.Read(context, Const.strDefaultPath,
					Const.getSDCard());
		} else
		{
			return mCurrentPath;
		}
	}
	/**
	 * 刷新列表
	 */
	public void Refresh()
	{
		setFilePath(mCurrentPath, Const.cmd_Local_List_Refresh);
	}

	/**
	 * true:显示隐藏文件 false:不显示隐藏文件
	 * 
	 * @param bHidden
	 */
	public static List<FileBean> setFilePath(String mCurrentPath)
	{
		return setFilePath(mCurrentPath, Const.cmd_Local_List_Go);
	}

	public static List<FileBean> setFilePath(String mCurrentPath, int type)
	{
		List<FileBean> mFileList = new ArrayList<FileBean>();;
		if (mCurrentPath.length() <= 0)
		{
			setFilePath(Const.getSDCard(), type);
			return mFileList;
		}
		P.v("setFilePath:start");
		mFileList.clear();
		File mFile = new File(mCurrentPath);
		File[] mFiles = mFile.listFiles();// 遍历出该文件夹路径下的所有文件/文件夹
		boolean bHidden = Theme.getShowHideFile();
		if (mFiles != null)
		{
			/* 将所有文件信息添加到集合中 */
			for (File mCurrentFile : mFiles)
			{
				if (mCurrentFile.isHidden() && !bHidden)
				{
					continue;
				}
				FileBean bean = new FileBean(mCurrentFile,
						mCurrentFile.getName(), mCurrentFile.getPath());
				bean.setDirectory(mCurrentFile.isDirectory());
				if (mCurrentFile.isDirectory())
				{
					File[] tempList = mCurrentFile.listFiles();
					if (tempList != null)
					{
						int FileCount = 0, FolderCount = 0;
						for (File file : tempList)
						{
							if (file.isDirectory())
							{
								FolderCount++;
							} else
							{
								FileCount++;
							}
						}
						bean.setItemFileCount(FileCount);
						bean.setItemFolderCount(FolderCount);
					}
				} else
				{
					bean.setLength(mCurrentFile.length());
				}
				mCurrentFile.canWrite();
				mFileList.add(bean);
			}
			switch (Theme.getSortMode() % 10)
			{
			case 0:
				Collections.sort(mFileList, new FileSort());
				break;
			case 1:
				Collections.sort(mFileList, new FileNameSort());
				break;
			case 2:
				Collections.sort(mFileList, new FileSizeSort());
				break;
			case 3:
				Collections.sort(mFileList, new FileModifiedSort());
				break;
			}
			// if (Theme.getSortMode() < 10)
			// {// 倒序
			// ArrayList<FileBean> TempFileList = new ArrayList<FileBean>();
			// for (FileBean fileBean : mFileList)
			// {
			// TempFileList.add(0, fileBean);
			// }
			// mFileList.clear();
			// mFileList.addAll(TempFileList);
			// }
		}
		P.v("setFilePath:end");
		return mFileList;
	}

	/**
	 * 创建文件
	 * 
	 * @param path
	 * @param filenName
	 * @param Extension
	 * @return
	 */
	public static int CreateFile(Context context,String path, String filenName, String Ext,INotifyDataSetChanged notif)
	{
		try
		{
			if (filenName == null || filenName.length() <= 0)
			{
				Toast.makeText(context,
						context.getString(R.string.msg_file_name_no_empty),
						Toast.LENGTH_SHORT).show();
				return 0;
			}
			File mCreateFile = new File(path + java.io.File.separator
					+ filenName.trim() + "." + Ext);
			if (mCreateFile.exists())
			{
				Toast.makeText(
						context,
						context.getString(R.string.msg_file_already_exists)
								+ "!", Toast.LENGTH_SHORT).show();
				return 2;
			}
			mCreateFile.createNewFile();
			if(notif!=null)
				notif.NotifyDataSetChanged(Const.cmd_CreateFileEvent_Finish, null);
			return 1;
			// initFileListInfo(mCurrentFilePath);wmh更新
		}
		catch (Exception e)
		{
			String msg = context.getString(R.string.msg_failed_create)
					+ "!error:" + e.getMessage();
			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
			return -1;
		}
	}

	/**
	 * 创建文件夹
	 * 
	 * @param path
	 * @param filenName
	 * @param Extension
	 * @return
	 */
	public static int CreateFolder(Context context,String path, String folerName,INotifyDataSetChanged notif)
	{
		if (folerName == null || folerName.length() <= 0)
		{
			Toast.makeText(context,
					context.getString(R.string.msg_folder_name_no_empty),
					Toast.LENGTH_SHORT).show();
			return 0;
		}
		File mCreateFile = new File(path + java.io.File.separator
				+ folerName.trim());

		if (mCreateFile.exists())
		{
			Toast.makeText(
					context,
					context.getString(R.string.msg_folder_already_exists) + "!",
					Toast.LENGTH_SHORT).show();
			return 2;
		}
		if (mCreateFile.mkdirs())
		{
			if(notif!=null)
				notif.NotifyDataSetChanged(Const.cmd_CreateFolderEvent_Finish, null);
			return 1;
		} else
		{
			Toast.makeText(context,
					context.getString(R.string.msg_failed_create) + "!",
					Toast.LENGTH_SHORT).show();
		}
		return 0;
	}
}
