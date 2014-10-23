package com.kenny.Slidingmenu.Fragment;

import java.io.File;
import java.util.ArrayList;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Adapter.DiskInfoAdapter;
import com.kenny.file.Event.LoadSDFolderEvent;
import com.kenny.file.Parser.FavoriteGroupParser;
import com.kenny.file.bean.FGroupInfo;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.tools.SaveData;
import com.kenny.file.tools.StorageUtil;
import com.kenny.file.tools.T;
import com.kenny.file.util.Theme;

public class DiskInfoFragment extends ContentFragment implements
		INotifyDataSetChanged
{

	private ArrayList<FGroupInfo> mGroupList = new ArrayList<FGroupInfo>(); // 正在运行程序列表
	private FrameLayout flMain;
	private ListView lvLocallist;
	private LoadSDFolderEvent mLoadSDFileEvent;
	DialogInterface.OnClickListener clPositive = new DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface dialog, int which)
		{
			mLoadSDFileEvent = new LoadSDFolderEvent(m_act, false, mGroupList,
					DiskInfoFragment.this);
			SysEng.getInstance().addEvent(mLoadSDFileEvent);
		}
	};

	public View loadLocalStatus(int resId, String title, Long Total,
			Long available)
	{
		int TextColor = Theme.getTextColor();
		LayoutInflater factory = LayoutInflater.from(m_act);
		View tmpview = factory.inflate(R.layout.disk_info_local_status, null);
		tmpview.setBackgroundResource(Theme.getBackgroundResource());
		ImageView ItemLogo = (ImageView) tmpview.findViewById(R.id.ItemLogo);
		ItemLogo.setImageResource(resId);

		((TextView) tmpview.findViewById(R.id.ItemTitle)).setText(title);
		((TextView) tmpview.findViewById(R.id.ItemTitle))
				.setTextColor(TextColor);

		 TextColor =m_act.getResources().getColor(R.color.white);
		((TextView) tmpview.findViewById(R.id.ItemAvailable)).setText(
				T.FileSizeToString(available));
		((TextView) tmpview.findViewById(R.id.ItemAvailable))
				.setTextColor(TextColor);
		((TextView) tmpview.findViewById(R.id.ItemTotalSize)).setText("/"+T
				.FileSizeToString(Total));
		((TextView) tmpview.findViewById(R.id.ItemTotalSize))
				.setTextColor(TextColor);
		ProgressBar pbInternalStatus = (ProgressBar) tmpview
				.findViewById(R.id.ItemProgress);
		pbInternalStatus.setMax((int) (Total / 1024));
		pbInternalStatus.setProgress((int) (Total / 1024 - available / 1024));
		return tmpview;
	}

	/**
	 * 获取手机目录
	 * 
	 * @return
	 */
	private void LoadSDCardList(ViewGroup lyDiskInfoGroup)
	{
		File mFile = new File("/mnt/");
		File[] mFiles = mFile.listFiles();// 遍历出该文件夹路径下的所有文件/文件夹

		for (int i = 0; i < mFiles.length; i++)
		{
			if (mFiles[i].canWrite())
			{
				Long lTotalInternalSize = StorageUtil.getTotalSpace(mFiles[i]
						.getAbsolutePath());
				Long lFreeInternalSize = StorageUtil.getFreeSpace(mFiles[i]
						.getAbsolutePath());
				lyDiskInfoGroup.addView(loadLocalStatus(R.drawable.phone_icon,
						mFiles[i].getAbsolutePath(), lTotalInternalSize,
						lFreeInternalSize));
			}
		}
	}

	public void onCreate(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		setContentView(R.layout.disk_info_fragment, inflater);
		// pbLoading = mView.findViewById(R.id.pbLoading);
		flMain = (FrameLayout) mView.findViewById(R.id.flMain);
		flMain.setBackgroundResource(Theme.getBackgroundResource());
		lvLocallist = (ListView) mView.findViewById(R.id.lvLocallist);
		ViewGroup lyDiskInfoGroup = (ViewGroup) mView
				.findViewById(R.id.lyDiskInfoGroup);
		// 根目录
		Long lTotalInternalSize = StorageUtil.getTotalInternalMemorySize();
		Long lFreeInternalSize = StorageUtil.getAvailableInternalMemorySize();
		lyDiskInfoGroup.addView(loadLocalStatus(R.drawable.phone_icon, "/",
				lTotalInternalSize, lFreeInternalSize));
		// /system
		// File mFiles=new File("/system");
		// lTotalInternalSize = StorageUtil.getTotalSpace(mFiles
		// .getAbsolutePath());
		// lFreeInternalSize = StorageUtil.getFreeSpace(mFiles
		// .getAbsolutePath());
		// lyDiskInfoGroup.addView(loadLocalStatus(R.drawable.phone_icon,
		// mFiles.getAbsolutePath(), lTotalInternalSize,
		// lFreeInternalSize));

		LoadSDCardList(lyDiskInfoGroup);

		String Data;
		String FileName = m_act.getString(R.string.FavoriteType);
		Data = new String(T.ReadResourceAssetsFile(m_act, FileName));
		FavoriteGroupParser mFavoriteParser = new FavoriteGroupParser();
		mGroupList.clear();
		ArrayList<FGroupInfo> result = mFavoriteParser.parseJokeByData(m_act,
				Data);
		mGroupList.addAll(result);

		boolean bFavoriteInit = SaveData.Read(m_act, "FavoriteInit", false);
		if (!bFavoriteInit)
		{
			mLoadSDFileEvent = new LoadSDFolderEvent(m_act, false, mGroupList,
					DiskInfoFragment.this);
			SysEng.getInstance().addThreadEvent(mLoadSDFileEvent);
		}
		Refresh();
		DiskInfoAdapter adapter = new DiskInfoAdapter(m_act, mGroupList, null);
		lvLocallist.setAdapter(adapter);
	}

	@Override
	public void onPause()
	{
		super.onPause();
	}

	@Override
	public void onResume()
	{
		Refresh();
		super.onResume();
	}

	protected void Refresh()
	{
		FavoriteGroupInit();
	}

	private void FavoriteGroupInit()
	{
		try
		{
			for (int i = 0; i < mGroupList.size(); i++)
			{
				FGroupInfo temp = mGroupList.get(i);
				long size = SaveData.Read(m_act,
						"FavGroupSize_" + temp.getId(), 0l);
				int count = SaveData.Read(m_act,
						"FavGroupCount_" + temp.getId(), temp.getCount());
				temp.setCount(count);
				temp.setSize(size);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 *  跳转到指定本地数据页面
	 */
	private void onSpecifyLocalFavoriteClick(String path)
	{
		// child = new SpecifyLocalFilePage(m_act, path);
		// AddChildPage(child);
	}

	public void onStart()
	{
		P.v("NullPointError", "onStart");
		super.onStart();
	}

	/** 注销广播 */

	public void onDestroy()
	{
		super.onDestroy();
	}

	public boolean onTouchEvent(MotionEvent event)
	{
		return false;
	}

	public void NotifyDataSetChanged(final int cmd, Object value)
	{
	}

	// public void NotifyDataSetChanged(final int cmd, Object value)
	// {
	// if (Const.cmd_LoadSDFile_State != cmd)
	// {
	// P.debug("Fav.NotifyDataSetChanged:cmd=" + cmd);
	// }
	// switch (cmd)
	// {
	// case Const.cmd_LoadSDFile_Init:
	// mNotifyData.setKey(cmd);
	// mNotifyData.Push("count", value);
	// SysEng.getInstance().addHandlerEvent(mNotifyData);
	// break;
	// case Const.cmd_LoadSDFile_State:
	// mNotifyStateData.setKey(cmd);
	// mNotifyStateData.setValue(value);
	// SysEng.getInstance().addHandlerEvent(mNotifyStateData);
	// break;
	// default:
	// mNotifyData.setKey(cmd);
	// mNotifyData.setValue(value);
	// SysEng.getInstance().addHandlerEvent(mNotifyData);
	// break;
	// }
	// }
	//
	// private ParamEvent mNotifyStateData = new ParamEvent()
	// {
	// public void ok()
	// {
	// LoadSDFolderEvent.LoadSDFile_State staValue =
	// (LoadSDFolderEvent.LoadSDFile_State) getValue();
	// tvSDFileStatus.setText("正在遍历SD卡,请耐心等待!\n已遍历:" + staValue.Progress
	// + "文件");
	// tvMessage.setText(staValue.strPath);
	// }
	// };
	// private ParamEvent mNotifyData = new ParamEvent()
	// {
	// public void ok()
	// {
	// switch (getKey())
	// {
	// case Const.cmd_DelFileEvent_Finish:
	// Refresh();
	// break;
	// case Const.cmd_LoadSDFile_Error:
	// pbLoading.setVisibility(View.GONE);
	// Toast.makeText(m_act, "遍历出错！", Toast.LENGTH_SHORT).show();
	// break;
	// case Const.cmd_LoadSDFile_Init:
	// Integer value = (Integer) Pop("count");
	// pbSDFileStatus.setMax(value.intValue());
	// pbSDFileStatus.setProgress(0);
	// tvSDFileStatus.setText("");
	// tvMessage.setText("");
	// pbLoading.setVisibility(View.VISIBLE);
	// break;
	// case Const.cmd_LoadSDFile_Start:
	// pbLoading.setVisibility(View.VISIBLE);
	// break;
	// case Const.cmd_LoadSDFile_State:
	// LoadSDFolderEvent.LoadSDFile_State staValue =
	// (LoadSDFolderEvent.LoadSDFile_State) getValue();
	// tvSDFileStatus.setText("正在遍历SD卡,请耐心等待!\n已遍历:"
	// + staValue.Progress + "文件");
	// tvMessage.setText(staValue.strPath);
	// break;
	// case Const.cmd_LoadSDFile_Finish:
	// if (pbLoading.getVisibility() != View.GONE)
	// {
	// pbLoading.setVisibility(View.GONE);
	// Refresh();
	// Toast.makeText(m_act,
	// m_act.getString(R.string.msg_Scan_Finish),
	// Toast.LENGTH_SHORT).show();
	// }
	// break;
	// }
	// }
	// };

	@Override
	public boolean onCreateOptionsMenu(MenuInflater inflater, Menu menu)
	{
		inflater.inflate(R.menu.favoritepagemenu, menu);
		return false;
	}

}