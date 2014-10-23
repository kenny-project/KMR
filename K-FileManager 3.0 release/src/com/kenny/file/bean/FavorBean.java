package com.kenny.file.bean;

public class FavorBean
{
   public FavorBean(String filename, int flags, String path, long size)
   {
      this.filename = filename;
      this.flags = flags;
      this.path = path;
      this.size = size;
   }
   
   public String filename;
   public int flags;
   public String path;
   public long size;
}
