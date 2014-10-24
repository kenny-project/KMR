package com.kenny.file.Event;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.file.bean.FileDetailsBean;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.tools.T;
import com.kenny.file.util.NFileTools;

/**
 * @author aimery 初始化event
 * */
public class GetFolderSizeEvent extends AbsEvent
{
	private Context act;
	private String folderPath;
	private INotifyDataSetChanged notifChanged;

	public GetFolderSizeEvent(Context act, String folderPath,
			INotifyDataSetChanged notifChanged)
	{
		this.act = act;
		this.folderPath = folderPath;
		this.notifChanged = notifChanged;
	}

	private ProgressDialog myDialog = null;

	public void ok()
	{
		SysEng.getInstance().addHandlerEvent(new AbsEvent()
		{

			public void ok()
			{
				myDialog = ProgressDialog.show(act, "", "正在获取文件夹大小...", true,
						true);
				myDialog.show();
				myDialog.setOnDismissListener(new OnDismissListener()
				{

					public void onDismiss(DialogInterface dialog)
					{
						// TODO Auto-generated method stub

					}
				});
			}
		});
		P.debug("T.FileSize start");
		final Long length = T.FileSize(folderPath);
		P.debug("T.FileSize end");
		final Long count = T.FileCount(folderPath);
		P.debug("T.NFileTools start");
		final FileDetailsBean bean=NFileTools.GetInstance().getFileSizes(folderPath) ;
		P.debug("T.NFileTools start");
		P.debug("length="+length+"count="+count+"TotalFileSize"+bean.TotalFileSize+"TotalFileCount="+bean.TotalFileCount);
		SysEng.getInstance().addHandlerEvent(new AbsEvent()
		{

			public void ok()
			{
				if (myDialog != null)
					myDialog.dismiss();
				if (notifChanged != null && bean!=null)
				{
					notifChanged.NotifyDataSetChanged(1, bean.TotalFileSize);
					notifChanged.NotifyDataSetChanged(2, bean.TotalFileCount);

				}

			}
		});
	}
}
