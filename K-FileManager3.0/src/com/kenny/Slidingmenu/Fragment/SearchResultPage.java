package com.kenny.Slidingmenu.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.view.MenuInflater;
import com.framework.event.ParamEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Adapter.FavorFileAdapter;
import com.kenny.file.Event.FavoriteFileEvent;
import com.kenny.file.Event.LoadSearchFileEvent;
import com.kenny.file.Event.copyFileEvent;
import com.kenny.file.Event.cutFileEvent;
import com.kenny.file.Event.delFileEvent;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.Parser.FavoriteGroupParser;
import com.kenny.file.bean.FGroupInfo;
import com.kenny.file.bean.FileBean;
import com.kenny.file.bean.FileEnd;
import com.kenny.file.bean.ScarchParam;
import com.kenny.file.dialog.SearchFileDialog;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.manager.FileManager;
import com.kenny.file.manager.IManager;
import com.kenny.file.sort.FileEndSort;
import com.kenny.file.tools.T;
import com.kenny.file.util.Const;

public class SearchResultPage extends ContentFragment implements
		OnItemClickListener, INotifyDataSetChanged, OnClickListener, IManager
{
	private Button btnBack, btSearch, btSearchMode;
	private EditText etValue;
	private TextView tvSearchNotify;// search 消息栏通知
	private ListView m_locallist, lvSearchMode;
	private FavorFileAdapter fileAdapter;
	private ScarchParam param = new ScarchParam();
	private ArrayList<FileBean> mFileListFilter = new ArrayList<FileBean>();
	private ArrayList<FGroupInfo> mGroupList = new ArrayList<FGroupInfo>(); // 正在运行程序列表

	@Override
	public void onCreate(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		setContentView(R.layout.searchresultpage, inflater);
		setTitle(R.string.search);
		ArrayList<FileBean> mAllFileList = new ArrayList<FileBean>();
		param.setSearchItems(mAllFileList);
		final View lySearchMode = mView.findViewById(R.id.lySearchMode);
		tvSearchNotify = (TextView) mView.findViewById(R.id.tvSearchNotify);
		etValue = (EditText) mView.findViewById(R.id.etSearchFileName);
		etValue.setOnFocusChangeListener(new OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				lySearchMode.setVisibility(View.GONE);
			}
		});
		etValue.setOnEditorActionListener(new TextView.OnEditorActionListener()
		{
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event)
			{
				lySearchMode.setVisibility(View.GONE);
				return false;
			}
		});
		btSearchMode = (Button) mView.findViewById(R.id.btSearchMode);
		btSearchMode.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if (lySearchMode.getVisibility() == View.GONE)
				{
					lySearchMode.setVisibility(View.VISIBLE);
				} else
				{
					lySearchMode.setVisibility(View.GONE);
				}
			}
		});
		btSearch = (Button) mView.findViewById(R.id.btSearch);
		btSearch.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if (etValue.length() > 0)
				{
					mFileListFilter.clear();
					fileAdapter.notifyDataSetChanged();
					btSearch.setEnabled(false);
					T.hideInputPad(etValue);
					param.setCaseSensitive(false);
					param.setPath(FileManager.getInstance().getCurrentPath());
					param.setHide(false);
					param.setSearchValue(etValue.getText().toString());
					param.setSubdirectory(true);
					SysEng.getInstance().addThreadEvent(
							new LoadSearchFileEvent(v.getContext(), true,
									param, SearchResultPage.this));
				} else
				{
					Toast.makeText(m_act, "请输入要查询文件的名称", Toast.LENGTH_LONG)
							.show();
				}
			}
		});

		Button btOptions = (Button) mView.findViewById(R.id.btOptions);
		btOptions.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				new SearchFileDialog().Show(m_act, FileManager.getInstance()
						.getCurrentPath(), param, SearchResultPage.this);
			}
		});

		btnBack = (Button) mView.findViewById(R.id.btBack);
		btnBack.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				ShowMenu();
			}
		});

		lvSearchMode = (ListView) mView.findViewById(R.id.lvSearchMode);
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		HashMap<String, String> item;

		item = new HashMap<String, String>();
		item.put("Title", "搜索全部");
		data.add(item);

		item = new HashMap<String, String>();
		item.put("Title", "搜索音乐");
		data.add(item);
		item = new HashMap<String, String>();
		item.put("Title", "搜索视频");
		data.add(item);
		item = new HashMap<String, String>();
		item.put("Title", "搜索图片");
		data.add(item);
		item = new HashMap<String, String>();
		item.put("Title", "搜索文档");
		data.add(item);
		item = new HashMap<String, String>();
		item.put("Title", "搜索安装包");
		data.add(item);
		item = new HashMap<String, String>();
		item.put("Title", "搜索压缩包");
		data.add(item);

		final SimpleAdapter modeAdapter = new SimpleAdapter(getActivity(),
				data, R.layout.listitem_searchmode, new String[]
				{ "Title" }, new int[]
				{ R.id.tvTitle });

		lvSearchMode.setAdapter(modeAdapter);
		lvSearchMode.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Map<String, String> item = (Map<String, String>) modeAdapter
						.getItem(position);
				btSearchMode.setText(item.get("Title").substring(2));
				param.setSearchType(position);
				lySearchMode.setVisibility(View.GONE);
				ResultFilter();
			}
		});

		lySearchMode.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				lySearchMode.setVisibility(View.GONE);
			}
		});

		m_locallist = (ListView) mView.findViewById(R.id.lvLocallist);
		fileAdapter = new FavorFileAdapter(m_act, 1, mFileListFilter);
		m_locallist.setAdapter(fileAdapter);
		m_locallist.setOnScrollListener(m_localOnScrollListener);
		m_locallist.setOnItemClickListener(this);
		// if (mFileList.size() == 0)
		// {
		// new SearchFileDialog().Show(m_act, FileManager.GetHandler()
		// .getCurrentPath(), param, this);
		// }

		String FileName = m_act.getString(R.string.FavoriteType);
		String Data = new String(T.ReadResourceAssetsFile(m_act, FileName));
		FavoriteGroupParser mFavoriteParser = new FavoriteGroupParser();
		mGroupList.clear();
		ArrayList<FGroupInfo> result = mFavoriteParser.parseJokeByData(m_act,
				Data);
		mGroupList.addAll(result);

		mView.findViewById(R.id.btCopy).setOnClickListener(this);
		mView.findViewById(R.id.btCut).setOnClickListener(this);
		mView.findViewById(R.id.btDelete).setOnClickListener(this);
		mView.findViewById(R.id.btPaste).setOnClickListener(this);
		mView.findViewById(R.id.btSelectAll).setOnClickListener(this);
		tvSearchNotify.setText("搜索路径:"
				+ FileManager.getInstance().getCurrentPath());
	}

	private OnScrollListener m_localOnScrollListener = new OnScrollListener()
	{

		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			switch (scrollState)
			{
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
				P.debug("SCROLL_STATE_FLING");
				if (fileAdapter != null)
					fileAdapter.setShowLogo(false);
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
				P.debug("SCROLL_STATE_IDLE");
				if (fileAdapter != null)
				{
					fileAdapter.setShowLogo(true);
					fileAdapter.notifyDataSetChanged();
				}
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				P.debug("SCROLL_STATE_TOUCH_SCROLL");
				if (fileAdapter != null)
					fileAdapter.setShowLogo(false);
				break;
			default:
				break;
			}
		}

		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount)
		{

		}
	};

	@Override
	public void onResume()
	{
		super.onResume();
		tvSearchNotify.setText("搜索路径:"
				+ FileManager.getInstance().getCurrentPath());
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		FileBean temp = mFileListFilter.get(position);
		if (temp != null)
		{
			final File mFile = temp.getFile();
			SysEng.getInstance()
					.addEvent(new FavoriteFileEvent(m_act, temp, 1));
			// 如果该文件是可读的，我们进去查看文件
			if (mFile.isDirectory())
			{
				if (mFile.canRead())
				{
					// FileManager.getInstance().setFilePath(mFile.getPath());
					// KMainPage.mKMainPage.ChangePage(KMainPage.Local, null);

					switchFragment(new LocalPage((String) mFile.getPath()));
				} else
				{// 如果该文件不可读，我们给出提示不能访问，防止用户操作系统文件造成系统崩溃等
					Toast.makeText(m_act, "该文件夹不存在或权限不够!", Toast.LENGTH_SHORT)
							.show();
				}
			} else
			{
				if (mFile.canRead())
				{
					if (!mFile.exists())
					{
						Toast.makeText(m_act, "未找到该文件!", Toast.LENGTH_SHORT)
								.show();
						return;
					}
					SysEng.getInstance().addHandlerEvent(
							new openDefFileEvent(m_act, mFile.getPath()));
				} else
				{// 如果该文件不可读，我们给出提示不能访问，防止用户操作系统文件造成系统崩溃等
					Toast.makeText(m_act, "对不起，访问权限不够!", Toast.LENGTH_SHORT)
							.show();
				}
			}
		}
	}

	public void NotifyDataSetChanged(int cmd, Object value)
	{
		mNotifyData.setKey(cmd);
		mNotifyData.setValue(value);
		SysEng.getInstance().addHandlerEvent(mNotifyData);
	}

	private ParamEvent mNotifyData = new ParamEvent()
	{

		public void ok()
		{
			P.v("SearchResult getKey()=" + getKey());

			switch (getKey())
			{
			case Const.cmd_LoadSDFile_Error:
				// pbLoading.setVisibility(View.GONE);
				break;
			case Const.cmd_LoadSDFile_Init:
				// Long value=(Long)getValue();
				// pbSDFileStatus.setMax(value.intValue());
				// pbSDFileStatus.setProgress(0);
				// tvMessage.setText("正在遍历文件...");
				// pbSDFileMsg.setVisibility(View.GONE);
				break;
			case Const.cmd_LoadSDFile_Start:
				// pbLoading.setVisibility(View.VISIBLE);
				// tvMessage.setText("正在获取文件数,请耐心等待!\n根据SD卡空间大小的不同遍历时间不等");
				// mListView.setVisibility(View.GONE);
				// mGridView.setVisibility(View.GONE);
				// btRefresh.setVisibility(View.GONE);
				// pbSDFileMsg.setVisibility(View.GONE);
				break;
			case Const.cmd_LoadSDFile_State:
				// fileAdapter.notifyDataSetChanged();
				// LoadSDFileEvent.LoadSDFile_State staValue =
				// (LoadSDFileEvent.LoadSDFile_State) getValue();
				// pbSDFileStatus.setProgress(staValue.Progress);
				// pbSDFileStatus.setMax(staValue.count);
				// tvMessage.setText(staValue.strPath);
				break;
			case Const.cmd_LoadSDFile_Finish:
				// pbLoading.setVisibility(View.GONE);
				// SwitchStyle(bFlag, bStyle);
				etValue.setText(param.getSearchValue());
				btSearch.setEnabled(true);
				ResultFilter();
				// mFileListFilter.clear();
				// mFileListFilter.addAll(param.getSearchItems());
				// fileAdapter.notifyDataSetChanged();
				// Toast.makeText(m_act,
				// m_act.getString(R.string.msg_Scan_Finish),
				// Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	public FGroupInfo getGroupItem(int id)
	{
		for (int i = 0; i < mGroupList.size(); i++)
		{
			FGroupInfo mNowGItem = mGroupList.get(i);
			if (mGroupList.get(i).getId() == id)
			{
				return mNowGItem;
			}
		}
		return null;
	}

	public void ResultFilter()
	{
		mFileListFilter.clear();
		switch (param.getSearchType())
		{
		case 0:// 全部
			mFileListFilter.addAll(param.getSearchItems());
			fileAdapter.notifyDataSetChanged();
			break;
		case 1:// 音乐
			mFileListFilter.addAll(ItemFilter(getGroupItem(1),
					param.getSearchItems()));
			fileAdapter.notifyDataSetChanged();
			break;
		case 2:// 视频
			mFileListFilter.addAll(ItemFilter(getGroupItem(3),
					param.getSearchItems()));
			fileAdapter.notifyDataSetChanged();
			break;
		case 3:// 图片
			mFileListFilter.addAll(ItemFilter(getGroupItem(2),
					param.getSearchItems()));
			fileAdapter.notifyDataSetChanged();
			break;
		case 4:// 文档
			mFileListFilter.addAll(ItemFilter(getGroupItem(4),
					param.getSearchItems()));
			fileAdapter.notifyDataSetChanged();
			break;
		case 5:// 安装包
			mFileListFilter.addAll(ItemFilter(getGroupItem(7),
					param.getSearchItems()));
			fileAdapter.notifyDataSetChanged();
			break;
		case 6:// 压缩包
			mFileListFilter.addAll(ItemFilter(getGroupItem(6),
					param.getSearchItems()));
			fileAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}

	}

	private ArrayList<FileBean> ItemFilter(FGroupInfo info,
			ArrayList<FileBean> list)
	{
		ArrayList<FileBean> tempList = new ArrayList<FileBean>();
		List<FileEnd> listItem = FilterInit(info);
		for (int i = 0; i < list.size(); i++)
		{
			try
			{
				File file = list.get(i).getFile();
				String strFileName = file.getName();
				String fileEnds = strFileName.substring(
						strFileName.lastIndexOf(".") + 1).toLowerCase();// 取出文件后缀名并转成小写
				FileEnd result = null;
				if (file.isDirectory() && fileEnds == null
						|| fileEnds.length() < 1)
				{
					continue;
				}
				result = BinarySearch(listItem, fileEnds);
				if (result != null)
				{
					tempList.add(list.get(i));
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return tempList;
	}

	// 二分法查找查找完全匹配的数据
	private FileEnd BinarySearch(List<FileEnd> strList, String strWord)
	{
		int nIndex = 0;
		int nStart = 0;
		int nEnd = strList.size() - 1;
		while (nStart <= nEnd)
		{
			// if (nIndex == strList.size() - 2)
			// {
			// nIndex = strList.size() - 1;
			// break;
			// }
			nIndex = (nStart + nEnd) / 2;
			int nCompare = strWord.compareTo(strList.get(nIndex).key);
			if (nCompare == 0)
			{
				return strList.get(nIndex);// .flag;
			} else if (nCompare < 0)
			{
				nEnd = nIndex - 1;
			} else if (nCompare > 0)
			{
				nStart = nIndex + 1;
			}
		}
		return null;
	}

	/**
	 * 获取分组的所有后缀名
	 * 
	 * @param info
	 * @return
	 */
	private List<FileEnd> FilterInit(FGroupInfo info)
	{
		ArrayList<FileEnd> mFileEndItem = new ArrayList<FileEnd>();
		info.length = 0l;
		info.setCount(0);
		String[] ends = info.getArrayEnd();
		for (String end : ends)
		{
			if (end.length() > 1)
			{
				mFileEndItem.add(new FileEnd(end.toLowerCase(), info.getId(),
						info.getMinSize(), info));
			}
		}
		Collections.sort(mFileEndItem, new FileEndSort());
		return mFileEndItem;
	}

	public boolean onKeyDown(int keyCode, KeyEvent msg)
	{
		switch (keyCode)
		{
		case KeyEvent.KEYCODE_BACK:
			// KMainPage.mKMainPage.ChangePage(KMainPage.Local, null);
			// ShowMenu();
			backFragment();
			break;
		default:
			return super.onKeyDown(keyCode, msg);
		}
		return true;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btCopy:
			SysEng.getInstance().addHandlerEvent(
					new copyFileEvent(m_act, mFileListFilter, this));
			break;
		case R.id.btCut:
			SysEng.getInstance().addHandlerEvent(
					new cutFileEvent(m_act, mFileListFilter, this));
			break;
		case R.id.btDelete:
			deletefiles();
			break;
		case R.id.btSelectAll:
			SelectAll();
			break;
		case R.id.btBack:
			m_act.onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(1, 1));
			break;
		}
	}

	private void deletefiles()
	{
		if (mFileListFilter.size() > 0)
		{
			final ArrayList<FileBean> mDelFiles = new ArrayList<FileBean>();
			for (int i = 0; i < mFileListFilter.size(); i++)
			{
				FileBean tmpInfo = mFileListFilter.get(i);
				if (tmpInfo.isChecked())
				{
					mDelFiles.add(tmpInfo);
				}
			}
			if (mDelFiles.size() > 0)
			{
				new AlertDialog.Builder(m_act)
						.setTitle(
								m_act.getString(R.string.msg_dialog_info_title))
						.setMessage(m_act.getString(R.string.msg_delselectfile))
						.setPositiveButton(m_act.getString(R.string.ok),
								new DialogInterface.OnClickListener()
								{
									public void onClick(DialogInterface dialog,
											int which)
									{
										SysEng.getInstance().addEvent(
												new delFileEvent(m_act,
														mDelFiles));
									}
								})
						.setNegativeButton(m_act.getString(R.string.cancel),
								null).show();
				return;
			}
		}
		Toast.makeText(m_act,
				m_act.getString(R.string.msg_please_del_operate_file),
				Toast.LENGTH_SHORT).show();
	}

	private void SelectAll()
	{
		if (mFileListFilter.size() >= 1)
		{
			boolean check = !mFileListFilter.get(0).isChecked();
			for (int i = 0; i < mFileListFilter.size(); i++)
			{
				FileBean tmpInfo = mFileListFilter.get(i);
				tmpInfo.setChecked(check);
			}
			fileAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void Refresh()
	{
		// TODO Auto-generated method stub
		fileAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(MenuInflater inflater,
			com.actionbarsherlock.view.Menu menu)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
