package com.kenny.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.zip.GZIPInputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore.Images.Media;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.view.WindowManager;
import android.widget.Toast;

import com.kenny.dailyenglish.R;
import com.kenny.file.SaveData;

public class KCommand {
	private static String errorMsg = "";
	private static WakeLock mWakeLock = null;// 电源管理
	public static final boolean bDefToolsVisible = true;// ranking
	// 默认是否显示工具条true:显示
	public static String AppPackageName = null;
	private static boolean bToolsVisible = false;// HomeTools工具栏

	public static Hashtable<String, Integer> facecacheid = new Hashtable<String, Integer>();

	public static String getReaderFilter(Context context) {
		if (AppPackageName == null) {
			AppPackageName = context.getPackageName();
		}
		return AppPackageName + ".web";
	}

	//
	// public static String getNetFilter(Context context)
	// {
	// if (AppPackageName == null)
	// {
	// AppPackageName = context.getPackageName();
	// }
	// return AppPackageName + ".net";
	// }

	// 获取IMEI号
	public static String GetIMEI(Context context) {
		try {
			String IMEI = SaveData.readPreferencesString(context, "IMEI");
			if (IMEI.equals("")) {
				TelephonyManager tm = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				IMEI = tm.getDeviceId();
				SaveData.writePreferencesString(context, "IMEI", IMEI);
			}
			return IMEI;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0000000000";
	}

	/**
	 * 获得当前系统版 本号
	 * 
	 * @param context
	 * @return
	 */
	public static int GetVersionCode(Context context) {
		int versionName = 0;
		try {
			versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
			return versionName;
		} catch (NameNotFoundException e) {
			return versionName;
		}
	}

	/**
	 * 获得当前系统版 本号
	 * 
	 * @param context
	 * @return
	 */
	public static String GetVersionName(Context context) {
		String versionName = "";
		try {
			versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			return versionName;
		}
	}

	public static void FullScreen(Activity context) {
		/** 全屏设置，隐藏窗口所有装饰 */
		context.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 * @throws IOException
	 */
	public static void copyFile(InputStream inStream, String newPath)
			throws IOException {
		int bytesum = 0;
		int byteread = 0;
		FileOutputStream fs = new FileOutputStream(newPath);
		byte[] buffer = new byte[1444];
		while ((byteread = inStream.read(buffer)) != -1) {
			bytesum += byteread; // 字节数 文件大小
			System.out.println(bytesum);
			fs.write(buffer, 0, byteread);
		}
		inStream.close();
	}

	// 初始化运行程序所需要的文件
	private static boolean ResourceRAWFile(Context context, String szFileName,
			int ResourceID) {
		String path = "/data/data/" + context.getPackageName() + "/files/";
		// + szFileName;
		File fileName;
		try {
			new File(path).mkdirs();
			path += szFileName;
			fileName = new File(path);
			if (fileName.exists()) {
				fileName.delete();
			}
			fileName.createNewFile();
			InputStream inputStream = context.getResources().openRawResource(
					ResourceID);
			copyFile(inputStream, path);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fileName = new File(path);
			fileName.delete();
			return false;
		}
	}

	public static String GetLastError() {
		return errorMsg;
	}

	// public static void NetAddFavoriten(Context context, int id, String key)//
	// 网络收藏夹命令
	// {
	// int kcid = SaveData.readPreferencesInt(context, "KCID");
	// if (kcid > 0)
	// {
	// String url = SaveData.readPreferencesString(context, "webside");
	// url += "FavolitenSync.aspx";
	// url += "?key=" + key + "&KCID=" + kcid + "&value=" + id;
	// AddFavolitenParser parser = new AddFavolitenParser();
	// parser.parseRssByUrl(context, url);
	// }
	// }
	
	
	// 发送给好友信息
	public static void SendShare(Context context, String Title, String Body,
			String imagePath1) {
		Intent it = new Intent(Intent.ACTION_SEND);
		String[] tos = { "" };
		if (imagePath1 != null) 
		{
			String url;
			try {
				File imageFile = new File(imagePath1);
				if (imageFile.exists()&&imageFile.length()>0) 
				{
//					url = Media.insertImage(context.getContentResolver(),
//							imageFile.getAbsolutePath(), imageFile.getName(),
//							imageFile.getName());
					Uri uri = Uri.fromFile(imageFile);
//					Uri uri = Uri.parse(url);// + imagePath1);
					it.putExtra(Intent.EXTRA_STREAM, uri);
				}
				else
				{
					imagePath1=null;	
				}
			}
			catch (Exception e) 
			{
				e.printStackTrace();
				imagePath1=null;
			}
		}
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		it.putExtra(Intent.EXTRA_EMAIL, tos);
		//it.putExtra(Intent.EXTRA_SUBJECT, "");
		it.putExtra(Intent.EXTRA_TITLE, "" + Title);
		it.putExtra(Intent.EXTRA_TEXT, "" + Body);
		if(imagePath1==null)
		{
		 it.setType("text/plain");
		}
		else
		{
			it.setType("image/*");
		}
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(it);
		return;
	}

	/**
	 * 开启电源长亮
	 * 
	 * @param context
	 */
	public static void PowerStart(Context context) {
		if (mWakeLock == null) {
			// ----------电源管理-------------------------------
			PowerManager pm = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
					"KREAD");
			mWakeLock.acquire();
			// --------------电源管理 end----------------------
		}
	}

	/**
	 * 关闭电源长亮,使之恢复正常状态
	 * 
	 * @param context
	 */
	public static void PowerClose() {
		mWakeLock.release();
		mWakeLock = null;
	}

	public static void KWriteString(Context context, InputStream inStream,
			String filename) throws IOException {
		int bytesum = 0;
		int byteread = 0;
		FileOutputStream fs = context.openFileOutput(filename,
				Context.MODE_WORLD_WRITEABLE);// MODE_WORLD_WRITEABLE
		byte[] buffer = new byte[1444];
		while ((byteread = inStream.read(buffer)) != -1) {
			bytesum += byteread; // 字节数 文件大小
			System.out.println(bytesum);
			fs.write(buffer, 0, byteread);
		}
		fs.flush();
		fs.close();
		inStream.close();
	}

	public static String GZipStreamToString(InputStream in, int requesttype)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		in = new GZIPInputStream(in);
		copyStream(in, baos, requesttype);
		return baos.toString("UTF-8");
	}

