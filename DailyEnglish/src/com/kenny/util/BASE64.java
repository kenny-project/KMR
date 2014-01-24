package com.kenny.util;

import android.util.Base64;

public class BASE64 {
	/**
	 * BASE64解密
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String decryptBASE64(String str) {

		return new String(android.util.Base64.decode(str, Base64.DEFAULT));
	}

	/**
	 * BASE64加密
	 * 
	 * @param str
	 * @return
	 * 
	 */
	public static String encryptBASE64(String str) {
		return Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
	}

}
