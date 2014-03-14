package com.kenny.file.Adapter;

import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.framework.event.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Application.KFileManagerApp;
import com.kenny.file.Image.ImageLoader;
import com.kenny.file.bean.FileBean;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.interfaces.ImageCallback;
import com.kenny.file.util.Const;
import com.kenny.file.util.FolderTypeUtil;
import com.kenny.file.util.Res;
import com.kenny.file.util.Theme;
import com.umeng.analytics.MobclickAgent;

/** 自定义Adapter内部 */
public class FileAdapter extends BaseAdapter
{
	protected Context mContext;
	protected List<FileBean> mFileList;
	protected ImageLoader mLogoImage;
	protected Hashtable<String, FileBean> mSelectList = new Hashtable<String, FileBean>();
	protected int nFlag = 1; // 窗体标记:1:ListView 2:GridView
	protected boolean bShowLogo = true; // 是否启动加载后台图片
	protected String go_back;
	protected INotifyDataSetChanged notify;
	private FolderTypeUtil mFolderType;
	protected Drawable im_go_back;

	/**
	 * // 窗体标记:1:ListView 2:GridView
	 * 
	 * @param context
	 * @param nFlag
	 * @param mFileList
	 * @param notify
	 */
	public FileAdapter(Context context, int nFlag, List<FileBean> mFileList,
			INotifyDataSetChanged notify)
	{
		mContext = context.getApplicationContext();
		this.nFlag = nFlag;
		this.notify = notify;
		this.mFileList = mFileList;
		go_back = context.getString(R.string.back_previous);
		im_go_back = Res.getInstance(mContext).getBackUp();
		mLogoImage = ImageLoader.getInstance(mContext);
		mFolderType = ((KFileManagerApp) mContext).getFolderType();
	}

