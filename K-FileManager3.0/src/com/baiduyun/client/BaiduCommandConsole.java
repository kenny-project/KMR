package com.baiduyun.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.widget.Toast;

import com.baidu.pcs.BaiduPCSActionInfo;
import com.baidu.pcs.BaiduPCSStatusListener;
import com.baidu.pcs.BaiduPCSActionInfo.PCSCommonFileInfo;
import com.baidu.pcs.BaiduPCSClient;
import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kuaipan.client.exception.KuaipanAuthExpiredException;
import com.kuaipan.client.exception.KuaipanIOException;
import com.kuaipan.client.exception.KuaipanServerException;

public class BaiduCommandConsole
{
	private String path = "/apps/K-FileManager";
	private String prompt = ">";
	StringBuffer stdout = new StringBuffer();
	private BaiduPCSClient api;
	private Activity context;
	private String token;

	// private static BaiduCommandConsole handler;

	public String getPath()
	{
		if (!path.endsWith("/"))
		{
			return path = path + "/";
		}
		return path;
	}

	public BaiduCommandConsole(Activity context)
	{
		this.context = context;
		api = new BaiduPCSClient();
	}

	public void setAuthToken(String token)
	{
		this.token = token;
		api.setAccessToken(token);
	}

	/**
	 * return false indicates to exit.
	 * 
	 * @return
	 */
	public boolean execute(String cmd, String value)
	{
		try
		{
			if (cmd.equals("ls"))
				do_ls();
			else if (cmd.equals("cd"))
				do_cd(value);
			else if (cmd.equals("mkdir"))
				// do_mkdir(value);
				return false;
			else if (cmd.equals("rm"))
				// do_rm(value);
				return false;
			else if (cmd.equals("cat"))
				// do_cat(value);
				return false;
			else if (cmd.equals("exit"))
				return false;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}

	private final String TAG = "kuaipan";

	public List<BaiduFile> do_ls() throws KuaipanIOException,
			KuaipanServerException, KuaipanAuthExpiredException
	{
		return do_ls(path);
	}

	public List<BaiduFile> do_ls(String path)
	{
		P.v(TAG, "do_ls start:path=" + path);
		final BaiduPCSActionInfo.PCSListInfoResponse ret = api.list(path,
				"name", "asc");
		ArrayList<BaiduFile> list = new ArrayList<BaiduFile>();
		if (ret.status.errorCode == 0)
		{
			this.path = path;
			List<PCSCommonFileInfo> files = ret.list;
			if (files != null)
			{
				for (PCSCommonFileInfo temp : files)
				{
					list.add(new BaiduFile(temp));
				}
			}
			return list;
		} else
		{
			SysEng.getInstance().addHandlerEvent(new AbsEvent()
			{

				@Override
				public void ok()
				{
					Toast.makeText(
							context,
							"List:  " + ret.status.errorCode + "    "
									+ ret.status.message, Toast.LENGTH_SHORT)
							.show();
				}
			});
		}
		P.v("do_ls end");
		return null;
	}

	public String do_cd(String dir)
	{
		return joinPath(dir);
	}

	public final static int BAIDU_COMMAND_MIDIR = 10;
	public final static int BAIDU_COMMAND_DELETEFILE = 11;

	public boolean do_mkdir(final String dir, final INotifyDataSetChanged notif)
	{
		if (null != token && token.length() > 0)
		{
			SysEng.getInstance().addEvent(new AbsEvent()
			{
				@Override
				public void ok()
				{
					String tpath = path + "/" + dir;
					final BaiduPCSActionInfo.PCSFileInfoResponse ret = api
							.makeDir(tpath);
					notif.NotifyDataSetChanged(BAIDU_COMMAND_MIDIR, ret);
				}
			});
			return true;
		} else
		{
			return false;
		}
	}

	// 删除文件
	public void do_rm(final List<String> files,
			final INotifyDataSetChanged notif)
	{

		SysEng.getInstance().addEvent(new AbsEvent()
		{
			@Override
			public void ok()
			{
				final BaiduPCSActionInfo.PCSSimplefiedResponse ret = api
						.deleteFiles(files);
				notif.NotifyDataSetChanged(BAIDU_COMMAND_DELETEFILE, ret);
			}
		});
		// Toast.makeText(getApplicationContext(), "Delete files:  " +
		// ret.errorCode + "  " + ret.message, Toast.LENGTH_SHORT).show();
	}

	public boolean do_upload(File file, String folderPath)
	{

		if (null != token && token.length() > 0)
		{
			// String tmpFile = "/mnt/sdcard/zzzz.jpg";
			// String tmpFile = "/mnt/sdcard/DCIM/File/1.txt";
			final BaiduPCSActionInfo.PCSFileInfoResponse response = api
					.uploadFile(file.getPath(), folderPath + file.getName());

			return response.status.errorCode == 0;
			// Toast.makeText(
			// getApplicationContext(),
			// response.status.errorCode + "  " + response.status.message
			// + "  " + response.commonFileInfo.blockList,
			// Toast.LENGTH_SHORT).show();
		}
		return false;
	}

	public int do_download(String source, String target)
	{
		if (null != token && token.length() > 0)
		{
			// BaiduPCSClient api = new BaiduPCSClient();
			// api.setAccessToken(mbOauth);
			// String source = mbRootPath + "/189.jpg";
			// String target = "/mnt/sdcard/DCIM/100MEDIA/yytest0801.mp4";
			final BaiduPCSActionInfo.PCSSimplefiedResponse response = api
					.downloadFileFromStream(source, target,
							new BaiduPCSStatusListener()
							{
								// yangyangdd
								@Override
								public void onProgress(long bytes, long total)
								{
									// TODO Auto-generated method stub
									final long bs = bytes;
									final long tl = total;

									// mbUiThreadHandler.post(new Runnable(){
									// public void run(){
									// Toast.makeText(getApplicationContext(),
									// "total: " + tl + "    downloaded:" + bs,
									// Toast.LENGTH_SHORT).show();
									// }
									// });
								}

								@Override
								public long progressInterval()
								{
									return 500;
								}

							});
			if(response.errorCode!=0)
			{
			SysEng.getInstance().addHandlerEvent(new AbsEvent()
			{
				
				@Override
				public void ok()
				{
					Toast.makeText(context, "Download files:  " +
							response.errorCode + "   " + response.message, Toast.LENGTH_SHORT).show();
				}
			});
			}
			return response.errorCode;
			// mbUiThreadHandler.post(new Runnable(){
			// public void run(){
			// Toast.makeText(getApplicationContext(), "Download files:  " +
			// ret.errorCode + "   " + ret.message, Toast.LENGTH_SHORT).show();
			// }
			// });
		}
		return -1;
	}

	// public void do_cat(String dir) throws KuaipanIOException,
	// KuaipanServerException, KuaipanAuthExpiredException
	// {
	// ByteArrayOutputStream os = new ByteArrayOutputStream();
	// KuaipanHTTPResponse resp = KuaipanAPI.downloadFile(joinPath(dir), os,
	// null);
	// P.v("kuaipan", "resp.code =" + resp.code);
	// // assertTrue(resp.code == 200); //by wmh
	// String download_content = KPTestUtility.outputStream2String(os);
	// try
	// {
	// os.close();
	// } catch (IOException e)
	// {
	// e.printStackTrace();
	// }
	// }

	public String joinPath(String dir)
	{
		if (dir.startsWith("/"))
		{
			return dir;
		} else if (dir.equals(".."))
		{
			String[] path_slices = this.path.split("/");
			if (path_slices.length > 1)
				return stringJoin(path_slices, '/', 1, path_slices.length - 1);
			return "/";
		} else
		{
			if (!path.endsWith("/"))
				return path + "/" + dir;
			return this.path + dir;
		}
	}

	private void println(String str)
	{
		print(str + "\n");
	}

	private void print(String str)
	{
		P.debug(str);
	}

	private String stringJoin(String[] seq, char c, int start, int end)
	{
		if (start >= end)
			return "/";
		StringBuffer buf = new StringBuffer();
		for (int i = start; i < end; i++)
		{
			buf.append(c);
			buf.append(seq[i]);
		}
		return buf.toString();
	}
}
