package com.work.market.view;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byfen.market.R;
import com.byfen.market.SoftActivity;
import com.work.market.net.Common;
import com.work.market.net.HttpUtil;

public class classlistView extends ObjectView
{

	private Context mContext;
	private Activity m_MainActivity;

	private ProgressDialog pd;
	private NetTask mNetTask;

	public MyAdapter adaptersoft;
	private ListView lv1;
	private String m_url;

	private EditText m_send_message;
	private String m_type = "";

	public classlistView(Context context, Activity aActivity)
	{
		this(context, null, aActivity);
		mContext = context;
	}

	public classlistView(Context context, AttributeSet attrs, Activity aActivity)
	{
		super(context, attrs);
		mContext = context;
		m_MainActivity = aActivity;
		LayoutInflater.from(context).inflate(R.layout.classlist, this, true);
		pd = new ProgressDialog(m_MainActivity);
		pd.setMessage(m_MainActivity.getText(R.string.pd_loading));
		pd.setCancelable(true);
		lv1 = (ListView) findViewById(R.id.class_soft_ls);
		adaptersoft = new MyAdapter(mContext);
		lv1.setAdapter(adaptersoft);

		lv1.setOnItemClickListener(new OnItemClickListener()
		{

			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id)
			{
				Log.v("wmh", "classlistView.onItemClick");
				Intent seta = new Intent(mContext,
						SoftActivity.class);
				Bundle bundle = new Bundle();
				String dialogstring = adaptersoft.title.get(pos);
				bundle.putInt("pagetype", SoftActivity.PAGETYPE_SECOND_GROUP);
				bundle.putString("title", dialogstring);
				bundle.putString("type", adaptersoft.id.get(pos));//id
				bundle.putString("kind", m_type);
				bundle.putInt("is_modify", -1);//�Ƿ�Ϊ�޸�״̬ 0:δ�޸�,1:�޸��ƽ��,����Ϊ���� 
				bundle.putString("lang", "all");//����,'cn':����,'en':Ӣ��,����Ϊ����
				bundle.putInt("min_file_size", -1);//�ļ���С,���� min_file_size=10,��ʾ�ļ���С������10mb,-1:������
				bundle.putString(
						"url",
						"http://api.byfen.com/list/type?type="
								+ adaptersoft.id.get(pos) + "&");
				seta.putExtras(bundle);
				m_MainActivity.startActivity(seta);
			}
		});

	}

	private class MyAdapter extends BaseAdapter
	{

		private LayoutInflater inflater;

		public ArrayList<String> title;
		public ArrayList<String> id;

		public Context mcontext;

		public MyAdapter(Context context)
		{

			super();

			mcontext = context;
			inflater = LayoutInflater.from(mcontext);
			title = new ArrayList<String>();
			id = new ArrayList<String>();

		}

		public int getCount()
		{

			// TODO Auto-generated method stub

			return title.size();

		}

		public Object getItem(int arg0)
		{

			// TODO Auto-generated method stub

			return arg0;

		}

		public long getItemId(int arg0)
		{

			// TODO Auto-generated method stub

			return arg0;

		}

		public View getView(final int position, View view, ViewGroup arg2)
		{

			// TODO Auto-generated method stub
			if (view == null)
			{
				view = inflater.inflate(R.layout.classitem, null);
			}

			if (title.size() > 0)
			{
				final TextView myButton = (TextView) view
						.findViewById(R.id.class_item_image1);// <Button
				int nums = position + 1;
				myButton.setText(nums + "");

				final TextView mytext = (TextView) view
						.findViewById(R.id.class_item_text);// @+id/zhaongjiang_item1_text1
				mytext.setText(title.get(position));
			}

			return view;
		}
	}

	private void upDatasoft(int num, boolean apd, String url)
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
				String json = HttpUtil.doGet(m_url);// =
													// NetUtil.photoShow(mContext,
													// SharedUtil.getUserKey(mContext));
				return json;
				// String filename =
				// Common.getmymd5(m_home_image_url.get(m_home_image_index))+".jpg";
				// String data =
				// HttpUtil.GetPhoto5(m_home_image_url.get(m_home_image_index),filename);//
				// return data;
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
				String dialogstring = m_MainActivity
						.getString(R.string.net_faile);
				Toast.makeText(mContext, dialogstring, Toast.LENGTH_LONG)
						.show();

			}
			else
			{
				if (m_num == 0)
				{
					try
					{
						JSONArray jsonObj1 = new JSONObject("{\"list\":"
								+ result + "}").getJSONArray("list");
						for (int i = 0; i < jsonObj1.length(); i++)
						{
							JSONObject tempJson = jsonObj1.optJSONObject(i);
							// String id = tempJson.getString("id");//����
							String id = tempJson.getString("id");// ����
							String value = tempJson.getString("value");// package��
							adaptersoft.title.add(value);
							adaptersoft.id.add(id);

						}
					}
					catch (JSONException e)
					{

					}
					lv1.setAdapter(adaptersoft);
					adaptersoft.notifyDataSetChanged();
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

	public void SetUrl(String aUrl, String atype)
	{
		m_url = aUrl;
		m_type = atype;
	}
	public void Clearedittextfocus()
	{
		m_send_message.clearFocus();
	}

	@Override
	public void onResume()
	{

		if (adaptersoft.getCount() == 0)
		{
			upDatasoft(0, true, m_url);
		}// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		
	}
}
