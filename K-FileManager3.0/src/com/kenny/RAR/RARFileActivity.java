package com.kenny.RAR;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
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
import com.kenny.KFileManager.R;
import com.kenny.file.Event.openRARFileEvent;
import com.kenny.file.bean.RARFileBean;
import com.kenny.file.sort.FileSort;

/**
 * RAR文件操作
 * 
 * @author WangMinghui
 * 
 */
public class RARFileActivity extends Activity implements OnItemClickListener,OnClickListener {
	private TextView txtTextTitle;
	private ListView lvlist;
	private String strZipPath = null;
	// private boolean mSaveFlag = false; // false: 未保存 true:保存
	private LoadData mLoadData = new LoadData();
	private HashMap<String, RARFileBean> mAllist = new HashMap<String, RARFileBean>();
	private Vector<RARFileBean> mlist = new Vector<RARFileBean>();
	private RARFileAdapter mFileAdapter;

	private class LoadData extends AsyncTask<String, Boolean, Boolean> {
		private ProgressDialog mProgressDialog = null;
		private File inFile;

		public void setPath(String path) {
			inFile = new File(path);
			LoadData.this.execute(path);
		}
		@Override
		protected void onPostExecute(Boolean result) {
			try {
				if (result) {
					mlist.clear();
					mlist.addAll(mAllist.values());
					Collections.sort(mlist, new FileSort());
					mFileAdapter.notifyDataSetChanged();
				} else {
					Toast.makeText(RARFileActivity.this, "加载失败!,请稍后在试",
							Toast.LENGTH_SHORT).show();
					finish();
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(RARFileActivity.this, "内存不足加载失败",
						Toast.LENGTH_SHORT).show();
			}
			if (mProgressDialog != null)
				mProgressDialog.dismiss();
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
			mProgressDialog = ProgressDialog.show(RARFileActivity.this, "",
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

				//mAllist.putAll(ZIP.getZipFileBeans(inFile));
				mAllist.putAll(UnRAR.getRARFileBeans(inFile));
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
		findViewById(R.id.ButtonUPZip).setOnClickListener(this);
		lvlist = (ListView) findViewById(R.id.lvlist);
		mlist.addAll(mAllist.values());
		mFileAdapter = new RARFileAdapter(this, 1, mlist);
		lvlist.setAdapter(mFileAdapter);
		lvlist.setOnItemClickListener(this);
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		RARFileBean temp = mlist.get(position);
		if (temp.isDirectory()) {
			mlist.clear();
			mlist.addAll(temp.getCollectionItem());
			mFileAdapter.notifyDataSetChanged();
			return;
		} else {
			SysEng.getInstance().addHandlerEvent(
					new openRARFileEvent(this, strZipPath, temp.getFilePath()));
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
		final ProgressDialog dialog=new ProgressDialog(RARFileActivity.this);
		dialog.setMessage("正在解压文件...");
		dialog.show();
		final String targetDir=file.getAbsolutePath()+"_uprar/";
		SysEng.getInstance().addEvent(new AbsEvent()
		{
			@Override
			public void ok()
			{
				try
				{
					UnRAR.unrar(Path, targetDir);
					SysEng.getInstance().addHandlerEvent(new AbsEvent()
					{
						@Override
						public void ok()
						{
							dialog.dismiss();
							Toast.makeText(RARFileActivity.this, "解压后的路径:"+targetDir, Toast.LENGTH_LONG).show();
						}
					});

				} catch (final Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					SysEng.getInstance().addHandlerEvent(new AbsEvent()
					{
						@Override
						public void ok()
						{
							dialog.dismiss();
							Toast.makeText(RARFileActivity.this, "解压文件失败:"+e.getMessage(), Toast.LENGTH_LONG).show();
						}
					});

				}
			}
		});
		
	}
}
