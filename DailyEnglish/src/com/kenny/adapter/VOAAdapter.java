package com.kenny.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.kenny.Application.KApp;
import com.kenny.dailyenglish.R;
import com.kenny.data.VOANetData;
import com.kenny.util.Const;

public class VOAAdapter extends ArrayAdapter<VOANetData>
{
    private final LayoutInflater mInflater;
    private int TextColor = 0xff000000;
    //private AsyncIcoLoader imageLoader = null;
    private ViewHolder viewHolder = null;
    private Context context;
	private boolean bSubscribe=false;
	private String  views;
	public boolean isSubscribe() {
		return bSubscribe;
	}

	public void setSubscribe(boolean bSubscribe) 
	{
		this.bSubscribe = bSubscribe;
	}

    public VOAAdapter(Context context, ArrayList<VOANetData> apps)
    {
        super(context, 0, apps);
        mInflater = LayoutInflater.from(context);
        this.context = context;
        views=context.getString(R.string.voa_adapter_browse);
		//imageLoader = AsyncIcoLoader.GetObject(context);
    }
	class ViewHolder {
		com.kenny.comui.CircleImageButton ItemImage; // 图标
		TextView ItemTitle; // Title
		TextView ItemDesc; // Desc
		TextView ItemDate; // Desc
		TextView ItemViews; // Desc
	}

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
    	if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.listitem_bilingual,
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
    	VOANetData bean = this.getItem(position);
    	if(bSubscribe)
    	{
    		viewHolder.ItemImage.setImageResource(R.drawable.listview_voa_true);
    	}
    	else
    	{
    		if(bean.isIsfree())
    		{
    			viewHolder.ItemImage.setImageResource(R.drawable.listview_voa_true);
    		}
    		else
    		{
    			viewHolder.ItemImage.setImageResource(R.drawable.listview_voa_false);	
    		}
    	}
    	
		viewHolder.ItemTitle.setText(bean.getCntitle());
		viewHolder.ItemDesc.setText(bean.getTitle());
		KApp app = (KApp) context.getApplicationContext();
		viewHolder.ItemTitle.setTextColor(app.colorFactory.getColor());
		
		viewHolder.ItemDate.setTextColor(app.colorFactory.getColor());
		viewHolder.ItemViews.setTextColor(app.colorFactory.getColor());
//		viewHolder.ItemImage.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		});
		
		viewHolder.ItemDate.setText(bean.getPublish());
		viewHolder.ItemViews.setText(bean.getViews()+views);
		return convertView;
    }
}
