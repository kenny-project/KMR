package com.kenny.file.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.framework.log.P;
import com.kenny.file.bean.AppBean;
import com.kenny.file.bean.FGroupInfo;
import com.kenny.file.bean.FavorBean;
import com.kenny.file.bean.FavorFileBean;
import com.kenny.file.bean.FileBean;

/**
 * 
 * 一个业务类
 */
public class Dao extends DBHelper
{
   // private final static Object _readLock = new Object();
   private String TAG = "Dao";
   private final static Object _readLock = new Object();
   private static final String mDBName = "DBInfo.db";
   private static final int mDBVersion = 7;
   private static Dao m_dao=null; 
   private Dao(Context context)
   {
      super(context, mDBName, null, mDBVersion);
   }
   public static Dao getInstance(Context context)
   {
      if(m_dao==null)
      {
         m_dao=new Dao(context);
      }
      return m_dao;
   }
   /**
    * 查看数据库中数据总数
    */
   public int FavoritesCount(String flags)
   {
      synchronized (_readLock)
      {
         SQLiteDatabase database = this.getReadableDatabase();
         String sql = "select count(*)  from favorites_info where flags=?";
         Cursor cursor = database.rawQuery(sql, new String[]
         { flags });
         cursor.moveToFirst();
         int count = cursor.getInt(0);
         cursor.close();
         this.close();
         return count;
      }
   }
   
   /**
    * 查看数据库中数据总数
    */
   public Long FavoritesSize(String flags)
   {
      synchronized (_readLock)
      {
         SQLiteDatabase database = this.getReadableDatabase();
         String sql = "select sum(" + DBHelper.favFilds[3]
	     + ")  from favorites_info where flags=?";
         Cursor cursor = database.rawQuery(sql, new String[]
         { flags });
         cursor.moveToFirst();
         Long count = cursor.getLong(0);
         cursor.close();
         return count;
      }
   }
   
   /**
    * 查看数据库中是否有数据
    */
   public boolean isHasInfors(String flags)
   {
      synchronized (_readLock)
      {
         SQLiteDatabase database = this.getReadableDatabase();
         String sql = "select count(*)  from App_info where flags=?";
         Cursor cursor = database.rawQuery(sql, new String[]
         { flags });
         cursor.moveToFirst();
         int count = cursor.getInt(0);
         cursor.close();
         return count == 0;
      }
   }
   
   /**
    * 保存 下载的具体信息
    */
   public void saveInfos(List<AppBean> infos)
   {
      synchronized (_readLock)
      {
         SQLiteDatabase database = this.getWritableDatabase();
         String sql = "";
         String fields = "";
         String values = "";
         for (int i = 1; i < DBHelper.appInfoFilds.length - 1; i++)
         {
	  fields += DBHelper.appInfoFilds[i] + ",";
	  values += "?,";
         }
         fields += DBHelper.appInfoFilds[DBHelper.appInfoFilds.length - 1];
         values += "?";
         sql = "insert into App_info(" + fields + ") values (" + values + ")";
         for (AppBean info : infos)
         {
	  
	  Object[] bindArgs =
	  { info.getAppName(), info.getPackageName(), info.getVersionName(),
	        info.getVersionCode(), info.getFilePath(), info.getFlags(),
	        info.getCodeSize(), info.getDataSize(), info.getCacheSize() };
	  database.execSQL(sql, bindArgs);
         }
      }
   }
   
   /**
    * 保存 下载的具体信息
    */
   public void InsertFavorites(List<FavorBean> favorItems)
   {
      synchronized (_readLock)
      {
         SQLiteDatabase database = this.getWritableDatabase();
         String sql = "";
         sql = "insert into " + DBHelper.favTable + "(" + DBHelper.favFilds[1]
	     + "," + DBHelper.favFilds[2] + "," + DBHelper.favFilds[3] + ","
	     + DBHelper.favFilds[4] + ") values (?,?,?,?)";
         for (FavorBean favorItem : favorItems)
         {
	  Object[] bindArgs =
	  { favorItem.flags, favorItem.path, favorItem.size,
	        favorItem.filename };
	  database.execSQL(sql, bindArgs);
         }
      }
   }
   
   
   /**
    * 保存 下载的具体信息
    */
   public void InsertFavorites(int flags, String path, long size)
   {
      synchronized (_readLock)
      {
         SQLiteDatabase database = this.getWritableDatabase();
         String sql = "";
         sql = "insert into " + DBHelper.favTable + "(" + DBHelper.favFilds[1]
	     + "," + DBHelper.favFilds[2] + "," + DBHelper.favFilds[3]
	     + ") values (?,?,?)";
         Object[] bindArgs =
         { flags, path, size };
         database.execSQL(sql, bindArgs);
      }
   }
   
