package com.work.market.adapter;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.kenny.file.tools.ApkTools;
import com.work.Image.ImageLoader;
import com.work.market.bean.AppListBean;
import com.work.market.net.DictBean;
import com.work.market.net.Downloader;
import com.work.market.server.DownLoadService;

/**
 * 通用列表
 * 
 * @author WangMinghui
 * 
 */
public abstract class ObjectListAdapter extends BaseAdapter
{
	protected LayoutInflater inflater;

	protected ImageLoader mLogoImage;
	protected Context mcontext;
	protected boolean bShowLogo = true; // 是否启动加载后台图片
	protected List<AppListBean> mList;
	public void notifyDataSetChanged()
	{
		bShowLogo = true;
		super.notifyDataSetChanged();
	}

	public boolean isShowLogo()
	{
		return bShowLogo;
	}

	public void setShowLogo(boolean bShowLogo)
	{
		mLogoImage.cancel();
		this.bShowLogo = bShowLogo;

	}
	
	public ObjectListAdapter(Context context, List<AppListBean> mList)
	{

		super();
		this.mList = mList;
		mcontext = context;
		inflater = LayoutInflater.from(mcontext);
		mLogoImage = ImageLoader.GetObject(mcontext);
	}

	public int getCount()
	{
		return mList.size();
	}

	public Object getItem(int arg0)
	{
		return mList.get(arg0);

	}

	public long getItemId(int arg0)
	{
		return mList.get(arg0).getId();

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
			String url=(String)logImage.getTag();
//			Log.v("wmh", "url="+url+",imageUrl="+imageUrl
//					);
			if (url.equals(imageUrl))
			{
				logImage.setImageDrawable(imageDrawable);
			}
		}
	};

	protected class ViewHolder
	{
		View VRoot;
		ImageView logImage; // 图标
		TextView mytitle;
		TextView myver;
		ProgressBar myProgressBar;
		RatingBar tbScore;
		TextView mysize;
		TextView down_item_Rate;
		TextView mytimes;
		LinearLayout mydownLinearLayout;
		ImageView mydownimage;
		TextView mydowntext;
		TextView down_item_percentage;
	}

	public abstract View getView(final int position, View view, ViewGroup arg2);
	protected void Cancel()
	{
		
	}
	protected class OnKClickListener implements View.OnClickListener
	{
		private ViewHolder viewHolder = null;
		private AppListBean bean;
		private DictBean downing;

		public OnKClickListener(ViewHolder viewHolder, AppListBean bean)
		{
			this.viewHolder = viewHolder;
			this.bean = bean;
			this.downing = bean.getDictBean();
		}

		@Override
		public void onClick(View v)
		{
			if (downing != null)
			{
				switch (downing.getStatus())
				{
				case Downloader.INIT:
				case Downloader.UPDATE:// 更新
					bean.Start(mcontext);
					break;
				case Downloader.INIT_SERVER:
				case Downloader.DOWNLOADING:
				case Downloader.WAIT:
					// Toast.makeText(mcontext,
					// "已经加到到下载队列,请等待",Toast.LENGTH_LONG).show();
					downing.Pause();
					break;
				case Downloader.PAUSE:// 暂停中
					bean.Start(mcontext);
					break;
				case Downloader.FINISH:// 下载完成
					try
					{
						PackageInfo packages = mcontext.getPackageManager()
								.getPackageInfo(bean.getPn(), 1);
						if (packages.versionCode == bean.getVercode())
						{// 运行
//							Toast.makeText(mcontext, "运行", Toast.LENGTH_LONG)
//									.show();
							ApkTools.StartApk(mcontext, bean.getPn());
						}
						else
						{// 安装
//							Toast.makeText(mcontext, "安装", Toast.LENGTH_LONG)
//									.show();
							ApkTools.InstallApk(mcontext, downing.getFilePath());
						}
					}
					catch (Exception e)
					{
						//e.printStackTrace();
//						Toast.makeText(mcontext, "安装", Toast.LENGTH_LONG)
//								.show();
						ApkTools.InstallApk(mcontext, downing.getFilePath());
					}
					// viewHolder.mydownimage.setBackgroundResource(R.drawable.goon);
					// viewHolder.mydowntext.setText("打开");
					break;
				default:
					bean.Start(mcontext);
					break;
				}
			}
			else
			{
				bean.Start(mcontext);
				this.downing = bean.getDictBean();
			}
		}
	}
}
