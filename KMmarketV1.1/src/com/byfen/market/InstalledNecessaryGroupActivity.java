package com.byfen.market;

import java.util.ArrayList;

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

import com.byfen.market.SearchActivity.NetTask1;
import com.work.market.view.MustSoftlistView;

//"装机必备"
public class InstalledNecessaryGroupActivity extends Activity implements OnClickListener{

	
	private TextView      m_special_text1;
	private IntentFilter mIntentFilter; //消息处理
	
	
	private  ProgressDialog   pd;
		
	private  NetTask1          mNetTask1;
	private  ArrayList<String> m_list_url;//首页列表地址
	private  String           m_title;
	private  String           m_url;
	private  TextView         m_title_view;
	private  MustSoftlistView    m_SoftlistView_command;
	private  RelativeLayout          m_soft_last_view;
	private LinearLayout     m_back;
	
	
	/**
	 * 进入页面
	* @param savedInstanceState    
	* @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			//加载页面
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
			setContentView(R.layout.specials);
			//异常处理  
			
			mIntentFilter = new IntentFilter();        
			mIntentFilter.addAction("com.android.my.action.sticky"); 
			
			m_special_text1 = (TextView)findViewById(R.id.special_text1);
			
			Bundle bunde = this.getIntent().getExtras();
			
			m_title = bunde.getString("title");
			m_url = bunde.getString("url");
			m_special_text1.setText(m_title);
			
			pd = new ProgressDialog(this);
			pd.setMessage(this.getText(R.string.pd_loading));
			pd.setCancelable(true);
			
			m_back = (LinearLayout)findViewById(R.id.dpecials_back);
			m_back.setOnClickListener(this);
			
			m_SoftlistView_command = new MustSoftlistView(this);//SoftlistView3
			m_SoftlistView_command.SetUrl(m_url);
			m_soft_last_view = (RelativeLayout)findViewById(R.id.specials_last_view);


			m_title_view = (TextView)findViewById(R.id.special_text1);
			m_title_view.setText(m_title);
			m_soft_last_view.addView(m_SoftlistView_command);
			m_SoftlistView_command.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
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
		m_SoftlistView_command.onResume();
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		m_SoftlistView_command.onPause();
	}
	  

	
}


