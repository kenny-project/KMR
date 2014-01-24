package com.kenny.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;
import com.example.android.trivialdrivesample.util.Inventory;
import com.example.android.trivialdrivesample.util.Purchase;
import com.kenny.Application.KApp;
import com.kenny.dailyenglish.R;
import com.kenny.util.Utils;
import com.umeng.analytics.MobclickAgent;

public class SubscribePage extends Activity {

	private Button btnBack;
	private Button btSubscribe, btRecoverybuy;
	private RelativeLayout rlTitle;
	private boolean bIabHelperError = false;
	// Debug tag, for logging
	static final String TAG = "wmh";
	// Does the user have the premium upgrade?
	boolean mIsPremium = false;

	// Does the user have an active subscription to the infinite gas plan?
	boolean mSubscribedToInfiniteGas = false;

	// SKUs for our products: the premium upgrade (non-consumable) and gas
	// (consumable)
	// static final String SKU_PREMIUM = "premium";
	static final String SKU_PREMIUM = "test5";
	static final String SKU_GAS = "gas";

	// SKU for our subscription (infinite gas)
	static final String SKU_INFINITE_GAS = "infinite_gas";

	// (arbitrary) request code for the purchase flow
	static final int RC_REQUEST = 10001;

	// Graphics for the gas gauge
	// static int[] TANK_RES_IDS = { R.drawable.gas0, R.drawable.gas1,
	// R.drawable.gas2,
	// R.drawable.gas3, R.drawable.gas4 };

	// How many units (1/4 tank is our unit) fill in the tank.
	static final int TANK_MAX = 4;

	// Current amount of gas in tank, in units
	int mTank;

	// The helper object
	IabHelper mHelper;

