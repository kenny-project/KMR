package com.work.Image;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
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
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.framework.log.P;

/**
 * ����? * 
 * @author wangmh
 * */
public class T
{
	private static DecimalFormat myformat = new DecimalFormat("####.00");
	private static WakeLock mWakeLock = null;// ��Դ����

	/**
	 * ���ϵͳ�Ƿ�֧��ROOTȨ��
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
	 * �������?	 * */
	public static void hideInputPad(final View edit)
	{
		// TODO Auto-generated method stub
		InputMethodManager inputmanager = (InputMethodManager) edit
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputmanager.hideSoftInputFromWindow(edit.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * ��ʾ���?	 * */
	public static void showInputpad(final View edit)
	{

		// TODO Auto-generated method stub
		InputMethodManager inputmanager = (InputMethodManager) edit
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputmanager.showSoftInput(edit,
				InputMethodManager.RESULT_UNCHANGED_SHOWN);
	}

	/**
	 * ?��Դ����
	 * 
	 * @param context
	 */
	public static void PowerStart(Context context)
	{
		if (mWakeLock == null)
		{
			// ----------��Դ����-------------------------------
			PowerManager pm = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
					"KREAD");
			mWakeLock.acquire();
			// --------------��Դ���� end----------------------
		}
	}

	/**
	 * ��ȡ��ǰʱ��
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
	 * �رյ�Դ����,ʹ֮�ָ�����״?
	 * 
	 * @param context
	 */
	public static void PowerClose()
	{
		mWakeLock.release();
		mWakeLock = null;
	}

	/**
	 * Ӧ��ȫ��
	 * 
	 * @param context
	 */
	public static void FullScreen(Activity context)
	{
		/** ȫ�����ã����ش�������װ?*/
		context.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**
	 * ��õ�ǰϵͳ?����
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
	 * ��õ�ǰϵͳ?����
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

	// ��ȡIMEI?	
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
	 * ��ȡ��ǰϵͳ����
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
	 * �趨��Ļ����
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
	 * ����汾ֹͣӦ�ý���
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
	 * �������?	 * 
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
	 * ������ת����
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
	 * ��������
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

	/**
	 * ����ļ�����
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
	 * ����ļ���?��С
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
		if (totalsize > 1024)
		{
			totalsize = totalsize / 1024.0;
			if (totalsize > 1024)
			{
				totalsize = totalsize / 1024.0;
				if (totalsize > 1024)
				{
					totalsize = totalsize / 1024.0;
					szLength = myformat.format(totalsize) + "G";
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
	 * ����ͼƬ���ʼ�����ʽ��?
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
		i.putExtra(Intent.EXTRA_STREAM, U);// ���������ͼƬ��uri
		i.setType("image/*");
		context.startActivity(Intent.createChooser(i, title));// TITLE_TIP�ǵ�����ѡ�����������ֱ�?		
		return 1;
	}

	/**
	 * ��ȡ��ǰӦ�õ���ϸ��?	 * 
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

	/**
	 * ����ͼƬ���ʼ�����ʽ��?
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
		i.putExtra(Intent.EXTRA_STREAM, U);// ���������ͼƬ��uri
		i.setType("*/*");
		context.startActivity(Intent.createChooser(i, title));// TITLE_TIP�ǵ�����ѡ�����������ֱ�?		
		return 1;
	}

	public static int dip2px(Context context, float dipValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * ��ȡxml�е�String
	 * 
	 * @param con
	 * @param id
	 *            xml��string��id
	 * */
	public static String getString(Context con, int id)
	{
		String s = "";
		s = con.getString(id);
		return s;
	}

	// ?SD���Ƿ��?true:���� false:����?
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
	 * ��õ�ǰϵͳ?����
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

	// ��ȡʱ��
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
	 * ?�ļ��Ƿ����
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
	 * Bitmapת����byte[]
	 * 
	 * @param Bitmap
	 *            ?ת���Ķ�?	 * @return byte[]
	 * */
	public byte[] Bitmap2Bytes(Bitmap bm)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * ��ȡ�Ҷ�ͼƬ
	 * 
	 * @param bitmap
	 *            �����ͼ?	 * @return �Ҷ�ͼƬ
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
	 * ��ȡԲ��ͼƬ
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
	 * byte[]ת����bitmap
	 * 
	 * @param b
	 *            ?ת����byte[]
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

	// ��ȡ�����ļ�
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

	// ��ʼ�����г�����?����?
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
	 * ���Ƶ����ļ�
	 * 
	 * @param oldPath
	 *            String ԭ�ļ�·?�磺c:/fqf.txt
	 * @param newPath
	 *            String ���ƺ�·?�磺f:/fqf.txt
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
			bytesum += byteread; // �ֽ�?�ļ���С
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
	 * ��ȡassetĿ¼�µ���Դ
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

	// ��ʼ�����г�����?����?	
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
	 * ��ȡassetĿ¼�µ���Դ
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
	 * bitmap ?drawable
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
	 * �Ŵ���СͼƬ
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
	 * ��ȡ��ǰ����İ汾��
	 */
	public static String getVersionName(Context context) throws Exception
	{
		// ��ȡpackagemanager��ʵ?	
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()���㵱ǰ��İ���?�����ǻ�ȡ�汾��?		
		PackageInfo packInfo = packageManager.getPackageInfo(
				context.getPackageName(), 0);
		return packInfo.versionName;
	}

	/**
	 * ��ѹzip�ļ�
	 * 
	 * @param zipFile
	 *            Ҫ��ѹ���ļ�?	 * @param targetDir
	 *            ��ѹ�����ŵ��ļ���
	 */
	private static void Unzip(String zipFile, String targetDir)
	{
		int BUFFER = 4096; // ���ﻺ��������ʹ?KB?		
		String strEntry; // ����ÿ��zip����Ŀ��?
		try
		{
			BufferedOutputStream dest = null; // �������?		
			FileInputStream fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry; // ÿ��zip��Ŀ��ʵ?
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
	 * ��ȡ����̵�״?
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
