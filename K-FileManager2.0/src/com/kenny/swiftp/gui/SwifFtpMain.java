/*
Copyright 2009 David Revell

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

package com.kenny.swiftp.gui;

import java.net.InetAddress;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.log.P;
import com.kenny.KFileManager.t.R;
import com.kenny.file.tools.NetTools;
import com.kenny.file.tools.StorageUtil;
import com.kenny.file.util.Const;
import com.kenny.file.util.NetConst;
import com.kenny.file.util.SDFile;
import com.kenny.ppareit.swiftp.FTPServerService;
import com.kenny.ppareit.swiftp.Globals;
import com.kenny.ppareit.swiftp.MyLog;
import com.umeng.analytics.MobclickAgent;

public class SwifFtpMain extends Activity
{
	private Button startStopButton;
	private ImageButton ConfigButton;
	private Button wifiButton;
	private Button ExitButton;

	private TextView wifiSSID;
	private TextView wifiAddr;
	private TextView wifimac;
	private TextView wifiStatusText;
	private TextView serverStatusText;
	private TextView ipText;

	private TextView tvUserName, tvPassWord, tvShareFolder;

	private TextView notif_msg, notif_title;
	private ImageView notif_ico;
	protected MyLog myLog = new MyLog(this.getClass().getName());

	private static String TAG = SwifFtpMain.class.getSimpleName();

	public Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case 0: // We are being told to do a UI update
				// If more than one UI update is queued up, we only need to do
				// one.
				removeMessages(0);
				updateUi();
				break;
			case 1: // We are being told to display an error message
				removeMessages(1);
			}
		}
	};

	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.swif_ftp_main_page);
		MobclickAgent.onEvent(this, "KMainPage", "FtpServerPage");
		
		Globals.setContext(getApplicationContext());
		Context myContext = Globals.getContext();
		
		if (myContext == null)
		{
			throw new NullPointerException("Null context!?!?!?");
		}
		ipText = (TextView) findViewById(R.id.ip_address);
		serverStatusText = (TextView) findViewById(R.id.server_status);
		wifiStatusText = (TextView) findViewById(R.id.wifi_status);

		wifiSSID = (TextView) findViewById(R.id.wifi_SSID);
		wifiAddr = (TextView) findViewById(R.id.wifi_addr);
		wifimac = (TextView) findViewById(R.id.wifi_mac);
		tvUserName = (TextView) findViewById(R.id.tvUserName);
		tvPassWord = (TextView) findViewById(R.id.tvPassWord);
		tvShareFolder = (TextView) findViewById(R.id.tvShareFolder);

		startStopButton = (Button) findViewById(R.id.start_stop_button);
		ConfigButton = (ImageButton) findViewById(R.id.btSetting);
		wifiButton = (Button) findViewById(R.id.wifi_button);
		ExitButton = (Button) findViewById(R.id.btBack);

		startStopButton.setOnClickListener(startStopListener);
		ConfigButton.setOnClickListener(configListener);
		ExitButton.setOnClickListener(exitListener);
		wifiButton.setOnClickListener(wifiButtonListener);

		notif_ico = (ImageView) findViewById(R.id.server_notif_ico);
		notif_title = (TextView) findViewById(R.id.notif_title);
		notif_msg = (TextView) findViewById(R.id.notif_msg);
	}

	/*
	 * Whenever we lose focus, we must unregister from UI update messages from
	 * the FTPServerService, because we may be deallocated.
	 */
	protected void onPause()
	{
		super.onPause();
		unregisterReceiver(ftpServerReceiver);
		this.unregisterReceiver(sdcardReceiver);

	}
	protected void onResume()
	{
		super.onResume();
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);

		String username = settings.getString(FtpConfigure.USERNAME, null);
		String password = settings.getString(FtpConfigure.PASSWORD, null);
		String rootdir = settings.getString(FtpConfigure.CHROOTDIR, null);
		if (username == null || password == null|| rootdir == null)
		{
			ReInit();
			username = settings.getString(FtpConfigure.USERNAME, null);
			password = settings.getString(FtpConfigure.PASSWORD, null);
			rootdir = settings.getString(FtpConfigure.CHROOTDIR, null);
		}
		tvUserName.setText(username);
		tvPassWord.setText(password);
		tvShareFolder.setText(rootdir);

		updateUi();

		// this.registerReceiver(wifiReceiver, new IntentFilter(
		// WifiManager.WIFI_STATE_CHANGED_ACTION));

		IntentFilter sdCardFilter = new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED);
		sdCardFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		sdCardFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		// intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
		sdCardFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		// intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
		// intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		sdCardFilter.addDataScheme("file");
		registerReceiver(sdcardReceiver, sdCardFilter);// 注册监听函数

		// UiUpdater.registerClient(handler);
		IntentFilter filter = new IntentFilter();
		filter.addAction(FTPServerService.ACTION_STARTED);
		filter.addAction(FTPServerService.ACTION_STOPPED);
		filter.addAction(FTPServerService.ACTION_FAILEDTOSTART);
		registerReceiver(ftpServerReceiver, filter);
	}

	

	private boolean ReInit()
	{
		try
		{
			String sdroot = Const.Root;
			if (StorageUtil.checkSDCard())
			{
				sdroot = Const.getSDCard();
			}
			SharedPreferences settings = PreferenceManager
					.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(FtpConfigure.USERNAME, "admin");// admin
			editor.putString(FtpConfigure.PASSWORD, "123456");// 111111
			editor.putString(FtpConfigure.PORTNUM, "8880"); // 8530
			editor.putString(FtpConfigure.CHROOTDIR, sdroot);// /11
			editor.putBoolean(FtpConfigure.ACCEPT_WIFI, true);// true
			editor.putBoolean(FtpConfigure.ACCEPT_NET, false); // false
			editor.putBoolean(FtpConfigure.STAY_AWAKE, false); // false
			editor.commit();
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}


	BroadcastReceiver ftpServerReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.v(TAG,
					"FTPServerService action received: " + intent.getAction());
			if (intent.getAction().equals(FTPServerService.ACTION_STARTED))
			{
				SwifFTPNotification.setupNotification(getApplicationContext());
				updateUi();
			} else if (intent.getAction().equals(
					FTPServerService.ACTION_STOPPED))
			{
				updateUi();
				SwifFTPNotification.clearNotification(getApplicationContext());
			} else if (intent.getAction().equals(
					FTPServerService.ACTION_FAILEDTOSTART))
			{
				SwifFTPNotification.clearNotification(getApplicationContext());
				updateUi();
			}
		}
	};

	/**
	 * Will check if the device contains external storage (sdcard) and display a
	 * warning for the user if there is no external storage. Nothing more.
	 */
	private void warnIfNoExternalStorage()
	{
		String storageState = Environment.getExternalStorageState();
		if (!storageState.equals(Environment.MEDIA_MOUNTED))
		{
			Log.v(TAG, "Warning due to storage state " + storageState);
			Toast toast = Toast.makeText(this, R.string.storage_warning,
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}

	protected void onStop()
	{
		super.onStop();
	}

	protected void onDestroy()
	{
		super.onDestroy();
	}

	private boolean updateWifiUi()
	{

		if (NetConst.isWiFiActive(this))
		{
			wifiStatusText.setText(R.string.wifi_enabled);
			startStopButton.setVisibility(View.VISIBLE);
			wifiButton.setVisibility(View.GONE);

			WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
			if (wifiInfo != null)
			{ 
				// Wifi连接成功
				if (wifiInfo.getSSID() == null)
				{
					wifiSSID.setText(R.string.unknown);
				} else
				{
					wifiSSID.setText(wifiInfo.getSSID());
				}
				if (wifiInfo.getMacAddress() == null)
				{
					wifimac.setText(R.string.unknown);
				} else
				{
					wifimac.setText(wifiInfo.getMacAddress());
				}
				wifiAddr.setText(NetTools.intToIp(wifiInfo.getIpAddress()));
			}
			return true;
		} else
		{ // Wifi连接失败
			wifimac.setText(R.string.unknown);
			wifiSSID.setText(R.string.unknown);
			wifiAddr.setText(NetTools.intToIp(0));

			notif_ico.setImageResource(R.drawable.list_icon_risk);
			notif_title.setText(R.string.status_unwifi);
			notif_msg.setText(R.string.status_desc_unwifi);

			wifiStatusText.setText(R.string.wifi_disabled);
			startStopButton.setVisibility(View.GONE);
			wifiButton.setVisibility(View.VISIBLE);
		}
		return false;
	}

	private void updateFTPServerUi()
	{

		if (FTPServerService.isRunning())
		{
			myLog.l(Log.DEBUG, "updateUi: server is running", true);
			// Put correct text in start/stop button
			startStopButton.setText(R.string.stop_server);

			// Fill in wifi status and address
			InetAddress address = FTPServerService.getLocalInetAddress();
			if (address != null)
			{
				ipText.setText("ftp://" + address.getHostAddress() + ":"
						+ FTPServerService.getPort() + "/");
			} else
			{
				myLog.l(Log.VERBOSE, "Null address from getServerAddress()",
						true);
				ipText.setText(R.string.cant_get_url);
			}
			serverStatusText.setText(R.string.running);

			notif_ico.setImageResource(R.drawable.list_icon_security);
			notif_title.setText(R.string.status_server_on);
			notif_msg.setText(R.string.status_desc_server_on);
			if (!SDFile.checkSDCard())
			{
				notif_msg.setText(R.string.status_desc_unSDCard);
			}
		} else
		{
			myLog.l(Log.DEBUG, "updateUi: server is not running", true);
			// Update the start/stop button to show the correct text
			ipText.setText(R.string.unknown);
			serverStatusText.setText(R.string.stopped);
			startStopButton.setText(R.string.start_server);

			notif_ico.setImageResource(R.drawable.list_icon_optimization);
			notif_title.setText(R.string.status_server_off);
			notif_msg.setText(R.string.status_desc_server_off);
		}
	}

	/**
	 * This will be called by the static UiUpdater whenever the service has
	 * changed state in a way that requires us to update our UI.
	 * 
	 * We can't use any myLog.l() calls in this function, because that will
	 * trigger an endless loop of UI updates.
	 */
	public void updateUi()
	{
		if (updateWifiUi())
		{
			updateFTPServerUi();
		}
	}
	private void startServer()
	{
		Context context = getApplicationContext();
		Intent serverService = new Intent(context, FTPServerService.class);
		if (!FTPServerService.isRunning())
		{
			warnIfNoExternalStorage();
			startService(serverService);
		}
	}

	private void stopServer()
	{
		Context context = getApplicationContext();
		Intent serverService = new Intent(context, FTPServerService.class);
		stopService(serverService);
	}

	OnClickListener startStopListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			/*
			 * In order to choose whether to stop or start the server, we check
			 * the text on the button to see which action the user was
			 * expecting.
			 */
			String startString = getString(R.string.start_server);
			String stopString = getString(R.string.stop_server);
			String buttonText = startStopButton.getText().toString();
			if (buttonText.equals(startString))
			{
				/* The button had the "start server" text */
				startServer();
			} else if (buttonText.equals(stopString))
			{
				/*
				 * The button had the "stop server" text. We stop the server
				 * now.
				 */
				stopServer();
			} else
			{
				// Do nothing
				myLog.l(Log.ERROR, "Unrecognized start/stop text");
			}
		}
	};
	OnClickListener wifiButtonListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			startActivity(new Intent(
					android.provider.Settings.ACTION_WIFI_SETTINGS)); // 直接进入手机中的wifi网络设置界面
		}
	};
	/**
	 * A call-back for when the user presses the "setup" button.
	 */
	OnClickListener exitListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			finish();
		}
	};
	/**
	 * A call-back for when the user presses the "setup" button.
	 */
	OnClickListener configListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			 Intent intent = new Intent(SwifFtpMain.this, FtpConfigure.class);
			 startActivity(intent);
		}
	};

	BroadcastReceiver wifiReceiver = new BroadcastReceiver()
	{
		public void onReceive(Context ctx, Intent intent)
		{
			Bundle bundle = intent.getExtras();
			int oldInt = bundle.getInt("previous_wifi_state");
			int newInt = bundle.getInt("wifi_state");

			P.v("wmh", "oldInt=" + oldInt + ":newInt=" + newInt);
			if (newInt == WifiManager.WIFI_STATE_DISABLED
					|| newInt == WifiManager.WIFI_STATE_ENABLED)
			{
				updateUi();
			}
		}
	};

	BroadcastReceiver sdcardReceiver = new BroadcastReceiver()
	{
		public void onReceive(Context ctx, Intent intent)
		{
			myLog.l(Log.DEBUG, "Wifi status broadcast received");
			updateUi();
		}
	};
}
