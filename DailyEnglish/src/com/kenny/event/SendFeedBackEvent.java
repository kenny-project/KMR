package com.kenny.event;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.kenny.activity.MainOld;
import com.kenny.net.RequestEntry;
import com.kenny.struct.AbsEvent;

public class SendFeedBackEvent extends AbsEvent {
	private HttpPost request;

	public SendFeedBackEvent(MainOld m) {
		super(m);
		// TODO Auto-generated constructor stub
	}

	public SendFeedBackEvent(MainOld m, HttpPost request) {
		super(m);
		this.request = request;
	}

	@Override
	public void ok() {
		// TODO Auto-generated method stub
		DefaultHttpClient httpClient = new DefaultHttpClient();
		RequestEntry entry = new RequestEntry(RequestEntry.FEEDBACK_TYPE, this.request);
		try {
			entry._response = httpClient.execute(entry._request);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
