package com.kenny.data;

public class FeedbackBean {
	public final static int TYPE_USER = 0;
	public final static int TYPE_DEV = 1;
	private long date = System.currentTimeMillis();
	private int type = TYPE_USER;
	private String content = "";
	
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
