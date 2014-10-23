package com.kenny.file.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import com.kenny.file.tools.T;

public class FileTypeBean
{
   
   private int id = 0;
   private String img;
   private Drawable drawable=null;
   public Drawable getDrawable(Context con)
   {
      if(drawable==null	  )
      {
         return   drawable=T.DrawableAssetsFile(con, "filetype/"+img);
      }
      else
      {
      return drawable;
      }
   }

   public String getImg()
   {
      return img;
   }

   public void setImg(String img)
   {
      this.img = img;
   }

   private String ends;
    public String getEnds()
   {
      return ends;
   }
   
   public void setEnds(String ends)
   {
      this.ends = ends;
   }
   
   public int getId()
   {
      return id;
   }
   
   public void setId(int id)
   {
      this.id = id;
   }
}
