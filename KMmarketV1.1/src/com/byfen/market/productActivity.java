package com.byfen.market;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.work.Image.ImageLoader;
import com.work.market.net.Common;
import com.work.market.view.ProductDetailView;
import com.work.market.view.SoftlistView;
import com.work.market.view.feedbacklistView;

public class productActivity extends Activity implements OnClickListener
{
	private Button m_description_button;// product_tab1_description
	private Button m_feedback_button;// @+id/product_tab2_feedback
	private Button m_about_button;//@+id/product_tab3_about
	private ProductDetailView m_ProductDetailView;
	private feedbacklistView m_feedbacklistView;
	private SoftlistView m_ProductaboutView;
	private String m_title = "";
	private String mLogoUrl="";//图标
	private ImageView product_logo;
	private ViewPager myViewPager;
	private MyPagerAdapter mAbsPageAdapter = new MyPagerAdapter();
	private ArrayList<View> mListViews = new ArrayList<View>();

	private void LoadLogo()
	{
		Drawable draw = null;
		draw = ImageLoader.GetObject(this).loadNetDrawable(true,
				mLogoUrl, new KImageCallback(product_logo));
		if (draw != null)
		{
			product_logo.setImageDrawable(draw);
		}
		else
		{
			product_logo.setImageResource(R.drawable.deflogo);
		}
		Bitmap tempbitmap = Getphontnames(mLogoUrl);
		if (tempbitmap != null)
		{
			Drawable drawable = (Drawable) new BitmapDrawable(tempbitmap);
			product_logo.setBackgroundDrawable(drawable);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.product_activity);

		Bundle bunde = this.getIntent().getExtras();
		String m_id = String.valueOf(bunde.getInt("id"));
		m_title = bunde.getString("title");
		mLogoUrl = bunde.getString("logo_url");
		
		findViewById(R.id.product_back).setOnClickListener(this);
		((TextView) findViewById(R.id.product_name_text)).setText(m_title);
		
		product_logo= (ImageView) findViewById(R.id.product_logo);
		myViewPager = (ViewPager) findViewById(R.id.viewpagerLayout);
		
		LoadLogo();
		m_ProductDetailView = new ProductDetailView(this, this,myViewPager);
		m_ProductDetailView.SetID(bunde.getInt("id"));//
		m_feedbacklistView = new feedbacklistView(this, this);
		m_feedbacklistView.SetUrl("http://api.byfen.com/comment/get?id=" + m_id
				+ "&", m_id);
		m_ProductaboutView = new SoftlistView(this);
		m_ProductaboutView.SetUrl("http://api.byfen.com/list/app_related?id="
				+ m_id + "&");

		mListViews.add(m_ProductDetailView);
		mListViews.add(m_feedbacklistView);
		mListViews.add(m_ProductaboutView);

		m_description_button = (Button) findViewById(R.id.product_tab1_description);
		m_description_button.setOnClickListener(this);
		m_feedback_button = (Button) findViewById(R.id.product_tab2_feedback);
		m_feedback_button.setOnClickListener(this);
		m_about_button = (Button) findViewById(R.id.product_tab3_about);
		m_about_button.setOnClickListener(this);

		m_description_button.setBackgroundResource(R.drawable.toptab_line);
		m_description_button.setTextColor(getResources()
				.getColor(R.color.green));

		m_feedback_button.setBackgroundResource(R.drawable.toptab_bg_soft);
		m_feedback_button.setTextColor(getResources().getColor(
				R.color.toptab_TextColor_normal));
		m_about_button.setBackgroundResource(R.drawable.toptab_bg_soft);
		m_about_button.setTextColor(getResources().getColor(
				R.color.toptab_TextColor_normal));
		
		
		m_feedbacklistView.Clearedittextfocus();
		m_ProductDetailView.Startget();

		myViewPager.setAdapter(mAbsPageAdapter);
		myViewPager.setCurrentItem(0);
		myViewPager.setOnPageChangeListener(new OnPageChangeListener()
		{
			public void onPageSelected(int arg0)
			{
				SwitchPage(arg0);
			}

			public void onPageScrolled(int arg0, float arg1, int arg2)
			{
				
			}

			public void onPageScrollStateChanged(int arg0)
			{
				
			}
		});
		myViewPager.setCurrentItem(0);
		SwitchPage(0);
	}

	public void SwitchPage(int index)
	{
		switch (index)
		{
		case 0:
			m_description_button.setBackgroundResource(R.drawable.toptab_line);
			m_description_button.setTextColor(getResources().getColor(
					R.color.green));// setTextColor

			m_feedback_button.setBackgroundResource(R.drawable.toptab_no_line);
			m_feedback_button.setTextColor(getResources().getColor(
					R.color.toptab_TextColor_normal));
			m_about_button.setBackgroundResource(R.drawable.toptab_no_line);
			m_about_button.setTextColor(getResources().getColor(
					R.color.toptab_TextColor_normal));
			m_feedbacklistView.SetHidesoftkey();
			m_feedbacklistView.Clearedittextfocus();
			m_ProductDetailView.onResume();
			break;
		case 1:
			m_feedback_button.setBackgroundResource(R.drawable.toptab_line);
			m_feedback_button.setTextColor(getResources().getColor(
					R.color.green));
			m_description_button
					.setBackgroundResource(R.drawable.toptab_no_line);
			m_description_button.setTextColor(getResources().getColor(
					R.color.toptab_TextColor_normal));
			m_about_button.setBackgroundResource(R.drawable.toptab_no_line);
			m_about_button.setTextColor(getResources().getColor(
					R.color.toptab_TextColor_normal));
			m_feedbacklistView.onResume();
			m_feedbacklistView.Clearedittextfocus();
			break;
		case 2:
			m_about_button.setBackgroundResource(R.drawable.toptab_line);
			m_about_button.setTextColor(getResources().getColor(R.color.green));
			m_description_button
					.setBackgroundResource(R.drawable.toptab_no_line);
			m_description_button.setTextColor(getResources().getColor(
					R.color.toptab_TextColor_normal));
			m_feedback_button.setBackgroundResource(R.drawable.toptab_no_line);
			m_feedback_button.setTextColor(getResources().getColor(
					R.color.toptab_TextColor_normal));
			m_feedbacklistView.SetHidesoftkey();
			m_ProductaboutView.onResume();
			m_feedbacklistView.Clearedittextfocus();
			break;
		}
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.product_tab1_description:
			myViewPager.setCurrentItem(0);
			break;
		case R.id.product_tab2_feedback:
			myViewPager.setCurrentItem(1);
			break;
		case R.id.product_tab3_about:
			myViewPager.setCurrentItem(2);
			break;
		case R.id.product_back:
			this.finish();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();
	}

	private class MyPagerAdapter extends PagerAdapter
	{
		public void destroyItem(View arg0, int arg1, Object arg2)
		{
			if (mListViews.size() > arg1)
			{
				((ViewPager) arg0).removeView(mListViews.get(arg1));
			}
		}

		public void finishUpdate(View arg0)
		{
		}

		public int getCount()
		{
			return mListViews.size();
		}

		public Object instantiateItem(View arg0, int arg1)
		{
			Log.v("k", "instantiateItem");
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		public boolean isViewFromObject(View arg0, Object arg1)
		{
			return arg0 == (arg1);
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1)
		{
		}

		public Parcelable saveState()
		{
			return null;
		}

		public void startUpdate(View arg0)
		{
		}

	}
	public Bitmap Getphontnames(String url)
	{
		String filename = Common.getMd5Code(url);

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
}
