package com.kenny.file.bean;

import java.io.File;
import java.lang.ref.SoftReference;
import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.kenny.file.util.Res;

public class FileBean {
	private static DecimalFormat myformat = new DecimalFormat("#####0.00");
	protected String FileName; // 文件名
	private String fileEnds = null; // 文件扩展名
	private String FolderPath = null;// 文件夹路径
	protected String FilePath = ""; // 文件路径
	private boolean isBackUp; //
	protected File mFile;
	private boolean check = false; // 是否选择
	private int itemCount = 0; // 该文件夹下个数
	private boolean directory; // true:目录
	private SoftReference<Drawable> mFileIco = null; // 文件内容生成图标
	private Drawable mDefFileIco = null; // 文件模认图标
	protected Long length = 0l; // 文件大小
	private String mDesc;// 详细信息

	public FileBean() {
		directory = false;
		this.isBackUp = false;
	}

	public FileBean(File mFile, String fileName)// throws Exception
	{
		this.mFile = mFile;
		if (mFile != null) {
			directory = mFile.isDirectory();
			FilePath = mFile.getPath();
			length = mFile.length();
			if (fileName == null) {
				FileName = mFile.getName();
			} else {
				FileName = fileName;
			}
			this.isBackUp = false;
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
		this.isBackUp = isBackUp;
	}

	public void setBackUp(boolean isBackUp) {
		this.isBackUp = isBackUp;
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

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
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

	public void setLength(Long length) {
		this.length = length;
	}

	public boolean isChecked() {
		return check;
	}

	public void setChecked(boolean check) {
		this.check = check;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	public boolean isDirectory() {
		return directory;
	}

	public File getFile() {
		if (mFile == null) {
			mFile = new File(FilePath);
		}
		return mFile;
	}

	public boolean isBackUp() {
		return isBackUp;
	}

	public String getFileName() {
		return FileName;
	}

	public String getFileEnds() {
		if (fileEnds == null) {
			if (FilePath == null) {
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
		if (mDefFileIco == null) {
			mDefFileIco = Init(mContext);
		}
		return mDefFileIco;
	}

	public String getDesc() {
		if (mDesc != null) {
			return mDesc;
		} else {
			if (isDirectory()) {
				mDesc = getItemCount() + "个文件";
			} else {
				mDesc =getLength();
			}
		}
		return mDesc;
	}

	private Drawable Init(Context mContext) {
		Res mRes = Res.getInstance(mContext);
		if (isDirectory()) {
			mDesc = getItemCount() + "个文件";
			return mRes.getFolder();
		} else {
			mDesc =  getLength();//+"   文件类型:"+getFileEnds();
			return mRes.getDefFileIco(getFileEnds());
		}
	}

}
