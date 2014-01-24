package com.kenny.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.kenny.Application.KApp;
import com.kenny.Interface.INotifyDataSetChanged;
import com.kenny.adapter.VOAContentAdapter;
import com.kenny.comui.DownloadDialog;
import com.kenny.dailyenglish.R;
import com.kenny.data.VOAContentBean;
import com.kenny.event.NetVOAPagebyFileEvent;
import com.kenny.event.NetWebPagebyFileEvent;
import com.kenny.event.NetXmlPagebyFileEvent;
import com.kenny.sqlite.DBManage;
import com.kenny.syseng.KSysEng;
import com.kenny.util.Const;
import com.kenny.util.KCommand;
import com.kenny.util.MD5Calculator;
import com.kenny.util.SafetyTimer;
import com.umeng.analytics.MobclickAgent;

public class InfoVOAViewPage extends Activity implements INotifyDataSetChanged,
		OnSeekBarChangeListener {
	public static final int INIT = 1;// 定义三种下载的状态：初始化状态，正在下载状态，暂停状态
	public static final int PAUSE = 2;
	public static final int FINISH = 3;// 下载完成
	public static final int UPDATE = 4;// 可更新

	public static final int DOWNLOADING = 30;// 正在下载
	public static final int INIT_SERVER = 31;// 定义三种下载的状态：初始化状态，正在下载状态，暂停状态
	public static final int WAIT = 32;// 等待已加入队列

	public static final int ERROR = 112;// 下载 失败
	public static final int ERROR_NET = 117;// 连接服务器失败
	public static final int ERROR_CREATE_FILE = 118;// 创建文件失败
	public static final int ERROR_NO_FILE = 120;// 文件未找到
	public static final int DELETE = 110;// 下载 失败
	public static final int ERROR_NET_DIC_FILE_SIZE = 111;// 获取词库文件大小失败
	public static final int ERROR_NET_DIC_UPDATE_SOFT = 121;// 当前版本不支持收费词典下载.请升级应用
	public static final int ERROR__NOTNET = 119;// 未找到可用网络

	private int nFlag = Const.Net_btVOA_Data;
	
	private final String MP3_Path = Const.NET_DIRECTORY;
	private String mStrUrl;
	private Integer mViews;
	private String groupName;
	private ListView wvBrower;
	private TextView tvStatus;
	private View rlBottom;
	private ImageView btAddFavorite, btShare, btPlay;
	boolean isEmptyAddFavorites;// true:空;false:存在
	private MediaPlayer mMediaPlayer;
	private String title;
	private VOAContentAdapter adapter;
	private VOAContentBean bean = null;
	private HashMap<String, Object> m_ReceiveMap;
	private SeekBar info_voa_mp3progressBar;
	private AudioManager audio = null;
	private String Mp3FilePath;
	// String groupName,
	// String itemTitle, String url, String imgUrl, int nFlag
	public void Init() 
	{
		Intent intent = getIntent();
		this.mStrUrl = intent.getStringExtra("url");
		this.mViews= intent.getIntExtra("views",0);
		this.nFlag =Const.Net_btVOA_Data;
//		this.nFlag = intent.getIntExtra("nFlag", Const.VOA_Page);
		this.groupName = this.getString(R.string.info_voa_title);
	}

	View LoadPannal, lyLoading, tvError_msg;
	View Head;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.info_voa_page);
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		Init();
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
		tvStatus = ((TextView) findViewById(R.id.tvStatus));
		info_voa_mp3progressBar = (SeekBar) findViewById(R.id.info_voa_mp3progressBar);
		info_voa_mp3progressBar.setProgress(0);
		info_voa_mp3progressBar.setOnSeekBarChangeListener(this);// 添加事件监听
		wvBrower = (ListView) findViewById(R.id.wvBrower);
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
		wvBrower.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				MobclickAgent.onEvent(InfoVOAViewPage.this, "Click-text2jump");
				int CurrentTime = bean.getTime(arg2);
				Log.v("wmh", "CurrentTime="+CurrentTime);
