package com.kenny.file.dialog;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kenny.KFileManager.t.R;
import com.kenny.file.tools.SaveData;
import com.kenny.file.util.Const;

public class EditKuaiPanDialog
{
   /** 调用弹出重命名框的方法 */
   public static void Show(final Activity context, String title)
   {
      LayoutInflater mLI = LayoutInflater.from(context);
      LinearLayout mLL = (LinearLayout) mLI.inflate(R.layout.rename_dialog,
	  null);
      final EditText mET = (EditText) mLL.findViewById(R.id.new_filename);
      String strValue = SaveData.Read(context,
	  Const.strEditKuaiPanDialogValue, "");// 输入自动化
      mET.setText(strValue);
      DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
      {
         public void onClick(DialogInterface dialog, int which)
         {
	  String modifyName = mET.getText().toString();
	  String msg;
	  if (modifyName.length() <= 0)
	  {
	     modifyName = Const.getSDCard();
	  }
	  String filePath = Const.szKuaiPanPath + modifyName;
	  File file = new File(filePath);
	  // 判断该新的文件名是否已经在当前目录下存在
	  if (file.exists())
	  {
	     msg = "该昵称已经存在!";
	  }
	  else
	  { // 文件名不重复时直接修改文件名后再次刷新列表
	     if (file.mkdirs())
	     {
	        msg = "创建成功!";
	        modifyName = "";
	        String auth_url;
	        try
	        {
//		 auth_url = KuaipanAPI.requestToken();
//		 SysEng.getInstance().addHandlerEvent(
//		       new NextPageEvent(context, new KuaiPanLoginPage(
//			   context, auth_url), 1, null));
	        }
	        catch (Exception e)
	        {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
	        }
	        
	     }
	     else
	     {
	        msg = "创建失败,请检查是否合法!";
	     }
	  }
	  SaveData.Write(context,
	        Const.strEditKuaiPanDialogValue, modifyName);// 输入自动化
	  Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
         }
         
      };
      AlertDialog renameDialog = new AlertDialog.Builder(context).create();
      renameDialog.setTitle(title);
      renameDialog.setView(mLL);
      renameDialog.setButton(context.getString(R.string.ok), listener);
      renameDialog.setButton2(context.getString(R.string.cancel),
	  new DialogInterface.OnClickListener()
	  {
	     public void onClick(DialogInterface dialog, int which)
	     {
	        
	     }
	  });
      renameDialog.show();
      
   }
}
