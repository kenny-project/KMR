package com.kenny.file.Event;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import com.kenny.file.bean.FGroupInfo;
import com.kenny.file.bean.FavorBean;
import com.kenny.file.bean.FileEnd;
import com.kenny.file.db.Dao;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.sort.FileEndSort;
import com.kenny.file.tools.SaveData;
import com.kenny.file.tools.T;
import com.kenny.file.util.Const;

/**
 * @author wangminghui 初始化event
 * */
public class LoadSDFileEvent extends AbsEvent implements OnCancelListener
{
   
   private Activity act;
   private Dao dao;
   private INotifyDataSetChanged m_NotifyDataSetChanged = null;
   private boolean isShow;
   private boolean mProgress = false;
   private ArrayList<FGroupInfo> mFGroupInfo;
   private AlertDialog mProgressDialog = null;
   private ArrayList<FileEnd> listItem = new ArrayList<FileEnd>();
   private TextView tvMessage;
   private LoadSDFile_State mLFstate = new LoadSDFile_State();
   
   public LoadSDFileEvent(Activity act, boolean isShow,
         ArrayList<FGroupInfo> mFGroupInfo,
         INotifyDataSetChanged notifyDataSetChanged)
   {
      this.mFGroupInfo = mFGroupInfo;
      this.act = act;
      this.isShow = isShow;
      m_NotifyDataSetChanged = notifyDataSetChanged;
      mProgress = true;
   }
   
   private void Init()
   {
      listItem.clear();
      for (int i = 0; i < mFGroupInfo.size(); i++)
      {
         FGroupInfo info = mFGroupInfo.get(i);
         String[] ends = info.getEnds().split("\\|");
         for (String end : ends)
         {
	  if (end.length() > 1)
	  {
	     listItem.add(new FileEnd(end.toLowerCase(), info.getId(),info.getMinSize()));
	  }
         }
      }
      Collections.sort(listItem, new FileEndSort());
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
   
   private int nSendState = 0;
   
   private void SendMessage(int cmd, Object value)
   {
      if (m_NotifyDataSetChanged != null)
      {
         if (nSendState > 10 && cmd == Const.cmd_LoadSDFile_State)
         {
	  nSendState++;
         }
         else
         {
	  m_NotifyDataSetChanged.NotifyDataSetChanged(cmd, value);
	  nSendState = 0;
         }
      }
      if (isShow)
      {
         mNotifyData.setKey(cmd);
         mNotifyData.setValue(value);
         SysEng.getInstance().addHandlerEvent(mNotifyData);
      }
   }
   
   private ParamEvent mNotifyData = new ParamEvent()
   {
      
      public void ok()
      {
         P.v("LoadSDFileEvent getKey()=" + getKey());
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
      Init();
      mLFstate.count = T.FileCount(Const.getSDCard()).intValue();
      SendMessage(Const.cmd_LoadSDFile_Init, mLFstate.count);
      dao = Dao.getInstance(act);
      dao.deleteFavoritesAll();
      P.v("refreshSDCardList:start");
      refreshSDCardList(Const.getSDCard());
      P.v("refreshSDCardList:end");
      dao.closeDb();
      // //记录相应大小
      // for (int i = 1; i < values.length; i++)
      // {
      // if(values[i]>0)
      // dao.InsertFavorites(i, strPath, values[i]);
      // }
      
      // StringBuilder strBuilder = new StringBuilder();
      // strBuilder.append("<items>");
      // for (int i = 0; i < mFGroupInfo.size(); i++)
      // {
      // FGroupInfo temp = mFGroupInfo.get(i);
      // temp.setCount(dao.FavoritesCount(String.valueOf(i)));
      // strBuilder.append(temp);
      // }
      // strBuilder.append("</items>");
      // SDFile.WriteRAMFile(act, strBuilder.toString(),
      // act.getString(R.string.FavoriteType));
      SaveData.Write(act, "FavoriteInit", true);
      SendMessage(Const.cmd_LoadSDFile_Finish, null);
      P.debug("LoadSDFileEvent:end");
   }
   
   public class LoadSDFile_State
   {
      public String strPath;
      public int count;
      public int Progress;
   }
   
   private void refreshSDCardList(String strPath)
   {
      File dir = new File(strPath);
      File[] files = dir.listFiles();
      ArrayList<FavorBean> favorItems = new ArrayList<FavorBean>();
      if (files == null) return;
     
      for (File file : files)
      {
         if (!mProgress) return;
         if (file.getName().charAt(0) == '.') continue;
         if (file.isDirectory())
         {
	  refreshSDCardList(file.getAbsolutePath());
         }
         else
         {
	  mLFstate.Progress++;
	  String strFileName = file.getName();
	  String fileEnds = strFileName.substring(
	        strFileName.lastIndexOf(".") + 1).toLowerCase();// 取出文件后缀名并转成小写
	  FileEnd result = null;
	  if (fileEnds == null || fileEnds.length() < 1)
	  {
	     continue;
	  }
	  try
	  {
	     result = BinarySearch(listItem, fileEnds);
	  }
	  catch (Exception e)
	  {
	     e.printStackTrace();
	  }
	  if (result !=null&&result.MinSize<file.length())
	  {
	     favorItems.add(new FavorBean(file.getName(), result.flag, file
		 .getAbsolutePath(), file.length()));
	  }
	     mLFstate.strPath = file.getAbsolutePath();
	     SendMessage(Const.cmd_LoadSDFile_State, mLFstate);
         }
      }
      dao.InsertFavorites(favorItems);
      
   }
   
   // 二分法查找查找完全匹配的数据
   private FileEnd  BinarySearch(List<FileEnd> strList, String strWord)
   {
      int nIndex = 0;
      int nStart = 0;
      int nEnd = strList.size();
      while (nStart <= nEnd)
      {
         if (nIndex == strList.size() - 2)
         {
	  nIndex = strList.size() - 1;
	  break;
         }
         nIndex = (nStart + nEnd) / 2;
         int nCompare = strWord.compareTo(strList.get(nIndex).key);
         if (nCompare == 0)
         {
	  
	  return strList.get(nIndex);//.flag;
         }
         else if (nCompare < 0)
         {
	  nEnd = nIndex - 1;
         }
         else if (nCompare > 0)
         {
	  nStart = nIndex + 1;
         }
      }
      return null;
   }
   
   
   public void onCancel(DialogInterface dialog)
   {
      // TODO Auto-generated method stub
      
   }
}
