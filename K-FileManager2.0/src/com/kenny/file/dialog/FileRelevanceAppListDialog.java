package com.kenny.file.dialog;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kenny.KFileManager.t.R;
import com.kenny.file.Adapter.FileRelevanceAdapter;
import com.kenny.file.interfaces.INotifyDataSetChanged;

/**
 * 文件关联应用列表设置
 * 
 * @author wangminghui
 * 
 */
public class FileRelevanceAppListDialog implements OnItemClickListener, OnClickListener
{
	public static final int Finish = 100;
	private Context mContext;
	private Dialog alertDialog;
	private View alertView;
	private ListView lvFolderlist;
	private INotifyDataSetChanged notify;
	private TextView mCurrentPath;
	private List<ResolveInfo> mSrcFileList;//需要复制的路径
	private FileRelevanceAdapter mFolderAdapter;
	public void ShowDialog(Context context,int type, 
			 INotifyDataSetChanged notify)
	{
		mContext = context;
		this.notify = notify;
		String title="";
		switch(type)
		{
		case 1://文本
			title="文本关联";
			this.mSrcFileList=getTxtShareTargets();
			break;
		case 2://图片
			title="图片关联";
			this.mSrcFileList=getImgShareTargets();
			break;
		case 3://音频
			title="音频关联";
			this.mSrcFileList=getAudioShareTargets();
			break;
		case 4://视频
			title="视频关联";
			this.mSrcFileList=getVideoShareTargets();
			break;
		}

		alertDialog = new Dialog(context, R.style.NobackDialog);
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// Window.FEATURE_CUSTOM_TITLE
		LayoutInflater factory = LayoutInflater.from(context);
		alertView = factory.inflate(R.layout.alert_dialog_folder_list, null);
		alertDialog.setContentView(alertView);

		mCurrentPath = (TextView) alertView.findViewById(R.id.mCurrentPath);
		Button btOK = (Button) alertView.findViewById(R.id.btOK);
		btOK.setOnClickListener(this);
		btOK.setVisibility(View.GONE);
		Button btCancel = (Button) alertView.findViewById(R.id.btCancel);
		btCancel.setOnClickListener(this);

		lvFolderlist = (ListView) alertView.findViewById(R.id.lvFolderlist);
		mFolderAdapter = new FileRelevanceAdapter(mContext,mSrcFileList);
		mCurrentPath.setText(title);
		lvFolderlist.setAdapter(mFolderAdapter);
		lvFolderlist.setOnItemClickListener(this);
		alertDialog.show();
	}
	

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btOK:
//			if(new File(manager.getCurrentPath()).canWrite())
//			{
//				notify.NotifyDataSetChanged(Finish, manager.getCurrentPath());
//				break;
//			}
//			else
//			{
//				Toast.makeText(
//						mContext,
//						"该路径为只读路径不能操作:"+manager.getCurrentPath(),
//						Toast.LENGTH_SHORT).show();
//				return;
//			}
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
		ResolveInfo temp = mFolderAdapter.getItem(position);
		if (temp != null)
		{
			
		
		}
//			if (position == 0 && temp.getFileName().equals(".."))
//			{// 返回到上一层
//				this.manager.Back();
//				mCurrentPath.setText(manager.getCurrentPath());
//				mFolderAdapter.notifyDataSetChanged();
//				return;
//			}
//			final File mFile = temp.getFile();
//			// 如果该文件是可读的，我们进去查看文件
//			if (mFile.isDirectory())
//			{
//				String targetPath=mFile.getPath();
//				for(int i=0;i<mSrcFileList.size();i++)
//				{
//					String inPath=mSrcFileList.get(0).getFile().getPath();
//					if(targetPath.startsWith(inPath))
//					{
//						Toast.makeText(
//								mContext,
//								mContext.getString(R.string.msg_sorry_not_move_file),
//								Toast.LENGTH_SHORT).show();		
//						return;
//					}
//					if(targetPath.indexOf(inPath)==0)
//					{
//						Toast.makeText(
//								mContext,
//								mContext.getString(R.string.msg_sorry_not_move_file),
//								Toast.LENGTH_SHORT).show();		
//						return;
//					}
//				}
//				if (mFile.canRead())
//				{
//					// 如果是文件夹，则直接进入该文件夹，查看文件目录
//					manager.setFilePath(targetPath);
//					mCurrentPath.setText(manager.getCurrentPath());
//					mFolderAdapter.notifyDataSetChanged();
//				} else
//				{	// 如果该文件不可读，我们给出提示不能访问，防止用户操作系统文件造成系统崩溃等
//					Toast.makeText(
//							mContext,
//							mContext.getString(R.string.msg_sorry_file_not_exist_permissions),
//							Toast.LENGTH_SHORT).show();
//				}
//			} else
//			{ // 非文件夹
//				Toast.makeText(
//						mContext,
//						mContext.getString(R.string.msg_sorry_file_permissions),
//						Toast.LENGTH_SHORT).show();
//			}
//		}
	}
	/**
	 * 文本文件
	 * @return
	 */
	public List<ResolveInfo> getTxtShareTargets(){  
	    List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();  
	    Intent intent=new Intent();  
	    intent.addCategory(Intent.CATEGORY_DEFAULT);
	    intent.setAction(android.content.Intent.ACTION_VIEW);
	    intent.setType("text/*");  
	    PackageManager pm=mContext.getPackageManager();  
	    mApps=pm.queryIntentActivities(intent,PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);  
	    return mApps;  
	}
	/**
	 * 图片文件
	 * @return
	 */
	public List<ResolveInfo> getImgShareTargets(){  
	    List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();  
	    Intent intent=new Intent();  
	    intent.addCategory(Intent.CATEGORY_DEFAULT);  
	    intent.setAction(android.content.Intent.ACTION_VIEW);
	    intent.setType("image/*");  
	    PackageManager pm=mContext.getPackageManager();  
	    mApps=pm.queryIntentActivities(intent,PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
	    return mApps;  
	}
	/**
	 * 音频文件
	 * @return
	 */
	public List<ResolveInfo> getAudioShareTargets(){  
	    List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();  
	    Intent intent=new Intent();  
	    intent.addCategory(Intent.CATEGORY_DEFAULT);
	    intent.setAction(android.content.Intent.ACTION_VIEW);
	    //intent.setType(type);
	    intent.setType("audio/*");  
	    //intent.setData(Uri.fromFile(new File("/sdcard/a.mp3")));
	    PackageManager pm=mContext.getPackageManager();  
	    mApps=pm.queryIntentActivities(intent,PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);  
//	    for (ResolveInfo resolveInfo : mApps)
//		{
//	    	resolveInfo.
//		}
//	    ResolveInfo resolveInfo=new ResolveInfo();
//	    mApps.add(0, resolveInfo);
	    return mApps;
	}
	/**
	 * 视频文件
	 * @return
	 */
	public List<ResolveInfo> getVideoShareTargets(){  
	    List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();  
	    Intent intent=new Intent();  
	    intent.addCategory(Intent.CATEGORY_DEFAULT);
	    intent.setAction(android.content.Intent.ACTION_VIEW);
	    intent.setType("video/*");  
	    PackageManager pm=mContext.getPackageManager();  
	    mApps=pm.queryIntentActivities(intent,PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);  
	    return mApps;  
	}
}
