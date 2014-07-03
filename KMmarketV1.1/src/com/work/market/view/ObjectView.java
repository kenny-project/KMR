package com.work.market.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public abstract class ObjectView extends LinearLayout 
{
	
	public ObjectView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public ObjectView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public abstract void onResume();


	public abstract void onPause();

}
