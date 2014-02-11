package com.kenny.file.page;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.event.ParamEvent;
import com.framework.log.P;
import com.framework.page.AbsFragmentPage;
import com.framework.page.AbsPage;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Event.ExitEvent;
import com.kenny.file.Event.LoadSDFolderEvent;
import com.kenny.file.Parser.FavoriteGroupParser;
import com.kenny.file.bean.FGroupInfo;
import com.kenny.file.bean.FileBean;
import com.kenny.file.db.Dao;
import com.kenny.file.dialog.KDialog;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.sort.FileSort;
import com.kenny.file.tools.SaveData;
import com.kenny.file.tools.StorageUtil;
import com.kenny.file.tools.T;
import com.kenny.file.util.Const;
import com.kenny.swiftp.gui.SwifFtpMain;
import com.kuaipan.client.KuaiPanPage;
import com.umeng.analytics.MobclickAgent;

public class FavoritePage extends AbsFragmentPage implements 
		INotifyDataSetChanged, OnClickListener
{

	private TextView tvPic, tvMusic, tvVideo, tvDocument, tvApp, tvZip,
			tvMessage, tvSDFileStatus;// tvWebPage
	private TextView tvPhoneStatus;// 手机消息
	private TextView tvExternalSize, tvInternalSize;
	private ArrayList<FGroupInfo> mGroupList = new ArrayList<FGroupInfo>(); // 正在运行程序列表
	private FGroupInfo mNowGItem; // 当前正在浏览的分组
	// private int mGroupID;// groupID 当前访问的Group
	private ProgressBar pbExternalStatus, pbInternalStatus, pbSDFileStatus;
	private View pbLoading;
	private FrameLayout flMain;
	private Button btLoading_cancel;
	private LoadSDFolderEvent mLoadSDFileEvent;
	private AbsPage child;
	DialogInterface.OnClickListener clPositive = new DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface dialog, int which)
		{
			mLoadSDFileEvent = new LoadSDFolderEvent(m_act, false, mGroupList,
					FavoritePage.this);
			SysEng.getInstance().addEvent(mLoadSDFileEvent);
		}
	};

	public void onCreate(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		setContentView(R.layout.favorgrouppanel,inflater);
		pbLoading =mView. findViewById(R.id.pbLoading);
		flMain = (FrameLayout) mView.findViewById(R.id.flMain);
		// flMain.setBackgroundColor(Theme.getBackgroundColor());
		pbSDFileStatus = (ProgressBar) mView.findViewById(R.id.pbSDFileStatus);
		pbExternalStatus = (ProgressBar) mView.findViewById(R.id.pbExternalStatus);
		pbInternalStatus = (ProgressBar) mView.findViewById(R.id.pbInternalStatus);
		tvExternalSize = (TextView) mView.findViewById(R.id.tvExternalSize);
		tvInternalSize = (TextView) mView.findViewById(R.id.tvInternalSize);
		tvPhoneStatus = (TextView) mView.findViewById(R.id.tvPhoneStatus);
		btLoading_cancel = (Button) mView.findViewById(R.id.btLoading_cancel);
		btLoading_cancel.setOnClickListener(this);

		tvPic = (TextView) mView.findViewById(R.id.tvPic);
		tvMusic = (TextView) mView.findViewById(R.id.tvMusic);
		tvVideo = (TextView) mView.findViewById(R.id.tvVideo);
		tvDocument = (TextView) mView.findViewById(R.id.tvDocument);
		tvApp = (TextView) mView.findViewById(R.id.tvApp);
		tvZip = (TextView) mView.findViewById(R.id.tvZip);
		tvMessage = (TextView) mView.findViewById(R.id.tvMessage);
		tvSDFileStatus = (TextView) mView.findViewById(R.id.tvSDFileStatus);

		((RelativeLayout) mView.findViewById(R.id.btPhotoGroup))
				.setOnClickListener(this);
		((RelativeLayout) mView.findViewById(R.id.btAudioGroup))
				.setOnClickListener(this);
		((RelativeLayout) mView.findViewById(R.id.btVideoGroup))
				.setOnClickListener(this);
		((RelativeLayout) mView.findViewById(R.id.btDocumentGroup))
				.setOnClickListener(this);
		((RelativeLayout) mView.findViewById(R.id.btApkGroup))
				.setOnClickListener(this);
		((RelativeLayout) mView.findViewById(R.id.btZipGroup))
				.setOnClickListener(this);
		((RelativeLayout) mView.findViewById(R.id.btfavoriteGroup))
				.setOnClickListener(this);
		((RelativeLayout) mView.findViewById(R.id.btBackupGroup))
				.setOnClickListener(this);
		((RelativeLayout) mView.findViewById(R.id.btDownloadGroup))
				.setOnClickListener(this);

		((RelativeLayout) mView.findViewById(R.id.btSwifFtp))
				.setOnClickListener(this);

		((RelativeLayout) mView.findViewById(R.id.btKuaiPanPage))
				.setOnClickListener(this);

		String Data;
		String FileName = m_act.getString(R.string.FavoriteType);
		Data = new String(T.ReadResourceAssetsFile(m_act, FileName));
		FavoriteGroupParser mFavoriteParser = new FavoriteGroupParser();
		mGroupList.clear();
		ArrayList<FGroupInfo> result = mFavoriteParser.parseJokeByData(m_act,
				Data);
		mGroupList.addAll(result);
		try
		{
			for (int i = 0; i < mGroupList.size(); i++)
			{
				String path = Const.szAppPath + mGroupList.get(i).getTitle();
				File file = new File(path);
				if (!file.exists())
				{
					file.mkdirs();
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		boolean bFavoriteInit = SaveData.Read(m_act, "FavoriteInit", false);
		if (!bFavoriteInit)
		{
			mLoadSDFileEvent = new LoadSDFolderEvent(m_act, false, mGroupList,
					FavoritePage.this);
			SysEng.getInstance().addThreadEvent(mLoadSDFileEvent);
		}
	}

	public void onLoad()
	{
		Refresh();
		Log.v("wmh", "onLoad");
	}


	private AbsPage apOldChildPage = null;

	public void AddChildPage(AbsPage childPage)
	{
		if (apOldChildPage != null)
		{
			flMain.removeView(apOldChildPage);
			apOldChildPage = childPage;
		}
		apOldChildPage = childPage;
		childPage.onCreate();
		flMain.addView(childPage);
	}

	/*
	 * 
	 * 删除老的子窗口
	 */
	public void RemoveChildPage()
	{
		if (apOldChildPage != null)
		{

			flMain.removeView(apOldChildPage);
			Refresh();
			apOldChildPage = null;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent msg)
	{
		// 弹出退出对话框
		if (child != null)
		{
			if (!child.onKeyDown(keyCode, msg))
			{
				if (keyCode == KeyEvent.KEYCODE_BACK)
				{
					RemoveChildPage();
				} else
				{
					return super.onKeyDown(keyCode, msg);
				}
			}
		} else
		{
			// 弹出退出对话框
			if (keyCode == KeyEvent.KEYCODE_BACK)
			{
				SysEng.getInstance()
						.addHandlerEvent(new ExitEvent(m_act, true));
				return true;
			}
		}
		return super.onKeyDown(keyCode, msg);
	}

	public void onPause()
	{
		Log.v("wmh", "onPause");
		super.onPause();
	}

	public void onResume()
	{
		Log.v("wmh", "onResume");
		Refresh();
		super.onResume();
		// flMain.setBackgroundColor(Theme.getBackgroundColor());
	}

	protected void Refresh()
	{
		FavoriteGroupInit();
	}

	public void FavoriteSize()
	{
		Log.v("wmh", "FavoriteSize");
		Long lTotalInternalSize = StorageUtil.getTotalInternalMemorySize();
		Long lFreeInternalSize = StorageUtil.getAvailableInternalMemorySize();
		tvInternalSize.setText(T.FileSizeToString(lTotalInternalSize));
		((TextView) mView.findViewById(R.id.tvInternalAvailable)).setText(T
				.FileSizeToString(lFreeInternalSize));
		pbInternalStatus.setMax((int) (lTotalInternalSize / 1024));
		pbInternalStatus
				.setProgress((int) (lTotalInternalSize / 1024 - lFreeInternalSize / 1024));
		/**
		 * SD卡空间
		 * 
		 */
		if (StorageUtil.externalMemoryAvailable())
		{
			tvPhoneStatus.setText("SD卡路径:" + Const.getSDCard());
			Long lTotalSpace = StorageUtil.getTotalSpace(Const.getSDCard());
			Long lFreeSpace = StorageUtil.getFreeSpace(Const.getSDCard());
			tvExternalSize.setText(T.FileSizeToString(lTotalSpace));
			((TextView) mView.findViewById(R.id.tvExternalAvailable)).setText(T
					.FileSizeToString(lFreeSpace));
			pbExternalStatus.setMax((int) (lTotalSpace / 1024));
			pbExternalStatus.setProgress((int) (lTotalSpace / 1024)
					- (int) (lFreeSpace / 1024));
		} else
		{
			tvExternalSize.setText(m_act.getString(R.string.unknown));
			((TextView) mView.findViewById(R.id.tvExternalAvailable)).setText(m_act
					.getString(R.string.unknown));
			pbExternalStatus.setMax(0);
			pbExternalStatus.setProgress(0);
		}
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
				switch (temp.getId())
				{
				case 1:
					tvMusic.setText(m_act.getString(R.string.lable_music)
							+ T.FileSizeToString(temp.getSize()));

					((TextView) mView.findViewById(R.id.tvMusicDesc)).setText("("
							+ temp.getCount() + ")");
					break;
				case 2:
					tvPic.setText(m_act.getString(R.string.lable_pic)
							+ T.FileSizeToString(temp.getSize()));
					((TextView) mView.findViewById(R.id.tvPhotoDesc)).setText("("
							+ temp.getCount() + ")");
					break;
				case 3:
					tvVideo.setText(m_act.getString(R.string.lable_video)
							+ T.FileSizeToString(temp.getSize()));
					((TextView) mView.findViewById(R.id.tvVideoDesc)).setText("("
							+ temp.getCount() + ")");
					break;
				case 4:
					tvDocument.setText(m_act.getString(R.string.lable_document)
							+ T.FileSizeToString(temp.getSize()));
					((TextView)mView. findViewById(R.id.tvDocDesc)).setText("("
							+ temp.getCount() + ")");
					break;
				case 5:
					// tvWebPage.setText(m_act.getString(R.string.lable_webpage)
					// + T.FileSizeToString(temp.getSize()));
					break;
				case 6:
					tvZip.setText(m_act.getString(R.string.lable_zip)
							+ T.FileSizeToString(temp.getSize()));
					((TextView) mView.findViewById(R.id.tvZipDesc)).setText("("
							+ temp.getCount() + ")");
					break;
				case 7:
					tvApp.setText(m_act.getString(R.string.lable_apk)
							+ T.FileSizeToString(temp.getSize()));
					((TextView) mView.findViewById(R.id.tvApkDesc)).setText("("
							+ temp.getCount() + ")");
					break;
				}
			}
			FavoriteSize();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btLoading_cancel:
			if (mLoadSDFileEvent != null)
				mLoadSDFileEvent.Cancel();
			MobclickAgent.onEvent(m_act, "FavoriteEvent", "Loading_cancel");
			break;
		case R.id.btAudioGroup:
			SwitchGroup(1);
			break;
		case R.id.btVideoGroup:
			SwitchGroup(3);
			break;
		case R.id.btPhotoGroup:
			SwitchGroup(2);
			break;
		case R.id.btDocumentGroup:
			SwitchGroup(4);
			break;
		case R.id.btApkGroup:
			SwitchGroup(7);
			// Intent intent = new Intent(m_act, ApkManager.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// //intent.setData(Uri.fromFile(file));
			// m_act.startActivity(intent);
			break;
		case R.id.btZipGroup:
			SwitchGroup(6);
			break;
		case R.id.btfavoriteGroup:// 收藏
			onMyFavoriteClick();
			break;
		case R.id.btBackupGroup:
			onSpecifyLocalFavoriteClick(Const.szAppPath);
			break;
		case R.id.btDownloadGroup:
			onSpecifyLocalFavoriteClick(Const.szDownLoadPath);
			break;
		case R.id.btRefresh:
			KDialog.ShowDialog(m_act, m_act.getString(R.string.ScanTitle),
					m_act.getString(R.string.ScanReContent),
					m_act.getString(R.string.ok), clPositive,
					m_act.getString(R.string.cancel), null);
			break;
		case R.id.btSwifFtp:
			MobclickAgent.onEvent(m_act, "KMainPage", "FtpServer");
			Intent intent = new Intent(m_act, SwifFtpMain.class);
			m_act.startActivity(intent);
			break;
		case R.id.btKuaiPanPage:
			intent = new Intent(m_act, KuaiPanPage.class);
			m_act.startActivity(intent);
			break;
		}
	}

	/**
	 *  跳转到指定本地数据页面
	 */
	private void onSpecifyLocalFavoriteClick(String path)
	{
		child = new SpecifyLocalFilePage(m_act, path);
		AddChildPage(child);
	}

	/**
	 * 进入到我的收藏夹
	 */
	private void onMyFavoriteClick()
	{
		ArrayList<FileBean> mFileList = new ArrayList<FileBean>();
		Dao dao = Dao.getInstance(getActivity().getApplicationContext());
		mFileList.clear();
		mFileList.addAll(dao.getHistoryInfos(String.valueOf(0)));
		Collections.sort(mFileList, new FileSort());
		dao.closeDb();
		mNowGItem = mGroupList.get(0);
		child = new MyFavoriteFilePage(m_act, mNowGItem, mFileList);
		AddChildPage(child);
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.muScan:
			RemoveChildPage();
			mLoadSDFileEvent = new LoadSDFolderEvent(m_act, false, mGroupList,
					FavoritePage.this);
			SysEng.getInstance().addThreadEvent(mLoadSDFileEvent);
			pbLoading.setVisibility(View.VISIBLE);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}
//
//	public boolean onCreateOptionsMenu(Menu menu)
//	{
//		P.debug("onCreateOptionsMenu");
//		return super.onCreateOptionsMenu(menu, R.menu.favoritepagemenu);
//	}
//
//	public boolean onPrepareOptionsMenu(Menu menu)
//	{
//		// return super.onCreateOptionsMenu(menu, R.menu.favoritepagemenu);
//		super.onPrepareOptionsMenu(menu);
//		P.debug("onPrepareOptionsMenu");
//		return true;
//	}

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

	public void SwitchGroup(int id)
	{
		for (int i = 0; i < mGroupList.size(); i++)
		{
			mNowGItem = mGroupList.get(i);
			if (mNowGItem.getId() == id)
			{
				child = new FavoriteFilePage(m_act, mNowGItem);
				AddChildPage(child);
				return;
			}
		}
	}

	public void NotifyDataSetChanged(final int cmd, Object value)
	{
		if (Const.cmd_LoadSDFile_State != cmd)
		{
			P.debug("Fav.NotifyDataSetChanged:cmd=" + cmd);
		}
		switch (cmd)
		{
		case Const.cmd_LoadSDFile_Init:
			mNotifyData.setKey(cmd);
			mNotifyData.Push("count", value);
			SysEng.getInstance().addHandlerEvent(mNotifyData);
			break;
		case Const.cmd_LoadSDFile_State:
			mNotifyStateData.setKey(cmd);
			mNotifyStateData.setValue(value);
			SysEng.getInstance().addHandlerEvent(mNotifyStateData);
			break;
		default:
			mNotifyData.setKey(cmd);
			mNotifyData.setValue(value);
			SysEng.getInstance().addHandlerEvent(mNotifyData);
			break;
		}
	}

	private ParamEvent mNotifyStateData = new ParamEvent()
	{
		public void ok()
		{
			LoadSDFolderEvent.LoadSDFile_State staValue = (LoadSDFolderEvent.LoadSDFile_State) getValue();
			tvSDFileStatus.setText("正在遍历SD卡,请耐心等待!\n已遍历:" + staValue.Progress
					+ "文件");
			tvMessage.setText(staValue.strPath);
		}
	};
	private ParamEvent mNotifyData = new ParamEvent()
	{
		public void ok()
		{
			switch (getKey())
			{
			case Const.cmd_DelFileEvent_Finish:
				Refresh();
				break;
			case Const.cmd_LoadSDFile_Error:
				pbLoading.setVisibility(View.GONE);
				Toast.makeText(m_act, "遍历出错！", Toast.LENGTH_SHORT).show();
				break;
			case Const.cmd_LoadSDFile_Init:
				Integer value = (Integer) Pop("count");
				pbSDFileStatus.setMax(value.intValue());
				pbSDFileStatus.setProgress(0);
				tvSDFileStatus.setText("");
				tvMessage.setText("");
				pbLoading.setVisibility(View.VISIBLE);
				break;
			case Const.cmd_LoadSDFile_Start:
				pbLoading.setVisibility(View.VISIBLE);
				break;
			case Const.cmd_LoadSDFile_State:
				LoadSDFolderEvent.LoadSDFile_State staValue = (LoadSDFolderEvent.LoadSDFile_State) getValue();
				tvSDFileStatus.setText("正在遍历SD卡,请耐心等待!\n已遍历:"
						+ staValue.Progress + "文件");
				tvMessage.setText(staValue.strPath);
				break;
			case Const.cmd_LoadSDFile_Finish:
				if (pbLoading.getVisibility() != View.GONE)
				{
					pbLoading.setVisibility(View.GONE);
					Refresh();
					Toast.makeText(m_act,
							m_act.getString(R.string.msg_Scan_Finish),
							Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};
}