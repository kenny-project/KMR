package com.work.market.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;


public class DBdataModel {


	protected int id=1;//id m_id =  bunde.getString("id");
	protected String title="test";//Ãû³Æ m_title = bunde.getString("title");
	protected String pn="com.kenny.test";// °üÃû m_pn =  bunde.getString("pn");
	


	public DBdataModel() {

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