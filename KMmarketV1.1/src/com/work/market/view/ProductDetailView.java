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
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.byfen.app.KApp;
import com.byfen.market.R;
import com.framework.syseng.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.file.tools.ApkTools;
import com.work.Interface.INotifyDataSetChanged;
import com.work.market.bean.AppListBean;
import com.work.market.db.DBAdapter;
import com.work.market.net.Common;
import com.work.market.net.DictBean;
import com.work.market.net.Downloader;
import com.work.market.net.HttpUtil;
import com.work.market.util.HttpUrlConst;
import com.work.market.util.T;

public class ProductDetailView extends ObjectView implements
		INotifyDataSetChanged, OnClickListener {
	private Context mContext;
	private Activity m_MainActivity;
	private ProgressDialog pd;
	private NetTask mNetTask;
	private AppListBean bean;
	private String m_url;
	private ArrayList<String> m_list_url;
	private TextView m_product_version;
	private TextView m_product_class;
	private TextView m_product_time;//
	private TextView m_product_author;//
	private TextView m_product_size;//
	private TextView m_product_times;//
	private TextView m_product_language;//
	// private TextView m_product_new_description;//

	private LinearLayout m_down_button;
	private ImageView m_Favorite_button;
	private ViewGroup detail_desc_panels;
	// private String mtime = "";//
	// private String dev_name = "";//
	// private String mdesc = "";//
	// private String mLang = "中文";//
	private ImageAdapter mImageAdapter;
	private ArrayList<Bitmap> m_list_bitmap;
	private CustomGallery mGallery;
	private TextView m_down_text;
	private String m_product_type = "";
	// private int m_product_type_num = 0;
	private String descHead = "test简介";
	private KApp app;

	public ProductDetailView(Context context, Activity aActivity,
			ViewPager mPager) {
		this(context, null, aActivity, mPager);
	}

	public Activity getActivity() {
		return m_MainActivity;
	}

	public ProductDetailView(Context context, AttributeSet attrs,
			Activity aActivity, ViewPager mPager) {
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
		// m_product_image = (ImageView)
		// findViewById(R.id.detail_soft_icon_img);

		// m_product_scr = (TextView) findViewById(R.id.detail_soft_scr);
		detail_desc_panels = (ViewGroup) findViewById(R.id.detail_desc_panels);
		m_product_version = (TextView) findViewById(R.id.detail_soft_version);
		m_product_class = (TextView) findViewById(R.id.detail_soft_class);

		m_product_time = (TextView) findViewById(R.id.detail_soft_time);
		m_product_author = (TextView) findViewById(R.id.detail_soft_author);
		m_product_size = (TextView) findViewById(R.id.detail_soft_size);
		m_product_times = (TextView) findViewById(R.id.detail_soft_down_times);
		m_product_language = (TextView) findViewById(R.id.detail_soft_language);
		// m_product_new_description = (TextView)
		// findViewById(R.id.detail_soft_description);
		m_down_text = (TextView) findViewById(R.id.detail_down_text);

		m_down_button = (LinearLayout) findViewById(R.id.betail_down_button);
		m_down_button.setOnClickListener(new OnKClickListener());
		m_Favorite_button = (ImageView) findViewById(R.id.betail_favorite_button);
		m_Favorite_button.setOnClickListener(m_Favorite_button_OnClick);

		mGallery = (CustomGallery) findViewById(R.id.detail_soft_gallery);
		mGallery.setmPager(mPager);
		mGallery.setAdapter(mImageAdapter);
		m_list_url = new ArrayList<String>();
	}

	public void SetID(int id) {
		bean.setId(id);
		m_url = HttpUrlConst.detail + id;
		UpdateStatus();
	}

	private void upDatasoft(int num, boolean apd, String url) {

		if (apd)
			pd.show();
		if (mNetTask != null) {
			mNetTask.cancel(true);
		}
		mNetTask = new NetTask(num, apd, url);
		mNetTask.execute("");
	}

	//
	class NetTask extends AsyncTask<Object, Integer, String> {

		private int m_num = 0;
		private boolean m_showlog = false;
		private String m_url;

		private NetTask(int aNUm, boolean showlog, String aurl) {
			m_num = aNUm;
			m_showlog = showlog;
			m_url = aurl;
		}

		protected String doInBackground(Object... params) {
			if (m_num == 0) {
				// adapter.title.add(keyword);
				String json = HttpUtil.doGet(m_url);// =
				return json;
			} else if (m_num == 1) {
				String filename = Common.getMd5Code(m_url);
				String data = HttpUtil.GetPhoto5(m_url, filename);//
				return data;
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result) {
			if (!pd.isShowing() && m_showlog)
				return;

			if (result == null) {
				pd.dismiss();
				String dialogstring = m_MainActivity
						.getString(R.string.net_faile);
				Toast.makeText(mContext, dialogstring, Toast.LENGTH_LONG)
						.show();
			} else {
				if (m_num == 0) {
					try {
						AppListBean group2 = JSON.parseObject(result,
								AppListBean.class);
						group2 = null;
						JSONObject jsresult = new JSONObject(result);//

						bean.setId(jsresult.getString("id"));
						bean.setTitle(jsresult.getString("title"));
						bean.setDev_name(jsresult.getString("dev_name"));
						bean.setEn_name(jsresult.getString("en_name"));
						bean.setPn(jsresult.getString("pn"));
						bean.setUpdate_time(jsresult.getString("update_time"));
						bean.setType(jsresult.getInt("type"));
						bean.setLogo(jsresult.getString("logo"));
						bean.setSize(jsresult.getString("size"));
						bean.setScore(jsresult.getString("score"));
						bean.setLang(jsresult.getString("lang"));
						bean.setDesc(jsresult.getString("desc"));
						bean.setAppurl(jsresult.getString("apkurl"));
						bean.setDowntiems(jsresult.getString("dc"));
						bean.setVername(jsresult.getString("last_update_ver"));
						bean.setAppFileExt(jsresult.getString("ext"));
						JSONArray jsonObj1 = jsresult.getJSONArray("imgs");
						for (int i = 0; i < jsonObj1.length(); i++) {
							String temps = jsonObj1.getString(i);
							m_list_url.add(temps);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					SetAllData();
					pd.dismiss();
					if (m_list_url.size() > 0) {
						int num = m_list_url.size();

						for (int i = 0; i < num; i++) {
							Bitmap tempbitmap = Getphontnames(m_list_url.get(0));
							if (tempbitmap != null) {
								m_list_bitmap.add(tempbitmap);
								m_list_url.remove(0);
							} else {
								upDatasoft(1, false, m_list_url.get(0));
								return;
							}
						}
						mImageAdapter.notifyDataSetChanged();
					}
					boolean isFavorite = DBAdapter.createDBAdapter(mContext)
							.queryMessageById(String.valueOf(bean.getId()),
									Common.user_key);

					if (isFavorite) {
						m_Favorite_button
								.setImageResource(R.drawable.detail_favorite);
					} else {
						m_Favorite_button
								.setImageResource(R.drawable.detail_unfavorite);
					}
				} else if (m_num == 1) {
					if (m_list_url.size() > 0) {
						int num = m_list_url.size();

						for (int i = 0; i < num; i++) {
							Bitmap tempbitmap = Getphontnames(m_list_url.get(0));
							if (tempbitmap != null) {
								m_list_bitmap.add(tempbitmap);
								m_list_url.remove(0);
							} else {
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

	public Bitmap Getphontnames(String url) {
		String filename = Common.getMd5Code(url);

		String path = Environment.getExternalStorageDirectory().toString()
				+ "/baifen/img/" + filename;
		File file1 = new File(path);
		if (file1.exists()) {
			try {
				FileInputStream fis = new FileInputStream(path);
				return BitmapFactory.decodeStream(fis);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	public void Startget() {
		upDatasoft(0, true, m_url);
	}

	private void SetAllData() {
		if (bean != null) {
			UpdateStatus();

			detail_desc_panels.removeAllViews();
			if (bean.getDesc() != null && bean.getDesc().length() > 0) {
				View view = getActivity().getLayoutInflater().inflate(
						R.layout.detail_subdesc, null);// 添加list
				((TextView) view.findViewById(R.id.detail_subdesc_title))
						.setText(descHead);
				((TextView) view.findViewById(R.id.detail_subdesc_Text))
						.setText(bean.getDesc());
				detail_desc_panels.addView(view);
			}

			m_product_class.setText("分类："
					+ T.getGroupName(getActivity(), bean.getType()));
			m_product_version.setText("版本：" + bean.getVername());
			m_product_time.setText("更新：" + bean.getUpdate_time());
			m_product_author.setText("作者：" + bean.getDev_name());
			m_product_size.setText("大小:" + bean.getSize());
			m_product_times.setText("下载：" + bean.getDowntiems());
			m_product_language.setText("语言：" + bean.getLang());

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
	private class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private int mHeight = 0, mWidth = 0;

		public ImageAdapter(Context c) {
			this.mContext = c;
			mWidth = dip2px(c, 150);
			mHeight = dip2px(c, 250);
		}

		@Override
		public int getCount() {
			return m_list_bitmap.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public int dip2px(Context context, float dipValue) {
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (dipValue * scale + 0.5f);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			DisplayMetrics displaysMetrics = new DisplayMetrics();
			((Activity) mContext).getWindowManager().getDefaultDisplay()
					.getMetrics(displaysMetrics);
			ImageView i = new ImageView(m_MainActivity);
			i.setScaleType(ImageView.ScaleType.FIT_CENTER);
			if (m_list_bitmap.get(position).getHeight() > m_list_bitmap.get(
					position).getWidth()) {
				i.setLayoutParams(new Gallery.LayoutParams(mWidth, mHeight));
			} else {
				i.setLayoutParams(new Gallery.LayoutParams(mHeight, mWidth));
			}
			i.setImageBitmap(m_list_bitmap.get(position));
			return i;
		}
	}

	private void UpdateStatus() {
		DictBean downing = bean.getDownloading();
		if (downing != null) {
			int Downing = downing.getStatus();
			switch (Downing) {
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
				// m_down_text.setText(downing.getCompeletePercentage() + "%");
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
				try {
					packages = mContext.getPackageManager().getPackageInfo(
							bean.getPn(), 1);
					if (packages.versionCode == bean.getVercode()) {// 运行
						m_down_text.setText("运行");
					} else {// 安装
						m_down_text.setText("安装");
					}
				} catch (NameNotFoundException e) {
					// e.printStackTrace();
					m_down_text.setText("安装");
				}
				break;
			default:
				if (Downing >= Downloader.ERROR) {
					m_down_text.setText("重试");
				}
				break;
			}
		} else {
			m_down_text.setText("下载");
		}
	}

	@Override
	public void onResume() {

		app.setINotifyChanged(this);

	}

	@Override
	public void onPause() {
		app.setINotifyChanged(null);
	}

	@Override
	public void NotifyDataSetChanged(int cmd, Object value) {

	}

	@Override
	public void NotifyDataSetChanged(int cmd, Object value, int arg1, int arg2) {
		if (cmd == Downloader.CHANGER_STATUS)// 状态信息
		{
			SysEng.getInstance().addHandlerEvent(new AbsEvent() {
				@Override
				public void ok() {
					DictBean downing = bean.getDownloading();
					if (downing != null) {
						int Downing = downing.getStatus();
						switch (Downing) {
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
							// m_down_text.setText(downing
							// .getCompeletePercentage() + "%");
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
							try {
								packages = mContext.getPackageManager()
										.getPackageInfo(bean.getPn(), 1);
								if (packages != null
										&& packages.versionCode == bean
												.getVercode()) {// 运行
									m_down_text.setText("运行");
								} else {// 安装
										// viewHolder.mydownimage.setBackgroundResource(R.drawable.inster);
									m_down_text.setText("安装");
								}
							} catch (NameNotFoundException e) {
								m_down_text.setText("安装");
							}
							break;
						default:
							if (Downing >= Downloader.ERROR) {
								m_down_text.setText("重试");
							}
							break;
						}
					} else {
						m_down_text.setText("下载");
					}
				}
			});
		}
	}

	protected class OnKClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			DictBean downing = bean.getDictBean();
			if (downing != null) {
				switch (downing.getStatus()) {
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
					try {
						PackageInfo packages = mContext.getPackageManager()
								.getPackageInfo(bean.getPn(), 1);
						if (packages.versionCode == bean.getVercode()) {// 运行
							Toast.makeText(mContext, "运行", Toast.LENGTH_LONG)
									.show();
							ApkTools.StartApk(mContext, bean.getPn());
						} else {// 安装
							Toast.makeText(mContext, "安装", Toast.LENGTH_LONG)
									.show();
							ApkTools.InstallApk(mContext, downing.getFilePath());
						}
					} catch (Exception e) {
						// e.printStackTrace();
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
			} else {
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

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	OnClickListener m_Favorite_button_OnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			boolean result = DBAdapter.createDBAdapter(mContext)
					.queryMessageById(String.valueOf(bean.getId()),
							Common.user_key);

			if (!result) {
				boolean aa = DBAdapter.createDBAdapter(mContext).insertMessage(
						bean.getId(), bean.getPn(), bean.getTitle(),
						Common.user_key);
				String dialogstring;
				if (aa) {
					dialogstring = "收藏成功";// m_MainActivity.getString(R.string.net_faile);
					m_Favorite_button
							.setImageResource(R.drawable.detail_favorite);
				} else {
					dialogstring = "收藏失败";// m_MainActivity.getString(R.string.net_faile);
				}
				Toast.makeText(mContext, dialogstring, Toast.LENGTH_LONG)
						.show();
			} else {
				// 提示已经收藏过
				String dialogstring = "取消收藏";
				boolean aa = DBAdapter.createDBAdapter(mContext)
						.deleteMessageById(bean.getId(), Common.user_key);
				m_Favorite_button
						.setImageResource(R.drawable.detail_unfavorite);
				Toast.makeText(mContext, dialogstring, Toast.LENGTH_LONG)
						.show();
			}
		}
	};
}
