package com.kenny.file.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Event.ExitEvent;
import com.kenny.file.bean.FileBean;
import com.kenny.file.sort.FileModifiedSort;
import com.kenny.file.sort.FileNameSort;
import com.kenny.file.sort.FileSizeSort;
import com.kenny.file.sort.FileSort;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.tools.SaveData;
import com.kenny.file.tools.T;

public class FileManager
{
	private String mCurrentPath = ""; // 当前目录路径信息
	private List<FileBean> mFileList = null;
	private static FileManager m_LocalManage = new FileManager();
	private Context context = null;

	private INotifyDataSetChanged m_notifyData = null;
	private boolean bRoot = false; // 是否到Root根目录

	// private int mFileType = 0;// 显示类型

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
			return SaveData.Read(context, Const.strDefaultPath, Const.SDCard);
		} else
		{
			return mCurrentPath;
		}
	}

	private FileManager()
	{

		mFileList = new ArrayList<FileBean>();
	}

	/**
	 * 获得当前类实倒，单态模式
	 * 
	 * @return
	 */
	public static FileManager GetHandler()
	{
		return m_LocalManage;
	}

	/**
	 * 退回上一层
	 */
	public void Back()
	{
		if (!mCurrentPath.equals(Const.Root))
		{
			String temp = new File(mCurrentPath).getParent();
			setFilePath(temp, null, Const.cmd_Local_List_Go);
			bRoot = false;
		} else
		{
			if (!bRoot)
			{
				bRoot = true;
				Toast.makeText(
						context,
						context.getString(R.string.dlg_press_again_to_exit_the_program),
						Toast.LENGTH_SHORT).show();
			} else
			{
				SysEng.getInstance().addEvent(
						new ExitEvent((Activity) context, false));
			}
		}
	}

	/**
	 * 刷新列表
	 */
	public void Refresh()
	{
		setFilePath(mCurrentPath, null, Const.cmd_Local_List_Refresh);
	}

	/**
	 * true:显示隐藏文件 false:不显示隐藏文件
	 * 
	 * @param bHidden
	 */
	public void setFilePath(String mPath)
	{
		setFilePath(mPath, Const.cmd_Local_List_Go);
	}

	public void setFilePath(String mPath, int type)
	{
		setFilePath(mPath, null, type);
	}

	public void setFilePath(String mCurrentPath, String SearchValue)
	{
		setFilePath(mCurrentPath, SearchValue, Const.cmd_Local_List_Go);
	}

	File mCurrentFile;
	int mFolderCount,mFileCount;
	public File getCurrentFile()
	{
		return mCurrentFile;
	}
	public String getFileStatus()
	{
		return String.valueOf(getFolderCount())+"个文件夹,"+String.valueOf(getFileCount())+"个文件";
	}
	public String getSpaceStatus()
	{
		return String.valueOf(T.FileSizeToString(mCurrentFile.getTotalSpace()-mCurrentFile.getFreeSpace()))+"/"+String.valueOf(T.FileSizeToString(mCurrentFile.getTotalSpace()));
	}
	/**
	 * 获取文件夹个数
	 * @return
	 */
	public int getFolderCount()
	{
		return mFolderCount;
	}
	/**
	 * 获取文件个数
	 * @return
	 */
	public int getFileCount()
	{
		return mFileCount;
	}
	public void setFilePath(String mCurrentPath, String SearchValue, int type)
	{
		if (mCurrentPath.length() <= 0)
		{
			setFilePath(Const.SDCard, SearchValue, type);
			return;
		}
		mFolderCount=0;
		mFileCount=0;
		this.mCurrentPath = mCurrentPath;
		mFileList.clear();
		mCurrentFile = new File(mCurrentPath);
		File[] mFiles = mCurrentFile.listFiles();// 遍历出该文件夹路径下的所有文件/文件夹
		boolean bHidden = Theme.getShowHideFile();
		/* 将所有文件信息添加到集合中 */
		for (File mCurrentFile : mFiles)
		{
			if (mCurrentFile.isHidden() && !bHidden)
			{
				continue;
			}
			if (SearchValue != null && SearchValue.length() > 0)
			{
				if (mCurrentFile.getName().indexOf(SearchValue) == -1)
				{
					continue;
				}
			}
			FileBean bean = new FileBean(mCurrentFile, mCurrentFile.getName(),
					mCurrentFile.getPath());
			bean.setDirectory(mCurrentFile.isDirectory());
			if (mCurrentFile.isDirectory())
			{
				String[] temp = mCurrentFile.list();
				if (temp != null)
				{
					bean.setItemCount(temp.length);
				}
				mFolderCount++;
			} else
			{

				mFileCount++;
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
		if (Theme.getSortMode() < 10)
		{ // 倒序
			ArrayList<FileBean> TempFileList = new ArrayList<FileBean>();
			for (FileBean fileBean : mFileList)
			{
				TempFileList.add(0, fileBean);
			}
			mFileList.clear();
			mFileList.addAll(TempFileList);
		}

		if (!mCurrentPath.equals(Const.Root))
		{
			String back = mCurrentFile.getParent();
			File temp = new File(back);
			mFileList.add(0, new FileBean(temp, "..", back, true));
		}
		if (m_notifyData != null)
		{
			m_notifyData.NotifyDataSetChanged(type, this);
		}
	}

	/**
	 * 获得当前路径的文件列表
	 * 
	 * @return
	 */
	public List<FileBean> getFileList()
	{
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
	public int CreateFile(String path, String filenName, String Extension)
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
					+ filenName.trim() + "." + Extension);
			if (mCreateFile.exists())
			{
				Toast.makeText(
						context,
						context.getString(R.string.msg_file_already_exists)
								+ "!", Toast.LENGTH_SHORT).show();
				return 2;
			}
			mCreateFile.createNewFile();
			Refresh();
			return 1;
			// initFileListInfo(mCurrentFilePath);wmh更新
		} catch (Exception e)
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
	public int CreateFolder(String path, String folerName)
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
			Refresh();
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
