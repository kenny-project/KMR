package com.kenny.data;


public class VOANetData {
	private int id;
	private String title;
	private String summary;
	private String smallpic;
	private String cntitle;
	private String thumbnail;
	private String publish;
	private String articleurl;
	private int feedback;
	private int dig;
	private int views;
	private boolean isfree=true;
	public String getSmallpic() {
		return smallpic;
	}
	public void setSmallpic(String smallpic) {
		this.smallpic = smallpic;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public int getFeedback() {
		return feedback;
	}
	public void setFeedback(int feedback) {
		this.feedback = feedback;
	}
	public int getDig() {
		return dig;
	}
	public void setDig(int dig) {
		this.dig = dig;
	}

	
	public boolean isIsfree() {
		return isfree;
	}
	public void setIsfree(int isfree) 
	{
		this.isfree = isfree==1;
	}
	public void setIsfree(boolean isfree) {
		this.isfree = isfree;
	}
	public int getViews() {
		return views;
	}
	public void setViews(int views) {
		this.views = views;
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
	public String getCntitle() {
		return cntitle;
	}
	public void setCntitle(String cntitle) {
		this.cntitle = cntitle;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getPublish() {
		return publish;
	}
	public void setPublish(String publish) {
		this.publish = publish;
	}
	public String getArticleurl() {
		return articleurl;
	}
	public void setArticleurl(String articleurl) {
		this.articleurl = articleurl;
	}

}
