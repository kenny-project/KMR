package com.kenny.KImageBrowser;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.log.P;
import com.framework.util.Const;
import com.kenny.KFileManager.R;
import com.kenny.file.bean.FileBean;
import com.kenny.file.manager.FileManager;
import com.kenny.file.tools.T;
import com.kenny.file.util.SDFile;

/**
 * 
 * @author 空山不空 Gallery图片页面，通过Intent得到GridView传过来的图片位置，加载图片，再设置适配器
 */
public class GalleryActivity extends Activity implements OnItemClickListener,
		OnClickListener, OnItemSelectedListener
{
	private int i_position = 0;
	private DisplayMetrics dm;
	private ImageAdapter ia;
	private List<FileBean> mImageList = new ArrayList<FileBean>();
	private String path;
	private GalleryExt g;
	private ImageView ivImage;
	private TextView gaStatus;
	private View rlImage, rlGallery;
	private OnKTouchListener mTouchListener = new OnKTouchListener();
	private int mFlag = 1; // 1:list
	private FileBean mFileBean; // 当被选中的文件
	// 2:image
	private Button btShare, btRotate, btDelete;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.galleryactivity);
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		rlImage = findViewById(R.id.rlImage);
		btShare = (Button) findViewById(R.id.btShare);
		btRotate = (Button) findViewById(R.id.btRotate);
		btDelete = (Button) findViewById(R.id.btDelete);
		btShare.setOnClickListener(this);
		btRotate.setOnClickListener(this);
		btDelete.setOnClickListener(this);
		rlGallery = findViewById(R.id.rlGallery);
		ivImage = (ImageView) findViewById(R.id.ivImage);
		gaStatus = (TextView) findViewById(R.id.tvGAStatus);
		ivImage.setOnTouchListener(mTouchListener);

		// 获得Gallery对象
		g = (GalleryExt) findViewById(R.id.ga);
		ia = new ImageAdapter(this, mImageList);
		g.setAdapter(ia);
		// 加载动画
		Animation an = AnimationUtils.loadAnimation(this, R.anim.scale);
		g.setAnimation(an);
		g.setOnItemClickListener(this);
		g.setOnItemSelectedListener(this);
		SW = getWindow().getWindowManager().getDefaultDisplay().getWidth();
		SH = getWindow().getWindowManager().getDefaultDisplay().getHeight();
		// 通过Intent得到GridView传过来的图片位置
		Intent intent = getIntent();
		Uri uri = (Uri) intent.getData();
		path = null;
		if (uri != null)
		{
			path = uri.getPath();
		}
		if (path != null)
		{
			String temp = path.substring(0, path.lastIndexOf('/'));
			Log.v("tag", temp);
			FileManager.getInstance().setFilePath(
					path.substring(0, path.lastIndexOf('/')));
			LoadImageList();
			SwitchFlag(1);
		} else
		{
			Toast.makeText(this, "未找到相应的图片文件", Toast.LENGTH_SHORT).show();
		}
	}

	private int SW, SH;

	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		SW = getWindow().getWindowManager().getDefaultDisplay().getWidth();
		SH = getWindow().getWindowManager().getDefaultDisplay().getHeight();
		ia.notifyDataSetChanged();
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	{
		mFileBean = ia.getItem(arg2);
		Bitmap mImage = SDFile.ReadFileToMaxBitmap(this,
				mFileBean.getFilePath(), SW * 2);
		if (mImage != null)
		{
			ivImage.setImageBitmap(mImage);
			Matrix matrix = new Matrix();
			int mWidth = SW / 2 - mImage.getWidth() / 2;
			int mHeight = SH / 2 - mImage.getHeight() / 2;
			matrix.postTranslate(mWidth, mHeight);
			ivImage.setImageMatrix(matrix);
			mTouchListener.setMatrix(matrix);
			SwitchFlag(2);
		}
		else
		{
			Toast.makeText(this, mFileBean.getFilePath()+"加载失败!",Toast.LENGTH_LONG).show();
		}
	}

	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		// g.removeAllViews();
		super.onDestroy();
	}

	public boolean onKeyDown(int keyCode, KeyEvent msg)
	{
		// 弹出退出对话框
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if (mFlag == 2)
			{
				SwitchFlag(1);
				return true;
			}
		}
		return super.onKeyDown(keyCode, msg);
	}

	private void SwitchFlag(int flag)
	{
		switch (flag)
		{
		case 2:
			rlImage.setVisibility(View.VISIBLE);
			rlGallery.setVisibility(View.GONE);
			break;
		case 1:
			rlGallery.setVisibility(View.VISIBLE);
			rlImage.setVisibility(View.GONE);
			break;
		}
		mFlag = flag;
	}

	private void LoadImageList()
	{
		mImageList.clear();
		List<FileBean> mFileList = FileManager.getInstance().getFileList();
		for (int i = 0; i < mFileList.size(); i++)
		{
			FileBean bean = mFileList.get(i);

			String fileEnds = bean.getFileEnds();
			if (fileEnds.equals("jpg") || fileEnds.equals("gif")
					|| fileEnds.equals("png") || fileEnds.equals("jpeg")
					|| fileEnds.equals("bmp"))
			{
				if (bean.getFilePath().equals(path))
				{
					i_position = mImageList.size();
				}
				mImageList.add(bean);
			}
		}
		P.v("i_position=" + i_position);
		g.setSelection(i_position);
		ia.notifyDataSetChanged();
	}

	private void startWallpaper()
	{
		int result = T.startWallpaper(this, mFileBean.getFilePath());
		if (result == 1)
		{
			Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
		} else
		{
			Toast.makeText(this, "设置失败", Toast.LENGTH_SHORT).show();
		}
	}

	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.btShare:
			T.ShareImageIntent(this, "分享", mFileBean.getFilePath());
			break;
		case R.id.btSetting:
			startWallpaper();
			break;
		case R.id.btRotate:
			break;
		case R.id.btDelete:
			break;
		}
	}

	public void onItemSelected(AdapterView<?> parent, View view,
			final int position, long id)
	{
		// SysEng.getInstance().addHandlerEvent(new AbsEvent()
		// {
		//
		//
		// public void ok()
		// {
		// // TODO Auto-generated method stub
		// gaStatus.setText(path+"("+position+"/"+mImageList.size()+")");
		// }
		// },500);
		// gaStatus.setText(path+"("+position+"/"+mImageList.size()+")");
	}

	public void onNothingSelected(AdapterView<?> parent)
	{
		// TODO Auto-generated method stub

	}

}