   /**
    * 保存 下载的具体信息
    */
   public int saveInfos(AppBean info)
   {
      synchronized (_readLock)
      {
         String sql = "";
         String fields = "";
         String values = "";
         for (int i = 1; i < DBHelper.appInfoFilds.length - 1; i++)
         {
	  fields += DBHelper.appInfoFilds[i] + ",";
	  values += "?,";
         }
         fields += DBHelper.appInfoFilds[DBHelper.appInfoFilds.length - 1];
         values += "?";
         sql = "insert into App_info(" + fields + ") values (" + values + ")";
         
         Object[] bindArgs =
         { info.getAppName(), info.getPackageName(), info.getVersionName(),
	     info.getVersionCode(), info.getFilePath(), info.getFlags(),
	     info.getCodeSize(), info.getDataSize(), info.getCacheSize() };
         SQLiteDatabase database = this.getWritableDatabase();
         database.execSQL(sql, bindArgs);
         //database.insert(table, nullColumnHack, values)
         return 1;
      }
   }
   

   
   /**
    * 得到相应分类的全部信息 bFlag:true:记录总的文件数及文件大小,boolean bFlag
    */
   public List<FileBean> getFavoritesFolderInfos(FGroupInfo groupInfo)
   {
      synchronized (_readLock)
      {
         List<FileBean> list = new ArrayList<FileBean>();
         SQLiteDatabase database = this.getReadableDatabase();
         String sql = "";
         Cursor cursor = null;
         sql = "select " + DBHelper.favFilds[0] + ", " + DBHelper.favFilds[1]
	     + ", " + DBHelper.favFilds[2] + " from " + DBHelper.favTable
	     + " where " + DBHelper.favFilds[1] + "=?  ORDER BY "
	     + DBHelper.favFilds[4] + " COLLATE LOCALIZED";
         cursor = database.rawQuery(sql, new String[]
         { String.valueOf(groupInfo.getId()) });
         while (cursor.moveToNext())
         {
	  File mCurrent = new File(cursor.getString(2));
	  if (mCurrent.exists()&& mCurrent.isDirectory())// 只处理文件夹
	  {
	     FavorFileBean bean = new FavorFileBean(mCurrent,
		 mCurrent.getName(), mCurrent.getPath(), false);
	     bean.setDirectory(mCurrent.isDirectory());
	     int count = getFavorFileSize(mCurrent, groupInfo);
	     if (count > 0)
	     {
	        bean.setItemCount(count);
	        list.add(bean);
	     }
	  }
         }
         cursor.close();
         return list;
      }
   }
   
   /**
    * 得到相应分类的全部信息 bFlag:true:记录总的文件数及文件大小,boolean bFlag
    */
   public List<FileBean> getFolderItem(String path, FGroupInfo groupInfo)
   {
      synchronized (_readLock)
      {
         List<FileBean> list = new ArrayList<FileBean>();
         File mCurrent = new File(path);
         if (mCurrent.isDirectory())
         {
	  for (File file : mCurrent.listFiles())
	  {
	     if (!file.isDirectory())
	     {
	        BuildFavorFolderFile(file, groupInfo, list);
	     }
	  }
         }
         else
         {
	  BuildFavorFolderFile(mCurrent, groupInfo, list);
         }
         return list;
      }
   }
   
   public boolean BuildFavorFolderFile(File mCurrentFile, FGroupInfo groupInfo,
         List<FileBean> list)
   {
      String[] ends = groupInfo.getArrayEnd();
      String strFileName = mCurrentFile.getName();
      String fileEnds = strFileName.substring(strFileName.lastIndexOf(".") + 1)
	  .toLowerCase();// 取出文件后缀名并转成小写
      for (String end : ends)
      {
         if (end.equals(fileEnds))
         {
	  FavorFileBean bean = new FavorFileBean(mCurrentFile,
	        mCurrentFile.getName(), mCurrentFile.getPath(), false);
	  bean.setDirectory(mCurrentFile.isDirectory());
	  bean.setLength(mCurrentFile.length());
	  list.add(bean);
	  return true;
         }
      }
      return false;
   }
   
