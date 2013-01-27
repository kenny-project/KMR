package com.kenny.file.dialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kenny.KFileManager.R;
import com.kenny.file.interfaces.KActivityStatus;
import com.kuaipan.demo.SimpleCommandConsole;

/**
 * 新建文件、文件夹
 * 
 * @author kenny
 * 
 */
public class CreateKPFileDialog
{
	/**
	 * 创建文件夹的方法:当用户点击软件下面的创建菜单的时候，是在当前目录下创建的一个文件夹 静态变量mCurrentFilePath存储的就是当前路径
	 * java.io.File.separator是JAVA给我们提供的一个File类中的静态成员，它会根据系统的不同来创建分隔符
	 * mNewFolderName正是我们要创建的新文件的名称，从EditText组件上得到的
	 */
	// private static int mChecked = 1;

	public static void Show(final Context context,
			final SimpleCommandConsole cli, final KActivityStatus mStatus)
	{
		// mChecked = 1;
		LayoutInflater mLI = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final LinearLayout mLL = (LinearLayout) mLI.inflate(
				R.layout.alert_dialog_kuaipan_create, null);

		final EditText meditText = (EditText) mLL
				.findViewById(R.id.new_filename);

		Builder mBuilder = new AlertDialog.Builder(context)
				.setTitle(R.string.CreateFileDialog_title)
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
									cli.do_mkdir(mNewFolderName);
									mStatus.KActivityResult(10, 1, 1, "");
									Toast.makeText(context, "操作成功!",
											Toast.LENGTH_SHORT).show();
								} catch (Exception e)
								{
									e.printStackTrace();
								}
							}
						})
				.setNeutralButton(context.getString(R.string.cancel), null);
		mBuilder.show();

	}
}
