package com.kenny.net;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

import com.kenny.Application.KApp;
import com.kenny.Interface.INotifyDataSetChanged;
import com.kenny.struct.AbsEvent;
import com.kenny.util.KCommand;

public abstract class Downloader extends AbsEvent implements
		INotifyDataSetChanged {

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
	public static final int ERROR_NO_FILE = 120;// 文件未找到
	public static final int DELETE = 110;// 下载 失败
	public static final int ERROR_NET_DIC_FILE_SIZE = 111;// 获取词库文件大小失败
	public static final int ERROR_NET_DIC_UPDATE_SOFT = 121;// 当前版本不支持收费词典下载.请升级应用
	public static final int ERROR__NOTNET = 119;// 未找到可用网络
	private String urlstr;// 下载的地址
	private String localfile;// 保存路径

	private Dao dao;// 工具类
	private int fileSize;// 所要下载的文件的大小
	private DownloadInfo infos;// 存放下载信息类的集合
	protected int state = INIT;

	public String getUrl() {
		return urlstr;
	}

	public void setUrlstr(String urlstr) {
		this.urlstr = urlstr;
	}

	public String getLocalfile() {
		return localfile;
	}

	public void setLocalfile(String localfile) {
		this.localfile = localfile;
	}

	public Downloader() {
		super(null);
		dao = KApp.getDao();
	}

	/**
	 * 判断是否正在下载
	 */
	public boolean isdownloading() {
		return state == DOWNLOADING;
	}

	/**
	 * 得到downloader里的信息 首先进行判断是否是第一次下载，如果是第一次就要进行初始化，并将下载器的信息保存到数据库中
	 * 如果不是第一次下载，那就要从数据库中读出之前下载的信息（起始位置，结束为止，文件大小等），并将下载信息返回给下载器
	 */
	public DownloadInfo getDownloaderInfors() {
		fileSize = getServerFileSize(urlstr);
		if (fileSize > 0) {
			int result = CreateLocalFile(fileSize);
			if (result > 0) {
				if (isFirst(urlstr) || result == 2 || result == 1) {
					dao.delete(urlstr);
					infos = new DownloadInfo(0, fileSize - 1, 0, urlstr);
					// 保存infos中的数据到数据库
					dao.saveInfos(infos);
					// 创建一个LoadInfo对象记载下载器的具体信息
					setState(INIT_SERVER);
					return infos;
				} else { // result=3;
							// 得到数据库中已有的urlstr的下载器的具体信息
					infos = dao.getInfos(urlstr);
					setState(INIT_SERVER);
					return infos;
				}
			} else {
				setState(ERROR_CREATE_FILE);
			}
		} else {
			setState(ERROR_NET);
		}
		return null;
	}

	public DownloadInfo getDownloadInfo() {
		if (infos == null) {
			infos = dao.getInfos(urlstr);
		}
		return infos;
	}

	/**
	 * 获得服务器文件大小
	 * 
	 * @return
	 */
	private int getServerFileSize(String urlstr) {
		try {
			URL url = new URL(urlstr);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setConnectTimeout(5000);
			connection.setRequestMethod("GET");
			int fileSize = connection.getContentLength();
			connection.disconnect();
			return fileSize;

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 1:成功 2:文件不一致 -1:创建失败 获得要下载的文件大小
	 */
	private int CreateLocalFile(int size) {
		try {

			File file = new File(localfile);

			String Folder = localfile.substring(0,
					localfile.lastIndexOf("/") + 1);
			new File(Folder).mkdirs();
			//file.mkdirs();
			if (!file.exists()) {
				file.createNewFile();
				// 本地访问文件
				RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
				accessFile.setLength(size);
				accessFile.close();
				return 1;
			} else if (file.length() != size || file.length() == 0) {
				file.delete();
				file.createNewFile();
				// 本地访问文件
				RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
				accessFile.setLength(size);
				accessFile.close();
				return 2;
			}
			return 3;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 判断是否是第一次 下载
	 */
	private boolean isFirst(String urlstr) {
		return dao.isHasInfors(urlstr);
	}

	/**
	 * 114 * 利用线程开始下载数据 115
	 */

	@Override
	public void ok() {
		HttpURLConnection connection = null;
		RandomAccessFile randomAccessFile = null;
		InputStream is = null;
		try {
			if (state != Downloader.WAIT) {
				Log.v("DownLoader", "DownLoader:code = " + state);
				setState(ERROR);
				return;
			}
			Log.v("wmh", "Downloader:RUN start");
			if (!KCommand.isNetConnectNoMsg(context)) {
				NotifyDataSetChanged(ERROR__NOTNET, urlstr, 0, 0);
				setState(ERROR__NOTNET);
				return;
			}
			infos = getDownloaderInfors();// 获取文件大小
			if (infos == null) {
				// Downloader.this.setState(Downloader.ERROR_NET_DIC_FILE_SIZE);
				NotifyDataSetChanged(this.getState(), urlstr, 0, 0);
				setState(ERROR);
				return;
			} else {
				setState(DOWNLOADING);
			}
			File mFile = new File(localfile);
			if (infos.getEndPos() > infos.getCompeleteSize()) {
				Log.v("DownLoader", "Downloader:url Start");
				URL url = new URL(infos.getUrl());
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(5000);
				connection.setReadTimeout(60000);
				connection.setRequestMethod("GET");
				// 设置范围，格式为Range：bytes x-y;
				String Range = "bytes=" + infos.getCompeleteSize() + "-"
						+ infos.getEndPos();
				connection.setRequestProperty("Range", Range);
				Log.v("DownLoader", "Downloader:url end Range=" + Range);
				randomAccessFile = new RandomAccessFile(localfile, "rwd");
				randomAccessFile.seek(infos.getCompeleteSize());
				// 将要下载的文件写到保存在保存路径下的文件中
				is = connection.getInputStream();

				byte[] buffer = new byte[5000];
				int length = -1;
				Log.v("DownLoader", "Downloader:read start CompeleteSize="
						+ infos.getCompeleteSize() + "end=" + infos.getEndPos());
				int responseCode = connection.getResponseCode();
				// if (HttpURLConnection.HTTP_OK != responseCode)// 连接成功
				// {
				// Log.v("DownLoader",
				// "DownLoader:code = " + connection.getResponseCode()
				// + ", status = "
				// + connection.getResponseMessage());
				// }
				Log.v("DownLoader", "DownLoader:code = " + responseCode
						+ ", msg = " + connection.getResponseMessage());
				while ((length = is.read(buffer)) != -1 && state == DOWNLOADING) {
					if (!mFile.exists()) {
						Downloader.this.setState(Downloader.ERROR_NO_FILE);
						break;
					}
					randomAccessFile.write(buffer, 0, length);
					infos.setCompeleteSize(infos.getCompeleteSize() + length);
					// 更新数据库中的下载信息
					dao.updataInfos(infos);
					// 用消息将下载信息传给进度条，对进度条进行更新
					Log.v("wmh", "compeleteSize=" + infos.getCompeleteSize());

					if (state != DOWNLOADING) {
						break;
					}
					NotifyDataSetChanged(1, urlstr, length, 0);
				}
				Log.v("wmh", "Downloader:read end");
			}
			Log.v("wmh", "Downloader:end fileSize=" + fileSize
					+ ":compeleteSize=" + infos.getCompeleteSize());
			if (fileSize <= infos.getCompeleteSize()) {
				infos.setCompeleteSize(0);
				dao.delete(urlstr);
				File dict = new File(localfile.substring(0,
						localfile.length() - 3));
				if (dict.exists()) {
					dict.delete();
				}
				mFile.renameTo(dict);
				Downloader.this.setState(Downloader.FINISH);
				NotifyDataSetChanged(2, urlstr, 1, 0);
				return;
			}
		} catch (Exception e) 
		{
			e.printStackTrace();
			Downloader.this.setState(Downloader.ERROR);
			NotifyDataSetChanged(3, urlstr, 0, 0);
		}
		finally 
		{
			try {
				if (is != null)
					is.close();
				if (randomAccessFile != null)
					randomAccessFile.close();
				if (connection != null)
					connection.disconnect();
				if (dao != null)
					dao.closeDb();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.v("wmh", "Downloader:RUN end");
		}
	}

	// 删除数据库中urlstr对应的下载器信息
	public void delete() {
		try {
			setState(INIT);
			if (dao != null)
				dao.delete(urlstr);
			if (infos != null)
				infos.setCompeleteSize(0);
			new File(localfile).delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
		NotifyDataSetChanged(4, 0, state, 0);
	}

	// 重置下载状态
	public void reset() {
		setState(INIT);
	}
}