package com.kenny.file.tools;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
/**
 * 键盘操作
 * @author WangMinghui
 *
 */
public class KInputMethod
{
   /**
    * 显示隐藏操作
    * @param ctx
    * @param view
    */
   public static void HideShowInputMethod(Context ctx,View view)
   {
      InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
      //imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
   }
   public static void HideInputMethod(Context ctx,View view)
   {
      InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
   }
   public static void ShowInputMethod(Context ctx,View view)
   {
      InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.showSoftInput(view, 0);
   }
}
