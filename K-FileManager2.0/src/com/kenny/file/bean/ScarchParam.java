package com.kenny.file.bean;

import java.util.ArrayList;

/**
 * 查询参数
 * @author WangMinghui
 *
 */
public class ScarchParam
{
   private String path,SearchValue;
   private boolean bSubdirectory,bCaseSensitive,hide;
   private int searchType;//搜索类型
   public int getSearchType()
{
	return searchType;
}
public void setSearchType(int searchType)
{
	this.searchType = searchType;
}
private ArrayList<FileBean> mSearchItems;
   public ArrayList<FileBean> getSearchItems()
   {
      return mSearchItems;
   }
   public void setSearchItems(ArrayList<FileBean> mSearchItems)
   {
      this.mSearchItems = mSearchItems;
   }
   public boolean isHide()
   {
      return hide;
   }
   public void setHide(boolean hide)
   {
      this.hide = hide;
   }
   public String getPath()
   {
      return path;
   }
   public void setPath(String path)
   {
      this.path = path;
   }
   public String getSearchValue()
   {
      return SearchValue;
   }
   public void setSearchValue(String searchValue)
   {
      SearchValue = searchValue;
   }
   public boolean isSubdirectory()
   {
      return bSubdirectory;
   }
   public void setSubdirectory(boolean bSubdirectory)
   {
      this.bSubdirectory = bSubdirectory;
   }
   public boolean isCaseSensitive()
   {
      return bCaseSensitive;
   }
   public void setCaseSensitive(boolean bCaseSensitive)
   {
      this.bCaseSensitive = bCaseSensitive;
   }
}
