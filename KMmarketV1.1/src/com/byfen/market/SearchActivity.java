package com.byfen.market;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byfen.app.KApp;
import com.framework.syseng.AbsEvent;
import com.framework.syseng.SysEng;
import com.work.Interface.INotifyDataSetChanged;
import com.work.market.adapter.SoftListAdapter;
import com.work.market.bean.AppListBean;
import com.work.market.net.Common;
import com.work.market.net.Downloader;
import com.work.market.net.HttpUtil;
import com.work.market.server.DownLoadService;
import com.work.market.util.T;

public class SearchActivity extends Activity implements INotifyDataSetChanged
{

	public SearchKeyAdapter mKeyAdapter;
	public SoftListAdapter adaptersoft;
	private ListView mkeyListView;
	private ListView mSearchResultList;
	private Context m_Context;
	private int m_Max_page = 0;
	private List<AppListBean> mList = new ArrayList<AppListBean>();

	private boolean mEnablePullLoad = false;
	private ProgressBar mfootProgressBar;
	private TextView mFootTextView;
	private View moreView;
	private int m_now_page = 1;
	private int m_list_page = 1;
	private ProgressDialog pd;
	private NetTask mNetTask;
	private NetTask1 mNetTask1;
	private EditText m_EditText;
	private ImageButton m_SearchButton;
	private Activity m_MainActivity;
	private View lySoftListPanel;
	private View lyKeyPanel;
	private KApp app;
	//..................启动服务..................
	
