package com.work.market.bean;

import java.io.File;
import java.lang.ref.SoftReference;
import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class FileBean {
	private static DecimalFormat myformat = new DecimalFormat("#####0.00");
	protected String FileName; // 文件�?
	private String fileEnds = null; // 文件扩展�?
	private String FolderPath = null;// 文件夹路�?
	protected String FilePath = ""; // 文件路径
	protected File mFile;
	private boolean check = false; // 是否选择
	private int ItemFolderCount = 0; //文件夹下个数
	private int ItemFileCount = 0; // 文件下个�?
	public int getItemFileCount()
	{
		return ItemFileCount;
	}

	public void setItemFileCount(int itemFileCount)
	{
		ItemFileCount = itemFileCount;
	}

	private SoftReference<Drawable> mFileIco = null; // 文件内容生成图标
	private Drawable mDefFileIco = null; // 文件模认图标
	protected Long length = 0l; // 文件大小
	private String mDesc;// 详细信息

	public FileBean(File mFile, String fileName)// throws Exception
	{
		this.mFile = mFile;
		if (mFile != null) {
			FilePath = mFile.getPath();
			length = mFile.length();
			if (fileName == null) {
				FileName = mFile.getName();
			} else {
				FileName = fileName;
			}
		}
		// else
		// {
		// throw new Exception("The file can not be empty");
		// }
	}

	public FileBean(File mFile, String fileName, String filePath) {
		this(mFile, fileName, filePath, false);
	}

	public FileBean(File mFile, String fileName, String filePath,
			boolean isBackUp) {
		this.mFile = mFile;
		FilePath = filePath;
		FileName = fileName;
	}


	/**
	 * 文件是否存在
	 * 
	 * @return
	 */
	public boolean exists() {
		if (mFile != null) {
			return mFile.exists();
		} else {
			return false;
		}
	}

	public int getItemFolderCount() {
		return ItemFolderCount;
	}

	public void setItemFolderCount(int itemCount) {
		this.ItemFolderCount = itemCount;
	}

	private String szLength = null;

	public int getIntLength() {
		return length.intValue();
	}

	public Long getLongLength() {
		return length;
	}

	public String getLength() {
		if (szLength == null) {
			double totalsize = length;
			if (totalsize > 1024) {
				totalsize = totalsize / 1024.0;
				if (totalsize > 1024) {
					totalsize = totalsize / 1024.0;
					szLength = myformat.format(totalsize) + "M";
				} else {
					szLength = myformat.format(totalsize) + "K";
				}
			} else {
				if (totalsize == 0) {
					return "0.00B";
				} else {
					szLength = myformat.format(totalsize) + "B";
				}
			}
		}
		return szLength;
	}

	public String getDesc() {
		if (mDesc != null) {
			return mDesc;
		} else {
				mDesc =getLength();
		}
		return mDesc;
	}
	public void setLength(Long length) {
		this.length = length;
	}

	public boolean isChecked() {
		return check;
	}

	public void setChecked(boolean check) {
		this.check = check;
	}
	public File getFile() {
		if (mFile == null) {
			mFile = new File(FilePath);
		}
		return mFile;
	}


	public String getFileName() {
		return FileName;
	}

	public String getFileEnds() {
		if (fileEnds == null) 
		{
			if (FilePath == null) 
			{
				FilePath = FileName;
			}
			fileEnds = FilePath.substring(FilePath.lastIndexOf(".") + 1,
					FilePath.length()).toLowerCase();// 取出文件后缀名并转成小写
		}
		return fileEnds;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	/**
	 * 获得文件全部路径
	 * 
	 * @return
	 */
	public String getFolderPath() {

		if (FolderPath == null) {
			FolderPath = FilePath.substring(0, FilePath.lastIndexOf("/") + 1);// 取出文件后缀名并转成小写
		}
		return FolderPath;
	}

	public void setFolderPath(String folderPath) {
		FolderPath = folderPath;
	}

	/**
	 * 获得文件全部路径
	 * 
	 * @return
	 */
	public String getFilePath() {
		return FilePath;
	}

	public void setFilePath(String filePath) {
		if (filePath != null) {
			FilePath = filePath;
		}
	}

	public void setFileIco(SoftReference<Drawable> mFileIco) {
		this.mFileIco = mFileIco;
	}

	public Drawable getFileIco(Context mContext) {
		if (mFileIco != null) {
			Drawable temp = mFileIco.get();
			if (temp != null) {
				return temp;
			}
		}
		if (mDefFileIco == null) 
		{
			//mDefFileIco = Init(mContext);
		}
		return mDefFileIco;
	}
}
