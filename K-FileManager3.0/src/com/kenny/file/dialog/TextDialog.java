package com.kenny.file.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.kenny.KFileManager.R;
public class TextDialog
{
   public void showDialog(final Activity m_act,String title,String content)
   {
      final AlertDialog dialog;
      AlertDialog.Builder builer = new Builder(m_act);
      builer.setTitle(title);
      builer.setMessage(content);
      // 当点确定按钮时从服务器上下载 新的apk 然后安装
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
}
