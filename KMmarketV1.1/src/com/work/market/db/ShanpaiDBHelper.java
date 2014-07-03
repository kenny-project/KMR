package com.work.market.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 数据库定义类
 * @author zhou
 *
 */
public class ShanpaiDBHelper extends SQLiteOpenHelper {
	
	//DB名称
	private static final String DB_FILENAME = "markuser.db";
	//版本号
	private static final int DB_VERSION = 2;
	
	//通知表结构
	public static final String TABLE_MESSAGE = "mark_table";
	public static final String MESSAGE_ID = "message_id";
	public static final String MESSAGE_TIME = "message_title";
	public static final String MESSAGE_INFO = "message_pn";
	public static final String MESSAGE_USERKEY = "message_userkey";
	
	//拍照历史记录表结构
	public static final String TABLE_PHOTO = "photo_table";
	public static final String PHOTO_PATH = "photo_path";
	public static final String PHOTO_KEY = "photo_key";
	public static final String PHOTO_URL = "photo_url";
	public static final String PHOTO_T = "photo_t";
	public static final String PHOTO_TIME = "photo_time";
	
	//优惠券表结构
	public static final String TABLE_FINISH = "finish_table";
	
	public static final String DISCOUNT_ID = "discount_id";
	public static final String DISCOUNT_TITLE = "discount_title";
	public static final String DISCOUNT_PN = "discount_pn";
	public static final String DISCOUNT_LOGO = "discount_logo";
	public static final String DISCOUNT_SIZE = "discount_size";
	public static final String DISCOUNT_APPURL = "discount_appurl";
	public static final String DISCOUNT_VERSION = "discount_versionCode";
	public static final String DISCOUNT_FILENAME = "discount_fileName";
	public static final String DISCOUNT_APPFILEEXT = "discount_appfileext";
	

	public ShanpaiDBHelper(Context context) {
		super(context, DB_FILENAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_MESSAGE +
				 " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 MESSAGE_ID + " VARCHAR NOT NULL, " +
				 MESSAGE_TIME + " VARCHAR, " +
				 MESSAGE_INFO + " VARCHAR, " +
				 MESSAGE_USERKEY + " VARCHAR);");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FINISH +
				 " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 DISCOUNT_ID + " VARCHAR NOT NULL, " +
				 DISCOUNT_TITLE + " VARCHAR, " +
				 DISCOUNT_PN + " VARCHAR, " +
				 DISCOUNT_LOGO + " VARCHAR, " +
				 DISCOUNT_SIZE + " VARCHAR, " +		
				 DISCOUNT_APPURL + " VARCHAR, " +
				 DISCOUNT_VERSION + " VARCHAR, " +
				 DISCOUNT_FILENAME + " VARCHAR, " +
				 DISCOUNT_APPFILEEXT + " VARCHAR, " +
				 MESSAGE_USERKEY + " VARCHAR);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FINISH);
		onCreate(db);
	}

}
