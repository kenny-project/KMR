package com.kenny.file.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import android.widget.TextView;

import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Adapter.AddressAdapter;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.bean.FileBean;
import com.kenny.file.db.Dao;
import com.kenny.file.manager.FileManager;
import com.kenny.file.manager.IManager;

public class LocalAddressDialog extends Dialog implements OnClickListener,
		android.view.View.OnClickListener, OnItemClickListener {
	private ListView lvList;
	private ArrayList<FileBean> mFileList = new ArrayList<FileBean>();
	private Button btHistory, btClearData;
	private Activity context;
	private TextView tvTitle;
	private String path;//当前路径地址
	private IManager iManager;
	private LocalAddressDialog(Activity context,int id) {
		super(context,id);
		this.context = context;
	}

	public static void ShowDialog(Activity context,String path,IManager iManager) {
		LocalAddressDialog alertDialog = new LocalAddressDialog(context,R.style.NobackDialog);
		alertDialog.path=path;
		alertDialog.iManager=iManager;
//		Window win = alertDialog.getWindow();
//		LayoutParams params = new LayoutParams();
//		params.y = y;//设置y坐标
//		win.setAttributes(params);
		alertDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
		alertDialog.show();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// Window.FEATURE_CUSTOM_TITLE
		setContentView(R.layout.alert_dialog_address);

		WindowManager m = context.getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值

		p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.8

		p.width = (int) (d.getWidth() * 0.95); // 宽度设置为屏幕的0.95
		getWindow().setAttributes(p); // 设置生效

		btHistory = (Button) findViewById(R.id.btHistory);
		btHistory.setOnClickListener(this);
		btClearData = (Button) findViewById(R.id.btClearData);
		
		btClearData.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		
		lvList = (ListView) findViewById(R.id.lvList);
		lvList.setOnItemClickListener(this);
		FavoriteInit(0);
	}

	private void FavoriteInit(int nFlag) {
		Dao dao = Dao.getInstance(getContext());
		mFileList.clear();
		mFileList.addAll(dao.getHistoryInfos(String.valueOf(nFlag)));
		dao.closeDb();

		if (nFlag == 1) 
		{
			tvTitle.setText(R.string.AddressDialog_Title_Latebrowse);
			btClearData.setVisibility(View.VISIBLE);
			btHistory.setVisibility(View.GONE);
			AddressAdapter mFileAdapter = new AddressAdapter(getContext(), 1, mFileList);
			lvList.setAdapter(mFileAdapter);
		}
		else if (nFlag == 0) 
		{
			tvTitle.setText(R.string.AddressDialog_Title_Address);
			btClearData.setVisibility(View.GONE);
			List<FileBean> mFileList = AddressList(path);
			AddressAdapter tempAdapter = new AddressAdapter(getContext(),
					1, mFileList,R.layout.listitem_address_item);
			lvList.setAdapter(tempAdapter);
			lvList.setVisibility(View.VISIBLE);
		}
	}

	public void onClick(DialogInterface dialog, int which) {

	}

	public void onClick(View v) {
		switch (v.getId()) 
		{
		case R.id.btHistory:
			FavoriteInit(1);
			break;
		case R.id.btClearData:
			ClearData();
			break;
		}
	}

	public void ClearData() {
		Dao dao = Dao.getInstance(getContext());
		dao.ClearHistory(1);
		dao.closeDb();
		FavoriteInit(1);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		FileBean temp=(FileBean)parent.getAdapter().getItem(position);
		final File mFile = temp.getFile();
		if (!mFile.isDirectory()) {
			SysEng.getInstance().addHandlerEvent(
					new openDefFileEvent(getContext(), mFile.getPath()));
		} else {
			if(iManager!=null)
				iManager.setFilePath(mFile.getPath());
		}
		dismiss();
	}
	/**
	 * 返回该路径对应的选择列表
	 * 
	 * @param path
	 *            路径
	 * @return
	 */
	public List<FileBean> AddressList(String path) {
		ArrayList<FileBean> list = new ArrayList<FileBean>();
		File file = new File(path);;
		do {
			FileBean bean = new FileBean(file, file.getName(),
					file.getPath(), false);
			bean.setDirectory(file.isDirectory());
//			if (file.isDirectory()) {
//				String[] temp = file.list();
//				if (temp != null) {
//					bean.setItemCount(temp.length);
//				}
//			}
//			bean.setLength(file.length());
			list.add(bean);
			file=file.getParentFile();
		} while (file!=null);
		return list;
	}
}
