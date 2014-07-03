package com.work.market.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.byfen.market.bean.BanBean;
/**
 * Ö÷Ò³µÄbanÒ³
 * @author WangMinghui
 */
public class KHomeBanItemParser
{
	public List<BanBean> parseStringByData(InputStream is) throws IOException
	{
		List<BanBean> mList = new ArrayList<BanBean>();
		int len = -1;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer=new byte[1000];
		while ((len = is.read(buffer)) != -1) 
		{
			baos.write(buffer,0,len);
		}
		String content = baos.toString();
		return parseStringByData(content,mList);		
	}
	public List<BanBean> parseStringByData(String result)
	{
		List<BanBean> mList = new ArrayList<BanBean>();
		return parseStringByData(result,mList);
	}
	private String buffer;
	public String GetBuffer()
	{
		return buffer;
	}
	public List<BanBean> parseStringByData(String result,List<BanBean> mList)
	{
		try
		{
			buffer=result;
			JSONObject jsresult = new JSONObject(result);//
			JSONArray jsonObj1 = jsresult.getJSONArray("list");
			for (int i = 0; i < jsonObj1.length(); i++)
			{
				JSONObject tempJson = jsonObj1.optJSONObject(i);
				BanBean bean = new BanBean();
				bean.setSrc(tempJson.getString("src"));
				bean.setTitle(tempJson.getString("title"));
				bean.setType(tempJson.getString("type"));
				bean.setId(tempJson.getInt("id"));//
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
