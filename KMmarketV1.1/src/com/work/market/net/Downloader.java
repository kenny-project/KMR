package com.work.market.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.byfen.app.KApp;
import com.work.Interface.INotifyDataSetChanged;

public abstract class Downloader // implements INotifyDataSetChanged
{
	public static DecimalFormat myformat = new DecimalFormat("#####0.00");

	public static final int CHANGER_STATUS = 4;// 状态变化

	public static final int INIT = 1;// 定义三种下载的状态：初始化状态，正在下载状态，暂停状态
	public static final int PAUSE = 2;
	public static final int FINISH = 3;// 下载完成
	public static final int UPDATE = 4;// 可更新

	public static final int DOWNLOADING = 30;// 正在下载
	public static final int INIT_SERVER = 31;// 定义三种下载的状态：初始化状态，正在下载状态，暂停状态
	public static final int WAIT = 32;// 等待已加入队列
	public static final int DOWNLOADING_STATUS = 35;// 正在下载显示下载进度

	public static final int ERROR = 112;// 下载 失败
	public static final int ERROR_NET = 117;// 连接服务器失败
	public static final int ERROR_CREATE_FILE = 118;// 创建文件失败
	public static final int ERROR_NO_FILE = 119;// 文件未找到
	public static final int DELETE = 110;// 下载 失败
	public static final int ERROR_NET_DIC_FILE_SIZE = 120;// 获取文件文件大小失败
	public static final int ERROR_NET_DIC_UPDATE_SOFT = 112;// 当前版本不支持收费词典下载.请升级应用
	public static final int ERROR__NOTNET = 119;// 未找到可用网络
	private String urlstr;// 下载的地址
	private String localfile;// 保存路径
	private int nNetFileSize;// 所要下载的文件的大小
	private int nLocalFileSize;// 所要下载的文件的大小
	private int Rate;
	private DownloadInfo infos;// 存放下载信息类的集合
	protected int state = INIT;
	protected Context main;
	protected INotifyDataSetChanged notify = null;
	/**
	 * 文件下载位置
	 */
	public static final String localPath = Environment
			.getExternalStorageDirectory() + "/baifen/dowsload/";// 本地路径

	/**
	 * 获取下载速率
	 * 
	 * @return
	 */
	public int getRate()
	{
		return Rate;
	}

	public INotifyDataSetChanged getNotify()
	{
		return notify;
	}

	public void setNotify(INotifyDataSetChanged notify)
	{
		this.notify = notify;
	}

	/**
	 * 获取下载速率
	 * 
	 * @return
	 */
	public String getStrRate()
	{
		double k = Rate / 1024.0;
		if (k < 600)
		{
			return myformat.format(Rate / 1024.0) + "K/s";
		}
		else
		{
			return myformat.format(k / 1024.0) + "M/s";
		}
	}

	public String getUrl()
	{
		return urlstr;
	}

	public void setUrl(String url)
	{
		this.urlstr = url;
	}

	public String getLocalfile()
	{
		return localfile;
	}

	public void setLocalfile(String localfile)
	{
		this.localfile = localfile;
	}

	/**
	 * 判断是否正在下载
	 */
	public boolean isdownloading()
	{
		return state == DOWNLOADING;
	}

	/**
	 * 得到downloader里的信息 首先进行判断是否是第一次下载，如果是第一次就要进行初始化，并将下载器的信息保存到数据库中
	 * 如果不是第一次下载，那就要从数据库中读出之前下载的信息（起始位置，结束为止，文件大小等），并将下载信息返回给下载器
	 */
	public DownloadInfo getDownloaderInfors(HttpURLConnection connection)
	{
		nNetFileSize = connection.getContentLength();
		if (nNetFileSize > 0)
		{
			// 得到数据库中已有的urlstr的下载器的具体信息
			File file = new File(localfile);
			nLocalFileSize = (int) file.length();
			Dao dao = ((KApp) main.getApplicationContext()).getDao();
			infos = dao.getInfos(urlstr);
			
			if (infos == null)
			{
				try
				{
					if (file.exists())
					{
						file.delete();
					}
					else
					{
						if (!file.getParentFile().exists())
						{
							file.getParentFile().mkdirs();
						}
					}
					file.createNewFile();
					infos = new DownloadInfo(0, nNetFileSize - 1, 0, urlstr);
					// 保存infos中的数据到数据库
					dao.saveInfos(infos);
					// 创建一个LoadInfo对象记载下载器的具体信息
				}
				catch (IOException e)
				{
					e.printStackTrace();
//					Log.v("wmh", "getDownloaderInfors:localfile=" + localfile);
					setState(ERROR_CREATE_FILE);
					return null;
				}
			}
			else if (nNetFileSize - 1 != infos.getEndPos())//文件变更
			{
				// || infos.getCompeleteSize() != nLocalFileSize
				try
				{
					if (file.exists())
					{
						file.delete();
					}
					file.createNewFile();
					dao.delete(urlstr);
					infos = new DownloadInfo(0, nNetFileSize - 1, 0, urlstr);
					// 保存infos中的数据到数据库
					dao.saveInfos(infos);
					// 创建一个LoadInfo对象记载下载器的具体信息
				}
				catch (IOException e)
				{
					e.printStackTrace();
					setState(ERROR_CREATE_FILE);
					Log.v("wmh", "getDownloaderInfors:localfile=" + localfile);
					return null;
				}
			}
			else
			{
				nLocalFileSize = (int) file.length();
				infos.setCompeleteSize(nLocalFileSize);	
			}
			setState(INIT_SERVER);
			return infos;
		}
		else
		{
			setState(ERROR_NET);
		}
		return null;
	}

