package com.kenny.receiver;

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.kenny.activity.V6LoadPage;
import com.kenny.dailyenglish.R;
import com.kenny.util.Const;
import com.kenny.util.Utils;

public class AlarmReceiver extends BroadcastReceiver {
	Notification noti;
	NotificationManager notiManager;
	public static final int MIN = 30;
	public static final int HOR = 20;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		try {

			Log.e("wmh", "alarm ok~~~~~~~~~~~~");
			if (noti == null) {
				noti = new Notification();
			}
			if (notiManager == null) {
				String service = Context.NOTIFICATION_SERVICE;
				notiManager = (NotificationManager) context
						.getSystemService(service);
			}
			boolean bSwitch = Utils.get(context,
					Const.CONFIG_SETTING_LEARNINGREMIND_SWITCH, false);
			Log.e("wmh", "bSwitch="+bSwitch);
			if (!bSwitch) {
				Utils.CleanAlerm(context);
				return;
			}
			noti.icon = R.drawable.icon;
			noti.when = System.currentTimeMillis();
			noti.tickerText = context.getString(R.string.alarm_message);
			PendingIntent pIntent = PendingIntent.getActivity(context, 0,
					new Intent(context, V6LoadPage.class),
					PendingIntent.FLAG_UPDATE_CURRENT);
			noti.contentIntent = pIntent;
			noti.setLatestEventInfo(context,
					context.getString(R.string.app_name),
					context.getString(R.string.alarm_message), pIntent);
			noti.flags |= Notification.FLAG_AUTO_CANCEL;
			noti.defaults = Notification.DEFAULT_SOUND;
			notiManager.notify(1035, noti);
			Toast.makeText(context, context.getString(R.string.alarm_message),
					Toast.LENGTH_SHORT).show();

			// Calendar cal = Calendar.getInstance();
			// cal.setTimeInMillis(System.currentTimeMillis());
			// if (MIN == cal.get(Calendar.MINUTE) && HOR ==
			// cal.get(Calendar.HOUR_OF_DAY)){
			// notiManager.notify(1035, noti);
			// Toast.makeText(context,
			// context.getString(R.string.alarm_message),
			// Toast.LENGTH_SHORT).show();
			// }
			// Utils.setAlerm(context, HOR, MIN);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
