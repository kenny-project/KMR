package com.kenny.LyricPlayer.xwg;

import java.util.List;

import com.framework.log.P;
import com.kenny.LyricPlayer.xwg.MediaPlayerService.MediaInfoProvider;

public class MyInfoProvider implements MediaInfoProvider
{

   
   private int mCurrentIndex;// 当前播放指针
   private int mPlayMode = 0;// 播放模式
   private List<LyricBean> mMusicList;// 播放列表
   private Boolean RandomEnable = false;// 是否启动随机
   
   public MyInfoProvider(List<LyricBean> music_list)
   {
      mMusicList = music_list;
      moveTo(0);
   }
   
   public int setPlayMode(int playMode)
   {
      mPlayMode = playMode;
      return 1;
   }
   
   public boolean getShuffle()
   {
      return RandomEnable;
   }
   
   public void setMusicList(List<LyricBean> mMusicList)
   {
      this.mMusicList = mMusicList;
   }

   public List<LyricBean> getMusicList()
   {
      return mMusicList;
   }

   public void setShuffle(Boolean randomEnable)
   {
      RandomEnable = randomEnable;
   }
   
   public int getPlayMode()
   {
      return mPlayMode;
   }
   
   
   public boolean moveToPrev()
   {
      if (RandomEnable)
      {
         mCurrentIndex = (int) Math.round(Math.random()
	     * (mMusicList.size() - 1));
         moveTo(mCurrentIndex);
         return true;
      }
      if (mPlayMode == MediaPlayerService.Normal)
      {
         if (hasPrev())
         {
	  moveTo(mCurrentIndex - 1);
	  return true;
         }
         else
         {
	  return false;
         }
      }
      else if (mPlayMode == MediaPlayerService.Repeat)
      {
         moveTo((mCurrentIndex + mMusicList.size() - 1) % mMusicList.size());
         return true;
      }
      else if (mPlayMode == MediaPlayerService.Repeat_1)
      {
         // moveTo((mCurrentIndex + mMusicList.size() - 1) % mMusicList.size());
         return true;
      }
      return false;
   }
   
   public boolean hasPrev()
   {
      if (mPlayMode == MediaPlayerService.Normal)
      {
         return (mCurrentIndex > 0);
      }
      else
      {
         return true;
      }
   }
   
   
   public boolean moveToNext()
   {
      if (RandomEnable)
      {
         mCurrentIndex = (int) Math.round(Math.random()
	     * (mMusicList.size() - 1));
         moveTo(mCurrentIndex);
         return true;
      }
      else
      {
         if (mPlayMode == MediaPlayerService.Repeat)
         {// 循环模式
	  moveTo((mCurrentIndex + 1) % mMusicList.size());
	  return true;
         }
         else
         { // 顺序,标准模式
	  if (hasNext())
	  {
	     moveTo(mCurrentIndex + 1);
	     return true;
	  }
	  else
	  {
	     return false;
	  }
         }
      }
   }
   
   public boolean hasNext()
   {
      if (mPlayMode == MediaPlayerService.Normal)
      {
         return (mCurrentIndex + 1 < mMusicList.size());
      }
      else
      {
         return true;
      }
   }
   
   
   public String getUrl()
   {
      return mMusicList.get(mCurrentIndex).getMusic_url();
   }
   
   
   public String getTitle()
   {
      // TODO Auto-generated method stub
      return mMusicList.get(mCurrentIndex).getMusic_title();
   }
   
   //
   // 
   // public String getLyricUrl()
   // {
   // return mMusicList.get(mCurrentIndex).getLyric_url();
   // }
   //
   // 
   // public String getLyricTitle()
   // {
   // return mMusicList.get(mCurrentIndex).getLyric_title();
   // }
   //
   // 
   // public String getLyricEncoding()
   // {
   // return mMusicList.get(mCurrentIndex).getLyric_encoding();
   // }
   
   
   public boolean moveTo(int index)
   {
      P.v("moveTo index=" + index);
      if (mMusicList.size() > index && index >= 0)
      {
         mCurrentIndex = index;
         P.v("moveTo end index=" + index);
         return true;
      }
      else
      {
         return false;
      }
   }
   
   
   public int getCurrentIndex()
   {
      // TODO Auto-generated method stub
      return mCurrentIndex;
   }
}
