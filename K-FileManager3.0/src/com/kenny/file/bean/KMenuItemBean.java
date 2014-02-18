package com.kenny.file.bean;
//词库信息
public class KMenuItemBean  implements CharSequence
{
	private int ID = 0;
	private String title = "";// 词典名称
	private int ImgID;
	private Object obj;
	public KMenuItemBean(int iD,String title,Object obj,int imgID)
	{
		ID = iD;
		this.title = title;
		this.obj=obj;
		ImgID = imgID;
	}
	
	public int getID()
	{
		return ID;
	}
	public void setID(int iD)
	{
		ID = iD;
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public int getImgID()
	{
		return ImgID;
	}
	public void setImgID(int imgID)
	{
		ImgID = imgID;
	}
	
	public Object getObj()
	{
		return obj;
	}

	public void setObj(Object obj)
	{
		this.obj = obj;
	}

	@Override
	public int length()
	{
		// TODO Auto-generated method stub
		return title.length();
	}
	@Override
	public char charAt(int index)
	{
		// TODO Auto-generated method stub
		return title.charAt(index);
	}
	@Override
	public CharSequence subSequence(int start, int end)
	{
		// TODO Auto-generated method stub
		return title.subSequence(start, end);
	}

}
