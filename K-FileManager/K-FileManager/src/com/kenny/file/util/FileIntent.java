package com.kenny.file.util;

import java.io.File;

import android.content.Intent;
import android.net.Uri;

//Intent it = getPdfFileIntent("file:///android_asset/helphelp.pdf");  

//下面这些都OK  

//Intent it = getHtmlFileIntent("/mnt/sdcard/tutorial.html");//SD卡主目录  

//Intent it = getHtmlFileIntent("/sdcard/tutorial.html");//SD卡主目录,这样也可以  

//Intent it = getHtmlFileIntent("/system/etc/tutorial.html");//系统内部的etc目录  

//Intent it = getPdfFileIntent("/system/etc/helphelp.pdf");  

//Intent it = getWordFileIntent("/system/etc/help.doc");  

//Intent it = getExcelFileIntent("/mnt/sdcard/Book1.xls")  

//Intent it = getPptFileIntent("/mnt/sdcard/download/Android_PPT.ppt");//SD卡的download目录下  

//Intent it = getVideoFileIntent("/mnt/sdcard/ice.avi");  

//Intent it = getAudioFileIntent("/mnt/sdcard/ren.mp3");  

//Intent it = getImageFileIntent("/mnt/sdcard/images/001041580.jpg");  

//Intent it = getTextFileIntent("/mnt/sdcard/hello.txt",false);  

public class FileIntent
{
   
   // android获取一个用于打开HTML文件的intent
   
   public static Intent getApkFileIntent(String param)
   {
      
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setDataAndType(Uri.parse("file://" + param),
	  "application/vnd.android.package-archive");
      return intent;
   }
   
   // android获取一个用于打开HTML文件的intent
   
   public static Intent getHtmlFileIntent(String param)
   {
      
      Uri uri = Uri.parse(param).buildUpon()
	  .encodedAuthority("com.android.htmlfileprovider").scheme("content")
	  .encodedPath(param).build();
      
      Intent intent = new Intent("android.intent.action.VIEW");
      
      intent.setDataAndType(uri, "text/html");
      
      return intent;
      
   }
   
   // android获取一个用于打开图片文件的intent
   
   public static Intent getImageFileIntent(String param)
   
   {
      
      Intent intent = new Intent("android.intent.action.VIEW");
      
      intent.addCategory("android.intent.category.DEFAULT");
      
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      
      Uri uri = Uri.fromFile(new File(param));
      
      intent.setDataAndType(uri, "image/*");
      
      return intent;
      
   }
   
   
// android获取一个用于打开PDF文件的intent
   
   public static Intent getZipFileIntent(String param)
   
   {
      
      Intent intent = new Intent("android.intent.action.VIEW");
      
      intent.addCategory("android.intent.category.DEFAULT");
      
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      
      Uri uri = Uri.fromFile(new File(param));
      
      intent.setDataAndType(uri, "application/zip");
      
      return intent;
      
   }
   // android获取一个用于打开RAR文件的intent
   
   public static Intent getRarFileIntent(String param)
   
   {
      
      Intent intent = new Intent("android.intent.action.VIEW");
      
      intent.addCategory("android.intent.category.DEFAULT");
      
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      
      Uri uri = Uri.fromFile(new File(param));
      
      intent.setDataAndType(uri, "application/rar");
      
      return intent;
      
   }
   
   // android获取一个用于打开PDF文件的intent
   
   public static Intent getPdfFileIntent(String param)
   
   {
      
      Intent intent = new Intent("android.intent.action.VIEW");
      
      intent.addCategory("android.intent.category.DEFAULT");
      
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      
      Uri uri = Uri.fromFile(new File(param));
      
      intent.setDataAndType(uri, "application/pdf");
      
      return intent;
      
   }
   
   // android获取一个用于打开文本文件的intent
   
   public static Intent getTextFileIntent(String param, boolean paramBoolean)
   
   {
      
      Intent intent = new Intent("android.intent.action.VIEW");
      
      intent.addCategory("android.intent.category.DEFAULT");
      
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      
      if (paramBoolean)
      
      {
         
         Uri uri1 = Uri.parse(param);
         
         intent.setDataAndType(uri1, "text/plain");
         
      }
      
      else
      
      {
         
         Uri uri2 = Uri.fromFile(new File(param));
         
         intent.setDataAndType(uri2, "text/plain");
         
      }
      
      return intent;
      
   }
   
   // android获取一个用于打开音频文件的intent
   
   public static Intent getAudioFileIntent(String param)
   
   {
      
      Intent intent = new Intent("android.intent.action.VIEW");
      
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      
      intent.putExtra("oneshot", 0);
      
      intent.putExtra("configchange", 0);
      
      Uri uri = Uri.fromFile(new File(param));
      
      intent.setDataAndType(uri, "audio/*");
      
      return intent;
      
   }
   
   // android获取一个用于打开视频文件的intent
   
   public static Intent getVideoFileIntent(String param)
   
   {
      
      Intent intent = new Intent("android.intent.action.VIEW");
      
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      
      intent.putExtra("oneshot", 0);
      
      intent.putExtra("configchange", 0);
      
      Uri uri = Uri.fromFile(new File(param));
      
      intent.setDataAndType(uri, "video/*");
      
      return intent;
      
   }
   
   // android获取一个用于打开CHM文件的intent
   
   public static Intent getChmFileIntent(String param)
   
   {
      
      Intent intent = new Intent("android.intent.action.VIEW");
      
      intent.addCategory("android.intent.category.DEFAULT");
      
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      
      Uri uri = Uri.fromFile(new File(param));
      
      intent.setDataAndType(uri, "application/x-chm");
      
      return intent;
      
   }
   
   // android获取一个用于打开Word文件的intent
   
   public static Intent getWordFileIntent(String param)
   
   {
      
      Intent intent = new Intent("android.intent.action.VIEW");
      
      intent.addCategory("android.intent.category.DEFAULT");
      
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      
      Uri uri = Uri.fromFile(new File(param));
      
      intent.setDataAndType(uri, "application/msword");
      
      return intent;
      
   }
   
   // android获取一个用于打开Excel文件的intent
   
   public static Intent getExcelFileIntent(String param)
   
   {
      
      Intent intent = new Intent("android.intent.action.VIEW");
      
      intent.addCategory("android.intent.category.DEFAULT");
      
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      
      Uri uri = Uri.fromFile(new File(param));
      
      intent.setDataAndType(uri, "application/vnd.ms-excel");
      
      return intent;
      
   }
   
   // android获取一个用于打开PPT文件的intent
   
   public static Intent getPptFileIntent(String param)
   
   {
      
      Intent intent = new Intent("android.intent.action.VIEW");
      
      intent.addCategory("android.intent.category.DEFAULT");
      
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      
      Uri uri = Uri.fromFile(new File(param));
      
      intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
      
      return intent;
      
   }
}
