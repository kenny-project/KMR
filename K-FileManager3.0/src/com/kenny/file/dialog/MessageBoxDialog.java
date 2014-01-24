package com.kenny.file.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class MessageBoxDialog
{
	public static void showdialog(final Activity activity, String Title,String message)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(Title).setMessage(message).setCancelable(false).setPositiveButton("确定",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						try
						{
							//GuoheAdManager.finish(activity);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
						activity.finish();
						System.exit(0);
//						android.os.Process.killProcess(android.os.Process
//								.myPid());
					}
				}).setNegativeButton("取消",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
