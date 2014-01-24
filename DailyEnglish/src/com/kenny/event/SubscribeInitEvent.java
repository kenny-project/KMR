package com.kenny.event;

import android.content.Context;
import android.util.Log;

import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;
import com.example.android.trivialdrivesample.util.Inventory;
import com.example.android.trivialdrivesample.util.Purchase;
import com.kenny.struct.AbsEvent;
import com.kenny.util.Utils;

public class SubscribeInitEvent extends AbsEvent {
	public SubscribeInitEvent(Context main) {
		super(main);
	}
	@Override
	public void ok() {
		Log.v("wmh", "SubscribeInitEvent:Run");
		try
		{
			onCreate();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	static final String TAG = "SubscribeInitEvent";
	boolean mIsPremium = false;
	static final String SKU_PREMIUM = "premium";
	static final int RC_REQUEST = 10001;
	IabHelper mHelper;

	public void onCreate() {

		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnMWS0GzGf2BZ+gbk/HNPuE6HMljvrfPGilBYoEfw3Wk0JNKm350DIPaqNkKHHnBZ/iECTt5cpa1nuHPHL2UIbfouQIzWeAfZ7apz97sdR1v5DehCFrB1Nq8OndrX5aIeYaw+Z6V2nDVEkORxudMv6kQ9HxeoURQXM8TrjVNlW8BPSfRnV+gf3AuR+79FBU2bYXDp+gYUcBFonF+p8wIpPaevp3vDstJ3I1zYEuvZ/ZL+g3IlnSgQ6KVjg0Ng6Qu+XlW27dP0v9vkPXAyyeAuowdOK2c5RS2CwIsQ/n09LxMEQfIHplWYOnNHQ7zL33TfQ6go1Mfdgr3KIHPFCFzT7wIDAQAB";

		Log.d(TAG, "Creating IAB helper.");
		mHelper = new IabHelper(context, base64EncodedPublicKey);
		mHelper.enableDebugLogging(true);
		Log.d(TAG, "Starting setup.");
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				Log.d(TAG, "Setup finished.");
				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					complain("Problem setting up in-app billing: " + result);
					onDestroy();
					return;
				}
				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null)
					return;
				// IAB is fully set up. Now, let's get an inventory of stuff we
				// own.
				Log.d(TAG, "Setup successful. Querying inventory.");
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
	}

	// Listener that's called when we finish querying the items and
	// subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result,
				Inventory inventory) {
			Log.d(TAG, "Query inventory finished.");

			// Have we been disposed of in the meantime? If so, quit.
			if (mHelper == null)
				return;

			// Is it a failure?
			if (result.isFailure()) {
				complain("Failed to query inventory: " + result);
				onDestroy();
				return;
			}
			Log.d(TAG, "Query inventory was successful.");
			/*
			 * Check for items we own. Notice that for each purchase, we check
			 * the developer payload to see if it's correct! See
			 * verifyDeveloperPayload().
			 */

			// Do we have the premium upgrade?
			Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
			if(premiumPurchase == null)
			{
				Utils.save(context, "bSubscribe", false);
			}
			Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
			onDestroy();
			Log.d(TAG, "Initial inventory query finished; enabling main UI.");
		}
	};

	public void onDestroy() {
		Log.d(TAG, "Destroying helper.");
		if (mHelper != null) {
			mHelper.dispose();
			mHelper = null;
		}
	}

	void complain(String message) {
		Log.e(TAG, "**** TrivialDrive Error: " + message);
		// alert("Error: " + message);
	}
	//
	// void alert(String message) {
	// AlertDialog.Builder bld = new AlertDialog.Builder(context);
	// bld.setMessage(message);
	// bld.setNeutralButton("OK", null);
	// Log.d(TAG, "Showing alert dialog: " + message);
	// bld.create().show();
	// }
}
