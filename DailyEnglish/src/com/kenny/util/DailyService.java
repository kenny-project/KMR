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

import com.kenny.adapter.KStoreItemAdapter;
import com.kenny.dailyenglish.R;
import com.kenny.data.DailyNetData;
import com.kenny.file.SDFile;
import com.kenny.file.SaveData;

public class DailyService {
	public static final int XML_PARSE_DOWNLOAD = 0x153;// 下载列表下载完成
	private Activity m_context;
	private static final int COUNT = 7;
	private int PagePos = 1;// 包的指针
	// private int PageCount = 5;
	private String url = "http://voa.iciba.com/index.php?app=voaapi&act=sentencelist&pagesize=10&page=";// 网络数据地址
	// private String url =
	// "http://192.168.10.91:92/index.php?app=voaapi&act=sentencelist&pagesize=10&page=";//
	// 网络数据地址
	private ArrayAdapter Adapter;
	private ArrayList<DailyNetData> m_ArrayData = new ArrayList<DailyNetData>();// 推荐栏目
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

	/**
	 * 
	 * @param context
	 * @param type
	 * @param nPCount
	 * @param pannal
	 * @param button
	 * @param LoadView
	 */
	public DailyService(Activity context, int nPCount, View pannal,
			View button, View LoadView, View InitPannal, View InitLoading,
			View InitError) {
		this.InitPannal = InitPannal;
		this.InitLoading = InitLoading;
		this.InitError = InitError;

		m_context = context;
		mPannal = pannal;
		mButton = button;
		m_LoadView = LoadView;
		isShowDialog = false;
		this.nPCount = nPCount;
		FileDay = "dailyStoeDay";
		FileName = "dailyStoreData";
		// String mod = "index", act = "index";
		// String k = "";
		Adapter = new KStoreItemAdapter(m_context.getBaseContext(), m_ArrayData);
		// mod = "index";
		// act = "index";
		// k = Coder.Md5(mod, act);
		// url = "http://dict-mobile.iciba.com/new/index.php?v=2&k=" + k +
		// "&pos=";// 网络数据地址
		// url =
		// "http://dict-mobile.iciba.com/new/newtest/index.php?k="+k+"&pos=";//
		// 网络数据地址
	}

	public void StoreInitData() {
		if (m_ArrayData.size() > 0)
			return;
		DailyItemParser hip = new DailyItemParser();
		String Buffer = null;
		PagePos = 1;
		try {
			Buffer = SDFile.ReadRAMFile(m_context, FileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		m_ArrayData.clear();
		// Buffer=null;
		if (Buffer != null) {
			ArrayList<DailyNetData> tempData = hip.parseJokeByData(m_context,
					Buffer);
			nPCount = hip.GetPageCount();
			m_ArrayData.addAll(tempData);
			Calendar c = Calendar.getInstance();
			int day = c.get(Calendar.DAY_OF_MONTH);
			int oldDay = SaveData.readPreferencesInt(m_context, FileDay);
			if (day != oldDay) {
				isShowDialog = false;
				Start();
			} else {
				if (nPCount <= 1)

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

	public DailyNetData get(int pos) {
		if (m_ArrayData.size() >= pos) {
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
			ArrayList<DailyNetData> ItemBean = null;// 推荐栏目
			DailyItemParser hip = new DailyItemParser();
			Message msg = new Message();
			msg.arg2 = Index;
			if (KCommand.isNetConnectNoMsg(m_context)) {
				try {
					Log.v("wmh", url + Index);
					url = "http://test.dict-mobile.iciba.com/new/index.php?act=list&k=af164df2429ab67de7ea6d9bc461de25";
					ItemBean = hip.parseJokeByUrl(m_context, url ,Index,COUNT);
					//ItemBean = hip.parseJokeByUrl(m_context, url + Index);//原日文链接
					msg.arg2 = Index;
					nPCount = hip.GetPageCount();
					if (ItemBean != null && ItemBean.size() > 0) {
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
						} else {
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

				switch (msg.what) {
				case XML_PARSE_DOWNLOAD:
					if (msg.arg1 == 1) {
						if (isShowDialog) {
							isShowDialog = false;
							InitPannal.setVisibility(View.GONE);
							mPannal.setVisibility(View.VISIBLE);
						}
						notifyDataSetChanged();
						if (msg.arg2 >= nPCount) {
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
											.getString(R.string.msg_Network_not_found),
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

	public int NextPage() {
		if (m_LoadView.getVisibility() != View.VISIBLE) {
			int temp = PagePos + 1;
			if (temp <= nPCount) {
				if (KCommand.isNetConnectNoMsg(m_context)) {
					m_LoadView.setVisibility(View.VISIBLE);
					m_downThread.Start(temp);
					return 1;
				} else {
					Toast.makeText(m_context,m_context.getString(R.string.msg_Network_not_found),
							Toast.LENGTH_SHORT).show();
				}
			} else {
				mButton.setVisibility(View.GONE);
				return 3;
			}
			return -1;
		} else {
			Toast.makeText(m_context,
					m_context.getString(R.string.msg_Loading_Please_retry),
					Toast.LENGTH_SHORT).show();
		}
		return 2;
	}
}
