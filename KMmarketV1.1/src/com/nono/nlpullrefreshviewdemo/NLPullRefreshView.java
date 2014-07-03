package com.nono.nlpullrefreshviewdemo;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.byfen.market.R;
import com.umeng.common.net.s;

/**
 * 
 * Copyright (c) 2013 Nono_Lilith All right reserved.
 * -------------------------------------------------------
 * 
 * @Description: NLPullRefreshView
 * @Author: Nono(�¿�)
 * @CreateDate: 2013--9 ����1:44:17
 * @version 1.0.0
 * 
 */
public class NLPullRefreshView extends LinearLayout {
	private static final String TAG = "LILITH";

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	enum Status {
		NORMAL, DRAGGING, REFRESHING,
	}

	private Status status = Status.NORMAL;

	private final static String REFRESH_RELEASE_TEXT = "�ͷź�ִ��ˢ��";
	private final static String REFRESH_DOWN_TEXT = "������׼��ִ��ˢ��";
	private final static float MIN_MOVE_DISTANCE = 5.0f;// ��С�ƶ����룬�����ж��Ƿ�������������Ϊ0��touch�¼����жϻ����ƽ��������ֵ���Ը����Լ����趨

	private Scroller scroller;
	private View refreshView;
	private ImageView refreshIndicatorView;
	private int refreshTargetTop = -50;
	private ProgressBar bar;
	private TextView downTextView;
	private TextView timeTextView;

	private RefreshListener refreshListener;// ˢ�¼�����

	// ��Ҫ�õ�����������
	private String downCanRefreshText;
	private String releaseCanRefreshText;

	private String refreshTime ;
	private int lastY;
	private Context mContext;

	public NLPullRefreshView(Context context) {
		super(context);
		mContext = context;
	}