   /**
    * 获取相关类的文件数
    * 
    * @param mCurrentFile
    * @param groupInfo
    * @return
    */
   public int getFavorFileSize(File mCurrent, FGroupInfo groupInfo)
   {
      int count = 0;
      String[] ends = groupInfo.getArrayEnd();
      for (File file : mCurrent.listFiles())
      {
         if (!file.isDirectory())
         {
	  String strFileName = file.getName();
	  String fileEnds = strFileName.substring(
	        strFileName.lastIndexOf(".") + 1).toLowerCase();// 取出文件后缀名并转成小写
	  for (String end : ends)
	  {
	     if (end.equals(fileEnds))
	     {
	        count++;
	        break;
	     }
	  }
         }
      }
      return count;
   }
   
   /**
    * 得到相应分类的全部信息 bFlag:true:记录总的文件数及文件大小,boolean bFlag
    */
   public List<FileBean> getFavoritesInfos(String searchValue,
         FGroupInfo groupInfo)
   {
      synchronized (_readLock)
      {
         List<FileBean> list = new ArrayList<FileBean>();
         SQLiteDatabase database = this.getReadableDatabase();
         searchValue = searchValue.toLowerCase();
         String sql = "";
         Cursor cursor = null;
         sql = "select " + DBHelper.favFilds[0] + ", " + DBHelper.favFilds[1]
	     + ", " + DBHelper.favFilds[2] + " from " + DBHelper.favTable
	     + " where " + DBHelper.favFilds[1] + "=?  ORDER BY "
	     + DBHelper.favFilds[4] + " COLLATE LOCALIZED";
         cursor = database.rawQuery(sql, new String[]
         { String.valueOf(groupInfo.getId()) });
         while (cursor.moveToNext())
         {
	  File mCurrent = new File(cursor.getString(2));
	  if (mCurrent.isDirectory())
	  {
	     for (File file : mCurrent.listFiles())
	     {
	        if (!file.isDirectory())
	        {
		 if (searchValue.length() == 0
		       || compare(file.getName(), searchValue))
		 {
		    BuildFavorFile(file, groupInfo, list);
		 }
	        }
	     }
	  }
	  else
	  {
	     if (searchValue == null
		 || compare(mCurrent.getName(), searchValue))
	     {
	        BuildFavorFile(mCurrent, groupInfo, list);
	     }
	  }
         }
         cursor.close();
         return list;
      }
   }
   
   /**
    * str1:源 str2:需要比较的字符串 true:str1包含str2 false:不包含
    * 
    * @param str1
    * @param str2
    * @return
    */
   public boolean compare(String str1, String str2)
   {
      if (str1.length() < str2.length()) { return false; }
      byte[] byte1 = str1.toLowerCase().getBytes();
      byte[] byte2 = str2.getBytes();
      for (int i = 0; i < byte2.length; i++)
      {
         if (byte1[i] == byte2[i])
         {
	  continue;
         }
         else
         {
	  return false;
         }
      }
      return true;
   }
   
   public boolean BuildFavorFile(File mCurrentFile, FGroupInfo groupInfo,
         List<FileBean> list)
   {
      String[] ends = groupInfo.getArrayEnd();
      String strFileName = mCurrentFile.getName();
      String fileEnds = strFileName.substring(strFileName.lastIndexOf(".") + 1)
	  .toLowerCase();// 取出文件后缀名并转成小写
      for (String end : ends)
      {
         if (end.equals(fileEnds))
         {
	  FavorFileBean bean = new FavorFileBean(mCurrentFile,
	        mCurrentFile.getName(), mCurrentFile.getPath(), false);
	  bean.setDirectory(mCurrentFile.isDirectory());
	  // if (mCurrentFile.isDirectory())
	  // {
	  // String[] temp = mCurrentFile.list();
	  // if (temp != null)
	  // {
	  // bean.setItemCount(temp.length);
	  // }
	  // }
	  bean.setLength(mCurrentFile.length());
	  groupInfo.length += mCurrentFile.length();
	  list.add(bean);
	  return true;
         }
      }
      return false;
   }
   
