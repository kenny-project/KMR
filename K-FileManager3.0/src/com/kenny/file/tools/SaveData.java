package com.kenny.file.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SaveData
{
   private final static String ShareName = "KJokeConfig";
   
   /**
    * 读取配置文件中的某个字段
    * */
   public static boolean Read(Context context, String filename,boolean def)
   {
      try
      {
         SharedPreferences sharedPreferences = context.getSharedPreferences(
	     ShareName, 0);
         // 得到共享区"ITEM"的接口引用
         boolean show = sharedPreferences.getBoolean(filename,def);
         return show;
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return false;
   }
   
   /**
    * 用配置文件的模式存储
    * */
   public static void Write(Context context, String filename,
         long content)
   {
      try
      {
         Editor passfileEditor = context.getSharedPreferences(ShareName, 0)
	     .edit();
         passfileEditor.putLong(filename, content);
         passfileEditor.commit();// 委托，存入数据
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   /**
    * 用配置文件的模式存储
    * */
   public static void Write(Context context, String filename,
         int content)
   {
      try
      {
         Editor passfileEditor = context.getSharedPreferences(ShareName, 0)
	     .edit();
         passfileEditor.putInt(filename, content);
         passfileEditor.commit();// 委托，存入数据
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   /**
    * 用配置文件的模式存储
    * */
   public static void Write(Context context, String filename,
         boolean content)
   {
      try
      {
         Editor passfileEditor = context.getSharedPreferences(ShareName, 0)
	     .edit();
         passfileEditor.putBoolean(filename, content);
         passfileEditor.commit();// 委托，存入数据
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   /**
    * 用配置文件的模式存储
    * */
   public static void Write(Context context, String filename,
         String content)
   {
      try
      {
         Editor passfileEditor = context.getSharedPreferences(ShareName, 0)
	     .edit();
         passfileEditor.putString(filename, content);
         passfileEditor.commit();// 委托，存入数据
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   /**
    * 读取配置文件中的某个字段
    * */
   public static long Read(Context context, String filename,Long def)
   {
      try
      {
         SharedPreferences sharedPreferences = context.getSharedPreferences(
	     ShareName, 0);
         // 得到共享区"ITEM"的接口引用
         Long show = sharedPreferences.getLong(filename, def);
         return show;
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return -1;
   }
   
   /**
    * 读取配置文件中的某个字段
    * */
   public static int Read(Context context, String filename,int def)
   {
      try
      {
         SharedPreferences sharedPreferences = context.getSharedPreferences(
	     ShareName, 0);
         // 得到共享区"ITEM"的接口引用
         int show = sharedPreferences.getInt(filename, def);
         return show;
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return -1;
   }
   /**
    * 读取配置文件中的某个字段
    * */
   public static String Read(Context context, String filename,
         String def)
   {
      try
      {
         SharedPreferences sharedPreferences = context.getSharedPreferences(
	     ShareName, 0);
         // 得到共享区"ITEM"的接口引用
         String show = sharedPreferences.getString(filename, def);
         return show;
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      return "";
   }
   

}
