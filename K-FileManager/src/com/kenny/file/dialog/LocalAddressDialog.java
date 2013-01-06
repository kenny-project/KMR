package com.kenny.file.dialog;

import java.io.File;
import java.util.ArrayList;

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
import com.kenny.KFileManager.R;
import com.kenny.file.Adapter.FavorFileAdapter;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.bean.FavorFileBean;
import com.kenny.file.bean.FileBean;
import com.kenny.file.db.Dao;
import com.kenny.file.tools.SaveData;
import com.kenny.file.util.FileManager;

public class LocalAddressDialog extends Dialog implements OnClickListener,
		android.view.View.OnClickListener, OnItemClickListener {
	private ListView lvList;
	private FavorFileAdapter mFileAdapter;;
	private ArrayList<FileBean> mFileList = new ArrayList<FileBean>();
	private Button btFavorite, btHistory, btClearData, btDeleteData;
	private int nFlag;
	private Activity context;
	private String path;//当前路径地址
	public LocalAddressDialog(Activity context) {
		super(context);
		this.context = context;
	}

	public static void ShowDialog(Activity context,String path) {
		LocalAddressDialog alertDialog = new LocalAddressDialog(context);
		alertDialog.path=path;
		alertDialog.show();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// Window.FEATURE_CUSTOM_TITLE
		setContentView(R.layout.alert_dialog_address);

		WindowManager m = context.getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值

		p.height = (int) (d.getHeight() * 0.8); // 高度设置为屏幕的0.8

		p.width = (int) (d.getWidth() * 0.95); // 宽度设置为屏幕的0.95
		getWindow().setAttributes(p); // 设置生效

		btHistory = (Button) findViewById(R.id.btHistory);
		btHistory.setOnClickListener(this);
		btFavorite = (Button) findViewById(R.id.btFavorite);
		btFavorite.setOnClickListener(this);

		btClearData = (Button) findViewById(R.id.btClearData);
		btClearData.setOnClickListener(this);
		btDeleteData = (Button) findViewById(R.id.btDeleteData);
		btDeleteData.setOnClickListener(this);

		lvList = (ListView) findViewById(R.id.lvList);
		mFileAdapter = new FavorFileAdapter(getContext(), 1, mFileList);
		lvList.setAdapter(mFileAdapter);
		lvList.setOnItemClickListener(this);
		nFlag = SaveData.Read(getContext(), "LocalAddressDialog_flag", 0);
		this.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				SaveData.Write(getContext(), "LocalAddressDialog_flag", nFlag);
			}
		});
		FavoriteInit(nFlag);
	}

	private void FavoriteInit(int flag) {
		nFlag = flag;
		Dao dao = Dao.getInstance(getContext());
		mFileList.clear();
		mFileList.addAll(dao.getHistoryInfos(String.valueOf(nFlag)));
		dao.closeDb();

		if (nFlag == 1) {
			btFavorite.setBackgroundResource(R.drawable.tab2_left_unselect);
			btHistory.setBackgroundResource(R.drawable.tab2_right_select);
			btDeleteData.setVisibility(View.GONE);
			btClearData.setVisibility(View.VISIBLE);
		} else if (nFlag == 0) {
			btFavorite.setBackgroundResource(R.drawable.tab2_left_select);
			btHistory.setBackgroundResource(R.drawable.tab2_right_unselect);
			btDeleteData.setVisibility(View.VISIBLE);
			btClearData.setVisibility(View.GONE);
		}

		if (mFileAdapter != null) {
			mFileAdapter.notifyDataSetChanged();
		}
	}

	public void onClick(DialogInterface dialog, int which) {

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btFavorite:
			FavoriteInit(0);
			break;
		case R.id.btHistory:
			FavoriteInit(1);
			break;
		case R.id.btClearData:
			ClearData();
			break;
		case R.id.btDeleteData:
			DeleteData();
			break;
		}
	}

	public void DeleteData() {
		Dao dao = Dao.getInstance(getContext());
		for (int i = 0; i < mFileList.size(); i++) {
			FavorFileBean tmpInfo = (FavorFileBean) mFileList.get(i);
			if (tmpInfo.isChecked()) {
				mFileList.remove(i);
				dao.deleteHistory(tmpInfo.getId());
			}
		}
		dao.closeDb();
		mFileAdapter.notifyDataSetChanged();
		return;
	}

	public void ClearData() {
		Dao dao = Dao.getInstance(getContext());
		dao.ClearHistory(nFlag);
		dao.closeDb();
		FavoriteInit(nFlag);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		FileBean temp = mFileList.get(position);
		final File mFile = temp.getFile();
		if (!mFile.isDirectory()) {
			SysEng.getInstance().addHandlerEvent(
					new openDefFileEvent(getContext(), mFile.getPath()));
		} else {
			FileManager.GetHandler().setFilePath(mFile.getPath());
		}
		SaveData.Write(getContext(), "LocalAddressDialog_flag", nFlag);
		dismiss();
	}
}
