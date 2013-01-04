package com.kenny.file.Event;

import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import cn.kuaipan.android.sdk.net.KPCallback;
import cn.kuaipan.android.sdk.net.KPException;

import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.file.sort.FileSort;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.util.Const;
import com.kenny.file.util.NetConst;
import com.kuaipan.client.model.KuaipanFile;
import com.kuaipan.demo.SimpleCommandConsole;

/**
 * @author wmh 初始化event
 * */
public class LoadKuaiPanEvent extends AbsEvent implements KPCallback
{
   private String TAG = "LoadAppsEvent";
   private Activity m_act;
   private INotifyDataSetChanged m_INotify = null;
   // private PkgSizeObserver mpkgSizeObserver;
   private ProgressDialog myDialog = null;
   private List<KuaipanFile> beans;
   private String path;
   private SimpleCommandConsole cli;
   public LoadKuaiPanEvent(Activity act, String path, SimpleCommandConsole cli,
         boolean bShowDialog, INotifyDataSetChanged INotify)
   {
      m_act = act;
      this.cli = cli;
      m_INotify = INotify;
      this.path = path;
      if (bShowDialog)
      {
         myDialog = ProgressDialog.show(m_act, "", "正在获取数据...", true, true);
         myDialog.setCancelable(false);
         myDialog.show();
      }
   }
   
   private int cmd = 0;
   
   /**
    * flag: :1:系统 0:用户 直接读取手机内存里面的数据
    */
   public void UserAppInfo()
   {
      try
      {
         if (NetConst.isNetConnectNoMsg(m_act))
         {
	  beans = cli.do_ls(path);
	  if (beans != null)
	  {
	     Collections.sort(beans, new FileSort());
	     if (!cli.getPath().equals("/"))
	     {
	        KuaipanFile back = new KuaipanFile();
	        back.setFileName("..");
	        back.setBackUp(true);
	        beans.add(0, back);
	     }
	     cmd = Const.cmd_KuaiPan_LS;
	  }
	  else
	  {
	     cmd = Const.cmd_KuaiPan_LS_Error;
	     
	  }
         }
         else
         {
	  cmd = Const.cmd_KuaiPan_LS_Error_NoNetWork;
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         cmd = Const.cmd_KuaiPan_LS_Error;
      }
      if (m_INotify != null)
      {
         m_INotify.NotifyDataSetChanged(cmd, beans);
      }
      SysEng.getInstance().addHandlerEvent(new AbsEvent()
      {
         
         public void ok()
         {
	  if (myDialog != null)
	  {
	     myDialog.dismiss();
	  }
         }
      });
      P.debug(TAG, "LoadAppsEvent end");
   }
   
   public void ok()
   {
      UserAppInfo();
   }
   
   public void onSuccess(Object obj)
   {
      // TODO Auto-generated method stub
      
   }
   
   public void onFail(int code, KPException e, String msg)
   {
      // TODO Auto-generated method stub
      
   }
   
   public void onProgress(long bytes, long total)
   {
      // TODO Auto-generated method stub
      
   }
}
