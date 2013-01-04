package com.kenny.file.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;

public class LocalAddressDialog 
{
   private Activity m_act;
   private Dialog alertDialog;
   private View alertView;

   
   public void ShowDialog(Activity context)
   {
      
      m_act = context;
      alertDialog = new SharePost(context);
      // Window mWindow = alertDialog.getWindow();
      // WindowManager.LayoutParams lp = mWindow.getAttributes();
      // lp.width=480;
      // lp.height=480;
      // mWindow.setAttributes(lp);
     // alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// Window.FEATURE_CUSTOM_TITLE
      // alertDialog.setTitle("菜单");
      LayoutInflater factory = LayoutInflater.from(context);
      //alertView = factory.inflate(R.layout.alert_dialog_address, null);
      //alertDialog.setContentView(alertView);
      alertDialog.show();
   }
   
   
}
