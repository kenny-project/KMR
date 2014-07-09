package com.byfen.market;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;

import com.kenny.Slidingmenu.MainUIActivity;
import com.work.market.net.Common;

/**
 * 
 * @author zhou
 * 
 */
public class LoadingActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.welcome);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// ����
		new Thread(new Runnable() {

			@Override
			public void run() {
				CreatPath();
				//GetData();
				toMain();
			}
		}).start();
		finish();
	}

	private void toMain() {
		Intent seta = new Intent(LoadingActivity.this, MainUIActivity.class);
		startActivity(seta);
		this.finish();
	}

	private void CreatPath() {
		if (Common.sdCardCheck()) {
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				String path = Environment.getExternalStorageDirectory()
						+ "/baifen";
				File file = new File(path);
				if (!file.exists()) {
					file.mkdir();
				}
				//
				// baifen/dowsload
				// baifen/img
				// baifen/temp

				File file1 = new File(path + "/dowsload");
				if (!file1.exists()) {
					file1.mkdir();
				}
				File file2 = new File(path + "/img");
				if (file2.exists()) {
					file2.mkdir();
				}
			}
		}
	}
}
