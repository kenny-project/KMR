package com.work.market.view;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byfen.market.R;
import com.work.market.net.HttpUtil;
import com.work.market.util.T;

public class feedbacklistView extends ObjectView {

	private Context mContext;
	private Activity m_MainActivity;

	private ProgressDialog pd;
	private NetTask mNetTask;

	public FeedBackAdapter adaptersoft;
	private ListView lv1;
	private String m_url;
	private ArrayList<String> m_list_url;

	private EditText m_send_message;
	private LinearLayout m_send_message_button;
	private RatingBar tbScore;
	private boolean mEnablePullLoad = false;
	private ProgressBar mfootProgressBar;
	private TextView mFootTextView;
	private String m_id = "";

	private View moreView;
	private int m_Max_page = 1;
	private int m_now_page = 1;
	private int m_everytime_num = 30;
	private int m_list_page = 1;
	private ImageButton feedback_star1;
	private ImageButton feedback_star2;
	private ImageButton feedback_star3;
	private ImageButton feedback_star4;
	private ImageButton feedback_star5;
	private int m_product_scro = 1;

	public feedbacklistView(Context context, Activity aActivity) {
		this(context, null, aActivity);
		mContext = context;
	}

	public feedbacklistView(Context context, AttributeSet attrs,
			Activity aActivity) {
		super(context, attrs);
		mContext = context;
		m_MainActivity = aActivity;
		LayoutInflater.from(context).inflate(R.layout.feedback, this, true);
		pd = new ProgressDialog(m_MainActivity);
		pd.setMessage(m_MainActivity.getText(R.string.pd_loading));
		pd.setCancelable(true);
		moreView = m_MainActivity.getLayoutInflater().inflate(
				R.layout.xlistview_feedback_footer, null);// ���list ���
		mfootProgressBar = (ProgressBar) moreView
				.findViewById(R.id.xlistview_footer_progressbar);
		mFootTextView = (TextView) moreView
				.findViewById(R.id.xlistview_footer_hint_textview);
		mfootProgressBar.setVisibility(View.INVISIBLE);
		lv1 = (ListView) findViewById(R.id.feedback_ls);
		lv1.addFooterView(moreView, null, false);
		adaptersoft = new FeedBackAdapter(mContext);
		lv1.setAdapter(adaptersoft);
		lv1.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
						|| scrollState == OnScrollListener.SCROLL_STATE_FLING) {
					// �ж��Ƿ�������ײ�
					if (lv1.getLastVisiblePosition() == lv1.getCount() - 1
							&& !mEnablePullLoad) {
						if (m_now_page < m_Max_page) {
							m_list_page = m_now_page + 1;
							// m_everytime_num = 2;
							addLoadMore();
							upDatasoft(0, false, "", m_url);
						}
					}
				}

			}
		});
		m_list_url = new ArrayList<String>();

		tbScore = (RatingBar) findViewById(R.id.tbScore1);
		m_send_message = (EditText) findViewById(R.id.feedback_message_edit);
		m_send_message_button = (LinearLayout) findViewById(R.id.feedback_send_button);
		m_send_message_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				T.hideInputPad(m_send_message);
				upDatasoft(1, false, m_send_message.getText().toString(), m_url);
				m_send_message.setText("");
			}
		});

		feedback_star1 = (ImageButton) findViewById(R.id.feedback_list_log11);
		feedback_star1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feedback_star1.setBackgroundResource(R.drawable.star1);
				feedback_star2.setBackgroundResource(R.drawable.star0);
				feedback_star3.setBackgroundResource(R.drawable.star0);
				feedback_star4.setBackgroundResource(R.drawable.star0);
				feedback_star5.setBackgroundResource(R.drawable.star0);
				m_product_scro = 1;
			}
		});
		feedback_star2 = (ImageButton) findViewById(R.id.feedback_list_log12);
		feedback_star2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feedback_star1.setBackgroundResource(R.drawable.star1);
				feedback_star2.setBackgroundResource(R.drawable.star1);
				feedback_star3.setBackgroundResource(R.drawable.star0);
				feedback_star4.setBackgroundResource(R.drawable.star0);
				feedback_star5.setBackgroundResource(R.drawable.star0);
				m_product_scro = 2;
			}
		});
		feedback_star3 = (ImageButton) findViewById(R.id.feedback_list_log13);
		feedback_star3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feedback_star1.setBackgroundResource(R.drawable.star1);
				feedback_star2.setBackgroundResource(R.drawable.star1);
				feedback_star3.setBackgroundResource(R.drawable.star1);
				feedback_star4.setBackgroundResource(R.drawable.star0);
				feedback_star5.setBackgroundResource(R.drawable.star0);
				m_product_scro = 3;
			}
		});
		feedback_star4 = (ImageButton) findViewById(R.id.feedback_list_log14);
		feedback_star4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feedback_star1.setBackgroundResource(R.drawable.star1);
				feedback_star2.setBackgroundResource(R.drawable.star1);
				feedback_star3.setBackgroundResource(R.drawable.star1);
				feedback_star4.setBackgroundResource(R.drawable.star1);
				feedback_star5.setBackgroundResource(R.drawable.star0);
				m_product_scro = 4;
			}
		});
		feedback_star5 = (ImageButton) findViewById(R.id.feedback_list_log15);
		feedback_star5.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				feedback_star1.setBackgroundResource(R.drawable.star1);
				feedback_star2.setBackgroundResource(R.drawable.star1);
				feedback_star3.setBackgroundResource(R.drawable.star1);
				feedback_star4.setBackgroundResource(R.drawable.star1);
				feedback_star5.setBackgroundResource(R.drawable.star1);
				m_product_scro = 4;
			}
		});
		feedback_star1.setBackgroundResource(R.drawable.star1);

	}

	private class FeedBackAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		public ArrayList<String> title;
		public ArrayList<String> score;

		public Context mcontext;

		public FeedBackAdapter(Context context) {

			super();

			mcontext = context;
			inflater = LayoutInflater.from(mcontext);
			title = new ArrayList<String>();
			score = new ArrayList<String>();
		}

		public int getCount() {

			// TODO Auto-generated method stub

			return title.size();

		}

		public Object getItem(int arg0) {

			// TODO Auto-generated method stub

			return arg0;

		}

		public long getItemId(int arg0) {

			// TODO Auto-generated method stub

			return arg0;

		}

		public View getView(final int position, View view, ViewGroup arg2) {

			// TODO Auto-generated method stub
			if (view == null) {
				view = inflater.inflate(R.layout.feedbackitem, null);
			}
			if (title.size() > 0) {
				final TextView mytitle = (TextView) view
						.findViewById(R.id.feedback_item_title2);
				String temp = title.get(position);// @+id/feedback_item_title2
				mytitle.setText(temp);
				RatingBar tbScore = (RatingBar) view
						.findViewById(R.id.feedback_item_RatingBar);// "@+id/feedback_item_RatingBar"

				float scro = Float.valueOf(score.get(position));
				tbScore.setRating(scro);
			}

			return view;
		}
	}

	private void upDatasoft(int num, boolean apd, String Data, String url) {

		if (apd) {
			pd.show();
		}
		if (mNetTask != null) {
			mNetTask.cancel(true);
		}
		mNetTask = new NetTask(num, apd, Data, url);
		mNetTask.execute("");// m_ad_layout.setVisibility
	}

	// ͼƬ����
	class NetTask extends AsyncTask<Object, Integer, String> {

		private int m_num = 0;
		private boolean m_showlog = false;
		private String m_url;
		private String data = "";

		private NetTask(int aNUm, boolean showlog, String data, String aurl) {
			m_num = aNUm;
			m_showlog = showlog;
			m_url = aurl;
			this.data = data;
		}

		protected String doInBackground(Object... params) {
			if (m_num == 0) {
				String url = m_url + "page=" + m_list_page + "&per_page="
						+ m_everytime_num;
				String json = HttpUtil.doGet(url);
				return json;
			} else if (m_num == 1) {
				String url = "http://api.byfen.com/comment/add?id=" + m_id
						+ "&serial_num=" + T.GetIMEI(mContext);

				String scro = String.valueOf((int) tbScore.getRating());
				String json = HttpUtil.RequestGetDatas(url, m_id, data, scro);// =
				Log.v("wmh", "result:" + json);
				// SharedUtil.getUserKey(mContext));
				return json;
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			if (!pd.isShowing() && m_showlog)
				return;

			if (result == null) {// ʧ�� ����
				pd.dismiss();
				String dialogstring = m_MainActivity
						.getString(R.string.net_faile);
				Toast.makeText(mContext, dialogstring, Toast.LENGTH_LONG)
						.show();

			} else {
				if (m_num == 0) {
					try {

						JSONObject jsresult = new JSONObject(result);//
						String maxpage = jsresult.getString("total_count");//
						String nowpage = jsresult.getString("cur_page");//
						m_Max_page = Integer.parseInt(maxpage);
						m_now_page = Integer.parseInt(nowpage);
						// m_Max_page = Integer.parseInt(maxpage);
						JSONArray jsonObj1 = jsresult.getJSONArray("list");
						adaptersoft.title.clear();
						 adaptersoft.score.clear();
						for (int i = 0; i < jsonObj1.length(); i++) 
						{
							JSONObject tempJson = jsonObj1.optJSONObject(i);
							// String id = tempJson.getString("id");//����
							String title = tempJson.getString("desc");// ����
							String desc = tempJson.getString("score");// package��
							adaptersoft.title.add(title);
							adaptersoft.score.add(desc);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (mEnablePullLoad) {
						removeLoadMore();
					}
					// lv1.setAdapter(adaptersoft); by wmh
					adaptersoft.notifyDataSetChanged();
					pd.dismiss();
				} else if (m_num == 1) {
					if (result.indexOf("1") >= 0) {
						String dialogstring = "���۷��ͳɹ�";
						Toast.makeText(mContext, dialogstring,
								Toast.LENGTH_LONG).show();
						String title = m_send_message.getText().toString();// ����
						String desc = T.GetIMEI(mContext);// package��
						adaptersoft.title.add(title);
						adaptersoft.score.add(desc);
						adaptersoft.notifyDataSetChanged();

					} else {
						String dialogstring = "���۷���ʧ��";
						Toast.makeText(mContext, dialogstring,
								Toast.LENGTH_LONG).show();
					}

				}

			}
		}
	}

	// public Bitmap Getphontnames(String url)
	// {
	// String filename = Common.getmymd5(url)+".jpg";
	//
	// String path = Environment.getExternalStorageDirectory().toString()
	// + "/market/" + filename;
	// File file1 = new File(path);
	// if (file1.exists()) {
	// try{
	// FileInputStream fis = new FileInputStream(path);
	// return BitmapFactory.decodeStream(fis);
	// }catch(FileNotFoundException e)
	// {
	// e.printStackTrace();
	// return null;
	// }
	// }else
	// {
	// return null;
	// }
	// }

	public void SetUrl(String aUrl, String id) {
		m_url = aUrl;
		m_id = id;
	}

	public void Clearedittextfocus() {
		m_send_message.clearFocus();

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

	public void SetHidesoftkey() {
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
		// if (imm.isActive()) //һֱ��true
		// imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
		// InputMethodManager.HIDE_NOT_ALWAYS);
		if (m_send_message.isFocusable()) {
			imm.hideSoftInputFromWindow(m_send_message.getWindowToken(), 0);
		}

	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		if (adaptersoft.getCount() == 0) {
			upDatasoft(0, true, "", m_url);
		}
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		
	}
}