		//..................结束服务
	/**
	 * 进入页面
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		// 加载页面
		setContentView(R.layout.searchpage);
		// 异常处理
		m_Context = this;
		 app=((KApp)getApplicationContext());
	
		moreView = getLayoutInflater().inflate(R.layout.xlistview_footer, null);// 添加list
																				// 进度
		mfootProgressBar = (ProgressBar) moreView
				.findViewById(R.id.xlistview_footer_progressbar);
		mFootTextView = (TextView) moreView
				.findViewById(R.id.xlistview_footer_hint_textview);
		mfootProgressBar.setVisibility(View.INVISIBLE);

		pd = new ProgressDialog(this);
		pd.setMessage(this.getText(R.string.pd_loading));
		pd.setCancelable(true);
		m_MainActivity = this;

		m_EditText = (EditText) findViewById(R.id.etSearch);
		m_SearchButton = (ImageButton) findViewById(R.id.btSearch);
		m_SearchButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (m_EditText.getText().length() > 0)
				{
					T.hideInputPad(m_EditText);
					m_EditText.setText(m_EditText.getText().toString());
					upDatasoft(true, m_EditText.getText().toString());
				}
				else
				{
					Toast.makeText(m_Context, "输入不能为空", Toast.LENGTH_LONG)
							.show();
				}
			}
		});

		lySoftListPanel = findViewById(R.id.lySoftListPanel);
		lyKeyPanel = findViewById(R.id.lyKeyPanel);

		mkeyListView = (ListView) findViewById(R.id.search_ls);
		mKeyAdapter = new SearchKeyAdapter(this);
		mkeyListView.setAdapter(mKeyAdapter);

		mSearchResultList = (ListView) findViewById(R.id.searchResult_ls);
		mSearchResultList.addFooterView(moreView);
		adaptersoft = new SoftListAdapter(this, mList);
		mSearchResultList.setAdapter(adaptersoft);
		upData();
		mSearchResultList.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id)
			{
				if(mList.size()>pos)
				{
				Intent seta = new Intent(m_Context, productActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("title", mList.get(pos).getTitle());
				bundle.putInt("id", mList.get(pos).getId());
				seta.putExtras(bundle);
				m_MainActivity.startActivity(seta);// SoftclassActivity
				}
			}
		});

		mSearchResultList.setOnScrollListener(new OnScrollListener()
		{

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3)
			{
			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState)
			{
				// TODO Auto-generated method stub
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						|| scrollState == OnScrollListener.SCROLL_STATE_FLING)
				{
					// 判断是否滚动到底部
					if (mSearchResultList.getLastVisiblePosition() == mSearchResultList
							.getCount() - 1 && !mEnablePullLoad)
					{
						if (m_now_page < m_Max_page)
						{
							m_list_page = m_now_page + 1;
							// m_everytime_num = 2;
							addLoadMore();
							upDatasoft(false, m_EditText.getText().toString());
						}
					}
				}

			}
		});
	}

	private class SearchKeyAdapter extends BaseAdapter
	{

		private LayoutInflater inflater;
		public ArrayList<String> title;
		public ArrayList<String> count;

		public Context mcontext;

		public SearchKeyAdapter(Context context)
		{
			super();
			mcontext = context;
			inflater = LayoutInflater.from(mcontext);
			title = new ArrayList<String>();
			count = new ArrayList<String>();
		}

		public int getCount()
		{
			return title.size();
		}

		public Object getItem(int arg0)
		{
			return arg0;
		}

		public long getItemId(int arg0)
		{
			return arg0;
		}

		public View getView(final int position, View view, ViewGroup arg2)
		{
			if (view == null)
			{
				view = inflater.inflate(R.layout.searchitem, null);
			}
			final Button myButton = (Button) view
					.findViewById(R.id.search_item_image1);// <Button
			int nums = position + 1;
			myButton.setText(nums + "");

			final TextView mytext = (TextView) view
					.findViewById(R.id.search_item_text);// @+id/zhaongjiang_item1_text1
			mytext.setText(title.get(position));

			final TextView mytext1 = (TextView) view
					.findViewById(R.id.search_item_text_num);
			mytext1.setText(count.get(position));

			final RelativeLayout myRelativeLayout = (RelativeLayout) view
					.findViewById(R.id.search_item_lay);
			myRelativeLayout.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					m_EditText.setText(title.get(position));
					upDatasoft(true, title.get(position));

				}
			});
			return view;
		}
	}
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		adaptersoft.notifyDataSetChanged();
		app.setINotifyChanged(this);
	}

	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		SetHidesoftkey();
		app.setINotifyChanged(null);
	}
	private void upData()
	{

		pd.show();
		if (mNetTask != null)
		{
			mNetTask.cancel(true);
		}
		mNetTask = new NetTask();
		mNetTask.execute(null);// m_ad_layout.setVisibility
	}

	// 图片处理
	class NetTask extends AsyncTask<Object, Integer, String>
	{
		protected String doInBackground(Object... params)
		{
			String url = "http://api.byfen.com/search/keywords";
			String json = HttpUtil.doGet(url);// =
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
			if (!pd.isShowing()) return;

			if (result == null)
			{// 失败 处理
				pd.dismiss();
				String dialogstring = getString(R.string.net_faile);
				Toast.makeText(m_Context, dialogstring, Toast.LENGTH_LONG)
						.show();

			}
			else
			{
				try
				{
					JSONObject jsresult = new JSONObject(result);//
					JSONArray jsonObj1 = jsresult.getJSONArray("list");
					for (int i = 0; i < jsonObj1.length(); i++)
					{
						JSONObject tempJson = jsonObj1.optJSONObject(i);
						String keyword = tempJson.getString("keyword");//
						String count = tempJson.getString("count");//

						mKeyAdapter.title.add(keyword);
						mKeyAdapter.count.add(count);
					}
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				lySoftListPanel.setVisibility(View.GONE);
				lyKeyPanel.setVisibility(View.VISIBLE);
				mKeyAdapter.notifyDataSetChanged();
				pd.dismiss();
			}
		}
	}

	private void upDatasoft(boolean apd, String url)
	{

		if (apd) pd.show();
		if (mNetTask1 != null)
		{
			mNetTask1.cancel(true);
		}
		mNetTask1 = new NetTask1(apd, url);
		mNetTask1.execute(null);
		// m_ad_layout.setVisibility
		// moreView.setVisibility(View.VISIBLE);
	}

	// 图片处理
	class NetTask1 extends AsyncTask<Object, Integer, String>
	{
		private boolean m_showlog = false;
		private String m_url;

		private NetTask1(boolean showlog, String aurl)
		{
			m_showlog = showlog;
			m_url = aurl;
		}

		protected String doInBackground(Object... params)
		{

			String temp = Common.getUTF8XMLString(m_url);
			String url = "http://api.byfen.com/search/get?keywords=" + temp
					+ "&per_page=30&page=" + m_list_page;
			String json = HttpUtil.doGet(url);// =
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
			{// 失败 处理
				pd.dismiss();
				if (mEnablePullLoad)
				{
					removeLoadMore();
					m_list_page--;
				}
				String dialogstring = getString(R.string.net_faile);
				Toast.makeText(m_Context, dialogstring, Toast.LENGTH_LONG)
						.show();
			}
			else
			{
				if (m_showlog)
				{
					mList.clear();
				}
				try
				{
					JSONObject jsresult = new JSONObject(result);//
					String maxpage = jsresult.getString("last_page");//
					String nowpage = jsresult.getString("cur_page");//
					m_Max_page = Integer.parseInt(maxpage);
					m_now_page = Integer.parseInt(nowpage);
					JSONArray jsonObj1 = jsresult.getJSONArray("list");
					for (int i = 0; i < jsonObj1.length(); i++)
					{
						JSONObject tempJson = jsonObj1.optJSONObject(i);
						String id = tempJson.getString("id");// 标题
						String title = tempJson.getString("title");// 标题
						String pn = tempJson.getString("pn");// package码
						String logo = tempJson.getString("logo");// 图片地址
						String size = tempJson.getString("size");// 文件大小(字节)score
						String score = tempJson.getString("score");// 文件大小(字节)
						String apkurl = tempJson.getString("apkurl");// 下载地址
						String dc = tempJson.getString("dc");// 下载次数
						
						AppListBean tempAppListBean = new AppListBean();
						tempAppListBean.setId(id);
						tempAppListBean.setTitle(title);
						tempAppListBean.setPn(pn);
						tempAppListBean.setLogourl(logo);
						tempAppListBean.setSize(Common.getLength(size));
						tempAppListBean.setScore(score);
						tempAppListBean.setAppurl(apkurl);
						tempAppListBean.setDowntiems(dc);
						tempAppListBean.setAppFileExt(tempJson.getString("ext"));
						mList.add(tempAppListBean);
					}
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				if(mList.size()>0)
				{
					adaptersoft.notifyDataSetChanged();
					lySoftListPanel.setVisibility(View.VISIBLE);
					lyKeyPanel.setVisibility(View.GONE);
					if (mEnablePullLoad)
					{
						removeLoadMore();
					}
				}
				else
				{
					Toast.makeText(lyKeyPanel.getContext(), "未查到相应的结果", Toast.LENGTH_LONG).show();
				}
				pd.dismiss();
			}
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		// TODO Auto-generated method stub
		if (event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if (keyCode == KeyEvent.KEYCODE_BACK)
			{
				if (lySoftListPanel.getVisibility() == View.GONE)
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
													SearchActivity.this,
													DownLoadService.class);
											serviceIntent.putExtra("type",
													"finish");
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
				else
				{
					m_list_page = 1;
					lySoftListPanel.setVisibility(View.GONE);
					lyKeyPanel.setVisibility(View.VISIBLE);
				}

			}
		}
		return true;
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

	public void SetHidesoftkey()
	{
		InputMethodManager imm = (InputMethodManager) m_Context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
		// if (imm.isActive()) //一直是true
		// imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
		// InputMethodManager.HIDE_NOT_ALWAYS);
		if (m_EditText.isFocusable())
		{
			imm.hideSoftInputFromWindow(m_EditText.getWindowToken(), 0);
		}
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

	@Override
	public void NotifyDataSetChanged(int cmd, Object value)
	{
		// TODO Auto-generated method stub
		
	}
}
