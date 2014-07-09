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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byfen.market.R;
import com.byfen.market.productActivity;
import com.work.market.bean.AppListBean;
import com.work.market.net.DictBean;
import com.work.market.net.Downloader;
import com.work.market.server.DownLoadService;

public class UpdateAdapter extends ObjectListAdapter
{
	private DownLoadService service;
	public UpdateAdapter(Context context, List<AppListBean> mList,DownLoadService service)
	{
		super(context, mList);
		this.service=service;
	}
	public class updateHolder extends ViewHolder
	{
		TextView listitem_explain_text;
		ImageView listitem_explain_img;
	}
	
	public View getView(final int position, View view, ViewGroup arg2)
	{
		final updateHolder viewHolder;
		if (view == null)
		{
			viewHolder = new updateHolder();
			view = inflater.inflate(R.layout.list_updateitem, null);
			viewHolder.logImage = (ImageView) view
					.findViewById(R.id.new_item_image);
			
			viewHolder.listitem_explain_img = (ImageView) view
					.findViewById(R.id.listitem_explain_img);
			
			viewHolder.mytitle = (TextView) view
					.findViewById(R.id.new_item_text1);
			viewHolder.myver = (TextView) view
					.findViewById(R.id.new_item_text2);
			viewHolder.mysize = (TextView) view
					.findViewById(R.id.new_item_text3);

			viewHolder.mydownLinearLayout = (LinearLayout) view
					.findViewById(R.id.new_item_layout);
			viewHolder.mydownimage = (ImageView) view
					.findViewById(R.id.new_item_item_images);// @+id/new_item_item_images
			viewHolder.mydowntext = (TextView) view
					.findViewById(R.id.new_item_item_text);
			
			viewHolder.listitem_explain_text = (TextView) view
			.findViewById(R.id.listitem_explain_text);
			view.setTag(viewHolder);
		}
		else
		{
			viewHolder = (updateHolder) view.getTag();
		}

		final AppListBean bean = mList.get(position);
		{
			Drawable draw = null;
			viewHolder.logImage.setTag(bean.getLogo());
			draw = mLogoImage.loadNetDrawable(bShowLogo, bean.getLogo(),
					new KImageCallback(viewHolder.logImage));
			if (draw != null)
			{
				viewHolder.logImage.setImageDrawable(draw);
			}
			else
			{
				viewHolder.logImage.setImageResource(R.drawable.deflogo);
			}
			
			if(bean.isbExplainVisible())
			{
				viewHolder.listitem_explain_img.setBackgroundResource(R.drawable.update_explain_img_open);
				viewHolder.listitem_explain_text.setMaxLines(1);
			}
			else
			{
				viewHolder.listitem_explain_img.setBackgroundResource(R.drawable.update_explain_img_open);
				viewHolder.listitem_explain_text.setMaxLines(100);
			}

			viewHolder.mytitle.setText(bean.getTitle());
			viewHolder.myver.setText("�汾��" + bean.getVername());
			viewHolder.mysize.setText(bean.getSize());
			viewHolder.listitem_explain_text.setText(bean.getDesc()); 
			DictBean downing = bean.getDownloading();
			if (downing != null)
			{
				int Downing = downing.getStatus();
				switch (Downing)
				{
				case Downloader.INIT:
					viewHolder.mydownimage
							.setImageResource(R.drawable.list_update);
					viewHolder.mydowntext.setText("����");
					break;
				case Downloader.UPDATE:// ����
					viewHolder.mydownimage
							.setImageResource(R.drawable.list_pause);
					viewHolder.mydowntext.setText("����");
					break;
				case Downloader.INIT_SERVER:
					viewHolder.mydownimage
							.setImageResource(R.drawable.list_pause);
					viewHolder.mydowntext.setText("��ʼ��");
				case Downloader.DOWNLOADING:
					viewHolder.mydownimage
							.setImageResource(R.drawable.list_pause);
					viewHolder.mydowntext.setText("������");
					break;
				case Downloader.WAIT:
					viewHolder.mydownimage
							.setImageResource(R.drawable.list_pause);
					viewHolder.mydowntext.setText("�ȴ�");
					break;
				case Downloader.PAUSE:// ��ͣ��
					viewHolder.mydownimage
							.setImageResource(R.drawable.list_down);
					viewHolder.mydowntext.setText("����");
					break;
				case Downloader.FINISH:// �������
					PackageInfo packages;
					try
					{
						if(!downing.getUrl().equals(bean.getAppurl()))
						{
							bean.Delete();
							if (downing != null)
							{
								downing.Delete();
							}
							service.SelectDelDownLoadedItem(bean.getId());
							viewHolder.mydowntext.setText("����");
							bean.setDownloading(null);
						}
						else
						{
						packages = mcontext.getPackageManager().getPackageInfo(bean.getPn(),1);
						if(packages.versionCode==bean.getVercode())
						{//����
							viewHolder.mydownimage.setImageResource(R.drawable.list_open);
							viewHolder.mydowntext.setText("����");
						}
						else
						{//��װ
							viewHolder.mydownimage.setImageResource(R.drawable.list_install);
							viewHolder.mydowntext.setText("��װ");
						}
						}
					}
					catch (NameNotFoundException e)
					{
						e.printStackTrace();
						viewHolder.mydownimage.setImageResource(R.drawable.list_install);
						viewHolder.mydowntext.setText("��װ");
					}
					break;
				default:
					if (Downing >= Downloader.ERROR)
					{
						viewHolder.mydownimage
								.setImageResource(R.drawable.list_down);
						viewHolder.mydowntext.setText("����");
					}
					break;
				}
			}
			else
			{
				viewHolder.mydownimage.setImageResource(R.drawable.list_update);
				viewHolder.mydowntext.setText("����");
			}
			viewHolder.mydownLinearLayout
					.setOnClickListener(new OnKClickListener(viewHolder, bean));
			// @+id/newitem_RelativeLayout
			final RelativeLayout RelativeLayout1 = (RelativeLayout) view
					.findViewById(R.id.newitem_RelativeLayout);
			RelativeLayout1.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent seta = new Intent(mcontext, productActivity.class);
					Bundle bundle = new Bundle();
					String dialogstring = bean.getTitle();
					bundle.putString("title", dialogstring);
					dialogstring = bean.getPn();
					bundle.putString("pn", dialogstring);
					int tempID = bean.getId();
					bundle.putInt("id", tempID);
					seta.putExtras(bundle);
					mcontext.startActivity(seta);
				}
			});
			
			viewHolder.listitem_explain_text.setOnClickListener(new OnClickListener()
			{
				public void onClick(View arg0)
				{
					if(bean.isbExplainVisible())
					{
						bean.setbExplainVisible(false);
						viewHolder.listitem_explain_text.setSingleLine(false);
					}
					else
					{
						bean.setbExplainVisible(true);
						viewHolder.listitem_explain_text.setSingleLine(true);	
					}
				}
			});
		}
		return view;
	}
}
