package com.kenny.data;

public class SyncBookBean {
	int bookId;  // 生词本id，sid
	String bookName; // 生词本名
	String bookType; // 生词本类型
	long bookTime; // 生词本修改时间
	int wordCount = 0; // 生词本里单词数
	boolean syncFinish = false; // 是否同步完
	int syncWordCount = 0; // 已经同步完的单词数
	
	public long getBookTime() {
		return bookTime;
	}
	public void setBookTime(long bookTime) {
		this.bookTime = bookTime;
	}
	public int getSyncWordCount() {
		return syncWordCount;
	}
	public void setSyncWordCount(int syncWordCount) {
		this.syncWordCount = syncWordCount;
	}
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public String getBookType() {
		return bookType;
	}
	public void setBookType(String bookType) {
		this.bookType = bookType;
	}
	public int getWordCount() {
		return wordCount;
	}
	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}
	public boolean isSyncFinish() {
		return syncFinish;
	}
	public void setSyncFinish(boolean syncFinish) {
		this.syncFinish = syncFinish;
	}
}
