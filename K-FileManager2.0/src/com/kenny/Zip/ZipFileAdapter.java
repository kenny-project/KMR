package com.kenny.Zip;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
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

import com.kenny.KFileManager.t.R;
import com.kenny.file.Image.ImageLoader;
import com.kenny.file.bean.FileBean;
import com.kenny.file.bean.ZipFileBean;
import com.kenny.file.interfaces.ImageCallback;
import com.kenny.file.util.Res;

/** 自定义Adapter内部 */
public class ZipFileAdapter extends BaseAdapter
{
        private Context           mContext;
        private List<ZipFileBean> mFileList;
        private int               nFlag   = 1;    // 窗体标记:1:ListView 2:GridView
        private boolean           bSelect = false; // true: 显示 false:不显示
	                                         
        public boolean isSelected()
        {
	      return bSelect;
        }
        
        public void setSelected(boolean bSelect)
        {
	      this.bSelect = bSelect;
        }
        
        public ZipFileAdapter(Context context, int nFlag,
	              List<ZipFileBean> mFileList)
        {
	      mContext = context;
	      this.nFlag = nFlag;
	      if (nFlag == 1)
	      {
		    bSelect = true;
	      }
	      this.mFileList = mFileList;
        }
        
        public int getCount()
        {
	      return mFileList.size();
        }
        
        public Object getItem(int position)
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
	      ViewHolder viewHolder = null;
	      if (convertView == null)
	      {
		    viewHolder = new ViewHolder();
		    LayoutInflater mLI = (LayoutInflater) mContext
			          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
		    if (nFlag == 1)
		    {
			  convertView = mLI.inflate(
				        R.layout.listitem_local, null);
		    }
		    else
		    {
			  convertView = mLI.inflate(
				        R.layout.gridview_local, null);
		    }
		    viewHolder.mIV = (ImageView) convertView
			          .findViewById(R.id.image_list_childs);
		    viewHolder.mTV = (TextView) convertView
			          .findViewById(R.id.tvTitle);
		    viewHolder.mTD = (TextView) convertView
			          .findViewById(R.id.tvDesc);
		    convertView.setTag(viewHolder);
		    viewHolder.mCB = (CheckBox) convertView
			          .findViewById(R.id.cbChecked);
		    viewHolder.mCB.setVisibility(View.GONE);
	      }
	      else
	      {
		    viewHolder = (ViewHolder) convertView.getTag();
	      }
	      
	      FileBean temp = mFileList.get(position);
	      
	      if (temp.getFileName().equals(".."))
	      {
		    viewHolder.mIV.setImageDrawable(Res.getInstance(mContext)
			          .getBackUp());
		    viewHolder.mTV.setText(mContext.getString(R.string.back_previous));
		    viewHolder.mTD.setText("");
		    viewHolder.mCB.setVisibility(View.GONE);
	      }
	      else
	      {
		    String fileName = temp.getFileName();
		    viewHolder.mTV.setText(fileName);
		    viewHolder.mIV.setImageDrawable(temp.getFileIco(mContext));
		    viewHolder.mTD.setText(temp.getDesc());
	      }
	      return convertView;
        }
        
        class ViewHolder
        {
	      ImageView mIV; // image
	      TextView  mTV; // title
	      TextView  mTD; // desc
	      CheckBox  mCB; // 选择
        }
}