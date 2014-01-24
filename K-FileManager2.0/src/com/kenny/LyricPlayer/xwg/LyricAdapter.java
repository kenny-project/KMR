package com.kenny.LyricPlayer.xwg;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LyricAdapter
{
   private ArrayList<LyricLine> mLyricLines = null;
   private LyricListener mListener = null;
   private int mCurrentLyric = 0;
   private int mLyricOffset = -300;
   private static final String TAG = new String("LyricAdapter");
   
   public interface LyricListener
   {
      public void onLyricChanged(int lyric_index);
      
      public void onLyricLoaded();
   }
   
   private class LyricLine
   {
      long mLyricTime; // in milliseconds
      String mLyricText;
      
      LyricLine(long time, String lyric)
      {
         mLyricTime = time;
         mLyricText = lyric;
      }
   }
   
   private static class TimeParser
   {
      // @return value in milliseconds.
     public static long parse(String strTime)
      {
         String beforeDot = new String("00:00:00");
         String afterDot = new String("0");
         
         int dotIndex = strTime.indexOf(".");
         if (dotIndex < 0)
         {
	  beforeDot = strTime;
         }
         else if (dotIndex == 0)
         {
	  afterDot = strTime.substring(1);
         }
         else
         {
	  beforeDot = strTime.substring(0, dotIndex);// 00:01:23
	  afterDot = strTime.substring(dotIndex + 1); // 45
         }
         
         long intSeconds = 0;
         int counter = 0;
         while (beforeDot.length() > 0)
         {
	  int colonPos = beforeDot.indexOf(":");
	  try
	  {
	     if (colonPos > 0)
	     {
	        intSeconds *= 60;
	        intSeconds += new Integer(beforeDot.substring(0, colonPos));
	        beforeDot = beforeDot.substring(colonPos + 1);
	     }
	     else if (colonPos < 0)
	     {
	        intSeconds *= 60;
	        intSeconds += new Integer(beforeDot);
	        beforeDot = "";
	     }
	     else
	     {
	        return -1;
	     }
	  }
	  catch (NumberFormatException e)
	  {
	     return -1;
	  }
	  ++counter;
	  if (counter > 3) { return -1; }
         }
         // intSeconds=83
         
         String totalTime = String.format("%d.%s", intSeconds, afterDot);// totaoTimer
							   // =
							   // "83.45"
         Double doubleSeconds = new Double(totalTime);
         return (long) (doubleSeconds * 1000);
      }
   }
   
   public void LoadLyric(String path, String encoding)
   {
      mLyricLines = null;
      mCurrentLyric = -1;
      
      if (path != null)
      {
         mLyricLines = new ArrayList<LyricLine>();
         
         try
         {
	  FileInputStream fi = new FileInputStream(path);
	  BufferedReader br = new BufferedReader(new InputStreamReader(fi,
	        encoding));
	  
	  String line;
	  while ((line = br.readLine()) != null)
	  {
	     int timeEndIndex = line.lastIndexOf("]");
	     if (timeEndIndex >= 3)
	     {
	        String lyricText = new String();
	        if (timeEndIndex < (line.length() - 1))
	        {
		 lyricText = line
		       .substring(timeEndIndex + 1, line.length());
	        }
	        
	        int timeSegmentEnd = timeEndIndex;
	        while (timeSegmentEnd > 0)
	        {
		 timeEndIndex = line.lastIndexOf("]", timeSegmentEnd);
		 if (timeEndIndex < 1) break;
		 int timeStartIndex = line.lastIndexOf("[",
		       timeEndIndex - 1);
		 if (timeStartIndex < 0) break;
		 long lyricTime = TimeParser.parse(line.substring(
		       timeStartIndex + 1, timeEndIndex));
		 if (lyricTime >= 0)
		 {
		    lyricTime += mLyricOffset;
		    if (lyricTime < 0)
		    {
		       lyricTime = 0;
		    }
		    mLyricLines.add(new LyricLine(lyricTime, lyricText));
		 }
		 timeSegmentEnd = timeStartIndex;
	        }
	     }
	  }
	  Collections.sort(mLyricLines, new Comparator<LyricLine>()
	  {
	     public int compare(LyricLine object1, LyricLine object2)
	     {
	        if (object1.mLyricTime > object2.mLyricTime)
	        {
		 return 1;
	        }
	        else if (object1.mLyricTime < object2.mLyricTime)
	        {
		 return -1;
	        }
	        else
	        {
		 return 0;
	        }
	     }
	  });
	  fi.close();
	  
         }
         catch (FileNotFoundException e)
         {
	  mLyricLines = null;
	  e.printStackTrace();
         }
         catch (IOException e)
         {
	  mLyricLines = null;
	  e.printStackTrace();
         }
      }
      if (mListener != null)
      {
         mListener.onLyricLoaded();
      }
   }
   
   public int getLyricCount()
   {
      if (mLyricLines != null)
      {
         return mLyricLines.size();
      }
      else
      {
         return 0;
      }
   }
   
   public String getLyric(int index)
   {
      if (mLyricLines != null)
      {
         if (index >= 0 && index < mLyricLines.size())
         {
	  return mLyricLines.get(index).mLyricText;
         }
         else
         {
	  return null;
         }
      }
      else
      {
         return null;
      }
   }
   
   public long getLyricTime(int index)
   {
      if (mLyricLines != null)
      {
         if (index >= 0 && index < mLyricLines.size())
         {
	  return mLyricLines.get(index).mLyricTime;
         }
         else
         {
	  return -1;
         }
      }
      else
      {
         return -1;
      }
   }
   
   public int getCurrentLyric()
   {
      return mCurrentLyric;
   }
   
   public void setListener(LyricListener listener)
   {
      mListener = listener;
   }
   
   public void notifyTime(long millisecond)
   {
      if (mLyricLines != null)
      {
         int newLyric = seekLyric(millisecond);
         // Log.i(TAG, "newLyric = " + newLyric);
         if (newLyric != -1 && newLyric != mCurrentLyric)
         {
	  if (mListener != null)
	  {
	     mListener.onLyricChanged(newLyric);
	  }
	  mCurrentLyric = newLyric;
         }
      }
   }
   
   private int seekLyric(long millisecond)
   {
      int findStart = 0;
      if (mCurrentLyric >= 0)
      {
         findStart = mCurrentLyric;
      }
      
      if (mLyricLines.size() == 0) return findStart;
      
      long lyricTime = mLyricLines.get(findStart).mLyricTime;
      
      if (millisecond > lyricTime)
      {
         if (findStart == (mLyricLines.size() - 1)) return findStart;
         
         int new_index = findStart + 1;
         while (new_index < mLyricLines.size()
	     && mLyricLines.get(new_index).mLyricTime <= millisecond)
         {
	  ++new_index;
         }
         return new_index - 1;
      }
      else if (millisecond < lyricTime)
      {
         if (findStart == 0) return 0;
         
         int new_index = findStart - 1;
         while (new_index > 0
	     && mLyricLines.get(new_index).mLyricTime > millisecond)
         {
	  --new_index;
         }
         return new_index;
      }
      else
      {
         return findStart;
      }
      
   }
}
