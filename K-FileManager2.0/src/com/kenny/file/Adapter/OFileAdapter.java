package com.kenny.file.Adapter;

import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.framework.log.P;
import com.kenny.KFileManager.t.R;
import com.kenny.file.Image.ImageLoader;
import com.kenny.file.bean.FileBean;
import com.kenny.file.interfaces.ImageCallback;
import com.kenny.file.util.Res;
import com.kenny.file.util.Theme;

/** 自定义Adapter内部 */
public class OFileAdapter extends BaseAdapter
{
	protected Context mContext;
	protected List<FileBean> mFileList;
	protected ImageLoader mLogoImage;
	protected Hashtable<String, FileBean> mSelectList = new Hashtable<String, FileBean>();
	protected int nFlag = 1; // 窗体标记:1:ListView
	// 2:GridView
	protected boolean bShowLogo = true; // 是否启动加载后台图片
	protected boolean bSelect = false; // true: 显示 false:不显示
	protected String go_back;

	public OFileAdapter(Context context, int nFlag, List<FileBean> mFileList)
	{
		mContext = context;
		this.nFlag = nFlag;
		if (nFlag == 1)
		{
			bSelect = true;
		}
		this.mFileList = mFileList;
		go_back = context.getString(R.string.back_previous);
		mLogoImage = ImageLoader.GetObject(mContext);
	}

	public boolean isSelectVisible()
	{
		return bSelect;
	}

	public void notifyDataSetChanged()
	{
		bShowLogo = true;
		super.notifyDataSetChanged();
	}

	/**
	 * 清空状态
	 */
	public void Clear()
	{
		bShowLogo = true;
		mSelectList.clear();
	}

	/**
	 * 是否已经选择相应的Item选项
	 * 
	 * @return
	 */
	public boolean isSelected()
	{
		return mSelectList.size() > 0;
	}

	public boolean isShowLogo()
	{
		return bShowLogo;
	}

	public void setShowLogo(boolean bShowLogo)
	{
		mLogoImage.cancel();
		this.bShowLogo = bShowLogo;

	}

	public void setSelected(boolean bSelect)
	{
		this.bSelect = bSelect;
	}

	public int getCount()
	{
		return mFileList.size();
	}

