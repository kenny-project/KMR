package com.kenny.file.Adapter;

import java.util.List;

import com.kenny.KFileManager.t.R;
import com.kenny.file.bean.TreeElement;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TreeViewAdapter extends ArrayAdapter
{
   private LayoutInflater mInflater;
   private List<TreeElement> mTreelist;
   private Bitmap mIconCollapse;
   private Bitmap mIconExpand;
   
   public TreeViewAdapter(Context context, int textViewResourceId, List objects)
   {
      super(context, textViewResourceId, objects);
      mInflater = LayoutInflater.from(context);
      mTreelist = objects;
      mIconCollapse = BitmapFactory.decodeResource(context.getResources(),
	  R.drawable.outline_list_collapse);
      mIconExpand = BitmapFactory.decodeResource(context.getResources(),
	  R.drawable.outline_list_expand);
      
   }
   
   public int getCount()
   {
      return mTreelist.size();
   }
   
   public Object getItem(int position)
   {
      return position;
   }
   
   public long getItemId(int position)
   {
      return position;
   }
   
   public View getView(int position, View convertView, ViewGroup parent)
   {
      ViewHolder holder;
      /* if (convertView == null) { */
      convertView = mInflater.inflate(R.layout.outline, null);
      holder = new ViewHolder();
      holder.text = (TextView) convertView.findViewById(R.id.text);
      holder.icon = (ImageView) convertView.findViewById(R.id.icon);
      holder.itemLogo = (ImageView) convertView.findViewById(R.id.itemLogo);
      convertView.setTag(holder);
      /*
       * } else { holder = (ViewHolder) convertView.getTag(); }
       */
      final TreeElement obj = mTreelist.get(position);
      holder.itemLogo.setImageDrawable(obj.getFileIco(getContext()));
//      holder.text.setOnClickListener(new View.OnClickListener()
//      {
//         @Override
//         public void onClick(View v)
//         {
//	  System.out.println(obj.getFileName());
//         }
//      });
      
      int level = obj.getLevel();
      holder.icon.setPadding(50 * (level + 1), holder.icon.getPaddingTop(), 0,
	  holder.icon.getPaddingBottom());
      holder.text.setText(obj.getFileName());
      if (obj.isChildFolder())
      {
         if (obj.isExpanded() == false)
         {
	  holder.icon.setImageBitmap(mIconCollapse);
         }
         else
         {
	  holder.icon.setImageBitmap(mIconExpand);
         }
      }
      else
      {
         holder.icon.setImageBitmap(mIconCollapse);
         holder.icon.setVisibility(View.INVISIBLE);
      }
      return convertView;
   }
   
   class ViewHolder
   {
      TextView text;
      ImageView icon;
      ImageView itemLogo;
   }
}
