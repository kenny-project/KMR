package com.work.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.work.Interface.ImageCallback;
import com.work.market.net.Common;
import com.work.market.net.HttpUtil;

//��������ͼ�겢��ȡ����ͼ
public class ImageLoader extends Thread {
	private static ImageLoader m_object = new ImageLoader();
	private Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
	private Context m_ctx;
	private Thread mThread = null;

	private ImageLoader() {
		mThread = new Thread(this);
		mThread.start();
	}

	public static ImageLoader GetObject(Context ctx) {
		m_object.m_ctx = ctx;
		return m_object;
	}

	/**
	 * ���ȫ�����
	 */
	public void RemoveAll() {
		imageCache.clear();
	}

	public Drawable loadApp(Context context, final String packageName) {
		Drawable image = null;
		// final String path = fileBean.getFilePath();
		// final String fileEnds = fileBean.getFileEnds();
		if (imageCache.containsKey(packageName))// ���б��л�ȡ���
		{
			SoftReference<Drawable> softReference = imageCache.get(packageName);
			if (softReference != null) {
				image = softReference.get();
				if (image != null) {
					return image;
				}
			}
			imageCache.remove(packageName);
		}
		try {
			image = context.getPackageManager().getApplicationIcon(packageName);
			imageCache.put(packageName, new SoftReference<Drawable>(image));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return image;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj != null) {
				try {
					LoadNetIcoEvent event = (LoadNetIcoEvent) msg.obj;
					if (event != null) {
						event.callback.imageLoaded(event.image, event.path);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	public Drawable loadNetDrawable(boolean bShowLogo,final String url,
			final ImageCallback callback) {
		Drawable image = null;
		if (imageCache.containsKey(url))// ���б��л�ȡ���
		{
			SoftReference<Drawable> softReference = imageCache.get(url);
			if (softReference != null) {
				image = softReference.get();
				if (image != null) {
					return image;
				}
			}
			imageCache.remove(url);;
		}
		image = LoadLocalImg(url);
		if (bShowLogo&&image == null) 
		{
			LoadNetIcoEvent event = new LoadNetIcoEvent();
			event.path = url;
			event.callback = callback;
			addEvent(event);
			return null;
		}
		else 
		{
			return image;
		}

	}

	private Drawable LoadLocalImg(String path) {
		Bitmap tempbitmap = Getphontnames(path);
		if (tempbitmap != null) {
			BitmapDrawable image = new BitmapDrawable(tempbitmap);
			if (image != null) 
			{
				SoftReference<Drawable> softReferece = new SoftReference<Drawable>(
						image);
				imageCache.put(path, softReferece);
				return image;
			}
		}
		return null;
	}

	private boolean isrun = true;
	private Vector<LoadNetIcoEvent> vec = new Vector<LoadNetIcoEvent>();

	public void cancel() {
		try {
			// this.isrun = false;
			synchronized (vec) {
				if (vec.size() != 0) {
					vec.removeAllElements();
				}
			}
		} catch (Exception e) {
			Log.v("event", "cancel ex-" + e.toString());
		}
	}

	public void addEvent(LoadNetIcoEvent event) {
		synchronized (vec) {
			vec.add(event);
			vec.notify();
//			Log.v("event", "add event unblock " + event.getClass().getName());
		}
	}

	public void run() {
		try {
			while (isrun) {
				synchronized (vec) {
					if (vec.size() <= 0) {
						vec.wait();
						continue;
					}
				}
				if (vec.size() > 0) {
					LoadNetIcoEvent event = vec.remove(0);
//					Log.v("event", "[exce start]<" + event.getClass().getName()
//							+ ">");
					event.ok();
//					Log.v("event", "[exce end]<" + event.getClass().getName()
//							+ ">");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.v("event", "run " + e.toString());
		}
	}

	public Bitmap Getphontnames(String url) 
	{
		String filename = Common.getMd5Code(url);

		String path = Environment.getExternalStorageDirectory().toString()
				+ "/baifen/img/" + filename;
		File file1 = new File(path);
		if (file1.exists()) {
			try {
				FileInputStream fis = new FileInputStream(path);
				return BitmapFactory.decodeStream(fis);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	Bitmap drawableToBitmap(Drawable drawable) // drawable ת��?bitmap
	{
		int width = drawable.getIntrinsicWidth(); // ?drawable �ĳ�?
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565; // ?drawable ����ɫ��?
		Bitmap bitmap = Bitmap.createBitmap(width, height, config); // ������Ӧ
		// bitmap
		Canvas canvas = new Canvas(bitmap); // ������Ӧ bitmap �Ļ�?
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas); // ?drawable ���ݻ�������?
		return bitmap;
	}

	public class LoadNetIcoEvent {
		// public String path, fileEnds;
		public String path;
		public ImageCallback callback;
		public Drawable image = null;

		public void ok() {
			Message msg = new Message();
			msg.what = 0;
			if (imageCache.containsKey(path))// ���б��л�ȡ���
			{
				SoftReference<Drawable> softReference = imageCache.get(path);
				if (softReference != null) {
					image = softReference.get();
					if (image != null) {
						handler.sendMessage(handler.obtainMessage(0, this));
						return;
					}
				}
			}
			Bitmap tempbitmap = Getphontnames(path);
			if (tempbitmap == null) {
				String filename = Common.getMd5Code(path);
				String data = HttpUtil.GetPhoto5(path, filename);//
				tempbitmap = Getphontnames(path);
			}

			image = new BitmapDrawable(tempbitmap);
			if (image != null) {
				SoftReference<Drawable> softReferece = new SoftReference<Drawable>(
						image);
				imageCache.put(path, softReferece);
				msg.obj = this;
				handler.sendMessage(msg);
			}
		}

	}
}
