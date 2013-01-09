package com.kenny.file.page;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.event.AbsEvent;
import com.framework.interfaces.MenuAble;
import com.framework.log.P;
import com.framework.page.MultiItemPage;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.KFileManager.R.color;
import com.kenny.file.Adapter.TaskAdapter;
import com.kenny.file.Event.ExitEvent;
import com.kenny.file.Event.UnInstallEvent;
import com.kenny.file.bean.AppBean;
import com.kenny.file.bean.TaskBean;
import com.kenny.file.commui.ListHeaderView;
import com.kenny.file.dialog.AppBackUpDialog;
import com.kenny.file.menu.PopAppDialog;
import com.kenny.file.tools.ApkTools;
import com.kenny.file.tools.PackageUtil;
import com.kenny.file.tools.T;
import com.kenny.file.util.Theme;
import android.view.*;
import android.view.View.*;

//import com.umeng.analytics.MobclickAgent;

public class TaskPage extends MultiItemPage implements MenuAble,
      OnItemClickListener, OnClickListener, OnItemLongClickListener      
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
   private GridView m_AppsGrid;
   private Button btKillProc, btAllKillProc, btSelectAll, btUnInstall,
         btSelectVisible, btBackUp, btRefresh;
   private TaskAdapter appAdapter;
   private ActivityManager manager;
   private ArrayList<TaskBean> appList;// 正在运行程序列表
   private PackageManager packManager;// 包名
   // private boolean bStyle = false;// false:listView true:gridView
   private int bType = 0;// 0:user 未零:sys
   private Button btUserApp, btSystemApp;
   private int nUserAppCount = 0;
   private int nSysAppCount = 0;
   private View lyBTools;
   
   private View FooterView()
   {
      TextView tview = new TextView(m_act);
      tview.setHeight(100);
      tview.setWidth(-1);
      tview.setBackgroundColor(color.green);
      ListHeaderView headerView = new ListHeaderView(m_act, tview);
      return headerView;
   }
   
   /**
    * 切换窗体视图 false:ListView true:GridView
    */
   private void SwitchStyle()// boolean bStyle)
   {
      btUserApp.setText(m_act.getString(R.string.btUserAppTitle) + "("
	  + nUserAppCount + ")");
      btSystemApp.setText(m_act.getString(R.string.btSystemAppTitle) + "("
	  + nSysAppCount + ")");
      if (bType == 0)
      { // 用户
         btUserApp.setBackgroundResource(R.drawable.tab2_left_select);
         btSystemApp.setBackgroundResource(R.drawable.tab2_right_unselect);
         
         // btUserApp.setBackgroundResource(R.drawable.tab2_left_select);
         // btSystemApp.setBackgroundResource(R.drawable.tab2_mid_unselect);
         btSystemApp.setTextColor(m_act.getResources().getColor(
	     R.color.tab_TextColor_selected));
         btUserApp.setTextColor(m_act.getResources().getColor(
	     R.color.tab_TextColor_normal));
      }
      else
      {
         // 系统
         btUserApp.setBackgroundResource(R.drawable.tab2_left_unselect);
         btSystemApp.setBackgroundResource(R.drawable.tab2_right_select);
         // btUserApp.setBackgroundResource(R.drawable.tab2_mid_unselect);
         // btSystemApp.setBackgroundResource(R.drawable.tab2_right_select);
         
         btUserApp.setTextColor(m_act.getResources().getColor(
	     R.color.tab_TextColor_selected));
         btSystemApp.setTextColor(m_act.getResources().getColor(
	     R.color.tab_TextColor_normal));
      }
      // this.bStyle = bStyle;
   }
   
   public void onCreate()
   {
      setContentView(R.layout.taskpage);
      super.onCreate();
      lyBTools = (View) findViewById(R.id.lyBTools);
      
      packageUtil = new PackageUtil(m_act);
      
      m_Appslist = (ListView) findViewById(R.id.lvLocallist);
      m_Appslist.setOnItemClickListener(this);
      m_Appslist.addFooterView(FooterView(), null, false);
      m_Appslist.setOnItemLongClickListener(this);
      
      m_AppsGrid = (GridView) findViewById(R.id.gvLocallist);
      m_AppsGrid.setOnItemClickListener(this);
      m_AppsGrid.setOnItemLongClickListener(this);
      
      btUserApp = (Button) findViewById(R.id.btUserApp);
      btUserApp.setOnClickListener(this);
      
      btSystemApp = (Button) findViewById(R.id.btSystemApp);
      btSystemApp.setOnClickListener(this);
      
      btSelectVisible = (Button) findViewById(R.id.btSelectVisible);
      btSelectVisible.setOnClickListener(this);
      
      btKillProc = (Button) findViewById(R.id.btKillProc);
      btKillProc.setOnClickListener(this);
      
      btAllKillProc = (Button) findViewById(R.id.btAllKillProc);
      btAllKillProc.setOnClickListener(this);
      
      btRefresh = (Button) findViewById(R.id.btRefresh);
      btRefresh.setOnClickListener(this);
      
      btSelectAll = (Button) findViewById(R.id.btSelectAll);
      btSelectAll.setOnClickListener(this);
      
      btUnInstall = (Button) findViewById(R.id.btUnInstall);
      btUnInstall.setOnClickListener(this);
      
      btBackUp = (Button) findViewById(R.id.btBackUp);
      btBackUp.setOnClickListener(this);
      
      appList = new ArrayList<TaskBean>(); // 用来存储获取的应用信息数据
      manager = (ActivityManager) m_act
	  .getSystemService(Context.ACTIVITY_SERVICE);
      packManager = m_act.getPackageManager();
      appAdapter = new TaskAdapter(m_act, 1, appList);
      m_Appslist.setAdapter(appAdapter);
      SwitchStyle();
   }
   
   public void onLoad()
   {
      // TODO Auto-generated method stub
      // TaskList(bType, false);
   }
   
   public void onReload()
   {
      // if (bStyle != Theme.isStyleType())
      // {
      // SwitchStyle(Theme.isStyleType());
      // }
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
      TaskList(bType, false);
      if (Theme.getToolsVisible())
      {
         lyBTools.setVisibility(View.VISIBLE);
      }
      else
      {
         lyBTools.setVisibility(View.GONE);
      }
   }
   
   // /**
   // * 获得TaskList列表
   // */
   // private void TaskList()
   // {
   //
   // List<RunningAppProcessInfo> list = manager.getRunningAppProcesses();
   // final ArrayList<AppBean> Apps = new ArrayList<AppBean>();
   // for (int j = 0; j < list.size(); j++)
   // {
   // RunningAppProcessInfo temp = list.get(j);
   // try
   // {
   // PackageInfo packageInfo = packManager.getPackageInfo(
   // temp.processName, PackageManager.GET_ACTIVITIES);
   // int tFlags = packageInfo.applicationInfo.flags
   // & ApplicationInfo.FLAG_SYSTEM;
   // if (tFlags == 0)// 0:用户
   // {
   // AppBean tmpInfo = new AppBean(
   // packageInfo.applicationInfo.sourceDir,
   // packageInfo.applicationInfo.loadLabel(packManager)
   // .toString());
   // tmpInfo.setPackageName(packageInfo.packageName);
   // tmpInfo.setVersionName(packageInfo.versionName);
   // tmpInfo.setVersionCode(packageInfo.versionCode);
   // tmpInfo.setFilePath(packageInfo.applicationInfo.sourceDir);
   // tmpInfo.setFlags(packageInfo.applicationInfo.flags);
   // Apps.add(tmpInfo);
   // }
   // }
   // catch (Exception e)
   // {
   // e.printStackTrace();
   // continue;
   // }
   // }
   // appList.clear();
   // appList.addAll(Apps);
   // appAdapter.notifyDataSetChanged();
   // }
   ProgressDialog pdTaskDialog = null;
   private PackageUtil packageUtil;
   
   /**
    * type:0:用户 1:系统 获得TaskList列表
    */
   private void TaskList(final int type, final boolean bShowDialog)
   {
      P.v("TaskList(final int type, final boolean bShowDialog)");
      if (bShowDialog)
      {
         pdTaskDialog = ProgressDialog.show(m_act, "",
	     m_act.getString(R.string.msg_loading_data), true, true);
         pdTaskDialog.show();
      }
      else
      {
         pdTaskDialog = null;
      }
      SysEng.getInstance().addThreadEvent(new AbsEvent()
      {
         
         private TaskBean buildProgramTaskBean(int procId, String procNameString)
         {
	  // 根据进程名，获取应用程序的ApplicationInfo对象
	  ApplicationInfo tempAppInfo = packageUtil
	        .getApplicationInfo(procNameString);
	  TaskBean programUtil;
	  int tFlags = tempAppInfo.flags & ApplicationInfo.FLAG_SYSTEM;
	  if (tFlags == 0)// 0:用户
	  {
	     nUserAppCount++;
	  }
	  else
	  {
	     nSysAppCount++;
	  }
	  if (tFlags == type && tempAppInfo != null)// 0:用户
	  {
	     // 为进程加载图标和程序名称
	     String appName = tempAppInfo.loadLabel(packManager).toString();
	     programUtil = new TaskBean(procId, tempAppInfo.sourceDir,
		 appName);
	     programUtil.setProcessName(procNameString);
	     // programUtil.setIcon(tempAppInfo.loadIcon(packManager));
	     programUtil.setProgramName(appName);
	  }
	  else
	  {
	     // 如果获取失败，则使用默认的图标和程序名
	     // programUtil.setIcon(Res.getInstance(m_act).getDefFileIco("apk"));
	     // programUtil.setProgramName(procNameString);
	     return null;
	  }
	  // String str = processMemoryUtil.getMemInfoByPid(procId);
	  // programUtil.setCpuMemString(str);
	  // AppBean tmpInfo = new AppBean((String sourceDir, String appName);
	  programUtil.setPackageName(tempAppInfo.packageName);
	  // programUtil.setFilePath(tempAppInfo.sourceDir);
	  programUtil.setFlags(tempAppInfo.flags);
	  return programUtil;
         }
         
         public void ok()
         {
	  nUserAppCount = 0;
	  nSysAppCount = 0;
	  List<RunningAppProcessInfo> list = manager.getRunningAppProcesses();
	  final ArrayList<TaskBean> Apps = new ArrayList<TaskBean>();
	  
	  for (int j = 0; j < list.size(); j++)
	  {
	     
	     try
	     {
	        RunningAppProcessInfo procInfo = list.get(j);
	        TaskBean taskBean = buildProgramTaskBean(procInfo.pid,
		    procInfo.processName);
	        if (taskBean != null)
	        {
		 Apps.add(taskBean);
	        }
	        // PackageInfo packageInfo = packManager.getPackageInfo(
	        // temp.processName, 0);
	        //
	        // int tFlags = packageInfo.applicationInfo.flags
	        // & ApplicationInfo.FLAG_SYSTEM;
	        // if (tFlags == 0)// 0:用户
	        // {
	        // nUserAppCount++;
	        // }
	        // else
	        // {
	        // nSysAppCount++;
	        // }
	        // if (tFlags == type)// 0:用户
	        // {
	        // AppBean tmpInfo = new AppBean(
	        // packageInfo.applicationInfo.sourceDir,
	        // packageInfo.applicationInfo.loadLabel(
	        // m_act.getPackageManager()).toString());
	        // tmpInfo.setPackageName(packageInfo.packageName);
	        // tmpInfo.setVersionName(packageInfo.versionName);
	        // tmpInfo.setVersionCode(packageInfo.versionCode);
	        // tmpInfo.setFilePath(packageInfo.applicationInfo.sourceDir);
	        // tmpInfo.setFlags(packageInfo.applicationInfo.flags);
	        // Apps.add(tmpInfo);
	        // }
	        
	     }
	     catch (Exception e)
	     {
	        // e.printStackTrace();
	        continue;
	     }
	  }
	  SysEng.getInstance().addHandlerEvent(new AbsEvent()
	  {
	     
	     public void ok()
	     {
	        appList.clear();
	        appList.addAll(Apps);
	        appAdapter.notifyDataSetChanged();
	        SwitchStyle();
	        // String avail = "可用" + MemoryStatus.getAvailMemory(m_act)
	        // + "M ";
	        // String total= "总共"+MemoryStatus.getTotalMemory()+"M";
	        // mPath.setText(m_act.getString(R.string.TaskPage_Title)
	        // + avail);
	        if (bShowDialog && pdTaskDialog != null)
	        {
		 pdTaskDialog.dismiss();
	        }
	     }
	  });
         }
      });
   }
   
   public void onClick(View v)
   {
      // TODO Auto-generated method stub
      switch (v.getId())
      {
      case R.id.btUserApp:
         bType = 0;
         TaskList(bType, true);
         // MobclickAgent.onEvent(m_act, "TaskEvent","btUserApp");
         break;
      case R.id.btSystemApp:
         bType = 1;
         TaskList(bType, true);
         // MobclickAgent.onEvent(m_act, "TaskEvent","btSystemApp");
         break;
      case R.id.btRefresh:
         TaskList(bType, true);
         // MobclickAgent.onEvent(m_act, "TaskEvent","btRefresh");
         break;
      case R.id.btAllKillProc:
         AllKillProc();
         // MobclickAgent.onEvent(m_act, "TaskEvent","btAllKillProc");
         break;
      case R.id.btKillProc:
         SelectKillProc();
         // MobclickAgent.onEvent(m_act, "TaskEvent","btKillProc");
         break;
      case R.id.btBackUp:
         // AppBackUp();
         break;
      case R.id.btUnInstall:
         // AllUnInstall();
         break;
      case R.id.btSelectAll:
         SelectAll();
         break;
      case R.id.btSelectVisible:
         // Selected(appAdapter.isSelected());
         break;
      }
   }
   
   // private void Selected(boolean nFlag)
   // {
   // appAdapter.setSelected(!nFlag);
   // appAdapter.notifyDataSetChanged();
   // if (!nFlag)
   // {
   // this.btSelectVisible.setText(m_act.getString(R.string.cancel));
   // // btSelectAll.setVisibility(View.VISIBLE);
   // // btBackUp.setVisibility(View.VISIBLE);
   // // btUnInstall.setVisibility(View.VISIBLE);
   // // btAPFlag.setVisibility(View.GONE);
   // }
   // else
   // {
   // this.btSelectVisible.setText("选择");
   // // btSelectAll.setVisibility(View.GONE);
   // // btBackUp.setVisibility(View.GONE);
   // // btUnInstall.setVisibility(View.GONE);
   // // btAPFlag.setVisibility(View.VISIBLE);
   // }
   // }
   
   private void SelectAll()
   {
      if (appList.size() > 0)
      {
         boolean check = !appList.get(0).isChecked();
         for (int i = 0; i < appList.size(); i++)
         {
	  appList.get(i).setChecked(check);
         }
         appAdapter.notifyDataSetChanged();
      }
   }
   
   /**
    * 停止选重的进程
    */
   private void SelectKillProc()
   {
      int count = 0;
      for (int i = 0; i < appList.size(); i++)
      {
         AppBean tmpInfo = appList.get(i);
         if (tmpInfo.isChecked())
         {
	  manager.restartPackage(appList.get(i).getPackageName());
	  count++;
	  // manager.killBackgroundProcesses("cn.com.android123.cwj");
	  // //
	  // API Level至少为8才能使用
         }
      }
      if (count > 0)
      {
         TaskList(bType, false);
      }
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
      TaskList(bType, false);
   }
   
   public boolean onPrepareOptionsMenu(Menu menu)
   {
      return super.onCreateOptionsMenu(menu, R.menu.taskpagemenu);
   }
   
   public void onStart()
   {
      P.v("NullPointError", "onStart");
   }
   
   /** 注销广播 */
   
   public void onDestroy()
   {
      
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
   
   public void onItemClick(AdapterView<?> parent, View view, int position,
         long id)
   {
      // TODO Auto-generated method stub
      AppBean temp = appList.get(position);
      ApkTools.StartApk(m_act, temp.getPackageName());
      //ApkTools.showAppDetails(m_act, temp.getPackageName());
   }
   
   public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
         long arg3)
   {
      AppBean  temp = appList.get(arg2);
      ShowTaskDialog(m_act,temp);
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
	  case 0: // 刷新
	     TaskList(bType, true);
	     break;
	  case 1:
	        SysEng.getInstance().addHandlerEvent(
		    new UnInstallEvent(context, mAppInfo));
	     break;
	  case 2:
	     ApkTools.showAppDetails(context, mAppInfo.getPackageName());
	     break;
	  case 3: // 全部停止
	     AllKillProc();
	     break;
	  }
         }
      };
      String[] mMenu =
      { "刷新","卸载","属性", "全部停止" };
      new AlertDialog.Builder(context).setTitle("请选择操作!")
	  .setItems(mMenu, listener)
	  .setPositiveButton(context.getString(R.string.cancel), null).show();
   }
}