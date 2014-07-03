package com.work.market.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class T 
{
	public static String IMEI="";
	// ��ȡIMEI��
	public static String GetIMEI(Context context)
	{
		if (IMEI.equals(""))
		{
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			IMEI = tm.getDeviceId();
		}
		return IMEI;
	}
	/**
	 * ��õ�ǰϵͳ�� ����
	 * 
	 * @param context
	 * @return
	 */
	public static int GetVersionCode(Context context)
	{
		int versionCode = 0;
		try
		{
			versionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
			return versionCode;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return versionCode;
	}
	// ��ȡ�����ļ�
		public static byte[] ReadResourceAssetsFile(Context context,
				String szFileName)
		{
			ByteArrayOutputStream dest = new ByteArrayOutputStream(1024);
			try
			{
				AssetManager am = context.getAssets();
				InputStream inputStream = am.open(szFileName);
				byte[] buffer = new byte[1444];
				int bufferCount = 0;
				while ((bufferCount = inputStream.read(buffer)) != -1)
				{
					dest.write(buffer, 0, bufferCount);
				}
				dest.flush();
				dest.close();
				return dest.toByteArray();
			} catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
	/**
	 * ���������
	 * */
	public static void hideInputPad(final View edit)
	{
		// TODO Auto-generated method stub
		InputMethodManager inputmanager = (InputMethodManager) edit
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputmanager.hideSoftInputFromWindow(edit.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * ��ʾ�����
	 * */
	public static void showInputpad(final View edit)
	{

		// TODO Auto-generated method stub
		InputMethodManager inputmanager = (InputMethodManager) edit
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputmanager.showSoftInput(edit,
				InputMethodManager.RESULT_UNCHANGED_SHOWN);
	}

}
