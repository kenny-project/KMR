package com.kenny.comui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

/**
 * @author aimery
 * */
public class KsExpandableListView extends ExpandableListView implements OnChildClickListener
{

	private OnChildClickListener kslistener;

	public void setKsOnChildClickListener(OnChildClickListener l)
	{
		kslistener=l;	
	}
	private void setListener()
	{
		setOnChildClickListener(this);
	}
	public KsExpandableListView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		setListener();
		//this.setChildDivider(context.getResources().getDrawable(R.drawable.ks_list_divider));
		this.setDividerHeight(1);
	}

	public KsExpandableListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setListener();
		//this.setChildDivider(context.getResources().getDrawable(R.drawable.ks_list_divider));
		this.setDividerHeight(1);
	}

	public KsExpandableListView(Context context, AttributeSet attrs,
			int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		setListener();
		//this.setChildDivider(context.getResources().getDrawable(R.drawable.ks_list_divider));
		this.setDividerHeight(1);
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id)
	{
		// TODO Auto-generated method stub
//		if(kslistener!=null&&System.currentTimeMillis()-Const.clicktime>Const.de_time)
//		{
//			Const.clicktime=System.currentTimeMillis();
//			return kslistener.onChildClick(parent, v, groupPosition, childPosition, id);
//		}
		return false;
	}

}
