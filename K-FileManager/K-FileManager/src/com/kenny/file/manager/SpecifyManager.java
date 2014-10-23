package com.kenny.file.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kenny.file.bean.FileBean;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.sort.FileModifiedSort;
import com.kenny.file.sort.FileNameSort;
import com.kenny.file.sort.FileSizeSort;
import com.kenny.file.sort.FileSort;
import com.kenny.file.util.Const;
import com.kenny.file.util.Theme;

public class SpecifyManager implements IManager
{
	private String mCurrentPath = ""; // 当前目录路径信息
	private List<FileBean> mFileList = null;
	// private Context context = null;
	private String RootPath;
	private INotifyDataSetChanged m_notifyData = null;

	public void setNotifyData(INotifyDataSetChanged m_notifyData) {
		this.m_notifyData = m_notifyData;
	}

	public SpecifyManager(String RootPath) {
		mFileList = new ArrayList<FileBean>();
		this.RootPath = RootPath;
		mCurrentPath = RootPath;
	}

	public String getCurrentPath() {
		return mCurrentPath;
	}

	/**
	 * 退回上一层 成功：true 失败:false
	 */
	public boolean Back() {
		if (mCurrentPath.length()>RootPath.length()) 
		{
			String temp = new File(mCurrentPath).getParent();
			setFilePath(temp, Const.cmd_Local_List_Go);
			return true;
		} 
		return false;
	}

	/**
	 * 刷新列表
	 */
	public void Refresh() {
		setFilePath(mCurrentPath, Const.cmd_Local_List_Refresh);
	}

	/**
	 * true:显示隐藏文件 false:不显示隐藏文件
	 * 
	 * @param bHidden
	 */
	public void setFilePath(String mCurrentPath) {
		setFilePath(mCurrentPath, Const.cmd_Local_List_Go);
	}

	public void setFilePath(String mCurrentPath, int type) {
		if (mCurrentPath!=null&&mCurrentPath.length() > 0)
		{
			this.mCurrentPath = mCurrentPath;
		}
		else
		{
			mCurrentPath=RootPath;
		}
		
		mFileList.clear();
		File mFile = new File(mCurrentPath);
		File[] mFiles = mFile.listFiles();// 遍历出该文件夹路径下的所有文件/文件夹
		boolean bHidden = Theme.getShowHideFile();
		if (mFiles != null) {
			/* 将所有文件信息添加到集合中 */
			for (File mCurrentFile : mFiles) {
				if (mCurrentFile.isHidden() && !bHidden) {
					continue;
				}
				FileBean bean = new FileBean(mCurrentFile,
						mCurrentFile.getName(), mCurrentFile.getPath());
				bean.setDirectory(mCurrentFile.isDirectory());
				if (mCurrentFile.isDirectory()) {
					File[] tempList = mCurrentFile.listFiles();
					if (tempList != null) {
						int FileCount = 0, FolderCount = 0;
						for (File file : tempList) {
							if (file.isDirectory()) {
								FolderCount++;
							} else {
								FileCount++;
							}
						}
						bean.setItemFileCount(FileCount);
						bean.setItemFolderCount(FolderCount);// FileCount(FileCount);
					}
				} else {
					bean.setLength(mCurrentFile.length());
				}
				mCurrentFile.canWrite();
				mFileList.add(bean);
			}
			switch (Theme.getSortMode() % 10) {
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
			if (Theme.getSortMode() < 10) {// 倒序
				ArrayList<FileBean> TempFileList = new ArrayList<FileBean>();
				for (FileBean fileBean : mFileList) {
					TempFileList.add(0, fileBean);
				}
				mFileList.clear();
				mFileList.addAll(TempFileList);
			}
		}
		if (mCurrentPath.length()>RootPath.length()) {
			String back = mFile.getParent();
			File temp = new File(back);
			mFileList.add(0, new FileBean(temp, "..", back, true));
		}
		if (m_notifyData != null) {
			m_notifyData.NotifyDataSetChanged(type, this);
		}
	}

	/**
	 * 获得当前路径的文件列表
	 * 
	 * @return
	 */
	public List<FileBean> getFileList() {
		return mFileList;
	}
}
