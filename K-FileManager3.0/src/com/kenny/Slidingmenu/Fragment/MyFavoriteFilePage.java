package com.kenny.Slidingmenu.Fragment;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.KFileManager.R.color;
import com.kenny.file.Adapter.FavorDialogAdapter;
import com.kenny.file.Event.FavoriteFileEvent;
import com.kenny.file.Event.InstallEvent;
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
import com.kenny.file.page.KMainPage;
import com.kenny.file.tools.SaveData;
import com.kenny.file.tools.T;
import com.kenny.file.util.Const;
import com.kenny.file.util.Theme;

public class MyFavoriteFilePage extends ContentFragment implements
		INotifyDataSetChanged, OnItemClickListener, OnClickListener,
		OnItemLongClickListener
{
	public MyFavoriteFilePage(Activity context, FGroupInfo mNowGItem,
			ArrayList<FileBean> mAllFileList)
	{
		this.mAllFileList = mAllFileList;
		this.mNowGItem = mNowGItem;
	}

	private View m_lvMain;// 主页面
	private ListView mListView;
	private Button btAllFile, btFolder;
	private Button btBack, btInstall, btArrange;
	private Button btDelete, btSelectAll;

	private FavorDialogAdapter mFileAdapter;
	private ArrayList<FileBean> mAllFileList = new ArrayList<FileBean>();
	private FGroupInfo mNowGItem; // 当前正在浏览的分组

	/**
	 * 1:Folder 2:Item
	 */

	private View FooterView()
	{
		TextView tview = new TextView(m_act);
		tview.setHeight(100);
		tview.setWidth(-1);
		tview.setBackgroundColor(color.green);
		ListHeaderView headerView = new ListHeaderView(m_act, tview);
		return headerView;
	}

	private OnScrollListener m_AllFileOnScrollListener = new OnScrollListener()
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

	public void onCreate(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		setContentView(R.layout.favoritempanel, inflater);

		m_lvMain = mView.findViewById(R.id.lvMain);
		m_lvMain.setBackgroundColor(Theme.getBackgroundColor());
		mView.findViewById(R.id.lyBTools).setVisibility(View.GONE);
		mView.findViewById(R.id.btSelectAll).setVisibility(View.GONE);
		mView.findViewById(R.id.btInstall).setVisibility(View.GONE);
		mView.findViewById(R.id.btDelete).setVisibility(View.GONE);
		mView.findViewById(R.id.btArrange).setVisibility(View.GONE);

		mView.findViewById(R.id.lyTools2).setVisibility(View.GONE);
		mListView = (ListView) mView.findViewById(R.id.lvLocallist);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(m_AllFileOnScrollListener);
		mListView.setOnItemLongClickListener(this);
//		mListView.addFooterView(FooterView(), null, false);

		btAllFile = (Button) mView.findViewById(R.id.btAllFile);
		btAllFile.setOnClickListener(this);
		btFolder = (Button) mView.findViewById(R.id.btFolder);
		btFolder.setOnClickListener(this);

		btBack = (Button) mView.findViewById(R.id.btBack);
		btBack.setOnClickListener(this);

		btInstall = (Button) mView.findViewById(R.id.btInstall);
		btInstall.setOnClickListener(this);

		btDelete = (Button) mView.findViewById(R.id.btDelete);
		btDelete.setOnClickListener(this);

		btArrange = (Button) mView.findViewById(R.id.btArrange);
		btArrange.setOnClickListener(this);

		btSelectAll = (Button) mView.findViewById(R.id.btSelectAll);
		btSelectAll.setOnClickListener(this);

		btAllFile.setBackgroundResource(R.drawable.tab2_left_select);
		btFolder.setBackgroundResource(R.drawable.tab2_right_unselect);
		btFolder.setTextColor(m_act.getResources().getColor(
				R.color.tab_TextColor_selected));
		btAllFile.setTextColor(m_act.getResources().getColor(
				R.color.tab_TextColor_normal));
		mFileAdapter = new FavorDialogAdapter(m_act, 1, mAllFileList);
		mListView.setAdapter(mFileAdapter);

		// FavoriteItemInit();
	}
	public void onPause()
	{
		super.onPause();
	}

	public void onResume()
	{
		super.onResume();
		m_lvMain.setBackgroundColor(Theme.getBackgroundColor());
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btInstall:
			InstallApp();
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
		for (int i = 0; i < mAllFileList.size(); i++)
		{
			FileBean tmpInfo = mAllFileList.get(i);

			if (tmpInfo.isChecked())
			{
				mInstallFiles.add(tmpInfo);
			}
		}
		if (mInstallFiles.size() > 0)
		{
			SysEng.getInstance().addEvent(
					new InstallEvent(m_act, mInstallFiles,
							MyFavoriteFilePage.this));
			return;
		}
		Toast.makeText(m_act,
				m_act.getString(R.string.msg_Please_select_instal_file) + "!",
				Toast.LENGTH_SHORT).show();
	}

	private void SelectAll()
	{
		if (mAllFileList.size() > 2)
		{
			boolean check = !mAllFileList.get(1).isChecked();
			for (int i = 1; i < mAllFileList.size(); i++)
			{
				FileBean tmpInfo = mAllFileList.get(i);
				tmpInfo.setChecked(check);
			}
			mFileAdapter.notifyDataSetChanged();
		}
	}

	public void DeleteFile(FileBean file)
	{
		SysEng.getInstance().addEvent(
				new delFileEvent(m_act, file, MyFavoriteFilePage.this));
	}

	private void deletefiles()
	{
		final ArrayList<FileBean> mDelFiles = new ArrayList<FileBean>();
		for (int i = 0; i < mAllFileList.size(); i++)
		{
			FileBean tmpInfo = mAllFileList.get(i);

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
													MyFavoriteFilePage.this));
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

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(
			com.actionbarsherlock.view.MenuInflater inflater, Menu menu)
	{
		inflater.inflate(R.menu.favoritepagemenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		return super.onOptionsItemSelected(item);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		FileBean temp = mAllFileList.get(position);
		final File mFile = temp.getFile();
		if (!mFile.isDirectory())
		{
			SysEng.getInstance().addHandlerEvent(
					new openDefFileEvent(m_act, mFile.getPath()));
		} else
		{
//			FileManager.getInstance().setFilePath(mFile.getPath());
			switchFragment(new LocalPage(mFile.getPath()));
//			KMainPage.mKMainPage.ChangePage(KMainPage.Local, null);
		}
	}

	public void NotifyDataSetChanged(final int cmd, Object value)
	{
		if (Const.cmd_LoadSDFile_State != cmd)
		{
			P.debug("Fav.NotifyDataSetChanged:cmd=" + cmd);
		}
	}

	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3)
	{
		// MobclickAgent.onEvent(m_act, "FavoriteEvent","ItemLongClick");
		FileBean temp = mFileAdapter.getItem(arg2);
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