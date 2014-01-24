package com.kenny.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;

import com.kenny.sqlite.DBManage;

public class Config {
	public String NEWWORD_AOTU_SAVE;
	public String NEWWORD_SYNC_USERNAME;
	public String NEWWORD_SYNC_PASSWORD;
	public String SETTING_LEARNINGREMIND_SWITCH;
	public String SETTING_USE_TTS;
	public String SETTING_VOICE_INPUT;
	public String IS_FIRST;
	public boolean IS_SHOWED_HELP = false;
	public String IS_SHOW_HELP;
	public String HISTORY_ORDER_MODE;
	public String NEWWORD_ORDER_MODE;
	public boolean IS_HAVE_TTS;    // 是否支持tts
	public String DEFAULT_NEWWORD_BOOK_NAME = "默认生词本";
	public String DEFAULT_NEWWORD_BOOK_ID = "0";
	public String DEFAULT_NEWWORD_PAGE_ID = "1";
	public String DEFAULT_TOP_LABEL = Const.CONFIG_DEFAULT_TL_BASIC;
	public int NEW_APP_STATUS = 3; // 1-HOT 2-NEW 3-NORMAR
	
	public void readConfig(Context main){
		DBManage db = DBManage.getInstance(main);
		db.open();
		Cursor newwordAutoSave = db.fetchConfig(Const.CONFIG_NEWWORD_SAVE);
		if (newwordAutoSave.moveToNext()){
			NEWWORD_AOTU_SAVE = newwordAutoSave.getString(newwordAutoSave.getColumnIndex("config_value"));
			if (NEWWORD_AOTU_SAVE == null){
				NEWWORD_AOTU_SAVE = "1";
			}
		}
		Cursor newwordUserName = db.fetchConfig(Const.CONFIG_NEWWORD_USERNAME);
		if (newwordUserName.moveToNext()){
			NEWWORD_SYNC_USERNAME = newwordUserName.getString(newwordUserName.getColumnIndex("config_value"));
			if (NEWWORD_SYNC_USERNAME == null){
				NEWWORD_SYNC_USERNAME = "";
			}
		}
		Cursor newwordPassword = db.fetchConfig(Const.CONFIG_NEWWORD_PASSWORD);
		if (newwordPassword.moveToNext()){
			NEWWORD_SYNC_PASSWORD = newwordPassword.getString(newwordPassword.getColumnIndex("config_value"));
			if (NEWWORD_SYNC_PASSWORD == null){
				NEWWORD_SYNC_PASSWORD = "";
			}
		}
		Cursor settingAutoConnection = db.fetchConfig(Const.CONFIG_SETTING_LEARNINGREMIND_SWITCH);
		if (settingAutoConnection.moveToNext()){
			SETTING_LEARNINGREMIND_SWITCH = settingAutoConnection.getString(settingAutoConnection.getColumnIndex("config_value"));
			if (SETTING_LEARNINGREMIND_SWITCH == null){
				SETTING_LEARNINGREMIND_SWITCH = "1";
			}
		}
		Cursor settingVoiceInputType = db.fetchConfig(Const.CONFIG_SETTING_VOICE_INPUT_TYPE);
		if (settingVoiceInputType.moveToNext()){
			SETTING_VOICE_INPUT = settingVoiceInputType.getString(settingVoiceInputType.getColumnIndex("config_value"));
			if (SETTING_VOICE_INPUT == null){
				SETTING_VOICE_INPUT = "1";
			}
		}
		
		Cursor isFirst = db.fetchConfig(Const.CONFIG_FIRST);
		if (isFirst.moveToNext()){
			IS_FIRST = isFirst.getString(isFirst.getColumnIndex("config_value"));
			if (IS_FIRST == null){
				IS_FIRST = "1";
			}
		}
		
		Cursor isShowHelp = db.fetchConfig(Const.CONFIG_SHOW_HELP);
		if (isShowHelp.moveToNext()){
			IS_SHOW_HELP = isShowHelp.getString(isShowHelp.getColumnIndex("config_value"));
			if (IS_SHOW_HELP == null){
				IS_SHOW_HELP = "1";
			}
		}
		
		Cursor historyMode = db.fetchConfig(Const.CONFIG_HISTORY_MODE);
		if (historyMode.moveToNext()){
			HISTORY_ORDER_MODE = historyMode.getString(historyMode.getColumnIndex("config_value"));
			if (HISTORY_ORDER_MODE == null){
				HISTORY_ORDER_MODE = "1";
			}
		}
		
		Cursor newwordMode = db.fetchConfig(Const.CONFIG_NEWWORD_MODE);
		if (newwordMode.moveToNext()){
			NEWWORD_ORDER_MODE = newwordMode.getString(newwordMode.getColumnIndex("config_value"));
			if (NEWWORD_ORDER_MODE == null){
				NEWWORD_ORDER_MODE = "1";
			}
		}
		
		Cursor useTTS = db.fetchConfig(Const.CONFIG_USE_TTS);
		if (useTTS.moveToNext()){
			SETTING_USE_TTS = useTTS.getString(useTTS.getColumnIndex("config_value"));
			if (SETTING_USE_TTS == null){
				SETTING_USE_TTS = "0";
			}
			
		}
		
		Cursor defaultBook = db.fetchConfig(Const.CONFIG_DEFAULT_NEWWORD_BOOK);
		if (defaultBook.moveToNext()){
			DEFAULT_NEWWORD_BOOK_NAME = defaultBook.getString(defaultBook.getColumnIndex("config_value"));
			if (DEFAULT_NEWWORD_BOOK_NAME == null){
				DEFAULT_NEWWORD_BOOK_NAME = Utils.get(main, "default_newword_bookid", "1");;
			}
		}
		
		Cursor defaultBookID = db.fetchConfig(Const.CONFIG_DEFAULT_NEWWORD_ID);
		if (defaultBookID.moveToNext()){
			DEFAULT_NEWWORD_BOOK_ID = defaultBookID.getString(defaultBookID.getColumnIndex("config_value"));
			if (DEFAULT_NEWWORD_BOOK_ID == null){
				DEFAULT_NEWWORD_BOOK_ID = "1";
			}
		}
		
		Cursor defaultPageID = db.fetchConfig(Const.CONFIG_DEFAULT_NEWWORD_PAGE);
		if (defaultPageID.moveToNext()){
			DEFAULT_NEWWORD_PAGE_ID = defaultPageID.getString(defaultPageID.getColumnIndex("config_value"));
			if (DEFAULT_NEWWORD_PAGE_ID == null){
				DEFAULT_NEWWORD_PAGE_ID = "1";
			}
		}
		
		Cursor topLabel = db.fetchConfig(Const.CONFIG_DEFAULT_TL);
		if (topLabel.moveToNext()){
			DEFAULT_TOP_LABEL = topLabel.getString(topLabel.getColumnIndex("config_value"));
			if (DEFAULT_TOP_LABEL == null){
				DEFAULT_TOP_LABEL = "1";
			}
		}
		
		db.close();
	}
	
