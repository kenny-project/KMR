package com.kenny.file.Adapter;

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

import com.kenny.KFileManager.R;
import com.kenny.file.struct.ImageCallback;
import com.kenny.file.util.Res;
import com.kuaipan.client.model.KuaipanFile;

/** 自定义Adapter内部 */
public class KuaiPanFileAdapter extends BaseAdapter
{
   protected Context mContext;
   protected List<KuaipanFile> mFileList;
   protected String go_back;
   
   public void notifyDataSetChanged()
   {
      super.notifyDataSetChanged();
   }
   
   public KuaiPanFileAdapter(Context context, List<KuaipanFile> mFileList)
   {
      mContext = context;
      this.mFileList = mFileList;
      go_back = context.getString(R.string.back_previous);
   }
   
   public int getCount()
   {
      return mFileList.size();
   }
   
   public KuaipanFile getItem(int position)
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
         
         convertView = mLI.inflate(R.layout.listitem_kuaipan, null);
         viewHolder.mIV = (ImageView) convertView
	     .findViewById(R.id.image_list_childs);
         viewHolder.mEIV = (ImageView) convertView
	     .findViewById(R.id.image_list_exists);
         viewHolder.mTV = (TextView) convertView.findViewById(R.id.tvTitle);
         viewHolder.mTD = (TextView) convertView.findViewById(R.id.tvDesc);
         convertView.setTag(viewHolder);
         viewHolder.mCB = (CheckBox) convertView.findViewById(R.id.cbChecked);
         viewHolder.lyTools=convertView.findViewById(R.id.lyTools);
      }
      else
      {
         viewHolder = (ViewHolder) convertView.getTag();
      }
      KuaipanFile temp = mFileList.get(position);
      
      if (temp.getFileName().equals(".."))
      {
         viewHolder.mIV.setImageDrawable(Res.getInstance(mContext).getBackUp());
         viewHolder.mTV.setText(go_back);
         viewHolder.mTD.setVisibility(View.GONE);
         viewHolder.mCB.setVisibility(View.GONE);
         viewHolder.mEIV.setVisibility(View.GONE);
      }
      else
      {
         viewHolder.mTD.setVisibility(View.VISIBLE);
         viewHolder.mCB.setVisibility(View.VISIBLE);
         viewHolder.mCB.setChecked(temp.isChecked());
//         if(viewHolder.mCB.isChecked())
//         {
//	  viewHolder.lyTools.setVisibility(View.VISIBLE);
//         }
//         else
//         {
//	  viewHolder.lyTools.setVisibility(View.GONE);
//         }
         viewHolder.mCB.setOnClickListener(new OnKCheckedChangeListener(viewHolder,temp));
         viewHolder.mTV.setText(temp.getFileName());
         viewHolder.mIV.setImageDrawable(temp.getFileIco(mContext));
         if (temp.isDirectory())
         {
	  viewHolder.mTD.setVisibility(View.GONE);
	  viewHolder.mEIV.setVisibility(View.GONE);
         }
         else
         {
	  viewHolder.mTD.setText(temp.getDesc());
	  viewHolder.mTD.setVisibility(View.VISIBLE);
	  if (temp.exists())
	  {
	     viewHolder.mEIV.setVisibility(View.VISIBLE);
	  }
	  else
	  {
	     viewHolder.mEIV.setVisibility(View.GONE);
	  }
         }
         
      }
      return convertView;
   }
   
   protected class ViewHolder
   {
      public ImageView mIV; // image
      public ImageView mEIV; // image
      public TextView mTV; // title
      public TextView mTD; // desc
      public CheckBox mCB; // 选择
      public View lyTools;//工具栏
   }
   
   protected class KImageCallback implements ImageCallback
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
   
   protected class OnKCheckedChangeListener implements OnCheckedChangeListener,
         OnClickListener
   {
      KuaipanFile tmpInfo;
      ViewHolder holder;
      public OnKCheckedChangeListener(ViewHolder holder,KuaipanFile tmpInfo)
      {
         this.tmpInfo = tmpInfo;
         this.holder=holder;
      }
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
         tmpInfo.setChecked(isChecked);
      }
      
      
      public void onClick(View v)
      {
         // TODO Auto-generated method stub
         CheckBox cb = (CheckBox) v;
         boolean isselect = cb.isChecked();
         tmpInfo.setChecked(isselect);
//         if(holder.mCB.isChecked())
//         {
//	  holder.lyTools.setVisibility(View.VISIBLE);
//         }
//         else
//         {
//	  holder.lyTools.setVisibility(View.GONE);
//         }
      }
   }
}