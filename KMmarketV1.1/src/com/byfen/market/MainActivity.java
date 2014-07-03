package com.byfen.market;

import java.io.File;
import java.io.FileInputStream;

import android.app.TabActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;

import com.byfen.app.KApp;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.ReportPolicy;
import com.umeng.update.UmengUpdateAgent;
import com.work.market.net.Common;
import com.work.market.util.Comm;
import com.work.market.util.Res;

public class MainActivity extends TabActivity implements OnClickListener
{

	public TabHost mHost;
	public static RadioButton rankButton;// 兑换
	public static RadioButton indexButton;// 首页

	public LinearLayout m_LinearLayout1;
	public LinearLayout m_LinearLayout2;
	public LinearLayout m_LinearLayout3;
	public LinearLayout m_LinearLayout4;
	public LinearLayout m_LinearLayout5;
	public ImageView m_image1;
	public TextView m_text1;
	public ImageView m_image2;
	public TextView m_text2;
	public ImageView m_image3;
	public TextView m_text3;
	public ImageView m_image4;
	public TextView m_text4;
	public ImageView m_image5;
	public TextView m_text5;

	private void Init()
	{
		Res.setActivity(this);
		UMInit();
	}

	private void UMInit()
	{
		// 友盟统计数据
		MobclickAgent.setDebugMode(false);
		MobclickAgent.updateOnlineConfig(this);// 在线参数配置
		MobclickAgent.onError(this);
		MobclickAgent.setSessionContinueMillis(10 * 60 * 1000);
		MobclickAgent.setAutoLocation(true);// collect location info,
		MobclickAgent.setDebugMode(false); // set debug mode ,will print
		MobclickAgent
				.setDefaultReportPolicy(this, ReportPolicy.BATCH_AT_LAUNCH);
		UmengUpdateAgent.update(this);
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		Init();
		setContentView(R.layout.activity_main);

		Comm.CreatPath();
		
		KApp app=((KApp)getApplicationContext());
		if(app.getDownLoadService()==null)
		{
			app.startAndBindService();
		}
		mHost = this.getTabHost();

		mHost.addTab(mHost.newTabSpec("ONE").setIndicator("ONE")
				.setContent(new Intent(this, HomeActivity.class)));
		mHost.addTab(mHost.newTabSpec("TWO").setIndicator("TWO")
				.setContent(new Intent(this, SearchActivity.class)));
		mHost.addTab(mHost.newTabSpec("THREE").setIndicator("THREE")
				.setContent(new Intent(this, DownActivity.class)));
		mHost.addTab(mHost.newTabSpec("FOUR").setIndicator("FOUR")
				.setContent(new Intent(this, UpdateActivity.class)));
		mHost.addTab(mHost.newTabSpec("FIVE").setIndicator("FIVE")
				.setContent(new Intent(this, MoreActivity.class)));

		m_LinearLayout1 = (LinearLayout) findViewById(R.id.index_table1);
		m_LinearLayout1.setOnClickListener(this);
		m_LinearLayout2 = (LinearLayout) findViewById(R.id.index_table2);
		m_LinearLayout2.setOnClickListener(this);
		m_LinearLayout3 = (LinearLayout) findViewById(R.id.index_table3);
		m_LinearLayout3.setOnClickListener(this);
		m_LinearLayout4 = (LinearLayout) findViewById(R.id.index_table4);
		m_LinearLayout4.setOnClickListener(this);
		m_LinearLayout5 = (LinearLayout) findViewById(R.id.index_table5);
		m_LinearLayout5.setOnClickListener(this);
		m_LinearLayout1.setBackgroundDrawable(null);
		m_LinearLayout2.setBackgroundDrawable(null);
		m_LinearLayout3.setBackgroundDrawable(null);
		m_LinearLayout4.setBackgroundDrawable(null);
		m_LinearLayout5.setBackgroundDrawable(null);
		m_LinearLayout1.setBackgroundResource(R.drawable.tab_button_selected);

		m_image1 = (ImageView) findViewById(R.id.index_table1_image);
		m_image2 = (ImageView) findViewById(R.id.index_table2_image);
		m_image3 = (ImageView) findViewById(R.id.index_table3_image);
		m_image4 = (ImageView) findViewById(R.id.index_table4_image);
		m_image5 = (ImageView) findViewById(R.id.index_table5_image);

		m_text1 = (TextView) findViewById(R.id.index_table1_text);
		m_text2 = (TextView) findViewById(R.id.index_table2_text);
		m_text3 = (TextView) findViewById(R.id.index_table3_text);
		m_text4 = (TextView) findViewById(R.id.index_table4_text);
		m_text5 = (TextView) findViewById(R.id.index_table5_text);
		m_image1.setImageResource(R.drawable.tab_home_normal1);
		m_text1.setTextColor(getResources().getColor(R.color.whites));
		m_image2.setImageResource(R.drawable.tab_search_normal);
		m_text2.setTextColor(getResources().getColor(R.color.black));
		m_image3.setImageResource(R.drawable.tab_download_normal);
		m_text3.setTextColor(getResources().getColor(R.color.black));
		m_image4.setImageResource(R.drawable.tab_update_normal);
		m_text4.setTextColor(getResources().getColor(R.color.black));
		m_image5.setImageResource(R.drawable.tab_more_normal);
		m_text5.setTextColor(getResources().getColor(R.color.black));
		Common.new_num = 0;
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.index_table1:
			m_LinearLayout1.setBackgroundDrawable(null);
			m_LinearLayout2.setBackgroundDrawable(null);
			m_LinearLayout3.setBackgroundDrawable(null);
			m_LinearLayout4.setBackgroundDrawable(null);
			m_LinearLayout5.setBackgroundDrawable(null);
			m_LinearLayout1
					.setBackgroundResource(R.drawable.tab_button_selected);
			m_image1.setImageResource(R.drawable.tab_home_normal1);
			m_text1.setTextColor(getResources().getColor(R.color.whites));
			m_image2.setImageResource(R.drawable.tab_search_normal);
			m_text2.setTextColor(getResources().getColor(R.color.black));
			m_image3.setImageResource(R.drawable.tab_download_normal);
			m_text3.setTextColor(getResources().getColor(R.color.black));
			m_image4.setImageResource(R.drawable.tab_update_normal);
			m_text4.setTextColor(getResources().getColor(R.color.black));
			if (Common.new_num != 0)
			{
				m_text4.setText("更新(" + Common.new_num + ")");
			}
			m_image5.setImageResource(R.drawable.tab_more_normal);
			m_text5.setTextColor(getResources().getColor(R.color.black));
			mHost.setCurrentTabByTag("ONE");

			break;
		case R.id.index_table2:
			m_LinearLayout1.setBackgroundDrawable(null);
			m_LinearLayout2.setBackgroundDrawable(null);
			m_LinearLayout3.setBackgroundDrawable(null);
			m_LinearLayout4.setBackgroundDrawable(null);
			m_LinearLayout5.setBackgroundDrawable(null);
			m_LinearLayout2
					.setBackgroundResource(R.drawable.tab_button_selected);
			m_image1.setImageResource(R.drawable.tab_home_normal);
			m_text1.setTextColor(getResources().getColor(R.color.black));
			m_image2.setImageResource(R.drawable.tab_search_normal1);
			m_text2.setTextColor(getResources().getColor(R.color.whites));
			m_image3.setImageResource(R.drawable.tab_download_normal);
			m_text3.setTextColor(getResources().getColor(R.color.black));
			m_image4.setImageResource(R.drawable.tab_update_normal);
			m_text4.setTextColor(getResources().getColor(R.color.black));
			if (Common.new_num != 0)
			{
				m_text4.setText("更新(" + Common.new_num + ")");
			}
			m_image5.setBackgroundResource(R.drawable.tab_more_normal);
			m_text5.setTextColor(getResources().getColor(R.color.black));

			mHost.setCurrentTabByTag("TWO");
			break;
		case R.id.index_table3:
			m_LinearLayout1.setBackgroundDrawable(null);
			m_LinearLayout2.setBackgroundDrawable(null);
			m_LinearLayout3.setBackgroundDrawable(null);
			m_LinearLayout4.setBackgroundDrawable(null);
			m_LinearLayout5.setBackgroundDrawable(null);
			m_LinearLayout3
					.setBackgroundResource(R.drawable.tab_button_selected);
			m_image1.setImageResource(R.drawable.tab_home_normal);
			m_text1.setTextColor(getResources().getColor(R.color.black));
			m_image2.setImageResource(R.drawable.tab_search_normal);
			m_text2.setTextColor(getResources().getColor(R.color.black));
			m_image3.setImageResource(R.drawable.tab_download_normal1);
			m_text3.setTextColor(getResources().getColor(R.color.whites));
			m_image4.setImageResource(R.drawable.tab_update_normal);
			m_text4.setTextColor(getResources().getColor(R.color.black));
			if (Common.new_num != 0)
			{
				m_text4.setText("更新(" + Common.new_num + ")");
			}
			m_image5.setBackgroundResource(R.drawable.tab_more_normal);
			m_text5.setTextColor(getResources().getColor(R.color.black));
			mHost.setCurrentTabByTag("THREE");
			break;
		case R.id.index_table4:
			m_LinearLayout1.setBackgroundDrawable(null);
			m_LinearLayout2.setBackgroundDrawable(null);
			m_LinearLayout3.setBackgroundDrawable(null);
			m_LinearLayout4.setBackgroundDrawable(null);
			m_LinearLayout5.setBackgroundDrawable(null);
			m_LinearLayout4
					.setBackgroundResource(R.drawable.tab_button_selected);
			m_image1.setImageResource(R.drawable.tab_home_normal);
			m_text1.setTextColor(getResources().getColor(R.color.black));
			m_image2.setImageResource(R.drawable.tab_search_normal);
			m_text2.setTextColor(getResources().getColor(R.color.black));
			m_image3.setImageResource(R.drawable.tab_download_normal);
			m_text3.setTextColor(getResources().getColor(R.color.black));
			m_image4.setImageResource(R.drawable.tab_update_normal1);
			m_text4.setTextColor(getResources().getColor(R.color.whites));
			if (Common.new_num != 0)
			{
				m_text4.setText("更新(" + Common.new_num + ")");
			}
			m_image5.setBackgroundResource(R.drawable.tab_more_normal);
			m_text5.setTextColor(getResources().getColor(R.color.black));
			mHost.setCurrentTabByTag("FOUR");
			break;

		case R.id.index_table5:
			m_LinearLayout1.setBackgroundDrawable(null);
			m_LinearLayout2.setBackgroundDrawable(null);
			m_LinearLayout3.setBackgroundDrawable(null);
			m_LinearLayout4.setBackgroundDrawable(null);
			m_LinearLayout5.setBackgroundDrawable(null);
			m_LinearLayout5
					.setBackgroundResource(R.drawable.tab_button_selected);
			m_image1.setImageResource(R.drawable.tab_home_normal);
			m_text1.setTextColor(getResources().getColor(R.color.black));
			m_image2.setImageResource(R.drawable.tab_search_normal);
			m_text2.setTextColor(getResources().getColor(R.color.black));
			m_image3.setImageResource(R.drawable.tab_download_normal);
			m_text3.setTextColor(getResources().getColor(R.color.black));
			m_image4.setImageResource(R.drawable.tab_update_normal);
			m_text4.setTextColor(getResources().getColor(R.color.black));
			if (Common.new_num != 0)
			{
				m_text4.setText("更新(" + Common.new_num + ")");
			}
			m_image5.setBackgroundResource(R.drawable.tab_more_normal1);
			m_text5.setTextColor(getResources().getColor(R.color.whites));
			mHost.setCurrentTabByTag("FIVE");
			Log.v("wmh", "five");
			break;
		default:
			break;
		}
	}

	public void getTabHostToTab()
	{
		// mHost=this.getTabHost();
		// mHost.addTab(mHost.newTabSpec("THREE").setIndicator("THREE")
		// .setContent(new
		// Intent(this,RankActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));
	}

	public int getTabId()
	{
		Bundle extras = getIntent().getExtras();
		Resources resources = getResources();
		String defaultTab = extras.getString("THREE");
		int tadid = defaultTab == null ? 2 : Integer.valueOf(defaultTab);
		return tadid;
	}

	private void CreatPath()
	{
		if (Common.sdCardCheck())
		{
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED))
			{
				String path = Environment.getExternalStorageDirectory()
						+ "/baifen";
				File file = new File(path);
				if (!file.exists())
				{
					file.mkdir();
				}
				//
				// baifen/dowsload
				// baifen/img
				// baifen/temp

				File file1 = new File(path + "/dowsload");
				if (!file1.exists())
				{
					file1.mkdir();
				}
				File file2 = new File(path + "/img");
				if (file2.exists())
				{
					file2.mkdir();
				}
			}
		}
	}

	public String RedFile(String files)
	{
		String data = null;
		File file = new File(files);
		if (!file.exists())
		{
			return null;
		}
		byte[] buffer = null;
		try
		{

			FileInputStream fin = new FileInputStream(files);
			int length = fin.available();
			buffer = new byte[length];
			fin.read(buffer);
			data = new String(buffer);
			fin.close();

		}
		catch (Exception e)
		{

			e.printStackTrace();

		}

		return data;

	}

}
