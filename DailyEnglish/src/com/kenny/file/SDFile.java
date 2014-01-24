package com.kenny.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import com.kenny.util.Const;
import com.kenny.util.T;

public class SDFile {
	public final static String defpath = Const.NET_DIRECTORY;
	public final static String filePath = defpath + "file/";
	public final static String logoPath = defpath + "logo";
	public final static String webviewlogo = defpath + "webviewlogo";
	public final static String imgPath = defpath + "image/";

	// 检查SD卡是否存在 true:存在. false:不存在
	public static boolean checkSDCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	private static String DESCoderInit() {
		try {
			return DESCoder.initKey("kennyamhy530ewrqjcxvjkleuiopanl");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 检查SD卡是否有该文件
	 * 
	 * @param FileName
	 * @return
	 */
	// public static boolean CheckSDFile(String FileName)
	// {
	// if (!checkSDCard())
	// return false;
	// String path = defpath;
	// path += FileName;
	// File file = new File(path);
	// if (file.exists())
	// {
	// if (file.length() > 0)
	// {
	// return true;
	// } else
	// {
	// return false;
	// }
	// } else
	// {
	// return false;
	// }
	// }

	/**
	 * 写文件到手机内存,未加密
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

	// 写文件到ＳＤ卡
	public static boolean WriteSDByteFile(byte[] Data, String FileName) {
		try {
			if (!checkSDCard())
				return false;
			String path = filePath;
			File file = new File(path);
			file.mkdirs();
			path = path + FileName;
			file = new File(path);
			if (file.exists()) {
				if (file.length() > 0) {
					file.delete();
				}
			}
			file.createNewFile();
			// if (key == null)
			// {
			// key = DESCoderInit();
			// }
			// if (key == null)
			// {
			// return false;
			// }
			// byte[] enData = DESCoder.encrypt(Data, key);
			copyFile(Data, path);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * 读SD卡文件
	 * 
	 * @param FileName
	 * @return
	 */
	public static String ReadSDFile(String FileName) {
		try {
			if (!checkSDCard())
				return null;
			String path = filePath + FileName;
			File file = new File(path);
			if (file.exists()) {
				FileInputStream fs = new FileInputStream(file);
				int bufferLen = (int) file.length();
				byte[] Data = new byte[bufferLen];
				fs.read(Data, 0, bufferLen);
				String result = new String(Data);
				return result;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	// 写文件到ＳＤ卡
	public static boolean WriteSDFile(String Data, String FileName) {
		try {
			if (!checkSDCard())
				return false;
			String path = filePath;
			File file = new File(path);
			file.mkdirs();
			path = path + FileName;
			file = new File(path);
			if (file.exists()) {
				if (file.length() > 0) {
					file.delete();
				}
			}
			file.createNewFile();
			byte[] enData = Data.getBytes();
			copyFile(enData, path);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
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
			// if (key == null) {
			// key = DESCoderInit();
			// }
			// if (key == null) {
			// return false;
			// }
			// byte[] enData = DESCoder.encrypt(Data.getBytes(), key);
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
	 * 写文件到手机内存,未加密
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
	public static boolean DeleteRAMFile(Context context, String FileName) {
		try {
			String path = "/data/data/" + context.getPackageName() + "/files/"
					+ FileName;
			File file = new File(path);
			file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 读SD卡文件
	 * 
	 * @param FileName
	 * @return
	 */
	public static byte[] ReadSDByteFile(String FileName) {
		try {
			if (!checkSDCard())
				return null;
			String path = filePath + FileName;
			File file = new File(path);
			if (file.exists()) {
				FileInputStream fs = new FileInputStream(file);
				int bufferLen = (int) file.length();
				byte[] Data = new byte[bufferLen];
				fs.read(Data, 0, bufferLen);
				// String result=buffer.toString();
				return Data;
				// if (key == null)
				// {
				// key = DESCoderInit();
				// }
				// if (key != null)
				// {
				// byte[] deData = DESCoder.decrypt(Data, key);
				// Data.clone();
				// return deData;
				// }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
	public static void copyFile(byte[] inStream, String newPath)
			throws IOException {
		FileOutputStream fs = new FileOutputStream(newPath);
		fs.write(inStream, 0, inStream.length);
		inStream.clone();
	}

	// 初始化运行程序所需要的文件
	public static Drawable ReadImageFile(String url) {
		if (!T.checkSDCard())
			return null;
		String path = imgPath;
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

	// 初始化运行程序所需要的文件
	public static Drawable ReadImageFullFile(String url) {
		if (!T.checkSDCard())
			return null;
		String path = url;
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

	// 初始化运行程序所需要的文件
	public static boolean WriteImageFile(String url, Bitmap bitmap) {
		if (!T.checkSDCard())
			return false;

		String path = imgPath;
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
			// Finally compress the bitmap, saving to the file previously
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

	// 初始化运行程序所需要的文件
	public static Drawable ReadSDLogoFile(Context context, String url) {
		if (!T.checkSDCard())
			return null;
		String path = logoPath;
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

	// 初始化运行程序所需要的文件
	public static boolean getSDWebViewLogoFileExits(String url) {
		String path = webviewlogo;
		String szFileName = URL2FileName(url);
		path += szFileName;
		File file = new File(path);
		return file.exists();
	}

	// 初始化运行程序所需要的文件
	public static String getSDLogoFilePath(String url) {
		String path = webviewlogo;
		String szFileName = URL2FileName(url);
		path += szFileName;
		return path;
	}

	// 初始化运行程序所需要的文件
	public static boolean WriteSDWebViewLogoFile(Context context, String url,
			Bitmap bitmap) {
		if (!T.checkSDCard())
			return false;
		String path = webviewlogo;
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

	// 初始化运行程序所需要的文件
	public static boolean WriteSDLogoFile(Context context, String url,
			Bitmap bitmap) {
		if (!T.checkSDCard())
			return false;
		String path = logoPath;
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

	// 初始化运行程序所需要的文件
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

	// 初始化运行程序所需要的文件
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
