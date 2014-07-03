package com.work.market.net;

import java.io.File;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.byfen.market.R;
import com.framework.syseng.AbsEvent;
import com.framework.syseng.SysEng;
import com.work.Image.SDFile;
import com.work.Interface.INotifyDataSetChanged;
import com.work.market.util.KCommand;

//词库信息
public class DictBean extends Downloader 
{

	private String szLength = null;// 字节大小
	private String FileName = "";// 本地路径
	private File mFile;
//	private INotifyDataSetChanged mHandler;
	private int ID = 0;
	private int verCode; // 词库版本序列号
	public DictBean(Context main, int ID, String url, String ext)
	{
		this.main = main;
		super.setUrl(url);
		this.ID = ID;
		FileName = SDFile.URL2FileName(url) + "." + ext;
//		Log.v("wmh", "url="+url);
//		Log.v("wmh", "FileName="+FileName);
		setLocalfile(localPath + FileName + ".tm");
		mFile = new File(localPath + FileName);
	}

	/**
	 * 绑定需要显示的数据
	 * 
	 * @param mHandler
	 */
//	public void setHandler(INotifyDataSetChanged mHandler)
//	{
//		this.mHandler = mHandler;
//	}

	public  int getId()
	{
		return ID;
	}

	/**
	 * 获取文件是否存在
	 * 
	 * @return
	 */
	public boolean exists()
	{
		return mFile.exists();
	}

	public String getFilePath()
	{
		return mFile.getPath();
	}

	public String getFileName()
	{
		return FileName;
	}

	// 设置暂停
	public void Pause()
	{
		setState(PAUSE);
	}

	public void remove()
	{
		Pause();
	};

	/**
	 * 删除词典和未下载完成的词典
	 */
	private void DeleteDic()
	{
		delete();
		if (mFile.exists())
		{
			mFile.delete();
		}
	}

	/**
	 * 删除未下载完成的词典
	 */
	public int Delete()
	{
		DeleteDic();
		return 1;// 成功
	}

	public String getSize()
	{
		if (szLength == null)
		{
			double totalsize = mFile.length();
			if (totalsize > 1024)
			{
				totalsize = totalsize / 1024.0;
				if (totalsize > 1024)
				{
					totalsize = totalsize / 1024.0;
					szLength = myformat.format(totalsize) + "M";
				}
				else
				{
					szLength = myformat.format(totalsize) + "K";
				}
			}
			else
			{
				if (totalsize == 0)
				{
					return "0.00B";
				}
				else
				{
					szLength = myformat.format(totalsize) + "B";
				}
			}
		}
		return szLength;
	}

	/**
	 * 获取当前已经下载大小
	 * 
	 * @return
	 */
	public int getCompeleteSize()
	{
		DownloadInfo temp = getDownloadInfo();
		if (temp == null)
		{
			return 0;
		}
		else
		{
			return temp.getCompeleteSize();
		}
	}

	/**
	 * 获取下载进度%比
	 * 
	 * @return
	 */
	public int getCompeletePercentage()
	{
		DownloadInfo temp = getDownloadInfo();
		if (temp == null)
		{
			return 0;
		}
		else
		{
			return temp.getCompeletePercentage();
		}
	}

	public int getStatus()
	{
		int state = super.getState();
		if (state == Downloader.FINISH || state == Downloader.INIT)
		{
			if (exists() == true)
			{
				return Downloader.FINISH;
			}
			else
			{
				return Downloader.INIT;
			}
		}
		return state;
	}

	public int getVerCode()
	{
		return verCode;
	}
	
	// -1:未知错误 2:已经启动 1:启动成功 -2:创建文件或网络失败
	public int Start()
	{
		if (!SDFile.checkSDCard())
		{
			SysEng.getInstance().addHandlerEvent(new AbsEvent()
			{
				@Override
				public void ok()
				{
					Toast.makeText(
							main,
							main.getString(R.string.toast_msg_unknown_SD_error),
							Toast.LENGTH_SHORT).show();
				}
			});
			return -4;
		}
		if (getUrl() == null)
		{
			SysEng.getInstance().addHandlerEvent(new AbsEvent()
			{
				@Override
				public void ok()
				{
					Toast.makeText(main,
							main.getString(R.string.toast_msg_unknown_error),
							Toast.LENGTH_SHORT).show();
				}
			});
			return -1;
		}
		if (!KCommand.isNetConnectNoMsg(main))
		{
			SysEng.getInstance().addHandlerEvent(new AbsEvent()
			{
				@Override
				public void ok()
				{
					Toast.makeText(main,
							main.getString(R.string.toast_msg_no_network),
							Toast.LENGTH_SHORT).show();
				}
			});
			return -2;
		}
//		Log.v("wmh", "Downloader:RUN start");
		setState(Downloader.WAIT);
		return DownLoading();
	}
}
