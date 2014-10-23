package com.kenny.LyricPlayer.xwg;

import com.framework.log.P;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
//import android.util.Log;
import android.view.KeyEvent;

public class MediaButtonReceiver extends BroadcastReceiver
{
         private static final String TAG = new
         String("MediaButtonReceiver");
        @Override
        public void onReceive(Context context, Intent intent)
        {
	      if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction()))
	      {
		    KeyEvent key = (KeyEvent) intent
			          .getParcelableExtra(Intent.EXTRA_KEY_EVENT);
		    if (key.getAction() == KeyEvent.ACTION_DOWN)
		    {
			  TelephonyManager tm = (TelephonyManager) context
				        .getSystemService(Context.TELEPHONY_SERVICE);
			  if (tm.getCallState() == TelephonyManager.CALL_STATE_IDLE)
			  {
				 P.debug(TAG, "OnReceive, getKeyCode = "
				 + key.getKeyCode());
				switch (key.getKeyCode())
				{
				case KeyEvent.KEYCODE_HEADSETHOOK:
				        context.startService(new Intent(
					              MediaPlayerService.ACTION_PLAY_PAUSE));
				        break;
				case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
				        context.startService(new Intent(
					              MediaPlayerService.ACTION_PREVIOUS));
				        break;
				case KeyEvent.KEYCODE_MEDIA_NEXT:
				        context.startService(new Intent(
					              MediaPlayerService.ACTION_NEXT));
				        break;
				}
			  }
		    }
	      }
        }
}
