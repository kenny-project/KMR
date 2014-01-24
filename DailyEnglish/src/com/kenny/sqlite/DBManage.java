package com.kenny.sqlite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;

import com.kenny.Application.KApp;
import com.kenny.data.BookBean;
import com.kenny.data.DailyNetData;
import com.kenny.data.FavoriteBean;
import com.kenny.data.FavoriteGroupBean;
import com.kenny.data.PushMsgBean;
import com.kenny.data.VOANetData;
import com.kenny.util.Config;
import com.kenny.util.Const;
import com.kenny.util.Log;
import com.kenny.util.Utils;

public class DBManage {
	// database版本
	private final static int DB_VERSION = 7;
	// database名
	private final static String DB_NAME = "powerword.db";

	private Context mMain;

	private static DBManage mInstance;

	// 执行open（）打开数据库时，保存返回的数据库
	private SQLiteDatabase mSQLiteDatabase = null;

	// 由SQLiteOpenHelper继承过来
	private DatabaseHelper mDatabaseHelper = null;

	private Object object = new Object();
	private Config config;

	/* 构造函数，取得Context */
	private DBManage(Context context) {
		mMain = context;
		KApp app = (KApp) context.getApplicationContext();
		config = app.config;
	}

	// 打开数据库
	public void open() throws SQLException {
		if (isOpen() == true) {
			return;
		}
		mDatabaseHelper = new DatabaseHelper(mMain);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}

	public static DBManage getInstance(Context main) {
		if (mInstance == null) {
			mInstance = new DBManage(main);
		}

		return mInstance;
	}

	// 关闭数据库
	public void close() 
	{
			closeDB();
	}

	public void closeDB() {
		Log.e("chenjg", "db close");
		try {
			if (isOpen() == true && mDatabaseHelper != null)
				mDatabaseHelper.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			// 当调用getWritableDatabase()
			// getReadableDatabase()方法
			// 则创建一个数据库
			super(context, DB_NAME, null, DB_VERSION);
		}

		/* 创建表 */
		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			db.execSQL("create table dailyfavorite(_sid text PRIMARY KEY , _title text,_content text, _translation text, _note text, _picture text, _tts text, _date long)");
			db.execSQL("create table voafavorite(_id INTEGER PRIMARY KEY , _title text,_cntitle text,_publish text,_articleurl text,_views INTEGER)");
			// 生词本表 opera字段为操作字段，0 - add(添加) 1 - delete(删除) 2 - modify(修改) 3 -
			// none(无操作)
			db.execSQL("create table config(config_name text PRIMARY KEY, config_value text)");
			db.execSQL("create table push_msg(_id INTEGER PRIMARY KEY autoincrement, _title text,_desc text, _value text, _iread INTEGER)"); //通知栏

		}

		/* 升级数据库 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
	}

	public void beginTransaction() {
		mSQLiteDatabase.beginTransaction();
	}

	public void endTransaction() {
		try {
			mSQLiteDatabase.endTransaction();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setTransactionSuccessful() {
		mSQLiteDatabase.setTransactionSuccessful();
	}

	public boolean isOpen() {
		if (mSQLiteDatabase == null) {
			return false;
		}
		return mSQLiteDatabase.isOpen();
	}

	


	
	// 插入一条到msg列表
	public long insertPushMsg(String title,String desc, String value) {
		open();
		ContentValues values = new ContentValues();
		values.put("_title", title);
		values.put("_desc", desc);
		values.put("_value", value);
		values.put("_iread", 0);
		return mSQLiteDatabase.insert("push_msg", null, values);
	}
	// 根据word更新一条历史记录
	public int updatePushMsg(int id,int _iread) {
			ContentValues values = new ContentValues();
			values.put("_iread", _iread);
			return mSQLiteDatabase.update("push_msg", values, "_id = ?",
					new String[] { String.valueOf(id) });
		}
	/**
	 * 得到消息的全部信息
	 */
	public List<PushMsgBean> getPushMsgInfos() 
	{
		ArrayList<PushMsgBean> groupArraylist = new ArrayList<PushMsgBean>();
		String sql = "select _id,_title,_desc,_value,_iread from push_msg order by _id desc";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		for (int i=0;i<30&&cursor.moveToNext();i++) 
		{
			PushMsgBean bean = new PushMsgBean();
			bean.setId(cursor.getInt(0));
			bean.setTitle(cursor.getString(1));
			bean.setDesc(cursor.getString(2));
			bean.setValue(cursor.getString(3));
			bean.setIread(cursor.getInt(4));
			groupArraylist.add(bean);
		}
		cursor.close();
		return groupArraylist;
	}

