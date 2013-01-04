package com.kenny.file.bean;

import java.io.File;

public class HistoryBean extends FileBean
{
   public HistoryBean(String sourceDir, String appName)
   {
      super(new File(sourceDir), appName);
      setDirectory(false);
   }

   @Override
   public String getDesc()
   {
      return getFilePath();
   }
}
