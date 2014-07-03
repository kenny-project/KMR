package com.work.market.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.byfen.market.R;
import com.byfen.market.productActivity;
import com.work.market.bean.AppListBean;
import com.work.market.net.DictBean;
import com.work.market.net.Downloader;
import com.work.market.server.DownLoadService;

public class DownLoadedAdapter extends ObjectListAdapter
{
	private DownLoadService service;
	public DownLoadedAdapter(Context context, List<AppListBean> mList,DownLoadService service)
	{
		super(context, mList);
		this.service=service;
	}

	class CViewHolder extends ViewHolder
	{
		View lySecndPanel;
		View btDeail;
		View btDelete;
	}

	public View getView(final int position, View view, ViewGroup arg2)
	{
		final CViewHolder viewHolder;
		if (view == null)
		{
			view = inflater.inflate(R.layout.list_finishitem, null);
			viewHolder = new CViewHolder();
			viewHolder.logImage = (ImageView) view
					.findViewById(R.id.dowmn_item_image);
			viewHolder.mytitle = (TextView) view
					.findViewById(R.id.down_item_title_text);
			viewHolder.mysize = (TextView) view
					.findViewById(R.id.down_item_size);
			viewHolder.mydownLinearLayout = (LinearLayout) view
					.findViewById(R.id.down_item_layout);
			viewHolder.mydownimage = (ImageView) view
					.findViewById(R.id.down_item_donwn_button_image);
			viewHolder.mydowntext = (TextView) view
					.findViewById(R.id.down_item_donwn_text_image);
			viewHolder.btDeail = view.findViewById(R.id.btDeail);
			viewHolder.VRoot =  view
					.findViewById(R.id.VRoot);
			viewHolder.btDelete = view.findViewById(R.id.btDelete);
			viewHolder.lySecndPanel = view.findViewById(R.id.lySecndPanel);
			view.setTag(viewHolder);
		}
		else
		{
			viewHolder = (CViewHolder) view.getTag();
		}
		final AppListBean bean = mList.get(position);
		if (bean.iSpread())
		{
			viewHolder.lySecndPanel.setVisibility(View.VISIBLE);
		}
		else
		{
			viewHolder.lySecndPanel.setVisibility(View.GONE);
		}
		Drawable draw = null;
		viewHolder.logImage.setTag(bean.getLogourl());
		draw = mLogoImage.loadNetDrawable(bShowLogo, bean.getLogourl(),
				new KImageCallback(viewHolder.logImage));
		if (draw != null)
		{
			viewHolder.logImage.setImageDrawable(draw);
		}
		else
		{
			viewHolder.logImage.setTag(bean.getLogourl());
			viewHolder.logImage.setImageResource(R.drawable.deflogo);
		}
		viewHolder.mytitle.setText(bean.getTitle());
		viewHolder.mysize.setText(bean.getSize());
		DictBean downing = bean.getDownloading();
		if (downing != null)
		{
			int Downing = downing.getStatus();
			switch (Downing)
			{
			case Downloader.INIT:
				viewHolder.mydownimage.setImageResource(R.drawable.list_down);
				viewHolder.mydowntext.setText("下载");
				break;
			case Downloader.UPDATE:// 更新
				viewHolder.mydownimage.setImageResource(R.drawable.list_pause);
				viewHolder.mydowntext.setText("更新");
				break;
			case Downloader.INIT_SERVER:
				viewHolder.mydownimage.setImageResource(R.drawable.list_pause);
				viewHolder.mydowntext.setText("初始化");
			case Downloader.DOWNLOADING:
				viewHolder.mydownimage.setImageResource(R.drawable.list_pause);
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
					packages = mcontext.getPackageManager().getPackageInfo(
							bean.getPn(), 1);
					if (packages.versionCode == bean.getVercode())
					{// 运行
						viewHolder.mydownimage
								.setImageResource(R.drawable.list_open);
						viewHolder.mydowntext.setText("运行");
					}
					else
					{// 安装
						viewHolder.mydownimage
								.setImageResource(R.drawable.list_install);
						viewHolder.mydowntext.setText("安装");
					}
				}
				catch (NameNotFoundException e)
				{
					//e.printStackTrace();
					viewHolder.mydownimage
							.setImageResource(R.drawable.list_install);
					viewHolder.mydowntext.setText("安装");
				}
				break;
			default:
				if (Downing >= Downloader.ERROR)
				{
					viewHolder.mydownimage
							.setImageResource(R.drawable.list_down);
					viewHolder.mydowntext.setText("重试");
				}
				break;
			}
		}
		else
		{
			viewHolder.mydownimage.setImageResource(R.drawable.list_down);
			viewHolder.mydowntext.setText("下载");
		}
		viewHolder.mydownLinearLayout.setOnClickListener(new OnKClickListener(
				viewHolder, bean));
		viewHolder.VRoot
				.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						bean.setSpread(!bean.iSpread());
						if (bean.iSpread())
						{
							viewHolder.lySecndPanel.setVisibility(View.VISIBLE);
						}
						else
						{
							viewHolder.lySecndPanel.setVisibility(View.GONE);
						}
					}
				});
		viewHolder.btDelete.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				bean.Delete();
				DictBean downing = bean.getDownloading();
				if (downing != null)
				{
					downing.Delete();
				}
				service.DelDownLoaded(position);
			}
		});
		viewHolder.btDeail.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				Intent seta = new Intent(mcontext, productActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("title", bean.getTitle());
				bundle.putInt("id", bean.getId());
				seta.putExtras(bundle);
				mcontext.startActivity(seta);
			}
		});
		return view;
	}
}
