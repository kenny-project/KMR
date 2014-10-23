package com.kenny.file.bean;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.kenny.file.tools.T;
import com.kenny.file.util.SDFile;

public class FileTypeBean {

	private int id = 0;
	private String img;
	private Drawable drawable = null;
	private String title;
	private String path;
	@Override
	public String toString()
	{
	return "   <item ends=\""+ends+"\" title=\""+title+"\"  path=\""+path+"\" img=\""+img+"\"/>";
	}
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	public void Clear()
	{
		drawable=null;
	}
	public Drawable getDrawable(Context con) 
	{
		if (drawable == null) 
		{
			if(path.length()>0)
			{
				File file=new File(path);
				if(file.exists())
				{
					Bitmap temp = SDFile.ReadFileToBitmap(con, path, 108);
					return drawable = new BitmapDrawable(temp);
				}
			}
			return drawable=T.DrawableAssetsFile(con, "filetype/"+img);
			//return drawable = T.DrawableLocalFile(con, "filetype/" + img);
		} else {
			return drawable;
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	private String ends;

	public String getEnds() {
		return ends;
	}

	public void setEnds(String ends) {
		this.ends = ends;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
