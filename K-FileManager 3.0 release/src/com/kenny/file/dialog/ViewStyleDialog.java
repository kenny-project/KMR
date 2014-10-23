package com.kenny.file.dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.kenny.KFileManager.R;
import com.kenny.file.interfaces.KActivityStatus;
import com.kenny.file.util.Theme;
/**
 * 显示风格
 * @author WangMinghui
 *
 */
public class ViewStyleDialog
{
   private Activity m_context;
   public void ShowDialog(final Activity context, final KActivityStatus result)
   {
       int nStyleMode=Theme.getStyleMode();
       m_context = context;
       AlertDialog.Builder test = new AlertDialog.Builder(context);
       test.setTitle(m_context.getString(R.string.viewStyle_Title));
       
       test.setSingleChoiceItems(R.array.viewStyle, nStyleMode, 
               new OnClickListener()
               {
                   
                   public void onClick(DialogInterface dialog, int which)
                   {
                       Theme.setStyleMode(which);
                       Theme.Save(m_context);
                       dialog.cancel();
                   }
               });
       test.setNegativeButton(m_context.getString(R.string.cancel), null);
       test.create();
       test.show();
   }
}
