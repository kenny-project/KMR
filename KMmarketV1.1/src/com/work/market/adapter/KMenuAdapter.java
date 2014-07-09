package com.work.market.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.byfen.market.R;
import com.work.market.bean.KMenuGroupBean;
import com.work.market.bean.KMenuItemBean;

/**
 * 主页面列表
 * 
 * @author WangMinghui
 * 
 */
public class KMenuAdapter extends BaseExpandableListAdapter
{
	private final LayoutInflater mInflater;
	private ArrayList<KMenuGroupBean> sapps = null;

	public KMenuAdapter(Activity context, ArrayList<KMenuGroupBean> apps)
	{
		mInflater = LayoutInflater.from(context);
		this.sapps = apps;
	}

	@Override
	public int getGroupCount()
	{
		return sapps.size();
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		// TODO Auto-generated method stub
		return sapps.get(groupPosition).ItemSize();
	}

	@Override
	public Object getGroup(int groupPosition)
	{
		// TODO Auto-generated method stub
		return sapps.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition)
	{
		// TODO Auto-generated method stub
		return sapps.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		// TODO Auto-generated method stub
		return sapps.get(groupPosition).getID();
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		// TODO Auto-generated method stub
		return sapps.get(groupPosition).get(childPosition).getID();
	}

	@Override
	public boolean hasStableIds()
	{
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent)
	{
		// TODO Auto-generated method stub
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.listitem_dic_group,
					parent, false);
		}
		TextView ItemTitle = (TextView) convertView.findViewById(R.id.title);
		ItemTitle
				.setText(((KMenuGroupBean) getGroup(groupPosition)).getTitle());
		return convertView;
	}

	class ViewHolder
	{
		ImageView ItemImage;
		TextView ItemTitle;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent)
	{

		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.listitem_dic_child,
					parent, false);
			viewHolder = new ViewHolder();
			viewHolder.ItemImage = (ImageView) convertView
					.findViewById(R.id.ItemImage);
			viewHolder.ItemTitle = (TextView) convertView
					.findViewById(R.id.ItemTitle);
			convertView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		KMenuItemBean bean = (KMenuItemBean) getChild(groupPosition,
				childPosition);

		viewHolder.ItemTitle.setText(bean.getTitle());
		viewHolder.ItemImage.setImageResource(bean.getImgID());
		// viewHolder.ItemImage.setImageResource(R.drawable.dic_product);
		// if (bean.getLogoDrawable() == null)
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		// TODO Auto-generated method stub
		return true;
	}

}
