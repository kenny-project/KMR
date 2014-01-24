package com.kenny.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;

import com.kenny.data.BilingualBean;
import com.kenny.data.StoreItemBean;
import com.kenny.data.VOANetData;

/**
 * 
 * @author kenny
 */
public class VOAItemParser {
	private ArrayList<VOANetData> ItemList = null;
	private StringBuilder sb = new StringBuilder();
	private StoreItemBean rb = null;
	private int ItemID = 0;// 编号
	private boolean Itemflag = false;// 1:Item 2:version,3services
	private Context m_ctx = null;
	private String errorMsg = "";
	private String buffer = "";
	private int Pos = 0;// 总共多少分页
	private int code = 0;

	public String GetBuffer() {
		return buffer;
	}

	public String GetLastError() {
		return errorMsg;
	}

	public int GetPagePos() {
		return Pos;
	}
	public ArrayList<VOANetData> parseDataByHTTPGet(Context context, String url,String uuid, int page)
    {
        ItemList = new ArrayList<VOANetData>();
        try
        {
			String ver = "1.0";
			int size = 20;
			int cid = 3;
			String sign = MD5Calculator.calculateMD5(uuid
					+ "icibaiosclient_#&$%");
			// 先封装一个 JSON 对象
			Pos=page;
        	url=url+"&page="+page;
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
            buffer=T.StreamToString(inStream);
            return parseJokeByData(context, buffer);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return ItemList;
    }
	public ArrayList<VOANetData> parseJokeByUrl(Context context, String url,
			String uuid, int page) {
		ItemList = new ArrayList<VOANetData>();
		HttpPost request = new HttpPost(url);
		try {
			String ver = "1.0";
			int size = 20;
			int cid = 3;
			String sign = MD5Calculator.calculateMD5(uuid
					+ "icibaiosclient_#&$%");
			// 先封装一个 JSON 对象
			JSONObject param = new JSONObject();
			param.put("uuid", uuid);
			param.put("cid", cid);
			param.put("ver", ver);
			param.put("page", page);
			param.put("size", size);
			param.put("sign", sign);
		    List <NameValuePair> params=new ArrayList<NameValuePair>();
		    params.add(new BasicNameValuePair("name","this is post"));
			// 绑定到请求 Entry
			StringEntity se = new StringEntity(param.toString());
			//request.setEntity(se);
			request.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			// 发送请求
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				long len=httpResponse.getEntity().getContentLength();
				Log.v("wmh","len="+len);
				
				// 得到应答的字符串，这也是一个 JSON 格式保存的数据
				String retSrc = EntityUtils.toString(httpResponse.getEntity());
				// 生成 JSON 对象
				return parseJokeByData(context, retSrc);
			} else {
				return ItemList;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ItemList;
	}

	public int getCode() {
		return code;
	}
	public int GetPageCount() {
		return page_total;
	}

	private int page_total=1;// 总共多少分页
	public ArrayList<VOANetData> parseJokeByData(Context context, String Data) {
		ItemList = new ArrayList<VOANetData>();
		try {
			m_ctx = context;
			buffer = Data;
			// 生成 JSON 对象
			JSONObject result = new JSONObject(Data);
			page_total=result.getInt("total");
			
			JSONArray mJsonArray = result.getJSONArray("list");

			for (int i = 0; i < mJsonArray.length(); i++) {
				VOANetData data = new VOANetData();
				JSONObject jo = (JSONObject) mJsonArray.get(i);
				// 2010-12-12
				data.setId(jo.getInt("id"));
				data.setTitle(jo.getString("title"));
				data.setCntitle(jo.getString("cntitle"));
				data.setSmallpic(jo.getString("smallpic"));
//				data.setSummary(jo.getString("summary"));
//				data.setThumbnail(jo.getString("thumbnail"));
				data.setPublish(jo.getString("pubdate"));
				data.setArticleurl(jo.getString("articleurl"));
//				data.setIsfree(jo.getInt("isfree"));
				
				data.setFeedback(jo.getInt("feedback"));
				data.setDig(jo.getInt("dig"));
				data.setViews(jo.getInt("views"));
				ItemList.add(data);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ItemList;
	}
}
