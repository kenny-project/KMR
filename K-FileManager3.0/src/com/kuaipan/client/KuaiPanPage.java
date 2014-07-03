package com.kuaipan.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.kuaipan.android.sdk.KPConstants;
import cn.kuaipan.android.sdk.KPManager;
import cn.kuaipan.android.sdk.ui.KPLoginView;

import com.framework.event.AbsEvent;
import com.framework.event.ParamEvent;
import com.framework.log.P;
import com.framework.syseng.KSysEng;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.KFileManager.R.color;
import com.kenny.file.Adapter.KuaiPanFileAdapter;
import com.kenny.file.Event.KuaiPanFileListEvent;
import com.kenny.file.Event.delFileEvent;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.Event.uploadKPFileEvent;
import com.kenny.file.bean.FileBean;
import com.kenny.file.commui.ListHeaderView;
import com.kenny.file.dialog.CreateKPFileDialog;
import com.kenny.file.dialog.openFileSelectDialog;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.interfaces.KActivityStatus;
import com.kenny.file.tools.SaveData;
import com.kenny.file.tools.T;
import com.kenny.file.util.Const;
import com.kuaipan.client.model.KuaipanFile;
import com.kuaipan.client.model.KuaipanPublicLink;
import com.kuaipan.client.model.KuaipanUser;
import com.kuaipan.demo.SimpleCommandConsole;

