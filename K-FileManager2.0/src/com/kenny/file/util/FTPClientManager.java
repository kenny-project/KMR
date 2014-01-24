package com.kenny.file.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.content.Context;

import com.kenny.KFileManager.R;
import com.kenny.file.bean.NetClientBean;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.net.KHttpPost;
import com.kenny.file.tools.SaveData;
import com.kenny.file.tools.T;

/**
 * 读取本地的GroupFile文件
 * 
 * @author kenny
 */
public class FTPClientManager extends DefaultHandler
{
	private HttpGroupThread m_httpGroup = new HttpGroupThread();
	private ArrayList<NetClientBean> ItemList = new ArrayList<NetClientBean>();// 数据列表
	private KNetParser MyGroupParser = new KNetParser();// 解析XML文件
	private Activity m_context = null;
	private String errorMsg = "";
	private String buffer;
	// private String m_File = "";// 需要操作的XML文件
	/**
	 * 数据更新线程 0:未启动 1：正在运行 2：正在停止 3：停止
	 */
	private int UpdateThreadLive = 0;
	private static final FTPClientManager m_KGroupManage = new FTPClientManager();

	private FTPClientManager()
	{

	}

	/**
	 * 获得当前类实倒，单态模式
	 * 
	 * @return
	 */
	public static FTPClientManager GetHandler()
	{
		return m_KGroupManage;
	}

	private INotifyDataSetChanged m_notifyData = null;

	public void setNotifyData(INotifyDataSetChanged m_notifyData)
	{
		this.m_notifyData = m_notifyData;
	}

	public void SendNotifyData(int cmd,Object object)
	{
		if (m_notifyData != null)
		{
			m_notifyData.NotifyDataSetChanged(1, object);
		}
	}
	// public void SetFileName(String file)
	// {
	// m_File = file;
	// }

	public String GetBuffer()
	{
		return buffer;
	}

	public String GetLastError()
	{
		return errorMsg;
	}

	public ArrayList<NetClientBean> Get()
	{
		return ItemList;
	}

	public void SetContext(Activity context)
	{
		m_context = context;
	}
	/**
	 * 1:删除成功 0:删除失败
	 * 
	 * @param id
	 * @return
	 */
	public boolean Del(NetClientBean bean)
	{
		return ItemList.remove(bean);
	}

	/**
	 * 1:删除成功 0:删除失败
	 * 
	 * @param id
	 * @return
	 */
	public int Del(int id)
	{
		for (int i = 0; i < ItemList.size(); i++)
		{
			int tid = ItemList.get(i).getID();

			if (id == tid)
			{
				ItemList.remove(i);
				return 1;
			}
		}
		return 0;
	}

