package com.kenny.file.Adapter;

import java.util.List;

import com.kenny.KFileManager.R;
import com.kenny.file.Image.ImageLoader;
import com.kenny.file.bean.FileBean;
import com.kenny.file.bean.NetClientBean;
import com.kenny.file.struct.ImageCallback;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

/** 自定义Adapter内部籄1�7 */
public class NetAdapter extends BaseAdapter
{
	// private Bitmap mBackRoot;
	private Bitmap mFtpLogo;
	private Bitmap mImage;
	private Bitmap mAudio;
	private Bitmap mRar;
	private Bitmap mVideo;
	private Bitmap mFolder;
	private Bitmap mApk;
	private Bitmap mOthers;
	private Bitmap mTxt;
	private Bitmap mWeb;
	private Context mContext;
	private List<NetClientBean> mFileList;
	private ImageLoader mLogoImage;

	public NetAdapter(Context context, List<NetClientBean> mFileList)
	{
		mContext = context;
		this.mFileList = mFileList;
//		mFtpLogo = BitmapFactory.decodeResource(mContext.getResources(),
//				R.drawable.ftplogo);
//		mImage = BitmapFactory.decodeResource(mContext.getResources(),
//				R.drawable.f_image);
//		mAudio = BitmapFactory.decodeResource(mContext.getResources(),
//				R.drawable.f_audio);
//		mVideo = BitmapFactory.decodeResource(mContext.getResources(),
//				R.drawable.f_video);
//		mApk = BitmapFactory.decodeResource(mContext.getResources(),
//				R.drawable.f_apk);
//		mTxt = BitmapFactory.decodeResource(mContext.getResources(),
//				R.drawable.f_txt);
//		mOthers = BitmapFactory.decodeResource(mContext.getResources(),
//				R.drawable.f_others);
//		mFolder = BitmapFactory.decodeResource(mContext.getResources(),
//				R.drawable.f_folder);
//		mRar = BitmapFactory.decodeResource(mContext.getResources(),
//				R.drawable.f_zip);
//		mWeb = BitmapFactory.decodeResource(mContext.getResources(),
//				R.drawable.f_web);
//		mLogoImage = ImageLoader.GetObject(mContext);
	}

	public int getCount()
	{
		return mFileList.size();
	}

	public Object getItem(int position)
	{
		return mFileList.get(position);
	}

	/**
	 * 返回用对像的ID;
	 */
	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position, View convertView, ViewGroup viewgroup)
	{
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			viewHolder = new ViewHolder();
			LayoutInflater mLI = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mLI.inflate(R.layout.listitem_local, null);
			viewHolder.mIV = (ImageView) convertView
					.findViewById(R.id.image_list_childs);
			viewHolder.mTV = (TextView) convertView.findViewById(R.id.tvTitle);
			convertView.setTag(viewHolder);
			viewHolder.mCB = (CheckBox) convertView
					.findViewById(R.id.cbChecked);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		NetClientBean temp = mFileList.get(position);

		if (temp.isVisible())
		{
			viewHolder.mCB.setVisibility(View.VISIBLE);
			viewHolder.mCB.setChecked(temp.isChecked());
			viewHolder.mCB
					.setOnClickListener(new OnKCheckedChangeListener(temp));
		} else
		{
			viewHolder.mCB.setVisibility(View.GONE);
		}
		viewHolder.mTV.setText(temp.getTitle());
		viewHolder.mIV.setImageBitmap(mFtpLogo);
		return convertView;
	}

	class ViewHolder
	{
		ImageView mIV;
		TextView mTV;
		CheckBox mCB; // 选择
	}

	class KImageCallback implements ImageCallback
	{
		ViewHolder viewHolder;

		public KImageCallback(ViewHolder viewHolder)
		{
			this.viewHolder = viewHolder;
		}

		
		public void imageLoaded(Drawable imageDrawable, String imageUrl)
		{
			// TODO Auto-generated method stub
			viewHolder.mIV.setImageDrawable(imageDrawable);
		}
	}

	class OnKCheckedChangeListener implements OnCheckedChangeListener,
			OnClickListener
	{
		NetClientBean tmpInfo;

		public OnKCheckedChangeListener(NetClientBean tmpInfo)
		{
			this.tmpInfo = tmpInfo;
		}

		
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked)
		{
			tmpInfo.setChecked(isChecked);
		}

		
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			CheckBox cb = (CheckBox) v;
			boolean isselect = cb.isChecked();
			tmpInfo.setChecked(isselect);
		}
	}
}