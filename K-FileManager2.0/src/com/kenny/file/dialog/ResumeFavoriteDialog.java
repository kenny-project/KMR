package com.kenny.file.dialog;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Event.delFileEvent;
import com.kenny.file.bean.FileBean;
import com.kenny.file.util.Const;

public class ResumeFavoriteDialog
{
	public static void showdialog(final Activity activity, String Title)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(Title)
				.setCancelable(false)
				.setPositiveButton(activity.getString(R.string.ok),
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int id)
							{
								String path = "/data/data/"
										+ activity.getPackageName() + "/";
								deleteFolder(new File(path));
								File file = new File(Const.szAppTempPath);
								if (file.exists())
								{
									SysEng.getInstance().addEvent(
											new delFileEvent(activity,
													new FileBean(file, null)));
								}
							}
						})
				.setNegativeButton(activity.getString(R.string.cancel),
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

	/** 删除文件夹的方法（删除该文件夹下的所有文件） */
	private static int deleteFolder(final File folder)
	{
		if (folder.canWrite())
		{
			if (folder.isFile())
			{
				boolean result = folder.delete();// 是文件则直接删除
				return 1;
			} else
			{
				File[] fileArray = folder.listFiles();
				if (fileArray.length == 0)
				{ // 空文件夹则直接删除
					folder.delete();
				} else
				{
					for (File currentFile : fileArray)
					{
						// 遍历该目录
						if (currentFile.exists() && currentFile.isFile())
						{// 文件则直接删除
							currentFile.delete();
						} else
						{
							deleteFolder(currentFile);// 回调
						}
					}
					folder.delete();
				}
			}
			return 0;
		} else
		{
			return -1;
		}
	}
}
