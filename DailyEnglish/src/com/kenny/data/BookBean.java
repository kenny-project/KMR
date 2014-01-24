package com.kenny.data;

import java.io.Serializable;

public class BookBean implements Serializable{
	private String bookName;       // 生词本名
	private int bookId;         // 生词本ID
	private int pageId;         // 页数ID
	private boolean isDelete;      // 是否是删除状态
	private int newwordCount;
	private boolean isNew;
	private int bookNewwordCount;  // 该音词本总单词数
	
	public int getBookNewwordCount() {
		return bookNewwordCount;
	}
	public void setBookNewwordCount(int bookNewwordCount) {
		this.bookNewwordCount = bookNewwordCount;
	}
	public boolean isNew() {
		return isNew;
	}
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	public int getPageId() {
		return pageId;
	}
	public void setPageId(int pageId) {
		this.pageId = pageId;
	}
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	public boolean isDelete() {
		return isDelete;
	}
	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}
	public int getNewwordCount() {
		return newwordCount;
	}
	public void setNewwordCount(int newwordCount) {
		this.newwordCount = newwordCount;
	}
	
	
}
