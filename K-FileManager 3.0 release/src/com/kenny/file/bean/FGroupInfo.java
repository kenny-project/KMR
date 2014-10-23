package com.kenny.file.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.kenny.file.tools.T;

public class FGroupInfo
{
   
   private int id = 0;
   private String szLogoFileName;
   private String title;
   private String desc;
   private int count = 0;
   private int minsize = 0;
   public long length;
   
   public int getMinSize()
   {
      return minsize;
   }
   
   public void setMinSize(int minsize)
   {
      this.minsize = minsize;
   }
   
   private long size = 0;
   private String ends;
   
   public long getSize()
   {
      return size;
   }
   
   public void setSize(long size)
   {
      this.size = size;
   }
   
   public String getEnds()
   {
      return ends;
   }
   
   private String[] ArrayEnds = null;
   
   public String[] getArrayEnd()
   {
      if (ArrayEnds == null)
      {
         ArrayEnds = getEnds().split("\\|");
      }
      return ArrayEnds;
   }
   
   public void setEnds(String ends)
   {
      this.ends = ends;
   }
   
   public String getDesc()
   {
      return desc;
   }
   
   public void setDesc(String desc)
   {
      this.desc = desc;
   }
   
   public int getId()
   {
      return id;
   }
   
   public void setId(int id)
   {
      this.id = id;
   }
   
   private Bitmap imLogo = null;
   
   public Bitmap getLogo(Context context)
   {
      if (imLogo == null)
      {
         byte[] data = T.ReadResourceAssetsFile(context, szLogoFileName);
         imLogo = BitmapFactory.decodeByteArray(data, 0, data.length);
      }
      return imLogo;
   }
   
   public void setLogo(String szLogoFileName)
   {
      this.szLogoFileName = szLogoFileName;
   }
   
   public String getTitle()
   {
      return title;
   }
   
   public void setTitle(String title)
   {
      this.title = title;
   }
   
   public int getCount()
   {
      return count;
   }
   
   public void setCount(int count)
   {
      if (count < 0) count = 0;
      this.count = count;
   }
   
   public void AddCount()
   {
      count++;
   }
   
   public String toString()
   {
      return "<item id=\"" + String.valueOf(id) + "\" title=\"" + title
	  + "\" icon=\"" + szLogoFileName + "\" count=\"" + count + "\"/>";
   }
}
