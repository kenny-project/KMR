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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.event.AbsEvent;
import com.framework.event.ParamEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.t.R;
import com.kenny.file.Adapter.FavorFileAdapter;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.bean.FGroupInfo;
import com.kenny.file.bean.FavorFileBean;
import com.kenny.file.bean.FileBean;
import com.kenny.file.bean.FileEnd;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.util.Const;
import com.umeng.analytics.MobclickAgent;

public class ApkManager extends Activity implements OnItemClickListener,
		OnClickListener, INotifyDataSetChanged {

	private ListView lvList;
	private TextView tvScan;// 搜索路径
	private CheckBox cbAllChecked;// 是否全选
	private LoadSDFileEvent event;
	private ArrayList<FileBean> mFileList = new ArrayList<FileBean>();
	private FavorFileAdapter mFileAdapter;

	/** Called with the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.apk_manager);
		MobclickAgent.onEvent(this, "KMainPage", "ApkManager");
		lvList = (ListView) findViewById(R.id.lvList);
		lvList.setOnItemClickListener(this);
		mFileAdapter = new FavorFileAdapter(this, 1, mFileList);
		lvList.setAdapter(mFileAdapter);
		tvScan = (TextView) findViewById(R.id.tvScan);
		cbAllChecked = (CheckBox) findViewById(R.id.cbAllChecked);
		cbAllChecked.setOnCheckedChangeListener(cbAllCheckChangeListener);
		ImageButton ibButton = (ImageButton) findViewById(R.id.ibFilter);
		ibButton.setOnClickListener(this);
		Button btButton = (Button) findViewById(R.id.btBack);
		btButton.setOnClickListener(this);
		btButton = (Button) findViewById(R.id.btInstall);
		btButton.setOnClickListener(this);
		btButton = (Button) findViewById(R.id.btScan);
		btButton.setOnClickListener(this);
		btButton = (Button) findViewById(R.id.btClear);
		btButton.setOnClickListener(this);
		btButton = (Button) findViewById(R.id.btArrange);
		btButton.setOnClickListener(this);
		btButton = (Button) findViewById(R.id.btCancelScan);
		btButton.setOnClickListener(this);
		FGroupInfo mFGroupInfo = new FGroupInfo();
		mFGroupInfo.setEnds("apk");
		event = new LoadSDFileEvent(this, mFGroupInfo);
		event.Start();
	}
	
	OnCheckedChangeListener cbAllCheckChangeListener=new OnCheckedChangeListener()
	{

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {

			for(FileBean bean:mFileList)
			{
				bean.setChecked(isChecked);
			}
		}
	};
	/**
	 * 处理路径的点击安装
	 */
	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
		FileBean bean = (FileBean) parent.getAdapter().getItem(pos);
		SysEng.getInstance().addHandlerEvent(
				new openDefFileEvent(this, bean.getFilePath()));
	}

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
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
		case R.id.ibFilter:
			break;
		case R.id.btBack:
			event.Cancel();
			finish();
			break;
		case R.id.btInstall:

			break;
		case R.id.btScan:
			event.Start();
			break;
		case R.id.btClear:
			break;
		case R.id.btArrange:
			break;
		case R.id.btCancelScan:
			event.Cancel();
			break;
		}
	}

	/**
	 * @author wangminghui 初始化event
	 * */
	public class LoadSDFileEvent extends AbsEvent implements OnCancelListener {

		private Activity act;
		private boolean mProgress = false;

		private FGroupInfo mFGroupInfo;
		private ArrayList<FileEnd> listItem = new ArrayList<FileEnd>();
		private LoadSDFile_State mLFstate = new LoadSDFile_State();

		public LoadSDFileEvent(Activity act, FGroupInfo mFGroupInfo) {
			this.mFGroupInfo = mFGroupInfo;
			this.act = act;
			mProgress = true;
		}

		private void Init() {
			listItem.clear();
			FGroupInfo info = mFGroupInfo;
			String[] ends = info.getEnds().split("\\|");
			for (String end : ends) {
				if (end.length() > 1) {
					listItem.add(new FileEnd(end.toLowerCase(), info.getId(),
							info.getMinSize()));
				}
			}
		}

		public void Start() {
			mProgress = true;
			SysEng.getInstance().addEvent(event);
		}

		public void Cancel() {
			mProgress = false;
		}

		private int num = 0;

		private void SendMessage(int cmd, Object value) {
			switch (cmd)

			{
			case Const.cmd_LoadSDFile_State:
				if (num < 20) {
					num++;
					return;
				} else {
					num = 0;
				}
				mNotifyData.setKey(cmd);
				mNotifyData.setValue(value);
				SysEng.getInstance().addHandlerEvent(mNotifyData);
				break;
			default:
				mNotifyData.setKey(cmd);
				mNotifyData.setValue(value);
				SysEng.getInstance().addHandlerEvent(mNotifyData);
				break;
			case Const.cmd_LoadSDFile_Change:
				ParamEvent event = new ParamEvent() 
				{
					public void ok() {
						FavorFileBean bean = (FavorFileBean) getValue();
						mFileList.add(bean);
						mFileAdapter.notifyDataSetChanged();
						tvScan.setText("正在搜索文件:\n" + bean.getFilePath());
					}
				};
				event.setValue(value);
				SysEng.getInstance().addHandlerEvent(event);
				break;
			}

		}

		private ParamEvent mNotifyData = new ParamEvent() {

			public void ok() {
				P.v("LoadSDFileEvent getKey()=" + getKey());
				switch (getKey()) {
				case Const.cmd_LoadSDFile_Error:
					break;
				case Const.cmd_LoadSDFile_Start:
					tvScan.setText("正在搜索文件...");
					break;
				case Const.cmd_LoadSDFile_Change:
					mFileAdapter.notifyDataSetChanged();
				case Const.cmd_LoadSDFile_State:
					LoadSDFile_State value = (LoadSDFile_State) getValue();
					tvScan.setText("正在搜索文件:\n" + value.strPath);
					break;
				case Const.cmd_LoadSDFile_Finish:
					tvScan.setText("正在搜索完成,共" + mFileAdapter.getCount() + "个文件");
					mFileAdapter.notifyDataSetChanged();
					Toast.makeText(act,
							act.getString(R.string.msg_Scan_Finish),
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};

		public void ok() {
			P.debug("LoadSDFileEvent:start");
			SendMessage(Const.cmd_LoadSDFile_Start, null);
			Init();
			SendMessage(Const.cmd_LoadSDFile_Init, mLFstate.count);
			P.v("refreshSDCardList:start");
			refreshSDCardList(Const.getSDCard());
			P.v("refreshSDCardList:end");
			SendMessage(Const.cmd_LoadSDFile_Finish, null);
			P.debug("LoadSDFileEvent:end");
		}

		public class LoadSDFile_State {
			public String strPath;
			public int count;
			public int Progress;
		}

		private void refreshSDCardList(String strPath) {

			File dir = new File(strPath);
			File[] files = dir.listFiles();
			if (files == null)
				return;

			for (File file : files) {
				if (!mProgress)
					return;
				// if (file.getName().charAt(0) == '.'&& file.isHidden()) by wmh
				// 隐藏文件夹
				// continue;
				mLFstate.Progress++;
				if (file.isDirectory()) {
					refreshSDCardList(file.getAbsolutePath());
				} else if (file.length() > 20 * 1000) {

					String strFileName = file.getName();
					String fileEnds = strFileName.substring(
							strFileName.lastIndexOf(".") + 1).toLowerCase();// 取出文件后缀名并转成小写
					FileEnd result = null;
					if (fileEnds == null || fileEnds.length() < 1) {
						continue;
					}
					try {
						result = BinarySearch(listItem, fileEnds);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (result != null && result.MinSize < file.length()) {
						FavorFileBean bean = new FavorFileBean(file,
								file.getName(), file.getPath(), false);
						bean.setDirectory(file.isDirectory());
						bean.setLength(file.length());
						SendMessage(Const.cmd_LoadSDFile_Change, bean);
					}
					mLFstate.strPath = file.getAbsolutePath();
					SendMessage(Const.cmd_LoadSDFile_State, mLFstate);
				}
			}
			return;
		}

		// 二分法查找查找完全匹配的数据
		private FileEnd BinarySearch(List<FileEnd> strList, String strWord) {
			for (int nIndex = 0; nIndex < strList.size(); nIndex++) {
				int nCompare = strWord.compareTo(strList.get(nIndex).key);
				if (nCompare == 0) {
					return strList.get(nIndex);
				}
			}
			return null;
		}

		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	public void NotifyDataSetChanged(int cmd, Object value) {
		// TODO Auto-generated method stub

	}
}
