package com.kenny.file.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

public class KMessageDialog extends Dialog
{
   private Activity context;
   private View view;
   private OnCancelListener onCancelListener;
   private String title;
   
   public KMessageDialog(Activity context, String title, View view,
         OnCancelListener onCancelListener)
   {
      super(context);
      this.context = context;
      this.title = title;
      this.view = view;
      this.onCancelListener = onCancelListener;
   }
   
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      if (title == null)
      {
         requestWindowFeature(Window.FEATURE_NO_TITLE);// Window.FEATURE_CUSTOM_TITLE
      }
      else
      {
         this.setTitle(title);
      }
      // setContentView(R.layout.alert_dialog_load_sdcard_file);
      setContentView(view);
      WindowManager m = context.getWindowManager();
      Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
      LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
      // p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.8
      p.width = (int) (d.getWidth() * 0.95); // 宽度设置为屏幕的0.95
      getWindow().setAttributes(p); // 设置生效
      if (onCancelListener != null) this.setOnCancelListener(onCancelListener);
   }
}
