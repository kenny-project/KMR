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
import com.kenny.KFileManager.R;
import com.kenny.file.Image.ImageLoader;
import com.kenny.file.bean.FileBean;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.struct.ImageCallback;
import com.kenny.file.util.Const;
import com.kenny.file.util.Res;
import com.kenny.file.util.Theme;

/** 自定义Adapter内部 */
public class FileAdapter extends BaseAdapter {
	protected Context mContext;
	protected List<FileBean> mFileList;
	protected ImageLoader mLogoImage;
	protected Hashtable<String, FileBean> mSelectList = new Hashtable<String, FileBean>();
	protected int nFlag = 1; // 窗体标记:1:ListView 2:GridView
	protected boolean bShowLogo = true; // 是否启动加载后台图片
	protected String go_back;
	protected INotifyDataSetChanged notify;

	public FileAdapter(Context context, int nFlag, List<FileBean> mFileList,
			INotifyDataSetChanged notify) {
		mContext = context;
		this.nFlag = nFlag;
		this.notify = notify;
		this.mFileList = mFileList;
		go_back = context.getString(R.string.back_previous);
		mLogoImage = ImageLoader.GetObject(mContext);
	}

	public void notifyDataSetChanged() {
		bShowLogo = true;
		super.notifyDataSetChanged();
	}

	/**
	 * 清空状态
	 */
	public void Clear() {
		bShowLogo = true;
		mSelectList.clear();
	}

	/**
	 * 是否已经选择相应的Item选项
	 * 
	 * @return
	 */
	public boolean isSelected() {
		return mSelectList.size() > 0;
	}

	public boolean isShowLogo() {
		return bShowLogo;
	}

	public void setShowLogo(boolean bShowLogo) {
		mLogoImage.cancel();
		this.bShowLogo = bShowLogo;

	}

	public int getCount() {
		return mFileList.size();
	}

	public FileBean getItem(int position) {
		return mFileList.get(position);
	}

