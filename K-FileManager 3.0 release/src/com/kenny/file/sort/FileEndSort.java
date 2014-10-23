package com.kenny.file.sort;

import java.util.Comparator;

import com.framework.log.P;
import com.kenny.file.bean.FileEnd;

public class FileEndSort implements Comparator<FileEnd>
{
   /**
    * 0:相等 1:大于 -1:小于
    */
   public int compare(FileEnd o1, FileEnd o2)
   {
      return sortUp(o1, o2);
   }
   
   private int sortUp(FileEnd o1, FileEnd o2)
   {
      if (o1 == null || o2 == null)
      {
         P.v("sort error, bean is null");
         return 0;
      }
      
      if (o1.key.compareTo(o2.key) > 0)
      {
         return 1;
      }
      else if (o1.key.compareToIgnoreCase(o2.key) < 0)
      {
         return -1;
      }
      return 0;
   }
}