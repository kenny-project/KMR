package com.kenny.activity;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.kenny.Application.KApp;
import com.kenny.Interface.INotifyDataSetChanged;
import com.kenny.Interface.ImageCallback;
import com.kenny.dailyenglish.R;
import com.kenny.data.DailySentencebean;
import com.kenny.event.NetByFileEvent;
import com.kenny.event.NetWebPagebyFileEvent;
import com.kenny.event.NetXmlPagebyFileEvent;
import com.kenny.file.Coder;
import com.kenny.file.SDFile;
import com.kenny.sqlite.DBManage;
import com.kenny.syseng.KSysEng;
import com.kenny.util.AsyncIcoInfoViewLoader;
import com.kenny.util.Const;
import com.kenny.util.KCommand;
import com.kenny.util.Log;
import com.kenny.util.Utils;

import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

public class InfoViewPage extends Activity implements INotifyDataSetChanged {
	private int nFlag = Const.Net_Dailysentence_Data;;
	private String mStrUrl;
	private String groupName;
	private WebView wvBrower;
	private View rlBottom;
	private ImageView btAddFavorite, btShare;
	boolean isEmptyAddFavorites;// true:空;false:存在
	private boolean nDownFlag = false;
	private MediaPlayer mPlayer;
	private String title;
	private String buffer;
	private DailySentencebean bean = null;
	private HashMap<String, Object> m_ReceiveMap;
	private AsyncIcoInfoViewLoader imageLoader = null;
//	String groupName,
//	String itemTitle, String url, String imgUrl, int nFlag
	public void Init() 
	{
		imageLoader = AsyncIcoInfoViewLoader.GetObject(this);
		mPlayer = new MediaPlayer();
		
	     Intent intent = getIntent();
	     this.mStrUrl=intent.getStringExtra("url");
	     this.groupName=intent.getStringExtra("groupName");
	     this.nFlag=intent.getIntExtra("nFlag",Const.Bilingual);
	}

