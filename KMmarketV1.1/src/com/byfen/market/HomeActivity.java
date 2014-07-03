package com.byfen.market;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.byfen.Services.AdapterService;
import com.byfen.app.KApp;
import com.byfen.market.bean.BanBean;
import com.byfen.market.event.InitEvent;
import com.framework.syseng.AbsEvent;
import com.framework.syseng.SysEng;
import com.work.Image.ImageLoader;
import com.work.Image.SDFile;
import com.work.Image.SaveData;
import com.work.Interface.INotifyDataSetChanged;
import com.work.Interface.ImageCallback;
import com.work.market.adapter.ObjectListAdapter;
import com.work.market.bean.AppListBean;
import com.work.market.net.Common;
import com.work.market.net.Downloader;
import com.work.market.net.HttpUtil;
import com.work.market.server.DownLoadService;
import com.work.market.server.KHomeBanItemParser;
import com.work.market.util.AnimationUtil;

public class HomeActivity extends Activity implements OnClickListener,INotifyDataSetChanged
{
	private Button m_soft;
	private Button m_game;
	private Button m_special;
	private Button m_wallpaper;
	private View mVFooterListView;
	private View mVHeadListView;
	public ObjectListAdapter adapter;
	private ListView index_ls;
	// 动画广告
	private ArrayList<View> m_ad_pageViews;
	private ViewPager m_ad_viewPager;
	private int m_ad_indexNum = 0;
	private KApp app ;
	private Handler m_ad_Handler = new Handler();
	// ..................启动服务..................

