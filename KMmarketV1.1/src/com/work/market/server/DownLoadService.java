package com.work.market.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.byfen.market.InstallApkActivity;
import com.byfen.market.MainActivity;
import com.byfen.market.R;
import com.framework.syseng.AbsEvent;
import com.framework.syseng.SysEng;
import com.work.Interface.INotifyDataSetChanged;
import com.work.market.adapter.DownLoadedAdapter;
import com.work.market.adapter.DownLoadingAdapter;
import com.work.market.bean.AppListBean;
import com.work.market.db.DBAdapter;
import com.work.market.db.DBdatafinishModel;
import com.work.market.net.Common;
import com.work.market.net.DictBean;
import com.work.market.net.DownloadInfo;
import com.work.market.net.Downloader;
import com.work.market.net.HttpUtil;
import com.work.market.util.CONST;
import com.work.market.util.HttpUrlConst;

/**
 * 
 * 下载的service
 * 
 * @version 1.0
 * @created 2012-12-19 下午9:50:59
 */
public class DownLoadService extends Service implements Runnable,
		INotifyDataSetChanged
{
	public static final String ACTION = "com.work.market.server.MyService";
	private int NOTIFICATION = R.string.app_name;
	private static HashMap<Integer, AppListBean> mMapList = new HashMap<Integer, AppListBean>();
	public int progressint = 0;
	public static boolean downing = false;
	private boolean isrun = true;
	private Context mContext;
	// 正在下载 apk列表
	private Vector<AppListBean> mRunList = new Vector<AppListBean>();
	// 加载到下载队列的 apk列表
	private Vector<AppListBean> downloadingList = new Vector<AppListBean>();
	// 下载完成 apk列表
	private Vector<AppListBean> downloadFinishList = new Vector<AppListBean>();
	// 更新列表
	private Vector<AppListBean> updateList = new Vector<AppListBean>();
	private DownLoadingAdapter mDownLoadingAddapter;
	private DownLoadedAdapter mDownloadedadapter;
	private Thread thread;
	private UpdateTask mNetTask;
//	private static Notification motif;

//	public static Notification getNotif()
//	{
//		return mDownLoadingNotify;
//	}

	public class LocalBinder extends Binder
	{
		public DownLoadService getService()
		{
			return DownLoadService.this;
		}
	}

	public void AdapterInit(Context mContext)
	{
		mDownLoadingAddapter = new DownLoadingAdapter(mContext,
				downloadingList, DownLoadService.this);
		mDownloadedadapter = new DownLoadedAdapter(mContext,
				downloadFinishList, DownLoadService.this);
	}

	public DownLoadingAdapter getDownLoadingAddapter()
	{
		return mDownLoadingAddapter;
	}

	public DownLoadedAdapter getDownloadedadapter()
	{
		return mDownloadedadapter;
	}

	private final IBinder mBinder = new LocalBinder();
	private boolean mBinded = false;

	public IBinder onBind(Intent intent)
	{
		mBinded = true;
		return mBinder;
	}

	public void onRebind(Intent intent)
	{
		mBinded = true;
		super.onRebind(intent);
	}

	public boolean onUnbind(Intent intent)
	{
		mBinded = false;
		return super.onUnbind(intent);
	}

	private String mTitle = "";

	public String getTitle()
	{
		return mTitle;
	}

	public void Init(Context con)
	{
		InitUpdateData(con);
		InitRunListData(con);
		InitFinishData(con);
	}

	/**
	 * 获取需要更新的软件
	 * 
	 * @param con
	 */
	public void InitUpdateData(Context con)
	{
		if (mNetTask != null)
		{
			mNetTask.cancel(true);
		}
		mNetTask = new UpdateTask();
		mNetTask.execute(HttpUrlConst.UPDATE_URLS);
	}

	/**
	 * 初始化已经下载的列表
	 * 
	 * @param con
	 */
	public void InitRunListData(Context con)
	{

		mContext = con;
		ArrayList<DBdatafinishModel> list = DBAdapter.createDBAdapter(con)
				.queryAllFinish(Common.user_RunListData);
		synchronized (mRunList)
		{
			downloadingList.clear();
			mRunList.clear();
			for (int i = list.size() - 1; i >= 0; i--)
			{
				DBdatafinishModel model = list.get(i);
				AppListBean temp = new AppListBean();
//				String paths = CONST.DownLoad + model.getFileName();
				// File file = new File(paths);
				// if (file.exists())
				// {
				temp.setId(model.getId());
				temp.setTitle(model.getTitle());
				temp.setPn(model.getPn());
				temp.setLogourl(model.getlogurl());
				temp.setSize(model.getsize());
				temp.setAppurl(model.getappurl());
				temp.setVercode(model.getversioncode());
				temp.setId(model.getId());
				temp.setAppFileExt(model.getAppFileExt());
				temp.setDownloading(new DictBean(mContext, temp.getId(), temp
						.getAppurl(), temp.getAppFileExt()));
				downloadingList.add(temp);
				if (!mMapList.containsKey(temp.getId()))
				{
					mMapList.put(temp.getId(), temp);
				}

				// }
			}
		}
		DBAdapter.close();
	}

	/**
	 * 初始化已经下载的列表
	 * 
	 * @param con
	 */
	public void InitFinishData(Context con)
	{
		downloadFinishList.clear();
		mRunList.clear();
		mContext = con;
		ArrayList<DBdatafinishModel> list = DBAdapter.createDBAdapter(con)
				.queryAllFinish(Common.user_key);
		for (int i = list.size() - 1; i >= 0; i--)
		{
			DBdatafinishModel model = list.get(i);
			AppListBean temp = new AppListBean();
			String paths = CONST.DownLoad + model.getFileName();
			File file = new File(paths);
			if (file.exists())
			{
				temp.setId(model.getId());
				temp.setTitle(model.getTitle());
				temp.setPn(model.getPn());
				temp.setLogourl(model.getlogurl());
				temp.setSize(model.getsize());
				temp.setAppurl(model.getappurl());
				temp.setVercode(model.getversioncode());
				temp.setId(model.getId());
				temp.setAppFileExt(model.getAppFileExt());
				temp.setDownloading(new DictBean(mContext, temp.getId(), temp
						.getAppurl(), temp.getAppFileExt()));
				downloadFinishList.add(temp);
				if (!mMapList.containsKey(temp.getId()))
				{
					mMapList.put(temp.getId(), temp);
				}
			}
		}
		DBAdapter.close();
	}

	public void finish(Intent intent)
	{
		HttpGetAndPostNet.stop();
		stopService(intent);
	}

	public void cancle()
	{
		HttpGetAndPostNet.stop();
	}

	public static DictBean getDictBean(int id)
	{
		AppListBean bean = mMapList.get(id);
		if (bean != null)
		{
			return bean.getDictBean();
		}
		return null;
	}

	public static AppListBean getBean(int id)
	{
		return mMapList.get(id);
	}

	public List<AppListBean> getUpdateList()
	{
		return updateList;
	}

	public int DelDownLoading(int pos)
	{
		synchronized (mRunList)
		{
			AppListBean bean = downloadingList.get(pos);
			if (bean != null)
			{
				DelDLEvent(bean);
				// downloadingList.remove(pos);
				DBAdapter.createDBAdapter(mContext).deleteFinishById(
						bean.getId(), Common.user_RunListData);
				DBAdapter.close();
				mDownLoadingAddapter.notifyDataSetChanged();
				return 1;
			}
			return -1;
		}
	}

	/**
	 * 删除指定的ID
	 * 
	 * @param id
	 * @return
	 */
	public int SelectDelDownLoadedItem(int id)
	{
		int pos = -1;
		for (int i = 0; i < downloadFinishList.size(); i++)
		{
			if (downloadFinishList.get(i).getId() == id)
			{
				pos = i;
				break;
			}
		}
		AppListBean bean = downloadFinishList.get(pos);
		if (bean != null)
		{
			bean.Delete();
			DictBean temp=bean.getDownloading();
			if(temp!=null)
			{
				temp.setState(Downloader.INIT);
			}
			DBAdapter.createDBAdapter(this).deleteMessageById(id,
					Common.user_key);
			DBAdapter.close();
			downloadFinishList.remove(pos);
			return 1;
		}
		return -1;
	}

	public int DelDownLoaded(int pos)
	{
		AppListBean bean = downloadFinishList.get(pos);
		if (bean != null)
		{
			bean.Delete();
			DictBean temp=bean.getDownloading();
			if(temp!=null)
			{
				temp.setState(Downloader.INIT);
			}
			DBAdapter.createDBAdapter(this).deleteMessageById(bean.getId(),
					Common.user_key);
			DBAdapter.close();
			downloadFinishList.remove(pos);
			mDownloadedadapter.notifyDataSetChanged();
			return 1;
		}
		return -1;
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
	}

	/**
	 * 初始化项目
	 */
	@Override
	public void onCreate()
	{
		super.onCreate();
		SysEng.getInstance().setHandler(new Handler());
		Log.v("wmh", "SysEng.getInstance().setHandler(new Handler());");
	}

	@Override
	public void onDestroy()
	{
		downing = false;
		HttpGetAndPostNet.stop();
		cancel();
		downloadFinishList.clear();
		super.onDestroy();
	}

	public class DownLoadServiceBinder extends Binder
	{
		public DownLoadService getService()
		{
			return DownLoadService.this;
		}
	}

	// 检查SD卡是否存在 true:存在
	public static boolean checkSDCard()
	{
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) return true;
		else
			return false;
	}

	/**
	 * 
	 * @return
	 */
	private String ListBeanListToJson(List<AppListBean> list)
	{
		JSONObject object = new JSONObject();
		try
		{
			JSONArray array = new JSONArray();
			for (int i = 0; i < list.size(); i++)
			{
				AppListBean bean = list.get(i);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", bean.getId());
				jsonObject.put("title", bean.getTitle());
				jsonObject.put("pn", bean.getPn());
				jsonObject.put("logo", bean.getLogourl());
				jsonObject.put("size", bean.getSize());
				jsonObject.put("score", bean.getScore());
				jsonObject.put("dc", bean.getDowntiems());
				jsonObject.put("appurl", bean.getAppurl());
				jsonObject.put("vername", bean.getVername());
				jsonObject.put("vercode", bean.getVercode());
				jsonObject.put("downprogress", bean.getPercentage());
				// jsonObject.put("downing", bean.getDowning());
				jsonObject.put("downingsize", 0);
				jsonObject.put("Percentage", bean.getPercentage());
				array.put(jsonObject);
			}
			object.put("list", array);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		return object.toString();
	}

	/**
	 * 该函数,只能在UI主线程中调用
	 */
	public void start()
	{
		if (thread == null)
		{
			thread = new Thread(this);
			thread.setPriority(Thread.NORM_PRIORITY);
		}
		thread.start();
	}

	public void cancel()
	{
		try
		{
			this.isrun = false;
			synchronized (mRunList)
			{
				mRunList.removeAllElements();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void addDLEvent(AppListBean event)
	{
		synchronized (mRunList)
		{
			DelDLEvent(event);
			mRunList.add(event);
			downloadingList.add(event);
			DBAdapter.createDBAdapter(mContext).insertfinish(event,
					Common.user_RunListData);
			mMapList.put(event.getId(), event);
			mRunList.notify();
			Log.v("event", "add addDLEvent unblock "
					+ event.getClass().getName());
		}
	}

	public void DelDLEvent(AppListBean event)
	{
		synchronized (mRunList)
		{
			int id = event.getId();
			mMapList.remove(id);
			for (int i = 0; i < downloadingList.size(); i++)
			{
				AppListBean element = downloadingList.get(i);

				if (id == element.getId())
				{
					mRunList.remove(element);
					downloadingList.remove(element);
					DictBean bean=element.getDictBean();
					if(bean!=null)
					{
						bean.setState(Downloader.INIT);
					}
					DBAdapter.createDBAdapter(mContext).deleteFinishById(
							event.getId(), Common.user_RunListData);
				}
			}
		}
	}

	public boolean IsDLEvent(AppListBean event)
	{
		synchronized (mRunList)
		{
			boolean result = mRunList.contains(event);
			Log.v("event", "IsDLEvent unblock " + event.getClass().getName());
			return result;
		}
	}

	private AppListBean mRunEvent = null;

	public void run()
	{
		while (isrun)
		{
			try
			{
				synchronized (mRunList)
				{
					if (mRunList.size() <= 0)
					{
						mRunList.wait();
						continue;
					}
					else
					{
						mRunEvent = mRunList.get(0);
					}
				}
				if (mRunEvent != null)
				{
					ShowDownLoadingNotification("正在下载" + mRunEvent.getTitle(),
							mRunEvent);
					download(mRunEvent);
					if(mRunList.size()>0)
					{
						mRunList.remove(0);
					}
					stopForeground(true);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		isrun = false;

	}

	/**
	 * 
	 * 开始下载
	 * 
	 * @param path
	 *            下载url
	 * @param savedir
	 *            下载存放的路径
	 */
	private void download(final AppListBean bean)
	{
		try
		{
			DictBean mDownLoadDictBean = bean.getDictBean();
			if (mDownLoadDictBean == null)
			{
				mDownLoadDictBean = new DictBean(mContext, bean.getId(),
						bean.getAppurl(), bean.getAppFileExt());
			}
			mDownLoadDictBean.setNotify(this);
			int result = mDownLoadDictBean.Start();
			mDownLoadDictBean.setNotify(null);
			if (Downloader.FINISH == result)
			{
				SysEng.getInstance().addHandlerEvent(new AbsEvent()
				{
					@Override
					public void ok()
					{
						synchronized (mRunList)
						{
							downloadingList.remove(bean);
						}
						downloadFinishList.add(bean);
						if(mDownloadedadapter!=null)
						mDownloadedadapter.notifyDataSetChanged();
						if(mDownLoadingAddapter!=null)
						mDownLoadingAddapter.notifyDataSetChanged();
					}
				});
				DBAdapter.createDBAdapter(mContext).deleteFinishById(
						bean.getId(), Common.user_RunListData);
				DBAdapter.createDBAdapter(mContext).insertfinish(bean,
						Common.user_key);
				DBAdapter.close();
				// sendBroadcast_finish();
				showNotification(mContext, mDownLoadDictBean.getId(),
						bean.getTitle(), mDownLoadDictBean.getFilePath());
				Log.v("wmh", "showNotification show");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void NotifyDataSetChanged(final int what, final Object value,
			final int arg1, final int arg2)
	{
		//Log.v("wmh", "DownLoad.notifyDataSetChanged");
		if (what >= Downloader.ERROR)
		{
			ErrorMsg(what, value, arg1, arg2);
		}
		else if (what == Downloader.CHANGER_STATUS)// 状态信息
		{
			if (arg1 == Downloader.WAIT)
			{
				makeText(
						DownLoadService.this,
						DownLoadService.this
								.getString(R.string.toast_msg_add_download_queue),
						0);
			}
			else if (arg1 == Downloader.FINISH)// 下载成功
			{
				makeText(
						DownLoadService.this,
						DownLoadService.this
								.getString(R.string.toast_msg_dic_download_success),
						0);
			}
		}
		if (what == Downloader.DOWNLOADING_STATUS)// 状态信息
		{
			SysEng.getInstance().addHandlerEvent(new AbsEvent()
			{
				@Override
				public void ok()
				{
					// DownloadInfo infos = (DownloadInfo) value;
					ShowDownLoadingNotification("test", mRunEvent);
					if (mINotifyChanged != null)
					{
						// SysEng.getInstance().addHandlerEvent(new AbsEvent()
						// {
						// @Override
						// public void ok()
						// {
						mINotifyChanged.NotifyDataSetChanged(what, value, arg1, arg2);
						// }
						// });
					}
				}
			});
			return;
		}

		if (mINotifyChanged != null)
		{
			// SysEng.getInstance().addHandlerEvent(new AbsEvent()
			// {
			// @Override
			// public void ok()
			// {
			mINotifyChanged.NotifyDataSetChanged(what, value, arg1, arg2);
			// }
			// });
		}
	}

	private INotifyDataSetChanged mINotifyChanged;

	public void setINotifyChanged(INotifyDataSetChanged mINotifyChanged)
	{
		this.mINotifyChanged = mINotifyChanged;
	}

	private void ErrorMsg(int what, Object value, int arg1, int arg2)
	{
		String msg = "";
		switch (what)
		{
		case Downloader.ERROR_NET:
			msg = DownLoadService.this
					.getString(R.string.toast_msg_net_connect_fail);
			break;
		case Downloader.ERROR_NET_DIC_FILE_SIZE:
			msg = DownLoadService.this
					.getString(R.string.toast_msg_file_size_fail);
			// msg=main.getString(R.string.error_net_dic_file_size);
			break;
		case Downloader.ERROR_CREATE_FILE:
			msg = DownLoadService.this
					.getString(R.string.toast_msg_create_file_fail);
			break;
		}
		if (msg.length() > 0)
		{
			makeText(DownLoadService.this, msg, 0);
		}
	}

	public void makeText(Context context, final CharSequence text,
			final int duration)
	{
		SysEng.getInstance().addHandlerEvent(new AbsEvent()
		{
			@Override
			public void ok()
			{
				Toast.makeText(DownLoadService.this, text, duration).show();
			}
		});
	}

	@Override
	public void NotifyDataSetChanged(int cmd, Object value)
	{

	}

	public interface NotificationProvider
	{
		public Notification createNotification(Context context, String title,
				String path);

		public Notification createNotification(Context context, String desc,
				AppListBean appBean);
	}

	NotificationProvider mNotificationProvider = new NotificationProvider()
	{
		public Notification createNotification(Context context, String title,
				String path)// 下载完成
		{
			Notification notification = new Notification(R.drawable.deflogo,
					title + "下载完成", System.currentTimeMillis());

			notification.flags = Notification.FLAG_AUTO_CANCEL;
			// The PendingIntent to launch our activity if the user selects
			// this notification
			Intent intent = new Intent(context, InstallApkActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.fromFile(new File(path)));

			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					intent, 0);
			// contentIntent.
			// Set the info for the views that show in the notification
			// panel.
			notification.setLatestEventInfo(context, title + "下载完成",
					"下载完成,点击安装", contentIntent);
			notification.flags = notification.flags
					| Notification.FLAG_AUTO_CANCEL;
			return notification;
		}

		public Notification createNotification(Context context, String desc,
				AppListBean appBean)// 正在下载
		{
			Notification notification = new Notification(R.drawable.deflogo,
					getTitle(), System.currentTimeMillis());

			notification.contentView = new RemoteViews(getPackageName(),
					R.layout.content_view);
			DownloadInfo info = null;
			DictBean bean = appBean.getDictBean();
			if (bean != null) info = bean.getDownloadInfo();
			// 使用notification.xml文件作VIEW
			if (info != null)
			{
				notification.contentView.setImageViewBitmap(
						R.id.content_view_image, appBean.getLogo());
				// Log.v("wmh",
				// "info.getEndPos()="+info.getEndPos()+",info.getStartPos()="+info.getStartPos());
				notification.contentView.setTextViewText(
						R.id.content_view_Title, appBean.getTitle() + "已下载 "
								+ info.getCompeletePercentage() + "%");
				notification.contentView.setProgressBar(
						R.id.content_view_progress, 100,
						info.getCompeletePercentage(), false);
				Intent intent = new Intent(context, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// intent.setData(Uri.fromFile(new File(provider.getUrl())));
				PendingIntent contentIntent = PendingIntent.getActivity(
						context, 0, intent, 0);

				// notification.contentView.setOnClickPendingIntent(R.id.content_view_Title,
				// contentIntent);
				notification.contentView.setOnClickPendingIntent(
						R.id.content_view_progress, contentIntent);
				// notification.contentView.setOnClickPendingIntent(R.id.content_view_image,
				// contentIntent);
				// notification.contentView.setOnClickPendingIntent(R.id.rlRoot,
				// contentIntent);
			}
			else
			{
				notification.contentView.setProgressBar(
						R.id.content_view_progress, 100, 0, false);
			}
			// The PendingIntent to launch our activity if the user selects
			// this notification
			Intent intent = new Intent(context, DownLoadService.class);
			// intent.setData(Uri.fromFile(new File(provider.getUrl())));
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					intent, 0);
			notification.contentIntent = contentIntent;
			// contentIntent.
			// Set the info for the views that show in the notification
			// panel.

			// notification.setLatestEventInfo(context, "下载管理器", desc,
			// contentIntent);
			notification.flags = notification.flags
					| Notification.FLAG_ONGOING_EVENT;

			return notification;
		}
	};

	private void showNotification(Context context, int id, String title,
			String path)
	{
		try
		{
			// startForeground Service 服务
			// startForeground(bean.getId(),
			// mNotificationProvider.createNotification(context,bean));
			Notification notif = mNotificationProvider.createNotification(
					context, title, path);
			NotificationManager nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(id, notif);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private Notification mDownLoadingNotify;

	/** * Show a notification while this service is running. */
	private Notification ShowDownLoadingNotification(String desc,
			AppListBean appBean)
	{

		if (mDownLoadingNotify == null)
		{
			mDownLoadingNotify = mNotificationProvider.createNotification(this,
					desc, appBean);
			try
			{
			startForeground(NOTIFICATION, mDownLoadingNotify);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				
			}
		}
		else
		{
			DictBean bean = appBean.getDictBean();
			DownloadInfo info = bean.getDownloadInfo();
			if (info == null)
			{
				mDownLoadingNotify.contentView.setProgressBar(
						R.id.content_view_progress, 100, 0, false);
			}
			else
			{
				mDownLoadingNotify.contentView.setTextViewText(
						R.id.content_view_Title, mRunEvent.getTitle() + "已下载 "
								+ info.getCompeletePercentage() + "%");
				mDownLoadingNotify.contentView.setProgressBar(
						R.id.content_view_progress, 100,
						info.getCompeletePercentage(), false);
				mDownLoadingNotify.contentView.setImageViewBitmap(
						R.id.content_view_image, appBean.getLogo());
			}
			startForeground(NOTIFICATION, mDownLoadingNotify);
		}
		return mDownLoadingNotify;
	}

	/**
	 * 查看更新应用列表
	 * 
	 * @author WangMinghui
	 * 
	 */
	class UpdateTask extends AsyncTask<Object, Integer, String>
	{
		private String m_url;

		private String setpostdata()
		{
			try
			{
				JSONObject jsonObject = new JSONObject();
				List<PackageInfo> packages = mContext.getPackageManager()
						.getInstalledPackages(
								PackageManager.GET_UNINSTALLED_PACKAGES);
				if (packages != null)
				{
					for (PackageInfo packageInfo : packages)
					{
						ApplicationInfo appInfo = packageInfo.applicationInfo;
						if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
						{
							String packageName = packageInfo.applicationInfo.packageName;
							String versionCode = String
									.valueOf(packageInfo.versionCode);
							jsonObject.put(packageName, versionCode);
						}
					}
				}
				return jsonObject.toString();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			return new JSONObject().toString();
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}

		protected String doInBackground(Object... params)
		{
			m_url = (String) params[0];
			try
			{
				
				String temp = setpostdata();
				return HttpUtil.RequestGetData(m_url, temp);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result)
		{
			if (result != null)
			{
				updateList.clear();
				KUpdateItemParser parser = new KUpdateItemParser();
				parser.parseStringByData(result, updateList);
				Common.new_num = updateList.size();
			}
		}
	}
}