   /**
    * 得到相应分类的全部信息
    */
   public List<FileBean> getFavoritesInfos(String flags, String search)
   {
      synchronized (_readLock)
      {
         List<FileBean> list = new ArrayList<FileBean>();
         SQLiteDatabase database = this.getReadableDatabase();
         String sql = "";
         Cursor cursor = null;
         if (search == null || search.length() == 0)
         {
	  sql = "select " + DBHelper.favFilds[0] + ", "
	        + DBHelper.favFilds[1] + ", " + DBHelper.favFilds[2]
	        + " from " + DBHelper.favTable + " where "
	        + DBHelper.favFilds[1] + "=?  ORDER BY "
	        + DBHelper.favFilds[4] + " COLLATE LOCALIZED";
	  cursor = database.rawQuery(sql, new String[]
	  { flags });
         }
         else
         {
	  sql = "select " + DBHelper.favFilds[0] + ", "
	        + DBHelper.favFilds[1] + ", " + DBHelper.favFilds[2]
	        + " from " + DBHelper.favTable + " where "
	        + DBHelper.favFilds[1] + "=? and " + DBHelper.favFilds[4]
	        + " LIKE ? ORDER BY " + DBHelper.favFilds[4]
	        + " COLLATE LOCALIZED";
	  search = search + "%";
	  cursor = database.rawQuery(sql, new String[]
	  { flags, search });
         }
         while (cursor.moveToNext())
         {
	  File mCurrentFile = new File(cursor.getString(2));
	  
	  FavorFileBean bean = new FavorFileBean(mCurrentFile,
	        mCurrentFile.getName(), mCurrentFile.getPath(), false);
	  bean.setId(cursor.getInt(0));
	  bean.setFlag(cursor.getInt(1));
	  bean.setDirectory(mCurrentFile.isDirectory());
	  // bean.setItemCount(cursor.getInt(1));
	  if (mCurrentFile.isDirectory())
	  {
	     String[] temp = mCurrentFile.list();
	     if (temp != null)
	     {
	        bean.setItemCount(temp.length);
	     }
	  }
	  bean.setLength(mCurrentFile.length());
	  mCurrentFile.canWrite();
	  list.add(bean);
         }
         cursor.close();
         return list;
      }
   }
   
   /**
    * 得到相应分类的全部信息
    */
   public AppBean getAppInfo(String packageName)
   {
      synchronized (_readLock)
      {
         AppBean info = null;
         SQLiteDatabase database = this.getReadableDatabase();
         
         String fields = "";
         for (int i = 0; i < DBHelper.appInfoFilds.length - 1; i++)
         {
	  fields += DBHelper.appInfoFilds[i] + ",";
         }
         fields += DBHelper.appInfoFilds[DBHelper.appInfoFilds.length - 1];
         String sql = "select " + fields + " from App_info where packageName=?";
         Cursor cursor = database.rawQuery(sql, new String[]
         { packageName });
         if (cursor.moveToNext())
         {
	  info = new AppBean(cursor.getInt(0), cursor.getString(1),
	        cursor.getString(2), cursor.getString(3), cursor.getInt(4),
	        cursor.getString(5), cursor.getInt(6), cursor.getInt(7),
	        cursor.getInt(8), cursor.getInt(9));
         }
         cursor.close();
         return info;
      }
   }
   
   /**
    * 查看数据库中应用数据总数
    */
   public int AppInfoCount()
   {
      synchronized (_readLock)
      {
         try
         {
	  SQLiteDatabase database = this.getReadableDatabase();
	  String sql = "select count(*) from " + DBHelper.appTable;
	  Cursor cursor = database.rawQuery(sql, null);
	  cursor.moveToFirst();
	  int count = cursor.getInt(0);
	  cursor.close();
	  Log.v("wmh", "AppInfoCount=" + count);
	  return count;
         }
         catch (Exception e)
         {
	  e.printStackTrace();
	  return 0;
         }
      }
   }
   
   /**
    * 得到相应分类的全部信息
    */
   public List<AppBean> getAppInfos(List<String> packageName)
   {
      synchronized (_readLock)
      {
         List<AppBean> list = new ArrayList<AppBean>();
         SQLiteDatabase database = this.getReadableDatabase();
         
         String fields = "";
         for (int i = 0; i < DBHelper.appInfoFilds.length - 1; i++)
         {
	  fields += DBHelper.appInfoFilds[i] + ",";
         }
         fields += DBHelper.appInfoFilds[DBHelper.appInfoFilds.length - 1];
         String sql = "select " + fields + " from App_info where packageName=?";
         for (int i = 0; i < packageName.size(); i++)
         {
	  Cursor cursor = database.rawQuery(sql, new String[]
	  { packageName.get(i) });
	  if (cursor.moveToNext())
	  {
	     AppBean info = new AppBean(cursor.getInt(0),
		 cursor.getString(1), cursor.getString(2),
		 cursor.getString(3), cursor.getInt(4),
		 cursor.getString(5), cursor.getInt(6), cursor.getInt(7),
		 cursor.getInt(8), cursor.getInt(9));
	     list.add(info);
	  }
	  cursor.close();
         }
         return list;
      }
   }
   
