package com.kenny.data;

public class PushMsgBean {
	private int id;
	private String title;
	private String desc;
	private String value;
	private int iread;//是否已读 0:未读 1：已读
	public int getId() {
		return id;
	}
	
	public int getIread() {
		return iread;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int isIread() {
		return iread;
	}
	public void setIread(int iread) {
		this.iread = iread;
	}
	
}