	// 自动切换下一页
	private Runnable m_ad_autoRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			m_ad_indexNum++;
			m_ad_indexNum = m_ad_indexNum % m_ad_pageViews.size();
			// 向后切换动画
			if (m_ad_indexNum > 0)
			{
				final View v1 = m_ad_pageViews.get(m_ad_indexNum - 1);
				v1.startAnimation(AnimationUtil.outToLeftAnimation());
				m_ad_Handler.postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						View v2 = m_ad_pageViews.get(m_ad_indexNum);
						v2.startAnimation(AnimationUtil.inFromRightAnimation());
						m_ad_viewPager.setCurrentItem(m_ad_indexNum);
						v1.clearAnimation();
					}
				}, 200);
				// 切换到第一页动画
			}
			else
			{
				final View v1 = m_ad_pageViews.get(m_ad_pageViews.size() - 1);
				v1.startAnimation(AnimationUtil.outToLeftAnimation());
				m_ad_Handler.postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						View v2 = m_ad_pageViews.get(m_ad_indexNum);
						v2.startAnimation(AnimationUtil.inFromRightAnimation());
						m_ad_viewPager.setCurrentItem(m_ad_indexNum);
						v1.clearAnimation();
					}
				}, 200);
			}
			m_ad_Handler.removeCallbacks(m_ad_autoRunnable);
			m_ad_Handler.postDelayed(m_ad_autoRunnable, 5000);
		}
	};

	private IntentFilter mIntentFilter; // 消息处理
	// 网络

	private NetTask mNetTask;
	// private NetTask1 mNetTask1;
	private ImageLoader mLogoImage;
	private ArrayList<BanBean> mBanList = new ArrayList<BanBean>();// 首页图片推荐
	private Context m_Context;
	private AdapterService mService;
	private static final String BanFileName = "ban.xml";
	private static final String BanFileDay = "Ban_ListDay";

	/**
	 * 进入页面
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		// 加载页面
		setContentView(R.layout.index);
		app = ((KApp) getApplicationContext());
		app.setINotifyChanged(this);

		// 异常处理
		m_Context = this;
		mLogoImage = ImageLoader.GetObject(m_Context);
		mBanList = new ArrayList<BanBean>();
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("com.android.my.action");

		m_soft = (Button) findViewById(R.id.btSoftGroup);
		m_soft.setOnClickListener(this);

		m_game = (Button) findViewById(R.id.btGameGroup);
		m_game.setOnClickListener(this);
		m_special = (Button) findViewById(R.id.btSubjectGroup);
		m_special.setOnClickListener(this);
		m_wallpaper = (Button) findViewById(R.id.btInstalledNecessaryGroup);
		m_wallpaper.setOnClickListener(this);

		// head
		mVHeadListView = getLayoutInflater().inflate(R.layout.list_head_index,
				null);// 添加list

		mVHeadListView.findViewById(R.id.btBigGameGroup).setOnClickListener(
				this);
		mVHeadListView.findViewById(R.id.btCrackGameGroup).setOnClickListener(
				this);
		mVHeadListView.findViewById(R.id.btMobileNetGameGroup)
				.setOnClickListener(this);
		mVHeadListView.findViewById(R.id.btChineseGameGroup)
				.setOnClickListener(this);

		m_ad_viewPager = (ViewPager) mVHeadListView.findViewById(R.id.ad_page);

		// foot
		mVFooterListView = getLayoutInflater().inflate(
				R.layout.xlistview_footer, null);// 添加list

		index_ls = (ListView) findViewById(R.id.index_ls);
		index_ls.addHeaderView(mVHeadListView);
		index_ls.addFooterView(mVFooterListView, null, false);
		mService = new AdapterService(getParent(), mVFooterListView);
		adapter = mService.InitData(0, 1);
		index_ls.setAdapter(adapter);
		adinitData();
		index_ls.setOnScrollListener(mService);
		index_ls.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id)
			{
				if(pos>0)pos--;
				AppListBean bean = (AppListBean)adapter.getItem(pos);
				Intent seta = new Intent(HomeActivity.this, productActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("title", bean.getTitle());
				bundle.putInt("id", bean.getId());
				seta.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				seta.putExtras(bundle);
				HomeActivity.this.startActivity(seta);
			}
		});
		BanInit();
		new InitEvent(this).ok();
	}

	public void BanInit()
	{
		KHomeBanItemParser hip = new KHomeBanItemParser();
		String Buffer = null;

		try
		{
			Buffer = SDFile.ReadSDFile(BanFileName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if (Buffer != null)
		{
			mBanList.clear();
			hip.parseStringByData(Buffer, mBanList);
			SetadData();
			Calendar c = Calendar.getInstance();
			int day = c.get(Calendar.DAY_OF_MONTH);
			int oldDay = SaveData.Read(this, BanFileDay, 1);
			if (day != oldDay)
			{
				upData(false);
			}
		}
		else
		{
			upData(true);
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btSoftGroup:
			Intent seta = new Intent(HomeActivity.this, SoftActivity.class);
			Bundle bundle = new Bundle();
			bundle.putInt("pagetype", SoftActivity.PAGETYPE_FIRST_GROUP);
			bundle.putString("title", "软件");
			bundle.putString("url", "");
			bundle.putString("kind", "soft");
			bundle.putString("type", "0");
			bundle.putString("id", "1");
			bundle.putInt("is_modify", -1);//是否为修改状态 0:未修改,1:修改破解版,不传为所有 
			bundle.putString("lang", "all");//语言,'cn':中文,'en':英文,不传为所有
			bundle.putInt("min_file_size", -1);//文件最小,例如 min_file_size=10,表示文件大小不少于10mb,-1:不限制
			seta.putExtras(bundle);
			startActivity(seta);
			break;
		case R.id.btGameGroup:
			seta = new Intent(HomeActivity.this, SoftActivity.class);
			bundle = new Bundle();
			bundle.putInt("pagetype", SoftActivity.PAGETYPE_FIRST_GROUP);
			bundle.putString("url", "");
			bundle.putString("title", "游戏");
			bundle.putString("kind", "game");
			bundle.putString("type", "0");
			bundle.putString("id", "1");
			bundle.putInt("is_modify", -1);//是否为修改状态 0:未修改,1:修改破解版,不传为所有 
			bundle.putString("lang", "all");//语言,'cn':中文,'en':英文,不传为所有
			bundle.putInt("min_file_size", -1);//文件最小,例如 min_file_size=10,表示文件大小不少于10mb,-1:不限制
			seta.putExtras(bundle);
			startActivity(seta);
			break;
		case R.id.btSubjectGroup:
			Intent seta0 = new Intent(HomeActivity.this, SpecialActivity.class);
			this.startActivity(seta0);
			break;
		case R.id.btInstalledNecessaryGroup:// bizhi
			Intent seta2 = new Intent(HomeActivity.this,
			InstalledNecessaryGroupActivity.class);
			Bundle bundle2 = new Bundle();
			String dialogstring2 = "装机必备";// http://api.byfen.com/list/must
			bundle2.putString("title", dialogstring2);
			bundle2.putString("url", "http://api.byfen.com/list/must?");
			seta2.putExtras(bundle2);
			startActivity(seta2);
			break;
		case R.id.btBigGameGroup:
			seta = new Intent(HomeActivity.this, SoftActivity.class);
			bundle = new Bundle();
			bundle.putInt("pagetype", SoftActivity.PAGETYPE_SECOND_GROUP);
			bundle.putString("url", "");
			bundle.putString("title", "大型游戏");
			bundle.putString("kind", "game");//game|soft
			bundle.putString("type", "0");
			bundle.putString("id", "1");
			bundle.putInt("is_modify", -1);//是否为修改状态 0:未修改,1:修改破解版,不传为所有 
			bundle.putString("lang", "all");//语言,'cn':中文,'en':英文,不传为所有
			bundle.putInt("min_file_size", 60);//文件最小,例如 min_file_size=10,表示文件大小不少于10mb,-1:不限制
			seta.putExtras(bundle);
			startActivity(seta);
			
			break;
		case R.id.btCrackGameGroup:

			seta = new Intent(HomeActivity.this, SoftActivity.class);
			bundle = new Bundle();
			bundle.putInt("pagetype", SoftActivity.PAGETYPE_SECOND_GROUP);
			bundle.putString("url", "");
			bundle.putString("title", "修改游戏");
			bundle.putString("kind", "game");
			bundle.putString("type", "0");
			bundle.putString("id", "1");
			bundle.putInt("is_modify", 1);//是否为修改状态 0:未修改,1:修改破解版,不传为所有
			bundle.putString("lang", "all");//语言,'cn':中文,'en':英文,不传为所有
			bundle.putInt("min_file_size", -1);//文件最小,例如 min_file_size=10,表示文件大小不少于10mb,-1:不限制
			seta.putExtras(bundle);
			startActivity(seta);
			break;
		case R.id.btMobileNetGameGroup:
			seta = new Intent(HomeActivity.this, SoftActivity.class);
			bundle = new Bundle();
			bundle.putInt("pagetype", SoftActivity.PAGETYPE_SECOND_GROUP);
			bundle.putString("url", "");
			bundle.putString("title", "手机网游");
			bundle.putString("kind", "game");
			bundle.putString("type", "40");
			bundle.putString("id", "1");
			bundle.putInt("is_modify", -1);//是否为修改状态 0:未修改,1:修改破解版,不传为所有
			bundle.putString("lang", "all");//语言,'cn':中文,'en':英文,不传为所有
			bundle.putInt("min_file_size", -1);//文件最小,例如 min_file_size=10,表示文件大小不少于10mb,-1:不限制
			
			seta.putExtras(bundle);
			startActivity(seta);
			break;
		case R.id.btChineseGameGroup:
			seta = new Intent(HomeActivity.this, SoftActivity.class);
			bundle = new Bundle();
			bundle.putInt("pagetype", SoftActivity.PAGETYPE_SECOND_GROUP);
			bundle.putString("title", "中文游戏");
			bundle.putString("url", "");
			bundle.putString("kind", "game");
			bundle.putString("type", "0");
			bundle.putString("id", "1");
			bundle.putInt("is_modify", -1);//是否为修改状态 0:未修改,1:修改破解版,不传为所有
			bundle.putString("lang", "cn");//语言,'cn':中文,'en':英文,不传为所有
			bundle.putInt("min_file_size", -1);//文件最小,例如 min_file_size=10,表示文件大小不少于10mb,-1:不限制
			seta.putExtras(bundle);
			startActivity(seta);
			break;
		default:
			break;
		}
	}

	/**
	 * 刷新商店UI，放入数据
	 */
	private void adinitData()
	{

		m_ad_indexNum = 0;
		m_ad_pageViews = new ArrayList<View>();
		// 初始化要切换的页面 放入一个list中保存
		for (int i = 0; i < 1; i++)
		{
			LayoutInflater inflater = getLayoutInflater();
			// 绑定UI
			View v = inflater.inflate(R.layout.ad_item, null);
			ImageView adimage = (ImageView) v.findViewById(R.id.myadimage);
			m_ad_pageViews.add(v);
		}

		m_ad_viewPager.setAdapter(new GuidePageAdapter());
		m_ad_viewPager.setOnPageChangeListener(new GuidePageChangeListener());
		adautoItem();
	}

	private void adautoItem()
	{
		if (m_ad_pageViews == null || m_ad_pageViews.size() <= 0)
		{
			return;
		}
		Log.i("TAG", "--=-=-=-=-=-==-=--aaa---" + m_ad_indexNum);
		// 延时5秒后 执行autoRunnable
		m_ad_Handler.postDelayed(m_ad_autoRunnable, 5000);
	}

	/**
	 * UI显示需要的适配器
	 * 
	 * @author zhou
	 * 
	 */
	class GuidePageAdapter extends PagerAdapter
	{
		@Override
		public int getCount()
		{
			return m_ad_pageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object)
		{
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2)
		{
			((ViewPager) arg0).removeView(m_ad_pageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1)
		{
			((ViewPager) arg0).addView(m_ad_pageViews.get(arg1));
			return m_ad_pageViews.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1)
		{
		}

		@Override
		public Parcelable saveState()
		{
			return null;
		}

		@Override
		public void startUpdate(View arg0)
		{
		}

		@Override
		public void finishUpdate(View arg0)
		{
		}
	}

	/**
	 * 切换page
	 * 
	 * @author zhou
	 * 
	 */
	class GuidePageChangeListener implements OnPageChangeListener
	{

		@Override
		public void onPageScrollStateChanged(int arg0)
		{
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2)
		{
		}

		@Override
		public void onPageSelected(int arg0)
		{
			// 设置当前页
			m_ad_indexNum = arg0;
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			// final String action = intent.getAction();
			Bundle bunde = intent.getExtras();
			String type = bunde.getString("type");
			if (type.equals("finish") && adapter != null)
			{
				adapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		registerReceiver(mReceiver, mIntentFilter);
		adapter.notifyDataSetChanged();
		app.setINotifyChanged(this);
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(mReceiver);
		app.setINotifyChanged(null);
	}

	private void upData(boolean ashowdialg)
	{
		if (mNetTask != null)
		{
			mNetTask.cancel(true);
		}
		mNetTask = new NetTask(ashowdialg);
		mNetTask.execute(null);
	}

	// 图片处理
	class NetTask extends AsyncTask<Object, Integer, String>
	{
		private boolean m_showlog = false;
		private ProgressDialog pd = null;

		private NetTask(boolean ashowlog)
		{
			m_showlog = ashowlog;
			if (m_showlog)
			{
				pd = new ProgressDialog(HomeActivity.this);
				pd.setMessage(HomeActivity.this.getText(R.string.pd_loading));
				pd.setCancelable(false);
			}
		}

		protected String doInBackground(Object... params)
		{
			String url = "http://api.byfen.com/home/img";
			String json = HttpUtil.doGet(url);
			return json;
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result)
		{
			if (result == null)
			{// 失败 处理
				if (pd != null) pd.dismiss();
				String dialogstring = getString(R.string.net_faile);
				Toast.makeText(m_Context, dialogstring, Toast.LENGTH_LONG)
						.show();
			}
			else
			{
				KHomeBanItemParser hip = new KHomeBanItemParser();
				hip.parseStringByData(result, mBanList);
				SDFile.WriteSDFile(hip.GetBuffer(), BanFileName);
				Calendar c = Calendar.getInstance();
				int day = c.get(Calendar.DAY_OF_MONTH);
				SaveData.Write(m_Context, BanFileDay, day);
				Log.v("wmh", "BanFileDay=" + day);
				SetadData();
				if (pd != null) pd.dismiss();
			}
		}
	}

	public Bitmap Getphontnames(String url)
	{
		String filename = Common.getmymd5(url);

		String path = Environment.getExternalStorageDirectory().toString()
				+ "/baifen/img/" + filename;
		File file1 = new File(path);
		if (file1.exists())
		{
			try
			{
				FileInputStream fis = new FileInputStream(path);
				return BitmapFactory.decodeStream(fis);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		else
		{
			return null;
		}
	}

	public Bitmap Getphontnames1(String url)
	{
		String filename = getmymd5(url);

		String path = Environment.getExternalStorageDirectory().toString()
				+ "/baifen/img/" + filename;
		File file1 = new File(path);
		if (file1.exists())
		{
			try
			{
				FileInputStream fis = new FileInputStream(path);
				return BitmapFactory.decodeStream(fis);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				return null;
			}
		}
		else
		{
			return null;
		}
	}

	private void SetadData()
	{// 更新首页图片推荐
		m_ad_Handler.removeCallbacks(m_ad_autoRunnable);
		m_ad_indexNum = 0;
		m_ad_pageViews.clear();
		// 初始化要切换的页面 放入一个list中保存
		for (int i = 0; i < mBanList.size(); i++)
		{
			LayoutInflater inflater = getLayoutInflater();
			final BanBean bean = mBanList.get(i);
			// 绑定UI
			View v = inflater.inflate(R.layout.ad_item, null);
			final ImageView adimage = (ImageView) v
					.findViewById(R.id.myadimage);
			adimage.setTag(bean.getSrc());
			Drawable draw = null;
			draw = mLogoImage.loadNetDrawable(true, bean.getSrc(),
					new ImageCallback()
					{
						@Override
						public void imageLoaded(Drawable imageDrawable,
								String imageUrl)
						{
							if (adimage.getTag().equals(imageUrl))
							{
								adimage.setImageDrawable(imageDrawable);
							}
						}
					});

			if (draw != null)
			{
				adimage.setImageDrawable(draw);
			}
			else
			{
				adimage.setImageResource(R.drawable.deflogo);
			}

			adimage.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					// 点击处理
					if (bean.getType().equals("app"))
					{
						Intent seta = new Intent(HomeActivity.this,
								productActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("title", bean.getTitle());
						bundle.putInt("id", bean.getId());
						seta.putExtras(bundle);
						HomeActivity.this.startActivity(seta);
					}
					else if (bean.getType().equals("theme"))
					{
						Intent seta = new Intent(HomeActivity.this,
								specialitemActivity2.class);
						Bundle bundle = new Bundle();
						bundle.putString("title", bean.getTitle());
						bundle.putString(
								"url",
								"http://api.byfen.com/theme/detail?id="
										+ bean.getId());
						seta.putExtras(bundle);
						startActivity(seta);
					}
				}
			});
			m_ad_pageViews.add(v);
		}
		m_ad_viewPager.removeAllViews();
		m_ad_viewPager.setAdapter(new GuidePageAdapter());
		m_ad_viewPager.setOnPageChangeListener(new GuidePageChangeListener());
		adautoItem();
	}

	public String getmymd5(String aDta)
	{
		MessageDigest md5 = null;
		try
		{
			md5 = MessageDigest.getInstance("MD5");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "";
		}
		char[] charArray = aDta.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++)
		{
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++)
		{
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
			{
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
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
												HomeActivity.this,
												DownLoadService.class);
										serviceIntent
												.putExtra("type", "finish");
										startService(serviceIntent);
										finish();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener()
								{
									public void onClick(
											DialogInterface dialoginterface,
											int i)
									{
									}
								}).show();
			}
		}
		return true;
	}

	@Override
	public void NotifyDataSetChanged(int cmd, Object value)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void NotifyDataSetChanged(int cmd, Object value, int arg1, int arg2)
	{
		//Log.v("wmh", "NotifyDataSetChanged:cmd="+cmd);
		if (cmd == Downloader.CHANGER_STATUS)//状态信息
		{
			SysEng.getInstance().addHandlerEvent(new AbsEvent()
			{
				@Override
				public void ok()
				{
					adapter.notifyDataSetChanged();
				}
			});
		}		
	}
}
