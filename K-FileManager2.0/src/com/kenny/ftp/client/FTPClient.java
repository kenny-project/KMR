package com.kenny.ftp.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;

import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.framework.page.AbsPage;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Adapter.FileAdapter;
import com.kenny.file.bean.FileBean;
import com.kenny.file.bean.NetClientBean;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.manager.FileManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * 测试Activity.
 * 
 * @author cui_tao
 * 
 */
public class FTPClient extends AbsPage implements OnClickListener,
		INotifyDataSetChanged, OnItemClickListener
{
	private NetClientBean mNetClientBean = null;

	public FTPClient(Activity context, NetClientBean bean)
	{
		super(context);
		mNetClientBean = bean;
	}

	/**
	 * 标签.
	 */
	private static final String TAG = "FTPActivity";

	/**
	 * FTP.
	 */
	private FTP ftp;

	/**
	 * FTP文件集合.
	 */
	private List<FTPFile> remoteFile;

	/**
	 * 本地文件集合.
	 */
	private List<File> localFile;

	/**
	 * 本地根目录.
	 */
	private static final String LOCAL_PATH = "/sdcard/";

	/**
	 * 当前选中项.
	 */
	private int position = 0;
	/**
	 * ListView.
	 */
	private ListView RemoteList;

	private ListView LocalList;
	private TextView mCurrentPath;// 路径
	private RemoteAdapter remoteAdapter;

	
	public void onCreate()
	{
		setContentView(R.layout.ftpclient);
		initView(); // 初始化视图
		// InitSettingView();
	}

	private void InitSettingView()
	{
		EditText etTitle;
		EditText etFTPAddr;
		EditText etPort;
		EditText etUserName;
		EditText etPassWord;
		CheckBox cbAnonymousSetting;
		Spinner spEncode;
		final LinearLayout lyAnonymous;

		mCurrentPath = (TextView) findViewById(R.id.mCurrentPath);

		etTitle = (EditText) findViewById(R.id.etTitle);

		etFTPAddr = (EditText) findViewById(R.id.etFTPAddr);

		etPort = (EditText) findViewById(R.id.etPort);

		etUserName = (EditText) findViewById(R.id.etUserName);
		etPassWord = (EditText) findViewById(R.id.etPassWord);

		lyAnonymous = (LinearLayout) findViewById(R.id.lyAnonymous);

		cbAnonymousSetting = (CheckBox) findViewById(R.id.cbAnonymousSetting);
		cbAnonymousSetting.setChecked(false);
		cbAnonymousSetting
				.setOnCheckedChangeListener(new OnCheckedChangeListener()
				{

					
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked)
					{
						// TODO Auto-generated method stub
						if (isChecked)
						{
							lyAnonymous.setVisibility(View.GONE);
						} else
						{
							lyAnonymous.setVisibility(View.VISIBLE);
						}
					}

				});

		spEncode = (Spinner) findViewById(R.id.spEncode);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter
				.createFromResource(m_act, R.array.TextEncode,
						android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spEncode.setAdapter(adapter);
		spEncode.setSelection(2);
		spEncode.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id)
			{
				P.v("Spinner1: position=" + position + " id=" + id);
			}

			public void onNothingSelected(AdapterView<?> parent)
			{
				P.v("Spinner1: unselected");

			}
		});
		String title;
		if (mNetClientBean == null)
		{
			title = "创建";
			mNetClientBean = new NetClientBean();
		} else
		{
			title = "修改";
			etTitle.setText(mNetClientBean.getTitle());
			etFTPAddr.setText(mNetClientBean.getHost());
			etPort.setText(mNetClientBean.getPort());
			etUserName.setText(mNetClientBean.getUserName());
			etPassWord.setText(mNetClientBean.getPassWord());
			cbAnonymousSetting.setChecked(mNetClientBean.isAnonymous());
			spEncode.setSelection(mNetClientBean.getEncode());
		}
	}

	// 
	public void onStop()
	{
		// 关闭服务
		try
		{
			ftp.closeConnect();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private Button btLocal, btNetWork, btLog, btSetting;
	private RelativeLayout Localpannal, Remotepannal, Logpannal, Settingpannal;

	/**
	 * 初始化视图.
	 */
	private void initView()
	{
		localManage = FileManager.getInstance();

		mCurrentPath = (TextView) findViewById(R.id.mCurrentPath);
		// 初始化控件
		RemoteList = (ListView) findViewById(R.id.lvremotelist);
		LocalList = (ListView) findViewById(R.id.lvLocalList);

		btLocal = (Button) findViewById(R.id.btLocal);
		btNetWork = (Button) findViewById(R.id.btNetWork);
		btLog = (Button) findViewById(R.id.btLog);
		btSetting = (Button) findViewById(R.id.btSetting);
		btLocal.setOnClickListener(this);
		btNetWork.setOnClickListener(this);
		btLog.setOnClickListener(this);
		btSetting.setOnClickListener(this);

		Localpannal = (RelativeLayout) findViewById(R.id.Localpannal);
		Remotepannal = (RelativeLayout) findViewById(R.id.Remotepannal);
		Logpannal = (RelativeLayout) findViewById(R.id.Logpannal);
		Settingpannal = (RelativeLayout) findViewById(R.id.Settingpannal);

		Button btUploading = (Button) findViewById(R.id.btUploading);
		Button btContent = (Button) findViewById(R.id.btContent);
		Button btDownLoad = (Button) findViewById(R.id.btDownLoad);
		Button btClose = (Button) findViewById(R.id.btClose);
		// ListView单击
		LocalList.setOnItemClickListener(this);
		RemoteList.setOnItemClickListener(new OnItemClickListener()
		{
			
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				FTPFile ftpFile = remoteFile.get(position);
				if (ftpFile.isDirectory())
				{
					System.out.println(ftpFile.getName());
					loadRemoteView(ftpFile.getName());
				} else
				{
					Toast.makeText(m_act, remoteFile.get(position).getName(),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		// 切换到FTP
		btContent.setOnClickListener(buttonChangeRemoteClick);
		// // 下载
		btDownLoad.setOnClickListener(buttonDownloadClick);
		// 上传
		btUploading.setOnClickListener(buttonUploadingClick);
		// // 断开FTP服务
		btClose.setOnClickListener(buttonCloseClick);
		SwitchPage(R.id.btLocal);
		loadLocalView();

		remoteFile = new ArrayList<FTPFile>();
		// FTP列表适配器
		remoteAdapter = new RemoteAdapter(m_act, remoteFile);
		// 加载数据到ListView
		RemoteList.setAdapter(remoteAdapter);
	}

	public void SwitchPage(int flag)
	{
		btLocal.setBackgroundResource(R.drawable.tab_normal);
		btLocal.setTextColor(m_act.getResources().getColor(
				R.color.tab_TextColor_normal));

		btNetWork.setBackgroundResource(R.drawable.tab_normal);
		btNetWork.setTextColor(m_act.getResources().getColor(
				R.color.tab_TextColor_normal));

		btLog.setBackgroundResource(R.drawable.tab_normal);
		btLog.setTextColor(m_act.getResources().getColor(
				R.color.tab_TextColor_normal));

		btSetting.setBackgroundResource(R.drawable.tab_normal);
		btSetting.setTextColor(m_act.getResources().getColor(
				R.color.tab_TextColor_normal));
		Localpannal.setVisibility(View.GONE);
		Remotepannal.setVisibility(View.GONE);
		Logpannal.setVisibility(View.GONE);
		Settingpannal.setVisibility(View.GONE);
		;
		Button btTemp = btLocal;
		switch (flag)
		{
		case R.id.btLocal:
			btTemp = btLocal;
			Localpannal.setVisibility(View.VISIBLE);
			break;
		case R.id.btNetWork:
			btTemp = btNetWork;
			Remotepannal.setVisibility(View.VISIBLE);
			break;
		case R.id.btLog:
			btTemp = btLog;
			Logpannal.setVisibility(View.VISIBLE);
			break;
		case R.id.btSetting:
			btTemp = btSetting;
			Settingpannal.setVisibility(View.VISIBLE);
			break;
		}
		btTemp.setBackgroundResource(R.drawable.tab_select);
		btTemp.setTextColor(m_act.getResources().getColor(
				R.color.tab_TextColor_selected));
	}

	
	public void onClick(View v)
	{

		switch (v.getId())
		{
		case R.id.btLocal:
			SwitchPage(v.getId());
			break;
		case R.id.btNetWork:
			SwitchPage(v.getId());
			break;
		case R.id.btLog:
			SwitchPage(v.getId());
			break;
		case R.id.btSetting:
			SwitchPage(v.getId());
			break;
		}
	}

	/**
	 * 加载FTP视图.
	 */
	private void loadRemoteView()
	{

		final ProgressDialog myDialog = ProgressDialog.show(m_act, "",
				"正在连接...", true, true);
		myDialog.show();
		SysEng.getInstance().addEvent(new AbsEvent()
		{

			
			public void ok()
			{
				try
				{
					if (ftp != null)
					{
						// 关闭FTP服务
						ftp.closeConnect();
					}
					ftp = new FTP(mNetClientBean.getHost(), mNetClientBean
							.getUserName(), mNetClientBean.getPassWord());
					// 打开FTP服务

					ftp.openConnect();
					if (ftp.changeWorkingDirectory(FTP.REMOTE_PATH))
					{
						remoteFile.clear();
						// 加载FTP列表
						remoteFile.addAll(ftp.listFiles());
					}
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
				SysEng.getInstance().addHandlerEvent(new AbsEvent()
				{

					
					public void ok()
					{
						// TODO Auto-generated method stub
						myDialog.dismiss();
						remoteAdapter.notifyDataSetChanged();
					}
				});
			}
		});

	}

	/**
	 * 加载FTP视图.
	 */
	private void loadRemoteView(final String path)
	{
		final ProgressDialog myDialog = ProgressDialog.show(m_act, "",
				"正在获取数据...", true, true);
		myDialog.show();
		SysEng.getInstance().addEvent(new AbsEvent()
		{

			
			public void ok()
			{
				try
				{
					// 加载FTP列表
					if (ftp.changeWorkingDirectory(path))
					{
						remoteFile.clear();
						// 加载FTP列表
						remoteFile.addAll(ftp.listFiles());
					} else
					{
						P.v(path);
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}
				SysEng.getInstance().addHandlerEvent(new AbsEvent()
				{
					
					public void ok()
					{
						myDialog.dismiss();
						remoteAdapter.notifyDataSetChanged();
					}
				});
			}
		});
	}

	private FileManager localManage;
	private FileAdapter fileAdapter;
	private List<FileBean> mFileList = null;

	private void loadLocalView()
	{
		localManage.setNotifyData(this);
		mCurrentPath.setText(localManage.getCurrentPath());
		mFileList = localManage.getFileList();
		fileAdapter = new FileAdapter(m_act,1, mFileList,null);
		LocalList.setAdapter(fileAdapter);
	}

	/**
	 * 加载本地列表.
	 * 
	 * @param filePath
	 *            文件目录
	 */
	private List<File> getFileDir(String filePath)
	{
		localFile = new ArrayList<File>();
		// 获取根目录
		File f = new File(filePath);
		// 获取根目录下所有文件
		File[] files = f.listFiles();
		localFile.clear();
		// 循环添加到本地列表
		for (int i = 0; i < files.length; i++)
		{
			File file = files[i];
			if (file.isHidden())
			{
				continue;
			}
			localFile.add(file);
		}
		return localFile;
	}

	/**
	 * 切换到FTP.
	 */
	private OnClickListener buttonChangeRemoteClick = new OnClickListener()
	{

		
		public void onClick(View v)
		{
			loadRemoteView();
			// 加载FTP视图
		}
	};

	/**
	 * 下载.
	 */
	private OnClickListener buttonDownloadClick = new OnClickListener()
	{
		
		public void onClick(View v)
		{
			Result result = null;
			try
			{
				// 下载
				result = ftp.download(FTP.REMOTE_PATH, remoteFile.get(position)
						.getName(), LOCAL_PATH);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			if (result.isSucceed())
			{
				Log.e(TAG, "download ok...time:" + result.getTime()
						+ " and size:" + result.getResponse());
				Toast.makeText(m_act, "下载成功", Toast.LENGTH_SHORT).show();
			} else
			{
				Log.e(TAG, "download fail");
				Toast.makeText(m_act, "下载失败", Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * 上传.
	 */
	private OnClickListener buttonUploadingClick = new OnClickListener()
	{

		
		public void onClick(View v)
		{
			Result result = null;
			try
			{
				// 上传
				result = ftp
						.uploading(localFile.get(position), FTP.REMOTE_PATH);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			if (result.isSucceed())
			{
				Log.e(TAG, "uploading ok...time:" + result.getTime()
						+ " and size:" + result.getResponse());
				Toast.makeText(m_act, "上传成功", Toast.LENGTH_SHORT).show();
			} else
			{
				Log.e(TAG, "uploading fail");
				Toast.makeText(m_act, "上传失败", Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * 断开服务.
	 */
	private OnClickListener buttonCloseClick = new OnClickListener()
	{

		
		public void onClick(View v)
		{
			try
			{
				// 关闭FTP服务
				if (ftp != null)
					ftp.closeConnect();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			finish();
		}
	};

	
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
		// TODO Auto-generated method stub
		fileAdapter.notifyDataSetChanged();
	}

	
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		// TODO Auto-generated method stub
		
	}

	
	public void onResume()
	{
		// TODO Auto-generated method stub
		
	}

	
	public void onPause()
	{
		// TODO Auto-generated method stub
		
	}

	
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		
	}

}