package com.kenny.event;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.kenny.Interface.INotifyDataSetChanged;
import com.kenny.data.DailySentencebean;
import com.kenny.file.Coder;
import com.kenny.file.SDFile;
import com.kenny.struct.AbsEvent;
import com.kenny.util.Const;
import com.kenny.util.KCommand;
import com.kenny.util.T;
import com.kenny.xml.DailySentenceParser;

/**
 * 获取文件 将messageBoard页面保存成文件
 * 
 * @author kenny 获取指定内容的参数
 * */
public class NetXmlPagebyFileEvent extends AbsEvent {
	private Context m_ctx = null;
	private INotifyDataSetChanged notify;
	private HashMap<String, Object> m_ReceiveMap = new HashMap<String, Object>();
	private String strUrl;
	private String strFileName;
	private String mLocalPath;
	private DailySentencebean bean = null;
	DailySentenceParser parser = new DailySentenceParser();

	public NetXmlPagebyFileEvent(Context ctx, String strUrl,
			INotifyDataSetChanged notify) {
		super(null);
		mLocalPath = "file:///android_asset/";
		m_ReceiveMap.put("url", strUrl);

		this.strUrl = strUrl + "&v=3";
		this.m_ctx = ctx;
		this.notify = notify;
		strFileName = Coder.Md5(strUrl);
	}

	@Override
	public void ok() {
		String buffer = SDFile.ReadSDFile(strFileName);
		if (notify != null && buffer != null && buffer.length() > 0) {
			List<DailySentencebean> itemList = parser.parseStringByData(m_ctx,
					buffer);
			if (itemList.size() > 0) {
				bean = itemList.get(0);
				m_ReceiveMap.put("bean", bean);
				m_ReceiveMap.put("ec", bean.getEc());
				m_ReceiveMap.put("ce", bean.getCe());
				m_ReceiveMap.put("title", bean.getTitle());
				m_ReceiveMap.put("buffer", bean.toString());
				notify.NotifyDataSetChanged(Const.Net_WebPagebyFile_Finish,
						m_ReceiveMap, 0, 0);
				return;
			}
		}
		if (notify != null) {
			notify.NotifyDataSetChanged(Const.Net_WebPagebyFile_Run,
					m_ReceiveMap, 0, 0);
		}

		if (KCommand.isNetConnectNoMsg(m_ctx))// 非阅读
		{
			HttpURLConnection connection = null;
			try {
				InputStream is = null;
				Log.v("wmh", "NetXmlPagebyFileEvent:start");
				URL url = new URL(strUrl);
				connection = (HttpURLConnection) url.openConnection();
				int code = connection.getResponseCode();
				connection.setConnectTimeout(5000);
				if (HttpURLConnection.HTTP_OK == code) {
					connection.connect();
					is = connection.getInputStream();
					buffer = T.StreamToString(is);
					Log.v("wmh", "NetXmlPagebyFileEvent:end");
					SDFile.WriteSDFile(buffer, strFileName);
					if (notify != null && buffer != null && buffer.length() > 0) {
						List<DailySentencebean> itemList = parser
								.parseStringByData(m_ctx, buffer);
						if (itemList.size() > 0) {
							bean = itemList.get(0);
							m_ReceiveMap.put("bean", bean);
							m_ReceiveMap.put("title", bean.getTitle());
							m_ReceiveMap.put("buffer", bean.toString());
							m_ReceiveMap.put("ec", bean.getEc());
							m_ReceiveMap.put("ce", bean.getCe());
							notify.NotifyDataSetChanged(
									Const.Net_WebPagebyFile_Finish,
									m_ReceiveMap, 0, 0);
							return;
						} else {
							if (notify != null) {
								notify.NotifyDataSetChanged(
										Const.Net_WebPagebyFile_Error,
										m_ReceiveMap, 0, 0);
							}
						}
					}
				} else {
					if (notify != null) {
						notify.NotifyDataSetChanged(
								Const.Net_WebPagebyFile_Error, m_ReceiveMap, 0,
								0);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (notify != null) {
					notify.NotifyDataSetChanged(Const.Net_WebPagebyFile_Error,
							m_ReceiveMap, 0, 0);
				}
			} finally {

				if (connection != null) {
					connection.disconnect();
				}
			}
		} else {
			if (notify != null) {
				notify.NotifyDataSetChanged(Const.Net_WebPagebyFile_Error,
						m_ReceiveMap, 0, 0);
			}
		}
	}
}
