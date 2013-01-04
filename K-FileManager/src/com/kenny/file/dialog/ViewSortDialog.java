package com.kenny.file.dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.framework.log.P;
import com.kenny.KFileManager.R;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.util.Const;
import com.kenny.file.util.Theme;
/**
 * 显示风格
 * @author WangMinghui
 *
 */
public class ViewSortDialog
{
   private Activity m_context;
   private int nStyleMode;
   public void ShowDialog(final Activity context, final INotifyDataSetChanged result)
   {
       nStyleMode=Theme.getSortMode()%10;
       P.v("ViewSortDialog:Theme.getSortMode()="+Theme.getSortMode());
       m_context = context;
       AlertDialog.Builder test = new AlertDialog.Builder(context);
       test.setTitle(m_context.getString(R.string.viewStyle_Title));
       
       test.setSingleChoiceItems(R.array.viewSort, nStyleMode, 
               new OnClickListener()
               {
                   public void onClick(DialogInterface dialog, int which)
                   {
          	  nStyleMode=which;
                   }
               });
       test.setPositiveButton(m_context.getString(R.string.list_sort_Asc_Title), new OnClickListener()
      {
         
         public void onClick(DialogInterface dialog, int which)
         {
            // TODO Auto-generated method stub
	  P.v("ViewSortDialog:Theme.setSortMode()="+nStyleMode+10);
            Theme.setSortMode(nStyleMode+10);
            Theme.Save(m_context);
            dialog.cancel();
            result.NotifyDataSetChanged(Const.cmd_Local_ListSort_Finish,null);
         }
      });
       test.setNegativeButton(m_context.getString(R.string.list_sort_Desc_Title), new OnClickListener()
      {
         public void onClick(DialogInterface dialog, int which)
         {
            // TODO Auto-generated method stub
	  P.v("ViewSortDialog:Theme.setSortMode()="+nStyleMode);
            Theme.setSortMode(nStyleMode);
            Theme.Save(m_context);
            dialog.cancel();
            result.NotifyDataSetChanged(Const.cmd_Local_ListSort_Finish,null);
         }
      });
       test.create();
       test.show();
   }
}
