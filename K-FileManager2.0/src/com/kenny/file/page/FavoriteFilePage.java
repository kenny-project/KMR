package com.kenny.file.page;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.framework.interfaces.MenuAble;
import com.framework.log.P;
import com.framework.page.AbsPage;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.KFileManager.R.color;
import com.kenny.file.Adapter.FavorFileAdapter;
import com.kenny.file.Event.FavoriteFileEvent;
import com.kenny.file.Event.InstallEvent;
import com.kenny.file.Event.cutFileCmdEvent;
import com.kenny.file.Event.delFileEvent;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.Event.openFileEvent;
import com.kenny.file.bean.FGroupInfo;
import com.kenny.file.bean.FavorFileBean;
import com.kenny.file.bean.FileBean;
import com.kenny.file.commui.ListHeaderView;
import com.kenny.file.db.Dao;
import com.kenny.file.dialog.KDialog;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.manager.FileManager;
import com.kenny.file.sort.FileSort;
import com.kenny.file.tools.SaveData;
import com.kenny.file.tools.T;
import com.kenny.file.util.Const;
import com.kenny.file.util.Theme;

public class FavoriteFilePage extends AbsPage implements MenuAble,
		INotifyDataSetChanged, OnItemClickListener, OnClickListener,
		OnItemLongClickListener
{

	private ListView mListView;
	private Button btAllFile, btFolder;
	private Button btBack, btInstall, btArrange, btDelete, btSelectAll;
	private View m_lvMain;// 主页面
	private FavorFileAdapter mFileAdapter;
	private ArrayList<FileBean> mAllFileList = new ArrayList<FileBean>();
	private ArrayList<FileBean> mFolderList = new ArrayList<FileBean>();
	private ArrayList<FileBean> mTempList = new ArrayList<FileBean>();
	private FGroupInfo mNowGItem; // 当前正在浏览的分组
	private String[] mStrFolderpaths;// 获取当前分类所有的文件路径
	private String mFolderPath;
//	private boolean isFolderType = false;// 判断是不是文件夹类型
	enum ListType 
	{
		FolderList,AllFileList,SpecifyPathFileList
	} ;
	ListType mListType=ListType.FolderList;
	
	public FavoriteFilePage(Activity context, FGroupInfo mNowGItem)
	{
		super(context);
		this.mNowGItem = mNowGItem;
	}

	private View FooterView()
	{
		TextView tview = new TextView(m_act);
		tview.setHeight(100);
		tview.setWidth(-1);
		tview.setBackgroundColor(color.green);
		ListHeaderView headerView = new ListHeaderView(m_act, tview);
		return headerView;
	}

	private OnScrollListener mListViewOnScrollListener = new OnScrollListener()
	{
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			switch (scrollState)
			{
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
				// P.debug("SCROLL_STATE_FLING");
				if (mFileAdapter != null)
					mFileAdapter.setShowLogo(false);
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
				// P.debug("SCROLL_STATE_IDLE");
				if (mFileAdapter != null)
				{
					mFileAdapter.setShowLogo(true);
					mFileAdapter.notifyDataSetChanged();
				}
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				// P.debug("SCROLL_STATE_TOUCH_SCROLL");
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
		setContentView(R.layout.favoritempanel);
		m_lvMain = findViewById(R.id.lvMain);
		m_lvMain.setBackgroundColor(Theme.getBackgroundColor());
		mListView = (ListView) findViewById(R.id.lvLocallist);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(mListViewOnScrollListener);
		mListView.setOnItemLongClickListener(this);
		mListView.addFooterView(FooterView(), null, false);

		btAllFile = (Button) findViewById(R.id.btAllFile);
		btAllFile.setOnClickListener(this);
		btFolder = (Button) findViewById(R.id.btFolder);
		btFolder.setOnClickListener(this);

		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(this);

		btArrange = (Button) findViewById(R.id.btArrange);
		btArrange.setOnClickListener(this);

		btInstall = (Button) findViewById(R.id.btInstall);
		btInstall.setOnClickListener(this);

		btDelete = (Button) findViewById(R.id.btDelete);
		btDelete.setOnClickListener(this);

		btSelectAll = (Button) findViewById(R.id.btSelectAll);
		btSelectAll.setOnClickListener(this);

		String strPaths = SaveData.Read(m_act,
				"FavGroupPaths_" + mNowGItem.getId(), "");
		mStrFolderpaths = strPaths.split("\n");

		ShowFolderList();
	}

	public boolean onKeyDown(int keyCode, KeyEvent msg)
	{
		// 弹出退出对话框
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (mListType!=ListType.FolderList)
			{
				ShowFolderList();
				return true;
			}
		}
		return false;
	}

	public void onPause()
	{

	}

	public void onResume()
	{
		m_lvMain.setBackgroundColor(Theme.getBackgroundColor());
	}

	private void LoadFolderInit()
	{
		if (mNowGItem != null)
		{
			mFolderList.clear();
			mFolderList.addAll(getFavoritesFolderInfos(mNowGItem));
			Collections.sort(mFolderList, new FileSort());
			// mTempList.clear();
			// mTempList.addAll(mFolderList);
			// if (mTempList.size() > 0)
			// {
			// FavorFileBean bean = new FavorFileBean(null, "ALL", "ALL", true);
			// bean.setDirectory(true);
			// mTempList.add(0, bean);
			// }
			// if (mFileAdapter != null)
			// {
			// mFileAdapter.notifyDataSetChanged();
			// }
		}
	}

	/**
	 * 获取相关类的文件数
	 * 
	 * @param mCurrentFile
	 * @param groupInfo
	 * @return
	 */
	private int getFavorFileSize(File mCurrent, FGroupInfo groupInfo)
	{
		int count = 0;
		Long size = 0l;
		String[] ends = groupInfo.getArrayEnd();
		File[] files = mCurrent.listFiles();
		if (files != null)
		{
			for (File file : files)
			{
				if (!file.isDirectory())
				{
					String strFileName = file.getName();
					String fileEnds = strFileName.substring(
							strFileName.lastIndexOf(".") + 1).toLowerCase();// 取出文件后缀名并转成小写
					for (String end : ends)
					{
						if (end.equals(fileEnds))
						{
							count++;
							size += file.length();
							break;
						}
					}
				}
			}
			groupInfo.setCount(groupInfo.getCount() + count);
			groupInfo.setSize(groupInfo.getSize() + count);
		}
		return count;
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
			mTempList.clear();
			mTempList.addAll(getFolderItem(path, info));
			Collections.sort(mTempList, new FileSort());
			mTempList.add(0, new FileBean(null, "..", "back", true));
			if (mFileAdapter != null)
			{
				mFileAdapter.notifyDataSetChanged();
			}
		}
	}

	private List<FileBean> getFolderItem(String path, FGroupInfo groupInfo)
	{
		List<FileBean> list = new ArrayList<FileBean>();
		File mCurrent = new File(path);
		if (mCurrent.isDirectory())
		{
			for (File file : mCurrent.listFiles())
			{
				if (!file.isDirectory())
				{
					BuildFavorFolderFile(file, groupInfo, list);
				}
			}
		} else
		{
			BuildFavorFolderFile(mCurrent, groupInfo, list);
		}
		return list;
	}

	public boolean BuildFavorFolderFile(File mCurrentFile,
			FGroupInfo groupInfo, List<FileBean> list)
	{
		String[] ends = groupInfo.getArrayEnd();
		String strFileName = mCurrentFile.getName();
		String fileEnds = strFileName.substring(
				strFileName.lastIndexOf(".") + 1).toLowerCase();// 取出文件后缀名并转成小写
		for (String end : ends)
		{
			if (end.equals(fileEnds))
			{
				FavorFileBean bean = new FavorFileBean(mCurrentFile,
						mCurrentFile.getName(), mCurrentFile.getPath(), false);
				bean.setDirectory(mCurrentFile.isDirectory());
				bean.setLength(mCurrentFile.length());
				list.add(bean);
				return true;
			}
		}
		return false;
	}

	/**
	 * 得到相应分类的全部信息 bFlag:true:记录总的文件数及文件大小,boolean bFlag
	 */
	private List<FileBean> getFavoritesFolderInfos(FGroupInfo groupInfo)
	{
		List<FileBean> list = new ArrayList<FileBean>();
		groupInfo.setCount(0);
		groupInfo.setSize(0);
		for (int i = 0; i < mStrFolderpaths.length; i++)
		{
			File mCurrent = new File(mStrFolderpaths[i]);
			if (mCurrent.exists() && mCurrent.isDirectory())// 只处理文件夹
			{
				FavorFileBean bean = new FavorFileBean(mCurrent,
						mCurrent.getName(), mCurrent.getPath(), false);
				bean.setDirectory(mCurrent.isDirectory());
				int count = getFavorFileSize(mCurrent, groupInfo);
				if (count > 0)
				{
					bean.setItemFileCount(count);
					list.add(bean);
				}
			}
		}
		SaveData.Write(m_act, "FavGroupSize_" + groupInfo.getId(),
				groupInfo.getSize());
		SaveData.Write(m_act, "FavGroupCount_" + groupInfo.getId(),
				groupInfo.getCount());
		return list;
	}

	/**
	 * 得到相应分类的全部信息 bFlag:true:记录总的文件数及文件大小
	 */
	public List<FileBean> getFavoritesInfos(List<FileBean> mFolderList)
	{
		List<FileBean> list = new ArrayList<FileBean>();

		for (int i = 0; i < mFolderList.size(); i++)
		{
			File mCurrent = mFolderList.get(i).getFile();
			try
			{
				if (mCurrent.isDirectory())
				{
					for (File file : mCurrent.listFiles())
					{
						if (!file.isDirectory())
						{
							BuildFavorFile(file, mNowGItem, list);
						}
					}
				} else
				{
					BuildFavorFile(mCurrent, mNowGItem, list);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}

	public boolean BuildFavorFile(File mCurrentFile, FGroupInfo groupInfo,
			List<FileBean> list)
	{
		String[] ends = groupInfo.getArrayEnd();
		String strFileName = mCurrentFile.getName();
		String fileEnds = strFileName.substring(
				strFileName.lastIndexOf(".") + 1).toLowerCase();// 取出文件后缀名并转成小写
		for (String end : ends)
		{
			if (end.equals(fileEnds))
			{
				FavorFileBean bean = new FavorFileBean(mCurrentFile,
						mCurrentFile.getName(), mCurrentFile.getPath(), false);
				bean.setDirectory(mCurrentFile.isDirectory());
				bean.setLength(mCurrentFile.length());
				groupInfo.length += mCurrentFile.length();
				list.add(bean);
				return true;
			}
		}
		return false;
	}

	private boolean FavoriteItemInit(boolean bSaveSize,
			List<FileBean> mFolderList)
	{
		if (mNowGItem != null)
		{
			mNowGItem.length = 0l;
			mAllFileList.clear();
			mAllFileList.addAll(getFavoritesInfos(mFolderList));
			mNowGItem.setCount(mAllFileList.size());
			if (bSaveSize)
			{
				SaveData.Write(m_act, "FavGroupSize_" + mNowGItem.getId(),
						mNowGItem.length);
			}
			SaveData.Write(m_act, "FavGroupCount_" + mNowGItem.getId(),
					mNowGItem.getCount());
			return true;
		}
		return false;
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		// case R.id.btAllFile:
		// SwitchStyle(FLAG_FILE);
		// break;
		// case R.id.btFolder:
		// SwitchStyle(FLAG_Folder);
		// break;
		case R.id.btInstall:
			InstallApp();
			break;
		case R.id.btArrange:
			Arrangefiles();
			break;
		case R.id.btBack:
			// MobclickAgent.onEvent(m_act, "FavoriteEvent","Back");
			// SwitchStyle(FLAG_GROUP);
			m_act.onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(1, 1));
			break;
		case R.id.btDelete:
			// MobclickAgent.onEvent(m_act, "FavoriteEvent","Del");
			deletefiles();
			break;
		case R.id.btSelectAll:
			// MobclickAgent.onEvent(m_act, "FavoriteEvent","SelAll");
			SelectAll();
			break;
		}
	}

	private void InstallApp()
	{
		final ArrayList<FileBean> mInstallFiles = new ArrayList<FileBean>();
		for (int i = 0; i < mTempList.size(); i++)
		{
			FileBean tmpInfo = mTempList.get(i);

			if (tmpInfo.isChecked())
			{
				mInstallFiles.add(tmpInfo);
			}
		}
		if (mInstallFiles.size() > 0)
		{
			SysEng.getInstance().addEvent(
					new InstallEvent(m_act, mInstallFiles,
							FavoriteFilePage.this));
			return;
		}
		Toast.makeText(m_act,
				m_act.getString(R.string.msg_Please_select_instal_file) + "!",
				Toast.LENGTH_SHORT).show();
	}

	private void SelectAll()
	{
		if (mTempList.size() > 1)
		{
			boolean check = !mTempList.get(1).isChecked();
			for (int i = 1; i < mTempList.size(); i++)
			{
				FileBean tmpInfo = mTempList.get(i);
				tmpInfo.setChecked(check);
			}
			mFileAdapter.notifyDataSetChanged();
		}
	}

	public void DeleteFile(FileBean file)
	{
		SysEng.getInstance().addEvent(
				new delFileEvent(m_act, file, FavoriteFilePage.this));
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

	private void Arrangefiles()
	{
		final ArrayList<FileBean> mSelectFiles = new ArrayList<FileBean>();
		for (int i = 0; i < mTempList.size(); i++)
		{
			FileBean tmpInfo = mTempList.get(i);

			if (tmpInfo.isChecked())
			{
				mSelectFiles.add(tmpInfo);
			}
		}
		if (mSelectFiles.size() > 0)
		{
			new AlertDialog.Builder(m_act)
					.setTitle("提示!")
					.setMessage("确定整理已选的文件吗?\n这些文件将会拷贝到整理箱，并删除源文件")
					.setPositiveButton(m_act.getString(R.string.ok),
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int which)
								{
									Dao dao = Dao.getInstance(m_act);
									for (FileBean fileBean : mSelectFiles)
									{
										if (fileBean.isDirectory())
										{
											FavorFileBean bean1 = (FavorFileBean) fileBean;
											dao.deleteFavorites(bean1.getId());
										}
									}
									dao.closeDb();
									mNowGItem.setCount(mNowGItem.getCount()
											- mSelectFiles.size());
									SaveData.Write(m_act, "FavGroupCount_"
											+ mNowGItem.getId(),
											mNowGItem.getCount());
									SysEng.getInstance()
											.addEvent(
													new cutFileCmdEvent(
															m_act,
															Const.szAppPath
																	+ mNowGItem
																			.getTitle(),
															mSelectFiles,
															FavoriteFilePage.this));
								}
							})
					.setNegativeButton(m_act.getString(R.string.cancel), null)
					.show();
			return;
		} else
		{
			Toast.makeText(m_act, "请选择需要整理的文件!", Toast.LENGTH_SHORT).show();
		}
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
											FavorFileBean bean1 = (FavorFileBean) fileBean;
											dao.deleteFavorites(bean1.getId());
											// mTempList.remove(fileBean);
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
													FavoriteFilePage.this));
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
		return super.onOptionsItemSelected(item);
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

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		FileBean temp = mTempList.get(position);
		if (position == 0)
		{
			if (mListType!=ListType.FolderList)
			{ // 返回到上一层
				ShowFolderList();
				return;
			}
			if (temp.getFileName().equals("ALL"))
			{ // 加载全部该类型文件
				ShowAllFileList();
				return;
			}
		}
		final File mFile = temp.getFile();
		if (temp.isDirectory())
		{	// 文件夹
			ShowSpecifyPathFileList(mFile.getPath());
		}
		else
		{	// 文件
			SysEng.getInstance().addHandlerEvent(
					new openDefFileEvent(m_act, mFile.getPath()));
		}
	}

	// /**
	// * 切换窗体视图 false:ListView true:GridView
	// */
	// private void SwitchStyle()
	// {
	// btInstall.setVisibility(View.GONE);
	// btDelete.setVisibility(View.GONE);
	// btSelectAll.setVisibility(View.GONE);
	// btArrange.setVisibility(View.GONE);
	// if(mFileAdapter==null)
	// {
	// mFileAdapter = new FavorFileAdapter(m_act, 1, mTempList);
	// mListView.setAdapter(mFileAdapter);
	// }
	// mTempList.clear();
	// mTempList.addAll(mFolderList);
	// if (mTempList.size() > 0)
	// {
	// FavorFileBean bean = new FavorFileBean(null, "ALL", "ALL",
	// false);
	// bean.setDirectory(true);
	// mTempList.add(0, bean);
	// }
	// if (mFileAdapter != null)
	// {
	// mFileAdapter.notifyDataSetChanged();
	// }
	// nFolderType=1;
	// }

	/**
	 * 加载指定分类文件夹列表
	 */
	private void ShowFolderList()
	{
		mListType=ListType.FolderList;
		mTempList.clear();
		if (mFolderList.size() <= 0)
		{
			LoadFolderInit();
		}
		mTempList.addAll(mFolderList);
		if (mFileAdapter == null)
		{
			mFileAdapter = new FavorFileAdapter(m_act, 1, mTempList);
			mListView.setAdapter(mFileAdapter);
		}
		btInstall.setVisibility(View.GONE);
		btDelete.setVisibility(View.GONE);
		btSelectAll.setVisibility(View.GONE);
		btArrange.setVisibility(View.GONE);

		if (mTempList.size() > 0)
		{
			FavorFileBean bean = new FavorFileBean(null, "ALL", "ALL", false);
			bean.setDirectory(true);
			mTempList.add(0, bean);
		}
		if (mFileAdapter != null)
		{
			mFileAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 显示所有文件列表
	 */
	private void ShowAllFileList()
	{
		mListType=ListType.AllFileList;
		btDelete.setVisibility(View.VISIBLE);
		btSelectAll.setVisibility(View.VISIBLE);
		btArrange.setVisibility(View.VISIBLE);
		if (mNowGItem.getId() == 7)
		{
			btInstall.setVisibility(View.VISIBLE);
		} else
		{
			btInstall.setVisibility(View.GONE);
		}
		if (mAllFileList.size() <= 0)
		{
			FavoriteItemInit(true, mFolderList);
		}
		mTempList.clear();
		mTempList.addAll(mAllFileList);
		if (mFileAdapter != null)
		{
			mFileAdapter.notifyDataSetChanged();
		}
	}
	/**
	 * 指定路径的文件列表
	 * @param path
	 */
	private void ShowSpecifyPathFileList(String path)
	{
		mListType=ListType.SpecifyPathFileList;
		mFolderPath = path;
		FavoriteFolderItem(mNowGItem, mFolderPath);
		btDelete.setVisibility(View.VISIBLE);
		btSelectAll.setVisibility(View.VISIBLE);
		btArrange.setVisibility(View.VISIBLE);
		if (mNowGItem.getId() == 7)
		{
			btInstall.setVisibility(View.VISIBLE);
		} else
		{
			btInstall.setVisibility(View.GONE);
		}
		if (mFileAdapter != null)
		{
			mFileAdapter.notifyDataSetChanged();
		}
	}
	public void Refresh()
	{
		
		if(mListType==ListType.SpecifyPathFileList)
		{
			ShowSpecifyPathFileList(mFolderPath);
		}
		else if(mListType==ListType.AllFileList)
		{
			mAllFileList.clear();
			ShowAllFileList();
		}
		else if(mListType==ListType.FolderList)
		{
			ShowFolderList();
		}

	}
	public void NotifyDataSetChanged(final int cmd, Object value)
	{
		switch (cmd)
		{
		case Const.cmd_DelFileEvent_Finish:
		case Const.cmd_CutFileEvent_Finish:
			Refresh();
			break;
		}
	}

	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3)
	{
		FileBean temp = null;
		if (mListType!=ListType.FolderList)
		{
			temp = mFileAdapter.getItem(arg2);
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
					FileManager.getInstance().setFilePath(
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