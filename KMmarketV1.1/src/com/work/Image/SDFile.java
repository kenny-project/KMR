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

	// ?SD���Ƿ��?true:����
	public static boolean checkSDCard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * ?SD���Ƿ��и���?
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
	 * д�ļ����ֻ��ڴ�,δ��?
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

	// ���ļ�д��ָ��Ŀ?
	/**
	 * �����ļ���ָ��Ŀ?�����ļ�
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

	// ���ļ�д��ָ��Ŀ?
	/**
	 * �����ļ���ָ��Ŀ?�����ļ�
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
	 * �����ļ���ָ��Ŀ?�����ļ� outPath,inFile -4:����ʧ��
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
	 * �����ļ���ָ��Ŀ?�����ļ� outPath,inFile -4:����ʧ�� inFile:Դ��?outFile:����?
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
	 * �����ļ��ķ�?inFile:Դ��?outFile:����?return 1:�ɹ�
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

	/** �����ļ��ķ�? */
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
	 * ��SD����
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
	// д�ļ����ӣ�?
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

	// д�ļ����ӣ�?
	public static boolean WriteSDFile(String path, String Data, String FileName) {
		return WriteSDFile(path, Data.getBytes(), FileName);
	}

	// д�ļ����ֻ��ڴ�
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

	// д�ļ����ֻ��ڴ�
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
	 * д�ļ����ֻ��ڴ�,δ��?
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
	 * д�ļ����ֻ��ڴ�
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
	 * ���Ƶ����ļ�
	 * 
	 * @param oldPath
	 *            String ԭ�ļ�·?�磺c:/fqf.txt
	 * @param newPath
	 *            String ���ƺ�·?�磺f:/fqf.txt
	 * @return boolean
	 * @throws IOException
	 */
	public static void copyFile(byte[] inStream, String newPath)
			throws IOException {
		FileOutputStream fs = new FileOutputStream(newPath);
		fs.write(inStream, 0, inStream.length);
		inStream.clone();
	}

	// ��ʼ�����г�����?����?
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

	// ��ʼ�����г�����?����?
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

	// ��ȡ�ļ�����Drawable
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
	 * ��ȡ?ͼƬ���ܳ�����?
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
	 * ��ȡͼƬ�ļ�,��������Ӧ���ű����Ĵ�С
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

	// ��ʼ�����г�����?����?
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

	// ��ʼ�����г�����?����?
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

	// ��ʼ�����г�����?����?
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
