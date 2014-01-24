package com.kenny.data;

import com.kenny.file.Coder;
import com.kenny.util.Log;


//网络接收链接的Bean
public class StoreItemBean implements CharSequence
{
	private int ID = 0;
    private String title = "";
    private String desc = "";
    private String pubDate = "";// Sun, 16 May 2010 10:18:46 +0800
    private String url = "";
    private String num="";
	public String getNum()
	{
		return num;
	}
	public void setNum(String num)
	{
		this.num = num;
	}
	private int icount;// 服务器上该类型内容的总个数
    private int order; // 排序方式 1:正序 2:倒序
    private int type = 0;// 内容类型 1:笑话  2:小说
    private String imageUrl;//分类的图标

    public String getUrl()
	{
    	String temp=url.substring(url.lastIndexOf("?")+1);
    	String[] params=temp.split("&");
    	String act=null,mod=null;
    	for(String param :params)
    	{
    		String[] field=param.split("=");
    		if(field[0].equals("act"))
    		{
    			act=field[1];
    		}
    		else     		if(field[0].equals("mod"))
    		{
    			mod=field[1];
    		}
    		
    	}
    	
    	
    	String k =Coder.Md5(mod,act);
    	Log.v("p", url+"&k="+k);
		return url+"&k="+k;
	}
	public void setUrl(String url)
	{
		this.url = url;
	}
    public String getImageUrl()
	{
		return imageUrl;
	}
	public void setImageUrl(String imageUrl)
	{
		this.imageUrl = imageUrl;
	}
	public int getType()
    {
        return type;
    }
    public void setType(int type)
    {
        this.type = type;
    }
    public void setType(String type)
    {
        this.type = Integer.valueOf(type);
    }
    public int getOrder()
    {
        return order;
    }
    public void setOrder(String order)
    {
        this.order = Integer.valueOf(order);
    }
    public void setOrder(int order)
    {
        this.order = order;
    }
    public int getCount()
    {
        return icount;
    }
    public void setCount(String count)
    {
        this.icount = Integer.valueOf(count);
    }
    public void setCount(int count)
    {
        this.icount = count;
    }
    public int getID()
    {
        return ID;
    }
    public void setID(int ID)
    {
        this.ID = ID;
    }
    public void setID(String ID)
    {
    	this.ID = Integer.valueOf(ID);
    }

    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public String getDesc()
    {
        return desc;
    }
    public void setDesc(String desc)
    {
        this.desc = desc;
    }
    public String getPubDate()
    {
        return pubDate;
    }
    public void setPubDate(String pubDate)
    {
        this.pubDate = pubDate;
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
