package com.kenny.file.Adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.kenny.KFileManager.R;
import com.kenny.file.bean.FileBean;
import com.kenny.file.util.Res;
import com.kenny.file.util.Theme;

/** 自定义Adapter内部 */
public class FolderAdapter extends BaseAdapter
{
   protected Context mContext;
   protected List<FileBean> mFileList;
   protected String go_back;

   public FolderAdapter(Context context, List<FileBean> mFileList)
   {
      mContext = context;
      this.mFileList = mFileList;
      go_back = context.getString(R.string.back_previous);
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
         return getListView(position, convertView, viewgroup);
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
         convertView.setTag(viewHolder);
         viewHolder.mCB = (CheckBox) convertView.findViewById(R.id.cbChecked);
         viewHolder.mCB.setVisibility(View.GONE);
         
      }
      else
      {
         viewHolder = (ViewHolder) convertView.getTag();
      }
      
      FileBean temp = mFileList.get(position);
      viewHolder.mTV.setTextColor(Theme.getTextColor());
      viewHolder.mTD.setTextColor(Theme.getTextColor());
      convertView.setBackgroundColor(Theme.getBackgroundColor());
      if (temp.getFileName().equals(".."))
      {
         viewHolder.mIV.setImageDrawable(Res.getInstance(mContext).getBackUp());
         viewHolder.mTV.setText(go_back);
         viewHolder.mTD.setVisibility(View.GONE);
      }
      else
      {
         viewHolder.mTD.setVisibility(View.VISIBLE);
         String fileName = temp.getFileName();
         viewHolder.mTV.setText(fileName);
         
         if (temp.isDirectory())
         {
	  viewHolder.mIV.setImageDrawable(temp.getFileIco(mContext));
         }
         viewHolder.mTD.setText(temp.getDesc());
      }
      return convertView;
   }
   
   protected class ViewHolder
   {
      public ImageView mIV; // image
      public TextView mTV; // title
      public TextView mTD; // desc
      public CheckBox mCB; // 选择
   }
}