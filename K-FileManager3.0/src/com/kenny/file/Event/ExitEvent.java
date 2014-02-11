package com.kenny.file.Event;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.framework.event.AbsEvent;
import com.framework.util.CommLayer;
import com.kenny.KFileManager.R;

/**
 * @author aimery 初始化event
 * */
public class ExitEvent extends AbsEvent
{
   Activity act;
   boolean isShow;
   
   public ExitEvent(Activity act, boolean isShow)
   {
      this.act = act;
      this.isShow = isShow;
   }
   
   @Override
   public void ok()
   {
      if (isShow)
      {
         showdialog(act, act.getString(R.string.msg_you_want_quit));
      }
      else
      {
         //CommLayer.getPMG().onDestroy(); by wmh 
         act.finish();
         System.exit(0);
      }
   }
   
   private void showdialog(final Activity activity, String Title)
   {
      AlertDialog.Builder builder = new AlertDialog.Builder(activity);
      builder
	  .setMessage(Title)
	  .setCancelable(false)
	  .setPositiveButton(act.getString(R.string.yes),
	        new DialogInterface.OnClickListener()
	        {
	           public void onClick(DialogInterface dialog, int id)
	           {
//	              CommLayer.getPMG().onDestroy();
		    activity.finish();
		    System.exit(0);
		    // android.os.Process.killProcess(android.os.Process
		    // .myPid());
	           }
	        })
	  .setNegativeButton(act.getString(R.string.no),
	        new DialogInterface.OnClickListener()
	        {
	           public void onClick(DialogInterface dialog, int id)
	           {
		    dialog.cancel();
	           }
	        });
      AlertDialog alert = builder.create();
      alert.show();
   }
}
