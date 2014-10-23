package com.kenny.file.bean;

public class FileEnd
{
   public FileEnd(String key, int flag,int MinSize)
   {
      this.key = key;
      this.flag = flag;
      this.MinSize = MinSize;
   }
   public FileEnd(String key, int flag,int MinSize,FGroupInfo groupInfo)
   {
      this.key = key;
      this.groupInfo=groupInfo;
      this.flag = flag;
      this.MinSize = MinSize;
   }
   public FileEnd(String key, int flag,FileTypeBean mFileTypeBean)
   {
      this.key = key;
      this.flag = flag;
      this.mFileTypeBean=mFileTypeBean;
   }
   public FGroupInfo groupInfo;
   public String key;	// 结尾数据 end
   public int flag;		//标记位
   public int MinSize; 	//最大
   public FileTypeBean mFileTypeBean;//所属分类

}
