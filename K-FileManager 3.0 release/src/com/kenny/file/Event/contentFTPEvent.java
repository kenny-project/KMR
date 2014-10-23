package com.kenny.file.Event;

import java.io.File;
import java.util.List;
import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.file.bean.FileBean;
import com.kenny.file.util.SDFile;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

//wmh 未弄完
public class contentFTPEvent extends AbsEvent
{
	private Activity m_context;
	private ProgressDialog mProgressDialog;

	public contentFTPEvent(Activity context, String mCurrentPath,
			List<FileBean> mFileList)
	{
		m_context = context;
		ShowDialog(mFileList.size());
	}

	private void ShowDialog(int count)
	{
		mProgressDialog = new ProgressDialog(m_context);
		mProgressDialog.setTitle("连接服务器:");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(count);
		mProgressDialog.setButton("取消", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
			}
		});
		mProgressDialog.setProgress(0);
		mProgressDialog.show();
		// Thread thread = new Thread(this);
		// thread.setPriority(Thread.MIN_PRIORITY);
		// thread.start();
	}

	@Override
	public void ok()
	{
	}
}
