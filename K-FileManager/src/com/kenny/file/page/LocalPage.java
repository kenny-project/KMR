package com.kenny.file.page;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.event.NextPageEvent;
import com.framework.interfaces.MenuAble;
import com.framework.log.P;
import com.framework.page.MultiItemPage;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.KFileManager.R.color;
import com.kenny.file.Adapter.FileAdapter;
import com.kenny.file.Event.copyFileEvent;
import com.kenny.file.Event.cutFileEvent;
import com.kenny.file.Event.delFileEvent;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.bean.FileBean;
import com.kenny.file.commui.ListHeaderView;
import com.kenny.file.dialog.CreateFileDialog;
import com.kenny.file.dialog.FavoritesDialog;
import com.kenny.file.dialog.LocalAddressDialog;
import com.kenny.file.dialog.SearchFileDialog;
import com.kenny.file.dialog.ViewSortDialog;
import com.kenny.file.menu.MGoneAinm;
import com.kenny.file.menu.MVisibleAinm;
import com.kenny.file.menu.PopLocalMenu;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.tools.SaveData;
import com.kenny.file.util.Const;
import com.kenny.file.util.FileManager;
import com.kenny.file.util.Theme;
import com.umeng.analytics.MobclickAgent;

public class LocalPage extends MultiItemPage implements
		OnItemLongClickListener, MenuAble, INotifyDataSetChanged,
		OnClickListener, OnItemClickListener
{
	/*
	 * 声明成员变量： mFileName ：存放显示的文件列表的名称 mFilePaths ：存放显示的文件列表的相对应的路径 mRootPath
	 * ：起始目录“/” (用java.io.File.separator获取) mSDCard ： SD卡根目录 mPath
	 * ：显示当前路径的TextView文本组件
	 */
	private List<FileBean> mFileList = null;
	private HashMap<String, Integer> mScrollList = new HashMap<String, Integer>();
	private View m_lvMain;// 主页面
	private TextView mPath;
	private ListView m_locallist;
	private GridView m_localGrid;
	private FileAdapter fileAdapter;
	private FileManager localManage;
	private EditText etSearchFileName;
	// private int ListPos;
	private int nStyle = 0, nSortMode = 0; // false:listView
	// true:gridView

	private Button btMenuListSort, btMenuListMode, btMenuShowOrHide,
			btMenuFavorites;// 菜单项
	private Button btSearchDialog, btFileCreate;
	private View lyBTools;
	private SearchResultPage mSearchPage;

	public LocalPage(Activity context)
	{
		super(context);
		P.v("LocalFilePage:LocalFilePage");
		localManage = FileManager.GetHandler();
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

	public void onCreate()
	{
		setContentView(R.layout.localpage);
		super.onCreate();
		// String apkRoot="chmod 777 "+m_act.getPackageCodePath();
		// cmd.RootCommand(apkRoot);
		localManage.setContext(m_act);
		m_lvMain = findViewById(R.id.lvMain);

		m_locallist = (ListView) findViewById(R.id.lvLocallist);
		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(50);
		set.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(100);
		set.addAnimation(animation);
		// LayoutAnimationController controller = new LayoutAnimationController(
		// set, 0.5f);

		m_locallist.setOnScrollListener(m_localOnScrollListener);
		m_locallist.setOnItemLongClickListener(this);
		m_locallist.setOnItemClickListener(this);
		m_localGrid = (GridView) findViewById(R.id.gvLocallist);
		m_localGrid.setOnItemClickListener(this);
		m_localGrid.setOnItemLongClickListener(this);
		m_localGrid.setOnScrollListener(m_localOnScrollListener);
		mPath = (TextView) findViewById(R.id.mCurrentPath);
		mPath.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				MobclickAgent.onEvent(m_act, "localEvent", "LocalAddress");
				Rect rect = new Rect();
				findViewById(R.id.lyTools2).getGlobalVisibleRect(rect);
				LocalAddressDialog.ShowDialog(m_act,
						localManage.getCurrentPath(), rect.bottom);
			}
		});
		mFileList = localManage.getFileList();
		TextView tview = new TextView(m_act);
		tview.setHeight(150);
		tview.setWidth(-1);
		tview.setVisibility(View.VISIBLE);
		tview.setBackgroundColor(color.green);
		ListHeaderView headerView = new ListHeaderView(m_act, tview);
		m_locallist.addFooterView(headerView, null, false);
		localManage.setNotifyData(this);

		lyBTools = (View) findViewById(R.id.lyBTools);

		btSearchDialog = (Button) findViewById(R.id.btSearchDialog);
		btSearchDialog.setOnClickListener(this);

		btFileCreate = (Button) findViewById(R.id.btFileCreate);
		btFileCreate.setOnClickListener(this);

		etSearchFileName = (EditText) findViewById(R.id.etSearchFileName);
		etSearchFileName.addTextChangedListener(mSearchWatcher);

		Button btButton = (Button) findViewById(R.id.btNew);
		btButton.setOnClickListener(this);

		btButton = (Button) findViewById(R.id.btToolsMemu);
		btButton.setOnClickListener(this);
		btButton = (Button) findViewById(R.id.btBack);
		btButton.setOnClickListener(this);

		btButton = (Button) findViewById(R.id.btListSort);
		btButton.setOnClickListener(this);

		btButton = (Button) findViewById(R.id.btCopy);
		btButton.setOnClickListener(this);

		btButton = (Button) findViewById(R.id.btCut);
		btButton.setOnClickListener(this);

		btButton = (Button) findViewById(R.id.btDelete);
		btButton.setOnClickListener(this);

		btButton = (Button) findViewById(R.id.btPaste);
		btButton.setOnClickListener(this);

		btButton = (Button) findViewById(R.id.btSelectAll);
		btButton.setOnClickListener(this);

		// ///////////

		btButton = (Button) findViewById(R.id.btToolsNew);
		btButton.setOnClickListener(this);
		btButton = (Button) findViewById(R.id.btToolsOrganize);
		btButton.setOnClickListener(this);
		btButton = (Button) findViewById(R.id.btToolsFilter);
		btButton.setOnClickListener(this);
		btButton = (Button) findViewById(R.id.btToolsShowMode);
		btButton.setOnClickListener(this);
		btButton = (Button) findViewById(R.id.btToolsMenu);
		btButton.setOnClickListener(this);

		SwitchStyle(Theme.getStyleMode());
		onCreateMenu();
	}

	/**
	 * 创建主菜单
	 */
	public void OnCreateMainMenu()
	{
		final RelativeLayout rlMenu = (RelativeLayout) findViewById(R.id.menu_main);
		rlMenu.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				rlMenu.setVisibility(View.GONE);
				return false;
			}
		});
	}

	/**
	 * 创建文件夹菜单
	 */
	public void OnShowCreateMenu()
	{
		final RelativeLayout rlMenu = (RelativeLayout) findViewById(R.id.menu_show);
		rlMenu.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				rlMenu.setVisibility(View.GONE);
				return false;
			}
		});
	}

	/**
	 * 创建文件夹菜单
	 */
	public void OnLocalCreateMenu()
	{
		final RelativeLayout rlMenu = (RelativeLayout) findViewById(R.id.menu_new);
		rlMenu.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				rlMenu.setVisibility(View.GONE);
				return false;
			}
		});
		final MGoneAinm goneAnim = new MGoneAinm(m_act, rlMenu,
				findViewById(R.id.main_menu));
		Button btToolsMemu = (Button) findViewById(R.id.btToolsMemu);
		btToolsMemu.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				if (rlMenu.getVisibility() == View.VISIBLE)
				{
					goneAnim.ShowAnim();
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
					new MVisibleAinm(m_act, rlMenu,
							findViewById(R.id.main_menu)).ShowAnim();

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
				case R.id.btMenuFavorites:
					Rect rect = new Rect();
					findViewById(R.id.btToolsMemu).getGlobalVisibleRect(rect);
					FavoritesDialog.ShowDialog(m_act, rect.bottom);
					break;
				}
			}
		};
		btMenuListSort = (Button) findViewById(R.id.btMenuListSort);
		btMenuListSort.setOnClickListener(menuListener);
		btMenuListMode = (Button) findViewById(R.id.btMenuListMode);
		btMenuListMode.setOnClickListener(menuListener);
		btMenuShowOrHide = (Button) findViewById(R.id.btMenuShowOrHide);
		btMenuShowOrHide.setOnClickListener(menuListener);
		btMenuFavorites = (Button) findViewById(R.id.btMenuFavorites);
		btMenuFavorites.setOnClickListener(menuListener);
	}

	public void onCreateMenu()
	{
		OnLocalCreateMenu();
		OnShowCreateMenu();
		OnCreateMainMenu();
		final RelativeLayout rlMenu = (RelativeLayout) findViewById(R.id.ip_menu);
		rlMenu.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				rlMenu.setVisibility(View.GONE);
				return false;
			}
		});
		final MGoneAinm goneAnim = new MGoneAinm(m_act, rlMenu,
				findViewById(R.id.main_menu));
		Button btToolsMemu = (Button) findViewById(R.id.btToolsMemu);
		btToolsMemu.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				if (rlMenu.getVisibility() == View.VISIBLE)
				{
					goneAnim.ShowAnim();
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
					new MVisibleAinm(m_act, rlMenu,
							findViewById(R.id.main_menu)).ShowAnim();
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
				case R.id.btMenuFavorites:
					Rect rect = new Rect();
					findViewById(R.id.btToolsMemu).getGlobalVisibleRect(rect);
					FavoritesDialog.ShowDialog(m_act, rect.bottom);
					break;
				}
			}
		};
		btMenuListSort = (Button) findViewById(R.id.btMenuListSort);
		btMenuListSort.setOnClickListener(menuListener);
		btMenuListMode = (Button) findViewById(R.id.btMenuListMode);
		btMenuListMode.setOnClickListener(menuListener);
		btMenuShowOrHide = (Button) findViewById(R.id.btMenuShowOrHide);
		btMenuShowOrHide.setOnClickListener(menuListener);
		btMenuFavorites = (Button) findViewById(R.id.btMenuFavorites);
		btMenuFavorites.setOnClickListener(menuListener);

	}

	public void onResume()
	{
		P.v("Log.DEBUG", "onResume");
		IntentFilter sdCardFilter = new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED);
		sdCardFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		sdCardFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		sdCardFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		sdCardFilter.addDataScheme("file");
		this.m_act.registerReceiver(sdcardReceiver, sdCardFilter);// 注册监听函数

		m_lvMain.setBackgroundColor(Theme.getBackgroundColor());
		if (nSortMode != Theme.getSortMode())
		{
			nSortMode = Theme.getSortMode();
			localManage.setFilePath(localManage.getCurrentPath(),
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
			localManage.setFilePath(localManage.getCurrentPath(),
					Const.cmd_Local_List_Go);
		}
	};

	/** 注销广播 */

	public void onDestroy()
	{
		mFileList.clear();
	}

	public void onLoad()
	{
		// TODO Auto-generated method stub
		P.v("LocalFilePage:onLoad");
		localManage.setFilePath(
				SaveData.Read(m_act, Const.strDefaultPath, Const.SDCard),
				Const.cmd_Local_List_Go);
	}

	public void onReload()
	{
		P.v("LocalFilePage:onReload");
		// m_lvMain.setBackgroundColor(Theme.getBackGroupColor());
		if (nStyle != Theme.getStyleMode())
		{
			SwitchStyle(Theme.getStyleMode());
		}
	}

	public void onExit()
	{
		// TODO Auto-generated method stub
		P.v("LocalFilePage:onExit");
	}

	private void Back()
	{
		if (findViewById(R.id.ip_menu).getVisibility() == View.VISIBLE)
		{
			new MGoneAinm(m_act, findViewById(R.id.ip_menu),
					findViewById(R.id.main_menu)).ShowAnim();
			return;
		}
		String currentPath = localManage.getCurrentPath();
		if (mScrollList.containsKey(currentPath))
		{
			mScrollList.remove(currentPath);
		}
		localManage.Back();
	}

	public boolean onKeyDown(int keyCode, KeyEvent msg)
	{
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_BACK:
			Back();
			break;
		case KeyEvent.KEYCODE_SEARCH:
			if (mSearchPage == null)
			{
				mSearchPage = new SearchResultPage(m_act);
			}
			new SearchFileDialog().Show(m_act, FileManager.GetHandler()
					.getCurrentPath(), mSearchPage.getParam(), this);
			break;
		default:
			return super.onKeyDown(keyCode, msg);
		}
		return true;
	}

	/**
	 * 点击激活相应的窗体
	 */

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{

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

	public void ShowMenu(int id)
	{
		// findViewById(R.id.new_menu).setVisibility(View.GONE);
		// findViewById(R.id.lySearchPanel).setVisibility(View.GONE);
		// findViewById(R.id.show_menu).setVisibility(View.GONE);
		// lyBTools.setVisibility(View.GONE);
		switch (id)
		{
		case 0:
			if (findViewById(R.id.menu_new).getVisibility() == View.VISIBLE)
			{
				findViewById(R.id.menu_new).setVisibility(View.GONE);
			} else
			{
				findViewById(R.id.menu_new).setVisibility(View.VISIBLE);
			}
			break;
		case 1:
			if (lyBTools.getVisibility() == View.VISIBLE)
			{
				lyBTools.setVisibility(View.GONE);
			} else
			{
				lyBTools.setVisibility(View.VISIBLE);
			}
			break;
		case 2:
			if (findViewById(R.id.lySearchPanel).getVisibility() == View.VISIBLE)
			{
				findViewById(R.id.lySearchPanel).setVisibility(View.GONE);
				etSearchFileName.setText("");
			} else
			{
				findViewById(R.id.lySearchPanel).setVisibility(View.VISIBLE);
				etSearchFileName.setText("");
			}
			break;
		case 3:
			if (findViewById(R.id.menu_show).getVisibility() == View.VISIBLE)
			{
				findViewById(R.id.menu_show).setVisibility(View.GONE);
			} else
			{
				findViewById(R.id.menu_show).setVisibility(View.VISIBLE);
			}
			break;
		case 4:
			if (findViewById(R.id.menu_main).getVisibility() == View.VISIBLE)
			{
				findViewById(R.id.menu_main).setVisibility(View.GONE);
			} else
			{
				findViewById(R.id.menu_main).setVisibility(View.VISIBLE);
			}
			break;
		}
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btToolsNew:
			ShowMenu(0);
			break;
		case R.id.btToolsOrganize:
			ShowMenu(1);
			break;
		case R.id.btToolsFilter:
			ShowMenu(2);
			break;
		case R.id.btToolsShowMode:
			ShowMenu(3);
			break;
		case R.id.btToolsMenu:
			ShowMenu(4);
			break;
		case R.id.btSearchDialog:
			KMainPage.mKMainPage.ChangePage(KMainPage.Search, null);
			break;
		case R.id.btFileCreate:
			CreateFileDialog.Show(m_act, localManage.getCurrentPath());
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
		case R.id.btNew:
			// MobclickAgent.onEvent(m_act, "localEvent","create");
			CreateFileDialog.Show(m_act, localManage.getCurrentPath());
			break;
		case R.id.btCopy:
			// MobclickAgent.onEvent(m_act, "localEvent","copy");
			SysEng.getInstance().addHandlerEvent(
					new copyFileEvent(m_act, localManage.getFileList()));
			break;
		case R.id.btCut:
			// MobclickAgent.onEvent(m_act, "localEvent","cut");
			SysEng.getInstance().addHandlerEvent(
					new cutFileEvent(m_act, localManage.getFileList()));
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
														mDelFiles));
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

	public boolean onCreateOptionsMenu(Menu menu)
	{
		P.debug("onCreateOptionsMenu");
		return super.onCreateOptionsMenu(menu, R.menu.localpagemenu);
	}

	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// // return super.onCreateOptionsMenu(menu, R.menu.favoritepagemenu);
		// onCreateOptionsMenu(menu);
		super.onPrepareOptionsMenu(menu);
		P.debug("onPrepareOptionsMenu");
		// for (int i = 0; i < menu.size(); i++)
		// {
		// MenuItem item = menu.getItem(i);
		// if (item.getItemId() == R.id.muListView)
		// {
		// if (nStyle)
		// {
		// item.setIcon(R.drawable.common_icon_menu_list_view);
		// item.setTitle(R.string.m_List);
		// }
		// else
		// {
		// item.setIcon(R.drawable.common_icon_menu_grid_view);
		// item.setTitle(R.string.m_Grid);
		// }
		// break;
		// }
		// }
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		// case R.id.muCreate:
		// CreateFileDialog.Show(m_act, localManage.getCurrentPath());
		// break;
		case R.id.muSearch:
			if (mSearchPage == null)
			{
				mSearchPage = new SearchResultPage(m_act);
			}
			if (mSearchPage.Size() > 0)
			{
				SysEng.getInstance().addHandlerEvent(
						new NextPageEvent(m_act, mSearchPage, 1, null));
			} else
			{
				new SearchFileDialog().Show(m_act,
						localManage.getCurrentPath(), mSearchPage.getParam(),
						this);
			}
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return false;
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
		FileBean temp = mFileList.get(position);
		if (!temp.isBackUp())
		{
			new PopLocalMenu().ShowFile(m_act, temp, 0);
		}
		return false;
	}

	public void clear()
	{
		// TODO Auto-generated method stub

	}

	public boolean onTouchEvent(MotionEvent event)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void NotifyDataSetChanged(int cmd, Object value)
	{
		switch (cmd)
		{
		case Const.cmd_Local_List_Selected:
			this.lyBTools.setVisibility(View.VISIBLE);
			break;
		case Const.cmd_Local_List_UnSelected:
			this.lyBTools.setVisibility(View.GONE);
			break;
		case Const.cmd_LoadSDFile_Finish:
			SysEng.getInstance().addHandlerEvent(
					new NextPageEvent(m_act, mSearchPage, 1, null));
			break;
		case Const.cmd_Local_ListSort_Finish:
			nSortMode = Theme.getSortMode();
			localManage.setFilePath(localManage.getCurrentPath(),
					Const.cmd_Local_List_Go);
			SwitchStyle(Theme.getStyleMode());
			fileAdapter.Clear();
			lyBTools.setVisibility(View.GONE);
			break;
		case Const.cmd_Local_List_Refresh:// 更新列表
			fileAdapter.notifyDataSetChanged();
			fileAdapter.Clear();
			lyBTools.setVisibility(View.GONE);
			break;
		case Const.cmd_Local_List_Go: // 新数据
			mPath.setText(localManage.getCurrentPath());
			fileAdapter.Clear();
			lyBTools.setVisibility(View.GONE);
			fileAdapter.notifyDataSetChanged();
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

	private TextWatcher mSearchWatcher = new TextWatcher()
	{

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count)
		{
			if (etSearchFileName.length() > 0)
			{
				localManage.setFilePath(localManage.getCurrentPath(),
						etSearchFileName.getText().toString());
			} else
			{
				localManage.Refresh();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s)
		{
			// TODO Auto-generated method stub

		}
	};
}