package com.baiduyun.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
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
import cn.kuaipan.android.sdk.KPManager;
import cn.kuaipan.android.sdk.ui.KPLoginView;

import com.baidu.oauth.BaiduOAuth;
import com.baidu.oauth.BaiduOAuth.BaiduOAuthResponse;
import com.baidu.pcs.BaiduPCSActionInfo;
import com.framework.event.AbsEvent;
import com.framework.event.ParamEvent;
import com.framework.log.P;
import com.framework.syseng.KSysEng;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Event.delFileEvent;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.bean.FileBean;
import com.kenny.file.dialog.openFileSelectDialog;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.interfaces.KActivityStatus;
import com.kenny.file.tools.SaveData;
import com.kenny.file.util.Const;

public class BaiduPage extends Activity implements OnItemClickListener,
		INotifyDataSetChanged, OnClickListener, KActivityStatus,
		OnItemLongClickListener
{
	private Button btError;
	private ListView m_locallist, m_DownLoadlist;
	private BaiduFileAdapter fileAdapter;
	private BaiduCommandConsole cli;

	private ArrayList<BaiduFile> mFileList = new ArrayList<BaiduFile>();
	private ArrayList<BaiduFile> mDownLoadList = new ArrayList<BaiduFile>();
	private View rlError, pbLoading;
	private TextView mCurrentPath;
	private TextView tvError_msg, tvKuaiPanSpace;// 空间
	private int nCommCode = 0;// 0:没有错误
	private View lyTools2, lyBTools;// lyUpLoadTools,

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		cli = new BaiduCommandConsole(this);
		setContentView(R.layout.baidu_page);

		lyTools2 = findViewById(R.id.lyTools2);
		lyBTools = findViewById(R.id.lyBTools);

		m_locallist = (ListView) findViewById(R.id.lvLocallist);
		m_DownLoadlist = (ListView) findViewById(R.id.lvDownLoadlist);
		fileAdapter = new BaiduFileAdapter(this, mFileList);

		m_locallist.setOnItemClickListener(this);
		m_DownLoadlist.setOnItemClickListener(this);
//		TextView tview = new TextView(this);
//		tview.setHeight(150);
//		tview.setWidth(-1);
//		tview.setVisibility(View.VISIBLE);
//		tview.setBackgroundColor(color.green);
//		ListHeaderView headerView = new ListHeaderView(this, tview);
//		m_locallist.addFooterView(headerView, null, false);
//		m_DownLoadlist.addFooterView(headerView, null, false);
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
		init();
	}

	private void init()
	{
        mbOauth =SaveData.Read(BaiduPage.this, BaiduOauth, "");
 		if (mbOauth.length() <= 0 )
		{
			try
			{
				NotifyDataSetChanged(Const.cmd_KuaiPan_OAuth_Error, null);
				test_login();
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		} else
		{
			cli.setAuthToken(mbOauth);
			getAccountInfo();
			Refresh();
		}
	}
	  
    // the api key
    /*
     * mbApiKey should be your app_key, please instead of "your app_key"
     */
    private final static String mbApiKey = "QbY3Gk2MiH4E9IkRPt1585q5"; //your app_key";
    private static final String BaiduOauth="BaiduOauth";
    private String mbOauth = "";
    private void test_login(){

		BaiduOAuth oauthClient = new BaiduOAuth();
		oauthClient.startOAuth(this, mbApiKey, new String[]{"basic", "netdisk"}, new BaiduOAuth.OAuthListener() {
			@Override
			public void onException(String msg) {
				Toast.makeText(getApplicationContext(), "Login failed " + msg, Toast.LENGTH_SHORT).show();
				Toast.makeText(getApplicationContext(), "登录失败",
						Toast.LENGTH_SHORT).show();
			}
			@Override
			public void onComplete(BaiduOAuthResponse response) {
				if(null != response){
					mbOauth = response.getAccessToken();
					cli.setAuthToken(mbOauth);
					SaveData.Write(BaiduPage.this, BaiduOauth, mbOauth);
					Toast.makeText(getApplicationContext(), "Token: " + mbOauth + "    User name:" + response.getUserName(), Toast.LENGTH_SHORT).show();
					SysEng.getInstance().addEvent(
							new BaiduFileListEvent(BaiduPage.this, cli
									.getPath(), cli, pbLoading, BaiduPage.this));
				}
			}
			@Override
			public void onCancel() {
				Toast.makeText(getApplicationContext(), "Login cancelled", Toast.LENGTH_SHORT).show();
			}
		});
    }
	@Override
	public void onResume()
	{
		if (mbOauth!=null || mbOauth.length() > 0)
		{
			fileAdapter.notifyDataSetChanged();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}


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
//				KuaipanPublicLink mKuaiPanUser = KuaipanAPI.shares(fileName,
//						"", "");
//				if (mKuaiPanUser != null)
//				{
//					T.SendShare(KuaiPanPage.this, "共享链接", "分享:"
//							+ mKuaiPanUser.url);
//				}
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
//				KuaipanUser mKuaiPanUser = KuaipanAPI.accountInfo();
//				if (mKuaiPanUser != null)
//				{
//					String AccountInfo = T
//							.FileSizeToString(mKuaiPanUser.quota_used)
//							+ "/"
//							+ T.FileSizeToString(mKuaiPanUser.quota_total);
//					NotifyDataSetChanged(Const.cmd_KuaiPan_AccountInfo,
//							AccountInfo);
//				}
			}
		});
	}

	public boolean onTouchEvent(MotionEvent event)
	{
		return false;
	}

	private void Refresh()
	{
		if (mbOauth==null || mbOauth.length() == 0)
		{
			nCommCode = Const.cmd_KuaiPan_OAuth_Error;
			NotifyDataSetChanged(nCommCode, null);
		} else
		{
			SysEng.getInstance().addEvent(
					new BaiduFileListEvent(BaiduPage.this, cli.getPath(),
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
		if (mbOauth!=null || mbOauth.length() == 0)
		{
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
					new BaiduFileListEvent(BaiduPage.this, tempPath, cli,
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
		BaiduFile temp = mFileList.get(position);
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
							new openDefFileEvent(BaiduPage.this, temp
									.getFilePath()));
				} else
				{
					Toast.makeText(BaiduPage.this, "添加到下载队列",
							Toast.LENGTH_SHORT).show();
					temp.setContext(BaiduPage.this);
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
			case BaiduCommandConsole.BAIDU_COMMAND_DELETEFILE:
				BaiduPCSActionInfo.PCSSimplefiedResponse ret=(BaiduPCSActionInfo.PCSSimplefiedResponse)getValue();
				if(ret.errorCode==0)
				{
					Toast.makeText(BaiduPage.this,
					"删除成功!", Toast.LENGTH_SHORT)
					.show();
					Refresh();
				}
				else
				{
					Toast.makeText(BaiduPage.this,
							"删除失败："+ret.message, Toast.LENGTH_SHORT)
							.show();
				}
			break;
			case openFileSelectDialog.SelectList:
				List<FileBean> list = (List<FileBean>) getValue();
				SysEng.getInstance().addEvent(
						new UploadBaiduFileEvent(BaiduPage.this, list,cli,
								BaiduPage.this));
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
						BaiduPage.this,
						BaiduPage.this
								.getString(R.string.msg_upload_file_succeed),
						Toast.LENGTH_SHORT).show();
				break;
			case Const.cmd_KuaiPan_LS:
				mFileList.clear();
				try
				{
					mFileList.addAll((List<BaiduFile>) getValue());
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
//		case R.id.btRefresh:
//			Refresh();
//			break;
		case R.id.btNew:
			new CreateBaiduFileDialog().Show(BaiduPage.this, cli, this);
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
				Intent intent = new Intent(BaiduPage.this, KPLoginView.class);
				BaiduPage.this.startActivity(intent);
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
		MenuInflater inflater = BaiduPage.this.getMenuInflater();
		inflater.inflate(R.menu.kuaipanpagemenu, menu);
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub
		menu.clear();
		MenuInflater inflater = BaiduPage.this.getMenuInflater();
		inflater.inflate(R.menu.kuaipanpagemenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// TODO Auto-generated method stub
		menu.clear();
		MenuInflater inflater = BaiduPage.this.getMenuInflater();
		inflater.inflate(R.menu.kuaipanpagemenu, menu);
		return super.onPrepareOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.muLogout:
			mbOauth="";
			SaveData.Write(BaiduPage.this, BaiduOauth, mbOauth);
			cli.setAuthToken(mbOauth);
			
			SysEng.getInstance().addEvent(
					new delFileEvent(BaiduPage.this, new FileBean(new File(
							Const.szKuaiPanPath), null)));
			nCommCode = Const.cmd_KuaiPan_OAuth_Error;
			m_locallist.setVisibility(View.GONE);
			NotifyDataSetChanged(nCommCode, null);
			break;
		case R.id.muClearCache:
			new AlertDialog.Builder(BaiduPage.this)
					.setTitle("提示!")
					.setMessage("确定要清空缓存吗?")
					.setPositiveButton(BaiduPage.this.getString(R.string.ok),
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int which)
								{
									SysEng.getInstance()
											.addEvent(
													new delFileEvent(
															BaiduPage.this,
															new FileBean(
																	new File(
																			Const.szKuaiPanPath),
																	null)));
								}
							})
					.setNegativeButton(
							BaiduPage.this.getString(R.string.cancel), null)
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
			final ArrayList<BaiduFile> mSelFiles = new ArrayList<BaiduFile>();
			for (int i = 0; i < mFileList.size(); i++)
			{
				BaiduFile tmpInfo = mFileList.get(i);
				if (tmpInfo.isChecked())
				{
					mSelFiles.add(tmpInfo);
				}
			}
			KSysEng mKSysEng = KSysEng.getInstance();
			mDownLoadList.addAll(mSelFiles);
			if (mSelFiles.size() > 0)
			{
				try
				{
					for (int i = 0; i < mSelFiles.size(); i++)
					{
						BaiduFile tmpInfo = mSelFiles.get(i);
						// cli.do_cat(tmpInfo.getFileName());
						tmpInfo.setContext(BaiduPage.this);
						mKSysEng.addDLEvent(tmpInfo);
					}
					Toast.makeText(BaiduPage.this, "已添加到下载对队列!",
							Toast.LENGTH_SHORT).show();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				return;
			}
		}
		Toast.makeText(BaiduPage.this, "请选择需要下载的文件!", Toast.LENGTH_SHORT)
				.show();
	}

	private void deletefiles(BaiduFile mAppInfo)
	{
		List<String> list=new ArrayList<String>();
		list.add(mAppInfo.getFilePath());
		cli.do_rm(list,BaiduPage.this);
	}

	private void deletefiles()
	{
		if (mFileList.size() > 0)
		{
			final List<String> mSelFiles = new ArrayList<String>();
			for (int i = 0; i < mFileList.size(); i++)
			{
				FileBean tmpInfo = mFileList.get(i);
				if (tmpInfo.isChecked())
				{
					mSelFiles.add(tmpInfo.getFilePath());
				}
			}
			if (mSelFiles.size() > 0)
			{
				new AlertDialog.Builder(BaiduPage.this)
						.setTitle("提示!")
						.setMessage("确定删除已选的文件吗?")
						.setPositiveButton(
								BaiduPage.this.getString(R.string.ok),
								new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog,
											int which)
									{
										try
										{
//											for (int i = 0; i < mSelFiles
//													.size(); i++)
//											{
//												FileBean tmpInfo = mSelFiles
//														.get(i);
//												cli.do_rm(tmpInfo.getFileName());
//											}
											cli.do_rm(mSelFiles,BaiduPage.this);

										} catch (Exception e)
										{
											e.printStackTrace();
										}
										Refresh();
									}
								})
						.setNegativeButton(
								BaiduPage.this.getString(R.string.cancel),
								null).show();
				return;
			}
		}
		Toast.makeText(BaiduPage.this, "请选择需要删除的文件!", Toast.LENGTH_SHORT)
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
		ShowBaiduFile(BaiduPage.this, mFileList.get(position));
		return true;
	}

	public void Share(BaiduFile mAppInfo)
	{

	}

	public void DownLoad(BaiduFile mAppInfo)
	{
		File file = mAppInfo.getFile();
		if (file.exists())
		{
			mAppInfo.getFile().delete();
		}
		mAppInfo.setContext(BaiduPage.this);
		KSysEng.getInstance().addDLEvent(mAppInfo);
	}

	/** 长按文件或文件夹时弹出的带ListView效果的功能菜单 */
	public void ShowBaiduFile(final Activity context,
			final BaiduFile mAppInfo)
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
					case 0://重命名
						
						break;
					case 1:// 分享
						Share(mAppInfo);
						break;
					case 2:// 删除
						deletefiles(mAppInfo);
						break;
					case 3: // 下载
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
		{ "重命名","分享", "删除", "下载" };
		Builder mAlertDialog = new AlertDialog.Builder(context);
		mAlertDialog.setTitle("请选择操作!");
		mAlertDialog.setItems(mMenu, listener);
		mAlertDialog
				.setPositiveButton(context.getString(R.string.cancel), null);
		mAlertDialog.show();
	}
}
