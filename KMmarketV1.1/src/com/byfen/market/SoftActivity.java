package com.byfen.market;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.byfen.app.KApp;
import com.framework.log.P;
import com.work.market.view.ObjectView;
import com.work.market.view.SoftRankView;
import com.work.market.view.SoftlistView;
import com.work.market.view.classlistView;

public class SoftActivity extends Activity implements OnClickListener
{
	/**
	 * 推荐
	 */
	private Button mRecommendButton;
	/**
	 * 最新
	 */
	private Button m_news_button;
	/**
	 * 排名
	 */
	private Button m_rank_button;
	/**
	 * 分组列表
	 */
	private Button m_class_button;

	private SoftlistView m_SoftlistView_command;
	private SoftlistView m_SoftlistView_news;
	private SoftRankView m_SoftRankView_rank;
	private classlistView m_SoftlistView_class;

	private String m_title = "";
	private String m_kind = "";
	private String m_type = "";
	private int m_is_modify = -1;
	private String m_lang = "";
	private int m_min_file_size = -1;

	private int m_log = 0;

	private ArrayList<ObjectView> mListViews = new ArrayList<ObjectView>();
	private IntentFilter mIntentFilter; // 消息处理
	private ViewPager myViewPager;
	private MyPagerAdapter mAbsPageAdapter = new MyPagerAdapter();

