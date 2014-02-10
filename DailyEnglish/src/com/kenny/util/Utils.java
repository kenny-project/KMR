package com.kenny.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.kenny.Application.KApp;
import com.kenny.activity.MainOld;
import com.kenny.activity.Setting;
import com.kenny.dailyenglish.R;
import com.kenny.receiver.AlarmReceiver;

public class Utils {

	public final static String RECOMMENDATION_VER_STR = "RECOMMENDATION_VER_STR";
//	private static Activity main;
	private Context context;

	public Utils() {

	}

//	public Utils(Activity main) {
////		this.main = main;
//	}

	// 判断sd卡是否可用
	public static boolean getSDCardStatus() {
		String status = Environment.getExternalStorageState();
		boolean result = false;
		if (Environment.MEDIA_MOUNTED.equals(status)) {
			result = true;
		}
		return result;
	}

	public static boolean isNetConnect(Context context) {
		try {
			ConnectivityManager connManager = (ConnectivityManager) (context)
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = connManager.getActiveNetworkInfo();
			/******* EMULATOR HACK - false condition needs to be removed *****/
			if ((netInfo == null || netInfo.isConnected() == false)) {
				// SendMessage((context), "No Internet Connection");
			} else if (netInfo.getTypeName().toUpperCase().equals("MOBILE")
					&& netInfo.getExtraInfo() != null
					&& netInfo.getExtraInfo().equals("cmwap")) {
				
				Toast.makeText(context,context.getString(R.string.msg_Network_not_found), Toast.LENGTH_SHORT).show();
				
				return false;
			} else {

				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Toast.makeText(context,context.getString(R.string.msg_Network_not_found), Toast.LENGTH_SHORT).show();
		return false;
	}

	// 判断是否有可用的网络连接
	public static boolean isNetConnectNoMsg(Context context) {
		try {
			ConnectivityManager connManager = (ConnectivityManager) (context)
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = connManager.getActiveNetworkInfo();
			/******* EMULATOR HACK - false condition needs to be removed *****/
			if ((netInfo == null || netInfo.isConnected() == false)) {
				// SendMessage((context), "No Internet Connection");
			} else if (netInfo.getTypeName().toUpperCase().equals("MOBILE")
					&& netInfo.getExtraInfo() != null
					&& netInfo.getExtraInfo().equals("cmwap")) {
				return false;
			} else {

				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean isConnect2g(Context context){
		// 连接的是否是2G网络
		boolean result = false;
		try {
			ConnectivityManager connManager = (ConnectivityManager) (context)
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = connManager.getActiveNetworkInfo();
			int subType = netInfo.getSubtype();
			/******* EMULATOR HACK - false condition needs to be removed *****/
			if ((netInfo == null || netInfo.isConnected() == false)) {
				// SendMessage((context), "No Internet Connection");
				result = false;
			} else if (netInfo.getTypeName().toUpperCase().equals("MOBILE")) {
				// 连接是手机网络
				if (subType == TelephonyManager.NETWORK_TYPE_CDMA    //电信２g
						|| subType == TelephonyManager.NETWORK_TYPE_GPRS    //  联通2g
						|| subType == TelephonyManager.NETWORK_TYPE_EDGE){    // 移动2ｇ
					// 这是2g网络
					result = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 判断是否是wifi联接
	public static boolean isNetWifi(Context context) {
		try {
			ConnectivityManager connManager = (ConnectivityManager) (context)
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = connManager.getActiveNetworkInfo();
			/******* EMULATOR HACK - false condition needs to be removed *****/
			if ((netInfo == null || netInfo.isConnected() == false)) {
				// SendMessage((context), "No Internet Connection");
			} else if (netInfo.getTypeName().equals("WIFI")) {
				return true;
			} else {

				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	
	public static void init(Activity main) {
		KApp app = (KApp) main.getApplicationContext();
		app.config.readConfig(main);
		app.config.DEFAULT_TOP_LABEL = get(
				main.getApplicationContext(), "defaultTopLabel",
				Const.CONFIG_DEFAULT_TL_BASIC);
		File file = new File(Const.CATCH_DIRECTORY);
		if (!file.exists()) {
			file.mkdir();
		}

		file = new File(Const.DICT_DIRECTORY);
		if (!file.exists()) {
			file.mkdir();
		}
}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/* 去左空格 */

	public static String leftTrim(String str) {
		if (str == null || str.equals("")) {
			return str;
		} else {
			return str.replaceAll("^[ ]+", "");
		}
	}

	// 去空格
	public static String trim(String str) {
		if (str == null || str.equals("")) {
			return str;
		}
		return str.trim();
	}

	// uid的格式：渠道号-机型-平台版本号-IMEI
	public static String generateClientUID(String date, Context main) {
		TelephonyManager telephonyManager = (TelephonyManager) main
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		// 渠道号
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(getChannelNum(main));
		sbuf.append("-");
		// 机型
		StringBuffer idBuf = new StringBuffer();
		idBuf.append(android.os.Build.MODEL);
		idBuf.append(".");
		idBuf.append(android.os.Build.BRAND);
		idBuf.append(".");
		idBuf.append(android.os.Build.DEVICE);
		idBuf.append(".");
		idBuf.append(android.os.Build.PRODUCT);

		String id = idBuf.toString();
		id = id.replaceAll("-", "_");
		id = id.replaceAll(" ", "");
		if (idBuf.length() > 20) {
			if (id.length() > 20){
				id = id.substring(0, 20);
			}
		}
		sbuf.append(id);
		sbuf.append("-");
		sbuf.append(Const.SOFTWARE_VERSION_ID);
		sbuf.append("-");
		if (imei != null && imei.length() > 0) {
			try {
				imei = MD5Calculator.calculateMD5(imei.getBytes("ASCII"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			int length = imei.length();
			if (length > 16) {
				imei = imei.substring((length - 16) / 2, length
						- ((length - 16) / 2 - (length - 16) % 2));
			}
			sbuf.append("IMEI" + imei);
		} else {
			imei = "" + System.currentTimeMillis();
			try {
				imei = MD5Calculator.calculateMD5(imei.getBytes("ASCII"));
			} catch (Exception e) {
			}
			int length = imei.length();
			if (length > 16) {
				try {
					imei = imei.substring((length - 16) / 2,
							(length - length) / 2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			sbuf.append("IMEI" + imei);
		}
		return sbuf.toString();
	}

	public int versionCode = -1;
	public int currentVersionCode = -1;
	public int showAd = -1;
	public int showBaidu = -1;
	public String updateNote = "";
	public String packageUrl = "";

	public class ParserADModel extends DefaultHandler {
		String value = "";
		String localName = "";
		boolean isCheck = false;
		Context main;

		public ParserADModel(boolean isCheck, Context main) {
			this.isCheck = isCheck;
			this.main = main;
			Utils.this.context =  main;
		}

		@Override
		public void startDocument() throws SAXException {
			// TODO Auto-generated method stub
			super.startDocument();
			updateNote = "";
		}

		@Override
		public void endDocument() throws SAXException {
			// TODO Auto-generated method stub
			super.endDocument();
			getCurrentVersion(main);
			Message msg = handler.obtainMessage(Const.MSG_ON_SHOW_AND_HIDE_AD);
			msg.obj = isCheck;
			handler.sendMessage(msg);
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			// TODO Auto-generated method stub
			super.startElement(uri, localName, qName, attributes);
			this.localName = localName;
			if ("updateNote".equals(localName)) {
				updateNote = "";
			}

		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			// TODO Auto-generated method stub
			super.endElement(uri, localName, qName);
			if ("versionCode".equals(localName)) {
				// 版本号
				versionCode = Integer.valueOf(value);
			} else if ("showAd".equals(localName)) {
				// 是否显示广告
				showAd = Integer.valueOf(value);
			} else if ("showBaidu".equals(localName)) {
				// 是否显示百度搜索
				showBaidu = Integer.valueOf(value);
			} else if ("updateNote".equals(localName)) {
				// 更新日志
				updateNote = updateNote + value;
			} else if ("packageUrl".equals(localName)) {
				packageUrl = value;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			// TODO Auto-generated method stub
			super.characters(ch, start, length);
			if ("updateNote".equals(localName)) {
				value += new String(ch, start, length);
			} else {
				value = new String(ch, start, length);
			}
		}

		public void parseRssByUrl(String urlString)
				throws ParserConfigurationException, SAXException,
				MalformedURLException, IOException {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(10000);
			InputStreamReader fsr = new InputStreamReader(
					connection.getInputStream(), "utf-8");
			InputSource is = new InputSource(fsr);
			parser.parse(is, this);
			factory = null;
			parser = null;
		}
	}

	public void getCurrentVersion(Context main) {
		try {
			PackageInfo info = main.getPackageManager().getPackageInfo(
					main.getPackageName(), 0);
			this.currentVersionCode = info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void requestLoadingAD(Activity main) {
		String signURL = "http://dict-mobile.iciba.com/new/index.php?mod=load";
		JSONObject json = new JSONObject();
		int screenHeight = main.getWindowManager().getDefaultDisplay()
				.getHeight();
		int screenWidth = main.getWindowManager().getDefaultDisplay()
				.getWidth();
		int versionid = Integer.valueOf(Utils.get(main,
				"loadingAdVersionid", "0"));
		String key = "1000000";
		String secret = "k3det036g3rs5fgfsw300wr3ddt3e2n";
		try {
			json.put("key", key);
			json.put("versionid", versionid); // 代表版本号
			json.put("type", "1"); // 1代表android
			json.put("app", "1"); // 1代表金山词霸
			json.put("height", screenHeight);
			json.put("width", screenWidth);
			json.put(
					"auth",
					MD5Calculator.calculateMD5(key + versionid + "1" + "1"
							+ secret)); // md5格式 key + versionid + type + app +
										// secret
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpURLConnection conn;
		StringBuffer sbResult = new StringBuffer();
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		try {
			String param = json.toString();
			URL urlU = new URL(signURL);
			conn = (HttpURLConnection) urlU.openConnection();
			// 这个要打开
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 处理POST的数据
			conn.setRequestMethod("POST");
			conn.getOutputStream().write(param.getBytes());
			conn.getOutputStream().flush();
			conn.getOutputStream().close();
			InputStream inputStream = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					inputStream, "utf-8"));
			String data = "";
			if (sbResult.toString().equalsIgnoreCase("")) {
				while ((data = br.readLine()) != null) {
					sbResult.append(data);
				}
			}
			inputStream.close();
			conn.disconnect();
			JSONObject jsonResult = new JSONObject(sbResult.toString());
			String flag = jsonResult.getString("stat");
			if ("1".equals(flag)) {
				// 有更新，需要更新
				String imageUrl = jsonResult.getString("url");
				downLoadPic(imageUrl); // 下载图片
				Utils.save(main, "loadingAdStarttime",
						jsonResult.getString("starttime")); // 保存ad开始时间
				Utils.save(main, "loadingAdEndtime",
						jsonResult.getString("endtime")); // 保存ad结束时间
				Utils.save(main, "loadingAdVersionid",
						jsonResult.getString("versionid")); // 保存ad versionid
				Utils.save(main, "loadingAdFileName",
						imageUrl.substring(imageUrl.lastIndexOf("/") + 1));
			} else {
				// 无更新，暂不处理
			}

			Log.e("chenjg", sbResult.toString() + "  !!versionId is "
					+ versionid);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void downLoadPic(String imageUrl) {
		String path = Const.LOADING_AD_IMAGE_DIRECTORY;
		String szFileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
		File file;
		path += szFileName;
		file = new File(path);
		if (file.exists()) {
			return;
		}
		try {
			InputStream imageStream = new URL(imageUrl).openStream();
			Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
			WriteImageFile(imageUrl, bitmap);
			bitmap.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 初始化运行程序所需要的文件
	public boolean WriteImageFile(String url, Bitmap bitmap) {
		if (!T.checkSDCard())
			return false;
		String path = Const.LOADING_AD_IMAGE_DIRECTORY;
		String szFileName = url.substring(url.lastIndexOf("/") + 1);
		File file;
		try {
			new File(path).mkdirs();
			path += szFileName;
			file = new File(path);
			file.createNewFile();
			// Finally compress the bitmap, saving to the file previously
			// created
			bitmap.compress(CompressFormat.PNG, 100, new FileOutputStream(file));
			// copyFile(is,path);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.v("chenjg", "url is " + url);
			file = new File(path);
			file.delete();
			return false;
		}
	}



	public void showUpdateDialog(final Activity main) {
		if (isShowUpdateDialogTime(main) == false) {
			// 判断是否在上次弹出的24小时之内
			return;
		}
		if (isShowUpdateDialog(main, versionCode) == false) {
			return;
		}
		setShowUpdataDialogTime(main);
		String message = "";
		message = "R.string.have_new_version" + updateNote;
		final Dialog exitDialog = new Dialog(main);
		exitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		exitDialog.setContentView(R.layout.update_dialog);
		exitDialog.setCancelable(false);
		TextView tvText = (TextView) exitDialog.findViewById(R.id.title);
		tvText.setText(message);
		Button exitOk = (Button) exitDialog.findViewById(R.id.exit_yes);
		exitOk.setText("R.string.download");
		exitOk.setFocusable(false);
		final CheckBox ckNextShow = (CheckBox) exitDialog
				.findViewById(R.id.next_show);
		ckNextShow.setVisibility(View.VISIBLE);
		exitOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// if ()
				setShowUpdataDialog(main, !ckNextShow.isChecked(), versionCode);
				downloadTheFile(packageUrl, main);
				exitDialog.dismiss();
			}
		});
		Button exitNo = (Button) exitDialog.findViewById(R.id.exit_no);
		exitNo.setText(R.string.cancel);
		exitNo.setFocusable(false);
		exitNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setShowUpdataDialog(main, !ckNextShow.isChecked(), versionCode);
				exitDialog.dismiss();
			}
		});
		exitDialog.show();
	}

	boolean isDownloading = false;

	private void downloadTheFile(final String strPath, final Context main) {
		if (isDownloading == true) {
			return;
		}
		notification = new Notification(R.drawable.icon, "金山词霸正在下载",
				System.currentTimeMillis());

		notification.contentView = new RemoteViews(main
				.getPackageName(), R.layout.notification_progress);
		notification.contentView.setProgressBar(R.id.progressBar1, 100, 0,
				false);
		notification.contentView.setTextViewText(R.id.textView1, "进度"
				+ _progress + "%");
		notification.contentView.setTextViewText(R.id.np_app_name,
				main.getString(R.string.app_name));

		Intent i = new Intent(main, Setting.class);
		notification.contentIntent = PendingIntent.getActivity(
				main, 0, i, 0);

		// notification.contentIntent = PendingIntent.getActivity(this, 0,new
		// Intent(this, PowerWordCore.class), 1);

		manager = (NotificationManager) main
				.getSystemService(Context.NOTIFICATION_SERVICE);
		try {
			if (!Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				// sd卡不存在
				Toast.makeText(main,
						"R.string.no_sdcard_no_download", 1000).show();
				return;
			}
			Toast.makeText(main,
					"R.string.update_downloading_back", 2000).show();
			isDownloading = true;
			String str = "R.string.update_downloading";
			// showNotification(str, main);
			Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						doDownloadTheFile(strPath, main);
					} catch (Exception e) {
						Message msg = handler
								.obtainMessage(Const.MSG_ON_UPDATE_FAILURE);
						handler.sendMessage(msg);
					}
				}
			};
			new Thread(r).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showNotification(String msg, MainOld main) {
		// look up the notification manager service
		manager = (NotificationManager) main
				.getSystemService(Context.NOTIFICATION_SERVICE);

		CharSequence from = "金山词霸";
		CharSequence message = msg;
		PendingIntent contentIntent = PendingIntent.getActivity(main, 0,
				new Intent(main, MainOld.class), 0);

		String tickerText = msg;

		Notification notif = new Notification(R.drawable.notice_icon,
				tickerText, System.currentTimeMillis());
		// if (isSucess == true && file != null){
		// Intent intent = new Intent();
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// intent.setAction(android.content.Intent.ACTION_VIEW);
		// String type = getMIMEType(file);
		// intent.setDataAndType(Uri.fromFile(file), type);
		// PendingIntent pIntent =
		// PendingIntent.getActivity(main.getBaseContext(), 999, intent, 0);
		// notif.contentIntent = pIntent;
		// }
		notif.setLatestEventInfo(main, from, message, contentIntent);
		manager.notify(0, notif);
	}

	NotificationManager manager;
	Notification notification;

	private void doDownloadTheFile(String strPath, Context main)
			throws Exception {
		String fileEx = packageUrl.substring(packageUrl.lastIndexOf(".") + 1,
				packageUrl.length()).toLowerCase();
		String fileNa = packageUrl.substring(packageUrl.lastIndexOf("/") + 1,
				packageUrl.lastIndexOf("."));
		URL myURL = new URL(strPath);
		URLConnection conn = myURL.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		int fileSize = conn.getContentLength();
		if (is == null) {
			throw new RuntimeException("stream is null");
		}
		String dir = Environment.getExternalStorageDirectory() + "/kingsoft";
		File f = new File(dir);
		if (!f.exists()) {
			f.mkdir();
		}
		final File f1 = new File(dir + "/" + fileNa + "." + fileEx);
		if (f1.exists()) {
			f1.delete();
		}

		File myTempFile = new File(dir + "/" + fileNa + "." + fileEx);
		FileOutputStream fos = new FileOutputStream(myTempFile);
		float downloadSize = 0;
		float progress = 0;
		byte buf[] = new byte[8000];
		do {
			int numread = is.read(buf);
			if (numread <= 0) {
				break;
			}
			fos.write(buf, 0, numread);
			downloadSize += numread;
			progress = downloadSize / fileSize * 100;
			Message msg = handler.obtainMessage();
			msg.arg1 = (int) progress;
			msg.sendToTarget();
		} while (true);
		String str = "R.string.update_finish";
		// showNotification(str, main);
		isDownloading = false;
		openFile(myTempFile, main);
		try {
			is.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void showNotificationProgress(String msg, int _progress, MainOld main) {

		manager.notify(1, notification);
	}

	int _progress = 0;
	long showNotifyTime = 0;

	public Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
//			msg.
			switch (msg.what) {
			case Const.MSG_ON_SHOW_AND_HIDE_AD:
//				isShowBaiduAndAd(Boolean.valueOf(msg.obj + ""), Utils.this.context);
				break;
			case Const.MSG_ON_UPDATE_FAILURE:
				Toast.makeText(context, "R.string.update_net_failure",
						Toast.LENGTH_LONG).show();
				break;
			case Const.MSG_ON_UPDATE_SHOW_APK_EXIST:
				String message = "";
				message = "R.string.install_package_exist";
				final Dialog exitDialog2 = new Dialog(context);
				exitDialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
				exitDialog2.setContentView(R.layout.exit_dialog);
				TextView tvText2 = (TextView) exitDialog2
						.findViewById(R.id.title);
				tvText2.setText(message);
				Button exitOk2 = (Button) exitDialog2
						.findViewById(R.id.exit_yes);
				exitOk2.setText("R.string.install");
				exitOk2.setFocusable(false);
				final File f1 = (File) msg.obj;
				exitOk2.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						openFile(f1, context);
						exitDialog2.dismiss();
					}
				});
				Button exitNo = (Button) exitDialog2.findViewById(R.id.exit_no);
				exitNo.setText(R.string.cancel);
				exitNo.setFocusable(false);
				exitNo.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						exitDialog2.dismiss();
					}
				});
				exitDialog2.show();
				break;
			case Const.MSG_ON_UPDATE_FAILURE_PROGRESS:
				boolean isCheck = Boolean.valueOf(msg.obj.toString());
				if (isCheck) {
					Toast.makeText(context, "R.string.net_connect_failure", 2000)
							.show();
				}
				break;
			default:
				if (System.currentTimeMillis() - showNotifyTime > 2000) {
					showNotifyTime = System.currentTimeMillis();
					notification.contentView.setProgressBar(R.id.progressBar1,
							100, msg.arg1, false);
					notification.contentView.setTextViewText(R.id.textView1,
							"进度" + msg.arg1 + "%");
					notification.contentView.setTextViewText(R.id.np_app_name,
							"金山词霸");
					manager.notify(1, notification);
				}
				if (msg.arg1 >= 100) {
					_progress = 0;
					manager.cancel(1);
					// Toast.makeText(main.this, "下载完毕", 1000).show();
				}
				break;
			}
		}
	};

	File file = null;

	public void openFile(File f, Context main) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = getMIMEType(f);
		intent.setDataAndType(Uri.fromFile(f), type);
		file = f;
		main.startActivity(intent);
	}

	private String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else if (end.equals("apk")) {
			type = "application/vnd.android.package-archive";
		} else {
			type = "*";
		}
		if (end.equals("apk")) {
		} else {
			type += "/*";
		}
		return type;
	}

	public static boolean isFirstUpdate(Context ctx, boolean isUpdate) {
		String is_first_update = "firstUpdate";
		SharedPreferences pref = ctx.getSharedPreferences("first.lag",
				Context.MODE_PRIVATE);
		boolean isFirstTime = pref.getBoolean(is_first_update, true);
		// String mPassword = pref.getString("password", "");
		// boolean isFirstTime = !taggingFile.exists();

		if (isFirstTime && isUpdate) {
			Editor ed = pref.edit();
			ed.putBoolean(is_first_update, false);
			ed.commit();
		}

		return isFirstTime;
	}

	public static boolean isShowUpdateDialog(Context ctx, int versionCode) {
		String show_update = "showUpdate";
		String version_code = "version_code";
		SharedPreferences pref = ctx.getSharedPreferences("first.lag",
				Context.MODE_PRIVATE);
		boolean isFirstTime = pref.getBoolean(show_update, true);
		int currentVersionCode = pref.getInt(version_code, 0);
		if (versionCode > currentVersionCode) {
			// 如果最新版本比记录版本早，不管怎么样，都提示
			return true;
		}

		return isFirstTime;
	}

	public static void setShowUpdataDialog(Context ctx, boolean isShow,
			int versionCode) {
		String show_update = "showUpdate";
		String version_code = "version_code";
		SharedPreferences pref = ctx.getSharedPreferences("first.lag",
				Context.MODE_PRIVATE);
		Editor ed = pref.edit();
		ed.putBoolean(show_update, isShow);
		ed.putInt(version_code, versionCode);
		ed.commit();
	}

	public static void setShowUpdataDialogTime(Context ctx) {
		String show_update_time = "showUpdateTime";
		SharedPreferences pref = ctx.getSharedPreferences("first.lag",
				Context.MODE_PRIVATE);
		Editor ed = pref.edit();
		ed.putLong(show_update_time, new Date().getTime());
		ed.commit();
	}

	public static boolean isShowUpdateDialogTime(Context ctx) {
		String show_update_time = "showUpdateTime";
		SharedPreferences pref = ctx.getSharedPreferences("first.lag",
				Context.MODE_PRIVATE);
		Long isFirstTime = pref.getLong(show_update_time, 0);
		if (Math.abs(new Date().getTime() - isFirstTime) > 24 * 60 * 60 * 1000) {
			return true;
		}

		return false;
	}

	/**
	 * 获取SDCARD剩余存储空间
	 * 
	 * @return
	 */
	public static long getAvailableExternalMemorySize() {
		if (externalMemoryAvailable()) {
			Long lFreeSpace = 0l;
			File file = Environment.getExternalStorageDirectory();

			if (android.os.Build.VERSION.SDK_INT >= 9) {
				lFreeSpace = file.getFreeSpace();
			} else {
				StatFs fs = new StatFs(file.getPath());
				Long lBlockSize = (long) fs.getBlockSize();
				lFreeSpace = (long) (lBlockSize * fs.getAvailableBlocks());
			}
			return lFreeSpace;
		} else {
			return -1;
		}
	}

	/**
	 * SDCARD是否存
	 */
	public static boolean externalMemoryAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	public static String getChannelNum(Context main) {
		ApplicationInfo appInfo = null;
		try {
			appInfo = main.getPackageManager().getApplicationInfo(
					main.getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (appInfo == null) {
			return "";
		}
		String msg = appInfo.metaData.getString("UMENG_CHANNEL");
		String msgs[] = msg.split("_");
		return msgs[msgs.length - 1];
	}
	
	public static String getChannelNumAll(Context main) {
		ApplicationInfo appInfo = null;
		try {
			appInfo = main.getPackageManager().getApplicationInfo(
					main.getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (appInfo == null) {
			return "";
		}
		String msg = appInfo.metaData.getString("UMENG_CHANNEL");
		return msg;
	}

	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(100);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}


	private String getNetXml(String url) {
		Log.e("chenjg", "url is " + url);
		StringBuffer sbResult = new StringBuffer();
		HttpURLConnection conn;
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		try {
			URL urlU = new URL(url);
			conn = (HttpURLConnection) urlU.openConnection();
			// 这个要打开
			conn.setDoOutput(false);
			conn.setDoInput(true);
			// 处理POST的数据
			conn.setRequestMethod("GET");
			InputStream inputStream = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					inputStream, "utf-8"));
			String data = "";
			if (sbResult.toString().equalsIgnoreCase("")) {
				while ((data = br.readLine()) != null) {
					sbResult.append(data);
				}
			}
			inputStream.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sbResult.toString();
	}


	/**
	 * 向SharedPreferences中存入String
	 * 
	 * */
	public static void save(Context context, String columnName,
			String value) {
		try {
			Editor passfileEditor = context
					.getSharedPreferences("powerword", 0).edit();
			passfileEditor.putString(columnName, value);
			
			passfileEditor.commit(); // 提交数据
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 向SharedPreferences中存入String
	 * 
	 * */
	public static void save(Context context, String columnName,
			int value) {
		try {
			Editor passfileEditor = context
					.getSharedPreferences("powerword", 0).edit();
			passfileEditor.putInt(columnName, value);
			
			passfileEditor.commit(); // 提交数据
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 向SharedPreferences中存入String
	 * 
	 * */
	public static void save(Context context, String columnName,
			boolean value) {
		try {
			Editor passfileEditor = context
					.getSharedPreferences("powerword", 0).edit();
			passfileEditor.putBoolean(columnName, value);
			
			passfileEditor.commit(); // 提交数据
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 向SharedPreferences中存入String
	 * 
	 * */
	public static void removeString(Context context, String columnName) {
		try {
			Editor passfileEditor = context
					.getSharedPreferences("powerword", 0).edit();
			passfileEditor.remove(columnName);
			passfileEditor.commit(); // 提交数据
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从SharedPreferences中读取String
	 * 
	 * */
	public static String get(Context context, String columnName,
			String defValue) {
		try {
			SharedPreferences sharedPreferences = context.getSharedPreferences(
					"powerword", 0);
			String show = sharedPreferences.getString(columnName, defValue);
			return show;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	/**
	 * 从SharedPreferences中读取String
	 * 
	 * */
	public static int get(Context context, String columnName,
			int defValue) {
		try {
			SharedPreferences sharedPreferences = context.getSharedPreferences(
					"powerword", 0);
			int show = sharedPreferences.getInt(columnName, defValue);
			return show;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defValue;
	}
	/**
	 * 从SharedPreferences中读取String
	 * 
	 * */
	public static boolean get(Context context, String columnName,
			boolean defValue) {
		try {
			SharedPreferences sharedPreferences = context.getSharedPreferences(
					"powerword", 0);
			boolean show = sharedPreferences.getBoolean(columnName, defValue);
			return show;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return defValue;
	}
	public void writeStringFile(String fileName, String content) {
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
		try {
			OutputStreamWriter osw = new OutputStreamWriter(
					new FileOutputStream(file), "UTF-8");
			osw.write(content);
			osw.flush();
			osw.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	

	private Bitmap scaleBitmp(Bitmap bitmap, float fx, float fy) {
		Matrix matrix = new Matrix();
		matrix.postScale(fx, fy); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	

	public static void writelog(String msg, String filename) {
		try {
			File file = new File(Const.SDCard + File.separator + filename);
			if (!file.exists()) {
				file.createNewFile();
			}

			// FileOutputStream fos = new FileOutputStream(file);
			// fos.write(msg.getBytes());
			// fos.close();

			RandomAccessFile randomFile = new RandomAccessFile(file, "rw");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 将写文件指针移到文件尾。
			randomFile.seek(fileLength);
			Calendar date = Calendar.getInstance();
			String strDate = DateFormat.format("yyyy-MM-dd hh:mm:ss", date)
					.toString();
			randomFile.writeBytes(strDate + " ");
			randomFile.writeBytes(msg + "\n");
			randomFile.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getUUID() {
		
		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
		return uuid;
	}
	public static String getUUID(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE); 
		final String tmDevice, tmSerial, tmPhone, androidId; 
		tmDevice = "" + tm.getDeviceId();
		tmSerial = "" + tm.getSimSerialNumber();  
		androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID); 
		UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());  
		String uniqueId = deviceUuid.toString(); 
		
		
//		String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");
		return uniqueId;
	}
	/**
	 * @author ZhouKang
	 * @param url
	 *            网络地址 如http://iciba.com/aa.jpg
	 * @param picPath
	 *            SD卡路径 如 /sdcadr/kvoa/
	 * @return isok 是否成功
	 */
	public static boolean saveNetFile2SDCard(String url, String picPath) {
		File destDir = new File(picPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		String temp[] = url.split("/");
		String picName = temp[temp.length - 1];
		return saveNetFile2SDCard(url, picPath, picName);
	}

	/**
	 * @author ZhouKang
	 * @param url
	 *            网络地址 如http://iciba.com/aa.jpg
	 * @param picPath
	 *            SD卡路径 如 /sdcadr/kvoa/
	 * @param rename
	 *            新文件的名字 如 xx.xx
	 * @return isok 是否成功
	 */
	public static boolean saveNetFile2SDCard(String url, String picPath,
			String rename) {

		File destDir = new File(picPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}

		boolean ok = false;
		InputStream is;
		try {
			String cnname = rename;
			// url = url.replaceAll(cnname, "");
			is = getUrlInputStream(url);
			saveStreamToFile(is, picPath + rename);
			ok = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ok;
	}

	/**
	 * @author ZhouKang
	 * @param url
	 *            网络地址 如http://iciba.com/aa.jpg
	 * @param picPath
	 *            SD卡路径 如 /sdcadr/kvoa/
	 * @return Bitmap 对象
	 */

	public static Bitmap getUrlimg(String url, String picPath) {
		String picName = getNetFileName(url);
		if (!saveNetFile2SDCard(url, picPath))
			return null;
		return BitmapFactory.decodeFile(picPath + picName);
	}

	public static Bitmap getUrlimg(String url, String filename, String picPath) {
		String picName = filename;
		if (!saveNetFile2SDCard(url, picPath, filename))
			return null;
		return BitmapFactory.decodeFile(picPath + picName);
	}

	/**
	 * 
	 * @param url
	 *            地址 如 http://iciba.com/aa.xx
	 * @return aa.xx
	 */
	public static String getNetFileName(String url) {
		String temp[] = url.split("/");
		String picName = temp[temp.length - 1];
		return picName;
	}

	public static InputStream getUrlInputStream(String url) throws IOException {
		URL imageUrl = null;
		imageUrl = new URL(url);

		HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		Log.e("chenjg", "voice url is " + url);
		return is;
	}

	public static boolean saveStreamToFile(InputStream in, String fileNamePath)
			throws IOException {
		boolean result = true;

		File f = null;
		try {
			f = new File(fileNamePath);
			if (f.exists()) {
				f.delete();
			} else {
				f.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(f);
			copyStream(in, fos);
			fos.close();
		} catch (Exception e) {
			if (f != null && f.exists()) {
				f.delete();
			}
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	public static void copyStream(InputStream in, OutputStream out)
			throws IOException {
		BufferedInputStream bin = new BufferedInputStream(in);
		BufferedOutputStream bout = new BufferedOutputStream(out);

		byte[] buffer = new byte[4096];

		while (true) {
			int doneLength = bin.read(buffer);
			if (doneLength == -1)
				break;
			bout.write(buffer, 0, doneLength);
		}
		bin.close();
		bout.flush();
		bout.close();
	}

	/**
	 * 获得 bitmap的缩略图
	 * 
	 * @param times
	 *            缩小倍数 1/tiames
	 * @return bitmap
	 * @author ZhouKang
	 * 
	 */
	// public static Bitmap getSmaleBitmap(Bitmap bit, int times) {
	// if (bit != null) {
	// bit = ThumbnailUtils.extractThumbnail(bit, bit.getWidth() / times,
	// bit.getHeight() / times);
	// }
	// return bit;
	// }
	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}

		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	public static int sp2px(Context context, float spValue) {
		float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}
	
	public static boolean speakWordForCache(int type, String word){
		boolean result = false;
		final String filename = MD5Calculator.calculateMD5(word + type) + ".p";
		final String path = Const.VOICE_DIRECTORY + filename;
		File music = new File(path);
		// 有缓存
		if (music.exists()) {
			try {
				MediaPlayer mediaPlayer = new MediaPlayer();
				mediaPlayer.reset();
				mediaPlayer.setDataSource(path);
				mediaPlayer.prepare();
				mediaPlayer.start();
				result = true;
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}


	// 获取所有发音
	public static void speakWord(String url, final Handler hanlder,
			final Context context) {
		// + type 区分 us en 的缓存
		final String filename = MD5Calculator.calculateMD5(url) + ".p";
		final String path = Const.VOICE_DIRECTORY + filename;
		File music = new File(path);
		// 有缓存
		if (music.exists()) {
			try {
				MediaPlayer mediaPlayer = new MediaPlayer();
				mediaPlayer.reset();
				mediaPlayer.setDataSource(path);
				mediaPlayer.prepare();
				mediaPlayer.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		// 没有缓存
		if (Utils.isNetConnectNoMsg(context) == true) {
			// 网络可用
			Toast.makeText(context, R.string.voice_net_connection,
					Toast.LENGTH_SHORT).show();
		} else {
			// 网络不可用
			Toast.makeText(context, R.string.voice_net_unconnection,
					Toast.LENGTH_SHORT).show();
			return;
		}

		final StringBuffer sbuf = new StringBuffer();
		sbuf.append(url);
		new Thread() {
			@Override
			public void run() {
				boolean isok = false;
				isok = Utils.saveNetFile2SDCard(sbuf.toString(),
						Const.VOICE_DIRECTORY, filename);
				if (isok == true) {
					hanlder.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							try {
								MediaPlayer mediaPlayer = new MediaPlayer();
								mediaPlayer.reset();
								mediaPlayer.setDataSource(path);
								mediaPlayer.prepare();
								mediaPlayer.start();
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				} else {
					hanlder.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(context, "获取发音失败",
									Toast.LENGTH_SHORT).show();
						}
					});
				}
			};
		}.start();

	}
	
	// 获取所有发音
		public static void speakWord(String url, final Handler hanlder,
				final Context context, final MediaPlayer mediaPlayer) {
			// + type 区分 us en 的缓存
			Log.e("chenjg", "voice url is " + url);
			final String filename = MD5Calculator.calculateMD5(url) + ".p";
			final String path = Const.VOICE_DIRECTORY + filename;
			File music = new File(path);
			// 有缓存
			if (music.exists()) {
				try {
					mediaPlayer.reset();
					mediaPlayer.setDataSource(path);
					mediaPlayer.prepare();
					mediaPlayer.start();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(context, "获取发音失败",
							Toast.LENGTH_SHORT).show();
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(context, "获取发音失败",
							Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(context, "获取发音失败",
							Toast.LENGTH_SHORT).show();
				}
				return;
			}
			// 没有缓存
			if (Utils.isNetConnectNoMsg(context) == true) {
				// 网络可用
				Toast.makeText(context, R.string.voice_net_connection,
						Toast.LENGTH_SHORT).show();
			} else {
				// 网络不可用
				Toast.makeText(context, R.string.voice_net_unconnection,
						Toast.LENGTH_SHORT).show();
				return;
			}

			final StringBuffer sbuf = new StringBuffer();
			sbuf.append(url);
			new Thread() {
				@Override
				public void run() {
					boolean isok = false;
					final Long startTime = System.currentTimeMillis();
					isok = Utils.saveNetFile2SDCard(sbuf.toString(),
							Const.VOICE_DIRECTORY, filename);
					if (isok == true) {
						hanlder.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									mediaPlayer.reset();
									mediaPlayer.setDataSource(path);
									mediaPlayer.prepare();
									mediaPlayer.start();
//									new AlertDialog.Builder(context).setMessage("TTS总请求时间为" + (System.currentTimeMillis() - startTime)).show();
//									Toast.makeText(context, "TTS总请求时间为" + (System.currentTimeMillis() - startTime), Toast.LENGTH_LONG).show();
								} catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									hanlder.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											Toast.makeText(context, "获取发音失败",
													Toast.LENGTH_SHORT).show();
										}
									});
								} catch (IllegalStateException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									hanlder.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											Toast.makeText(context, "获取发音失败",
													Toast.LENGTH_SHORT).show();
										}
									});
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									hanlder.post(new Runnable() {

										@Override
										public void run() {
											// TODO Auto-generated method stub
											Toast.makeText(context, "获取发音失败",
													Toast.LENGTH_SHORT).show();
										}
									});
								}
							}
						});
					} else {
						hanlder.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(context, "获取发音失败",
										Toast.LENGTH_SHORT).show();
							}
						});
					}
				};
			}.start();

		}
		public static void CleanAlerm(Context context)
		{
			Intent intent = new Intent(context, AlarmReceiver.class);
			PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent,
					0);
			AlarmManager am = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			am.cancel(pIntent);
		}
		public static void setAlerm(Context context, int hourOfDay, int minute) {
			Log.v("wmh", "hour of day is " + hourOfDay + "  minute is " + minute);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			Intent intent = new Intent(context, AlarmReceiver.class);
			PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent,
					0);
			AlarmManager am = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);

			am.cancel(pIntent);

			if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
				calendar.add(Calendar.DAY_OF_YEAR, 1);
			}

			if (hourOfDay >= 0 && minute >= 0) {
				// 设置闹钟，闹钟开
				am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
				am.setRepeating(AlarmManager.RTC_WAKEUP,
						calendar.getTimeInMillis(), (24 * 60 * 60 * 1000), pIntent);
			} else {
				// 设置闹钟，闹钟关
			}
		}
}
