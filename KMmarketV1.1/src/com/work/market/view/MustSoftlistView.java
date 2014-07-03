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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byfen.app.KApp;
import com.byfen.market.R;
import com.byfen.market.productActivity;
import com.framework.log.P;
import com.framework.syseng.AbsEvent;
import com.framework.syseng.SysEng;
import com.work.Interface.INotifyDataSetChanged;
import com.work.market.adapter.SoftListAdapter;
import com.work.market.bean.AppListBean;
import com.work.market.net.Common;
import com.work.market.net.Downloader;
import com.work.market.net.HttpUtil;
import com.work.market.server.DownLoadService;

/**
 * 装机必备
 * 
 * @author WangMinghui
 * 
 */
public class MustSoftlistView extends ObjectView implements INotifyDataSetChanged {

	private Context mContext;

	private ProgressDialog pd;
	private NetTask1 mNetTask;

	public SoftListAdapter adaptersoft;
	private ListView lv1;
	private String m_urls;
	private boolean mEnablePullLoad = false;
	private ProgressBar mfootProgressBar;
	private TextView mFootTextView;
	private View moreView;
	private KApp app;
	private List<AppListBean> mList = new ArrayList<AppListBean>();

	public MustSoftlistView(Context context) {
		this(context, null);
		mContext = context;
	}

	public MustSoftlistView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		app = ((KApp) context.getApplicationContext());
		LayoutInflater.from(context).inflate(R.layout.must_softlistview, this, true);
		pd = new ProgressDialog(context);
		pd.setMessage(context.getText(R.string.pd_loading));
		pd.setCancelable(true);
		moreView =LayoutInflater.from(context).inflate(
				R.layout.xlistview_footer, null);// 添加list 进度
		mfootProgressBar = (ProgressBar) moreView
				.findViewById(R.id.xlistview_footer_progressbar);
		mFootTextView = (TextView) moreView
				.findViewById(R.id.xlistview_footer_hint_textview);
		lv1 = (ListView) findViewById(R.id.softlist_soft_ls);
		// lv1.addFooterView(moreView,null,false);
		KApp app=((KApp)context.getApplicationContext());
		adaptersoft = new SoftListAdapter(mContext, mList);
		mfootProgressBar.setVisibility(View.INVISIBLE);
		lv1.setAdapter(adaptersoft);

