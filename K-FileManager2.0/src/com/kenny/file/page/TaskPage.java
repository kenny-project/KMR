package com.kenny.file.page;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.framework.interfaces.MenuAble;
import com.framework.log.P;
import com.framework.page.MultiItemPage;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.t.R;
import com.kenny.KFileManager.t.R.color;
import com.kenny.file.Adapter.TaskAdapter;
import com.kenny.file.Event.ExitEvent;
import com.kenny.file.Event.UnInstallEvent;
import com.kenny.file.bean.AppBean;
import com.kenny.file.bean.TaskBean;
import com.kenny.file.commui.ListHeaderView;
import com.kenny.file.tools.ApkTools;
import com.kenny.file.tools.PackageUtil;
import com.kenny.file.tools.T;


public class TaskPage extends MultiItemPage implements MenuAble,
		OnItemClickListener,OnItemLongClickListener
{
	public TaskPage(Activity context)
	{
		super(context);
	}

	/*
	 * 声明成员变量： mFileName ：存放显示的文件列表的名称 mFilePaths ：存放显示的文件列表的相对应的路径 mRootPath
	 * ：起始目录“/” (用java.io.File.separator获取) mSDCard ： SD卡根目录 mPath
	 * ：显示当前路径的TextView文本组件
	 */
	// private List<FileBean> mFileList = null;
	private ListView m_Appslist;
	private Button btAllKillProc;
	private TaskAdapter appAdapter;
	private ActivityManager manager;
	private ArrayList<TaskBean> appList;// 正在运行程序列表
	private PackageManager packManager;// 包名

	private View FooterView()
	{
		TextView tview = new TextView(m_act);
		tview.setHeight(100);
		tview.setWidth(-1);
		tview.setBackgroundColor(color.green);
		ListHeaderView headerView = new ListHeaderView(m_act, tview);
		return headerView;
	}

	public void onCreate()
	{
		setContentView(R.layout.taskpage);
		super.onCreate();

		packageUtil = new PackageUtil(m_act);

		m_Appslist = (ListView) findViewById(R.id.lvLocallist);
		m_Appslist.setOnItemClickListener(this);
		m_Appslist.addFooterView(FooterView(), null, false);
		m_Appslist.setOnItemLongClickListener(this);

		btAllKillProc = (Button) findViewById(R.id.btAllKillProc);
		btAllKillProc.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				AllKillProc();
			}
		});

		appList = new ArrayList<TaskBean>(); // 用来存储获取的应用信息数据
		manager = (ActivityManager) m_act
				.getSystemService(Context.ACTIVITY_SERVICE);
		packManager = m_act.getPackageManager();
		appAdapter = new TaskAdapter(m_act, 1, appList);
		m_Appslist.setAdapter(appAdapter);
	}

	public void onLoad()
	{
	}

	public void onReload()
	{
	}

	public void onExit()
	{

	}

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

	public void onPause()
	{
	}

	public void onResume()
	{
		RunningAppList();
	}

	/**
	 * 获得RunningAppList列表
	 */
	private void RunningAppList()
	{
		List<RunningAppProcessInfo> list = manager.getRunningAppProcesses();
		final ArrayList<TaskBean> Apps = new ArrayList<TaskBean>();
		for (int j = 0; j < list.size(); j++)
		{

			RunningAppProcessInfo temp = list.get(j);
			try
			{
				TaskBean taskBean = buildProgramTaskBean(0, temp.pid,
						temp.processName);
				if (taskBean != null)
				{
					Apps.add(taskBean);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				continue;
			}
		}
		appList.clear();
		appList.addAll(Apps);
		appAdapter.notifyDataSetChanged();
	}

	ProgressDialog pdTaskDialog = null;
	private PackageUtil packageUtil;

	private TaskBean buildProgramTaskBean(int type, int procId,
			String procNameString)
	{
		// 根据进程名，获取应用程序的ApplicationInfo对象
		ApplicationInfo tempAppInfo = packageUtil
				.getApplicationInfo(procNameString);
		if (tempAppInfo != null)
		{
			if (type == (tempAppInfo.flags & ApplicationInfo.FLAG_SYSTEM))// 0:用户
			{
				TaskBean programUtil;
				// 为进程加载图标和程序名称
				String appName = tempAppInfo.loadLabel(packManager).toString();
				programUtil = new TaskBean(procId, tempAppInfo.sourceDir,
						appName);
				programUtil.setProcessName(procNameString);
				// programUtil.setIcon(tempAppInfo.loadIcon(packManager));
				programUtil.setProgramName(appName);
				programUtil.setPackageName(tempAppInfo.packageName);
				// programUtil.setFilePath(tempAppInfo.sourceDir);
				programUtil.setFlags(tempAppInfo.flags);
				return programUtil;
			}
		}
		// 如果获取失败，则使用默认的图标和程序名
		// programUtil.setIcon(Res.getInstance(m_act).getDefFileIco("apk"));
		// programUtil.setProgramName(procNameString);

		// String str = processMemoryUtil.getMemInfoByPid(procId);
		// programUtil.setCpuMemString(str);
		// AppBean tmpInfo = new AppBean((String sourceDir, String
		// appName);
		return null;
	}
	/**
	 * 停止选重的进程
	 */
	private void AllKillProc()
	{
		for (int i = 0; i < appList.size(); i++)
		{
			T.KillProcessess(manager, appList.get(i).getPackageName());
		}
		// TaskList(bType, false);
		RunningAppList();
	}

	public boolean onPrepareOptionsMenu(Menu menu)
	{
		return super.onCreateOptionsMenu(menu, R.menu.taskpagemenu);
	}

	public boolean onTouchEvent(MotionEvent event)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		// TODO Auto-generated method stub
		AppBean temp = appList.get(position);
		// ApkTools.StartApk(m_act, temp.getPackageName());
		ApkTools.showAppDetails(m_act, temp.getPackageName());
	}

	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3)
	{
		AppBean temp = appList.get(arg2);
		ShowTaskDialog(m_act, temp);
		return true;
	}

	/** 长按文件或文件夹时弹出的带ListView效果的功能菜单 */
	public void ShowTaskDialog(final Activity context, final AppBean mAppInfo)
	{
		P.v("PopAppDialog");
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int item)
			{// item的值就是从0开始的索引值(从列表的第一项开始)
				dialog.cancel();
				switch (item)
				{
				case 0: // 打开
					ApkTools.StartApk(m_act, mAppInfo.getPackageName());
					break;
				case 1:
					SysEng.getInstance().addHandlerEvent(
							new UnInstallEvent(context, mAppInfo));
					break;
				}
			}
		};
		String[] mMenu =
		{ "打开", "卸载" };
		new AlertDialog.Builder(context).setTitle("请选择操作!")
				.setItems(mMenu, listener)
				.setPositiveButton(context.getString(R.string.cancel), null)
				.show();
	}

	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void clear()
	{
		// TODO Auto-generated method stub
	}
}