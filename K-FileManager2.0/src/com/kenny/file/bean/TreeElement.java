package com.kenny.file.bean;

import java.io.File;
import java.util.ArrayList;

public class TreeElement extends FileBean
{
   private boolean mhasParent;
   private TreeElement parent;
   private int level;
   private ArrayList<TreeElement> childList = new ArrayList<TreeElement>();
   
   public boolean isMhasParent()
   {
      return mhasParent;
   }
   
   public void setMhasParent(boolean mhasParent)
   {
      this.mhasParent = mhasParent;
   }
   
   public int getLevel()
   {
      return level;
   }
   
   public void setLevel(int level)
   {
      this.level = level;
   }
   
   public boolean isExpanded()
   {
      return expanded;
   }
   
   public void setExpanded(boolean expanded)
   {
      this.expanded = expanded;
   }
   
   public boolean isChildFolder()
   {
      File[] mFiles = mFile.listFiles();// 遍历出该文件夹路径下的所有文件/文件夹
      if (mFiles != null)/* 将所有文件信息添加到集合中 */
      {
         for (File mCurrentFile : mFiles)
         {
	  if (mCurrentFile.isDirectory()) { return true; }
         }
      }
      return false;
   }
   
   public ArrayList<TreeElement> getChildList()
   {
      File[] mFiles = mFile.listFiles();// 遍历出该文件夹路径下的所有文件/文件夹
      this.childList.clear();
      if (mFiles != null)
      {
         /* 将所有文件信息添加到集合中 */
         for (File mCurrentFile : mFiles)
         {
	  if (mCurrentFile.isDirectory())
	  {
	     TreeElement element = new TreeElement(mCurrentFile.getName(),
		 mCurrentFile.getPath());
	     this.childList.add(element);
	     this.mhasParent = false;
	     element.parent = this;
	     element.level = this.level + 1;
	  }
         }
      }
      return childList;
   }
   
   public TreeElement getParent()
   {
      return parent;
   }
   
   public void setParent(TreeElement parent)
   {
      this.parent = parent;
   }
   
   // private OutlineElement outlineElement;
   private boolean expanded = false;
   
   public void addChild(TreeElement c)
   {
      this.childList.add(c);
      this.mhasParent = false;
      c.parent = this;
      c.level = this.level + 1;
   }
   
   public TreeElement(String title, String path)
   {
      super(new File(path), title);
      // this.id = id;
      // this.path = path;
      // this.outlineTitle = title;
      this.level = 0;
      this.mhasParent = true;
      this.parent = null;
   }
   
   // public TreeElement(String id, String outlineTitle, boolean mhasParent,
   // boolean mhasChild, TreeElement parent, int level, boolean expanded)
   // {
   // super(new File(path));
   // this.id = id;
   // this.outlineTitle = outlineTitle;
   // this.mhasParent = mhasParent;
   // this.mhasChild = mhasChild;
   // this.parent = parent;
   // if (parent != null)
   // {
   // this.parent.getChildList().add(this);
   // }
   // this.level = level;
   // this.expanded = expanded;
   // }
   
}
