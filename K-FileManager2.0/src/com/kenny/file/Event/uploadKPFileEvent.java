package com.kenny.file.Event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.framework.event.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.t.R;
import com.kenny.file.bean.FileBean;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.tools.T;
import com.kenny.file.util.Const;
import com.kuaipan.client.KuaipanAPI;
import com.kuaipan.client.exception.KuaipanAuthExpiredException;
import com.kuaipan.client.exception.KuaipanIOException;
import com.kuaipan.client.exception.KuaipanServerException;
import com.kuaipan.demo.SimpleCommandConsole;

/**
 * @author kenny 初始化event
 * */
public class uploadKPFileEvent extends AbsEvent
{
   private List<FileBean> list;
   private Context m_context;
   private ProgressDialog mProgressDialog;
   private boolean mProgress = false;
   private INotifyDataSetChanged notifySetChanged = null;
   private SimpleCommandConsole cli;
   
   public uploadKPFileEvent(Activity context, List<FileBean> list,
         INotifyDataSetChanged notifySetChanged)
   {
      cli = SimpleCommandConsole.getHandler(context);
      this.list = list;
      this.notifySetChanged = notifySetChanged;
      this.m_context = context;
      ShowDialog(list);
   }
   
   public uploadKPFileEvent(Activity context, FileBean fileBean,
         INotifyDataSetChanged notifySetChanged)
   {
      cli = SimpleCommandConsole.getHandler(context);
      this.list = new ArrayList<FileBean>();
      list.add(fileBean);
      this.m_context = context;
      this.notifySetChanged = notifySetChanged;
      ShowDialog(list);
   }
   
   public uploadKPFileEvent(Activity context, List<FileBean> list)
   {
      this.list = list;
      this.m_context = context;
      ShowDialog(list);
      cli = SimpleCommandConsole.getHandler(context);
   }
   
   public uploadKPFileEvent(Activity context, FileBean fileBean)
   {
      cli = SimpleCommandConsole.getHandler(context);
      this.list = new ArrayList<FileBean>();
      list.add(fileBean);
      this.m_context = context;
      ShowDialog(list);
   }
   
   public uploadKPFileEvent(Activity context, FileBean fileBean,
         boolean isShowDialog)
   {
      cli = SimpleCommandConsole.getHandler(context);
      this.list = new ArrayList<FileBean>();
      list.add(fileBean);
      this.m_context = context;
      ShowDialog(list);
   }
   
   @Override
   public void ok()
   {
      Long count = 0l;
      for (int i = 0; i < list.size(); i++)
      {
         count += T.FileCount(list.get(i).getFilePath());
      }
      mProgressDialog.setMax(count.intValue());
      String path = cli.getPath();
      final boolean result = UpLoad(path, list);
      SysEng.getInstance().addHandlerEvent(new AbsEvent()
      {
         @Override
         public void ok()
         {
	  if (notifySetChanged != null)
	  {
	     if (result)
	     {
	        notifySetChanged.NotifyDataSetChanged(
		    Const.cmd_KuaiPan_upload_Finish, null);
	     }
	     else
	     {
	        notifySetChanged.NotifyDataSetChanged(
		    Const.cmd_KuaiPan_upload_Error, null);
	     }
	  }
	  mProgressDialog.dismiss();
         }
      });
   }
   
   private void ShowDialog(List<FileBean> mFileList)
   {
      mProgress = true;
      mProgressDialog = new ProgressDialog(m_context);
      mProgressDialog.setTitle(m_context
	  .getString(R.string.msg_upload_dialog_title));
      mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      mProgressDialog.setMax(mFileList.size());
      mProgressDialog.setButton(m_context.getString(R.string.cancel),
	  new DialogInterface.OnClickListener()
	  {
	     public void onClick(DialogInterface dialog, int whichButton)
	     {
	        mProgress = false;
	     }
	  });
      mProgressDialog.setProgress(0);
      mProgressDialog.show();
   }
   
   /** 上传文件的方法（删除该文件夹下的所有文件） */
   private boolean UpLoad(String path, List<FileBean> list)
   {
      boolean result = true;
      for (int i = 0; i < list.size() && mProgress; i++)
      {
         File temp = list.get(i).getFile();
         result = UpLoadFolder(path, temp);
         if (!result) { return result; }
      }
      return result;
   }
   
   /** 上传文件夹的方法（删除该文件夹下的所有文件） */
   private boolean UpLoadFolder(String path, final File folder)
   {
      boolean result = true;
      if (folder.isFile())
      {
         try
         {
	  result = UpLoadFile(path, folder);
         }
         catch (Exception e)
         {
	  e.printStackTrace();
	  result = false;
         }
         mProgressDialog.incrementProgressBy(1);
         if (!result)
         {
	  SysEng.getInstance().addHandlerEvent(new AbsEvent()
	  {
	     @Override
	     public void ok()
	     {
	        Toast.makeText(
		    m_context,
		    folder.getName()
		          + m_context
		                .getString(R.string.msg_upload_file_error),
		    Toast.LENGTH_SHORT).show();
	     }
	  });
         }
         return result;
      }
      else
      {
         File[] fileArray = folder.listFiles();
         path += folder.getName() + File.separator;
         try
         {
	  result = KuaipanAPI.createFolder(path) != null;// by wmh
         }
         catch (Exception e)
         {
	  e.printStackTrace();
	  result = false;
         }
         if (!result)
         {
	  SysEng.getInstance().addHandlerEvent(new AbsEvent()
	  {
	     @Override
	     public void ok()
	     {
	        Toast.makeText(
		    m_context,
		    folder.getName()
		          + m_context
		                .getString(R.string.msg_upload_createfolder_error),
		    Toast.LENGTH_SHORT).show();
	     }
	  });
	  return result;
         }
         if (fileArray.length > 0)
         {
	  for (File currentFile : fileArray)
	  {
	     if (!mProgress) return false;
	     // 遍历该目录
	     result = UpLoadFolder(path, currentFile);// 回调
	     if (!result) { return result; }
	  }
         }
         mProgressDialog.incrementProgressBy(1);
         return result;
      }
   }
   
   public boolean UpLoadFile(String path, File file)
         throws FileNotFoundException, KuaipanIOException,
         KuaipanServerException, KuaipanAuthExpiredException
   {
      long size = file.length();
      InputStream is = null;
      is = new FileInputStream(file);
      boolean result = KuaipanAPI.uploadFile(path + file.getName(), is, size,
	  true, null);
      return result;
   }
   
}
