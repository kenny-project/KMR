package com.byfen.market;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byfen.market.bean.AppThemeBean;
import com.work.Image.ImageLoader;
import com.work.market.net.Common;
import com.work.market.net.HttpUtil;

public class SpecialActivity extends Activity implements OnClickListener, OnItemClickListener
{

	public static final String MYURL1 = "http://api.byfen.com/theme/recommend?";
	public static final String MYURL2 = "http://api.byfen.com/theme/new?";
	private Context m_Context;

	public MyAdapter adapter;
	public MyAdapter adapter1;
	private ListView lv,lv1;
	private int times = 0;
	private int times1 = 0;

	private Button m_command_button;
	private Button m_news_button;

	private IntentFilter mIntentFilter; // ��Ϣ����
	

	private ProgressDialog pd;
	private NetTask mNetTask;
	private NetTask1 mNetTask1;
	private ArrayList<String> m_list_url1;
	private ArrayList<String> m_list_url2;
	private int m_Max_page = 1;
	private int m_now_page = 1;
	private int m_everytime_num = 30;
	private int m_list_page = 1;
	private int m_Max_page1 = 1;
	private int m_now_page1 = 1;
	private int m_everytime_num1 = 30;
	private int m_list_page1 = 1;
	private boolean m_first_table = true;

	private ProgressBar mfootProgressBar;
	private TextView mFootTextView;
	private View moreView,moreView1;
	private boolean mEnablePullLoad = false;
	private LinearLayout m_back;

