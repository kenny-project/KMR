package com.kenny.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.kenny.data.VOAContentBean;

/**
 * 每日一句
 * 
 * @author kenny
 */
public class VOAContentParser {
	private String errorMsg = "";
	private String buffer;

	public String GetBuffer() {
		return buffer;
	}

	public String GetLastError() {
		return errorMsg;
	}

	public VOAContentBean parseByUrl(Context context, String url) {
		try {
			URL urlStream = new URL(url);
			// 创建URL连接
			URLConnection connection;
			connection = urlStream.openConnection();
			// 设置参数
			connection.setConnectTimeout(10000);
			connection.addRequestProperty("User-Agent", "J2me/MIDP2.0");
			// 连接服务器
			connection.connect();
			InputStream inStream = connection.getInputStream();
			buffer = T.StreamToString(inStream);
			return parseStringByData(context, buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public VOAContentBean parseStringByData(Context context, String Data) {
		try {
			buffer = Data;
			// 生成 JSON 对象
			JSONObject jo = new JSONObject(Data);
//			int code = jo.getInt("code");// title 为单条日期
//			if (code == 0) 
			{
//				JSONObject jo = result.getJSONObject("result");

				VOAContentBean data = new VOAContentBean();
				data.setId(jo.getInt("id"));
				data.setTitle(jo.getString("title"));
				data.setCntitle(jo.getString("cntitle"));
				//data.setThumbnail(jo.getString("thumbnail"));
				JSONArray  joarray =jo.getJSONArray("cncontent");
				JSONArray  contentArray =jo.getJSONArray("content");
				ArrayList<String> list=new ArrayList<String>();
				int count =joarray.length()<contentArray.length()?joarray.length():contentArray.length();
				for (int i=0;i<count;i++) 
				{
//					con+=contentArray.get(i);
					list.add(contentArray.get(i)+"\n"+joarray.getString(i));
				}
				data.setContent(list);
				//data.setCncontent(jo.getString("cncontent"));
				data.setPublish(jo.getString("pubdate"));
				//data.setLrcurlData(jo.getString("lrccontent"));
				data.setLrcurlData("");
				data.setMp3_url(jo.getString("mp3"));
				return data;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
