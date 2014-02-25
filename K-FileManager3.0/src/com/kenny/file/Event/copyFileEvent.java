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

/**
 * 复制文件
 * 
 * @author wangmh 初始化event
 * */
public class copyFileEvent extends AbsEvent implements INotifyDataSetChanged
{
	private Context context;
	private List<FileBean> mFileList = new ArrayList<FileBean>();
	private INotifyDataSetChanged notif=null;
	/**
	 * FileManager.getInstance().getCurrentPath()
	 * @param context
	 * @param CurrentPath
	 * @param file
	 */
	public copyFileEvent(Context context,FileBean file,INotifyDataSetChanged notif)
	{
		this.context = context;
		this.notif=notif;
		mFileList.clear();
		mFileList.add(file);
	}
	/**
	 * FileManager.getInstance().getCurrentPath()
	 * @param context
	 * @param CurrentPath
	 * @param file
	 */
	public copyFileEvent(Context context,List<FileBean> list, INotifyDataSetChanged notif)
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
