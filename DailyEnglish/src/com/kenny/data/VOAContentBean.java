package com.kenny.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VOAContentBean {
	private int id;
	private String title;
	private String cntitle;
	private String thumbnail;
	private String publish;
	private List<String> content;
	private ArrayList<Integer> LrcTimes = new ArrayList<Integer>();
	private String cncontent;
	private String mp3_url;

	public List<String> getContent() {
		return content;
	}

	public int getTime(int pos) {
		if (LrcTimes.size() > 0) 
		{
			if (LrcTimes.size() > pos) 
			{
				return LrcTimes.get(pos);
			}
			return LrcTimes.get(LrcTimes.size() - 1);
		}
		else 
		{
			return 0;
		}
	}

	/**
	 * 获得当前播放的位置
	 * 
	 * @return
	 */
	public int getTimeToPlayPos(int time) {
		if (LrcTimes.size() > 0) 
		{
			for (int i = 0; i < LrcTimes.size(); i++) 
			{
				if (LrcTimes.get(i) > time) 
				{
					return i - 1;
				}
			}
			return LrcTimes.size() - 1;
		}
		return -1;
	}

	public void setContent(List<String> content) {
		this.content = content;
	}

	public String getCncontent() {
		return cncontent;
	}

	public void setCncontent(String cncontent) {
		this.cncontent = cncontent;
	}

	public void setLrcurlData(String LrcurlData) {
		LrcTimes.clear();
		try {
			Read(LrcurlData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Read(String data) throws FileNotFoundException, IOException {

		String Lrc_data = "";
		String[] dataArray = data.split("\n");
		for (int i = 0; i < dataArray.length; i++) {
			Lrc_data = dataArray[i].replace("[", "");
			Lrc_data = Lrc_data.replace("]", "@");
			String splitLrc_data[] = Lrc_data.split("@");
			if (splitLrc_data.length > 1) {
				int LyricTime = TimeStr(splitLrc_data[0]);
				LrcTimes.add(LyricTime);
			}
		}
	}

	public int TimeStr(String timeStr) {

		// 针对特殊情况特殊处理
		// 如果形如00:00.00（这里是9个字符）的样子 特殊处理
		if (timeStr.trim().equals("﻿00:00.00") == true) {
			timeStr = "00:00.01";
		}

		timeStr = timeStr.replace(":", ".");
		timeStr = timeStr.replace(".", "@");

		String timeData[] = timeStr.split("@");

		int minute = Integer.parseInt(timeData[0]);
		int second = Integer.parseInt(timeData[1]);
		int millisecond = Integer.parseInt(timeData[2]);

		int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;

		return currentTime;
	}

	public String getMp3_url() {
		return mp3_url;
	}

	public void setMp3_url(String mp3_url) {
		this.mp3_url = mp3_url;
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

}
