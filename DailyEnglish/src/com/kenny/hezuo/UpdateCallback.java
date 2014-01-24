package com.kenny.hezuo;

import android.app.ProgressDialog;
import android.content.Context;

import com.nd.android.smartupdate.IUpdateCallback;

public class UpdateCallback implements IUpdateCallback{
	
	private ProgressDialog progressDialog ;
	private ProgressDialog installDialog ;
	private Context context;
	
	public UpdateCallback(Context context){
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
			progressDialog.setMessage("应用下载开始");
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
				progressDialog.setMessage("应用下载成功");
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
				progressDialog.setMessage("应用更新失败");
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
				progressDialog.setMessage("应用下载进行中");
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
				progressDialog.hide();
			}
			if(installDialog == null){
				installDialog = new ProgressDialog(context);
				installDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				installDialog.setMessage("应用安装开始,请稍候...");
			}
			if(!installDialog.isShowing())
				installDialog.show();
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
			if(installDialog != null && installDialog.isShowing()){
				installDialog.hide();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
