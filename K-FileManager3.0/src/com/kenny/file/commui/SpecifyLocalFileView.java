package com.kenny.file.commui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
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
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.event.ObjectLinearLayout;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.KFileManager.R.color;
import com.kenny.file.Adapter.FileAdapter;
import com.kenny.file.Event.InstallEvent;
import com.kenny.file.Event.copyFileEvent;
import com.kenny.file.Event.cutFileEvent;
import com.kenny.file.Event.delFileEvent;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.bean.FileBean;
import com.kenny.file.dialog.ViewSortDialog;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.manager.SpecifyManager;
import com.kenny.file.menu.MGoneAinm;
import com.kenny.file.menu.MVisibleAinm;
import com.kenny.file.menu.PopLocalMenu;
import com.kenny.file.util.Const;
import com.kenny.file.util.Theme;

public class SpecifyLocalFileView extends ObjectLinearLayout implements
		OnItemLongClickListener, INotifyDataSetChanged,
		OnClickListener, OnItemClickListener {
	/*
	 * 声明成员变量： mFileName ：存放显示的文件列表的名称 mFilePaths ：存放显示的文件列表的相对应的路径 mRootPath
	 * ：起始目录“/” (用java.io.File.separator获取) mSDCard ： SD卡根目录 mPath
	 * ：显示当前路径的TextView文本组件
	 */
	private List<FileBean> mLocalFileList = null;
	private View m_lvMain;// 主页面
	private TextView mPath;
	private ListView m_locallist;
	private GridView m_localGrid;
	private FileAdapter fileAdapter;
	private SpecifyManager localManage;
	private int nStyle = 0, nSortMode = 0; // false:listView true:gridView

	private Button btMenuListSort, btMenuListMode, btMenuShowOrHide;

	public SpecifyLocalFileView(Context context, String path) {
		super(context);
		P.v("LocalFilePage:LocalFilePage");
		localManage = new SpecifyManager(path);
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
		setContentView(R.layout.specifylocalpage);
		// String apkRoot="chmod 777 "+m_act.getPackageCodePath();
		// cmd.RootCommand(apkRoot);
		m_lvMain = findViewById(R.id.lvMain);
		m_lvMain.setBackgroundColor(Theme.getBackgroundColor());
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
		m_locallist.setOnItemLongClickListener(this);
		m_locallist.setOnScrollListener(m_localOnScrollListener);
		m_locallist.setOnItemClickListener(this);
		m_localGrid = (GridView) findViewById(R.id.gvLocallist);
		m_localGrid.setOnItemClickListener(this);
		m_localGrid.setOnItemLongClickListener(this);
		m_localGrid.setOnScrollListener(m_localOnScrollListener);
		mPath = (TextView) findViewById(R.id.mCurrentPath);
		localManage.Refresh();
		mPath.setText(localManage.getCurrentPath());
		mLocalFileList = localManage.getFileList();
		TextView tview = new TextView(getContext());
		tview.setHeight(150);
		tview.setWidth(-1);
		tview.setVisibility(View.VISIBLE);
		tview.setBackgroundColor(color.green);
		ListHeaderView headerView = new ListHeaderView(getContext(), tview);
		m_locallist.addFooterView(headerView, null, false);
		localManage.setNotifyData(this);

		Button btButton = (Button) findViewById(R.id.btNew);
		btButton.setOnClickListener(this);

		btButton = (Button) findViewById(R.id.btToolsMemu);
		btButton.setOnClickListener(this);
		btButton = (Button) findViewById(R.id.btBack);
		btButton.setOnClickListener(this);

		btButton = (Button) findViewById(R.id.btInstall);
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

		btButton = (Button) findViewById(R.id.btInstall);
		btButton.setOnClickListener(this);

		SwitchStyle(Theme.getStyleMode());
		onCreateMenu();
	}

	public void onCreateMenu() {
		final RelativeLayout rlMenu = (RelativeLayout) findViewById(R.id.ip_menu);
		rlMenu.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				rlMenu.setVisibility(View.GONE);
				return false;
			}
		});
		final MGoneAinm goneAnim = new MGoneAinm(getContext(), rlMenu,
				findViewById(R.id.main_menu));
		Button btToolsMemu = (Button) findViewById(R.id.btToolsMemu);
		btToolsMemu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (rlMenu.getVisibility() == View.VISIBLE) {
					goneAnim.ShowAnim();
				} else {
					if (Theme.getShowHideFile()) {
						btMenuShowOrHide
								.setText(R.string.btMenuShowOrHide_False);
					} else {
						btMenuShowOrHide
								.setText(R.string.btMenuShowOrHide_True);
					}
					if (Theme.getStyleMode() == 1) {
						btMenuListMode.setText(R.string.btMenuListMode_List);
					} else {
						btMenuListMode.setText(R.string.btMenuListMode_Grid);
					}
					new MVisibleAinm(getContext(), rlMenu,
							findViewById(R.id.main_menu)).ShowAnim();

				}
			}
		});
		OnClickListener menuListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				goneAnim.ShowAnim();
				switch (v.getId()) {
				case R.id.btMenuListSort:
					new ViewSortDialog().ShowDialog(getContext(),
							SpecifyLocalFileView.this);
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
					Theme.Save(getContext());
					localManage.setFilePath(localManage.getCurrentPath(),
							Const.cmd_Local_List_Go);
					break;
				// case R.id.btMenuFavorites:
				// Rect rect = new Rect();
				// findViewById(R.id.btToolsMemu).getGlobalVisibleRect(rect);
				// FavoritesDialog.ShowDialog(m_act, rect.bottom);
				// break;
				}
			}
		};
		btMenuListSort = (Button) findViewById(R.id.btMenuListSort);
		btMenuListSort.setOnClickListener(menuListener);
		btMenuListMode = (Button) findViewById(R.id.btMenuListMode);
		btMenuListMode.setOnClickListener(menuListener);
		btMenuShowOrHide = (Button) findViewById(R.id.btMenuShowOrHide);
		btMenuShowOrHide.setOnClickListener(menuListener);
	}
	
	public void onResume() {
		P.v("Log.DEBUG", "onResume");
		IntentFilter sdCardFilter = new IntentFilter(
				Intent.ACTION_MEDIA_MOUNTED);
		sdCardFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		sdCardFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		sdCardFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		sdCardFilter.addDataScheme("file");
		getContext().registerReceiver(sdcardReceiver, sdCardFilter);// 注册监听函数

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
			getContext().unregisterReceiver(sdcardReceiver);
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
		mLocalFileList.clear();
	}

	public void onReload() {
		P.v("LocalFilePage:onReload");
		 m_lvMain.setBackgroundColor(Theme.getBackgroundColor());
		if (nStyle != Theme.getStyleMode()) {
			SwitchStyle(Theme.getStyleMode());
		}
	}

	private void InstallApp() {
		final ArrayList<FileBean> mSelFiles = new ArrayList<FileBean>();
		for (int i = 0; i < fileAdapter.getCount(); i++) {
			FileBean tmpInfo = fileAdapter.getItem(i);
			if (tmpInfo.isChecked() && tmpInfo.getFileEnds().equals("apk")) {
				mSelFiles.add(tmpInfo);
			}
		}
		if (mSelFiles.size() > 0) {
			SysEng.getInstance().addEvent(
					new InstallEvent(getContext(), mSelFiles,
							SpecifyLocalFileView.this));
			return;
		}
		Toast.makeText(getContext(),
				getContext().getString(R.string.msg_Please_select_instal_file) + "!",
				Toast.LENGTH_SHORT).show();
	}

	private boolean Back() {
		if (findViewById(R.id.ip_menu).getVisibility() == View.VISIBLE) {
			new MGoneAinm(getContext(), findViewById(R.id.ip_menu),
					findViewById(R.id.main_menu)).ShowAnim();
			return true;
		}
		return localManage.Back();
	}

	public boolean onKeyDown(int keyCode, KeyEvent msg) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return Back();
		}
		return false;
	}

	/**
	 * 点击激活相应的窗体
	 */

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		FileBean temp = null;
		if (mLocalFileList.size() > position) {
			temp = mLocalFileList.get(position);
		}
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
			// 如果该文件是可读的，我们进去查看文件
			if (mFile.isDirectory()) {
				if (mFile.canRead()) {
					// 如果是文件夹，则直接进入该文件夹，查看文件目录
					localManage.setFilePath(mFile.getPath(),
							Const.cmd_Local_List_Go);
				} else {// 如果该文件不可读，我们给出提示不能访问，防止用户操作系统文件造成系统崩溃等
					Toast.makeText(
							getContext(),
							getContext().getString(R.string.msg_sorry_file_not_exist_permissions),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				if (mFile.canRead()) {
					if (!mFile.exists()) {
						Toast.makeText(getContext(),
								getContext().getString(R.string.msg_not_find_file),
								Toast.LENGTH_SHORT).show();
						return;
					}
					SysEng.getInstance().addHandlerEvent(
							new openDefFileEvent(getContext(), mFile.getPath()));
				} else {// 如果该文件不可读，我们给出提示不能访问，防止用户操作系统文件造成系统崩溃等
					Toast.makeText(
							getContext(),
							getContext().getString(R.string.msg_sorry_file_permissions),
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btListSort:// 排序
			new ViewSortDialog().ShowDialog(getContext(), this);
			break;
		case R.id.btListStyle:
			if (nStyle == 0) {
				SwitchStyle(1);
			} else {
				SwitchStyle(0);
			}
			break;
		case R.id.btCopy:
			SysEng.getInstance().addHandlerEvent(
					new copyFileEvent(getContext(), localManage.getFileList(),
							localManage));
			break;
		case R.id.btCut:
			SysEng.getInstance().addHandlerEvent(
					new cutFileEvent(getContext(), localManage.getFileList(),
							localManage));
			break;
		case R.id.btDelete:
			deletefiles();
			break;
		case R.id.btSelectAll:
			SelectAll();
			break;
		case R.id.btBack:
			onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(1, 1));
			break;
		case R.id.btInstall:
			P.v("btInstall");
			InstallApp();
			break;
		}
	}

	private void deletefiles() {
		if (mLocalFileList.size() > 0) {
			final ArrayList<FileBean> mDelFiles = new ArrayList<FileBean>();
			for (int i = 0; i < mLocalFileList.size(); i++) {
				FileBean tmpInfo = mLocalFileList.get(i);
				if (tmpInfo.isChecked()) {
					mDelFiles.add(tmpInfo);
				}
			}
			if (mDelFiles.size() > 0) {
				new AlertDialog.Builder(getContext())
						.setTitle(
								getContext().getString(R.string.msg_dialog_info_title))
						.setMessage(getContext().getString(R.string.msg_delselectfile))
						.setPositiveButton(getContext().getString(R.string.ok),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										SysEng.getInstance()
												.addEvent(
														new delFileEvent(
																getContext(),
																mDelFiles,
																SpecifyLocalFileView.this));
									}
								})
						.setNegativeButton(getContext().getString(R.string.cancel),
								null).show();
				return;
			}
		}
		Toast.makeText(getContext(),
				getContext().getString(R.string.msg_please_del_operate_file),
				Toast.LENGTH_SHORT).show();
	}

	private void SelectAll() {
		if (mLocalFileList.size() >= 2) {
			boolean check = !mLocalFileList.get(1).isChecked();
			for (int i = 1; i < mLocalFileList.size(); i++) {
				FileBean tmpInfo = mLocalFileList.get(i);
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
		if (nStyle != this.nStyle) {

			Theme.setStyleMode(nStyle);
			Theme.Save(getContext());
		}
		if (nStyle == 1) 
		{
			fileAdapter = new FileAdapter(getContext(), 2, mLocalFileList, this);
			m_localGrid.setAdapter(fileAdapter);
			m_locallist.setVisibility(View.GONE);
			m_localGrid.setVisibility(View.VISIBLE);
		} else {
			fileAdapter = new FileAdapter(getContext(), 1, mLocalFileList, this);
			m_locallist.setAdapter(fileAdapter);
			m_localGrid.setVisibility(View.GONE);
			m_locallist.setVisibility(View.VISIBLE);
		}
		this.nStyle = nStyle;
	}

	/** 长按列表项的事件监听:对长按需要进行一个控制，当列表中包括”返回根目录“和”返回上一级“时，需要对这两列进行屏蔽 */

	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			final int position, long arg3) {
		FileBean temp = mLocalFileList.get(position);
		if (!temp.isBackUp()) {
			new PopLocalMenu().ShowFile(getContext(), temp, 0);
		}
		return false;
	}

	public void NotifyDataSetChanged(int cmd, Object value) {
		switch (cmd) {
		case Const.cmd_DelFileEvent_Finish:
			localManage.setFilePath(localManage.getCurrentPath(),
					Const.cmd_Local_List_Go);
			fileAdapter.notifyDataSetChanged();
			break;
		case Const.cmd_Local_ListSort_Finish:
			nSortMode = Theme.getSortMode();
			localManage.setFilePath(localManage.getCurrentPath(),
					Const.cmd_Local_List_Go);
			SwitchStyle(Theme.getStyleMode());
			fileAdapter.Clear();
			break;
		case Const.cmd_Local_List_Refresh:// 更新列表
			fileAdapter.notifyDataSetChanged();
			fileAdapter.Clear();
			break;
		case Const.cmd_Local_List_Go: // 新数据
			mPath.setText(localManage.getCurrentPath());
			fileAdapter.Clear();
			fileAdapter.notifyDataSetChanged();
			break;
		}
	}
}