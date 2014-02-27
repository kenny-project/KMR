package com.kenny.file.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.framework.syseng.SysEng;
import com.kenny.KFileManager.t.R;
import com.kenny.file.Adapter.FavorDialogAdapter;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.bean.FileBean;
import com.kenny.file.db.Dao;
import com.kenny.file.manager.FileManager;
import com.kenny.file.sort.FileSort;

public class FavoritesDialog extends Dialog implements OnClickListener,
		android.view.View.OnClickListener, OnItemClickListener {
	private ListView lvList;
	private FavorDialogAdapter mFileAdapter;;
	private ArrayList<FileBean> mFileList = new ArrayList<FileBean>();
	private Button btCancel;
	private Activity context;

	public FavoritesDialog(Activity context,int id) {
		super(context,id);
		this.context = context;
	}

	public static void ShowDialog(Activity context,int y) {
		FavoritesDialog alertDialog = new FavoritesDialog(context,R.style.NobackDialog);
		Window win = alertDialog.getWindow();
		LayoutParams params = new LayoutParams();
		//params.x = -80;//设置x坐标
		params.y = y;//设置y坐标
		win.setAttributes(params);
		alertDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
		alertDialog.show();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// Window.FEATURE_CUSTOM_TITLE
		setContentView(R.layout.alert_dialog_favorites);

		WindowManager m = context.getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值

		p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.8

		p.width = (int) (d.getWidth() * 0.93); // 宽度设置为屏幕的0.95
		getWindow().setAttributes(p); // 设置生效


		btCancel = (Button) findViewById(R.id.btCancel);
		btCancel.setOnClickListener(this);

		lvList = (ListView) findViewById(R.id.lvList);
		mFileAdapter = new FavorDialogAdapter(getContext(), 1, mFileList);
		lvList.setAdapter(mFileAdapter);
		lvList.setOnItemClickListener(this);
		FavoriteInit();
	}

	private void FavoriteInit() {
		Dao dao = Dao.getInstance(getContext());
		mFileList.clear();
		mFileList.addAll(dao.getHistoryInfos(String.valueOf(0)));
		Collections.sort(mFileList, new FileSort());
		dao.closeDb();
		if (mFileAdapter != null) {
			mFileAdapter.notifyDataSetChanged();
		}
	}

	public void onClick(DialogInterface dialog, int which) {

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btCancel:
			dismiss();
			break;
		}
	}
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		FileBean temp = mFileList.get(position);
		final File mFile = temp.getFile();
		if (!mFile.isDirectory()) {
			SysEng.getInstance().addHandlerEvent(
					new openDefFileEvent(getContext(), mFile.getPath()));
		} else {
			FileManager.getInstance().setFilePath(mFile.getPath());
		}
		dismiss();
	}
}
