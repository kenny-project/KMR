package com.kenny.ftp.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.framework.log.P;

/**
 * FTP封装类.
 * 
 * @author cui_tao
 */
public class FTP
{
	private static final String TAG = "FTP";
	/**
	 * 服务器名.
	 */
	private String hostName;

	/**
	 * 用户名.
	 */
	private String userName;

	/**
	 * 密码.
	 */
	private String password;

	/**
	 * FTP连接.
	 */
	private FTPClient ftpClient;

	/**
	 * FTP列表.
	 */
	private List<FTPFile> list;

	/**
	 * FTP根目录.
	 */
	public static final String REMOTE_PATH = "\\";

	/**
	 * FTP当前目录.
	 */
	private String currentPath = "";

	/**
	 * 统计流量.
	 */
	private double response;

	/**
	 * 构造函数.
	 * 
	 * @param host
	 *            hostName 服务器名
	 * @param user
	 *            userName 用户名
	 * @param pass
	 *            password 密码
	 */
	public FTP(String host, String user, String pass)
	{
		this.hostName = host;
		this.userName = user;
		this.password = pass;
		this.ftpClient = new FTPClient();
		this.list = new ArrayList<FTPFile>();
	}

	/**
	 * 打开FTP服务.
	 * 
	 * @throws IOException
	 */
	public void openConnect() throws IOException
	{
		// 中文转码
		ftpClient.setControlEncoding("UTF-8");
		int reply; // 服务器响应值
		// 连接至服务器
		ftpClient.connect(hostName);
		P.v("hostName:" + hostName);
		// 获取响应值
		reply = ftpClient.getReplyCode();
		P.v(TAG, "connect:" + reply);
		if (!FTPReply.isPositiveCompletion(reply))
		{
			// 断开连接
			ftpClient.disconnect();
			throw new IOException("connect fail: " + reply);
		}
		P.v("Login:userName=" + userName + ",password=" + password);
		// 登录到服务器
		boolean ReplyCode = ftpClient.login(userName, password);
		P.v("ReplyCode:" + ReplyCode);
		// 获取响应值
		reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply))
		{
			// 断开连接
			ftpClient.disconnect();
			throw new IOException("connect fail: " + reply);
		} else
		{
			// 获取登录信息
			FTPClientConfig config = new FTPClientConfig(ftpClient
					.getSystemType().split(" ")[0]);
			config.setServerLanguageCode("zh");
			ftpClient.configure(config);
			// 使用被动模式设为默认
			ftpClient.enterLocalPassiveMode();
			// 二进制文件支持
			ftpClient
					.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
		}
	}

	/**
	 * 关闭FTP服务.
	 * 
	 * @throws IOException
	 */
	public void closeConnect() throws IOException
	{
		if (ftpClient != null)
		{
			// 登出FTP
			ftpClient.logout();
			// 断开连接
			ftpClient.disconnect();
			System.out.println("logout");
		}
	}

	private String currentlyPath;

	public String getCurrentlyPath()
	{
		return currentlyPath;
	}

	/**
	 * FTP访问地址
	 * 
	 * @param path
	 * @return
	 */
	public boolean changeWorkingDirectory(String path)
	{
		try
		{
			this.currentlyPath = currentlyPath + path;
			P.v(TAG, "changeWorking currentlyPath=" + currentlyPath);
			return ftpClient.changeWorkingDirectory(path);
		} catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 连接ftp服务器
	 * 
	 * @param ip
	 * @param port
	 * @param user
	 * @param pwd
	 * @return
	 * @throws Exception
	 */
	public boolean connectServer(String ip, int port, String user, String pwd)
			throws Exception
	{
		boolean isSuccess = false;
		try
		{
			ftpClient = new FTPClient();
			ftpClient.connect(ip, port);
			ftpClient.login(user, pwd);
			isSuccess = true;
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return isSuccess;
	}

	// 获得FTPClient类，可以直接对其操作
	public FTPClient getFTPClient()
	{

		return ftpClient;
	}

	/**
	 * 列出当前目录下所有文件.
	 * 
	 * @param remotePath
	 *            服务器目录
	 * @return FTPFile集合
	 * @throws IOException
	 */
	public List<FTPFile> listFiles() throws IOException
	{
		// 获取文件

		FTPFile[] files = ftpClient.listFiles();
		list = new ArrayList<FTPFile>();
		// 遍历并且添加到集合
		for (FTPFile file : files)
		{
			list.add(file);
		}
		if(list.get(0).getName().equals("."))
		{
			list.remove(0);
		}
		Collections.sort(list, new Sort());
		return list;
	}

	class Sort implements Comparator<FTPFile>
	{
		/**
		 * 0:相等 1:大于 -1:小于
		 */
		public int compare(FTPFile o1, FTPFile o2)
		{
			return sortUp(o1, o2);
		}

		private int sortUp(FTPFile o1, FTPFile o2)
		{
			if (o1 == null || o2 == null)
			{
				P.v("sort error, bean is null");
				return 0;
			}
			boolean bo1 = o1.isDirectory();
			boolean bo2 = o2.isDirectory();
			if (bo1 == bo2)
			{
				if (o1.getName().compareTo(o2.getName()) > 0)
				{
					return 1;
				} else if (o1.getName().compareTo(o2.getName()) < 0)
				{
					return -1;
				}
				return 0;
			}
			if (bo1)
			{
				return -1;
			} else
			{
				return 1;
			}
		}
	}

	/**
	 * 下载.
	 * 
	 * @param remotePath
	 *            FTP目录
	 * @param fileName
	 *            文件名
	 * @param localPath
	 *            本地目录
	 * @return Result
	 * @throws IOException
	 */
	public Result download(String remotePath, String fileName, String localPath)
			throws IOException
	{
		boolean flag = true;
		Result result = null;
		// 初始化FTP当前目录
		currentPath = remotePath;
		// 初始化当前流量
		response = 0;
		// 更改FTP目录
		ftpClient.changeWorkingDirectory(remotePath);
		// 得到FTP当前目录下所有文件
		FTPFile[] ftpFiles = ftpClient.listFiles();
		// 循环遍历
		for (FTPFile ftpFile : ftpFiles)
		{
			// 找到需要下载的文件
			if (ftpFile.getName().equals(fileName))
			{
				System.out.println("download...");
				// 创建本地目录
				File file = new File(localPath + "/" + fileName);
				// 下载前时间
				Date startTime = new Date();
				if (ftpFile.isDirectory())
				{
					// 下载多个文件
					flag = downloadMany(file);
				} else
				{
					// 下载当个文件
					flag = downloadSingle(file, ftpFile);
				}
				// 下载完时间
				Date endTime = new Date();
				// 返回值
				result = new Result(flag, Util.getFormatTime(endTime.getTime()
						- startTime.getTime()), Util.getFormatSize(response));
			}
		}
		return result;
	}

	/**
	 * 下载单个文件.
	 * 
	 * @param localFile
	 *            本地目录
	 * @param ftpFile
	 *            FTP目录
	 * @return true下载成功, false下载失败
	 * @throws IOException
	 */
	private boolean downloadSingle(File localFile, FTPFile ftpFile)
			throws IOException
	{
		boolean flag = true;
		// 创建输出流
		OutputStream outputStream = new FileOutputStream(localFile);
		// 统计流量
		response += ftpFile.getSize();
		// 下载单个文件
		flag = ftpClient.retrieveFile(localFile.getName(), outputStream);
		// 关闭文件流
		outputStream.close();
		return flag;
	}

	/**
	 * 下载多个文件.
	 * 
	 * @param localFile
	 *            本地目录
	 * @return true下载成功, false下载失败
	 * @throws IOException
	 */
	private boolean downloadMany(File localFile) throws IOException
	{
		boolean flag = true;
		// FTP当前目录
		if (!currentPath.equals(REMOTE_PATH))
		{
			currentPath = currentPath + REMOTE_PATH + localFile.getName();
		} else
		{
			currentPath = currentPath + localFile.getName();
		}
		// 创建本地文件夹
		localFile.mkdir();
		// 更改FTP当前目录
		ftpClient.changeWorkingDirectory(currentPath);
		// 得到FTP当前目录下所有文件
		FTPFile[] ftpFiles = ftpClient.listFiles();
		// 循环遍历
		for (FTPFile ftpFile : ftpFiles)
		{
			// 创建文件
			File file = new File(localFile.getPath() + "/" + ftpFile.getName());
			if (ftpFile.isDirectory())
			{
				// 下载多个文件
				flag = downloadMany(file);
			} else
			{
				// 下载单个文件
				flag = downloadSingle(file, ftpFile);
			}
		}
		return flag;
	}

	/**
	 * 上传.
	 * 
	 * @param localFile
	 *            本地文件
	 * @param remotePath
	 *            FTP目录
	 * @return Result
	 * @throws IOException
	 */
	public Result uploading(File localFile, String remotePath)
			throws IOException
	{
		boolean flag = true;
		Result result = null;
		// 初始化FTP当前目录
		currentPath = remotePath;
		// 初始化当前流量
		response = 0;
		// 二进制文件支持
		ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
		// 使用被动模式设为默认
		ftpClient.enterLocalPassiveMode();
		// 设置模式
		ftpClient
				.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
		// 改变FTP目录
		ftpClient.changeWorkingDirectory(REMOTE_PATH);
		// 获取上传前时间
		Date startTime = new Date();
		if (localFile.isDirectory())
		{
			// 上传多个文件
			flag = uploadingMany(localFile);
		} else
		{
			// 上传单个文件
			flag = uploadingSingle(localFile);
		}
		// 获取上传后时间
		Date endTime = new Date();
		// 返回值
		result = new Result(flag, Util.getFormatTime(endTime.getTime()
				- startTime.getTime()), Util.getFormatSize(response));
		return result;
	}

	/**
	 * 上传单个文件.
	 * 
	 * @param localFile
	 *            本地文件
	 * @return true上传成功, false上传失败
	 * @throws IOException
	 */
	private boolean uploadingSingle(File localFile) throws IOException
	{
		boolean flag = true;
		// 创建输入流
		InputStream inputStream = new FileInputStream(localFile);
		// 统计流量
		response += (double) inputStream.available() / 1;
		// 上传单个文件
		flag = ftpClient.storeFile(localFile.getName(), inputStream);
		// 关闭文件流
		inputStream.close();
		return flag;
	}

	/**
	 * 上传多个文件.
	 * 
	 * @param localFile
	 *            本地文件夹
	 * @return true上传成功, false上传失败
	 * @throws IOException
	 */
	private boolean uploadingMany(File localFile) throws IOException
	{
		boolean flag = true;
		// FTP当前目录
		if (!currentPath.equals(REMOTE_PATH))
		{
			currentPath = currentPath + REMOTE_PATH + localFile.getName();
		} else
		{
			currentPath = currentPath + localFile.getName();
		}
		// FTP下创建文件夹
		ftpClient.makeDirectory(currentPath);
		// 更改FTP目录
		ftpClient.changeWorkingDirectory(currentPath);
		// 得到当前目录下所有文件
		File[] files = localFile.listFiles();
		// 遍历得到每个文件并上传
		for (File file : files)
		{
			if (file.isHidden())
			{
				continue;
			}
			if (file.isDirectory())
			{
				// 上传多个文件
				flag = uploadingMany(file);
			} else
			{
				// 上传单个文件
				flag = uploadingSingle(file);
			}
		}
		return flag;
	}
}