	/**
	 * 返回用对像的ID;
	 */
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup viewgroup) {
		if (nFlag == 1) {
			return getListView(position, convertView, viewgroup);
		} else {
			return getGridView(position, convertView, viewgroup);
		}
	}

	public View getGridView(int position, View convertView, ViewGroup viewgroup) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
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
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		FileBean temp = mFileList.get(position);

		viewHolder.mTV.setTextColor(Theme.getTextColor());
		viewHolder.mTD.setTextColor(Theme.getTextColor());
		if (temp.getFileName().equals("..")) {
			viewHolder.mIV.setImageDrawable(Res.getInstance(mContext)
					.getBackUp());
			viewHolder.mTV.setText(go_back);
			viewHolder.mCB.setVisibility(View.GONE);
			convertView.setBackgroundColor(Theme.getBackgroundColor());
		} else {
			viewHolder.mCB.setVisibility(View.VISIBLE);
			viewHolder.mCB.setChecked(temp.isChecked());
			viewHolder.mCB.setOnClickListener(new OnKCheckedChangeListener(
					convertView, temp));
			if (temp.isChecked()) {
				convertView.setBackgroundColor(Theme
						.getListSelectedBackgroundColor());
			} else {
				convertView.setBackgroundColor(Theme.getBackgroundColor());
			}

			String fileName = temp.getFileName();
			viewHolder.mTV.setText(fileName);
			if (temp.isDirectory()) {
				viewHolder.mIV.setImageDrawable(temp.getFileIco(mContext));
			} else {
				Drawable draw = null;
				if (bShowLogo) {
					String fileEnds = temp.getFileEnds();
					if (fileEnds.equals("jpg") || fileEnds.equals("gif")
							|| fileEnds.equals("png")
							|| fileEnds.equals("jpeg")
							|| fileEnds.equals("bmp")) {
						draw = mLogoImage.loadDrawable(temp,
								new KImageCallback(viewHolder));
					} else if (fileEnds.equals("apk")) {
						draw = mLogoImage.loadDrawable(temp,
								new KImageCallback(viewHolder));
					}
				}
				if (draw != null) {
					viewHolder.mIV.setImageDrawable(draw);
				} else {
					viewHolder.mIV.setImageDrawable(temp.getFileIco(mContext));
				}
			}
		}
		return convertView;
	}

	public View getListView(int position, View convertView, ViewGroup viewgroup) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
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
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		FileBean temp = mFileList.get(position);
		viewHolder.mTV.setTextColor(Theme.getTextColor());
		viewHolder.mTD.setTextColor(Theme.getTextColor());
		if (temp.getFileName().equals("..")) {
			viewHolder.mIV.setImageDrawable(Res.getInstance(mContext)
					.getBackUp());
			viewHolder.mTV.setText(go_back);
			viewHolder.mTD.setVisibility(View.GONE);
			viewHolder.mCB.setVisibility(View.GONE);
		} else {
			viewHolder.mTD.setVisibility(View.VISIBLE);
			viewHolder.mCB.setVisibility(View.VISIBLE);
			viewHolder.mCB.setChecked(temp.isChecked());

			if (temp.isChecked()) {
				convertView.setBackgroundColor(Theme
						.getListSelectedBackgroundColor());
			} else {
				convertView.setBackgroundColor(Theme.getBackgroundColor());
			}
			viewHolder.mCB.setOnClickListener(new OnKCheckedChangeListener(
					convertView, temp));
			String fileName = temp.getFileName();
			viewHolder.mTV.setText(fileName);

			if (temp.isDirectory()) {
				viewHolder.mIV.setImageDrawable(temp.getFileIco(mContext));
			} else {
				Drawable draw = null;
				if (bShowLogo) {
					String fileEnds = temp.getFileEnds();
					if (fileEnds.equals("jpg") || fileEnds.equals("gif")
							|| fileEnds.equals("png")
							|| fileEnds.equals("jpeg")
							|| fileEnds.equals("bmp")) {
						draw = mLogoImage.loadDrawable(temp,
								new KImageCallback(viewHolder));
					} else if (fileEnds.equals("apk")) {
						draw = mLogoImage.loadDrawable(temp,
								new KImageCallback(viewHolder));
					}
				}
				if (draw != null) {
					viewHolder.mIV.setImageDrawable(draw);
				} else {
					viewHolder.mIV.setImageDrawable(temp.getFileIco(mContext));
				}
			}
			viewHolder.mTD.setText(temp.getDesc());
		}
		return convertView;
	}

	protected class ViewHolder {
		public ImageView mIV; // image
		public TextView mTV; // title
		public TextView mTD; // desc
		public CheckBox mCB; // 选择
	}

	protected class KImageCallback implements ImageCallback {
		ViewHolder viewHolder;

		public KImageCallback(ViewHolder viewHolder) {
			this.viewHolder = viewHolder;
		}

		public void imageLoaded(Drawable imageDrawable, String imageUrl) {
			viewHolder.mIV.setImageDrawable(imageDrawable);
		}
	}

	/**
	 * 设置选择状态
	 * 
	 * @param tmpInfo
	 */
	public void setChecked(FileBean tmpInfo) {
		tmpInfo.setChecked(!tmpInfo.isChecked());
		if (tmpInfo.isChecked()) {
			mSelectList.put(tmpInfo.getFileName(), tmpInfo);
		} else {
			mSelectList.remove(tmpInfo.getFileName());
		}
	}

	protected class OnKCheckedChangeListener implements OnClickListener {
		private FileBean tmpInfo;
		private View convertView = null;

		public OnKCheckedChangeListener(View convertView, FileBean tmpInfo) {
			this.tmpInfo = tmpInfo;
			this.convertView = convertView;
		}

		public void onClick(View v) {
			P.debug("Checked:OnClick");
			CheckBox cb = (CheckBox) v;
			boolean isselect = cb.isChecked();
			tmpInfo.setChecked(isselect);

			if (isselect) {
				convertView.setBackgroundColor(Theme
						.getListSelectedBackgroundColor());
				mSelectList.put(tmpInfo.getFileName(), tmpInfo);
				if (notify != null) {
					notify.NotifyDataSetChanged(Const.cmd_Local_List_Selected,
							tmpInfo);
				}
			} else {
				convertView.setBackgroundColor(Theme.getBackgroundColor());
				mSelectList.remove(tmpInfo.getFileName());
				if (mSelectList.size() == 0) {
					if (notify != null) {
						notify.NotifyDataSetChanged(
								Const.cmd_Local_List_UnSelected, tmpInfo);
					}
				}
			}
		}
	}
}