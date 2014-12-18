package com.example.test;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {
	private CustomDialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.button1).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CustomDialog.Builder customBuilder = new
		                CustomDialog.Builder(MainActivity.this);
		            customBuilder.setTitle("标题")
		                .setMessage("提示内容")
		                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int which) {
		                    	
		                    }
		                })
		                .setPositiveButton("确定", 
		                        new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int which) {
		                        dialog.dismiss();
		                    }
		                });
		            dialog = customBuilder.create();
		            dialog.show();
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
