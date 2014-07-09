package com.work.market.view;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
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
import com.work.market.adapter.SoftListAdapter;
import com.work.market.bean.AppListBean;
import com.work.market.net.Common;
import com.work.market.net.HttpUtil;

public class SoftlistView2 extends LinearLayout
{

	private Context mContext;
	private Activity m_MainActivity;

	private ProgressDialog pd;
	private NetTask1 mNetTask;

	public SoftListAdapter adaptersoft;
	private ListView lv1;
	private String m_urls;

	private boolean mEnablePullLoad = false;
	private ProgressBar mfootProgressBar;
	private TextView mFootTextView;
	private View moreView;

	private List<AppListBean> mList = new ArrayList<AppListBean>();

	public SoftlistView2(Context context, Activity aActivity)
	{
		this(context, null, aActivity);
		mContext = context;
	}

	public SoftlistView2(Context context, AttributeSet attrs, Activity aActivity)
	{
		super(context, attrs);
		mContext = context;
		m_MainActivity = aActivity;
		LayoutInflater.from(context).inflate(R.layout.softlistview2, this, true);
		pd = new ProgressDialog(m_MainActivity);
		pd.setMessage(m_MainActivity.getText(R.string.pd_loading));
		pd.setCancelable(true);
		moreView = m_MainActivity.getLayoutInflater().inflate(
				R.layout.xlistview_footer, null);// ���list ���
		mfootProgressBar = (ProgressBar) moreView
				.findViewById(R.id.xlistview_footer_progressbar);
		mFootTextView = (TextView) moreView
				.findViewById(R.id.xlistview_footer_hint_textview);
		lv1 = (ListView) findViewById(R.id.softlist_soft_ls);
		// lv1.addFooterView(moreView,null,false);
		lv1.setOnScrollListener(m_localOnScrollListener);
		KApp app=((KApp)context.getApplicationContext());
		adaptersoft = new SoftListAdapter(mContext, mList);
		mfootProgressBar.setVisibility(View.INVISIBLE);
		lv1.setAdapter(adaptersoft);

		lv1.setOnItemClickListener(new OnItemClickListener()
		{

			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id)
			{
				Intent seta = new Intent(mContext, productActivity.class);
				Bundle bundle = new Bundle();
				String dialogstring = mList.get(pos).getTitle();
				bundle.putString("title", dialogstring);
				dialogstring = mList.get(pos).getPn();
				int tempID = mList.get(pos).getId();
				bundle.putInt("id", tempID);
				seta.putExtras(bundle);
				seta.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				m_MainActivity.startActivity(seta);// SoftclassActivity
			}
		});

	}

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
		mNetTask = new NetTask1( url, apd);
		mNetTask.execute("");
	}

	// 
	class NetTask1 extends AsyncTask<Object, Integer, String>
	{
		private boolean m_showlog = false;
		private String m_url;

		private NetTask1(String aurl, boolean ashow)
		{
			m_url = aurl;
			m_showlog = ashow;
		}

		protected String doInBackground(Object... params)
		{

			String url = m_url;
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
			if (!pd.isShowing() && m_showlog) return;

			if (result == null)
			{// ʧ�� ����
				pd.dismiss();
				if (mEnablePullLoad)
				{
					removeLoadMore();
				}
				String dialogstring = m_MainActivity
						.getString(R.string.net_faile);
				Toast.makeText(mContext, dialogstring, Toast.LENGTH_LONG)
						.show();

			}
			else
			{
				try
				{
					JSONObject jsresult = new JSONObject(result);//

					JSONArray jsonObj1 = jsresult.getJSONArray("child_list");
					for (int i = 0; i < jsonObj1.length(); i++)
					{
						JSONObject tempJson = jsonObj1.optJSONObject(i);
						AppListBean tempAppListBean = new AppListBean();
						tempAppListBean.setId(tempJson.getString("id"));// ����
						tempAppListBean.setTitle(tempJson.getString("title"));//
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
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				if (mEnablePullLoad)
				{
					removeLoadMore();
				}
				adaptersoft.notifyDataSetChanged();
				pd.dismiss();

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

}
