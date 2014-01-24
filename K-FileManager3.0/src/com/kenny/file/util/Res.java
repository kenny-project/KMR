package com.kenny.file.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.kenny.KFileManager.R;
import com.kenny.file.Parser.FileTypeParser;
import com.kenny.file.bean.FileEnd;
import com.kenny.file.bean.FileTypeBean;
import com.kenny.file.sort.FileEndSort;

public class Res
{
	private Context mContext;
	private static Res res;
	private Drawable mBackUp;
	private Drawable mFolder;
	private Drawable mOthers;
	private boolean isFirstRun;
	private BitmapDrawable mImgError;
	private ArrayList<FileTypeBean> mFGroupInfo = new ArrayList<FileTypeBean>();
	private ArrayList<FileEnd> listItem = new ArrayList<FileEnd>();
	public void Clear()
	{
		for(FileTypeBean bean: mFGroupInfo)
		{
			bean.Clear();
		}
	}
	private void Init(Context context)
	{
		FileTypeInit(context);
	}
	
	public void setFirstRun(boolean isFirstRun) {
		this.isFirstRun = isFirstRun;
	}
	public boolean isFirstRun() {
		return isFirstRun;
	}
	private void FileTypeInit(Context context)
	{
		String Data;
		try {
			Data = SDFile.ReadRAMFile(context, "fileType.xml");
		FileTypeParser mFavoriteParser = new FileTypeParser();
		mFGroupInfo.clear();
		ArrayList<FileTypeBean> result = mFavoriteParser.parseJokeByData(
				context, Data);
		mFGroupInfo.addAll(result);
		for (int i = 0; i < mFGroupInfo.size(); i++)
		{
			FileTypeBean info = mFGroupInfo.get(i);
			String[] ends = info.getEnds().split("\\|");

			for (String end : ends)
			{
				if (end.length() > 1)
				{
					listItem.add(new FileEnd(end.toLowerCase(), info.getId(),
							info));
				}
			}
		}
		Collections.sort(listItem, new FileEndSort());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 根据扩展名返回相对应的文件
	 * 
	 * @param fileEnds
	 * @return
	 */
	public Drawable getDefFileIco(String fileEnds)
	{
		FileTypeBean temp = BinarySearch(listItem, fileEnds);
		if (temp != null)
		{
			return temp.getDrawable(mContext);
		} else
		{
			return getOthers();
		}
	}

	// 二分法查找查找完全匹配的数据
	private FileTypeBean BinarySearch(List<FileEnd> strList, String strWord)
	{
		int nIndex = 0;
		int nStart = 0;
		int nEnd = strList.size() - 1;
		while (nStart <= nEnd)
		{
			nIndex = (nStart + nEnd) / 2;
			int nCompare = strWord.compareTo(strList.get(nIndex).key);
			if (nCompare == 0)
			{ // 等于
				return strList.get(nIndex).mFileTypeBean;

			}  else if (nCompare < 0)
			{
				nEnd = nIndex - 1;
			} else if (nCompare > 0)
			{
				nStart = nIndex + 1;
			}
		}
		return null;
	}
//	private FileTypeBean BinarySearch(List<FileEnd> strList, String strWord)
//	{
//		int nIndex = 0;
//		int nStart = 0;
//		int nEnd = strList.size() - 1;
//
//		while (nStart <= nEnd)
//		{
//			nIndex = (nStart + nEnd) / 2;
//			int nCompare = strWord.compareTo(strList.get(nIndex).key);
//			if (nCompare == 0)
//			{ // 等于
//				return strList.get(nIndex).mFileTypeBean;
//
//			} else if (nCompare < 0)
//			{// 大于
//				nEnd = nIndex - 1;
//			} else if (nCompare > 0)
//			{// 小于
//				nStart = nIndex + 1;
//			}
//			if (nEnd - nStart < 3)
//			{
//				for (; nStart <= nEnd; nStart++)
//				{
//					nIndex = nStart;
//					nCompare = strWord.compareTo(strList.get(nIndex).key);
//					if (nCompare == 0)
//					{// 等于
//						return strList.get(nIndex).mFileTypeBean;
//					}
//				}
//				break;
//			}
//		}
//		return null;
//	}

	public Drawable getBackUp()
	{
		return mBackUp;
	}

	public Drawable getFolder()
	{
		return mFolder;
	}

	public Drawable getOthers()
	{
		return mOthers;
	}

	public BitmapDrawable getImgError()
	{
		return mImgError;
	}

	public synchronized static Res getInstance(Context context)
	{
		if (res == null)
		{
			res = new Res(context);
		}
		return res;
	}

	protected Res(Context context)
	{
		mContext = context;
		Init(context);
		mBackUp = new BitmapDrawable(BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.f_up));
		mOthers = new BitmapDrawable(BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.f_others));
		mFolder = new BitmapDrawable(BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.f_folder));
		mImgError = new BitmapDrawable(BitmapFactory.decodeResource(
				mContext.getResources(), R.drawable.f_img_error));
	}
}