   /**
    * 得到相应分类的全部信息
    */
   public List<AppBean> getAppInfos(String flags)
   {
      synchronized (_readLock)
      {
         P.debug(TAG, "dao.getAppInfos start");
         List<AppBean> list = new ArrayList<AppBean>();
         SQLiteDatabase database = this.getReadableDatabase();
         
         String fields = "";
         for (int i = 0; i < DBHelper.appInfoFilds.length - 1; i++)
         {
	  fields += DBHelper.appInfoFilds[i] + ",";
         }
         fields += DBHelper.appInfoFilds[DBHelper.appInfoFilds.length - 1];
         String sql = "select " + fields + " from App_info where flags=?";
         Cursor cursor = database.rawQuery(sql, new String[]
         { flags });
         while (cursor.moveToNext())
         {
	  AppBean info = new AppBean(cursor.getInt(0), cursor.getString(1),
	        cursor.getString(2), cursor.getString(3), cursor.getInt(4),
	        cursor.getString(5), cursor.getInt(6), cursor.getInt(7),
	        cursor.getInt(8), cursor.getInt(9));
	  list.add(info);
         }
         cursor.close();
         P.debug(TAG, "dao.getAppInfos end");
         return list;
      }
   }
   
   /**
    * 更新数据库中的下载信息
    */
   public void updataInfos(AppBean info)
   {
      synchronized (_readLock)
      {
         SQLiteDatabase database = this.getReadableDatabase();
         String sql = "update App_info set codeSize=?, dataSize=?,cacheSize=? where packageName=?";
         Object[] bindArgs =
         { info.getCodeSize(), info.getDataSize(), info.getCacheSize(),
	     info.getPackageName() };
         database.execSQL(sql, bindArgs);
      }
   }
   
   /**
    * 更新数据库中的下载信息
    */
   public void updataInfos(String packageName, long codeSize, long dataSize,
         long cacheSize)
   {
      synchronized (_readLock)
      {
         SQLiteDatabase database = this.getReadableDatabase();
         String sql = "update App_info set codeSize=?, dataSize=?,cacheSize=? where packageName=?";
         Object[] bindArgs =
         { codeSize, dataSize, cacheSize, packageName };
         database.execSQL(sql, bindArgs);
      }
   }
   
   /**
    * 关闭数据库
    */
   public void closeDb()
   {
      synchronized (_readLock)
      {
         this.close();
      }
   }
   
   /**
    * 下载完成后删除数据库中的数据
    * 返回删除的条数
    */
   public int delete(String packageName)
   {
      synchronized (_readLock)
      {
         SQLiteDatabase database = this.getWritableDatabase();
        int result= database.delete("App_info", "packageName=?", new String[]
         { packageName });
         database.close();
         return result;
      }
   }
   
   /**
    *清空APP列表数据
    */
   public void deleteAppAll()
   {
      synchronized (_readLock)
      {
         SQLiteDatabase database = this.getWritableDatabase();
         database.delete("App_info", "", null);
         database.close();
      }
   }
   
   /**
    * 清空收藏列表中的数据
    */
   public void deleteFavoritesAll()
   {
      synchronized (_readLock)
      {
         SQLiteDatabase database = this.getWritableDatabase();
         database.delete(DBHelper.favTable, DBHelper.favFilds[1] + "<>?",
	     new String[]
	     { "0" });
         database.close();
      }
   }
   
