/*
Copyright 2011-2013 Pieter Pareit

This file is part of SwiFTP.

SwiFTP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SwiFTP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.byfen.app;

import java.io.File;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.IBinder;
import android.util.Config;
import android.util.Log;

import com.framework.log.P;
import com.nostra13.universalimageloader.cache.disc.DiscCacheAware;
import com.nostra13.universalimageloader.cache.disc.impl.FileCountLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.work.Interface.INotifyDataSetChanged;
import com.work.market.net.Dao;
import com.work.market.server.DownLoadService;
import com.work.market.util.ExtendedImageDownloader;

public class KApp extends Application implements INotifyDataSetChanged
{
	private static final String TAG = KApp.class.getSimpleName();
	private static Context sContext;
	public Config config = null;
	// ..................启动服务..................
	private DownLoadService mDownloadService = null;
	private ServiceConnection mServiceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			P.v("ServiceConnection:onServiceConnected");
			try
			{
			mDownloadService = ((DownLoadService.LocalBinder) service).getService();
			mDownloadService.Init(sContext);
			mDownloadService.start();
			mDownloadService.setINotifyChanged(KApp.this);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		@Override
		public void onServiceDisconnected(ComponentName arg0)
		{
			P.v("ServiceConnection:onServiceDisconnected");
			try
			{
			mDownloadService.cancel();
			mDownloadService = null;
			mDownloadService.setINotifyChanged(null);

			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	};
	private INotifyDataSetChanged notify;
	public void setINotifyChanged(INotifyDataSetChanged notify)
	{
		this.notify=notify;
	}
	public DownLoadService getDownLoadService()
	{
		return mDownloadService;
	}
	public void startAndBindService()
	{
		startService(new Intent(this, DownLoadService.class));
		bindService(new Intent(this, DownLoadService.class), mServiceConnection,
				Context.BIND_AUTO_CREATE);
	}

	public void stopService()
	{
		if (mDownloadService != null)
		{
			stopService(new Intent(this, DownLoadService.class));
		}
	}
	private Dao dao = null;// 工具类

	public Dao getDao()
	{
		if(dao==null)
		{
			dao=new Dao(getAppContext());
		}
		return dao;
	}
	// ..................结束服务
	public void Init()
	{
		startAndBindService();
		String path = Environment.getExternalStorageDirectory().toString()
				+ "/baifen/img/";
		File file=new File(path);
		if(!file.exists())
		{
			file.mkdirs();
		}
		DiscCacheAware discCache=new FileCountLimitedDiscCache(file,1000);
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCacheExtraOptions(480, 480)
				// 2MB
				.memoryCacheSize(2 * 1024 * 1024)
				.denyCacheImageMultipleSizesInMemory()
				.discCache(discCache)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.imageDownloader(
						new ExtendedImageDownloader(getApplicationContext()))
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.memoryCache(new WeakMemoryCache()).build();

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		sContext = getApplicationContext();
		Init();
	}

	public static Context getAppContext()
	{
		if (sContext == null) Log.e(TAG, "Global context not set");
		return sContext;
	}

	/**
	 * @return true if this is the free version
	 */
	public static boolean isFreeVersion()
	{
		try
		{
			Context context = getAppContext();
			return context.getPackageName().contains("free");
		}
		catch (Exception swallow)
		{
		}
		return false;
	}

	/**
	 * Get the version from the manifest.
	 * 
	 * @return The version as a String.
	 */
	public static String getVersion()
	{
		Context context = getAppContext();
		String packageName = context.getPackageName();
		try
		{
			PackageManager pm = context.getPackageManager();
			return pm.getPackageInfo(packageName, 0).versionName;
		}
		catch (NameNotFoundException e)
		{
			Log.e(TAG, "Unable to find the name " + packageName
					+ " in the package");
			return null;
		}
	}
	@Override
	public void NotifyDataSetChanged(int cmd, Object value)
	{
		// TODO Auto-generated method stub
		if(notify!=null)
		notify.NotifyDataSetChanged(cmd, value);
	}
	@Override
	public void NotifyDataSetChanged(int cmd, Object value, int arg1, int arg2)
	{
		// TODO Auto-generated method stub
//		Log.v("wmh", "KApp:NotifyDataSetChanged:cmd="+cmd);
		if(notify!=null)
		notify.NotifyDataSetChanged(cmd, value, arg1, arg2);
	}

}
