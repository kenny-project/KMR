package com.kenny.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kenny.Application.KApp;
import com.kenny.dailyenglish.R;
import com.kenny.event.ShowOrHideInputpadEvent;
import com.kenny.net.RequestEntry;
import com.kenny.syseng.SysEng;
import com.kenny.util.Utils;
import com.umeng.analytics.MobclickAgent;

public class Feedback extends Activity {

	private View btnBack;
	private Button btnSend;
	private EditText etContent;
	private EditText etEmail;
	private TextView tvContent;
	private TextView tvEmail;
	private long feedbackTime = 0;
	private RelativeLayout rlTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO Auto-generated constructor stub
		setContentView(R.layout.v6eedback);
		onLoad();
	}

	public void onLoad() {
		// TODO Auto-generated method stub
		initView();
	}

	private void initView() {
		btnBack = findViewById(R.id.sf_back);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (etContent.hasFocus()) {
					SysEng.getInstance().runEvent(
							new ShowOrHideInputpadEvent(Feedback.this,
									etContent, false));
				} else if (etEmail.hasFocus()) {
					SysEng.getInstance().runEvent(
							new ShowOrHideInputpadEvent(Feedback.this,
									etContent, false));
				}

				finish();
			}
		});

		btnSend = (Button) findViewById(R.id.sf_submit);
		btnSend.setEnabled(true);
		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (System.currentTimeMillis() - feedbackTime < 2000) {
					return;
				}
				Log.e("CHANNEL_NUM",
						"CHANNEL_NUM is "
								+ Utils.getChannelNum(Feedback.this));
				feedbackTime = System.currentTimeMillis();
				sendFeedBack();
			}
		});

		etContent = (EditText) findViewById(R.id.sf_content);
		etContent.setText("");
		KApp app = (KApp) getApplicationContext();
		etContent.setTextColor(app.colorFactory.getColor());
		etEmail = (EditText) findViewById(R.id.sf_email);
		tvContent = (TextView) findViewById(R.id.sf_content_text);
		tvEmail = (TextView) findViewById(R.id.sf_email_text);
		String email = "";
		etEmail.setText(email);
		etEmail.setTextColor(app.colorFactory.getColor());
		rlTitle = (RelativeLayout) findViewById(R.id.sa_head);
		rlTitle.setBackgroundColor(app.colorFactory.getColor());
	}

	private void sendFeedBack() {
		if (Utils.isNetConnect(Feedback.this) == false) {
			return;
		}
		final String feedbackContent = etContent.getText().toString();
		final String feedbackEmail = etEmail.getText().toString();
		if (feedbackContent.length() == 0) {
			tvContent.setTextColor(Color.RED);

			showFeedbackContentErrorToast();

			return;
		} else {

			tvContent.setTextColor(Color.BLACK);
		}

		if (feedbackEmail.length() == 0) {

			tvEmail.setTextColor(Color.RED);
			showFeedbackContentErrorToast();
			return;
		} else {
			tvEmail.setTextColor(Color.BLACK);
		}

		if (!checkEmailadd(feedbackEmail)) {

			tvEmail.setTextColor(Color.RED);
			showFeedBackEmailErrorToast();
			return;
		} else {
			tvEmail.setTextColor(Color.BLACK);
		}
		SysEng.getInstance().runEvent(
				new ShowOrHideInputpadEvent(Feedback.this, etContent, false));

		btnSend.setEnabled(false);
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				submitFeedback(feedbackContent, feedbackEmail);
			}
		}.start();

		showFeedBackSuccessToast();
		finish();

	}

	private void submitFeedback(String content, String email) {
		String feedbackURL = "http://ued.iciba.com/uedplat/service/ued_post_result.php?";
		StringBuffer idBuf = new StringBuffer();
		idBuf.append(android.os.Build.MODEL);
		idBuf.append(".");
		idBuf.append(android.os.Build.BRAND);
		idBuf.append(".");
		idBuf.append(android.os.Build.DEVICE);
		idBuf.append(".");
		idBuf.append(android.os.Build.PRODUCT);
		String id = idBuf.toString();

		HttpPost request = new HttpPost(feedbackURL);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("pid", "37"));
		nvps.add(new BasicNameValuePair("version", "1.0"));
		nvps.add(new BasicNameValuePair("versionid", "100"));
		nvps.add(new BasicNameValuePair("canal", "client"));
		nvps.add(new BasicNameValuePair("mobiletype", id));
		nvps.add(new BasicNameValuePair("content", content));
		nvps.add(new BasicNameValuePair("email", email));

		try {
			request.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.v("cmd", "submitFeedback ex." + e.toString());
		} catch (Exception e) {
			e.printStackTrace();
			Log.v("cmd", "submitFeedback ex." + e.toString());
		}

		DefaultHttpClient httpClient = new DefaultHttpClient();
		RequestEntry entry = new RequestEntry(RequestEntry.FEEDBACK_TYPE,
				request);
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

	private void showFeedbackContentErrorToast() {
		Toast.makeText(
				Feedback.this,
				Feedback.this.getResources().getString(
						R.string.feedback_content_empty), 1000).show();

	}

	private void showFeedBackEmailErrorToast() {
		Toast.makeText(
				Feedback.this,
				Feedback.this.getResources().getString(
						R.string.feedback_email_error), 1000).show();

	}

	private void showFeedBackSuccessToast() {
		Toast.makeText(
				Feedback.this,
				Feedback.this.getResources().getString(
						R.string.feedback_submit_sucess), 1000).show();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean checkEmailadd(String emailAddress) {
		if (emailAddress == null) {
			return false;
		}
		try {
			String check = "^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+\\.)+([a-zA-Z0-9]{2,4})+$";
			// Pattern regex = Pattern.compile(check);
			// Matcher matcher = regex.matcher(emailAddress);
			// boolean isMatched = matcher.matches();
			boolean isMatched = emailAddress.matches(check);
			return isMatched;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
}
