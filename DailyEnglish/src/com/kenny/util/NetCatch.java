package com.kenny.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class NetCatch {
	
	public static void saveGZipStreamToFile(InputStream is, String fileName) throws IOException{
		File file = new File(Const.NET_DIRECTORY);
		if (!file.exists()){
			file.mkdir();
		}
		StreamToolBox.saveGZipStreamToFile(is, Const.NET_DIRECTORY + fileName);
	}
	
	public static boolean saveStreamToFile(InputStream is, String fileName) throws IOException{
		File file = new File(Const.NET_DIRECTORY);
		if (!file.exists()){
			file.mkdir();
		}
		return StreamToolBox.saveStreamToFile(is, Const.NET_DIRECTORY + MD5Calculator.calculateMD5(fileName));
		
	}
	
	public static void saveVoiceStreamToFile(InputStream is, String fileName) throws IOException{
		File file = new File(Const.VOICE_DIRECTORY);
		if (!file.exists()){
			file.mkdir();
		}
		StreamToolBox.saveStreamToFile(is, Const.VOICE_DIRECTORY + fileName);
		
	}
	
	public static void saveStringToFile(String str, String fileName) throws IOException{
		File file = new File(Const.NET_DIRECTORY);
		if (!file.exists()){
			file.mkdir();
		}
		StreamToolBox.saveStringToFile(str, Const.NET_DIRECTORY + fileName);
	}
	
	public static String getNetContent(String fileName) throws FileNotFoundException, IOException{
		String content = "";
		fileName = Const.NET_DIRECTORY + MD5Calculator.calculateMD5(fileName);
		File file = new File(fileName);
		if (file.exists()){
			content = StreamToolBox.loadStringFromStream(new FileInputStream(fileName));
		}
		return content;
	}
	
	public static void deleteVoiceCacheByWord(String word){
		File file = new File(Const.VOICE_DIRECTORY + MD5Calculator.calculateMD5(word) + ".p");
		if (file != null && file.exists()){
			file.delete();
		}
	}
	
	public static void clearCache(Context context){
		File file2 = context.getCacheDir();
		if (file2 != null && file2.listFiles() != null){
			for (File f : file2.listFiles()){
				if (f.isDirectory()){
					clearFile(f.getAbsolutePath());
					continue;
				}
				f.delete();
			}
		}
		if (Utils.getSDCardStatus() == false){
			return;
		}
		File file = new File(Const.NET_DIRECTORY);
		if (file != null && file.listFiles() != null){
			for (File f : file.listFiles()){
				if (f.isDirectory()){
					clearFile(f.getAbsolutePath());
					continue;
				}
				f.delete();
			}
		}
		File file1 = new File(Const.VOICE_DIRECTORY);
		if (file1 != null && file1.listFiles() != null){
			for (File f : file1.listFiles()){
				if (f.isDirectory()){
					clearFile(f.getAbsolutePath());
					continue;
				}
				f.delete();
			}
		}
		File file3 = new File(Const.LOGO_DIRECTORY);
		if (file3 != null && file3.listFiles() != null){
			for (File f : file3.listFiles()){
				if (f.isDirectory()){
					clearFile(f.getAbsolutePath());
					continue;
				}
				f.delete();
			}
		}
		
	}
	
	private static void clearFile(String path){
		File file = new File(path);
		if (file != null && file.listFiles() != null){
			for (File f : file.listFiles()){
				if (f.isDirectory()){
					clearFile(f.getAbsolutePath());
					continue;
				}
				f.delete();
			}
		}
	}
	
	public static void clearPageCache(){
		if (Utils.getSDCardStatus() == false){
			return;
		}
		File file = new File(Const.NET_DIRECTORY);
		if (file != null && file.listFiles() != null){
			for (File f : file.listFiles()){
				f.delete();
			}
		}
	}
	
}
