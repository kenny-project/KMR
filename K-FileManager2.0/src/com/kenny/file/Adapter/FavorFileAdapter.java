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

import com.kenny.KFileManager.t.R;
import com.kenny.file.bean.FileBean;
import com.kenny.file.interfaces.ImageCallback;
import com.kenny.file.util.Res;
import com.kenny.file.util.Theme;

/** 自定义Adapter内部 */
public class FavorFileAdapter extends OFileAdapter {
	private int layoutID = R.layout.listitem_favor_item;

	public FavorFileAdapter(Context context, int nFlag,
			List<FileBean> mFileList, int layoutID) {
		this(context, nFlag, mFileList);
		this.layoutID = layoutID;
	}

	public FavorFileAdapter(Context context, int nFlag, List<FileBean> mFileList) {
		super(context, nFlag, mFileList);
	}

	public class FavViewHolder extends ViewHolder {
		public TextView mPath; // 路径
		public int ThemeMode = -1;// 当前样式
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
			viewHolder.mBackground = (ImageView) convertView
					.findViewById(R.id.rlBackground);

			viewHolder.ThemeMode = Theme.getThemeMode();
			viewHolder.mTV.setTextColor(Theme.getTextColor());
			viewHolder.mTD.setTextColor(Theme.getTextColor());
			viewHolder.mPath.setTextColor(Theme.getTextColor());
			viewHolder.mBackground.setBackgroundColor(Theme
					.getListSelectedBackgroundColor());
			convertView.setBackgroundColor(Theme.getBackgroundColor());

		} else {
			viewHolder = (FavViewHolder) convertView.getTag();

			if (viewHolder.ThemeMode != Theme.getThemeMode()) {
				viewHolder.mTV.setTextColor(Theme.getTextColor());
				viewHolder.mTD.setTextColor(Theme.getTextColor());
				viewHolder.mPath.setTextColor(Theme.getTextColor());
				viewHolder.mBackground.setBackgroundColor(Theme
						.getListSelectedBackgroundColor());
				convertView.setBackgroundColor(Theme.getBackgroundColor());
			}
		}
		FileBean temp = mFileList.get(position);

		if (temp.getFileName().equals("..")) {
			viewHolder.mIV.setImageDrawable(Res.getInstance(mContext)
					.getBackUp());
			viewHolder.mTV.setText(go_back);
			viewHolder.mTD.setVisibility(View.GONE);
			viewHolder.mCB.setVisibility(View.GONE);
			viewHolder.mPath.setVisibility(View.GONE);
			viewHolder.mBackground.setVisibility(View.GONE);
			convertView.setBackgroundColor(Theme.getBackgroundColor());
			viewHolder.mBackground.setVisibility(View.GONE);
		}
		else if (temp.getFileName().equals("ALL")) 
		{
			viewHolder.mIV.setImageDrawable(Res.getInstance(mContext)
					.getFolder());
			viewHolder.mTV.setText("全部文件");
			viewHolder.mTD.setVisibility(View.GONE);
			viewHolder.mCB.setVisibility(View.GONE);
			viewHolder.mPath.setVisibility(View.GONE);
			viewHolder.mBackground.setVisibility(View.GONE);
			convertView.setBackgroundColor(Theme.getBackgroundColor());
			viewHolder.mBackground.setVisibility(View.GONE);
		}
		else 
		{
			if (temp.isChecked()) 
			{
				viewHolder.mBackground.setVisibility(View.VISIBLE);
			} else {
				viewHolder.mBackground.setVisibility(View.GONE);
			}
			viewHolder.mTD.setVisibility(View.VISIBLE);
			viewHolder.mCB.setVisibility(View.VISIBLE);
			viewHolder.mCB.setChecked(temp.isChecked());
			viewHolder.mCB.setOnClickListener(new OnKCheckedChangeListener(
					viewHolder.mBackground, temp));
			String fileName = temp.getFileName();
			viewHolder.mTV.setText(fileName);
			viewHolder.mTD.setText(temp.getDesc());
			viewHolder.mPath.setText(temp.getFolderPath());

			if (temp.isDirectory()) {
				viewHolder.mIV.setImageDrawable(temp.getFileIco(mContext));
			} else {
				Drawable draw = null;
				if (bShowLogo) {
					// P.debug("bShowLogo=" + bShowLogo);
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

	protected class KImageCallback implements ImageCallback {
		ViewHolder viewHolder;

		public KImageCallback(ViewHolder viewHolder) {
			this.viewHolder = viewHolder;

		}

		public void imageLoaded(Drawable imageDrawable, String imageUrl) {
			viewHolder.mIV.setImageDrawable(imageDrawable);
		}
	}
}