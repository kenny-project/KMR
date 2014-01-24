package com.kenny.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.kenny.util.Const;
import com.kenny.util.Log;
import com.kenny.util.T;

public class QueueDownloader extends Thread {
	private ArrayList<String> downList;
	private String outputPath;

	public QueueDownloader(ArrayList<String> list, String path) {
		this.downList = list;
		this.outputPath = path;

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		for (String url : downList) {
			loadImageFromUrl(url);
		}
	}

	protected void loadImageFromUrl(String imageUrl) {
		String path = Const.RECOMMENDATION_ICON_DIRECTORY;
		String szFileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
		File file;
		path += szFileName;
		file = new File(path);
		if (file.exists()) {
			 return ;
		}
		try {
			InputStream imageStream = new URL(imageUrl).openStream();
			Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
			WriteImageFile(imageUrl, bitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// 初始化运行程序所需要的文件
	public boolean WriteImageFile(String url, Bitmap bitmap) {
		if (!T.checkSDCard())
			return false;
		String path = Const.RECOMMENDATION_ICON_DIRECTORY;
		String szFileName = url.substring(url.lastIndexOf("/") + 1);
		File file;
		try {
			new File(path).mkdirs();
			path += szFileName;
			file = new File(path);
			file.createNewFile();
			// Finally compress the bitmap, saving to the file previously
			// created
			bitmap.compress(CompressFormat.PNG, 100, new FileOutputStream(file));
			// copyFile(is,path);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.v("chenjg", "url is " + url);
			file = new File(path);
			file.delete();
			return false;
		}
	}
	
	public String URL2FileEnd(String url)
	  {
	    int pos = url.lastIndexOf(".");
	    if ( pos > 0 ) { return url.substring(pos); }
	    return "";
	  }
}
