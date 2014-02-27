package com.framework.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ViewFlipper;

import com.baidu.mobstat.StatService;
import com.framework.event.NextPageEvent;
import com.framework.interfaces.IPageManage;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.framework.util.CommLayer;
import com.framework.util.Const;
import com.kenny.KFileManager.t.R;
import com.kenny.file.Event.InitEvent;
import com.kenny.file.page.KMainPage;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.ReportPolicy;
import com.umeng.update.UmengUpdateAgent;

/**
 * Main
 * 
 * @author wangminghui
 * 
 * */
public class Main extends Activity {
	/** Called when the activity is first created. */
	public Context context;
	private IPageManage pageManage;

	public void Init() {
		BaiduInit();
		UMInit();
		SysEng.getInstance().addHandlerEvent(new InitEvent(this));
		// Resources resources = getResources();//获得res资源对象
		// Configuration config = resources.getConfiguration();//获得设置对象
		// DisplayMetrics dm = resources
		// .getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
		// config.locale = Locale.SIMPLIFIED_CHINESE; //简体中文
		// resources.updateConfiguration(config, dm);
//		try {
//			zypushInit();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

//	public void zypushInit() {
//
//		// 14.设置是否上传经纬度
//		// 最好调用的地方是：连接推送之前进行调用
//		PushManager.getInstance().isAllowUploadGpsMessage(
//				getApplicationContext(), false, 24);
//
//		// 7.启动push
//		// 设置是否自动重连
//		//
//		//
//		// 7.1 连接自动重连
//		// 如果出现连接失败的情况，推送服务会进行自动重连，不需要再广播接收器中进行手动重连，减少开发者业务逻辑
//		// import com.joboevan.push.tool.PushManager;
//		try {
//			boolean result = PushManager.getInstance().connect(
//					getApplicationContext(), false); // true表示自动重连，false表示不自动重连，
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		//
//		//
//		// result:表示推送开启的基本条件是否满足(appkey是否填写,当前网络是否正常)
//		//
//		// 7.2 连接(无参数)
//		// try {
//		// boolean result = PushManager.getInstance().connect(
//		// getApplicationContext()); // 默认自动重连
//		// } catch (Exception e) {
//		// e.printStackTrace();
//		// }
//		//
//		// 8.关闭推送服务
//		// PushManager.getInstance().disconnect(getApplicationContext());
//
//		// 为了能够及时将消息推送到客户端，没有特殊需求请不要调用此接口
//		// 9.暂停接收消息/恢复接收
//		// PushManager.getInstance().isStopMessage(getApplicationContext(),true/false);
//		//
//		// 第二个参数传递true，则表示暂停接收，false表示恢复接收
//		//
//		//
//		// 10.设置推送接收时间
//		// PushManager.getInstance().setPushReceviceTime(this,
//		// strWeek, strStartTime, strEndTime);
//		// 参数分别为：Context对象
//		// 第二个参数为：一周之内接收的天数，如strWeek = “1,2,3,4,5,6”;意思为周一到周6接收推送消息
//		// 第三个参数：一天之内开始接收时间
//		// 第四个参数：一天之内结束接收时间
//		//
//		// 11.设置别名
//		PushManager.getInstance().bindAlias(getApplicationContext(),
//				this.getString(R.string.app_name));
//
//		// 12.清除别名
//		// PushManager.getInstance().clearAlias(getApplicationContext());
//		// 如果取消成功，广播接收器中会显示“设置别名成功”
//		//
//		// 13.设置标签
//		List<String> tag = new ArrayList<String>();
//		tag.add("1");
//		PushManager.getInstance().setTags(getApplicationContext(), tag);
//		// 限制：每个tag命名长度限制为40，并且不能为空
//		//
//
//		// 第一个参数：Context对象
//		// 第二个参数：true为允许上传经纬度
//		// 第三个参数：上传间隔时间，单位是小时（int）
//		// 15.上传经纬度
//		// PushManager.getInstance().UploadGpsMessage(context,
//		// "113.631768592180", "34.752936240280", "1");
//		// 第一个参数：Context对象
//		// 第二个参数：经度
//		// 第三个参数：纬度
//		// 第四个参数：通过何种方式获取的经纬度（1：百度，2：google,3:GPS）
//		//
//		// 16.是否获取通知信息内容
//		PushManager.getInstance().isShowNotificationMessage(true);
//		//
//		// 默认是false，开发者获取不到通知的信息内容，如果设置为true，开发者可以接收到通知的信息内容
//		//
//		// 17.获取设备唯一标识
//		PushManager.getInstance().getDeviceId(getApplicationContext());
//		// 18.客户端与服务端的连接状态
//		// 在广播接收器里，可以接收客户端推送的连接状态，如果接收到客户端推送服务已经断开了，在推送服务里面会自动进行重连，减少开发者的维护
//		//
//		//
//		// 19.日志信息
//		//
//		PushManager.getInstance().setLogDisplay(false);
//		//
//		// true :显示日志信息
//		// false:不显示日志信息
//		registerBroadCast();
//	}
//
//	public static String RECEIVER_MESSAGE_ACTION = "RECEIVER_MESSAGE_ACTION";
//
//	/** 注册广播接收器 **/
//	private void registerBroadCast() {
//		RegistBroastBean registBean = new RegistBroastBean(this, mReceiver,
//				RECEIVER_MESSAGE_ACTION);
//		Tool.registReceiver(registBean);
//	}

	/**
	 * 卸载广播接收器
	 */
	private void unRegistReceiver() {
		if (mReceiver != null)
			unregisterReceiver(mReceiver);
	}

	/**
	 * 广播接收器
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// if (RECEIVER_MESSAGE_ACTION.equals(action)) {
			// Bundle bundle = intent.getExtras();
			// String key = bundle.getString("key");
			// if (Consts.MESSAGE_KEY_CONNECT.equals(key)) {
			// // 连接结果
			// int value = bundle.getInt("value");
			// switch (value) {
			// case Consts.RESULT_Y:
			// appendContent("成功与服务器建立连接");
			// break;
			// case Consts.RESULT_N:
			// appendContent("连接失败");
			// if(dialog != null){
			// dialog.dismiss();
			// }
			// break;
			// }
			// } else if (Consts.MESSAGE_KEY_LOGIN.equals(key)) {
			// // 登陆结果
			// int value = bundle.getInt("value");
			// switch (value) {
			// case Consts.RESULT_Y:
			// result = true;
			// setBooleanShared(context, "login_state", true);
			// connectState.setText("连接成功");
			// appendContent("登录成功");
			// packageName.setText(""+getPackageName());
			// connectBt.setVisibility(View.GONE);
			// disconnectBt.setVisibility(View.VISIBLE);
			// if(dialog != null){
			// dialog.dismiss();
			// }
			// break;
			// case Consts.RESULT_N:
			// appendContent("登录失败");
			// connectState.setText("未连接");
			// if(dialog != null){
			// dialog.dismiss();
			// }
			// break;
			// }
			// } else if(Consts.MESSAGE_KEY_SETALIAS.equals(key)){
			// if(dialog != null){
			// dialog.dismiss();
			// }
			// int value = bundle.getInt("value");
			// switch (value) {
			// case Consts.RESULT_Y:
			// appendContent("别名设置成功");
			// break;
			// case Consts.RESULT_N:
			// appendContent("别名设置失败");
			// break;
			// default:
			// break;
			// }
			// }else if (Consts.MESSAGE_KEY_SETTAGS.equals(key)) {
			// int value = bundle.getInt("value");
			// switch (value) {
			// case Consts.RESULT_Y:
			// appendContent("标签设置成功");
			// break;
			// case Consts.RESULT_N:
			// appendContent("标签设置失败");
			// break;
			// default:
			// break;
			// }
			// }else if (Consts.MESSAGE_KEY_CUSTOM.equals(key)) {
			// // 接收自定义推送信息
			// String value = bundle.getString("value");
			// appendContent("推送消息是：  "+value);
			//
			// }else if(Consts.MESSAGE_KEY_NOTIFICATION.equals(key)){
			// //如需在工程中展示通知标题和内容，请设置是否展示通知内容
			// String title = bundle.getString(Consts.NOTIFICATION_TITLE);
			// String content = bundle.getString(Consts.NOTIFICATION_CONTENT);
			// }else if(Consts.MESSAGE_KEY_PUSHSTATECHANGED.equals(key)){
			// //客户端与服务端的状态
			// int value = bundle.getInt("value");
			// switch (value) {
			// case Consts.PUSH_CONNECTSTATE_CONNECTING: //通道连接
			// connectState.setText("连接成功");
			// if(dialog != null){
			// dialog.dismiss();
			// }
			// result = true;
			// setBooleanShared(context, "login_state", true);
			// break;
			//
			// case Consts.PUSH_CONNECTSTATE_DISCONNECT: //通道断开
			// if(dialog != null){
			// dialog.dismiss();
			// }
			// result = false;
			// setBooleanShared(context, "login_state", false);
			// connectState.setText("未连接");
			// disconnectBt.setVisibility(View.GONE);
			// connectBt.setVisibility(View.VISIBLE);
			// break;
			// }
			// }else if(Consts.ACTION_RECEIVER_VERSION.equals(key)){
			// int value = bundle.getInt("value");
			// switch (value) {
			// case Consts.VERSION_LATEST:
			// break;
			// case Consts.VERSION_OLD_PERMIT:
			// break;
			// case Consts.VERSION_OLD_REFUSE:
			// break;
			// default:
			// break;
			// }
			// }else if(Consts.MESSAGE_BACK_FLAG.equals(key)){ //接收通知或自定义消息的ID值
			//
			// }

			// }
		}
	};

	private void UMInit() {
		// 友盟统计数据
		MobclickAgent.setDebugMode(false);
		MobclickAgent.updateOnlineConfig(this);// 在线参数配置
		MobclickAgent.onError(this);
		MobclickAgent.setSessionContinueMillis(10 * 60 * 1000);
		MobclickAgent.setAutoLocation(true);// collect location info,
		MobclickAgent
				.setDefaultReportPolicy(this, ReportPolicy.BATCH_AT_LAUNCH);

		UmengUpdateAgent.update(this);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
	}

	private void BaiduInit() {
		// //设置AppKey与AppChannel
		// StatService.setAppKey("abc1234");
		// StatService.setAppChannel("Baidu Market");
		//
		// //进行错误分析
		// //setOn函数与manifest.xml中的配置BaiduMobAd_EXCEPTION_LOG是等效的，推荐使用配置文件。
		// StatService.setOn(this,StatService.EXCEPTION_LOG);
		//
		// //设置发送延迟
		// StatService.setLogSenderDelayed(10);
		//
		// //设置发送策略
		// //setSendLogStrategy函数与配置文件中的BaiduMobAd_SEND_STRATEGY等是等效的，推荐使用配置文件。
		// StatService.setSendLogStrategy(this,SendStrategyEnum.APP_START,
		// 1,false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		P.v("Main:Oncreate");
		// Thread.setDefaultUncaughtExceptionHandler(new
		// UncaughtExceptionHandler(
		// this));//获取错误
		this.setContentView(R.layout.framework_main);
		Const.SW = getWindow().getWindowManager().getDefaultDisplay()
				.getWidth();
		Const.SH = getWindow().getWindowManager().getDefaultDisplay()
				.getHeight();
		context = this.getApplicationContext();
		ViewFlipper flipper = (ViewFlipper) findViewById(R.id.mainflip);
		pageManage = new KPageManage();
		pageManage.Init(this, flipper);
		CommLayer.setPMG(pageManage);
		Init();

		P.v("Main:KMainPage");
		SysEng.getInstance().addHandlerEvent(
				new NextPageEvent(this, new KMainPage(this), Const.SHOWANIM,
						null));
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			System.out.println("现在是竖屏");
		}
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			System.out.println("现在是横屏");
		}
		super.onConfigurationChanged(newConfig);
		Const.SW = getWindow().getWindowManager().getDefaultDisplay()
				.getWidth();
		Const.SH = getWindow().getWindowManager().getDefaultDisplay()
				.getHeight();
		if (pageManage != null)
			pageManage.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (pageManage != null && pageManage.onKeyDown(keyCode, event)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (pageManage != null && pageManage.onCreateOptionsMenu(menu)) {
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (pageManage != null && pageManage.onPrepareOptionsMenu(menu)) {
			return true;
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (pageManage != null && pageManage.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// ////life cycle////////
	@Override
	protected void onStart() {
		super.onStart();
		if (pageManage != null) {
			pageManage.onStart();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (pageManage != null)
			pageManage.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		/**
		 * 此处调用基本统计代码
		 */
		StatService.onResume(this);
		if (pageManage != null)
			pageManage.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		/**
		 * 此处调用基本统计代码
		 */
		StatService.onPause(this);
		if (pageManage != null)
			pageManage.onPause();

	}

	@Override
	protected void onStop() {
		super.onStop();
		pageManage.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		unRegistReceiver();
		// 8.关闭推送服务
//		PushManager.getInstance().disconnect(getApplicationContext());
		pageManage.onDestroy();
		Log.v("wmh", "Main:onDestroy");
		// 彻底关闭程序
		// android.os.Process.killProcess(android.os.Process.myPid());
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		pageManage.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		pageManage.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		pageManage.onActivityResult(requestCode, resultCode, data);
	}
}