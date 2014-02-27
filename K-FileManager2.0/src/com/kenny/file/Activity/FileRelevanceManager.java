/*
Copyright 2009 David Revell

This file is part of SwiFTP.

SwiFTP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SwiFTP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.kenny.file.Activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.kenny.KFileManager.t.R;
import com.kenny.file.Adapter.FileRelevanceAdapter;
import com.kenny.file.Parser.FileTypeParser;
import com.kenny.file.bean.FileTypeBean;
import com.kenny.file.dialog.openFileRelevanceDialog;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.util.Const;
import com.kenny.file.util.Res;
import com.kenny.file.util.SDFile;
import com.umeng.analytics.MobclickAgent;

public class FileRelevanceManager extends Activity implements OnItemClickListener,
		OnClickListener, INotifyDataSetChanged {

	private ListView lvList;
	private FileRelevanceAdapter mFileAdapter;
	private ArrayList<FileTypeBean> mFGroupInfo = new ArrayList<FileTypeBean>();
	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_ico_relevance_manager);
		MobclickAgent.onEvent(this, "KMainPage", "FileIcoRelevanceManager");
		lvList = (ListView) findViewById(R.id.lvList);
		lvList.setOnItemClickListener(this);
//		mFileAdapter = new FileRelevanceAdapter(this, mFGroupInfo);
//		lvList.setAdapter(mFileAdapter);
		Init(this);
	}
	private void Init(Context context)
	{
		String Data=null;
		try 
		{
			Data = SDFile.ReadRAMFile(context, "fileType.xml");
			FileTypeParser mFavoriteParser = new FileTypeParser();
			mFGroupInfo.clear();
			ArrayList<FileTypeBean> result = mFavoriteParser.parseJokeByData(
					context, Data);
			mFGroupInfo.addAll(result);
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	/**
	 * 处理路径的点击安装
	 */
	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) 
	{
		FileTypeBean bean = (FileTypeBean) parent.getAdapter().getItem(pos);
		openFileRelevanceDialog dialog=new openFileRelevanceDialog();
		dialog.ShowDialog(this,bean, Const.getSDCard(),this);
	}

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		FileTypeParser mFavoriteParser = new FileTypeParser();
		String value=mFavoriteParser.ObjectToString(mFGroupInfo);
		SDFile.WriteRAMFile(this, value, "fileType.xml");
	}

	protected void onResume() 
	{
		super.onResume();
		MobclickAgent.onResume(this);
		for(FileTypeBean bean: mFGroupInfo)
		{
			bean.Clear();
		}
		mFileAdapter.notifyDataSetChanged();
	}

	protected void onStop() {
		super.onStop();
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btBack:
			finish();
			break;
		}
	}

	@Override
	public void NotifyDataSetChanged(int cmd, Object value) {
		// TODO Auto-generated method stub
		for(FileTypeBean bean: mFGroupInfo)
		{
			bean.Clear();
		}
		Res.getInstance(getParent()).Clear();
		mFileAdapter.notifyDataSetChanged();
	}
}
