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

package com.kenny.swiftp;

import java.net.InetAddress;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.log.P;
import com.kenny.KFileManager.R;
import com.kenny.file.util.Const;
import com.kenny.file.util.NetConst;
import com.kenny.file.util.SDFile;
import com.umeng.analytics.MobclickAgent;

public class FtpServer extends Activity
{
   
   public final static String USERNAME = "username";
   public final static String PASSWORD = "password";
   public final static String PORTNUM = "portNum";
   public final static String CHROOTDIR = "chrootDir";
   public final static String ACCEPT_WIFI = "allowWifi";
   public final static String ACCEPT_NET = "allowNet";
   public final static String STAY_AWAKE = "stayAwake";
   
   private Button startStopButton;
   // private Button addUserButton;
   // private Button manageUsersButton;
   // private Button serverOptionsButton;
   // private Button instructionsButton;
   private ImageButton ConfigButton;
   private Button wifiButton;
   private Button ExitButton;
   
   private TextView wifiSSID;
   private TextView wifiAddr;
   private TextView wifimac;
   private TextView wifiStatusText;
   private TextView serverStatusText;
   private TextView ipText;
   private TextView lastErrorText;
   
   private TextView proxyStatusText;
   private TextView proxyUrlText;
   private TextView proxyUsedText;
   private TextView proxyUrlLabel;
   private TextView proxyUsedLabel;
   private TextView proxyNewsLabel;
   private TextView proxyNews;
   
   private TextView sessionMonitor;
   private CheckBox sessionMonitorCheckBox;
   private TextView serverLog;
   private CheckBox serverLogCheckBox;
   
   private TextView tvUserName, tvPassWord, tvShareFolder;
   
   private TextView notif_msg, notif_title;
   private ImageView notif_ico;
   protected MyLog myLog = new MyLog(this.getClass().getName());
   
   protected Context activityContext = this;
   
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
      
      MobclickAgent.onEvent(this, "KMainPage", "FtpServerPage");
      // Request no title bar on our window
      // requestWindowFeature(Window.FEATURE_NO_TITLE);
      // Set the application-wide context global, if not already set
      Context myContext = Globals.getContext();
      if (myContext == null)
      {
         myContext = getApplicationContext();
         if (myContext == null) { throw new NullPointerException(
	     "Null context!?!?!?"); }
         Globals.setContext(myContext);
      }
      // Inflate our UI from its XML layout description.
      setContentView(R.layout.ftp_server);
      
      ipText = (TextView) findViewById(R.id.ip_address);
      serverStatusText = (TextView) findViewById(R.id.server_status);
      wifiStatusText = (TextView) findViewById(R.id.wifi_status);
      
      wifiSSID = (TextView) findViewById(R.id.wifi_SSID);
      wifiAddr = (TextView) findViewById(R.id.wifi_addr);
      wifimac = (TextView) findViewById(R.id.wifi_mac);
      tvUserName = (TextView) findViewById(R.id.tvUserName);
      tvPassWord = (TextView) findViewById(R.id.tvPassWord);
      tvShareFolder = (TextView) findViewById(R.id.tvShareFolder);
      
      lastErrorText = (TextView) findViewById(R.id.last_error);
      
      proxyStatusText = (TextView) findViewById(R.id.proxy_status);
      proxyUrlText = (TextView) findViewById(R.id.proxy_url);
      proxyUrlLabel = (TextView) findViewById(R.id.proxy_url_label);
      proxyUsedText = (TextView) findViewById(R.id.proxy_transferred);
      proxyUsedLabel = (TextView) findViewById(R.id.proxy_transferred_label);
      proxyNewsLabel = (TextView) findViewById(R.id.proxy_news_label);
      proxyNews = (TextView) findViewById(R.id.proxy_news);
      
      startStopButton = (Button) findViewById(R.id.start_stop_button);
      // addUserButton = (Button) findViewById(R.id.add_user_button);
      // manageUsersButton = (Button) findViewById(R.id.manage_users_button);
      // serverOptionsButton = (Button)
      // findViewById(R.id.server_options_button);
      // instructionsButton = (Button) findViewById(R.id.instructions);
      ConfigButton = (ImageButton) findViewById(R.id.btSetting);
      wifiButton = (Button) findViewById(R.id.wifi_button);
      ExitButton = (Button) findViewById(R.id.btBack);
      
