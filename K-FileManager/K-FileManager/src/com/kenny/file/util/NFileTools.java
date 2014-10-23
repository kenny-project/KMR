package com.kenny.file.util;


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

    public native int deleteFiles(String folderPath);
    public native int getFileSizes(String folderPath);
    
    static {
    	System.loadLibrary("FileTools");
    }
}
