package com.kenny.file.tools;

import java.io.File;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class MIMEType
{
   //
   private static HashMap<String, String> map = new HashMap<String, String>(64,
         1.0f);
   public static final HashMap<String, String> MimeTypeTab()
   {
      map.put("3gp", "video/3gpp");
      map.put("apk", "application/vnd.android.package-archive");
      map.put("asf", "video/x-ms-asf");
      map.put("avi", "video/x-msvideo");
      map.put("bin", "application/octet-stream");
      map.put("bmp", "image/bmp");
      map.put("ico", "image/ico");
      map.put("c", "text/plain");
      map.put("class", "application/octet-stream");
      map.put("conf", "text/plain");
      map.put("cpp", "text/plain");
      map.put("chm", "application/x-chm");
      map.put("doc", "application/msword");
      map.put("exe", "application/octet-stream");
      map.put("gif", "image/gif");
      map.put("gtar", "application/x-gtar");
      map.put("gz", "application/x-gzip");
      map.put("h", "text/plain");
      map.put("htm", "text/html");
      map.put("html", "text/html");
      map.put("jar", "application/java-archive");
      map.put("java", "text/plain");
      map.put("jpeg", "image/jpeg");
      map.put("jpg", "image/jpeg");
      map.put("js", "application/x-javascript");
      map.put("log", "text/plain");
      map.put("m3u", "audio/x-mpegurl");
      map.put("m4a", "audio/mp4a-latm");
      map.put("m4b", "audio/mp4a-latm");
      map.put("m4p", "audio/mp4a-latm");
      map.put("m4u", "video/vnd.mpegurl");
      map.put("m4v", "video/x-m4v");
      map.put("mov", "video/quicktime");
      map.put("mp2", "audio/x-mpeg");
      map.put("mp3", "audio/x-mpeg");
      map.put("mp4", "video/mp4");
      map.put("mpc", "application/vnd.mpohun.certificate");
      map.put("mpe", "video/mpeg");
      map.put("mpeg", "video/mpeg");
      map.put("mpg", "video/mpeg");
      map.put("mpg4", "video/mp4");
      map.put("mpga", "audio/mpeg");
      map.put("msg", "application/vnd.ms-outlook");
      map.put("ogg", "audio/ogg");
      map.put("pdf", "application/pdf");
      map.put("png", "image/png");
      map.put("pps", "application/vnd.ms-powerpoint");
      map.put("ppt", "application/vnd.ms-powerpoint");
      map.put("prop", "text/plain");
      map.put("rar", "application/x-rar-compressed");
      map.put("rc", "text/plain");
      map.put("rmvb", "video/*");
      map.put("rtf", "application/rtf");
      map.put("sh", "text/plain");
      map.put("tar", "application/x-tar");
      map.put("tgz", "application/x-compressed");
      map.put("txt", "text/plain");
      map.put("wav", "audio/x-wav");
      map.put("wma", "audio/x-ms-wma");
      map.put("wmv", "audio/x-ms-wmv");
      map.put("wps", "application/vnd.ms-works");
      map.put("xml", "text/plain");
      map.put("xls", "application/vnd.ms-excel");
      map.put("z", "application/x-compress");
      map.put("zip", "application/zip");
      map.put("", "*/*");
      return map;
   }
   
   /**
    * 
    * @param file
    */
   public static String getMIMEType(File file)
   {
      String type = null;// "*/*";
      String fName = file.getName();
      int dotIndex = fName.lastIndexOf(".");
      if (dotIndex <= 0) { return type; }
      String end = fName.substring(dotIndex + 1, fName.length()).toLowerCase();
      if (end == "") { return type; }
      // for (int i = 0; i < map.size(); i++)
      // {
      if (map.size() <= 0)
      {
         MIMEType.MimeTypeTab();
      }
      type = map.get(end);
      // }
      return type;
   }
   
   /**
    * 
    * @param file
    */
   public static void openFile(File file, Activity activity)
   {
      // Uri uri = Uri.parse("file://"+file.getAbsolutePath());
      MIMEType.MimeTypeTab();
      Intent intent = new Intent();
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.setAction(Intent.ACTION_VIEW);
      String type = MIMEType.getMIMEType(file);
      intent.setDataAndType(/* uri */Uri.fromFile(file), type);
      activity.startActivity(intent);
   }
}
