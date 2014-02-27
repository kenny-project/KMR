package com.kenny.file.Image;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.kenny.file.bean.FileBean;
import com.kenny.file.interfaces.ImageCallback;
import com.kenny.file.tools.ApkTools;
import com.kenny.file.tools.ApkTools.AppInfoData;
import com.kenny.file.util.SDFile;

//下载网络图标并读取本地图标
public class ImageLoader extends Thread
{
	private static ImageLoader m_object;
	private Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
	private Context m_ctx;
	private Thread mThread = null;

	private ImageLoader()
	{
		mThread = new Thread(this);
		mThread.start();
	}

	public static ImageLoader getInstance(Context ctx)
	{
		if (m_object == null)
		{
			m_object = new ImageLoader();
		}
		m_object.m_ctx = ctx;
		return m_object;
	}

	/**
	 * 清除全部数据
	 */
	public void RemoveAll()
	{
		imageCache.clear();
	}

	// public Drawable loadApp(Context context, final String packageName)
	// {
	// Drawable image = null;
	// // final String path = fileBean.getFilePath();
	// // final String fileEnds = fileBean.getFileEnds();
	// if (imageCache.containsKey(packageName))// 从列表中获取数据
	// {
	// SoftReference<Drawable> softReference = imageCache.get(packageName);
	// if (softReference != null)
	// {
	// image = softReference.get();
	// if (image != null)
	// {
	// return image;
	// }
	// }
	// imageCache.remove(packageName);
	// }
	// try
	// {
	// image = context.getPackageManager().getApplicationIcon(packageName);
	// imageCache.put(packageName, new SoftReference<Drawable>(image));
	// } catch (NameNotFoundException e)
	// {
	// e.printStackTrace();
	// }
	// return image;
	// }

	Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if (msg.obj != null)
			{
				try
				{
					LoadFileIcoEvent event = (LoadFileIcoEvent) msg.obj;
					if (event != null)
					{
						event.callback.imageLoaded(event.image,
								event.fileBean.getFilePath());
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	};

	public Drawable loadDrawable(final FileBean fileBean,
			final ImageCallback callback)
	{
		Drawable image = null;
		if (imageCache.containsKey(fileBean.getFilePath()))// 从列表中获取数据
		{
			SoftReference<Drawable> softReference = imageCache.get(fileBean
					.getFilePath());
			if (softReference != null)
			{
				image = softReference.get();
				if (image != null)
				{
					return image;
				}
			}
			imageCache.remove(fileBean.getFilePath());
		}
		LoadFileIcoEvent event = new LoadFileIcoEvent();
		event.fileBean = fileBean;
		event.callback = callback;
		addEvent(event);
		return null;
	}

	private boolean isrun = true;
	private Vector<LoadFileIcoEvent> vec = new Vector<LoadFileIcoEvent>();

	public void cancel()
	{
		try
		{
			// this.isrun = false;
			synchronized (vec)
			{
				if (vec.size() != 0)
				{
					vec.removeAllElements();
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			// P.v("event", "cancel ex-" + e.toString());
		}
	}

	public void addEvent(LoadFileIcoEvent event)
	{
		synchronized (vec)
		{
			vec.add(event);
			vec.notify();
			// P.v("event", "add event unblock " + event.getClass().getName());
		}
	}

	public void run()
	{
		try
		{
			while (isrun)
			{
				synchronized (vec)
				{
					if (vec.size() <= 0)
					{
						vec.wait();
						continue;
					}
				}
				if (vec.size() > 0)
				{
					LoadFileIcoEvent event = vec.remove(0);
					// P.v("event", "[exce start]<" + event.getClass().getName()
					// + ">");
					try
					{
						event.ok();
					} catch (Exception e)
					{
						e.printStackTrace();
					}
					// P.v("event", "[exce end]<" + event.getClass().getName()
					// + ">");
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			m_object = null;
			// P.v("event", "run " + e.toString());
		}
	}

	public static final int APK_UNINSTALL = 0x1002;// 未安装
	public static final int APK_LOW_VERCODE = 0x1003;// 低版本
	public static final int APK_HIGH_VERCODE = 0x1004;// 高版本
	public static final int APK_NOW_VERCODE = 0x1005;// 当前版本

	public class LoadFileIcoEvent
	{
		// public String path, fileEnds;
		public FileBean fileBean;
		public ImageCallback callback;
		public Drawable image = null;

		public void ok()
		{
			String path = fileBean.getFilePath();
			String fileEnds = fileBean.getFileEnds();
			Message msg = new Message();
			msg.what = 0;
			if (imageCache.containsKey(path))// 从列表中获取数据
			{
				SoftReference<Drawable> softReference = imageCache.get(path);
				if (softReference != null)
				{
					image = softReference.get();
					if (image != null)
					{
						handler.sendMessage(handler.obtainMessage(0, this));
						return;
					}
				}
			}

			if (fileEnds.equals("jpg") || fileEnds.equals("gif")
					|| fileEnds.equals("png") || fileEnds.equals("jpeg")
					|| fileEnds.equals("bmp"))
			{
				Bitmap temp = SDFile.ReadFileToBitmap(m_ctx, path, 108);
				image = new BitmapDrawable(temp);
			} else if (fileEnds.equals("apk"))
			{
				int nFlag = 0;
				AppInfoData info = ApkTools.getApplicationInfo(m_ctx, new File(
						path));
				try
				{
					PackageInfo pgInfo = m_ctx.getPackageManager()
							.getPackageInfo(info.getApppackage(),
									PackageManager.GET_UNINSTALLED_PACKAGES);
					if (pgInfo.versionCode > info.getAppversionCode())
					{
						nFlag = APK_LOW_VERCODE;
					}
					else if (pgInfo.versionCode > info.getAppversionCode())
					{
						nFlag = APK_LOW_VERCODE;
					}
				} catch (NameNotFoundException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					nFlag = APK_UNINSTALL;
				}
				if (info != null)
				{
					fileBean.setNickName("名称:" + info.getAppname() + "\n版本:"
							+ info.getAppversion());
					image = info.getAppIcon();
					msg.what = 1;
				} else
				{
					image = ApkTools.getApkFileInfo(m_ctx, new File(path));
				}
				if (image != null)
				{
					switch (nFlag)
					{
					case APK_UNINSTALL:
						break;
					case APK_LOW_VERCODE:
						break;
					case APK_HIGH_VERCODE:
						break;
					case APK_NOW_VERCODE:
						break;
					}
				}

			}
			if (image != null)
			{
				SoftReference<Drawable> softReferece = new SoftReference<Drawable>(
						image);
				imageCache.put(path, softReferece);
				fileBean.setFileIco(softReferece);
				msg.obj = this;
				handler.sendMessage(msg);
			}
		}
	}

	// Drawable zoomDrawable(Bitmap oldbitmap, int w, int h)
	// {
	// try
	// {
	// if (oldbitmap == null)
	// return null;
	// int width = oldbitmap.getWidth();
	// int height = oldbitmap.getHeight();
	// if (width > 500 || height > 800)
	// return null;
	//
	// Matrix matrix = new Matrix(); // 创建操作图片用的 Matrix 对象
	// float scaleWidth = ((float) w / width); // 计算缩放比例
	// float scaleHeight = ((float) h / height);
	// // matrix.postScale(scaleWidth, scaleHeight); // 设置缩放比例
	// matrix.postScale(scaleWidth, scaleWidth); // 设置缩放比例
	// Bitmap newbmp = Bitmap.createBitmap(oldbitmap, 0, 0, width, height,
	// matrix, true); // 建立新的
	// return new BitmapDrawable(newbmp); // 把 bitmap 转换成
	// // drawable 并返回
	// } catch (Exception e)
	// {
	// e.printStackTrace();
	// return null;
	// }
	// }

	Bitmap drawableToBitmap(Drawable drawable) // drawable 转换成 bitmap
	{
		int width = drawable.getIntrinsicWidth(); // 取 drawable 的长宽
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565; // 取 drawable 的颜色格式
		Bitmap bitmap = Bitmap.createBitmap(width, height, config); // 建立对应
		// bitmap
		Canvas canvas = new Canvas(bitmap); // 建立对应 bitmap 的画布
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas); // 把 drawable 内容画到画布中
		return bitmap;
	}

}
