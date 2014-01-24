package com.kenny.file.dialog;

import java.io.File;
import java.util.ArrayList;
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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.KFileManager.R.color;
import com.kenny.file.Adapter.FileAdapter;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.bean.FileBean;
import com.kenny.file.commui.ListHeaderView;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.manager.SpecifyManager;
import com.kenny.file.util.Const;
import com.kenny.file.util.Theme;

/**
 * 选择要选打开的文件
 * 
 * @author wangminghui
 * 
 */
public class openFileSelectDialog implements OnItemClickListener,
		OnClickListener, INotifyDataSetChanged
{
	public static final int SelectList = 1303;//返回选中的文件列表
	private Context mContext;
	private Dialog alertDialog;
	private View alertView;
	private ListView m_locallist;
	private SpecifyManager localManage;
	private FileAdapter fileAdapter;
	private INotifyDataSetChanged notify;
	private TextView mCurrentPath;

	public Context getContext()
	{
		return mContext;
	}

	public void ShowDialog(Context context, String path,
			INotifyDataSetChanged notify)
	{
		mContext = context;
		this.notify = notify;
		localManage = new SpecifyManager(path);

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

		m_locallist = (ListView) alertView.findViewById(R.id.lvFolderlist);
		// fileAdapter = new FileAdapter(mContext, localManage.getFileList());
		fileAdapter = new FileAdapter(getContext(), 1,
				localManage.getFileList(), this);
		mCurrentPath.setText(localManage.getCurrentPath());
		m_locallist.setAdapter(fileAdapter);
		m_locallist.setOnScrollListener(m_localOnScrollListener);
		m_locallist.setOnItemClickListener(this);
		TextView tview = new TextView(getContext());
		tview.setHeight(150);
		tview.setWidth(-1);
		tview.setVisibility(View.VISIBLE);
		tview.setBackgroundColor(color.green);
		ListHeaderView headerView = new ListHeaderView(getContext(), tview);
		m_locallist.addFooterView(headerView, null, false);
		localManage.setNotifyData(this);

		localManage.setFilePath(localManage.getCurrentPath(),
				Const.cmd_Local_List_Go);
		SwitchStyle(Theme.getStyleMode());

		WindowManager m = alertDialog.getWindow().getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		LayoutParams p = alertDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的1.0
		p.width = (int) (d.getWidth() * 1); // 宽度设置为屏幕的0.8
		//d.getSize(outSize)
		p.alpha = 1.0f; // 设置本身透明度
		p.dimAmount = 0.0f; // 设置黑暗度

		alertDialog.getWindow().setAttributes(p); // 设置生效
		alertDialog.getWindow().setGravity(Gravity.CENTER); // 设置靠右对齐
		alertDialog.show();
	}

	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btOK:
			List<FileBean> mResultFileList=getSelectFiles();
			if (mResultFileList.size()>0)
			{
				notify.NotifyDataSetChanged(SelectList,
						mResultFileList);
				break;
			}
			else
			{
				Toast.makeText(mContext,
						"没有选择任何文件,请选择相应的文件",
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

	private List<FileBean> getSelectFiles()
	{
		List<FileBean> mResultFileList =new ArrayList<FileBean>();
		List<FileBean> mLocalFileList = localManage.getFileList();
		for (int i = 1; i < mLocalFileList.size(); i++)
		{
			FileBean tmpInfo = mLocalFileList.get(i);
			if(tmpInfo.isChecked())
			{
				mResultFileList.add(tmpInfo);
			}
		}
		return mResultFileList;
	}

	private boolean Back()
	{

		boolean result = localManage.Back();
		if (!result)
		{
			Toast.makeText(getContext(), "已经到根目录", Toast.LENGTH_SHORT).show();
		}
		return result;
	}

	/**
	 * 点击激活相应的窗体
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		FileBean temp = fileAdapter.getItem(position);
		if (temp != null)
		{
			if (position == 0 && temp.getFileName().equals(".."))
			{// 返回到上一层
				Back();
				return;
			}
			if (fileAdapter.isSelected())
			{
				fileAdapter.setChecked(temp);
				fileAdapter.notifyDataSetChanged();
				return;
			}
			final File mFile = temp.getFile();
			// 如果该文件是可读的，我们进去查看文件
			if (mFile.isDirectory())
			{
				if (mFile.canRead())
				{
					// 如果是文件夹，则直接进入该文件夹，查看文件目录
					localManage.setFilePath(mFile.getPath(),
							Const.cmd_Local_List_Go);
				} else
				{// 如果该文件不可读，我们给出提示不能访问，防止用户操作系统文件造成系统崩溃等
					Toast.makeText(
							getContext(),
							getContext()
									.getString(
											R.string.msg_sorry_file_not_exist_permissions),
							Toast.LENGTH_SHORT).show();
				}
			} else
			{
				if (mFile.canRead())
				{
					if (!mFile.exists())
					{
						Toast.makeText(
								getContext(),
								getContext().getString(
										R.string.msg_not_find_file),
								Toast.LENGTH_SHORT).show();
						return;
					}
					SysEng.getInstance()
							.addHandlerEvent(
									new openDefFileEvent(getContext(), mFile
											.getPath()));
				} else
				{// 如果该文件不可读，我们给出提示不能访问，防止用户操作系统文件造成系统崩溃等
					Toast.makeText(
							getContext(),
							getContext().getString(
									R.string.msg_sorry_file_permissions),
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	private OnScrollListener m_localOnScrollListener = new OnScrollListener()
	{

		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			switch (scrollState)
			{
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
				P.debug("SCROLL_STATE_FLING");
				if (fileAdapter != null)
					fileAdapter.setShowLogo(false);
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
				P.debug("SCROLL_STATE_IDLE");
				if (fileAdapter != null)
				{
					fileAdapter.setShowLogo(true);
					fileAdapter.notifyDataSetChanged();
				}
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				P.debug("SCROLL_STATE_TOUCH_SCROLL");
				if (fileAdapter != null)
					fileAdapter.setShowLogo(false);
				break;
			default:
				break;
			}
		}

		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount)
		{

		}
	};

	/**
	 * 切换窗体视图 false:ListView true:GridView
	 */
	private void SwitchStyle(int nStyle)
	{
		fileAdapter = new FileAdapter(getContext(), 1,
				localManage.getFileList(), this);
		m_locallist.setAdapter(fileAdapter);
	}

	public void NotifyDataSetChanged(int cmd, Object value)
	{
		switch (cmd)
		{
		case Const.cmd_DelFileEvent_Finish:
			localManage.setFilePath(localManage.getCurrentPath(),
					Const.cmd_Local_List_Go);
			fileAdapter.notifyDataSetChanged();
			break;
		case Const.cmd_Local_ListSort_Finish:
			// nSortMode = Theme.getSortMode();
			localManage.setFilePath(localManage.getCurrentPath(),
					Const.cmd_Local_List_Go);
			SwitchStyle(Theme.getStyleMode());
			fileAdapter.Clear();
			break;
		case Const.cmd_Local_List_Refresh:// 更新列表
			fileAdapter.notifyDataSetChanged();
			fileAdapter.Clear();
			break;
		case Const.cmd_Local_List_Go: // 新数据
			// mPath.setText(localManage.getCurrentPath());
			fileAdapter.Clear();
			fileAdapter.notifyDataSetChanged();
			break;
		}
	}
}
