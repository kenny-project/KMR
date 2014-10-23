package com.kenny.file.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

public class GZip
{
	public static String GZipStreamToString(InputStream in, int requesttype)
			throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		in = new GZIPInputStream(in);
		copyStream(in, baos, requesttype);
		return baos.toString("UTF-8");
	}

	public static String GZipStreamToString(String str, int requesttype)
			throws IOException
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(str.getBytes());
		return GZipStreamToString(bin, requesttype);
	}

	public static String GZipStreamToString(byte[] data, int requesttype)
			throws IOException
	{
		ByteArrayInputStream bin = new ByteArrayInputStream(data);
		return GZipStreamToString(bin, requesttype);
	}

	private static void copyStream(InputStream in, OutputStream out,
			int requesttype) throws IOException
	{
		BufferedInputStream bin = new BufferedInputStream(in);
		BufferedOutputStream bout = new BufferedOutputStream(out);
		byte[] buffer = new byte[2048];
		int doneLength = -1;
		while (true)
		{
			doneLength = bin.read(buffer);
			if (doneLength == -1)
				break;
			bout.write(buffer, 0, doneLength);
		}
		bout.flush();
	}
}
