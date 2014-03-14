package com.kenny.file.Adapter;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.kenny.KFileManager.R;
import com.kenny.file.Adapter.OFileAdapter.ViewHolder;
import com.kenny.file.bean.FileBean;
import com.kenny.file.db.Dao;
import com.kenny.file.interfaces.ImageCallback;
import com.kenny.file.util.Theme;

/** 自定义Adapter内部 */
public class FavorDialogAdapter extends OFileAdapter 
{
	private Dao dao;
	private final int nFlag = 0;// 0:收藏 1:历史
	public FavorDialogAdapter(Context context, int nFlag,
			List<FileBean> mFileList) {
		super(context, nFlag, mFileList);
		dao = Dao.getInstance(mContext);
	}
	protected class KImageCallback implements ImageCallback
	{
		ViewHolder viewHolder;

		public KImageCallback(ViewHolder viewHolder)
		{
			this.viewHolder = viewHolder;
			
		}

		public void imageLoaded(Drawable imageDrawable, String imageUrl)
		{
			viewHolder.mIV.setImageDrawable(imageDrawable);
		}
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

			convertView = mLI.inflate(R.layout.listitem_favor_dialog_item, null);
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
		{
			if (temp.isChecked()) {
				convertView.setBackgroundColor(Theme
						.getSelBackgroundResource());
			} else {
				convertView.setBackgroundColor(Theme.getBackgroundResource());
			}
			viewHolder.mCB.setChecked(dao.isHasHistory(nFlag, temp.getFile().getPath()));
			viewHolder.mCB
					.setOnClickListener(new OnFavoritesCheckedChangeListener(
							temp));
			String fileName = temp.getFileName();
			viewHolder.mTV.setText(fileName);

			viewHolder.mTD.setText(temp.getDesc());
			viewHolder.mPath.setText(temp.getFolderPath());

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

	protected class OnFavoritesCheckedChangeListener implements OnClickListener {
		private FileBean tmpInfo;

		public OnFavoritesCheckedChangeListener(FileBean tmpInfo) {
			this.tmpInfo = tmpInfo;
		}

		public void onClick(View v) 
		{
			CheckBox cb = (CheckBox) v;
			boolean isselect = cb.isChecked();
			if (!isselect)
			{
				File temp = tmpInfo.getFile();
				dao.deleteHistory(nFlag, temp.getPath());
				dao.closeDb();
			}
			else 
			{
				File temp = tmpInfo.getFile();
				dao.InsertHistory(nFlag, temp.getPath(), temp.length());
				dao.closeDb();
			}
			notifyDataSetChanged();
		}
	}
}