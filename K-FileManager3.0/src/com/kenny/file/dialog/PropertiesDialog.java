package com.kenny.file.dialog;


import java.io.File;
import android.app.Activity;

public class PropertiesDialog
{
	public void showDialog(Activity m_ctx, File fl, String f)
	{
//		java.lang.Process p = null;
//		BufferedReader br = null, ber = null;
//		String[] cmds = null;
//		Log.v("def", "doGetPropertiesf=" + f);
//		try
//		{
//			boolean directory = fl.isDirectory();
//
//			if (fileManager.isRoot() == false)
//			{
//				if (directory)
//				{
//					cmds = new String[]
//					{ "ls", "-l", "-a", f + "/.." };
//
//				} else
//				{
//					cmds = new String[]
//					{ "ls", "-l", "-a", f };
//				}
//				p = fileManager.linux.shell.exec(cmds);
//				br = new BufferedReader(new InputStreamReader(
//						p.getErrorStream()));
//				ber = new BufferedReader(new InputStreamReader(
//						p.getInputStream()));
//			}
//			int w = p.waitFor();
//			// String con = ber.readLine();
//			if (w != 0)
//			{
//				Toast.makeText(fileManager, br.readLine(), Toast.LENGTH_LONG)
//						.show();
//				br.close();
//				ber.close();
//				return;
//			}
//
//			// 从shell中获取内容
//			String con;
//			while ((con = ber.readLine()) != null)
//			{
//				if (con.endsWith(fileManager.currentFileInfo().get(position)
//						.name()))
//				{
//					break;
//				}
//			}
//
//			if (con == null)
//			{
//				propertyDialog.show();
//				return;
//			}
//			String[] ss = con.split(" +");
//			String fName = "文件:\n\t\t" + f;
//			String fStyle = "文件类型:\t\t" + fileStyle(ss[0].charAt(0));
//			String fSize;
//			if (fl.isDirectory())
//			{
//				/**
//				 * fSize = "大小:\t\t\t" + Common.formatString(String.valueOf(
//				 * FileOperation.getDirectorySize(fl))); /
//				 **/
//				fSize = "大小:\t\t\t 计算中...";
//				perDismiss = false;
//				new Thread(new FileSizeThread(fl, getSizeListener)).start();
//			} else
//			{
//				fSize = "大小:\t\t\t"
//						+ Common.formatString(String.valueOf(fl.length()));
//			}
//			String fUser = "用户:\t\t\t" + ss[1];
//			String fGroup = "用户群:\t\t\t" + ss[2];
//			String createDate = "修改日期:\t\t"
//					+ (new Date(fl.lastModified()).toLocaleString());
//			permission = filePermissions(ss[0]);
//			spinnerSelected(permission);
//
//			perText = (TextView) dView.findViewById(R.id.permessage);
//			perString = fName + "\n" + fStyle + "\n" + fUser + "\n" + fGroup
//					+ "\n" + createDate + "\n";
//			perText.setText(perString + fSize);
//			propertyDialog.show();
//
//		} catch (IOException e)
//		{
//			e.printStackTrace();
//		} catch (InterruptedException e)
//		{
//			e.printStackTrace();
//		} finally
//		{
//			p.destroy();
//			try
//			{
//				if (br != null)
//					br.close();
//				if (ber != null)
//					ber.close();
//			} catch (IOException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
}
