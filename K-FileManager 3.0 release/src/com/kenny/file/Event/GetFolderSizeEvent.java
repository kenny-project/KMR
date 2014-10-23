package com.kenny.file.Event;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;

import com.framework.event.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.tools.T;

/**
 * @author aimery 初始化event
 * */
public class GetFolderSizeEvent extends AbsEvent
{
   private Context act;
   private String folderPath;
   private INotifyDataSetChanged notifChanged;
   
   public GetFolderSizeEvent(Context act, String folderPath,
         INotifyDataSetChanged notifChanged)
   {
      this.act = act;
      this.folderPath = folderPath;
      this.notifChanged = notifChanged;
   }
   
   private ProgressDialog myDialog = null;
   
   
   public void ok()
   {
      SysEng.getInstance().addHandlerEvent(new AbsEvent()
      {
         
         public void ok()
         {
	  myDialog = ProgressDialog.show(act, "", "正在获取文件夹大小...", true, true);
	  myDialog.show();
	  myDialog.setOnDismissListener(new OnDismissListener()
	  {
	     
	     public void onDismiss(DialogInterface dialog)
	     {
	        // TODO Auto-generated method stub
	        
	     }
	  });
         }
      });
      final Long length = T.FileSize(folderPath);
      final Long count= T.FileCount(folderPath);
      SysEng.getInstance().addHandlerEvent(new AbsEvent()
      {
         
         public void ok()
         {
	  if (myDialog != null) myDialog.dismiss();
	  if(notifChanged!=null)
	  {
	     notifChanged.NotifyDataSetChanged(1,length);
	     notifChanged.NotifyDataSetChanged(2,count);
	  
	  }
	  
         }
      });
   }
}
