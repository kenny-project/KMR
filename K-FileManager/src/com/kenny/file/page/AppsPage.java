package com.kenny.file.page;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.interfaces.MenuAble;
import com.framework.log.P;
import com.framework.page.MultiItemPage;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.KFileManager.R.color;
import com.kenny.file.Adapter.AppAdapter;
import com.kenny.file.Adapter.AppFileAdapter;
import com.kenny.file.Event.ExitEvent;
import com.kenny.file.Event.InstallEvent;
import com.kenny.file.Event.LoadAppsDBEvent;
import com.kenny.file.Event.delFileEvent;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.bean.AppBean;
import com.kenny.file.bean.AppGroupBean;
import com.kenny.file.bean.FileBean;
import com.kenny.file.commui.ListHeaderView;
import com.kenny.file.dialog.AppBackUpDialog;
import com.kenny.file.dialog.PopAppDialog;
import com.kenny.file.dialog.UnInstallDialog;
import com.kenny.file.sort.FileSort;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.tools.ApkTools;
import com.kenny.file.util.Const;
import com.kenny.file.util.Theme;

public class AppsPage extends MultiItemPage implements MenuAble,
      OnItemClickListener, OnClickListener, OnItemLongClickListener,
      INotifyDataSetChanged, OnChildClickListener
{
   public AppsPage(Activity context)
   {
      super(context);
   }
   
   /*
    * 声明成员变量： mFileName ：存放显示的文件列表的名称 mFilePaths ：存放显示的文件列表的相对应的路径 mRootPath
    * ：起始目录“/” (用java.io.File.separator获取) mSDCard ： SD卡根目录 mPath
    * ：显示当前路径的TextView文本组件
    */
   private ListView m_Appslist;
   // private GridView m_AppsGrid;
   private View lyBTools;
   private ExpandableListView m_AppFilelist;
   private AppAdapter appAdapter;
   private AppFileAdapter appFileAdapter;
   private ArrayList<AppBean> mAppFileList = new ArrayList<AppBean>();
   private ArrayList<AppGroupBean> mFileList = new ArrayList<AppGroupBean>();
   private ArrayList<AppBean> mAppList = new ArrayList<AppBean>();
   private ArrayList<AppBean> mUserAppList = new ArrayList<AppBean>();
   private ArrayList<AppBean> mSysAppList = new ArrayList<AppBean>();
   private final int UserAppFlag = 0; // 用户应用列表内容:
   private final int SystemAppFlag = 1; // 系统应用列表内容:
   private final int BackUpAppFlag = 2; // 系统应用备份内容:
   // private final int RecycleAppFlag = 3; // 系统应用备份内容:
   private int AFlag = UserAppFlag; // 列表内容:
   private Button btSelectAll, btSelectVisible, btUnInstall, btBackUp,
         btDelete, btInstall;
   private Button btUserApp, btSystemApp, btTools2BackUp;
   
   private boolean bCheckSelect = false;
   
   // private Dao m_Dao;
   
   private View FooterView()
   {
      TextView tview = new TextView(m_act);
      tview.setHeight(100);
      tview.setWidth(-1);
      tview.setBackgroundColor(color.green);
      ListHeaderView headerView = new ListHeaderView(m_act, tview);
      return headerView;
   }
   
   private OnScrollListener m_AppOnScrollListener = new OnScrollListener()
   {
      
      public void onScrollStateChanged(AbsListView view, int scrollState)
      {
         switch (scrollState)
         {
         case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
	  P.debug("SCROLL_STATE_FLING");
	  if (appAdapter != null) appAdapter.setShowLogo(false);
	  break;
         case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
	  P.debug("SCROLL_STATE_IDLE");
	  if (appAdapter != null)
	  {
	     appAdapter.setShowLogo(true);
	     appAdapter.notifyDataSetChanged();
	  }
	  break;
         case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
	  P.debug("SCROLL_STATE_TOUCH_SCROLL");
	  if (appAdapter != null) appAdapter.setShowLogo(false);
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
   private OnScrollListener m_AppFileOnScrollListener = new OnScrollListener()
   {
      
      public void onScrollStateChanged(AbsListView view, int scrollState)
      {
         switch (scrollState)
         {
         case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
	  P.debug("SCROLL_STATE_FLING");
	  if (appAdapter != null) appAdapter.setShowLogo(false);
	  break;
         case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
	  P.debug("SCROLL_STATE_IDLE");
	  if (appAdapter != null)
	  {
	     appAdapter.setShowLogo(true);
	     appAdapter.notifyDataSetChanged();
	  }
	  break;
         case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
	  P.debug("SCROLL_STATE_TOUCH_SCROLL");
	  if (appAdapter != null) appAdapter.setShowLogo(false);
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
   
   public void onCreate()
   {
      setContentView(R.layout.appspage);
      super.onCreate();
      lyBTools = (View) findViewById(R.id.lyBTools);
      // 应用
      m_Appslist = (ListView) findViewById(R.id.lvLocallist);
      m_Appslist.setOnScrollListener(m_AppOnScrollListener);
      m_Appslist.setOnItemClickListener(this);
      m_Appslist.addFooterView(FooterView(), null, false);
      m_Appslist.setOnItemLongClickListener(this);
      appAdapter = new AppAdapter(m_act, 1, mAppList);
      m_Appslist.setAdapter(appAdapter);
      // 文件
      m_AppFilelist = (ExpandableListView) findViewById(R.id.elvAppFilelist);
      m_AppFilelist.setOnScrollListener(m_AppFileOnScrollListener);
      m_AppFilelist
	  .setOnCreateContextMenuListener(new OnCreateContextMenuListener()
	  {
	     public void onCreateContextMenu(ContextMenu menu, View v,
	           ContextMenuInfo menuInfo)
	     {
	        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
	        // int type =
	        // ExpandableListView.getPackedPositionType(info.packedPosition);
	        int group = ExpandableListView
		    .getPackedPositionGroup(info.packedPosition);
	        int child = ExpandableListView
		    .getPackedPositionChild(info.packedPosition);
	        if (child >= 0 && group >= 0)
	        {
		 // P.v("type="+type+",group="+group+",child="+child);
		 AppBean temp = (AppBean) appFileAdapter.getChild(group,
		       child);
		 PopAppDialog.ShowFile(m_act, temp, AppsPage.this);
	        }
	        // menu.clear();
	        // menu.setHeaderTitle("Sample menu");
	        // menu.add(0, 0, 0, "这是一个测试");
	        // PopAppDialog.ShowFile(m_act, temp, this);
	     }
	     // public boolean onContextItemSelected(MenuItem item)
	     // {
	     // ExpandableListContextMenuInfo info =
	     // (ExpandableListContextMenuInfo) item
	     // .getMenuInfo();
	     // String title = "你长按了";
	     // int type = ExpandableListView
	     // .getPackedPositionType(info.packedPosition);
	     // // setTitle("点击了长按菜单里面的第"+item.getItemId()+"个项目")
	     // if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD)
	     // {
	     // int groupPos = ExpandableListView
	     // .getPackedPositionGroup(info.packedPosition);
	     // int childPos = ExpandableListView
	     // .getPackedPositionChild(info.packedPosition);
	     // Toast.makeText(
	     // m_act,
	     // title + ": Child " + childPos + " clicked in group "
	     // + groupPos, Toast.LENGTH_SHORT).show();
	     // return true;
	     // }
	     // else if (type ==
	     // ExpandableListView.PACKED_POSITION_TYPE_GROUP)
	     // {
	     // int groupPos = ExpandableListView
	     // .getPackedPositionGroup(info.packedPosition);
	     // Toast.makeText(m_act,
	     // title + ": Group " + groupPos + " clicked",
	     // Toast.LENGTH_SHORT).show();
	     // return true;
	     // }
	     //
	     // return false;
	     // }
	     
	  });
      // m_AppFilelist.setOnItemClickListener(this);
      m_AppFilelist.addFooterView(FooterView(), null, false);
      // m_AppFilelist.setOnItemLongClickListener(this);
      m_AppFilelist.setOnChildClickListener(this);
      appFileAdapter = new AppFileAdapter(m_act, 1, mFileList);
      m_AppFilelist.setAdapter(appFileAdapter);
      // m_AppFilelist.setOnGroupClickListener(new OnGroupClickListener()
      // {
      //
      // public boolean onGroupClick(ExpandableListView parent, View v,
      // int groupPosition, long id)
      // {
      // return true;
      // }
      // });
      
      btUserApp = (Button) findViewById(R.id.btUserApp);
      btUserApp.setOnClickListener(this);
      
      btSystemApp = (Button) findViewById(R.id.btSystemApp);
      btSystemApp.setOnClickListener(this);
      
      btTools2BackUp = (Button) findViewById(R.id.btTools2BackUp);
      btTools2BackUp.setOnClickListener(this);
      
      btSelectVisible = (Button) findViewById(R.id.btSelectVisible);
      btSelectVisible.setOnClickListener(this);
      
      btSelectAll = (Button) findViewById(R.id.btSelectAll);
      btSelectAll.setOnClickListener(this);
      
      btUnInstall = (Button) findViewById(R.id.btUnInstall);
      btUnInstall.setOnClickListener(this);
      
      btBackUp = (Button) findViewById(R.id.btBackUp);
      btBackUp.setOnClickListener(this);
      
      btDelete = (Button) findViewById(R.id.btDelete);
      btDelete.setOnClickListener(this);
      
      btInstall = (Button) findViewById(R.id.btInstall);
      btInstall.setOnClickListener(this);
      
   }
   
   /**
    * 切换窗体视图 false:ListView true:GridView
    */
   // private void SwitchStyle(boolean bStyle)
   // {
   // // if (bStyle != this.bStyle)
   // // {
   // // Theme.setStyleType(bStyle);
   // // Theme.Save(m_act);
   // // }
   // // if (bStyle)
   // // {
   // // appAdapter = new AppAdapter(m_act, 2, mAppList);
   // // m_AppsGrid.setAdapter(appAdapter);
   // // m_Appslist.setVisibility(View.GONE);
   // // m_AppsGrid.setVisibility(View.VISIBLE);
   // // btSelectVisible.setVisibility(View.VISIBLE);
   // // btSelectAll.setVisibility(View.GONE);
   // // }
   // // else
   // // {
   // appAdapter = new AppAdapter(m_act, 1, mAppList);
   // m_Appslist.setAdapter(appAdapter);
   // //m_AppsGrid.setVisibility(View.GONE);
   // btSelectVisible.setVisibility(View.GONE);
   // m_Appslist.setVisibility(View.VISIBLE);
   // btSelectAll.setVisibility(View.VISIBLE);
   // //}
   // this.bStyle = bStyle;
   // }
   BroadcastReceiver packageReceiver = new BroadcastReceiver()
   {
      public void onReceive(Context ctx, Intent intent)
      {
         Log.v("wmh", "----------APACKAGE_ADDED----start");
         mUserAppList.clear();
         appList(AFlag);
         // SysEng.getInstance().addHandlerEvent(new AbsEvent()
         // {
         // @Override
         // public void ok()
         // {
         // mUserAppList.clear();
         // appList(AFlag);
         // }
         // }, 100);
      }
   };
   
   public void registerReceiver()
   {
      IntentFilter filter = new IntentFilter("com.kenny.package.change"); // 和广播中Intent的action对应
      m_act.registerReceiver(packageReceiver, filter);// 注册监听函数
   }
   
   public void onResume()
   {
      // mUserAppList.clear();
      // appList(AFlag);
      // this.onLoad();
      // SwitchStyle(Theme.isStyleType());
      if (Theme.getToolsVisible())
      {
         lyBTools.setVisibility(View.VISIBLE);
      }
      else
      {
         lyBTools.setVisibility(View.GONE);
      }
      
   }
   
   public void onPause()
   {
      
   }
   
   /** 注销广播 */
   
   public void onDestroy()
   {
      P.v("wmh", "App:onDestroy()");
      this.m_act.unregisterReceiver(this.packageReceiver);
   }
   
   public void clear()
   {
      P.v("wmh", "App:onClear()");
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
   
   public void onLoad()
   {
      registerReceiver();
      appList(AFlag);
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
      P.v("wmh", "App:onExit()");
   }
   
   public void onClick(View v)
   {
      switch (v.getId())
      {
      case R.id.btUserApp:
         // MobclickAgent.onEvent(m_act, "AppEvent","UserApp");
         appList(UserAppFlag);
         break;
      case R.id.btSystemApp:
         // MobclickAgent.onEvent(m_act, "AppEvent","SystemApp");
         appList(SystemAppFlag);
         break;
      case R.id.btTools2BackUp:
         // MobclickAgent.onEvent(m_act, "AppEvent","BackUpApp");
         appList(BackUpAppFlag);
         break;
      case R.id.btBackUp:
         // MobclickAgent.onEvent(m_act, "AppEvent","btBackUp");
         AppBackUp();
         break;
      case R.id.btInstall:
         InstallApp();
         // MobclickAgent.onEvent(m_act, "AppEvent","btInstall");
         break;
      case R.id.btDelete:
         deletefiles();
         // MobclickAgent.onEvent(m_act, "AppEvent","btDel");
         break;
      case R.id.btUnInstall:
         AllUnInstall();
         // MobclickAgent.onEvent(m_act, "AppEvent","btUnInstall");
         break;
      case R.id.btSelectAll:
         SelectAll();
         // MobclickAgent.onEvent(m_act, "AppEvent","btSelectAll");
         break;
      case R.id.btSelectVisible:
         Selected(appAdapter.isSelected());
         // MobclickAgent.onEvent(m_act, "AppEvent","btSelectVisible");
         break;
      }
   }
   
   private void InstallApp()
   {
      final ArrayList<FileBean> mDelFiles = new ArrayList<FileBean>();
      if (AFlag == this.BackUpAppFlag)
      {
         for (int i = 0; i < appFileAdapter.getCount(); i++)
         {
	  ArrayList<AppBean> TempList = ((AppGroupBean) appFileAdapter
	        .getItem(i)).getItems();
	  for (int j = 0; j < TempList.size(); j++)
	  {
	     AppBean tmpInfo = TempList.get(j);
	     if (tmpInfo.isChecked())
	     {
	        mDelFiles.add(tmpInfo);
	     }
	  }
         }
      }
      else if (mAppList.size() > 0)
      {
         
         for (int i = 0; i < mAppList.size(); i++)
         {
	  FileBean tmpInfo = mAppList.get(i);
	  if (tmpInfo.isChecked())
	  {
	     mDelFiles.add(tmpInfo);
	  }
         }
      }
      
      if (mDelFiles.size() > 0)
      {
         SysEng.getInstance().addEvent(
	     new InstallEvent(m_act, mDelFiles, AppsPage.this));
         return;
      }
      Toast.makeText(m_act,
	  m_act.getString(R.string.msg_Please_select_instal_file) + "!",
	  Toast.LENGTH_SHORT).show();
   }
   
   private void deletefiles()
   {
      final ArrayList<FileBean> mDelFiles = new ArrayList<FileBean>();
      if (AFlag == this.BackUpAppFlag)
      {
         for (int i = 0; i < appFileAdapter.getCount(); i++)
         {
	  ArrayList<AppBean> TempList = ((AppGroupBean) appFileAdapter
	        .getItem(i)).getItems();
	  for (int j = 0; j < TempList.size(); j++)
	  {
	     AppBean tmpInfo = TempList.get(j);
	     if (tmpInfo.isChecked())
	     {
	        mDelFiles.add(tmpInfo);
	     }
	  }
         }
      }
      else if (mAppList.size() > 0)
      {
         
         for (int i = 0; i < mAppList.size(); i++)
         {
	  FileBean tmpInfo = mAppList.get(i);
	  if (tmpInfo.isChecked())
	  {
	     mDelFiles.add(tmpInfo);
	  }
         }
      }
      if (mDelFiles.size() > 0)
      {
         new AlertDialog.Builder(m_act)
	     .setTitle(m_act.getString(R.string.msg_dialog_info_title) + "!")
	     .setMessage(m_act.getString(R.string.msg_delselectfile))
	     .setPositiveButton(m_act.getString(R.string.ok),
	           new DialogInterface.OnClickListener()
	           {
		    public void onClick(DialogInterface dialog, int which)
		    {
		       SysEng.getInstance().addEvent(
		             new delFileEvent(m_act, mDelFiles,
		                   AppsPage.this));
		    }
	           })
	     .setNegativeButton(m_act.getString(R.string.cancel), null)
	     .show();
         
         return;
      }
      
      Toast.makeText(m_act,
	  m_act.getString(R.string.msg_please_del_operate_file),
	  Toast.LENGTH_SHORT).show();
   }
   
   private void Selected(boolean nFlag)
   {
      appAdapter.setSelected(!nFlag);
      appAdapter.notifyDataSetChanged();
      if (!nFlag)
      {
         this.btSelectVisible.setText(R.string.cancel);
         // btSelectAll.setVisibility(View.VISIBLE);
         // btBackUp.setVisibility(View.VISIBLE);
         // btUnInstall.setVisibility(View.VISIBLE);
         // btAPFlag.setVisibility(View.GONE);
      }
      else
      {
         this.btSelectVisible.setText(R.string.btSelect);
         // btSelectAll.setVisibility(View.GONE);
         // btBackUp.setVisibility(View.GONE);
         // btUnInstall.setVisibility(View.GONE);
         // btAPFlag.setVisibility(View.VISIBLE);
      }
   }
   
   private void appList(final int flag)
   {
      List<AppBean> tempList = null;
      this.AFlag = flag;
      if (flag == UserAppFlag)
      {
         btInstall.setVisibility(View.GONE);
         btDelete.setVisibility(View.GONE);
         btUnInstall.setVisibility(View.VISIBLE);
         btBackUp.setVisibility(View.VISIBLE);
         P.debug("appList start");
         if (mUserAppList.size() == 0)
         {
	  SysEng.getInstance().addEvent(
	        new LoadAppsDBEvent(m_act, UserAppFlag, mUserAppList, true,
		    this));
         }
         else
         {
	  tempList = mUserAppList;
         }
         P.debug("appList end");
         btUserApp.setBackgroundResource(R.drawable.tab2_left_select);
         btSystemApp.setBackgroundResource(R.drawable.tab2_mid_unselect);
         btTools2BackUp.setBackgroundResource(R.drawable.tab2_mid_unselect);
         btTools2BackUp.setBackgroundResource(R.drawable.tab2_right_unselect);
         btSystemApp.setTextColor(m_act.getResources().getColor(
	     R.color.tab_TextColor_selected));
         btUserApp.setTextColor(m_act.getResources().getColor(
	     R.color.tab_TextColor_normal));
         btTools2BackUp.setTextColor(m_act.getResources().getColor(
	     R.color.tab_TextColor_selected));
         btTools2BackUp.setTextColor(m_act.getResources().getColor(
	     R.color.tab_TextColor_selected));
         m_AppFilelist.setVisibility(View.GONE);
         m_Appslist.setVisibility(View.VISIBLE);
      }
      else if (flag == SystemAppFlag)
      {
         P.debug("appList start");
         btDelete.setVisibility(View.GONE);
         btInstall.setVisibility(View.GONE);
         btUnInstall.setVisibility(View.GONE);
         btBackUp.setVisibility(View.VISIBLE);
         if (mSysAppList.size() == 0)
         {
	  SysEng.getInstance().addEvent(
	        new LoadAppsDBEvent(m_act, SystemAppFlag, mSysAppList, true,
		    this));
         }
         else
         {
	  tempList = mSysAppList;
         }
         P.debug("appList end");
         btUserApp.setBackgroundResource(R.drawable.tab2_left_unselect);
         btSystemApp.setBackgroundResource(R.drawable.tab2_mid_select);
         btTools2BackUp.setBackgroundResource(R.drawable.tab2_mid_unselect);
         btTools2BackUp.setBackgroundResource(R.drawable.tab2_right_unselect);
         
         btUserApp.setTextColor(m_act.getResources().getColor(
	     R.color.tab_TextColor_selected));
         btSystemApp.setTextColor(m_act.getResources().getColor(
	     R.color.tab_TextColor_normal));
         btTools2BackUp.setTextColor(m_act.getResources().getColor(
	     R.color.tab_TextColor_selected));
         btTools2BackUp.setTextColor(m_act.getResources().getColor(
	     R.color.tab_TextColor_selected));
         
         m_AppFilelist.setVisibility(View.GONE);
         m_Appslist.setVisibility(View.VISIBLE);
      }
      else if (flag == BackUpAppFlag)
      {
         BackUpAppList();
         btDelete.setVisibility(View.VISIBLE);
         btInstall.setVisibility(View.VISIBLE);
         btUnInstall.setVisibility(View.GONE);
         btBackUp.setVisibility(View.GONE);
         
         btUserApp.setBackgroundResource(R.drawable.tab2_left_unselect);
         btSystemApp.setBackgroundResource(R.drawable.tab2_mid_unselect);
         btTools2BackUp.setBackgroundResource(R.drawable.tab2_right_select);
         
         btUserApp.setTextColor(m_act.getResources().getColor(
	     R.color.tab_TextColor_selected));
         btSystemApp.setTextColor(m_act.getResources().getColor(
	     R.color.tab_TextColor_selected));
         btTools2BackUp.setTextColor(m_act.getResources().getColor(
	     R.color.tab_TextColor_normal));
         appFileAdapter.setShowLogo(true);
         appFileAdapter.notifyDataSetChanged();
         
         m_Appslist.setVisibility(View.GONE);
         m_AppFilelist.setVisibility(View.VISIBLE);
         return;
      }
      if (tempList != null)
      {
         mAppList.clear();
         mAppList.addAll(tempList);
         Collections.sort(mAppList, new FileSort());
         appAdapter.setShowLogo(true);
         appAdapter.notifyDataSetChanged();
      }
   }
   
   /**
    * 备份列表
    * 
    * @return
    */
   public void BackUpAppList()
   {
      mFileList.clear();
      AppGroupBean groupBean = new AppGroupBean();
      groupBean.setTitle(m_act.getString(R.string.msg_backups_file));
      groupBean.setPath(Const.szBackupPath);
      mFileList.add(groupBean);
      mAppFileList.clear();
      groupBean = new AppGroupBean();
      groupBean.setTitle(m_act.getString(R.string.msg_deleted_files));
      groupBean.setPath(Const.szRecyclePath);
      mFileList.add(groupBean);
      for (int i = 0; i < mFileList.size(); i++)
      {
         groupBean = mFileList.get(i);
         File mFile = new File(groupBean.getPath());
         File[] mFiles = mFile.listFiles();// 遍历出该文件夹路径下的所有文件/文件夹
         groupBean.Clear();
         if (mFiles != null)
         {
	  /* 将所有文件信息添加到集合中 */
	  for (File mCurrentFile : mFiles)
	  {
	     String FilePath = mCurrentFile.getPath();
	     String fileEnds = FilePath.substring(
		 FilePath.lastIndexOf(".") + 1, FilePath.length())
		 .toLowerCase();// 取出文件后缀名并转成小写
	     if (fileEnds.equals("apk"))
	     {
	        AppBean bean = new AppBean(mCurrentFile.getAbsolutePath(),
		    mCurrentFile.getName());
	        groupBean.AddBean(bean);
	     }
	  }
	  Collections.sort(groupBean.getItems(), new FileSort());
	  mAppFileList.addAll(groupBean.getItems());
         }
      }
      return;
   }
   
   /**
    * 备份已选择的应用
    */
   private void AppBackUp()
   {
      if (mAppList.size() > 0)
      {
         ArrayList<AppBean> mAppFiles = new ArrayList<AppBean>();
         for (int i = 0; i < mAppList.size(); i++)
         {
	  AppBean tmpInfo = mAppList.get(i);
	  if (tmpInfo.isChecked())
	  {
	     mAppFiles.add(tmpInfo);
	  }
         }
         if (mAppFiles.size() > 0)
         {
	  new AppBackUpDialog().ShowDialog(m_act, mAppFiles);
	  return;
         }
      }
      Toast.makeText(m_act,
	  m_act.getString(R.string.msg_Please_select_backup_program) + "!",
	  Toast.LENGTH_SHORT).show();
   }
   
   private void AllUnInstall()
   {
      if (mAppList.size() > 0)
      {
         ArrayList<AppBean> mAppFiles = new ArrayList<AppBean>();
         for (int i = 0; i < mAppList.size(); i++)
         {
	  AppBean tmpInfo = mAppList.get(i);
	  if (tmpInfo.isChecked())
	  {
	     mAppFiles.add(tmpInfo);
	  }
         }
         if (mAppFiles.size() > 0)
         {
	  new UnInstallDialog().ShowDialog(m_act, mAppFiles, this);
	  return;
         }
      }
      Toast.makeText(
	  m_act,
	  m_act.getString(R.string.msg_Please_select_uninstall_program) + "!",
	  Toast.LENGTH_SHORT).show();
   }
   
   private void SelectAll()
   {
      if (AFlag == this.BackUpAppFlag)
      {
         bCheckSelect = !bCheckSelect;
         for (int i = 0; i < appFileAdapter.getCount(); i++)
         {
	  ArrayList<AppBean> TempList = ((AppGroupBean) appFileAdapter
	        .getItem(i)).getItems();
	  for (int j = 0; j < TempList.size(); j++)
	  {
	     AppBean tmpInfo = TempList.get(j);
	     tmpInfo.setChecked(bCheckSelect);
	  }
         }
         appFileAdapter.notifyDataSetChanged();
      }
      else
      {
         if (mAppList.size() > 0)
         {
	  boolean bCheck = !mAppList.get(0).isChecked();
	  for (int i = 0; i < mAppList.size(); i++)
	  {
	     AppBean tmpInfo = mAppList.get(i);
	     tmpInfo.setChecked(bCheck);
	  }
	  appAdapter.notifyDataSetChanged();
         }
      }
      
   }
   
   public boolean onCreateOptionsMenu(Menu menu)
   {
      P.debug("onCreateOptionsMenu");
      return super.onCreateOptionsMenu(menu, R.menu.apppagemenu);
   }
   
   public boolean onPrepareOptionsMenu(Menu menu)
   {
      super.onPrepareOptionsMenu(menu);
      return true;
   }
   
   public boolean onTouchEvent(MotionEvent event)
   {
      // TODO Auto-generated method stub
      return false;
   }
   
   public void onItemClick(AdapterView<?> parent, View view, int position,
         long id)
   {
      if (mAppList.size() > position)
      {
         AppBean temp = mAppList.get(position);
         switch (AFlag)
         {
         case UserAppFlag:
         case SystemAppFlag:
	  ApkTools.StartApk(m_act, temp.getPackageName());
	  break;
         // ApkTools.showAppDetails(m_act, temp.getPackageName());
         // ApkTools.showAppDetails(m_act, temp.getPackageName());
         // break;
         case BackUpAppFlag:
	  SysEng.getInstance().addHandlerEvent(
	        new openDefFileEvent(m_act, temp.getFilePath()));
	  break;
         }
      }
      else
      {
         Toast.makeText(m_act, m_act.getString(R.string.msg_file_is_not_found),
	     Toast.LENGTH_LONG).show();
      }
   }
   
   public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
         long arg3)
   {
      // MobclickAgent.onEvent(m_act, "AppEvent","ItemLongClick");
      AppBean temp = mAppList.get(arg2);
      switch (AFlag)
      {
      case UserAppFlag:
         temp = mAppList.get(arg2);
         PopAppDialog.ShowApp(m_act, temp);
         break;
      case SystemAppFlag:
         temp = mAppList.get(arg2);
         PopAppDialog.ShowApp(m_act, temp);
         break;
      default:
         return false;
         // case BackUpAppFlag:
         // temp = mAppFileList.get(arg2);
         // // temp=(AppBean)appFileAdapter.getItem(arg2);
         // PopAppDialog.ShowFile(m_act, temp, this);
         // break;
      }
      return true;
   }
   
   public void NotifyDataSetChanged(int cmd, Object value)
   {
      if (value != null)
      {
         mAppList.clear();
         mAppList.addAll((List<AppBean>) value);
         Collections.sort(mAppList, new FileSort());
         appAdapter.setShowLogo(true);
         appAdapter.notifyDataSetChanged();
      }
      else
      {
         mUserAppList.clear();
         appList(AFlag);
      }
   }
   
   public boolean onChildClick(ExpandableListView parent, View v,
         int groupPosition, int childPosition, long id)
   {
      AppBean temp = (AppBean) appFileAdapter.getChild(groupPosition,
	  childPosition);
      SysEng.getInstance().addHandlerEvent(
	  new openDefFileEvent(m_act, temp.getFilePath()));
      return false;
   }
}
