package com.kenny.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kenny.Application.KApp;
import com.kenny.comui.CircleImageButton;
import com.kenny.dailyenglish.R;
import com.kenny.util.ColorFactory;

public class MoHuCiAdapter extends BaseAdapter{
	
	private Context context;
	private ArrayList<String> list;
	
	public MoHuCiAdapter(Context context, ArrayList<String> list){
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.v6mohuci_item, null);
		}
		CircleImageButton ibArrow = (CircleImageButton) convertView.findViewById(R.id.mi_arrow_image);
		ibArrow.setTouchable(false);
		TextView tvText = (TextView) convertView.findViewById(R.id.mi_word);
		tvText.setText(list.get(position));
		KApp app = (KApp) context.getApplicationContext();
		tvText.setTextColor(app.colorFactory.getColor());
		return convertView;
	}

}
