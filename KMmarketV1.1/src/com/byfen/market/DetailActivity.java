package com.byfen.market;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.byfen.market.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 *     详情页
 *    
 * @author caoliang      
 * @version 1.0    
 * @created 2012-12-19 下午8:59:44
 */
public class DetailActivity extends Activity{
	//view
	
	Gallery mGallery;
	private TextView mBack;
	private TextView mDownload;
	private TextView mShare;
	private ImageView mIcon;
	private TextView mName;
	private TextView mSize;
	private TextView mIntegralCount;
	private TextView mDownloadCount;
	private TextView mShareCount;
	private TextView mOpenCount;
	private TextView mDetail;
//	mServiceUIReceiver receiver;
	private Handler handlers = new Handler() {
		@Override
		public void handleMessage(Message message) {
			switch (message.what) {
			case 0:
				Toast.makeText(DetailActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				break;
			case 2:
				Toast.makeText(DetailActivity.this, "发送失败", Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
			super.handleMessage(message);
		}
	};
	
	List<String> listStr = new ArrayList<String>();

	String text = null;
	TextView mTop ;
	TextView mVersion;
	TextView mSum;
	TextView mAppLine;
	TextView mDetailJifen;
	LinearLayout mShareButton;
	LinearLayout mDownloadButton;
	String id;
	ImageView mDownloadImage;
	ProgressDialog mProgressDialog = null;
	LinearLayout mDownliadLiner;
	//下载服务
//	private DownloadService downLoadService;
	//连接下载服务
	private ServiceConnection serviceConnection = new ServiceConnection()
	{
		//  连接服务失败后，该方法被调用
		@Override
		public void onServiceDisconnected(ComponentName name)
		{
		//	downLoadService = null;
			Toast.makeText(DetailActivity.this, "Service Failed.", Toast.LENGTH_LONG).show();
		}
		//  成功连接服务后，该方法被调用。在该方法中可以获得downLoadService对象
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			//  获得downLoadService对象
	//		downLoadService = ((DownloadService.DownLoadServiceBinder) service).getService();
			Toast.makeText(DetailActivity.this, "Service Connected.", Toast.LENGTH_LONG).show();
		}
	}; 
	/**
	 * 进入页面
	* @param savedInstanceState    
	* @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//加载页面
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
		setContentView(R.layout.product);
		//异步处理
//		 CrashHandler crashHandler = CrashHandler.getInstance();  
//	        crashHandler.init(getApplicationContext());  
	        //创建广播实例
//		 receiver = new mServiceUIReceiver();
//    	 IntentFilter filter = new IntentFilter();
//         filter.addAction("android.intent.action.recommend");
//         this.registerReceiver(receiver, filter);
//         UI();
	}
	/**
	 * 详情页的图片显示数据
	 *     
	 *    
	 * @author caoliang      
	 * @version 1.0    
	 * @created 2012-12-22 下午11:03:59
	 */
	 private class ImageAdapter extends BaseAdapter{
	        private Context mContext;
	        private List<String> list;
	        
	        public ImageAdapter(Context c,List<String> list){
	        	this.mContext = c;
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

	            return position;
	        }

	        @Override
	        public long getItemId(int position) {
	            // TODO Auto-generated method stub

	            return position;
	        }

	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	            // TODO Auto-generated method stub
//	        	DisplayMetrics displaysMetrics = new DisplayMetrics();
//				((Activity) mContext).getWindowManager().getDefaultDisplay()
//						.getMetrics(displaysMetrics);
	            ImageView i = new ImageView (DetailActivity.this);
//	            String imageUrl = listStr.get(position);
//	    		Map<String,Object> map = new HashMap<String,Object>(); 
//	    		map.put("image",imageUrl);
//	    		if (imageUrl != null && !imageUrl.equals("")) {
//	    			File imageFile = new File(
//	    					CacheHelper.getImageDir(DetailActivity.this),
//	    					ApplicationUtils.imageName(imageUrl));
//	    			if (!imageFile.exists()
//	    					|| BitmapFactory
//	    							.decodeFile(imageFile.getAbsolutePath()) == null) {
//	    				mImageFetcher = new ImageFetcher(0f);
//	    				mImageFetcher.addTask(map, imageFile, i);
//	    			} else {
//	    				i.setImageBitmap(ApplicationUtils
//	    						.getSmallBitmapDependWidth(
//	    								imageFile.getAbsolutePath(), 0f));
//	    			}
//	    		}
////	            i.setImageResource(list.get(position));
//	            i.setScaleType(ImageView.ScaleType.FIT_CENTER);
//	            i.setLayoutParams(new Gallery.LayoutParams((displaysMetrics.widthPixels*2)/3,displaysMetrics.heightPixels/2));
//	            
//	            
	            return i;
//	        }
	}
	    
	    

//		@Override
		public void onBackPressed() {
			finish();
		}
		/**
		 * 显示应用是否下载并点击下载后进入下载服务
		 * 描述
		 */
		public void appDownload(){
//			boolean isDownload = false;
//			boolean isAtDownload = false;
//			if(DownloadService.completeList.contains(app.getId())){
//				isDownload = true;
//		}
//		if(DownloadService.downloadList.contains(app.getId())){
//				isAtDownload = true;
//		}
//		if(isAtDownload){
//			mDownload.setText("取消");
//			mDownloadImage.setImageResource(R.drawable.cancel2_click);
//		}else{
//			mDownload.setText("下载");
//			mDownloadImage.setImageResource(R.drawable.download_click);
//		}
//		if(isDownload){
//			mDownloadImage.setImageResource(R.drawable.download_canel);
//			mDownload.setText("下载");
//		}else{
//			mDownliadLiner.setOnClickListener(new OnClickListener() {
//				/**
//				 * 下载或者取消
//				* @param v    
//				* @see android.view.View.OnClickListener#onClick(android.view.View)
//				 */
//				@Override
//				public void onClick(View v) {
//					if(mNetWork.getIsNetworkAvailable()){
//						mDownloadImage.setImageResource(R.drawable.cancel2_click);
//						mDownload.setText("取消");
//						if(!DownloadService.downloadList.contains(app.getId())){
//							Toast.makeText(DetailActivity.this, "下载"+Double.valueOf(app.getDownloadIntegral()*0.001).floatValue()+"元"+"+激活"+Double.valueOf(app.getOpenIntegral()*0.001).floatValue()+"元"+"，全部完成可获得"+Double.valueOf((app.getDownloadIntegral()+app.getOpenIntegral())*0.001).floatValue()+"元", Toast.LENGTH_LONG).show();
//						}
//						Intent serviceIntent = new Intent(DetailActivity.this,DownloadService.class);
//						serviceIntent.putExtra("url", app.getFileName());
//						serviceIntent.putExtra("item", (int)(Math.random()*50009));
//						serviceIntent.putExtra("id", app.getId());
//						serviceIntent.putExtra("fielSize", app.getFileSize());
//						serviceIntent.putExtra("name", app.getName());
//						serviceIntent.putExtra("count", app.getDownloadIntegral());
//						DetailActivity.this.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//						DetailActivity.this.startService(serviceIntent);
//					}else{
//						Toast.makeText(DetailActivity.this, "网络连接出错！", 1).show();
//					}
//				}
//			});
//		}
//			
//		if(DownloadService.downloadList.size()==0){
//			 Intent serviceIntent = new Intent(DetailActivity.this,DownloadService.class);
//				bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
//				stopService(serviceIntent);
//		}
		}
		
		
//		@Override
		 protected void onDestroy() {
//		  super.onDestroy();
////		  if(serviceConnection!=null){
////			  unbindService(serviceConnection);
//			  this.unregisterReceiver(receiver);
////		  }
////		  if(downLoadService!=null){
////			  downLoadService.stopSelf();
////		  }
		 }
		/**
		 * 
		 *   内部广播  
		 *    
		 * @author caoliang      
		 * @version 1.0    
		 * @created 2012-12-22 下午11:06:51
		 */
		private class mServiceUIReceiver extends BroadcastReceiver{
	          @Override
	          public void onReceive(Context context, Intent intent) {
	                  Bundle bundle = intent.getExtras();
//	                  String appid = bundle.getString("appid");
//						int sizes = bundle.getInt("size");
//						int length = bundle.getInt("length");
						appDownload();
//						 adapter.setSize(appid, sizes,length);
//		                 adapter.notifyDataSetChanged();
	                 
	                  
	          }
	    }
		
		
	    /**
	     * 分享方式的显示数据
	     *     
	     *    
	     * @author caoliang      
	     * @version 1.0    
	     * @created 2012-12-22 下午11:07:28
	     */
		class ShareAdapter extends BaseAdapter{
			private Context mContext;
			private String[] list;
			private int[] icons;
			
			@Override
			public int getCount() {
				return list.length;
			}

			public ShareAdapter(Context mContext, String[] list,int[] icons) {
				super();
				this.mContext = mContext;
				this.list = list;
				this.icons = icons;
			}

			@Override
			public Object getItem(int position) {
				return position;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
//				LayoutInflater layout = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				View view = layout.inflate(R.layout.share_list_item, null);
//				ImageView image = (ImageView)view.findViewById(R.id.share_icon);
//				TextView mTextView = (TextView)view.findViewById(R.id.share_text);
//				image.setImageResource(icons[position]);
//				mTextView.setText(list[position]);
//				return view;
				return null;
			}
			
		}
//		@Override
		protected void onPause() {
//			super.onPause();
//			TCClick.onPause(this);
		}

//		@Override
		protected void onResume() {
//			super.onResume();
//			TCClick.onResume(this);

		}
		
		
		  private String buildTransaction(final String type) {
				return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
			}
}
}
