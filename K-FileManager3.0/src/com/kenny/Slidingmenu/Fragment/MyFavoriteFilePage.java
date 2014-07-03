package com.kenny.Slidingmenu.Fragment;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Adapter.FavorDialogAdapter;
import com.kenny.file.Event.FavoriteFileEvent;
import com.kenny.file.Event.delFileEvent;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.Event.openFileEvent;
import com.kenny.file.bean.FGroupInfo;
import com.kenny.file.bean.FileBean;
import com.kenny.file.dialog.KDialog;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.tools.T;
import com.kenny.file.util.Const;
import com.kenny.file.util.Theme;

public class MyFavoriteFilePage extends ContentFragment implements
		INotifyDataSetChanged, OnItemClickListener, 
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

	private FavorDialogAdapter mFileAdapter;
	private ArrayList<FileBean> mAllFileList = new ArrayList<FileBean>();
	private FGroupInfo mNowGItem; // 当前正在浏览的分组

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
//		mView.findViewById(R.id.icEmptyPannal).setVisibility(View.GONE);
		m_lvMain = mView.findViewById(R.id.lvMain);
		m_lvMain.setBackgroundColor(Theme.getBackgroundResource());
		mView.findViewById(R.id.lyBTools).setVisibility(View.GONE);
		mView.findViewById(R.id.btSelectAll).setVisibility(View.GONE);
		mView.findViewById(R.id.btInstall).setVisibility(View.GONE);
		mView.findViewById(R.id.btDelete).setVisibility(View.GONE);
		mView.findViewById(R.id.btArrange).setVisibility(View.GONE);

		mListView = (ListView) mView.findViewById(R.id.lvLocallist);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(m_AllFileOnScrollListener);
		mListView.setOnItemLongClickListener(this);
		mFileAdapter = new FavorDialogAdapter(m_act, 1, mAllFileList);
		mListView.setAdapter(mFileAdapter);
		if(mAllFileList.size()<=0)
		{
			mView.findViewById(R.id.icEmptyPannal).setVisibility(View.VISIBLE);
		}
		// FavoriteItemInit();
	}
	public void onPause()
	{
		super.onPause();
	}

	public void onResume()
	{
		super.onResume();
		m_lvMain.setBackgroundResource(Theme.getBackgroundResource());
	}



	public void DeleteFile(FileBean file)
	{
		SysEng.getInstance().addEvent(
				new delFileEvent(m_act, file, MyFavoriteFilePage.this));
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
		return true;
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
//					FileManager.getInstance().setFilePath(
//							);
					String path=file.getFilePath().substring(
							0,
							file.getFilePath().lastIndexOf(
									File.separator));
//					KMainPage.mKMainPage.ChangePage(KMainPage.Local, null);
					LocalFragmentActivity.actionSettingPage(m_act, path);
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