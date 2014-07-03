package com.work.market.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class KCommand {
	public static boolean isNetConnectNoMsg(Context context) {
		try {
			/*
			 * Check for Internet Connection (Through whichever interface)
			 */
			ConnectivityManager connManager = (ConnectivityManager) (context)
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = connManager.getActiveNetworkInfo();
			/******* EMULATOR HACK - false condition needs to be removed *****/
			// if (false && (netInfo == null || !netInfo.isConnected())){
			if ((netInfo == null || netInfo.isConnected() == false)) {
				// SendMessage((context), "No Internet Connection");
			} else {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
