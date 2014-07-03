package com.kenny.file.Event;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.framework.event.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
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
	public cutFileEvent(Context context, FileBean file,INotifyDataSetChanged notif)
	{
		this.context = context;
		this.notif=notif;
		mFileList.clear();
		mFileList.add(file);
	}
	private INotifyDataSetChanged notif=null;
	public cutFileEvent(Context context, List<FileBean> list,INotifyDataSetChanged notif)
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
			String currentPath=mFileList.get(0).getFile().getParent();
			new FolderListDialog().ShowDialog(context, currentPath,mFileList, this);
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
