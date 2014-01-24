package com.kenny.file.bean;

import java.util.ArrayList;
import java.util.List;

public class AppGroupBean implements CharSequence
{
   private int ID = 0;
   private String title = "";// 词典名称
   private ArrayList<AppBean> items = new ArrayList<AppBean>();
   private String path;
   
   public String getPath()
   {
      return path;
   }
   
   public void setPath(String path)
   {
      this.path = path;
   }
   
   public int AddBean(AppBean bean)
   {
      items.add(bean);
      return 1;
   }
   
   public int AddAllDictBean(List<AppBean> beans)
   {
      items.addAll(beans);
      return 1;
   }
   
   public int ItemSize()
   {
      return items.size();
   }
   
   public AppBean get(int pos)
   {
      return items.get(pos);
   }
   
   public void Clear()
   {
      items.clear();
   }
   
   public ArrayList<AppBean> getItems()
   {
      return items;
   }
   
   public int getID()
   {
      return ID;
   }
   
   public void setID(String iD)
   {
      ID = Integer.valueOf(iD);
   }
   
   public void setID(int iD)
   {
      ID = iD;
   }
   
   public String getTitle()
   {
      return title + "(" + items.size() + ")";
   }
   
   public void setTitle(String title)
   {
      this.title = title;
   }
   
   
   public String toString()
   {
      // TODO Auto-generated method stub
      return title;
   }
   
   
   public char charAt(int index)
   {
      // TODO Auto-generated method stub
      return title.charAt(index);
   }
   
   
   public int length()
   {
      // TODO Auto-generated method stub
      return title.length();
   }
   
   
   public CharSequence subSequence(int start, int end)
   {
      // TODO Auto-generated method stub
      return title.subSequence(start, end);
   }
}
