package com.kenny.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.kenny.Application.KApp;
import com.kenny.dailyenglish.R;
import com.kenny.data.BilingualBean;
import com.kenny.data.VOANetData;
import com.kenny.util.BilingualService;
import com.kenny.util.Const;
import com.kenny.util.DailyService;
import com.kenny.util.Utils;
import com.kenny.util.VOAService;
import com.umeng.analytics.MobclickAgent;

public class InfoHomePage extends Activity {
	private int nFlag = Const.Net_Dailysentence_Data;
	private DailyService m_HomeService;
	private BilingualService m_BilingualService;// 双语资讯
	private VOAService m_VoaService;// 双语资讯
	private String gname = "";
	private View lyBilingualViewPanel, lyDailyViewPanel, lyVOAViewPanel,
			lySettingPanel;
	private View Head;// 头部
	private Button btDailysentence, btBilingualinfo, btVOA, btSetting,
			btSubscribe;
	private Button btFavorite;
	private TextView tvHeadTitle;
	private boolean bSubscribe = false;
	private AudioManager audio = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_home_page);
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		onLoad();
		
		KApp app = (KApp) InfoHomePage.this.getApplicationContext();
		int color = app.colorFactory.getColor();
		Head.setBackgroundColor(color);
	}

	private void DailyItem() {
		lyDailyViewPanel = findViewById(R.id.lyListItem);
		View InitPannal = findViewById(R.id.icListItem);
		InitPannal.setVisibility(View.VISIBLE);
		View InitLoading = InitPannal.findViewById(R.id.lyLoading);
		InitLoading.setVisibility(View.VISIBLE);
		View InitError = (View) InitPannal
				.findViewById(R.id.tvError_msg);
		InitError.setVisibility(View.GONE);

		ListView lvListItem = (ListView) findViewById(R.id.lvListItem);
		View DailyfoolerView = InfoHomePage.this.getLayoutInflater().inflate(
				R.layout.listitem_fooler, null);
		LinearLayout DailyLoading = (LinearLayout) DailyfoolerView
				.findViewById(R.id.lyListItemLoading);
		final Button btDailyDownLoadItem = (Button) DailyfoolerView
				.findViewById(R.id.btDownLoad);

		m_HomeService = new DailyService(this, 4, lvListItem, btDailyDownLoadItem,
				DailyLoading, InitPannal, InitLoading, InitError);
		btDailyDownLoadItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				m_HomeService.NextPage();
				MobclickAgent.onEvent(getBaseContext(), "More-sentenceeveryday");
			}
		});
		lvListItem.addFooterView(DailyfoolerView, null, false);
		lvListItem.setAdapter(m_HomeService.getAdapter());
	}

	public Activity getActivity() {
		return this;
	}

	private void BilingualInit() {

		lyBilingualViewPanel = findViewById(R.id.lyBilingualViewPanel);
		View InitPannal = findViewById(R.id.icListGroup);
		InitPannal.setVisibility(View.VISIBLE);
		View InitLoading = InitPannal.findViewById(R.id.lyLoading);
		InitLoading.setVisibility(View.VISIBLE);
		View InitError = (View) InitPannal
				.findViewById(R.id.tvError_msg);
		InitError.setVisibility(View.GONE);

		ListView lvBilingual = (ListView) findViewById(R.id.lvBilingual);
		View foolerView1 = getLayoutInflater().inflate(
				R.layout.listitem_fooler, null);
		LinearLayout Loading1 = (LinearLayout) foolerView1
				.findViewById(R.id.lyListItemLoading);
		final Button btDownLoadItem1 = (Button) foolerView1
				.findViewById(R.id.btDownLoad);
		btDownLoadItem1.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				m_BilingualService.NextPage();
			}
		});
		m_BilingualService = new BilingualService(this, 4, lvBilingual,
				btDownLoadItem1, Loading1, InitPannal, InitLoading, InitError);
		m_BilingualService.StoreInitData();
		lvBilingual.addFooterView(foolerView1, null, false);
		lvBilingual.setAdapter(m_BilingualService.getAdapter());
		lvBilingual.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// if (!bClickFlag) {
				// bClickFlag = true;

				 BilingualBean temp = m_BilingualService.get(position);
				 Intent intent = new Intent(getActivity(),
				 InfoViewPage.class);
				 intent.putExtra("url", temp.getUrl());
				 intent.putExtra("groupName", gname);
				 intent.putExtra("nFlag", nFlag);
				 getActivity().startActivity(intent);
				
			}
		});
	}

	private void VoaInit() {

		lyVOAViewPanel = findViewById(R.id.lyVOAViewPanel);

		View InitPannal = findViewById(R.id.icVOAListGroup);
		InitPannal.setVisibility(View.VISIBLE);
		View InitLoading = InitPannal.findViewById(R.id.lyLoading);
		InitLoading.setVisibility(View.VISIBLE);
		View InitError = (View) InitPannal
				.findViewById(R.id.tvError_msg);
		InitError.setVisibility(View.GONE);

		ListView lvVOA = (ListView) findViewById(R.id.lvVOA);
		View VOAFoolerView = this.getActivity().getLayoutInflater()
				.inflate(R.layout.listitem_fooler, null);
		LinearLayout Loading1 = (LinearLayout) VOAFoolerView
				.findViewById(R.id.lyListItemLoading);
		final Button btDownLoadItem1 = (Button) VOAFoolerView
				.findViewById(R.id.btDownLoad);
		btDownLoadItem1.setOnClickListener(new OnClickListener() {

			public void onClick(View v) 
			{
				m_VoaService.NextPage();
				MobclickAgent.onEvent(getBaseContext(), "More-VOA");
			}
		});

		m_VoaService = new VOAService(this, 4, lvVOA, btDownLoadItem1,
				Loading1, InitPannal, InitLoading, InitError);
		m_VoaService.setSubscribe(bSubscribe);
		m_VoaService.StoreInitData();
		lvVOA.addFooterView(VOAFoolerView, null, false);
		lvVOA.setAdapter(m_VoaService.getAdapter());
		lvVOA.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				boolean result = false;
				VOANetData temp = m_VoaService.get(position);
				if (bSubscribe) 
				{
					result = true;
					MobclickAgent.onEvent(getActivity(), "Click-paidVOA");
				} else 
				{
					if (temp.isIsfree()) 
					{
						result = true;
						MobclickAgent.onEvent(getActivity(), "Click-freeVOA");
					} else {
						result = false;
						MobclickAgent.onEvent(getActivity(), "Click-unfreeVOA");
					}
				}
//				if (result) 
//				{
					Intent intent = new Intent(getActivity(),
							InfoVOAViewPage.class);
					intent.putExtra("url", temp.getArticleurl());
					Log.v("wmh", "temp.getArticleurl()=" + temp.getArticleurl());
					intent.putExtra("groupName", gname);
					intent.putExtra("nFlag", nFlag);
					intent.putExtra("views", temp.getViews());
					getActivity().startActivity(intent);
					MobclickAgent.onEvent(getActivity(), "Click-freeVOA");
//				} else {
//					Intent intent = new Intent(getActivity(),
//							SubscribePage.class);
//					getActivity().startActivity(intent);
//					MobclickAgent.onEvent(getActivity(), "Click-subscribebutton");
//				}
			}
		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		boolean tSubscribe = Utils.get(this, "bSubscribe", false);
		if (bSubscribe != tSubscribe) {
			bSubscribe = tSubscribe;
			m_VoaService.setSubscribe(bSubscribe);
			m_VoaService.notifyDataSetChanged();
			if (bSubscribe && nFlag == Const.Net_btVOA_Data) {
				btSubscribe.setVisibility(View.INVISIBLE);
			} else {
				btSubscribe.setVisibility(View.VISIBLE);
			}
		}
		if (m_HomeService != null)
			m_HomeService.notifyDataSetChanged();
	}

	public void onLoad() {
		// 在这初始化
		bSubscribe = Utils.get(this, "bSubscribe", false);
		int mHour = Utils
				.get(this, Const.CONFIG_SETTING_LEARNINGREMIND_HOUR, 9);
		int mMinute = Utils.get(this,
				Const.CONFIG_SETTING_LEARNINGREMIND_MINUTE, 0);
		if (Utils.get(this, Const.CONFIG_SETTING_LEARNINGREMIND_SWITCH, false)) {
			Utils.setAlerm(this, mHour, mMinute);
		} else {
			Utils.CleanAlerm(this);
		}

		Head = findViewById(R.id.Head);
		tvHeadTitle = (TextView) findViewById(R.id.tvHeadTitle);

		BilingualInit();
		DailyItem();
		VoaInit();
		lySettingPanel = findViewById(R.id.lySettingPanel);

		btSubscribe = (Button) findViewById(R.id.btSubscribe);
		btSubscribe.setVisibility(View.GONE);
//		btSubscribe.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(getActivity(), SubscribePage.class);
//				getActivity().startActivity(intent);
//				MobclickAgent.onEvent(getActivity(), "Click-subscribebutton");
//			}
//		});
		btSubscribe.setVisibility(View.INVISIBLE);
		btFavorite = (Button) findViewById(R.id.btFavorite);
		btFavorite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				
				if (nFlag == Const.Net_Dailysentence_Data) {
					Intent intent = new Intent(getActivity(),
							InfoDailyFavoritePage.class);
					getActivity().startActivity(intent);
					MobclickAgent.onEvent(InfoHomePage.this, "Click-favoritesentence");
				} else {
					MobclickAgent.onEvent(InfoHomePage.this, "Click-favoriteVOA");
					Intent intent = new Intent(getActivity(),
							InfoVOAFavoritePage.class);
					getActivity().startActivity(intent);
				}
				// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// intent.setData(Uri.fromFile(file));
				// SysEng.getInstance().runEvent(
				// new NextPageEvent(main, new InfoFavoritePage(main),
				// Const.SHOW_ANIM, null));
			}
		});
		btDailysentence = (Button) findViewById(R.id.btDailysentence);
		btDailysentence.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.v("wmh", "btDailysentence");
				RefreshPage(Const.Net_Dailysentence_Data);
				
				MobclickAgent.onEvent(InfoHomePage.this, "Click-sentenceeveryday");
			}
		});

		btBilingualinfo = (Button) findViewById(R.id.btBilingualinfo);
		btBilingualinfo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RefreshPage(Const.Net_Bilingual_Data);
			}
		});

		btVOA = (Button) findViewById(R.id.btVOA);
		btVOA.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RefreshPage(Const.Net_btVOA_Data);
			}
		});
		btSetting = (Button) findViewById(R.id.btSetting);
		btSetting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent intent = new Intent(getActivity(),
				// V6Setting.class);
				// getActivity().startActivity(intent);
				MobclickAgent.onEvent(InfoHomePage.this, "Click-setting");
				RefreshPage(Const.Net_btSetting_Data);
			}
		});
		RefreshPage(Const.Net_Dailysentence_Data);
	}

	private void RefreshPage(int type) {

		btDailysentence.setBackgroundResource(R.drawable.info_tab_bg_nor);
		btDailysentence.setTextColor(Color.rgb(0x78, 0x78, 0x78));
		btDailysentence.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.info_daily_nor), null,
				null);
		btBilingualinfo.setBackgroundResource(R.drawable.info_tab_bg_nor);
		btBilingualinfo.setTextColor(Color.rgb(0x78, 0x78, 0x78));
		btBilingualinfo.setCompoundDrawablesWithIntrinsicBounds(null,
				getResources().getDrawable(R.drawable.info_daily_nor), null,
				null);
		
		btVOA.setBackgroundResource(R.drawable.info_tab_bg_nor);
		btVOA.setTextColor(Color.rgb(0x78, 0x78, 0x78));
		btVOA.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
				.getDrawable(R.drawable.info_voa_nor), null, null);
		btSetting.setBackgroundResource(R.drawable.info_tab_bg_nor);
		btSetting.setTextColor(Color.rgb(0x78, 0x78, 0x78));
		btSetting.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
				.getDrawable(R.drawable.info_setting_nor), null, null);
		nFlag = type;
		switch (type) {
		case Const.Net_Dailysentence_Data:
			btFavorite.setVisibility(View.VISIBLE);
			btDailysentence
					.setBackgroundResource(R.drawable.info_tab_bg_select);
			btDailysentence.setTextColor(Color.WHITE);
			btDailysentence.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.info_daily_select),
					null, null);

			lyBilingualViewPanel.setVisibility(View.GONE);
			lyVOAViewPanel.setVisibility(View.GONE);
			lySettingPanel.setVisibility(View.GONE);
			lyDailyViewPanel.setVisibility(View.VISIBLE);
			btSubscribe.setVisibility(View.INVISIBLE);
			tvHeadTitle.setText(R.string.Dailysentence_title);
			m_HomeService.StoreInitData();
			gname = btDailysentence.getText().toString();
			break;
		case Const.Net_Bilingual_Data:
			btFavorite.setVisibility(View.GONE);
			btBilingualinfo
					.setBackgroundResource(R.drawable.info_tab_bg_select);
			btBilingualinfo.setTextColor(Color.WHITE);
			btBilingualinfo.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.info_daily_select),
					null, null);

			lyBilingualViewPanel.setVisibility(View.VISIBLE);
			lyVOAViewPanel.setVisibility(View.GONE);
			lySettingPanel.setVisibility(View.GONE);
			lyDailyViewPanel.setVisibility(View.GONE);
			btSubscribe.setVisibility(View.INVISIBLE);
			tvHeadTitle.setText(R.string.info_home_bilingualinfo_title);
			m_HomeService.StoreInitData();
			gname = btBilingualinfo.getText().toString();
			break;
		case Const.Net_btVOA_Data:
			btFavorite.setVisibility(View.VISIBLE);
			lyVOAViewPanel.setVisibility(View.VISIBLE);
			btVOA.setBackgroundResource(R.drawable.info_tab_bg_select);
			btVOA.setTextColor(Color.WHITE);
			btVOA.setCompoundDrawablesWithIntrinsicBounds(null, getResources()
					.getDrawable(R.drawable.info_voa_select), null, null);
			lyDailyViewPanel.setVisibility(View.GONE);
			lySettingPanel.setVisibility(View.GONE);
			lyBilingualViewPanel.setVisibility(View.GONE);
			gname = btVOA.getText().toString();
			m_VoaService.StoreInitData();
			tvHeadTitle.setText(R.string.VOA_title);
			if (bSubscribe) {
				btSubscribe.setVisibility(View.INVISIBLE);
			} else {
				btSubscribe.setVisibility(View.VISIBLE);
			}
			break;
		case Const.Net_btSetting_Data:
			btFavorite.setVisibility(View.GONE);
			btSubscribe.setVisibility(View.INVISIBLE);
			btSetting.setBackgroundResource(R.drawable.info_tab_bg_select);
			btSetting.setTextColor(Color.WHITE);
			btSetting.setCompoundDrawablesWithIntrinsicBounds(null,
					getResources().getDrawable(R.drawable.info_setting_select),
					null, null);
			lyVOAViewPanel.setVisibility(View.GONE);
			lyDailyViewPanel.setVisibility(View.GONE);
			lyBilingualViewPanel.setVisibility(View.GONE);
			lySettingPanel.setVisibility(View.VISIBLE);
			tvHeadTitle.setText(R.string.Setting_title);
			break;
		default:
			return;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				new AlertDialog.Builder(this)
						.setTitle(this.getString(R.string.exitdialog_app_title))
						.setMessage(
								this.getString(R.string.exitdialog_app_content))
						.setPositiveButton(this.getString(R.string.ok),
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialoginterface,
											int i) {
										finish();
									}
								})
						.setNegativeButton(this.getString(R.string.cancel),
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialoginterface,
											int i) {
									}
								}).show();
			} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
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
		}
		return super.onKeyDown(keyCode, event);
	}
}
