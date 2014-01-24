package com.kenny.ftp.client;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.net.ftp.FTPFile;
import com.kenny.KFileManager.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * FTP列表适配器.
 * 
 * @author cui_tao
 */
public class RemoteAdapter extends BaseAdapter
{
	/**
	 * FTP文件列表.
	 */
	private List<FTPFile> list = new ArrayList<FTPFile>();

	/**
	 * 布局.
	 */
	private LayoutInflater inflater;
	private Bitmap mImage;
	private Bitmap mAudio;
	private Bitmap mRar;
	private Bitmap mVideo;
	private Bitmap mFolder;
	private Bitmap mApk;
	private Bitmap mOthers;
	private Bitmap mTxt;
	private Bitmap mWeb;
	private Bitmap mDoc;

	/**
	 * 构造函数.
	 * 
	 * @param context
	 *            当前环境
	 * @param li
	 *            FTP文件列表
	 */
	public RemoteAdapter(Context mContext, List<FTPFile> li)
	{
		this.list = li;
		this.inflater = LayoutInflater.from(mContext);
		// 文件夹显示图片
		// 文件显示图片
//	      mImage = BitmapFactory.decodeResource(mContext.getResources(),
//		            R.drawable.f_image);
//	      mAudio = BitmapFactory.decodeResource(mContext.getResources(),
//		            R.drawable.f_audio);
//	      mVideo = BitmapFactory.decodeResource(mContext.getResources(),
//		            R.drawable.f_video);
//	      mApk = BitmapFactory.decodeResource(mContext.getResources(),
//		            R.drawable.f_apk);
//	      mTxt = BitmapFactory.decodeResource(mContext.getResources(),
//		            R.drawable.f_txt);
//	      mOthers = BitmapFactory.decodeResource(mContext.getResources(),
//		            R.drawable.f_others);
//	      mFolder = BitmapFactory.decodeResource(mContext.getResources(),
//		            R.drawable.f_folder);
//	      mRar = BitmapFactory.decodeResource(mContext.getResources(),
//		            R.drawable.f_zip);
//	      mWeb = BitmapFactory.decodeResource(mContext.getResources(),
//		            R.drawable.f_web);

	}

	
	public int getCount()
	{
		return list.size();
	}

	
	public Object getItem(int position)
	{
		return list.get(position);
	}

	
	public long getItemId(int position)
	{
		return position;
	}

	
	public View getView(int position, View view, ViewGroup parent)
	{
		ViewHolder holder;
		if (view == null)
		{
			// 设置视图
			view = this.inflater.inflate(R.layout.listitem_remote, null);
			// 获取控件实例
			holder = new ViewHolder();
			holder.icon = (ImageView) view.findViewById(R.id.image_icon);
			holder.fileName = (TextView) view.findViewById(R.id.text_name);
			holder.fileSize = (TextView) view.findViewById(R.id.text_size);
			// 设置标签
			view.setTag(holder);
		} else
		{
			// 获取标签
			holder = (ViewHolder) view.getTag();
		}

		FTPFile ftpFile = list.get(position);
		// 获取文件名
		holder.fileName.setText(Util.convertString(ftpFile.getName(), "UTF-8"));
		if (ftpFile.isDirectory())
		{
			// 获取显示文件夹图片
			holder.icon.setImageBitmap(mFolder);
		} else
		{
			String fileName = holder.fileName.getText().toString();
			String fileEnds = fileName.substring(fileName.lastIndexOf(".") + 1,
					fileName.length()).toLowerCase();// 取出文件后缀名并转成小写
			// 获取显示文件图片
			holder.icon.setImageBitmap(mDoc);
			// 获取文件大小
			holder.fileSize.setText(Util.getFormatSize(ftpFile.getSize()));

			if (fileEnds.equals("mp3") || fileEnds.equals("mid")
					|| fileEnds.equals("xmf") || fileEnds.equals("ogg")
					|| fileEnds.equals("wav") || fileEnds.equals("wma"))
			{
				holder.icon.setImageBitmap(mAudio);
			} else if (fileEnds.equals("3gp") || fileEnds.equals("mp4"))
			{
				holder.icon.setImageBitmap(mVideo);
			} else if (fileEnds.equals("jpg") || fileEnds.equals("gif")
					|| fileEnds.equals("png") || fileEnds.equals("jpeg")
					|| fileEnds.equals("bmp"))
			{
				holder.icon.setImageBitmap(mImage);
			} else if (fileEnds.equals("apk"))
			{
				holder.icon.setImageBitmap(mApk);
				
			} else if (fileEnds.equals("txt") || fileEnds.equals("ini"))
			{
				holder.icon.setImageBitmap(mTxt);
			} else if (fileEnds.equals("zip") || fileEnds.equals("rar"))
			{
				holder.icon.setImageBitmap(mRar);
			} else if (fileEnds.equals("html") || fileEnds.equals("htm")
					|| fileEnds.equals("mht"))
			{
				holder.icon.setImageBitmap(mWeb);
			} else
			{
				holder.icon.setImageBitmap(mOthers);
			}
		}

		return view;
	}

	/**
	 * 获取控件.
	 */
	private class ViewHolder
	{
		/**
		 * 图片.
		 */
		private ImageView icon;

		/**
		 * 文件名.
		 */
		private TextView fileName;

		/**
		 * 文件大小.
		 */
		private TextView fileSize;
	}
}
