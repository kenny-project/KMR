package com.kenny.file.dialog;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kenny.KFileManager.R;
import com.kenny.file.Adapter.FolderAdapter;
import com.kenny.file.bean.FileBean;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.util.FolderManager;

/**
 * 文件列表获取功能
 * 
 * @author wangminghui
 * 
 */
public class FolderListDialog implements OnItemClickListener, OnClickListener
{
   public static final int Finish=100;
   private Context mContext;
   private Dialog alertDialog;
   private View alertView;
   private ListView lvFolderlist;
   private FolderManager manager;
   private FolderAdapter mFolderAdapter;
   private INotifyDataSetChanged notify;
   private TextView mCurrentPath;
   public FolderListDialog()
   {
      manager = new FolderManager();
   }
   
   public void ShowDialog(Context context, String path,INotifyDataSetChanged notify)
   {
      mContext = context;
      this.notify=notify;
      manager.setContext(context);
      manager.setFilePath(path);
      alertDialog = new Dialog(context);
      alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// Window.FEATURE_CUSTOM_TITLE
      LayoutInflater factory = LayoutInflater.from(context);
      alertView = factory.inflate(R.layout.alert_dialog_folder_list, null);
      alertDialog.setContentView(alertView);
      
      mCurrentPath= (TextView) alertView.findViewById(R.id.mCurrentPath);
      Button btOK = (Button) alertView.findViewById(R.id.btOK);
      btOK.setOnClickListener(this);
      Button btCancel = (Button) alertView.findViewById(R.id.btCancel);
      btCancel.setOnClickListener(this);
      
      lvFolderlist = (ListView) alertView.findViewById(R.id.lvFolderlist);
      mFolderAdapter = new FolderAdapter(mContext, manager.getFileList());
      mCurrentPath.setText(manager.getCurrentPath());
      lvFolderlist.setAdapter(mFolderAdapter);
      lvFolderlist.setOnItemClickListener(this);
      alertDialog.show();
   }
   
   public void onClick(View v)
   {
      switch (v.getId())
      {
      case R.id.btOK:
         notify.NotifyDataSetChanged(Finish, manager.getCurrentPath());
         break;
      case R.id.btCancel:
         break;
      }
      if (alertDialog != null)
      {
         alertDialog.dismiss();
         alertDialog = null;
      }
   }
   
   public void onItemClick(AdapterView<?> arg0, View arg1, int position,
         long arg3)
   {
      FileBean temp = mFolderAdapter.getItem(position);
      if (temp != null)
      {
         if (position == 0 && temp.getFileName().equals(".."))
         {// 返回到上一层
	  this.manager.Back();
	  mCurrentPath.setText(manager.getCurrentPath());
	  mFolderAdapter.notifyDataSetChanged();
	  return;
         }
         final File mFile = temp.getFile();
         // 如果该文件是可读的，我们进去查看文件
         if (mFile.isDirectory())
         {
	  if (mFile.canRead())
	  {
	     // 如果是文件夹，则直接进入该文件夹，查看文件目录
	     manager.setFilePath(mFile.getPath());
	     mCurrentPath.setText(manager.getCurrentPath());
	     mFolderAdapter.notifyDataSetChanged();
	  }
	  else
	  {// 如果该文件不可读，我们给出提示不能访问，防止用户操作系统文件造成系统崩溃等
	     Toast.makeText(
		 mContext,
		 mContext
		       .getString(R.string.msg_sorry_file_not_exist_permissions),
		 Toast.LENGTH_SHORT).show();
	  }
         }
         else
         {	//非文件夹
	  Toast.makeText(mContext,
	        mContext.getString(R.string.msg_sorry_file_permissions),
	        Toast.LENGTH_SHORT).show();
         }
      }
   }
}