	public int Add(NetClientBean groupBean)
	{
		try
		{
			NetClientBean m_rb = null;
			int maxId = 1;
			for (int i = 0; i < ItemList.size(); i++)
			{
				int id = ItemList.get(i).getID();
				if (maxId < id)
				{
					maxId = id;
				}
				if (id == groupBean.getID())
				{
					m_rb = ItemList.get(i);
					break;
				}
			}
			if (m_rb == null)
			{
				m_rb = groupBean;
				m_rb.setID(maxId + 1);
			} else
			{
				m_rb.setTitle(groupBean.getTitle());
				m_rb.setDesc(groupBean.getDesc());
				m_rb.setUserName(groupBean.getUserName());
				m_rb.setPassWord(groupBean.getPassWord());
				m_rb.setDate(groupBean.getDate());
				m_rb.setType(groupBean.getType());
				m_rb.setHost(groupBean.getHost());
				m_rb.setPort(groupBean.getPort());
			}
			ItemList.add(m_rb);
			SaveRamdFile();
			return 1;
		} catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	public boolean SaveRamdFile()
	{
		return WriteRAMFile(ItemList);
	}

	// 将类转换成相应的XML
	private boolean WriteRAMFile(ArrayList<NetClientBean> arrayList)
	{
		StringBuilder szBuffer = new StringBuilder();
		szBuffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		szBuffer.append("<rss version=\"2.0\">");
		szBuffer.append("<channel>");
		szBuffer.append("<title>K-Manager</title>");
		for (int i = 0; i < arrayList.size(); i++)
		{
			NetClientBean temp = arrayList.get(i);
			szBuffer.append("<item><id>");
			szBuffer.append(temp.getID());
			szBuffer.append("</id><title>");
			szBuffer.append(temp.getTitle());
			szBuffer.append("</title><desc>");
			szBuffer.append(temp.getDesc());
			szBuffer.append("</desc>");
			szBuffer.append("<un>" + temp.getUserName() + "</un>");
			szBuffer.append("<ps>" + temp.getPassWord() + "</ps>");
			szBuffer.append("<date>" + temp.getDate() + "</date>");
			szBuffer.append("<host>" + temp.getHost() + "</host>");
			szBuffer.append("<port>" + temp.getPort() + "</port>");
			szBuffer.append("<type>" + temp.getType() + "</type>");
			szBuffer.append("</item>");
		}
		szBuffer.append("</channel></rss>");

		String m_File = m_context.getString(R.string.FTPClientConfig);
		// 写文件到手机内存
		return SDFile.WriteRAMFile(m_context, szBuffer.toString(), m_File, 0);
	}

	// 将类转换成相应的XML
	public List<NetClientBean> ReadRAMFile()
	{
		try
		{
			String m_File = m_context.getString(R.string.FTPClientConfig);
			if (!SDFile.CheckRAMFile(m_context, m_File))
			{
				T.ResourceAssetsFile(m_context, m_File);
			}
			String Data = SDFile.ReadRAMFile(m_context, m_File, 0);
			if (Data != null)
			{
				ItemList.clear();
				ArrayList<NetClientBean> TempItemList = MyGroupParser
						.parseJokeStringByData(Data);
				ItemList.addAll(TempItemList);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return ItemList;
	}

	public static enum NetStatus
	{
		NotStart, Run, finish, close
	}

	// 更新Group页
	public void UpdateGroupPage(boolean ReLoad)
	{
		if (UpdateThreadLive == 0 && NetConst.isNetConnectNoMsg(m_context))
		{
			Calendar c = Calendar.getInstance();
			int day = c.get(Calendar.DAY_OF_MONTH);
			int oldDay = SaveData.Read(m_context,
					"MyGroupListDay",-1);
			if (day != oldDay || ReLoad)
			{

				if (m_httpGroup.isLock())
				{
					return;
				}
				m_httpGroup.setLock(true);
				m_httpGroup.SetContext(m_context);
				Thread thread = new Thread(m_httpGroup);
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.start();
			}
		}
	}

	// 网络联接下载分组列表
	public class HttpGroupThread implements Runnable
	{
		public String url;
		private NetStatus live;
		private boolean isLock = false;
		private Object o = new Object();
		private Context m_cx;

		public void SetContext(Context context)
		{
			m_cx = context;
			this.url = NetConst.WebSide();
		}

		public boolean isLock()
		{
			synchronized (o)
			{
				return isLock;
			}
		}

		// true:正在使用 false:未使用
		public void setLock(boolean isLock)
		{
			synchronized (o)
			{
				this.isLock = isLock;
			}
		}

		public NetStatus getLive()
		{
			synchronized (o)
			{
				return live;
			}
		}

		public void setLive(NetStatus live)
		{
			synchronized (o)
			{
				this.live = live;
			}
		}

		private boolean DownLoadGroup() throws IOException
		{
			ArrayList<NetClientBean> TypeBean = new ArrayList<NetClientBean>();
			UpdateThreadLive = 1;
			String param = "code=1&uid=0&key=1&value=0|";
			try
			{
				InputStream is;
				is = KHttpPost.doPost(NetConst.WebSide(), param);
				TypeBean = MyGroupParser.parseRssByUrl(is);
				if (TypeBean != null && TypeBean.size() > 0)
				{
					for (int i = 0; i < TypeBean.size(); i++)
					{
						NetClientBean source = TypeBean.get(i);
						Add(source);
					}
					SaveRamdFile();// 测试
					SendNotifyData(1, this);

				}
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			UpdateThreadLive = 3;
			return true;
		}

		
		public void run()
		{
			this.isLock = true;
			setLive(NetStatus.Run);
			// 创建URL对象
			try
			{
				DownLoadGroup();
				Calendar c = Calendar.getInstance();
				int day = c.get(Calendar.DAY_OF_MONTH);
				SaveData.Write(m_cx, "MyGroupListDay", day);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			setLive(NetStatus.finish);// 标记为未启动
			this.isLock = false;
		}
	}
}
