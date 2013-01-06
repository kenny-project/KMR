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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.kenny.file.Adapter.TreeViewAdapter;
import com.kenny.file.Event.FavoriteFileEvent;
import com.kenny.file.Event.copyFileEvent;
import com.kenny.file.Event.cutFileEvent;
import com.kenny.file.Event.delFileEvent;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.bean.FileBean;
import com.kenny.file.bean.TreeElement;
import com.kenny.file.commui.ListHeaderView;
import com.kenny.file.dialog.CreateFileDialog;
import com.kenny.file.dialog.FavoritesDialog;
import com.kenny.file.dialog.KDialog;
import com.kenny.file.dialog.LocalAddressDialog;
import com.kenny.file.dialog.PopLocalMenu;
import com.kenny.file.dialog.SearchFileDialog;
import com.kenny.file.dialog.ViewSortDialog;
import com.kenny.file.menu.MGoneAinm;
import com.kenny.file.menu.MVisibleAinm;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.tools.SaveData;
import com.kenny.file.util.Const;
import com.kenny.file.util.FileManager;
import com.kenny.file.util.Theme;
import com.umeng.analytics.MobclickAgent;

public class LocalFilePage extends MultiItemPage implements
		OnItemLongClickListener, MenuAble, INotifyDataSetChanged,
		OnClickListener, OnItemClickListener {
	/*
	 * 声明成员变量： mFileName ：存放显示的文件列表的名称 mFilePaths ：存放显示的文件列表的相对应的路径 mRootPath
	 * ：起始目录“/” (用java.io.File.separator获取) mSDCard ： SD卡根目录 mPath
	 * ：显示当前路径的TextView文本组件
	 */
	private List<FileBean> mFileList = null;
	private HashMap<String, Integer> mScrollList = new HashMap<String, Integer>();
	private View m_lvMain;// 主页面
	private TextView mPath;
	private ListView m_locallist, m_TreeLocallist;
	private GridView m_localGrid;
	private FileAdapter fileAdapter;
	private TreeViewAdapter treeViewAdapter;// 树结构
	private FileManager localManage;
	// private int ListPos;
	private int nStyle = 0, nSortMode = 0; // false:listView
	// true:gridView
	private Button btToolsMemu;
	
	private Button btMenuListSort,btMenuListMode,btMenuShowOrHide,btMenuFavorites;//菜单项
	private Button btFileType, btListStyle, btFavorite;
	private View lyBTools;
	private ArrayList<TreeElement> mPdfOutlinesCount = new ArrayList<TreeElement>();
	private int mFileType = 0;// 显示类型
	private SearchResultPage mSearchPage;

	public LocalFilePage(Activity context) {
		super(context);
		P.v("LocalFilePage:LocalFilePage");
		localManage = FileManager.GetHandler();
	}

	private OnScrollListener m_localOnScrollListener = new OnScrollListener() {

		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
				P.debug("SCROLL_STATE_FLING");
				if (fileAdapter != null)
					fileAdapter.setShowLogo(false);
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
				P.debug("SCROLL_STATE_IDLE");
				if (fileAdapter != null) {
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
				int visibleItemCount, int totalItemCount) {

		}
	};

	public void onCreate() {
		setContentView(R.layout.localpage);
		super.onCreate();
		// String apkRoot="chmod 777 "+m_act.getPackageCodePath();
		// cmd.RootCommand(apkRoot);
		localManage.setContext(m_act);
		m_lvMain = findViewById(R.id.lvMain);
		m_TreeLocallist = (ListView) findViewById(R.id.lvTreeLocallist);
		m_TreeLocallist.setOnItemClickListener(mItemClickListenter);
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
		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.5f);
		// m_locallist.setLayoutAnimation(controller);

		m_locallist.setOnScrollListener(m_localOnScrollListener);
		m_locallist.setOnItemLongClickListener(this);
		// m_locallist.setOnLongClickListener(this);
		m_locallist.setOnItemClickListener(this);

		m_localGrid = (GridView) findViewById(R.id.gvLocallist);
		m_localGrid.setOnItemClickListener(this);
		// m_localGrid.setOnLongClickListener(this);
		m_localGrid.setOnItemLongClickListener(this);
		m_localGrid.setOnScrollListener(m_localOnScrollListener);
		mPath = (TextView) findViewById(R.id.mCurrentPath);
		mPath.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				MobclickAgent.onEvent(m_act, "localEvent", "LocalAddress");
				LocalAddressDialog.ShowDialog(m_act,localManage.getCurrentPath());
			}
		});
		Spinner spinner = (Spinner) findViewById(R.id.spSpinner);
		spinner.setPrompt("类型");

		String[] items = m_act.getResources().getStringArray(R.array.fileType);
		ArrayAdapter<String> array_adapter = new ArrayAdapter<String>(m_act,
				android.R.layout.simple_spinner_item, items);
		array_adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(array_adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mFileType = position;
				localManage.Refresh();
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		mFileList = localManage.getFileList();
		TextView tview = new TextView(m_act);
		tview.setHeight(150);
		tview.setWidth(-1);
		tview.setVisibility(View.VISIBLE);
		tview.setBackgroundColor(color.green);
		ListHeaderView headerView = new ListHeaderView(m_act, tview);
		// m_locallist.addHeaderView(headerView);
		// headerView.setEnabled(false);
		m_locallist.addFooterView(headerView, null, false);
		// m_locallist.setDivider(m_act.getResources().getDrawable(
		// R.drawable.listview_line));
		localManage.setNotifyData(this);
		m_locallist.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});
		lyBTools = (View) findViewById(R.id.lyBTools);
		btFileType = (Button) findViewById(R.id.btFileType);
		btFileType.setOnClickListener(this);

		btFavorite = (Button) findViewById(R.id.btFavorite);
		btFavorite.setOnClickListener(this);

		btListStyle = (Button) findViewById(R.id.btListStyle);
		btListStyle.setOnClickListener(this);

		Button btButton = (Button) findViewById(R.id.btNew);
		btButton.setOnClickListener(this);

		btButton = (Button) findViewById(R.id.btToolsMemu);
		btButton.setOnClickListener(this);
		btButton = (Button) findViewById(R.id.btBack);
		btButton.setOnClickListener(this);

		btButton = (Button) findViewById(R.id.btTree);
		btButton.setOnClickListener(this);

		btButton = (Button) findViewById(R.id.btListSort);
		btButton.setOnClickListener(this);

		// btButton = (Button) findViewById(R.id.btEnter);
		// btButton.setOnClickListener(this);

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
		
		SwitchStyle(Theme.getStyleMode());
		onCreateMenu();
	}
	public void onCreateMenu()
	{
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
				}
				else
				{
					if(Theme.getShowHideFile())
					{
						btMenuShowOrHide.setText(R.string.btMenuShowOrHide_False);
					}
					else
					{
						
						btMenuShowOrHide.setText(R.string.btMenuShowOrHide_True);
					}
					if(	Theme.getStyleMode()==1)
					{
						btMenuListMode.setText(R.string.btMenuListMode_List);
					}
					else
					{
						btMenuListMode.setText(R.string.btMenuListMode_Grid);
					}
					new MVisibleAinm(m_act, rlMenu, findViewById(R.id.main_menu))
							.ShowAnim();

				}
			}
		});
		OnClickListener menuListener=new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				goneAnim.ShowAnim();
				switch(v.getId())
				{
				case R.id.btMenuListSort:
					new ViewSortDialog().ShowDialog(m_act, LocalFilePage.this);
					break;
				case R.id.btMenuListMode:
					if (nStyle == 1) {
						SwitchStyle(0);
					} else {
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
					FavoritesDialog.ShowDialog(m_act);
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
	
	public void onResume() {
		P.v("Log.DEBUG", "onResume");
		//
		IntentFilter sdCardFilter = new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED);
		sdCardFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		sdCardFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		sdCardFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		sdCardFilter.addDataScheme("file");
		this.m_act.registerReceiver(sdcardReceiver, sdCardFilter);// 注册监听函数

		m_lvMain.setBackgroundColor(Theme.getBackgroundColor());
		if (nSortMode != Theme.getSortMode()) {
			nSortMode = Theme.getSortMode();
			localManage.setFilePath(localManage.getCurrentPath(),
					Const.cmd_Local_List_Go);
			SwitchStyle(Theme.getStyleMode());
		}
		if (nStyle != Theme.getStyleMode()) {
			SwitchStyle(Theme.getStyleMode());
		}
	}

	public void onPause() {
		try {
			this.m_act.unregisterReceiver(sdcardReceiver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	BroadcastReceiver sdcardReceiver = new BroadcastReceiver() {
		public void onReceive(Context ctx, Intent intent) {
			// P.v("Log.DEBUG", "SDCard status broadcast received");
			localManage.setFilePath(localManage.getCurrentPath(),
					Const.cmd_Local_List_Go);
		}
	};

	/** 注销广播 */

	public void onDestroy() {
		mFileList.clear();
	}

	public void onLoad() {
		// TODO Auto-generated method stub
		P.v("LocalFilePage:onLoad");
		localManage.setFilePath(
				SaveData.Read(m_act, Const.strDefaultPath, Const.SDCard),
				Const.cmd_Local_List_Go);
	}

	public void onReload() {
		P.v("LocalFilePage:onReload");
		// m_lvMain.setBackgroundColor(Theme.getBackGroupColor());
		if (nStyle != Theme.getStyleMode()) {
			SwitchStyle(Theme.getStyleMode());
		}
	}

	public void onExit() {
		// TODO Auto-generated method stub
		P.v("LocalFilePage:onExit");
	}

	private void Back() {
		if (findViewById(R.id.ip_menu).getVisibility() == View.VISIBLE) {
			new MGoneAinm(m_act, findViewById(R.id.ip_menu),
					findViewById(R.id.main_menu)).ShowAnim();
			return;
		}

		String currentPath = localManage.getCurrentPath();
		if (mScrollList.containsKey(currentPath)) {
			mScrollList.remove(currentPath);
		}
		localManage.Back();
	}

	public boolean onKeyDown(int keyCode, KeyEvent msg) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Back();
			break;
		case KeyEvent.KEYCODE_SEARCH:
			if (mSearchPage == null) {
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
			long id) {

		FileBean temp = mFileList.get(position);
		if (temp != null) {
			if (position == 0 && temp.getFileName().equals("..")) {// 返回到上一层
				Back();
				return;
			}
			if (fileAdapter.isSelected()) {
				fileAdapter.setChecked(temp);
				fileAdapter.notifyDataSetChanged();
				return;
			}
			final File mFile = temp.getFile();
			SysEng.getInstance()
					.addEvent(new FavoriteFileEvent(m_act, temp, 1));

			// 如果该文件是可读的，我们进去查看文件
			if (mFile.isDirectory()) {
				if (mFile.canRead()) {
					String currentPath = localManage.getCurrentPath();
					int pos = m_locallist.getFirstVisiblePosition(); // ListPos记录
					if (mScrollList.containsKey(currentPath)) {
						mScrollList.remove(currentPath);
					}
					mScrollList.put(currentPath, pos);
					// 如果是文件夹，则直接进入该文件夹，查看文件目录
					localManage.setFilePath(mFile.getPath(),
							Const.cmd_Local_List_Go);
				} else {// 如果该文件不可读，我们给出提示不能访问，防止用户操作系统文件造成系统崩溃等
					Toast.makeText(
							m_act,
							m_act.getString(R.string.msg_sorry_file_not_exist_permissions),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				if (mFile.canRead()) {
					if (!mFile.exists()) {
						Toast.makeText(m_act,
								m_act.getString(R.string.msg_not_find_file),
								Toast.LENGTH_SHORT).show();
						return;
					}
					SysEng.getInstance().addHandlerEvent(
							new openDefFileEvent(m_act, mFile.getPath()));
				} else {// 如果该文件不可读，我们给出提示不能访问，防止用户操作系统文件造成系统崩溃等
					Toast.makeText(
							m_act,
							m_act.getString(R.string.msg_sorry_file_permissions),
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

		public void onClick(DialogInterface dialog, int which) {
			mFileType = which;
			// localManage.setFileType(mFileType);
			localManage.Refresh();
		}
	};

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btToolsMemu:
			break;
		case R.id.btFavorite:
			break;
		case R.id.btFileType:
			KDialog.ShowFileTypeArray(m_act, "类别", mFileType, listener);
			// MobclickAgent.onEvent(m_act, "localEvent","Type");
			break;
		case R.id.btListSort:// 排序
			new ViewSortDialog().ShowDialog(m_act, this);
			break;
		case R.id.btListStyle:
			// MobclickAgent.onEvent(m_act, "localEvent","ListStyle");
			if (nStyle == 0) {
				SwitchStyle(1);
			} else {
				SwitchStyle(0);
			}
			break;
		case R.id.btTree:
			if (m_TreeLocallist.getVisibility() == View.GONE) {
				m_TreeLocallist.setVisibility(View.VISIBLE);
			} else {
				m_TreeLocallist.setVisibility(View.GONE);
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
		case R.id.btPaste:
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
			break;
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

	private void deletefiles() {
		if (mFileList.size() > 0) {
			final ArrayList<FileBean> mDelFiles = new ArrayList<FileBean>();
			for (int i = 0; i < mFileList.size(); i++) {
				FileBean tmpInfo = mFileList.get(i);
				if (tmpInfo.isChecked()) {
					mDelFiles.add(tmpInfo);
				}
			}
			if (mDelFiles.size() > 0) {
				new AlertDialog.Builder(m_act)
						.setTitle(
								m_act.getString(R.string.msg_dialog_info_title))
						.setMessage(m_act.getString(R.string.msg_delselectfile))
						.setPositiveButton(m_act.getString(R.string.ok),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
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

	private void SelectAll() {
		if (mFileList.size() >= 2) {
			boolean check = !mFileList.get(1).isChecked();
			for (int i = 1; i < mFileList.size(); i++) {
				FileBean tmpInfo = mFileList.get(i);
				tmpInfo.setChecked(check);
			}
			fileAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 切换窗体视图 false:ListView true:GridView
	 */
	private void SwitchStyle(int nStyle) {
		if (nStyle != this.nStyle) {

			Theme.setStyleMode(nStyle);
			Theme.Save(m_act);
		}
		if (nStyle == 1) {
			fileAdapter = new FileAdapter(m_act, 2, mFileList, this);
			m_localGrid.setAdapter(fileAdapter);
			m_locallist.setVisibility(View.GONE);
			m_localGrid.setVisibility(View.VISIBLE);
			btListStyle
					.setBackgroundResource(R.drawable.file_manager_browser_gridmode);
		} else {
			fileAdapter = new FileAdapter(m_act, 1, mFileList, this);
			m_locallist.setAdapter(fileAdapter);
			m_localGrid.setVisibility(View.GONE);
			m_locallist.setVisibility(View.VISIBLE);
			
			btListStyle
					.setBackgroundResource(R.drawable.file_manager_browser_listmode);
		}
		treeViewAdapter = new TreeViewAdapter(m_act, R.layout.outline,
				mPdfOutlinesCount);
		TreeElement element1 = new TreeElement("SD卡", Const.Root);
		TreeElement element2 = new TreeElement("手机", Const.SDCard);
		mPdfOutlinesCount.add(element1);
		mPdfOutlinesCount.add(element2);
		m_TreeLocallist.setAdapter(treeViewAdapter);
		this.nStyle = nStyle;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		P.debug("onCreateOptionsMenu");
		return super.onCreateOptionsMenu(menu, R.menu.localpagemenu);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
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

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.muCreate:
			CreateFileDialog.Show(m_act, localManage.getCurrentPath());
			break;
		case R.id.muSearch:
			if (mSearchPage == null) {
				mSearchPage = new SearchResultPage(m_act);
			}
			if (mSearchPage.Size() > 0) {
				SysEng.getInstance().addHandlerEvent(
						new NextPageEvent(m_act, mSearchPage, 1, null));
			} else {
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
			final int position, long arg3) {
		// MobclickAgent.onEvent(m_act, "localEvent","ItemLongClick");
		FileBean temp = mFileList.get(position);
		if (!temp.isBackUp()) {
			new PopLocalMenu().ShowFile(m_act, temp, 0);
		}
		return false;
	}

	public void clear() {
		// TODO Auto-generated method stub

	}

	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	public void NotifyDataSetChanged(int cmd, Object value) {
		switch (cmd) {
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
			break;
		case Const.cmd_Local_List_Refresh:// 更新列表
			fileAdapter.notifyDataSetChanged();
			break;
		case Const.cmd_Local_List_Go: // 新数据
			mPath.setText(localManage.getCurrentPath());
			fileAdapter.Clear();
			fileAdapter.notifyDataSetChanged();
			switch (mFileType) {
			case 0:
				btFileType.setText("全部");
				break;
			case 1:
				btFileType.setText("文件夹");
				break;
			case 2:
				btFileType.setText("文件");
				break;
			}
			Integer y = mScrollList.get(localManage.getCurrentPath());
			if (y != null) {
				P.v("mScrollList=" + y);
				m_locallist.setSelection(y);
			} else {
				m_locallist.setSelection(0);
			}
			break;
		}
	}

	public void OnItemClickListener(View view, int position, long id) {
		System.out.println("position:" + position);
		TreeElement treeElement = mPdfOutlinesCount.get(position);
		if (!treeElement.isDirectory()) {
			Toast.makeText(m_act, treeElement.getFileName(), 2000).show();
			/*
			 * int pageNumber; Intent i = getIntent(); element element =
			 * mPdfOutlinesCount .get(position); pageNumber =
			 * element.getOutlineElement().pageNumber; if (pageNumber <= 0) {
			 * String name = element.getOutlineElement().destName; pageNumber =
			 * idocviewer.getPageNumberForNameForOutline(name);
			 * element.getOutlineElement().pageNumber = pageNumber;
			 * element.getOutlineElement().destName = null; }
			 * i.putExtra("PageNumber", pageNumber); setResult(RESULT_OK, i);
			 * finish();
			 */

			return;
		}

		if (treeElement.isExpanded()) {
			treeElement.setExpanded(false);
			TreeElement element = treeElement;
			ArrayList<TreeElement> temp = new ArrayList<TreeElement>();

			for (int i = position + 1; i < mPdfOutlinesCount.size(); i++) {
				if (element.getLevel() >= mPdfOutlinesCount.get(i).getLevel()) {
					break;
				}
				temp.add(mPdfOutlinesCount.get(i));
			}

			mPdfOutlinesCount.removeAll(temp);

			treeViewAdapter.notifyDataSetChanged();
			/*
			 * fileExploreAdapter = new TreeViewAdapter(this, R.layout.outline,
			 * mPdfOutlinesCount);
			 */

			// setListAdapter(fileExploreAdapter);

		} else {
			TreeElement obj = treeElement;
			obj.setExpanded(true);
			int level = obj.getLevel();
			int nextLevel = level + 1;

			for (TreeElement element : obj.getChildList()) {
				element.setLevel(nextLevel);
				element.setExpanded(false);
				mPdfOutlinesCount.add(position + 1, element);

			}
			treeViewAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 双击事件(赛事明细,事件)
	 */
	private TreeElement lastClickId;
	private Long lastClickTime;
	private OnItemClickListener mItemClickListenter = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
			TreeElement mv = mPdfOutlinesCount.get(pos);
			// MatchVO mv = (MatchVO) ((ListView)
			// parent).getAdapter().getItem(pos);
			// 如果是双击,1秒内连续点击判断为双击
			if (mv == lastClickId
					&& (Math.abs(lastClickTime - System.currentTimeMillis()) < 1500)) {
				lastClickId = null;
				lastClickTime = 0l;
				OnDoubleClickListener(parent, v, pos, id);
			} else {
				lastClickId = mv;
				lastClickTime = System.currentTimeMillis();
				OnItemClickListener(v, pos, id);
			}
		}
	};

	private void OnDoubleClickListener(AdapterView<?> parent, View v, int pos,
			long id) {
		TreeElement mv = mPdfOutlinesCount.get(pos);
		localManage.setFilePath(mv.getFilePath(), Const.cmd_Local_List_Go);
		Toast.makeText(m_act, mv.getFileName(), Toast.LENGTH_SHORT).show();
		m_TreeLocallist.setVisibility(View.GONE);
	}
}