	public static String GZipStreamToString(String str, int requesttype)
			throws IOException {
		ByteArrayInputStream bin = new ByteArrayInputStream(str.getBytes());
		return GZipStreamToString(bin, requesttype);
	}

	public static String GZipStreamToString(byte[] data, int requesttype)
			throws IOException {
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		return GZipStreamToString(bin, requesttype);
	}

	private static void copyStream(InputStream in, OutputStream out,
			int requesttype) throws IOException {
		BufferedInputStream bin = new BufferedInputStream(in);
		BufferedOutputStream bout = new BufferedOutputStream(out);
		byte[] buffer = new byte[2048];
		int doneLength = -1;
		while (true) {
			doneLength = bin.read(buffer);
			if (doneLength == -1)
				break;
			bout.write(buffer, 0, doneLength);
		}
		bout.flush();
	}

	// // 发送广播信息
	// public static void sendMsg(Context context, int msg, int arg1, int arg2)
	// {
	// // 指定广播目标的 action （注：指定了此 action 的 receiver 会接收此广播）
	// Intent intent = new Intent(KCommand.getNetFilter(context));
	// // 需要传递的参数
	// intent.putExtra("msg", msg);
	// intent.putExtra("arg1", arg1);
	// intent.putExtra("arg2", arg2);
	// context.sendBroadcast(intent); // 发送广播
	// }

	public static String NetConnect(String urlString) throws IOException {
		URL url = new URL(urlString);
		String htmlcontent = KCommand.GZipStreamToString(url.openStream(), 0);
		return htmlcontent;
	}

