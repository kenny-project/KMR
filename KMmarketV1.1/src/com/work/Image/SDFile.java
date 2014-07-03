package com.work.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;

public class SDFile {
	public static final String SDCard = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath();
	public final static String szAppPath = SDCard + File.separator
			+ "baifen/";
	// private static String key = null;
	private final static String defpath = szAppPath;
	private final static String filePath = defpath + "list/";
	private final static String logoPath = defpath + "logo/";
	private final static String kuaipanPath = defpath + "Kuaipan/";
	private final static String imgPath = defpath + "image/";

	// ?SD卡是否存?true:存在
	public static boolean checkSDCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * ?SD卡是否有该文?
	 * 
	 * @param FileName
	 * @return
	 */
	public static boolean CheckSDFile(String FileName) {
		if (!checkSDCard())
			return false;
		String path = "" + defpath;
		path += FileName;
		File file = new File(path);
		if (file.exists()) {
			if (file.length() > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 写文件到手机内存,未加?
	 * 
	 * @param context
	 * @param FileName
	 * @return
	 * @throws Exception
	 */
	public static boolean CheckRAMFile(Context context, String FileName) {
		String path = "/data/data/" + context.getPackageName() + "/files/"
				+ FileName;
		File file = new File(path);
		if (file.exists()) {
			if (file.length() > 0) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	// 将文件写到指定目?
	/**
	 * 拷贝文件到指定目?备份文件
	 * 
	 * @param srcName
	 * @param filePath
	 * @param FileName
	 * @return
	 */
	public static int BackAppFile(String srcfilePath, String filePath,
			String FileName) {
		try {
			if (!checkSDCard())
				return -1;
			if (FileName == null || FileName.length() <= 0) {
				FileName = srcfilePath
						.substring(srcfilePath.lastIndexOf('/') + 1);
			} else {
				FileName = FileName + ".apk";
			}

			String path = filePath;
			new File(path).mkdirs();

			path = path + FileName;
			File file = new File(path);
			if (file.exists()) {
				if (file.length() == new File(srcfilePath).length()) {
					return 2;
				} else {
					file.delete();
				}
			}
			file.createNewFile();
			copyStreamFile(srcfilePath, path);
			return 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -2;
		}
	}

	// 将文件写到指定目?
	/**
	 * 拷贝文件到指定目?备份文件
	 * 
	 * @param srcName
	 * @param filePath
	 * @param FileName
	 * @return
	 */
	public static int BackUpFile(String srcfilePath, String filePath) {
		try {
			if (!checkSDCard())
				return -1;

			String FileName = srcfilePath.substring(srcfilePath
					.lastIndexOf('/') + 1);
			String path = filePath;
			new File(path).mkdirs();

			path = path + FileName;
			File file = new File(path);
			if (file.exists()) {
				if (file.length() > 0) {
					file.delete();
				}
			}
			file.createNewFile();
			copyStreamFile(srcfilePath, path);
			return 1;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -2;
		}
	}

	/**
	 * 拷贝文件到指定目?备份文件 outPath,inFile -4:创建失败
	 * 
	 * @param srcName
	 * @param filePath
	 * @param FileName
	 * @return
	 */
	// public static int CopyFile(String outPath, File inFile)
	// {
	// try
	// {
	// File outFile = new File(outPath);
	// if (outFile.exists())
	// {
	// // if (outFile.length() > 0)
	// // {
	// outFile.delete();
	// // }
	// }
	// if (outFile.createNewFile())
	// {
	// return copyStreamFile(inFile, outFile);
	// }
	// else
	// {
	// return -4;
	// }
	// }
	// catch (Exception e)
	// {
	// e.printStackTrace();
	// return -3;
	// }
	// }
	/**
	 * 拷贝文件到指定目?备份文件 outPath,inFile -4:创建失败 inFile:源文?outFile:新文?
	 * 
	 * @param srcName
	 * @param filePath
	 * @param FileName
	 * @return
	 */
	public static int CopyFile(File sourceFile, File newFile) {
		try {
			if (newFile.exists()) {
				newFile.delete();
			}
			if (newFile.createNewFile()) {
				return copyStreamFile(sourceFile, newFile);
			} else {
				return -4;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -3;
		}
	}

	/**
	 * 复制文件的方?inFile:源文?outFile:新文?return 1:成功
	 * 
	 * @throws IOException
	 */
	public static int copyStreamFile(File sourceFile, File newFile)
			throws IOException {
		FileInputStream fis = new FileInputStream(sourceFile);
		FileOutputStream fos = new FileOutputStream(newFile);
		byte[] buffer = new byte[8000];
		int len = 0;
		do {
			len = fis.read(buffer);
			if (len > 0) {
				fos.write(buffer, 0, len);
			}
		} while (len != -1);
		if (fis != null) {
			fis.close();
		}
		if (fos != null) {
			fos.close();
		}
		return 1;
	}

	/** 复制文件的方? */
	public static void copyStreamFile(String read, String write) {
		try {
			FileInputStream fis = new FileInputStream(read);
			FileOutputStream fos = new FileOutputStream(write);
			byte[] buffer = new byte[8000];
			int len = 0;
			do {
				len = fis.read(buffer);
				if (len != -1) {
					fos.write(buffer, 0, len);
				}

			} while (len != -1);
			if (fis != null) {
				fis.close();
			}
			if (fos != null) {
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读SD卡文
	 * 
	 * @param FileName
	 * @return
	 */
	public static String ReadSDFile(String FileName) {
		try {
			if (!checkSDCard())
				return null;
			String path = filePath + FileName + ".km";
			File file = new File(path);
			if (file.exists()) {
				FileInputStream fs = new FileInputStream(file);
				int bufferLen = (int) file.length();
				byte[] Data = new byte[bufferLen];
				fs.read(Data, 0, bufferLen);
				// String result=buffer.toString();
				return new String(Data);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static boolean WriteSDFile(String value, String FileName) 
	{
		try {
			if (!checkSDCard())
				return false;
			byte[] Data=value.getBytes();
			String path = filePath + FileName + ".km";
			File file = new File(filePath);
			file.mkdirs();
			file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			copyFile(Data, path);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
		public static boolean WriteSDFile(byte[] Data, String FileName) 
		{
			try {
				if (!checkSDCard())
					return false;
				String path = filePath + FileName + ".km";
				File file = new File(path);
				file.mkdirs();
				path = path + FileName;
				file = new File(path);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				copyFile(Data, path);
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
	// 写文件到ＳＤ?
	public static boolean WriteSDFile(String path, byte[] Data, String FileName) {
		try {
			if (!checkSDCard())
				return false;
			File file = new File(path);
			file.mkdirs();
			path = path + FileName;
			file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			copyFile(Data, path);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	// 写文件到ＳＤ?
	public static boolean WriteSDFile(String path, String Data, String FileName) {
		return WriteSDFile(path, Data.getBytes(), FileName);
	}

	// 写文件到手机内存
	public static boolean WriteRAMFile(Context context, String Data,
			String FileName) {
		try {
			String path = "/data/data/" + context.getPackageName() + "/files/"
					+ FileName;
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			copyFile(Data.getBytes(), path);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	// 写文件到手机内存
	public static boolean WriteRAMFile(Context context, String Data,
			String FileName, int mode) {
		try {
			String path = "/data/data/" + context.getPackageName() + "/files/"
					+ FileName;
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			copyFile(Data.getBytes(), path);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 写文件到手机内存,未加?
	 * 
	 * @param context
	 * @param FileName
	 * @return
	 * @throws Exception
	 */
	public static String ReadRAMFile(Context context, String FileName, int mode)
			throws Exception {
		String path = "/data/data/" + context.getPackageName() + "/files/"
				+ FileName;
		File file = new File(path);
		file = new File(path);
		if (file.exists()) {
			FileInputStream fs = new FileInputStream(file);
			int bufferLen = (int) file.length();
			byte[] Data = new byte[bufferLen];
			fs.read(Data, 0, bufferLen);
			return new String(Data);
		}
		return null;
	}

	/**
	 * 写文件到手机内存
	 * 
	 * @param context
	 * @param FileName
	 * @return
	 * @throws Exception
	 */
	public static String ReadRAMFile(Context context, String FileName)
			throws Exception {
		String path = "/data/data/" + context.getPackageName() + "/files/"
				+ FileName;
		File file = new File(path);
		file = new File(path);
		if (file.exists()) {
			FileInputStream fs = new FileInputStream(file);
			int bufferLen = (int) file.length();
			byte[] Data = new byte[bufferLen];
			fs.read(Data, 0, bufferLen);
			// String result=buffer.toString();
			return new String(Data);
		}
		return null;
	}


	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路?如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路?如：f:/fqf.txt
	 * @return boolean
	 * @throws IOException
	 */
	public static void copyFile(byte[] inStream, String newPath)
			throws IOException {
		FileOutputStream fs = new FileOutputStream(newPath);
		fs.write(inStream, 0, inStream.length);
		inStream.clone();
	}

	// 初始化运行程序所?的文?
	public static Drawable ReadImageFile(String url) {
		if (!T.checkSDCard())
			return null;
		String sdroot = android.os.Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String path = "" + sdroot + imgPath;
		String szFileName = URL2FileName(url);
		File file;
		try {
			new File(path).mkdirs();
			path += szFileName;
			file = new File(path);
			if (file.exists()) {
				return Drawable.createFromPath(path);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			file = new File(path);
			file.delete();
		}
		return null;
	}

	// 初始化运行程序所?的文?
	public static boolean WriteImageFile(String url, Bitmap bitmap) {
		if (!T.checkSDCard())
			return false;
		String sdroot = android.os.Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String path = "" + sdroot + imgPath;
		String szFileName = URL2FileName(url);
		File file;
		try {
			new File(path).mkdirs();
			path += szFileName;
			file = new File(path);
			if (file.exists()) {
				// return false;
				file.delete();
			}
			file.createNewFile();
			// Finally compress the bitmap, saving to the file
			// previously
			// created
			bitmap.compress(CompressFormat.PNG, 100, new FileOutputStream(file));
			// copyFile(is,path);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			file = new File(path);
			file.delete();
			return false;
		}
	}

	public static String URL2FileName(String url) {
		String[] urlData = url.split("/");

		return urlData[urlData.length - 1];
	}

	// 读取文件生成Drawable
	public static Drawable ReadImageFileToDrawable(Context context, String path) {

		File file;
		try {
			file = new File(path);
			if (file.exists()) {
				return Drawable.createFromPath(path);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取?图片不能超过该?
	 * 
	 * @param context
	 * @param path
	 * @param nWidth
	 * @return
	 */
	public static Bitmap ReadFileToMaxBitmap(Context context, String path,
			int nWidth) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			options.inJustDecodeBounds = true;
			// options.outWidth=40;
			BitmapFactory.decodeFile(path, options);
			if (nWidth < options.outWidth) {
				if (options.outHeight > options.outWidth) {
					// nWidth = options.outHeight / nWidth;
					if (options.outHeight % nWidth == 0) {
						nWidth = options.outHeight / nWidth;
					} else {
						nWidth = options.outHeight / nWidth + 1;
					}
				} else {
					if (options.outWidth % nWidth == 0) {
						nWidth = options.outWidth / nWidth;
					} else {
						nWidth = options.outWidth / nWidth + 1;
					}
				}
				options.inSampleSize = nWidth;
			}
			options.inJustDecodeBounds = false;
			Bitmap bmp = BitmapFactory.decodeFile(path, options);
			return bmp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取图片文件,并生成相应缩放比例的大小
	 */
	public static Bitmap ReadFileToBitmap(Context context, String path,
			int nWidth) {
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			options.inJustDecodeBounds = true;
			// options.outWidth=40;
			BitmapFactory.decodeFile(path, options);
			if (nWidth > 0) {
				if (options.outHeight > options.outWidth) {
					nWidth = options.outHeight / nWidth;
				} else {
					nWidth = options.outWidth / nWidth;
				}
				options.inSampleSize = nWidth;
			}
			options.inJustDecodeBounds = false;
			Bitmap bmp = BitmapFactory.decodeFile(path, options);
			return bmp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 初始化运行程序所?的文?
	public static boolean WriteSDLogoFile(Context context, String url,
			Bitmap bitmap) {
		if (!T.checkSDCard())
			return false;
		String sdroot = android.os.Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String path = "" + sdroot + logoPath;
		String szFileName = URL2FileName(url);
		File file;
		try {
			new File(path).mkdirs();
			path += szFileName;
			file = new File(path);
			if (file.exists()) {
				// return false;
				file.delete();
			}
			file.createNewFile();
			bitmap.compress(CompressFormat.PNG, 100, new FileOutputStream(file));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			file = new File(path);
			file.delete();
			return false;
		}
	}

	// 初始化运行程序所?的文?
	public static Drawable ReadRAMImageFile(Context context, String url) {
		String szFileName = URL2FileName(url);
		String path = "/data/data/" + context.getPackageName() + "/files/"
				+ szFileName;
		File file;
		try {
			file = new File(path);
			if (file.exists()) {
				return Drawable.createFromPath(path);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			file = new File(path);
			file.delete();
		}
		return null;
	}

	// 初始化运行程序所?的文?
	public static boolean WriteRAMImageFile(Context context, String url,
			Bitmap bitmap) {
		String path = "/data/data/" + context.getPackageName() + "/files/";
		String szFileName = URL2FileName(url);
		File file;
		try {
			path += szFileName;
			file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			bitmap.compress(CompressFormat.PNG, 100, new FileOutputStream(file));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			file = new File(path);
			file.delete();
			return false;
		}
	}
}