	public static final int PAGETYPE_FIRST_GROUP = 1;
	public static final int PAGETYPE_SECOND_GROUP = 2;
	public static final int PAGETYPE_ROOT = 3;
	private int mPageType = PAGETYPE_FIRST_GROUP;
	private KApp app;
	private int mNowPagePos=0;//当前界面
	// 下载服务
	/**
	 * 进入页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		app = ((KApp) getApplicationContext());
		app.getDownLoadService();
		// 加载页面
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		setContentView(R.layout.softpage);
		// 异常处理
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("com.android.my.action");
		mIntentFilter.addAction("com.android.my.action.sticky");

		Bundle bunde = this.getIntent().getExtras();
		mPageType = bunde.getInt("pagetype", PAGETYPE_FIRST_GROUP);
		m_title = bunde.getString("title");
		// m_url = bunde.getString("url");
		m_kind = bunde.getString("kind");// game|soft
		m_type = bunde.getString("type");
		m_is_modify = bunde.getInt("is_modify", -1);
		m_lang = bunde.getString("lang");
		m_min_file_size = bunde.getInt("min_file_size", -1);

		TextView m_title_view = (TextView) findViewById(R.id.soft_title_text);
		m_title_view.setText(m_title);

		findViewById(R.id.soft_back).setOnClickListener(this);

		mRecommendButton = (Button) findViewById(R.id.soft_tab1_recommand);
		mRecommendButton.setOnClickListener(this);
		m_news_button = (Button) findViewById(R.id.soft_tab1_news);
		m_news_button.setOnClickListener(this);
		m_rank_button = (Button) findViewById(R.id.soft_tab1_rank);
		m_rank_button.setOnClickListener(this);
		m_class_button = (Button) findViewById(R.id.soft_tab1_class);
		m_class_button.setOnClickListener(this);

		Log.v("wmh", "SoftActivity:type=" + m_kind);
		Init(mPageType);

		m_log = 1;
		myViewPager = (ViewPager) findViewById(R.id.viewpagerLayout);
		myViewPager.setAdapter(mAbsPageAdapter);
		myViewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			public void onPageSelected(int arg0)
			{
				SwitchPage(arg0);
				mNowPagePos=arg0;
			}

			public void onPageScrolled(int arg0, float arg1, int arg2)
			{

			}

			public void onPageScrollStateChanged(int arg0)
			{

			}
		});
		myViewPager.setCurrentItem(mNowPagePos);
		SwitchPage(mNowPagePos);
	}

	private void Init(int mPageType)
	{
		String url = "http://api.byfen.com/list/rank?" + "kind=" + m_kind
				+ "&type=" + m_type + "&is_modify=" + m_is_modify + "&lang="
				+ m_lang + "&min_file_size=" + m_min_file_size + "&";
		if (mPageType == PAGETYPE_FIRST_GROUP)
		{
			m_SoftlistView_command = new SoftlistView(this);
			m_SoftlistView_news = new SoftlistView(this);
			m_SoftRankView_rank = new SoftRankView(this, this);
			m_SoftlistView_class = new classlistView(this, this);
			m_SoftlistView_command.SetUrl(url + "sort=recommand&");
			m_SoftlistView_news.SetUrl(url + "sort=new&");
			m_SoftlistView_command.onResume();
			m_SoftRankView_rank.Init(m_kind, m_type, m_is_modify, m_lang,
					m_min_file_size);
			m_SoftlistView_class.SetUrl("http://api.byfen.com/home/type?type="
					+ m_kind, m_kind);
		}
		else if (mPageType == PAGETYPE_SECOND_GROUP)
		{
			m_SoftlistView_command = new SoftlistView(this);
			m_SoftlistView_news = new SoftlistView(this);
			m_SoftRankView_rank = new SoftRankView(this, this);

			m_SoftlistView_command.SetUrl(url + "sort=recommand&");
			m_SoftlistView_news.SetUrl(url + "sort=new&");
			m_SoftlistView_command.onResume();
			m_SoftRankView_rank.Init(m_kind, m_type, m_is_modify, m_lang,
					m_min_file_size);
			m_class_button.setVisibility(View.GONE);
		}
		else
		{

		}
		if (m_SoftlistView_command != null)
		{
			mListViews.add(m_SoftlistView_command);
		}
		if (m_SoftlistView_news != null)
		{
			mListViews.add(m_SoftlistView_news);
		}
		if (m_SoftRankView_rank != null)
		{
			mListViews.add(m_SoftRankView_rank);
		}
		if (m_SoftlistView_class != null)
		{
			mListViews.add(m_SoftlistView_class);
		}

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.soft_tab1_recommand:
			myViewPager.setCurrentItem(0);
			break;
		case R.id.soft_tab1_news:
			myViewPager.setCurrentItem(1);
			break;
		case R.id.soft_tab1_rank:
			myViewPager.setCurrentItem(2);
			break;
		case R.id.soft_tab1_class:
			myViewPager.setCurrentItem(3);
			break;
		case R.id.soft_back:
			this.finish();
			break;
		default:
			break;
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			final String action = intent.getAction();
			 System.out.println("wmh.action"+action);
			Bundle bunde = intent.getExtras();
			String type = bunde.getString("type");
			if (type.equals("finish"))
			{
				if (m_log == 1)
				{
					m_SoftlistView_command.NotifyDataSetChanged();
				}
				else if (m_log == 2)
				{
					m_SoftlistView_news.NotifyDataSetChanged();
				}
				else if (m_log == 3)
				{
					m_SoftRankView_rank.NotifyDataSetChanged();
				}
			}
		}
	};

	@Override
	protected void onResume()
	{
		super.onResume();
		registerReceiver(mReceiver, mIntentFilter);
		SwitchPage(mNowPagePos);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		unregisterReceiver(mReceiver);
		onPause(mNowPagePos);
	}
	protected void onPause(int pos)
	{
		switch (pos)
		{
		case 0:
			m_SoftlistView_command.onPause();
			break;
		case 1:
			m_SoftlistView_news.onPause();
			break;
		case 2:
			m_SoftRankView_rank.onPause();
			break;
		case 3:
			m_SoftlistView_class.onPause();
			break;
		}
	}
	public void SwitchPage(int index)
	{
		switch (index)
		{
		case 0:
			// toptab_line
			mRecommendButton.setBackgroundResource(R.drawable.toptab_line);
			mRecommendButton.setTextColor(getResources()
					.getColor(R.color.green));// setTextColor
			m_news_button.setBackgroundResource(R.drawable.toptab_no_line);
			m_news_button.setTextColor(this.getResources().getColor(
					R.color.toptab_TextColor_normal));
			m_rank_button.setBackgroundResource(R.drawable.toptab_no_line);
			m_rank_button.setTextColor(this.getResources().getColor(
					R.color.toptab_TextColor_normal));
			m_class_button.setBackgroundResource(R.drawable.toptab_no_line);
			m_class_button.setTextColor(this.getResources().getColor(
					R.color.toptab_TextColor_normal));
			m_SoftlistView_command.onResume();
			m_log = 1;
			break;
		case 1:
			m_news_button.setBackgroundResource(R.drawable.toptab_line);
			m_news_button.setTextColor(getResources().getColor(R.color.green));
			mRecommendButton.setBackgroundResource(R.drawable.toptab_no_line);
			mRecommendButton.setTextColor(this.getResources().getColor(
					R.color.toptab_TextColor_normal));
			m_rank_button.setBackgroundResource(R.drawable.toptab_no_line);
			m_rank_button.setTextColor(this.getResources().getColor(
					R.color.toptab_TextColor_normal));
			m_class_button.setBackgroundResource(R.drawable.toptab_no_line);
			m_class_button.setTextColor(this.getResources().getColor(
					R.color.toptab_TextColor_normal));
			m_SoftlistView_news.onResume();
			m_log = 2;
			break;
		case 2:
			m_rank_button.setBackgroundResource(R.drawable.toptab_line);
			m_rank_button.setTextColor(getResources().getColor(R.color.green));
			m_news_button.setBackgroundResource(R.drawable.toptab_no_line);
			m_news_button.setTextColor(this.getResources().getColor(
					R.color.toptab_TextColor_normal));
			mRecommendButton.setBackgroundResource(R.drawable.toptab_no_line);
			mRecommendButton.setTextColor(this.getResources().getColor(
					R.color.toptab_TextColor_normal));
			m_class_button.setBackgroundResource(R.drawable.toptab_no_line);
			m_class_button.setTextColor(this.getResources().getColor(
					R.color.toptab_TextColor_normal));
			m_SoftRankView_rank.onResume();
			m_log = 3;
			break;
		case 3:
			m_class_button.setBackgroundResource(R.drawable.toptab_line);
			m_class_button.setTextColor(getResources().getColor(R.color.green));
			m_news_button.setBackgroundResource(R.drawable.toptab_no_line);
			m_news_button.setTextColor(this.getResources().getColor(
					R.color.toptab_TextColor_normal));
			m_rank_button.setBackgroundResource(R.drawable.toptab_no_line);
			m_rank_button.setTextColor(this.getResources().getColor(
					R.color.toptab_TextColor_normal));
			mRecommendButton.setBackgroundResource(R.drawable.toptab_no_line);
			mRecommendButton.setTextColor(this.getResources().getColor(
					R.color.toptab_TextColor_normal));
			m_SoftlistView_class.onResume();
			m_log = 4;
			break;
		}
	}

	private class MyPagerAdapter extends PagerAdapter
	{
		public void destroyItem(View arg0, int arg1, Object arg2)
		{
			if (mListViews.size() > arg1)
			{
				((ViewPager) arg0).removeView(mListViews.get(arg1));
				P.d("wmh", "destroyItem:"+arg1);
			}
		}

		public void finishUpdate(View arg0)
		{
			P.d("wmh", "finishUpdate："+arg0);
		}

		public int getCount()
		{
			return mListViews.size();
		}

		public Object instantiateItem(View arg0, int arg1)
		{
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			P.d("wmh", "instantiateItem:"+arg1);
			return mListViews.get(arg1);
		}

		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == (arg1);
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1)
		{

		}

		public Parcelable saveState()
		{
			return null;
		}

		public void startUpdate(View arg0)
		{

		}

	}
}
