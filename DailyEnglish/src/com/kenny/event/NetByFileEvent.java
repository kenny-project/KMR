package com.kenny.event;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.util.ByteArrayBuffer;

import android.content.Context;
import android.util.Log;

import com.kenny.Interface.INotifyDataSetChanged;
import com.kenny.file.Coder;
import com.kenny.file.SDFile;
import com.kenny.struct.AbsEvent;
import com.kenny.util.Const;
import com.kenny.util.KCommand;
import com.kenny.util.Utils;

/**
 * 获取文件 将messageBoard页面保存成文件
 * 
 * @author kenny 获取指定内容的参数
 * */
public class NetByFileEvent extends AbsEvent
{
	private Context m_ctx = null;
	private INotifyDataSetChanged notify;
	private HashMap<String, Object> m_ReceiveMap = new HashMap<String, Object>();
	private String strUrl;
	private String strFileName;

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

	public NetByFileEvent(Context ctx, String strUrl,
			INotifyDataSetChanged notify)
	{
		super(null);
		m_ReceiveMap.put("url", strUrl);
		String fileEnds = getExName(strUrl);
		m_ReceiveMap.put("end", fileEnds);
		this.strUrl = strUrl;
		this.m_ctx = ctx;
		this.notify = notify;
		strFileName = Coder.Md5(strUrl);
		m_ReceiveMap.put("path", SDFile.filePath + strFileName);
	}

	@Override
	public void ok()
	{
		byte[] buffer = SDFile.ReadSDByteFile(strFileName);
		if (!SDFile.checkSDCard()||Utils.getAvailableExternalMemorySize()<50000)//10K
		{
			if (notify != null)
			{
				notify.NotifyDataSetChanged(Const.Net_WebbyFile_Run,
						m_ReceiveMap, 0, 0);
				m_ReceiveMap.put("result", 0);
				notify.NotifyDataSetChanged(Const.Net_WebbyFile_Finish,
						m_ReceiveMap, 0, 0);
			}
			return;
		}

		if (notify != null && buffer != null && buffer.length > 0)
		{
			m_ReceiveMap.put("result", 1);

			notify.NotifyDataSetChanged(Const.Net_WebbyFile_Finish,
					m_ReceiveMap, 0, 0);
			return;
		}

		if (KCommand.isNetConnectNoMsg(m_ctx))// 非阅读
		{
			HttpURLConnection connection = null;
			try
			{
				if (notify != null)
				{
					notify.NotifyDataSetChanged(Const.Net_WebbyFile_Run,
							m_ReceiveMap, 0, 0);
				}
				Log.v("wmh", "NetByEvent:start");
				InputStream is = null;
				URL url = new URL(strUrl);
				connection = (HttpURLConnection) url.openConnection();
				int code = connection.getResponseCode();
				connection.setConnectTimeout(5000);
				if (HttpURLConnection.HTTP_OK == code)
				{
					connection.connect();
					is = connection.getInputStream();

					BufferedInputStream bis = new BufferedInputStream(is);
					ByteArrayBuffer bab = new ByteArrayBuffer(32);
					buffer = new byte[1024];
					int len = 0;
					while ((len = bis.read(buffer)) != -1)
					{
						bab.append(buffer, 0, len);
					}
					Log.v("wmh", "NetByEvent:end");
					SDFile.WriteSDByteFile(bab.buffer(), strFileName);
					if (notify != null && buffer != null && buffer.length > 0)
					{
						m_ReceiveMap.put("result", 1);
						notify.NotifyDataSetChanged(Const.Net_WebbyFile_Finish,
								m_ReceiveMap, 0, 0);
					}
				} else
				{
					if (notify != null)
					{
						notify.NotifyDataSetChanged(Const.Net_WebbyFile_Error,
								m_ReceiveMap, 0, 0);
					}
				}
			} catch (Exception e)
			{
				e.printStackTrace();
				if (notify != null)
				{
					notify.NotifyDataSetChanged(Const.Net_WebbyFile_Error,
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
				notify.NotifyDataSetChanged(Const.Net_WebbyFile_Error,
						m_ReceiveMap, 0, 0);
			}
		}
	}
}
