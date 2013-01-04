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
import com.kenny.file.util.FileManager;

public class RenameDialog
{
        /** 调用弹出重命名框的方法 */
        public static void Show(final Context context, final File file)
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
				// if
				// (!modifyName.equals(file.getName()))
				// {// 把“重命名”操作时没做任何修改的情况过滤掉
				// // 弹出该新命名后的文件已经存在的提示，并提示接下来的操作
				// new AlertDialog.Builder(context)
				// .setTitle("提示!")
				// .setMessage("该文件名已存在，是否要覆盖?")
				// .setPositiveButton(
				// "确定",
				// new DialogInterface.OnClickListener()
				// {
				// public void onClick(
				// DialogInterface dialog,
				// int which)
				// {
				// file.renameTo(new File(
				// newFilePath));
				// Toast.makeText(context,
				// "the file path is "
				// + new File(
				// newFilePath),
				// Toast.LENGTH_SHORT)
				// .show();
				// LocalFileManage.GetHandler()
				// .Refresh();
				// }
				// })
				// .setNegativeButton(
				// "取消",
				// null)
				// .show();
				// }
			  }
			  else
			  { // 文件名不重复时直接修改文件名后再次刷新列表
				file.renameTo(new File(newFilePath));
				FileManager.GetHandler().Refresh();
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
