package com.kenny.file.dialog;

import com.kenny.file.interfaces.INotifyDataSetChanged;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * 文件类型
 * 
 * @author WangMinghui
 * 
 */
public class SimpleArrayDialog
{

	public void ShowDialog(final Context context, String Title,
			final CharSequence[] items,CharSequence cancelName, OnClickListener listener, final INotifyDataSetChanged result)
	{
		AlertDialog.Builder test = new AlertDialog.Builder(context);
		test.setTitle(Title);
		// final String[]
		// item=context.getResources().getStringArray(R.array.TextEncode);
		test.setItems(items, new OnClickListener()
		{
			
			public void onClick(DialogInterface dialog, int which)
			{
				if (result != null)
				{
					result.NotifyDataSetChanged(which, items[which]);
				}
				dialog.cancel();
			}
		});
		test.setNegativeButton(cancelName, listener);
		test.create();
		test.show();
	}
}
