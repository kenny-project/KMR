package com.kenny.file.Event;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.kenny.KFileManager.R;
import com.kenny.file.bean.FileBean;
import com.kenny.file.manager.IManager;
import com.kenny.file.tools.LinuxFileCommand;

public class copyFileCmdEvent extends AbsEvent
{
	private Context mContext;
	private ProgressDialog mProgressDialog;
	private boolean mProgress = false;
	private List<FileBean> mFileList; // 内存里面的数据
	private String mCurrentPath;// 需要粘贴的位置
	private boolean bflag = false;// 标记是否全部粘贴
	private int nDialogResult = 1;
	private IManager notif;

	public copyFileCmdEvent(Context context, String mCurrentPath,
			List<FileBean> mFileList, IManager notif)// bCut:true:剪切:
	{
		mContext = context;
		this.notif = notif;
		this.mCurrentPath = mCurrentPath;
		this.mFileList = mFileList;
		ShowDialog(mFileList.size());
	}

	private void ShowDialog(int count)
	{
		mProgress = true;
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setTitle(R.string.palseFileEvent_Title);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(count);
		mProgressDialog.setButton(mContext.getString(R.string.cancel),
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						mProgress = false;
					}
				});
		mProgressDialog.setProgress(0);
		mProgressDialog.show();
	}
	public void ok()
	{
		try
		{
			Long count = 0l;
//			for (int i = 0; i < mFileList.size(); i++)
//			{
//				count += T.FileCount(mFileList.get(i).getFilePath());
//			}
//			mProgressDialog.setMax(count.intValue());
			P.debug("copyFileCmdEvent.Start");
			for (int i = 0; i < mFileList.size() && mProgress; i++)
			{
				final FileBean mSrcFile = mFileList.get(i);

				String mStrSrcPath=mSrcFile.getFile().getPath();
				if(mCurrentPath.startsWith(mStrSrcPath))
				{
					Log.v("wmh", "true:mStrSrcPath="+"mCurrentPath="+mCurrentPath);
					 Message msg = new Message();
					 msg.what = 100;
					 msg.obj = mCurrentPath+"包括子目录,粘贴失败";
					 myHandler.sendMessage(msg);
					 return;
				}
				else
				{
					Log.v("wmh", "false:mStrSrcPath="+"mCurrentPath="+mCurrentPath);
				}
				//将mSrcFile复制到mCurrentPath目录
				int result = Copy(mCurrentPath, mSrcFile.getFile());
				P.v("result=" + result);
				if(result>0)
				{
					mProgressDialog.incrementProgressBy(1);
				}
				else if (result < 0)
				{
					Message msg = new Message();
					msg.what = 101;
					msg.obj = mSrcFile.getFileName();
					myHandler.sendMessage(msg);
					return;
				}
				else
				{
					Message msg = new Message();
					msg.what = 100;
					msg.obj = "粘贴取消";
					myHandler.sendMessage(msg);
					return;
				}
			}
			P.debug("copyFileCmdEvent.end");
			Message msg = new Message();
			msg.what = 100;
			if (mProgress)
			{
				msg.obj = "粘贴完成";
			} else
			{
				msg.obj = "粘贴取消";
			}
			myHandler.sendMessage(msg);
		} catch (Exception e)
		{
			e.printStackTrace();
			Message msg = new Message();
			msg.what = 103;
			msg.obj = e.getMessage();
			myHandler.sendMessage(msg);
			return;
		}

	}

	Handler myHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if (msg.what == 100)// 拷贝完成
			{
				String message = msg.obj.toString();
				Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
				mProgressDialog.dismiss();
			} else if (msg.what == 101)// 拷贝失败
			{
				String message = "拷贝文件失败!";
				Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
				mProgressDialog.dismiss();
			} else if (msg.what == 103)// 拷贝异常失败
			{
				String message = mContext.getString(R.string.error_lable)
						+ (String) msg.obj;
				Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
				mProgressDialog.dismiss();
			}
			if (notif != null)
			{
				notif.Refresh();
			}
//			else //by wmh2014-2-21
//			{
//				FileManager.getInstance().Refresh();
//			}
		}
	};
	private LinuxFileCommand comm=new LinuxFileCommand();
	/**
	 * 粘贴 path:目地地址 file:源文件
	 * 
	 * @param mSaveFilePaths
	 */
	private int Copy(String newPath, File file)
	{
		try
		{
			if (file.exists())
			{
				newPath = newPath + "/" + file.getName();
			Process process=comm.copyFile(file.getPath(), newPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					process.getErrorStream()));
			int ret = process.waitFor();
			if (ret != 0)
			{
				final String errorMsg = br.readLine();
				P.d(tag, "Error(code = " + ret + "): " + errorMsg);
				return -1;
			}
			return 1;
			}
			else
			{
				return -2;
			}

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -2;

	}
}
