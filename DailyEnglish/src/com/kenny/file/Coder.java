package com.kenny.file;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import android.util.Log;

//10. /** 
//11.  * 基础加密组件 
//12.  *  
//13.  * @author 梁栋 
//14.  * @version 1.0 
//15.  * @since 1.0 
//16.  */  
public abstract class Coder
{
	public static final String KEY_SHA = "SHA";
	public static final String KEY_MD5 = "MD5";
	// 20.
	// 21. /**
	// 22. * MAC算法可选以下多种算法
	// 23. *
	// 24. * <pre>
	// 25. * HmacMD5
	// 26. * HmacSHA1
	// 27. * HmacSHA256
	// 28. * HmacSHA384
	// 29. * HmacSHA512
	// 30. * </pre>
	// 31. */
	public static final String KEY_MAC = "HmacMD5";

	// 33.
	// 34. /**
	// 35. * BASE64解密
	// 36. *
	// 37. * @param key
	// 38. * @return
	// 39. * @throws Exception
	// 40. */
	public static byte[] decryptBASE64(String key) throws Exception
	{
		return (new Base64()).decode(key.getBytes());
	}

	/**
	 * BASE64加密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptBASE64(byte[] key) throws Exception
	{
		byte[] result = Base64.encodeBase64(key);
		String szResult = new String(result);
		return szResult;
	}

	// 密码加密 与php加密一致
		public static String Md5(String value)
		{
			try
			{
				String input = value;
				Log.v("wmh", "key="+input);
				String result = input;
				if (input != null)
				{
					MessageDigest md = MessageDigest.getInstance("MD5"); // or
																			// "SHA-1"
					md.update(input.getBytes());
					BigInteger hash = new BigInteger(1, md.digest());
					result = hash.toString(16);
					while (result.length() < 32)
					{
						result = "0" + result;
					}
				}
				Log.v("wmh", "en key="+result);
				return result;
			} catch (NoSuchAlgorithmException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
	// 密码加密 与php加密一致
	public static String Md5(String mod, String act)
	{
		try
		{
			String key = "^EnjoyMyLifeInIciba$";
			if (mod == null || mod.length() == 0)
			{
				mod = "index";
			}
			if (act == null || act.length() == 0)
			{
				act = "index";
			}
			String input = mod + act + key;
			Log.v("wmh", "key="+input);
			String result = input;
			if (input != null)
			{
				MessageDigest md = MessageDigest.getInstance("MD5"); // or
																		// "SHA-1"
				md.update(input.getBytes());
				BigInteger hash = new BigInteger(1, md.digest());
				result = hash.toString(16);
				while (result.length() < 32)
				{
					result = "0" + result;
				}
			}
			Log.v("wmh", "en key="+result);
			return result;
		} catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// 56. /**
	// 57. * MD5加密
	// 58. *
	// 59. * @param data
	// 60. * @return
	// 61. * @throws Exception
	// 62. */
	public static byte[] encryptMD5(byte[] data) throws Exception
	{

		MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
		md5.update(data);

		return md5.digest();
	}

	// 71.
	// 72. /**
	// 73. * SHA加密
	// 74. *
	// 75. * @param data
	// 76. * @return
	// 77. * @throws Exception
	// 78. */
	public static byte[] encryptSHA(byte[] data) throws Exception
	{

		MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
		sha.update(data);

		return sha.digest();

	}

	// 87.
	// 88. /**
	// 89. * 初始化HMAC密钥
	// 90. *
	// 91. * @return
	// 92. * @throws Exception
	// 93. */
	public static String initMacKey() throws Exception
	{
		KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_MAC);

		SecretKey secretKey = keyGenerator.generateKey();
		return encryptBASE64(secretKey.getEncoded());
	}

	//
	// 101. /**
	// 102. * HMAC加密
	// 103. *
	// 104. * @param data
	// 105. * @param key
	// 106. * @return
	// 107. * @throws Exception
	// 108. */
	public static byte[] encryptHMAC(byte[] data, String key) throws Exception
	{

		SecretKey secretKey = new SecretKeySpec(decryptBASE64(key), KEY_MAC);
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);

		return mac.doFinal(data);
	}
}
