package com.byfen.market;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.byfen.app.KApp;
import com.framework.log.P;
import com.framework.syseng.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.file.tools.ApkTools;
import com.work.Interface.INotifyDataSetChanged;
import com.work.market.adapter.UpdateAdapter;
import com.work.market.bean.AppListBean;
import com.work.market.net.Common;
import com.work.market.net.DictBean;
import com.work.market.net.Downloader;
import com.work.market.net.HttpUtil;
import com.work.market.server.DownLoadService;
import com.work.market.server.KUpdateItemParser;
import com.work.market.util.HttpUrlConst;

public class UpdateActivity extends Activity implements INotifyDataSetChanged
{

	// 更新链接地址
	public UpdateAdapter adapter;
	private ListView lv1;
	private IntentFilter mIntentFilter; // 消息处理
	private NetTask1 mNetTask;
	private Context mContext;
	private KApp app;
	private List<AppListBean> mList;
	private LinearLayout m_all_lin;
	//..................启动服务..................
		private DownLoadService mPlaybackService = null;
		private ServiceConnection mServiceConnection=new ServiceConnection()
		{
			@Override
			public void onServiceConnected(ComponentName className, IBinder service)
			{
				P.v("ServiceConnection:onServiceConnected");
				mPlaybackService = ((DownLoadService.LocalBinder) service)
						.getService();
				mList = mPlaybackService.getUpdateList();
				adapter = new UpdateAdapter(UpdateActivity.this, mList,mPlaybackService);
				lv1.setAdapter(adapter);
				if(mList.size()==0)
				{
					upDatasoft(true);
				}
			}

			@Override
			public void onServiceDisconnected(ComponentName arg0)
			{
				P.v("ServiceConnection:onServiceDisconnected");
				mPlaybackService = null;			
			}
			
		};
		void startAndBindService()
		{
			startService(new Intent(this, DownLoadService.class));
			bindService(new Intent(this, DownLoadService.class), mServiceConnection,
					Context.BIND_AUTO_CREATE);
		}

