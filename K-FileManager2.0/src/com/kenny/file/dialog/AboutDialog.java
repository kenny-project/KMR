package com.kenny.file.dialog;

import com.kenny.KFileManager.t.R;
import com.kenny.file.tools.T;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;

public class AboutDialog
{
   public void showDialog(final Activity m_act)
   {
      MobclickAgent.onEvent(m_act, "KMainPage", "aboutPage");
      final AlertDialog dialog;
      AlertDialog.Builder builer = new Builder(m_act);
      String title = m_act.getString(R.string.aboutTitle);
      builer.setTitle(title);
      String content = m_act.getTitle() +" V "+ GetVersionName(m_act)
	  + m_act.getString(R.string.aboutContent);
      builer.setMessage(content);
      // 当点确定按钮时从服务器上下载 新的apk 然后安装
      builer.setPositiveButton(m_act.getString(R.string.about_CommentScore),
	  new OnClickListener()
	  {
	     public void onClick(DialogInterface dialog, int which)
	     {
	        T.DetailsIntent(m_act);
	        MobclickAgent.onEvent(m_act, "CommentScore", "AboutScore");
	     }
	  });
      builer.setNegativeButton(m_act.getString(R.string.done),
	  new OnClickListener()
	  {
	     
	     public void onClick(DialogInterface dialog, int which)
	     {
	        dialog.dismiss();
	     }
	  });
      dialog = builer.create();
      dialog.show();
   }
   
   /**
    * 获得当前系统版 本号
    * 
    * @param context
    * @return
    */
   String GetVersionName(Context context)
   {
      String versionName = "";
      try
      {
         versionName = context.getPackageManager().getPackageInfo(
	     context.getPackageName(), 0).versionName;
         return versionName;
      }
      catch (NameNotFoundException e)
      {
         return versionName;
      }
   }
}