	private ViewPager myViewPager;
	private MyPagerAdapter mAbsPageAdapter = new MyPagerAdapter();
	private ArrayList<View> mListViews = new ArrayList<View>();
	/**
	 * ����ҳ��
	 * 
	 * @param savedInstanceState
	 * @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// ����ҳ��
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// ����
		setContentView(R.layout.special);
		// �쳣����
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction("com.android.my.action");
		mIntentFilter.addAction("com.android.my.action.sticky");
		m_Context = this;
		pd = new ProgressDialog(this);
		pd.setMessage(this.getText(R.string.pd_loading));
		pd.setCancelable(true);

		m_back = (LinearLayout) findViewById(R.id.special_back);
		m_back.setOnClickListener(this);

		moreView = getLayoutInflater().inflate(R.layout.xlistview_footer, null);// ���list
																				// ���
		mfootProgressBar = (ProgressBar) moreView
				.findViewById(R.id.xlistview_footer_progressbar);
		mfootProgressBar.setVisibility(View.INVISIBLE);
		mFootTextView = (TextView) moreView
				.findViewById(R.id.xlistview_footer_hint_textview);
		m_list_url1 = new ArrayList<String>();
		m_list_url2 = new ArrayList<String>();
		m_command_button = (Button) findViewById(R.id.special_tab1_recommand);// @+id/special_tab1_recommand
		m_command_button.setOnClickListener(this);
		m_news_button = (Button) findViewById(R.id.special_tab1_news);// @+id/special_tab1_news
		m_news_button.setOnClickListener(this);

		lv = new ListView(this);
		//lv.addFooterView(moreView, null, false);
		adapter = new MyAdapter(this);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		lv.setOnScrollListener(mScrollListener);

		
		lv1 = new ListView(this);
		//lv1.addFooterView(moreView1, null, false);
		adapter1 = new MyAdapter(this);
		lv1.setAdapter(adapter1);
		lv1.setOnItemClickListener(this);
		lv1.setOnScrollListener(mScrollListener);

		mListViews.add(lv);
		mListViews.add(lv1);
		m_command_button.setBackgroundResource(R.drawable.toptab_line);
		m_command_button.setTextColor(getResources().getColor(
				R.color.head_tab_selected_textcolor));// setTextColor
		m_news_button.setBackgroundResource(R.drawable.toptab_bg_soft);
		m_news_button.setTextColor(this.getResources().getColor(
				R.color.toptab_TextColor_normal));
		if (adapter.getCount() == 0)
		{
			upData(0, true, MYURL1);
		}
		
		m_first_table = true;
		myViewPager = (ViewPager) findViewById(R.id.viewpagerLayout);
		myViewPager.setAdapter(mAbsPageAdapter);
		myViewPager.setCurrentItem(0);
		myViewPager.setOnPageChangeListener(new OnPageChangeListener()
		{

			public void onPageSelected(int arg0)
			{
				// P.v("king", "onPageSelected - " + arg0);
				// activity��1��2������2�����غ���ô˷���
				// View v = mListViews.get(arg0);
				SwitchPage(arg0);
			}

			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
				// P.v("king", "onPageScrolled:arg0=" + arg0);//+",arg1=" +
				// arg1+",arg2=" + arg2);
				// ��1��2��������1����ǰ����
			}

			public void onPageScrollStateChanged(int arg0)
			{
			}
		});
		myViewPager.setCurrentItem(0);
	}
	public void SwitchPage(int index)
	{
		switch (index)
		{
		case 0:
			m_command_button.setBackgroundResource(R.drawable.toptab_line);
			m_command_button.setTextColor(getResources().getColor(
					R.color.head_tab_selected_textcolor));// setTextColor
			m_news_button.setBackgroundResource(R.drawable.toptab_no_line);
			m_news_button.setTextColor(this.getResources().getColor(
					R.color.toptab_TextColor_normal));
//			lv1.setAdapter(adapter);
			m_first_table = true;
			if (adapter.getCount() == 0)
			{
				upData(0, true, MYURL1);
			}
			break;
		case 1:
			m_news_button.setBackgroundResource(R.drawable.toptab_line);
			m_news_button.setTextColor(getResources().getColor(
					R.color.head_tab_selected_textcolor));// setTextColor
			m_command_button.setBackgroundResource(R.drawable.toptab_no_line);
			m_command_button.setTextColor(this.getResources().getColor(
					R.color.toptab_TextColor_normal));
//			lv1.setAdapter(adapter1);
			m_first_table = false;
			if (adapter1.getCount() == 0)
			{
				upData1(0, true, MYURL2);
			}
			break;
		}
	}
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.special_tab1_recommand:
			myViewPager.setCurrentItem(0);
			break;
		case R.id.special_tab1_news:
			myViewPager.setCurrentItem(1);
			break;
		case R.id.special_back:
			this.finish();
			break;
		default:
			break;
		}
	}
	OnScrollListener mScrollListener= new OnScrollListener()
	{

		@Override
		public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3)
		{
			// TODO Auto-generated method stub

		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			// TODO Auto-generated method stub
			Log.v("==================", scrollState + "");
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					|| scrollState == OnScrollListener.SCROLL_STATE_FLING)
			{
				// �ж��Ƿ�������ײ�
				if (view.getLastVisiblePosition() == view.getCount() - 1
						&& !mEnablePullLoad)
				{
					if (m_first_table)
					{
						if (m_now_page < m_Max_page)
						{
							m_list_page = m_now_page + 1;
							m_everytime_num = 30;
							addLoadMore();
							upData(0, false, MYURL1);
						}
					}
					else
					{
						if (m_now_page1 < m_Max_page1)
						{
							m_list_page1 = m_now_page1 + 1;
							m_everytime_num1 = 30;
							addLoadMore();
							upData1(0, false, MYURL2);
						}
					}

				}
			}

		}
	};
	private class MyAdapter extends BaseAdapter
	{

		private LayoutInflater inflater;
		public Context mcontext;
		private ArrayList<AppThemeBean> List = new ArrayList<AppThemeBean>();
		protected boolean bShowLogo = true; // �Ƿ��������غ�̨ͼƬ
		private ImageLoader mLogoImage;
		public MyAdapter(Context context)
		{

			super();
			List.clear();
			mcontext = context;
			inflater = LayoutInflater.from(mcontext);
			mLogoImage = ImageLoader.GetObject(mcontext);
		}

		public void Clear()
		{
			List.clear();
		}

		public int AddBean(AppThemeBean bean)
		{
			List.add(bean);
			return 1;
		}

		public int getCount()
		{
			return List.size();
		}

		public Object getItem(int arg0)
		{

			// TODO Auto-generated method stub
			return List.get(arg0);
		}
		public void notifyDataSetChanged() {
			bShowLogo = true;
			super.notifyDataSetChanged();
		}

		public boolean isShowLogo() {
			return bShowLogo;
		}

		public void setShowLogo(boolean bShowLogo) {
			mLogoImage.cancel();
			this.bShowLogo = bShowLogo;

		}
		class KImageCallback implements com.work.Interface.ImageCallback {
			ImageView logImage;

			public KImageCallback(ImageView logImage) {
				this.logImage = logImage;
			}

			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				// TODO Auto-generated method stub
				// notifyDataSetChanged();
				logImage.setImageDrawable(imageDrawable);
				// viewHolder.mIV.setImageDrawable(imageDrawable);
			}
		};
		public View getView(final int position, View view, ViewGroup arg2)
		{

			if (view == null)
			{
				view = inflater.inflate(R.layout.list_specialitem, null);
			}
			AppThemeBean bean = List.get(position);

			final ImageView logImage = (ImageView) view
					.findViewById(R.id.special_item_image);// @+id/special_item_image
			Drawable draw = null;
				draw = mLogoImage.loadNetDrawable(bShowLogo,bean.getLogo(),
						new KImageCallback(logImage));
				if (draw != null) 
				{
					logImage.setImageDrawable(draw);
				} else 
				{
					logImage.setImageResource(R.drawable.deflogo);
				}
			final TextView mytext = (TextView) view
					.findViewById(R.id.special_item_title_text);// @+id/special_item_title_text
			mytext.setText(bean.getTitle());

			final TextView mytext1 = (TextView) view
					.findViewById(R.id.special_item_time_text);// @+id/special_item_time_text
			mytext1.setText(bean.getTime());

			final TextView mytext2 = (TextView) view
					.findViewById(R.id.special_item_descripte_text);// @+id/special_item_descripte_text
			mytext2.setText(bean.getCount()+"��Ӧ��");
			return view;
		}

		@Override
		public long getItemId(int arg0)
		{
			// TODO Auto-generated method stub
			return 0;
		}
	}

	private void StartActivty()
	{
		Intent seta1 = new Intent(SpecialActivity.this,
				SubjectGroupActivity.class);// RockDiscountActivity RockActivity
											// RockDiscountActivity

		this.startActivity(seta1);
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
		super.onPause();
	}

	private void upData(int num, boolean apd, String url)
	{

		if (apd) pd.show();
		if (mNetTask != null)
		{
			mNetTask.cancel(true);
		}
		mNetTask = new NetTask(num, apd, url);
		mNetTask.execute("");// m_ad_layout.setVisibility
	}

	// ͼƬ����
	class NetTask extends AsyncTask<Object, Integer, String>
	{

		private int m_num = 0;
		private boolean m_showlog = false;
		private String m_url;

		private NetTask(int aNUm, boolean showlog, String aurl)
		{
			m_num = aNUm;
			m_showlog = showlog;
			m_url = aurl;
		}

		protected String doInBackground(Object... params)
		{
			if (m_num == 0)
			{// adapter.title.add(keyword);
				String temp = m_url + "page=" + m_list_page + "&per_page="
						+ m_everytime_num;
				String json = HttpUtil.doGet(temp);// =
													// NetUtil.photoShow(mContext,
													// SharedUtil.getUserKey(mContext));
				return json;

			}
			else if (m_num == 1)
			{
				String filename = Common.getMd5Code(m_url) + ".jpg";
				String data = HttpUtil.GetPhoto5(m_url, filename);//
				return data;
			}
			return null;// m_tempurl = new ArrayList<String>() ;
			// m_tempBitmap = new ArrayList<Bitmap>() ;
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result)
		{
			if (!pd.isShowing() && m_showlog) return;

			if (result == null)
			{// ʧ�� ����
				pd.dismiss();
				String dialogstring = getString(R.string.net_faile);
				Toast.makeText(m_Context, dialogstring, Toast.LENGTH_LONG)
						.show();

			}
			else
			{
				if (m_num == 0)
				{
					try
					{

						JSONObject jsresult = new JSONObject(result);//

						String maxpage = jsresult.getString("last_page");//
						String nowpage = jsresult.getString("cur_page");//
						m_Max_page = Integer.parseInt(maxpage);
						m_now_page = Integer.parseInt(nowpage);
						JSONArray jsonObj1 = jsresult.getJSONArray("list");
						adapter.Clear();
						for (int i = 0; i < jsonObj1.length(); i++)
						{
							JSONObject tempJson = jsonObj1.optJSONObject(i);
							AppThemeBean bean = new AppThemeBean();
							bean.setId(tempJson.getString("id"));
							bean.setTitle(tempJson.getString("title"));
							bean.setDesc(tempJson.getString("desc"));
							bean.setLogo(tempJson.getString("logo"));
							bean.setTime(getString(R.string.times)
									+ tempJson.getString("time"));//
							bean.setKind(tempJson.getString("kind"));
							bean.setCount(tempJson.getString("count"));
							bean.setScore(tempJson.getString("score"));
							adapter.AddBean(bean);
						}
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
					lv1.setAdapter(adapter);
					adapter.notifyDataSetChanged();
					pd.dismiss();

				}
			}
		}
	}

	private void upData1(int num, boolean apd, String url)
	{

		if (apd) pd.show();
		if (mNetTask1 != null)
		{
			mNetTask1.cancel(true);
		}
		mNetTask1 = new NetTask1(num, apd, url);
		mNetTask1.execute("");// m_ad_layout.setVisibility
	}

	// ͼƬ����
	class NetTask1 extends AsyncTask<Object, Integer, String>
	{

		private int m_num = 0;
		private boolean m_showlog = false;
		private String m_url;

		private NetTask1(int aNUm, boolean showlog, String aurl)
		{
			m_num = aNUm;
			m_showlog = showlog;
			m_url = aurl;
		}

		protected String doInBackground(Object... params)
		{
			if (m_num == 0)
			{// adapter.title.add(keyword);
				String temp = m_url + "page=" + m_list_page1 + "&per_page="
						+ m_everytime_num1;
				String json = HttpUtil.doGet(temp);// =
													// NetUtil.photoShow(mContext,
													// SharedUtil.getUserKey(mContext));
				return json;
			}
			else if (m_num == 1)
			{
				String filename = Common.getMd5Code(m_url) + ".jpg";
				String data = HttpUtil.GetPhoto5(m_url, filename);//
				return data;
			}
			return null;// m_tempurl = new ArrayList<String>() ;
			// m_tempBitmap = new ArrayList<Bitmap>() ;
		}

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result)
		{
			if (!pd.isShowing() && m_showlog) return;

			if (result == null)
			{// ʧ�� ����
				pd.dismiss();
				String dialogstring = getString(R.string.net_faile);
				Toast.makeText(m_Context, dialogstring, Toast.LENGTH_LONG)
						.show();

			}
			else
			{
				if (m_num == 0)
				{
					try
					{

						JSONObject jsresult = new JSONObject(result);//
						String maxpage = jsresult.getString("last_page");//
						String nowpage = jsresult.getString("cur_page");//
						m_Max_page1 = Integer.parseInt(maxpage);
						m_now_page1 = Integer.parseInt(nowpage);
						JSONArray jsonObj1 = jsresult.getJSONArray("list");
						for (int i = 0; i < jsonObj1.length(); i++)
						{
							JSONObject tempJson = jsonObj1.optJSONObject(i);
							AppThemeBean bean = new AppThemeBean();
							bean.setId(tempJson.getString("id"));
							bean.setTitle(tempJson.getString("title"));
							bean.setDesc(tempJson.getString("desc"));
							bean.setLogo(tempJson.getString("logo"));
							bean.setTime(getString(R.string.times)
									+ tempJson.getString("time"));//
							bean.setKind(tempJson.getString("kind"));
							bean.setCount(tempJson.getString("count"));
							bean.setScore(tempJson.getString("score"));
							adapter1.AddBean(bean);
						}
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
					lv1.setAdapter(adapter1);
					adapter1.notifyDataSetChanged();
					pd.dismiss();

				}
			}
		}
	}

	public Bitmap Getphontnames(String url)
	{
		String filename = Common.getMd5Code(url) + ".jpg";

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

	public void addLoadMore()
	{
		if (!mEnablePullLoad)
		{
			mEnablePullLoad = true;
			mfootProgressBar.setVisibility(View.VISIBLE);
			mFootTextView.setVisibility(View.INVISIBLE);
		}
	}

	public void removeLoadMore()
	{
		if (mEnablePullLoad)
		{
			mEnablePullLoad = false;
			mFootTextView.setVisibility(View.VISIBLE);
			mfootProgressBar.setVisibility(View.INVISIBLE);
		}

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
			// P.v("k", "finishUpdate");
		}

		public int getCount()
		{
			// P.v("k", "getCount");
			return mListViews.size();
		}

		public Object instantiateItem(View arg0, int arg1)
		{
			Log.v("k", "instantiateItem");
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);

			return mListViews.get(arg1);
		}

		public boolean isViewFromObject(View arg0, Object arg1)
		{
			// P.v("k", "isViewFromObject");
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		AppThemeBean bean=(AppThemeBean)arg0.getAdapter().getItem(arg2);;
		Intent seta = new Intent(SpecialActivity.this,
				specialitemActivity2.class);
		Bundle bundle = new Bundle();
		bundle.putString("title", bean.getTitle());
		bundle.putString("url", "http://api.byfen.com/theme/detail?id="
				+ bean.getId());
		seta.putExtras(bundle);
		startActivity(seta);		
	}
}
