package com.kenny.file.dialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kenny.KFileManager.t.R;
import com.kenny.Zip.ZIP;
import com.kenny.file.manager.FileManager;
import com.kenny.file.util.Const;

public class ZipDialog
{
	private final String TAG = "ZipDialogEvent";
	private static Collection<File> resFileList = new ArrayList<File>();

	/** 调用弹出重命名框的方法 */
	public void Show(final Context context, final File file)
	{
		LayoutInflater mLI = LayoutInflater.from(context);
		LinearLayout mLL = (LinearLayout) mLI.inflate(R.layout.rename_dialog,
				null);
		final EditText mET = (EditText) mLL.findViewById(R.id.new_filename);
		mET.setText(file.getName() + ".zip");

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				String modifyName = mET.getText().toString();
				final String modifyFilePath = file.getParentFile().getPath()
						+ java.io.File.separator;
				final String newFilePath = modifyFilePath + modifyName;
				// 判断该新的文件名是否已经在当前目录下存在
				if (new File(newFilePath).exists())
				{
					Toast.makeText(context, "该文件名已经存在", Toast.LENGTH_SHORT)
							.show();
				} else
				{ // 文件名不重复时直接修改文件名后再次刷新列表
					// file.renameTo(new File(newFilePath));11
					// Palse(file);
					try
					{
						ZIP.zipFile(file, new File(newFilePath), Const.AppName);
						FileManager.getInstance().Refresh();
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(context, "压缩文件失败", Toast.LENGTH_SHORT)
								.show();
					}
				}
			}

		};
		AlertDialog renameDialog = new AlertDialog.Builder(context).create();
		renameDialog.setTitle(R.string.ZipDialog_Title);
		renameDialog.setView(mLL);
		renameDialog.setButton("确定", listener);
		renameDialog.setButton2("取消", new DialogInterface.OnClickListener()
		{

			public void onClick(DialogInterface dialog, int which)
			{

			}
		});
		renameDialog.show();
	}

	/**
	 * 粘贴
	 * 
	 * @param mSaveFilePaths
	 */
	private void Palse(File file)
	{
		if (file.isFile())
		{ // 文件处理
			PalseFile(file);
		} else
		{ // 文件夹处理
			PalseFolder(file);
		}
	}

	/**
	 * 粘贴文件
	 * 
	 * @param path
	 * @param file
	 */
	private void PalseFolder(final File file)
	{
		File[] fileArray = file.listFiles();
		resFileList.add(file);
		for (File currentFile : fileArray)
		{// 遍历该目录
			if (currentFile.isFile())
			{ // 文件则直接粘贴
				PalseFile(currentFile);
			} else
			{
				PalseFolder(currentFile);// 回调
			}
		}
	}

	/**
	 * 粘贴文件
	 * 
	 * @param path
	 *            绝结路径
	 * @param file
	 */
	private void PalseFile(File file)
	{
		resFileList.add(file);
	}
}
