/*
Copyright 2011-2013 Pieter Pareit

This file is part of SwiFTP.

SwiFTP is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SwiFTP is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.kenny.swiftp.gui;

import java.net.InetAddress;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kenny.KFileManager.R;
import com.kenny.ppareit.swiftp.FTPServerService;

public class SwifFTPNotification{
    private static final String TAG = SwifFTPNotification.class.getSimpleName();
    private static final int NOTIFICATIONID = 7890;
    
	public static void setupNotification(Context context) {
		// http://developer.android.com/guide/topics/ui/notifiers/notifications.html
		
		// Get NotificationManager reference
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nm = (NotificationManager) context.getSystemService(ns);
		// Instantiate a Notification
		int icon = R.drawable.notification;
		CharSequence tickerText = context.getString(R.string.notif_server_starting);
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);

		// Define Notification's message and Intent
		CharSequence contentTitle = context.getString(R.string.notif_title);
		CharSequence contentText="";
		InetAddress address = FTPServerService.getLocalInetAddress();
		if (address != null)
		{
			String strAddress="ftp://" + address.getHostAddress() + ":"
					+ FTPServerService.getPort() + "/";
		 contentText = context.getString(R.string.notif_text)+strAddress;
		}
		
		Intent notificationIntent = new Intent(context, SwifFtpMain.class);
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, 
				notificationIntent, 0);
		notification.setLatestEventInfo(context.getApplicationContext(), 
				contentTitle, contentText, contentIntent);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		
		// Pass Notification to NotificationManager
		//notificationMgr.notify(0, notification);
		nm.notify(NOTIFICATIONID, notification);
		Log.d(TAG,"Notication setup done");
	}
	public static void clearNotification(Context context) {
        Log.d(TAG, "kenny Clearing the notifications");
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nm = (NotificationManager) context.getSystemService(ns);
        nm.cancelAll();
        Log.d(TAG, "kenny Cleared notification");
    }
}
