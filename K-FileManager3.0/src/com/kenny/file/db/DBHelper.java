package com.kenny.file.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 建立一个数据库帮助类
 */
public class DBHelper extends SQLiteOpenHelper
{
   // download.db-->数据库名
   // public DBHelper(Context context)
   // {
   // super(context, "DBInfo.db", null, 3);
   // }
   protected Context context;
   public DBHelper(Context context, String name, CursorFactory factory,
         int version)
   {
      super(context, name, factory, version);
      this.context=context;
   }
   
   /**
    * 在download.db数据库下创建一个download_info表存储下载信息
    */
   
   public static final String[] appInfoFilds = new String[]
   { "id", "appName", "packageName", "versionName", "versionCode", "sourceDir",
         "flags", "codeSize", "dataSize", "cacheSize" };
   public static final String favTable = "favorites_info";
   public static final String appTable = "App_info";
   public static final String[] favFilds = new String[]
   { "id", "flags", "path", "count", "filename" };
   
   public static final String hisTable = "his_info";
   
   @Override
   public void onCreate(SQLiteDatabase db)
   {
      db.execSQL("create table " + appTable + "(" + appInfoFilds[0]
	  + " integer PRIMARY KEY AUTOINCREMENT, " + appInfoFilds[1]
	  + " char, " + " " + appInfoFilds[2] + " char, " + appInfoFilds[3]
	  + " char, " + appInfoFilds[4] + " char," + "" + appInfoFilds[5]
	  + " char, " + appInfoFilds[6] + " integer, " + appInfoFilds[7]
	  + " integer, " + appInfoFilds[8] + " integer, " + appInfoFilds[9]
	  + " integer)");
      
      db.execSQL("create table " + favTable + "(" + favFilds[0]
	  + " integer PRIMARY KEY AUTOINCREMENT, " + favFilds[1]
	  + " integer, " + " " + favFilds[2] + " char," + favFilds[3]
	  + " integer," + favFilds[4] + " char" + ")");
      
      db.execSQL("create table " + hisTable + "(" + favFilds[0]
	  + " integer PRIMARY KEY AUTOINCREMENT, " + favFilds[1]
	  + " integer, " + " " + favFilds[2] + " char," + favFilds[3]
	  + " integer," + favFilds[4] + " char" + ")");
      // db.execSQL("create table " + hisTable + "(" + hisFilds[0]
      // + " integer PRIMARY KEY AUTOINCREMENT, " + hisFilds[1]
      // + " char, " + " " + hisFilds[2] + " char" + ")");
   }
   
   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
   {
      if (tabIsExist(db, appTable))
      {
         db.execSQL("DROP TABLE " + appTable);
      }
      if (tabIsExist(db, favTable))
      {
         db.execSQL("DROP TABLE " + favTable);
      }
      if (tabIsExist(db, hisTable))
      {
         db.execSQL("DROP TABLE " + hisTable);
      }
      onCreate(db);
   }
   
   public boolean tabIsExist(SQLiteDatabase db, String tabName)
   {
      boolean result = false;
      if (tabName == null) { return false; }
      Cursor cursor = null;
      try
      {
         String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"
	     + tabName.trim() + "' ";
         cursor = db.rawQuery(sql, null);
         if (cursor.moveToNext())
         {
	  int count = cursor.getInt(0);
	  if (count > 0)
	  {
	     result = true;
	  }
         }
      }
      catch (Exception e)
      {
         // TODO: handle exception
      }
      return result;
   }
}