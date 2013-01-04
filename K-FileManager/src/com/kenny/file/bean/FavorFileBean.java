package com.kenny.file.bean;

import java.io.File;

public class FavorFileBean extends FileBean
{
        protected int flag; // 用来标记是属于那一分组的数据
        private int   id;
        
        public FavorFileBean(File mFile, String fileName, String filePath,
	              boolean isBackUp)
        {
	      super(mFile, fileName, filePath, isBackUp);
        }
        
        public int getFlag()
        {
	      return flag;
        }
        
        public void setFlag(int flag)
        {
	      this.flag = flag;
        }
        
        public int getId()
        {
	      return id;
        }
        
        public void setId(int id)
        {
	      this.id = id;
        }
        
}
