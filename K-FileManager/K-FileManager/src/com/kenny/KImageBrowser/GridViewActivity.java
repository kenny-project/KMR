package com.kenny.KImageBrowser;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.kenny.KFileManager.R;
import com.kenny.file.bean.FileBean;
import com.kenny.file.manager.FileManager;

public class GridViewActivity extends Activity
{
        private DisplayMetrics   dm;
        private GridImageAdapter ia;
        private GridView         g;
        private static int       imageCol   = 3;
        private List<FileBean>   mImageList = new ArrayList<FileBean>();
        
        
        protected void onCreate(Bundle savedInstanceState)
        {
	      // TODO Auto-generated method stub
	      super.onCreate(savedInstanceState);
	      requestWindowFeature(Window.FEATURE_NO_TITLE);
	      // 得到屏幕的大小
	      dm = new DisplayMetrics();
	      getWindowManager().getDefaultDisplay().getMetrics(dm);
	      
	      setContentView(R.layout.mygridview);
	      g = (GridView) findViewById(R.id.myGrid);
	      ia = new GridImageAdapter(this, imageCol, dm,mImageList);
	      g.setAdapter(ia);
	      g.setOnItemClickListener(new OnItemClick(this));
	      LoadImageList();
        }
        
        private void LoadImageList()
        {
	      mImageList.clear();//by wmh 还没处理完
//	      List<FileBean> mFileList = FileManager.getInstance()
//		            .getFileList();
//	      for (int i = 0; i < mFileList.size(); i++)
//	      {
//		    FileBean bean = mFileList.get(i);
//		    String fileEnds = bean.getFileEnds();
//		    if (fileEnds.equals("jpg") || fileEnds.equals("gif")
//			          || fileEnds.equals("png")
//			          || fileEnds.equals("jpeg")
//			          || fileEnds.equals("bmp"))
//		    {
//			  mImageList.add(bean);
//		    }
//	      }
//	      ia.notifyDataSetChanged();
        }
        
        /**
         * 屏幕切换时进行处理 如果屏幕是竖屏，则显示3列，如果是横屏，则显示4列
         */
        
        public void onConfigurationChanged(Configuration newConfig)
        {
	      try
	      {
		    super.onConfigurationChanged(newConfig);
		    // 如果屏幕是竖屏，则显示3列，如果是横屏，则显示4列
		    if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
		    {
			  imageCol = 4;
		    }
		    else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
		    {
			  imageCol = 3;
		    }
		    g.setNumColumns(imageCol);
		    ia = new GridImageAdapter(this, imageCol, dm,mImageList);
		    g.setAdapter(ia);
		    //ia.notifyDataSetChanged();
	      }
	      catch (Exception ex)
	      {
		    ex.printStackTrace();
	      }
        }
        
        /**
         * 
         * @author 空山不空 点击具体的小图片时，会链接到GridViewActivity页面，进行加载和展示
         */
        public class OnItemClick implements OnItemClickListener
        {
	      public OnItemClick(Context c)
	      {
		    mContext = c;
	      }
	      
	      
	      public void onItemClick(AdapterView aview, View view,
		            int position, long arg3)
	      {
		    Intent intent = new Intent();
		    intent.setClass(GridViewActivity.this,
			          GalleryActivity.class);
		    intent.putExtra("position", position);
		    GridViewActivity.this.startActivity(intent);
	      }
	      
	      private Context mContext;
        }
        
}
