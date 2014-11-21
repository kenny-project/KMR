package com.kenny.file.util;

import com.kenny.file.bean.FileDetailsBean;


//NDK相应的接口
public class NFileTools
{
	static NFileTools mFileTools;
	
	public static NFileTools GetInstance(){
		if(mFileTools == null){
			mFileTools = new NFileTools();
		}
		return mFileTools;
	}

	
	public static native boolean isDirectory(String folderPath);
    public static native int deleteFiles(String folderPath);
    public static native long getFileSize(String folderPath);
    public static native FileDetailsBean getFileSizes(String folderPath);
    
    static {
    	System.loadLibrary("FileTools");
    }
}
