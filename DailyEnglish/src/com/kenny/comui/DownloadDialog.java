package com.kenny.comui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import org.apache.http.util.ByteArrayBuffer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kenny.Interface.INotifyDataSetChanged;
import com.kenny.dailyenglish.R;
import com.kenny.util.KCommand;
import com.kenny.util.Log;

public class DownloadDialog {
	private Context mContext;
	private AlertDialog mProgressDialog;
	private boolean mProgress = false;
	private String mCurrentPath;// 需要粘贴的位置
	private boolean bflag = false;// 标记是否全部粘贴
	private int nDialogResult = 1;
	private INotifyDataSetChanged notif;
	public static final int INIT = 1;// 定义三种下载的状态：初始化状态，正在下载状态，暂停状态
	public static final int PAUSE = 2;
	public static final int FINISH = 3;// 下载完成
	public static final int UPDATE = 4;// 可更新

	public static final int DOWNLOADING = 30;// 正在下载
	public static final int INIT_SERVER = 31;// 定义三种下载的状态：初始化状态，正在下载状态，暂停状态
	public static final int WAIT = 32;// 等待已加入队列

	public static final int ERROR = 112;// 下载 失败
	public static final int ERROR_NET = 117;// 连接服务器失败
	public static final int ERROR_CREATE_FILE = 118;// 创建文件失败
	public static final int ERROR_NO_FILE = 119;// 文件未找到
	public static final int DELETE = 110;// 下载 失败
	public static final int ERROR_NET_DIC_FILE_SIZE = 111;// 获取词库文件大小失败
	public static final int ERROR_NET_DIC_UPDATE_SOFT = 112;// 当前版本不支持收费词典下载.请升级应用
	public static final int ERROR__NOTNET = 119;// 未找到可用网络
	private String localfile;// 本地文件

	public DownloadDialog(Context context, String mCurrentPath,
			String localfile, INotifyDataSetChanged notif)// bCut:true:剪切:
	{
		mContext = context;
		this.notif = notif;
		this.localfile = localfile;
		this.mCurrentPath = mCurrentPath;
		//ShowDialog(100);
	}
	private ProgressBar mProgressBar;
	private TextView tvMessage;
	private Handler handler=new Handler();
	public void ShowDialog(int count) {
		mProgress = true;
		AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
		mBuilder.setTitle(R.string.Download_VOAMp3_Dialog_title);
		mBuilder.setCancelable(false);
		LayoutInflater factory = LayoutInflater.from(mContext);
		final View view = factory.inflate(R.layout.alert_dialog_download,
				null);
		
		mProgressBar = (ProgressBar) view.findViewById(R.id.progress);
		tvMessage = (TextView) view.findViewById(R.id.tvMessage);
		mBuilder.setView(view);
		
		mBuilder.setNegativeButton(mContext.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mProgress = false;
					}
				});
		mProgressBar.setProgress(0);
		mProgressDialog=mBuilder.show();
	}

	public void ok() {
		HttpURLConnection connection = null;
		FileOutputStream randomAccessFile = null;
		InputStream is = null;
		try {
			if (!KCommand.isNetConnectNoMsg(mContext)) {
				NotifyDataSetChanged(ERROR__NOTNET, null, 0, 0);
				return;
			}
			Log.v("wmh", "mCurrentPath="+mCurrentPath);
			URL url = new URL(mCurrentPath);
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(8000);
			connection.setReadTimeout(30000);
			connection.setRequestMethod("GET");
			int fileSize = connection.getContentLength();

			File mFile = new File(localfile + ".tm");
			if (!mFile.exists()) {
				new File(mFile.getParent()).mkdirs();
			}
			Log.v("DownLoader", "Downloader:url Start");
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(60000);
			connection.setRequestMethod("GET");
			randomAccessFile = new FileOutputStream(mFile, true);
			// 将要下载的文件写到保存在保存路径下的文件中
			is = connection.getInputStream();
			ByteArrayBuffer arrayBuffer = new ByteArrayBuffer(20 * 1024);
			byte[] buffer = new byte[4096];
			int length = -1;
			int i = 1;
			mProgressBar.setMax(fileSize);
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					tvMessage.setText(getLength(mProgressBar.getProgress())+"/"+getLength(mProgressBar.getMax()));
				}
			});
			while (mProgress) {
				try {
					if ((length = is.read(buffer)) == -1) {
						//NotifyDataSetChanged(DownloadDialog.ERROR_NET);
						//myHandler.sendEmptyMessage(101);
						//mProgress=false;
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					NotifyDataSetChanged(DownloadDialog.ERROR_NET);
					myHandler.sendEmptyMessage(101);
					mProgress=false;
					break;
				}
				arrayBuffer.append(buffer, 0, length);
				mProgressBar.incrementProgressBy(length);
				// 更新数据库中的下载信息
				if (i % 10 == 0) 
				{
					i = 0;
					randomAccessFile.write(arrayBuffer.buffer(), 0,
							arrayBuffer.length());
					arrayBuffer.clear();
					if (!mFile.exists()) {
						NotifyDataSetChanged(DownloadDialog.ERROR_NO_FILE);
						myHandler.sendEmptyMessage(101);
						mProgress=false;
						break;
					}
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							tvMessage.setText(getLength(mProgressBar.getProgress())+"/"+getLength(mProgressBar.getMax()));
						}
					});
				}
				++i;
			}
			if(arrayBuffer.length()>0)
			{
				mProgressBar.incrementProgressBy(arrayBuffer.length());
			randomAccessFile.write(arrayBuffer.buffer(), 0,
					arrayBuffer.length());
			arrayBuffer.clear();
			}
