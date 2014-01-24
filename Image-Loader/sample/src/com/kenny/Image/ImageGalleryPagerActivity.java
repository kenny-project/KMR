/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.kenny.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kenny.imgviewer.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class ImageGalleryPagerActivity extends Activity {

	private static final String STATE_POSITION = "STATE_POSITION";
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;
	private List<String> imageUrls=null;
	private ViewPager pager;
	private Gallery gallery;
	private int pagerPosition=0;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_image_gallery_pager);

		Bundle bundle = getIntent().getExtras();
		assert bundle != null;
//		imageUrls = bundle.getStringArray(Extra.IMAGES);
//		int pagerPosition = bundle.getInt(Extra.IMAGE_POSITION, 0);

		// 通过Intent得到GridView传过来的图片位置
		Intent intent = getIntent();
		Uri uri = (Uri) intent.getData();
		String path = null;
		if (uri != null)
		{
			path = uri.getPath();
		}
		if (path == null)
		{
			Log.v("tag", path);
			path=android.os.Environment
					.getExternalStorageDirectory().getAbsolutePath();			
		} 
		imageUrls=setFilePath(path);

		options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.resetViewBeforeLoading(true)
			.cacheOnDisc(true)
			.imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.considerExifParams(true)
			.displayer(new FadeInBitmapDisplayer(300))
			.build();

		pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(new ImagePagerAdapter(imageUrls));
		pager.setCurrentItem(pagerPosition);
		pager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				gallery.setSelection(arg0);
				ImageGalleryPagerActivity.this.setTitle(imageUrls.get(arg0));
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		gallery = (Gallery) findViewById(R.id.gallery);
		gallery.setAdapter(new ImageGalleryAdapter());
		gallery.setSelection(pagerPosition);
		gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startImagePagerActivity(position);
			}
		});
		
//		Intent intent = new Intent();
//		intent.setClassName("com.kenny.KFileManager", "com.kenny.file.Activity.SettingPage");
//		startActivity(intent);
	}
	private static final String TAG="wmh";
	public List<String> setFilePath(String path)
	{
		File file=new File(path);
		String SelectFileName=null;
		if(!file.isDirectory())
		{
			SelectFileName=file.getName();
		}
		String mCurrentPath=file.getParent();
		Log.v(TAG,"setFilePath:start");
		ArrayList<String> mFileList=new ArrayList<String>();
		File mFile = new File(mCurrentPath);
		String [] mFiles = mFile.list();// 遍历出该文件夹路径下的所有文件/文件夹
		if (mFiles != null)
		{
			/* 将所有文件信息添加到集合中 */
			for (String mCurrentFile : mFiles)
			{
				if (mCurrentFile.length()<5)
				{
					continue;
				}
				String fileEnds = mCurrentFile.substring(mCurrentFile.length()-4);
				if (fileEnds.equals(".jpg") || fileEnds.equals(".gif")
						|| fileEnds.equals(".png") || fileEnds.equals(".jpeg")
						|| fileEnds.equals(".bmp"))
				{
					if (SelectFileName!=null&&mCurrentFile.equals(SelectFileName))
					{
						pagerPosition = mFileList.size();
					}
					mFileList.add("file://"+mCurrentPath+"//"+mCurrentFile);
				}
			}
//			Collections.sort(mFileList, new FileSort());
		}

		Log.v(TAG,"setFilePath:end");
		return mFileList;
	}
	
//	private int LoadImageList()
//	{
//		mImageList.clear();
//		List<FileBean> mFileList = FileManager.getInstance().getFileList();
//		for (int i = 0; i < mFileList.size(); i++)
//		{
//			FileBean bean = mFileList.get(i);
//
//			String fileEnds = bean.getFileEnds();
//			if (fileEnds.equals("jpg") || fileEnds.equals("gif")
//					|| fileEnds.equals("png") || fileEnds.equals("jpeg")
//					|| fileEnds.equals("bmp"))
//			{
//				if (bean.getFilePath().equals(path))
//				{
//					i_position = mImageList.size();
//				}
//				mImageList.add(bean);
//			}
//		}
//		Log.v(TAG,"i_position=" + i_position);
//		g.setSelection(i_position);
//		ia.notifyDataSetChanged();
//	}
	private void startImagePagerActivity(int position) {
//		Intent intent = new Intent(this, ImagePagerActivity.class);
//		intent.putExtra(Extra.IMAGES, imageUrls);
//		intent.putExtra(Extra.IMAGE_POSITION, position);
//		startActivity(intent);
		pager.setCurrentItem(position);
	}
	
	private class ImageGalleryAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return imageUrls.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = (ImageView) convertView;
			if (imageView == null) {
				imageView = (ImageView) getLayoutInflater().inflate(R.layout.item_gallery_image, parent, false);
			}
			imageLoader.displayImage(imageUrls.get(position), imageView, options);
			return imageView;
		}
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

	private class ImagePagerAdapter extends PagerAdapter {

		private List<String> images;
		private LayoutInflater inflater;

		ImagePagerAdapter(List<String> images) {
			this.images = images;
			inflater = getLayoutInflater();
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public int getCount() {
			return images.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
			assert imageLayout != null;
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

			imageLoader.displayImage(images.get(position), imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					String message = null;
					switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
					}
					Toast.makeText(ImageGalleryPagerActivity.this, message, Toast.LENGTH_SHORT).show();

					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);
				}
			});

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}
}