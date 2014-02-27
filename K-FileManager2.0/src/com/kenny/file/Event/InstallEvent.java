package com.kenny.file.Event;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.t.R;
import com.kenny.file.bean.FileBean;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.manager.FileManager;
import com.kenny.file.tools.T;

/**
 * @author kenny 初始化event
 * */
public class InstallEvent extends AbsEvent
{
	private List<FileBean> list;
	private Context m_context;
	private ProgressDialog mProgressDialog;
	private boolean mProgress = false;
	private INotifyDataSetChanged notifySetChanged = null;

	public InstallEvent(Context context, List<FileBean> list,
			INotifyDataSetChanged notifySetChanged)
	{
		this.list = list;
		this.notifySetChanged = notifySetChanged;
		this.m_context = context;
		ShowDialog(list);
	}

	public InstallEvent(Context context, List<FileBean> list)
	{
		this.list = list;
		this.m_context = context;
		ShowDialog(list);
	}

	public InstallEvent(Context context, FileBean fileBean)
	{
		this.list = new ArrayList<FileBean>();
		list.add(fileBean);
		this.m_context = context;
		ShowDialog(list);
	}

	@Override
	public void ok()
	{
		Long count = 0l;
		for (int i = 0; i < list.size(); i++)
		{
			count += T.FileCount(list.get(i).getFilePath());
		}
		mProgressDialog.setMax(count.intValue());
		if (T.RootCommand(m_context))
		{
			InstallFileRoot(list);
		} else
		{
			InstallFile(list);
		}
		SysEng.getInstance().addHandlerEvent(new AbsEvent()
		{
			@Override
			public void ok()
			{
				FileManager.getInstance().Refresh();
				if (notifySetChanged != null)
				{
					notifySetChanged.NotifyDataSetChanged(0, null);
				}
				mProgressDialog.dismiss();
			}
		});
	}

	private void ShowDialog(List<FileBean> mFileList)
	{
		mProgress = true;
		mProgressDialog = new ProgressDialog(m_context);
		mProgressDialog.setTitle(m_context
				.getString(R.string.installFileEvent_Title));
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(mFileList.size());
		mProgressDialog.setButton(m_context.getString(R.string.cancel),
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

	/** 安装已选择的应用 */
	private void InstallFile(List<FileBean> list)
	{
		for (int i = 0; i < list.size() && mProgress; i++)
		{
			File temp = list.get(i).getFile();
			mProgressDialog.incrementProgressBy(1);

			new openDefFileEvent(m_context, temp.getAbsolutePath()).run();
		}
	}

	private void InstallFileRoot(List<FileBean> list)
	{

		Process process = null;
		OutputStream os = null;
		InputStream in = null;
		// 请求root
		// 调用安装
		for (int i = 0; i < list.size() && mProgress; i++)
		{
			File temp = list.get(i).getFile();
			try
			{
				process = Runtime.getRuntime().exec("su");
				os = process.getOutputStream();
				in = process.getInputStream();

				os.write(("pm install -r " + temp.getPath() + "\n").getBytes());
				os.write(new String("exit\n").getBytes());
				os.flush();
				process.waitFor();
				int len = 0;
				byte[] bs = new byte[256];
				while (-1 != (len = in.read(bs)))
				{
					String state = new String(bs, 0, len);
					if (state.equals("Success\n"))
					{
						// 安装成功后的操作
						mProgressDialog.incrementProgressBy(1);
					} else
					{
						P.d("Install", "state=" + state);
					}
				}
				os.close();
				in.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				mProgressDialog.incrementProgressBy(1);
				new openDefFileEvent(m_context, temp.getAbsolutePath()).run();
			}
			finally
			{
				try
				{
					if (os != null)
					{
						os.close();
					}
					if (in != null)
					{
						in.close();
					}
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}

	}
}
