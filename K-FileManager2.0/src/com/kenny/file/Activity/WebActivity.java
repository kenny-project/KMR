package com.kenny.file.Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.http.protocol.HTTP;

import com.kenny.KFileManager.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.ZoomControls;

public class WebActivity extends Activity {
	
	private WebView webView;
	private RelativeLayout loadingLayout,webLayout;
	private ZoomControls zoomControls;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web);
		
		webView = (WebView)findViewById(R.id.webkit);
		loadingLayout = (RelativeLayout)findViewById(R.id.loadingLayout);
		webLayout = (RelativeLayout)findViewById(R.id.weblayout);
		zoomControls = (ZoomControls)findViewById(R.id.zoomControls);

//		webView.invokeZoomPicker();
		
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		// ���������
//        webView.clearCache(true);
		new MyAsyncTask().execute("");
	}
	
//	
//	protected void onResume() {
//		super.onResume();
//		new Thread(new Runnable() {
//			
//			
//			public void run() {
//				try {
//					Thread.sleep(10);
//					final Looper looper = Looper.getMainLooper();
//					Handler handler = new Handler(looper){
//						public void handleMessage(android.os.Message msg) {
//							reading();
//						};
//					};
//					handler.sendEmptyMessage(0);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}).start();
//	}
	
	private void reading(){
		String filePath = getIntent().getStringExtra("filePath");
		if (filePath != null) {
			webView.loadData(readWebDataToStringFromPath(filePath, new FileReadOverBack() {
				
				public void fileReadOver() {
//					loadingLayout.setVisibility(View.GONE);
//					webLayout.setVisibility(View.VISIBLE);
				}
			}), "text/html", HTTP.UTF_8);
		} else {
			new AlertDialog.Builder(WebActivity.this).setTitle("�����").setMessage("��ȡ�ļ�·�����!").setPositiveButton("����", new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					WebActivity.this.finish();
				}
			});
		}
	}
	
	private String readWebDataToStringFromPath(String path,final FileReadOverBack fileReadOverBack){
		File file = new File(path);
		StringBuffer stringBuffer = new StringBuffer();
		try {
			FileInputStream inputStream = new FileInputStream(file);
			byte[] bytes = new byte[1024];
			int readCount = 0;
			while ((readCount = inputStream.read(bytes)) > 0) {
				stringBuffer.append(new String(bytes, 0, readCount));
			}
			fileReadOverBack.fileReadOver();
		} catch (FileNotFoundException e) {
			return "�ļ�������!";
		} catch (IOException e) {
			return "�ļ���ȡ����!";
		}
		return stringBuffer.toString();
	}
	
	interface FileReadOverBack{
		void fileReadOver();
	}
	
	class MyAsyncTask extends AsyncTask<String, String, String>{

		
		protected void onPreExecute() {
			super.onPreExecute();
			loadingLayout.setVisibility(View.VISIBLE);
			webLayout.setVisibility(View.GONE);
		}
		
		
		protected String doInBackground(String... params) {
			reading();
			return null;
		}
		
		
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			loadingLayout.setVisibility(View.GONE);
			webLayout.setVisibility(View.VISIBLE);
			
			// �Ŵ�ť
			zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
				
				
				public void onClick(View v) {
					Log.d("debug","big");
					webView.zoomIn();
				}
			});
			// ��С��ť
			zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
				
				
				public void onClick(View v) {
					Log.d("debug","small");
					webView.zoomOut();
				}
			});
		}
		
	}
}
