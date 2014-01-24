package com.kenny.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.net.Uri;
import android.os.Environment;
import android.view.inputmethod.InputMethodManager;

/**
 * 工具类
 * 
 * @author aimery
 * */
public class T
{
	public static int dip2px(Context context, float dipValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}
	/**
	 * 获取当前应用的详细信息
	 * 
	 * @param context
	 * @param title
	 * @param imageUri
	 * @return
	 */
	public static int DetailsIntent(Context context,String pageName)
	{
		try
		{
			Uri uri = Uri.parse("market://details?id="
					+ pageName);
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
		}
		else
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
		}
		catch (NameNotFoundException e)
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
		}
		catch (Exception e)
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
		
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
				.getHeight(), Config.ARGB_8888);
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
		}
		else
		{
			return null;
		}
	}

	
	// 初始化运行程序所需要的文件
	public static String AssetsStringFile(Context context, String szFileName)
	{
		try
		{
			AssetManager am = context.getAssets();
			InputStream inputStream = am.open(szFileName);
			InputStreamReader    reader = new InputStreamReader (inputStream);
			int len=0;
			char[] buf=new char[6000];
			StringBuilder sb = new StringBuilder();
			while((len=reader.read(buf))>0)
			{
			         
				sb.append(buf,0,len);
			}
			return sb.toString();
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	// 初始化运行程序所需要的文件
	public static boolean ResourceAssetsFile(Context context,String folderName, String szFileName)
	{
		String path = "/data/data/" + context.getPackageName() + "/files/"+folderName+"/";
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fileName = new File(path);
			fileName.delete();
			return false;
		}
	}
	
	// 初始化运行程序所需要的文件
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fileName = new File(path);
			fileName.delete();
			return false;
		}
	}
	// 初始化运行程序所需要的文件
	public static boolean ResourceAssetsSDLogoFile(Context context, String szFileName)
	{
		if (!T.checkSDCard())
			return false;
		String path = Const.LOGO_DIRECTORY;
		File file;
		try
		{
			new File(path).mkdirs();
			path += szFileName;
			file = new File(path);
			if (file.exists())
			{
				return false;
			}
			file.createNewFile();
			AssetManager am = context.getAssets();
			InputStream inputStream = am.open(szFileName);
			copyFile(inputStream, path);
			return true;
		} catch (Exception e)
		{
			e.printStackTrace();
			file = new File(path);
			file.delete();
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
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				is.close();
			}
			catch (IOException e)
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
	      }
	      catch (Exception e)
	      {
	         e.printStackTrace();
	      }
	      return "";
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
		}
		catch (Exception e)
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
		}
		catch (Exception e)
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
				if (bytesRead == -1) break;
				bout.write(buffer, 0, bytesRead);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
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
		PackageInfo packInfo = packageManager.getPackageInfo(context
				.getPackageName(), 0);
		return packInfo.versionName;
	}
	/**
	 * 解压zip文件
	 * 
	 * @param zipFile 要解压的文件名
	 * @param targetDir 解压缩后存放的文件夹
	 */
	private static void Unzip(String zipFile, String targetDir) {
		   int BUFFER = 4096; //这里缓冲区我们使用4KB，
		   String strEntry; //保存每个zip的条目名称

		   try {
		    BufferedOutputStream dest = null; //缓冲输出流
		    FileInputStream fis = new FileInputStream(zipFile);
		    ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		    ZipEntry entry; //每个zip条目的实例

		    while ((entry = zis.getNextEntry()) != null) {

		     try 
		     {
		       //P.i("Unzip: ","="+ entry);
		      int count; 
		      byte data[] = new byte[BUFFER];
		      strEntry = entry.getName();

		      File entryFile = new File(targetDir + strEntry);
		      File entryDir = new File(entryFile.getParent());
		      if (!entryDir.exists()) {
		       entryDir.mkdirs();
		      }

		      FileOutputStream fos = new FileOutputStream(entryFile);
		      dest = new BufferedOutputStream(fos, BUFFER);
		      while ((count = zis.read(data, 0, BUFFER)) != -1) {
		       dest.write(data, 0, count);
		      }
		      dest.flush();
		      dest.close();
		     } catch (Exception ex) {
		      ex.printStackTrace();
		     }
		    }
		    zis.close();
		   } catch (Exception cwj) {
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
    	if(inputmanager!=null){
    		return inputmanager.isActive();
    	}
    	return false;
    }
}
