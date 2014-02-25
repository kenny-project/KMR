package com.kenny.file.dialog;

import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kenny.KFileManager.R;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.manager.FileManager;
import com.kenny.file.manager.IManager;
import com.kenny.file.util.Const;

public class RenameDialog
{
        /** 调用弹出重命名框的方法 */
        public static void Show(final Context context, final File file,final INotifyDataSetChanged notif)
        {
	      LayoutInflater mLI = LayoutInflater.from(context);
	      LinearLayout mLL = (LinearLayout) mLI.inflate(
		            R.layout.rename_dialog, null);
	      final EditText mET = (EditText) mLL
		            .findViewById(R.id.new_filename);
	      mET.setText(file.getName());
	      
	      DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
	      {
		    public void onClick(DialogInterface dialog, int which)
		    {
			  String modifyName = mET.getText().toString();
			  final String modifyFilePath = file
				        .getParentFile().getPath()
				        + java.io.File.separator;
			  final String newFilePath = modifyFilePath
				        + modifyName;
			  // 判断该新的文件名是否已经在当前目录下存在
			  if (new File(newFilePath).exists())
			  {
				Toast.makeText(context, "该文件名已经存在",
				                Toast.LENGTH_SHORT)
				                .show();
			  }
			  else
			  { // 文件名不重复时直接修改文件名后再次刷新列表
				file.renameTo(new File(newFilePath));
				if(notif!=null)
				notif.NotifyDataSetChanged(Const.cmd_RenameFileEvent_Finish, null);
			  }
		    }
		    
	      };
	      AlertDialog renameDialog = new AlertDialog.Builder(context)
		            .create();
	      renameDialog.setTitle(R.string.RenameDialog_Title);
	      renameDialog.setView(mLL);
	      renameDialog.setButton(context.getString(R.string.submit), listener);
	      renameDialog.setButton2(context.getString(R.string.cancel),
		            new DialogInterface.OnClickListener()
		            {
			          
			          public void onClick(
			                          DialogInterface dialog,
			                          int which)
			          {
				        
			          }
		            });
	      renameDialog.show();
        }
}
