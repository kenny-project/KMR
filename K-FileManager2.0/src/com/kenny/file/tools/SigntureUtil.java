package com.kenny.file.tools;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Base64;
import android.util.Log;

public class SigntureUtil
{
	final static String TAG = "Signture";
	/**
	 * {@link PackageInfo} flag: return information about the signatures
	 * included in the package.
	 */
	public static final int GET_SIGNATURES = 0x00000040;
	private static final String key="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC1y6g4rNC1V+WnOzD9pSfub4AqaEy5xgg9WEOHxst/5/VrcvLKTGtNBj9Pm19ncgDf6kH0+XxMKkCdA8szv99iAUdWy4qw4Da2GDLZt48Hudf/qF/28+vmx2uSobS3txH3IDSJ33lSL3F/qcxWz4XYY+MfnTrz2WG8hKfuBu9zfwIDAQAB";
	// 对比应用签名的KEY是否一致
	public static boolean PublicKey(Context ctx)
	{
		try
		{
			PackageManager packageManager = ctx.getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(
					ctx.getPackageName(), GET_SIGNATURES);
			if (packageInfo.signatures.length > 0)
			{
				String[] values = getPublicKeyString(packageInfo);
				String value="";
				for(int i=0;i<values.length;i++)
				{
					value+=values[i].trim().replace("\n", "");
					//Log.v("wmh", "value"+i+"="+values[i]);
				}
				if (value
						.equals(key))
				{
					return true;
				} else
				{
					return false;
				}
			} else
			{
				return true;
			}
			// File file=new File(Const.szAppTempPath+"test.txt");
			// if (file.exists())
			// {
			// file.delete();
			// }
			// file.createNewFile();
			//
			// OutputStreamWriter osw = new OutputStreamWriter(new
			// FileOutputStream(file));
			// for(int i=0;i<value.length;i++)
			// {
			// osw.write(value[i]+"\n");
			// }
			// osw.flush();
		} catch (Exception e)
		{
			e.printStackTrace();
			return true;
		}
	}

	public static String[] getPublicKeyString(PackageInfo pi)
	{
		PublicKey pubKeys[] = getPublicKey(pi);
		if (pubKeys == null || pubKeys.length == 0)
		{
			return null;
		}
		String[] strPubKeys = new String[pubKeys.length];
		for (int i = 0; i < pubKeys.length; i++)
			strPubKeys[i] = Base64.encodeToString(pubKeys[i].getEncoded(),
					Base64.DEFAULT);
		return strPubKeys;
	}

	private static PublicKey[] getPublicKey(PackageInfo pi)
	{
		try
		{
			if (pi.signatures == null || pi.signatures.length == 0)
			{
				return null;
			}
			PublicKey[] publicKeys = new PublicKey[pi.signatures.length];
			for (int i = 0; i < publicKeys.length; i++)
			{
				byte[] signature = pi.signatures[i].toByteArray();
				CertificateFactory certFactory = CertificateFactory
						.getInstance("X.509");
				InputStream is = new ByteArrayInputStream(signature);
				X509Certificate cert = (X509Certificate) certFactory
						.generateCertificate(is);

				publicKeys[i] = cert.getPublicKey();
			}
			return publicKeys;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private static PublicKey[] getInstalledAppPublicKey(Context context,
			String packageName)
	{
		PackageManager pm = context.getPackageManager();
		PackageInfo pi;
		try
		{
			pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
			if (pi != null && pi.versionName != null)
			{
				return getPublicKey(pi);
			}
		} catch (NameNotFoundException e)
		{
			// not installed
			return null;
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	private static Certificate[] loadCertificates(JarFile jarFile, JarEntry je)
	{
		try
		{
			// We must read the stream for the JarEntry to retrieve
			// its certificates.
			byte[] readBuffer = new byte[1024];
			InputStream is = jarFile.getInputStream(je);
			while (is.read(readBuffer, 0, readBuffer.length) != -1)
				;
			is.close();

			return (je != null) ? je.getCertificates() : null;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static boolean verifySignature(Context context, String packageName,
			String filePath)
	{
		boolean verifyed = true;
		try
		{
			PublicKey[] installedAppPubKeys = getInstalledAppPublicKey(context,
					packageName);
			if (installedAppPubKeys == null || installedAppPubKeys.length == 0)
			{
				// package not installed
				return true;
			}
			JarFile jarFile = new JarFile(filePath);
			verifyed = false;
			JarEntry je = jarFile.getJarEntry("classes.dex");
			Certificate[] certs = loadCertificates(jarFile, je);
			if (certs != null && certs.length > 0)
			{
				for (int i = 0; i < certs.length; i++)
				{
					PublicKey pubKey = certs[i].getPublicKey();
					for (int j = 0; j < installedAppPubKeys.length; j++)
					{
						if (pubKey.equals(installedAppPubKeys[j]))
						{
							verifyed = true;
							break;
						}
					}
					if (verifyed)
						break;
				}
			} else
			{
				verifyed = true;
			}

			jarFile.close();
		} catch (Exception e)
		{
			verifyed = true;
		}

		return verifyed;
	}

}