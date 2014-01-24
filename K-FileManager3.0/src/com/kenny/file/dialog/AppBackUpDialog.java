package com.kenny.file.dialog;

import java.util.ArrayList;
import com.kenny.KFileManager.R;
import com.kenny.file.bean.AppBean;
import com.kenny.file.util.Const;
import com.kenny.file.util.SDFile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

//wmh 未弄完
public class AppBackUpDialog implements Runnable
{
	private Activity m_context;
	private ProgressDialog mProgressDialog;
	private boolean mProgress = false;
	private ArrayList<AppBean> appList = new ArrayList<AppBean>();

	public void ShowDialog(Activity context, AppBean appInfo)
	{
		this.appList.clear();
		this.appList.add(appInfo);
		mProgress = true;
		m_context = context;
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setTitle("备份软件");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(appList.size());
		mProgressDialog.setButton(context.getString(R.string.cancel),
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						mProgress = false;
					}
				});
		mProgressDialog.setProgress(0);
		mProgressDialog.show();
		mProgressDialog.setProgress(1);
		Thread thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	public void ShowDialog(Activity context, ArrayList<AppBean> appList)
	{
		this.appList = appList;
		mProgress = true;
		m_context = context;
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setTitle("备份软件");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(appList.size());
		mProgressDialog.setButton(context.getString(R.string.cancel),
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						mProgress = false;
					}
				});
		mProgressDialog.setProgress(0);
		mProgressDialog.show();
		mProgressDialog.setProgress(1);
		Thread thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	public void run()
	{
		Message msg = new Message();
		try
		{
			if (appList.size() > 0)
			{
				msg.arg1 = appList.size();
				msg.arg2 = 0;
				for (int i = 0; i < appList.size() && mProgress; i++)
				{
					AppBean tmpInfo = appList.get(i);
					 int result = SDFile.BackAppFile(tmpInfo.getFilePath(),
					 Const.szBackupPath,
					 tmpInfo.getAppName() + tmpInfo.getVersionName());

					// int result = SDFile.BackAppFile(
					// tmpInfo.getFilePath(),
					// Const.szBackupPath,
					// MD5Calculator.calculateMD5(tmpInfo.getPackageName()
					// + "_" + tmpInfo.getVersionName()));
//					int result = SDFile.BackAppFile(tmpInfo.getFilePath(),
//							Const.szBackupPath, tmpInfo.getPackageName());
					mProgressDialog.incrementProgressBy(1);
					if (result >= 1)
					{
						msg.arg2++;
					} else
					{
						Message imsg = new Message();
						imsg.what = 101;
						imsg.obj = tmpInfo.getAppName();
						myHandler.sendMessage(imsg);
					}
				}
			}
			msg.what = 100;
			if (mProgress)
			{
				msg.obj = "备份完成!";
			} else
			{
				msg.obj = "备份取消!";
			}
			myHandler.sendMessage(msg);
		} catch (Exception e)
		{
			e.printStackTrace();
			msg.what = 103;
			msg.obj = e.getMessage();
			myHandler.sendMessage(msg);
		}
	}

	Handler myHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (msg.what == 100)// 备份完成
			{
				mProgressDialog.dismiss();
				String message = msg.obj.toString();
				String szContent = message + "\n";
				szContent += "备份程序数量：" + msg.arg1 + "个\n";
				szContent += "备份成功数量：" + msg.arg2 + "个\n";
				szContent += "快速入口:收藏->整理箱->Backup";
				szContent += "存放位置:\n";
				szContent += Const.szBackupPath+"\n";
				
				KDialog.ShowDialog(m_context, "软件备份", szContent, null, null,
						m_context.getString(R.string.submit), null);
				return;
			}
			else if (msg.what == 101)// 备份失败
			{
				String message = "备份" + (String) msg.obj + "软件包失败";
				Toast.makeText(m_context, message, Toast.LENGTH_SHORT).show();
			} else if (msg.what == 103)// 备份异常失败
			{
				String message = "错误:" + (String) msg.obj;
				Toast.makeText(m_context, message, Toast.LENGTH_SHORT).show();
				mProgressDialog.dismiss();
			}
		}
	};
}
