package com.kenny.file.Adapter;

import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.kenny.KFileManager.R;
import com.kenny.file.Image.ImageLoader;
import com.kenny.file.bean.AppBean;
import com.kenny.file.bean.TaskBean;
import com.kenny.file.interfaces.ImageCallback;
import com.kenny.file.tools.ProcessMemoryUtil;
import com.kenny.file.util.Res;

/** 自定义Adapter内部籄1�7 */
public class TaskAdapter extends BaseAdapter

{
	private Context mContext;
	private List<TaskBean> mFileList;
	private ProcessMemoryUtil processMemoryUtil;

	private int nFlag = 1; // 窗体标记:1:ListView
	// 2:GridView
	private boolean bSelect = false; // true:
	// 显示
	// false:不显示
	private Drawable mApp;
	private ImageLoader mLogoImage;
	private boolean bShowLogo = true; // 是否启动加载后台图片
	private ActivityManager manager;

	public boolean isSelected()
	{
		return bSelect;
	}

	public void setSelected(boolean bSelect)
	{
		this.bSelect = bSelect;
	}

	public TaskAdapter(Context context, int nFlag, List<TaskBean> mFileList)
	{

		mContext = context;
		this.mFileList = mFileList;
		this.nFlag = nFlag;
		manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		if (nFlag == 1)
		{
			bSelect = true;
		}
		mLogoImage = ImageLoader.GetObject(mContext);
		mApp = Res.getInstance(mContext).getDefFileIco("apk");
		// processMemoryUtil = new ProcessMemoryUtil();
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

	public void notifyDataSetChanged()
	{
		super.notifyDataSetChanged();
	}

	public boolean isShowLogo()
	{
		return bShowLogo;
	}

	public void setShowLogo(boolean bShowLogo)
	{
		this.bShowLogo = bShowLogo;
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
				convertView = mLI.inflate(R.layout.listitem_task, null);
			} else
			{
				convertView = mLI.inflate(R.layout.gridview_app, null);
			}
			viewHolder.mIV = (ImageView) convertView
					.findViewById(R.id.image_list_childs);
			viewHolder.mISDV = (ImageView) convertView
					.findViewById(R.id.itemSDLogo);

			viewHolder.mTV = (TextView) convertView.findViewById(R.id.tvTitle);
			viewHolder.mDV = (TextView) convertView.findViewById(R.id.tvDesc);
			viewHolder.mCB = (CheckBox) convertView
					.findViewById(R.id.cbChecked);
			viewHolder.mKP = (Button) convertView.findViewById(R.id.btKillProc);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		TaskBean temp = mFileList.get(position);
		if (temp != null)
		{
			// viewHolder.mDV.setText("大小:" + temp.getSTotalsize());
			int nSdcard = temp.getFlags()
					& ApplicationInfo.FLAG_EXTERNAL_STORAGE;// 判断是否存放在SD卡上.
			if (nSdcard != 0)
			{
				viewHolder.mISDV.setVisibility(View.VISIBLE);
			} else
			{
				viewHolder.mISDV.setVisibility(View.GONE);
			}
			// if(temp.getCpuMemString().length()==0)
			// {
			// String str = processMemoryUtil.getMemInfoByPid(temp.getProcId());
			// programUtil.setCpuMemString(str);
			// }

			viewHolder.mDV.setText(temp.getVersionName());
			// viewHolder.mDV.setText(processMemoryUtil.getMemInfoByPid(temp
			// .getProcId()));
			viewHolder.mTV.setText(temp.getAppName());
			viewHolder.mCB.setChecked(temp.isChecked());
			viewHolder.mCB
					.setOnClickListener(new OnKCheckedChangeListener(temp));
			viewHolder.mKP.setOnClickListener(new OnKillProcChangeListener(
					temp, position));

			if (temp.getAppIco() == null)
			{
				if (bShowLogo)
				{
					// mpkgSizeObserver.Start(temp, notify);by wmh 获取应用程序文件大小
					Drawable draw = null;
					draw = mLogoImage.loadDrawable(temp, new KImageCallback(
							viewHolder));

					// draw = mLogoImage.loadApp(mContext,
					// temp.getPackageName());
					if (draw != null)
					{
						viewHolder.mIV.setImageDrawable(draw);
						temp.setAppIco(draw);
					}
				} else
				{
					viewHolder.mIV.setImageDrawable(mApp);
				}
			} else
			{
				viewHolder.mIV.setImageDrawable(temp.getAppIco());
			}
		}
		if (nFlag == 1 || bSelect)
		{
			viewHolder.mCB.setVisibility(View.GONE);
		} else
		{
			viewHolder.mCB.setVisibility(View.GONE);
		}
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
			// TODO Auto-generated method stub
			viewHolder.mIV.setImageDrawable(imageDrawable);
		}
	}

	class ViewHolder
	{

		ImageView mISDV; // 图标
		ImageView mIV; // 图标
		TextView mTV; // Title
		TextView mDV; // Desc
		CheckBox mCB; // 选择
		Button mKP;//
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

	class OnKillProcChangeListener implements OnClickListener
	{
		AppBean tmpInfo;
		int position;

		public OnKillProcChangeListener(AppBean tmpInfo, int position)
		{
			this.tmpInfo = tmpInfo;
			this.position = position;
		}

		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			try
			{
				manager.restartPackage(tmpInfo.getPackageName());
				if(mFileList.size()>position)
				{
				mFileList.remove(position);
				}

			} catch (Exception e)
			{
				e.printStackTrace();
			} finally
			{
				notifyDataSetChanged();
			}
		}
	}
}