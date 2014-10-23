package com.kenny.file.util;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;
/**
 * 获取手机存储空间和SD卡存储空间
 * @author WangMinghui
 *
 */
public class StorageUtil
{
   private static final int ERROR = -1;
   
   /**
    * SDCARD是否存
    */
   public static boolean externalMemoryAvailable()
   {
      return android.os.Environment.getExternalStorageState().equals(
	  android.os.Environment.MEDIA_MOUNTED);
   }
   
   /**
    * 获取手机内部剩余存储空间
    * 
    * @return
    */
   public static long getAvailableInternalMemorySize()
   {
      File path = Environment.getDataDirectory();
      StatFs stat = new StatFs(path.getPath());
      long blockSize = stat.getBlockSize();
      long availableBlocks = stat.getAvailableBlocks();
      return availableBlocks * blockSize;
   }
   
   /**
    * 获取手机内部总的存储空间
    * 
    * @return
    */
   public static long getTotalInternalMemorySize()
   {
      File path = Environment.getDataDirectory();
      StatFs stat = new StatFs(path.getPath());
      long blockSize = stat.getBlockSize();
      long totalBlocks = stat.getBlockCount();
      return totalBlocks * blockSize;
   }
   
   /**
    * 获取SDCARD剩余存储空间
    * 
    * @return
    */
   public static long getAvailableExternalMemorySize()
   {
      if (externalMemoryAvailable())
      {
         Long lFreeSpace = 0l;
         File file = Environment.getExternalStorageDirectory();
         
         if (android.os.Build.VERSION.SDK_INT >= 9)
         {
	  lFreeSpace = file.getFreeSpace();
         }
         else
         {
	  StatFs fs = new StatFs(file.getPath());
	  // The number of blocks that are free on the file system and
	  // available
	  // to
	  // applications. This corresponds to the Unix statfs.f_bavail field.
	  Long lBlockSize = (long) fs.getBlockSize();
	  lFreeSpace = (long) (lBlockSize * fs.getAvailableBlocks());
         }
         return lFreeSpace;
      }
      else
      {
         return ERROR;
      }
   }
   
   /**
    * 获取SDCARD总的存储空间
    * 
    * @return
    */
   public static long getTotalExternalMemorySize()
   {
      if (externalMemoryAvailable())
      {
         Long lTotalSpace = 0l;
         File file = Environment.getExternalStorageDirectory();
         if (android.os.Build.VERSION.SDK_INT >= 9)
         {
	  lTotalSpace = file.getTotalSpace();
         }
         else
         {
	  StatFs fs = new StatFs(file.getPath());
	  // The number of blocks that are free on the file system and
	  // available
	  // to
	  // applications. This corresponds to the Unix statfs.f_bavail field.
	  Long lBlockSize = (long) fs.getBlockSize();
	  lTotalSpace = (long) (lBlockSize * fs.getBlockCount());
         }
         return lTotalSpace;
      }
      else
      {
         return ERROR;
      }
   }
}
