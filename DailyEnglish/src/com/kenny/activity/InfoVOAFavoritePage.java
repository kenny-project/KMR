package com.kenny.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.kenny.Application.KApp;
import com.kenny.adapter.VOAAdapter;
import com.kenny.dailyenglish.R;
import com.kenny.data.VOANetData;
import com.kenny.sqlite.DBManage;
import com.kenny.util.Const;
import com.umeng.analytics.MobclickAgent;

public class InfoVOAFavoritePage extends Activity implements OnItemClickListener
{
	private VOAAdapter m_KIFAdapter;
	private ListView mList;
	private ArrayList<VOANetData> list = new ArrayList<VOANetData>();
	private TextView tvEmpty;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.info_favorite_page);
		View Head = findViewById(R.id.Head);
		((TextView)findViewById(R.id.Title)).setText(R.string.info_VOAfavorite_title);
		KApp app = (KApp) getApplicationContext();
		Head.setBackgroundColor(app.colorFactory.getColor());
		mList = (ListView) findViewById(R.id.lvList);
		m_KIFAdapter=new VOAAdapter(this, list);
		m_KIFAdapter.setSubscribe(true);
		mList.setAdapter(m_KIFAdapter);
		mList.setOnItemClickListener(this);
		tvEmpty= (TextView) findViewById(R.id.tvEmpty);
		RefreshPage();
		View temp =  findViewById(R.id.btBack);
		temp.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				InfoVOAFavoritePage.this.finish();
			}
		});
	}

	private void RefreshPage()
	{
		DBManage.getInstance(this).open();
		list.clear();
		list.addAll(DBManage.getInstance(this).getVOAFavoritesInfos());
		DBManage.getInstance(this).close();
		if(list.size()==0)
		{
			tvEmpty.setVisibility(View.VISIBLE);
		}
		else
		{
			tvEmpty.setVisibility(View.GONE);
		}
		m_KIFAdapter.notifyDataSetChanged();
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) 
	{
		// TODO Auto-generated method stub
		VOANetData  temp=m_KIFAdapter.getItem(arg2);
		Intent intent=new Intent(this,InfoVOAViewPage.class);
		intent.putExtra("url", temp.getArticleurl());
		intent.putExtra("views", temp.getViews());
		intent.putExtra("nFlag", Const.Net_btVOA_Data);
		this.startActivity(intent);
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}


}
