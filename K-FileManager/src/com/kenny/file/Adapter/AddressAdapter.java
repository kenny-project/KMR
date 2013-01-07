package com.kenny.file.Adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.kenny.KFileManager.R;
import com.kenny.file.bean.FileBean;

/** 自定义Adapter内部 */
public class AddressAdapter extends OFileAdapter {
	private int layoutID = R.layout.listitem_favor_item;

	public AddressAdapter(Context context, int nFlag, List<FileBean> mFileList,
			int layoutID) {
		this(context, nFlag, mFileList);
		this.layoutID = layoutID;
	}

	public AddressAdapter(Context context, int nFlag, List<FileBean> mFileList) {
		super(context, nFlag, mFileList);
	}

	public class FavViewHolder extends ViewHolder {
		public TextView mPath; // 路径
	}

	public View getListView(int position, View convertView, ViewGroup viewgroup) {
		FavViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new FavViewHolder();
			LayoutInflater mLI = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			convertView = mLI.inflate(layoutID, null);
			viewHolder.mIV = (ImageView) convertView
					.findViewById(R.id.image_list_childs);
			viewHolder.mTV = (TextView) convertView.findViewById(R.id.tvTitle);
			viewHolder.mTD = (TextView) convertView.findViewById(R.id.tvDesc);
			viewHolder.mPath = (TextView) convertView.findViewById(R.id.tvPath);
			convertView.setTag(viewHolder);
			viewHolder.mCB = (CheckBox) convertView
					.findViewById(R.id.cbChecked);

		} else {
			viewHolder = (FavViewHolder) convertView.getTag();
		}
		FileBean temp = mFileList.get(position);
		String fileName = temp.getFileName();
		viewHolder.mTV.setText(fileName);
		viewHolder.mPath.setText(temp.getFilePath());
		if (temp.isDirectory()) {
			viewHolder.mIV.setImageDrawable(temp.getFileIco(mContext));
		} else {
			Drawable draw = null;
			String fileEnds = temp.getFileEnds();
			if (fileEnds.equals("jpg") || fileEnds.equals("gif")
					|| fileEnds.equals("png") || fileEnds.equals("jpeg")
					|| fileEnds.equals("bmp")) {
				draw = mLogoImage.loadDrawable(temp, new KImageCallback(
						viewHolder));
			} else if (fileEnds.equals("apk")) {
				draw = mLogoImage.loadDrawable(temp, new KImageCallback(
						viewHolder));
			}
			if (draw != null) {
				viewHolder.mIV.setImageDrawable(draw);
			} else {
				viewHolder.mIV.setImageDrawable(temp.getFileIco(mContext));
			}
		}
		return convertView;

	}
}