package com.kenny.file.Event;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.provider.ContactsContract.Directory;

import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.kenny.file.bean.FGroupInfo;
import com.kenny.file.bean.FileEnd;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.sort.FileEndSort;
import com.kenny.file.tools.SaveData;
import com.kenny.file.util.Const;
import com.kenny.file.util.NFileTools;

/**
 * 遍历SD卡后台线程执行方法
 * 
 * @author wmh 初始化event
 * */
public class LoadSDFolderBGEventV2 extends AbsEvent
{
	private Context act;
	private INotifyDataSetChanged m_NotifyDataSetChanged = null;
	private boolean mProgress = false;
	private ArrayList<FGroupInfo> mFGroupInfo;
	private ArrayList<FileEnd> listItem = new ArrayList<FileEnd>();
	private String szAppPath = "K-FileManager";
	private static final String TAG = "LoadSDFolderBGEvent";

	public LoadSDFolderBGEventV2(Context act,
			ArrayList<FGroupInfo> mFGroupInfo,
			INotifyDataSetChanged notifyDataSetChanged)
	{
		this.mFGroupInfo = mFGroupInfo;
		this.act = act;
		m_NotifyDataSetChanged = notifyDataSetChanged;
		mProgress = true;
		szAppPath = Const.AppName;
	}

	private void Init()
	{
		listItem.clear();
		for (int i = 0; i < mFGroupInfo.size(); i++)
		{
			FGroupInfo info = mFGroupInfo.get(i);
			info.Clear();
			String[] ends = info.getArrayEnd();
			for (String end : ends)
			{
				if (end.length() > 1)
				{
					listItem.add(new FileEnd(end.toLowerCase(), info.getId(),
							info.getMinSize(), info));
				}
			}
		}
		Collections.sort(listItem, new FileEndSort());
	}

	public void Cancel()
	{
		mProgress = false;
	}


	public void ok()
	{
		P.debug("LoadSDFileEvent:start");
		SendMessage(Const.cmd_LoadSDFile_Start, null);
		mProgress = true;
		Init();
		SendMessage(Const.cmd_LoadSDFile_Init, null);
		P.debug("refreshSDCardList:start");

		File dir = new File("/mnt/");
		String[] files = dir.list();
		for (int i = 0; i < files.length; i++)
		{
			ReadSDFolderList("/mnt/" + files[i], 0);// Const.getSDCard() by wmh
													// 2013-11-6
		}
		P.debug("refreshSDCardList:end listItem.size=" + listItem.size());
		for (FileEnd end : listItem)
		{
			SaveData.Write(act, "FavGroupSize_" + end.groupInfo.getId(),
					end.groupInfo.length);
			SaveData.Write(act, "FavGroupCount_" + end.groupInfo.getId(),
					end.groupInfo.getCount());
			SaveData.Write(act, "FavGroupPaths_" + end.groupInfo.getId(),
					end.groupInfo.getPaths());
		}
		SaveData.Write(act, "FavoriteInit", true);
		P.debug("LoadSDFileEvent:end");
		SendMessage(Const.cmd_LoadSDFile_Finish, null);
	}

	public class LoadSDFile_State
	{
		public String strPath;
		public int count;
		public int Progress;
	}

//	private void CheckFile(int Level, File file)
//	{
//		if (Level == 0 && file.getName().equals(szAppPath))
//		{
//			return;
//		} else
//		{
//			ReadSDFolderList(file.getAbsolutePath(), Level + 1);
//		}
//	}

	private void ReadSDFolderList(String strParentPath, int Level)
	{
		if (Level >= 5)
		{
			return;
		}
		File dir = new File(strParentPath);
		strParentPath = dir.getAbsolutePath() + "/";
		String[] files = dir.list();
		if (files == null||!mProgress)
		{
			return;
		}

		for (String fileName : files)
		{
			String filePath = strParentPath + fileName;
			if (NFileTools.isDirectory(filePath))
			{
				if (Level == 0 && (fileName.equals(szAppPath)||fileName.equals("Android")||fileName.equals("mipush")))
				{
					continue;
				}
				else
				{
					ReadSDFolderList(filePath, Level + 1);
				}
			}
			else
			{	int lastIndex =	filePath.lastIndexOf(".");
				int count = filePath.length()-lastIndex;
				if (lastIndex!=-1 && count <=5)
				{
					String fileEnds = filePath.substring(lastIndex+1);// 取出文件后缀名并转成小写
					FileEnd result = null;
					try
					{
						result = BinarySearch(listItem, fileEnds);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						continue;
					}
					if (result != null) // 比较文件大小限制
					{
						long length = NFileTools.getFileSize(null);
						if(result.MinSize < length)
						{
						result.groupInfo.length += length;
						result.groupInfo.AddCount();
						result.groupInfo.AddFolderPath(strParentPath);
						}
					}
				}
			}
		}
	}

	// 二分法查找查找完全匹配的数据
	private FileEnd BinarySearch(List<FileEnd> strList, String strWord)
	{
		int nIndex = 0;
		int nStart = 0;
		int nEnd = strList.size() - 1;
		while (nStart <= nEnd)
		{
			nIndex = (nStart + nEnd) / 2;
			int nCompare = strWord.compareToIgnoreCase(strList.get(nIndex).key);
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
	private Handler handler = new Handler();
	private boolean bRefresh = false;

	private void SendMessage(int cmd, Object value)
	{
		if (m_NotifyDataSetChanged != null)
		{
			if (cmd == Const.cmd_LoadSDFile_State)
			{
				if (!bRefresh)
				{
					bRefresh = true;
					handler.postDelayed(new Runnable()
					{
						@Override
						public void run()
						{
							bRefresh = false;
						}
					}, 500);
					m_NotifyDataSetChanged.NotifyDataSetChanged(cmd, value);
				}
			} else
			{
				m_NotifyDataSetChanged.NotifyDataSetChanged(cmd, value);
			}
		}
	}

}
