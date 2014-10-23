package com.kenny.file.util;

import java.io.File;

public class FT
{
   /** 获得MIME类型的方法 */
   public static String getMIMEType(File file)
   {
      String type = "";
      String fileName = file.getName();
      String fileEnds = getExName(fileName).toLowerCase();// 取出文件后缀名并转成小写
      if (fileEnds.equals("swf"))
      {
         type = "flash/*";// 系统将列出所有可能打开音频文件的程序选择器
      }
      else if (fileEnds.equals("txt") || fileEnds.equals("ini")
	  || fileEnds.equals("xml") || fileEnds.equals("html")
	  || fileEnds.equals("mht") || fileEnds.equals("htm")
	  || fileEnds.equals("xml") || fileEnds.equals("log"))// 默认执行
      {
         type = "text/*";// 系统将列出所有可能打开音频文件的程序选择器
      }
      else if (fileEnds.equals("m4a") || fileEnds.equals("mp3")
	  || fileEnds.equals("mid") || fileEnds.equals("xmf")
	  || fileEnds.equals("ogg") || fileEnds.equals("wav"))
      {
         type = "audio/*";// 系统将列出所有可能打开音频文件的程序选择器
      }
      else if (fileEnds.equals("3gp") || fileEnds.equals("mp4"))
      {
         type = "video/*";// 系统将列出所有可能打开视频文件的程序选择器
      }
      else if (fileEnds.equals("jpg") || fileEnds.equals("gif")
	  || fileEnds.equals("png") || fileEnds.equals("jpeg")
	  || fileEnds.equals("bmp"))
      {
         type = "image/*";// 系统将列出所有可能打开图片文件的程序选择器
      }
      else
      {
         type = "*/*"; // 系统将列出所有可能打开该文件的程序选择器
      }
      return type;
   }
   
   /*
    * Java文件操作 获取文件扩展名
    * 
    * Created on: 2011-8-2 Author: blueeagle
    */
   public static String getExName(String filename)
   {
      if ((filename != null) && (filename.length() > 0))
      {
         int dot = filename.lastIndexOf('.');
         if ((dot > -1) && (dot < (filename.length() - 1))) { return filename
	     .substring(dot + 1).toLowerCase(); }
      }
      return filename.toLowerCase();
   }
   
   /*
    * Java文件操作 获取不带扩展名的文件名
    * 
    * Created on: 2011-8-2 Author: blueeagle
    */
   public static String getFileNameNoEx(String filename)
   {
      if ((filename != null) && (filename.length() > 0))
      {
         int dot = filename.lastIndexOf('.');
         if ((dot > -1) && (dot < (filename.length()))) { return filename
	     .substring(0, dot); }
      }
      return filename;
   }
}
