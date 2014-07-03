package com.byfen.Services;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Toast;

import com.work.Image.SDFile;
import com.work.Image.SaveData;
import com.work.Interface.INotifyDataSetChanged;
import com.work.market.adapter.ObjectListAdapter;
import com.work.market.adapter.SoftListAdapter;
import com.work.market.bean.AppListBean;
import com.work.market.net.HttpUtil;
import com.work.market.server.KItemParser;
import com.work.market.util.KCommand;

public class AdapterService implements OnScrollListener//, IMService
{
	public static final int XML_PARSE_DOWNLOAD = 0x153; // 下载列表下载完成
	private Activity m_context;
	private int PageCount = 0; // 包的总数
	private int KIndex = 2; // 网络包下载序号
	private ObjectListAdapter Adapter;
	private String param; // post
							// 参数
	private ArrayList<AppListBean> m_ArrayData = new ArrayList<AppListBean>(); // 推荐栏目
	private ThreadDownLoad m_downThread = new ThreadDownLoad();
	// private boolean Order = true;// 下载顺序 true:正序　false:倒序
	private View m_LoadView;
	private String FileDay = "_RankDay";
	private String FileName = "_RankData";
	private int m_ItemPos = 0;
	private static final int per_page = 20;
	private int groupid;// 组ID

	public AdapterService(Activity context, View LoadView)
	{
		m_context = context;
		m_LoadView = LoadView;
	}

	public ObjectListAdapter InitData(int type, int groupid)
	{
		this.groupid = groupid;
		
		FileDay = groupid + "_ListDay";
		FileName = groupid + "_ListData";
		KItemParser hip = new KItemParser();
		PageCount = 1;
		String Buffer = null;
		try
		{
			Buffer = SDFile.ReadSDFile(FileName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		m_ArrayData.clear();
		if (Buffer != null)
		{
			hip.parseStringByData(Buffer, m_ArrayData);
			PageCount = hip.getMaxpage();
			KIndex = 2;
			Calendar c = Calendar.getInstance();
			int day = c.get(Calendar.DAY_OF_MONTH);
			int oldDay = SaveData.Read(m_context, FileDay, 1);
			if (day != oldDay)
			{
				KIndex = 1;
				m_downThread.Start(KIndex);
			}
		}
		else
		{
			KIndex = 1;
			m_downThread.Start(KIndex);
		}
		Adapter = new SoftListAdapter(m_context.getBaseContext(), m_ArrayData);
		return Adapter;
	}

	public void notifyDataSetChanged()
	{
		if (Adapter != null) Adapter.notifyDataSetChanged();
	}

	public AppListBean getItem(int pos)
	{
		if (m_ArrayData.size() >= pos)
		{
			m_ItemPos = pos;
			return m_ArrayData.get(pos);
		}
		else
		{
			return null;
		}
	}

	class ThreadDownLoad implements Runnable
	{
		private int Index = 0;
		private boolean isThread;
		private Object Object = new Object();

		public void Start(int index)
		{
			Index = index;
			synchronized (Object)
			{
				if (isThread)
				{
					return;
				}
				isThread = true;
			}
			m_LoadView.setVisibility(View.VISIBLE);

			Thread thread = new Thread(this);
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.start();
		}

		@Override
		public void run()
		{
			List<AppListBean> ItemBean = null;// 推荐栏目
			KItemParser hip = new KItemParser();
			Message msg = new Message();
			InputStream is;
			if (KCommand.isNetConnectNoMsg(m_context))
			{
				try
				{
					// is = KHttpPost.doPost(NetConst.WebSide(), param + Index +
					// "|");
					String url = "http://api.byfen.com/home/list?page=" + Index
							+ "&per_page=" + per_page;

					String json = HttpUtil.doGet(url);
					ItemBean = hip.parseStringByData(json);
					PageCount = hip.getMaxpage();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				msg.arg1 = 2;
				msg.what = XML_PARSE_DOWNLOAD;
				myHandler.sendMessage(msg);
				synchronized (Object)
				{
					isThread = false;
				}
				return;
			}
			if (ItemBean != null)
			{
				if (Index == 1)
				{
					SDFile.WriteSDFile(hip.GetBuffer(), FileName);
					m_ArrayData.clear();
					m_ArrayData.addAll(ItemBean);

					Calendar c = Calendar.getInstance();
					int day = c.get(Calendar.DAY_OF_MONTH);
					SaveData.Write(m_context, FileDay, day);
				}
				else
				{
					m_ArrayData.addAll(ItemBean);
				}
				msg.arg1 = 1;
			}
			else
			{
				msg.arg1 = 0;
			}
			msg.what = XML_PARSE_DOWNLOAD;
			myHandler.sendMessage(msg);
			synchronized (Object)
			{
				isThread = false;
			}
		}
	}

	Handler myHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case XML_PARSE_DOWNLOAD:
				if (msg.arg1 == 1)
				{
					KIndex++;
					Adapter.notifyDataSetChanged();
				}
				else if (msg.arg1 == 2)
				{
					String message = "未找到可用网络，请稍候在试！";
					Toast.makeText(m_context, message, Toast.LENGTH_SHORT)
							.show();
				}
				else
				{
					String message = "下载失败，请稍候在试！";
					Toast.makeText(m_context, message, Toast.LENGTH_SHORT)
							.show();
				}
				m_LoadView.setVisibility(View.GONE);
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}
	};

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount)
	{
		if (totalItemCount != 0
				&& firstVisibleItem + visibleItemCount == totalItemCount)
		{
			if (m_LoadView.getVisibility() != View.VISIBLE
					&& KIndex <= PageCount)
			{
				if (KCommand.isNetConnectNoMsg(m_context))
				{
					m_LoadView.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState)
	{
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& m_LoadView.getVisibility() == View.VISIBLE)
		{
			m_downThread.Start(KIndex);
		}
	}

//	public int getPagePos()
//	{
//		// TODO Auto-generated method stub
//		return 0;
//	}
//
//	public void SetPagePos(int pos, int index)
//	{
//		// TODO Auto-generated method stub
//
//	}

//	@Override
//	public int NextItem()
//	{
//		m_ItemPos++;
//		if (m_ArrayData.size() > m_ItemPos)
//		{// 找到
//			if (mNotifItem != null)
//			{
//				mNotifItem.NotifyDataSetChanged(Const.NextItem,
//						m_ArrayData.get(m_ItemPos));
//			}
//			return 0;
//		}
//		else
//		{ // 未找到
//			String message = "到达最后一条";
//			Toast.makeText(m_context, message, Toast.LENGTH_SHORT).show();
//			return -1;
//		}
//	}
//
//	@Override
//	public int PreItem()
//	{
//		m_ItemPos--;
//		if (m_ItemPos >= 0)
//		{// 找到
//			if (mNotifItem != null)
//			{
//				mNotifItem.NotifyDataSetChanged(Const.NextItem,
//						m_ArrayData.get(m_ItemPos));
//			}
//			return 0;
//		}
//		else
//		{ // 未找到
//			String message = "已经到达第一条";
//			Toast.makeText(m_context, message, Toast.LENGTH_SHORT).show();
//			return -1;
//		}
//	}

	private INotifyDataSetChanged mNotifItem = null;

	public void setNotifItem(INotifyDataSetChanged mNotifItem)
	{
		this.mNotifItem = mNotifItem;
	}

}
