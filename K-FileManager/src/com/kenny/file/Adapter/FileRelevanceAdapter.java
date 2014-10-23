package com.kenny.file.Adapter;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kenny.KFileManager.R;
import com.kenny.file.Image.ImageLoader;
import com.kenny.file.util.Theme;

/** 自定义Adapter内部 */
public class FileRelevanceAdapter extends BaseAdapter {
	protected Context mContext;
	protected List<ResolveInfo> mFileList;
	protected ImageLoader mLogoImage;
	 private PackageManager pManager;
	/**
	 * // 窗体标记:1:ListView 2:GridView
	 * 
	 * @param context
	 * @param nFlag
	 * @param mFileList
	 * @param notify
	 */
	public FileRelevanceAdapter(Context context,
			List<ResolveInfo> mFileList) {
		mContext = context;
		this.mFileList = mFileList;
		mLogoImage = ImageLoader.getInstance(mContext);
		pManager = mContext.getPackageManager();
	}

	public int getCount() {
		return mFileList.size();
	}

	public ResolveInfo getItem(int position) {
		return mFileList.get(position);
	}

	/**
	 * 返回用对像的ID;
	 */
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup viewgroup) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater mLI = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mLI.inflate(R.layout.listitem_file_ico_relevance,
					null);
			viewHolder.mIV = (ImageView) convertView
					.findViewById(R.id.image_list_childs);
			viewHolder.mTV = (TextView) convertView.findViewById(R.id.tvTitle);
			viewHolder.mTD = (TextView) convertView.findViewById(R.id.tvDesc);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		ResolveInfo temp = mFileList.get(position);
		viewHolder.mTV.setTextColor(Theme.getTextColor());
		viewHolder.mTD.setTextColor(Theme.getTextColor());
		viewHolder.mTD.setVisibility(View.VISIBLE);
		viewHolder.mIV.setImageDrawable(pManager.getApplicationIcon(temp.activityInfo.applicationInfo));
		viewHolder.mTV.setText(pManager.getApplicationLabel(temp.activityInfo.applicationInfo).toString());
		viewHolder.mTD.setText(temp.activityInfo.packageName);
		return convertView;
	}

	protected class ViewHolder {
		public ImageView mIV; // image
		public TextView mTV; // title
		public TextView mTD; // desc
	}
}