package com.kenny.Zip;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.event.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.t.R;
import com.kenny.file.Event.openZipFileEvent;
import com.kenny.file.bean.ZipFileBean;
import com.kenny.file.sort.FileSort;

/**
 * ZIP文件操作
 * 
 * @author WangMinghui
 * 
 */
public class ZIPFileActivity extends Activity implements OnItemClickListener, OnClickListener {
	private TextView txtTextTitle;
	private ListView lvlist;
	private String strZipPath = null;
	private LoadData mLoadData = new LoadData();
	private HashMap<String, ZipFileBean> mAllist = new HashMap<String, ZipFileBean>();
	private Vector<ZipFileBean> mlist = new Vector<ZipFileBean>();
	private ZipFileAdapter mFileAdapter;

	private class LoadData extends AsyncTask<String, Boolean, Boolean> 
	{
		private ProgressDialog mProgressDialog = null;
		private File inFile;
		public void setPath(String path) {
			inFile = new File(path);
			LoadData.this.execute(path);
		}
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			if (mProgressDialog != null)
				mProgressDialog.dismiss();
			try {
				if (result) {
					mlist.clear();
					mlist.addAll(mAllist.values());
					Collections.sort(mlist, new FileSort());
					mFileAdapter.notifyDataSetChanged();
				} else {
					Toast.makeText(ZIPFileActivity.this, "加载失败!,请稍后在试",
							Toast.LENGTH_SHORT).show();
					finish();
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(ZIPFileActivity.this, "加载失败:"+e.getMessage(),
						Toast.LENGTH_SHORT).show();
			}

			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			Long len = inFile.length() / 1024;
			if (len > 16) {
				ShowDialog(len);
			}
			super.onPreExecute();
		}

		private void ShowDialog(Long count) {
			if (count < 1) {
				count = 1l;
			}
			mProgressDialog = ProgressDialog.show(ZIPFileActivity.this, "",
					"正在加载数据...", true, true);
//			mProgressDialog.setCancelable(false);
			mProgressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface arg0) {
					// TODO Auto-generated method stub
					// LoadData.this.
				}
			});
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {

				mAllist.putAll(ZIP.getZipFileBeans(inFile));
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zf_manager);
		initContentView();
		Intent intent = getIntent();
		Uri uri = (Uri) intent.getData();
		strZipPath = null;
		if (uri != null) {
			strZipPath = uri.getPath();
		}
		if (strZipPath != null) {
			mLoadData.setPath(strZipPath);
			// //将字节流转换为String,操作String就好。
			txtTextTitle.setText(strZipPath);
		} else {
			txtTextTitle.setText("未找到相应的文件");
		}
		
	}

	/** 初始化 */
	private void initContentView() {
		txtTextTitle = (TextView) findViewById(R.id.TextViewTitle);
		lvlist = (ListView) findViewById(R.id.lvlist);
		
		findViewById(R.id.ButtonUPZip).setOnClickListener(this);
		mlist.addAll(mAllist.values());
		mFileAdapter = new ZipFileAdapter(this, 1, mlist);
		lvlist.setAdapter(mFileAdapter);
		lvlist.setOnItemClickListener(this);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ZipFileBean temp = mlist.get(position);
		if (temp.isDirectory()) {
			mlist.clear();
			mlist.addAll(temp.getCollectionItem());
			mFileAdapter.notifyDataSetChanged();
			return;
		} else {
			 SysEng.getInstance().addHandlerEvent(
			 new openZipFileEvent(this, strZipPath, temp
			 .getFilePath()));
//			SysEng.getInstance().addEvent(
//					new openZipFileEvent(this, strZipPath, strZipPath
//							.substring(0, strZipPath.length() - 4)
//							+ File.separator, temp.getFilePath()));
		}
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.ButtonUPZip:
			UPZipFile(strZipPath);
			break;
		}
	}
	public void UPZipFile(final String Path)
	{
		File file= new File(strZipPath);
		final ProgressDialog dialog=new ProgressDialog(ZIPFileActivity.this);
		dialog.setMessage("正在解压文件...");
		dialog.show();
		final String targetDir=file.getAbsolutePath()+"_upzip/";
		SysEng.getInstance().addEvent(new AbsEvent()
		{
			@Override
			public void ok()
			{
				// TODO Auto-generated method stub
				ZIP.Unzip(Path, targetDir);
				SysEng.getInstance().addHandlerEvent(new AbsEvent()
				{
					@Override
					public void ok()
					{
						dialog.dismiss();
						Toast.makeText(ZIPFileActivity.this, "解压后的路径:"+targetDir, Toast.LENGTH_LONG).show();
					}
				});
			}
		});
		
	}
}