	/**
	 * 下载完成后删除数据库中的数据
	 */
	public int deleteVOAFavorites(String url) {
		// mSQLiteDatabase.delete("favorite", "", null);删除全部
		return mSQLiteDatabase
				.delete("voafavorite", "_articleurl=?", new String[] { url });
	}
	/**
	 * 保存 下载的具体信息
	 */
	public boolean InsertVOAFavorites(String id,String title,String cntitle,String publish,String articleurl,Integer views) {
		try {
			if (!isVOAFavorites(articleurl)) 
			{
				return true;
			}
			else 
			{
				ContentValues values = new ContentValues();
				values.put("_id", id);
				values.put("_title", title);
				values.put("_cntitle", cntitle);
				values.put("_publish", publish);
				values.put("_articleurl", articleurl);
				values.put("_views", views);
				long result = mSQLiteDatabase.insert("voafavorite", null, values);
			}
			// mSQLiteDatabase.execSQL(sql, bindArgs);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 查看数据库中是否有数据 true:空 false:有数据
	 */
	public boolean isVOAFavorites(String _articleurl) 
	{
		String sql = "select count(*)  from voafavorite where _articleurl=?";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, new String[] { _articleurl });
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count == 0;
	}
	/**
	 * 得到相应分类的全部信息
	 */
	public List<VOANetData> getVOAFavoritesInfos() {
		ArrayList<VOANetData> list = new ArrayList<VOANetData>();
		String sql = "select _id,_title,_cntitle,_publish,_articleurl,_views from voafavorite order by _id desc";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		while (cursor.moveToNext()) 
		{
			VOANetData bean = new VOANetData();
			bean.setId(cursor.getInt(0));
			bean.setTitle(cursor.getString(1));
			bean.setCntitle(cursor.getString(2));
			bean.setPublish(cursor.getString(3));
			bean.setArticleurl(cursor.getString(4));
			bean.setViews(cursor.getInt(5));
			list.add(bean);
		}
		cursor.close();
		return list;
	}
	/**
	 * 得到相应分类的全部信息
	 */
	public List<DailyNetData> getDailyFavoritesInfos() 
	{
//		(_sid text PRIMARY KEY , _title text,_content text, _translation text, 
//				_note text, _picture text, _tts text
		ArrayList<DailyNetData> list = new ArrayList<DailyNetData>();
		String sql = "select _sid,_title,_content,_translation,_note,_picture,_tts,_date from dailyfavorite order by _date desc";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, null);
		while (cursor.moveToNext()) 
		{
			DailyNetData bean = new DailyNetData();
			bean.setSid(cursor.getString(0));
			bean.setTitle(cursor.getString(1));
			bean.setContent(cursor.getString(2));
			bean.setTranslation(cursor.getString(3));
			bean.setNote(cursor.getString(4));
			bean.setPicture(cursor.getString(5));
			bean.setTts(cursor.getString(6));
			bean.setDate(cursor.getLong(7));
			list.add(bean);
		}
		cursor.close();
		return list;
	}
	// 插入一条查词历史记录
	public long insertDaily(DailyNetData bean) {
		ContentValues values = new ContentValues();
		values.put("_sid", bean.getSid());
		values.put("_title", bean.getTitle());
		values.put("_content", bean.getContent());
		values.put("_translation", bean.getTranslation());
		values.put("_note", bean.getNote());
		values.put("_picture", bean.getPicture());
		values.put("_tts", bean.getTts());
		values.put("_date", bean.getDate().getTime());
		return mSQLiteDatabase.insert("dailyfavorite", null, values);
	}
	/**
	 * 下载完成后删除数据库中的数据
	 */
	public int deleteDailyFavorites(String sid) {
		// mSQLiteDatabase.delete("favorite", "", null);删除全部
		return mSQLiteDatabase
				.delete("dailyfavorite", "_sid=?", new String[] { sid });
	}
	/**
	 * 查看数据库中是否有数据 true:空 false:有数据
	 */
	public boolean isDailyFavorites(String sid) {
		String sql = "select count(*)  from dailyfavorite where _sid=?";
		Cursor cursor = mSQLiteDatabase.rawQuery(sql, new String[] { sid });
		cursor.moveToFirst();
		int count = cursor.getInt(0);
		cursor.close();
		return count == 0;
	}
	
		// 更新配置信息
	public int updateConfig(String name, String value) {
		ContentValues values = new ContentValues();
		values.put("config_value", value);
		return mSQLiteDatabase.update("config", values, "config_name = ?",
				new String[] { name });
	}

	// 查询配置信息
	public Cursor fetchConfig(String configName) {
		Cursor cursor = null;
		cursor = mSQLiteDatabase.query("config", new String[] { "config_name",
				"config_value" }, "config_name = ?",
				new String[] { configName }, null, null, null);
		return cursor;
	}

	// 查询配置信息
	public Cursor fetchConfig() {
		Cursor cursor = null;
		cursor = mSQLiteDatabase.query("config", new String[] { "config_name",
				"config_value" }, null, null, null, null, null);
		return cursor;
	}
}
