package com.kenny.file.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.framework.event.NextPageEvent;
import com.framework.interfaces.MenuAble;
import com.framework.log.P;
import com.framework.page.MultiItemPage;
import com.framework.syseng.SysEng;
import com.framework.util.Const;
import com.kenny.KFileManager.t.R;
import com.kenny.file.Adapter.NetAdapter;
import com.kenny.file.Event.ExitEvent;
import com.kenny.file.Event.delNetClientEvent;
import com.kenny.file.bean.NetClientBean;
import com.kenny.file.commui.ListHeaderView;
import com.kenny.file.dialog.FTPClientDialog;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.util.FTPClientManager;
import com.kenny.ftp.client.FTPClient;
import com.kenny.swiftp.gui.SwifFtpMain;
import com.kuaipan.client.KuaiPanPage;
import com.umeng.analytics.MobclickAgent;

public class NetworkPage extends MultiItemPage implements MenuAble,
		INotifyDataSetChanged, OnItemClickListener, OnClickListener
{
	public NetworkPage(Activity context)
	{
		super(context);
	}

	/*
	 * 声明成员变量： mFileName ：存放显示的文件列表的名称 mFilePaths ：存放显示的文件列表的相对应的路径 mRootPath
	 * ：起始目录“/” (用java.io.File.separator获取) mSDCard ： SD卡根目录 mPath
	 * ：显示当前路径的TextView文本组件
	 */
	private List<NetClientBean> mNetClientList = null;
	private LinearLayout lyTools;
	private FrameLayout flSubview;
	private NetAdapter netAdapter;
	private FTPClientManager localManage;
	// private KuaiPanPage mKuaiPanPage = null;
	private static final int KuaiPanPage = 10;
	private HashMap<String, Object> intent;
	private int nFlag = 0;// 0:group 1:kuaipanitem
	private ListView m_lvNetlist;

	public void onCreate()
	{
		setContentView(R.layout.networkpage);
		super.onCreate();
		localManage = FTPClientManager.GetHandler();
		localManage.SetContext(m_act);
		lyTools = (LinearLayout) findViewById(R.id.lyTools);
		flSubview = (FrameLayout) findViewById(R.id.flSubview);

		m_lvNetlist = (ListView) findViewById(R.id.lvNetlist);
		m_lvNetlist.setOnItemClickListener(this);

		mNetClientList = new ArrayList<NetClientBean>();
		netAdapter = new NetAdapter(m_act, mNetClientList);

		ListHeaderView headerView = new ListHeaderView(m_act,
				getView(R.layout.listitem_netserver));
		headerView.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				MobclickAgent.onEvent(m_act, "KMainPage", "FtpServer");
				Intent intent = new Intent(m_act, SwifFtpMain.class);
				m_act.startActivity(intent);
			}
		});
		m_lvNetlist.addHeaderView(headerView, null, false);
		ListHeaderView footerView = new ListHeaderView(m_act,
				getView(R.layout.listitem_add_cloud));
		footerView.setOnClickListener(new OnClickListener()
		{

			public void onClick(View v)
			{

				P.v("footerView.setOnClickListener");

//				SwitchPage(KuaiPanPage);
				Intent intent = new Intent(m_act, KuaiPanPage.class);
				m_act.startActivity(intent);
				// CoolGuang.init(m_act.getApplicationContext());
				// CoolGuang.showListAd(m_act);
			}
		});

		m_lvNetlist.addFooterView(footerView, null, false);
		m_lvNetlist.setAdapter(netAdapter);
		localManage.setNotifyData(this);

		Button btButton = (Button) findViewById(R.id.btNetCreate);
		btButton.setOnClickListener(this);
		btButton = (Button) findViewById(R.id.btNetRefresh);
		btButton.setOnClickListener(this);
		btButton = (Button) findViewById(R.id.btNetDel);
		btButton.setOnClickListener(this);
	}

