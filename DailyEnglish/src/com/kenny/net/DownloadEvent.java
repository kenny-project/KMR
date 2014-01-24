package com.kenny.net;
//package com.kingsoft.net;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.InputStream;
//import java.net.URL;
//import java.net.URLConnection;
//import java.util.List;
//
//import com.kingsoft.data.DictBean;
//import com.kingsoft.file.SaveData;
//import com.kingsoft.struct.AbsEvent;
//import com.kingsoft.Main;
//import com.kingsoft.util.KCommand;
//import com.kingsoft.util.Log;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Handler;
//import android.os.Message;
//import android.view.View;
//
//// ��һ��ʵ���첽���أ�����ʾ Android ���첽��Ϣ���?�� Handler �ķ�ʽ��
//public class DownloadEvent extends AbsEvent
//{
//	private static final int FILE_DOWNLOAD_CONNECT = 0;
//	private static final int FILE_DOWNLOAD_UPDATE = 1;
//	private static final int FILE_DOWNLOAD_COMPLETE = 2;
//	private static final int FILE_FULLDOWNLOAD_COMPLETE = 3;// 0:���� 0>Ϊ������
//															// -1:δ�ҵ�����
//															// -2:������ݻ�ȡʧ��
//															// -3:δ������Ӧ�ķ���
//															// -4:δ���ص����.�Ժ�����
//	private static final int FILE_DOWNLOAD_COUNT = 4;// ����
//	private static final int FILE_DOWNLOAD_ING = 5;// ��������
//	private static final int FILE_DOWNLOAD_ERROR = -1;
//
//	// ʵ���Զ���� Handler
//	EventHandler mHandler = new EventHandler(this);
//	private DictBean gdata;
//
//	public int getGroupID()
//	{
//		return gdata.getID();
//	}
//
//	private Context context;
//
//	private boolean thLive = false;// ������������
//	private String btUpdateName = "����";
//	private String NetStatus;
//	private int percent = 0;
//	private int DownCount = 0;// ��Ҫ���ص�����
//	private int DownPannalTag = View.GONE;
//
//	public int getDownPannalTag()
//	{
//		return DownPannalTag;
//	}
//
//	public void setDownPannalTag(int downPannalTag)
//	{
//		DownPannalTag = downPannalTag;
//	}
//
//	public int getDownCount()
//	{
//		return DownCount;
//	}
//
//	public void setDownCount(int downCount)
//	{
//		DownCount = downCount;
//	}
//
//	public String getBtUpdateName()
//	{
//		return btUpdateName;
//	}
//
//	public void setBtUpdateName(String btUpdateName)
//	{
//		this.btUpdateName = btUpdateName;
//	}
//
//	public String getNetStatus()
//	{
//		return NetStatus;
//	}
//
//	public void setNetStatus(String netStatus)
//	{
//		NetStatus = netStatus;
//	}
//
//	public int getPercent()
//	{
//		return percent;
//	}
//
//	public void setPercent(int percent)
//	{
//		this.percent = percent;
//	}
//
//	public boolean isThLive()
//	{
//		return thLive;
//	}
//
//	public void setThLive(boolean thLive)
//	{
//		this.thLive = thLive;
//	}
//
//	public DownloadEvent(Main main, DictBean gdata)
//	{
//		super(main);
//		this.context = main;
//		this.gdata = gdata;
//		// this.thLive=true;
//	}
//
//	@Override
//	public void ok()
//	{
//		sendMessage(FILE_DOWNLOAD_CONNECT);
//		int result = 0;
//		if (gdata != null&&KCommand.isNetConnect(context))
//		{
//			// int pos = SaveData.readPreferencesInt(context, gdata.getID()
//			// + "_ppos", 1);
//			DataPersist dp = new DataPersist(context);// ��ݿ��������
//			int pos = dp.GetCount(1, gdata.getID()) / 5;
//			if (pos >= gdata.getPCount())
//			{
//				sendMessage(FILE_FULLDOWNLOAD_COMPLETE, 0);
//				return;
//			}
//			sendMessage(FILE_DOWNLOAD_ING, gdata.getPCount() - pos);
//			// �������������ݲ���ȡӦ��
//			result = download(context, gdata.getID(), pos, gdata.getPCount());
//			sendMessage(FILE_FULLDOWNLOAD_COMPLETE, result);
//		}
//		else
//		{
//			sendMessage(FILE_FULLDOWNLOAD_COMPLETE, -3);
//		}
//	}
//
//	/**
//	 * ���������ظ���
//	 * 
//	 * @param context
//	 * @param groupid
//	 * @param pos
//	 * @param count
//	 * @return
//	 */
//	private int download(Context context, int groupid, int pos, int count)
//	{
//		JokeItemParser itemParser = new JokeItemParser();
//		List<JokeBean> ItemBean;
//		DataPersist dp = new DataPersist(context);
//		DatabaseHelper OpenHelper = null; // ��ݿ�ľ��
//		SQLiteDatabase db = null;
//		int result = 0;
//		int ppos = 0;
//		for (ppos = pos; thLive && ppos <= count; ppos++)
//		{
//			String param = "code=1&uid=0&key=" + KeyDefine.ItemData + "&value="
//					+ DataType.Item + "|" + groupid + "|" + ppos + "|";
//			InputStream is;
//			try
//			{
//				is = KHttpPost.doPost(NetConst.WebSide(), param);
//			}
//			catch (Exception e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				P.v("error", itemParser.GetLastError());
//				result = -4;
//				break;
//			}
//
//			ItemBean = itemParser.parseXmlStream(is);
//			// mProgressDialog.incrementProgressBy(1);
//			// sendMessage(FILE_DOWNLOAD_UPDATE, groupid);
//			sendMessage(FILE_DOWNLOAD_UPDATE, ppos, count);
//			if (ItemBean != null)
//			{
//				if (ItemBean.size() > 0)
//				{
//					OpenHelper = dp.Open(); // ��ݿ�ľ��
//					db = OpenHelper.getWritableDatabase();
//					result += dp.KInsertPLData(DataPersist.PL_TABLENAME,
//							ItemBean, db);
//					db.close();
//					OpenHelper.close();
//				}
//				ItemBean.clear();
//			}
//			else
//			{
//				P.v("error", itemParser.GetLastError());
//				result = -4;
//				break;
//			}
//		}
//		SaveData.writePreferencesInt(context, gdata.getID() + "_ppos", ppos);
//		if (db != null) db.close();
//		if (db != null) OpenHelper.close();
//		return result;
//	}
//
//	// ��ָ�� url ��ַ�����ļ���ָ��·��
//	public void download(final String url, final String savePath)
//	{
//		new Thread(new Runnable()
//		{
//			public void run()
//			{
//				try
//				{
//					sendMessage(FILE_DOWNLOAD_CONNECT);
//					URL sourceUrl = new URL(url);
//					URLConnection conn = sourceUrl.openConnection();
//					InputStream inputStream = conn.getInputStream();
//
//					int fileSize = conn.getContentLength();
//
//					File savefile = new File(savePath);
//					if (savefile.exists())
//					{
//						savefile.delete();
//					}
//					savefile.createNewFile();
//
//					FileOutputStream outputStream = new FileOutputStream(
//							savePath, true);
//
//					byte[] buffer = new byte[5024];
//					int readCount = 0;
//					int readNum = 0;
//					int prevPercent = 0;
//					while (readCount < fileSize && readNum != -1)
//					{
//						readNum = inputStream.read(buffer);
//						if (readNum > -1)
//						{
//							outputStream.write(buffer, 0, readNum);
//							readCount = readCount + readNum;
//
//							int percent = (int) (readCount * 100 / fileSize);
//							if (percent > prevPercent)
//							{
//								// �������ؽ����Ϣ
//								sendMessage(FILE_DOWNLOAD_UPDATE, percent,
//										readCount);
//								Log.v("package", String.valueOf(percent));
//								prevPercent = percent + 8;
//							}
//						}
//					}
//					outputStream.close();
//					sendMessage(FILE_DOWNLOAD_COMPLETE, savePath);
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//					sendMessage(FILE_DOWNLOAD_ERROR, e);
//				}
//			}
//		}).start();
//	}
//
//	// �� Handler ������Ϣ
//	private void sendMessage(int what, Object obj)
//	{
//		// ������Ҫ�� Handler ���͵���Ϣ
//		Message msg = mHandler.obtainMessage(what, obj);
//		// ������Ϣ
//		mHandler.sendMessage(msg);
//	}
//
//	private void sendMessage(int what)
//	{
//		Message msg = mHandler.obtainMessage(what);
//		mHandler.sendMessage(msg);
//	}
//
//	private void sendMessage(int what, int arg1, int arg2)
//	{
//		Message msg = mHandler.obtainMessage(what, arg1, arg2);
//		mHandler.sendMessage(msg);
//	}
//
//	// �Զ���� Handler
//	private class EventHandler extends Handler
//	{
//		private DownloadEvent mManager;
//
//		public EventHandler(DownloadEvent manager)
//		{
//			mManager = manager;
//		}
//
//		// ������յ�����Ϣ
//		@Override
//		public void handleMessage(Message msg)
//		{
//			Log.v("KA", "msg.what=" + msg.what);
//			switch (msg.what)
//			{
//			case FILE_DOWNLOAD_CONNECT:// ��������
//				if (mOnDownloadConnectListener != null)
//				{
//					mOnDownloadConnectListener.onDownloadConnect(mManager);
//				}
//				break;
//			case FILE_DOWNLOAD_UPDATE:// ���ؽ�����
//				if (mOnDownloadUpdateListener != null)
//				{
//					mOnDownloadUpdateListener.onDownloadUpdate(mManager,
//							msg.arg1, msg.arg2);
//				}
//				break;
//			case FILE_DOWNLOAD_COMPLETE:// �������
//				if (mOnDownloadCompleteListener != null)
//				{
//					mOnDownloadCompleteListener.onDownloadComplete(mManager,
//							msg.obj);
//				}
//				break;
//			case FILE_FULLDOWNLOAD_COMPLETE:// ȫ��������
//				setThLive(false);
//				if (mOnFullDownloadCompleteListener != null)
//				{
//					mOnFullDownloadCompleteListener.onFullDownloadComplete(
//							mManager, msg.obj);
//				}
//				break;
//			case FILE_DOWNLOAD_ERROR:// ����ʧ��
//				setThLive(false);
//				if (mOnDownloadErrorListener != null)
//				{
//					mOnDownloadErrorListener.onDownloadError(mManager,
//							(Exception) msg.obj);
//				}
//				break;
//			default:
//				break;
//			}
//		}
//	}
//
//	// ���������¼�
//	private OnDownloadConnectListener mOnDownloadConnectListener;
//
//	public interface OnDownloadConnectListener
//	{
//		void onDownloadConnect(DownloadEvent manager);
//	}
//
//	public void setOnDownloadConnectListener(OnDownloadConnectListener listener)
//	{
//		mOnDownloadConnectListener = listener;
//	}
//
//	// �������ؽ�ȸ����¼�
//	private OnDownloadUpdateListener mOnDownloadUpdateListener;
//
//	public interface OnDownloadUpdateListener
//	{
//		void onDownloadUpdate(DownloadEvent manager, int percent, int count);
//	}
//
//	public void setOnDownloadUpdateListener(OnDownloadUpdateListener listener)
//	{
//		mOnDownloadUpdateListener = listener;
//	}
//
//	// ������������¼�
//	private OnFullDownloadCompleteListener mOnFullDownloadCompleteListener;
//
//	public interface OnFullDownloadCompleteListener
//	{
//		void onFullDownloadComplete(DownloadEvent manager, Object result);
//	}
//
//	public void setOnFullDownloadUpdateListener(
//			OnFullDownloadCompleteListener listener)
//	{
//		mOnFullDownloadCompleteListener = listener;
//	}
//
//	// ������������¼�
//	private OnDownloadCompleteListener mOnDownloadCompleteListener;
//
//	public interface OnDownloadCompleteListener
//	{
//		void onDownloadComplete(DownloadEvent manager, Object result);
//	}
//
//	public void setOnDownloadCompleteListener(
//			OnDownloadCompleteListener listener)
//	{
//		mOnDownloadCompleteListener = listener;
//	}
//
//	// ���������쳣�¼�
//	private OnDownloadErrorListener mOnDownloadErrorListener;
//
//	public interface OnDownloadErrorListener
//	{
//		void onDownloadError(DownloadEvent manager, Exception e);
//	}
//
//	public void setOnDownloadErrorListener(OnDownloadErrorListener listener)
//	{
//		mOnDownloadErrorListener = listener;
//	}
//
//}