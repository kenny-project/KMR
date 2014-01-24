package com.kenny.file.util;

import java.util.Collections;
import java.util.List;

import android.content.Context;

import com.kenny.file.Parser.FolderTypeParser;
import com.kenny.file.bean.FolderTypeBean;
import com.kenny.file.sort.FolderTypeSort;
import com.kenny.file.tools.T;
/**
 * 遍历文件夹，找到相应的名称
 * @author WangMinghui
 *
 */
public class FolderTypeUtil
{
	private List<FolderTypeBean> listItem;
	public void Init(Context context)
	{
		String Data;
		try
		{
			// SDFile.ReadRAMFile
			Data =T.getAssetStringFile(context, "folderType.xml");
			FolderTypeParser mFavoriteParser = new FolderTypeParser();
			listItem = mFavoriteParser.parseJokeByData(context, Data);
			Collections.sort(listItem, new FolderTypeSort());
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public String BinarySearch(String strWord)
	{
		int nIndex = 0;
		int nStart = 0;
		int nEnd = listItem.size() - 1;
		while (nStart <= nEnd)
		{
			nIndex = (nStart + nEnd) / 2;
			int nCompare = strWord.compareTo(listItem.get(nIndex).path);
			if (nCompare == 0)
			{ // 等于
				return listItem.get(nIndex).title;
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
}
