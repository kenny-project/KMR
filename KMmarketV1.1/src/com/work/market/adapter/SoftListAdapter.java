package com.work.market.adapter;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.byfen.market.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.work.market.bean.AppListBean;
import com.work.market.net.DictBean;
import com.work.market.net.Downloader;
/**
 * 通用列表
 * @author WangMinghui
 *
 */
public class SoftListAdapter extends ObjectListAdapter{
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	public SoftListAdapter(Context context, List<AppListBean> mList) {
		super(context,mList);
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.deflogo)
		.showStubImage(R.drawable.deflogo)
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
		.cacheInMemory().cacheOnDisc().build();
	}
	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = inflater.inflate(R.layout.list_softitem, null);
			viewHolder.logImage = (ImageView) view
					.findViewById(R.id.soft_item_image);
			viewHolder.mytitle = (TextView) view
					.findViewById(R.id.soft_name_text);
			viewHolder.tbScore = (RatingBar) view.findViewById(R.id.tbScore);
			viewHolder.mysize = (TextView) view
					.findViewById(R.id.soft_size_text);
			viewHolder.mytimes = (TextView) view
					.findViewById(R.id.soft_times_text);
			viewHolder.mydownLinearLayout = (LinearLayout) view
					.findViewById(R.id.soft_down_lay);
			viewHolder.mydownimage = (ImageView) view
					.findViewById(R.id.soft_down_image);
			viewHolder.mydowntext = (TextView) view
					.findViewById(R.id.soft_down_text);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		final AppListBean bean = mList.get(position);
//		Drawable draw = null;
//		viewHolder.logImage.setTag(bean.getLogourl());
//		draw = mLogoImage.loadNetDrawable(bShowLogo, bean.getLogourl(),
//				new KImageCallback(viewHolder.logImage));
//		if (draw != null) 
//		{
//			bean.setLogo(draw);
//			viewHolder.logImage.setImageDrawable(draw);
//		} else {
//			viewHolder.logImage.setImageResource(R.drawable.deflogo);
//		}
		imageLoader.displayImage(bean.getLogourl(),
				viewHolder.logImage, options);
		viewHolder.mytitle.setText(bean.getTitle());
		viewHolder.tbScore.setRating(bean.getScore());
		viewHolder.mysize.setText(bean.getSize());
		viewHolder.mytimes.setText(bean.getDowntiems()
				+ mcontext.getString(R.string.soft_down_times));
		DictBean downing = bean.getDownloading();
		if (downing != null) 
		{
			int Downing = downing.getStatus();
			switch (Downing) {
			case Downloader.INIT:
				viewHolder.mydownimage.setImageResource(R.drawable.list_down);
				viewHolder.mydowntext.setText("下载");
				break;
			case Downloader.UPDATE://更新
				viewHolder.mydownimage.setImageResource(R.drawable.list_pause);
				viewHolder.mydowntext.setText("更新");
				break;
			case Downloader.INIT_SERVER:
				viewHolder.mydownimage.setImageResource(R.drawable.list_pause);
				viewHolder.mydowntext.setText("初始化");
			case Downloader.DOWNLOADING:
				viewHolder.mydownimage.setImageResource(R.drawable.list_pause);
				//viewHolder.mydowntext.setText(downing.getCompeletePercentage()+"%");
				viewHolder.mydowntext.setText("下载中");
				break;
			case Downloader.WAIT:
				viewHolder.mydownimage.setImageResource(R.drawable.list_pause);
				viewHolder.mydowntext.setText("等待");
				break;
			case Downloader.PAUSE:// 暂停中
				viewHolder.mydownimage.setImageResource(R.drawable.list_down);
				viewHolder.mydowntext.setText("继续");
				break;
			case Downloader.FINISH:// 下载完成
				PackageInfo packages;
				try
				{
					packages = mcontext.getPackageManager().getPackageInfo(bean.getPn(),1);
					if(packages.versionCode<bean.getVercode())
					{//运行
						viewHolder.mydownimage.setImageResource(R.drawable.list_pause);
						viewHolder.mydowntext.setText("更新");
						downing.setState(Downloader.UPDATE);
					}
					else
					 if(packages.versionCode==bean.getVercode())
					{//运行
						viewHolder.mydownimage.setImageResource(R.drawable.list_open);
						viewHolder.mydowntext.setText("运行");
					}
					else
					{//安装
						viewHolder.mydownimage.setImageResource(R.drawable.list_install);
						viewHolder.mydowntext.setText("安装");
					}
				}
				catch (Exception e)
				{
					//e.printStackTrace();
					viewHolder.mydownimage.setImageResource(R.drawable.list_install);
					viewHolder.mydowntext.setText("安装");
				}
				break;
			default:
				if (Downing >= Downloader.ERROR) {
					viewHolder.mydownimage
							.setImageResource(R.drawable.list_down);
					viewHolder.mydowntext.setText("重试");
				}
				break;
			}
		} else {
			viewHolder.mydownimage.setImageResource(R.drawable.list_down);
			viewHolder.mydowntext.setText("下载");
		}
		viewHolder.mydownLinearLayout.setOnClickListener(new OnKClickListener(
				viewHolder, bean));
		return view;
	}
}
