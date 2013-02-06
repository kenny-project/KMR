package com.kenny.file.page;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.event.ParamEvent;
import com.framework.interfaces.MenuAble;
import com.framework.log.P;
import com.framework.page.MultiItemPage;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.KFileManager.R.color;
import com.kenny.file.Adapter.FGroupAdapter;
import com.kenny.file.Adapter.FavorFileAdapter;
import com.kenny.file.Event.ExitEvent;
import com.kenny.file.Event.FavoriteFileEvent;
import com.kenny.file.Event.LoadSDFolderEvent;
import com.kenny.file.Event.delFileEvent;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.Event.openFileEvent;
import com.kenny.file.Parser.FavoriteGroupParser;
import com.kenny.file.bean.FGroupInfo;
import com.kenny.file.bean.FavorFileBean;
import com.kenny.file.bean.FileBean;
import com.kenny.file.commui.ListHeaderView;
import com.kenny.file.db.Dao;
import com.kenny.file.dialog.KDialog;
import com.kenny.file.sort.FileSort;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.tools.SaveData;
import com.kenny.file.tools.T;
import com.kenny.file.util.Const;
import com.kenny.file.util.FileManager;
import com.kenny.file.util.StorageUtil;
import com.kenny.file.util.Theme;
import com.umeng.analytics.MobclickAgent;

