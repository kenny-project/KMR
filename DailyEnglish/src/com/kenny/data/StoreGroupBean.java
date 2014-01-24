package com.kenny.data;

//网络接收链接的Bean
public class StoreGroupBean implements CharSequence
{
    private String title = "";
    private String desc = "";
    private String link = ""; // 数据地址
    private String pubDate = "";// Sun, 16 May 2010 10:18:46 +0800
    private int ID = 0;
    private int visible;
    public int getVisible()
    {
        return visible;
    }
    public void setVisible(String visible)
    {
        this.visible =Integer.valueOf(visible);
    }
    public void setVisible(int visible)
    {
        this.visible = visible;
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
    public void setLink(String link)
    {
        this.link = link;
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
    public String getLink()
    {
        return link;
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
