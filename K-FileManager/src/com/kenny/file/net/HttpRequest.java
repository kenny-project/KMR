package com.kenny.file.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import android.util.Log;
// //使用PSOT方式，必须用NameValuePair数组传递参数
// List<NameValuePair> nameValuePairs = new
// ArrayList<NameValuePair>();
// nameValuePairs.add(new BasicNameValuePair("id", "12345"));
// nameValuePairs.add(new
// BasicNameValuePair("stringdata","hps is Cool!"));
public class HttpRequest
{
    /**
     *Post请求
     */
    public byte[] doPost(String url, List<NameValuePair> nameValuePairs)
    {
        // 新建HttpClient对象
        HttpClient httpclient = new DefaultHttpClient();
        // 创建POST连接
        HttpPost httppost = new HttpPost(url);
        try
        {

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            // ----------------获取数据-----------------------
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            Long len=entity.getContentLength();
            
            byte[] bcontent=new byte[len.intValue()];
            content.read(bcontent, 0, len.intValue());
            return bcontent;
            //String strResult = convertStreamToString(content);
            //return strResult;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     *Get请求
     */
    public void doGet(String url)
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
    private String convertStreamToString(InputStream is)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                sb.append(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
