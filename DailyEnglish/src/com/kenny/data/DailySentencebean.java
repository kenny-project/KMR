package com.kenny.data;

import com.kenny.file.SDFile;

public class DailySentencebean 
{
	private String id;
	private String title;
	private String time;
	private String img;
	private String mp3;
	private String mp3size;
	private String ec;
	private String ce;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getMp3() {
		return mp3;
	}
	public void setMp3(String mp3) {
		this.mp3 = mp3;
	}
	public String getMp3size() {
		return mp3size;
	}
	public void setMp3size(String mp3size) {
		this.mp3size = mp3size;
	}
	public String getEc() {
		return ec;
	}
	public void setEc(String ec) {
		this.ec = ec;
	}
	public String getCe() {
		return ce;
	}
	public void setCe(String ce) {
		this.ce = ce;
	}
	@Override
	public String toString()
	{
		StringBuffer value=new StringBuffer();
		value.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		value.append("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head>");
		value.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title></title>");
		value.append("<link href=\"file:///android_asset/css/news.css\" rel=\"stylesheet\" type=\"text/css\" />");
		value.append("<script src=\"file:///android_asset/js/iphone.js\" charset=\"utf-8\" type=\"text/javascript\" language=\"javascript\"></script>");
		value.append("</head>");
		value.append("<body><h1 class=\"title\">"+this.title+"</h1>");
		value.append("<div class=\"time\"><span>"+this.time+"</span><span class=\"fr\">来源：每日一句</span></div>");
		value.append("<div class=\"line\"></div><div class=\"cont\">");
		//value.append("<img alt=\"词霸每日一句\" src=\""+this.img+"\" />");

        if (SDFile.getSDWebViewLogoFileExits(img))
        {
        	value.append("<img alt=\"词霸每日一句\" src=\"file://"+SDFile.getSDLogoFilePath(this.img)+"\" />");
        }
		value.append("<p class=\"e\">"+ec+"<a title=\"TTS发音\" href=\""+this.mp3+"\" class=\"laba\"></a>"+this.mp3size+"</p>");
		value.append("<p class=\"c\">"+ce+"</p>");
		value.append("</div></body></html>");
		return value.toString();
	}
}
