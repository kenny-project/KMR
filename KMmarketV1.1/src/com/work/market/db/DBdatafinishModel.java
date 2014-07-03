package com.work.market.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;


public class DBdatafinishModel {

	protected int id=1;//id m_id =  bunde.getString("id");
	protected String title="test";//名称 m_title = bunde.getString("title");
	protected String pn="com.kenny.test";// 包名 m_pn =  bunde.getString("pn");
	protected String appurl="";// 包名 m_pn =  bunde.getString("pn");
	protected String logurl="";// 包名 m_pn =  bunde.getString("pn");
	protected String size="1";// 包名 m_pn =  bunde.getString("pn");
	protected String versioncode="12.2.2";// 包名 m_pn =  bunde.getString("pn");
	protected String AppFileExt="";// 包名 m_pn =  bunde.getString("pn");
	protected String FileName;
	


	public String getFileName()
	{
		return FileName;
	}

	public void setFileName(String fileName)
	{
		FileName = fileName;
	}

	public DBdatafinishModel() {

	}
	
	public String getAppFileExt() {
		return AppFileExt;
	}

	public void setAppFileExt(String aAppFileExt) {
		this.AppFileExt = aAppFileExt;
	}
	
	
	public String getversioncode() {
		return versioncode;
	}

	public void setversioncode(String versioncode) {
		this.versioncode = versioncode;
	}
	
	public String getsize() {
		return size;
	}

	public void setsize(String size) {
		this.size = size;
	}
	

	public String getlogurl() {
		return logurl;
	}

	public void setlogurl(String logurl) {
		this.logurl = logurl;
	}
	
	public String getappurl() {
		return appurl;
	}

	public void setappurl(String appurl) {
		this.appurl = appurl;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPn() {
		return pn;
	}

	public void setPn(String pn) {
		this.pn = pn;
	}

	
}

