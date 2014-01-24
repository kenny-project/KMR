package com.kenny.adapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kenny.Application.KApp;
import com.kenny.dailyenglish.R;
import com.kenny.data.FeedbackBean;
import com.kenny.util.BASE64;
import com.kenny.util.Const;
import com.kenny.util.Utils;

public class FeedbackAdapter extends BaseAdapter{

	private ArrayList<FeedbackBean> list;
	private Context context;
	private Bitmap userbitmap;
	public FeedbackAdapter(Context context, ArrayList<FeedbackBean> list){
		this.context = context;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int id) {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parentView) {
		// TODO Auto-generated method stub
		int type = list.get(position).getType();
//		if (type == FeedbackBean.TYPE_DEV){
//			if (leftView == null){
//				leftView = LayoutInflater.from(context).inflate(R.layout.v6feedback_item_left, null);
//			}
//			convertView = leftView;
//		} else {
//			if (rightView == null){
//				rightView = LayoutInflater.from(context).inflate(R.layout.v6feedback_item_right, null);
//			}
//			convertView = rightView;
//		}
		if (convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.v6feedback_item_left, null);
		}
		ImageView ivIcon = (ImageView) convertView.findViewById(R.id.fbi_touxiang);
		TextView tvContent = (TextView) convertView.findViewById(R.id.fbi_content);
		RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(Utils.dip2px(context, 40), Utils.dip2px(context, 40));
		RelativeLayout.LayoutParams tvParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		if (type == FeedbackBean.TYPE_DEV){
			ivIcon.setImageResource(R.drawable.v6_dev);
			iconParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			tvParams.addRule(RelativeLayout.RIGHT_OF, R.id.fbi_touxiang);
			tvContent.setBackgroundResource(R.drawable.v6_left_kuang);
		} else {
			String picName = BASE64.decryptBASE64(
					Utils.get(context, "v6_name", "username")).trim();
			File f = new File(Const.LOGO_DIRECTORY + picName);
			if (userbitmap == null && f.exists()){
				// 用户头像为空，并且用户头像文件存在
				userbitmap = Utils.toRoundBitmap(BitmapFactory
						.decodeFile(Const.LOGO_DIRECTORY + picName));
			}
			if (userbitmap != null) {
				ivIcon.setImageBitmap(userbitmap);
			} else {
				ivIcon.setImageResource(R.drawable.v6_user);
			}
			iconParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			tvParams.addRule(RelativeLayout.LEFT_OF, R.id.fbi_touxiang);
			tvContent.setBackgroundResource(R.drawable.v6_right_kuang);
		}
		iconParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		tvParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		tvParams.setMargins(Utils.dip2px(context, 5), 0, Utils.dip2px(context, 5), 0);
		tvContent.setPadding(Utils.dip2px(context, 10), Utils.dip2px(context, 5), Utils.dip2px(context, 10), Utils.dip2px(context, 5));
//		tvContent.setBackgroundColor(Color.RED);
		ivIcon.setLayoutParams(iconParams);
		tvContent.setLayoutParams(tvParams);
		TextView tvDate = (TextView) convertView.findViewById(R.id.fbi_date);
		KApp app = (KApp) context.getApplicationContext();
		tvDate.setTextColor(app.colorFactory.getColor());
		SimpleDateFormat from = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		tvDate.setText(from.format(new Date(list.get(position).getDate())));
		tvContent.setText(list.get(position).getContent());
		
		return convertView;
	}

}
