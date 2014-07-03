package com.work.market.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byfen.market.R;
import com.byfen.market.productActivity;
import com.work.market.adapter.SoftListAdapter;
import com.work.market.bean.AppListBean;
import com.work.market.net.Common;
import com.work.market.net.HttpUtil;

public class SoftExpandableListView extends LinearLayout {

	private Context mContext;
	private Activity m_MainActivity;

	private ProgressDialog pd;
	private NetTask1 mNetTask;

	public SoftListAdapter adaptersoft;
	private ExpandableListView lv1;
	private String m_urls;
	private ArrayList<String> m_list_url;
	private boolean mEnablePullLoad = false;
	private ProgressBar mfootProgressBar;
	private TextView mFootTextView;
	private View moreView;

	private List<AppListBean> mList = new ArrayList<AppListBean>();

	public SoftExpandableListView(Context context, Activity aActivity) {
		this(context, null, aActivity);
		mContext = context;
	}

	public SoftExpandableListView(Context context, AttributeSet attrs,
			Activity aActivity) {
		super(context, attrs);
		mContext = context;
		m_MainActivity = aActivity;
		LayoutInflater.from(context).inflate(R.layout.softlist, this, true);
		pd = new ProgressDialog(m_MainActivity);
		pd.setMessage(m_MainActivity.getText(R.string.pd_loading));
		pd.setCancelable(true);
		moreView = m_MainActivity.getLayoutInflater().inflate(
				R.layout.xlistview_footer, null);// 添加list 进度
		mfootProgressBar = (ProgressBar) moreView
				.findViewById(R.id.xlistview_footer_progressbar);
		mFootTextView = (TextView) moreView
				.findViewById(R.id.xlistview_footer_hint_textview);
		lv1 = (ExpandableListView) findViewById(R.id.softlist_soft_ls);
		// lv1.addFooterView(moreView,null,false);
		adaptersoft = new SoftListAdapter(mContext,mList);

		mfootProgressBar.setVisibility(View.INVISIBLE);
		lv1.setAdapter(adaptersoft);
		m_list_url = new ArrayList<String>();

		lv1.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true;
			}
		});
		lv1.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Intent seta = new Intent(mContext, productActivity.class);
				Bundle bundle = new Bundle();
				String dialogstring = mList.get(pos).getTitle();
				bundle.putString("title", dialogstring);
				dialogstring = mList.get(pos).getPn();
				bundle.putString("pn", dialogstring);
				int tempID = mList.get(pos).getId();
				bundle.putInt("id", tempID);
				seta.putExtras(bundle);
				m_MainActivity.startActivity(seta);// SoftclassActivity
			}
		});

	}

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
				String dialogstring = m_MainActivity
						.getString(R.string.net_faile);
				Toast.makeText(mContext, dialogstring, Toast.LENGTH_LONG)
						.show();

			} else {
				if (m_num == 0) {

					try {
						JSONObject jsresult = new JSONObject(result);//

						JSONArray jsonObj1 = jsresult
								.getJSONArray("child_list");
						for (int i = 0; i < jsonObj1.length(); i++) {
							JSONObject tempJson = jsonObj1.optJSONObject(i);
							String id = tempJson.getString("id");// 标题
							String title = tempJson.getString("title");// 标题
							String pn = tempJson.getString("pn");// package码
							String logo = tempJson.getString("logo");// 图片地址
							String size = tempJson.getString("size");// 文件大小(字节)score
							String score = tempJson.getString("score");// 文件大小(字节)
							String apkurl = tempJson.getString("apkurl");// 下载地址
							String dc = tempJson.getString("dc");// 下载次数

							// private List<AppListBean> mList = new
							// ArrayList<AppListBean>();
							AppListBean tempAppListBean = new AppListBean();
							tempAppListBean.setId(id);
							tempAppListBean.setTitle(title);
							tempAppListBean.setPn(pn);
							tempAppListBean.setLogourl(logo);
							tempAppListBean.setSize(Common.getLength(size));
							tempAppListBean.setScore(score);
							tempAppListBean.setAppurl(apkurl);
							tempAppListBean.setDowntiems(dc);
							mList.add(tempAppListBean);
							m_list_url.add(logo);
						}
					} catch (JSONException e) {

					}
					if (mEnablePullLoad) {
						removeLoadMore();
					}
					adaptersoft.notifyDataSetChanged();
					if (m_list_url.size() > 0) {
						int num = m_list_url.size();
//						for (int i = 0; i < num; i++) {
//							Bitmap tempbitmap = Getphontnames(m_list_url.get(0));
//							if (tempbitmap != null) {
//								m_list_url.remove(0);
//
//								adaptersoft.image.add(tempbitmap);
//								adaptersoft.notifyDataSetChanged();
//							} else {
//								upDatasoft(1, false, m_list_url.get(0));
//								pd.dismiss();
//								return;
//							}
//						}

					}
					pd.dismiss();

				} else if (m_num == 1) {
					int num = m_list_url.size();
//					for (int i = 0; i < m_list_url.size(); i++) {
//						Bitmap tempbitmap = Getphontnames(m_list_url.get(0));
//						if (tempbitmap != null) {
//							m_list_url.remove(0);
//							adaptersoft.image.add(tempbitmap);
//							adaptersoft.notifyDataSetChanged();
//
//						} else {
//							upDatasoft(1, false, m_list_url.get(0));
//							return;
//						}
//					}

					// pd.dismiss();
				}
			}
		}
	}

	public Bitmap Getphontnames(String url) {
		String filename = Common.getmymd5(url) + ".jpg";

		String path = Environment.getExternalStorageDirectory().toString()
				+ "/baifen/img/" + filename;
		File file1 = new File(path);
		if (file1.exists()) {
			try {
				FileInputStream fis = new FileInputStream(path);
				return BitmapFactory.decodeStream(fis);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	public void SetUrl(String aUrl) {
		m_urls = aUrl;
	}

	public void SetList() {
		if (adaptersoft.getCount() == 0) {
			upDatasoft(0, true, m_urls);
		}
	}
}
