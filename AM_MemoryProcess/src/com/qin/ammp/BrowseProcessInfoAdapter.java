package com.qin.ammp;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

//�Զ����������࣬�ṩ��listView���Զ���view
public class BrowseProcessInfoAdapter extends BaseAdapter   {
	
	private List<ProcessInfo> mlistProcessInfo = null;
	
	LayoutInflater infater = null;
    
	public BrowseProcessInfoAdapter(Context context,  List<ProcessInfo> apps) {
		infater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mlistProcessInfo = apps ;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		System.out.println("size" + mlistProcessInfo.size());
		return mlistProcessInfo.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mlistProcessInfo.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public View getView(int position, View convertview, ViewGroup arg2) {
		System.out.println("getView at " + position);
		View view = null;
		ViewHolder holder = null;
		if (convertview == null || convertview.getTag() == null) {
			view = infater.inflate(R.layout.browse_process_item, null);
			holder = new ViewHolder(view);
			view.setTag(holder);
		} 
		else{
			view = convertview ;
			holder = (ViewHolder) convertview.getTag() ;
		}
		ProcessInfo processInfo = (ProcessInfo) getItem(position);
		holder.tvPID.setText(processInfo.getPid() +"");
		holder.tvUID.setText(processInfo.getUid() +"") ;
		holder.tvProcessMemSize.setText(processInfo.getMemSize()+"KB");
		holder.tvProcessName.setText(processInfo.getProcessName());
			
		return view;
	}

	class ViewHolder {
		TextView tvPID ;             //����ID
		TextView tvUID ;             //�û�ID   
		TextView tvProcessMemSize ;  //����ռ���ڴ��С 
        TextView tvProcessName ;   // ������
		public ViewHolder(View view) {
			this.tvPID = (TextView)view.findViewById(R.id.tvProcessPID) ;
			this.tvUID = (TextView) view.findViewById(R.id.tvProcessUID);
			this.tvProcessMemSize = (TextView) view.findViewById(R.id.tvProcessMemSize);
			this.tvProcessName = (TextView)view.findViewById(R.id.tvProcessName) ;
		}
	}

}