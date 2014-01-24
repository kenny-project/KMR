package com.kenny.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kenny.dailyenglish.R;
import com.kenny.data.PushMsgBean;
import com.kenny.util.ColorFactory;

/**
 * 主页面列表
 * 
 * @author WangMinghui
 * 
 */
public class KPushMsgAdapter extends BaseAdapter
{
	private final LayoutInflater mInflater;
	private List<PushMsgBean> sapps = null;
	public KPushMsgAdapter(Context context, List<PushMsgBean> apps)
	{
		mInflater = LayoutInflater.from(context);
		this.sapps = apps;
	}
	@Override
	public View getView(int arg0, View convertView, ViewGroup parent)
	{
		PushMsgBean bean = (PushMsgBean)getItem(arg0);
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.listitem_pushmsg_item, parent,
					false);
		}
        TextView ItemTitle = (TextView) convertView
                .findViewById(R.id.ItemTitle);
        TextView ItemDesc = (TextView) convertView
                .findViewById(R.id.ItemDesc);
        ItemTitle.setText(bean.getTitle());
        ItemDesc.setText(bean.getDesc());
        if(bean.getIread()==0)
        {//未读
        	ItemTitle.setTextColor(Color.rgb(0x2e,0x2e,0x2e));
        }
        else
        {//已读
        	ItemTitle.setTextColor(Color.rgb(0x94,0x94,0x94));
        }
		return convertView;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return sapps.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return sapps.get(arg0)				;
	}

	@Override
	public long getItemId(int arg0) 
	{
		// TODO Auto-generated method stub
		return sapps.get(arg0).getId();
	}
}
