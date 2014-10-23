package com.kenny.file.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.kenny.KFileManager.R;
import com.kenny.file.bean.FileBean;
import com.kenny.file.sort.FileNameSort;
import com.kenny.file.util.Const;

public class FolderManager
{
	private String mCurrentPath = ""; // 当前目录路径信息

	private List<FileBean> mFileList = null;
	private Context context = null;

	public void setContext(Context context)
	{
		this.context = context;
	}

	public FolderManager()
	{
		mFileList = new ArrayList<FileBean>();
	}

	/**
	 * 获取当前文件路径
	 * 
	 * @return
	 */
	public String getCurrentPath()
	{
		return mCurrentPath;
	}

	/**
	 * 退回上一层
	 */
	public boolean Back()
	{
		if (!mCurrentPath.equals(Const.Root))
		{
			String temp = new File(mCurrentPath).getParent();
			setFilePath(temp);
			return true;
		} else
		{
			Toast.makeText(
					context,
					context.getString(R.string.dlg_press_again_to_exit_the_program),
					Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	/**
	 * 刷新列表
	 */
	public void Refresh()
	{
		setFilePath(mCurrentPath);
	}

	/**
	 * true:显示隐藏文件 false:不显示隐藏文件
	 * 
	 * @param bHidden
	 */
	// public void setbHidden(boolean bHidden)
	// {
	// this.bHidden = bHidden;
	// }
	// public void setFilePath(String mCurrentPath)
	// {
	// setFilePath(mCurrentPath,Const.cmd_Local_List_Go);
	// }
	public void setFilePath(String mCurrentPath)
	{
		if(mCurrentPath==null||mCurrentPath.length()==0)
		{
			mCurrentPath=Const.getSDCard();
		}
		this.mCurrentPath = mCurrentPath;
		mFileList.clear();
		File mFile = new File(mCurrentPath);
		File[] mFiles = mFile.listFiles();// 遍历出该文件夹路径下的所有文件/文件夹
		// boolean bHidden=Theme.getShowHideFile();
		if (mFiles != null)
		{
			/* 将所有文件信息添加到集合中 */
			for (File mCurrentFile : mFiles)
			{
				// if (mCurrentFile.isHidden() && !bHidden)
				// {
				// continue;
				// }
				FileBean bean = new FileBean(mCurrentFile,
						mCurrentFile.getName(), mCurrentFile.getPath());
				bean.setDirectory(mCurrentFile.isDirectory());
				if (mCurrentFile.isDirectory())
				{
					File[] tempList = mCurrentFile.listFiles();;
					if (tempList != null)
					{
						int FileCount=0,FolderCount=0;
						for (File file : tempList)
						{
							if(file.isDirectory())
							{
								FolderCount++;
							}
							else
							{
								FileCount++;	
							}
						}
						bean.setItemFileCount(FileCount);
						bean.setItemFolderCount(FolderCount);//FileCount(FileCount);
					}
				
					if (mCurrentFile.canWrite())
					{
						mFileList.add(bean);
					}
				}
			}
			Collections.sort(mFileList, new FileNameSort());
		}
		if (!mCurrentPath.equals(Const.Root))
		{
			String back = mFile.getParent();
			File temp = new File(back);
			mFileList.add(0, new FileBean(temp, "..", back, true));
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
}
