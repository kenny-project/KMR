package com.kenny.LyricPlayer.xwg;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

public class PhoneStateReceiver extends BroadcastReceiver
{
   @Override
   public void onReceive(Context context, Intent intent)
   {
      if (android.os.Build.VERSION.SDK_INT < 8)
      {
         TelephonyManager tm = (TelephonyManager) context
	     .getSystemService(Context.TELEPHONY_SERVICE);
         if (tm.getCallState() == TelephonyManager.CALL_STATE_IDLE)
         {
	  context.startService(new Intent(MediaPlayerService.ACTION_PLAY));
         }
         else
         {
	  context.startService(new Intent(MediaPlayerService.ACTION_PAUSE));
         }
      }
   }
}
