package com.work.market.net;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * 一个业务类
 */
public class Dao
{
	private DBHelper dbHelper;
	private final static Object _writeLock = new Object();

	public Dao(Context context)
	{
		dbHelper = new DBHelper(context);
	}

	/**
	 * 查看数据库中是否有数据
	 */
	public boolean isHasInfors(String urlstr)
	{
		synchronized (_writeLock)
		{
			SQLiteDatabase database = dbHelper.getReadableDatabase();
			String sql = "select count(*)  from download_info where url=?";
			Cursor cursor = database.rawQuery(sql, new String[]
			{ urlstr });
			cursor.moveToFirst();
			int count = cursor.getInt(0);
			cursor.close();
			return count == 0;
		}
	}

	/**
	 * 保存 下载的具体信息
	 */
	public void saveInfos(DownloadInfo info)
	{
		synchronized (_writeLock)
		{
			if(isHasInfors(info.getUrl()))
			{
			SQLiteDatabase database = dbHelper.getWritableDatabase();

			String sql = "insert into download_info(start_pos, end_pos,compelete_size,url) values (?,?,?,?)";
			Object[] bindArgs =
			{ info.getStartPos(), info.getEndPos(), info.getCompeleteSize(),
					info.getUrl() };
			database.execSQL(sql, bindArgs);
			}
			else
			{
				SQLiteDatabase database = dbHelper.getWritableDatabase();
				//String sql = "update download_info set compelete_size=? where thread_id=? and url=?";
				String sql = "update download_info set start_pos=?, end_pos=?,compelete_size=? where url=?";
				Object[] bindArgs =
				{ info.getStartPos(), info.getEndPos(), info.getCompeleteSize(),
						info.getUrl() };
				database.execSQL(sql, bindArgs);				
			}
		}
	}

	/**
	 * 得到下载具体信息
	 */
	public DownloadInfo getInfos(String urlstr)
	{
		synchronized (_writeLock)
		{
			DownloadInfo info = null;
			SQLiteDatabase database = dbHelper.getReadableDatabase();
			String sql = "select start_pos, end_pos,compelete_size,url from download_info where url=?";
			Cursor cursor = database.rawQuery(sql, new String[]
			{ urlstr });
			while (cursor.moveToNext())
			{
				info = new DownloadInfo(cursor.getInt(0), cursor.getInt(1),
						cursor.getInt(2), cursor.getString(3));
				break;
			}
			cursor.close();
			return info;
		}
	}

	/**
	 * 更新数据库中的下载信息
	 */
	public void updataInfos(DownloadInfo info)
	{
		synchronized (_writeLock)
		{
			SQLiteDatabase database = dbHelper.getReadableDatabase();
			String sql = "update download_info set compelete_size=? where url=?";
			Object[] bindArgs =
			{ info.getCompeleteSize(), info.getUrl() };
			database.execSQL(sql, bindArgs);
		}
	}

	/**
	 * 更新数据库中的下载信息
	 */
	public void updataInfos(int threadId, int compeleteSize, String urlstr)
	{
		synchronized (_writeLock)
		{
			SQLiteDatabase database = dbHelper.getReadableDatabase();
			String sql = "update download_info set compelete_size=? where thread_id=? and url=?";
			Object[] bindArgs =
			{ compeleteSize, threadId, urlstr };
			database.execSQL(sql, bindArgs);
		}
	}

	/**
	 * 关闭数据库
	 */
	public void closeDb()
	{
		synchronized (_writeLock)
		{
			dbHelper.close();
		}
	}

	/**
	 * 下载完成后删除数据库中的数据
	 */
	public void delete(String url)
	{
		synchronized (_writeLock)
		{
			SQLiteDatabase database = dbHelper.getReadableDatabase();
			database.delete("download_info", "url=?", new String[]
			{ url });
		}
	}
}