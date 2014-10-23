package com.kenny.file.dialog;

import java.io.File;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kenny.KFileManager.R;
import com.kenny.file.Adapter.FolderAdapter;
import com.kenny.file.bean.FileBean;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.manager.FolderManager;

/**
 * 文件列表获取功能
 * 
 * @author wangminghui
 * 
 */
public class FolderListDialog implements OnItemClickListener, OnClickListener
{
	public static final int Finish = 100;
	private Context mContext;
	private Dialog alertDialog;
	private View alertView;
	private ListView lvFolderlist;
	private FolderManager manager;
	private FolderAdapter mFolderAdapter;
	private INotifyDataSetChanged notify;
	private TextView mCurrentPath;
	private List<FileBean> mSrcFileList;//需要复制的路径
	public FolderListDialog()
	{
		manager = new FolderManager();
	}

	public void ShowDialog(Context context, String path,
			List<FileBean> mSrcFileList, INotifyDataSetChanged notify)
	{
		mContext = context;
		this.notify = notify;
		this.mSrcFileList=mSrcFileList;
		manager.setContext(context);
		manager.setFilePath(path);
		alertDialog = new Dialog(context, R.style.NobackDialog);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// Window.FEATURE_CUSTOM_TITLE
		LayoutInflater factory = LayoutInflater.from(context);
		alertView = factory.inflate(R.layout.alert_dialog_folder_list, null);
		alertDialog.setContentView(alertView);

		mCurrentPath = (TextView) alertView.findViewById(R.id.mCurrentPath);
		Button btOK = (Button) alertView.findViewById(R.id.btOK);
		btOK.setOnClickListener(this);
		Button btCancel = (Button) alertView.findViewById(R.id.btCancel);
		btCancel.setOnClickListener(this);

		lvFolderlist = (ListView) alertView.findViewById(R.id.lvFolderlist);
		mFolderAdapter = new FolderAdapter(mContext, manager.getFileList());
		mCurrentPath.setText(manager.getCurrentPath());
		lvFolderlist.setAdapter(mFolderAdapter);
		lvFolderlist.setOnItemClickListener(this);
		
		 WindowManager m = alertDialog.getWindow().getWindowManager();  
	        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高  
	        LayoutParams p = alertDialog.getWindow().getAttributes();  //获取对话框当前的参数值  
	        p.height = (int) (d.getHeight() * 0.8);   //高度设置为屏幕的1.0 
	        p.width = (int) (d.getWidth() * 1);    //宽度设置为屏幕的0.8 
	        p.alpha = 1.0f;		//设置本身透明度
	        p.dimAmount = 0.0f;		//设置黑暗度
	          
	        alertDialog.getWindow().setAttributes(p);     //设置生效
	        alertDialog.getWindow().setGravity(Gravity.CENTER); 		//设置靠右对齐
		alertDialog.show();
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btOK:
			if(new File(manager.getCurrentPath()).canWrite())
			{
				notify.NotifyDataSetChanged(Finish, manager.getCurrentPath());
				break;
			}
			else
			{
				Toast.makeText(
						mContext,
						"该路径为只读路径不能操作:"+manager.getCurrentPath(),
						Toast.LENGTH_SHORT).show();
				return;
			}
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
				String targetPath=mFile.getPath();
				for(int i=0;i<mSrcFileList.size();i++)
				{
					String inPath=mSrcFileList.get(0).getFile().getPath();
					if(targetPath.startsWith(inPath))
					{
						Toast.makeText(
								mContext,
								mContext.getString(R.string.msg_sorry_not_move_file),
								Toast.LENGTH_SHORT).show();		
						return;
					}
					if(targetPath.indexOf(inPath)==0)
					{
						Toast.makeText(
								mContext,
								mContext.getString(R.string.msg_sorry_not_move_file),
								Toast.LENGTH_SHORT).show();		
						return;
					}
				}
				if (mFile.canRead())
				{
					// 如果是文件夹，则直接进入该文件夹，查看文件目录
					manager.setFilePath(targetPath);
					mCurrentPath.setText(manager.getCurrentPath());
					mFolderAdapter.notifyDataSetChanged();
				} else
				{	// 如果该文件不可读，我们给出提示不能访问，防止用户操作系统文件造成系统崩溃等
					Toast.makeText(
							mContext,
							mContext.getString(R.string.msg_sorry_file_not_exist_permissions),
							Toast.LENGTH_SHORT).show();
				}
			} else
			{ // 非文件夹
				Toast.makeText(
						mContext,
						mContext.getString(R.string.msg_sorry_file_permissions),
						Toast.LENGTH_SHORT).show();
			}
		}
	}
}