      startStopButton.setOnClickListener(startStopListener);
      // addUserButton.setOnClickListener(addUserListener);
      // manageUsersButton.setOnClickListener(manageUsersListener);
      // serverOptionsButton.setOnClickListener(serverOptionsListener);
      // instructionsButton.setOnClickListener(instructionsListener);
      ConfigButton.setOnClickListener(configListener);
      ExitButton.setOnClickListener(exitListener);
      wifiButton.setOnClickListener(wifiButtonListener);
      
      sessionMonitor = (TextView) findViewById(R.id.session_monitor);
      sessionMonitorCheckBox = (CheckBox) findViewById(R.id.session_monitor_checkbox);
      serverLog = (TextView) findViewById(R.id.server_log);
      serverLogCheckBox = (CheckBox) findViewById(R.id.server_log_checkbox);
      
      notif_ico = (ImageView) findViewById(R.id.server_notif_ico);
      notif_title = (TextView) findViewById(R.id.notif_title);
      notif_msg = (TextView) findViewById(R.id.notif_msg);
      // sessionMonitor.setHeight(1);
      // serverLog.setHeight(1);
      
      sessionMonitorCheckBox.setOnClickListener(sessionMonitorCheckBoxListener);
      serverLogCheckBox.setOnClickListener(serverLogCheckBoxListener);
      