//			if (fileSize == mFile.length()) {
			if (mProgress) {
				// 下载成功
				File dict = new File(localfile);
				if (dict.exists()) {
					dict.delete();
				}
				mFile.renameTo(dict);
				if (randomAccessFile != null)
				{
					randomAccessFile.close();
					randomAccessFile=null;
				}
				NotifyDataSetChanged(DownloadDialog.FINISH);
				myHandler.sendEmptyMessage(100);
			}
//			else
//			{
//				myHandler.sendEmptyMessage(101);
//			}
		} catch (Exception e) {
			e.printStackTrace();
			NotifyDataSetChanged(DownloadDialog.ERROR);
			myHandler.sendEmptyMessage(101);
		} finally {
			try {
				if (is != null)
					is.close();
				if (randomAccessFile != null)
					randomAccessFile.close();
				if (connection != null)
					connection.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.v("wmh", "Downloader:RUN end");
		}

	}
	private static DecimalFormat myformat = new DecimalFormat("#####0.00");
	public String getLength(double totalsize) 
	{
		String szLength="";
			if (totalsize > 1024) {
				totalsize = totalsize / 1024.0;
				if (totalsize > 1024) {
					totalsize = totalsize / 1024.0;
					szLength = myformat.format(totalsize) + "M";
				} else {
					szLength = myformat.format(totalsize) + "K";
				}
			} else {
				if (totalsize == 0) {
					return "0.00B";
				} else {
					szLength = myformat.format(totalsize) + "B";
				}
			}
		return szLength;
	}
	public void NotifyDataSetChanged(int what) {
		NotifyDataSetChanged(what, null, 0, 0);

	}

	public void NotifyDataSetChanged(int what, Object value, int arg1, int arg2) {
		if (notif != null) {
			notif.NotifyDataSetChanged(what, value, arg1, arg2);
		}
	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) 
		{
			Log.v("wmh", "Handler myHandler = new Handler()");
			if (msg.what == 100)// 下载完成
			{
				Toast.makeText(mContext, "下载完成", Toast.LENGTH_SHORT).show();
				mProgressDialog.dismiss();
			} else {
				String message = "下载失败,请稍后再试";
				Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
				mProgressDialog.dismiss();
			}
			//NotifyDataSetChanged(msg.what, msg, msg.arg1, msg.arg2);
		}
	};
}
