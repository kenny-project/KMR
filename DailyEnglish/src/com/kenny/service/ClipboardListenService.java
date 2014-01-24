package com.kenny.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.ClipboardManager;
import android.util.Log;
import android.widget.Toast;

public class ClipboardListenService extends Service {
	private static String  pre_txt="";
	private static String now_txt="";
	private ClipboardManager clipboard;
	private Context context;
	private boolean run = false;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.d("zk", "oncreat");
		clipboard = ((ClipboardManager)getSystemService("clipboard"));
		if(clipboard.hasText()){
			CharSequence cs = clipboard.getText();
			pre_txt = cs.toString();
		}
		run = true;
		thread.start();
		super.onCreate();
		
	}
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		Log.d("zk", "onstar");
		super.onStart(intent, startId);
		
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.d("zk", "ondestory");
		if(thread.isAlive()){
//			thread.stop();
			run = false;
		}
		super.onDestroy();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.d("zk", "onstarcommed");
//		    while (true)
//		    {
//		      return 1;
//		    }
//		    for(int i = 0;i<50;i++){
//		    	Intent localIntent = new Intent(this, ClipboardListenService.class);
////			    localIntent.setAction("STOP_SERVICE");
//			    startService(localIntent);
//		    }
//		return 1;
		return super.onStartCommand(intent, flags, startId);
	}
	private Thread thread = new Thread(){
		@Override
		public void run() {
			while (run){
				try {
					sleep(500);
					handler.sendEmptyMessage(0);
					Log.d("zk", "run");
					
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
	};
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if(clipboard.hasText()){
					now_txt = clipboard.getText().toString();
					if(!now_txt.equals(pre_txt)&&!now_txt.equals("")){
						pre_txt = now_txt;
//						Toast.makeText(getBaseContext(), clipboard.getText(), Toast.LENGTH_SHORT).show();
						Intent intent = new Intent("APowerword");
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("word", now_txt);
						intent.putExtra("x", 0);// x坐标
						intent.putExtra("y", 0);// y坐标
						intent.putExtra("intention", 0);// intention 为0 为开启
						startActivity(intent);
					}
				}
				break;

			default:
				break;
			}
			
		};
	};
}
