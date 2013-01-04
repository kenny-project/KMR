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

import com.kenny.KFileManager.R;
import com.kenny.file.bean.FileBean;
import com.kenny.file.util.Res;
import com.kenny.file.util.Theme;

/** 自定义Adapter内部 */
public class FavorFileAdapter extends OFileAdapter
{
   public FavorFileAdapter(Context context, int nFlag, List<FileBean> mFileList)
   {
      super(context, nFlag, mFileList);
   }
   
   public class FavViewHolder extends ViewHolder
   {
      public TextView mPath; // 路径
   }
   
   public View getListView(int position, View convertView, ViewGroup viewgroup)
   {
      FavViewHolder viewHolder = null;
      if (convertView == null)
      {
         viewHolder = new FavViewHolder();
         LayoutInflater mLI = (LayoutInflater) mContext
	     .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         
         convertView = mLI.inflate(R.layout.listitem_favor_item, null);
         viewHolder.mIV = (ImageView) convertView
	     .findViewById(R.id.image_list_childs);
         viewHolder.mTV = (TextView) convertView.findViewById(R.id.tvTitle);
         viewHolder.mTD = (TextView) convertView.findViewById(R.id.tvDesc);
         viewHolder.mPath = (TextView) convertView.findViewById(R.id.tvPath);
         
         convertView.setTag(viewHolder);
         viewHolder.mCB = (CheckBox) convertView.findViewById(R.id.cbChecked);
         
      }
      else
      {
         viewHolder = (FavViewHolder) convertView.getTag();
      }
      FileBean temp = mFileList.get(position);
      
      if (temp.getFileName().equals(".."))
      {
         viewHolder.mIV.setImageDrawable(Res.getInstance(mContext).getBackUp());
         viewHolder.mTV.setText(go_back);
         viewHolder.mTD.setVisibility(View.GONE);
         viewHolder.mCB.setVisibility(View.GONE);
         viewHolder.mPath.setVisibility(View.GONE);
         convertView.setBackgroundColor(Theme.getBackgroundColor());
      }
      else
      {
         if(temp.isChecked())
         {
	  convertView.setBackgroundColor(Theme.getListSelectedBackgroundColor());
         }
         else
         {
	  convertView.setBackgroundColor(Theme.getBackgroundColor());
         }
         viewHolder.mTD.setVisibility(View.VISIBLE);
         viewHolder.mCB.setVisibility(View.VISIBLE);
         viewHolder.mCB.setChecked(temp.isChecked());
         viewHolder.mCB.setOnClickListener(new OnKCheckedChangeListener(convertView,temp));
         String fileName = temp.getFileName();
         viewHolder.mTV.setText(fileName);
         
         viewHolder.mTD.setText(temp.getDesc());
         viewHolder.mPath.setText(temp.getFolderPath());
         
         if (temp.isDirectory())
         {
	  viewHolder.mIV.setImageDrawable(temp.getFileIco(mContext));
         }
         else
         {
	  Drawable draw = null;
	  if (bShowLogo)
	  {
	     //P.debug("bShowLogo=" + bShowLogo);
	     String fileEnds = temp.getFileEnds();
	     if (fileEnds.equals("jpg") || fileEnds.equals("gif")
		 || fileEnds.equals("png") || fileEnds.equals("jpeg")
		 || fileEnds.equals("bmp"))
	     {
	        draw = mLogoImage.loadDrawable(temp, new KImageCallback(
		    viewHolder));
	     }
	     else if (fileEnds.equals("apk"))
	     {
	        draw = mLogoImage.loadDrawable(temp, new KImageCallback(
		    viewHolder));
	     }
	  }
	  if (draw != null)
	  {
	     viewHolder.mIV.setImageDrawable(draw);
	  }
	  else
	  {
	     viewHolder.mIV.setImageDrawable(temp.getFileIco(mContext));
	  }
         }
      }
      return convertView;
   }
}