package com.kenny.file.tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.framework.log.P;

import android.util.Log;

public class LinuxShell
{
	public static boolean isRoot() throws IOException,
			InterruptedException
	{
		boolean root = false;
		Process process = null;
		BufferedReader errReader = null;
		process = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(process.getOutputStream());
		os.writeBytes("exit\n");
		os.flush();
		process.waitFor();
		int exit_value = process.waitFor();
		errReader = new BufferedReader(
				new InputStreamReader(process.getErrorStream()));
		if (!errReader.ready())
		{
			root = true;
			process.destroy();
		}
		return root;
	}

	/**
	 * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
	 * 
	 * @param command
	 *            命令：String apkRoot="chmod 777 "+getPackageCodePath();
	 *            RootCommand(apkRoot);
	 * @return 应用程序是/否获取Root权限
	 */
	public static boolean RootCommand(String command)
	{
		Process process = null;
		DataOutputStream os = null;
		try
		{
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e)
		{
			Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
			return false;
		} finally
		{
			try
			{
				if (os != null)
				{
					os.close();
				}
				process.destroy();
			} catch (Exception e)
			{

			}
		}
		Log.d("*** DEBUG ***", "Root SUC ");
		return true;
	}

	/**
	 * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
	 * 
	 * @param command
	 *            命令：String apkRoot="chmod 777 "+getPackageCodePath();
	 *            RootCommand(apkRoot);
	 * @return 应用程序是/否获取Root权限
	 */
	public static boolean Chomd(String strPackageCodePath)
	{
		Process process = null;
		DataOutputStream os = null;
		try
		{
			String command = "chmod 777 " + strPackageCodePath;
			P.v("root command=" + command);
			// String path = "/data/data/com.kenny.Screen/files/";
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");

			os.flush();
			process.waitFor();
		} catch (Exception e)
		{
			Log.d("*** DEBUG ***", "ROOT REE" + e.getMessage());
			return false;
		} finally
		{
			try
			{
				if (os != null)
				{
					os.close();
				}
				process.destroy();
			} catch (Exception e)
			{
			}
		}
		Log.d("*** DEBUG ***", "Root SUC ");
		return true;
	}
}
