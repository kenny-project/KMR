package com.kenny.event;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.content.Context;

import com.kenny.Interface.INotifyDataSetChanged;
import com.kenny.file.Coder;
import com.kenny.file.SDFile;
import com.kenny.struct.AbsEvent;
import com.kenny.util.Const;
import com.kenny.util.KCommand;
import com.kenny.util.T;

/**
 * 获取文件 将messageBoard页面保存成文件
 * 
 * @author kenny 获取指定内容的参数
 * */
public class NetWebPagebyFileEvent extends AbsEvent
{
	private Context m_ctx = null;
	private INotifyDataSetChanged notify;
	private HashMap<String, Object> m_ReceiveMap = new HashMap<String, Object>();
	private String strUrl;
	private String strFileName;
	private String mLocalPath;

	public NetWebPagebyFileEvent(Context ctx, String strUrl,
			INotifyDataSetChanged notify)
	{
		super(null);
		mLocalPath = "file:///android_asset/";
		m_ReceiveMap.put("url", strUrl);
		this.strUrl = strUrl;
		this.m_ctx = ctx;
		this.notify = notify;
		strFileName = Coder.Md5(strUrl);
	}

	@Override
	public void ok()
	{
		String buffer = SDFile.ReadSDFile(strFileName);
		String fileEnds = getExName(strUrl);
		if (notify != null && buffer != null && buffer.length() > 0)
		{
			// buffer = buffer.replaceAll("./www/", mLocalPath);
			m_ReceiveMap.put("title", buffer.substring(
					buffer.indexOf("<title>")+7,
					buffer.indexOf("</title>")));
			m_ReceiveMap.put("buffer", buffer);
			notify.NotifyDataSetChanged(Const.Net_WebPagebyFile_Finish,
					m_ReceiveMap, 0, 0);
			return;
		}
		if (notify != null)
		{
			notify.NotifyDataSetChanged(Const.Net_WebPagebyFile_Run,
					m_ReceiveMap, 0, 0);
		}

		if (KCommand.isNetConnectNoMsg(m_ctx))// 非阅读
		{
			HttpURLConnection connection = null;
			try
			{
				InputStream is = null;
				URL url = new URL(strUrl + "&v=2");
				connection = (HttpURLConnection) url.openConnection();
				int code = connection.getResponseCode();
				connection.setConnectTimeout(5000);
				if (HttpURLConnection.HTTP_OK == code)
				{
					connection.connect();
					is = connection.getInputStream();
					buffer = T.StreamToString(is);
					buffer = buffer.replaceAll("./www/", mLocalPath);
					SDFile.WriteSDFile(buffer, strFileName);
					if (notify != null && buffer != null && buffer.length() > 0)
					{
						m_ReceiveMap.put("title", buffer.substring(
								buffer.indexOf("<title>")+7,
								buffer.indexOf("</title>")));
						m_ReceiveMap.put("buffer", buffer);
						notify.NotifyDataSetChanged(
								Const.Net_WebPagebyFile_Finish, m_ReceiveMap,
								0, 0);
					}
				} else
				{
					if (notify != null)
					{
						notify.NotifyDataSetChanged(
								Const.Net_WebPagebyFile_Error, m_ReceiveMap, 0,
								0);
					}
				}
			} catch (Exception e)
			{
				e.printStackTrace();
				if (notify != null)
				{
					notify.NotifyDataSetChanged(Const.Net_WebPagebyFile_Error,
							m_ReceiveMap, 0, 0);
				}
			} finally
			{

				if (connection != null)
				{
					connection.disconnect();
				}
			}
		} else
		{
			if (notify != null)
			{
				notify.NotifyDataSetChanged(Const.Net_WebPagebyFile_Error,
						m_ReceiveMap, 0, 0);
			}
		}
	}

	public static String getExName(String filename)
	{
		if ((filename != null) && (filename.length() > 0))
		{
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1)))
			{
				return filename.substring(dot + 1).toLowerCase();
			}
		}
		return filename.toLowerCase();
	}
}