	public void readVoiceInput(Context main){
		DBManage db = DBManage.getInstance(main);
		db.open();
		Cursor settingVoiceInputType = db.fetchConfig(Const.CONFIG_SETTING_VOICE_INPUT_TYPE);
		if (settingVoiceInputType.moveToNext()){
			SETTING_VOICE_INPUT = settingVoiceInputType.getString(settingVoiceInputType.getColumnIndex("config_value"));
		}
		db.closeDB();
	}
	
	public void writeConfig(Context main){
		DBManage db = DBManage.getInstance(main);
		db.open();
		db.updateConfig(Const.CONFIG_NEWWORD_SAVE, NEWWORD_AOTU_SAVE);
		db.updateConfig(Const.CONFIG_NEWWORD_USERNAME, NEWWORD_SYNC_USERNAME);
		db.updateConfig(Const.CONFIG_NEWWORD_PASSWORD, NEWWORD_SYNC_PASSWORD);
		db.updateConfig(Const.CONFIG_SETTING_LEARNINGREMIND_SWITCH, SETTING_LEARNINGREMIND_SWITCH);
		db.updateConfig(Const.CONFIG_SETTING_VOICE_INPUT_TYPE, SETTING_VOICE_INPUT);
		db.updateConfig(Const.CONFIG_FIRST, IS_FIRST);
		db.updateConfig(Const.CONFIG_SHOW_HELP, IS_SHOW_HELP);
		db.updateConfig(Const.CONFIG_HISTORY_MODE, HISTORY_ORDER_MODE);
		db.updateConfig(Const.CONFIG_NEWWORD_MODE, NEWWORD_ORDER_MODE);
		db.updateConfig(Const.CONFIG_USE_TTS, SETTING_USE_TTS);
		db.updateConfig(Const.CONFIG_DEFAULT_NEWWORD_BOOK, DEFAULT_NEWWORD_BOOK_NAME);
		db.updateConfig(Const.CONFIG_DEFAULT_NEWWORD_ID, DEFAULT_NEWWORD_BOOK_ID);
		db.updateConfig(Const.CONFIG_DEFAULT_NEWWORD_PAGE, DEFAULT_NEWWORD_PAGE_ID);
		db.updateConfig(Const.CONFIG_DEFAULT_TL, DEFAULT_TOP_LABEL);
		db.close();
	}
}
