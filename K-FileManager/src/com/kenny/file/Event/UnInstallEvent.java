package com.kenny.file.Event;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.framework.event.AbsEvent;
import com.kenny.file.bean.AppBean;
import com.kenny.file.util.Const;
import com.kenny.file.util.SDFile;

/**
 * @author aimery 初始化event
 * */
public class UnInstallEvent extends AbsEvent
{
   private AppBean mAppFile;
   private Activity m_act;
   
   public UnInstallEvent(Activity act, AppBean mAppFiles)
   {
      mAppFile = mAppFiles;
      this.m_act = act;
   }
   
   @Override
   public void ok()
   {
      try
      {
         int result = SDFile.BackAppFile(mAppFile.getFilePath(),
	     Const.szRecyclePath,
	     mAppFile.getAppName() + mAppFile.getVersionName());
         Uri uninstallUri = Uri.fromParts("package", mAppFile.getPackageName(),
	     null);
         Intent intent = new Intent(Intent.ACTION_DELETE, uninstallUri);
         // // 通过程序的包名创建URL
         // Uri packageURI = Uri.parse("package:"
         // + tmpInfo.packageName);
         // // 创建Intent意图
         // Intent intent = new Intent(Intent.ACTION_EDIT);
         // 设置Uri
         // intent.setData(packageURI);
         // 卸载程序
         m_act.startActivity(intent);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
