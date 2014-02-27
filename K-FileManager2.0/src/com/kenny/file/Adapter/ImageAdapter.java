package com.kenny.file.Adapter;

import java.util.List;

import com.kenny.KFileManager.t.R;
import com.kenny.file.Image.ImageLoader;
import com.kenny.file.bean.FileBean;
import com.kenny.file.interfaces.ImageCallback;
import com.kenny.file.util.Res;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/** 自定义Adapter内部籄1�7 */
public class ImageAdapter extends BaseAdapter
{
	// private Bitmap mBackRoot;
	private Drawable mImage;
	private Context mContext;
	private List<FileBean> mFileList;
	private ImageLoader mLogoImage;

	public ImageAdapter(Context context, List<FileBean> mFileList)
	{
		mContext = context;
		this.mFileList = mFileList;
		
		mImage = Res.getInstance(context).getDefFileIco("jpg");
		mLogoImage = ImageLoader.GetObject(mContext);
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
			convertView = mLI.inflate(R.layout.listitem_image, null);
			viewHolder.mIV = (ImageView) convertView
					.findViewById(R.id.image_list_childs);
			viewHolder.mTV = (TextView) convertView.findViewById(R.id.tvTitle);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		FileBean temp = mFileList.get(position);
		viewHolder.mTV.setText(temp.getFileName());
		Drawable draw = mLogoImage.loadDrawable(temp, new KImageCallback(
				viewHolder));
		if (draw != null)
		{
			viewHolder.mIV.setImageDrawable(draw);
		} else
		{
			viewHolder.mIV.setImageDrawable(mImage);
		}
		return convertView;
	}

	class ViewHolder
	{
		ImageView mIV;// image
		TextView mTV;// title
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
}