package com.work.market.view;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byfen.app.KApp;
import com.byfen.market.R;
import com.byfen.market.productActivity;
import com.framework.log.P;
import com.nono.nlpullrefreshviewdemo.NLPullRefreshView;
import com.nono.nlpullrefreshviewdemo.NLPullRefreshView.RefreshListener;
import com.work.market.adapter.SoftListAdapter;
import com.work.market.bean.AppListBean;
import com.work.market.net.Common;
import com.work.market.net.HttpUtil;

public class SoftFilterlistView extends LinearLayout implements RefreshListener
{
	private Context mContext;
	private ProgressDialog pd;
	private NetTask1 mNetTask;
	public SoftListAdapter adaptersoft;
	private ListView lv1;
	private String m_urls;
	private int m_Max_page = 1;
	private int m_now_page = 1;
	private final static int m_everytime_num = 30;
	private int m_list_page = 1;
	private boolean mEnablePullLoad = false;
	private ProgressBar mfootProgressBar;
	private TextView mFootTextView;
	private View moreView;
	private List<AppListBean> mList = new ArrayList<AppListBean>();
	private boolean bRefreshLoading=false;//����ˢ��
	private NLPullRefreshView mPullRefreshView ;
	public SoftFilterlistView(Context context)
	{
		super(context);
		init(context);
	}

	public SoftFilterlistView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public void init(Context context)
	{
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.softlist, this, true);
		pd = new ProgressDialog(context);
		pd.setMessage(context.getText(R.string.pd_loading));
		pd.setCancelable(true);
		moreView = View.inflate(context, R.layout.xlistview_footer, null);
		mfootProgressBar = (ProgressBar) moreView
				.findViewById(R.id.xlistview_footer_progressbar);
		mFootTextView = (TextView) moreView
				.findViewById(R.id.xlistview_footer_hint_textview);
		mPullRefreshView = (NLPullRefreshView) findViewById(R.id.refresh_root);
		mPullRefreshView.setRefreshListener(this);
		
		lv1 = (ListView) findViewById(R.id.softlist_soft_ls);
		lv1.addFooterView(moreView, null, false);
		KApp app = ((KApp) context.getApplicationContext());
		adaptersoft = new SoftListAdapter(mContext, mList);
		mfootProgressBar.setVisibility(View.INVISIBLE);
		lv1.setAdapter(adaptersoft);
		lv1.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id)
			{
//				if(pos>0)
//				{
//					pos--;
//				}
				AppListBean bean = (AppListBean)adaptersoft.getItem(pos);
				Intent seta = new Intent(getContext(), productActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("title", bean.getTitle());
				bundle.putInt("id", bean.getId());
				seta.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				seta.putExtras(bundle);
				getContext().startActivity(seta);
			}
		});
		
		lv1.setOnScrollListener(m_localOnScrollListener);
