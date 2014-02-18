package com.kenny.Slidingmenu.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Toast;

import com.framework.page.AbsFragmentPage;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Adapter.KMenuAdapter;
import com.kenny.file.Event.ExitEvent;
import com.kenny.file.Event.LoadSDFolderEvent;
import com.kenny.file.Parser.FavoriteGroupParser;
import com.kenny.file.bean.FGroupInfo;
import com.kenny.file.bean.FileBean;
import com.kenny.file.bean.KMenuGroupBean;
import com.kenny.file.bean.KMenuItemBean;
import com.kenny.file.db.Dao;
import com.kenny.file.page.SpecifyLocalFilePage;
import com.kenny.file.sort.FileSort;
import com.kenny.file.tools.SaveData;
import com.kenny.file.tools.T;
import com.kenny.file.util.Const;
import com.kenny.swiftp.gui.SwifFtpMain;
import com.kuaipan.client.KuaiPanPage;

public class KMenuFragment extends AbsFragmentPage implements
		OnChildClickListener, OnClickListener
{
	private ExpandableListView lvEList;
	private KMenuAdapter adapter;
	private ArrayList<KMenuGroupBean> mGroupBeans = new ArrayList<KMenuGroupBean>(
			10);
	private HashMap<Integer, FGroupInfo> mFavoriteGroupList = new HashMap<Integer, FGroupInfo>(); // 正在运行程序列表
	private static final int LocalPage = 102;
	public static final int Search = 0;
	public static final int NetWork = -1;// 2
	public static final int Favorite = 2;
	public static final int appsPage = 3;
	// public static final int task = 4;
	public static final int tools = 4;
	public static final int setting = 5;

	@Override
	public void onCreate(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		setContentView(R.layout.kmenu, inflater);
		Init();

		mView.findViewById(R.id.btn_search).setOnClickListener(this);
		mView.findViewById(R.id.btn_setting).setOnClickListener(this);
		lvEList = (ExpandableListView) mView.findViewById(R.id.lvEList);
		adapter = new KMenuAdapter(getActivity(), mGroupBeans);
		lvEList.setAdapter(adapter);
		lvEList.setOnGroupClickListener(new OnGroupClickListener()
		{
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id)
			{
				return true;
			}
		});
		lvEList.setOnChildClickListener(this);
		for (int i = 0; i < adapter.getGroupCount(); i++)
		{
			lvEList.expandGroup(i);
		}
	}

	private void Init()
	{
		FavoriteInit();
		MenuInit();
	}

	/**
	 * 获取手机目录
	 * 
	 * @return
	 */
	private List<KMenuItemBean> getSDCardList()
	{
		File mFile = new File("/mnt/");
		File[] mFiles = mFile.listFiles();// 遍历出该文件夹路径下的所有文件/文件夹
		List<KMenuItemBean> Roots = new ArrayList<KMenuItemBean>();
		Roots.add(new KMenuItemBean(0, "手机", Const.Root,
				R.drawable.ic_textedit_save));
		for (int i = 0; i < mFiles.length; i++)
		{
			if (mFiles[i].canWrite())
			{
				Roots.add(new KMenuItemBean(i + 1, mFiles[i].getName(),
						mFiles[i].getAbsolutePath(),
						R.drawable.ic_textedit_save));
				Log.d("wmh", mFiles[i].getAbsolutePath());
			}
		}
		return Roots;
	}

	private void MenuInit()
	{
		mGroupBeans.clear();
		KMenuGroupBean groupbean = new KMenuGroupBean();
		groupbean.setID(LocalPage);
		groupbean.setTitle("目录");
		groupbean.AddAllDictBean(getSDCardList());
		// groupbean.AddDictBean(new KMenuItemBean(-1, "搜索", Const.Root,
		// R.drawable.audio_prev_nor));
		mGroupBeans.add(groupbean);
		groupbean = new KMenuGroupBean();
		groupbean.setTitle("分类");
		groupbean.setID(Favorite);
		groupbean.AddDictBean(new KMenuItemBean(1, "音乐", mFavoriteGroupList
				.get(1), R.drawable.ic_category_music));
		groupbean.AddDictBean(new KMenuItemBean(2, "图片", mFavoriteGroupList
				.get(2), R.drawable.ic_category_picture));
		groupbean.AddDictBean(new KMenuItemBean(3, "视频", mFavoriteGroupList
				.get(3), R.drawable.ic_category_video));
		groupbean.AddDictBean(new KMenuItemBean(4, "文档", mFavoriteGroupList
				.get(4), R.drawable.ic_category_document));
		groupbean.AddDictBean(new KMenuItemBean(6, "压缩包", mFavoriteGroupList
				.get(6), R.drawable.ic_category_zip));
		groupbean.AddDictBean(new KMenuItemBean(7, "安装包", mFavoriteGroupList
				.get(7), R.drawable.ic_root_explorer));
		groupbean.AddDictBean(new KMenuItemBean(-1, "整理箱", Const.szAppPath, R.drawable.ic_category_favorite));
		groupbean.AddDictBean(new KMenuItemBean(-1, "下载", Const.szDownLoadPath, R.drawable.ic_category_favorite));
		groupbean.AddDictBean(new KMenuItemBean(0, "收藏夹", mFavoriteGroupList
				.get(8), R.drawable.ic_category_favorite));
		mGroupBeans.add(groupbean);
		groupbean = new KMenuGroupBean();
		groupbean.setTitle("应用");
		groupbean.setID(appsPage);
		groupbean.AddDictBean(new KMenuItemBean(1, "用户安装", 0,
				R.drawable.ic_root_explorer));
		groupbean.AddDictBean(new KMenuItemBean(2, "系统应用", 1,
				R.drawable.ic_root_explorer));
		mGroupBeans.add(groupbean);
		groupbean = new KMenuGroupBean();
		groupbean.setTitle("高级");
		groupbean.setID(tools);
		groupbean.AddDictBean(new KMenuItemBean(1, "FTP服务器", 1,
				R.drawable.ic_sys_ftp));
		groupbean.AddDictBean(new KMenuItemBean(2, "金山网盘", 2,
				R.drawable.ic_cloud_disk));
		mGroupBeans.add(groupbean);
//		groupbean = new KMenuGroupBean();
//		groupbean.setTitle("设置");
//		groupbean.setID(setting);
//		groupbean.AddDictBean(new KMenuItemBean(1, "设置", 1,
//				R.drawable.ic_settings));
//		mGroupBeans.add(groupbean);

	}

	/**
	 * 初始化收藏夹
	 */
	private void FavoriteInit()
	{
		String Data;
		String FileName = m_act.getString(R.string.FavoriteType);
		Data = new String(T.ReadResourceAssetsFile(m_act, FileName));
		FavoriteGroupParser mFavoriteParser = new FavoriteGroupParser();
		mFavoriteGroupList.clear();
		ArrayList<FGroupInfo> result = mFavoriteParser.parseJokeByData(m_act,
				Data);
		try
		{
			for (int i = 0; i < result.size(); i++)
			{
				FGroupInfo temp = result.get(i);
				mFavoriteGroupList.put(temp.getId(), temp);
				String path = Const.szAppPath + temp.getTitle();
				File file = new File(path);
				if (!file.exists())
				{
					file.mkdirs();
				}
				long size = SaveData.Read(m_act,
						"FavGroupSize_" + temp.getId(), 0l);
				int count = SaveData.Read(m_act,
						"FavGroupCount_" + temp.getId(), temp.getCount());
				temp.setCount(count);
				temp.setSize(size);
			}
			LoadSDFolderEvent mLoadSDFileEvent = new LoadSDFolderEvent(m_act,
					false, result, null);
			SysEng.getInstance().addThreadEvent(mLoadSDFileEvent);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	private HashMap<String, ContentFragment> mHashMap=new HashMap<String, ContentFragment>();
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id)
	{
		KMenuItemBean bean = (KMenuItemBean) adapter.getChild(groupPosition,
				childPosition);
		int GroupID = ((KMenuGroupBean) adapter.getGroup(groupPosition))
				.getID();
		ContentFragment newContent = null;
		Intent intent;
		switch (GroupID)
		{
		case LocalPage:
			if (bean.getID() == -1)
			{
				newContent = new SearchResultPage(this);
			} else
			{
				newContent = new LocalPage((String) bean.getObj());
			}
			break;
		case Favorite:
			if (bean.getID() == 0)
			{
				newContent = onMyFavoriteClick((FGroupInfo) bean.getObj());
			}
			else if (bean.getID() == -1)
			{
				// newContent = newContent = new LocalPage(
				// (String)
				// bean.getObj());onSpecifyLocalFavoriteClick(Const.szAppPath);
				newContent = new LocalPage((String) bean.getObj());
			}
			else
			{
				newContent = new FavoriteFilePage(getActivity(),
						(FGroupInfo) bean.getObj());
			}
			break;
		case appsPage:
			newContent = new AppsPage((Integer) bean.getObj());
			break;
		case tools:
			int type = (Integer) bean.getObj();
			switch (type)
			{
			case 1:
				intent = new Intent(m_act, SwifFtpMain.class);
				m_act.startActivity(intent);
				break;
			case 2:
				intent = new Intent(m_act, KuaiPanPage.class);
				m_act.startActivity(intent);
				break;
			}
			break;
		case setting:
			newContent = new SettingPage();
			break;
		}
		if (newContent != null)
		{
			newContent.setTitle(bean.getTitle());
			switchFragment(newContent);
		}
		return false;
	}

	/**
	 *  跳转到指定本地数据页面
	 */
	private ContentFragment onSpecifyLocalFavoriteClick(String path)
	{
		return new SpecifyLocalFilePage(m_act, path);
	}

	/**
	 * 进入到我的收藏夹
	 */
	private ContentFragment onMyFavoriteClick(FGroupInfo bean)
	{
		ArrayList<FileBean> mFileList = new ArrayList<FileBean>();
		Dao dao = Dao.getInstance(getActivity().getApplicationContext());
		mFileList.clear();
		mFileList.addAll(dao.getHistoryInfos(String.valueOf(0)));
		Collections.sort(mFileList, new FileSort());
		dao.closeDb();
		return new MyFavoriteFilePage(m_act, bean, mFileList);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		// String[] birds = getResources().getStringArray(R.array.birds);
		// ArrayAdapter<String> colorAdapter = new
		// ArrayAdapter<String>(getActivity(),
		// android.R.layout.simple_list_item_1, android.R.id.text1, birds);
		// setListAdapter(colorAdapter);
	}

	private Long mOldTimeInMillis=0l;
	public boolean onKeyDown(int keyCode, KeyEvent msg)
	{

		// 弹出退出对话框
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			Calendar c = Calendar.getInstance();
			if (c.getTimeInMillis()-mOldTimeInMillis>3000)
			{
				Toast.makeText(
						getActivity(),
						getActivity().getString(
								R.string.dlg_press_again_to_exit_the_program),
						Toast.LENGTH_SHORT).show();
				mOldTimeInMillis=c.getTimeInMillis();
				return true;
			} else
			{
				SysEng.getInstance().addEvent(
						new ExitEvent((Activity) getActivity(), false));
			}
		}
		return super.onKeyDown(keyCode, msg);
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.btn_search:
			switchFragment(new SearchResultPage(this));
			break;
		case R.id.btn_setting:
			switchFragment(new SettingPage());
			break;
		}
	}

	// @Override
	// public void onListItemClick(ListView lv, View v, int position, long id) {
	// Fragment newContent = new BirdGridFragment(position);
	// if (newContent != null)
	// switchFragment(newContent);
	// }
	// the meat of switching the above fragment

}
