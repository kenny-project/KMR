package com.kenny.file.dialog;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Event.GetFolderSizeEvent;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.tools.T;

public class KDialog
{
   public static void ShowDialog(Activity m_ctx, String szTitle,
         String szContent, String szPositive, OnClickListener clPositive,
         String szNegative, OnClickListener clNegative)
   {
      final AlertDialog dialog;
      AlertDialog.Builder builer = new Builder(m_ctx);
      if (szTitle != null)
      {
         builer.setTitle(szTitle);
      }
      if (szContent != null)
      {
         builer.setMessage(szContent);
      }
      // 当点确定按钮时从服务器上下载 新的apk 然后安装
      if (szPositive != null) builer.setPositiveButton(szPositive, clPositive);
      if (szNegative != null) builer.setNegativeButton(szNegative, clNegative);
      dialog = builer.create();
      dialog.show();
   }
   
   /** 长按文件或文件夹时弹出的带ListView效果的功能菜单 */
   public static void ShowFileTypeArray(final Context context, String title,
         int SelectPos, final DialogInterface.OnClickListener listener)
   {
      AlertDialog.Builder test = new AlertDialog.Builder(context);
      test.setTitle(title);
      test.setSingleChoiceItems(R.array.fileType, SelectPos,
	  new OnClickListener()
	  {
	     
	     public void onClick(DialogInterface dialog, int which)
	     {
	        if (listener != null)
	        {
		 listener.onClick(dialog, which);
	        }
	        dialog.cancel();
	     }
	  });
      test.setNegativeButton(context.getString(R.string.cancel), null);
      test.create();
      test.show();
   }
   
   @TargetApi(9)
   public static void ShowDetailsDialog(final Context m_ctx, String path)
   {
      final AlertDialog dialog;
      File file = new File(path);
      
      LayoutInflater factory = LayoutInflater.from(m_ctx);
      final View textEntryView = factory.inflate(R.layout.details_dialog, null);
      AlertDialog.Builder builer = new AlertDialog.Builder(m_ctx);
      // .setIcon(R.drawable.alert_dialog_icon)
      builer.setTitle(R.string.DetailsDialog_Title);
      builer.setView(textEntryView);
      builer.setPositiveButton(R.string.ok,
	  new DialogInterface.OnClickListener()
	  {
	     public void onClick(DialogInterface dialog, int whichButton)
	     {
	        
	        /* User clicked OK so do some stuff */
	     }
	  });
      dialog = builer.create();
      TextView tvfileName = (TextView) textEntryView
	  .findViewById(R.id.tvfileName);
      tvfileName.setText(file.getName());
      
      TextView tvFileType = (TextView) textEntryView
	  .findViewById(R.id.tvFileType);
      TextView tvfilePath = (TextView) textEntryView
	  .findViewById(R.id.tvfilePath);
      // String strSize = "空间:" + T.FileSizeToString(file.getFreeSpace()) + "/"
      // + T.FileSizeToString(file.getTotalSpace());
      
      tvfilePath.setText(file.getPath());
      
      final TextView tvFileSize = (TextView) textEntryView
	  .findViewById(R.id.tvFileSize);
      tvFileSize.setText(T.FileSizeToString(0L));
      tvFileSize.setText(T.FileSizeToString(file.getFreeSpace()));
      
      final TextView tvFileCount = (TextView) textEntryView
	  .findViewById(R.id.tvFileCount);
      tvFileCount.setText(m_ctx.getString(R.string.unknown));
      GetFolderSizeEvent FolderSize = new GetFolderSizeEvent(m_ctx,
	  file.getPath(), new INotifyDataSetChanged()
	  {
	     
	     public void NotifyDataSetChanged(int cmd, Object value)
	     {
	        if (cmd == 1)
	        {
		 tvFileSize.setText(T.FileSizeToString((Long) value));
	        }
	        else
	        {
		 tvFileCount.setText(String.format(
		       m_ctx.getString(R.string.msg_total_documents), value));// "共"
		 // +
		 // ((Long)
		 // value).toString()
		 // + "个文件");
	        }
	     }
	  });
      SysEng.getInstance().addEvent(FolderSize);
      
      TextView tvFileTotalSpace = (TextView) textEntryView
	  .findViewById(R.id.tvFileTotalSpace);
      tvFileTotalSpace.setText(T.FileSizeToString(file.getTotalSpace()));
      TextView tvFileFreeSpace = (TextView) textEntryView
	  .findViewById(R.id.tvFileFreeSpace);
      tvFileFreeSpace.setText(T.FileSizeToString(file.getFreeSpace()));
      
      String yes = m_ctx.getString(R.string.yes);
      String no = m_ctx.getString(R.string.no);
      TextView tvFileModified = (TextView) textEntryView
	  .findViewById(R.id.tvFileModified);
      String datetime = DateFormat.format("yyyy-MM-dd kk:mm:ss",
	  file.lastModified()).toString();
      tvFileModified.setText(datetime);
      TextView tvFileWriteable = (TextView) textEntryView
	  .findViewById(R.id.tvFileWriteable);
      tvFileWriteable.setText(file.canWrite() ? yes : no);
      
      TextView tvFileReadable = (TextView) textEntryView
	  .findViewById(R.id.tvFileReadable);
      tvFileReadable.setText(file.canWrite() ? yes : no);
      
      TextView tvFileHidden = (TextView) textEntryView
	  .findViewById(R.id.tvFileHidden);
      tvFileHidden.setText(file.isHidden() ? yes : no);
      if (file.isDirectory())
      {
         tvFileType.setText(R.string.foler);
      }
      else
      {
         tvFileType.setText(R.string.file);
      }
      // 当点确定按钮时从服务器上下载 新的apk 然后安装
      dialog.show();
   }
}
