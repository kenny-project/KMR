package com.kenny.file.dialog;

import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kenny.KFileManager.R;
import com.kenny.file.tools.SaveData;
import com.kenny.file.util.Const;

public class EditDialog
{
        /** 调用弹出重命名框的方法 */
        public static void Show(final Context context,String title,final String strValue,String strButton1,OnClickListener onButton1,String strButton2,OnClickListener onButton2 )
        {
	      LayoutInflater mLI = LayoutInflater.from(context);
	      LinearLayout mLL = (LinearLayout) mLI.inflate(
		            R.layout.rename_dialog, null);
	      final EditText mET = (EditText) mLL
		            .findViewById(R.id.new_filename);
	      mET.setText(strValue);
	      DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
	      {
		    public void onClick(DialogInterface dialog, int which)
		    {
			  String modifyName = mET.getText().toString();
			  if(modifyName.length()<=0)
			  {
			     modifyName=Const.getSDCard();
			  }
			  // 判断该新的文件名是否已经在当前目录下存在
			  if (!new File(modifyName).exists())
			  {
				Toast.makeText(context, "该路径不存在,请重新输入",
				                Toast.LENGTH_SHORT)
				                .show();
			  }
			  else
			  { // 文件名不重复时直接修改文件名后再次刷新列表
			         SaveData
				     .Write(context, Const.strTabVisible, modifyName);// 输入自动化
			  }
		    }
		    
	      };
	      AlertDialog renameDialog = new AlertDialog.Builder(context)
		            .create();
	      renameDialog.setTitle(title);
	      renameDialog.setView(mLL);
	      renameDialog.setButton(strButton1, onButton1);
	      renameDialog.setButton2(strButton2,onButton2);
	      renameDialog.show();
//		            new DialogInterface.OnClickListener()
//		            {
//			          
//			          public void onClick(
//			                          DialogInterface dialog,
//			                          int which)
//			          {
//				        
//			          }
//		            });
	      
        }
}
