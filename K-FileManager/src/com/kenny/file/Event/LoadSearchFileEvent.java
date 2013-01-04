package com.kenny.file.Event;

import java.io.File;
import java.util.Collections;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.event.AbsEvent;
import com.framework.event.ParamEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.bean.FileBean;
import com.kenny.file.bean.ScarchParam;
import com.kenny.file.sort.FileSort;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.util.Const;

/**
 * @author aimery 初始化event
 * */
public class LoadSearchFileEvent extends AbsEvent implements OnCancelListener
{
   private Context act;
   private INotifyDataSetChanged m_NotifyDataSetChanged = null;
   private boolean isShow;
   private boolean mProgress = false;
   private AlertDialog mProgressDialog = null;
   private TextView tvMessage;
   private ScarchParam param;
   private char[] SearchValue;
   
   public LoadSearchFileEvent(Context act, boolean isShow, ScarchParam param,
         INotifyDataSetChanged notifyDataSetChanged)
   {
      this.act = act;
      this.param = param;
      SearchValue = param.getSearchValue().toCharArray();
      this.isShow = isShow;
      m_NotifyDataSetChanged = notifyDataSetChanged;
      mProgress = true;
   }
   
   public void Cancel()
   {
      mProgress = false;
   }
   
   private void ShowDialog()
   {
      mProgress = true;
      LayoutInflater factory = LayoutInflater.from(act);
      final View view = factory.inflate(R.layout.alert_dialog_load_sdcard_file,
	  null);
      Builder mBuilder = new AlertDialog.Builder(act);
      mBuilder.setTitle("扫描文件");
      mBuilder.setView(view);
      tvMessage = (TextView) view.findViewById(R.id.tvMessage);
      mBuilder.setNegativeButton(act.getString(R.string.cancel),
	  new DialogInterface.OnClickListener()
	  {
	     
	     public void onClick(DialogInterface dialog, int which)
	     {
	        mProgress = false;
	     }
	  });
      mProgressDialog = mBuilder.create();
      mProgressDialog.show();
   }
   
   private int nSendState = 10000;
   
   private void SendMessage(int cmd, Object value)
   {
      if (cmd == Const.cmd_LoadSDFile_State && nSendState < 200)
      {
         nSendState++;
      }
      else
      {
         if (cmd == Const.cmd_LoadSDFile_State)
         {
	  nSendState = 0;
         }
         if (m_NotifyDataSetChanged != null)
         {
	     m_NotifyDataSetChanged.NotifyDataSetChanged(cmd, value);
         }
         if (isShow)
         {
	  mNotifyData.setKey(cmd);
	  mNotifyData.setValue(value);
	  SysEng.getInstance().addHandlerEvent(mNotifyData);
         }
      }
   }
   
   private ParamEvent mNotifyData = new ParamEvent()
   {
      
      public void ok()
      {
         P.v("LoadSearchFile getKey()=" + getKey());
         switch (getKey())
         {
         case Const.cmd_LoadSDFile_Error:
	  break;
         case Const.cmd_LoadSDFile_Start:
	  ShowDialog();
	  break;
         case Const.cmd_LoadSDFile_State:
	  String value = (String) getValue();
	  tvMessage.setText(value);
	  break;
         case Const.cmd_LoadSDFile_Finish:
	  if (mProgressDialog != null) mProgressDialog.dismiss();
	  Toast.makeText(act, act.getString(R.string.msg_Scan_Finish),
	        Toast.LENGTH_SHORT).show();
	  break;
         }
      }
   };
   
   
   public void ok()
   {
      P.debug("LoadSDFileEvent:start");
      SendMessage(Const.cmd_LoadSDFile_Start, null);
      param.getSearchItems().clear();
      refreshSDCardList(param.getPath());
      Collections.sort(param.getSearchItems(), new FileSort());
      SendMessage(Const.cmd_LoadSDFile_Finish, null);
      P.debug("LoadSDFileEvent:end");
   }
   
   private void refreshSDCardList(String strPath)
   {
      File dir = new File(strPath);
      File[] files = dir.listFiles();
      
      if (files == null) return;
      
      for (File file : files)
      {
         if (!mProgress) return;
         if (!param.isHide())
         {
	  if (file.isHidden()) continue;
         }
         if (file.isDirectory() && param.isSubdirectory())
         {
	  refreshSDCardList(file.getAbsolutePath());
         }
         SendMessage(Const.cmd_LoadSDFile_State, file.getAbsolutePath());
         if (compare(file.getName()))
         {
	  FileBean mFileBean=new FileBean(file, file.getName());
	  if (file.isDirectory())
	  {
	     String[] temp = file.list();
	     if (temp != null)
	     {
	        mFileBean.setItemCount(temp.length);
	     }
	  }
	  param.getSearchItems().add(mFileBean);
         }
      }
   }
   
   public boolean compare(String fileName)
   {
      char[] src = SearchValue;
      char[] des = fileName.toCharArray();
      int length = des.length;
      if (src.length > des.length) { return false; }
      int pos = 0;
      for (int i = 0; i < length; i++)
      {
         if (src[pos] == des[i])
         {
	  pos++;
	  if (src.length > pos)
	  {
	     continue;
	  }
	  else
	  {
	     return true;
	  }
         }
         else
         {
	  pos = 0;
         }
      }
      return false;
   }
   
   
   public void onCancel(DialogInterface dialog)
   {
      // TODO Auto-generated method stub
      
   }
}
