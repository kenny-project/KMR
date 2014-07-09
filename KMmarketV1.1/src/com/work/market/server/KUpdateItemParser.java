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

public class KUpdateItemParser
{
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
			JSONArray jsresult = new JSONArray(result);
			for (int i = 0; i < jsresult.length(); i++)
			{
				JSONObject tempJsonPG = jsresult.optJSONObject(i);
				JSONArray jsonobjList = tempJsonPG.getJSONArray("list");
				for (int j = 0; j < jsonobjList.length(); j++)
				{
					JSONObject tempJson2 = jsonobjList.optJSONObject(j);
					AppListBean bean = new AppListBean();
					bean.setId(tempJson2.getString("id"));
					bean.setTitle(tempJson2.getString("title"));
					bean.setPn(tempJson2.getString("pn"));
					bean.setDesc(tempJson2.getString("desc"));
					bean.setLogo(tempJson2.getString("logo"));
					bean.setSize(tempJson2
							.getString("size"));
					bean.setVercode(tempJson2.getString("vercode"));
					bean.setScore(tempJson2.getString("score"));
					bean.setAppurl(tempJson2.getString("apkurl"));
					bean.setDowntiems(tempJson2.getString("dc"));
					bean.setVername(tempJson2.getString("ver"));
					bean.setAppFileExt(tempJson2.getString("ext"));
					mList.add(bean);
				}
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
