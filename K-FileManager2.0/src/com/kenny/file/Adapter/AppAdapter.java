package com.kenny.file.Adapter;

import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.kenny.KFileManager.R;
import com.kenny.file.Image.ImageLoader;
import com.kenny.file.bean.AppBean;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.interfaces.ImageCallback;
import com.kenny.file.util.Res;

/**
 * 自定义ArrayAdapter
 * 
 * 
 */
public class AppAdapter extends ArrayAdapter<AppBean> {
	private Context mContext;
	// private List<AppBean> mFileList;
	private int nFlag = 1; // 窗体标记:1:ListView
							// 2:GridView
	private boolean bSelect = false; // true:
	// 显示
	// false:不显示
	// private PkgSizeObserver mpkgSizeObserver;
	private Drawable mApp;
	private Handler mHandler;
	private NotifyPackageSize notify = new NotifyPackageSize();
	private ImageLoader mLogoImage;
	private boolean bShowLogo = true; // 是否启动加载后台图片

	public boolean isSelected() {
		return bSelect;
	}

	public void setSelected(boolean bSelect) {
		this.bSelect = bSelect;
	}

	public AppAdapter(Context context, int nFlag, List<AppBean> mFileList) {
		super(context, R.layout.listitem_app, mFileList);
		mContext = context;
		// this.mFileList = mFileList;
		this.nFlag = nFlag;
		if (nFlag == 1) {
			bSelect = true;
		}
		// mpkgSizeObserver = PkgSizeObserver.getHandler(mContext);
		mLogoImage = ImageLoader.GetObject(mContext);
		mApp = Res.getInstance(mContext).getDefFileIco("apk");
		mHandler = new Handler();
	}

	// public int getCount()
	// {
	// return mFileList.size();
	// }

	// public Object getItem(int position)
	// {
	// return mFileList.get(position);
	// }

	// public long getItemId(int position)
	// {
	// return position;
	// }
	public boolean isShowLogo() {
		return bShowLogo;
	}

	public void setShowLogo(boolean bShowLogo) {
		this.bShowLogo = bShowLogo;
	}

	public View getView(int position, View convertView, ViewGroup viewgroup) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater mLI = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (nFlag == 1) {
				convertView = mLI.inflate(R.layout.listitem_app, null);
			} else {
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
			viewHolder.mCB.setVisibility(View.VISIBLE);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		AppBean temp = getItem(position);
		if (temp != null) {
			// viewHolder.mDV.setText("大小:" + temp.getSTotalsize());
			int nSdcard = temp.getFlags()
					& ApplicationInfo.FLAG_EXTERNAL_STORAGE;// 判断是否存放在SD卡上.
			if (nSdcard != 0) {
				viewHolder.mISDV.setVisibility(View.VISIBLE);
			} else {
				viewHolder.mISDV.setVisibility(View.GONE);
			}
			//viewHolder.mDV.setText(temp.getVersionName());
			viewHolder.mDV.setText("包名:"+temp.getPackageName()+"\n版本:"+temp.getVersionName());
			viewHolder.mTV.setText(temp.getAppName());
			viewHolder.mCB.setChecked(temp.isChecked());
			viewHolder.mCB
					.setOnClickListener(new OnKCheckedChangeListener(temp));
			if (temp.getAppIco() == null) {
				if (bShowLogo) {
					// mpkgSizeObserver.Start(temp, notify);by wmh 获取应用程序文件大小
					Drawable draw = null;
					draw = mLogoImage.loadDrawable(temp, new KImageCallback(
							temp, viewHolder));
					// draw = mLogoImage.loadApp(mContext,
					// temp.getPackageName());
					if (draw != null) {
						viewHolder.mIV.setImageDrawable(draw);
						temp.setAppIco(draw);
					} else {
						viewHolder.mIV.setImageDrawable(mApp);
					}
				} else {
					viewHolder.mIV.setImageDrawable(mApp);
				}
			} else {
				viewHolder.mIV.setImageDrawable(temp.getAppIco());
			}
		}
		// if (nFlag == 1 || bSelect)
		// {
		// viewHolder.mCB.setVisibility(View.VISIBLE);
		// }
		// else
		// {
		// viewHolder.mCB.setVisibility(View.GONE);
		// }
		return convertView;
	}

	class KImageCallback implements ImageCallback {
		ViewHolder viewHolder;
		AppBean bean;

		public KImageCallback(AppBean bean, ViewHolder viewHolder) {
			this.viewHolder = viewHolder;
			this.bean = bean;
		}

		public void imageLoaded(Drawable imageDrawable, String imageUrl) {
			// TODO Auto-generated method stub
			bean.setAppIco(imageDrawable);
			notifyDataSetChanged();
			// viewHolder.mIV.setImageDrawable(imageDrawable);
		}
	}

	class ViewHolder {
		ImageView mISDV; // 图标
		ImageView mIV; // 图标
		TextView mTV; // Title
		TextView mDV; // Desc
		CheckBox mCB; // 选择
	}

	class OnKCheckedChangeListener implements OnCheckedChangeListener,
			OnClickListener {
		AppBean tmpInfo;

		public OnKCheckedChangeListener(AppBean tmpInfo) {
			this.tmpInfo = tmpInfo;
		}

		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			tmpInfo.setChecked(isChecked);
			// TODO Auto-generated method stub
		}

		public void onClick(View v) {
			// TODO Auto-generated method stub
			CheckBox cb = (CheckBox) v;
			boolean isselect = cb.isChecked();
			tmpInfo.setChecked(isselect);
		}
	}

	class NotifyPackageSize implements INotifyDataSetChanged {

		public void NotifyDataSetChanged(int cmd, Object value) {
			// TODO Auto-generated method stub
			// PackageStats pStats = (PackageStats) value;
			// if(pStats.packageName.eq)
			// m_AppInfo.setCacheSize(pStats.cacheSize); // 缓存大小
			// m_AppInfo.setDataSize(pStats.dataSize); // 数据大小
			// m_AppInfo.setCodeSize(pStats.codeSize); // 应用程序大小
			mHandler.post(new Runnable() {

				public void run() {
					// TODO Auto-generated method stub
					notifyDataSetChanged();
				}
			});
		}
	}
}