		void stopService()
		{
			if (mPlaybackService != null)
			{
				stopService(new Intent(this, DownLoadService.class));
			}
		}
	/**
	 * 进入页面
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		// 加载页面
		setContentView(R.layout.update_page);
		mContext = this;
		// 异常处理
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("com.android.my.action");
		mIntentFilter.addAction("com.android.my.action.sticky");
		m_all_lin = (LinearLayout) findViewById(R.id.mynew_all_lin);
		m_all_lin.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				StartDownLoadAllApk();
				adapter.notifyDataSetChanged();
			}
		});
		lv1 = (ListView) findViewById(R.id.new_ls);
		app=((KApp)getApplicationContext());
		mPlaybackService=app.getDownLoadService();
		if(mPlaybackService==null)
		{
			startAndBindService();
		}
		else
		{
			mList = mPlaybackService.getUpdateList();
			adapter = new UpdateAdapter(UpdateActivity.this, mList,mPlaybackService);
			lv1.setAdapter(adapter);
			if(mList.size()==0)
			{
				upDatasoft(true);
			}
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (adapter != null) adapter.notifyDataSetChanged();
		}
	};

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(mReceiver, mIntentFilter);
		app.setINotifyChanged(this);
		CheckListChanage();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		unregisterReceiver(mReceiver);
		app.setINotifyChanged(null);
	}

	private void upDatasoft(boolean apd)
	{
		if (mNetTask != null)
		{
			mNetTask.cancel(true);
		}
		mNetTask = new NetTask1();
		mNetTask.execute(HttpUrlConst.UPDATE_URLS, apd);// m_ad_layout.setVisibility
	}

	// 图片处理
	class NetTask1 extends AsyncTask<Object, Integer, String>
	{
		private boolean m_showlog = true;
		private String m_url;
		private ProgressDialog pd;

		private String setpostdata() throws JSONException
		{
			JSONObject jsonObject = new JSONObject();
			List<PackageInfo> packages = UpdateActivity.this.getPackageManager()
					.getInstalledPackages(
							PackageManager.GET_UNINSTALLED_PACKAGES);
			for (PackageInfo packageInfo : packages)
			{
				ApplicationInfo appInfo = packageInfo.applicationInfo;
				if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
				{
					String temp1 = packageInfo.applicationInfo.packageName;
					String temp = String.valueOf(packageInfo.versionCode);
					jsonObject.put(temp1, temp);
				}
			}
			return jsonObject.toString();
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			if (m_showlog)
			{
				pd = new ProgressDialog(UpdateActivity.this);
				pd.setMessage(getText(R.string.pd_loading));
				pd.setCancelable(true);
				pd.show();
			}
		}

		protected String doInBackground(Object... params)
		{
			m_url = (String) params[0];
			try
			{
				String temp = setpostdata();
				return HttpUtil.RequestGetData(m_url, temp);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result)
		{
			if (result == null)
			{// 失败 处理
				String dialogstring = getString(R.string.net_faile);
				Toast.makeText(mContext, dialogstring, Toast.LENGTH_LONG)
						.show();
			}
			else
			{
				mList.clear();
				KUpdateItemParser parser = new KUpdateItemParser();
				parser.parseStringByData(result, mList);
				Common.new_num = mList.size();
				adapter.notifyDataSetChanged();
			}
			if (pd != null)
			{
				pd.dismiss();
				pd = null;
			}
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if (keyCode == KeyEvent.KEYCODE_BACK)
			{
				new AlertDialog.Builder(this)
						.setTitle("提示")
						.setMessage("是否要退出软件？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener()
								{
									public void onClick(
											DialogInterface dialoginterface,
											int i)
									{
										Intent serviceIntent = new Intent(
												UpdateActivity.this,
												DownLoadService.class);
										serviceIntent
												.putExtra("type", "finish");
										stopService(serviceIntent);
										finish();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener()
								{
									public void onClick(
											DialogInterface dialoginterface,
											int i)
									{

									}
								}).show();
			}
		}
		return true;
	}

	/**
	 * 全部更新
	 */
	public void StartDownLoadAllApk()
	{
		for (int i = 0; i < mList.size(); i++)
		{
			AppListBean bean = mList.get(i);
			DictBean downing = bean.getDownloading();
			if(downing!=null)
			{
				switch (downing.getStatus()) {
				case Downloader.INIT:
				case Downloader.UPDATE:// 更新
					bean.Start(mContext);
					break;
				case Downloader.INIT_SERVER:
				case Downloader.DOWNLOADING:
				case Downloader.WAIT:
					//downing.Pause();
					break;
				case Downloader.PAUSE://暂停中
					bean.Start(mContext);
					break;
				case Downloader.FINISH://下载完成
					try
					{
						PackageInfo packages=mContext.getPackageManager().getPackageInfo(bean.getPn(),1);
						if(packages.versionCode==bean.getVercode())
						{//运行
							Toast.makeText(mContext, "运行",Toast.LENGTH_LONG).show();
							ApkTools.StartApk(mContext,bean.getPn());
						}
						else
						{//安装
							Toast.makeText(mContext, "安装",Toast.LENGTH_LONG).show();
							ApkTools.InstallApk(mContext, downing.getFilePath());
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						Toast.makeText(mContext, "安装",Toast.LENGTH_LONG).show();
						ApkTools.InstallApk(mContext, downing.getFilePath());					
					}
//					viewHolder.mydownimage.setBackgroundResource(R.drawable.goon);
//					viewHolder.mydowntext.setText("打开");
					break;
				default:
					bean.Start(mContext);
					break;
				}
			}
			else
			{
				bean.Start(mContext);
			}
		}
	}

	/**
	 * 检测去掉已经更新的软件
	 */
	public void CheckListChanage()
	{
		for (int i = 0; i < mList.size(); i++)
		{
			AppListBean bean = mList.get(i);
			try
			{
				PackageInfo packages = this.getPackageManager().getPackageInfo(
						bean.getPn(), 1);
				if (packages != null) if (mList.get(i).getVercode() == packages.versionCode)
				{
					mList.remove(i);
					i--;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		Common.new_num = mList.size();
		adapter.notifyDataSetChanged();
	}
	@Override
	public void NotifyDataSetChanged(int cmd, Object value)
	{
		
	}

	@Override
	public void NotifyDataSetChanged(int cmd, Object value, int arg1, int arg2)
	{
		if (cmd == Downloader.CHANGER_STATUS)//状态信息
		{
			SysEng.getInstance().addHandlerEvent(new AbsEvent()
			{
				@Override
				public void ok()
				{
					adapter.notifyDataSetChanged();
				}
			});
		}
	}
}
