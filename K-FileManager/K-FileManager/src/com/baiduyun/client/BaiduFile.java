package com.baiduyun.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.widget.Toast;

import com.baidu.pcs.BaiduPCSActionInfo.PCSCommonFileInfo;
import com.framework.event.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.bean.FileBean;
import com.kenny.file.util.Const;

public class BaiduFile extends FileBean implements Runnable
{
	public String file_id;
	public String hash;
	public String root;
	public String rev;
	public Date create_time = null;
	public Date modify_time = null;
	public boolean is_deleted = false;
	public String type = "file";
	public List<BaiduFile> files = null;
	private Context mContext = null;
	private String msg = "";
	public int size = 0;
	private int ItemCount;

	public int getItemCount()
	{
		return ItemCount;
	}

	public void setItemCount(int itemCount)
	{
		ItemCount = itemCount;
	}

	public void setContext(Context mContext)
	{
		this.mContext = mContext;
	}
	public BaiduFile()
	{
		
	}
	public BaiduFile(PCSCommonFileInfo info)
	{
		setLength(info.size);
		setFilePath(Const.szBaiduYunPath+info.path);
		setDirectory(info.isDir);
		setFileName(new File(info.path).getName());
//		set info.cTime
		// info.hasSubFolder;//子文件夹
	}
	/**
	 * 判断文件是否存在，文件大否是否一致
	 */
	public boolean exists()
	{
		if (super.exists())
		{
			if (mFile.length() == length)
			{
				return true;
			}
		}
		return false;
	}

	public Date convert2Date(Object obj)
	{
		if (obj == null)
			return null;

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;

		try
		{
			date = format.parse((String) obj);
		} catch (ParseException e)
		{
			e.printStackTrace();
		}
		return date;
	}

	public boolean convert2Boolean(Object obj)
	{
		if (obj == null)
			return false;
		if (obj instanceof Boolean)
			return ((Boolean) obj).booleanValue();
		return ((String) obj).toLowerCase().equals("true");
	}

	public int convert2Int(Object obj)
	{
		int ret = 0;
		if (obj != null)
		{
			if (obj instanceof Number)
			{
				ret = ((Number) obj).intValue();
			} else if (obj instanceof String)
			{
				ret = Integer.parseInt((String) obj);
			}
		}
		return ret;
	}

	public Long convert2Log(Object obj)
	{
		Long ret = 0l;
		if (obj != null)
		{
			if (obj instanceof Number)
			{
				ret = ((Number) obj).longValue();
			} else if (obj instanceof String)
			{
				ret = Long.valueOf((String) obj);
			}
		}
		return ret;
	}

	private ProgressDialog mProgressDialog = null;

	private void ShowDialog()
	{
		progress();
	}

	private boolean bCancel = false;

	public void progress()
	{
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// progressDialog.setMessage(message);
		mProgressDialog.setTitle("正在下载文件");
		mProgressDialog.setProgress(0);
		mProgressDialog.setMax(1000);
		mProgressDialog.show();
		bCancel = false;
		mProgressDialog.setOnCancelListener(new OnCancelListener()
		{

			public void onCancel(DialogInterface dialog)
			{
				bCancel = true;
			}
		});
	}

	private String TAG = "kuaipan";
	private FileOutputStream os = null;

	public void run()
	{
		try
		{ 
			if (exists())
			{
				SysEng.getInstance().addHandlerEvent(
						new openDefFileEvent(mContext, getFilePath()));
			} else
			{
				SysEng.getInstance().addHandlerEvent(new AbsEvent()
				{
					public void ok()
					{
						ShowDialog();
					}
				});
				new File(getFolderPath()).mkdirs();
				try
				{
					if (mFile == null)
					{
						mFile = new File(this.getFilePath());
					}
					if (mFile.exists())
					{
						mFile.delete();
					}
					mFile.createNewFile();
					os = new FileOutputStream(mFile);
				} catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
				
				//事件请求先停
//				KuaipanHTTPResponse resp = KuaipanAPI.downloadFile(
//						getKuaiPanFolderPath() + getFileName(), os,
//						new ProgressListener()
//						{
//							public void started()
//							{
//								P.v(TAG, "started");
//							}
//
//							public void processing(final long bytes,
//									final long total)
//							{
//								SysEng.getInstance().addHandlerEvent(
//										new AbsEvent()
//										{
//											public void ok()
//											{
//												double per = bytes * 1000.0
//														/ total;
//												P.v(TAG, "per=" + per);
//												mProgressDialog
//														.setProgress((int) (per));
//												// /mProgressDialog.setMax((int)
//												// (1000));
//											}
//										});
//							}
//
//							public int getUpdateInterval()
//							{
//								// TODO Auto-generated method stub
//								P.v(TAG, "getUpdateInterval");
//								return 100;
//							}
//
//							public void completed()
//							{
//								try
//								{
//									if (os != null)
//										os.close();
//								} catch (IOException e)
//								{
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
//								P.v(TAG, "completed");
//								if (!bCancel)
//									SysEng.getInstance().addHandlerEvent(
//											new openDefFileEvent(mContext,
//													getFilePath()));
//							}
//
//							public boolean cancel()
//							{
//								return bCancel;
//							}
//						});
				os.close();
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			bCancel = true;
		}
//		catch (KuaipanServerException e)
//		{
//			bCancel = true;
//			switch (e.code)
//			{
//			case 302:
//				msg = "文件在另外的服务器上，请处理好跳转";
//				break;
//			case 401:
//				msg = "授权失败，参考";
//				break;
//			case 403:
//				msg = "文件不存在或者无权访问";
//				break;
//			case 404:
//				msg = "文件不存在";
//				break;
//			case 500:
//			case 507:
//			default:
//				msg = "	文件不存在或者服务器内部错误";
//				break;
//			}
//			e.printStackTrace();
//		} 
		

		if (bCancel)
		{
			mFile.delete();
		}
		SysEng.getInstance().addHandlerEvent(new AbsEvent()
		{

			public void ok()
			{
				if (mProgressDialog != null)
					mProgressDialog.dismiss();
				if (msg.length() > 0)
					Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
