package com.work.market.util;

import java.io.File;
import java.util.List;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.framework.log.P;
import com.framework.syseng.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.file.tools.ApkTools;

public class ZipInstall
{
	public static void InstallZip(Context context, String path)
	{
		LoadData data1 = new LoadData();
		data1.setPath(context, path);
	}

	private static class LoadData extends AbsEvent
	{
		private ProgressDialog mProgressDialog = null;
		private File inFile;
		private String folderPath = CONST.mZipTempPaths;
		private String ApkPath = CONST.mApkTempPaths;
		private List<String> appList = null;
		private Context context;

		public void setPath(final Context context, String path)
		{
			this.context = context;
			inFile = new File(path);
			Long len = inFile.length() / 1024;
			// if (len > 16)
			// {
			ShowDialog(context, len);
			// }
//			P.v("len=" + len);
			SysEng.getInstance().addEvent(this);
			Toast.makeText(context, "文件较大,解压需要一段时间,请等待", Toast.LENGTH_SHORT)
					.show();
		}

		private void ShowDialog(Context context, Long count)
		{
			if (count < 1)
			{
				count = 1l;
			}
			try
			{
				if(!(context instanceof Application))
				{
				mProgressDialog = ProgressDialog.show(context, "", "正在解压数据...",
						true, true);
				mProgressDialog.setCancelable(false);
				}
				else
				{
					mProgressDialog = null;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				mProgressDialog = null;
			}
		}

		public void ok()
		{
			// TODO Auto-generated method stub
			try
			{
				Log.v("wmh", "ZipInstall start");
				appList = ZIP.upZipFile(inFile, ApkPath, folderPath);
				Log.v("wmh", "ZipInstall upzipFile end");

			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (mProgressDialog != null)
			{
				mProgressDialog.dismiss();
			}
			SysEng.getInstance().addHandlerEvent(new AbsEvent()
			{
				public void ok()
				{
					try
					{
						if (appList != null && appList.size() > 0)
						{
							// Uri uri = Uri.fromFile(new File(appList.get(0)));
							// Intent intent = new Intent(Intent.ACTION_VIEW);
							// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							// intent.setDataAndType(uri,
							// "application/vnd.android.package-archive");
							// context.startActivity(intent);
							ApkTools.InstallApk(context, appList.get(0));
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						Toast.makeText(context, "内存不足加载失败", Toast.LENGTH_SHORT);
					}

				}
			});
		}
	}
}
