package com.kenny.file.Event;

import java.util.ArrayList;
import java.util.List;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.framework.event.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.file.bean.NetClientBean;
import com.kenny.file.util.FTPClientManager;
import com.kenny.file.util.FileManager;

/**
 * @author kenny 初始化event
 * */
public class delNetClientEvent extends AbsEvent
{
	private List<NetClientBean> list;
	private Context m_context;
	private ProgressDialog mProgressDialog;
	private boolean mProgress = false;

	public delNetClientEvent(Context context, ArrayList<NetClientBean> list)
	{
		this.list = list;
		this.m_context = context;
		ShowDialog(list);
	}

	public delNetClientEvent(Context context, NetClientBean fileBean)
	{
		this.list = new ArrayList<NetClientBean>();
		list.add(fileBean);
		this.m_context = context;
		ShowDialog(list);
	}

	@Override
	public void ok()
	{
		Delete(list);
		SysEng.getInstance().addHandlerEvent(new AbsEvent()
		{
			@Override
			public void ok()
			{
				FileManager.GetHandler().Refresh();
				mProgressDialog.dismiss();
				FTPClientManager.GetHandler().SendNotifyData(1,null);
			}
		});
	}

	private void ShowDialog(List<NetClientBean> mFileList)
	{
		mProgress = true;
		mProgressDialog = new ProgressDialog(m_context);
		mProgressDialog.setTitle("正在处理:");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(mFileList.size());
		mProgressDialog.setButton("取消", new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				mProgress = false;
			}
		});
		mProgressDialog.setProgress(0);
		mProgressDialog.show();
	}

	/** 删除文件夹的方法（删除该文件夹下的所有文件） */
	private void Delete(List<NetClientBean> list)
	{
		FTPClientManager clientManager = FTPClientManager.GetHandler();
		for (int i = 0; i < list.size() && mProgress; i++)
		{
			NetClientBean temp = list.get(i);
			clientManager.Del(temp);
			mProgressDialog.incrementProgressBy(1);
		}
		clientManager.SaveRamdFile();
	}

}
