package com.kenny.file.bean;

public class FolderTypeBean
{
	public String title;
	public String path;
	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return path;
	}
	
	
}
