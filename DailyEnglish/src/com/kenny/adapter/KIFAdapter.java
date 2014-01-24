package com.kenny.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kenny.dailyenglish.R;
import com.kenny.data.FavoriteBean;
import com.kenny.data.FavoriteGroupBean;

/**
 * 主页面列表
 * 
 * @author WangMinghui
 * 
 */
public class KIFAdapter extends BaseExpandableListAdapter
{
	private final LayoutInflater mInflater;
	private ArrayList<FavoriteGroupBean> sapps = null;
	private boolean isDelVisible = false;

	public void SetDelVisible(boolean isDelVisible)
	{
		this.isDelVisible = isDelVisible;
	}

	public boolean GetDelVisible()
	{
		return isDelVisible;
	}

	public KIFAdapter(Context context, ArrayList<FavoriteGroupBean> apps)
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
	public FavoriteGroupBean getGroup(int groupPosition)
	{
		// TODO Auto-generated method stub
		return sapps.get(groupPosition);
	}

	@Override
	public FavoriteBean getChild(int groupPosition, int childPosition)
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
			convertView = mInflater.inflate(R.layout.listitem_favorite_group, parent,
					false);
		}
		TextView ItemTitle = (TextView) convertView
				.findViewById(R.id.text);
		ItemTitle.setText(getGroup(groupPosition)
				.getTitle());
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent)
	{
		FavoriteBean bean = getChild(groupPosition,
				childPosition);
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.listitem_favorite_item, parent,
					false);
		}
        TextView ItemTitle = (TextView) convertView
                .findViewById(R.id.ItemTitle);
        Log.v("wmh", "bean.getTitle()"+bean.getTitle());
        ItemTitle.setText(bean.getTitle());
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		return true;
	}
}
