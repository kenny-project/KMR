package com.kenny.LyricPlayer.xwg;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;

//import android.util.Log;

public class SafetyTimer
{
   private Timer mTimer = null;
   private Handler mHandler = null;
   private TimerTask mTask = null;
   private OnTimeListener mListener = null;
   private long mInterval = 0; // in milliseconds
   
   // private static final String TAG = new String("SafetyTimer");
   
   public interface OnTimeListener
   {
      public void OnTimer();
   }
   
   public SafetyTimer(long interval, OnTimeListener listener)
   {
      mInterval = interval;
      mListener = listener;
   }
   
   public void startTimer()
   {
      mHandler = new Handler()
      {
         public void handleMessage(Message msg)
         {
	  if (mListener != null)
	  {
	     mListener.OnTimer();
	     // Log.i(TAG, "mListener.OnTimer()");
	  }
	  super.handleMessage(msg);
         }
      };
      mTask = new TimerTask()
      {
         public void run()
         {
	  Message message = new Message();
	  message.what = 0; // anything is ok.
	  mHandler.sendMessage(message);
         }
      };
      mTimer = new Timer();
      mTimer.schedule(mTask, 0, mInterval);
   }
   
   public void stopTimer()
   {
      if (mTimer != null)
      {
         mTimer.cancel();
         mTimer.purge();
      }
      mTimer = null;
      mHandler = null;
      mTask = null;
      // Log.i(TAG, "stopTimer()");
   }
   
   public boolean isRunging()
   {
      return (mTimer != null);
   }
}
