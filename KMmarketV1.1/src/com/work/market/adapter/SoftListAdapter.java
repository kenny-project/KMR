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
 * ͨ���б�
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
		imageLoader.displayImage(bean.getLogo(),
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
				viewHolder.mydowntext.setText("����");
				break;
			case Downloader.UPDATE://����
				viewHolder.mydownimage.setImageResource(R.drawable.list_pause);
				viewHolder.mydowntext.setText("����");
				break;
			case Downloader.INIT_SERVER:
				viewHolder.mydownimage.setImageResource(R.drawable.list_pause);
				viewHolder.mydowntext.setText("��ʼ��");
			case Downloader.DOWNLOADING:
				viewHolder.mydownimage.setImageResource(R.drawable.list_pause);
				//viewHolder.mydowntext.setText(downing.getCompeletePercentage()+"%");
				viewHolder.mydowntext.setText("������");
				break;
			case Downloader.WAIT:
				viewHolder.mydownimage.setImageResource(R.drawable.list_pause);
				viewHolder.mydowntext.setText("�ȴ�");
				break;
			case Downloader.PAUSE:// ��ͣ��
				viewHolder.mydownimage.setImageResource(R.drawable.list_down);
				viewHolder.mydowntext.setText("����");
				break;
			case Downloader.FINISH:// �������
				PackageInfo packages;
				try
				{
					packages = mcontext.getPackageManager().getPackageInfo(bean.getPn(),1);
					if(packages.versionCode<bean.getVercode())
					{//����
						viewHolder.mydownimage.setImageResource(R.drawable.list_pause);
						viewHolder.mydowntext.setText("����");
						downing.setState(Downloader.UPDATE);
					}
					else
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
				catch (Exception e)
				{
					//e.printStackTrace();
					viewHolder.mydownimage.setImageResource(R.drawable.list_install);
					viewHolder.mydowntext.setText("��װ");
				}
				break;
			default:
				if (Downing >= Downloader.ERROR) {
					viewHolder.mydownimage
							.setImageResource(R.drawable.list_down);
					viewHolder.mydowntext.setText("����");
				}
				break;
			}
		} else {
			viewHolder.mydownimage.setImageResource(R.drawable.list_down);
			viewHolder.mydowntext.setText("����");
		}
		viewHolder.mydownLinearLayout.setOnClickListener(new OnKClickListener(
				viewHolder, bean));
		return view;
	}
}
