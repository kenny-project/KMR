package com.kenny.file.Event;

import java.io.File;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.framework.event.AbsEvent;
import com.kenny.KFileManager.R;
import com.kenny.file.dialog.SimpleArrayDialog;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.tools.MIMEType;
import com.kenny.file.util.FT;

/**
 * @author kenny 初始化event
 * */
public class openFileEvent extends AbsEvent implements INotifyDataSetChanged
{
   protected Context m_act;
   protected String path;
   protected String fileEnds;
   protected File file;
   
   public openFileEvent(Context m_act, String path)
   {
      this.m_act = m_act;
      this.path = path;
      file = new File(path);
      fileEnds = FT.getExName(file.getName());
   }
   
   
   public void ok()
   {
      // 调用系统
      String strMIMEType = MIMEType.getMIMEType(file);// FT.getMIMEType(file);
      if (strMIMEType == null)
      {
         String[] item = m_act.getResources().getStringArray(R.array.MimeType);
         new SimpleArrayDialog().ShowDialog(m_act, "打开方式", item,
	     m_act.getString(R.string.cancel), null, this);
      }
      else
      {
         try
         {
	  
         Intent intent = new Intent();
         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
         intent.setAction(android.content.Intent.ACTION_VIEW);
         intent.setDataAndType(Uri.fromFile(file), strMIMEType);
         m_act.startActivity(intent);
	}
	catch (ActivityNotFoundException e){
		Toast.makeText(m_act, 
		      m_act.getString(R.string.can_not_open_file), 
				Toast.LENGTH_SHORT).show();
	}
//	try {
//	   m_act.startActivityForResult(intent, 2);
//	}
//	catch (ActivityNotFoundException e){
//		Toast.makeText(m_act, 
//		      m_act.getString(R.string.can_not_open_file), 
//				Toast.LENGTH_SHORT).show();
//	}
      }
   }
   
   
   public void NotifyDataSetChanged(int cmd, Object value)
   {
      String type = "";
      switch (cmd)
      {
      case 0:
         type = "text/*";// 系统将列出所有可能打开音频文件的程序选择器
         break;
      case 1:
         type = "audio/*";// 系统将列出所有可能打开音频文件的程序选择器
         break;
      case 2:
         type = "video/*";// 系统将列出所有可能打开视频文件的程序选择器
         break;
      case 3:
         type = "image/*";// 系统将列出所有可能打开图片文件的程序选择器
         break;
      default:
         type = "*/*"; // 系统将列出所有可能打开该文件的程序选择器
         break;
      }
      File file = new File(path);
      Intent intent = new Intent();
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.setAction(android.content.Intent.ACTION_VIEW);
      intent.setDataAndType(Uri.fromFile(file), type);
      m_act.startActivity(intent);
   }
}
