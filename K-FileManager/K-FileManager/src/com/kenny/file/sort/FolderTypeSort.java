package com.kenny.file.sort;

import java.util.Comparator;

import com.framework.log.P;
import com.kenny.file.bean.FolderTypeBean;

public class FolderTypeSort implements Comparator<FolderTypeBean>
{
   /**
    * 0:相等 1:大于 -1:小于
    */
   public int compare(FolderTypeBean o1, FolderTypeBean o2)
   {
      return sortUp(o1, o2);
   }
   
   private int sortUp(FolderTypeBean o1, FolderTypeBean o2)
   {
      if (o1 == null || o2 == null)
      {
         P.v("sort error, bean is null");
         return 0;
      }
      
      if (o1.path.compareTo(o2.path) > 0)
      {
         return 1;
      }
      else if (o1.path.compareTo(o2.path) < 0)
      {
         return -1;
      }
      return 0;
   }
}