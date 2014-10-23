package com.kenny.KImageBrowser;

import java.util.List;

import com.framework.log.P;
import com.kenny.file.bean.FileBean;
import com.kenny.file.util.Res;
import com.kenny.file.util.SDFile;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Gallery.LayoutParams;

/**
 * 
 * @author 空山不空 图片适配器，用来加载图片
 */
public class ImageAdapter extends BaseAdapter
{
   // 图片适配器
   // 定义Context
   private static String TAG = "ImageAdapter";
   private int ownposition;

   private List<FileBean> mFileList;
   private OnKTouchListener mTouchListener = new OnKTouchListener();
   
   public int getOwnposition()
   {
      return ownposition;
   }
   
   public void setOwnposition(int ownposition)
   {
      this.ownposition = ownposition;
   }
   
   private Context mContext;
   
   // 定义整型数组 即图片源
   
   // 声明 ImageAdapter
   public ImageAdapter(Context c, List<FileBean> mFileList)
   {
      mContext = c;
      this.mFileList = mFileList;
   }
   
   // 获取图片的个数
   public int getCount()
   {
      return mFileList.size();
   }
   
   // 获取图片在库中的位置
   public FileBean getItem(int position)
   {
      ownposition = position;
      // return position;
      return mFileList.get(position);
   }
   
   // 获取图片ID
   public long getItemId(int position)
   {
      ownposition = position;
      return position;
   }
   
   public View getView(int position, View convertView, ViewGroup parent)
   {
      ImageView imageview = null;
      ownposition = position;
      if (convertView == null)
      {
         imageview = new ImageView(mContext);
         imageview.setBackgroundColor(0xFF000000);
         imageview.setScaleType(ImageView.ScaleType.MATRIX);
         imageview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
	     LayoutParams.FILL_PARENT));
      }
      else
      {
         imageview = (ImageView) convertView;
      }
      FileBean temp = mFileList.get(position);
      Bitmap mImage=null; 
      P.v("wmh","getView Start");
      mImage = SDFile.ReadFileToMaxBitmap(mContext, temp.getFilePath(),
	  com.framework.util.Const.SW);
      if (mImage != null)
      {
         Matrix matrix = new Matrix();
         int mWidth = com.framework.util.Const.SW / 2 - mImage.getWidth() / 2;
         int mHeight = com.framework.util.Const.SH / 2 - mImage.getHeight() / 2;
         matrix.postTranslate(mWidth, mHeight);
         mTouchListener.setMatrix(matrix);
         imageview.setImageMatrix(matrix);
         imageview.setImageBitmap(mImage);
      }
      else
      {
         mImage = Res.getInstance(mContext).getImgError().getBitmap();
         Matrix matrix = new Matrix();
         int mWidth = com.framework.util.Const.SW / 2 - mImage.getWidth() / 2;
         int mHeight = com.framework.util.Const.SH / 2 - mImage.getHeight() / 2;
         matrix.postTranslate(mWidth, mHeight);
         mTouchListener.setMatrix(matrix);
         imageview.setImageMatrix(matrix);
         imageview.setImageBitmap(mImage);
      }
      P.v("wmh","getView end");
      return imageview;
   }
   
}
