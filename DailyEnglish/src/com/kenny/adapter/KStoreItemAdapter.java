package com.kenny.adapter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kenny.Application.KApp;
import com.kenny.Interface.ImageCallback;
import com.kenny.activity.SubscribePage;
import com.kenny.comui.CircleImageButton;
import com.kenny.dailyenglish.R;
import com.kenny.data.DailyNetData;
import com.kenny.sqlite.DBManage;
import com.kenny.util.AsyncIcoLoader;
import com.kenny.util.ColorFactory;
import com.kenny.util.Const;
import com.kenny.util.KCommand;
import com.kenny.util.Utils;
import com.umeng.analytics.MobclickAgent;

public class KStoreItemAdapter extends ArrayAdapter<DailyNetData> {
	private final LayoutInflater mInflater;
	private Context m_ctx;
	private AsyncIcoLoader imageLoader = null;
	private int TextColor = 0xff000000;
	private boolean isDelVisible = false;
	private ViewHolder viewHolder = null;
	private final String voicePath = Const.VOICE_DIRECTORY;
	private MediaPlayer mMediaPlayer = null;
	private  final String sharePath = Environment.getExternalStorageDirectory()
			+ "/powerword/cache/logo/";
	public void SetDelVisible(boolean isDelVisible) {
		this.isDelVisible = isDelVisible;
	}

	public boolean GetDelVisible() {
		return isDelVisible;
	}

	public KStoreItemAdapter(Context context, ArrayList<DailyNetData> apps) {
		super(context, 0, apps);
		mInflater = LayoutInflater.from(context);
		m_ctx = context;
		imageLoader = AsyncIcoLoader.GetObject(m_ctx);
		mMediaPlayer = new MediaPlayer();
	}

	class ViewHolder 
	{
		ImageView daily_image;
		TextView daily_date_mouth;
		TextView daily_date_day;
		TextView daily_content;
		TextView daily_note;
		TextView daily_translation;
		CircleImageButton info_btn_share;
		CircleImageButton info_btn_add;
		CircleImageButton info_voice;
	}

	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.v("wmh", "getView:start");
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listitem_store_item,
					parent, false);

			viewHolder.daily_image = (ImageView) convertView
					.findViewById(R.id.daily_image);
			
			viewHolder.daily_date_mouth = (TextView) convertView
					.findViewById(R.id.daily_date_mouth);
			viewHolder.daily_date_day = (TextView) convertView
					.findViewById(R.id.daily_date_day);
			viewHolder.daily_content = (TextView) convertView
					.findViewById(R.id.daily_content);
			viewHolder.daily_note = (TextView) convertView
					.findViewById(R.id.daily_note);
			viewHolder.daily_translation = (TextView) convertView
					.findViewById(R.id.daily_translation);

			viewHolder.info_btn_share = (CircleImageButton) convertView
					.findViewById(R.id.info_btn_share);
