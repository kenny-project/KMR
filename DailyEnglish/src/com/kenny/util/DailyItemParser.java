package com.kenny.util;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.kenny.data.DailyNetData;
import com.kenny.data.StoreItemBean;

/**
 * 
 * @author kenny
 */
public class DailyItemParser {
	private ArrayList<DailyNetData> ItemList = null;
	private StringBuilder sb = new StringBuilder();
	private StoreItemBean rb = null;
	private int ItemID = 0;// 编号
	private boolean Itemflag = false;// 1:Item 2:version,3services
	private Context m_ctx = null;
	private String errorMsg = "";
	private String buffer = "";
	private int Pos = 0;// 总共多少分页
	private int page_total=20;// 总共多少分页
	public String GetBuffer() {
		return buffer;
	}

	public String GetLastError() {
		return errorMsg;
	}

	public int GetPagePos() {
		return Pos;
	}
	public int GetPageCount() {
		return page_total;
	}
    public ArrayList<DailyNetData> parseJokeByUrl(Context context, String url,int index,int count)
    {
        ItemList = new ArrayList<DailyNetData>();
		HttpPost request = new HttpPost(url);
		try {
			// 先封装一个 JSON 对象
			JSONObject param = new JSONObject();
			param.put("pageindex", index);
			param.put("pagecount", count);

			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			request.setEntity(se);
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			// 得到应答的字符串，这也是一个 JSON 格式保存的数据
			String retSrc = EntityUtils.toString(httpResponse.getEntity());
			buffer=retSrc;
			// 生成 JSON 对象
			JSONObject result = new JSONObject(retSrc);

			JSONArray mJsonArray = result.getJSONArray("Results");

			for (int i = 0; i < mJsonArray.length(); i++) {
				DailyNetData data = new DailyNetData();
				JSONObject jo = (JSONObject) mJsonArray.get(i);
				String title = jo.getString("title");// title 为单条日期
														// 2010-12-12
				data.setDate(title);
				data.setPicture(jo.getString("picture"));
				data.setContent(jo.getString("content"));
				data.setSid(jo.getString("sid"));
				data.setTts(jo.getString("tts"));
				data.setTranslation(jo.getString("translation"));
				data.setNote(jo.getString("note"));
				data.setTitle(jo.getString("title"));
				ItemList.add(data);
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
        return ItemList;
    }
	public ArrayList<DailyNetData> parseJokeByUrl(Context context, String url) {
		ItemList = new ArrayList<DailyNetData>();

		try {
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
			HttpConnectionParams.setSoTimeout(httpParams, 30000);

			HttpClient httpClient = new DefaultHttpClient(httpParams);
			// GET
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				Log.i("GET", "Bad Request!");
				String strResult = EntityUtils.toString(response.getEntity());
				return ItemList;
			}

			// 得到应答的字符串，这也是一个 JSON 格式保存的数据
			String retSrc = EntityUtils.toString(response.getEntity());
			buffer = retSrc;
			return parseJokeByData(context,buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ItemList;
	}

	public ArrayList<DailyNetData> parseJokeByData(Context context, String Data) {
		ItemList = new ArrayList<DailyNetData>();
		try {
			m_ctx = context;
			// 生成 JSON 对象
			JSONObject result = new JSONObject(Data);
//			page_total=result.getInt("page_total");//TotalPages参数未用
//			JSONArray mJsonArray = result.getJSONArray("list");
			JSONArray mJsonArray = result.getJSONArray("Results");
			for (int i = 0; i < mJsonArray.length(); i++) {
				DailyNetData data = new DailyNetData();
				JSONObject jo = (JSONObject) mJsonArray.get(i);
				String title = jo.getString("title");// title 为单条日期
														// 2010-12-12
				data.setTitle(title);
				data.setDate(title);
				data.setPicture(jo.getString("picture"));
				data.setContent(jo.getString("content"));
				data.setSid(jo.getString("sid"));
				data.setTts(jo.getString("tts"));
				data.setTranslation(jo.getString("translation"));
				data.setNote(jo.getString("note"));
				ItemList.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ItemList;
	}
}
