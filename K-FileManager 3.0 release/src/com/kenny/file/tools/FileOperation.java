package com.kenny.file.tools;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileOperation 
{
   

   /**
    * 将文件名后面,后缀前面加apd.
    * */
   public static String fileNameAppend(String name, String apd){
   	int i = name.lastIndexOf(".");
   	if (i == -1 || i == 0)
   		return name + apd;
   	return name.substring(0, i) + apd + name.substring(i, name.length());
   }
   /**
    * 截取文件路径的最后文件名. 根目录返回 根目录 /, 如果文件名最后是/, 返回空字符串.
    * /  		--> /
    * /path 	--> /path
    * /path/1 	--> 1
    * /path/1/ --> ""
    */
   public static String getPathName(String path){
   	int index = path.lastIndexOf('/');
   	if (index == -1 || index == 0)
   		return path;
   	return path.substring(index + 1);
   }
   /**
    * 将文件路径后面,后缀前面加apd.
    * */
   public static String pathNameAppend(String path, String apd){
   	String name = getPathName(path);
   	int i = name.lastIndexOf(".");
   	if (i == -1 || i == 0)
   		return path + apd;
   	int l = path.lastIndexOf("/") + 1;
   	return path.substring(0, l + i) + apd + path.substring(l + i);
   }
	public static void copyFile(String oldPath, String newPath)
			throws IOException {
		int bytesum = 0;
		int byteread = 0;
		File oldfile = new File(oldPath);
		if (oldfile.exists()) {
			InputStream inStream = new FileInputStream(oldPath); // 读入原文件
			FileOutputStream fs = new FileOutputStream(newPath);
			byte[] buffer = new byte[4096];
			//int length;
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread; // 字节数 文件大小
				fs.write(buffer, 0, byteread);
			}
			inStream.close();
		}
	}
	 
	
	/**
	 * 递归获取文件夹里所有文件的总大小
	 * BUG: 不能分辨链接文件
	 * */
	public static long getDirectorySize(File f) throws IOException{
		long size = 0;
		File flist[] = f.listFiles();
		if (flist == null)
			return f.length();
		int length = flist.length;
		for (int i = 0; i < length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getDirectorySize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}
	/**
	 * BUG: 不能分辨链接文件
	 * */
	public static long getDirectorySize(String fp) throws IOException{
		long size = 0;
		File f = new File(fp);
		File flist[] = f.listFiles();
		if (flist == null)
			return f.length();
		int length = flist.length;
		for (int i = 0; i < length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getDirectorySize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}
}
