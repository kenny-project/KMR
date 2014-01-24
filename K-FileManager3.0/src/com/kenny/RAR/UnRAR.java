package com.kenny.RAR;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.ZipException;

import android.util.Log;

import com.kenny.file.bean.RARFileBean;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.exception.RarException;
import de.innosystec.unrar.rarfile.FileHeader;

public class UnRAR
{
	public static void unrar(String srcPath, String unrarPath)
			throws RarException, IOException
	{
		unrar(new File(srcPath), unrarPath);
	}

	public static void unrar(File srcFile, String unrarPath)
			throws RarException, IOException
	{
		if (unrarPath.isEmpty())
		{
			unrarPath = srcFile.getParentFile().getPath();
		}
		File dir = new File(unrarPath);
		if (!dir.exists())
		{
			dir.mkdir();
		}
		System.out.println("unrar file to :" + unrarPath);
		FileOutputStream fileOut;
		File file;
		Archive rarfile = new Archive(srcFile);
		FileHeader entry = rarfile.nextFileHeader();
		while (entry != null)
		{
			String key = entry.getFileNameW();
			if (key.length() <= 0)
			{
				key = entry.getFileNameString();
			}
			String entrypath = key.trim();

			entrypath = entrypath.replaceAll("\\\\", "/");

			file = new File(unrarPath + "/" + entrypath);
			System.out.println("unrar entry file :" + file.getPath());
			if (entry.isDirectory())
			{
				file.mkdirs();
			} else
			{
				File parent = file.getParentFile();
				if (parent != null && !parent.exists())
				{
					parent.mkdirs();
				}
				fileOut = new FileOutputStream(file);
				rarfile.extractFile(entry, fileOut);
				fileOut.close();
			}
			entry = rarfile.nextFileHeader();
		}
		rarfile.close();
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
	 * @throws RarException
	 */
	public static HashMap<String, RARFileBean> getRARFileBeans(File srcFile)
			throws ZipException, IOException, RarException
	{
		HashMap<String, RARFileBean> list = new HashMap<String, RARFileBean>();
		Archive rarfile = new Archive(srcFile);
		FileHeader entry;

		while ((entry = rarfile.nextFileHeader()) != null)
		{
			String key = entry.getFileNameW();
			if (key.length() <= 0)
			{
				key = entry.getFileNameString();
			}
			int pos = key.indexOf("\\");

			if (pos == -1 || pos + 1 == key.length())
			{ // 根目录
				if (pos != -1)
				{
					key = key.substring(0, pos);
				}
				if (!list.containsKey(key))
				{
					RARFileBean temp = new RARFileBean(key, key, list);
					temp.setDirectory(entry.isDirectory());
					temp.setLength(Long.valueOf(entry.getDataSize()));
					list.put(key, temp);
				}
			} else
			{ // 二级及以上目录
				String first = key.substring(0, pos);
				String end = key.substring(pos + 1);
				RARFileBean temp = null;
				if (!list.containsKey(first))
				{
					temp = new RARFileBean(first, first, list);
					temp.setDirectory(true);
					list.put(first, temp);
				} else
				{
					temp = list.get(first);
				}
				if (temp == null)
				{
					rarfile.close();
					throw new IOException("未找到文件");
				}
				temp.AddItem(end, entry);
			}

			// list.put(paths[0], temp);
		}
		rarfile.close();
		return list;
	}

	public static boolean UpSelectedFile(String zipFile, String targetDir,
			String nameContains)
	{
		int BUFFER = 4096;
		try
		{
			File dir = new File(targetDir);
			if (dir.exists())
			{// is empty
				dir.mkdirs();
			}
			BufferedOutputStream dest = null;
			Archive rarfile = new Archive(new File(zipFile));
			FileHeader entry;

			while ((entry = rarfile.nextFileHeader()) != null)
			{
				String strEntry = entry.getFileNameW();
				if (strEntry.length() <= 0)
				{
					strEntry = entry.getFileNameString();
				}

				try
				{
					if (strEntry.contains(nameContains))
					{
						Log.i("Unzip: ", "=" + entry);
						int count;
						byte data[] = new byte[BUFFER];

						File entryFile = new File(targetDir + nameContains);
						File entryDir = new File(targetDir);
						if (!entryFile.exists())
						{
							entryFile.delete();
						}

						if (!entryDir.exists())
						{
							entryDir.mkdirs();
						}

						FileOutputStream fos = new FileOutputStream(entryFile);
						dest = new BufferedOutputStream(fos, BUFFER);
						rarfile.extractFile(entry, dest);
						// while ((count = entry.g.read(data, 0, BUFFER)) != -1)
						// {
						// dest.write(data, 0, count);
						// }
						dest.flush();
						dest.close();
						rarfile.close();
						return true;
					}
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			rarfile.close();
		} catch (Exception cwj)
		{
			cwj.printStackTrace();
		}
		return false;
	}
}
