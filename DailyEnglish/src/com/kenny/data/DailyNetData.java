package com.kenny.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.text.format.DateFormat;
import android.util.Log;

public class DailyNetData {
	private String sid;
	private String title;
	private String content;
	private String translation;
	private String note;
	private String picture;
	private String tts;
	private Date date;
	
	private Calendar calendar;

	public Date getDate() {
		return date;
	}

	public int getDay() 
	{
		if(calendar==null)
		{
			return 1;
		}
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	public int getYear() {
		return 1900 + date.getYear();
	}

	public String getStrMonth() {
		SimpleDateFormat from = new SimpleDateFormat("MMM", Locale.US);
		Log.v("wmh", "date.getMonth()="+date.getMonth());
		return from.format(date);
	}

	public void setDate(Long date)
	{
		setDate(DateFormat.format("yyyy-MM-dd", date).toString());
	}
	public void setDate(String date) 
	{
		try 
		{
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");
			this.date = simpleDateFormat.parse(date);
			calendar = Calendar.getInstance();
			calendar.setTime(this.date);
		}
		catch (ParseException e) 
		{
			
			e.printStackTrace();
		}
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getTts() {
		return tts;
	}

	public void setTts(String tts) {
		this.tts = tts;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTranslation() {
		return translation;
	}

	public void setTranslation(String translation) {
		this.translation = translation;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
