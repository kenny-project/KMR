package com.baiduyun.client;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.pcs.BaiduPCSActionInfo;
import com.framework.event.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.interfaces.KActivityStatus;

/**
 * 新建文件、文件夹
 * 
 * @author kenny
 * 
 */
public class CreateBaiduFileDialog implements INotifyDataSetChanged
{
	/**
	 * 创建文件夹的方法:当用户点击软件下面的创建菜单的时候，是在当前目录下创建的一个文件夹 静态变量mCurrentFilePath存储的就是当前路径
	 * java.io.File.separator是JAVA给我们提供的一个File类中的静态成员，它会根据系统的不同来创建分隔符
	 * mNewFolderName正是我们要创建的新文件的名称，从EditText组件上得到的
	 */
	// private static int mChecked = 1;
	private Context context;
	private KActivityStatus mStatus;

	public void Show(final Context context, final BaiduCommandConsole cli,
			final KActivityStatus mStatus)
	{
		// mChecked = 1;
		this.context = context;
		this.mStatus = mStatus;
		LayoutInflater mLI = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final LinearLayout mLL = (LinearLayout) mLI.inflate(
				R.layout.alert_dialog_kuaipan_create, null);

		final EditText meditText = (EditText) mLL
				.findViewById(R.id.new_filename);

		Builder mBuilder = new AlertDialog.Builder(context)
				.setTitle(R.string.CreateKPFileDialog_title)
				.setView(mLL)
				.setPositiveButton(R.string.m_New,
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog,
									int which)
							{
								String mNewFolderName = meditText.getText()
										.toString().trim();
								try
								{
									boolean result = cli.do_mkdir(
											mNewFolderName,
											CreateBaiduFileDialog.this);
									if(!result)
									{
										Toast.makeText(context, "创建失败!", Toast.LENGTH_SHORT)
										.show();
									}

								} catch (Exception e)
								{
									e.printStackTrace();
								}
							}
						})
				.setNeutralButton(context.getString(R.string.cancel), null);
		mBuilder.show();

	}

	@Override
	public void NotifyDataSetChanged(int cmd, Object obj)
	{
		// TODO Auto-generated method stub
		if (cmd == BaiduCommandConsole.BAIDU_COMMAND_MIDIR)
		{
			final BaiduPCSActionInfo.PCSFileInfoResponse value = (BaiduPCSActionInfo.PCSFileInfoResponse) obj;
			SysEng.getInstance().addHandlerEvent(new AbsEvent()
			{
				@Override
				public void ok()
				{
					if (value.status.errorCode == 0)
					{
						mStatus.KActivityResult(10, 1, 1, "");
						Toast.makeText(context, "创建成功!", Toast.LENGTH_SHORT)
								.show();
					} else
					{
						Toast.makeText(context, "创建失败:"+value.status.message, Toast.LENGTH_SHORT)
								.show();
					}
					// Toast.makeText(
					// context,
					// "Mkdir:  " + ret.status.errorCode + "   "
					// + ret.status.message, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
}
