package com.kenny.LyricPlayer.xwg;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.sort.LyricFileNameSort;
import com.kenny.file.util.FT;

public class MediaPlayerService extends Service implements
		MediaPlayer.OnCompletionListener, MusicFocusable
{
	public static final int Normal = 0;
	public static final int Repeat = 1;
	public static final int Repeat_1 = 2;
	// 0:init 1:play 2:pause 3:stop
	public static final int State_Init = 0;
	public static final int State_Play = 1;
	public static final int State_Pause = 2;
	public static final int State_Stop = 3;
	public static final int State_PlayMode = 4;
	public static final int State_Shuffle = 5;
	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private int NOTIFICATION = R.string.local_service_started;
	// private NotificationManager mNotificationManager;
	private ComponentName mReceiverName = null;

	private AudioManager mAudioManager = null;
	protected MediaPlayer mMediaPlayer = null;
	private boolean mIsPausing = false;
	private ArrayList<LyricBean> mMusicList = new ArrayList<LyricBean>();// 播放列表
	private LoadData mLoadData = new LoadData();// 加载文件Event
	private Handler handler = new Handler();
	private MusicFileAdapter adapter;// 播放列表Adapter
	// our AudioFocusHelper object, if it's available (it's available on SDK
	// level >= 8)
	// If not available, this will be null. Always check for null before
	// using!
	AudioFocusHelper mAudioFocusHelper = null;

	public class LocalBinder extends Binder
	{
		public MediaPlayerService getService()
		{
			return MediaPlayerService.this;
		}
	}

	private final IBinder mBinder = new LocalBinder();
	private boolean mBinded = false;

	public IBinder onBind(Intent intent)
	{
		mBinded = true;
		return mBinder;
	}

	public void onRebind(Intent intent)
	{
		mBinded = true;
		super.onRebind(intent);
	}

	public boolean onUnbind(Intent intent)
	{
		mBinded = false;
		return super.onUnbind(intent);
	}

	public int openFile(String path)
	{
		if (path != null && path.length() > 0)
		{
			String strFileName = path.substring(path.lastIndexOf("/") + 1);
			path = path.substring(0, path.lastIndexOf("/") + 1);
			mLoadData.setPath(path, strFileName);
			return 1;
		} else
		{
			return 0;
		}
	}

	/**
	 * 加载音频数据
	 * 
	 * @author WangMinghui
	 * 
	 */
	public class LoadData extends AbsEvent
	{
		private File inFile;
		private String mPath, mFileName;
		private String[] fileNameList;

		public void setPath(String path, String fileName)
		{
			mPath = path;
			mFileName = fileName;
			SysEng.getInstance().addEvent(this);
		}
		public void ok()
		{
			// handler.post(new AbsEvent()
			// {
			// public void ok()
			// {
			// ShowDialog(fileNameList.length);
			// }
			// });
			inFile = new File(mPath);
			fileNameList = inFile.list();
			final ArrayList<LyricBean> mTMusicList = new ArrayList<LyricBean>();// 播放列表
			if (fileNameList != null && fileNameList.length > 0)
			{
				for (String fileName : fileNameList)
				{
					String fileEnds = FT.getExName(fileName);
					if (fileEnds.equals("m4a") || fileEnds.equals("mp3")
							|| fileEnds.equals("mid") || fileEnds.equals("xmf")
							|| fileEnds.equals("ogg") || fileEnds.equals("wav")
							|| fileEnds.equals("3gp"))
					{
						LyricBean bean = new LyricBean();
						bean.setId("1");
						bean.setMusic_title(fileName);
						bean.setMusic_url(mPath + fileName);
						mTMusicList.add(bean);
					}
				}
				Collections.sort(mTMusicList, new LyricFileNameSort());


				handler.post(new AbsEvent()
				{
					public void ok()
					{
						try
						{
							mMusicList.clear();
							mMusicList.addAll(mTMusicList);
							
							
							for (int i = 0; i < mMusicList.size(); i++)
							{
								String fileName = mMusicList.get(i).getMusic_title();
								if (mFileName.equals(fileName))
								{
//									P.v("mFileName=" + mFileName + ",fileName=" + fileName
//											+ ",i=" + i);
									mMediaInfoProvider.moveTo(i);
									if (adapter != null)
									{
										adapter.setSelectIndex(i);
									}
									break;
								}
							}
							adapter.notifyDataSetChanged();
							stop();
							mDataSource = mMediaInfoProvider.getUrl();
							mTitle = mMediaInfoProvider.getTitle();
							start();
						} catch (Exception e)
						{
							e.printStackTrace();
							Toast.makeText(MediaPlayerService.this,
									"加载失败:" + e.getMessage(),
									Toast.LENGTH_SHORT).show();
						}
					}
				});
			} else
			{
				handler.post(new AbsEvent()
				{
					public void ok()
					{
				Toast.makeText(
						MediaPlayerService.this,
						MediaPlayerService.this
								.getString(R.string.error_unknown),
						Toast.LENGTH_SHORT).show();
					}
				});
			}
		}
	}

	public interface MediaInfoProvider
	{
		int getCurrentIndex();

		String getUrl();

		String getTitle();

		boolean moveToNext();

		boolean moveTo(int pos);

		boolean moveToPrev();

		boolean hasNext();

		boolean hasPrev();

		void setShuffle(Boolean shuffle);

		boolean getShuffle();

		List<LyricBean> getMusicList();

		void setMusicList(List<LyricBean> mMusicList);

		int setPlayMode(int playMode);

		int getPlayMode();
	}

	private MediaInfoProvider mMediaInfoProvider = null;
	private String mDataSource;
	private String mTitle;

	public int getCurrentIndex()
	{
		return mMediaInfoProvider.getCurrentIndex();
	}

	public MediaInfoProvider getMediaInfoProvider()
	{
		return mMediaInfoProvider;
	}

	public void onCreate()
	{
		super.onCreate();

		mMediaPlayer = new MediaPlayer();
		P.debug("MediaPlayerService:onCreate");
		mMediaPlayer.setWakeMode(getApplicationContext(),
				PowerManager.PARTIAL_WAKE_LOCK);

		mMediaPlayer.setOnCompletionListener(this);
		mAudioFocusHelper = new AudioFocusHelper(getApplicationContext(), this);

		// create the Audio Focus Helper, if the Audio Focus feature is
		// available (SDK 8 or above)
		if (android.os.Build.VERSION.SDK_INT >= 8)
		{
			mReceiverName = new ComponentName(getPackageName(),
					MediaButtonReceiver.class.getName());
			mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			// mAudioManager.registerMediaButtonEventReceiver(mReceiverName);//by
			// wmh
		}
		mMediaInfoProvider = new MyInfoProvider(mMusicList);
		adapter = new MusicFileAdapter(this, mMusicList);

	}

	public MusicFileAdapter getMusicFileAdapter()
	{
		return adapter;
	}

	public void onDestroy()
	{
		super.onDestroy();
		P.debug("MediaPlayerService:onDestroy");
		mAudioFocusHelper.giveUpAudioFocus();
		if (mAudioManager != null && mReceiverName != null)
		{
			// mAudioManager.unregisterMediaButtonEventReceiver(mReceiverName);//by
			// wmh
		}
		mMediaPlayer.setOnCompletionListener(null);
		if (!isStop())
		{
			stop();
		}
		setLyricPlayerListener(null);
		// mMediaPlayer.release();
		// mMediaPlayer = null;
	}

	public String getTitle()
	{
		return mTitle;
	}

	public String getDataSource()
	{
		return mDataSource;
	}

	public void start()
	{
		mAudioFocusHelper.tryToGetAudioFocus();
		if (!isPausing())
		{
			if (isPlaying())
			{
				// 停止
				mAudioFocusHelper.giveUpAudioFocus();
				mMediaPlayer.stop();
				mIsPausing = false;
				// mNotificationManager.cancel(NOTIFICATION);
				stopForeground(true);
			}
			try
			{
				mMediaPlayer.reset();
				P.debug("mDataSource=" + mDataSource);
				File file = new File(mDataSource);
				FileInputStream fis = new FileInputStream(file);
				mMediaPlayer.setDataSource(fis.getFD());
				mMediaPlayer.prepare();
			} catch (Exception e)
			{
				e.printStackTrace();
				return;
			}
			mMediaPlayer.start();
		} else
		{
			mIsPausing = false;
			mMediaPlayer.start();
		}
		if (!mLyricTimer.isRunging())
		{
			mLyricTimer.startTimer();
		}
		onStateChanged(State_Play);
		showNotification();
	}

	public void stop()
	{
		mAudioFocusHelper.giveUpAudioFocus();
		mMediaPlayer.stop();
		mIsPausing = false;
		// mNotificationManager.cancel(NOTIFICATION);
		stopForeground(true);
		if (mLyricTimer.isRunging())
		{
			mLyricTimer.stopTimer();
		}
		onStateChanged(State_Stop);
	}

	private int nMediaState = 0;// 播放器状态

	/**
	 * 获取播放器状态 State_Stop:
	 * 
	 * @return
	 */
	public int getMediaState()
	{
		return nMediaState;
	}

	public void onStateChanged(int nState)
	{
		nMediaState = nState;
		if (mLyricPlayerListener != null)
		{
			mLyricPlayerListener.onStateChanged(nState);
		}
	}

	public void pause()
	{

		mAudioFocusHelper.giveUpAudioFocus();
		mIsPausing = true;
		mMediaPlayer.pause();
		if (mLyricTimer.isRunging())
		{
			mLyricTimer.stopTimer();
		}
		onStateChanged(State_Pause);
	}

	private SafetyTimer mLyricTimer = new SafetyTimer(1000,
			new SafetyTimer.OnTimeListener()
			{
				public void OnTimer()
				{
					if (mMediaPlayer != null)
					{
						int position = mMediaPlayer.getCurrentPosition();
						if (mLyricPlayerListener != null)
						{
							mLyricPlayerListener.onPositionChanged(position);
						}
					}
				}
			});

	// 播放器状态监听
	public interface LyricPlayerListener
	{
		public void onPositionChanged(long position);// 播放进度

		public void onStateChanged(int nState);// 播放器状态 0:init 1:play 2:pause
												// 3:stop
	}

	private LyricPlayerListener mLyricPlayerListener = null;

	void setLyricPlayerListener(LyricPlayerListener listener)
	{
		mLyricPlayerListener = listener;
	}

	public boolean playTo(int pos)
	{
		if (isPlaying() || isPausing())
		{
			stop();
		}
		if (mMediaInfoProvider.moveTo(pos))
		{
			mDataSource = mMediaInfoProvider.getUrl();
			mTitle = mMediaInfoProvider.getTitle();
			seek(0);
			start();
			adapter.setSelectIndex(pos);
			return true;
		} else
		{
			return false;
		}
	}

	public boolean playNext()
	{
		if (isPlaying() || isPausing())
		{
			stop();
		}
		if (mMediaInfoProvider.moveToNext())
		{
			mDataSource = mMediaInfoProvider.getUrl();
			mTitle = mMediaInfoProvider.getTitle();
			seek(0);
			start();
			return true;
		} else
		{
			return false;
		}
	}

	public boolean playStart()
	{
		if (!isPlaying())
		{
			start();
		}
		return true;
	}

	public boolean Pause()
	{
		if (isPlaying())
		{
			pause();
		}
		return true;
	}

	public boolean playPrev()
	{
		if (isPlaying() || isPausing())
		{
			stop();
		}
		if (mMediaInfoProvider.moveToPrev())
		{
			mDataSource = mMediaInfoProvider.getUrl();
			mTitle = mMediaInfoProvider.getTitle();
			seek(0);
			start();
			return true;
		} else
		{
			return false;
		}
	}

	public boolean isPausing()
	{
		return mIsPausing;
	}

	public boolean isPlaying()
	{
		return mMediaPlayer.isPlaying();
	}

	public boolean isStop()
	{
		return (isPlaying() == false && isPausing() == false);
	}

	public int getDuration()
	{
		return mMediaPlayer.getDuration();
	}

	public int getPosition()
	{
		return mMediaPlayer.getCurrentPosition();
	}

	public long seek(int whereto)
	{
		mMediaPlayer.seekTo(whereto);
		return whereto;
	}

	public static final String ACTION_PAUSE = "KENNY.MediaPlayer.action.PAUSE";
	public static final String ACTION_PLAY = "KENNY.MediaPlayer.action.PLAY";
	public static final String ACTION_PLAY_PAUSE = "KENNY.MediaPlayer.action.PLAY_PAUSE";
	public static final String ACTION_PREVIOUS = "KENNY.MediaPlayer.action.PREVIOUS";
	public static final String ACTION_NEXT = "KENNY.MediaPlayer.action.NEXT";
	public static final String ACTION_PLAYMODE = "KENNY.MediaPlayer.action.PLAYMODE";
	public static final String ACTION_SHUFFLE = "KENNY.MediaPlayer.action.SHUFFLE";

	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return START_NOT_STICKY;
	}

	public int onStartCommand(String action, int flags)
	{
		int s1 = 1;
		s1 = s1 + 1;
		P.debug("onStartCommand");

		P.debug("action=" + action);
		if (action.equals(ACTION_PLAY_PAUSE))
		{
			if (isPlaying())
			{
				pause();
			} else
			{
				start();
			}
		} else if (action.equals(ACTION_PLAYMODE))
		{
			flags = (mMediaInfoProvider.getPlayMode() + 1) % 2;
			mMediaInfoProvider.setPlayMode(flags);
			onStateChanged(State_PlayMode);
		} else if (action.equals(ACTION_SHUFFLE))
		{
			mMediaInfoProvider.setShuffle(!mMediaInfoProvider.getShuffle());
			onStateChanged(State_Shuffle);
		} else if (action.equals(ACTION_PLAY))
		{
			if (!isPlaying())
			{
				start();
			}
		} else if (action.equals(ACTION_PAUSE))
		{
			if (isPlaying())
			{
				pause();
			}
		} else if (action.equals(ACTION_PREVIOUS))
		{
			if (isPlaying() || isPausing())
			{
				playPrev();
			}
		} else if (action.equals(ACTION_NEXT))
		{
			if (isPlaying() || isPausing())
			{

				playNext();
			}
		}

		return START_NOT_STICKY;
		// Means we started the service, but
		// don't want it to
		// restart in case it's killed.
	}

	public void onCompletion(MediaPlayer mp)
	{
		P.v("mediaPlayer", "onCompletion");
		if (mLyricTimer.isRunging())
		{
			mLyricTimer.stopTimer();
		}
		// onStateChanged(State_Play);
		mIsPausing = false;
		// mNotificationManager.cancel(NOTIFICATION);
		stopForeground(true);
		// if (mBinded == false)
		// {
		// stopSelf();
		// }
		handler.post(new Runnable()
		{
			public void run()
			{
				playNext();
			}
		});
	}

	// The volume we set the media player to when we lose audio focus, but
	// are allowed to reduce
	// the volume instead of stopping playback.
	public final float DUCK_VOLUME = 0.1f;

	public void onGainedAudioFocus()
	{
		// restart media player with new focus settings
		if (isPausing())
			configAndStartMediaPlayer();
	}

	public void onLostAudioFocus()
	{
		// start/restart/pause media player with new focus settings
		if (mMediaPlayer != null && mMediaPlayer.isPlaying())
			configAndStartMediaPlayer();
	}

	/**
	 * Reconfigures MediaPlayer according to audio focus settings and
	 * starts/restarts it. This method starts/restarts the MediaPlayer
	 * respecting the current audio focus state. So if we have focus, it will
	 * play normally; if we don't have focus, it will either leave the
	 * MediaPlayer paused or set it to a low volume, depending on what is
	 * allowed by the current focus settings. This method assumes mPlayer !=
	 * null, so if you are calling it, you have to do so from a context where
	 * you are sure this is the case.
	 */
	void configAndStartMediaPlayer()
	{
		if (mAudioFocusHelper.getAudioFocus() == AudioFocusHelper.NoFocusNoDuck)
		{
			// If we don't have audio focus and can't duck, we have
			// to pause, even if mState
			// is State.Playing. But we stay in the Playing state so
			// that we know we have to resume
			// playback once we get the focus back.
			if (isPlaying())
				pause();
			return;
		} else if (mAudioFocusHelper.getAudioFocus() == AudioFocusHelper.NoFocusCanDuck)
			mMediaPlayer.setVolume(DUCK_VOLUME, DUCK_VOLUME); // we'll
																// be
																// relatively
																// quiet
		else
			mMediaPlayer.setVolume(1.0f, 1.0f); // we can be loud

		if (isPausing())
			start();
	}

	public interface NotificationProvider
	{
		public Notification createNotification(Context context);
	}

	NotificationProvider mNotificationProvider = new NotificationProvider()
	{

		public Notification createNotification(Context context)
		{
			Notification notification = new Notification(R.drawable.audio_play,
					getTitle(), System.currentTimeMillis());
			// The PendingIntent to launch our activity if the user selects
			// this notification
			Intent intent = new Intent(context, MediaPlayActivity.class);
			// intent.setData(Uri.fromFile(new File(provider.getUrl())));
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					intent, 0);
			// contentIntent.
			// Set the info for the views that show in the notification
			// panel.
			notification.setLatestEventInfo(context,
					getText(R.string.audio_playing_title), getTitle(),
					contentIntent);
			notification.flags = notification.flags
					| Notification.FLAG_ONGOING_EVENT;
			return notification;
		}
	};

	/** * Show a notification while this service is running. */
	private void showNotification()
	{
		// Send the notification.
		// mNotificationManager.notify(NOTIFICATION,
		// mNotificationProvider.createNotification(this));
		startForeground(NOTIFICATION,
				mNotificationProvider.createNotification(this));

	}
}
