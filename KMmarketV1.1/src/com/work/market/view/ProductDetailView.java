package com.work.market.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.byfen.app.KApp;
import com.byfen.market.R;
import com.framework.syseng.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.file.tools.ApkTools;
import com.work.Image.ImageLoader;
import com.work.Interface.INotifyDataSetChanged;
import com.work.market.bean.AppListBean;
import com.work.market.db.DBAdapter;
import com.work.market.net.Common;
import com.work.market.net.DictBean;
import com.work.market.net.Downloader;
import com.work.market.net.HttpUtil;
import com.work.market.util.HttpUrlConst;

public class ProductDetailView extends ObjectView implements
		INotifyDataSetChanged
{

	private Context mContext;
	private Activity m_MainActivity;
	private ProgressDialog pd;
	private NetTask mNetTask;
	private AppListBean bean;
	private String m_url;
	private ArrayList<String> m_list_url;
	private ImageView m_product_image;
	private ImageView m_star_image1;
	private ImageView m_star_image2;
	private ImageView m_star_image3;
	private ImageView m_star_image4;
	private ImageView m_star_image5;

	private TextView m_product_scr;
	private TextView m_product_title;
	private TextView m_product_version;
	private TextView m_product_class;
	private TextView m_product_time;//
	private TextView m_product_author;//
	private TextView m_product_size;//
	private TextView m_product_times;//
	private TextView m_product_language;//
	private TextView m_product_new_description;//

	private LinearLayout m_down_button;
	private ImageView m_shoucang_button;

	private String mtime = "";//
	private String dev_name = "";//
	private String mdesc = "";//
	private String mLang = "中文";//
	private ImageAdapter mImageAdapter;
	private ArrayList<Bitmap> m_list_bitmap;
	private CustomGallery mGallery;
	private TextView m_down_text;
	private String m_product_type = "";
	private int m_product_type_num = 0;
	private KApp app;

	public void SetID(int id)
	{
		bean.setId(id);
		m_url = HttpUrlConst.detail + id;
		UpdateStatus();
	}

	protected class OnKClickListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			DictBean downing = bean.getDictBean();
			if (downing != null)
			{
				switch (downing.getStatus())
				{
				case Downloader.INIT:
				case Downloader.UPDATE:// 更新
					bean.Start(mContext);
					break;
				case Downloader.INIT_SERVER:
				case Downloader.DOWNLOADING:
				case Downloader.WAIT:
					// Toast.makeText(mContext,
					// "已经加到到下载队列,请等待",Toast.LENGTH_LONG).show();
					downing.Pause();
					break;
				case Downloader.PAUSE:// 暂停中
					bean.Start(mContext);
					break;
				case Downloader.FINISH:// 下载完成
					try
					{
						PackageInfo packages = mContext.getPackageManager()
								.getPackageInfo(bean.getPn(), 1);
						if (packages.versionCode == bean.getVercode())
						{// 运行
							Toast.makeText(mContext, "运行", Toast.LENGTH_LONG)
									.show();
							ApkTools.StartApk(mContext, bean.getPn());
						}
						else
						{// 安装
							Toast.makeText(mContext, "安装", Toast.LENGTH_LONG)
									.show();
							ApkTools.InstallApk(mContext, downing.getFilePath());
						}
					}
					catch (Exception e)
					{
						//e.printStackTrace();
						Toast.makeText(mContext, "安装", Toast.LENGTH_LONG)
								.show();
						ApkTools.InstallApk(mContext, downing.getFilePath());
					}
					// //viewHolder.mydownimage.setBackgroundResource(R.drawable.goon);
					// m_down_text.setText("打开");
					break;
				default:
					bean.Start(mContext);
					break;
				}
			}
			else
			{
				bean.Start(mContext);
				// if (!MyService.downing) {
				// Intent serviceIntent = new Intent(mContext, MyService.class);
				// serviceIntent.putExtra("type", "start");
				// mContext.startService(serviceIntent);
				// }
				// //viewHolder.mydownimage.setBackgroundResource(R.drawable.puase);
				m_down_text.setText("等待");
			}
		}
	}

	public ProductDetailView(Context context, Activity aActivity,
			ViewPager mPager)
	{
		this(context, null, aActivity, mPager);
	}

	public ProductDetailView(Context context, AttributeSet attrs,
			Activity aActivity, ViewPager mPager)
	{
		super(context, attrs);
		mContext = context;
		app = ((KApp) context.getApplicationContext());
		m_MainActivity = aActivity;
		LayoutInflater.from(context).inflate(R.layout.detail, this, true);
		pd = new ProgressDialog(m_MainActivity);
		pd.setMessage(m_MainActivity.getText(R.string.pd_loading));
		pd.setCancelable(true);
		bean = new AppListBean();
		m_list_bitmap = new ArrayList<Bitmap>();
		mImageAdapter = new ImageAdapter(mContext);
		m_product_image = (ImageView) findViewById(R.id.detail_soft_icon_img);
		m_star_image1 = (ImageView) findViewById(R.id.detail_list_log1);
		m_star_image2 = (ImageView) findViewById(R.id.detail_list_log2);
		m_star_image3 = (ImageView) findViewById(R.id.detail_list_log3);
		m_star_image4 = (ImageView) findViewById(R.id.detail_list_log4);
		m_star_image5 = (ImageView) findViewById(R.id.detail_list_log5);

		m_product_scr = (TextView) findViewById(R.id.detail_soft_scr);
		m_product_title = (TextView) findViewById(R.id.detail_soft_title);
		m_product_version = (TextView) findViewById(R.id.detail_soft_version);
		m_product_class = (TextView) findViewById(R.id.detail_soft_class);

		m_product_time = (TextView) findViewById(R.id.detail_soft_time);
		m_product_author = (TextView) findViewById(R.id.detail_soft_author);
		m_product_size = (TextView) findViewById(R.id.detail_soft_size);
		m_product_times = (TextView) findViewById(R.id.detail_soft_down_times);
		m_product_language = (TextView) findViewById(R.id.detail_soft_language);
		m_product_new_description = (TextView) findViewById(R.id.detail_soft_description);
		m_down_text = (TextView) findViewById(R.id.detail_down_text);

		m_down_button = (LinearLayout) findViewById(R.id.betail_down_button);
		m_down_button.setOnClickListener(new OnKClickListener());
		m_shoucang_button = (ImageView) findViewById(R.id.betail_shouchang_button);

		m_shoucang_button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// TODO Auto-generated method stub
				boolean result = DBAdapter.createDBAdapter(mContext)
						.queryMessageById(String.valueOf(bean.getId()),
								Common.user_key);

				if (!result)
				{
					boolean aa = DBAdapter.createDBAdapter(mContext)
							.insertMessage(bean.getId(), bean.getPn(),
									bean.getTitle(), Common.user_key);
					String dialogstring;
					if (aa)
					{
						dialogstring = "收藏成功";// m_MainActivity.getString(R.string.net_faile);
						m_shoucang_button
								.setImageResource(R.drawable.detail_favorite);
					}
					else
					{
						dialogstring = "收藏失败";// m_MainActivity.getString(R.string.net_faile);
					}
					Toast.makeText(mContext, dialogstring, Toast.LENGTH_LONG)
							.show();
				}
				else
				{
					// 提示已经收藏过
					String dialogstring = "取消收藏";
					boolean aa = DBAdapter.createDBAdapter(mContext)
							.deleteMessageById(bean.getId(), Common.user_key);
					m_shoucang_button
							.setImageResource(R.drawable.detail_unfavorite);
					Toast.makeText(mContext, dialogstring, Toast.LENGTH_LONG)
							.show();
				}
			}
		});
		mGallery = (CustomGallery) findViewById(R.id.detail_soft_gallery);
		mGallery.setmPager(mPager);
		mGallery.setAdapter(mImageAdapter);
		m_list_url = new ArrayList<String>();
	}

	private void upDatasoft(int num, boolean apd, String url)
	{

		if (apd) pd.show();
		if (mNetTask != null)
		{
			mNetTask.cancel(true);
		}
		mNetTask = new NetTask(num, apd, url);
		mNetTask.execute(null);
	}

	//
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
			{
				// adapter.title.add(keyword);
				String json = HttpUtil.doGet(m_url);// =
				return json;
			}
			else if (m_num == 1)
			{
				String filename = Common.getmymd5(m_url);
				String data = HttpUtil.GetPhoto5(m_url, filename);//
				return data;
			}
			return null;
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
			{
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
						JSONObject jsresult = new JSONObject(result);//
						String id = jsresult.getString("id");//
						String lang = jsresult.getString("lang");
						String title = "";
						if (lang.equals("cn"))
						{
							title = jsresult.getString("title");
							mLang = "中文";
						}
						else
						{
							title = jsresult.getString("en_name");
							mLang = "英文";
						}
						String pn = jsresult.getString("pn");
						String logo = jsresult.getString("logo");
						String size = jsresult.getString("size");
						String score = jsresult.getString("score");
						String apkurl = jsresult.getString("apkurl");
						String dc = jsresult.getString("dc");
						String times = jsresult.getString("update_time");
						m_product_type_num = jsresult.getInt("type");
						if (times.indexOf(" ") > 0)
						{
							int x = times.indexOf(" ");
							mtime = times.substring(0, x);
						}
						else
						{
							mtime = times;
						}
						String Vername = jsresult.getString("last_update_ver");//
						mdesc = jsresult.getString("desc");//
						dev_name = jsresult.getString("dev_name");//

						bean.setId(id);
						bean.setTitle(title);
						bean.setPn(pn);
						bean.setLogourl(logo);
						bean.setSize(Common.getLength(size));
						bean.setScore(score);
						bean.setAppurl(apkurl);
						bean.setDowntiems(dc);
						bean.setVername(Vername);
						bean.setAppFileExt(jsresult.getString("ext"));
						JSONArray jsonObj1 = jsresult.getJSONArray("imgs");
						for (int i = 0; i < jsonObj1.length(); i++)
						{
							String temps = jsonObj1.getString(i);
							m_list_url.add(temps);
						}
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
					SetAllData();
					pd.dismiss();
					if (m_list_url.size() > 0)
					{
						int num = m_list_url.size();

						for (int i = 0; i < num; i++)
						{
							Bitmap tempbitmap = Getphontnames(m_list_url.get(0));
							if (tempbitmap != null)
							{
								m_list_bitmap.add(tempbitmap);
								m_list_url.remove(0);
							}
							else
							{
								upDatasoft(1, false, m_list_url.get(0));
								return;
							}
						}
						mImageAdapter.notifyDataSetChanged();
					}
					boolean isFavorite = DBAdapter.createDBAdapter(mContext)
							.queryMessageById(String.valueOf(bean.getId()),
									Common.user_key);

					if (isFavorite)
					{
						m_shoucang_button
								.setImageResource(R.drawable.detail_favorite);
					}
					else
					{
						m_shoucang_button
								.setImageResource(R.drawable.detail_unfavorite);
					}
				}
				else if (m_num == 1)
				{
					if (m_list_url.size() > 0)
					{
						int num = m_list_url.size();

						for (int i = 0; i < num; i++)
						{
							Bitmap tempbitmap = Getphontnames(m_list_url.get(0));
							if (tempbitmap != null)
							{
								m_list_bitmap.add(tempbitmap);
								m_list_url.remove(0);
							}
							else
							{
								upDatasoft(1, false, m_list_url.get(0));
								return;
							}
						}
						mImageAdapter.notifyDataSetChanged();
					}
				}
			}
		}
	}

	public Bitmap Getphontnames(String url)
	{
		String filename = Common.getmymd5(url);

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

	public void Startget()
	{
		upDatasoft(0, true, m_url);
	}

	class KImageCallback implements com.work.Interface.ImageCallback
	{
		ImageView logImage;

		public KImageCallback(ImageView logImage)
		{
			this.logImage = logImage;
		}

		public void imageLoaded(Drawable imageDrawable, String imageUrl)
		{
			logImage.setImageDrawable(imageDrawable);
		}
	};

	private void SetAllData()
	{
		if (bean != null)
		{
			UpdateStatus();
			SetTypes();
			Drawable draw = null;
			draw = ImageLoader.GetObject(mContext).loadNetDrawable(true,
					bean.getLogourl(), new KImageCallback(m_product_image));
			if (draw != null)
			{
				m_product_image.setImageDrawable(draw);
			}
			else
			{
				m_product_image.setImageResource(R.drawable.deflogo);
			}

			Bitmap tempbitmap = Getphontnames(bean.getLogourl());
			if (tempbitmap != null)
			{
				Drawable drawable = (Drawable) new BitmapDrawable(tempbitmap);
				m_product_image.setBackgroundDrawable(drawable);
			}

			float num = bean.getScore();
			if (num >= 1)
			{
				m_star_image1.setBackgroundResource(R.drawable.star1);
			}
			if (num >= 2)
			{
				m_star_image2.setBackgroundResource(R.drawable.star1);
			}
			if (num >= 3)
			{
				m_star_image3.setBackgroundResource(R.drawable.star1);
			}
			if (num >= 4)
			{
				m_star_image4.setBackgroundResource(R.drawable.star1);
			}
			if (num >= 5)
			{
				m_star_image5.setBackgroundResource(R.drawable.star1);
			}
			m_product_scr.setText(bean.getScore() + "分");
			m_product_title.setText(bean.getTitle());
			m_product_version.setText("版本：" + bean.getVername());
			// m_product_class.
			m_product_time.setText("更新：" + mtime);
			m_product_author.setText("作者：" + dev_name);
			m_product_size.setText("大小:" + bean.getSize());
			m_product_times.setText("下载：" + bean.getDowntiems());
			m_product_language.setText("语言：" + mLang);
			m_product_new_description.setText(mdesc);
		}
	}

	/**
	 * 
	 * 
	 * 
	 * @author caoliang
	 * @version 1.0
	 * @created 2012-12-22 11:03:59
	 */
	private class ImageAdapter extends BaseAdapter
	{
		private Context mContext;
		private int mHeight = 0, mWidth = 0;

		public ImageAdapter(Context c)
		{
			this.mContext = c;
			mWidth = dip2px(c, 150);
			mHeight = dip2px(c, 250);
		}

		@Override
		public int getCount()
		{
			return m_list_bitmap.size();
		}

		@Override
		public Object getItem(int position)
		{
			return position;
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		public int dip2px(Context context, float dipValue)
		{
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (dipValue * scale + 0.5f);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			// TODO Auto-generated method stub
			DisplayMetrics displaysMetrics = new DisplayMetrics();
			((Activity) mContext).getWindowManager().getDefaultDisplay()
					.getMetrics(displaysMetrics);
			ImageView i = new ImageView(m_MainActivity);
			i.setScaleType(ImageView.ScaleType.FIT_CENTER);
			if (m_list_bitmap.get(position).getHeight() > m_list_bitmap.get(
					position).getWidth())
			{
				i.setLayoutParams(new Gallery.LayoutParams(mWidth, mHeight));
			}
			else
			{
				i.setLayoutParams(new Gallery.LayoutParams(mHeight, mWidth));
			}
			i.setImageBitmap(m_list_bitmap.get(position));
			return i;
		}
	}

	public void SetTypes()
	{
		String res = "";
		String fileName = "type"; // 文件名字
		try
		{
			InputStream in = getResources().getAssets().open(fileName); // \Test\assets\yan.txt这里有这样的文件存在
			int length = in.available();
			byte[] buffer = new byte[length];
			in.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		if (res.length() > 2)
		{
			try
			{
				JSONArray jsonObj1 = new JSONArray(res);
				for (int i = 0; i < jsonObj1.length(); i++)
				{
					JSONObject tempJson = jsonObj1.optJSONObject(i);

					if (tempJson.getInt("id") == m_product_type_num)
					{
						m_product_type = tempJson.getString("value");
						m_product_class.setText("分类：" + m_product_type);
						return;
					}
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}

	}

	private void UpdateStatus()
	{
		DictBean downing = bean.getDownloading();
		if (downing != null)
		{
			int Downing = downing.getStatus();
			switch (Downing)
			{
			case Downloader.INIT:
				// //viewHolder.mydownimage.setBackgroundResource(R.drawable.puase);
				m_down_text.setText("下载");
				break;
			case Downloader.UPDATE:// 更新
				// //viewHolder.mydownimage.setBackgroundResource(R.drawable.puase);
				m_down_text.setText("更新");
				break;
			case Downloader.INIT_SERVER:
				// viewHolder.mydownimage.setBackgroundResource(R.drawable.puase);
				m_down_text.setText("初始化");
			case Downloader.DOWNLOADING:
				// viewHolder.mydownimage.setBackgroundResource(R.drawable.puase);
				//m_down_text.setText(downing.getCompeletePercentage() + "%");
				m_down_text.setText("下载中");
				break;
			case Downloader.WAIT:
				// viewHolder.mydownimage.setBackgroundResource(R.drawable.puase);
				m_down_text.setText("等待");
				break;
			case Downloader.PAUSE:// 暂停中
				// viewHolder.mydownimage.setBackgroundResource(R.drawable.goon);
				m_down_text.setText("继续");
				break;
			case Downloader.FINISH:// 下载完成
				// viewHolder.mydownimage.setBackgroundResource(R.drawable.goon);
				PackageInfo packages;
				try
				{
					packages = mContext.getPackageManager().getPackageInfo(
							bean.getPn(), 1);
					if (packages.versionCode == bean.getVercode())
					{// 运行
						m_down_text.setText("运行");
					}
					else
					{// 安装
						m_down_text.setText("安装");
					}
				}
				catch (NameNotFoundException e)
				{
					// e.printStackTrace();
					m_down_text.setText("安装");
				}
				break;
			default:
				if (Downing >= Downloader.ERROR)
				{
					m_down_text.setText("重试");
				}
				break;
			}
		}
		else
		{
			m_down_text.setText("下载");
		}
	}

	@Override
	public void onResume()
	{
		
		app.setINotifyChanged(this);

	}

	@Override
	public void onPause()
	{
		app.setINotifyChanged(null);
	}

	@Override
	public void NotifyDataSetChanged(int cmd, Object value)
	{

	}

	@Override
	public void NotifyDataSetChanged(int cmd, Object value, int arg1, int arg2)
	{
		if (cmd == Downloader.CHANGER_STATUS)// 状态信息
		{
			SysEng.getInstance().addHandlerEvent(new AbsEvent()
			{
				@Override
				public void ok()
				{
					DictBean downing = bean.getDownloading();
					if (downing != null)
					{
						int Downing = downing.getStatus();
						switch (Downing)
						{
						case Downloader.INIT:
							// //viewHolder.mydownimage.setBackgroundResource(R.drawable.puase);
							m_down_text.setText("下载");
							break;
						case Downloader.UPDATE:// 更新
							// //viewHolder.mydownimage.setBackgroundResource(R.drawable.puase);
							m_down_text.setText("更新");
							break;
						case Downloader.INIT_SERVER:
							// viewHolder.mydownimage.setBackgroundResource(R.drawable.puase);
							m_down_text.setText("初始化");
						case Downloader.DOWNLOADING:
							// viewHolder.mydownimage.setBackgroundResource(R.drawable.puase);
//							m_down_text.setText(downing
//									.getCompeletePercentage() + "%");
							m_down_text.setText("下载中");
							break;
						case Downloader.WAIT:
							// viewHolder.mydownimage.setBackgroundResource(R.drawable.puase);
							m_down_text.setText("等待");
							break;
						case Downloader.PAUSE:// 暂停中
							// viewHolder.mydownimage.setBackgroundResource(R.drawable.goon);
							m_down_text.setText("继续");
							break;
						case Downloader.FINISH:// 下载完成
							// viewHolder.mydownimage.setBackgroundResource(R.drawable.goon);
							PackageInfo packages;
							try
							{
								packages = mContext.getPackageManager()
										.getPackageInfo(bean.getPn(), 1);
								if (packages != null
										&& packages.versionCode == bean
												.getVercode())
								{// 运行
									m_down_text.setText("运行");
								}
								else
								{// 安装
									// viewHolder.mydownimage.setBackgroundResource(R.drawable.inster);
									m_down_text.setText("安装");
								}
							}
							catch (NameNotFoundException e)
							{
								m_down_text.setText("安装");
							}
							break;
						default:
							if (Downing >= Downloader.ERROR)
							{
								m_down_text.setText("重试");
							}
							break;
						}
					}
					else
					{
						m_down_text.setText("下载");
					}
				}
			});
		}
	}
}
