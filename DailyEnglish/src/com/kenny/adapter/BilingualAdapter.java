package com.kenny.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kenny.Application.KApp;
import com.kenny.dailyenglish.R;
import com.kenny.data.BilingualBean;
import com.kenny.util.ColorFactory;

public class BilingualAdapter extends ArrayAdapter<BilingualBean>
{
    private final LayoutInflater mInflater;
    private int TextColor = 0xff000000;
    //private AsyncIcoLoader imageLoader = null;
    private ViewHolder viewHolder = null;
    private Context context;
    public BilingualAdapter(Context context, ArrayList<BilingualBean> apps)
    {
        super(context, 0, apps);
        mInflater = LayoutInflater.from(context);
        this.context = context;
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
		BilingualBean bean = this.getItem(position);
		viewHolder.ItemTitle.setText(bean.getTitle());
		viewHolder.ItemDesc.setText(bean.getDesc());
		KApp app = (KApp) context.getApplicationContext();
		int color = app.colorFactory.getColor();
		viewHolder.ItemTitle.setTextColor(color);
		
		viewHolder.ItemDate.setTextColor(color);
		viewHolder.ItemViews.setTextColor(color);
//		viewHolder.ItemImage.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				return false;
//			}
//		});
		viewHolder.ItemDate.setText(bean.getDate());
		viewHolder.ItemViews.setText(bean.getViews()+"次浏览");
		return convertView;
    }
}