public class FavoriteItemPage extends MultiItemPage implements MenuAble,
		INotifyDataSetChanged, OnItemClickListener, OnClickListener,
		OnItemLongClickListener
{
	public FavoriteItemPage(Activity context)
	{
		super(context);
	}

	/*
	 * 声明成员变量： mFileName ：存放显示的文件列表的名称 mFilePaths ：存放显示的文件列表的相对应的路径 mRootPath
	 * ：起始目录“/” (用java.io.File.separator获取) mSDCard ： SD卡根目录 mPath
	 * ：显示当前路径的TextView文本组件
	 */
	private TextView tvPic, tvMusic, tvVideo, tvDocument, tvApp, tvZip,
			tvMessage, tvSDFileStatus;// tvWebPage
	private ListView mListView;
	private Button btRefresh;
	private Button btAllFile, btFolder;
	private Button btBack;
	private Button btDelete, btSelectAll;

	private FGroupAdapter mGroupAdapter;
	private TextView tvExternalSize, tvInternalSize;
	private FavorFileAdapter mFileAdapter;
	private FavorFileAdapter mFolderAdapter;
	private ArrayList<FGroupInfo> mGroupList = new ArrayList<FGroupInfo>(); // 正在运行程序列表
	private ArrayList<FileBean> mAllFileList = new ArrayList<FileBean>();
	private ArrayList<FileBean> mFolderList = new ArrayList<FileBean>();
	private ArrayList<FileBean> mTempList = new ArrayList<FileBean>();
	private FGroupInfo mNowGItem; // 当前正在浏览的分组
	private ProgressBar pbExternalStatus, pbInternalStatus, pbSDFileStatus;
	private View lyTools2, lyBTools;
	private View icGroupPannel, icItemPannel;
	private View pbLoading;
	private Button btLoading_cancel;
	private LoadSDFolderEvent mLoadSDFileEvent;
	/**
	 * 0:Group 1:Folder 2:Item
	 */
	private final static int FLAG_GROUP = 0;
	private final static int FLAG_FILE = 1;
	private final static int FLAG_Folder = 2;
	private int bFlag = FLAG_GROUP;

	private View FooterView()
	{
		TextView tview = new TextView(m_act);
		tview.setHeight(100);
		tview.setWidth(-1);
		tview.setBackgroundColor(color.green);
		ListHeaderView headerView = new ListHeaderView(m_act, tview);
		return headerView;
	}

	/**
	 * 切换窗体视图 false:ListView true:GridView
	 */
	private void SwitchStyle(int bFlag)
	{
		if (bFlag == FLAG_GROUP)
		{
			icGroupPannel.setVisibility(View.VISIBLE);
			icItemPannel.setVisibility(View.GONE);
			mGroupAdapter = new FGroupAdapter(m_act, 1, mGroupList);
			mListView.setAdapter(mGroupAdapter);
			mListView.setVisibility(View.VISIBLE);
		} else if (bFlag == FLAG_FILE)
		{
			icItemPannel.setVisibility(View.VISIBLE);
			icGroupPannel.setVisibility(View.GONE);

			btAllFile.setBackgroundResource(R.drawable.tab2_left_select);
			btFolder.setBackgroundResource(R.drawable.tab2_right_unselect);
			btFolder.setTextColor(m_act.getResources().getColor(
					R.color.tab_TextColor_selected));
			btAllFile.setTextColor(m_act.getResources().getColor(
					R.color.tab_TextColor_normal));
			mFileAdapter = new FavorFileAdapter(m_act, 1, mAllFileList);
			mListView.setAdapter(mFileAdapter);
			mListView.setVisibility(View.VISIBLE);
			btSelectAll.setVisibility(View.VISIBLE);
			if (Theme.getToolsVisible())
			{
				lyBTools.setVisibility(View.VISIBLE);
			} else
			{
				lyBTools.setVisibility(View.GONE);
			}
		} else if (bFlag == FLAG_Folder)
		{
			icItemPannel.setVisibility(View.VISIBLE);
			icGroupPannel.setVisibility(View.GONE);
			lyTools2.setVisibility(View.VISIBLE);
			mFolderAdapter = new FavorFileAdapter(m_act, 1, mTempList);
			mListView.setAdapter(mFolderAdapter);
			btAllFile.setBackgroundResource(R.drawable.tab2_left_unselect);
			btFolder.setBackgroundResource(R.drawable.tab2_right_select);
			btAllFile.setTextColor(m_act.getResources().getColor(
					R.color.tab_TextColor_selected));
			btFolder.setTextColor(m_act.getResources().getColor(
					R.color.tab_TextColor_normal));

			if (Theme.getToolsVisible())
			{
				lyBTools.setVisibility(View.VISIBLE);
			} else
			{
				lyBTools.setVisibility(View.GONE);
			}
		}
		this.bFlag = bFlag;
	}

	DialogInterface.OnClickListener clPositive = new DialogInterface.OnClickListener()
	{

		public void onClick(DialogInterface dialog, int which)
		{
			mLoadSDFileEvent = new LoadSDFolderEvent(m_act, false, mGroupList,
					FavoriteItemPage.this);
			SysEng.getInstance().addEvent(mLoadSDFileEvent);

		}
	};

	private OnScrollListener m_localOnScrollListener = new OnScrollListener()
	{

		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			switch (scrollState)
			{
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
				P.debug("SCROLL_STATE_FLING");
				if (mFileAdapter != null)
					mFileAdapter.setShowLogo(false);
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
				P.debug("SCROLL_STATE_IDLE");
				if (mFileAdapter != null)
				{
					mFileAdapter.setShowLogo(true);
					mFileAdapter.notifyDataSetChanged();
				}
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				P.debug("SCROLL_STATE_TOUCH_SCROLL");
				if (mFileAdapter != null)
					mFileAdapter.setShowLogo(false);
				break;
			default:
				break;
			}
		}

		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount)
		{
		}
	};

	public void onCreate()
	{
		setContentView(R.layout.favoritepage);
		super.onCreate();
		pbLoading = findViewById(R.id.pbLoading);
		icGroupPannel = findViewById(R.id.icGroupPannel);
		icItemPannel = findViewById(R.id.icItemPannel);
		;
		pbSDFileStatus = (ProgressBar) findViewById(R.id.pbSDFileStatus);
		pbExternalStatus = (ProgressBar) findViewById(R.id.pbExternalStatus);
		pbInternalStatus = (ProgressBar) findViewById(R.id.pbInternalStatus);
		tvExternalSize = (TextView) findViewById(R.id.tvExternalSize);
		lyBTools = findViewById(R.id.lyBTools);
		tvInternalSize = (TextView) findViewById(R.id.tvInternalSize);
		mListView = (ListView) findViewById(R.id.lvLocallist);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(m_localOnScrollListener);
		mListView.setOnItemLongClickListener(this);
		// mListView.setOnLongClickListener(this);
		mListView.addFooterView(FooterView(), null, false);

		btAllFile = (Button) findViewById(R.id.btAllFile);
		btAllFile.setOnClickListener(this);
		btFolder = (Button) findViewById(R.id.btFolder);
		btFolder.setOnClickListener(this);
		btLoading_cancel = (Button) findViewById(R.id.btLoading_cancel);
		btLoading_cancel.setOnClickListener(this);

		btRefresh = (Button) findViewById(R.id.btRefresh);
		btRefresh.setOnClickListener(this);

		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(this);

		lyTools2 = findViewById(R.id.lyTools2);
		btDelete = (Button) findViewById(R.id.btDelete);
		btDelete.setOnClickListener(this);

		btSelectAll = (Button) findViewById(R.id.btSelectAll);
		btSelectAll.setOnClickListener(this);

		tvPic = (TextView) findViewById(R.id.tvPic);
		tvMusic = (TextView) findViewById(R.id.tvMusic);
		tvVideo = (TextView) findViewById(R.id.tvVideo);
		tvDocument = (TextView) findViewById(R.id.tvDocument);
		tvApp = (TextView) findViewById(R.id.tvApp);
		tvZip = (TextView) findViewById(R.id.tvZip);
		tvMessage = (TextView) findViewById(R.id.tvMessage);
		tvSDFileStatus = (TextView) findViewById(R.id.tvSDFileStatus);

		((RelativeLayout) findViewById(R.id.btPhotoGroup))
				.setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.btAudioGroup))
				.setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.btVideoGroup))
				.setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.btDocumentGroup))
				.setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.btApkGroup))
				.setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.btZipGroup))
				.setOnClickListener(this);
		((RelativeLayout) findViewById(R.id.btfavoriteGroup))
		.setOnClickListener(this);		
		
		boolean bFavoriteInit = SaveData.Read(m_act, "FavoriteInit", false);
		if (!bFavoriteInit)
		{
			mLoadSDFileEvent = new LoadSDFolderEvent(m_act, false, mGroupList,
					FavoriteItemPage.this);
			SysEng.getInstance().addThreadEvent(mLoadSDFileEvent);
		}
	}

	public void onLoad()
	{
		Refresh(bFlag);
		SwitchStyle(bFlag);
	}

	public void onReload()
	{

	}

	public void onExit()
	{
	}

	public boolean onKeyDown(int keyCode, KeyEvent msg)
	{
		// 弹出退出对话框
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (bFlag == FLAG_FILE)// 1
			{
				Refresh(FLAG_GROUP);
				SwitchStyle(FLAG_GROUP);
			} else if (bFlag == FLAG_Folder)// 1
			{
				if (nFolderType == 1)
				{
					nFolderType = 0;
					mTempList.clear();
					mTempList.addAll(mFolderList);
					if (mFolderAdapter != null)
					{
						mFolderAdapter.notifyDataSetChanged();
					}
				} else
				{
					SwitchStyle(FLAG_GROUP);
				}
			} else
			{
				SysEng.getInstance()
						.addHandlerEvent(new ExitEvent(m_act, true));
			}
			return true;
		}
		return super.onKeyDown(keyCode, msg);
	}

	public void onPause()
	{

	}

	public void onResume()
	{
		if (bFlag != FLAG_GROUP)
		{
			if (Theme.getToolsVisible())
			{
				lyBTools.setVisibility(View.VISIBLE);
			} else
			{
				lyBTools.setVisibility(View.GONE);
			}
		}
	}

	protected void Refresh(int bFlag)
	{
		this.bFlag = bFlag;
		if (bFlag == FLAG_GROUP)
		{
			FavoriteGroupInit();
		} else if (bFlag == FLAG_FILE)
		{
			FavoriteItemInit();
		} else if (bFlag == FLAG_Folder)
		{
			FavoriteFolderInit();
		}
	}

	private void FavoriteFolderInit()
	{
		if (mNowGItem != null)
		{
			Dao dao = Dao.getInstance(m_act);
			mFolderList.clear();
			mFolderList.addAll(dao.getFavoritesFolderInfos(mNowGItem));
			dao.closeDb();
			Collections.sort(mFolderList, new FileSort());
			mFolderList.add(0, new FileBean(null, "..", "back", true));
			mTempList.clear();
			mTempList.addAll(mFolderList);
			if (mFolderAdapter != null)
			{
				mFolderAdapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * 指定文件夹文件
	 * 
	 * @param info
	 */
	private void FavoriteFolderItem(FGroupInfo info, String path)
	{
		if (info != null)
		{
			Dao dao = Dao.getInstance(m_act);
			mTempList.clear();
			mTempList.addAll(dao.getFolderItem(path, info));
			dao.closeDb();
			Collections.sort(mTempList, new FileSort());
			mTempList.add(0, new FileBean(null, "..", "back", true));
			if (mFolderAdapter != null)
			{
				mFolderAdapter.notifyDataSetChanged();
			}
		}
	}

	private void FavoriteItemInit()
	{
		FavoriteItemInit(false);
	}

	private void FavoriteItemInit(boolean bSaveSize)
	{
		if (mNowGItem != null)
		{
			mNowGItem.length = 0l;
			Dao dao = Dao.getInstance(m_act);
			mAllFileList.clear();
			mAllFileList.addAll(dao.getFavoritesInfos(mNowGItem));
			dao.closeDb();
			mNowGItem.setCount(mAllFileList.size());
			if (bSaveSize)
			{
				SaveData.Write(m_act, "FavGroupSize_" + mNowGItem.getId(),
						mNowGItem.length);
			}
			SaveData.Write(m_act, "FavGroupCount_" + mNowGItem.getId(),
					mNowGItem.getCount());

			mAllFileList.add(0, new FileBean(null, "..", "back", true));

			mTempList.clear();
			mTempList.addAll(mAllFileList);
			// Collections.sort(mAllFileList, new FileSort());
			if (mFileAdapter != null)
			{
				mFileAdapter.notifyDataSetChanged();
			}
		}
	}

	public void FavoriteSize()
	{
		Log.v("wmh", "FavoriteSize");
		Long lTotalInternalSize = StorageUtil.getTotalInternalMemorySize();
		Long lFreeInternalSize = StorageUtil.getAvailableInternalMemorySize();
		// tvTotalSpace.setText(T.FileSizeToString(size));
		tvInternalSize.setText(T.FileSizeToString(lTotalInternalSize));
		((TextView) findViewById(R.id.tvInternalAvailable)).setText(T
				.FileSizeToString(lFreeInternalSize));
		// tvTotalSpace.setText(T.FileSizeToString(file.getTotalSpace()));
		// tvFreeSpace.setText(T.FileSizeToString(file.getFreeSpace()));
		pbInternalStatus.setMax(lTotalInternalSize.intValue());
		pbInternalStatus
				.setProgress((int) (lTotalInternalSize - lFreeInternalSize));
		/**
		 * SD卡空间
		 */
		if (StorageUtil.externalMemoryAvailable())
		{
			Long lTotalSpace = StorageUtil.getTotalExternalMemorySize();
			Long lFreeSpace = StorageUtil.getAvailableExternalMemorySize();
			tvExternalSize.setText(T.FileSizeToString(lTotalSpace));
			((TextView) findViewById(R.id.tvExternalAvailable)).setText(T
					.FileSizeToString(lFreeSpace));
			pbExternalStatus.setMax((int) (lTotalSpace / 1024));
			pbExternalStatus.setProgress((int) (lTotalSpace / 1024)
					- (int) (lFreeSpace / 1024));
		} else
		{
			tvExternalSize.setText(m_act.getString(R.string.unknown));
			((TextView) findViewById(R.id.tvExternalAvailable)).setText(m_act
					.getString(R.string.unknown));
			pbExternalStatus.setMax(0);
			pbExternalStatus.setProgress(0);
		}
	}

	private void FavoriteGroupInit()
	{
		String Data;
		String FileName = m_act.getString(R.string.FavoriteType);
		try
		{
			Data = new String(T.ReadResourceAssetsFile(m_act, FileName));
			FavoriteGroupParser mFavoriteParser = new FavoriteGroupParser();
			mGroupList.clear();
			ArrayList<FGroupInfo> result = mFavoriteParser.parseJokeByData(
					m_act, Data);
			mGroupList.addAll(result);
			for (int i = 0; i < result.size(); i++)
			{
				FGroupInfo temp = result.get(i);
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
					
					((TextView) findViewById(R.id.tvMusicDesc)).setText("("
							+ temp.getCount()+")");
					break;
				case 2:
					tvPic.setText(m_act.getString(R.string.lable_pic)
							+ T.FileSizeToString(temp.getSize()));
					((TextView) findViewById(R.id.tvPhotoDesc)).setText("("
							+ temp.getCount()+")");
					break;
				case 3:
					tvVideo.setText(m_act.getString(R.string.lable_video)
							+ T.FileSizeToString(temp.getSize()));
					((TextView) findViewById(R.id.tvVideoDesc)).setText("("
							+ temp.getCount()+")");
					break;
				case 4:
					tvDocument.setText(m_act.getString(R.string.lable_document)
							+ T.FileSizeToString(temp.getSize()));
					((TextView) findViewById(R.id.tvDocDesc)).setText("("
							+ temp.getCount()+")");
					break;
				case 5:
					// tvWebPage.setText(m_act.getString(R.string.lable_webpage)
					// + T.FileSizeToString(temp.getSize()));
					break;
				case 6:
					tvZip.setText(m_act.getString(R.string.lable_zip)
							+ T.FileSizeToString(temp.getSize()));
					((TextView) findViewById(R.id.tvZipDesc)).setText("("
							+ temp.getCount()+")");
					break;
				case 7:
					tvApp.setText(m_act.getString(R.string.lable_apk)
							+ T.FileSizeToString(temp.getSize()));
					((TextView) findViewById(R.id.tvApkDesc)).setText("("
							+ temp.getCount()+")");
					break;
				}
			}
			if (mGroupAdapter != null)
			{
				mGroupAdapter.notifyDataSetChanged();
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
		case R.id.btSearch:
			FavoriteItemInit();
			// MobclickAgent.onEvent(m_act, "FavoriteEvent","Search");
			break;
		case R.id.btAllFile:
			SwitchStyle(FLAG_FILE);
			break;
		case R.id.btFolder:
			SwitchStyle(FLAG_Folder);
			Refresh(FLAG_Folder);
			break;
		case R.id.btAudioGroup:
			SwitchGroup(0);
			break;
		case R.id.btVideoGroup:
			SwitchGroup(2);
			break;
		case R.id.btPhotoGroup:
			SwitchGroup(1);
			break;
		case R.id.btDocumentGroup:
			SwitchGroup(3);
			break;
		case R.id.btApkGroup:
			SwitchGroup(5);
			break;
		case R.id.btZipGroup:
			SwitchGroup(4);
			break;
		case R.id.btfavoriteGroup://收藏
			break;
		case R.id.btListStyle:
			// MobclickAgent.onEvent(m_act, "FavoriteEvent","ListStyle");
			// if (nStyle == 0)
			// {
			// SwitchStyle(bFlag, 1);
			// }
			// else
			// {
			// SwitchStyle(bFlag, 0);
			// }
			break;
		case R.id.btRefresh:
			// MobclickAgent.onEvent(m_act, "FavoriteEvent","Refresh");
			KDialog.ShowDialog(m_act, m_act.getString(R.string.ScanTitle),
					m_act.getString(R.string.ScanReContent),
					m_act.getString(R.string.ok), clPositive,
					m_act.getString(R.string.cancel), null);
			break;
		case R.id.btBack:
			// MobclickAgent.onEvent(m_act, "FavoriteEvent","Back");
			SwitchStyle(FLAG_GROUP);
			break;
		case R.id.btDelete:
			// MobclickAgent.onEvent(m_act, "FavoriteEvent","Del");
			deletefiles();
			break;
		case R.id.btSelectAll:
			// MobclickAgent.onEvent(m_act, "FavoriteEvent","SelAll");
			SelectAll();
			break;
		// case R.id.btSelectVisible:
		// MobclickAgent.onEvent(m_act, "FavoriteEvent","SelVisible");
		// Selected(mFileAdapter.isSelected());
		// break;
		}
	}

	private void SelectAll()
	{
		if (mTempList.size() > 2)
		{
			boolean check = !mTempList.get(1).isChecked();
			for (int i = 1; i < mTempList.size(); i++)
			{
				FileBean tmpInfo = mTempList.get(i);
				tmpInfo.setChecked(check);
			}
			if (bFlag == FLAG_FILE)
			{
				mFileAdapter.notifyDataSetChanged();
			} else if (bFlag == FLAG_Folder)
			{
				mFolderAdapter.notifyDataSetChanged();
			}
		}
	}

	public void DeleteFile(FileBean file)
	{
		SysEng.getInstance().addEvent(
				new delFileEvent(m_act, file, FavoriteItemPage.this));
		// if (bFlag == FLAG_FILE)
		// {
		// mAllFileList.remove(file);
		// mFileAdapter.notifyDataSetChanged();
		// }
		// if (bFlag == FLAG_Folder)
		// {
		// mTempList.remove(file);
		// mFolderAdapter.notifyDataSetChanged();
		// }
	}

	private void deletefiles()
	{
		final ArrayList<FileBean> mDelFiles = new ArrayList<FileBean>();
		for (int i = 0; i < mTempList.size(); i++)
		{
			FileBean tmpInfo = mTempList.get(i);
			
			if (tmpInfo.isChecked())
			{
				mDelFiles.add(tmpInfo);
			}
		}
		if (mDelFiles.size() > 0)
		{
			new AlertDialog.Builder(m_act)
					.setTitle("提示!")
					.setMessage("确定删除已选的文件吗?")
					.setPositiveButton(m_act.getString(R.string.ok),
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int which)
								{
									Dao dao = Dao.getInstance(m_act);
									for (FileBean fileBean : mDelFiles)
									{
										if (fileBean.isDirectory())
										{
											FavorFileBean bean1=(FavorFileBean) fileBean;
											dao.deleteFavorites(bean1.getId());
											mDelFiles.remove(fileBean);
										}
									}
									dao.closeDb();
									mNowGItem.setCount(mNowGItem.getCount()
											- mDelFiles.size());
									SaveData.Write(m_act, "FavGroupCount_"
											+ mNowGItem.getId(),
											mNowGItem.getCount());
									SysEng.getInstance().addEvent(
											new delFileEvent(m_act, mDelFiles,
													FavoriteItemPage.this));
								}
							})
					.setNegativeButton(m_act.getString(R.string.cancel), null)
					.show();
			return;
		} else
		{
			Toast.makeText(m_act, "请选择需要删除的文件!", Toast.LENGTH_SHORT).show();
		}
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		// case R.id.muListView:
		// SwitchStyle(bFlag, !nStyle);
		// break;
		case R.id.muScan:
			mLoadSDFileEvent = new LoadSDFolderEvent(m_act, false, mGroupList,
					FavoriteItemPage.this);
			SysEng.getInstance().addThreadEvent(mLoadSDFileEvent);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		P.debug("onCreateOptionsMenu");
		return super.onCreateOptionsMenu(menu, R.menu.favoritepagemenu);
	}

	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// return super.onCreateOptionsMenu(menu, R.menu.favoritepagemenu);
		super.onPrepareOptionsMenu(menu);
		P.debug("onPrepareOptionsMenu");
		return true;
	}

	public void onStart()
	{
		P.v("NullPointError", "onStart");
	}

	/** 注销广播 */

	public void onDestroy()
	{

	}

	public void clear()
	{

	}

	public boolean onTouchEvent(MotionEvent event)
	{
		return false;
	}

	public void SwitchGroup(int pos)
	{
		mNowGItem = mGroupList.get(pos);
		this.bFlag = FLAG_FILE;
		FavoriteItemInit(true);
		SwitchStyle(FLAG_FILE);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		if (bFlag == FLAG_FILE)
		{
			if (position == 0)
			{	// 返回到上一层
				Refresh(FLAG_GROUP);
				SwitchStyle(FLAG_GROUP);
				return;
			}
			FileBean temp = mAllFileList.get(position);
			final File mFile = temp.getFile();
			if (!mFile.isDirectory())
			{
				SysEng.getInstance().addHandlerEvent(
						new openDefFileEvent(m_act, mFile.getPath()));
			} else
			{
				FileManager.GetHandler().setFilePath(mFile.getPath());
				KMainPage.mKMainPage.ChangePage(KMainPage.Local, null);
			}
		} else if (bFlag == FLAG_Folder)
		{
			if (position == 0)
			{// 返回到上一层
				if (nFolderType == 1)
				{
					nFolderType = 0;
					mTempList.clear();
					mTempList.addAll(mFolderList);
					if (mFolderAdapter != null)
					{
						mFolderAdapter.notifyDataSetChanged();
					}
				} else
				{
					SwitchStyle(FLAG_GROUP);
				}
				return;
			}
			FileBean temp = mTempList.get(position);
			final File mFile = temp.getFile();
			if (!mFile.isDirectory())
			{
				SysEng.getInstance().addHandlerEvent(
						new openDefFileEvent(m_act, mFile.getPath()));
			} else
			{
				mFolderPath = mFile.getPath();
				FavoriteFolderItem(mNowGItem, mFolderPath);
				nFolderType = 1;
			}
		}
	}

	private String mFolderPath;
	private int nFolderType = 0;// 0:文件夹 1:文件

	public void NotifyDataSetChanged(final int cmd, Object value)
	{
		mNotifyData.setKey(cmd);
		mNotifyData.setValue(value);
		SysEng.getInstance().addHandlerEvent(mNotifyData);
	}

	private ParamEvent mNotifyData = new ParamEvent()
	{

		public void ok()
		{
			switch (getKey())
			{
			case Const.cmd_DelFileEvent_Finish:
				if (bFlag == FLAG_Folder && 1 == nFolderType)
				{
					FavoriteFolderItem(mNowGItem, mFolderPath);
				} else
				{
					Refresh(bFlag);
				}
				break;
			case Const.cmd_LoadSDFile_Error:
				pbLoading.setVisibility(View.GONE);
				break;
			case Const.cmd_LoadSDFile_Init:
				Long value = (Long) getValue();
				pbSDFileStatus.setMax(value.intValue());
				pbSDFileStatus.setProgress(0);
				tvSDFileStatus.setText("");
				tvMessage.setText("");
				break;
			case Const.cmd_LoadSDFile_Start:
				pbLoading.setVisibility(View.VISIBLE);
				mListView.setVisibility(View.GONE);
				btRefresh.setVisibility(View.GONE);
				break;
			case Const.cmd_LoadSDFile_State:
				LoadSDFolderEvent.LoadSDFile_State staValue = (LoadSDFolderEvent.LoadSDFile_State) getValue();
				tvSDFileStatus.setText("正在遍历SD卡,请耐心等待!\n已遍历:"
						+ staValue.Progress + "文件");
				tvMessage.setText(staValue.strPath);
				break;
			case Const.cmd_LoadSDFile_Finish:
				pbLoading.setVisibility(View.GONE);
				Refresh(bFlag);
				SwitchStyle(bFlag);
				Toast.makeText(m_act,
						m_act.getString(R.string.msg_Scan_Finish),
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3)
	{
		// MobclickAgent.onEvent(m_act, "FavoriteEvent","ItemLongClick");
		FileBean temp = null;
		if (bFlag == FLAG_FILE)
		{
			temp = mFileAdapter.getItem(arg2);
		}
		if (bFlag == FLAG_Folder)
		{
			temp = mFolderAdapter.getItem(arg2);
		}
		if (temp != null && !temp.isBackUp())
		{
			if (temp.getFile().isDirectory())
			{
				ShowFavorDialog(m_act, temp);
			} else
			{
				ShowFavorDialog(m_act, temp);
			}
		}
		return false;
	}

	/** 长按文件或文件夹时弹出的带ListView效果的功能菜单 */
	public void ShowFavorDialog(final Context context, final FileBean file)
	{
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int item)
			{
				dialog.cancel();
				switch (item)
				{
				case 0: // 打开
					if (file.getFile().canRead())
					{
						SysEng.getInstance().addHandlerEvent(
								new openFileEvent(context, file.getFilePath()));
					} else
					{
						Toast.makeText(
								context,
								context.getString(R.string.msg_can_not_operated),
								Toast.LENGTH_SHORT).show();
					}
					break;
				case 1:// 打开文件夹
					FileManager.GetHandler().setFilePath(
							file.getFilePath().substring(
									0,
									file.getFilePath().lastIndexOf(
											File.separator)));
					KMainPage.mKMainPage.ChangePage(KMainPage.Local, null);
					break;
				case 2:// 删除
					DeleteFile(file);
					break;
				case 3: // 发送
					T.ShareIntent(context,
							context.getString(R.string.msg_Send),
							file.getFilePath());
					break;
				case 4:// 收藏
					SysEng.getInstance().addEvent(
							new FavoriteFileEvent(context, file, 0));
					break;
				case 5: // 属性
					KDialog.ShowDetailsDialog(context, file.getFilePath());
					break;
				}
			}
		};
		String[] mMenu =
		{ "打开", "打开所在文件夹", "删除", "发送", "收藏", "属性" };
		new AlertDialog.Builder(context)
				.setTitle(context.getString(R.string.msg_please_operate))
				.setItems(mMenu, listener)
				.setPositiveButton(context.getString(R.string.cancel), null)
				.show();
	}
}