//		lv1.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener()
//		{
//			
//		});
	}

	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			switch (msg.what)
			{
			case 0:
//				Toast.makeText(mContext, "ˢ�³ɹ�", Toast.LENGTH_LONG).show();
//				lv1
//				.onRefreshComplete(mContext.getString(R.string.pull_to_refresh_update)
//						+ new Date().toLocaleString());
//				lv1.onRefreshComplete();
				mList.clear();
				mList.addAll(mTempRefreshList);
				adaptersoft.notifyDataSetChanged();
				mPullRefreshView.finishRefresh();
				//Toast.makeText(mContext, "���ˢ��", Toast.LENGTH_SHORT).show();
				break;
			case 1://error
				Toast.makeText(mContext,
						mContext.getString(R.string.net_faile),
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};

	private OnScrollListener m_localOnScrollListener = new OnScrollListener()
	{

		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			switch (scrollState)
			{
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
				P.debug("SCROLL_STATE_FLING");
				if (adaptersoft != null) adaptersoft.setShowLogo(false);
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
				P.debug("SCROLL_STATE_IDLE");
				if (adaptersoft != null)
				{
					adaptersoft.setShowLogo(true);
					adaptersoft.notifyDataSetChanged();
				}
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				P.debug("SCROLL_STATE_TOUCH_SCROLL");
				if (adaptersoft != null) adaptersoft.setShowLogo(false);
				break;
			default:
				break;
			}
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
					|| scrollState == OnScrollListener.SCROLL_STATE_FLING)
			{
				// �ж��Ƿ�������ײ�
				if (lv1.getLastVisiblePosition() == lv1.getCount() - 1
						&& !mEnablePullLoad)
				{
					if (m_now_page < m_Max_page)
					{
						m_list_page = m_now_page + 1;
						addLoadMore();
						upDatasoft(false, m_urls);
					}
				}
			}
		}

		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount)
		{

		}
	};

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

	private void upDatasoft(boolean apd, String url)
	{

		if (apd) pd.show();
		if (mNetTask != null)
		{
			mNetTask.cancel(true);
		}
		mNetTask = new NetTask1(url);
		mNetTask.execute("");
	}

	// ͼƬ����
	class NetTask1 extends AsyncTask<Object, Integer, String>
	{

		private String m_url;

		private NetTask1(String aurl)
		{
			m_url = aurl;
		}

		protected String doInBackground(Object... params)
		{

			String url = m_url + "page=" + m_list_page + "&per_page="
					+ m_everytime_num;
			String json = HttpUtil.doGet(url);
			Log.v("wmh", "EndsoftlistViewurl" + url);
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
			Log.v("wmh", "onPostExecute");
			if (result == null)
			{// ʧ�� ����
				pd.dismiss();
				if (mEnablePullLoad)
				{
					removeLoadMore();
				}
				Toast.makeText(mContext,
						mContext.getString(R.string.net_faile),
						Toast.LENGTH_LONG).show();
			}
			else
			{
				try
				{
					JSONObject jsresult = new JSONObject(result);//
					String maxpage = jsresult.getString("last_page");//
					String nowpage = jsresult.getString("cur_page");//
					m_Max_page = Integer.parseInt(maxpage);
					m_now_page = Integer.parseInt(nowpage);
					Log.v("wmh", "m_now_page=" + m_now_page);
					if (m_list_page == 1)
					{
						mList.clear();
					}
					JSONArray jsonObj1 = jsresult.getJSONArray("list");
					for (int i = 0; i < jsonObj1.length(); i++)
					{
						JSONObject tempJson = jsonObj1.optJSONObject(i);
						AppListBean tempAppListBean = new AppListBean();
						tempAppListBean.setId(tempJson.getString("id"));// ����
						tempAppListBean.setTitle(tempJson.getString("title"));// ����
						tempAppListBean.setPn(tempJson.getString("pn"));// package��
						tempAppListBean.setLogo(tempJson.getString("logo"));// ͼƬ��ַ
						tempAppListBean.setSize(Common.getLength(tempJson
								.getString("size")));// �ļ���С(�ֽ�)score
						tempAppListBean.setScore(tempJson.getString("score"));// �ļ���С(�ֽ�)
						tempAppListBean.setAppurl(tempJson.getString("apkurl"));// ���ص�ַ
						tempAppListBean.setDowntiems(tempJson.getString("dc"));// ���ش���
						tempAppListBean
								.setAppFileExt(tempJson.getString("ext"));
						mList.add(tempAppListBean);
					}
					adaptersoft.notifyDataSetChanged();
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				if (mEnablePullLoad)
				{
					removeLoadMore();
				}
				if (pd != null)
				{
					pd.dismiss();
				}
				Log.v("wmh",
						"onPostExecute end count=" + adaptersoft.getCount());
			}
		}
	}

	public void SetUrl(String aUrl)
	{
		m_urls = aUrl;
	}

	public void SetList()
	{
		if (adaptersoft.getCount() == 0)
		{
			upDatasoft(true, m_urls);
		}
	}

	public void Notificview()
	{
		adaptersoft.notifyDataSetChanged();
	}
	List<AppListBean> mTempRefreshList = new ArrayList<AppListBean>();
	@Override
	public void onRefresh(NLPullRefreshView view)
	{
			// ��������������/��ݿ����/�ļ�����
			m_now_page = 1;
			m_list_page = m_now_page;
			new Thread()
			{
				@Override
				public void run()
				{
					if(bRefreshLoading)
					{
					handler.sendEmptyMessage(0);
					return;
					}
					bRefreshLoading=true;		
					String url = m_urls + "page=" + m_list_page
							+ "&per_page=" + m_everytime_num;
					String json = HttpUtil.doGet(url);
					Log.v("wmh", "onRefresh():onPostExecute");
					if (json != null)
					{
						try
						{
							JSONObject jsresult = new JSONObject(json);//
							String maxpage = jsresult
									.getString("last_page");//
							String nowpage = jsresult.getString("cur_page");//
							m_Max_page = Integer.parseInt(maxpage);
							m_now_page = Integer.parseInt(nowpage);
							JSONArray jsonObj1 = jsresult
									.getJSONArray("list");
							
							mTempRefreshList.clear();
							for (int i = 0; i < jsonObj1.length(); i++)
							{
								JSONObject tempJson = jsonObj1
										.optJSONObject(i);
								AppListBean tempAppListBean = new AppListBean();
								tempAppListBean.setId(tempJson
										.getString("id"));// ����
								tempAppListBean.setTitle(tempJson
										.getString("title"));// ����
								tempAppListBean.setPn(tempJson
										.getString("pn"));// package��
								tempAppListBean.setLogo(tempJson
										.getString("logo"));// ͼƬ��ַ
								tempAppListBean.setSize(Common
										.getLength(tempJson
												.getString("size")));// �ļ���С(�ֽ�)score
								tempAppListBean.setScore(tempJson
										.getString("score"));// �ļ���С(�ֽ�)
								tempAppListBean.setAppurl(tempJson
										.getString("apkurl"));// ���ص�ַ
								tempAppListBean.setDowntiems(tempJson
										.getString("dc"));// ���ش���
								tempAppListBean.setAppFileExt(tempJson
										.getString("ext"));
								mTempRefreshList.add(tempAppListBean);
							}
							handler.sendEmptyMessage(0);
							bRefreshLoading=false;
							return;
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					handler.sendEmptyMessage(1);
					bRefreshLoading=false;
				}

			}.start();

		
	}
}