//	private void SwitchPage(int nFlag)
//	{
//		switch (nFlag)
//		{
//		case KuaiPanPage:
//			MobclickAgent.onEvent(m_act, "KMainPage", "kuaipan");
//			// if (mKuaiPanPage == null)
//			// {
//			// mKuaiPanPage = new KuaiPanPage(m_act);
//			// mKuaiPanPage.onCreate();
//			// mKuaiPanPage.onResume();
//			// flSubview.addView(mKuaiPanPage);
//			// }
//			// mKuaiPanPage.setObj(viewobj);
//			flSubview.setVisibility(View.VISIBLE);
//			m_lvNetlist.setVisibility(View.GONE);
//			break;
//		case 0:
//			m_lvNetlist.setVisibility(View.VISIBLE);
//			flSubview.setVisibility(View.GONE);
//			break;
//		}
//		this.nFlag = nFlag;
//	}

	public boolean onKeyDown(int keyCode, KeyEvent msg)
	{
		// 弹出退出对话框
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			SysEng.getInstance().addHandlerEvent(new ExitEvent(m_act, true));
			return true;
		}
		return super.onKeyDown(keyCode, msg);
	}

	public boolean onCreateOptionsMenu(Menu menu)
	{
		P.debug("onCreateOptionsMenu");
		// if (nFlag != 0)
		// {
		// return mKuaiPanPage.onCreateOptionsMenu(menu);
		// } else
		{
			return super.onCreateOptionsMenu(menu, R.menu.networkpagemenu);
		}
	}

	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// if (nFlag != 0)
		// {
		// mKuaiPanPage.onPrepareOptionsMenu(menu);
		// }
		super.onPrepareOptionsMenu(menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		// if (nFlag != 0)
		// {
		// return mKuaiPanPage.onOptionsItemSelected(item);
		// } else
		{
			return super.onOptionsItemSelected(item);
		}
	}

	public void onStart()
	{
		Log.d("NullPointError", "onStart");

	}

	/** 注销广播 */

	public void onDestroy()
	{
		if (mNetClientList != null)
			mNetClientList.clear();
		if (nFlag != 0)
		{
			// if (mKuaiPanPage != null)
			// mKuaiPanPage.onDestroy();
		}
	}

	public void clear()
	{
		// TODO Auto-generated method stub
		if (nFlag != 0)
		{
			// mKuaiPanPage.clear();
		}
	}

	public boolean onTouchEvent(MotionEvent event)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void NotifyDataSetChanged(int cmd, Object value)
	{
		// TODO Auto-generated method stub
		switch (cmd)
		{
		case 1:// 更新
				// mPath.setText(localManage.getCurrentPath());
			netAdapter.notifyDataSetChanged();
			break;
		}
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		NetClientBean item = mNetClientList.get(arg2 - 1);
		P.debug("arg2:" + arg2 + "arg3:" + arg3 + "      title:"
				+ item.getTitle());

		switch (item.getType())
		{
		case 1:// 本地服务器
			SysEng.getInstance().addHandlerEvent(
					new NextPageEvent(m_act, new FTPClient(m_act, item),
							Const.SHOWANIM, null));
			break;
		case 2:// 资源服务器
			break;
		case 3:// 用户自己FTP服务器
			break;
		}
	}

	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.btNetCreate:
			new FTPClientDialog().Show(m_act, null);
			break;
		case R.id.btNetRefresh:
			break;
		case R.id.btNetDel:
			DeleteNetClient();
			break;
		}
	}

	private void DeleteNetClient()
	{
		if (mNetClientList.size() > 0)
		{
			final ArrayList<NetClientBean> mDelFiles = new ArrayList<NetClientBean>();
			for (int i = 0; i < mNetClientList.size(); i++)
			{
				NetClientBean tmpInfo = mNetClientList.get(i);
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
						.setMessage(
								m_act.getString(R.string.msg_del_select_item))
						.setPositiveButton(m_act.getString(R.string.ok),
								new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog,
											int which)
									{
										SysEng.getInstance().addEvent(
												new delNetClientEvent(m_act,
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

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoad()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReload()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onExit()
	{
		// TODO Auto-generated method stub
		
	}

}