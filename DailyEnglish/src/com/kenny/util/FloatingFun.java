package com.kenny.util;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kenny.dailyenglish.R;

/**
 * 可以永远显示在android屏幕最上方的浮动菜单
 * 
 * @author liujl v1.0 需要添加 <uses-permission
 *         android:name="android.permission.SYSTEM_ALERT_WINDOW"
 *         /><!--系统弹出窗口权限-->权限不然会报错
 */
public class FloatingFun {
	/**
	 * 浮动窗口在屏幕中的x坐标
	 */
	private static float x = 0;
	/**
	 * 浮动窗口在屏幕中的y坐标
	 */
	private static float y = 200;
	/**
	 * 鼠标触摸开始位置
	 */
	private static float mTouchStartX = 0;
	/**
	 * 鼠标触摸结束位置
	 */
	private static float mTouchStartY = 0;
	/**
	 * windows 窗口管理器
	 */
	private static WindowManager wm = null;

	/**
	 * 浮动显示对象
	 */
	private static View floatingViewObj = null;

	/**
	 * 参数设定类
	 */
	public static WindowManager.LayoutParams params = new WindowManager.LayoutParams();
	public static int TOOL_BAR_HIGH = 0;
	private static Context context;

	private static float moveX, moveY;

	private static float viewX, viewY;

	/**
	 * 要显示在窗口最前面的对象
	 */
	private static View view_obj = null;

	/**
	 * 要显示在窗口最前面的方法
	 * 
	 * @param context
	 *            调用对象Context getApplicationContext()
	 * @param window
	 *            调用对象 Window getWindow()
	 * @param floatingViewObj
	 *            要显示的浮动对象 View
	 */
	public static void show(Context context, View floatingViewObj,
			int[] position, int derection) {
		// 加载xml文件中样式例子代码
		// ********************************Start**************************
		// LayoutInflater inflater =
		// LayoutInflater.from(getApplicationContext());
		// View view = inflater.inflate(R.layout.topframe, null);
		// wm =
		// (WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		// 加载xml文件中样式例子代码
		// *********************************End***************************
		//
		// 关闭浮动显示对象然后再显示
		if (view_obj != null && view_obj.isShown()) {
			return;
		}
		close(context);
		FloatingFun.context = context;
		FloatingFun.floatingViewObj = floatingViewObj;
		view_obj = floatingViewObj;

		Rect frame = new Rect();
		// 这一句是关键，让其在top 层显示
		// getWindow()
		floatingViewObj.getRootView().getWindowVisibleDisplayFrame(frame);
		TOOL_BAR_HIGH = frame.top;
		wm = (WindowManager) context// getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
				| WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE;
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE); // 获取当前屏幕管理器对象
		Display display = wm.getDefaultDisplay(); // 获取屏幕信息的描述类
		int screenW = display.getWidth();
		int screenH = display.getHeight();
		// 设置悬浮窗口长宽数据
		if (derection == 1) {
			params.width = (int) (display.getHeight() * 0.4);
		} else {
			params.width = (int) (display.getWidth() * 0.4);
		}

		params.height = Utils.dip2px(context, 163);
		// 设定透明度
		// params.alpha = 100;
		params.format = PixelFormat.RGBA_8888;
		// 设定内部文字对齐方式
		params.gravity = Gravity.LEFT | Gravity.TOP;

		// 以屏幕左上角为原点，设置x、y初始值?
		if (position[0] != 0) {
			params.x = position[0];
			params.y = position[1];
		}

//		TextView tv = (TextView) floatingViewObj.findViewById(R.id.word);

