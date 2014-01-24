package com.kenny.file.tools;

public class NetTools {
	/**
	 * 将数字IP地址转换成字符串
	 * @param ip
	 * @return
	 */
	public static String intToIp(int ip)

	{
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "."
				+ ((ip >> 16) & 0xFF) + "."

				+ ((ip >> 24) & 0xFF);
	}
}
