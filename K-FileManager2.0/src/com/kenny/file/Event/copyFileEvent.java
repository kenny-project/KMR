package com.kenny.file.Event;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.framework.event.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.t.R;
import com.kenny.file.bean.FileBean;
import com.kenny.file.dialog.FolderListDialog;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.manager.FileManager;
import com.kenny.file.manager.IManager;

/**
 * 复制文件
 * 
 * @author wangmh 初始化event
 * */
public class copyFileEvent extends AbsEvent implements INotifyDataSetChanged
{

	private Context context;
	private List<FileBean> mFileList = new ArrayList<FileBean>();
	private IManager notif=null;
	public copyFileEvent(Context context, FileBean file)
	{
		this.context = context;
		mFileList.clear();
		mFileList.add(file);
	}

	public copyFileEvent(Context context, List<FileBean> list, IManager notif)
	{
		this.context = context;
		mFileList.clear();
		this.notif=notif;
		for (int i = 0; i < list.size(); i++)
		{
			FileBean temp = list.get(i);
			if (temp.isChecked())
			{
				mFileList.add(temp);
			}
		}
	}

	@Override
	public void ok()
	{
		if (mFileList.size() > 0)
		{
			new FolderListDialog().ShowDialog(context, FileManager.getInstance()
					.getCurrentPath(),mFileList, this);
		}
		else
		{
			Toast.makeText(
					context,
					context.getString(R.string.msg_Please_select_copy_file)
							+ "!", Toast.LENGTH_SHORT).show();			
		}
	}

	public void NotifyDataSetChanged(int cmd, Object value)
	{
		switch (cmd)
		{
		case FolderListDialog.Finish:
			SysEng.getInstance().addEvent(
					new palseFileEvent(context, (String) value, mFileList,false,
							notif));
			break;
		}
	}
}