	// 判断是否有可用的网络连接
	public static boolean isNetConnectNoMsg(Context context) {
		try {
			/*
			 * Check for Internet Connection (Through whichever interface)
			 */
			ConnectivityManager connManager = (ConnectivityManager) (context)
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = connManager.getActiveNetworkInfo();
			/******* EMULATOR HACK - false condition needs to be removed *****/
			// if (false && (netInfo == null || !netInfo.isConnected())){
			if ((netInfo == null || netInfo.isConnected() == false)) {
				// SendMessage((context), "No Internet Connection");
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// 检查网络 是否正常
	private boolean checkNet(Context context) {
		ConnectivityManager manager = (ConnectivityManager) (context)
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netWrokInfo = manager.getActiveNetworkInfo();
		if (netWrokInfo == null || !netWrokInfo.isAvailable()) {
			Toast.makeText(context, "当前的网络不可用，请开启\n网络", Toast.LENGTH_LONG)
					.show();
			return false;
		} else if (netWrokInfo.getTypeName().equals("MOBILE")
				& netWrokInfo.getExtraInfo().equals("cmwap")) {
			Toast.makeText(context, "cmwap网络不可用，请选择cmnet网络", Toast.LENGTH_LONG)
					.show();
			return false;
		} else if (netWrokInfo.getTypeName().equals("MOBILE")
				& netWrokInfo.getExtraInfo().equals("cmwap")) {
			Toast.makeText(context, "cmwap网络不可用，请选择cmnet网络", Toast.LENGTH_LONG)
					.show();
			return false;
		} else {

			return true;
		}
	}

	// 判断是否有可用的网络连接
	public static boolean isNetConnect(Context context) {
		try {
			ConnectivityManager connManager = (ConnectivityManager) (context)
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = connManager.getActiveNetworkInfo();
			/******* EMULATOR HACK - false condition needs to be removed *****/
			if ((netInfo == null || netInfo.isConnected() == false)) {
				// SendMessage((context), "No Internet Connection");
			} else if (netInfo.getTypeName().equals("MOBILE")
					&& netInfo.getExtraInfo().equals("cmwap")) {
				Toast.makeText(context, "cmwap网络不可用，请选择cmnet网络",
						Toast.LENGTH_LONG).show();
				return false;
			} else {

				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Toast.makeText(context,context.getString(R.string.msg_not_network_please_check_network), Toast.LENGTH_SHORT).show();
		return false;
	}

	public static boolean isWiFiActive(Context inContext) {
		Context context = inContext.getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getTypeName().equals("WIFI")
							&& info[i].isConnected()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// 屏幕亮度调节
	public static void SetScreenBrightness(Activity inActivity) {
		int value = SaveData
				.readPreferencesInt(inActivity, "ScreenBrightValue");
		;
		if (value < 0) {
			value = 5;
			SaveData.writePreferencesInt(inActivity, "ScreenBrightValue", value);
		}
		KCommand.ScreenBrightness(inActivity, value);
	}

	// 屏幕亮度调节
	public static void ScreenBrightness(Activity inActivity, int value) {
		if (value < 1) {
			value = 1;
		} else if (value > 10) {
			value = 10;
		}
		WindowManager.LayoutParams lp = inActivity.getWindow().getAttributes();
		lp.screenBrightness = value / 10.0f;
		inActivity.getWindow().setAttributes(lp);
	}

	// 屏幕切换
	public static boolean GetToolsVisible(Activity inActivity) {
		bToolsVisible = SaveData.readPreferencesBoolean(inActivity,
				"ToolsVisible", false);// 输入自动化
		return bToolsVisible;
	}

	// 屏幕切换
	public static boolean SetToolsVisible(Activity inActivity) {
		bToolsVisible = SaveData.readPreferencesBoolean(inActivity,
				"ToolsVisible", false);// 输入自动化
		bToolsVisible = !bToolsVisible;
		SaveData.writePreferencesBoolean(inActivity, "ToolsVisible",
				bToolsVisible);// 输入自动化
		return bToolsVisible;
	}

	public static boolean GetLightMode(Activity inActivity) {
		return SaveData.readPreferencesBoolean(inActivity, "LightMode", true);// 输入自动化

	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String now() {
		Time localTime = new Time("Asia/Hong_Kong");
		localTime.setToNow();
		return localTime.format("%Y-%m-%d %H:%M:%S");
	}

	private static String UserName = null;

	/**
	 * 获取用户名称
	 * 
	 * @param context
	 * @return
	 */
	public static String GetUserName(Context context) {
		if (UserName == null)
			UserName = SaveData.readPreferencesString(context, "UserName");
		return UserName;
	}

}

// public static String StringFilter(String str) throws PatternSyntaxException
// {
// // 只允许字母和数字
// // String regEx = "[^a-zA-Z0-9]";
// // 清除掉所有特殊字符
// //String regEx =
// "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
// String regEx =
// "[`~!#$%^&*()+=|{}':;',\\[\\]<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’、？]";
// Pattern p = Pattern.compile(regEx);
// Matcher m = p.matcher(str);
// return m.replaceAll("").trim();
// }