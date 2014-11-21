package com.qin.ammp;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private static String TAG = "AM_MEMORYIPROCESS" ;
	
	private ActivityManager mActivityManager = null ;
	
	private TextView tvAvailMem  ;
	private Button btProcessInfo ;
	private Button btRunningApp ;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        tvAvailMem = (TextView)findViewById(R.id.tvAvailMemory) ;
        btProcessInfo =(Button)findViewById(R.id.btProcessInfo);
        //��ת����ʾ������Ϣ����
        btProcessInfo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,BrowseProcessInfoActivity.class);
				startActivity(intent);
			}
		});               
        
        btRunningApp = (Button)findViewById(R.id.btRunningApp) ;
        btRunningApp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,BrowseRunningAppActivity.class);
				startActivity(intent);
			}
		});
        //���ActivityManager����Ķ���
        mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
      
        //��ÿ����ڴ���Ϣ
        String availMemStr = getSystemAvaialbeMemorySize();
        Log.i(TAG, "The Availabel Memory Size is"+availMemStr); 
        //��ʾ
        tvAvailMem.setText(availMemStr); 
        
    }
    //���ϵͳ�����ڴ���Ϣ
    private String getSystemAvaialbeMemorySize(){
    	//���MemoryInfo����
        MemoryInfo memoryInfo = new MemoryInfo() ;
        //���ϵͳ�����ڴ棬������MemoryInfo������
        mActivityManager.getMemoryInfo(memoryInfo) ;
        long memSize = memoryInfo.availMem ;
        
        //�ַ�����ת��
        String availMemStr = formateFileSize(memSize);
        
        return availMemStr ;
    }
  
    //����ϵͳ�������ַ���ת�� long -String KB/MB
    private String formateFileSize(long size){
    	return Formatter.formatFileSize(MainActivity.this, size); 
    }
    
}