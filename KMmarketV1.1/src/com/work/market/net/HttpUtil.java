package com.work.market.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.work.market.util.CONST;

/**
 * 网络工具类
 * 
 * @author 
 * 
 */
public class HttpUtil {

	/**
	 * get 请求
	 * 
	 * @param context
	 * @param url
	 * @return
	 */

	// public static String temp;
	// public static String temp1;
	// public static String temp2;

	public static String requestByGet(Context context, String url) {
		if (!checkNet(context)) {
			return null;
		}
		String result = null;
		HttpGet request = new HttpGet(url);
		try {
			HttpResponse response = request(context, request);
			Log.i("TAG", "http get url:" + url);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
				Log.i("TAG", "http result:" + result);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * post 请求
	 * 
	 * @param context
	 * @param url
	 * @return
	 */
	public static String requestByPost(Context context, String url,
			ArrayList<NameValuePair> params) {
		if (!checkNet(context)) {
			return null;
		}
		String result = null;
		HttpPost request = new HttpPost(url);
		try {
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			Log.i("TAG", "http post url:" + url);
			Log.i("TAG", "http post params:" + getParamsString(params));
			HttpResponse response = request(context, request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
				Log.i("TAG", "http result:" + result);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * get 请求
	 * 
	 * @param context
	 * @param url
	 * @return
	 */
	// public static String requestByPost(Context context, String url,
	// ArrayList<NameValuePair> params, HashMap<String, byte[]> dataMap) {
	// if(!checkNet(context)) {
	// return null;
	// }
	// String result = null;
	// HttpPost request = new HttpPost(url);
	// try {
	// MultipartEntity entity = new MultipartEntity();
	// for(int i = 0; i < params.size(); i++) {
	// NameValuePair param = params.get(i);
	// entity.addPart(param.getName(), new StringBody(param.getValue(),
	// Charset.forName(HTTP.UTF_8)));
	// }
	// for(Entry<String, byte[]> entry : dataMap.entrySet()) {
	// if(entry.getValue() != null) {
	// String name = entry.getKey();
	// name = name.substring(0, name.indexOf("."));
	// entity.addPart(name, new ByteArrayBody(entry.getValue(),
	// entry.getKey()));
	// }
	// }
	// request.setEntity(entity);
	// Log.i("TAG", "http post url:" + url);
	// Log.i("TAG", "http post params:" + getParamsString(params));
	// HttpResponse response = request(context, request);
	// if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	// result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
	// Log.i("TAG", "http result:" + result);
	// }
	// } catch (Exception ex) {
	//
	// ex.printStackTrace();
	// }
	// return result;
	// }

	/**
	 * 日志输出
	 * 
	 * @param params
	 * @return
	 */
	private static String getParamsString(ArrayList<NameValuePair> params) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < params.size(); i++) {
			NameValuePair pair = params.get(i);
			result.append("---");
			result.append(pair.getName());
			result.append(":");
			result.append(pair.getValue());
		}
		return result.toString();
	}

	/**
	 * 代理设置
	 * 
	 * @param context
	 * @return
	 */
	private static HttpResponse request(Context context, HttpRequestBase request)
			throws ClientProtocolException, IOException {
		HttpResponse response = null;
		DefaultHttpClient httpClient = new DefaultHttpClient();
		if (isCMWAP(context)) {
			try {
				HttpHost proxy = new HttpHost("10.0.0.172", 80, "http");
				httpClient.getParams().setParameter(
						ConnRoutePNames.DEFAULT_PROXY, proxy);
				response = httpClient.execute(request);
			} catch (Exception ex) {

			}
		} else {
			response = httpClient.execute(request);
		}
		return response;
	}

	/**
	 * 联网状态
	 * 
	 * @param context
	 * @return
	 */
	private static boolean isCMWAP(Context context) {
		boolean isCMWAP = false;
		try {
			ConnectivityManager con = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = con.getActiveNetworkInfo();
			if (networkInfo != null
					&& "WIFI".equals(networkInfo.getTypeName().toUpperCase())) {
				return isCMWAP;
			} else {
				Cursor cursor = context.getContentResolver().query(
						Uri.parse("content://telephony/carriers/preferapn"),
						new String[] { "apn" }, null, null, null);
				cursor.moveToFirst();
				if (cursor.isAfterLast()) {
					isCMWAP = false;
				}
				try {
					if ("cmwap".equals(cursor.getString(0))
							|| "uniwap".equals(cursor.getString(0))) {
						isCMWAP = true;
					} else {
						isCMWAP = false;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				} finally {
					if (cursor != null) {
						cursor.close();
					}
				}
				return isCMWAP;
			}
		} catch (Exception ex) {

		}
		return isCMWAP;
	}

	/**
	 * 联网状态
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager con = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = con.getActiveNetworkInfo();
		if (networkInfo != null
				&& "WIFI".equals(networkInfo.getTypeName().toUpperCase())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 联网检查
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNet(Context context) {
		try {
			ConnectivityManager con = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = con.getActiveNetworkInfo();
			if (networkInfo == null || !networkInfo.isAvailable()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String RequestGetDatas(String aurl, String aid, String adata,
			String scro) {
		String returnStr = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(aurl);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("updates", "{\"score\":"
					+ scro + ",\"content\":\"" + adata + "\"}"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));
			HttpResponse response = httpclient.execute(httppost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				returnStr = EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStr;
	}

	public static String RequestGetData(String aurl, String adata) {
		String returnStr = null;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(aurl);
		
		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("updates", adata));// 鍚嶅瓧
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));
			HttpResponse response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

				returnStr = EntityUtils.toString(response.getEntity());

			} else {
				Log.e("http", "error:response.getStatusLine().getStatusCode()="
						+ response.getStatusLine().getStatusCode());
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return returnStr;
	}

	public static String doGet(String url) {
		String returnStr = null;
		HttpClient httpClient = new DefaultHttpClient();
		// GET
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) 
			{
				Log.i("GET", "Bad Request!");
				returnStr = EntityUtils.toString(response.getEntity(),
						HTTP.UTF_8);

			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.v("wmh", "error:url=" + url);
		}
		return returnStr;

	}

	public static String GetPhoto5(String aurl, String alog) {
		String returnStr = null;
		try {

			URL url = new URL(aurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				{
					SaveListPic5(conn.getInputStream(), alog);
					returnStr = "ok";
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return returnStr;
	}

	private static void SaveListPic5(InputStream inputStream, String aName) {

		File folderPathFile = new File(CONST.ONPATH);

		if (!folderPathFile.exists()) {
			folderPathFile.mkdirs();
		}
		File file = new File(CONST.ONPATH, aName);
		try {
			OutputStream out = new FileOutputStream(file);
			byte buf[] = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			inputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
