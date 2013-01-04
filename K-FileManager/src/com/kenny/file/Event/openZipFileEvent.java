package com.kenny.file.Event;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.framework.event.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.file.tools.ZIP;

/**
 * @author aimery 初始化event
 * */
public class openZipFileEvent extends openDefFileEvent
{
   private Activity act;
   private String folderPath;
   private String nameContains;
   private String zipFile;
   
   public openZipFileEvent(Activity act, String zipFile, String folderPath,
         String nameContains)
   {
      super(act, folderPath + nameContains);
      Log.v("wmh", folderPath + nameContains);
      this.act = act;
      this.folderPath = folderPath;
      this.nameContains = nameContains;
      this.zipFile = zipFile;
   }
   
   private ProgressDialog myDialog = null;
   
   @Override
   public void ok()
   {
      SysEng.getInstance().addHandlerEvent(new AbsEvent()
      {
         @Override
         public void ok()
         {
	  myDialog = ProgressDialog.show(act, "", "正在解压数据...", true, true);
	  myDialog.show();
         }
      });
      try
      {
         boolean result = ZIP.upZipSelectedFile(zipFile, folderPath,
	     nameContains);
         if (result)
         {
	  super.ok();
         }
      }
      catch (Exception e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      SysEng.getInstance().addHandlerEvent(new AbsEvent()
      {
         @Override
         public void ok()
         {
	  if (myDialog != null) myDialog.dismiss();
	  
         }
      });
   }
}