	public NLPullRefreshView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	/**
	 * ��ʼ��
	 * 
	 * @MethodDescription init
	 * @exception
	 * @since 1.0.0
	 */
	private void init() {
		// TODO Auto-generated method stub
		refreshTargetTop = getPixelByDip(mContext, refreshTargetTop);
		// ��������
		scroller = new Scroller(mContext);
		// ˢ����ͼ���˵ĵ�view
		refreshView = LayoutInflater.from(mContext).inflate(
				R.layout.refresh_top_item, null);
		// ָʾ��view
		refreshIndicatorView = (ImageView) refreshView
				.findViewById(R.id.indicator);
		// ˢ��bar
		bar = (ProgressBar) refreshView.findViewById(R.id.progress);
		// ������ʾtext
		downTextView = (TextView) refreshView.findViewById(R.id.refresh_hint);
		// ������ʾʱ��
		timeTextView = (TextView) refreshView.findViewById(R.id.refresh_time);
		LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, -refreshTargetTop);
		lp.topMargin = refreshTargetTop;
		lp.gravity = Gravity.CENTER;
		addView(refreshView, lp);
		// //������Դ���Թ鵵����Դ���У��˴�Ϊ�˷��㡣
		downCanRefreshText = REFRESH_DOWN_TEXT;
		releaseCanRefreshText = REFRESH_RELEASE_TEXT;
		refreshTime = "1989-12-24 12:12:12";//���Դӱ����ļ���ȡ���ϴεĸ���ʱ��
		if (refreshTime != null) {
			setRefreshTime(refreshTime);
		}
	}

	/**
	 * ����ˢ�º������
	 * 
	 * @MethodDescription setRefreshText
	 * @param time
	 * @exception
	 * @since 1.0.0
	 */
	private void setRefreshText(String time) {
		Log.i(TAG, "------>setRefreshText");
		//timeTextView.setText(time);
		timeTextView.setText("");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if (status == Status.REFRESHING)
			return false;
		
		int y = (int) event.getRawY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.i(TAG, "MotionEvent.ACTION_DOWN");
			// ��¼��y����
			lastY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			Log.i(TAG, "MotionEvent.ACTION_MOVE");
			// y�ƶ�����
			int m = y - lastY;
			doMovement(m);
			// ��¼�´˿�y����
			this.lastY = y;
			break;
		case MotionEvent.ACTION_UP:
			Log.i(TAG, "MotionEvent.ACTION_UP");
			fling();
			break;
		}
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
		
		// layout��ȡtouch�¼�
		int action = e.getAction();
		int y = (int) e.getRawY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			lastY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			// y�ƶ�����
			int m = y - lastY;
			// ��¼�´˿�y����
			this.lastY = y;
			if (m > MIN_MOVE_DISTANCE && canScroll()) {
				Log.i(TAG, "canScroll");
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return false;
	}

	/**
	 * up�¼�����
	 */
	private void fling() {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams lp = (LayoutParams) refreshView
				.getLayoutParams();

		if (lp.topMargin > 0) {// �����˴�����ˢ���¼�
			status = Status.REFRESHING;
			Log.i(TAG, "fling ----->REFRESHING");
			refresh();
		} else {
			Log.i(TAG, "fling ----->NORMAL");
			status = Status.NORMAL;
			returnInitState();
		}
	}

	private void returnInitState() {
		// TODO Auto-generated method stub
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
				.getLayoutParams();
		int i = lp.topMargin;
		Log.i(TAG, "returnInitState top = "+i);
		scroller.startScroll(0, i, 0, refreshTargetTop);
		invalidate();
	}

	/**
	 * ִ��ˢ��
	 * 
	 * @MethodDescription refresh
	 * @exception
	 * @since 1.0.0
	 */
	private void refresh() {
		// TODO Auto-generated method stub
		Log.i(TAG, " ---> refresh()");
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
				.getLayoutParams();
		int i = lp.topMargin;
		refreshIndicatorView.setVisibility(View.GONE);
		bar.setVisibility(View.VISIBLE);
		downTextView.setVisibility(View.GONE);
		scroller.startScroll(0, i, 0, 0 - i);
		invalidate();
		if (refreshListener != null) {
			refreshListener.onRefresh(this);
		}
	}

	/**
	 * 
	 */
	@Override
	public void computeScroll() {
		if (scroller.computeScrollOffset()) {//scroll ������δ����
			Log.i(TAG, "----->computeScroll()");
			int i = this.scroller.getCurrY();
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
					.getLayoutParams();
			int k = Math.max(i, refreshTargetTop);
			lp.topMargin = k;
			this.refreshView.setLayoutParams(lp);
			postInvalidate();
		}
	}

	/**
	 * ����move�¼�����
	 * 
	 * @param moveY
	 */
	private void doMovement(int moveY) {
		status = Status.DRAGGING;
		LinearLayout.LayoutParams lp = (LayoutParams) refreshView
				.getLayoutParams();
		float f1 = lp.topMargin;
		float f2 = moveY * 0.3F;// ��0.3�����϶�
		int i = (int) (f1 + f2);
		// �޸��ϱ߾�
		lp.topMargin = i;
		// �޸ĺ�ˢ��
		refreshView.setLayoutParams(lp);
		refreshView.invalidate();
		invalidate();

		timeTextView.setVisibility(View.VISIBLE);
		downTextView.setVisibility(View.VISIBLE);
		refreshIndicatorView.setVisibility(View.VISIBLE);
		bar.setVisibility(View.GONE);
		if (lp.topMargin > 0) {
			downTextView.setText(releaseCanRefreshText);
			refreshIndicatorView.setImageResource(R.drawable.refresh_arrow_up);
		} else {
			downTextView.setText(downCanRefreshText);
			refreshIndicatorView
					.setImageResource(R.drawable.refresh_arrow_down);
		}

	}

	/**
	 * ����ˢ��ʱ��
	 * @MethodDescription setRefreshTime 
	 * @param refreshTime 
	 * @exception s
	 * @since  1.0.0
	 */
	public void setRefreshTime(String refreshTime){
		//timeTextView.setText("������:"+refreshTime);
		timeTextView.setText("");
	}

	/**
	 * ���ü���
	 * @MethodDescription setRefreshListener 
	 * @param listener 
	 * @exception 
	 * @since  1.0.0
	 */
	public void setRefreshListener(RefreshListener listener) {
		this.refreshListener = listener;
	}

	/**
	 * ˢ��ʱ��
	 * 
	 * @param refreshTime2
	 */
	private void refreshTimeBySystem() {
		String dateStr = dateFormat.format(new Date());//���Խ�ʱ�䱣������
		this.setRefreshText("������:"+dateStr);
		
	}

	/**
	 * ����ˢ���¼�
	 */
	public void finishRefresh() {
		Log.i(TAG, "------->finishRefresh()");
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView
				.getLayoutParams();
		int i = lp.topMargin;
		refreshIndicatorView.setVisibility(View.VISIBLE);//������ͷ��ʾ
		timeTextView.setVisibility(View.VISIBLE);//ʱ��ؼ�
		downTextView.setVisibility(VISIBLE);//������ʾ��ؼ�
		refreshTimeBySystem();//�޸�ʱ�䣻
		bar.setVisibility(GONE);
		scroller.startScroll(0, i, 0, refreshTargetTop);
		invalidate();
		status = Status.NORMAL;
	}

	/**
	 * �˷������������Ӳ��ֵ��жϣ�listview��scrollview ��Ҫ�������ж�������View�Ƿ�������������棬���ǣ����ʾ�˴�touch
	 * move�¼���ȡȻ����layout���������ƶ�������ͼ����֮��Ȼ
	 * 
	 * @MethodDescription canScroll
	 * @return
	 * @exception
	 * @since 1.0.0
	 */
	private boolean canScroll() {
		// TODO Auto-generated method stub
		View childView;
		if (getChildCount() > 1) {
			childView = this.getChildAt(1);
			if (childView instanceof ListView) {
				int top = ((ListView) childView).getChildAt(0).getTop();
				int pad = ((ListView) childView).getListPaddingTop();
				if ((Math.abs(top - pad)) < 3
						&& ((ListView) childView).getFirstVisiblePosition() == 0) {
					return true;
				} else {
					return false;
				}
			} else if (childView instanceof ScrollView) {
				if (((ScrollView) childView).getScrollY() == 0) {
					return true;
				} else {
					return false;
				}
			}

		}
		return false;
	}

	public static int getPixelByDip(Context c, int pix) {
		float f1 = c.getResources().getDisplayMetrics().density;
		float f2 = pix;
		return (int) (f1 * f2 + 0.5F);
	}

	/**
	 * ˢ�¼����ӿ�
	 * 
	 * @author Nono
	 * 
	 */
	public interface RefreshListener 
	{
		public void onRefresh(NLPullRefreshView view);
	}
	
	
}
