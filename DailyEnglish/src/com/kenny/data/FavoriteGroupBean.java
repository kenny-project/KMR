package com.kenny.data;

import java.util.ArrayList;

//词库信息
public class FavoriteGroupBean implements CharSequence
{
	private int ID = 0;
    private String title = "";//词典名称
    private ArrayList<FavoriteBean> items=new ArrayList<FavoriteBean>();
    public int AddBean(FavoriteBean bean)
    {
    	items.add(bean);
    	return 1;
    }
    public int AddAllBean(ArrayList<FavoriteBean> beans)
    {
    	items.addAll(beans);
    	return 1;
    }
    public int ItemSize()
    {
    	return items.size();
    }
    public FavoriteBean get(int pos)
    {
    	return items.get(pos);
    }
    public void Clear()
    {
    	items.clear();
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
