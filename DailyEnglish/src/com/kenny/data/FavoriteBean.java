package com.kenny.data;

import android.text.format.DateFormat;


//词库信息
public class FavoriteBean implements CharSequence
{
	private int ID = 0;
    private String title = "";//词典名称
    private String strUrl;
    private Long date;
    private String strDate;
    private String gname;
    public String getGname()
	{
		return gname;
	}
	public void setGname(String gname)
	{
		this.gname = gname;
	}
	public Long getDate()
	{
		return date;
	}
    public String getStrDate()
	{
    	return strDate;
	}
	public void setDate(Long date)
	{
		strDate = DateFormat.format("yyyy-MM-dd", date).toString();
		this.date = date;
	}
	public int getID()
	{
		return ID;
	}
	public void setID(String iD)
	{
		ID =Integer.valueOf(iD);
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
	
	public String getStrUrl()
	{
		return strUrl;
	}
	public void setStrUrl(String strUrl)
	{
		this.strUrl = strUrl;
	}
	@Override
    public String toString()
    {
        // TODO Auto-generated method stub
        return title;
    }
    @Override
    public char charAt(int index)
    {
        // TODO Auto-generated method stub
        return title.charAt(index);
    }
    @Override
    public int length()
    {
        // TODO Auto-generated method stub
        return title.length();
    }
    @Override
    public CharSequence subSequence(int start, int end)
    {
        // TODO Auto-generated method stub
        return title.subSequence(start, end);
    }
}
