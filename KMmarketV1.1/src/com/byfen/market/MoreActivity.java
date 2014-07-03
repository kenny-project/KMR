package com.byfen.market;

import java.util.ArrayList;

import com.byfen.market.R;
import com.umeng.fb.FeedbackAgent;
import com.work.market.server.DownLoadService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MoreActivity extends Activity implements OnClickListener{

	private LinearLayout  m_soft;
	private LinearLayout  m_game;
	private LinearLayout  m_special;
	private LinearLayout  m_wallpaper;
	private LinearLayout  m_recommend;
	private LinearLayout  m_need;
	
//	public MyAdapter adapter;
	private ListView lv1;
	
	private IntentFilter mIntentFilter; //消息处理
	
	//下载服务
		private DownLoadService downLoadService;
		//连接下载服务
		private ServiceConnection serviceConnection = new ServiceConnection()
		{
			//  连接服务失败后，该方法被调用
			@Override
			public void onServiceDisconnected(ComponentName name)
			{
				downLoadService = null;
//				Toast.makeText(DetailActivity.this, "Service Failed.", Toast.LENGTH_LONG).show();
			}
			//  成功连接服务后，该方法被调用。在该方法中可以获得downLoadService对象
			@Override
			public void onServiceConnected(ComponentName name, IBinder service)
			{
				//  获得downLoadService对象
				downLoadService = ((DownLoadService.DownLoadServiceBinder) service).getService();
//				Toast.makeText(DetailActivity.this, "Service Connected.", Toast.LENGTH_LONG).show();
			}
		}; 
	
	/**
	 * 进入页面
	* @param savedInstanceState    
	* @see android.app.ActivityGroup#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
			//加载页面
			setContentView(R.layout.more);
			//异常处理  
			
			mIntentFilter = new IntentFilter();     
			mIntentFilter.addAction("com.android.my.action");     
			mIntentFilter.addAction("com.android.my.action.sticky");
			RelativeLayout  m_collection;
			m_collection = (RelativeLayout)findViewById(R.id.rlFavorites);
			m_collection.setOnClickListener(this);
			
			m_collection = (RelativeLayout)findViewById(R.id.rlAbout);
			m_collection.setOnClickListener(this);
			
			m_collection = (RelativeLayout)findViewById(R.id.rlFeebback);
			m_collection.setOnClickListener(this);			
			
			
			
			
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.rlFavorites:
			Intent seta = new Intent(this, FavoritesActivity.class);
			startActivity(seta);//SoftclassActivity
			break;
		case R.id.rlAbout:
			new AboutDialog().showDialog(this);
			break;
		case R.id.rlFeebback:
	         FeedbackAgent agent = new FeedbackAgent(this);
	         agent.startFeedbackActivity();
			break;
		default :
			break;
		}
	}

	
//	private class MyAdapter extends BaseAdapter {
//
//		 
//		private LayoutInflater inflater;
//
//		public ArrayList<String> title1;
//		public ArrayList<String> title2;
//		public ArrayList<String> title3;
//		public ArrayList<Bitmap> image;
//	
//
//        public Context  mcontext;
//        
//		public MyAdapter(Context context) {
//
//			super();
//		
//            mcontext = context;
//			inflater = LayoutInflater.from(mcontext);
//			title1 = new ArrayList<String>();
//			title2 = new ArrayList<String>();
//			title3 = new ArrayList<String>();
//			image = new ArrayList<Bitmap>();
//			title1.add("");
//			title1.add("");
//			title1.add("");
//			title1.add("");
//			title1.add("");
//			title1.add("");
//			
//			
//			
//			
//		}
//		
//
//		public int getCount() {
//
//			// TODO Auto-generated method stub
//
//			return title1.size();
//
//		}
//
//		public Object getItem(int arg0) {
//
//			// TODO Auto-generated method stub
//
//			return arg0;
//
//		}
//
//		public long getItemId(int arg0) {
//
//			// TODO Auto-generated method stub
//
//			return arg0;
//
//		}
//
//		public View getView(final int position, View view, ViewGroup arg2) {
//
//			// TODO Auto-generated method stub
//			if (view == null) {
//				view = inflater.inflate(R.layout.downitem, null);
//			}  
//			
////			if(title1.size() > 0)
////			{
//// 					
//// 				final TextView mytext = (TextView) view.findViewById(R.id.zhaongjiang_item1_text1);//@+id/zhaongjiang_item1_text1
//// 				mytext.setText(title1.get(position));
//// 				
//// 				final TextView mytext1 = (TextView) view.findViewById(R.id.zhaongjiang_item1_text2);
//// 				mytext1.setText(title2.get(position));
//// 				
//// 				final TextView mytext2 = (TextView) view.findViewById(R.id.zhaongjiang_item1_text3);
//// 				mytext2.setText(title3.get(position));
//// 
////			}
////			if(image.size() > position)
////			{
////				final ImageView  myImage = (ImageView)view.findViewById(R.id.zhaongjiang_item1_image);
////				myImage.setImageBitmap(image.get(position));
////			}
//
//
// 			
//
//			return view;
//		}
//	}
	

//	private BroadcastReceiver mReceiver = new BroadcastReceiver() {     
//		  
//	      @Override    
//	      public void onReceive(Context context, Intent intent) {     
////	          final String action = intent.getAction();     
////	          System.out.println("action"+action);
////	          int x = downLoadService.GetTag();
////	          x++;
//	               
//	      }     
//	  };     
	       
	  @Override    
	  protected void onResume() {     
	      // TODO Auto-generated method stub     
	      super.onResume();     
	      //registerReceiver(mReceiver, mIntentFilter);     
	  }     
	       
	  @Override    
	  protected void onPause() {     
	      // TODO Auto-generated method stub     
	      super.onPause();     
	     // unregisterReceiver(mReceiver);     
	  }
	  
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if(event.getAction() == KeyEvent.ACTION_DOWN)
			{
				if(keyCode == KeyEvent.KEYCODE_BACK)
				{
					new AlertDialog.Builder(this)
			        .setTitle("提示")
			        .setMessage("是否要退出软件？")
			        .setPositiveButton("确定",
			           new DialogInterface.OnClickListener()
			            {
			           public void onClick(DialogInterface dialoginterface, int i)
			             {
			        	   Intent serviceIntent = new Intent(MoreActivity.this,DownLoadService.class);
							serviceIntent.putExtra("type", "finish");
							startService(serviceIntent);
							finish();
			             }
			             }
			            ).setNegativeButton("取消",new DialogInterface.OnClickListener()
			            {
			           public void onClick(DialogInterface dialoginterface, int i)
			             {
			          	 //纭畾澶勭悊
			          	 
			             }
			             } )
			        .show();
					
				}
			}
			//m_webview.goBack();
			return true;
		}
	
}


