package com.kenny.interfaces;

import android.webkit.WebView;

/**
 * 网络浏览器数据下载接口
 * 
 * @author WangMinghui
 * 
 */
public interface IUrlLoading
{
	public void Init();
	public boolean shouldOverrideUrlLoading(WebView view, String strUrl);

	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl);

	public void onPageFinished(WebView view, String url);
}
