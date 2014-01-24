package com.kenny.util;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.kenny.adapter.VOAAdapter;
import com.kenny.dailyenglish.R;
import com.kenny.data.VOANetData;
import com.kenny.file.SDFile;
import com.kenny.file.SaveData;

//双语资讯
public class VOAService {
	public static final int XML_PARSE_DOWNLOAD = 0x153;// 下载列表下载完成
	private Activity m_context;
	private int PagePos = 1;// 包的指针
	private String url = "http://voa.iciba.com/index.php?app=voaapi&act=list&isfree=1";// 网络数据地址
//	private final String url = "http://192.168.10.91:92/index.php?app=voaapi&act=listjp";// 网络数据地址
	
	private VOAAdapter Adapter;
	private ArrayList<VOANetData> m_ArrayData = new ArrayList<VOANetData>();// 推荐栏目
	private ThreadDownLoad m_downThread = new ThreadDownLoad();
	private View m_LoadView;
	private int nPCount = 4;
	private String FileDay = "st";// _StoreDay
	private String FileName = "sd";// "_StoreData";
	private boolean isShowDialog = false;
	private View mPannal, mButton;
	private View InitPannal;
	private View InitLoading;
	private View InitError;
	private String uuid = "ddfdsafdsa";
	
	public boolean isSubscribe() 
	{
		return Adapter.isSubscribe();
	}

	public void setSubscribe(boolean bSubscribe) 
	{
		if(bSubscribe)
		{
			url = "http://voa.iciba.com/index.php?app=voaapi&act=list&isfree=0";
		}
		else
		{
			url = "http://voa.iciba.com/index.php?app=voaapi&act=list&isfree=1";
		}
		Adapter.setSubscribe(bSubscribe);
	}

	/**
	 * 
	 * @param context
	 * @param type
	 * @param nPCount
	 * @param pannal
	 * @param button
	 * @param LoadView
	 */
	public VOAService(Activity context, int nPCount, View pannal, View button,
			View LoadView, View InitPannal, View InitLoading, View InitError) {
		this.InitPannal = InitPannal;
		this.InitLoading = InitLoading;
		this.InitError = InitError;

		m_context = context;
		mPannal = pannal;
		mButton = button;
		m_LoadView = LoadView;
		isShowDialog = false;
		this.nPCount = nPCount;
		int type = Const.VOA_Page;
		FileDay = type + "_Day";
		FileName = type + "_Data";
		Adapter = new VOAAdapter(m_context.getBaseContext(), m_ArrayData);// by
	}