	private void initView() {
		btnBack = (Button) findViewById(R.id.sf_back);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
			}
		});

		btSubscribe = (Button) findViewById(R.id.btSubscribe);
		btSubscribe.setEnabled(true);
		btSubscribe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onUpgradeAppButtonClicked(v);
				MobclickAgent.onEvent(SubscribePage.this, "Click-subscribeVOA");
			}
		});

		btRecoverybuy = (Button) findViewById(R.id.btRecoverybuy);
		btRecoverybuy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "Setup successful. Querying inventory.");
				MobclickAgent.onEvent(SubscribePage.this,
						"Click-restoresubscription");
				if (mHelper != null) {
					mHelper.queryInventoryAsync(mGotInventoryListener);
				} else {
					Toast.makeText(
							SubscribePage.this,
							SubscribePage.this.getResources().getString(
									R.string.msg_Recovery_buy_Failure),
							Toast.LENGTH_LONG).show();
				}
			}
		});
		KApp app = (KApp) getApplicationContext();
		rlTitle = (RelativeLayout) findViewById(R.id.sa_head);
		rlTitle.setBackgroundColor(app.colorFactory.getColor());
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subcribepage);
		initView();
		MobclickAgent.onEvent(this, "Appear-subscribepage");
		// load game data
		loadData();
		/*
		 * base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY (that
		 * you got from the Google Play developer console). This is not your
		 * developer public key, it's the *app-specific* public key.
		 * 
		 * Instead of just storing the entire literal string here embedded in
		 * the program, construct the key at runtime from pieces or use bit
		 * manipulation (for example, XOR with some other string) to hide the
		 * actual key. The key itself is not secret information, but we don't
		 * want to make it easy for an attacker to replace the public key with
		 * one of their own and then fake messages from the server.
		 */
		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnMWS0GzGf2BZ+gbk/HNPuE6HMljvrfPGilBYoEfw3Wk0JNKm350DIPaqNkKHHnBZ/iECTt5cpa1nuHPHL2UIbfouQIzWeAfZ7apz97sdR1v5DehCFrB1Nq8OndrX5aIeYaw+Z6V2nDVEkORxudMv6kQ9HxeoURQXM8TrjVNlW8BPSfRnV+gf3AuR+79FBU2bYXDp+gYUcBFonF+p8wIpPaevp3vDstJ3I1zYEuvZ/ZL+g3IlnSgQ6KVjg0Ng6Qu+XlW27dP0v9vkPXAyyeAuowdOK2c5RS2CwIsQ/n09LxMEQfIHplWYOnNHQ7zL33TfQ6go1Mfdgr3KIHPFCFzT7wIDAQAB";

		// Some sanity checks to see if the developer (that's you!) really
		// followed the
		// instructions to run this sample (don't put these checks on your app!)
		// if (base64EncodedPublicKey.contains("CONSTRUCT_YOUR"))
		// {
		// throw new
		// RuntimeException("Please put your app's public key in MainActivity.java. See README.");
		// }
		// if (getPackageName().startsWith("com.example")) {
		// throw new
		// RuntimeException("Please change the sample's package name! See README.");
		// }

		// Create the helper, passing it our context and the public key to
		// verify signatures with
		try {
			Log.d(TAG, "Creating IAB helper.");
			mHelper = new IabHelper(this, base64EncodedPublicKey);

			// enable debug logging (for a production application, you should
			// set
			// this to false).
			mHelper.enableDebugLogging(true);
			// Start setup. This is asynchronous and the specified listener
			// will be called once setup completes.
			Log.d(TAG, "Starting setup.");
			mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
				public void onIabSetupFinished(IabResult result) {
					Log.d(TAG, "Setup finished.");
					if (!result.isSuccess()) {
						mHelper = null;
						// Oh noes, there was a problem.
						complain("Problem setting up in-app billing: " + result);
						return;
					}
					// Have we been disposed of in the meantime? If so, quit.
					if (mHelper == null)
						return;
					// IAB is fully set up. Now, let's get an inventory of stuff
					// we
					// own.
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			// finish();
			// alert("您的手机不支持付费,请检查Google Play帐户");
			bIabHelperError = true;
			mHelper = null;
		}
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
			if (mHelper == null || result.isFailure()) {
				complain("Failed to query inventory: " + result);
				Toast.makeText(
						SubscribePage.this,
						SubscribePage.this.getResources().getString(
								R.string.msg_Recovery_buy_Failure),
						Toast.LENGTH_LONG).show();
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
			mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
			Utils.save(SubscribePage.this, "bSubscribe", mIsPremium);
			Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
			if (mIsPremium) {
				Toast.makeText(
						SubscribePage.this,
						SubscribePage.this.getResources().getString(
								R.string.msg_Recovery_buy_Success),
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(
						SubscribePage.this,
						SubscribePage.this.getResources().getString(
								R.string.msg_Recovery_buy_Failure),
						Toast.LENGTH_LONG).show();
			}
			// Do we have the infinite gas plan?
			Purchase infiniteGasPurchase = inventory
					.getPurchase(SKU_INFINITE_GAS);
			mSubscribedToInfiniteGas = (infiniteGasPurchase != null && verifyDeveloperPayload(infiniteGasPurchase));
			Log.d(TAG, "User "
					+ (mSubscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE")
					+ " infinite gas subscription.");
			if (mSubscribedToInfiniteGas)
				mTank = TANK_MAX;

			// Check for gas delivery -- if we own gas, we should fill up the
			// tank immediately
			Purchase gasPurchase = inventory.getPurchase(SKU_GAS);
			if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
				Log.d(TAG, "We have gas. Consuming it.");
				mHelper.consumeAsync(inventory.getPurchase(SKU_GAS),
						mConsumeFinishedListener);
				return;
			}

			// updateUi();
			// setWaitScreen(false);
			Log.d(TAG, "Initial inventory query finished; enabling main UI.");
		}
	};

	// User clicked the "Buy Gas" button
	public void onBuyGasButtonClicked(View arg0) {
		Log.d(TAG, "Buy gas button clicked.");

		if (mSubscribedToInfiniteGas) {
			complain("No need! You're subscribed to infinite gas. Isn't that awesome?");
			return;
		}

		if (mTank >= TANK_MAX) {
			complain("Your tank is full. Drive around a bit!");
			return;
		}

		// launch the gas purchase UI flow.
		// We will be notified of completion via mPurchaseFinishedListener
		// setWaitScreen(true);
		Log.d(TAG, "Launching purchase flow for gas.");

		/*
		 * TODO: for security, generate your payload here for verification. See
		 * the comments on verifyDeveloperPayload() for more info. Since this is
		 * a SAMPLE, we just use an empty string, but on a production app you
		 * should carefully generate this.
		 */
		String payload = "";

		mHelper.launchPurchaseFlow(this, SKU_GAS, RC_REQUEST,
				mPurchaseFinishedListener, payload);
	}

	// User clicked the "Upgrade to Premium" button.
	public void onUpgradeAppButtonClicked(View arg0) {
		Log.d(TAG,
				"Upgrade button clicked; launching purchase flow for upgrade.");
		/*
		 * TODO: for security, generate your payload here for verification. See
		 * the comments on verifyDeveloperPayload() for more info. Since this is
		 * a SAMPLE, we just use an empty string, but on a production app you
		 * should carefully generate this.
		 */

		if (mHelper != null) {
			String payload = "test1";
			try {
				mHelper.launchPurchaseFlow(this, SKU_PREMIUM, RC_REQUEST,
						mPurchaseFinishedListener, payload);
			} catch (Exception e) {
				e.printStackTrace();
				alert(this
						.getString(R.string.subcribepage_msg_googleplay_pay_error));
			}
		} else {
			alert(this
					.getString(R.string.subcribepage_msg_googleplay_pay_notsupport));
		}
	}

	// "Subscribe to infinite gas" button clicked. Explain to user, then start
	// purchase
	// flow for subscription.
	// 无限订阅
	public void onInfiniteGasButtonClicked(View arg0) {
		if (!mHelper.subscriptionsSupported()) {
			complain("Subscriptions not supported on your device yet. Sorry!");
			return;
		}
		/*
		 * TODO: for security, generate your payload here for verification. See
		 * the comments on verifyDeveloperPayload() for more info. Since this is
		 * a SAMPLE, we just use an empty string, but on a production app you
		 * should carefully generate this.
		 */
		String payload = "";
		// setWaitScreen(true);
		Log.d(TAG, "Launching purchase flow for infinite gas subscription.");
		mHelper.launchPurchaseFlow(this, SKU_INFINITE_GAS,
				IabHelper.ITEM_TYPE_SUBS, RC_REQUEST,
				mPurchaseFinishedListener, payload);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + ","
				+ data);
		if (mHelper == null)
			return;

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			Log.d(TAG, "onActivityResult handled by IABUtil.");
		}
	}

	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p) {
		String payload = p.getDeveloperPayload();
		Log.v("wmh", "verifyDeveloperPayload:payload" + payload);
		/*
		 * TODO: verify that the developer payload of the purchase is correct.
		 * It will be the same one that you sent when initiating the purchase.
		 * 
		 * WARNING: Locally generating a random string when starting a purchase
		 * and verifying it here might seem like a good approach, but this will
		 * fail in the case where the user purchases an item on one device and
		 * then uses your app on a different device, because on the other device
		 * you will not have access to the random string you originally
		 * generated.
		 * 
		 * So a good developer payload has these characteristics:
		 * 
		 * 1. If two different users purchase an item, the payload is different
		 * between them, so that one user's purchase can't be replayed to
		 * another user.
		 * 
		 * 2. The payload must be such that you can verify it even when the app
		 * wasn't the one who initiated the purchase flow (so that items
		 * purchased by the user on one device work on other devices owned by
		 * the user).
		 * 
		 * Using your own server to store and verify developer payloads across
		 * app installations is recommended.
		 */
		return true;
	}

	/**
	 * Callback for when a purchase is finished 完成购买后回调
	 */
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

			try {
				Log.d(TAG, "result.isSuccess()="+result.isSuccess()+"Purchase finished: " + result + ", purchase: "
						+ purchase);

				// if we were disposed of in the meantime, quit.
				if (mHelper == null)
					return;
				// if (result.isFailure()) {
				// complain("mPurchaseFinishedListener.Error result.isFailure:"
				// + result);
				// // setWaitScreen(false);
				// return;
				// }
				// if (!verifyDeveloperPayload(purchase)) {
				// complain("Error purchasing. Authenticity verification failed.");
				// // setWaitScreen(false);
				// return;
				// }
				
				if (purchase == null || purchase.getSku() == null) {
					mHelper.queryInventoryAsync(mGotInventoryListener);
					return;
				}
				if (purchase.getSku().equals(SKU_PREMIUM)) {
					// bought the premium upgrade!
					Log.d(TAG,
							"Purchase is premium upgrade. Congratulating user.");
					Log.v(TAG, "Thank you for upgrading to premium!");
					mIsPremium = true;
					Utils.save(SubscribePage.this, "bSubscribe", mIsPremium);
				}
				// if (purchase.getSku().equals(SKU_GAS)) {
				// // bought 1/4 tank of gas. So consume it.
				// Log.d(TAG, "Purchase is gas. Starting gas consumption.");
				// mHelper.consumeAsync(purchase, mConsumeFinishedListener);
				// } else if (purchase.getSku().equals(SKU_PREMIUM)) {
				// // bought the premium upgrade!
				// Log.d(TAG,
				// "Purchase is premium upgrade. Congratulating user.");
				// Log.v(TAG,"Thank you for upgrading to premium!");
				// mIsPremium = true;
				// Utils.save(SubscribePage.this, "bSubscribe", mIsPremium);
				// // updateUi();
				// // setWaitScreen(false);
				// } else if (purchase.getSku().equals(SKU_INFINITE_GAS)) {
				// // bought the infinite gas subscription
				// Log.d(TAG, "Infinite gas subscription purchased.");
				// alert("Thank you for subscribing to infinite gas!");
				// mSubscribedToInfiniteGas = true;
				// mTank = TANK_MAX;
				// // updateUi();
				// // setWaitScreen(false);
				// }

			} catch (Exception e) {
				e.printStackTrace();
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		}
	};

	/**
	 * 消费完成时调用 Called when consumption is complete
	 */
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			Log.d(TAG, "Consumption finished. Purchase: " + purchase
					+ ", result: " + result);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;
			// We know this is the "gas" sku because it's the only one we
			// consume,
			// so we don't check which sku was consumed. If you have more than
			// one
			// sku, you probably should check...
			if (result.isSuccess()) {
				// successfully consumed, so we apply the effects of the item in
				// our
				// game world's logic, which in our case means filling the gas
				// tank a bit
				Log.d(TAG, "Consumption successful. Provisioning.");
				mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
				Utils.save(SubscribePage.this, "bSubscribe", result.isSuccess());
				saveData();

				Toast.makeText(
						SubscribePage.this,
						SubscribePage.this
								.getString(R.string.subcribepage_msg_pay_success)
								+ purchase.getDeveloperPayload(),
						Toast.LENGTH_LONG).show();
			} else {
				complain("Error while consuming: " + result);
				// alert("支付失败");
				Utils.save(SubscribePage.this, "bSubscribe", false);
				Toast.makeText(
						SubscribePage.this,
						SubscribePage.this
								.getString(R.string.subcribepage_msg_pay_fail)
								+ result, Toast.LENGTH_LONG).show();
			}
			Log.d(TAG, "End consumption flow.");
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "Destroying helper.");
		try {
			if (mHelper != null) {
				mHelper.dispose();
				mHelper = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void complain(String message) {
		Log.e(TAG, "**** TrivialDrive Error: " + message);
		// alert("Error: " + message);
	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		Log.d(TAG, "Showing alert dialog: " + message);
		bld.create().show();
	}

	void saveData() {
		/*
		 * WARNING: on a real application, we recommend you save data in a
		 * secure way to prevent tampering. For simplicity in this sample, we
		 * simply store the data using a SharedPreferences.
		 */
		SharedPreferences.Editor spe = getPreferences(MODE_PRIVATE).edit();
		spe.putInt("tank", mTank);
		spe.commit();
		Log.d(TAG, "Saved data: tank = " + String.valueOf(mTank));
	}

	void loadData() {
		SharedPreferences sp = getPreferences(MODE_PRIVATE);
		mTank = sp.getInt("tank", 2);
		Log.d(TAG, "Loaded data: tank = " + String.valueOf(mTank));
	}

	/*
	 * 无用 Drive button clicked. Burn gas!
	 */
	public void onDriveButtonClicked(View arg0) {
		Log.d(TAG, "Drive button clicked.");
		if (!mSubscribedToInfiniteGas && mTank <= 0) {
			alert("Oh, no! You are out of gas! Try buying some!");
		} else {
			if (!mSubscribedToInfiniteGas)
				--mTank;
			saveData();
			alert("Vroooom, you drove a few miles.");
			// updateUi();
			Log.d(TAG, "Vrooom. Tank is now " + mTank);
		}
	}
}
