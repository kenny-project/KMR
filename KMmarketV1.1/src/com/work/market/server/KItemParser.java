package com.work.market.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.work.market.bean.AppListBean;
import com.work.market.net.Common;

public class KItemParser
{
	private int m_Max_page=1,m_now_page=1;
	public int  getMaxpage()
	{
		return m_Max_page;
	}
	public int  getNowpage()
	{
		return m_now_page;
	}
	public List<AppListBean> parseStringByData(InputStream is) throws IOException
	{
		List<AppListBean> mList = new ArrayList<AppListBean>();
		int len = -1;
		//org.apache.commons.io.output.ByteArrayOutputStream
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer=new byte[1000];
		while ((len = is.read(buffer)) != -1) 
		{
			baos.write(buffer,0,len);
		}
		String content = baos.toString();
		return parseStringByData(content,mList);		
	}
	public List<AppListBean> parseStringByData(String result)
	{
		List<AppListBean> mList = new ArrayList<AppListBean>();
		return parseStringByData(result,mList);
	}
	private String buffer;
	public String GetBuffer()
	{
		return buffer;
	}
	public List<AppListBean> parseStringByData(String result,List<AppListBean> mList)
	{
		try
		{
			buffer=result;
			mList.clear();
			JSONObject jsresult = new JSONObject(result);//
			String maxpage = jsresult.getString("last_page");//
			String nowpage = jsresult.getString("cur_page");//
			m_Max_page = Integer.parseInt(maxpage);
			m_now_page = Integer.parseInt(nowpage);
			JSONArray jsonObj1 = jsresult.getJSONArray("list");
			for (int i = 0; i < jsonObj1.length(); i++)
			{
				JSONObject tempJson = jsonObj1.optJSONObject(i);
				AppListBean bean = new AppListBean();
				bean.setId(tempJson.getString("id"));
				bean.setTitle( tempJson.getString("title"));
				bean.setPn(tempJson.getString("pn"));
				bean.setLogourl(tempJson.getString("logo"));
				bean.setSize(Common.getLength(tempJson.getString("size")));
				bean.setScore(tempJson.getString("score"));
				bean.setAppurl(tempJson.getString("apkurl"));
				bean.setDowntiems(tempJson.getString("dc"));
				bean.setAppFileExt(tempJson.getString("ext"));
				mList.add(bean);
			}
			return mList;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
