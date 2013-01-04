package com.kenny.file.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.framework.log.P;

import android.util.Log;

// //使用PSOT方式，必须用NameValuePair数组传递参数
// List<NameValuePair> nameValuePairs = new
// ArrayList<NameValuePair>();
// nameValuePairs.add(new BasicNameValuePair("id", "12345"));
// nameValuePairs.add(new
// BasicNameValuePair("stringdata","hps is Cool!"));
public class KHttpPost
{
	/**
	 *Post请求
	 * 
	 * @throws Exception
	 */
	public static InputStream doPost(String strUrl, String param)
			throws Exception
	{
		
		P.v("net", "KGroupManage:Url=" + strUrl);
		URL url = new URL(strUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// 这个要打开
		conn.setDoOutput(true);
		conn.setDoInput(true);
		// 处理POST的数据
		conn.setRequestMethod("POST");
		conn.getOutputStream().write(param.getBytes());
		conn.getOutputStream().flush();
		conn.getOutputStream().close();
		return conn.getInputStream();
		
	}
	
	/**
	 *Post请求
	 * 
	 * @throws Exception
	 */
	public static InputStream doPost(String strUrl, List<NameValuePair> params)
			throws Exception
	{
		
		HttpPost httpRequest = new HttpPost(strUrl);
		// 使用NameValuePair来保存要传递的Post参数
		// 添加要传递的参数
		params.add(new BasicNameValuePair("par", "HttpClient_android_Post"));
		// 设置字符集
		HttpEntity httpentity = new UrlEncodedFormEntity(params, "gb2312");
		// 请求httpRequest
		httpRequest.setEntity(httpentity);
		// 取得默认的HttpClient
		HttpClient httpclient = new DefaultHttpClient();
		// 取得HttpResponse
		HttpResponse httpResponse = httpclient.execute(httpRequest);
		// HttpStatus.SC_OK表示连接成功
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
		{
			// 取得返回的字符串
			String strResult = EntityUtils.toString(httpResponse.getEntity());
			// mTextView.setText(strResult);
		}
		else
		{
			// mTextView.setText("请求错误!");
		}
		return null;
	}
	
	/**
	 *Get请求
	 */
	public static void doGet(String url)
	{
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
		HttpConnectionParams.setSoTimeout(httpParams, 30000);
		
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		// GET
		HttpGet httpGet = new HttpGet(url);
		try
		{
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
			{
				Log.i("GET", "Bad Request!");
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
