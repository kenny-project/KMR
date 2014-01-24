package com.kenny.hezuo;

import android.app.ProgressDialog;
import android.content.Context;

import com.nd.android.smartupdate.IUpdateCallback;

public class DownloadCallback implements IUpdateCallback{
	private ProgressDialog progressDialog ;
	private ProgressDialog installDialog ;
	private Context context;
	
	public DownloadCallback(Context context){
		this.context = context;
	}
	
	@Override
	public void onDownloadStart(long total) {
		try{
			if(progressDialog == null){
				progressDialog = new ProgressDialog(context);
			}
			if(installDialog != null){
				installDialog.hide();
			}
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setMessage("91助手下载开始");
			progressDialog.setProgress(0);
			progressDialog.setMax(100);
			progressDialog.show();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onDownloadSuccess() {
		try{
			if(progressDialog != null){
				progressDialog.setProgress(100);
				progressDialog.setMessage("91助手下载成功");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onFail() {
		try{
			if(progressDialog != null){
				progressDialog.setProgress(0);
				progressDialog.setMessage("91助手下载失败");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onDownloadProgress(long finish, long total) {
		try{
			if(progressDialog != null){
				int progress = (int)(total == 0 ? 0 : finish * 100 / total);
				progressDialog.setProgress(progress);
				progressDialog.setMessage("91助手下载进行中");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	public void onInstallStart() {
		try{
			if(progressDialog != null){
				progressDialog.setProgress(100);
				progressDialog.setMessage("91助手安装开始");
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	public void onInstallSuccess() {
		try{
			if(progressDialog != null){
				progressDialog.hide();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
