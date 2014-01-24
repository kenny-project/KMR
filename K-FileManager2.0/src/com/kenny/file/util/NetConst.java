package com.kenny.file.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.kenny.KFileManager.R;

public class NetConst
{
	private static String webSideUrl = "";
	private static String webSideDataUrl = "";
	private static String webUrl = "";
	private static Context m_ctx = null;
	
	public static void SetContext(Context ctx)
	{
		m_ctx = ctx;
		if (webSideDataUrl == "")
		{
			if (webSideDataUrl.equals(""))
			{
				if (ctx != null) m_ctx = ctx;
				webSideDataUrl = m_ctx.getString(R.string.websidedata);
			}
		}
		if (webSideUrl != null)
		{
			// webSideUrl = SaveData.readPreferencesString(ctx, "webside", "");
			if (webSideUrl != null && webSideUrl.equals(""))
			{
				webSideUrl = m_ctx.getString(R.string.webside);
			}
		}
	}
	
	/**
	 * 获得网络地址
	 * 
	 * @param inActivity
	 * @return 返回服务器访问地址
	 */
	public static String WebSide()
	{
		
		return webSideUrl;
	}
	
	/**
	 * 获得网络地址
	 * 
	 * @param inActivity
	 * @return 返回服务器访问地址
	 */
	public static String WebSideData()
	{
		return webSideDataUrl;
	}
	// 判断是否有可用的网络连接
		public static boolean isNetConnect(Context context)
		{
			try
			{
				ConnectivityManager connManager = (ConnectivityManager) (context)
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = connManager.getActiveNetworkInfo();
				/******* EMULATOR HACK - false condition needs to be removed *****/
				if ((netInfo == null || netInfo.isConnected() == false))
				{
					// SendMessage((context), "No Internet Connection");
				}
				else
					if (netInfo.getTypeName().equals("MOBILE")
							&& netInfo.getExtraInfo().equals("cmwap"))
					{
						Toast.makeText(context, "cmwap网络不可用，请选择cmnet网络",
								Toast.LENGTH_LONG).show();
						return false;
					}
					else
					{
						
						return true;
					}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			Toast.makeText(context, "未找到网络,请检查连接", Toast.LENGTH_SHORT).show();
			return false;
		}
		/**
		 * true:有WIFI false:无WIFI
		 * @param inContext
		 * @return
		 */
		public static boolean isWiFiActive(Context inContext)
		{
			Context context = inContext.getApplicationContext();
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null)
			{
				NetworkInfo[] info = connectivity.getAllNetworkInfo();
				if (info != null)
				{
					for (int i = 0; i < info.length; i++)
					{
						if (info[i].getTypeName().equals("WIFI")
								&& info[i].isConnected()) { return true; }
					}
				}
			}
			return false;
		}

		// 判断是否有可用的网络连接
		public static boolean isNetConnectNoMsg(Context context)
		{
			try
			{
				/*
				 * Check for Internet Connection (Through whichever interface)
				 */
				ConnectivityManager connManager = (ConnectivityManager) (context)
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo netInfo = connManager.getActiveNetworkInfo();
				/******* EMULATOR HACK - false condition needs to be removed *****/
				// if (false && (netInfo == null || !netInfo.isConnected())){
				if ((netInfo == null || netInfo.isConnected() == false))
				{
					// SendMessage((context), "No Internet Connection");
				}
				else
				{
					return true;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return false;
		}
		
}