	public DownloadInfo getDownloadInfo()
	{
		if (infos == null)
		{
			Dao dao = ((KApp) main.getApplicationContext()).getDao();
			infos = dao.getInfos(urlstr);
		}
		return infos;
	}

	public int DownLoading()
	{
		HttpURLConnection connection = null;
		FileOutputStream mOutputStream = null;
		InputStream is = null;
		try
		{
			if (state != Downloader.WAIT)
			{
				return state;
			}
			if (!isNetConnectNoMsg(main))
			{
				// NotifyDataSetChanged(ERROR__NOTNET, urlstr, 0, 0);
				Downloader.this.setState(Downloader.ERROR__NOTNET);
				return state;
			}
			URL url = new URL(urlstr);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(8000);
			connection.setReadTimeout(30000);
			connection.setRequestMethod("GET");
			int fileSize = connection.getContentLength();
			infos = getDownloaderInfors(connection);// 获取文件大小
			if (infos == null)
			{
				// NotifyDataSetChanged(this.getState(), urlstr, 0, 0);
				return state;
			}
			else
			{
				setState(DOWNLOADING);
			}
			File mFile = new File(localfile);

			// Log.v("DownLoader", "Downloader:url Start");
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(8000);
			connection.setReadTimeout(60000);
			connection.setRequestMethod("GET");
			// 设置范围，格式为Range：bytes x-y;
			String Range = "bytes=" + infos.getCompeleteSize() + "-"
					+ infos.getEndPos();
			connection.setRequestProperty("Range", Range);
			mOutputStream = new FileOutputStream(mFile, true);
			// 将要下载的文件写到保存在保存路径下的文件中
			is = connection.getInputStream();
			// ByteArrayBuffer arrayBuffer = new ByteArrayBuffer(20 * 1024);
			byte[] buffer = new byte[400 * 1024];
			int length = -1;
			long start = 0;
			long end = 0;
			int len = 0;
			start = System.currentTimeMillis();
			Rate = 0;
			while (state == DOWNLOADING)
			{
				try
				{
					if ((length = is.read(buffer)) == -1)
					{
						setState(Downloader.ERROR_NET);
						break;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					setState(Downloader.ERROR_NET);
					break;
				}
				if (!mFile.exists())
				{
					setState(Downloader.ERROR_NO_FILE);
					break;
				}
				len += length;
				// arrayBuffer.append(buffer, 0, length);
				mOutputStream.write(buffer, 0, length);
				
				infos.setCompeleteSize(infos.getCompeleteSize() + length);
				length = 0;
				// 更新数据库中的下载信息
				end = System.currentTimeMillis();
				long timer = end - start;
				if (timer > 900)
				{
					Rate = (int) (len / timer * 1000);// 8*1000
					len = 0;
					start = System.currentTimeMillis();
					if (notify != null) notify.NotifyDataSetChanged(
							DOWNLOADING_STATUS, infos, length, 0);
				}
			}
			Rate = 0;
			Dao dao = ((KApp) main.getApplicationContext()).getDao();
			dao.updataInfos(infos);
			// NotifyDataSetChanged(1, urlstr, length, 0);
			if (fileSize <= infos.getCompeleteSize())
			{
				// 下载成功
				infos.setCompeleteSize(0);
				dao.delete(urlstr);
				File dict = new File(localfile.substring(0,
						localfile.length() - 3));
				if (dict.exists())
				{
					dict.delete();
				}
				mFile.renameTo(dict);
				setState(Downloader.FINISH);
				// NotifyDataSetChanged(2, urlstr, 1, 0);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			setState(Downloader.ERROR);
			// NotifyDataSetChanged(3, urlstr, 0, 0);
		}
		finally
		{
			try
			{
				if (is != null) is.close();
				if (mOutputStream != null) mOutputStream.close();
				if (connection != null) connection.disconnect();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
//			Log.v("wmh", "Downloader:RUN end");
		}
		return state;
	}

	// 删除数据库中urlstr对应的下载器信息
	protected void delete()
	{
		try
		{
			Dao dao = ((KApp) main.getApplicationContext()).getDao();
			if (dao != null) dao.delete(urlstr);
			if (infos != null) infos.setCompeleteSize(0);
			new File(localfile).delete();
			setState(INIT);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public int getState()
	{
		return state;
	}

	public void setState(int state)
	{
		this.state = state;
		if (notify != null) notify.NotifyDataSetChanged(4, 0, state, 0);
	}

	// 重置下载状态
	public void reset()
	{
		setState(INIT);
	}

	// 判断是否有可用的网络连接
	public static boolean isNetConnectNoMsg(Context context)
	{
		try
		{
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