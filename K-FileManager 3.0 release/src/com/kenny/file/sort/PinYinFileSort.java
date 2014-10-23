package com.kenny.file.sort;

import java.util.Comparator;


import com.framework.log.P;
import com.kenny.file.bean.FileBean;

public class PinYinFileSort implements Comparator<FileBean>
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
      boolean bo1 = o1.isDirectory();
      boolean bo2 = o2.isDirectory();
      if (bo1 == bo2)
      {
         return PinYincompare(o1.getFileName(),o2.getFileName());
      }
      if (bo1)
      {
         return -1;
      }
      else
      {
         return 1;
      }
   }
   
   public int PinYincompare(String o1, String o2)
   {
//      String out1 = Cn2Spell.converterToFirstSpell(o1);
//      String out2 = Cn2Spell.converterToFirstSpell(o2);
//      if (out1.compareToIgnoreCase(out2) > 0)
//      {
//	  return 1;
//      }
//      else if (out1.compareToIgnoreCase(out2) < 0)
//      {
//	  return -1;
//      }
//      else
      {
	  return 0;
      }
   }
   
   private String concatPinyinStringArray(String[] pinyinArray)
   {
      StringBuffer pinyinSbf = new StringBuffer();
      if ((pinyinArray != null) && (pinyinArray.length > 0))
      {
         for (int i = 0; i < pinyinArray.length; i++)
         {
	  pinyinSbf.append(pinyinArray[i]);
         }
      }
      return pinyinSbf.toString();
   }
}