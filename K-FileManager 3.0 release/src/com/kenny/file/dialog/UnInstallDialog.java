package com.kenny.file.dialog;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.framework.event.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.bean.AppBean;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.util.Const;
import com.kenny.file.util.SDFile;

//wmh 未弄完
public class UnInstallDialog extends AbsEvent
{
   private Activity m_context;
   private ProgressDialog mProgressDialog;
   private boolean mProgress = false;
   private ArrayList<AppBean> appList;
   private INotifyDataSetChanged mNotifyDataSetChanged;
   private boolean isAllHideDialog = false;//全部显示备份对话框
   private boolean bBackApp = true; //是否备份
   
   public void ShowDialog(Activity context, ArrayList<AppBean> appList,
         INotifyDataSetChanged notifyDataSetChanged)
   {
      isAllHideDialog = false;
      bBackApp = true;
      mProgress = true;
      this.appList = appList;
      mNotifyDataSetChanged = notifyDataSetChanged;
      m_context = context;
      mProgressDialog = new ProgressDialog(context);
      mProgressDialog.setTitle("正在卸载");
      mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      mProgressDialog.setMax(appList.size());
      mProgressDialog.setButton(context.getString(R.string.cancel),
	  new DialogInterface.OnClickListener()
	  {
	     public void onClick(DialogInterface dialog, int whichButton)
	     {
	        mProgress = false;
	     }
	  });
      mProgressDialog.setProgress(0);
      mProgressDialog.show();
      SysEng.getInstance().addEvent(this);

   }
   

   
   private int BackAppFileDialog(final AppBean tmpInfo)
   {
      if (!isAllHideDialog)
      {
         SysEng.getInstance().addHandlerEvent(new AbsEvent()
         {
	  public void ok()
	  {
	     AlertDialog.Builder test = new AlertDialog.Builder(m_context);
	     test.setTitle("备份提示");
	     test.setMessage("您要卸载的" + tmpInfo.getAppName() + "软件是否备份一下?");
	     CheckBox all = new CheckBox(m_context);
	     all.setOnCheckedChangeListener(new OnCheckedChangeListener()
	     {
	        
	        public void onCheckedChanged(CompoundButton buttonView,
		    boolean isChecked)
	        {
		 isAllHideDialog = isChecked;
	        }
	     });
	     all.setText("记住 选择");
	     test.setView(all);
	     test.setPositiveButton(m_context.getString(R.string.yes),
		 new OnClickListener()
		 {
		    public void onClick(DialogInterface dialog, int which)
		    {
		       bBackApp = true;
		       SysEng.getInstance().ThreadNotify();
		    }
		 });
	     
	     test.setNegativeButton(m_context.getString(R.string.no),
		 new OnClickListener()
		 {
		    public void onClick(DialogInterface dialog, int which)
		    {
		       bBackApp = false;
		       SysEng.getInstance().ThreadNotify();
		    }
		 });
	     test.create();
	     test.show();
	  }
         });
         SysEng.getInstance().ThreadWait();
      }
      if (bBackApp)
      {
         int result = SDFile.BackAppFile(tmpInfo.getFilePath(),
	     Const.szRecyclePath,
	     tmpInfo.getAppName() + tmpInfo.getVersionName());
         if (result != 1)
         {
	  Message msg = new Message();
	  msg.what = 101;
	  msg.obj = tmpInfo.getAppName();
	  myHandler.sendMessage(msg);
         }
      }
      return 1;
   }
   
   public void ok()
   {
      try
      {
         for (int i = 0; i < appList.size() && mProgress; i++)
         {
	  BackAppFileDialog(appList.get(i));
         }
 
         for (int i = 0; i < appList.size() && mProgress; i++)
         {
	  AppBean tmpInfo = appList.get(i);
	  try
	  {
	     Uri uninstallUri = Uri.fromParts("package",
		 tmpInfo.getPackageName(), null);
	     Intent intent = new Intent(Intent.ACTION_DELETE, uninstallUri);
	     m_context.startActivity(intent);
	     mProgressDialog.incrementProgressBy(1);
	  }
	  catch (Exception e)
	  {
	     e.printStackTrace();
	  }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         Message msg = new Message();
         msg.what = 103;
         msg.obj = e.getMessage();
         myHandler.sendMessage(msg);
      }
      Message msg = new Message();
      msg.what = 100;
      if (mProgress)
      {
         msg.obj = "卸载完成";
      }
      else
      {
         msg.obj = "卸载取消";
      }
      myHandler.sendMessage(msg);
   }
   
   Handler myHandler = new Handler()
   {
      public void handleMessage(Message msg)
      {
         int status = 0;
         if (msg.what == 100)// 备份完成
         {
	  mProgressDialog.dismiss();
	  // String message = msg.obj.toString();
	  // Toast.makeText(m_context, message, Toast.LENGTH_SHORT).show();
	  if (mNotifyDataSetChanged != null)
	  {
	     mNotifyDataSetChanged.NotifyDataSetChanged(msg.what, null);
	  }
	  mProgressDialog.dismiss();
	  return;
         }
         else if (msg.what == 101)// 备份失败
         {
	  status = msg.arg1;
	  String message = "备份" + status + "软件包失败";
	  Toast.makeText(m_context, message, Toast.LENGTH_SHORT).show();
	  mProgressDialog.dismiss();
         }
         else if (msg.what == 103)// 备份异常失败
         {
	  String message = "错误:" + (String) msg.obj;
	  Toast.makeText(m_context, message, Toast.LENGTH_SHORT).show();
	  mProgressDialog.dismiss();
	  if (mNotifyDataSetChanged != null)
	  {
	     mNotifyDataSetChanged.NotifyDataSetChanged(msg.what, null);
	  }
         }
      }
   };
}