	public static String getExName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1).toLowerCase();
			}
		}
		return filename.toLowerCase();
	}

	private WebViewClient mWebPageClient = new WebViewClient() {
		private int errorcode = 0;

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			try {
				String fileEnds = getExName(url);
				if (fileEnds.equals("mp3")) {
					KSysEng.getInstance().addEvent(
							new NetByFileEvent(InfoViewPage.this, url, InfoViewPage.this));
					return true;
				}

				String temp = url.substring(url.lastIndexOf("?") + 1);
				String[] params = temp.split("&");
				String act = null, mod = null;
				for (String param : params) {
					String[] field = param.split("=");
					if (field[0].equals("act")) {
						act = field[1];
					} else if (field[0].equals("mod")) {
						mod = field[1];
					}
				}
				String k = Coder.Md5(mod, act);
				url = url + "&k=" + k;
				Log.v("p", "url=" + url);
				if (act.equals("showTitle")) {
					// String
					// a="http://dict-mobile.iciba.com/new/newtest/index.php?mod=infor&act=showInforContent&cid=1539075&k=6e3325f6f075b0b4aa0958967802aab8";
					// wvBrower.loadUrl(url);1
					// url="http://dict-mobile.iciba.com/new/index.php?mod=infor&act=showInfor&cid=1537335&k=189ef65ad0b6c355d8648b3d201f3dff";
					// url="http://dict-mobile.iciba.com/new/index.php?act=show&sid=224&k=d95b1357cbf7627ca328fa10946eac55&v=2";

					KSysEng.getInstance().addEvent(
							new NetWebPagebyFileEvent(InfoViewPage.this, url,
									InfoViewPage.this));
					// wvBrower.loadUrl(a);
				} else {
					if (nDownFlag) 
					{
						return true;
					}
					nDownFlag = true;
//					SysEng.getInstance().runEvent(
//							new NextPageEvent(main, new InfoViewPage(main,
//									datatype, groupName, "test Title", url,
//									null, Const.Net_Conversation_Data),
//									Const.SHOW_ANIM, null));
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(InfoViewPage.this, InfoViewPage.this.getString(R.string.download_error),
						Toast.LENGTH_SHORT).show();
			}
			// switch (nFlag)
			// {
			// case Const.Net_Dailysentence_Data:
			// case Const.Net_Bilingual_Data:
			//
			// break;
			// case Const.Net_Conversation_Data:
			//
			// break;
			// case Const.Net_ConversationList_Data:
			// break;
			// default:
			// return false;
			// }
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			this.errorcode = -1;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			if (errorcode == 0) {
				LoadPannal.setVisibility(View.GONE);
				wvBrower.setVisibility(View.VISIBLE);
				if (bean != null) {
					imageLoader.loadDrawable(bean.getImg(),
							new ImageCallback() {
								@Override
								public void imageLoaded(Drawable imageDrawable,
										String imageUrl) {
									// webSettings.setBlockNetworkImage(false);
									
									handler.post(new Runnable() {
										@Override
										public void run() {
											// TODO Auto-generated method stub
											// wvBrower.reload();
											// wvBrower.refreshDrawableState();
											wvBrower.loadDataWithBaseURL(
													"http://dict-mobile.iciba.com/new/",
													bean.toString(),
													"text/html", "utf-8", "");
											// wvBrower.refreshDrawableState();
											// webSettings.setBlockNetworkImage(false);

										}
									});
								}
							});
				}
				// if (result)
				// {
				// webSettings.setBlockNetworkImage(false);
				// }
				RefreshPage(nFlag);
			} else {
				wvBrower.setVisibility(View.GONE);
				LoadPannal.setVisibility(View.VISIBLE);
				tvError_msg.setVisibility(View.VISIBLE);
				lyLoading.setVisibility(View.GONE);
				Toast.makeText(InfoViewPage.this, InfoViewPage.this.getString(R.string.download_error),
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	WebSettings webSettings;
	View LoadPannal, lyLoading, tvError_msg;
	View Head;
	private long startTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.info_view_page);
		Init();
		nDownFlag = false;

		
		 startTime = System.currentTimeMillis();  //開始時間


		   
		Head = findViewById(R.id.Head);
		KApp app = (KApp) getApplicationContext();
		Head.setBackgroundColor(app.colorFactory.getColor());
		LoadPannal = findViewById(R.id.rlNoNetwork);
		LoadPannal.setVisibility(View.VISIBLE);

		tvError_msg = findViewById(R.id.tvError_msg);
		tvError_msg.setVisibility(View.GONE);

		lyLoading = findViewById(R.id.lyLoading);
		lyLoading.setVisibility(View.VISIBLE);
		rlBottom = findViewById(R.id.rlBottom);
		rlBottom.setVisibility(View.GONE);
		((TextView) findViewById(R.id.Title)).setText(groupName);

		wvBrower = (WebView) findViewById(R.id.wvBrower);
		wvBrower.setFocusable(false);
		wvBrower.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		wvBrower.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				mGestureDetector.onTouchEvent(arg1);
				return false;
			}
		});
		webSettings = wvBrower.getSettings();
		wvBrower.setVisibility(View.GONE);
		webSettings.setBlockNetworkImage(true);
		webSettings.setJavaScriptEnabled(true);

		wvBrower.setSelected(false);
		wvBrower.setWebChromeClient(new WebChromeClient());
		wvBrower.setWebViewClient(mWebPageClient);
		Log.v("url", "mStrUrl=" + mStrUrl);
		switch (nFlag) {
		case Const.Net_Dailysentence_Data:
			KSysEng.getInstance().addEvent(
					new NetXmlPagebyFileEvent(InfoViewPage.this, mStrUrl, this));
			break;
		case Const.Net_Bilingual_Data:
			KSysEng.getInstance().addEvent(
					new NetWebPagebyFileEvent(InfoViewPage.this, mStrUrl, this));
			break;
		case Const.Net_ConversationItem_Data:
			KSysEng.getInstance().addEvent(
					new NetWebPagebyFileEvent(InfoViewPage.this, mStrUrl, this));
			break;
		default:
			KSysEng.getInstance().addEvent(
					new NetWebPagebyFileEvent(InfoViewPage.this, mStrUrl, this));
			break;
		}
		
		Button temp = (Button) findViewById(R.id.btBack);
		temp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		temp = (Button) findViewById(R.id.btNoNetwork);
		temp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				wvBrower.reload();
			}
		});

		btAddFavorite = (ImageView) findViewById(R.id.btAddFavorite);
		btShare = (ImageView) findViewById(R.id.btShare);
		btShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// String ec = "",ce="";
				// if (m_ReceiveMap != null)
				// {
				// ec = (String) m_ReceiveMap.get("ec");
				// ec+= (String) m_ReceiveMap.get("ce");
				// }
				try {
					switch (nFlag) {
					case Const.Net_Dailysentence_Data:
						
						String value ="@金山词霸 每日一句 " + title+ bean.getEc() + bean.getCe();
						String strMore="查看更多请点击这里 http://news.iciba.com/dailysentence";
						int len=140-strMore.length()+25;
						if(value.length()<len)
						{
							len=value.length();
						}
						KCommand.SendShare(
								InfoViewPage.this,
								wvBrower.getTitle(),
								value.substring(0, len) + strMore,
								SDFile.getSDLogoFilePath(bean.getImg()));
						break;
					case Const.Net_Bilingual_Data:
						KCommand.SendShare(InfoViewPage.this, wvBrower.getTitle(),
								"//<金山词霸手机版>双语资讯：" + title + "查看更多请点击这里 "
										+ mStrUrl, null);
						break;
					case Const.Net_Conversation_Data:
						KCommand.SendShare(InfoViewPage.this, wvBrower.getTitle(),
								"//<金山词霸手机版>情景会话：" + title + "查看更多请点击这里 "
										+ mStrUrl, null);
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		//by wmh 把收藏功能掩藏
		btAddFavorite.setVisibility(View.GONE);
//		DBManage.getInstance(InfoViewPage.this).open();
//		isEmptyAddFavorites = DBManage.getInstance(InfoViewPage.this)
//				.isHasFavorites(mStrUrl);
//		DBManage.getInstance(InfoViewPage.this).close();
//		if (isEmptyAddFavorites) {
//
//			// btAddFavorite.setBackgroundResource(R.drawable.bt_empty_favorite);
//			btAddFavorite.setImageResource(R.drawable.bt_empty_favorite);
//		} else {
//			// btAddFavorite.setBackgroundResource(R.drawable.bt_favorite);
//			btAddFavorite.setImageResource(R.drawable.bt_favorite);
//		}
//		btAddFavorite.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				DBManage.getInstance(InfoViewPage.this).open();
//				boolean result = false;
//				Log.v("wmh", "isEmptyAddFavorites=" + isEmptyAddFavorites);
//				Log.v("wmh", "wvBrower.getTitle()=" + title);
//				if (isEmptyAddFavorites) {
//					result = DBManage.getInstance(InfoViewPage.this).InsertFavorites(
//							groupName, title, mStrUrl);
//				} else {
//					result = DBManage.getInstance(InfoViewPage.this)
//							.deleteFavorites(mStrUrl) > 0;
//				}
//				DBManage.getInstance(InfoViewPage.this).close();
//				if (result) {
//					isEmptyAddFavorites = !isEmptyAddFavorites;
//
//					if (isEmptyAddFavorites) {
//						btAddFavorite
//								.setImageResource(R.drawable.bt_empty_favorite);
//						Toast.makeText(
//								InfoViewPage.this,
//								InfoViewPage.this.getString(R.string.toast_msg_delete_success),
//								Toast.LENGTH_SHORT).show();
//					} else {
//						btAddFavorite.setImageResource(R.drawable.bt_favorite);
//						Toast.makeText(InfoViewPage.this,
//								InfoViewPage.this.getString(R.string.toast_msg_add_success),
//								Toast.LENGTH_SHORT).show();
//					}
//				} else {
//					Toast.makeText(InfoViewPage.this,
//							InfoViewPage.this.getString(R.string.toast_msg_operate_fail),
//							Toast.LENGTH_SHORT).show();
//				}
//			}
//		});

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	private void RefreshPage(int type) {
		switch (type) {
		case Const.Net_Dailysentence_Data:
		case Const.Net_Bilingual_Data:
		case Const.Net_Favorite_Data:
		case Const.Net_Conversation_Data:
			rlBottom.setVisibility(View.VISIBLE);
			break;
//		case Const.Net_ConversationList_Data:
//			rlBottom.setVisibility(View.GONE);
//			break;
		case Const.Net_ConversationItem_Data:
			rlBottom.setVisibility(View.VISIBLE);
			break;
		default:
			return;
		}
	}
	Handler handler=new Handler();
	@Override
	public void NotifyDataSetChanged(int what, Object value, int arg1, int arg2) {
		m_ReceiveMap = (HashMap<String, Object>) value;
		switch (what) {
		case Const.Net_WebPagebyFile_Run:
			handler.post(new Runnable() {
				@Override
				public void run() {

				}
			});
			break;
		case Const.Net_WebPagebyFile_Error:
			handler.post(new Runnable() {
				@Override
				public void run() {
					wvBrower.setVisibility(View.GONE);
					LoadPannal.setVisibility(View.VISIBLE);
					tvError_msg.setVisibility(View.VISIBLE);
					lyLoading.setVisibility(View.GONE);
					Toast.makeText(InfoViewPage.this,
							InfoViewPage.this.getString(R.string.download_error),
							Toast.LENGTH_SHORT).show();
				}
			});
			break;
		case Const.Net_WebPagebyFile_Finish:
			handler.post(new Runnable() {
				@Override
				public void run() {
					title = (String) m_ReceiveMap.get("title");
					bean = (DailySentencebean) m_ReceiveMap.get("bean");
					buffer = (String) m_ReceiveMap.get("buffer");
					wvBrower.loadDataWithBaseURL(
							"http://dict-mobile.iciba.com/new/", buffer,
							"text/html", "utf-8", "");
					LoadPannal.setVisibility(View.GONE);
					wvBrower.setVisibility(View.VISIBLE);
					webSettings.setBlockNetworkImage(false);
					RefreshPage(nFlag);
				}
			});
			break;
		case Const.Net_WebbyFile_Run:
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(InfoViewPage.this,
							InfoViewPage.this.getString(R.string.Net_WebbyFile_Run),
							Toast.LENGTH_SHORT).show();
				}
			});
			break;
		case Const.Net_WebbyFile_Error:
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(InfoViewPage.this,
							InfoViewPage.this.getString(R.string.Net_WebbyFile_Error),
							Toast.LENGTH_SHORT).show();
				}
			});
			break;
		case Const.Net_WebbyFile_Finish:
			mPlayer.reset();
			AudioManager mAudioManager = (AudioManager) InfoViewPage.this
					.getSystemService(Context.AUDIO_SERVICE);
			int currentVolume = mAudioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			mPlayer.setVolume(currentVolume, currentVolume);
			try {
				int result = (Integer) m_ReceiveMap.get("result");
				if (result == 1) {
					File file = new File((String) m_ReceiveMap.get("path"));
					FileInputStream fis = new FileInputStream(file);
					mPlayer.setDataSource(fis.getFD());
				} else {
					mPlayer.setDataSource(InfoViewPage.this.getBaseContext(),
							Uri.parse((String) m_ReceiveMap.get("url")));
				}
				mPlayer.prepare();
				mPlayer.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
	}

	final GestureDetector mGestureDetector = new GestureDetector(
			new SimpleOnGestureListener() {

				@Override
				public boolean onDoubleTap(MotionEvent e) {
					return true;
				}

				@Override
				public boolean onSingleTapConfirmed(MotionEvent e) {
					return true;
				}

				@Override
				public void onLongPress(MotionEvent e) {
					// 这里触发长按事件
					KeyEvent shiftPressEvent = new KeyEvent(0, 0,
							KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SHIFT_LEFT,
							0, 0);
					shiftPressEvent.dispatch(wvBrower);
				}
			});

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	};
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
}
