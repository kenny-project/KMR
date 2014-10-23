package com.kenny.file.sort;

import java.util.Comparator;

import com.framework.log.P;
import com.kenny.LyricPlayer.xwg.LyricBean;

public class LyricFileNameSort implements Comparator<LyricBean>
{
   /**
    * 0:相等 1:大于 -1:小于
    */
   public int compare(LyricBean o1, LyricBean o2)
   {
      return sortUp(o1, o2);
   }
   
   private int sortUp(LyricBean o1, LyricBean o2)
   {
      if (o1 == null || o2 == null)
      {
         P.v("sort error, bean is null");
         return 0;
      }
      if (o1.getMusic_title().compareToIgnoreCase(o2.getMusic_title()) > 0)
      {
         return 1;
      }
      else if (o1.getMusic_title().compareToIgnoreCase(o2.getMusic_title()) < 0)
      {
         return -1;
      }
      else
      {
         return 0;
      }
   }
}