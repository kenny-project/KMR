package com.work.market.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SaveData
{
   private final static String ShareName = "KJokeConfig";
   
   /**
    * ��ȡ�����ļ��е�ĳ���ֶ�
    * */
   public static boolean Read(Context context, String filename,boolean def)
   {
      try
      {
         SharedPreferences sharedPreferences = context.getSharedPreferences(
	     ShareName, 0);
         // �õ�������"ITEM"�Ľӿ�����
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
    * �������ļ���ģʽ�洢
    * */
   public static void Write(Context context, String filename,
         long content)
   {
      try
      {
         Editor passfileEditor = context.getSharedPreferences(ShareName, 0)
	     .edit();
         passfileEditor.putLong(filename, content);
         passfileEditor.commit();// ί�У���������
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   /**
    * �������ļ���ģʽ�洢
    * */
   public static void Write(Context context, String filename,
         int content)
   {
      try
      {
         Editor passfileEditor = context.getSharedPreferences(ShareName, 0)
	     .edit();
         passfileEditor.putInt(filename, content);
         passfileEditor.commit();// ί�У���������
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   /**
    * �������ļ���ģʽ�洢
    * */
   public static void Write(Context context, String filename,
         boolean content)
   {
      try
      {
         Editor passfileEditor = context.getSharedPreferences(ShareName, 0)
	     .edit();
         passfileEditor.putBoolean(filename, content);
         passfileEditor.commit();// ί�У���������
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   /**
    * �������ļ���ģʽ�洢
    * */
   public static void Write(Context context, String filename,
         String content)
   {
      try
      {
         Editor passfileEditor = context.getSharedPreferences(ShareName, 0)
	     .edit();
         passfileEditor.putString(filename, content);
         passfileEditor.commit();// ί�У���������
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   /**
    * ��ȡ�����ļ��е�ĳ���ֶ�
    * */
   public static long Read(Context context, String filename,Long def)
   {
      try
      {
         SharedPreferences sharedPreferences = context.getSharedPreferences(
	     ShareName, 0);
         // �õ�������"ITEM"�Ľӿ�����
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
    * ��ȡ�����ļ��е�ĳ���ֶ�
    * */
   public static int Read(Context context, String filename,int def)
   {
      try
      {
         SharedPreferences sharedPreferences = context.getSharedPreferences(
	     ShareName, 0);
         // �õ�������"ITEM"�Ľӿ�����
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
    * ��ȡ�����ļ��е�ĳ���ֶ�
    * */
   public static String Read(Context context, String filename,
         String def)
   {
      try
      {
         SharedPreferences sharedPreferences = context.getSharedPreferences(
	     ShareName, 0);
         // �õ�������"ITEM"�Ľӿ�����
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
