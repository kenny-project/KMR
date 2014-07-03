package com.work.market.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import android.util.Log;

/**
 * ZIP文件操作
 * 
 * @author WangMinghui
 * 
 */
public class ZIP {
	private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte

	/**
	 * 释放文件
	 * 
	 * @param zipFile
	 * @param targetDir
	 */
	public static void Unzip(String zipFile, String targetDir) {
		int BUFFER = 4096; // 这里缓冲区我们使用4KB，
		String strEntry; // 保存每个zip的条目名称

		try {
			BufferedOutputStream dest = null; // 缓冲输出流
			FileInputStream fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry; // 每个zip条目的实例
			while ((entry = zis.getNextEntry()) != null) {
				try {
					Log.i("Unzip: ", "=" + entry);
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

	public static byte[] UnZipByte(String zipFile, String path) {
		int BUFFER = 4096;
		try {
			FileInputStream fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry; // 每个zip条目的实例
			byte data[] = new byte[BUFFER];
			while ((entry = zis.getNextEntry()) != null) {
				try {
					if (path.equals(entry.getName())) {
						int count;
						int size = (int) entry.getSize(); // 这里缓冲区我们使用4KB，
						//
						// strEntry = entry.getName();
						// count = zis.read(data, 0,
						// BUFFER);
						ByteArrayOutputStream dest = new ByteArrayOutputStream(
								size);
						while ((count = zis.read(data, 0, BUFFER)) != -1) {
							dest.write(data, 0, count);
						}
						dest.flush();
						dest.close();

						return dest.toByteArray();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			zis.close();
		} catch (Exception cwj) {
			cwj.printStackTrace();
		}
		return null;
	}

	/**
	 * 批量压缩文件（夹）
	 * 
	 * @param resFileList
	 *            要压缩的文件（夹）列表
	 * @param zipFile
	 *            生成的压缩文件
	 * @throws IOException
	 *             当压缩过程出错时抛出
	 */
	public static void zipFiles(Collection<File> resFileList, File zipFile)
			throws IOException {
		ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(
				new FileOutputStream(zipFile), BUFF_SIZE));
		for (File resFile : resFileList) {
			zipFile(resFile, zipout, "");
		}
		zipout.close();
	}

	/**
	 * 批量压缩文件（夹）
	 * 
	 * @param resFileList
	 *            要压缩的文件（夹）列表
	 * @param zipFile
	 *            生成的压缩文件
	 * @param comment
	 *            压缩文件的注释
	 * @throws IOException
	 *             当压缩过程出错时抛出
	 */
	public static void zipFiles(Collection<File> resFileList, File zipFile,
			String comment) throws IOException {
		ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(
				new FileOutputStream(zipFile), BUFF_SIZE));
		for (File resFile : resFileList) {
			zipFile(resFile, zipout, "");
		}
		zipout.setComment(comment);
		zipout.close();
	}

	/**
	 * 解压缩一个文件
	 * 
	 * @param zipFile
	 *            压缩文件
	 * @param folderPath
	 *            解压缩的目标目录
	 * @throws IOException
	 *             当解压缩过程出错时抛出
	 */
	public static List<String> upZipFile(File zipFile,String apkPath, String folderPath)
			throws ZipException, IOException {
		File desDir = new File(folderPath);
		if (!desDir.exists()) {
			desDir.mkdirs();
		}
		File apkDir = new File(apkPath);
		if (!apkDir.exists()) {
			apkDir.mkdirs();
		}
		List<String> apkList=new ArrayList<String>();
		ZipFile zf = new ZipFile(zipFile);
		for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
			ZipEntry entry = ((ZipEntry) entries.nextElement());
			if(entry.isDirectory())
			{
				continue;
			}
			InputStream in = zf.getInputStream(entry);
			String str = folderPath + File.separator + entry.getName();
			File desFile = new File(str);
			// str = new String(str.getBytes("8859_1"), "GB2312");
			if(entry.getName().substring(entry.getName().length()-3).lastIndexOf("apk")==0)
			{
				desFile=new File(apkPath+ File.separator + entry.getName());
				apkList.add(desFile.getAbsolutePath());
			}
			
			if (!desFile.exists()) 
			{
				File fileParentDir = desFile.getParentFile();
				if (!fileParentDir.exists()) {
					fileParentDir.mkdirs();
				}
				try
				{
				desFile.createNewFile();
				}
				catch(Exception e)
				{
					e.printStackTrace();
					Log.v("wmh"," :zippath="+desFile.getAbsolutePath());
				}
			}

			OutputStream out = new FileOutputStream(desFile);
			byte buffer[] = new byte[BUFF_SIZE];
			int realLength;
			while ((realLength = in.read(buffer)) > 0) {
				out.write(buffer, 0, realLength);
			}
			in.close();
			out.close();
		}
		return apkList;
	}

	public static boolean upZipSelectedFile(String zipFile, String targetDir,
			String nameContains) {
		int BUFFER = 4096;
		String strEntry;
		try {
			File dir = new File(targetDir);
			if (dir.exists()) {// is empty
				dir.mkdirs();
			}
			BufferedOutputStream dest = null;
			FileInputStream fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry; //
			while ((entry = zis.getNextEntry()) != null) {
				try {
					if (entry.getName().contains(nameContains)) {
						Log.i("Unzip: ", "=" + entry);
						int count;
						byte data[] = new byte[BUFFER];
						strEntry = entry.getName();

						File entryFile = new File(targetDir + nameContains);
						File entryDir = new File(targetDir);
						if (!entryFile.exists()) {
							entryFile.delete();
						}

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
						zis.close();
						return true;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			zis.close();
		} catch (Exception cwj) {
			cwj.printStackTrace();
		}
		return false;
	}

	/**
	 * 解压文件名包含传入文字的文件
	 * 
	 * @param zipFile
	 *            压缩文件
	 * @param folderPath
	 *            目标文件夹
	 * @param nameContains
	 *            传入的文件匹配名
	 * @throws ZipException
	 *             压缩格式有误时抛出
	 * @throws IOException
	 *             IO错误时抛出
	 */
	// public static File upZipSelectedFile(String zipFile, String
	// folderPath,
	// String nameContains) throws ZipException, IOException
	// {
	// // ArrayList<File> fileList = new ArrayList<File>();
	//
	// File desDir = new File(folderPath);
	// if (!desDir.exists())
	// {
	// desDir.mkdir();
	// }
	// // ZipFile zf = new ZipFile(zipFile);
	// // for (Enumeration<?> entries = zf.entries(); entries
	// // .hasMoreElements();)
	// FileInputStream fis = new FileInputStream(zipFile);
	// ZipInputStream zis = new ZipInputStream(
	// new BufferedInputStream(fis));
	// ZipEntry entry; // 每个zip条目的实例
	// while ((entry = zis.getNextEntry()) != null)
	// {
	// // ZipEntry entry = ((ZipEntry) entries.nextElement());
	// if (entry.getName().contains(nameContains))
	// {
	// entry.InputStream in = zf.getInputStream(entry);
	// String str = folderPath + File.separator
	// + nameContains;
	// // str = new String(str.getBytes("8859_1"),
	// // "GB2312");
	// // str.getBytes("GB2312"),"8859_1" 输出
	// // str.getBytes("8859_1"),"GB2312" 输入
	// File desFile = new File(str);
	// if (!desFile.exists())
	// {
	// File fileParentDir = desFile
	// .getParentFile();
	// if (!fileParentDir.exists())
	// {
	// fileParentDir.mkdirs();
	// }
	// desFile.createNewFile();
	// }
	// else
	// {
	// desFile.delete();
	// desFile.createNewFile();
	// }
	// OutputStream out = new FileOutputStream(desFile);
	// byte buffer[] = new byte[BUFF_SIZE];
	// int realLength;
	// while ((realLength = in.read(buffer)) > 0)
	// {
	// out.write(buffer, 0, realLength);
	// }
	// in.close();
	// out.close();
	// fileList.add(desFile);
	// }
	// }
	// return fileList;
	// }

	/**
	 * 获得压缩文件内文件列表
	 * 
	 * @param zipFile
	 *            压缩文件
	 * @return 压缩文件内文件名称
	 * @throws ZipException
	 *             压缩文件格式有误时抛出
	 * @throws IOException
	 *             当解压缩过程出错时抛出
	 */
	public static Vector<String> getEntriesNames(File zipFile)
			throws ZipException, IOException {
		Vector<String> entryNames = new Vector<String>();
		Enumeration<?> entries = getEntriesEnumeration(zipFile);
		while (entries.hasMoreElements()) {
			ZipEntry entry = ((ZipEntry) entries.nextElement());
			entryNames.add(getEntryName(entry));
		}
		return entryNames;
	}

	/**
	 * 获得压缩文件内文件列表
	 * 
	 * @param zipFile
	 *            压缩文件
	 * @return 压缩文件内文件名称
	 * @throws ZipException
	 *             压缩文件格式有误时抛出
	 * @throws IOException
	 *             当解压缩过程出错时抛出
	 */
	// public static HashMap<String, ZipFileBean> getZipFileBeans(File zipFile)
	// throws ZipException, IOException
	// {
	// HashMap<String, ZipFileBean> list = new HashMap<String, ZipFileBean>();
	// FileInputStream fis = new FileInputStream(zipFile);
	// ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
	// ZipEntry entry; // 每个zip条目的实例
	// while ((entry = zis.getNextEntry()) != null)
	// {
	//
	// String key = entry.getName();
	// int pos = key.indexOf("/");
	//
	// if (pos == -1 || pos + 1 == key.length())
	// {
	// if (pos != -1)
	// {
	// key = key.substring(0, pos);
	// }
	//
	// ZipFileBean temp = new ZipFileBean(key, key, list);
	// temp.setDirectory(entry.isDirectory());
	// temp.setLength(entry.getSize());
	// list.put(key, temp);
	// }
	// else
	// {
	// String first = key.substring(0, pos);
	// String end = key.substring(pos + 1);
	// if (!list.containsKey(first))
	// {
	// ZipFileBean temp = new ZipFileBean(first, first, list);
	// temp.setDirectory(true);
	// list.put(first, temp);
	// temp.AddItem(end, entry);
	// }
	// else
	// {
	// list.get(first).AddItem(end, entry);
	// }
	// }
	//
	// // list.put(paths[0], temp);
	// }
	// return list;
	// }

	/**
	 * 获得压缩文件内压缩文件对象以取得其属性
	 * 
	 * @param zipFile
	 *            压缩文件
	 * @return 返回一个压缩文件列表
	 * @throws ZipException
	 *             压缩文件格式有误时抛出
	 * @throws IOException
	 *             IO操作有误时抛出
	 */
	public static Enumeration<?> getEntriesEnumeration(File zipFile)
			throws ZipException, IOException {
		ZipFile zf = new ZipFile(zipFile);
		return zf.entries();

	}

	/**
	 * 取得压缩文件对象的注释
	 * 
	 * @param entry
	 *            压缩文件对象
	 * @return 压缩文件对象的注释
	 * @throws UnsupportedEncodingException
	 */
	public static String getEntryComment(ZipEntry entry)
			throws UnsupportedEncodingException {
		return entry.getComment();
		// return new String(entry.getComment().getBytes("GB2312"),
		// "8859_1");
	}

	/**
	 * 取得压缩文件对象的名称
	 * 
	 * @param entry
	 *            压缩文件对象
	 * @return 压缩文件对象的名称
	 * @throws UnsupportedEncodingException
	 */
	public static String getEntryName(ZipEntry entry)
			throws UnsupportedEncodingException {
		return entry.getName();
	}

	/**
	 * 压缩文件
	 * 
	 * @param resFileList
	 *            要压缩的文件（夹）列表
	 * @param zipFile
	 *            生成的压缩文件
	 * @param comment
	 *            压缩文件的注释
	 * @throws IOException
	 *             当压缩过程出错时抛出
	 */
	public static void zipFile(File resFile, File zipFile, String comment)
			throws IOException {
		ZipOutputStream zipout = new ZipOutputStream(new BufferedOutputStream(
				new FileOutputStream(zipFile), BUFF_SIZE));
		zipFile(resFile, zipout, "");
		zipout.setComment(comment);
		zipout.close();
	}

	/**
	 * 压缩文件
	 * 
	 * @param resFile
	 *            需要压缩的文件（夹）
	 * @param zipout
	 *            压缩的目的文件
	 * @param rootpath
	 *            压缩的文件路径
	 * @throws FileNotFoundException
	 *             找不到文件时抛出
	 * @throws IOException
	 *             当压缩过程出错时抛出
	 */
	private static void zipFile(File resFile, ZipOutputStream zipout,
			String rootpath) throws FileNotFoundException, IOException {
		rootpath = rootpath
				+ (rootpath.trim().length() == 0 ? "" : File.separator)
				+ resFile.getName();
		// rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");
		if (resFile.isDirectory()) {
			File[] fileList = resFile.listFiles();

			if (fileList.length <= 0) {
				zipout.putNextEntry(new ZipEntry(rootpath + File.separator));
				zipout.flush();
				zipout.closeEntry();
			} else {
				for (File file : fileList) {
					zipFile(file, zipout, rootpath);
				}
			}
		} else {
			byte buffer[] = new byte[BUFF_SIZE];
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(resFile), BUFF_SIZE);
			zipout.putNextEntry(new ZipEntry(rootpath));
			int realLength;
			while ((realLength = in.read(buffer)) != -1) {
				zipout.write(buffer, 0, realLength);
			}
			in.close();
			zipout.flush();
			zipout.closeEntry();
		}
	}

}
