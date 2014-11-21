package com.kenny.file.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import com.kenny.file.bean.SdCardInfo;
import com.kenny.file.bean.SdCardInfo.SDCardType;

public class FileUtil
{
	private static final String TAG = "FileUtil";

	public static void copy(Context context, String sourceFilename, File dest)
			throws Exception
	{
		dest.delete();
		InputStream ins = context.getAssets().open(sourceFilename);
		byte[] bytes = new byte[ins.available()];
		ins.read(bytes);
		ins.close();
		FileOutputStream fos = new FileOutputStream(dest);
		fos.write(bytes);
		fos.close();
	}

	public static boolean isFileLocked(String file)
	{
		RandomAccessFile raf = null;
		FileChannel channel = null;
		FileLock lock = null;
		try
		{
			raf = new RandomAccessFile(file, "rw");
			channel = raf.getChannel();
			lock = channel.tryLock();
			if (lock != null)
			{
				return false;
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (lock != null)
				{
					lock.release();
				}
				if (channel != null)
				{
					channel.close();
				}
				if (raf != null)
				{
					raf.close();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}

		}
		return true;
	}

	/**
	 * 检测文件是否存在
	 * 
	 * @param fileName
	 * @return boolean
	 */
	public static boolean isFileExists(String fileName)
	{
		if (!TextUtils.isEmpty(fileName))
		{// 2014-02-25 Kang, Leo add
			return new File(fileName).exists();
		} else
		{
			return false;
		}
	}

	/**
	 * 获取文本文件内容
	 * 
	 * @param fileName
	 * @return string
	 * @throws IOException
	 */

	public static String getFileContents(String fileName) throws IOException
	{
		byte[] bytes = new byte[1024];
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		File fHandler = new File(fileName);
		FileInputStream inputStream = new FileInputStream(fHandler);
		while ((inputStream.read(bytes)) != -1)
		{
			arrayOutputStream.write(bytes, 0, bytes.length);
		}
		String content = new String(arrayOutputStream.toByteArray());
		inputStream.close();
		arrayOutputStream.close();
		return content.trim();
	}

	/**
	 * 循环删除文件及文件夹
	 * 
	 * @param file
	 */
	public static void deleteFile(File file)
	{
		if (file.isDirectory())
		{
			File[] files = file.listFiles();
			for (int i = 0, len = files.length; i < len; i++)
			{
				deleteFile(files[i]);
			}
		}
		file.delete();
	}

	public static String getMapBaseStorage(Context context)
	{

		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		String PATH_FLAG = "map_base_path";
		if (currentapiVersion > 18)
			PATH_FLAG = "map_base_path_v44";

		SharedPreferences sp = context.getSharedPreferences("base_path",
				Context.MODE_PRIVATE);
		String map_base_path = sp.getString(PATH_FLAG, "");
		if (map_base_path != null && map_base_path.length() > 2)
		{
			File file = new File(map_base_path);
			if (file.isDirectory())
			{
				if (file.canWrite())
				{
					createNoMediaFileIfNotExist(map_base_path);
					return map_base_path;
				} else
				{
					map_base_path = context.getCacheDir().toString();
					if (map_base_path != null && map_base_path.length() > 2)
					{
						file = new File(map_base_path);
						if (file.isDirectory())
						{
							// createNoMediaFileIfNotExist(map_base_path);
							return map_base_path;
						}
					}
				}
			}
		}

		map_base_path = FileUtil.getExternalStroragePath(context);
		if (map_base_path != null && map_base_path.length() > 2)
		{
			File file = new File(map_base_path);
			if (file.isDirectory())
			{
				Editor editor = sp.edit();
				editor.putString(PATH_FLAG, map_base_path);
				editor.commit();

				createNoMediaFileIfNotExist(map_base_path);

				return map_base_path;
			}
		}

		map_base_path = context.getCacheDir().toString();
		if (map_base_path != null && map_base_path.length() > 2)
		{
			File file = new File(map_base_path);
			if (file.isDirectory())
			{

				// createNoMediaFileIfNotExist(map_base_path);
				// Editor editor = sp.edit();
				// editor.putString("map_base_path", map_base_path);
				// editor.commit();
				return map_base_path;
			}
		}

		return map_base_path;
	}

	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getExternalStroragePath(Context context)
	{

		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= 12)
		{// 12 is HONEYCOMB_MR1
			try
			{
				StorageManager manager = (StorageManager) context
						.getSystemService(Context.STORAGE_SERVICE);
				/************** StorageManager的方法 ***********************/
				Method getVolumeList = StorageManager.class.getMethod(
						"getVolumeList", null);
				Method getVolumeState = StorageManager.class.getMethod(
						"getVolumeState", String.class);

				Object[] Volumes = (Object[]) getVolumeList.invoke(manager,
						null);
				String state = null;
				String path = null;
				Boolean isRemove = false;
				String sdPath = "";
				String innerPath = "";
				String sdState = "";
				String innerState = "";
				String storageDir = null;
				for (Object volume : Volumes)
				{
					/************** StorageVolume的方法 ***********************/
					Method getPath = volume.getClass().getMethod("getPath",
							null);
					Method isRemovable = volume.getClass().getMethod(
							"isRemovable", null);
					path = (String) getPath.invoke(volume, null);

					state = (String) getVolumeState.invoke(manager,
							getPath.invoke(volume, null));
					isRemove = (Boolean) isRemovable.invoke(volume, null);

					Log.i(TAG, "path = " + path);
					Log.i(TAG, "isRemove = " + isRemove);
					Log.i(TAG, "state = " + state);
					// 三星S5存储卡分区问题
					if (path.toLowerCase().contains("private"))
					{
						continue;
					}
					if (isRemove)
					{
						sdPath = path;
						sdState = state;
						// 如果sd卡路径存在
						if (null != sdPath && null != sdState
								&& sdState.equals(Environment.MEDIA_MOUNTED))
						{

							if (currentapiVersion <= 18)
							{
								storageDir = sdPath;
							} else
							{
								try
								{
									storageDir = context
											.getExternalFilesDirs(null)[1]
											.getAbsolutePath();
								} catch (Exception ex)
								{
									// 此处保护java.lang.NoSuchMethodError:
									// android.content.Context.getExternalFilesDirs
									storageDir = sdPath;
								}
							}
							break;
						}
					} else
					{
						innerPath = path;
						innerState = state;
					}
				}

				// 如果sd卡路径为null,检测内部存储空间
				if (currentapiVersion <= 18)
				{// 18 is JELLY_BEAN_MR2
					if (null == storageDir && null != innerPath)
					{
						if (null != innerState
								&& innerState.equals(Environment.MEDIA_MOUNTED))
						{
							storageDir = innerPath;
						}
					}
					return storageDir;
				} else
				{// 4.4以上系统有限内部存储卡
					if (null != innerPath)
					{
						if (null != innerState
								&& innerState.equals(Environment.MEDIA_MOUNTED))
						{
							storageDir = innerPath;
						}
					}
					return storageDir;
				}
			} catch (Exception e)
			{

			}
		}

		{
			// 得到存储卡路径
			File sdDir = null;
			boolean sdCardExist = Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED); // 判断sd卡
			// 或可存储空间是否存在
			if (sdCardExist)
			{
				sdDir = Environment.getExternalStorageDirectory();// 获取sd卡或可存储空间的跟目录
				return sdDir.toString();
			}

			return null;
		}
	}

	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String[] enumExternalStroragePath(Context context)
	{

		String[] enumResult = null;
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= 12)
		{
			try
			{
				StorageManager manager = (StorageManager) context
						.getSystemService(Context.STORAGE_SERVICE);

				/************** StorageManager的方法 ***********************/
				Method getVolumeList = StorageManager.class.getMethod(
						"getVolumeList", null);
				Method getVolumeState = StorageManager.class.getMethod(
						"getVolumeState", String.class);

				Object[] Volumes = (Object[]) getVolumeList.invoke(manager,
						null);
				String state = null;
				String path = null;
				boolean isRemove = false;
				enumResult = new String[Volumes.length];
				int index = 0;
				for (Object volume : Volumes)
				{
					/************** StorageVolume的方法 ***********************/
					Method getPath = volume.getClass().getMethod("getPath",
							null);
					path = (String) getPath.invoke(volume, null);
					state = (String) getVolumeState.invoke(manager,
							getPath.invoke(volume, null));
					Method isRemovable = volume.getClass().getMethod(
							"isRemovable", null);
					isRemove = (Boolean) isRemovable.invoke(volume, null);
					if (null != path && null != state
							&& state.equals(Environment.MEDIA_MOUNTED))
					{

						if (currentapiVersion > 18 && Volumes.length > 1
								&& isRemove == true)
						{
							enumResult[index++] = context
									.getExternalFilesDirs(null)[1]
									.getAbsolutePath();
						} else
						{
							enumResult[index++] = path;
						}
					}

				}

				return enumResult;

			} catch (Exception e)
			{

			}
		}

		{
			// 得到存储卡路径
			File sdDir = null;
			boolean sdCardExist = Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED); // 判断sd卡
			// 或可存储空间是否存在
			if (sdCardExist)
			{
				enumResult = new String[1];
				sdDir = Environment.getExternalStorageDirectory();// 获取sd卡或可存储空间的跟目录
				enumResult[0] = sdDir.toString();
				return enumResult;
			}

			return null;
		}
	}

	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static ArrayList<SdCardInfo> enumExternalInnerPath(Context context)
	{

		ArrayList<SdCardInfo> list = new ArrayList<SdCardInfo>();

		SdCardInfo[] enumResult = null;
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= 12)
		{
			try
			{
				StorageManager manager = (StorageManager) context
						.getSystemService(Context.STORAGE_SERVICE);

				/************** StorageManager的方法 ***********************/
				Method getVolumeList = StorageManager.class.getMethod(
						"getVolumeList", null);
				Method getVolumeState = StorageManager.class.getMethod(
						"getVolumeState", String.class);

				Object[] Volumes = (Object[]) getVolumeList.invoke(manager,
						null);
				String state = null;
				String path = null;
				boolean isRemove = false;
				int index = 0;
				for (Object volume : Volumes)
				{
					/************** StorageVolume的方法 ***********************/
					Method getPath = volume.getClass().getMethod("getPath",
							null);
					path = (String) getPath.invoke(volume, null);
					state = (String) getVolumeState.invoke(manager,
							getPath.invoke(volume, null));
					Method isRemovable = volume.getClass().getMethod(
							"isRemovable", null);
					isRemove = (Boolean) isRemovable.invoke(volume, null);
					if (null != path && null != state
							&& state.equals(Environment.MEDIA_MOUNTED))
					{

						if (currentapiVersion > 18 && Volumes.length > 1
								&& isRemove == true)
						{
							String sdPath = context.getExternalFilesDirs(null)[1]
									.getAbsolutePath();
							SdCardInfo sdCard = new SdCardInfo(
									SDCardType.EXTERNALCARD, sdPath);
							list.add(sdCard);
						} else
						{
							if (isRemove)
							{
								SdCardInfo sdCard = new SdCardInfo(
										SDCardType.EXTERNALCARD, path);
								list.add(sdCard);
							} else
							{
								SdCardInfo sdCard = new SdCardInfo(
										SDCardType.INNERCARD, path);
								list.add(sdCard);
							}

						}
					}

				}
			} catch (Exception e)
			{

			}
		} else
		{
			// 得到存储卡路径
			File sdDir = null;
			boolean sdCardExist = Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED); // 判断sd卡
			// 或可存储空间是否存在
			if (sdCardExist)
			{
				sdDir = Environment.getExternalStorageDirectory();// 获取sd卡或可存储空间的跟目录
				SdCardInfo sdCard = new SdCardInfo(SDCardType.INNERCARD,
						sdDir.toString());
				list.add(sdCard);
			}
		}
		return list;
	}

	/**
	 * 将字符串保存到指定的文件
	 * 
	 * @param file
	 * @param data
	 */
	public static void writeTextFile(File file, String data)
	{
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		WriteLock writelock = lock.writeLock();
		writelock.lock();
		try
		{
			if (!file.exists())
			{
				File parent = file.getParentFile();
				parent.mkdirs();
			}
			if (file.exists())
			{ // 判断当前文件是否存在
				file.delete(); // 存在就删除
			}
			file.createNewFile();
			byte[] bytes = data.getBytes();
			OutputStream os = new FileOutputStream(file);
			os.write(bytes);
			os.flush();
			os.close();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		} finally
		{
			writelock.unlock();
		}
	}

	/**
	 * 将字符数组保存到指定的文件
	 * 
	 * @param file
	 * @param data
	 */
	public static void writeTextFile(File file, byte[] data)
	{

		try
		{
			if (file.exists())
			{ // 判断当前文件是否存在
				file.delete(); // 存在就删除
			}

			file.createNewFile();
			OutputStream os = new FileOutputStream(file);
			os.write(data);
			os.flush();
			os.close();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public static void writeDataToFile(File file, byte[] data)
	{

	}

	public static String getTmpFilePath(final Context aContext)
	{
		String f = "";
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			f = Environment.getExternalStorageDirectory() + "/autonavi/tmp";
		else
			f = aContext.getFilesDir() + "/tmp";
		return f;
	}

	/**
	 * 将byte数据保存到文件
	 * 
	 * @param fileName
	 * @param data
	 */
	public static void writeDatasToFile(String fileName, byte[] data)
	{
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		WriteLock writelock = lock.writeLock();
		writelock.lock();
		try
		{
			if (data == null || data.length == 0)
				return;
			File file = new File(fileName);
			if (file.exists())
			{ // 判断当前文件是否存在
				file.delete(); // 存在就删除
			}
			file.createNewFile();
			OutputStream os = new FileOutputStream(file);
			os.write(data);
			os.flush();
			os.close();
		} catch (Exception ex)
		{
			ex.printStackTrace();
		} finally
		{
			writelock.unlock();
		}
	}

	public static byte[] readFileContents(String fileName)
	{

		try
		{
			File fHandler = new File(fileName);
			if (!fHandler.exists())
			{ // 判断当前文件是否存在
				return null;
			}

			FileInputStream inputStream = new FileInputStream(fHandler);
			byte[] buffer = new byte[1024];
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			int len = 0;
			while ((len = inputStream.read(buffer)) != -1)
			{
				outStream.write(buffer, 0, len);
			}

			outStream.close();
			inputStream.close();
			return outStream.toByteArray();

		} catch (Exception ex)
		{
			ex.printStackTrace();
		}

		return null;

	}

	/**
	 * 在sd卡应用目录下创建.nomedia文件，使得应用中的图片不被系统图库扫描
	 * 
	 * @param filePath
	 */
	public static void createNoMediaFileIfNotExist(String filePath)
	{
		try
		{

			String appFilePath = filePath;
			File file = new File(appFilePath + "/autonavi/.nomedia");
			if (!file.exists())
			{
				file.createNewFile(); // 创建.nomedia文件，使得应用中的图片不被系统图库扫描
			}

			// 解决android系统的bug(如果目录下的图片在创建.nomedia之前已经被图库收录了，创建之后也不会从图库中消失)
			long mtime = file.lastModified();
			Calendar cal = Calendar.getInstance();
			cal.set(1970, 0, 1, 8, 0, 0); // 将时间设置成1970-1-1 8:00:00
			cal.getTimeInMillis();
			long time = cal.getTimeInMillis();
			if (mtime > time)
			{
				file.setLastModified(time); // 将文件时间修改成1970-1-1 8:00:00
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static String readData(String fileName)
	{
		ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		ReadLock readlock = lock.readLock();

		File file = new File(fileName);
		FileInputStream fin;
		String res = "";
		try
		{
			readlock.lock();
			if (file.exists())
			{
				fin = new FileInputStream(file);
				int length = fin.available();
				byte[] buffer = new byte[length];
				fin.read(buffer);
				res = new String(buffer, "UTF-8");
				fin.close();
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			readlock.unlock();
		}

		return res;
	}

	/*----------------Kang, Leo 2014-02-25 start-----------------*/
	/**
	 * 获得SD卡总大小
	 * 
	 * @param context
	 * @return
	 */
	public static String getSDTotalSize(Context context)
	{
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return Formatter.formatFileSize(context, blockSize * totalBlocks);
	}

	/**
	 * 获得sd卡剩余容量，即可用大小
	 * 
	 * @param context
	 * @return
	 */
	public static String getSDAvailableSize(Context context)
	{
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(context, blockSize * availableBlocks);
	}

	/**
	 * 获得机身内存总大小
	 * 
	 * @param context
	 * @return
	 */
	public static String getRomTotalSize(Context context)
	{
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return Formatter.formatFileSize(context, blockSize * totalBlocks);
	}

	/**
	 * 获得机身可用内存
	 * 
	 * @param context
	 * @return
	 */
	public static String getRomAvailableSize(Context context)
	{
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(context, blockSize * availableBlocks);
	}

	/*----------------Kang, Leo 2014-02-25 end-----------------*/

	/**
	 * 递归删除文件和文件夹
	 * 
	 * @param fileName
	 *            要删除的根目录名称
	 * @param isCancel
	 *            是否取消删除
	 * @author yi.kang
	 * @date 2014-03-20 Kang, Leo add
	 */
	public static void recursionDeleteFile(String fileName, boolean isCancel)
	{
		if (isCancel)
			return;
		File file = new File(fileName);
		if (file.isFile())
		{
			file.delete();
			return;
		}
		if (file.isDirectory())
		{
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0)
			{
				file.delete();
				return;
			}
			for (File f : childFile)
			{
				recursionDeleteFile(f.getName(), isCancel);
			}
			file.delete();
		}
	}

	/**
	 * 将指定内容保存到sd卡autonavi目录下指定文件中
	 * 
	 * @param content
	 *            指定内容
	 * @param aFileName
	 *            指定文件名
	 */
	public static void saveLogToFile(String content, String aFileName)
	{

		String fileName = getAppSDCardFileDir();
		if (fileName == null)
			return;
		fileName = fileName + "/" + aFileName;
		try
		{
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			FileWriter writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.write("\r\n-------------------\r\n");
			writer.close();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 将byte数据保存到sd卡autonavi目录下指定文件中
	 * 
	 * @param data
	 *            byte数组
	 * @param afileName
	 *            文件名
	 */
	public static void saveDataToFile(byte[] data, String afileName)
	{
		String fileName = getAppSDCardFileDir();
		if (fileName == null)
			return;
		fileName = fileName + "/" + afileName;
		writeDatasToFile(fileName, data);
	}

	/**
	 * 取高德地图在sd卡中的目录
	 * 
	 * @return
	 */
	public static String getAppSDCardFileDir()
	{
		// 得到存储卡路径
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡
		// 或可存储空间是否存在
		if (sdCardExist)
		{
			File fExternalStorageDirectory = Environment
					.getExternalStorageDirectory();
			sdDir = new File(fExternalStorageDirectory, "autonavi"); // 错误日志存储到SD卡autonavi目录下
			if (!sdDir.exists())
			{
				sdDir.mkdir();
			}
		}
		if (sdDir == null)
			return null;

		return sdDir.toString();
	}

	/**
	 * 文件下载完成之后，将临时文件改成.zip文件名
	 */
	public static void dealTheFileByCompelete(String tempFile, String apkFile)
	{
		// 将.xxx.tmp扩展名改成.xxx
		if (TextUtils.isEmpty(tempFile) || TextUtils.isEmpty(apkFile))
			return;

		File tmpFile = new File(tempFile);
		File zipFile = new File(apkFile);
		if (zipFile.exists())
		{// 文件是否存在
			if (!tmpFile.renameTo(zipFile))
			{
				// 文件重命名成功
			}
		} else
		{
			if (!tmpFile.renameTo(zipFile))
			{
				// 文件重命名成功
			}
		}
	}

	// data/data目录下获取安装apk的权限
	public static int permation(File file)
	{
		try
		{
			Process p = Runtime.getRuntime().exec("chmod 777 " + file);
			int status = p.waitFor();
			return status;
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return -1;
	}

	public static byte[] decodeAssetResData(Context context, String resName)
	{
		// on 1.6 later

		AssetManager assetManager = context.getAssets();

		InputStream is;
		try
		{
			is = assetManager.open(resName);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();

			byte[] bufferByte = new byte[1024];
			int l = -1;
			while ((l = is.read(bufferByte)) > -1)
			{
				bout.write(bufferByte, 0, l);
			}
			byte[] rBytes = bout.toByteArray();
			bout.close();
			is.close();
			return rBytes;

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (OutOfMemoryError e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
