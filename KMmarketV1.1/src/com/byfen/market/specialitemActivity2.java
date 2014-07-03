package com.byfen.market;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.work.market.view.SoftlistView2;

public class specialitemActivity2 extends Activity implements OnClickListener
{

	private TextView m_special_text1;
	private IntentFilter mIntentFilter; // 消息处理
	private ProgressDialog pd;

	private String m_title;
	private String m_url;
	private TextView m_title_view;
	private SoftlistView2 m_SoftlistView_command;
	private RelativeLayout m_soft_last_view;
	private LinearLayout m_back;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// 加载页面
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		setContentView(R.layout.specials);
		// 异常处理
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("com.android.my.action.sticky");

		m_special_text1 = (TextView) findViewById(R.id.special_text1);
		m_special_text1.setText("清单季节");

		Bundle bunde = this.getIntent().getExtras();

		m_title = bunde.getString("title");
		m_url = bunde.getString("url");
		m_special_text1.setText(m_title);

		pd = new ProgressDialog(this);
		pd.setMessage(this.getText(R.string.pd_loading));
		pd.setCancelable(true);

		m_back = (LinearLayout) findViewById(R.id.dpecials_back);
		m_back.setOnClickListener(this);

		m_SoftlistView_command = new SoftlistView2(this, this);
		m_SoftlistView_command.SetUrl(m_url);
		m_soft_last_view = (RelativeLayout) findViewById(R.id.specials_last_view);

		m_title_view = (TextView) findViewById(R.id.special_text1);
		m_title_view.setText(m_title);
		m_soft_last_view.addView(m_SoftlistView_command);
		m_SoftlistView_command.SetList();
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.dpecials_back:
			this.finish();
			break;
		}
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
	}

}