//				 && mMediaPlayer.isPlaying()
				Play(CurrentTime);
				if (mMediaPlayer != null) 
				{
					
				}
			}
		});

		Log.v("url", "mStrUrl=" + mStrUrl);
		switch (nFlag) {
		case Const.Net_btVOA_Data:
			KSysEng.getInstance().addEvent(
					new NetVOAPagebyFileEvent(InfoVOAViewPage.this, mStrUrl,
							this));
			break;
		case Const.Net_Dailysentence_Data:
			KSysEng.getInstance().addEvent(
					new NetXmlPagebyFileEvent(InfoVOAViewPage.this, mStrUrl,
							this));
			break;
		case Const.Net_Bilingual_Data:
			KSysEng.getInstance().addEvent(
					new NetWebPagebyFileEvent(InfoVOAViewPage.this, mStrUrl,
							this));
			break;
		case Const.Net_ConversationItem_Data:
			KSysEng.getInstance().addEvent(
					new NetWebPagebyFileEvent(InfoVOAViewPage.this, mStrUrl,
							this));
			break;
		default:
			KSysEng.getInstance().addEvent(
					new NetWebPagebyFileEvent(InfoVOAViewPage.this, mStrUrl,
							this));
			break;
		}

		View temp =findViewById(R.id.btBack);
		temp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		temp =  findViewById(R.id.btNoNetwork);
		temp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// wvBrower.reload();
				KSysEng.getInstance().addEvent(
						new NetVOAPagebyFileEvent(InfoVOAViewPage.this,
								mStrUrl, InfoVOAViewPage.this));
			}
		});
		btPlay = (ImageView) findViewById(R.id.btPlay);
		btPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Mp3FilePath = MP3_Path
						+ MD5Calculator.calculateMD5(bean.getMp3_url())
						+ ".kmp3";
				if (bean.getMp3_url().length() <= 10) {
					Toast.makeText(InfoVOAViewPage.this, "未找到MP3文件！",
							Toast.LENGTH_LONG).show();
					return;
				}
				MobclickAgent.onEvent(InfoVOAViewPage.this, "Click-playVOA");
				if (new File(Mp3FilePath).exists()) {
					
					if (mMediaPlayer == null || !mMediaPlayer.isPlaying()) {
						Play(info_voa_mp3progressBar.getProgress());
					} else {
						pauseMedia();
					}

				}
				else 
				{
					if (KCommand.isNetConnect(InfoVOAViewPage.this)) 
					{
						final DownloadDialog dialog = new DownloadDialog(
								InfoVOAViewPage.this, bean.getMp3_url(),
								Mp3FilePath, InfoVOAViewPage.this);
						dialog.ShowDialog(100);
						try {
							new Thread(new Runnable() {
								@Override
								public void run() {
									// TODO Auto-generated method stub
									dialog.ok();
								}
							}).start();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		btAddFavorite = (ImageView) findViewById(R.id.btAddFavorite);
		btShare = (ImageView) findViewById(R.id.btShare);
		btShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					switch (nFlag) {
					case Const.Net_Dailysentence_Data:

						// String value ="@金山词霸 每日一句 " + title+ bean.getEc() +
						// bean.getCe();
						// String
						// strMore="查看更多请点击这里 http://news.iciba.com/dailysentence";
						// int len=140-strMore.length()+25;
						// if(value.length()<len)
						// {
						// len=value.length();
						// }
						// KCommand.SendShare(
						// InfoVOAViewPage.this,
						// "wvBrower.getTitle()",
						// value.substring(0, len) + strMore,
						// SDFile.getSDLogoFilePath(bean.getImg()));
						break;
					case Const.Net_Bilingual_Data:
						KCommand.SendShare(InfoVOAViewPage.this,
								"wvBrower.getTitle()", "//<金山词霸手机版>双语资讯："
										+ title + "查看更多请点击这里 " + mStrUrl, null);
						break;
					case Const.Net_Conversation_Data:
						KCommand.SendShare(InfoVOAViewPage.this,
								"wvBrower.getTitle()", "//<金山词霸手机版>情景会话："
										+ title + "查看更多请点击这里 " + mStrUrl, null);
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		DBManage.getInstance(InfoVOAViewPage.this).open();
		isEmptyAddFavorites = DBManage.getInstance(InfoVOAViewPage.this)
				.isVOAFavorites(mStrUrl);
		DBManage.getInstance(InfoVOAViewPage.this).close();
		if (isEmptyAddFavorites) {
			btAddFavorite.setImageResource(R.drawable.bt_empty_favorite);
		} else {
			btAddFavorite.setImageResource(R.drawable.bt_favorite);
		}
		btAddFavorite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DBManage.getInstance(InfoVOAViewPage.this).open();
				boolean result = false;
				MobclickAgent.onEvent(InfoVOAViewPage.this, "Click-addVOA2favorite");
				
				isEmptyAddFavorites = DBManage.getInstance(InfoVOAViewPage.this)
						.isVOAFavorites(mStrUrl);
				if (isEmptyAddFavorites) 
				{
					result = DBManage.getInstance(InfoVOAViewPage.this).
							InsertVOAFavorites(String.valueOf(bean.getId()),bean.getTitle(),bean.getCntitle(),bean.getPublish(),mStrUrl,mViews); 
				} else {
					result = DBManage.getInstance(InfoVOAViewPage.this)
							.deleteVOAFavorites(mStrUrl) > 0;
				}
				DBManage.getInstance(InfoVOAViewPage.this).close();
				if (result) {
					isEmptyAddFavorites = !isEmptyAddFavorites;

					if (isEmptyAddFavorites) {
						btAddFavorite
								.setImageResource(R.drawable.bt_empty_favorite);
						Toast.makeText(
								InfoVOAViewPage.this,
								InfoVOAViewPage.this
										.getString(R.string.toast_msg_delete_success),
								Toast.LENGTH_SHORT).show();
					} else {
						btAddFavorite.setImageResource(R.drawable.bt_favorite);
						Toast.makeText(
								InfoVOAViewPage.this,
								InfoVOAViewPage.this
										.getString(R.string.toast_msg_add_success),
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(
							InfoVOAViewPage.this,
							InfoVOAViewPage.this
									.getString(R.string.toast_msg_operate_fail),
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		
	}

	Handler handler = new Handler();

	@Override
	public void NotifyDataSetChanged(int what, Object value, int arg1, int arg2) {
		m_ReceiveMap = (HashMap<String, Object>) value;
		switch (what) {
		case FINISH:
			Log.v("wmh", "DownloadDialog.FINISH");

			handler.post(new Runnable() {
				@Override
				public void run() {
					Play(info_voa_mp3progressBar.getProgress());
				}
			});

			break;
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
					Toast.makeText(
							InfoVOAViewPage.this,
							InfoVOAViewPage.this
									.getString(R.string.download_error),
							Toast.LENGTH_SHORT).show();
				}
			});
			break;
		case Const.Net_WebPagebyFile_Finish:
			handler.post(new Runnable() {
				@Override
				public void run() {
					title = (String) m_ReceiveMap.get("title");
					bean = (VOAContentBean) m_ReceiveMap.get("bean");
					adapter=new VOAContentAdapter(InfoVOAViewPage.this,bean.getContent());
					wvBrower.setAdapter(adapter);
					LoadPannal.setVisibility(View.GONE);
					wvBrower.setVisibility(View.VISIBLE);
					rlBottom.setVisibility(View.VISIBLE);
				}
			});
			break;
		case Const.Net_WebbyFile_Run:
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(
							InfoVOAViewPage.this,
							InfoVOAViewPage.this
									.getString(R.string.Net_WebbyFile_Run),
							Toast.LENGTH_SHORT).show();
				}
			});
			break;
		case Const.Net_WebbyFile_Error:
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(
							InfoVOAViewPage.this,
							InfoVOAViewPage.this
									.getString(R.string.Net_WebbyFile_Error),
							Toast.LENGTH_SHORT).show();
				}
			});
			break;
		case Const.Net_WebbyFile_Finish:
			mMediaPlayer.reset();
			AudioManager mAudioManager = (AudioManager) InfoVOAViewPage.this
					.getSystemService(Context.AUDIO_SERVICE);
			int currentVolume = mAudioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setVolume(currentVolume, currentVolume);
			try {
				int result = (Integer) m_ReceiveMap.get("result");
				if (result == 1) {
					File file = new File((String) m_ReceiveMap.get("path"));
					FileInputStream fis = new FileInputStream(file);
					mMediaPlayer.setDataSource(fis.getFD());
				} else {
					mMediaPlayer.setDataSource(
							InfoVOAViewPage.this.getBaseContext(),
							Uri.parse((String) m_ReceiveMap.get("url")));
				}
				mMediaPlayer.prepare();
				mMediaPlayer.start();
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

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mMediaPlayer != null) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
			}
			mMediaPlayer.stop();
			mMediaPlayer.release();
			if (mLyricTimer.isRunging()) {
				mLyricTimer.stopTimer();
			}
		}
	}
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
	public void stopVoice() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
			if (mLyricTimer.isRunging()) {
				mLyricTimer.stopTimer();
			}
		}
	}

	private boolean prepareMedia() 
	{
		try 
		{
			if (bean.getMp3_url().length() <= 10) 
			{
				Toast.makeText(this, this.getString(R.string.msg_mp3url_invalid), Toast.LENGTH_LONG).show();
				return false;
			}
			if(KCommand.isNetConnect(this)) 
			{
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.reset();
				mMediaPlayer.setDataSource(Mp3FilePath);
				mMediaPlayer.prepare();
//				mMediaPlayer.pause();
				mMediaPlayer
						.setOnCompletionListener(new OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp) {
								// TODO Auto-generated method stub

								if(mp.isPlaying())
								{
									btPlay.setImageResource(R.drawable.info_voa_stop);

								}
								else
								{
									btPlay.setImageResource(R.drawable.info_voa_play);
									info_voa_mp3progressBar.setProgress(0);
									tvStatus.setText(MilliToMinute(0));
									wvBrower.smoothScrollToPosition(0);
									adapter.setSelectItem(-1);
									adapter.notifyDataSetInvalidated();
//									info_voa_mp3progressBar.notifyAll();
								}
								Log.e("wmh", "zwzover:mp.getCurrentPosition()="+mp.getCurrentPosition());
							}
						});
				mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {

					@Override
					public void onPrepared(MediaPlayer mp) {
						// TODO Auto-generated method stub

					}
				});
				mMediaPlayer
						.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

							@Override
							public void onBufferingUpdate(MediaPlayer mp,
									int percent) {
								// TODO Auto-generated method stub

							}
						});
				mMediaPlayer.setOnInfoListener(new OnInfoListener() {

					@Override
					public boolean onInfo(MediaPlayer mp, int what, int extra) {
						// TODO Auto-generated method stub
						return false;
					}
				});
				return true;
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			pauseMedia();
			mMediaPlayer=null;
			info_voa_mp3progressBar.setProgress(0);
			tvStatus.setText(MilliToMinute(0));
			Log.v("wmh",bean.getMp3_url());
			Toast.makeText(getApplicationContext(), "音频错误无法播放!",Toast.LENGTH_LONG).show();
		}
		return false;
	}

	private SafetyTimer mLyricTimer =
	new SafetyTimer(1000,
			new SafetyTimer.OnTimeListener() {
				public void OnTimer() {
					Log.v("wmh", "SafetyTimer:OnTimer");
					if (mMediaPlayer != null&&mMediaPlayer.isPlaying()) 
					{
						int position = mMediaPlayer.getCurrentPosition();
						info_voa_mp3progressBar.setProgress(position);
						info_voa_mp3progressBar.setMax(mMediaPlayer.getDuration());
						tvStatus.setText(MilliToMinute(position));
						int index=bean.getTimeToPlayPos(mMediaPlayer.getCurrentPosition()
								);
//						wvBrower.smoothScrollToPosition(index); //by wmh 跟进播放
						if(adapter!=null)
						{
							adapter.notifyDataSetChanged();
							adapter.setSelectItem(index);
						}
					}
				}
			});

	/**
	 * 将毫秒转分钟
	 * 
	 * @param millisecond
	 * @return
	 */
	public static String MilliToMinute(int millisecond) {
		int nSecond = (int) (millisecond / 1000L);
		String strMinute = String.valueOf(nSecond / 60);
		String strSecond = String.valueOf(nSecond % 60);
		if (strSecond.length() <= 1) {
			strSecond = "0" + strSecond;
		}
		// String strSecond =myformat.format(nSecond%60);
		// String strSecond =myformat.format(nSecond/60+nSecond%60.0/100.0);
		return strMinute + ':' + strSecond;
	}

	// 播放音乐
	public void Play(int CurrentTime) {
		Log.v("wmh", "play");
		try {
			if (mMediaPlayer == null) 
			{
				prepareMedia();
			}
			if (mMediaPlayer.isPlaying()) 
			{
				mMediaPlayer.pause();
			}
			mMediaPlayer.seekTo(CurrentTime);
			mMediaPlayer.start();
			btPlay.setImageResource(R.drawable.info_voa_stop);
			info_voa_mp3progressBar.setMax(mMediaPlayer.getDuration());
			info_voa_mp3progressBar.setProgress(mMediaPlayer
					.getCurrentPosition());

			if (!mLyricTimer.isRunging()) {
				mLyricTimer.startTimer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pauseMedia() {
		if (mMediaPlayer == null || !mMediaPlayer.isPlaying()) {
			return;
		}
		mMediaPlayer.pause();
		btPlay.setImageResource(R.drawable.info_voa_play);
		if (mLyricTimer.isRunging()) {
			mLyricTimer.stopTimer();
		}
	}

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		pauseMedia();
		if (mLyricTimer.isRunging()) {
			mLyricTimer.stopTimer();
		}
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
		tvStatus.setText(MilliToMinute(arg0.getProgress()));
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
//		tvStatus.setText(MilliToMinute(arg0.getProgress()));
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
//		&& mMediaPlayer.isPlaying()
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) 
		{
			mMediaPlayer.seekTo(arg0.getProgress());
		}
		else if (mMediaPlayer != null)
		{
			arg0.setProgress(mMediaPlayer.getCurrentPosition());
			arg0.setMax(mMediaPlayer.getDuration());
		}
		else
		{
			arg0.setProgress(0);
			arg0.setMax(100);
		}
	};
}
