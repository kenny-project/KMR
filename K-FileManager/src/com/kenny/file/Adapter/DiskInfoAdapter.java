package com.kenny.file.Adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kenny.KFileManager.R;
import com.kenny.file.bean.FGroupInfo;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.tools.T;
import com.kenny.file.util.Theme;

/** 自定义Adapter内部 */
public class DiskInfoAdapter extends BaseAdapter
{
	protected Context mContext;
	protected List<FGroupInfo> mFileList;
	protected INotifyDataSetChanged notify;
	/**
	 * 窗体标记:1:ListView 2:GridView
	 * @param context
	 * @param nFlag
	 * @param mFileList
	 * @param notify
	 */
	public DiskInfoAdapter(Context context, List<FGroupInfo> mFileList,
			INotifyDataSetChanged notify)
	{
		mContext = context.getApplicationContext();
		this.notify = notify;
		this.mFileList = mFileList;
	}
	public int getCount()
	{
		return mFileList.size();
	}

	public FGroupInfo getItem(int position)
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

	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup viewgroup)
	{
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			viewHolder = new ViewHolder();
			LayoutInflater mLI = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mLI.inflate(R.layout.listitem_diskinfo, null);
			viewHolder.mIV = (ImageView) convertView
					.findViewById(R.id.image_list_childs);
			viewHolder.mTV = (TextView) convertView.findViewById(R.id.tvTitle);
			viewHolder.mTD = (TextView) convertView.findViewById(R.id.tvDesc);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		if (viewHolder.ThemeMode != Theme.getThemeMode())
		{
			viewHolder.ThemeMode=Theme.getThemeMode();
			viewHolder.mTV.setTextColor(Theme.getTextColor());
			viewHolder.mTD.setTextColor(Theme.getTextColor());
			convertView.setBackgroundResource(Theme.getBackgroundResource());
		}
		FGroupInfo temp = mFileList.get(position);
		viewHolder.mTV.setText(temp.getTitle()+"(" + temp.getCount() + ")");
		viewHolder.mTD.setText(T.FileSizeToString(temp.getSize()));
		viewHolder.mIV.setBackgroundDrawable(temp.getLogo(mContext));
		return convertView;
	}

	protected class ViewHolder
	{
		public int ThemeMode = -1;// 当前样式
		public ImageView mIV; // image
		public TextView mTV; // title
		public TextView mTD; // desc
	}
}