   /**
    * 历史记录相关处理函数
    */
   /**
    * 查看数据库中是否有数据
    * nFlage:0:收藏夹 1:历史
    * return :true:有数据 False:没数据
    */
   public boolean isHasHistory(int flags,String path)
   {
      synchronized (_readLock)
      {
         SQLiteDatabase database = this.getReadableDatabase();
         String sql = "select count(*)  from "+DBHelper.hisTable+" where flags=? and "+DBHelper.favFilds[2]+"=?";
         Cursor cursor = database.rawQuery(sql, new String[]
         { String.valueOf(flags),path });
         cursor.moveToFirst();
         int count = cursor.getInt(0);
         cursor.close();
         return count>0;
      }
   }
   /**
    * 删除指定ID的历史及收藏记录
    * nFlage:0:收藏 1:历史
    */
   public int deleteHistory(int flags,String path)
   {
      synchronized (_readLock)
      {
         SQLiteDatabase database = this.getWritableDatabase();
         int result = database.delete(DBHelper.hisTable,"flags=? and "+DBHelper.favFilds[2]+"=?", new String[]
	             { String.valueOf(flags),path });
         database.close();
         return result;
      }
   }
   /**
    * 添加历史记录
    * nFlage:0:收藏 1:历史
    * path:路径
    * size:文件大小
    */
   public void InsertHistory(int flags, String path, long size)
   {
      synchronized (_readLock)
      {
         SQLiteDatabase database = this.getWritableDatabase();
         database.delete(DBHelper.hisTable, favFilds[1] + "=? and "
	     + favFilds[2] + "=?", new String[]
         { String.valueOf(flags), path });
         String sql = "";
         sql = "insert into " + DBHelper.hisTable + "(" + DBHelper.favFilds[1]
	     + "," + DBHelper.favFilds[2] + "," + DBHelper.favFilds[3]
	     + ") values (?,?,?)";
         Object[] bindArgs =
         { flags, path, size };
         database.execSQL(sql, bindArgs);
      }
   }
   /**
    * 删除指定ID的历史及收藏记录
    * nFlage:0:收藏 1:历史
    */
   public int deleteHistory(int id)
   {
      synchronized (_readLock)
      {
         SQLiteDatabase database = this.getWritableDatabase();
         int result = database.delete(DBHelper.hisTable, DBHelper.favFilds[0]
	     + "=?", new String[]
         { String.valueOf(id) });
         database.close();
         return result;
      }
   }
   
   /**
    * 下载完成后删除数据库中的数据 
    * nFlage:0:收藏 1:历史
    */
   public void ClearHistory(int nFlage)
   {
      synchronized (_readLock)
      {
         SQLiteDatabase database = this.getWritableDatabase();
         database.delete(DBHelper.hisTable, DBHelper.favFilds[1] + "=?",
	     new String[]
	     { String.valueOf(nFlage) });
         database.close();
      }
   }
   
   /**
    * 获取用户收藏的所有数据 
    * nFlage:0:收藏 1:历史
    */
   public List<FavorFileBean> getHistoryInfos(String flags)
   {
      synchronized (_readLock)
      {
         List<FavorFileBean> list = new ArrayList<FavorFileBean>();
         SQLiteDatabase database = this.getReadableDatabase();
         String sql = "";
         Cursor cursor = null;
         if (flags.equals("0"))
         {
	  sql = "select " + DBHelper.favFilds[0] + ", "
	        + DBHelper.favFilds[1] + ", " + DBHelper.favFilds[2]
	        + " from " + DBHelper.hisTable + " where "
	        + DBHelper.favFilds[1] + "=?";
         }
         else
         {
	  sql = "select " + DBHelper.favFilds[0] + ", "
	        + DBHelper.favFilds[1] + ", " + DBHelper.favFilds[2]
	        + " from " + DBHelper.hisTable + " where "
	        + DBHelper.favFilds[1] + "=?" + " ORDER BY " + favFilds[0]
	        + " desc ";
         }
         cursor = database.rawQuery(sql, new String[]
         { flags });
         
         while (cursor.moveToNext())
         {
	  File mCurrentFile = new File(cursor.getString(2));
	  
	  FavorFileBean bean = new FavorFileBean(mCurrentFile,
	        mCurrentFile.getName(), mCurrentFile.getPath(), false);
	  bean.setId(cursor.getInt(0));
	  bean.setFlag(cursor.getInt(1));
	  bean.setDirectory(mCurrentFile.isDirectory());
	  // bean.setItemCount(cursor.getInt(1));
	  if (mCurrentFile.isDirectory())
	  {
	     String[] temp = mCurrentFile.list();
	     if (temp != null)
	     {
	        bean.setItemCount(temp.length);
	     }
	  }
	  bean.setLength(mCurrentFile.length());
	  mCurrentFile.canWrite();
	  list.add(bean);
         }
         cursor.close();
         return list;
      }
   }
}