	public void StoreInitData() {
		if (m_ArrayData.size() > 0)
			return;
		VOAItemParser hip = new VOAItemParser();
		String Buffer = null;
		PagePos = 1;
		try 
		{
			Buffer = SDFile.ReadRAMFile(m_context, FileName);
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		m_ArrayData.clear();
		
		// Buffer=null;
		if (Buffer != null) {
			ArrayList<VOANetData> tempData = hip.parseJokeByData(m_context,
					Buffer);
			nPCount=hip.GetPageCount();
			m_ArrayData.addAll(tempData);
			Calendar c = Calendar.getInstance();
			int day = c.get(Calendar.DAY_OF_MONTH);
			int oldDay = SaveData.readPreferencesInt(m_context, FileDay);
			if (day != oldDay) 
			{
				isShowDialog = false;
				Start();
			}
			else
			{
				if(nPCount<=1)
				{
				mButton.setVisibility(View.GONE);
				}
			}
			InitPannal.setVisibility(View.GONE);
			mPannal.setVisibility(View.VISIBLE);
		} else {
			InitPannal.setVisibility(View.VISIBLE);
			InitLoading.setVisibility(View.VISIBLE);
			mPannal.setVisibility(View.GONE);
			isShowDialog = true;
			Start();
		}
		notifyDataSetChanged();
	}

	public ArrayAdapter getAdapter() {
		return Adapter;
	}

	public void Start() {
		m_downThread.Start(PagePos);
	}

	public void notifyDataSetChanged() {
		if (Adapter != null)
			Adapter.notifyDataSetChanged();
	}

	private int mNowPos;// 当前索引位置

	public VOANetData getNowBilingualBean() {
		return m_ArrayData.get(mNowPos);
	}

	public VOANetData get(int pos) {
		if (m_ArrayData.size() >= pos) {
			this.mNowPos = pos;
			return m_ArrayData.get(pos);
		} else {
			return null;
		}
	}

	class ThreadDownLoad implements Runnable {
		private int Index = 0;
		private boolean isThread;
		private Object Object = new Object();

		public void Start(int index) {
			Index = index;
			synchronized (Object) {
				if (isThread) {
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
		public void run() {
			ArrayList<VOANetData> ItemBean = null;// 推荐栏目
			VOAItemParser hip = new VOAItemParser();
			Message msg = new Message();
			msg.arg2 = Index;
			if (KCommand.isNetConnectNoMsg(m_context)) {
				try {
					Log.v("wmh", "VOAService start"+url + Index);
					ItemBean = hip.parseDataByHTTPGet(m_context, url, uuid, Index);
					Index = hip.GetPagePos();
					nPCount=hip.GetPageCount();
					Log.v("wmh", "net:PagePos="+Index+",nPCount="+nPCount);
					msg.arg2 = Index;
					if (ItemBean != null && ItemBean.size() > 0) 
					{
						PagePos = Index;
						if (Index == 1) {
							m_ArrayData.clear();
							m_ArrayData.addAll(ItemBean);
							if (SDFile.WriteRAMFile(m_context, hip.GetBuffer(),
									FileName)) {
								Calendar c = Calendar.getInstance();
								int day = c.get(Calendar.DAY_OF_MONTH);
								SaveData.writePreferencesInt(m_context,
										FileDay, day);
							}
						}
						else 
						{
							m_ArrayData.addAll(ItemBean);
						}
						msg.arg1 = 1;
					} else {
						msg.arg1 = 0;
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.arg1 = 3;
				}
				msg.what = XML_PARSE_DOWNLOAD;
				myHandler.sendMessage(msg);
			} else {
				msg.arg1 = 2;
				msg.what = XML_PARSE_DOWNLOAD;
				myHandler.sendMessage(msg);
			}
			synchronized (Object) {
				isThread = false;
			}
		}

		Handler myHandler = new Handler() {
			@Override
			public void handleMessage(final Message msg) {

				switch (msg.what) 
				{
				case XML_PARSE_DOWNLOAD:
					if (msg.arg1 == 1) {
						if (isShowDialog) {
							isShowDialog = false;
							InitPannal.setVisibility(View.GONE);
							mPannal.setVisibility(View.VISIBLE);
						}
						notifyDataSetChanged();
						if (msg.arg2>= nPCount) {
							mButton.setVisibility(View.GONE);
						}
					} else {
						if (isShowDialog) {
							isShowDialog = false;
							InitPannal.setVisibility(View.VISIBLE);
							InitLoading.setVisibility(View.GONE);
							InitError.setVisibility(View.VISIBLE);
							mPannal.setVisibility(View.GONE);
						}
						if (msg.arg1 == 2) {
							Toast.makeText(
									m_context,
									m_context
											.getString(R.string.msg_not_network),
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(
									m_context,
									m_context
											.getString(R.string.download_error),
									Toast.LENGTH_SHORT).show();
						}
					}
					m_LoadView.setVisibility(View.GONE);

					break;
				default:
					super.handleMessage(msg);
					break;
				}
			}
		};
	}

	public VOANetData NextItem() {
		if (mNowPos < m_ArrayData.size() - 1) {
			mNowPos++;
			return m_ArrayData.get(mNowPos);
		} else {
			// NextPage();
			return null;
		}
	}

	/**
	 * 第一条
	 * 
	 * @return
	 */
	public boolean BOF() {
		return mNowPos == 0;
	}

	/**
	 * 最后一条
	 * 
	 * @return
	 */
	public boolean EOF() {
		return mNowPos == m_ArrayData.size() - 1;
	}

	public VOANetData PreItem() {
		if (mNowPos > 0) {
			mNowPos--;
			return m_ArrayData.get(mNowPos);
		} else {
			return null;
		}
	}

	public int NextPage() {
		if (m_LoadView.getVisibility() != View.VISIBLE) 
		{
			int temp = PagePos + 1;
			Log.v("wmh", "PagePos="+PagePos+",nPCount="+nPCount);
			if (temp <= nPCount) 
			{
				if (KCommand.isNetConnectNoMsg(m_context)) {
					m_LoadView.setVisibility(View.VISIBLE);
					m_downThread.Start(temp);
					return 1;
				} else {
					Toast.makeText(m_context,
							m_context.getString(R.string.msg_not_network),
							Toast.LENGTH_SHORT).show();
					return -1;
				}
			}
			else 
			{
				mButton.setVisibility(View.GONE);
				return 3;
			}
		} else {
			Toast.makeText(m_context,
					m_context.getString(R.string.msg_Loading_Please_retry),
					Toast.LENGTH_SHORT).show();
		}
		return 2;
	}
}