//			viewHolder.info_btn_share.setReversed(true);
			viewHolder.info_btn_add = (CircleImageButton) convertView
					.findViewById(R.id.info_btn_add);
			viewHolder.info_voice = (CircleImageButton) convertView
					.findViewById(R.id.info_voice);
			
			convertView.setTag(viewHolder);
		}
		else 
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		DailyNetData bean = this.getItem(position);
		DBManage.getInstance(m_ctx).open();
		boolean isEmptyAddFavorites = DBManage.getInstance(m_ctx).isDailyFavorites(
				bean.getSid());
		DBManage.getInstance(m_ctx).close();
		if (isEmptyAddFavorites) 
		{
			
//			viewHolder.info_btn_add.setReversed(true);
			viewHolder.info_btn_add.setImageResource(R.drawable.info_add_nor);
			//viewHolder.info_btn_add.setBackgroundResource(R.drawable.info_add_button_bg);
		}
		else 
		{
//			viewHolder.info_btn_add.setReversed(false);
			viewHolder.info_btn_add.setImageResource(R.drawable.info_del_nor);
			//viewHolder.info_btn_add.setBackgroundResource(R.drawable.info_added_button_bg);
		}
		KApp app = (KApp) m_ctx.getApplicationContext();
		int nTextcolor=app.colorFactory.getColor();
		viewHolder.daily_content.setTextColor(nTextcolor);
		viewHolder.daily_content.setText(bean.getContent());
		viewHolder.daily_note.setText(bean.getNote());
		viewHolder.daily_date_day.setText(String.valueOf(bean.getDay()));
		
		viewHolder.daily_date_mouth.setText(bean.getStrMonth()+"\n"+bean.getYear());
		viewHolder.daily_date_mouth.setTextColor(nTextcolor);
		
		viewHolder.daily_translation.setText(bean.getTranslation());
		int width=Utils.dip2px(m_ctx, 40);
		int height=Utils.dip2px(m_ctx,40);
		viewHolder.daily_date_day.setBackgroundDrawable(new BitmapDrawable(app.colorFactory.createArcBitmap(width, height)));

		Drawable drawImage = null;
		if (bean.getPicture() == null || bean.getPicture().equals("")) {

		} else {
			drawImage = imageLoader.loadDrawable(bean.getPicture(),
					new ImageCallback() {
						@Override
						public void imageLoaded(Drawable imageDrawable,
								String imageUrl) {
							try {
								if (imageDrawable != null) {
									viewHolder.daily_image
											.setImageDrawable(imageDrawable);
									KStoreItemAdapter.this.notifyDataSetChanged();
								}
							} catch (Exception e) {
								e.printStackTrace();
								viewHolder.daily_image
										.setImageResource(R.drawable.daily_image);
							}
						}
					});
		}
		viewHolder.info_voice.setOnClickListener(new OnVoiceClickListener(bean));
		viewHolder.info_btn_add.setOnClickListener(new OnAddClickListener(viewHolder.info_btn_add,bean));
		viewHolder.info_btn_share.setOnClickListener(new OnShareClickListener(viewHolder.daily_image,bean));
		
		if (drawImage != null) 
		{
			viewHolder.daily_image.setImageDrawable(drawImage);
		}
		else 
		{
			viewHolder.daily_image.setImageResource(R.drawable.daily_image);
		}
		Log.v("wmh", "getView:end");
		return convertView;
	}
	public class OnShareClickListener implements OnClickListener
	{
		private DailyNetData bean;
		private View view;
		public OnShareClickListener(View view,DailyNetData bean )
		{
			this.bean=bean;
			this.view=view;
		}

		public void onClick(View v) 
		{
			MobclickAgent.onEvent(m_ctx, "Click-sharesentence");
			String value = bean.getContent() +
					bean.getNote();
			String strMore ="    VOAから英語脳—超人気のアメリカ英語リスンニング！iPhoneとAndroid対応！http://goo.gl/W6NE8t";
			int len = 140 - strMore.length() + 25;
			if (value.length() < len) 
			{
				len = value.length();
			}
			view.buildDrawingCache();
			Bitmap bit = view.getDrawingCache();
			saveMyBitmap("temp.png", bit);
			String path = sharePath + "temp.png";
//			KCommand.SendShare(m_ctx, bean.getNote(),
//					value.substring(0, len) + strMore, path);
			KCommand.SendShare(m_ctx, bean.getNote(),
			value + strMore, path);
		}
	}
	public void saveMyBitmap(String fileName, Bitmap mBitmap) {
		try {
			File f = new File(sharePath, fileName);
			FileOutputStream fOut = null;

			f.createNewFile();
			fOut = new FileOutputStream(f);

			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public class OnAddClickListener implements OnClickListener
	{
		private DailyNetData bean;
		private CircleImageButton button;
		public OnAddClickListener(CircleImageButton button,DailyNetData bean )
		{
			this.bean=bean;
			this.button=button;
		}

		public void onClick(View v) 
		{
			// TODO Auto-generated method stub
			DBManage.getInstance(m_ctx).open();
			boolean isEmptyAddFavorites = DBManage.getInstance(m_ctx).isDailyFavorites(
					bean.getSid());
			MobclickAgent.onEvent(m_ctx, "Click-addsentence2favorite");
			if (isEmptyAddFavorites)
			{
//				button.setReversed(true);
				button.setImageResource(R.drawable.info_add_nor);
				isEmptyAddFavorites = false;
				DBManage.getInstance(m_ctx).insertDaily(bean);
				Toast.makeText(m_ctx,
						m_ctx.getString(R.string.toast_msg_add_success),
						Toast.LENGTH_SHORT).show();
			}
			else 
			{
//				button.setReversed(false);
				button.setImageResource(R.drawable.info_del_nor);
				Toast.makeText(m_ctx,
						m_ctx.getString(R.string.toast_msg_delete_success),
						Toast.LENGTH_SHORT).show();
				isEmptyAddFavorites = true;
				DBManage.getInstance(m_ctx).deleteDailyFavorites(bean.getSid());
			}
			DBManage.getInstance(m_ctx).close();
			button.invalidate();
			KStoreItemAdapter.this.notifyDataSetChanged();
		}
	}
	public class OnVoiceClickListener implements OnClickListener
	{
		private DailyNetData bean;
		public OnVoiceClickListener(DailyNetData bean )
		{
			this.bean=bean;
		}

		public void onClick(View v) {
			// TODO Auto-generated method stub

			String temp[] = String.valueOf(bean.getTts()).split("/");
			String voiceName = temp[temp.length - 1];
			File f = new File(voicePath + voiceName);
			// imageView = (ImageView) params[1];
			if (!f.exists()) {
				Toast.makeText(m_ctx,
						m_ctx.getString(R.string.Net_WebbyFile_Run),
						Toast.LENGTH_SHORT).show();
			}
			new VoiceDownloadTask().execute(bean.getTts());

		}
	}
	private class VoiceDownloadTask extends AsyncTask<Object, Object, Boolean> {

		// private ImageView imageView = null;

		@Override
		protected Boolean doInBackground(Object... params) {
			// TODO Auto-generated method stub

			String temp[] = String.valueOf(params[0]).split("/");
			String voiceName = temp[temp.length - 1];
			File destDir = new File(voicePath);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}

			File f = new File(voicePath + voiceName);
			// imageView = (ImageView) params[1];
			if (f.exists()) {

				try {
					mMediaPlayer.reset();
					mMediaPlayer.setDataSource(voicePath + voiceName);
					mMediaPlayer.prepare();
					mMediaPlayer.start();
					return true;
				} catch (Exception e) {
					return false;
				}
			} else {
				if (Utils.getSDCardStatus()) 
				{// 有sd卡 播放加载
					return getUrlVoice((String) params[0]);
				} else {
					try {
						mMediaPlayer.reset();
						mMediaPlayer.setDataSource(String.valueOf(params[0]));
						mMediaPlayer.prepare();
						mMediaPlayer.start();
						return true;
					} catch (Exception e) {
						return false;
					}
				}

			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			if (!result) {
				Toast.makeText(m_ctx,
						m_ctx.getString(R.string.Net_WebbyFile_Error),
						Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
	private boolean getUrlVoice(String url) {
		String imageUrl = url;
		String temp[] = url.split("/");
		String picName = temp[temp.length - 1];
		InputStream is;
		try {
			is = getUrlImgInputStream(imageUrl);
			// Toast.makeText(main, "正在加载音频...", Toast.LENGTH_SHORT).show();
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(url);// 设置要播放的文件路径
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			saveStreamToFile(is, voicePath + picName);
			return true;
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return false;
		}
		// 保存文件

	}

	private InputStream getUrlImgInputStream(String url) throws IOException {
		URL imageUrl = null;
		imageUrl = new URL(url);

		HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
		conn.connect();
		InputStream is = conn.getInputStream();
		return is;
	}
	public static boolean saveStreamToFile(InputStream in, String fileNamePath)
			throws IOException {
		boolean result = true;

		File f = null;
		try {
			f = new File(fileNamePath);
			if (f.exists()) {
				f.delete();
			} else {
				f.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(f);
			copyStream(in, fos);
			fos.close();
		} catch (Exception e) {
			if (f != null && f.exists()) {
				f.delete();
			}
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	public static void copyStream(InputStream in, OutputStream out)
			throws IOException {
		BufferedInputStream bin = new BufferedInputStream(in);
		BufferedOutputStream bout = new BufferedOutputStream(out);

		byte[] buffer = new byte[4096];

		while (true) {
			int doneLength = bin.read(buffer);
			if (doneLength == -1)
				break;
			bout.write(buffer, 0, doneLength);
		}
		bin.close();
		bout.flush();
		bout.close();
	}
}
