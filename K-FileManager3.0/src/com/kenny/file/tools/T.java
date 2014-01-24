package com.kenny.file.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.framework.log.P;
import com.kenny.KFileManager.R;

/**
 * 工具类
 * 
 * @author wangmh
 * */
public class T
{
	private static DecimalFormat myformat = new DecimalFormat("####.00");
	private static WakeLock mWakeLock = null;// 电源管理

	/**
	 * 获得系统是否支持ROOT权限
	 * 
	 * @param context
	 * @param command
	 * @return
	 */
	public static boolean RootCommand(Context context)
	{
		Process process = null;
		DataOutputStream os = null;
		String apkRoot = "chmod 777 " + context.getPackageCodePath();
		try
		{
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(apkRoot + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e)
		{
			Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
			return false;
		} finally
		{
			try
			{
				if (os != null)
				{
					os.close();
				}
				if (process != null)
				{
				process.destroy();
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		Log.d("*** DEBUG ***", "Root SUC ");
		return true;
	}

	/**
	 * 隐藏软键盘
	 * */
	public static void hideInputPad(final View edit)
	{
		// TODO Auto-generated method stub
		InputMethodManager inputmanager = (InputMethodManager) edit
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputmanager.hideSoftInputFromWindow(edit.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 显示软键盘
	 * */
	public static void showInputpad(final View edit)
	{

		// TODO Auto-generated method stub
		InputMethodManager inputmanager = (InputMethodManager) edit
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputmanager.showSoftInput(edit,
				InputMethodManager.RESULT_UNCHANGED_SHOWN);
	}

	/**
	 * 开启电源长亮
	 * 
	 * @param context
	 */
	public static void PowerStart(Context context)
	{
		if (mWakeLock == null)
		{
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
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String NowTimeString()
	{
		Time localTime = new Time("Asia/Hong_Kong");
		localTime.setToNow();
		return localTime.format("%Y-%m-%d %H:%M:%S");
	}

	/**
	 * 关闭电源长亮,使之恢复正常状态
	 * 
	 * @param context
	 */
	public static void PowerClose()
	{
		mWakeLock.release();
		mWakeLock = null;
	}

	/**
	 * 应用全屏
	 * 
	 * @param context
	 */
	public static void FullScreen(Activity context)
	{
		/** 全屏设置，隐藏窗口所有装饰 */
		context.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**
	 * 获得当前系统版 本号
	 * 
	 * @param context
	 * @return
	 */
	public static int GetVersionCode(Context context)
	{
		int versionCode = 0;
		try
		{
			versionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
			return versionCode;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return versionCode;
	}

	/**
	 * 获得当前系统版 本号
	 * 
	 * @param context
	 * @return
	 */
	public static String GetVersionName(Context context)
	{
		String versionName = "";
		try
		{
			versionName = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
			return versionName;
		} catch (NameNotFoundException e)
		{
			return versionName;
		}
	}

	// 获取IMEI号
	public static String GetIMEI(Context context)
	{
		String IMEI = SaveData.Read(context, "IMEI", "");
		if (IMEI.equals(""))
		{
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			IMEI = tm.getDeviceId();
			SaveData.Write(context, "IMEI", IMEI);
		}
		return IMEI;
	}

	/**
	 * 获取当前系统语言
	 * 
	 * @return
	 */
	public String getLocaleLanguage(Context ctx)
	{
		String value = ctx.getResources().getConfiguration().locale
				.getLanguage();
		Locale l = Locale.getDefault();
		return String.format("%s-%s", l.getLanguage(), l.getCountry());
	}

	/**
	 * 设定屏幕方向
	 * 
	 * @param inActivity
	 * @param enable
	 */
	public static void SetScreenOrientation(Activity act, boolean enable)
	{
		if (enable)
		{
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		} else
		{
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		}
	}

	/**
	 * 多个版本停止应用进程
	 * 
	 * @param packageName
	 */
	public static void KillProcessess(ActivityManager manager,
			String packageName)
	{
		if (android.os.Build.VERSION.SDK_INT >= 8)
		{
			manager.killBackgroundProcesses(packageName);
			// manager.killBackgroundProcesses(packageName);
		} else if (android.os.Build.VERSION.SDK_INT >= 3)
		{
			manager.restartPackage(packageName);
		}
	}

	/**
	 * 创建快捷方式
	 * 
	 * @param ctx
	 */
	public static void shortcut(Context ctx)
	{
		String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
		String EXTRA_SHORTCUT_DUPLICATE = "duplicate";
		Intent shortcutIntent = new Intent(ACTION_INSTALL_SHORTCUT);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				ctx.getString(R.string.app_name));
		shortcutIntent.putExtra(EXTRA_SHORTCUT_DUPLICATE, false);
		// Intent intent = new Intent();
		// intent.setComponent(new ComponentName(ctx.getPackageName(),
		// ".Splash"));

		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

		Intent intent = new Intent();// 启动应用程序
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		intent.setComponent(new ComponentName(ctx.getPackageName(),
				"com.framework.main.Main"));

		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(ctx, R.drawable.logo));
		ctx.sendBroadcast(shortcutIntent);

	}

	/**
	 * 隐藏软键盘
	 * 
	 * @param ctx
	 * @param binder
	 *            =EditText.getWindowToken()
	 */
	public static void hideSoftInput(Context ctx, IBinder binder)
	{
		InputMethodManager imm = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(binder, 0);
	}

	/**
	 * 将毫秒转分钟
	 * 
	 * @param millisecond
	 * @return
	 */
	public static String MilliToMinute(int millisecond)
	{
		int nSecond = (int) (millisecond / 1000L);
		String strMinute = String.valueOf(nSecond / 60);
		String strSecond = String.valueOf(nSecond % 60);
		if (strSecond.length() <= 1)
		{
			strSecond = "0" + strSecond;
		}
		// String strSecond =myformat.format(nSecond%60);
		// String strSecond =myformat.format(nSecond/60+nSecond%60.0/100.0);
		return strMinute + '.' + strSecond;
	}

	/**
	 * 设置桌面
	 * 
	 * @param context
	 * @param path
	 * @return
	 */
	public static int startWallpaper(Context context, String path)
	{

		// closeAllApps(true);
		FileInputStream is;
		try
		{
			is = new FileInputStream(new File(path));
			context.setWallpaper(is);
			return 1;
		} catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -1;
		}
	}

	public static void setMyRingtone(Context context, String path2)
	{
		File file = new File(path2);
		ContentValues values = new ContentValues();
		values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
		values.put(MediaStore.MediaColumns.TITLE, file.getName());
		values.put(MediaStore.MediaColumns.SIZE, file.length());
		values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
		values.put(MediaStore.Audio.Media.ARTIST, "Madonna");
		values.put(MediaStore.Audio.Media.DURATION, 230);
		values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
		values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
		values.put(MediaStore.Audio.Media.IS_ALARM, false);
		values.put(MediaStore.Audio.Media.IS_MUSIC, false);
		Uri uri = MediaStore.Audio.Media.getContentUriForPath(file
				.getAbsolutePath());
		Uri newUri = context.getContentResolver().insert(uri, values);
		RingtoneManager.setActualDefaultRingtoneUri(context,
				RingtoneManager.TYPE_RINGTONE, newUri);
	}

	public static void settingRingertone(Context context, String path2)
	{
		// TODO Auto-generated method stub
		try
		{
			ContentValues cv = new ContentValues();
			Uri newUri = null;
			Uri uri = MediaStore.Audio.Media.getContentUriForPath(path2);
			Cursor cursor = context.getContentResolver().query(uri, null,
					MediaStore.MediaColumns.DATA + "=?", new String[]
					{ path2 }, null);
			if (cursor.moveToFirst() && cursor.getCount() > 0)
			{
				String _id = cursor.getString(0);
				cv.put(MediaStore.Audio.Media.IS_RINGTONE, true);// 设置来电铃声为true
				cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);// 设置通知铃声为false
				cv.put(MediaStore.Audio.Media.IS_ALARM, false);// 设置闹钟铃声为false
				cv.put(MediaStore.Audio.Media.IS_MUSIC, false);
				// 把需要设为铃声的歌曲更新铃声库
				context.getContentResolver().update(uri, cv,
						MediaStore.MediaColumns.DATA + "=?", new String[]
						{ path2 });
				newUri = ContentUris.withAppendedId(uri, Long.valueOf(_id));
				// 来电铃声则为：
				// RingtoneManager.TYPE_RINGTONE、通知铃声为：RingtoneManager.TYPE_NOTIFICATION、
				// 闹钟铃声为:RingtoneManager.TYPE_ALARM、所有铃声为：RingtoneManager.TYPE_ALL
				RingtoneManager.setActualDefaultRingtoneUri(context,
						RingtoneManager.TYPE_RINGTONE, newUri);
				Ringtone rt = RingtoneManager.getRingtone(context, newUri);
				rt.play();
			} else
			{
				throw new Exception(context.getString(R.string.unknown));
				// insert 这里还有一点问题，故没有写上来
				// cv.put(AudioColumns.DATA,path2);
				// newUri = TestRingtone.this.getContentResolver().insert(uri,
				// cv);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 获得文件总数
	 * 
	 * @param strPath
	 * @return
	 */
	public static Long FileCount(String strPath)
	{
		File dir = new File(strPath);
		Long count = 1l;
		if (!dir.isDirectory())
		{
			return count;
		}
		File[] files = dir.listFiles();
		if (files == null)
			return count;
		count = (long) (files.length);
		for (int i = 0; i < files.length; i++)
		{
			File file = files[i];
			if (file.isDirectory())
			{
				count += FileCount(file.getAbsolutePath());
			}
		}
		return count;
	}

	/**
	 * 获得文件夹总大小
	 * 
	 * @param strPath
	 * @return
	 */
	public static Long FileSize(String strPath)
	{
		File dir = new File(strPath);
		Long length = 0l;
		if (!dir.isDirectory())
		{
			return dir.length();
		}
		File[] files = dir.listFiles();
		if (files == null)
			return length;
		for (int i = 0; i < files.length; i++)
		{
			File file = files[i];
			if (file.isDirectory())
			{
				length += FileSize(file.getAbsolutePath());
			} else
			{
				length += file.length();
			}
		}
		return length;
	}

	public static String FileSizeToString(Long length)
	{
		String szLength;
		double totalsize = length;
		if (totalsize > 1000)
		{
			totalsize = totalsize / 1024.0;
			if (totalsize > 1000)
			{
				totalsize = totalsize / 1024.0;
				if (totalsize > 1000)
				{
					totalsize = totalsize / 1024.0;
					if (totalsize > 1000)
					{
						totalsize = totalsize / 1024.0;
						szLength = myformat.format(totalsize) + "T";						
					}
					else
					{
					totalsize = totalsize / 1024.0;
					szLength = myformat.format(totalsize) + "G";
					}
				} else
				{
					szLength = myformat.format(totalsize) + "M";
				}
			} else
			{
				szLength = myformat.format(totalsize) + "K";
			}
		} else
		{
			if (totalsize == 0)
			{
				return "0.00B";
			} else
			{
				szLength = myformat.format(totalsize) + "B";
			}
		}
		return szLength;
	}

	/**
	 * 分享图片以邮件的形式发送
	 * 
	 * @param context
	 * @param title
	 * @param imageUri
	 * @return
	 */
	public static int ShareImageIntent(Context context, String title,
			String path)
	{
		P.v("share://" + path);
		Intent i = new Intent(Intent.ACTION_SEND);
		Uri U = Uri.fromFile(new File(path));
		i.putExtra(Intent.EXTRA_STREAM, U);// 这里必须是图片的uri
		i.setType("image/*");
		context.startActivity(Intent.createChooser(i, title));// TITLE_TIP是弹出的选择程序处理的文字标题
		return 1;
	}

	/**
	 * 获取当前应用的详细信息
	 * 
	 * @param context
	 * @param title
	 * @param imageUri
	 * @return
	 */
	public static int DetailsIntent(Context context)
	{
		try
		{
			Uri uri = Uri.parse("market://details?id="
					+ context.getPackageName());
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return 1;
	}

	public static void SendShare(Context context, String Title, String Body)
	{
		SendShare(context, Title, Body, null);
	}

	// 发送给好友 微薄信息加图片
	public static void SendShare(Context context, String Title, String Body,
			String imagePath1)
	{
		Intent it = new Intent(Intent.ACTION_SEND);
		String[] tos =
		{ "" };
		if (imagePath1 != null)
		{

			String url;
			try
			{
				File imageFile = new File(imagePath1);
				if (imageFile.exists())
				{
					url = Media.insertImage(context.getContentResolver(),
							imageFile.getAbsolutePath(), imageFile.getName(),
							imageFile.getName());
					Uri uri = Uri.parse(url);// + imagePath1);
					it.putExtra(Intent.EXTRA_STREAM, uri);
				}
			} catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		it.putExtra(Intent.EXTRA_EMAIL, tos);
		it.putExtra(Intent.EXTRA_SUBJECT, "看到一个很有意思的发给你");
		it.putExtra(Intent.EXTRA_TITLE, "" + Title);
		it.putExtra(Intent.EXTRA_TEXT, "" + Body);

		// it.setType("text/plain");
		it.setType("image/*");
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(it);
		return;
	}

	/**
	 * 分享图片以邮件的形式发送
	 * 
	 * @param context
	 * @param title
	 * @param imageUri
	 * @return
	 */
	public static int ShareIntent(Context context, String title, String path)
	{
		P.v("share://" + path);
		Intent i = new Intent(Intent.ACTION_SEND);
		Uri U = Uri.fromFile(new File(path));
		i.putExtra(Intent.EXTRA_STREAM, U);// 这里必须是图片的uri
		i.setType("*/*");
		context.startActivity(Intent.createChooser(i, title));// TITLE_TIP是弹出的选择程序处理的文字标题
		return 1;
	}

	public static int dip2px(Context context, float dipValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 获取xml中的String
	 * 
	 * @param con
	 * @param id
	 *            xml中string的id
	 * */
	public static String getString(Context con, int id)
	{
		String s = "";
		s = con.getString(id);
		return s;
	}

	// 检查SD卡是否存在 true:存在 false:不存在
	public static boolean checkSDCard()
	{
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
		{
			return true;
		} else
		{
			return false;
		}
	}

	/**
	 * 获得当前系统版 本号
	 * 
	 * @param context
	 * @return
	 */
	public static int GetAppVerCode(Context context)
	{
		int verCode = 0;
		try
		{
			verCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
			return verCode;
		} catch (NameNotFoundException e)
		{
			verCode = 0;
			e.printStackTrace();
			return 0;
		}
	}

	// 获取时间
	public static String getTimes(int time)
	{
		long times = (long) time * 1000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		// TimeZone tz=TimeZone.getDefault();
		// sdf.setTimeZone(tz);
		Date date = new Date(times);
		return sdf.format(date);
	}

	/**
	 * 检查文件是否存在
	 * 
	 * @return
	 */
	public static boolean CheckFileExists(String path)
	{
		try
		{
			File file = new File(path);
			return file.exists();
		} catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Bitmap转换成byte[]
	 * 
	 * @param Bitmap
	 *            需要转换的对象
	 * @return byte[]
	 * */
	public byte[] Bitmap2Bytes(Bitmap bm)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 获取灰度图片
	 * 
	 * @param bitmap
	 *            传入的图片
	 * @return 灰度图片
	 */
	public static Bitmap getGrayBitmap(Bitmap bitmap)
	{
		int width, height;
		height = bitmap.getHeight();
		width = bitmap.getWidth();
		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bitmap, 0, 0, paint);
		return bmpGrayscale;
	}

	public static Drawable getRoundImage(BitmapDrawable bitmapdrawable,
			int pixels)
	{
		Bitmap bitmap = bitmapdrawable.getBitmap();
		bitmapdrawable = new BitmapDrawable(getRoundImage(bitmap, pixels));
		return bitmapdrawable;
	}

	/**
	 * 获取圆角图片
	 * */
	public static Bitmap getRoundImage(Bitmap bitmap, int pixels)
	{

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * byte[]转换成bitmap
	 * 
	 * @param b
	 *            需要转换的byte[]
	 * @return Bitmap
	 * */
	public static Bitmap Bytes2Bimap(byte[] b)
	{
		if (b.length != 0)
		{
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else
		{
			return null;
		}
	}

	// 读取数据文件
	public static byte[] ReadResourceAssetsFile(Context context,
			String szFileName)
	{
		ByteArrayOutputStream dest = new ByteArrayOutputStream(1024);
		try
		{
			AssetManager am = context.getAssets();
			InputStream inputStream = am.open(szFileName);
			byte[] buffer = new byte[1444];
			int bufferCount = 0;
			while ((bufferCount = inputStream.read(buffer)) != -1)
			{
				dest.write(buffer, 0, bufferCount);
			}
			dest.flush();
			dest.close();
			return dest.toByteArray();
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	// 初始化运行程序所需要的文件
		public static boolean ResourceAssetsFile(Context context,String SPath, String szFileName)
		{
			String mPath = "/data/data/" + context.getPackageName() + "/files/"+SPath;
			File fileName;
			try
			{
				new File(mPath).mkdirs();
				mPath += szFileName;
				fileName = new File(mPath);
				if (fileName.exists())
				{
					fileName.delete();
				}
				fileName.createNewFile();
				AssetManager am = context.getAssets();
				InputStream inputStream = am.open(SPath+szFileName);
				copyFile(inputStream, mPath);
				return true;
			} catch (Exception e)
			{
				e.printStackTrace();
				fileName = new File(mPath);
				fileName.delete();
				return false;
			}
		}
	/**
	 * 将assets文件保存到应用软件空间中
	 * @param context
	 * @param szFileName
	 * @return
	 */
	public static boolean ResourceAssetsFile(Context context, String szFileName)
	{
		String path = "/data/data/" + context.getPackageName() + "/files/";
		File fileName;
		try
		{
			new File(path).mkdirs();
			path += szFileName;
			fileName = new File(path);
			if (fileName.exists())
			{
				fileName.delete();
			}
			fileName.createNewFile();
			AssetManager am = context.getAssets();
			InputStream inputStream = am.open(szFileName);
			copyFile(inputStream, path);
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			fileName = new File(path);
			fileName.delete();
			return false;
		}
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
			throws IOException
	{
		int bytesum = 0;
		int byteread = 0;
		FileOutputStream fs = new FileOutputStream(newPath);
		byte[] buffer = new byte[1444];
		while ((byteread = inStream.read(buffer)) != -1)
		{
			bytesum += byteread; // 字节数 文件大小
			System.out.println(bytesum);
			fs.write(buffer, 0, byteread);
		}
		inStream.close();
	}

	/**
	 * StreamToString
	 * 
	 * @param is
	 * @return
	 */
	public static String StreamToString(InputStream is)
	{
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means 31. * there's no more data to read. Each line
		 * will appended to a StringBuilder 32. * and returned as String. 33.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try
		{
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				is.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

	/**
	 * 获取asset目录下的资源
	 * */
	public static String getAssetStringFile(Context con, String filename)
	{
		try
		{
			return StreamToString(getAssetFile(con, filename));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return "";
	}
	// 初始化运行程序所需要的文件
	public static Drawable DrawableSDCardFile(Context context, String szFileName)
	{
		try
		{
			String mPath = "/data/data/" + context.getPackageName() + "/files/"+szFileName;
			FileInputStream inputStream = new FileInputStream(mPath);// context.openFileInput(szFileName);
			return Drawable.createFromStream(inputStream, szFileName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 加载手机空间的文件
	 * @param context
	 * @param szFileName
	 * @return
	 */
	public static Drawable LocalFile(Context context, String szFileName)
	{
		try
		{
			String mPath = "/data/data/" + context.getPackageName() + "/files/"+szFileName;
			FileInputStream inputStream = new FileInputStream(mPath);// context.openFileInput(szFileName);
			return Drawable.createFromStream(inputStream, szFileName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 加载手机空间的文件
	 * @param context
	 * @param szFileName
	 * @return
	 */
	public static Drawable DrawableLocalFile(Context context, String szFileName)
	{
		try
		{
			String mPath = "/data/data/" + context.getPackageName() + "/files/"+szFileName;
			FileInputStream inputStream = new FileInputStream(mPath);// context.openFileInput(szFileName);
			return Drawable.createFromStream(inputStream, szFileName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	// 初始化运行程序所需要的文件
	public static Drawable DrawableAssetsFile(Context context, String szFileName)
	{
		try
		{
			AssetManager am = context.getAssets();
			InputStream inputStream = am.open(szFileName);
			return Drawable.createFromStream(inputStream, szFileName);
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取asset目录下的资源
	 * */
	public static InputStream getAssetFile(Context con, String filename)
	{
		InputStream is = null;
		try
		{

			AssetManager am = con.getAssets();
			is = am.open(filename);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return is;
	}

	/**
	 * bitmap 转 drawable
	 * */
	public static Drawable bitmap2drawable(Bitmap bm)
	{
		try
		{
			BitmapDrawable bd = new BitmapDrawable(bm);
			return bd;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static Bitmap drawable2bitmap(Drawable d)
	{
		int w = d.getIntrinsicWidth();
		int h = d.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		d.setBounds(0, 0, w, h);
		d.draw(canvas);
		return bitmap;
	}

	public static byte[] copy(InputStream in) throws IOException
	{

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try
		{

			byte[] buffer = new byte[512];
			while (true)
			{
				int bytesRead = in.read(buffer);
				if (bytesRead == -1)
					break;
				bout.write(buffer, 0, bytesRead);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			bout.close();
		}
		return bout.toByteArray();
	}

	/**
	 * 放大缩小图片
	 * */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h)
	{

		int width = bitmap.getWidth();

		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();

		float scaleWidht = ((float) w / width);

		float scaleHeight = ((float) h / height);

		matrix.postScale(scaleWidht, scaleHeight);

		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);

		return newbmp;

	}

	/*
	 * 获取当前程序的版本号
	 */
	public static String getVersionName(Context context) throws Exception
	{
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(
				context.getPackageName(), 0);
		return packInfo.versionName;
	}

	/**
	 * 解压zip文件
	 * 
	 * @param zipFile
	 *            要解压的文件名
	 * @param targetDir
	 *            解压缩后存放的文件夹
	 */
	private static void Unzip(String zipFile, String targetDir)
	{
		int BUFFER = 4096; // 这里缓冲区我们使用4KB，
		String strEntry; // 保存每个zip的条目名称

		try
		{
			BufferedOutputStream dest = null; // 缓冲输出流
			FileInputStream fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry; // 每个zip条目的实例

			while ((entry = zis.getNextEntry()) != null)
			{

				try
				{
					P.i("Unzip: ", "=" + entry);
					int count;
					byte data[] = new byte[BUFFER];
					strEntry = entry.getName();

					File entryFile = new File(targetDir + strEntry);
					File entryDir = new File(entryFile.getParent());
					if (!entryDir.exists())
					{
						entryDir.mkdirs();
					}

					FileOutputStream fos = new FileOutputStream(entryFile);
					dest = new BufferedOutputStream(fos, BUFFER);
					while ((count = zis.read(data, 0, BUFFER)) != -1)
					{
						dest.write(data, 0, count);
					}
					dest.flush();
					dest.close();
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			zis.close();
		} catch (Exception cwj)
		{
			cwj.printStackTrace();
		}
	}

	/**
	 * 获取软键盘的状态
	 * */
	public boolean getInputPadStatus(Activity ctx)
	{
		InputMethodManager inputmanager = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (inputmanager != null)
		{
			return inputmanager.isActive();
		}
		return false;
	}

	
}
