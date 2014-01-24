package com.framework.page;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.kenny.KImageBrowser.OnKTouchListener;
import com.kenny.file.bean.FileBean;
import com.kenny.file.page.ObjectMenuPage;
import com.kenny.file.util.SDFile;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
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
public class PageAdapter extends BaseAdapter
{
	// 图片适配器
	// 定义Context
	private static String TAG = "ImageAdapter";
	private int ownposition;
	// private List<FileBean> mFileList;
	protected List<ObjectMenuPage> mPageList = new Vector<ObjectMenuPage>();

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
	public PageAdapter(Context c, List<ObjectMenuPage> mPageList)
	{
		mContext = c;
		this.mPageList = mPageList;
	}

	// 获取图片的个数
	public int getCount()
	{
		return mPageList.size();
	}

	// 获取图片在库中的位置
	public ObjectMenuPage getItem(int position)
	{
		ownposition = position;
		// return position;
		return mPageList.get(position);
	}

	// 获取图片ID
	public long getItemId(int position)
	{
		ownposition = position;
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent)
	{
		ownposition = position;

		ObjectMenuPage temp = mPageList.get(position);
		if (convertView != temp)
		{
			if (!temp.isCreate())
			{
				temp.onCreate();
				temp.onResume();
			}
		}
		return temp;
	}

}
