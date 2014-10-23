package com.kenny.file.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class ChangeCharset
{
	/** 7位ASCII字符，也叫作ISO646-US、Unicode字符集的基本拉丁块 */
	public static final String US_ASCII = " US-ASCII ";
	/** ISO拉丁字母表 No.1，也叫做ISO-LATIN-1 */
	public static final String ISO_8859_1 = " ISO-8859-1 ";
	/** 8 位 UCS 转换格式 */
	public static final String UTF_8 = "UTF-8";
	/** 16 位 UCS 转换格式，Big Endian(最低地址存放高位字节）字节顺序 */
	public static final String UTF_16BE = " UTF-16BE ";
	/** 16 位 UCS 转换格式，Litter Endian（最高地址存放地位字节）字节顺序 */
	public static final String UTF_16LE = " UTF-16LE ";
	/** 16 位 UCS 转换格式，字节顺序由可选的字节顺序标记来标识 */
	public static final String UTF_16 = " UTF-16 ";
	/** 中文超大字符集 * */
	public static final String GBK = "GBK";

	public static final String GB2312 = "GB2312";

	/** 将字符编码转换成US-ASCII码 */
	public static String toASCII(byte[] buffer)
			throws UnsupportedEncodingException
	{
		return changeCharset(buffer, US_ASCII);
	}

	/** 将字符编码转换成ISO-8859-1 */
	public static String toISO_8859_1(byte[] buffer)
			throws UnsupportedEncodingException
	{
		return changeCharset(buffer, ISO_8859_1);
	}

	/** 将字符编码转换成UTF-8 */
	public static String toUTF_8(byte[] buffer)
			throws UnsupportedEncodingException
	{
		return changeCharset(buffer, UTF_8);
	}

	/** 将字符编码转换成UTF-16BE */
	public static String toUTF_16BE(byte[] buffer)
			throws UnsupportedEncodingException
	{
		return changeCharset(buffer, UTF_16BE);
	}

	/** 将字符编码转换成UTF-16LE */
	public static String toUTF_16LE(byte[] buffer)
			throws UnsupportedEncodingException
	{
		return changeCharset(buffer, UTF_16LE);
	}

	/** 将字符编码转换成UTF-16 */
	public static String toUTF_16(byte[] buffer)
			throws UnsupportedEncodingException
	{
		return changeCharset(buffer, UTF_16);
	}

	/** 将字符编码转换成GBK */
	public static String toGBK(byte[] buffer)
			throws UnsupportedEncodingException
	{
		return changeCharset(buffer, GBK);
	}

	/** 将字符编码转换成GB2312 */
	public static String toGB2312(byte[] buffer)
			throws UnsupportedEncodingException
	{
		return changeCharset(buffer, GB2312);
	}

	/**
	 * 字符串编码转换的实现方法
	 * 
	 * @param buffer
	 *            待转换的字符串
	 * @param newCharset
	 *            目标编码
	 */
	public static String changeCharset(byte[] buffer, String newCharset)
			throws UnsupportedEncodingException
	{
		if (buffer != null)
		{
			// 用默认字符编码解码字符串。与系统相关，中文windows 默认为GB2312
			return new String(buffer, newCharset); // 用新的字符编码生成字符串
		}
		return null;
	}

	/**
	 * 字符串编码转换的实现方法
	 * 
	 * @param buffer
	 *            待转换的字符串
	 * @param oldCharset
	 *            源字符集
	 * @param newCharset
	 *            目标字符集
	 */
	public static String changeCharset(String str, String oldCharset,
			String newCharset) throws UnsupportedEncodingException
	{
		if (str != null)
		{
			// 用源字符编码解码字符串
			byte[] bs = str.getBytes(oldCharset);
			return new String(bs, newCharset);
		}
		return null;
	}

	// 获得文件内部编码
	public static String getEnCode(String str_filepath)
	{// 转码

		File file = new File(str_filepath);
		try
		{
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream in = new BufferedInputStream(fis);
			in.mark(4);
			byte[] first3bytes = new byte[3];
			in.read(first3bytes);// 找到文档的前三个字节并自动判断文档类型。
			in.close();
			if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB
					&& first3bytes[2] == (byte) 0xBF)
			{// utf-8
				return UTF_8;

			} else if (first3bytes[0] == (byte) 0xFF
					&& first3bytes[1] == (byte) 0xFE)
			{

				return "unicode";
			} else if (first3bytes[0] == (byte) 0xFE
					&& first3bytes[1] == (byte) 0xFF)
			{

				return "utf-16be";
			} else if (first3bytes[0] == (byte) 0xFF
					&& first3bytes[1] == (byte) 0xFF)
			{

				return "utf-16le";
			}
			return GBK;
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	// 读取文件并进行相应的转码
	public static String Read(String str_filepath, String enc)
	{// 转码

		File file = new File(str_filepath);
		BufferedReader reader;
		String text = "";
		try
		{
			// FileReader f_reader = new FileReader(file);
			// BufferedReader reader = new BufferedReader(f_reader);
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream in = new BufferedInputStream(fis);

			reader = new BufferedReader(new InputStreamReader(in, enc));

			String str = reader.readLine();

			while (str != null)
			{
				text = text + str + "\n";
				str = reader.readLine();

			}
			reader.close();

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;
	}

	public static int Write(String str_filepath, String data)
	{
		try
		{
			FileOutputStream outStream = new FileOutputStream(str_filepath,
					false);
			outStream.write((byte) 0xEF);
			outStream.write((byte) 0xBB);
			outStream.write((byte) 0xBF);
			outStream.write(data.getBytes(UTF_8));
			outStream.close();
//			OutputStreamWriter writer = new OutputStreamWriter(outStream, UTF_8);
////			writer.write((byte) 0xEF);
////			writer.write((byte) 0xBB);
////			writer.write((byte) 0xBF);
//			writer.write(data.getBytes(UTF_8));
//			writer.write("/n");
//			writer.flush();
//			writer.close();// 记得关闭
			// BufferedWriter mBW = new BufferedWriter(new FileWriter(new File(
			// str_filepath)));
			// mBW.write(data, 0, data.length());
			// mBW.newLine();
			// mBW.close();
			return 1;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return 0;
	}
}
