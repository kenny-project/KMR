package com.kenny.comui;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.kenny.interfaces.IOnProgressChanged;
import com.kenny.interfaces.IOnReceivedTitle;

public class MyWebChromeClient extends WebChromeClient{
	private IOnProgressChanged onProgressChanged;
	private IOnReceivedTitle onReceivedTitle;
	
	public IOnProgressChanged getOnProgressChanged() {
		return onProgressChanged;
	}

	public void setOnProgressChanged(IOnProgressChanged onProgressChanged) {
		this.onProgressChanged = onProgressChanged;
	}

	public IOnReceivedTitle getOnReceivedTitle() {
		return onReceivedTitle;
	}

	public void setOnReceivedTitle(IOnReceivedTitle onReceivedTitle) {
		this.onReceivedTitle = onReceivedTitle;
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		// TODO Auto-generated method stub
		super.onProgressChanged(view, newProgress);
		if (onProgressChanged != null){
			onProgressChanged.onProgressChanged(view, newProgress);
		}
	}
	
	@Override
	public void onReceivedTitle(WebView view, String title) {
		// TODO Auto-generated method stub
		super.onReceivedTitle(view, title);
		if (onReceivedTitle != null){
			onReceivedTitle.onReceivedTitle(view, title);
		}
	}
}
