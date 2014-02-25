package com.kenny.Slidingmenu.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Adapter.FileAdapter;
import com.kenny.file.Application.KFileManagerApp;
import com.kenny.file.Event.InstallEvent;
import com.kenny.file.Event.copyFileEvent;
import com.kenny.file.Event.cutFileEvent;
import com.kenny.file.Event.delFileEvent;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.bean.FileBean;
import com.kenny.file.dialog.CreateFileDialog;
import com.kenny.file.dialog.LocalAddressDialog;
import com.kenny.file.dialog.ViewSortDialog;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.manager.FileManager;
import com.kenny.file.menu.MGoneAinm;
import com.kenny.file.menu.MVisibleAinm;
import com.kenny.file.menu.PopLocalMenu;
import com.kenny.file.util.Const;
import com.kenny.file.util.Theme;
import com.umeng.analytics.MobclickAgent;

public class LocalPage extends ContentFragment implements
		OnItemLongClickListener, INotifyDataSetChanged, OnClickListener,
		OnItemClickListener
{
	private List<FileBean> mFileList = null;
	private HashMap<String, Integer> mScrollList = new HashMap<String, Integer>();
	private ViewGroup m_lvMain;// 主页面
	private TextView mPath;
	private ListView m_locallist;
	private GridView m_localGrid;
	private FileAdapter fileAdapter;
	private FileManager localManage;
	private int nStyle = 0, nSortMode = 0; // false:listView true:gridView
	private Button btMenuListSort, btMenuListMode, btMenuShowOrHide,
			btMenuSetting;
	
	private String mStrPath;

	public LocalPage(String path)
	{
		mStrPath = new File(path).getAbsolutePath();
	}
	/**
	 * The Fragment's UI is just a simple text view showing its instance number.
	 */
	@Override
	public void onCreate(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		setContentView(R.layout.localpage, inflater);
		mView.findViewById(R.id.icEmptyPannal).setVisibility(View.GONE);

		m_lvMain = (ViewGroup) mView.findViewById(R.id.lvMain);

		localManage = new FileManager();
		localManage.setContext(getActivity());
		localManage.setRootPath(mStrPath);
		localManage.setFilePath(mStrPath, Const.cmd_Local_List_Go);

		m_locallist = (ListView) mView.findViewById(R.id.lvLocallist);
		m_locallist.setOnScrollListener(m_localOnScrollListener);
		m_locallist.setOnItemLongClickListener(this);
		m_locallist.setOnItemClickListener(this);
		m_localGrid = (GridView) mView.findViewById(R.id.gvLocallist);
		m_localGrid.setOnItemClickListener(this);
		m_localGrid.setOnItemLongClickListener(this);
		m_localGrid.setOnScrollListener(m_localOnScrollListener);
		mPath = (TextView) mView.findViewById(R.id.mCurrentPath);
		mPath.setOnClickListener(new OnClickListener()
		{

			public void onClick(View v)
			{
				MobclickAgent.onEvent(m_act, "localEvent", "LocalAddress");
				Rect rect = new Rect();
				mView.findViewById(R.id.lyTools2).getGlobalVisibleRect(rect);
				LocalAddressDialog.ShowDialog(m_act,
						localManage.getCurrentPath(),localManage);
			}
		});
		mFileList = localManage.getFileList();
		localManage.setNotifyData(this);
		mView.findViewById(R.id.btFileCreate).setOnClickListener(this);
		mView.findViewById(R.id.btNew).setOnClickListener(this);
		mView.findViewById(R.id.btMore).setOnClickListener(this);
		mView.findViewById(R.id.btBack).setOnClickListener(this);
		mView.findViewById(R.id.btListSort).setOnClickListener(this);
		mView.findViewById(R.id.btCopy).setOnClickListener(this);
		mView.findViewById(R.id.btCut).setOnClickListener(this);
		mView.findViewById(R.id.btDelete).setOnClickListener(this);
		mView.findViewById(R.id.btPaste).setOnClickListener(this);
		mView.findViewById(R.id.btSelectAll).setOnClickListener(this);
		mView.findViewById(R.id.btInstall).setOnClickListener(this);
		
		SwitchStyle(Theme.getStyleMode());
		onCreateMenu();
		m_lvMain.addView(LoadingView(), new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		P.v("LocalPage:creat end");
	}

	public void onCreateMenu()
	{
		final RelativeLayout rlMenu = (RelativeLayout) mView
				.findViewById(R.id.ip_menu);
		rlMenu.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{ // 所有的点击全部关闭菜单
				rlMenu.setVisibility(View.GONE);
				return false;
			}
		});
		final MGoneAinm goneAnim = new MGoneAinm(m_act, rlMenu,
				mView.findViewById(R.id.main_menu));
		Button btMore = (Button) mView.findViewById(R.id.btMore);
		btMore.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (rlMenu.getVisibility() == View.VISIBLE)
				{
					goneAnim.ShowAnim();
					// rlMenu.setVisibility(View.GONE);
				} else
				{
					if (Theme.getShowHideFile())
					{
						btMenuShowOrHide
								.setText(R.string.btMenuShowOrHide_False);
					} else
					{
						btMenuShowOrHide
								.setText(R.string.btMenuShowOrHide_True);
					}
					if (Theme.getStyleMode() == 1)
					{
						btMenuListMode.setText(R.string.btMenuListMode_List);
					} else
					{
						btMenuListMode.setText(R.string.btMenuListMode_Grid);
					}
					new MVisibleAinm(m_act, rlMenu, mView
							.findViewById(R.id.main_menu)).ShowAnim();
				}
			}
		});
		OnClickListener menuListener = new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				goneAnim.ShowAnim();
				switch (v.getId())
				{
				case R.id.btMenuListSort:
					new ViewSortDialog().ShowDialog(m_act, LocalPage.this);
					break;
				case R.id.btMenuListMode:
					if (nStyle == 1)
					{
						SwitchStyle(0);
					} else
					{
						SwitchStyle(1);
					}
					break;
				case R.id.btMenuShowOrHide:
					Theme.setShowHideFile(!Theme.getShowHideFile());
					Theme.Save(m_act);
					localManage.setFilePath(localManage.getCurrentPath(),
							Const.cmd_Local_List_Go);
					break;
				}
			}
		};
		btMenuListSort = (Button) mView.findViewById(R.id.btMenuListSort);
		btMenuListSort.setOnClickListener(menuListener);
		btMenuListMode = (Button) mView.findViewById(R.id.btMenuListMode);
		btMenuListMode.setOnClickListener(menuListener);
		btMenuShowOrHide = (Button) mView.findViewById(R.id.btMenuShowOrHide);
		btMenuShowOrHide.setOnClickListener(menuListener);
	}

	@Override
	public void onDestroyView()
	{
		// TODO Auto-generated method stub
		Log.v("wmh", "onDestroyView()");
		super.onDestroyView();
	}

	public void onResume()
	{
		super.onResume();
		P.v("Log.DEBUG", "onResume");
		localManage.setFilePath(mStrPath,
				Const.cmd_Local_List_Go);
		IntentFilter sdCardFilter = new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED);
		sdCardFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		sdCardFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		sdCardFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		sdCardFilter.addDataScheme("file");
		this.m_act.registerReceiver(sdcardReceiver, sdCardFilter);// 注册监听函数
		setTitle(new File(mStrPath).getName());
		m_lvMain.setBackgroundColor(Theme.getBackgroundColor());
		if (nSortMode != Theme.getSortMode())
		{
			nSortMode = Theme.getSortMode();
			localManage.setFilePath(mStrPath,
					Const.cmd_Local_List_Go);
			SwitchStyle(Theme.getStyleMode());
		}
		if (nStyle != Theme.getStyleMode())
		{
			SwitchStyle(Theme.getStyleMode());
		}
	}

	public void onPause()
	{
		super.onPause();
		try
		{
			this.m_act.unregisterReceiver(sdcardReceiver);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	BroadcastReceiver sdcardReceiver = new BroadcastReceiver()
	{
		public void onReceive(Context ctx, Intent intent)
		{
			// P.v("Log.DEBUG", "SDCard status broadcast received");
			localManage.setFilePath(mStrPath,
					Const.cmd_Local_List_Go);
		}
	};

	/** 注销广播 */

	public void onDestroy()
	{
		mFileList.clear();
		super.onDestroy();
	}

	public void onReload()
	{
		P.v("LocalFilePage:onReload");
		if (nStyle != Theme.getStyleMode())
		{
			SwitchStyle(Theme.getStyleMode());
		}
	}

	public void onExit()
	{
		P.v("LocalFilePage:onExit");
	}

	private boolean Back()
	{
		if (mView.findViewById(R.id.ip_menu).getVisibility() == View.VISIBLE)
		{
			new MGoneAinm(m_act, mView.findViewById(R.id.ip_menu),
					mView.findViewById(R.id.main_menu)).ShowAnim();
			return true;
		}

		String currentPath = localManage.getCurrentPath();
		if (mScrollList.containsKey(currentPath))
		{
			mScrollList.remove(currentPath);
		}
		return localManage.Back();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_BACK:
			if (Back())
			{
				return true;
			}
			break;
		case KeyEvent.KEYCODE_SEARCH:
			ShowSearchDialog();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 显示搜索对话框
	 */
	public void ShowSearchDialog()
	{
		// KMainPage.mKMainPage.ChangePage(KMainPage.Search, null);
		// switchFragment(new SearchResultPage(this));
		SearchResultPage.actionSettingPage(m_act);
	}

	/**
	 * 点击激活相应的窗体
	 */

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{

		if (mFileList.size() <= position)
		{
			Toast.makeText(m_act, "数据出错，请稍后在试", Toast.LENGTH_SHORT).show();
			return;
		}
		FileBean temp = mFileList.get(position);
		if (temp != null)
		{
			if (position == 0 && temp.getFileName().equals(".."))
			{// 返回到上一层
				Back();
				return;
			}
			if (fileAdapter.isSelected())
			{
				fileAdapter.setChecked(temp);
				fileAdapter.notifyDataSetChanged();
				return;
			}
			final File mFile = temp.getFile();
			// SysEng.getInstance()
			// .addEvent(new FavoriteFileEvent(m_act, temp, 1));//历史记录
			// 如果该文件是可读的，我们进去查看文件
			if (mFile.isDirectory())
			{
				if (mFile.canRead())
				{
					String currentPath = localManage.getCurrentPath();
					int pos = m_locallist.getFirstVisiblePosition(); // ListPos记录
					if (mScrollList.containsKey(currentPath))
					{
						mScrollList.remove(currentPath);
					}
					mScrollList.put(currentPath, pos);
					// 如果是文件夹，则直接进入该文件夹，查看文件目录
					localManage.setFilePath(mFile.getPath(),
							Const.cmd_Local_List_Go);
				} else
				{// 如果该文件不可读，我们给出提示不能访问，防止用户操作系统文件造成系统崩溃等
					Toast.makeText(
							m_act,
							m_act.getString(R.string.msg_sorry_file_not_exist_permissions),
							Toast.LENGTH_SHORT).show();
				}
			} else
			{
				if (mFile.canRead())
				{
					if (!mFile.exists())
					{
						Toast.makeText(m_act,
								m_act.getString(R.string.msg_not_find_file),
								Toast.LENGTH_SHORT).show();
						return;
					}
					SysEng.getInstance().addHandlerEvent(
							new openDefFileEvent(m_act, mFile.getPath()));
				} else
				{// 如果该文件不可读，我们给出提示不能访问，防止用户操作系统文件造成系统崩溃等
					Toast.makeText(
							m_act,
							m_act.getString(R.string.msg_sorry_file_permissions),
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btMore:
			break;
		case R.id.btFileCreate:
			CreateFileDialog.Show(m_act, localManage.getCurrentPath(),this);
			break;
		// KDialog.ShowFileTypeArray(m_act, "类别", mFileType, listener);
		case R.id.btListSort:// 排序
			new ViewSortDialog().ShowDialog(m_act, this);
			break;
		case R.id.btListStyle:
			// MobclickAgent.onEvent(m_act, "localEvent","ListStyle");
			if (nStyle == 0)
			{
				SwitchStyle(1);
			} else
			{
				SwitchStyle(0);
			}
			break;
		case R.id.btInstall:
			InstallApp();
			break;
		case R.id.btNew:
			// MobclickAgent.onEvent(m_act, "localEvent","create");
			CreateFileDialog.Show(m_act, localManage.getCurrentPath(),LocalPage.this);
			break;
		case R.id.btCopy:
			// MobclickAgent.onEvent(m_act, "localEvent","copy");
			SysEng.getInstance().addHandlerEvent(
					new copyFileEvent(m_act,localManage.getFileList(),LocalPage.this));
			break;
		case R.id.btCut:
			// MobclickAgent.onEvent(m_act, "localEvent","cut");
			SysEng.getInstance().addHandlerEvent(
					new cutFileEvent(m_act, localManage.getFileList(), LocalPage.this));
			break;
		case R.id.btDelete:
			// MobclickAgent.onEvent(m_act, "localEvent","del");
			deletefiles();
			break;
		// case R.id.btPaste:
		// MobclickAgent.onEvent(m_act, "localEvent","paste");
		// by wmh 这段代码以后用不到了
		// List<FileBean> copyList = localManage.getCopyFiles();
		// if (localManage.isCopy())
		// {
		// SysEng.getInstance().addEvent(
		// new palseFileEvent(m_act, localManage.getCurrentPath(),
		// copyList, localManage.isCut()));
		// }
		// else
		// {
		// Toast.makeText(m_act,
		// m_act.getString(R.string.msg_copy_need_paste),
		// Toast.LENGTH_SHORT).show();
		// }
		// break;
		case R.id.btSelectAll:
			MobclickAgent.onEvent(m_act, "localEvent", "selAll");
			SelectAll();
			break;
		case R.id.btBack:
			// MobclickAgent.onEvent(m_act, "localEvent","back");
			Back();
			break;
		}
	}
	private void InstallApp()
	{
		final ArrayList<FileBean> mInstallFiles = new ArrayList<FileBean>();
		for (int i = 0; i < mFileList.size(); i++)
		{
			FileBean tmpInfo = mFileList.get(i);
			if (!tmpInfo.isDirectory()&&tmpInfo.isChecked()&&tmpInfo.getFileEnds().equalsIgnoreCase("apk"))
			{
				mInstallFiles.add(tmpInfo);
			}
		}
		if (mInstallFiles.size() > 0)
		{
			SysEng.getInstance().addEvent(
					new InstallEvent(m_act, mInstallFiles,
							null));
			return;
		}
		Toast.makeText(m_act,
				m_act.getString(R.string.msg_Please_select_instal_file) + "!",
				Toast.LENGTH_SHORT).show();
	}
	private void deletefiles()
	{
		if (mFileList.size() > 0)
		{
			final ArrayList<FileBean> mDelFiles = new ArrayList<FileBean>();
			for (int i = 0; i < mFileList.size(); i++)
			{
				FileBean tmpInfo = mFileList.get(i);
				if (tmpInfo.isChecked())
				{
					mDelFiles.add(tmpInfo);
				}
			}
			if (mDelFiles.size() > 0)
			{
				new AlertDialog.Builder(m_act)
						.setTitle(
								m_act.getString(R.string.msg_dialog_info_title))
						.setMessage(m_act.getString(R.string.msg_delselectfile))
						.setPositiveButton(m_act.getString(R.string.ok),
								new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog,
											int which)
									{
										SysEng.getInstance().addEvent(
												new delFileEvent(m_act,
														mDelFiles,LocalPage.this));
									}
								})
						.setNegativeButton(m_act.getString(R.string.cancel),
								null).show();
				return;
			}
		}
		Toast.makeText(m_act,
				m_act.getString(R.string.msg_please_del_operate_file),
				Toast.LENGTH_SHORT).show();
	}

	private void SelectAll()
	{
		if (mFileList.size() >= 2)
		{
			boolean check = !mFileList.get(1).isChecked();
			for (int i = 1; i < mFileList.size(); i++)
			{
				FileBean tmpInfo = mFileList.get(i);
				tmpInfo.setChecked(check);
			}
			fileAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 切换窗体视图 false:ListView true:GridView
	 */
	private void SwitchStyle(int nStyle)
	{
		if (nStyle != this.nStyle)
		{

			Theme.setStyleMode(nStyle);
			Theme.Save(m_act);
		}
		if (nStyle == 1)
		{
			fileAdapter = new FileAdapter(m_act, 2, mFileList, this);
			m_localGrid.setAdapter(fileAdapter);
			m_locallist.setVisibility(View.GONE);
			m_localGrid.setVisibility(View.VISIBLE);
		} else
		{
			fileAdapter = new FileAdapter(m_act, 1, mFileList, this);
			m_locallist.setAdapter(fileAdapter);
			m_localGrid.setVisibility(View.GONE);
			m_locallist.setVisibility(View.VISIBLE);
		}
		this.nStyle = nStyle;
	}

	/**
	 * 根据给定的一个文件夹路径字符串遍历出这个文 件夹中包含的文件名称并配置到ListView列表中
	 */
	EditText mET;

	/** 长按列表项的事件监听:对长按需要进行一个控制，当列表中包括”返回根目录“和”返回上一级“时，需要对这两列进行屏蔽 */

	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			final int position, long arg3)
	{
		// MobclickAgent.onEvent(m_act, "localEvent","ItemLongClick");
		if (mFileList.size() > position)
		{
			FileBean temp = mFileList.get(position);
			if (!temp.isBackUp())
			{
				new PopLocalMenu().ShowFile(m_act, temp, 0,this);
			}
		} else
		{
			fileAdapter.notifyDataSetChanged();
		}
		return true;
	}

	public boolean onTouchEvent(MotionEvent event)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void NotifyDataSetChanged(int cmd, Object value)
	{
		mStrPath=localManage.getCurrentPath();
		switch (cmd)
		{
		case Const.cmd_CreateFileEvent_Finish:
		case Const.cmd_CreateFolderEvent_Finish:
		case Const.cmd_DelFileEvent_Finish:
		case Const.cmd_CutFileEvent_Finish:
		case Const.cmd_RenameFileEvent_Finish:
		case Const.cmd_ZipFileEvent_Finish:
		case Const.cmd_PalseFileEvent_Finish:
			localManage.Refresh();
			break;
		case Const.cmd_Local_List_Selected://List选中执行的操作
			if(value instanceof FileBean)
			{
				FileBean tmpInfo=(FileBean)value;
				if(tmpInfo.getFileEnds().equalsIgnoreCase("apk"))
				{
					mView.findViewById(R.id.btInstall).setVisibility(View.VISIBLE);
					mView.findViewById(R.id.btMore).setVisibility(View.GONE);
				}
			}
			// this.lyBTools.setVisibility(View.VISIBLE);
			break;
		case Const.cmd_Local_List_UnSelected:
			// this.lyBTools.setVisibility(View.GONE);
			mView.findViewById(R.id.btInstall).setVisibility(View.GONE);
			mView.findViewById(R.id.btMore).setVisibility(View.VISIBLE);
			break;
		case Const.cmd_LoadSDFile_Finish:
			// SysEng.getInstance().addHandlerEvent(
			// new NextPageEvent(m_act, mSearchPage, 1, null));
			break;
		case Const.cmd_Local_ListSort_Finish:
			nSortMode = Theme.getSortMode();
			localManage.setFilePath(localManage.getCurrentPath(),
					Const.cmd_Local_List_Go);
			SwitchStyle(Theme.getStyleMode());
			fileAdapter.Clear();
			// lyBTools.setVisibility(View.GONE);
			break;
		case Const.cmd_Local_List_Refresh:// 更新列表
			fileAdapter.notifyDataSetChanged();
			fileAdapter.Clear();
			// lyBTools.setVisibility(View.GONE);
			break;
		case Const.cmd_Local_List_Go: // 新数据
			mPath.setText(localManage.getCurrentPath());
			setTitle(new File(localManage.getCurrentPath()).getName());
			fileAdapter.Clear();
			 ((KFileManagerApp)m_act.getApplication()).setCurrentPath(localManage.getCurrentPath());
			// lyBTools.setVisibility(View.GONE);
			fileAdapter.notifyDataSetChanged();
			if (fileAdapter.getCount() <= 0)
			{
				mView.findViewById(R.id.icEmptyPannal).setVisibility(
						View.VISIBLE);
			}
			Integer y = mScrollList.get(localManage.getCurrentPath());
			if (y != null)
			{
				m_locallist.setSelection(y);
			} else
			{
				m_locallist.setSelection(0);
			}
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(MenuInflater inflater, Menu menu)
	{
		// menu.clear();
		inflater.inflate(R.menu.localpagemenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// TODO Auto-generated method stub
		switch (item.getItemId())
		{
		case R.id.muCreateFile:
			CreateFileDialog.Show(m_act, localManage.getCurrentPath(),this);
			break;
		case R.id.muSearch:
			ShowSearchDialog();
			break;
		// case R.id.muFileSort:
		// new ViewSortDialog().ShowDialog(m_act, LocalPage.this);
		// break;
		// case R.id.muFileMode:
		// if (nStyle == 1)
		// {
		// SwitchStyle(0);
		// } else
		// {
		// SwitchStyle(1);
		// }
		// break;
		// case R.id.muFileHide:
		// Theme.setShowHideFile(!Theme.getShowHideFile());
		// Theme.Save(m_act);
		// localManage.setFilePath(localManage.getCurrentPath(),
		// Const.cmd_Local_List_Go);
		// break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private OnScrollListener m_localOnScrollListener = new OnScrollListener()
	{
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			switch (scrollState)
			{
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
				P.debug("SCROLL_STATE_FLING");
				if (fileAdapter != null)
					fileAdapter.setShowLogo(false);
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
				P.debug("SCROLL_STATE_IDLE");
				if (fileAdapter != null)
				{
					fileAdapter.setShowLogo(true);
					fileAdapter.notifyDataSetChanged();
				}
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				P.debug("SCROLL_STATE_TOUCH_SCROLL");
				if (fileAdapter != null)
					fileAdapter.setShowLogo(false);
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
}