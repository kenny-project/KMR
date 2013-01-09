package com.kuaipan.demo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;

import com.framework.event.NextPageEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kuaipan.client.KuaipanAPI;
import com.kuaipan.client.exception.KuaipanAuthExpiredException;
import com.kuaipan.client.exception.KuaipanIOException;
import com.kuaipan.client.exception.KuaipanServerException;
import com.kuaipan.client.model.KuaipanFile;
import com.kuaipan.client.model.KuaipanHTTPResponse;
import com.kuaipan.client.session.OauthSession;

public class SimpleCommandConsole
{
	private String path = "/";
	private String prompt = ">";
	StringBuffer stdout = new StringBuffer();
	private OauthSession session;
	private Activity context;
	private static SimpleCommandConsole handler;

	public String getPath()
	{
		if (!path.endsWith("/"))
		{
			return path = path + "/";
		}
		return path;
	}

	public static SimpleCommandConsole getHandler(Activity context)
	{
		if (handler == null)
		{
			handler = new SimpleCommandConsole(context);
		}
		return handler;
	}

	private SimpleCommandConsole(Activity context)
	{
		this.context = context;
		session = new OauthSession(KPTestUtility.CONSUMER_KEY,
				KPTestUtility.CONSUMER_SECRET, OauthSession.Root.APP_FOLDER);
		KuaipanAPI.setSession(session);
	}

	public void setAuthToken(String key, String secret)
	{
		session.setAuthToken(key, secret);
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
				do_mkdir(value);
			else if (cmd.equals("rm"))
				do_rm(value);
			else if (cmd.equals("cat"))
				do_cat(value);
			else if (cmd.equals("exit"))
				return false;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return true;
	}

	private final String TAG = "kuaipan";

	public List<KuaipanFile> do_ls() throws KuaipanIOException,
			KuaipanServerException, KuaipanAuthExpiredException
	{
		return do_ls(path);
	}

	public List<KuaipanFile> do_ls(String path)
	{
		P.v(TAG, "do_ls start:path=" + path);
		KuaipanFile file = KuaipanAPI.metadata(path, true);
		if (file != null)
		{
			this.path = path;
			if (file.files != null)
			{
				for (KuaipanFile temp : file.files)
				{
					temp.setKuaiPanFolderPath(path);
				}
			}
			return file.files;
		}
		P.v("do_ls end");
		return null;
	}

	public String do_cd(String dir)
	{
		return joinPath(dir);
	}

	public KuaipanFile do_mkdir(String dir) throws KuaipanIOException,
			KuaipanServerException, KuaipanAuthExpiredException
	{
		return KuaipanAPI.createFolder(joinPath(dir));
	}

	public void do_rm(String dir) throws KuaipanIOException,
			KuaipanServerException, KuaipanAuthExpiredException
	{
		KuaipanAPI.delete(joinPath(dir));
	}

	public void do_cat(String dir) throws KuaipanIOException,
			KuaipanServerException, KuaipanAuthExpiredException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		KuaipanHTTPResponse resp = KuaipanAPI.downloadFile(joinPath(dir), os,
				null);
		P.v("kuaipan", "resp.code =" + resp.code);
		// assertTrue(resp.code == 200); //by wmh
		String download_content = KPTestUtility.outputStream2String(os);
		try
		{
			os.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

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

	private void openBrowser(Activity context, String strUrl)
	{
		// Uri uri = Uri.parse(strUrl);
		// Intent it = new Intent(Intent.ACTION_VIEW, uri);
		// context.startActivity(it);
		SysEng.getInstance().addHandlerEvent(
				new NextPageEvent(context,
						new KuaiPanLoginPage(context, strUrl), 1, null));

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
