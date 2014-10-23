package com.kenny.file.dialog;

import com.kenny.KFileManager.R;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.util.Const;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class TextEncodeDialog
{

	public void ShowDialog(final Activity context, int TextMode,
			final INotifyDataSetChanged result)
	{
		AlertDialog.Builder test = new AlertDialog.Builder(context);
		test.setTitle("字符编码");

		final String[] item=context.getResources().getStringArray(R.array.TextEncode);
		test.setItems(item,
				new OnClickListener()
				{
					
					public void onClick(DialogInterface dialog, int which)
					{
						if (result != null)
						{
							result.NotifyDataSetChanged(
									Const.cmdTextEncodeDialog, item[which]);
						}
						dialog.cancel();
					}
				});
		test.setNegativeButton("取消", null);
		test.create();
		test.show();
	}
}