public class KuaiPanPage extends Activity implements OnItemClickListener,
		INotifyDataSetChanged, OnClickListener, KActivityStatus,
		OnItemLongClickListener
{
	private Button btError;
	private ListView m_locallist, m_DownLoadlist;
	private KuaiPanFileAdapter fileAdapter;
	private SimpleCommandConsole cli;

	private ArrayList<KuaipanFile> mFileList = new ArrayList<KuaipanFile>();
	private ArrayList<KuaipanFile> mDownLoadList = new ArrayList<KuaipanFile>();
	private View rlError, pbLoading;
	private TextView mCurrentPath;
	private TextView tvError_msg, tvKuaiPanSpace;// 空间
	private int nCommCode = 0;// 0:没有错误
	private View lyTools2, lyBTools;// lyUpLoadTools,
			// private List<FileBean> mFilelist;
	private String oauth_token = "";
	private String oauth_secret = "";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		cli = SimpleCommandConsole.getHandler(this);
		setContentView(R.layout.kuaipanpage);

		lyTools2 = findViewById(R.id.lyTools2);
		lyBTools = findViewById(R.id.lyBTools);

		m_locallist = (ListView) findViewById(R.id.lvLocallist);
		m_DownLoadlist = (ListView) findViewById(R.id.lvDownLoadlist);
		fileAdapter = new KuaiPanFileAdapter(this, mFileList);

		m_locallist.setOnItemClickListener(this);
		m_DownLoadlist.setOnItemClickListener(this);
		TextView tview = new TextView(this);
		tview.setHeight(150);
		tview.setWidth(-1);
		tview.setVisibility(View.VISIBLE);
		tview.setBackgroundColor(color.green);
		ListHeaderView headerView = new ListHeaderView(this, tview);
//		m_locallist.addFooterView(headerView, null, false);
		m_DownLoadlist.addFooterView(headerView, null, false);
		m_locallist.setAdapter(fileAdapter);
		m_locallist.setOnItemLongClickListener(this);
		m_DownLoadlist.setAdapter(fileAdapter);
		m_DownLoadlist.setVisibility(View.GONE);
		rlError = findViewById(R.id.rlError);
		pbLoading = findViewById(R.id.pbLoading);

		tvError_msg = (TextView) findViewById(R.id.tvError_msg);
		mCurrentPath = (TextView) findViewById(R.id.mCurrentPath);
		tvKuaiPanSpace = (TextView) findViewById(R.id.tvKuaiPanSpace);

		findViewById(R.id.btUpDownLoad).setOnClickListener(this);
		Button btButton = (Button) findViewById(R.id.btNew);
		btButton.setOnClickListener(this);

		btButton = (Button) findViewById(R.id.btBack);
		btButton.setOnClickListener(this);

		btButton = (Button) findViewById(R.id.btDownLoad);
		btButton.setOnClickListener(this);

		btButton = (Button) findViewById(R.id.btDelete);
		btButton.setOnClickListener(this);

		btButton = (Button) findViewById(R.id.btSelectAll);
		btButton.setOnClickListener(this);

		btButton = (Button) findViewById(R.id.btBack);
		btButton.setOnClickListener(this);

		btError = (Button) findViewById(R.id.btError);
		btError.setOnClickListener(this);

		btButton = (Button) findViewById(R.id.btBackPage);
		btButton.setOnClickListener(this);
		IntentFilter filter = new IntentFilter(KPConstants.ACTION_KP_AUTH);
		this.registerReceiver(kpReceiver, filter);
		init();
	}

	private void init()
	{
		oauth_token = SaveData.Read(this, "oauth_token", "");
		oauth_secret = SaveData.Read(this, "oauth_secret", "");
		if (oauth_token.length() <= 0 || oauth_secret.length() <= 0)
		{
			try
			{
				NotifyDataSetChanged(Const.cmd_KuaiPan_OAuth_Error, null);
				KPManager.init(Const.consumer_key, Const.consumer_secret);
				Intent intent = new Intent(this, KPLoginView.class);
				this.startActivity(intent);

			} catch (Exception e)
			{
				e.printStackTrace();
			}
		} else
		{
			cli.setAuthToken(oauth_token, oauth_secret);
			getAccountInfo();
			Refresh();
		}

	}

	@Override
	public void onResume()
	{
		if (oauth_token.length() > 0 || oauth_secret.length() > 0)
		{
			fileAdapter.notifyDataSetChanged();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		this.unregisterReceiver(kpReceiver);
		super.onDestroy();

	}

	private BroadcastReceiver kpReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			oauth_token = intent.getStringExtra(KPConstants.KP_BROADCAST_TOKEN);
			oauth_secret = intent
					.getStringExtra(KPConstants.KP_BROADCAST_SECRET);
			// 执行相应的操作
			SaveData.Write(KuaiPanPage.this, "oauth_token", oauth_token);
			SaveData.Write(KuaiPanPage.this, "oauth_secret", oauth_secret);
			cli.setAuthToken(oauth_token, oauth_secret);
			if (oauth_token.length() > 0 || oauth_secret.length() > 0)
			{
				getAccountInfo();
				SysEng.getInstance().addEvent(
						new KuaiPanFileListEvent(KuaiPanPage.this, cli
								.getPath(), cli, pbLoading, KuaiPanPage.this));
			} else
			{
				Toast.makeText(getApplicationContext(), "登录失败",
						Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	};

	/**
	 * 分享
	 * 
	 * @param fileName
	 */
	private void getShares(final String fileName)
	{
		SysEng.getInstance().addEvent(new AbsEvent()
		{

			@Override
			public void ok()
			{
				KuaipanPublicLink mKuaiPanUser = KuaipanAPI.shares(fileName,
						"", "");
				if (mKuaiPanUser != null)
				{
					T.SendShare(KuaiPanPage.this, "共享链接", "分享:"
							+ mKuaiPanUser.url);
				}
			}
		});
	}

	private void getAccountInfo()
	{
		SysEng.getInstance().addEvent(new AbsEvent()
		{
			@Override
			public void ok()
			{
				KuaipanUser mKuaiPanUser = KuaipanAPI.accountInfo();
				if (mKuaiPanUser != null)
				{
					String AccountInfo = T
							.FileSizeToString(mKuaiPanUser.quota_used)
							+ "/"
							+ T.FileSizeToString(mKuaiPanUser.quota_total);
					NotifyDataSetChanged(Const.cmd_KuaiPan_AccountInfo,
							AccountInfo);
				}
			}
		});
	}

	public boolean onTouchEvent(MotionEvent event)
	{
		return false;
	}

	private void Refresh()
	{
		if (oauth_token.length() == 0 || oauth_secret.length() == 0)
		{
			oauth_token = "";
			oauth_secret = "";
			cli.setAuthToken(oauth_token, oauth_secret);
			nCommCode = Const.cmd_KuaiPan_OAuth_Error;
			NotifyDataSetChanged(nCommCode, null);
		} else
		{
			SysEng.getInstance().addEvent(
					new KuaiPanFileListEvent(KuaiPanPage.this, cli.getPath(),
							cli, pbLoading, this));
		}
	}

	/**
	 * 返回上一层目录
	 * 
	 * @return
	 */
	private boolean BackFile()
	{
		if (oauth_token.length() == 0 || oauth_secret.length() == 0)
		{
			// finish();
			return false;
		}
		return CmdFile("..");
	}

	private boolean CmdFile(String dir)
	{
		String tempPath = cli.do_cd(dir);
		P.v("tempPath=" + tempPath);
		if (!tempPath.equals(cli.getPath()))
		{
			SysEng.getInstance().addEvent(
					new KuaiPanFileListEvent(KuaiPanPage.this, tempPath, cli,
							pbLoading, this));
			return true;
		} else
		{
			// Toast.makeText(KuaiPanPage.this, "已经到达根目录!", Toast.LENGTH_SHORT)
			// .show();
			// this.finish();
			return false;
		}
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		KuaipanFile temp = mFileList.get(position);
		if (temp != null)
		{
			if (temp.isBackUp())
			{
				CmdFile("..");
				return;
			}
			// 如果该文件是可读的，我们进去查看文件
			if (temp.isDirectory())
			{
				CmdFile(temp.getFileName());
				// {// 如果该文件不可读，我们给出提示不能访问，防止用户操作系统文件造成系统崩溃等
				// Toast.makeText(KuaiPanPage.this, "该文件夹不存在或权限不够!",
				// Toast.LENGTH_SHORT)
			} else
			{
				if (temp.exists())
				{
					SysEng.getInstance().addHandlerEvent(
							new openDefFileEvent(KuaiPanPage.this, temp
									.getFilePath()));
				} else
				{
					Toast.makeText(KuaiPanPage.this, "添加到下载队列",
							Toast.LENGTH_SHORT).show();
					temp.setContext(KuaiPanPage.this);
					KSysEng.getInstance().addDLEvent(temp);
				}
			}
		}
	}

	public void NotifyDataSetChanged(int cmd, Object value)
	{
		mNotifyData.setKey(cmd);
		mNotifyData.setValue(value);
		SysEng.getInstance().addHandlerEvent(mNotifyData);
	}

	private ParamEvent mNotifyData = new ParamEvent()
	{
		public void ok()
		{
			P.v("KuaiPan getKey()=" + getKey());
			switch (getKey())
			{
			case openFileSelectDialog.SelectList:
				List<FileBean> list = (List<FileBean>) getValue();
				SysEng.getInstance().addEvent(
						new uploadKPFileEvent(KuaiPanPage.this, list,
								KuaiPanPage.this));
				break;
			case Const.cmd_KuaiPan_uploadfile_error:
				break;
			case Const.cmd_KuaiPan_upload_Error:
				break;
			case Const.cmd_KuaiPan_AccountInfo:
				String size = (String) getValue();
				tvKuaiPanSpace.setText(size);
				break;
			case Const.cmd_KuaiPan_upload_Finish:// 上传完成 by wmh
				// if (mFilelist != null)
				// {
				// mFilelist.clear();
				// mFilelist = null;
				// }
				// lyUpLoadTools.setVisibility(View.GONE);
				lyBTools.setVisibility(View.VISIBLE);
				Refresh();
				Toast.makeText(
						KuaiPanPage.this,
						KuaiPanPage.this
								.getString(R.string.msg_upload_file_succeed),
						Toast.LENGTH_SHORT).show();
				break;
			case Const.cmd_KuaiPan_LS:
				mFileList.clear();
				try
				{
					mFileList.addAll((List<KuaipanFile>) getValue());
					fileAdapter.notifyDataSetChanged();
					m_locallist.setVisibility(View.VISIBLE);
					lyBTools.setVisibility(View.VISIBLE);
					rlError.setVisibility(View.GONE);
					mCurrentPath.setText(cli.getPath());
				} catch (Exception e)
				{
					e.printStackTrace();
					tvError_msg.setText("获取文件列表出错!,请稍候在试");
					btError.setText("重试");
					rlError.setVisibility(View.VISIBLE);
					m_locallist.setVisibility(View.GONE);
					// lyUpLoadTools.setVisibility(View.GONE);
					lyBTools.setVisibility(View.GONE);
					lyTools2.setVisibility(View.GONE);
				}
				break;
			case Const.cmd_KuaiPan_LS_Error:
				tvError_msg.setText("网络不稳定,获取列表失败,请稍候在试");
				btError.setText("重试");
				rlError.setVisibility(View.VISIBLE);
				m_locallist.setVisibility(View.GONE);
				m_DownLoadlist.setVisibility(View.GONE);
				// lyUpLoadTools.setVisibility(View.GONE);
				lyBTools.setVisibility(View.GONE);
				lyTools2.setVisibility(View.GONE);
				break;
			case Const.cmd_KuaiPan_LS_Error_NoNetWork:
				tvError_msg.setText("获取文件列表失败,未找到网络!,请稍候在试");
				btError.setText("重试");
				rlError.setVisibility(View.VISIBLE);
				lyTools2.setVisibility(View.GONE);
				lyBTools.setVisibility(View.GONE);
				m_locallist.setVisibility(View.GONE);
				m_DownLoadlist.setVisibility(View.GONE);
				break;
			case Const.cmd_KuaiPan_OAuth_Error:
				rlError.setVisibility(View.VISIBLE);
				lyBTools.setVisibility(View.GONE);
				m_locallist.setVisibility(View.GONE);
				m_DownLoadlist.setVisibility(View.GONE);
				lyTools2.setVisibility(View.GONE);
				tvError_msg.setText("未登录或登录失败,请重新登录");
				btError.setText("登录");
			}
			nCommCode = getKey();
		}
	};

	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.btUpDownLoad:
			openFileSelectDialog dialog = new openFileSelectDialog();
			dialog.ShowDialog(this, Const.getSDCard(), this);
			break;
		case R.id.btBackPage:
			finish();
			break;
		case R.id.btRefresh:
			Refresh();
			break;
		case R.id.btNew:
			CreateKPFileDialog.Show(KuaiPanPage.this, cli, this);
			break;
		case R.id.btDelete:
			deletefiles();
			break;
		case R.id.btDownLoad:
			DownLoads();
			break;
		case R.id.btSelectAll:
			SelectAll();
			break;
		case R.id.btBack:
			CmdFile("..");
			break;
		case R.id.btError:
			ErrorOnClick();
			break;
		// case R.id.btUpLoad://上传文件
		// SysEng.getInstance().addEvent(
		// new uploadKPFileEvent(KuaiPanPage.this, mFilelist, this));
		// break;
		// case R.id.btCancel:
		// mFilelist.clear();
		// mFilelist = null;
		// lyUpLoadTools.setVisibility(View.GONE);
		// break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_BACK:
			if (BackFile())
			{
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void ErrorOnClick()
	{
		P.v("ErrorOnClick:nCommCode=" + nCommCode);
		switch (this.nCommCode)
		{
		case Const.cmd_KuaiPan_OAuth_Error:// 授权失败
			try
			{
				KPManager.init(Const.consumer_key, Const.consumer_secret);
				Intent intent = new Intent(KuaiPanPage.this, KPLoginView.class);
				KuaiPanPage.this.startActivity(intent);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			break;
		case Const.cmd_KuaiPan_LS_Error:
		case Const.cmd_KuaiPan_LS_Error_NoNetWork:
			Refresh();
			break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo)
	{
		// TODO Auto-generated method stub
		menu.clear();
		MenuInflater inflater = KuaiPanPage.this.getMenuInflater();
		inflater.inflate(R.menu.kuaipanpagemenu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub
		menu.clear();
		MenuInflater inflater = KuaiPanPage.this.getMenuInflater();
		inflater.inflate(R.menu.kuaipanpagemenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub
		menu.clear();
		MenuInflater inflater = KuaiPanPage.this.getMenuInflater();
		inflater.inflate(R.menu.kuaipanpagemenu, menu);
		return super.onPrepareOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.muLogout:
			oauth_token = "";
			oauth_secret = "";
			SaveData.Write(KuaiPanPage.this, "oauth_token", oauth_token);
			SaveData.Write(KuaiPanPage.this, "oauth_secret", oauth_secret);
			cli.setAuthToken(oauth_token, oauth_secret);
			SysEng.getInstance().addEvent(
					new delFileEvent(KuaiPanPage.this, new FileBean(new File(
							Const.szKuaiPanPath), null)));
			nCommCode = Const.cmd_KuaiPan_OAuth_Error;
			m_locallist.setVisibility(View.GONE);
			NotifyDataSetChanged(nCommCode, null);
			break;
		case R.id.muClearCache:
			new AlertDialog.Builder(KuaiPanPage.this)
					.setTitle("提示!")
					.setMessage("确定要清空缓存吗?")
					.setPositiveButton(KuaiPanPage.this.getString(R.string.ok),
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int which)
								{
									SysEng.getInstance()
											.addEvent(
													new delFileEvent(
															KuaiPanPage.this,
															new FileBean(
																	new File(
																			Const.szKuaiPanPath),
																	null)));
								}
							})
					.setNegativeButton(
							KuaiPanPage.this.getString(R.string.cancel), null)
					.show();
			break;
		}
		return false;
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

	private void DownLoads()
	{
		if (mFileList.size() > 0)
		{
			final ArrayList<KuaipanFile> mDelFiles = new ArrayList<KuaipanFile>();
			for (int i = 0; i < mFileList.size(); i++)
			{
				KuaipanFile tmpInfo = mFileList.get(i);
				if (tmpInfo.isChecked())
				{
					mDelFiles.add(tmpInfo);
				}
			}
			KSysEng mKSysEng = KSysEng.getInstance();
			mDownLoadList.addAll(mDelFiles);
			if (mDelFiles.size() > 0)
			{
				try
				{
					for (int i = 0; i < mDelFiles.size(); i++)
					{
						KuaipanFile tmpInfo = mDelFiles.get(i);
						// cli.do_cat(tmpInfo.getFileName());
						tmpInfo.setContext(KuaiPanPage.this);
						mKSysEng.addDLEvent(tmpInfo);
					}
					Toast.makeText(KuaiPanPage.this, "已添加到下载对队列!",
							Toast.LENGTH_SHORT).show();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				return;
			}
		}
		Toast.makeText(KuaiPanPage.this, "请选择需要下载的文件!", Toast.LENGTH_SHORT)
				.show();
	}

	private void deletefiles(KuaipanFile mAppInfo)
	{
		try
		{
			cli.do_rm(mAppInfo.getFileName());
			Toast.makeText(KuaiPanPage.this, "删除成功!", Toast.LENGTH_SHORT)
					.show();
			Refresh();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(KuaiPanPage.this, "删除失败!", Toast.LENGTH_SHORT)
					.show();
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

				new AlertDialog.Builder(KuaiPanPage.this)
						.setTitle("提示!")
						.setMessage("确定删除已选的文件吗?")
						.setPositiveButton(
								KuaiPanPage.this.getString(R.string.ok),
								new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog,
											int which)
									{
										try
										{
											for (int i = 0; i < mDelFiles
													.size(); i++)
											{
												FileBean tmpInfo = mDelFiles
														.get(i);
												cli.do_rm(tmpInfo.getFileName());
											}
											Toast.makeText(KuaiPanPage.this,
													"操作成功!", Toast.LENGTH_SHORT)
													.show();
										} catch (Exception e)
										{
											e.printStackTrace();
										}
										Refresh();
									}
								})
						.setNegativeButton(
								KuaiPanPage.this.getString(R.string.cancel),
								null).show();
				return;
			}
		}
		Toast.makeText(KuaiPanPage.this, "请选择需要删除的文件!", Toast.LENGTH_SHORT)
				.show();
	}

	public boolean KActivityResult(int cmd, int msg, int arg1, String arg2)
	{
		Refresh();
		return false;
	}

	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id)
	{
		ShowKuaiPanFile(KuaiPanPage.this, mFileList.get(position));
		return false;
	}

	public void Share(KuaipanFile mAppInfo)
	{

	}

	public void DownLoad(KuaipanFile mAppInfo)
	{
		File file = mAppInfo.getFile();
		if (file.exists())
		{
			mAppInfo.getFile().delete();
		}
		mAppInfo.setContext(KuaiPanPage.this);
		KSysEng.getInstance().addDLEvent(mAppInfo);
	}

	/** 长按文件或文件夹时弹出的带ListView效果的功能菜单 */
	public void ShowKuaiPanFile(final Activity context,
			final KuaipanFile mAppInfo)
	{
		P.v("PopAppDialog");
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int item)
			{// item的值就是从0开始的索引值(从列表的第一项开始)
				dialog.cancel();
				if (mAppInfo != null)
				{// 注意，所有对文件的操作必须是在该文件可读的情况下才可以，否则报错
					switch (item)
					{
					case 0:// 分享
						Share(mAppInfo);
						break;
					case 1:// 删除
						deletefiles(mAppInfo);
						break;
					case 2: // 下载
						DownLoad(mAppInfo);
						break;
					}
				} else
				{
					Toast.makeText(context, "对不起，未找到该应用!", Toast.LENGTH_SHORT)
							.show();
				}
			}
		};
		String[] mMenu =
		{ "分享", "删除", "下载" };
		Builder mAlertDialog = new AlertDialog.Builder(context);
		mAlertDialog.setTitle("请选择操作!");
		mAlertDialog.setItems(mMenu, listener);
		mAlertDialog
				.setPositiveButton(context.getString(R.string.cancel), null);
		mAlertDialog.show();
	}
}
