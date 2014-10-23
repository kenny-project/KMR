package com.kenny.file.dialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kenny.KFileManager.R;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.manager.SDFileTools;

public class CreateFileDialog
{
   /**
    * 创建文件夹的方法:当用户点击软件下面的创建菜单的时候，是在当前目录下创建的一个文件夹 静态变量mCurrentFilePath存储的就是当前路径
    * java.io.File.separator是JAVA给我们提供的一个File类中的静态成员，它会根据系统的不同来创建分隔符
    * mNewFolderName正是我们要创建的新文件的名称，从EditText组件上得到的
    */
   public static void Show(final Context context, final String mCurrentFilePath,final INotifyDataSetChanged notif)
   {
      LayoutInflater mLI = (LayoutInflater) context
	  .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      final LinearLayout mLL = (LinearLayout) mLI.inflate(
	  R.layout.alert_dialog_create, null);
      RadioGroup mCreateRadioGroup = (RadioGroup) mLL
	  .findViewById(R.id.radiogroup_create);
      final RadioButton mrbCreateFile = (RadioButton) mLL
	  .findViewById(R.id.create_file);
      final EditText meditText = (EditText) mLL.findViewById(R.id.new_filename);
      // final RadioButton mCreateFolderButton = (RadioButton) mLL
      // .findViewById(R.id.create_folder);
      mrbCreateFile.setChecked(true);// 设置默认为创建文件夹
      // final Spinner s1 = (Spinner) mLL.findViewById(R.id.sp);
      
      Builder mBuilder = new AlertDialog.Builder(context)
	  .setTitle(R.string.CreateFileDialog_title)
	  .setView(mLL)
	  .setPositiveButton(R.string.m_New,
	        new DialogInterface.OnClickListener()
	        {
	           public void onClick(DialogInterface dialog, int which)
	           {
		    String mNewFolderName = meditText.getText().toString()
		          .trim();
		    if (mNewFolderName.length() > 0)
		    {
		       if (mrbCreateFile.isChecked())
		       {
		    	   SDFileTools.CreateFile(context,
		                mCurrentFilePath, mNewFolderName, "txt",notif);
		       }
		       else
		       {
		    	   SDFileTools.CreateFolder(context,
		                mCurrentFilePath, mNewFolderName,notif);
		       }
		    }
		    else
		    {
		       Toast.makeText(context, "名称不能为空!",
		             Toast.LENGTH_SHORT).show();
		    }
	           }
	        }).setNeutralButton(context.getString(R.string.cancel), null);
      mCreateRadioGroup
	  .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
	  {
	     
	     public void onCheckedChanged(RadioGroup arg0, int arg1)
	     {
	        if (arg1 == mrbCreateFile.getId())
	        {
		 // s1.setVisibility(View.VISIBLE);
		 meditText.setHint(R.string.hint_createfile);
	        }
	        else
	        {
		 // s1.setVisibility(View.GONE);
		 meditText.setHint(R.string.hint_createfolder);
	        }
	     }
	  });
      mBuilder.show();
      
   }
}
