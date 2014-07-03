package com.byfen.market;

import com.byfen.market.R;

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
      final AlertDialog dialog;
      AlertDialog.Builder builer = new Builder(m_act);
      String title = "关于";
      builer.setTitle(title);
      String content = m_act.getTitle() + GetVersionName(m_act)+"\n"
	  + m_act.getString(R.string.aboutContent);
      builer.setMessage(content);
      builer.setNegativeButton("确认",
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
    * 获得当前系统本号
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
