package com.kenny.file.sort;

import java.util.Comparator;

import com.framework.log.P;
import com.kenny.file.bean.FileBean;

public class FileNameSort implements Comparator<FileBean>
{
   /**
    * 0:相等 1:大于 -1:小于
    */
   public int compare(FileBean o1, FileBean o2)
   {
      return sortUp(o1, o2);
   }
   
   private int sortUp(FileBean o1, FileBean o2)
   {
      if (o1 == null || o2 == null)
      {
         P.v("sort error, bean is null");
         return 0;
      }
      if (o1.getFileName().compareToIgnoreCase(o2.getFileName()) > 0)
      {
         return 1;
      }
      else if (o1.getFileName().compareToIgnoreCase(o2.getFileName()) < 0) { return -1; }
      return 0;
   }
}