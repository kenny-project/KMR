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
 * @author aimery 初始化event
 * */
public class cutFileEvent extends AbsEvent implements INotifyDataSetChanged
{
	private Context context;
	private List<FileBean> mFileList = new ArrayList<FileBean>();

	public cutFileEvent(Context context, FileBean file)
	{
		this.context = context;
		mFileList.clear();
		mFileList.add(file);
	}
	private IManager notif=null;
	public cutFileEvent(Context context, List<FileBean> list,IManager notif)
	{
		this.context = context;
		this.notif=notif;
		mFileList.clear();
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
		} else
		{
			Toast.makeText(
					context,
					context.getString(R.string.msg_Please_select_cut_file)
							+ "!", Toast.LENGTH_SHORT).show();
		}
	}

	public void NotifyDataSetChanged(int cmd, Object value)
	{
		switch (cmd)
		{
		case FolderListDialog.Finish:
			SysEng.getInstance()
					.addEvent(
							new cutFileCmdEvent(context, (String) value,
									mFileList,notif));
			break;
		}
	}
}
