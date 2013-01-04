package com.kenny.file.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Event.UnInstallEvent;
import com.kenny.file.Event.delFileEvent;
import com.kenny.file.Event.openFileEvent;
import com.kenny.file.bean.AppBean;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.tools.ApkTools;
import com.kenny.file.tools.T;

/**
 * 文件对话框菜单
 * 
 * @author WangMinghui
 * 
 */
public class PopAppDialog
{
   
   /** 长按文件或文件夹时弹出的带ListView效果的功能菜单 */
   public static void ShowApp(final Activity context, final AppBean mAppInfo)
   {
      P.v("PopAppDialog");
      DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
      {
         public void onClick(DialogInterface dialog, int item)
         {// item的值就是从0开始的索引值(从列表的第一项开始)
	  dialog.cancel();
	  if (mAppInfo != null)
	  {// 注意，所有对文件的操作必须是在该文件可读的情况下才可以，否则报错
	     switch (item)
	     {
	     case 0: // 打开
	        ApkTools.StartApk(context, mAppInfo.getPackageName());
	        break;
	     case 1:
	        new AppBackUpDialog().ShowDialog(context, mAppInfo);
	        break;
	     case 2:
	        SysEng.getInstance().addHandlerEvent(
		    new UnInstallEvent(context, mAppInfo));
	        break;
	     case 3: // 属性
	        ApkTools.showAppDetails(context, mAppInfo.getPackageName());
	        break;
	     }
	  }
	  else
	  {
	     Toast.makeText(context, "对不起，未找到该应用!", Toast.LENGTH_SHORT)
		 .show();
	  }
         }
      };
      String[] mMenu =
      { "打开", "备份", "卸载", "属性" };
      new AlertDialog.Builder(context).setTitle("请选择操作!")
	  .setItems(mMenu, listener)
	  .setPositiveButton(context.getString(R.string.cancel), null).show();
   }
   
   /** 长按文件或文件夹时弹出的带ListView效果的功能菜单 */
   public static void ShowFile(final Activity context, final AppBean mAppInfo,
         final INotifyDataSetChanged iNotify)
   {
      P.v("PopAppDialog");
      DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
      {
         public void onClick(DialogInterface dialog, int item)
         {// item的值就是从0开始的索引值(从列表的第一项开始)
	  dialog.cancel();
	  if (mAppInfo != null)
	  {// 注意，所有对文件的操作必须是在该文件可读的情况下才可以，否则报错
	     switch (item)
	     {
	     case 0: // 打开
	        if (mAppInfo.getFile().canRead())
	        {
		 SysEng.getInstance().addHandlerEvent(
		       new openFileEvent(context, mAppInfo.getFilePath()));
	        }
	        else
	        {
		 Toast.makeText(context,
		       context.getString(R.string.msg_can_not_operated),
		       Toast.LENGTH_SHORT).show();
	        }
	        break;
	     case 1:
	        T.ShareIntent(context, context.getString(R.string.msg_Send),
		    mAppInfo.getFilePath());
	        break;
	     case 2:// 删除
	        
	        SysEng.getInstance().addEvent(
		    new delFileEvent(context, mAppInfo, iNotify));
	        if (mAppInfo.getFile().canWrite())
	        {
		 mAppInfo.getFile().delete();
	        }
	        else
	        {
		 Toast.makeText(context,
		       context.getString(R.string.msg_can_not_operated),
		       Toast.LENGTH_SHORT).show();
	        }
	        break;
	     case 3: // 属性
	        KDialog.ShowDetailsDialog(context, mAppInfo.getFilePath());
	        break;
	     }
	  }
	  else
	  {
	     Toast.makeText(context, "对不起，未找到该应用!", Toast.LENGTH_SHORT)
		 .show();
	  }
         }
      };
      String[] mMenu =
      { "安装", "分享", "删除", "属性" };
      Builder mAlertDialog = new AlertDialog.Builder(context);
      mAlertDialog.setTitle("请选择操作!");
      mAlertDialog.setItems(mMenu, listener);
      mAlertDialog.setPositiveButton(context.getString(R.string.cancel), null);
      mAlertDialog.show();
   }
   
}
