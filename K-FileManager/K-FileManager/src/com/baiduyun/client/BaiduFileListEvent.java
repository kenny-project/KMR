package com.baiduyun.client;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import cn.kuaipan.android.sdk.net.KPCallback;
import cn.kuaipan.android.sdk.net.KPException;

import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.sort.FileSort;
import com.kenny.file.util.Const;
import com.kenny.file.util.NetConst;

/**
 * 获取快盘路径
 * @author wmh 
 * */
public class BaiduFileListEvent extends AbsEvent implements KPCallback
{
	private String TAG = "LoadAppsEvent";
	private Activity m_act;
	private INotifyDataSetChanged m_INotify = null;
	// private PkgSizeObserver mpkgSizeObserver;
	//private ProgressDialog myDialog = null;
	private int cmd = 0;
	private List<BaiduFile> beans;
	private String path;
	private View mView;
	private BaiduCommandConsole cli;
	public BaiduFileListEvent(Activity act, String path,
			BaiduCommandConsole cli, View mView,
			INotifyDataSetChanged INotify)
	{
		m_act = act;
		this.cli = cli;
		this.mView=mView;
		m_INotify = INotify;
		this.path = path;
	}
	/**
	 *  获取相应路径的文件列表
	 */
	public void getFileList()
	{
		try
		{
			if (NetConst.isNetConnectNoMsg(m_act))
			{
				SysEng.getInstance().addHandlerEvent(new AbsEvent()
				{
					public void ok()
					{
						mView.setVisibility(View.VISIBLE);
					}
				});
				Log.v("wmh", "cli.do_ls("+path+"");
				beans = cli.do_ls(path);
				Log.v("wmh", "end cli.do_ls("+path+"");
				if (beans != null)
				{
					//Collections.sort(beans, new FileSort());
					if (!cli.getPath().equals("/"))
					{
						BaiduFile back = new BaiduFile();
						back.setFileName("..");
						back.setBackUp(true);
						beans.add(0, back);
					}
					cmd = Const.cmd_KuaiPan_LS;
				} else
				{
					cmd = Const.cmd_KuaiPan_LS_Error;

				}
			} else
			{
				cmd = Const.cmd_KuaiPan_LS_Error_NoNetWork;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			cmd = Const.cmd_KuaiPan_LS_Error;
		}
		if (m_INotify != null)
		{
			m_INotify.NotifyDataSetChanged(cmd, beans);
		}
		
		SysEng.getInstance().addHandlerEvent(new AbsEvent()
		{
			public void ok()
			{
//				if (myDialog != null)
//				{
//					myDialog.dismiss();
//				}
				mView.setVisibility(View.GONE);
			}
		});
		P.debug(TAG, "getKuapPanListEvent end");
	}

	public void ok()
	{
		getFileList();
	}

	public void onSuccess(Object obj)
	{
		// TODO Auto-generated method stub

	}

	public void onFail(int code, KPException e, String msg)
	{
		// TODO Auto-generated method stub

	}

	public void onProgress(long bytes, long total)
	{
		// TODO Auto-generated method stub

	}
}