      // If the required preferences are not present, launch the configuration
      // Activity.
      // SharedPreferences settings = getSharedPreferences(
      // Defaults.getSettingsName(), Defaults.getSettingsMode());
      // String username = settings.getString("username", null);
      // String password = settings.getString("password", null);
      // if(username == null || password == null) {
      // launchConfigureActivity();
      // }
      // updateUi();
      
   }
   
   protected void onResume()
   {
      super.onResume();
      Log.v("wmh", "onResume");
      SharedPreferences settings = getSharedPreferences(
	  Defaults.getSettingsName(), Defaults.getSettingsMode());
      // If the required preferences are not present, launch the configuration
      String username = settings.getString(USERNAME, null);
      String password = settings.getString(PASSWORD, null);
      String rootdir = settings.getString(CHROOTDIR, null);
      if (username == null || password == null)
      {
         ReInit();
         username = settings.getString(USERNAME, null);
         password = settings.getString(PASSWORD, null);
         rootdir = settings.getString(CHROOTDIR, null);
      }
      tvUserName.setText(username);
      tvPassWord.setText(password);
      tvShareFolder.setText(rootdir);
      
      UiUpdater.registerClient(handler);
      updateUi();
      
      // this.registerReceiver(wifiReceiver, new IntentFilter(
      // WifiManager.WIFI_STATE_CHANGED_ACTION));
      
      IntentFilter sdCardFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
      sdCardFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
      sdCardFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
      // intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
      sdCardFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
      // intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
      // intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
      sdCardFilter.addDataScheme("file");
      registerReceiver(sdcardReceiver, sdCardFilter);// 注册监听函数
      Log.v("wmh", "onResume end");
   }
   
   private boolean ReInit()
   {
      try
      {
         String sdroot = Const.Root;
         if (Util.checkSDCard())
         {
	  sdroot = Const.SDCard;
         }
         
         SharedPreferences settings = getSharedPreferences(
	     Defaults.getSettingsName(), Defaults.getSettingsMode());
         SharedPreferences.Editor editor = settings.edit();
         editor.putString(USERNAME, "admin");// admin
         editor.putString(PASSWORD, "123456");// 111111
         editor.putInt(PORTNUM, 8880); // 8530
         editor.putString(CHROOTDIR, sdroot);// /11
         editor.putBoolean(ACCEPT_WIFI, true);// true
         editor.putBoolean(ACCEPT_NET, false); // false
         editor.putBoolean(STAY_AWAKE, false); // false
         editor.commit();
         return true;
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return false;
      }
   }
   
   /*
    * Whenever we lose focus, we must unregister from UI update messages from
    * the FTPServerService, because we may be deallocated.
    */
   protected void onPause()
   {
      super.onPause();
      UiUpdater.unregisterClient(handler);
      myLog.l(Log.DEBUG, "Unregistered for wifi updates");
      // this.unregisterReceiver(wifiReceiver);
      this.unregisterReceiver(sdcardReceiver);
      
   }
   
   protected void onStop()
   {
      super.onStop();
      UiUpdater.unregisterClient(handler);
   }
   
   protected void onDestroy()
   {
      super.onDestroy();
      UiUpdater.unregisterClient(handler);
   }
   
   private void updateProxyUi()
   {
      /**
       * GPRS协议
       */
      ProxyConnector proxyConnector = Globals.getProxyConnector();
      boolean proxyEnabled = getSettings().getBoolean(FtpConfigure.ACCEPT_NET,
	  false);
      
      if (proxyEnabled)
      {
         proxyUrlText.setVisibility(View.VISIBLE);
         proxyUrlLabel.setVisibility(View.VISIBLE);
         proxyUsedText.setVisibility(View.VISIBLE);
         proxyUsedLabel.setVisibility(View.VISIBLE);
         if (proxyConnector == null)
         {
	  proxyStatusText.setText(R.string.pst_disconnected);
	  proxyUrlText.setText(R.string.unknown);
         }
         else
         {
	  ProxyConnector.State proxyState = proxyConnector.getProxyState();
	  proxyStatusText.setText(ProxyConnector.stateToString(proxyState));
	  float proxyGigs = Math.abs( // bytes to gigabytes
	        (float) proxyConnector.getProxyUsage() / (float) 1073741824);
	  proxyUsedText.setText(String.format("%.2f GB", proxyGigs));
	  proxyUrlText.setText(proxyConnector.getURL());
         }
      }
      else
      {
         proxyStatusText.setText(R.string.disabled);
         proxyUrlText.setVisibility(View.GONE);
         proxyUrlLabel.setVisibility(View.GONE);
         proxyUsedText.setVisibility(View.GONE);
         proxyUsedLabel.setVisibility(View.GONE);
      }
      
      if (proxyConnector != null)
      {
         String news = proxyConnector.getProxyMessage();
         if (news != null)
         {
	  proxyNews.setText(news);
	  proxyNews.setVisibility(View.VISIBLE);
	  proxyNewsLabel.setVisibility(View.VISIBLE);
         }
      }
      
   }
   
   private boolean updateWifiUi()
   {
      
      if (NetConst.isWiFiActive(this))
      {
         wifiStatusText.setText(R.string.wifi_enabled);
         startStopButton.setVisibility(View.VISIBLE);
         wifiButton.setVisibility(View.GONE);
         
         WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
         Log.v("wmh", "updateWifiUi");
         WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
         Log.v("wmh", "updateWifiUi end");
         if (wifiInfo != null)
         { // Wifi连接成功
	  if (wifiInfo.getSSID() == null)
	  {
	     wifiSSID.setText(R.string.unknown);
	  }
	  else
	  {
	     wifiSSID.setText(wifiInfo.getSSID());
	  }
	  if (wifiInfo.getMacAddress() == null)
	  {
	     wifimac.setText(R.string.unknown);
	  }
	  else
	  {
	     wifimac.setText(wifiInfo.getMacAddress());
	  }
	  wifiAddr.setText(intToIp(wifiInfo.getIpAddress()));
         }
         return true;
      }
      else
      { // Wifi连接失败
         wifimac.setText(R.string.unknown);
         wifiSSID.setText(R.string.unknown);
         wifiAddr.setText(intToIp(0));
         
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
         InetAddress address = FTPServerService.getWifiIp();
         if (address != null)
         {
	  ipText.setText("ftp://" + address.getHostAddress() + ":"
	        + FTPServerService.getPort() + "/");
         }
         else
         {
	  myLog.l(Log.VERBOSE, "Null address from getServerAddress()", true);
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
      }
      else
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
   
   private void updateErrorUi()
   {
      // Manage the visibility and text of the "last error" display
      // and popup a dialog box, if there has been an error
      String errString;
      if ((errString = Globals.getLastError()) != null)
      {
         Globals.setLastError(null); // Clear the error condition after
	                           // retrieving
         lastErrorText.setText(errString);
         lastErrorText.setVisibility(View.VISIBLE);
         TextView lastErrorLabel = (TextView) findViewById(R.id.last_error_label);
         lastErrorLabel.setVisibility(View.VISIBLE);
         
         // TextView textView = new TextView(this);
         // textView.setText(R.string.error_dialog_text);
         
         // Commented out the below code for 1.20.
         // AlertDialog dialog = new AlertDialog.Builder(this).create();
         // CharSequence text = getText(R.string.error_dialog_text);
         // String str = text.toString().replace("%%%Replace_Here%%%",
         // errString);
         // //text = text + "\n\n" + getText(R.string.the_error_was) + "\n\n"
         // // + errString;
         // dialog.setMessage(str);
         // dialog.setTitle(getText(R.string.error_dialog_label));
         // dialog.setButton(getText(R.string.ok), ignoreDialogListener);
         // dialog.show();
      }
      else
      {
         TextView lastErrorLabel = (TextView) findViewById(R.id.last_error_label);
         lastErrorText.setVisibility(View.GONE);
         lastErrorLabel.setVisibility(View.GONE);
      }
      
      // If the session monitor is enabled, then retrieve the contents
      // from the FTPServerService
      if (sessionMonitorCheckBox.isChecked())
      {
         sessionMonitor.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
         List<String> lines = FTPServerService.getSessionMonitorContents();
         int size = Defaults.getSessionMonitorScrollBack();
         sessionMonitor.setMinLines(size);
         sessionMonitor.setMaxLines(size);
         String showText = "";
         for (String line : lines)
         {
	  showText += showText + line + "\n";
         }
         sessionMonitor.setText(showText);
      }
      else
      {
         sessionMonitor.setHeight(1);
      }
      if (serverLogCheckBox.isChecked())
      {
         // If the server log is visible, then retrieve the contents
         // from the FTPServerService
         serverLog.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
         List<String> lines = FTPServerService.getServerLogContents();
         // Log.d("", "Got " + lines.size() + " lines from server");
         int size = Defaults.getServerLogScrollBack();
         serverLog.setMinLines(size);
         serverLog.setMaxLines(size);
         String showText = "";
         for (String line : lines)
         {
	  showText = showText + line + "\n";
         }
         serverLog.setText(showText);
      }
      else
      {
         serverLog.setHeight(1);
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
      Log.v("wmh", "updateUi");
      
      // updateProxyUi();
      
      // This is old code, from when the server would refuse to start without
      // wifi being enabled. Now, the server can start at any time.
      // // Manage the visibility of the start/stop button based on wifi state
      // if(wifiState == WifiManager.WIFI_STATE_ENABLED) {
      // //myLog.l(Log.DEBUG,
      // "Showing start/stop button due to enabled wifi");
      // startStopButton.setVisibility(View.VISIBLE);
      // } else {
      // // Only hide the button if the server is not running. If it is
      // // running, the button should be left visible so the server
      // // can be stopped, regardless of wifi state.
      // if(!FTPServerService.isRunning()) {
      // //myLog.l(Log.DEBUG,
      // "Hiding start/stop button due to disabled wifi");
      // startStopButton.setVisibility(View.GONE);
      // } else {
      // //myLog.l(Log.DEBUG, "Would hide startStopButton but running");
      // }
      // }
      
      // Manage the text of the wifi enable/disable button and the
      // wifi status text.
      
      if (updateWifiUi())
      {
         updateFTPServerUi();
      }
      // updateErrorUi();
      
      Log.v("wmh", "updateUi end");
   }
   
   private String intToIp(int ip)
   
   {
      
      return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF)
	  + "."
	  
	  + ((ip >> 24) & 0xFF);
      
   }
   
   /**
    * Called when your activity's options menu needs to be created.
    */
   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      super.onCreateOptionsMenu(menu);
      return true;
   }
   
   /**
    * Called right before your activity's option menu is displayed.
    */
   @Override
   public boolean onPrepareOptionsMenu(Menu menu)
   {
      super.onPrepareOptionsMenu(menu);
      return true;
   }
   
   /**
    * Called when a menu item is selected.
    */
   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      /*
       * switch (item.getItemId()) { case BACK_ID: finish(); return true; case
       * CLEAR_ID: mEditor.setText(""); return true; }
       */
      
      return super.onOptionsItemSelected(item);
   }
   
   OnClickListener startStopListener = new OnClickListener()
   {
      public void onClick(View v)
      {
         Context context = getApplicationContext();
         Intent intent = new Intent(context, FTPServerService.class);
         /*
          * In order to choose whether to stop or start the server, we check the
          * text on the button to see which action the user was expecting.
          */
         String startString = getString(R.string.start_server);
         String stopString = getString(R.string.stop_server);
         String buttonText = startStopButton.getText().toString();
         if (buttonText.equals(startString))
         {
	  /* The button had the "start server" text */
	  if (!FTPServerService.isRunning())
	  {
	     warnIfNoExternalStorage();
	     context.startService(intent);
	  }
         }
         else if (buttonText.equals(stopString))
         {
	  /*
	   * The button had the "stop server" text. We stop the server now.
	   */
	  context.stopService(intent);
         }
         else
         {
	  // Do nothing
	  myLog.l(Log.ERROR, "Unrecognized start/stop text");
         }
      }
   };
   
   private void warnIfNoExternalStorage()
   {
      String storageState = Environment.getExternalStorageState();
      if (!storageState.equals(Environment.MEDIA_MOUNTED))
      {
         myLog.i("Warning due to storage state " + storageState);
         Toast toast = Toast.makeText(this, R.string.storage_warning,
	     Toast.LENGTH_LONG);
         toast.setGravity(Gravity.CENTER, 0, 0);
         toast.show();
      }
   }
   
   private void checkNetworkInfo()
   {
      ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
      
      // mobile 3G Data Network
      // State mobile =
      // conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
      // txt3G.setText(mobile.toString());
      // wifi
      State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
	  .getState();
      // txtWifi.setText(wifi.toString());
      
      // 如果3G网络和wifi网络都未连接，且不是处于正在连接状态 则进入Network Setting界面 由用户配置网络连接
      // if(mobile==State.CONNECTED||mobile==State.CONNECTING)
      // return;
      if (wifi == State.CONNECTED || wifi == State.CONNECTING) return;
      
      // startActivity(new
      // Intent(Settings.ACTION_WIRELESS_SETTINGS));//进入无线网络配置界面
      // startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
      // //进入手机中的wifi网络设置界面
      
   }
   
   /* SwiFTP no longer offers wifi enable/disable functionality */
   OnClickListener wifiButtonListener = new OnClickListener()
   {
      public void onClick(View v)
      {
         startActivity(new Intent(
	     android.provider.Settings.ACTION_WIFI_SETTINGS)); // 直接进入手机中的wifi网络设置界面
      }
   };
   
   OnClickListener addUserListener = new OnClickListener()
   {
      public void onClick(View v)
      {
         myLog.l(Log.INFO, "Add user stub");
      }
   };
   
   OnClickListener manageUsersListener = new OnClickListener()
   {
      public void onClick(View v)
      {
         myLog.l(Log.INFO, "Manage users stub");
      }
   };
   
   OnClickListener serverOptionsListener = new OnClickListener()
   {
      public void onClick(View v)
      {
         myLog.l(Log.INFO, "Server options stub");
      }
   };
   
   DialogInterface.OnClickListener ignoreDialogListener = new DialogInterface.OnClickListener()
   {
      public void onClick(DialogInterface dialog, int which)
      {
      }
   };
   
   /**
    * A call-back for when the user presses the instructions button.
    */
   // OnClickListener instructionsListener = new OnClickListener()
   // {
   // public void onClick(View v)
   // {
   //
   // TextView textView = new TextView(getApplicationContext());
   // textView.setText(R.string.instructions_text);
   //
   // AlertDialog dialog = new AlertDialog.Builder(activityContext)
   // .create();
   // CharSequence instructions = getText(R.string.instructions_text);
   // dialog.setMessage(instructions);
   // dialog.setTitle(getText(R.string.instructions_label));
   // dialog.setButton(getText(R.string.ok), ignoreDialogListener);
   // dialog.show();
   //
   // }
   // };
   
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
         Intent intent = new Intent(activityContext, FtpConfigure.class);
         startActivity(intent);
      }
   };
   /**
    * A callback for when the user toggles the session monitor on or off
    */
   OnClickListener sessionMonitorCheckBoxListener = new OnClickListener()
   {
      public void onClick(View v)
      {
         // Trigger a UI update message to our Activity
         UiUpdater.updateClients();
         // updateUi();
      }
   };
   
   /**
    * A callback for when the user toggles the server log on or off
    */
   OnClickListener serverLogCheckBoxListener = new OnClickListener()
   {
      public void onClick(View v)
      {
         // Trigger a UI update message to our Activity
         UiUpdater.updateClients();
         // updateUi();
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
   
   /**
    * Get the settings from the FTPServerService if it's running, otherwise load
    * the settings directly from persistent storage.
    */
   SharedPreferences getSettings()
   {
      SharedPreferences settings = FTPServerService.getSettings();
      if (settings != null)
      {
         return settings;
      }
      else
      {
         return this.getPreferences(MODE_PRIVATE);
      }
   }
   
}
