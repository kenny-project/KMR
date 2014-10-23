package com.kenny.file.Adapter;

import java.util.List;

import android.content.Context;
import android.content.pm.PackageStats;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.kenny.KFileManager.R;
import com.kenny.file.bean.AppBean;
import com.kenny.file.bean.FGroupInfo;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.struct.ImageCallback;
import com.kenny.file.tools.T;
import com.kenny.file.util.Res;

/** 自定义Adapter内部籄1�7 */
public class FGroupAdapter extends BaseAdapter implements OnClickListener

{
	private Context mContext;
	private List<FGroupInfo> mFileList;
	private int nFlag = 1;// 窗体标记:1:ListView 2:GridView
	private boolean bSelect = false;// true: 显示 false:不显示
	private Drawable mApk;
	public boolean isSelected()
	{
		return bSelect;
	}
	public void setSelected(boolean bSelect)
	{
		this.bSelect = bSelect;
	}

	public FGroupAdapter(Context context, int nFlag, List<FGroupInfo> mFileList)
	{

		mContext = context;
		this.mFileList = mFileList;
		this.nFlag = nFlag;
		if (nFlag == 1)
		{
			bSelect = true;
		}
		mApk =Res.getInstance(mContext).getDefFileIco("apk");
	}

	public int getCount()
	{
		return mFileList.size();
	}

	public Object getItem(int position)
	{
		return mFileList.get(position);
	}

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
			if (nFlag == 1)
			{
				convertView = mLI.inflate(R.layout.listitem_favorite, null);
			} else
			{
				convertView = mLI.inflate(R.layout.gridview_favorite, null);
			}

			viewHolder.mIV = (ImageView) convertView
					.findViewById(R.id.image_list_childs);

			viewHolder.mTV = (TextView) convertView.findViewById(R.id.tvTitle);
			viewHolder.mDV = (TextView) convertView.findViewById(R.id.tvDesc);
			viewHolder.mCB = (CheckBox) convertView
					.findViewById(R.id.cbChecked);
			viewHolder.mCB.setVisibility(View.GONE);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		FGroupInfo temp = mFileList.get(position);
		if (temp != null)
		{
			viewHolder.mDV.setText(T.FileSizeToString(temp.getSize()));
			viewHolder.mTV.setText(temp.getTitle()+"("+temp.getCount()+")");
			//viewHolder.mCB.setChecked(temp.isChecked());
			Bitmap bitMap = temp.getLogo(mContext);
			if (bitMap != null)
			{
				viewHolder.mIV.setImageBitmap(bitMap);
			} else
			{
				viewHolder.mIV.setImageDrawable(mApk);
			}
		}
//		if (nFlag == 1 || bSelect)
//		{
//			viewHolder.mCB.setVisibility(View.VISIBLE);
//		} else
//		{
//			viewHolder.mCB.setVisibility(View.GONE);
//		}
		return convertView;
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
			viewHolder.mIV.setImageDrawable(imageDrawable);
		}
	}

	class ViewHolder
	{
		ImageView mIV;// 图标
		TextView mTV; // Title
		TextView mDV; // Desc
		CheckBox mCB; // 选择
	}

	class OnKCheckedChangeListener implements OnCheckedChangeListener,
			OnClickListener
	{
		AppBean tmpInfo;

		public OnKCheckedChangeListener(AppBean tmpInfo)
		{
			this.tmpInfo = tmpInfo;
		}

		
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked)
		{
			tmpInfo.setChecked(isChecked);
			// TODO Auto-generated method stub

		}

		
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			CheckBox cb = (CheckBox) v;
			boolean isselect = cb.isChecked();
			tmpInfo.setChecked(isselect);
		}

	}

	
	public void onClick(View v)
	{
		// TODO Auto-generated method stub

	}

	class NotifyPackageSize implements INotifyDataSetChanged
	{
		AppBean m_AppInfo;

		public NotifyPackageSize(AppBean appInfo)
		{
			m_AppInfo = appInfo;
		}

		
		public void NotifyDataSetChanged(int cmd, Object value)
		{
			// TODO Auto-generated method stub
			PackageStats pStats = (PackageStats) value;
			m_AppInfo.setCacheSize(pStats.cacheSize); // 缓存大小
			m_AppInfo.setDataSize(pStats.dataSize); // 数据大小
			m_AppInfo.setCodeSize(pStats.codeSize); // 应用程序大小
//			mHandler.post(new Runnable(){
//
//				
//				public void run()
//				{
//					// TODO Auto-generated method stub
//					notifyDataSetChanged();		
//				}
//			});
			
		}
	}
}