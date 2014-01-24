package com.kenny.file.Event;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.bean.FileBean;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.manager.FileManager;
import com.kenny.file.tools.LinuxFileCommand;
import com.kenny.file.util.Const;

/**
 * @author kenny 初始化event
 * */
public class delFileEvent extends AbsEvent
{
	private List<FileBean> list;
	private Context m_context;
	private ProgressDialog mProgressDialog;
	private boolean mProgress = false;
	private INotifyDataSetChanged notifySetChanged = null;

	public delFileEvent(Context context, List<FileBean> list,
			INotifyDataSetChanged notifySetChanged)
	{
		this.list = list;
		this.notifySetChanged = notifySetChanged;
		this.m_context = context;
		ShowDialog(list);
	}

	public delFileEvent(Context context, FileBean fileBean,
			INotifyDataSetChanged notifySetChanged)
	{
		this.list = new ArrayList<FileBean>();
		list.add(fileBean);
		this.m_context = context;
		this.notifySetChanged = notifySetChanged;
		ShowDialog(list);
	}

	public delFileEvent(Context context, List<FileBean> list)
	{
		this.list = list;
		this.m_context = context;
		ShowDialog(list);
	}

	public delFileEvent(Context context, FileBean fileBean)
	{
		this.list = new ArrayList<FileBean>();
		list.add(fileBean);
		this.m_context = context;
		ShowDialog(list);
	}

	public delFileEvent(Context context, FileBean fileBean, boolean isShowDialog)
	{
		this.list = new ArrayList<FileBean>();
		list.add(fileBean);
		this.m_context = context;
		ShowDialog(list);
	}

	@Override
	public void ok()
	{
		mProgressDialog.setMax(list.size());
		LinuxFileCommand command=new LinuxFileCommand();
		for (int i = 0; i < list.size()&&mProgress; i++)
		{
			String name = list.get(i).getFilePath();
			try
			{
				P.d(tag, "Delete File start ");
				Process deleProgress = command.delete(name);
				BufferedReader br = new BufferedReader(new InputStreamReader(
						deleProgress.getErrorStream()));
				int ret = deleProgress.waitFor();
				if (ret != 0)
				{
					final String errorMsg = br.readLine();
					P.d(tag, "Error(code = " + ret + "): " + errorMsg);
					SysEng.getInstance().addHandlerEvent(new AbsEvent()
					{
						@Override
						public void ok()
						{
							Toast.makeText(m_context, errorMsg,
									Toast.LENGTH_SHORT).show();
						}
					});
				}
				P.d(tag, "Delete File end ");
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			mProgressDialog.incrementProgressBy(1);
		}

		// Delete(list);
		SysEng.getInstance().addHandlerEvent(new AbsEvent()
		{
			@Override
			public void ok()
			{
				FileManager.getInstance().Refresh();
				if (notifySetChanged != null)
				{
					notifySetChanged.NotifyDataSetChanged(
							Const.cmd_DelFileEvent_Finish, null);
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
				.getString(R.string.deleteFileEvent_Title));
		// TextView text=new TextView(m_context);
		// text.setText("fdsafdsafdsa");
		// WindowManager.LayoutParams params=new WindowManager.LayoutParams();
		// params.width=WindowManager.LayoutParams.FILL_PARENT;
		// mProgressDialog.addContentView(text, params);
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

	/** 删除文件夹的方法（删除该文件夹下的所有文件） */
	private void Delete(List<FileBean> list)
	{
		for (int i = 0; i < list.size() && mProgress; i++)
		{
			File temp = list.get(i).getFile();
			deleteFolder(temp);
		}
	}

	/** 删除文件夹的方法（删除该文件夹下的所有文件） */
	private void deleteFolder(final File folder)
	{
		if (!folder.canWrite())
		{
			SysEng.getInstance().addHandlerEvent(new AbsEvent()
			{
				@Override
				public void ok()
				{
					Toast.makeText(
							m_context,
							folder.getName()
									+ m_context
											.getString(R.string.not_delete_file_authority),
							Toast.LENGTH_SHORT).show();
				}
			});
			return;
		}
		if (folder.isFile())
		{
			boolean result = folder.delete();// 是文件则直接删除
			mProgressDialog.incrementProgressBy(1);
			if (!result)
			{
				SysEng.getInstance().addHandlerEvent(new AbsEvent()
				{
					@Override
					public void ok()
					{
						Toast.makeText(
								m_context,
								folder.getName()
										+ m_context
												.getString(R.string.not_delete_file_authority),
								Toast.LENGTH_SHORT).show();
					}
				});
			}
			return;
		} else
		{
			File[] fileArray = folder.listFiles();
			if (fileArray.length == 0)
			{ // 空文件夹则直接删除
				mProgressDialog.incrementProgressBy(1);
				folder.delete();
			} else
			{
				for (File currentFile : fileArray)
				{
					if (!mProgress)
						return;
					// 遍历该目录
					if (currentFile.exists() && currentFile.isFile())
					{// 文件则直接删除
						currentFile.delete();
						mProgressDialog.incrementProgressBy(1);
					} else
					{
						deleteFolder(currentFile);// 回调
					}
				}
				mProgressDialog.incrementProgressBy(1);
				folder.delete();
			}
		}
	}

}
