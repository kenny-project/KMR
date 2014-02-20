package com.kenny.file.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Event.FavoriteFileEvent;
import com.kenny.file.Event.copyFileEvent;
import com.kenny.file.Event.cutFileEvent;
import com.kenny.file.Event.delFileEvent;
import com.kenny.file.Event.openFileEvent;
import com.kenny.file.bean.FileBean;
import com.kenny.file.commui.KString;
import com.kenny.file.dialog.KDialog;
import com.kenny.file.dialog.RenameDialog;
import com.kenny.file.dialog.ZipDialog;
import com.kenny.file.page.KMainPage;
import com.kenny.file.tools.T;
import com.kenny.file.util.FT;

/**
 * 文件对话框菜单
 * 
 * @author WangMinghui
 * 
 */
public class PopLocalMenu extends PopMenu
{

	private int nFlag;

	/**
	 * 长按文件或文件夹时弹出的带ListView效果的功能菜单 nFlag: 0:local,1:favor 2:kuaipan
	 * */
	public void ShowFile(final Context context, final FileBean file, int nFlag)
	{
		this.context = context;
		this.file = file;
		this.nFlag = nFlag;
		if (file.isDirectory())
		{
			ShowFile(context, 0, context.getString(R.string.msg_please_operate));
		} else
		{
			String fileEnds = FT.getExName(file.getFileName());
			if (fileEnds.equals("jpg") || fileEnds.equals("gif")
					|| fileEnds.equals("png") || fileEnds.equals("jpeg")
					|| fileEnds.equals("bmp"))
			{
				ShowFile(context, 3,
						context.getString(R.string.msg_please_operate));
			} else if (fileEnds.equals("mp3"))
			{
				ShowFile(context, 1,
						context.getString(R.string.msg_please_operate));
			} else
			{
				ShowFile(context, -1,
						context.getString(R.string.msg_please_operate));
			}
		}
	}

	private void deletefile(final Context context, final FileBean file)
	{
		new AlertDialog.Builder(context)
				.setTitle(context.getString(R.string.msg_dialog_info_title))
				.setMessage(context.getString(R.string.msg_delselectfile))
				.setPositiveButton(context.getString(R.string.ok),
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog,
									int which)
							{
								SysEng.getInstance().addEvent(
										new delFileEvent(context, file));
							}
						})
				.setNegativeButton(context.getString(R.string.cancel), null)
				.show();
	}

	/** 长按文件或文件夹时弹出的带ListView效果的功能菜单 */
	private Context context;
	private FileBean file;

	@Override
	protected void OnItemClickListener(int pos, KString key)
	{
		switch (key.getKey())
		{
		case open:
			if (file.getFile().canRead())
			{
				SysEng.getInstance().addHandlerEvent(
						new openFileEvent(context, file.getFilePath()));
			} else
			{
				Toast.makeText(context,
						context.getString(R.string.msg_can_not_operated),
						Toast.LENGTH_SHORT).show();
			}
			break;
		case copy:
			if (file.getFile().canRead())
			{
				SysEng.getInstance().addHandlerEvent(
						new copyFileEvent(context, file));
			} else
			{
				Toast.makeText(context,
						context.getString(R.string.msg_can_not_operated),
						Toast.LENGTH_SHORT).show();
			}
			break;
		case cut:
			if (file.getFile().canRead())
			{
				SysEng.getInstance().addHandlerEvent(
						new cutFileEvent(context, file));

			} else
			{
				Toast.makeText(context,
						context.getString(R.string.msg_can_not_operated),
						Toast.LENGTH_SHORT).show();
			}
			break;
		case rename:
			if (file.getFile().canWrite())
			{
				RenameDialog.Show(context, file.getFile());
			} else
			{
				Toast.makeText(context,
						context.getString(R.string.msg_can_not_operated),
						Toast.LENGTH_SHORT).show();
			}
			break;
		case delete:
			// if (file.getFile().canWrite())
			// {
			deletefile(context, file);
			// }
			// else
			// {
			// Toast.makeText(context,
			// context.getString(R.string.msg_can_not_operated),
			// Toast.LENGTH_SHORT).show();
			// }
			break;
		case favor:
			SysEng.getInstance().addEvent(
					new FavoriteFileEvent(context, file, 0));
			break;
		case zip:
			if (file.getFile().canRead())
			{
				new ZipDialog().Show(context, file.getFile());
			}
			else
			{
				Toast.makeText(context,
						context.getString(R.string.msg_can_not_operated),
						Toast.LENGTH_SHORT).show();
			}
			break;
		case attribute:
			KDialog.ShowDetailsDialog(context, file.getFilePath());
			break;
		case send:
			T.ShareIntent(context, context.getString(R.string.msg_Send),
					file.getFilePath());
			break;
		case openfolder:
			break;
		case setring:
			// T.setMyRingtone(context,file.getFilePath());
			T.settingRingertone(context, file.getFilePath());
			break;
		case setdesk:// 未实现
			if (file.getFile().canRead())
			{
				SysEng.getInstance().addHandlerEvent(
						new openFileEvent(context, file.getFilePath()));
			} else
			{
				Toast.makeText(context,
						context.getString(R.string.msg_can_not_operated),
						Toast.LENGTH_SHORT).show();
			}
			break;
		case uploadcloud:
			if (file.getFile().canRead())
			{
				List<FileBean> list = new ArrayList<FileBean>();
				list.add(file);
				HashMap<String, Object> intent = new HashMap<String, Object>();
				intent.put("type", 1);// 1:上传文件
				intent.put("list", list);
				KMainPage.mKMainPage.ChangePage(KMainPage.NetWork, intent);
			} else
			{
				Toast.makeText(context,
						context.getString(R.string.msg_can_not_operated),
						Toast.LENGTH_SHORT).show();
			}
			break;
		// int result = T.startWallpaper(context, file.getFilePath());
		// if (result == 1)
		// {
		// Toast.makeText(context, "设置成功", Toast.LENGTH_SHORT).show();
		// }
		// else
		// {
		// Toast.makeText(context, "设置失败", Toast.LENGTH_SHORT).show();
		// }
		// break;
		}

	}
}
