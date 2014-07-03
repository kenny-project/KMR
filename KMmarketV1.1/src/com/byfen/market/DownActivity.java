package com.byfen.market;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.byfen.app.KApp;
import com.framework.log.P;
import com.work.Interface.INotifyDataSetChanged;
import com.work.market.adapter.DownLoadedAdapter;
import com.work.market.adapter.DownLoadingAdapter;
import com.work.market.bean.AppListBean;
import com.work.market.server.DownLoadService;

/**
 * 下载界面
 * 
 * @author kenny
 */

public class DownActivity extends Activity implements OnClickListener,
		INotifyDataSetChanged
{
	private LinearLayout m_left_button;
	private LinearLayout m_right_button;
	public DownLoadingAdapter mDownLoadingadapter;
	public DownLoadedAdapter mDownloadedadapter;
	private ListView lvDownLoading, lvDownLoaded;
	// private IntentFilter mIntentFilter; // 消息处理
	public List<AppListBean> downingList = new ArrayList<AppListBean>();
	private Context mContext;
	private ViewPager myViewPager;
	private MyPagerAdapter mAbsPageAdapter = new MyPagerAdapter();
	private static final int DownLoading = 0, DownLoaded = 1;
	private int mPageIndex = DownLoading;
	private KApp app;
	private ArrayList<ListView> mListViews = new ArrayList<ListView>();
	private DownLoadService mPlaybackService = null;
	private ServiceConnection mServiceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			P.v("ServiceConnection:onServiceConnected");
			mPlaybackService = ((DownLoadService.LocalBinder) service)
					.getService();
			mPlaybackService.AdapterInit(DownActivity.this);
			mDownLoadingadapter = mPlaybackService.getDownLoadingAddapter();
			lvDownLoading.setAdapter(mDownLoadingadapter);
			mDownloadedadapter = mPlaybackService.getDownloadedadapter();
			lvDownLoaded.setAdapter(mDownloadedadapter);
			app.setINotifyChanged(DownActivity.this);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0)
		{
			P.v("ServiceConnection:onServiceDisconnected");
			mPlaybackService = null;
			app.setINotifyChanged(null);
		}
	};

	void startAndBindService()
	{
		startService(new Intent(this, DownLoadService.class));
		bindService(new Intent(this, DownLoadService.class),
				mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	void stopService()
	{
		if (mPlaybackService != null)
		{
			stopService(new Intent(this, DownLoadService.class));
		}
	}

	// ..................结束服务
	/**
	 * 进入页面
	 * 
	 * @param savedInstanceState
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		// 加载页面
		setContentView(R.layout.down_activity);
		// 异常处理
		mContext = this;
		app = ((KApp) getApplicationContext());

		m_left_button = (LinearLayout) findViewById(R.id.down_tab_left);
		m_left_button.setOnClickListener(this);
		m_right_button = (LinearLayout) findViewById(R.id.down_tab_right);
		m_right_button.setOnClickListener(this);
		lvDownLoading = new ListView(this);
		lvDownLoaded = new ListView(this);
		mListViews.add(lvDownLoading);
		mListViews.add(lvDownLoaded);
		// lvDownLoading = (ListView) findViewById(R.id.down_ls);
		mPlaybackService = app.getDownLoadService();
		if (mPlaybackService == null)
		{
			startAndBindService();
		}
		else
		{
			mPlaybackService.AdapterInit(DownActivity.this);
			mDownLoadingadapter = mPlaybackService.getDownLoadingAddapter();
			lvDownLoading.setAdapter(mDownLoadingadapter);
			mDownloadedadapter = mPlaybackService.getDownloadedadapter();
			lvDownLoaded.setAdapter(mDownloadedadapter);
		}

		lvDownLoaded.setOnItemLongClickListener(new OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id)
			{
				// 提示用户是否删除消息
				new AlertDialog.Builder(mContext)
						.setTitle("提示")
						.setMessage("是否要删除当前安装包？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog,
											int arg1)
									{
										dialog.dismiss();
										if (mPlaybackService != null) mPlaybackService
												.DelDownLoaded(position);
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog,
											int arg1)
									{
										dialog.dismiss();
									}
								}).show();
				return false;
			}
		});
		lvDownLoaded.setOnItemClickListener(new OnItemClickListener()
		{

			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id)
			{
				Intent seta = new Intent(mContext, productActivity.class);
				Bundle bundle = new Bundle();
				AppListBean bean = (AppListBean) parent.getItemAtPosition(pos);
				bundle.putString("title", bean.getTitle());
				bundle.putString("pn", bean.getPn());
				bundle.putInt("id", bean.getId());
				seta.putExtras(bundle);
				startActivity(seta);
			}
		});
		myViewPager = (ViewPager) findViewById(R.id.viewpagerLayout);
		myViewPager.setAdapter(mAbsPageAdapter);
		myViewPager.setCurrentItem(DownLoading);
		myViewPager.setOnPageChangeListener(new OnPageChangeListener()
		{

			public void onPageSelected(int arg0)
			{
				SwitchPage(arg0);
			}

			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
			}

			public void onPageScrollStateChanged(int arg0)
			{
			}
		});
		myViewPager.setCurrentItem(mPageIndex);
		m_right_button.setBackgroundResource(R.drawable.toptab_no_line);
		m_left_button.setBackgroundResource(R.drawable.toptab_line);

	}

	public void SwitchPage(int index)
	{
		mPageIndex = index;
		switch (index)
		{
		case 0:
			m_right_button.setBackgroundResource(R.drawable.toptab_no_line);
			m_left_button.setBackgroundResource(R.drawable.toptab_line);
			if (mDownLoadingadapter != null) mDownLoadingadapter
					.notifyDataSetChanged();
			break;
		case 1:
			m_left_button.setBackgroundResource(R.drawable.toptab_no_line);
			m_right_button.setBackgroundResource(R.drawable.toptab_line);
			if (mDownloadedadapter != null) mDownloadedadapter
					.notifyDataSetChanged();
			break;
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.down_tab_left:
			myViewPager.setCurrentItem(0);
			break;
		case R.id.down_tab_right:
			myViewPager.setCurrentItem(1);
			break;
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		if (mDownLoadingadapter != null) mDownLoadingadapter
				.notifyDataSetChanged();
		app.setINotifyChanged(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		app.setINotifyChanged(null);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if (keyCode == KeyEvent.KEYCODE_BACK)
			{
				new AlertDialog.Builder(this)
						.setTitle("提示")
						.setMessage("是否要退出软件？")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener()
								{
									public void onClick(
											DialogInterface dialoginterface,
											int i)
									{
										Intent serviceIntent = new Intent(
												DownActivity.this,
												DownLoadService.class);
										serviceIntent
												.putExtra("type", "finish");
										startService(serviceIntent);
										finish();
									}
								}).setNegativeButton("取消", null).show();
			}
		}
		return true;
	}

	private class MyPagerAdapter extends PagerAdapter
	{
		public void destroyItem(View arg0, int arg1, Object arg2)
		{
			if (mListViews.size() > arg1)
			{
				((ViewPager) arg0).removeView(mListViews.get(arg1));
			}
		}

		public void finishUpdate(View arg0)
		{
		}

		public int getCount()
		{
			return mListViews.size();
		}

		public Object instantiateItem(View arg0, int arg1)
		{
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);

			return mListViews.get(arg1);
		}

		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == (arg1);
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1)
		{
			// P.v("k", "restoreState");
		}

		public Parcelable saveState()
		{
			// P.v("k", "saveState");
			return null;
		}

		public void startUpdate(View arg0)
		{
			// P.v("k", "startUpdate");
		}
	}

	@Override
	public void NotifyDataSetChanged(int cmd, Object value)
	{
		// TODO Auto-generated method stub

	}

	private boolean bRefresh = false;

	@Override
	public void NotifyDataSetChanged(int cmd, Object value, int arg1, int arg2)
	{
//		Log.v("wmh", "notifyDataSetChanged");
//		if (!bRefresh)
//		{
//			bRefresh = true;
			handler.post(new Runnable()
			{
				@Override
				public void run()
				{
					
					mDownLoadingadapter.notifyDataSetChanged();
					bRefresh = false;
				}
			});
//		}
	}

	Handler handler = new Handler();
}
