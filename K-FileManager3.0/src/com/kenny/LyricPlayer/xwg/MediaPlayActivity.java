package com.kenny.LyricPlayer.xwg;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.framework.log.P;
import com.kenny.KFileManager.R;
import com.kenny.LyricPlayer.xwg.MediaPlayerService.LyricPlayerListener;
import com.kenny.LyricPlayer.xwg.MediaPlayerService.MediaInfoProvider;
import com.kenny.file.tools.T;

/**
 * ZIP文件操作
 * 
 * @author WangMinghui
 * 
 */
public class MediaPlayActivity extends Activity implements OnClickListener,
		MediaPlayerService.LyricPlayerListener,
		// IMusicCallback,
		OnItemClickListener, OnSeekBarChangeListener, ServiceConnection
{
	private TextView txtTextTitle, tvStartStatus, tvEndStatus;
	private String tvPath = null;// 路径

	private ImageView btPrev, btRepeat, btPlay, btShuffle, btNext;
	private ListView lvPlayLoaclList;

	private SeekBar sbPlayProgress;
	private Handler handler = new Handler();

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		P.v("media.onCreate start this=" + this);
		setContentView(R.layout.mediaplayactivity);
		initContentView();
		Intent intent = getIntent();
		Uri uri = (Uri) intent.getData();
		if (uri != null)
		{
			tvPath = uri.getPath();
			if (tvPath != null && tvPath.length() > 0)
			{
				openFile(tvPath);
			}
		}
		if (tvPath != null && tvPath.length() > 0)
		{
			txtTextTitle.setText(tvPath);
		} else
		{
			txtTextTitle.setText("未找到相应的文件");
		}
		startAndBindService();
		P.v("media.onCreate end");
	}

	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		setLyricPlayerListener(this);
		onStateChanged();
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		setLyricPlayerListener(null);
		super.onPause();
	}

	protected void onDestroy()
	{
		super.onDestroy();
		P.v("media.onDestroy start");
		setLyricPlayerListener(null);
		unbindService(this);
		if (!isPlaying())
		{
			stopService();
		}
		P.v("media.onDestroy end");
	}

	/** 初始化 */
	private void initContentView()
	{

		txtTextTitle = (TextView) findViewById(R.id.tvPath);
		tvStartStatus = (TextView) findViewById(R.id.tvStartStatus);
		tvEndStatus = (TextView) findViewById(R.id.tvEndStatus);
		lvPlayLoaclList = (ListView) findViewById(R.id.lvPlayLoaclList);
		btPrev = (ImageView) findViewById(R.id.btPrev);
		btRepeat = (ImageView) findViewById(R.id.btRepeat);
		btPlay = (ImageView) findViewById(R.id.btPlay);
		btShuffle = (ImageView) findViewById(R.id.btShuffle);
		btNext = (ImageView) findViewById(R.id.btNext);
		sbPlayProgress = (SeekBar) findViewById(R.id.sbPlayProgress);
		sbPlayProgress.setOnSeekBarChangeListener(this);
		btPrev.setOnClickListener(this);
		btRepeat.setOnClickListener(this);
		btPlay.setOnClickListener(this);
		btShuffle.setOnClickListener(this);
		btNext.setOnClickListener(this);
	}

	public void onClick(View view)
	{
		switch (view.getId())
		{
		case R.id.btPrev:
			onStartCommand(MediaPlayerService.ACTION_PREVIOUS, 0);
			// playPrev();
			break;
		case R.id.btRepeat:
			onStartCommand(MediaPlayerService.ACTION_PLAYMODE, 0);
			// play
			break;
		case R.id.btPlay:
			onStartCommand(MediaPlayerService.ACTION_PLAY_PAUSE, 0);
			// play();
			break;
		case R.id.btShuffle:
			onStartCommand(MediaPlayerService.ACTION_SHUFFLE, 0);
			break;
		case R.id.btNext:
			onStartCommand(MediaPlayerService.ACTION_NEXT, 0);
			break;
		}
	}

	// button 状态的更改
	protected void updateButtonState()
	{
		MediaInfoProvider provider = getMediaInfoProvider();
		if (provider != null)
		{
			btPrev.setEnabled(provider.hasPrev());
			if (isPlaying())
			{
				btPlay.setImageResource(R.drawable.audio_pause);
			} else
			{
				btPlay.setImageResource(R.drawable.audio_play);
			}
			btNext.setEnabled(provider.hasNext());
			btShuffle
					.setImageResource((provider.getShuffle()) ? R.drawable.audio_shuffle_sel
							: R.drawable.audio_shuffle_nor);
			switch (provider.getPlayMode())
			{
			case MediaPlayerService.Normal:
				this.btRepeat.setImageResource(R.drawable.audio_repeat_nor);
				break;
			case MediaPlayerService.Repeat:
				this.btRepeat.setImageResource(R.drawable.audio_repeat_sel);
				break;
			case MediaPlayerService.Repeat_1:
				this.btRepeat.setImageResource(R.drawable.audio_repeat_sel_1);
				break;
			}
		}
	}

	public void onPositionChanged(final long position)
	{
		// TODO Auto-generated method stub
		handler.post(new Runnable()
		{

			public void run()
			{
				// TODO Auto-generated method stub
				P.debug("onPositionChanged");
				sbPlayProgress.setProgress((int) position);
				tvStartStatus.setText(T.MilliToMinute((int) position));
			}
		});
	}

	public void onStateChanged()
	{
		if (mPlaybackService != null)
		{
			onStateChanged(mPlaybackService.getMediaState());
		}
	}

	public void onStateChanged(final int state)
	{
		if (mPlaybackService != null)
			handler.post(new Runnable()
			{
				public void run()
				{
					switch (state)
					{
					case MediaPlayerService.State_Init:
						break;
					case MediaPlayerService.State_Play:
						break;
					case MediaPlayerService.State_Pause:
						break;
					case MediaPlayerService.State_Stop:
						break;
					case MediaPlayerService.State_Shuffle:
						break;
					case MediaPlayerService.State_PlayMode:
						break;
					}
					sbPlayProgress.setMax(getDuration());
					// sbPlayProgress.setProgress(0);
					tvEndStatus.setText(T.MilliToMinute(getDuration()));
					txtTextTitle.setText(mPlaybackService.getDataSource());
					if (adapter != null && mPlaybackService != null)
					{
						adapter.setSelectIndex(mPlaybackService
								.getCurrentIndex());
						if (android.os.Build.VERSION.SDK_INT >= 8)
						{
						lvPlayLoaclList.smoothScrollToPosition(mPlaybackService
								.getCurrentIndex());
						}
						adapter.notifyDataSetChanged();
					}
					updateButtonState();
					P.debug("onStateChanged");
				}
			});
	}

	public void onButtonStateChanged()
	{
		handler.post(new Runnable()
		{
			public void run()
			{
				// TODO Auto-generated method stub
				updateButtonState();
			}
		});
	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id)
	{
		playTo(position);
	}

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser)
	{
		// TODO Auto-generated method stub
		P.debug("onProgressChanged:progress=" + progress);
		// seek(progress);
	}

	public void onStartTrackingTouch(SeekBar seekBar)
	{
		// TODO Auto-generated method stub
		P.debug("onStartTrackingTouch");
		pause();
	}

	public void onStopTrackingTouch(SeekBar seekBar)
	{
		// TODO Auto-generated method stub
		P.debug("onStopTrackingTouch");
		seek(seekBar.getProgress());
		start();
	}

	// ---------------------------------------
	private MediaPlayerService mPlaybackService = null;
	private MusicFileAdapter adapter;// 播放列表Adapter

	// private MediaInfoProvider mLyricInfoProvider;

	public void onServiceConnected(ComponentName className, IBinder service)
	{
		P.v("ServiceConnection:onServiceConnected");
		mPlaybackService = ((MediaPlayerService.LocalBinder) service)
				.getService();
		setLyricPlayerListener(this);
		adapter = mPlaybackService.getMusicFileAdapter();
		lvPlayLoaclList.setAdapter(adapter);
		lvPlayLoaclList.setOnItemClickListener(this);
		if (tvPath != null && tvPath.length() > 0)
		{
			mPlaybackService.openFile(tvPath);
		}
		onStateChanged();
	}

	public void onServiceDisconnected(ComponentName className)
	{
		P.v("ServiceConnection:onServiceDisconnected");
		mPlaybackService = null;
	}

	void startAndBindService()
	{
		startService(new Intent(this, MediaPlayerService.class));
		bindService(new Intent(this, MediaPlayerService.class), this,
				Context.BIND_AUTO_CREATE);
	}

	void stopService()
	{
		if (mPlaybackService != null)
		{
			stopService(new Intent(this, MediaPlayerService.class));
		}
	}

	public MediaInfoProvider getMediaInfoProvider()
	{
		if (mPlaybackService != null)
		{
			return mPlaybackService.getMediaInfoProvider();
		} else
		{
			return null;
		}
	}

	public String getDataSource()
	{
		if (mPlaybackService != null)
		{
			return mPlaybackService.getDataSource();
		} else
		{
			return null;
		}
	}

	public void openFile(String path)
	{
		if (mPlaybackService != null)
		{
			mPlaybackService.openFile(path);
		}
	}

	public void start()
	{
		if (mPlaybackService != null)
		{
			mPlaybackService.start();
		}
	}

	public void stop()
	{
		if (mPlaybackService != null)
		{
			mPlaybackService.stop();
		}
	}

	public void pause()
	{
		if (mPlaybackService != null)
		{
			mPlaybackService.pause();
		}
	}

	public boolean isPlaying()
	{
		if (mPlaybackService != null)
		{
			return mPlaybackService.isPlaying();
		} else
		{
			return false;
		}
	}

	public boolean isPausing()
	{
		if (mPlaybackService != null)
		{
			return mPlaybackService.isPausing();
		} else
		{
			return false;
		}
	}

	public int getDuration()
	{
		if (mPlaybackService != null)
		{
			return mPlaybackService.getDuration();
		} else
		{
			return 0;
		}
	}

	public int getPosition()
	{
		if (mPlaybackService != null)
		{
			return mPlaybackService.getPosition();
		} else
		{
			return 0;
		}
	}

	public void playTo(int pos)
	{
		if (mPlaybackService != null)
		{
			mPlaybackService.playTo(pos);

		}
	}

	public void play()
	{
		if (mPlaybackService != null)
		{
			mPlaybackService.playStart();

		}
	}

	public void playNext()
	{
		if (mPlaybackService != null)
		{
			mPlaybackService.playNext();
		}
	}

	public void playPrev()
	{
		if (mPlaybackService != null)
		{
			mPlaybackService.playPrev();
		}
	}

	public long seek(int whereto)
	{
		if (mPlaybackService != null)
		{
			return mPlaybackService.seek(whereto);
		} else
		{
			return 0;
		}
	}

	public long onStartCommand(String action, int flags)
	{
		if (mPlaybackService != null)
		{
			return mPlaybackService.onStartCommand(action, flags);
		} else
		{
			return 0;
		}
	}

	public void setLyricPlayerListener(LyricPlayerListener listener)
	{
		if (mPlaybackService != null)
		{
			mPlaybackService.setLyricPlayerListener(listener);
		}
	}
}
