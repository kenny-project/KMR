package com.byfen.market;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.kenny.file.tools.ApkTools;
/**
 * °²×°App
 * @author kenny
 */
public class InstallApkActivity extends Activity 
{
//	Handler handler=new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	      Intent intent = getIntent();
	      Uri uri = (Uri) intent.getData();
	      if (uri != null)
	      {
	    	  String appPath = uri.getPath();
	         ApkTools.InstallApk(this.getApplicationContext(), appPath);
	      }
	      finish();
//	      handler.postAtTime(new Runnable()
//		{
//			
//			@Override
//			public void run()
//			{
//						
//			}
//		}, 5000);
		
	}
}
