package com.kenny.file.util;

import java.io.File;

public class FT
{
//	{".3gp", "video/3gpp"},
//	　　{".apk", "application/vnd.android.package-archive"},
//	　　{".asf", "video/x-ms-asf"},
//	　　{".avi", "video/x-msvideo"},
//	　　{".bin", "application/octet-stream"},
//	　　{".bmp", "image/bmp"},
//	　　{".c", "text/plain"},
//	　　{".class", "application/octet-stream"},
//	　　{".conf", "text/plain"},
//	　　{".cpp", "text/plain"},
//	　　{".doc", "application/msword"},
//	　　{".exe", "application/octet-stream"},
//	　　{".gif", "image/gif"},
//	　　{".gtar", "application/x-gtar"},
//	　　{".gz", "application/x-gzip"},
//	　　{".h", "text/plain"},
//	　　{".htm", "text/html"},
//	　　{".html", "text/html"},
//	　　{".jar", "application/java-archive"},
//	　　{".java", "text/plain"},
//	　　{".jpeg", "image/jpeg"},
//	　　{".jpg", "image/jpeg"},
//	　　{".js", "application/x-javascript"},
//	　　{".log", "text/plain"},
//	　　{".m3u", "audio/x-mpegurl"},
//	　　{".m4a", "audio/mp4a-latm"},
//	　　{".m4b", "audio/mp4a-latm"},
//	　　{".m4p", "audio/mp4a-latm"},
//	　　{".m4u", "video/vnd.mpegurl"},
//	　　{".m4v", "video/x-m4v"},
//	　　{".mov", "video/quicktime"},
//	　　{".mp2", "audio/x-mpeg"},
//	　　{".mp3", "audio/x-mpeg"},
//	　　{".mp4", "video/mp4"},
//	　　{".mpc", "application/vnd.mpohun.certificate"},
//	　　{".mpe", "video/mpeg"},
//	　　{".mpeg", "video/mpeg"},
//	　　{".mpg", "video/mpeg"},
//	       {".mpg4", "video/mp4"},
//	　　{".mpga", "audio/mpeg"},
//	　　{".msg", "application/vnd.ms-outlook"},
//	　　{".ogg", "audio/ogg"},
//	　　{".pdf", "application/pdf"},
//	　　{".png", "image/png"},
//	　　{".pps", "application/vnd.ms-powerpoint"},
//	　　{".ppt", "application/vnd.ms-powerpoint"},
//	　　{".prop", "text/plain"},
//	　　{".rar", "application/x-rar-compressed"},
//	　　{".rc", "text/plain"},
//	　　{".rmvb", "audio/x-pn-realaudio"},
//	　　{".rtf", "application/rtf"},
//	　　{".sh", "text/plain"},
//	　　{".tar", "application/x-tar"},
//	　　{".tgz", "application/x-compressed"},
//	　　{".txt", "text/plain"},
//	　　{".wav", "audio/x-wav"},
//	　　{".wma", "audio/x-ms-wma"},
//	　　{".wmv", "audio/x-ms-wmv"},
//	　　{".wps", "application/vnd.ms-works"},
//	　　//{".xml", "text/xml"},
//	　　{".xml", "text/plain"},
//	　　{".z", "application/x-compress"},
//	　　{".zip", "application/zip"},
//	　　{"", "*/*"}
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
