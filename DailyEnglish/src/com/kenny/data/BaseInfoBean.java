package com.kenny.data;

public class BaseInfoBean {
	private String word;         // 单词
	private String cri;         // 是否是cri
	private String yinBiaoEn;    // 英式音标 
	private String yinBiaoUS;    // 美式音标
	private String enMp3;        // 英式mp3
	private String usMp3;        // 美式mp3
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getCri() {
		return cri;
	}
	public void setCri(String cri) {
		this.cri = cri;
	}
	public String getYinBiaoEn() {
		return yinBiaoEn;
	}
	public void setYinBiaoEn(String yinBiaoEn) {
		this.yinBiaoEn = yinBiaoEn;
	}
	public String getYinBiaoUS() {
		return yinBiaoUS;
	}
	public void setYinBiaoUS(String yinBiaoUS) {
		this.yinBiaoUS = yinBiaoUS;
	}
	public String getEnMp3() {
		return enMp3;
	}
	public void setEnMp3(String enMp3) {
		this.enMp3 = enMp3;
	}
	public String getUsMp3() {
		return usMp3;
	}
	public void setUsMp3(String usMp3) {
		this.usMp3 = usMp3;
	}	
}
