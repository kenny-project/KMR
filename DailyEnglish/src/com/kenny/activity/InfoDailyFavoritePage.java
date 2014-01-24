package com.kenny.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kenny.Application.KApp;
import com.kenny.adapter.KStoreItemAdapter;
import com.kenny.dailyenglish.R;
import com.kenny.data.DailyNetData;
import com.kenny.sqlite.DBManage;
import com.umeng.analytics.MobclickAgent;

public class InfoDailyFavoritePage extends Activity  
{
	private ArrayAdapter m_KIFAdapter;
	private ListView mList;
	private TextView tvEmpty;
	private ArrayList<DailyNetData> list = new ArrayList<DailyNetData>();// 推荐栏目
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.info_favorite_page);
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		View Head = findViewById(R.id.Head);
		((TextView)findViewById(R.id.Title)).setText(R.string.info_Dailyfavorite_title);
		KApp app = (KApp) getApplicationContext();
		Head.setBackgroundColor(app.colorFactory.getColor());
		mList = (ListView) findViewById(R.id.lvList);
		m_KIFAdapter = new KStoreItemAdapter(this, list);
		mList.setAdapter(m_KIFAdapter);
		tvEmpty= (TextView) findViewById(R.id.tvEmpty);
		RefreshPage();
		View temp = findViewById(R.id.btBack);
		temp.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				InfoDailyFavoritePage.this.finish();
			}
		});
	}

	private void RefreshPage()
	{
		DBManage.getInstance(this).open();
		list.clear();
		list.addAll(DBManage.getInstance(this).getDailyFavoritesInfos());
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
	private AudioManager audio = null;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND
							| AudioManager.FLAG_SHOW_UI);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
					AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND
							| AudioManager.FLAG_SHOW_UI);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
