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

	public static final int CHANGER_STATUS = 4;// ״̬�仯

	public static final int INIT = 1;// �����������ص�״̬����ʼ��״̬����������״̬����ͣ״̬
	public static final int PAUSE = 2;
	public static final int FINISH = 3;// �������
	public static final int UPDATE = 4;// �ɸ���

	public static final int DOWNLOADING = 30;// ��������
	public static final int INIT_SERVER = 31;// �����������ص�״̬����ʼ��״̬����������״̬����ͣ״̬
	public static final int WAIT = 32;// �ȴ��Ѽ������
	public static final int DOWNLOADING_STATUS = 35;// ����������ʾ���ؽ���

	public static final int ERROR = 112;// ���� ʧ��
	public static final int ERROR_NET = 117;// ���ӷ�����ʧ��
	public static final int ERROR_CREATE_FILE = 118;// �����ļ�ʧ��
	public static final int ERROR_NO_FILE = 119;// �ļ�δ�ҵ�
	public static final int DELETE = 110;// ���� ʧ��
	public static final int ERROR_NET_DIC_FILE_SIZE = 120;// ��ȡ�ļ��ļ���Сʧ��
	public static final int ERROR_NET_DIC_UPDATE_SOFT = 112;// ��ǰ�汾��֧���շѴʵ�����.������Ӧ��
	public static final int ERROR__NOTNET = 119;// δ�ҵ���������
	private String urlstr;// ���صĵ�ַ
	private String localfile;// ����·��
	private int nNetFileSize;// ��Ҫ���ص��ļ��Ĵ�С
	private int nLocalFileSize;// ��Ҫ���ص��ļ��Ĵ�С
	private int Rate;
	private DownloadInfo infos;// ���������Ϣ��ļ���
	protected int state = INIT;
	protected Context main;
	protected INotifyDataSetChanged notify = null;
	/**
	 * �ļ�����λ��
	 */
	public static final String localPath = Environment
			.getExternalStorageDirectory() + "/baifen/dowsload/";// ����·��

	/**
	 * ��ȡ��������
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
	 * ��ȡ��������
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
	 * �ж��Ƿ���������
	 */
	public boolean isdownloading()
	{
		return state == DOWNLOADING;
	}

	/**
	 * �õ�downloader�����Ϣ ���Ƚ����ж��Ƿ��ǵ�һ�����أ�����ǵ�һ�ξ�Ҫ���г�ʼ������������������Ϣ���浽���ݿ���
	 * ������ǵ�һ�����أ��Ǿ�Ҫ�����ݿ��ж���֮ǰ���ص���Ϣ����ʼλ�ã�����Ϊֹ���ļ���С�ȣ�������������Ϣ���ظ�������
	 */
	public DownloadInfo getDownloaderInfors(HttpURLConnection connection)
	{
		nNetFileSize = connection.getContentLength();
		if (nNetFileSize > 0)
		{
			// �õ����ݿ������е�urlstr���������ľ�����Ϣ
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
					// ����infos�е����ݵ����ݿ�
					dao.saveInfos(infos);
					// ����һ��LoadInfo��������������ľ�����Ϣ
				}
				catch (IOException e)
				{
					e.printStackTrace();
//					Log.v("wmh", "getDownloaderInfors:localfile=" + localfile);
					setState(ERROR_CREATE_FILE);
					return null;
				}
			}
			else if (nNetFileSize - 1 != infos.getEndPos())//�ļ����
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
					// ����infos�е����ݵ����ݿ�
					dao.saveInfos(infos);
					// ����һ��LoadInfo��������������ľ�����Ϣ
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
			infos = getDownloaderInfors(connection);// ��ȡ�ļ���С
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
			// ���÷�Χ����ʽΪRange��bytes x-y;
			String Range = "bytes=" + infos.getCompeleteSize() + "-"
					+ infos.getEndPos();
			connection.setRequestProperty("Range", Range);
			mOutputStream = new FileOutputStream(mFile, true);
			// ��Ҫ���ص��ļ�д�������ڱ���·���µ��ļ���
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
				// �������ݿ��е�������Ϣ
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
				// ���سɹ�
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

	// ɾ�����ݿ���urlstr��Ӧ����������Ϣ
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

	// ��������״̬
	public void reset()
	{
		setState(INIT);
	}

	// �ж��Ƿ��п��õ���������
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