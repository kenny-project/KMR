package com.kenny.activity;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.kenny.dailyenglish.R;
import com.kenny.event.InitEvent;
import com.kenny.util.Const;
import com.kenny.util.Utils;
import com.umeng.analytics.MobclickAgent;

public class V6LoadPage extends Activity {

	Utils utils = new Utils();
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.load_page);
		onLoad();
		new InitEvent(this).run();
	}

	public void onLoad() 
	{
		new Thread() 
		{
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Utils.init(V6LoadPage.this);
				handler.postDelayed(new Runnable() {
					public void run() {
						
						File files = new File(Const.CATCH_DIRECTORY);
						boolean hasNomedia = false;
						if (files != null) {
							if (files.list() != null) {
								for (String file : files.list()) {
									if (file.equals(".nomedia")) {
										hasNomedia = true;
									}
								}
							} else {
								hasNomedia = false;
							}

							if (hasNomedia == false) {
								File nomedia = new File(Const.CATCH_DIRECTORY
										+ ".nomedia");
								try {
									nomedia.createNewFile();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						finish();
						overridePendingTransition(R.anim.left_in,
								R.anim.left_out);
						
						Intent intent = new Intent(V6LoadPage.this,
								InfoHomePage.class);
						intent.putExtras(getIntent());
						startActivity(intent);
					};
				}, 2500);
			}
		}.start();
	}

	Handler handler = new Handler();

	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
}
