package com.kenny.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.kenny.Application.KApp;
import com.kenny.dailyenglish.R;
import com.umeng.analytics.MobclickAgent;

public class V6About extends Activity {

	private View btnBack;
	private TextView tvVersion;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Auto-generated constructor stub
		setContentView(R.layout.v6about);
		onLoad();
	}

	public void onLoad() {
		// TODO Auto-generated method stub
		initView();
	}

	private void initView()
	{
		View Head = findViewById(R.id.Head);
		KApp app = (KApp) getApplicationContext();
		Head.setBackgroundColor(app.colorFactory.getColor());
		
		btnBack =  findViewById(R.id.sa_back);
		btnBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		Spanned html = Html.fromHtml(
				getResources().getString(R.string.sa_ciba_version));
				
		tvVersion = (TextView) findViewById(R.id.sa_version);
		tvVersion.setText(html);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	
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
