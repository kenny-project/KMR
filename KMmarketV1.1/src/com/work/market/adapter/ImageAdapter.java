package com.work.market.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.byfen.market.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * 
 * 
 * 
 * @author caoliang
 * @version 1.0
 * @created 2012-12-22 11:03:59
 */
public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private List<String> imgUrls;
	private LayoutInflater inflater;
	public ImageAdapter(Context c, List<String> imgUrls) {
		this.mContext = c;
		this.imgUrls = imgUrls;
		options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.deflogo)
				.showStubImage(R.drawable.deflogo)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.cacheOnDisc()  
				//.bitmapConfig(Bitmap.Config.RGB_565)// 设置下载的图片是否缓存在SD卡中 
				.cacheInMemory().build();
		 inflater = LayoutInflater.from(mContext);
//		imageLoader.init(configuration)
//		ImageLoaderConfiguration con=new ImageLoaderConfiguration();
		
	}

	@Override
	public int getCount() {
		return imgUrls.size();
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

		ImageView imageView = (ImageView) convertView;
		if (imageView == null) {
			imageView = (ImageView) inflater.inflate(R.layout.list_image, parent, false);
		}
		
//		ImageView image = new ImageView(mContext);
//		image.setLayoutParams(new Gallery.LayoutParams(mWidth, mHeight));
//		image.setScaleType(ImageView.ScaleType.FIT_CENTER);

		imageLoader.displayImage(imgUrls.get(position), imageView, options,
				new KImageLoadingListener(imageView));
//		imageLoader.loadImage(mContext, imgUrls.get(position), options,new KImageLoadingListener(imageView));
		// if (m_list_bitmap.get(position).getHeight() > m_list_bitmap.get(
		// position).getWidth()) {
		// image.setLayoutParams(new Gallery.LayoutParams(mWidth, mHeight));
		// } else {
		// image.setLayoutParams(new Gallery.LayoutParams(mHeight, mWidth));
		// }
		// image.setImageBitmap(m_list_bitmap.get(position));
		return imageView;
	}

	private class KImageLoadingListener implements ImageLoadingListener {
		private ImageView image;

		public KImageLoadingListener(ImageView image) {
			this.image = image;
		}

		@Override
		public void onLoadingStarted() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLoadingFailed(FailReason failReason) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onLoadingComplete(Bitmap loadedImage) {
			// TODO Auto-generated method stub
			Log.v("wmh", "onLoadingComplete");
//			if (loadedImage.getHeight() > loadedImage.getWidth()) {
//				image.setLayoutParams(new Gallery.LayoutParams(mWidth, mHeight));
//			} else {
//				image.setLayoutParams(new Gallery.LayoutParams(mHeight, mWidth));
//			}
			//image.setImageBitmap(loadedImage);
		}

		@Override
		public void onLoadingCancelled() {
			// TODO Auto-generated method stub

		}
	};
}