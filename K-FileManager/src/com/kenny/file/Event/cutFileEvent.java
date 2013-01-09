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
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.util.FileManager;

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

	public cutFileEvent(Context context, List<FileBean> list)
	{
		this.context = context;
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
			new FolderListDialog().ShowDialog(context, FileManager.GetHandler()
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
							new palseFileEvent(context, (String) value,
									mFileList, true));
			break;
		}
	}
}
