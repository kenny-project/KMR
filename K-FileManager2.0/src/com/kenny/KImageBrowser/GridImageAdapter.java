package com.kenny.KImageBrowser;

import java.util.List;

import com.kenny.KFileManager.R;
import com.kenny.file.Image.ImageLoader;
import com.kenny.file.bean.FileBean;
import com.kenny.file.interfaces.ImageCallback;
import com.kenny.file.util.Res;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * 
 * @author 空山不空 设置GridView的图片适配器
 */
public class GridImageAdapter extends BaseAdapter
{
        private Drawable          mImage;
        private static int      imageCol = 3;
        private Drawable        btnDrawable;
        private DisplayMetrics  dm;
        private List<FileBean>  mFileList;
        private ImageLoader mLogoImage;
        private Context         mContext;
        
        public GridImageAdapter(Context c, int imageCol, DisplayMetrics dm,
	              List<FileBean> mFileList)
        {
	      mContext = c;
	      this.imageCol = imageCol;
	      this.dm = dm;
	      this.mFileList = mFileList;
	      Resources resources = c.getResources();
	      btnDrawable = resources.getDrawable(R.drawable.bg);
	      mLogoImage = ImageLoader.GetObject(mContext);
	      mImage = Res.getInstance(mContext).getDefFileIco("jpg");
        }
        
        public int getCount()
        {
	      return mFileList.size();
        }
        
        public Object getItem(int position)
        {
	      return mFileList.get(position);
        }
        
        public long getItemId(int position)
        {
	      return position;
        }
        
        public View getView(int position, View convertView, ViewGroup parent)
        {
	      ImageViewExt imageView;
	      
	      if (convertView == null)
	      {
		    imageView = new ImageViewExt(mContext);
		    // 如果是横屏，GridView会展示4列图片，需要设置图片的大小
		    if (imageCol == 4)
		    {
			  imageView.setLayoutParams(new GridView.LayoutParams(
				        dm.heightPixels / imageCol - 6,
				        dm.heightPixels / imageCol - 6));
		    }
		    else
		    {// 如果是竖屏，GridView会展示3列图片，需要设置图片的大小
			  imageView.setLayoutParams(new GridView.LayoutParams(
				        dm.widthPixels / imageCol - 6,
				        dm.widthPixels / imageCol - 6));
		    }
		    imageView.setAdjustViewBounds(true);
		    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		    
		    // imageView.setPadding(3, 3, 3, 3);
	      }
	      else
	      {
		    imageView = (ImageViewExt) convertView;
	      }
	      // 动画效果
	      // Animation an=
	      // AnimationUtils.loadAnimation(mContext,R.anim.zoom_enter
	      // );
	      // imageView.setAnimation(an);
	      
	      // Resources res = getResources();
	      // //将Drawable转化为Bitmap
	      // Bitmap bitmap
	      // =ImageUtils.drawableToBitmap(res.getDrawable(mThumbIds[position]));
	      // //缩放图片
	      // Bitmap zoomBitmap = ImageUtils.zoomBitmap(bitmap,
	      // 100,100);
	      // //获取圆角图片
	      // Bitmap roundBitmap =
	      // ImageUtils.getRoundedCornerBitmap(zoomBitmap, 10.0f);
	      
	      // imageView.setImageBitmap(roundBitmap);
	      
	      FileBean temp = mFileList.get(position);
	      Drawable draw = mLogoImage.loadDrawable(temp,
		            new KImageCallback(imageView));
	      
	      if (draw != null)
	      {
		    imageView.setImageDrawable(draw);
	      }
	      else
	      {
		    imageView.setImageDrawable(mImage);
	      }
	      return imageView;
        }
        
        class KImageCallback implements ImageCallback
        {
	      ImageViewExt viewHolder;
	      
	      public KImageCallback(ImageViewExt viewHolder)
	      {
		    this.viewHolder = viewHolder;
	      }
	      
	      
	      public void imageLoaded(Drawable imageDrawable, String imageUrl)
	      {
		    // TODO Auto-generated method stub
		    viewHolder.setImageDrawable(imageDrawable);
	      }
        }
        
}