	public void notifyDataSetChanged()
	{
		bShowLogo = true;
		if (mFileList.size() > 0)
		{
			FileBean bean = mFileList.get(0);
			setCurrentPath(bean.getFile().getParent());
		}
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
			viewHolder.mBackground = (ImageView) convertView
					.findViewById(R.id.rlBackground);

		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (viewHolder.ThemeMode != Theme.getThemeMode())
		{
			viewHolder.mTV.setTextColor(Theme.getTextColor());
			viewHolder.mTD.setTextColor(Theme.getTextColor());
			viewHolder.mBackground.setBackgroundResource(Theme
					.getSelBackgroundResource());
			convertView.setBackgroundResource(Theme.getBackgroundResource());
		}
		FileBean temp = mFileList.get(position);

		if (temp.getFileName().equals(".."))
		{
			viewHolder.mIV.setImageDrawable(im_go_back);
			viewHolder.mTV.setText(go_back);
			viewHolder.mCB.setVisibility(View.GONE);
			viewHolder.mBackground.setVisibility(View.GONE);
		} else
		{
			viewHolder.mCB.setVisibility(View.VISIBLE);
			viewHolder.mCB.setChecked(temp.isChecked());
			viewHolder.mCB.setOnClickListener(new OnKCheckedChangeListener(
					viewHolder.mBackground, temp));
			if (temp.isChecked())
			{
				viewHolder.mBackground.setVisibility(View.VISIBLE);
			} else
			{
				viewHolder.mBackground.setVisibility(View.GONE);
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
					viewHolder.mIV.setTag(temp.getFilePath());
					String fileEnds = temp.getFileEnds();
					if (fileEnds.equals("jpg") || fileEnds.equals("gif")
							|| fileEnds.equals("png")
							|| fileEnds.equals("jpeg")
							|| fileEnds.equals("bmp"))
					{
						draw = mLogoImage.loadDrawable(temp,
								new KImageCallback(temp, viewHolder));
					} else if (fileEnds.equals("apk"))
					{
						draw = mLogoImage.loadDrawable(temp,
								new KImageCallback(temp, viewHolder));
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
		return convertView;
	}
	private String CurrentPath;
	/**
	 * 当前列表路径
	 * 
	 * @return
	 */
	public String getCurrentPath()
	{
		return CurrentPath;
	}

	public void setCurrentPath(String currentPath)
	{
		CurrentPath = currentPath;
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
			viewHolder.mBackground = (ImageView) convertView
					.findViewById(R.id.rlBackground);
			// LayoutParams params=viewHolder.mBackground.getLayoutParams();
			// params.height=convertView.getHeight();

			viewHolder.mCB = (CheckBox) convertView
					.findViewById(R.id.cbChecked);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (viewHolder.ThemeMode != Theme.getThemeMode())
		{
			viewHolder.mTV.setTextColor(Theme.getTextColor());
			viewHolder.mTD.setTextColor(Theme.getTextColor());
			viewHolder.mBackground.setBackgroundResource(Theme
					.getSelBackgroundResource());
			convertView.setBackgroundResource(Theme.getBackgroundResource());
			viewHolder.ThemeMode = Theme.getThemeMode();
		}
		
		FileBean temp = mFileList.get(position);

		if (temp.getFileName().equals(".."))
		{
			viewHolder.mIV.setImageDrawable(im_go_back);
			viewHolder.mTV.setText(go_back);
			viewHolder.mTD.setVisibility(View.GONE);
			viewHolder.mCB.setVisibility(View.GONE);
			viewHolder.mBackground.setVisibility(View.GONE);
		} else
		{
			viewHolder.mTD.setVisibility(View.VISIBLE);
			viewHolder.mCB.setVisibility(View.VISIBLE);
			viewHolder.mCB.setChecked(temp.isChecked());
			// by wmh 长按后没有选择的BUG
			if (temp.isChecked())
			{

				viewHolder.mBackground.setVisibility(View.VISIBLE);
			} else
			{
				viewHolder.mBackground.setVisibility(View.GONE);
			}
			viewHolder.mCB.setOnClickListener(new OnKCheckedChangeListener(
					viewHolder.mBackground, temp));

			if (temp.isDirectory())
			{
				viewHolder.mIV.setImageDrawable(temp.getFileIco(mContext));
				if (temp.getNickName() == null)
				{
					if (getCurrentPath() != null
							&& getCurrentPath().equals(Const.getSDCard()))
					{
						String nickname = mFolderType.BinarySearch(temp
								.getFile().getName());
						if (nickname != null)
						{
							temp.setNickName(nickname);
						} else
						{
							temp.setNickName("");
							if (Res.getInstance(mContext).isFirstRun())
							{
								MobclickAgent.onEvent(mContext, "FA",
										temp.getFileName());
								Log.v("wmh", "FA:" + temp.getFileName());
							}
						}
					}
					// else
					// {
					// temp.setNickName("");
					// }
				}
			} else
			{
				Drawable draw = null;
				if (bShowLogo)
				{
					viewHolder.mIV.setTag(temp.getFilePath());
					String fileEnds = temp.getFileEnds();
					if (fileEnds.equals("jpg") || fileEnds.equals("gif")
							|| fileEnds.equals("png")
							|| fileEnds.equals("jpeg")
							|| fileEnds.equals("bmp"))
					{
						draw = mLogoImage.loadDrawable(temp,
								new KImageCallback(temp, viewHolder));
					}
					else if (fileEnds.equals("apk"))
					{
						draw = mLogoImage.loadDrawable(temp,
								new KImageCallback(temp, viewHolder));
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
			viewHolder.mTV.setText(temp.getFileName());
			viewHolder.mTD.setText(temp.getDesc());
		}
		return convertView;
	}

	protected class ViewHolder
	{
		public int ThemeMode = -1;// 当前样式
		public ImageView mIV; // image
		public TextView mTV; // title
		public TextView mTD; // desc
		public CheckBox mCB; // 选择
		private ImageView mBackground;
	}

	protected class KImageCallback implements ImageCallback
	{
		private ViewHolder viewHolder;
		private FileBean bean;

		public KImageCallback(FileBean bean, ViewHolder viewHolder)
		{
			this.viewHolder = viewHolder;
			this.bean = bean;
		}

		private boolean isRefurbish = false;

		public void imageLoaded(final Drawable imageDrawable,final  String imageUrl)
		{
			// viewHolder.mTD.setText(Html.fromHtml(bean.getDesc()));
			// viewHolder.mIV.setImageDrawable(imageDrawable);
			String url=(String)viewHolder.mIV.getTag();
			if(url.equals(imageUrl))
			{
				SysEng.getInstance().addHandlerEvent(new AbsEvent()
				{
					@Override
					public void ok()
					{
				viewHolder.mTD.setText(bean.getDesc());
				viewHolder.mIV.setImageDrawable(imageDrawable);
			}
				});
			}
			//定时刷新
//			if (false == isRefurbish)
//			{
//				isRefurbish = true;
//				SysEng.getInstance().addHandlerEvent(new AbsEvent()
//				{
//					@Override
//					public void ok()
//					{
//						isRefurbish = false;
//						notifyDataSetChanged();
//					}
//				}, 500);
//			}
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
			CheckBox cb = (CheckBox) v;
			boolean isselect = cb.isChecked();
			tmpInfo.setChecked(isselect);
			if (isselect)
			{
				convertView.setVisibility(View.VISIBLE);
				mSelectList.put(tmpInfo.getFileName(), tmpInfo);
				if (notify != null)
				{
					notify.NotifyDataSetChanged(Const.cmd_Local_List_Selected,
							tmpInfo);
				}
			} else
			{
				convertView.setVisibility(View.GONE);
				mSelectList.remove(tmpInfo.getFileName());
				if (mSelectList.size() == 0)
				{
					if (notify != null)
					{
						notify.NotifyDataSetChanged(
								Const.cmd_Local_List_UnSelected, tmpInfo);
					}
				}
			}
		}
	}
}