		// tv.setText("woc ao");
		// tv = new MyTextView(TopFrame.this);
		wm.addView(floatingViewObj, params);

	}

	public static void showResult(Context context, View floatingViewObj,
			View view) {
		close(context);
		int[] arrayOfInt = new int[2];
		view.getLocationOnScreen(arrayOfInt);
		view_obj = floatingViewObj;
		Rect frame = new Rect();
		// 这一句是关键，让其在top 层显示
		// getWindow()
		floatingViewObj.getRootView().getWindowVisibleDisplayFrame(frame);
		TOOL_BAR_HIGH = frame.top;
		wm = (WindowManager) context// getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
				| WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
		params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE;

		// 设置悬浮窗口长宽数据
		params.width = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		params.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
		// 设定透明度
		params.alpha = 80;
		params.format = PixelFormat.RGBA_8888;
		// 设定内部文字对齐方式
		params.gravity = Gravity.LEFT | Gravity.TOP;

		// 以屏幕左上角为原点，设置x、y初始值?
		params.x = arrayOfInt[0];
		params.y = arrayOfInt[1];
		// tv = new MyTextView(TopFrame.this);
		wm.addView(floatingViewObj, params);
		wm.notify();
	}

	private static boolean isMove = false;

	/**
	 * 跟谁滑动移动
	 * 
	 * @param event
	 *            事件对象
	 * @param view
	 *            弹出对象实例（View）
	 * @return
	 */
	static Timer mTimer = new Timer();

	public static boolean onTouchEvent(MotionEvent event, View view,
			View view1, Timer timer, final Handler handler, View header,
			Button btn, ImageView btn_logo, Button btn_go, Button btn_close) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// panTime();
			// 获取相对View的坐标，即以此View左上角为原点
			mTouchStartX = event.getX();
			mTouchStartY = event.getY();
			// 获取相对屏幕的坐标，即以屏幕左上角为原点
			Log.d("wps", "x" + mTouchStartX + "y" + mTouchStartY);
			x = event.getRawX();
			y = event.getRawY();

			viewX = params.x;
			viewY = params.y;

			isMove = false;
			timer.cancel();
			mTimer.cancel();
			btn.setVisibility(View.VISIBLE);
			header.setVisibility(View.VISIBLE);
			btn_logo.setVisibility(View.VISIBLE);
			btn_go.setVisibility(View.INVISIBLE);
			btn_close.setVisibility(View.INVISIBLE);

			timer.cancel();
			mTimer.cancel();
			break;
		case MotionEvent.ACTION_MOVE:
			moveX = event.getRawX();
			moveY = event.getRawY();
			updateViewPosition(view1);
			isMove = true;

			break;

		case MotionEvent.ACTION_UP:
			mTimer.cancel();
			mTimer = new Timer();
			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					handler.sendEmptyMessage(0);
				}
			};
			mTimer.schedule(task, 3 * 1000);
			Utils.save(context, "floating_x", String.valueOf(params.x));
			Utils.save(context, "floating_y", String.valueOf(params.y));
			mTouchStartX = mTouchStartY = 0;
			break;
		}
		return isMove;
	}

	/**
	 * 关闭浮动显示对象
	 */
	public static void close(Context context) {

		if (view_obj != null && view_obj.isShown()) {
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			wm.removeView(view_obj);
		}
	}

	/**
	 * 更新弹出窗口位置
	 */
	private static void updateViewPosition(View view) {
		// 更新浮动窗口位置参数
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE); // 获取当前屏幕管理器对象
		Display display = wm.getDefaultDisplay(); // 获取屏幕信息的描述类
		int screenW = display.getWidth();
		int screenH = display.getHeight();

		params.x = (int) (moveX - x + viewX);
		params.y = (int) (moveY - y + viewY);
		// 不在屏幕范围内的 处理
		if (params.x < 0) {
			params.x = 0;
		}
		if (params.y < 0) {
			params.y = 0;
		}

		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 20;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = context.getResources().getDimensionPixelSize(x);
			Log.d("error", "ok" + sbar);
		} catch (Exception e1) {
			Log.d("error", "filed");
			e1.printStackTrace();
		}

		if (params.y > screenH - view_obj.getHeight() - sbar) {
			params.y = screenH - view_obj.getHeight() - sbar;
		}
		if (params.x > screenW - view_obj.getWidth()) {
			params.x = screenW - view_obj.getWidth();
		}
		Log.d("wpsh", "" + view_obj.getHeight());
		Log.d("wps", "new pos  Y" + params.y);
		wm.updateViewLayout(FloatingFun.floatingViewObj, params);

	}

	// public static void updateViewPosition(View view, int a) {
	// // 更新浮动窗口位置参数
	// params.x = (int) (moveX - x + viewX);
	// params.y = (int) (moveY - y + viewY);
	//
	// floatingViewObj.invalidate();
	// wm.updateViewLayout(floatingViewObj, params);
	// }

	public static void updateViewAlpha(int alpha) {
		params.alpha = alpha;
		wm.updateViewLayout(FloatingFun.floatingViewObj, params);
	}

	public static void updateViewW(int derection) {
		// 更新浮动窗口位置参数
		// params.x = (int) (moveX - x + viewX);
		// params.y = (int) (moveY - y + viewY);
		if (context != null) {
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE); // 获取当前屏幕管理器对象
			Display display = wm.getDefaultDisplay(); // 获取屏幕信息的描述类
			int screenW = display.getWidth();
			int screenH = display.getHeight();
			// 设置悬浮窗口长宽数据
			if (derection == 1) {
				params.width = (int) (display.getWidth() * 0.4);
			} else {
				params.width = (int) (display.getWidth() * 0.4);
			}
			Log.d("zkzk", "" + params.width);
			if (FloatingFun.floatingViewObj != null) {
				wm.updateViewLayout(FloatingFun.floatingViewObj, params);
			}
		}
	}

}
