package com.kenny.file.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Event.LoadSearchFileEvent;
import com.kenny.file.bean.ScarchParam;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.page.KMainPage;

public class SearchFileDialog
{
	/**
	 * 创建文件夹的方法:当用户点击软件下面的创建菜单的时候，是在当前目录下创建的一个文件夹 静态变量mCurrentFilePath存储的就是当前路径
	 * java.io.File.separator是JAVA给我们提供的一个File类中的静态成员，它会根据系统的不同来创建分隔符
	 * mNewFolderName正是我们要创建的新文件的名称，从EditText组件上得到的
	 */
	public void Show(final Activity context, final String path,
			final ScarchParam param,
			final INotifyDataSetChanged notifyDataSetChanged)
	{
		P.debug("SearchFileDialog.show");
		LayoutInflater mLI = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final LinearLayout mLL = (LinearLayout) mLI.inflate(
				R.layout.alert_dialog_search, null);

		final CheckBox cbSearchSubdirectory = (CheckBox) mLL
				.findViewById(R.id.cbSearchSubdirectory);

		final CheckBox cbSearchCaseSensitive = (CheckBox) mLL
				.findViewById(R.id.cbSearchCaseSensitive);
		final CheckBox cbSearchHide = (CheckBox) mLL
				.findViewById(R.id.cbSearchHide);

		final EditText meditText = (EditText) mLL
				.findViewById(R.id.etSearchFileName);
		meditText.setText(param.getSearchValue());
		cbSearchSubdirectory.setChecked(param.isSubdirectory());
		cbSearchCaseSensitive.setChecked(param.isCaseSensitive());
		cbSearchHide.setChecked(param.isHide());
		Builder mBuilder = new AlertDialog.Builder(context)
				.setTitle(R.string.SearchFileDialog_Title)
				.setView(mLL)
				.setPositiveButton(context.getString(R.string.search),
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog,
									int which)
							{
								String value = meditText.getText().toString();
								if (value.length() > 0)
								{
									KMainPage.mKMainPage.ChangePage(KMainPage.Search, null);
									param.setCaseSensitive(cbSearchCaseSensitive
											.isChecked());
									param.setPath(path);
									param.setHide(cbSearchHide.isChecked());
									param.setSearchValue(value);
									param.setSubdirectory(cbSearchSubdirectory
											.isChecked());
									SysEng.getInstance().addThreadEvent(
											new LoadSearchFileEvent(context,
													true, param,
													notifyDataSetChanged));
								} else
								{
									Toast.makeText(context, "请输入字符后在点击搜索!",
											Toast.LENGTH_SHORT).show();
								}
							}
						})
				.setNeutralButton(context.getString(R.string.cancel), null);
		mBuilder.show();

	}
}
