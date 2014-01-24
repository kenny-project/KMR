package com.kenny.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kenny.Application.KApp;
import com.kenny.dailyenglish.R;
public class VOAContentAdapter extends ArrayAdapter<String>
{
    private final LayoutInflater mInflater;
    private int TextColor = 0xff000000;
    //private AsyncIcoLoader imageLoader = null;
    private ViewHolder viewHolder = null;
    private Context context;
    public VOAContentAdapter(Context context, List<String> apps)
    {
        super(context, 0, apps);
        mInflater = LayoutInflater.from(context);
        this.context = context;
		//imageLoader = AsyncIcoLoader.GetObject(context);
    }
	class ViewHolder 
	{
		com.kenny.comui.CircleImageButton ItemImage; // 图标
		TextView ItemTitle; // Title
		TextView ItemDesc; // Desc
		TextView ItemDate; // Desc
		TextView ItemViews; // Desc
	}
	private int nSelectPos=-1;
	public void setSelectItem(int pos)
	{
		nSelectPos=pos;
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
    	if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listitem_voacontent,
					parent, false);

			viewHolder.ItemImage = (com.kenny.comui.CircleImageButton) convertView
					.findViewById(R.id.ItemImage);
			viewHolder.ItemImage.setTouchable(false);
			viewHolder.ItemTitle = (TextView) convertView
					.findViewById(R.id.ItemTitle);
			viewHolder.ItemDate = (TextView) convertView
					.findViewById(R.id.ItemDate);
			viewHolder.ItemViews = (TextView) convertView
					.findViewById(R.id.ItemViews);
			viewHolder.ItemDesc = (TextView) convertView
					.findViewById(R.id.ItemDesc);
			viewHolder.ItemTitle.setTextColor(TextColor);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		String bean = this.getItem(position);
		viewHolder.ItemTitle.setText(bean);
//		viewHolder.ItemDesc.setText(bean.getDesc());
		KApp app = (KApp) context.getApplicationContext();
		int color = app.colorFactory.getColor();
		if(nSelectPos==position)
		{
			viewHolder.ItemTitle.setTextColor(color);
		}
		else
		{
			viewHolder.ItemTitle.setTextColor(Color.BLACK);	
		}
		viewHolder.ItemDate.setTextColor(color);
		viewHolder.ItemViews.setTextColor(color);
		return convertView;
    }
}