	public FileBean getItem(int position)
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
		if (nFlag == 1)
		{
			return getListView(position, convertView, viewgroup);
		} else
		{
			return getGridView(position, convertView, viewgroup);
		}
	}

	public View getGridView(int position, View convertView, ViewGroup viewgroup)
	{

		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			viewHolder = new ViewHolder();
			LayoutInflater mLI = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mLI.inflate(R.layout.gridview_local, null);
			viewHolder.mIV = (ImageView) convertView
					.findViewById(R.id.image_list_childs);
			viewHolder.mTV = (TextView) convertView.findViewById(R.id.tvTitle);
			viewHolder.mTD = (TextView) convertView.findViewById(R.id.tvDesc);
			viewHolder.mTD.setVisibility(View.GONE);
			convertView.setTag(viewHolder);
			viewHolder.mCB = (CheckBox) convertView
					.findViewById(R.id.cbChecked);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		FileBean temp = mFileList.get(position);

		viewHolder.mTV.setTextColor(Theme.getTextColor());
		viewHolder.mTD.setTextColor(Theme.getTextColor());
		if (temp.getFileName().equals(".."))
		{
			viewHolder.mIV.setImageDrawable(Res.getInstance(mContext)
					.getBackUp());
			viewHolder.mTV.setText(go_back);
			viewHolder.mCB.setVisibility(View.GONE);
		} else
		{
			if (bSelect)
			{
				viewHolder.mCB.setVisibility(View.VISIBLE);
				viewHolder.mCB.setChecked(temp.isChecked());
				viewHolder.mCB.setOnClickListener(new OnKCheckedChangeListener(
						convertView, temp));
			} else
			{
				viewHolder.mCB.setVisibility(View.GONE);
			}

			String fileName = temp.getFileName();
			viewHolder.mTV.setText(fileName);
			if (temp.isDirectory())
			{
				viewHolder.mIV.setImageDrawable(temp.getFileIco(mContext));
			} else
			{
				Drawable draw = null;
				if (bShowLogo)
				{
					// //P..debug("bShowLogo="+bShowLogo);
					String fileEnds = temp.getFileEnds();
					if (fileEnds.equals("jpg") || fileEnds.equals("gif")
							|| fileEnds.equals("png")
							|| fileEnds.equals("jpeg")
							|| fileEnds.equals("bmp"))
					{
						draw = mLogoImage.loadDrawable(temp,
								new KImageCallback(temp,viewHolder));
					} else if (fileEnds.equals("apk"))
					{
						draw = mLogoImage.loadDrawable(temp,
								new KImageCallback(temp,viewHolder));
					}
				}
				if (draw != null)
				{
					viewHolder.mIV.setImageDrawable(draw);
				} else
				{
					viewHolder.mIV.setImageDrawable(temp.getFileIco(mContext));
				}
			}
		}
		// viewHolder.mIV.setScaleType(ImageView.ScaleType.FIT_CENTER);
		// final int w = (int) (36 *
		// mContext.getResources().getDisplayMetrics().density + 0.5f);
		// viewHolder.mIV.setLayoutParams(new GridView.LayoutParams(w, w));
		return convertView;
	}

	public View getListView(int position, View convertView, ViewGroup viewgroup)
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
			viewHolder.mTD = (TextView) convertView.findViewById(R.id.tvDesc);
			convertView.setTag(viewHolder);
			viewHolder.mCB = (CheckBox) convertView
					.findViewById(R.id.cbChecked);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		FileBean temp = mFileList.get(position);
		viewHolder.mTV.setTextColor(Theme.getTextColor());
		viewHolder.mTD.setTextColor(Theme.getTextColor());
		if (temp.getFileName().equals(".."))
		{
			viewHolder.mIV.setImageDrawable(Res.getInstance(mContext)
					.getBackUp());
			viewHolder.mTV.setText(go_back);
			viewHolder.mTD.setVisibility(View.GONE);
			viewHolder.mCB.setVisibility(View.GONE);
		} else
		{
			viewHolder.mTD.setVisibility(View.VISIBLE);
			viewHolder.mCB.setVisibility(View.VISIBLE);
			viewHolder.mCB.setChecked(temp.isChecked());
			if (temp.isChecked())
			{
				convertView.setBackgroundColor(Theme
						.getListSelectedBackgroundColor());
			} else
			{
				convertView.setBackgroundColor(Theme.getBackgroundColor());
			}
			viewHolder.mCB.setOnClickListener(new OnKCheckedChangeListener(
					convertView, temp));
			String fileName = temp.getFileName();
			viewHolder.mTV.setText(fileName);

			if (temp.isDirectory())
			{
				viewHolder.mIV.setImageDrawable(temp.getFileIco(mContext));
			} else
			{
				Drawable draw = null;
				if (bShowLogo)
				{
					String fileEnds = temp.getFileEnds();
					if (fileEnds.equals("jpg") || fileEnds.equals("gif")
							|| fileEnds.equals("png")
							|| fileEnds.equals("jpeg")
							|| fileEnds.equals("bmp"))
					{
						draw = mLogoImage.loadDrawable(temp,
								new KImageCallback(temp,viewHolder));
					} else if (fileEnds.equals("apk"))
					{
						draw = mLogoImage.loadDrawable(temp,
								new KImageCallback(temp,viewHolder));
					}
				}
				if (draw != null)
				{
					viewHolder.mIV.setImageDrawable(draw);
				} else
				{
					viewHolder.mIV.setImageDrawable(temp.getFileIco(mContext));
				}
			}
			viewHolder.mTD.setText(temp.getDesc());
		}
		return convertView;
	}

	protected class ViewHolder
	{
		public ImageView mIV; // image
		public TextView mTV; // title
		public TextView mTD; // desc
		public CheckBox mCB; // 选择
		public ImageView mBackground;
	}

	protected class KImageCallback implements ImageCallback
	{
		ViewHolder viewHolder;
		FileBean bean;

		public KImageCallback(FileBean bean,ViewHolder viewHolder)
		{
			this.viewHolder = viewHolder;
			this.bean=bean;
			
		}

		public void imageLoaded(Drawable imageDrawable, String imageUrl)
		{
			viewHolder.mTV.setText(bean.getFileName());
			viewHolder.mIV.setImageDrawable(imageDrawable);
		}
	}

	/**
	 * 设置选择状态
	 * 
	 * @param tmpInfo
	 */
	public void setChecked(FileBean tmpInfo)
	{
		tmpInfo.setChecked(!tmpInfo.isChecked());
		if (tmpInfo.isChecked())
		{
			mSelectList.put(tmpInfo.getFileName(), tmpInfo);
		} else
		{
			mSelectList.remove(tmpInfo.getFileName());
		}
	}

	protected class OnKCheckedChangeListener implements OnClickListener
	{
		private FileBean tmpInfo;
		private View convertView = null;

		public OnKCheckedChangeListener(View convertView, FileBean tmpInfo)
		{
			this.tmpInfo = tmpInfo;
			this.convertView = convertView;
		}

		public void onClick(View v)
		{
			P.debug("Checked:OnClick");
			CheckBox cb = (CheckBox) v;
			boolean isselect = cb.isChecked();
			tmpInfo.setChecked(isselect);
			if (isselect)
			{
				convertView.setBackgroundColor(Theme
						.getListSelectedBackgroundColor());
				convertView.setVisibility(View.VISIBLE);
				mSelectList.put(tmpInfo.getFileName(), tmpInfo);
			} else
			{
				convertView.setBackgroundColor(Theme.getBackgroundColor());
				convertView.setVisibility(View.GONE);
				mSelectList.remove(tmpInfo.getFileName());
			}
		}
	}
}