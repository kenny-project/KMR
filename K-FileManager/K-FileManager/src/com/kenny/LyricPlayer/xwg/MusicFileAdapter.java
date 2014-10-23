package com.kenny.LyricPlayer.xwg;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kenny.KFileManager.R;

public class MusicFileAdapter extends BaseAdapter

{
   private Context mContext;
   private List<LyricBean> mFileList;
   private int Index = 0;// 当前播放的索引号
   private int TextColorNor,TextColorSel;
   public void setSelectIndex(int Index)
   {
      this.Index = Index;
   }
   
   public MusicFileAdapter(Context context,List<LyricBean> mFileList)
   {
      mContext = context;
      this.mFileList = mFileList;
      TextColorNor=context.getResources().getColor(R.color.MusicFileAdapter_NormalItem);
      TextColorSel=context.getResources().getColor(R.color.MusicFileAdapter_SelectItem);
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
         convertView = mLI.inflate(R.layout.listitem_musicfile, null);
         viewHolder.mTV = (TextView) convertView.findViewById(R.id.tvTitle);
         convertView.setTag(viewHolder);
      }
      else
      {
         viewHolder = (ViewHolder) convertView.getTag();
      }
      LyricBean temp = mFileList.get(position);
      if (temp != null)
      {
         viewHolder.mTV.setText(temp.getMusic_title());
      }
      if (this.Index == position)
      {
         viewHolder.mTV.setTextColor(TextColorSel);
      }
      else
      {
         viewHolder.mTV.setTextColor(TextColorNor);
      }
      return convertView;
   }
   
   class ViewHolder
   {
      TextView mTV; // Title
   }
}