		lv1.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Intent seta = new Intent(mContext, productActivity.class);
				Bundle bundle = new Bundle();
				String dialogstring = mList.get(pos).getTitle();
				bundle.putString("title", dialogstring);
				dialogstring = mList.get(pos).getPn();
				int tempID = mList.get(pos).getId();
				bundle.putInt("id", tempID);
				seta.putExtras(bundle);
				seta.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				getContext().startActivity(seta);// SoftclassActivity
			}
		});
		lv1.setOnScrollListener(m_localOnScrollListener);

	}
	private OnScrollListener m_localOnScrollListener = new OnScrollListener()
	{

		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			switch (scrollState)
			{
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
				P.debug("SCROLL_STATE_FLING");
				if (adaptersoft != null)
					adaptersoft.setShowLogo(false);
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
				if (adaptersoft != null)
					adaptersoft.setShowLogo(false);
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
	public void addLoadMore() {
		if (!mEnablePullLoad) {
			mEnablePullLoad = true;
			mfootProgressBar.setVisibility(View.VISIBLE);
			mFootTextView.setVisibility(View.INVISIBLE);
		}
	}

	public void removeLoadMore() {
		if (mEnablePullLoad) {
			mEnablePullLoad = false;
			mFootTextView.setVisibility(View.VISIBLE);
			mfootProgressBar.setVisibility(View.INVISIBLE);
		}

	}

	private void upDatasoft(int num, boolean apd, String url) {

		if (apd)
			pd.show();
		if (mNetTask != null) {
			mNetTask.cancel(true);
		}
		mNetTask = new NetTask1(num, url, apd);
		mNetTask.execute(null);// m_ad_layout.setVisibility
	}

	// 图片处理
	class NetTask1 extends AsyncTask<Object, Integer, String> {

		private int m_num = 0;
		private boolean m_showlog = false;
		private String m_url;

		private NetTask1(int aNUm, String aurl, boolean ashow) {
			m_num = aNUm;
			m_url = aurl;
			m_showlog = ashow;
		}

		protected String doInBackground(Object... params) {

			if (m_num == 0) {
				String url = m_url;
				// String url = "http://api.byfen.com/home/list?page=" +
				// m_list_page + "&per_page=" + m_everytime_num;
				String json = HttpUtil.doGet(url);// =
													// NetUtil.photoShow(mContext,
													// SharedUtil.getUserKey(mContext));
				return json;
			} else if (m_num == 1) {
				String filename = Common.getmymd5(m_url) + ".jpg";
				String data = HttpUtil.GetPhoto5(m_url, filename);//
				return data;
			}
			return null;// m_tempurl = new ArrayList<String>() ;
			// m_tempBitmap = new ArrayList<Bitmap>() ;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			if (!pd.isShowing() && m_showlog)
				return;

			if (result == null) {// 失败 处理
				pd.dismiss();
				if (mEnablePullLoad) {
					removeLoadMore();
				}
				String dialogstring = mContext
						.getString(R.string.net_faile);
				Toast.makeText(mContext, dialogstring, Toast.LENGTH_LONG)
						.show();

			} else {
				if (m_num == 0) 
				{
					int ti=0,tj=0;
					try {
						JSONArray jsresult = new JSONArray(result);//
						for (int i = 0; i < jsresult.length(); i++) 
						{
							JSONArray appListArray = jsresult.optJSONObject(i)
									.getJSONArray("list");
							ti=i;
							for (int j = 0; j < appListArray.length(); j++) 
							{
								tj=j;
								JSONObject tempJson = appListArray.optJSONObject(j);
								AppListBean tempAppListBean = new AppListBean();
								tempAppListBean.setId(tempJson.getString("id"));// 标题
								tempAppListBean.setTitle(tempJson
										.getString("title"));//
								tempAppListBean.setPn(tempJson.getString("pn"));// package码
								tempAppListBean.setLogourl(tempJson
										.getString("logo"));// 图片地址
								tempAppListBean.setSize(Common
										.getLength(tempJson.getString("size")));// 文件大小(字节)score
								tempAppListBean.setScore(tempJson
										.getString("score"));// 文件大小(字节)
								tempAppListBean.setAppurl(tempJson
										.getString("apkurl"));// 下载地址
								tempAppListBean.setDowntiems(tempJson
										.getString("dc"));// 下载次数
								tempAppListBean.setAppFileExt(tempJson.getString("ext"));
								mList.add(tempAppListBean);
							}
						}
					}
					catch (JSONException e) 
					{
						e.printStackTrace();
						Log.e("wmh", "ti,tj"+ti+","+tj);
					}
					if (mEnablePullLoad) {
						removeLoadMore();
					}
					adaptersoft.notifyDataSetChanged();
					pd.dismiss();

				} else if (m_num == 1) {
					adaptersoft.notifyDataSetChanged();
				}
			}
		}
	}

	public void SetUrl(String aUrl) {
		m_urls = aUrl;
	}


	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		if (adaptersoft.getCount() == 0) {
			upDatasoft(0, true, m_urls);
		}
		app.setINotifyChanged(this);
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		app.setINotifyChanged(null);
	}
	  
	  @Override
		public void NotifyDataSetChanged(int cmd, Object value)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void NotifyDataSetChanged(int cmd, Object value, int arg1, int arg2)
		{
			if (cmd == Downloader.CHANGER_STATUS)//状态信息
			{
				SysEng.getInstance().addHandlerEvent(new AbsEvent()
				{
					@Override
					public void ok()
					{
						adaptersoft.notifyDataSetChanged();
					}
				});
			}		
		}
}
