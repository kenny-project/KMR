package com.work.market.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Environment;

public class HttpGetAndPostNet
{
	private static PosetnetMonClick mObject;
	private static String mTdata;
	private static String mKdata;
	private static String mPdata;
	private static int mNum;
	private static byte[] imagebody;
	// private static String mData;
	private static String murl;
	private static Thread myThread;
	private static boolean mlog = false;
	private static String filesname;
	private static URL mmyUrl;
	private static long mstart = 0;

	/**
	 * 
	 * 
	 * @author zhouxl
	 */
	public static void HttpDown(String aUrl, String filenames, long start,
			PosetnetMonClick aPosetnetMonClick)
	{
		mObject = aPosetnetMonClick;
		murl = aUrl;
		mstart = start;
		filesname = filenames;
		mlog = false;
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				try
				{
					URL aurl = new URL(murl);
					HttpURLConnection connection = (HttpURLConnection) aurl
							.openConnection();
					connection.setConnectTimeout(8000);
					connection.setRequestMethod("GET");
					connection.setRequestProperty("Range", "bytes=" + mstart
							+ "-");

					long length = connection.getContentLength() + mstart;// 4157425
					String ContentType = connection.getContentType();
					ContentType = connection
							.getHeaderField("Content-Disposition");
					mObject.PostContentType(ContentType.substring(ContentType
							.length() - 4));
					mObject.HandHttpStartnum(length);
					InputStream is = connection.getInputStream();
					String s = null;
					if (is != null)
					{
						byte[] buf = new byte[1024];
						int ch = -1;
						int count = 0;
						String path = Environment.getExternalStorageDirectory()
								+ "/baifen/dowsload/" + filesname + ".apk";
						File ApkFile = new File(path);
						FileOutputStream fos = new FileOutputStream(ApkFile,
								true);
						while ((ch = is.read(buf)) != -1)
						{
							count += ch;
							fos.write(buf, 0, ch);
							// mData = new String(buf);

							if (mlog)
							{
								mObject.HandHttpError();
								fos.close();
								return;
							}
							else
							{
								mObject.HandHttpEndnum(
										(int) (((count + mstart) * 100) / length),
										(int) (count + mstart));
							}
						}
						fos.close();
						is.close();
						mObject.Postfinish(true);

						// s = new String(baos.toByteArray());
					}

				}
				catch (Exception e)
				{
					e.printStackTrace();
					mObject.Postfinish(true);
				}
			}
		}).start();
	}

	public static void HttpGetdata(String aUrl, String filenames, int start,
			PosetnetMonClick aPosetnetMonClick)// , MonClick aMonClick, int
												// aNUm)
	{// t= get_wifi_nearby_lbs& k =
		// 129ab7de655ad4aa0f0f9127c82f64fe&p={" latitude":"***"," longitude":锟斤�**锟斤�,
		// " distance":*** }
		mObject = aPosetnetMonClick;
		murl = aUrl;
		filesname = filenames;
		mlog = false;
		mstart = start;
		new Thread(new Runnable()
		{
			public void run()
			{
				try
				{

					HttpClient client = new DefaultHttpClient();
					HttpGet get = new HttpGet(murl);
					get.addHeader("Range", "bytes=" + mstart + "-");
					HttpResponse response = client.execute(get);

					if (response.getStatusLine().getStatusCode() == 200)
					{
						HttpEntity entity = response.getEntity();
						long length = entity.getContentLength();
						mObject.HandHttpStartnum(length);
						InputStream is = entity.getContent();
						String s = null;
						if (is != null)
						{
							// ByteArrayOutputStream baos = new
							// ByteArrayOutputStream();
							byte[] buf = new byte[128];
							int ch = -1;
							int count = 0;
							String path = Environment
									.getExternalStorageDirectory()
									+ "/baifen/dowsload/" + filesname + ".apk";
							File ApkFile = new File(path);
							if (!ApkFile.exists())
							{
								try
								{
									ApkFile.createNewFile();
								}
								catch (Exception e)
								{
									e.printStackTrace();
								}

							}

							FileOutputStream fos = new FileOutputStream(ApkFile);

							while ((ch = is.read(buf)) != -1)
							{
								count += ch;

								fos.write(buf, 0, ch);

								// mData = new String(buf);
								mObject.HandHttpEndnum(
										(int) ((count * 100) / length),
										(int) count);

								if (mlog)
								{
									mObject.HandHttpError();
									return;
								}
							}
							fos.close();
							is.close();
							mObject.Postfinish(true);
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					mObject.HandHttpError();
				}
			}
		}).start();

	}

	public static void stop()
	{
		mlog = true;
		// if(myThread != null)
		// {
		// aaa =1;
		// myThread.stop();
		// myThread = null;
		// }
	}
}
