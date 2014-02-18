package com.kenny.Slidingmenu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.framework.page.AbsFragmentPage;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.Slidingmenu.Fragment.KMenuFragment;
import com.kenny.Slidingmenu.Fragment.LocalPage;
import com.kenny.Slidingmenu.widget.ActionListView;
import com.kenny.file.Event.InitEvent;
import com.kenny.file.tools.SaveData;
import com.kenny.file.util.Const;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.ReportPolicy;
import com.umeng.update.UmengUpdateAgent;

/**
 * This activity is an example of a responsive Android UI. On phones, the
 * SlidingMenu will be enabled only in portrait mode. In landscape mode, it will
 * present itself as a dual pane layout. On tablets, it will will do the same
 * general thing. In portrait mode, it will enable the SlidingMenu, and in
 * landscape mode, it will be a dual pane layout.
 * 
 * @author wmh
 * 
 */
public class MainUIActivity extends SlidingFragmentActivity
{
	private Fragment mContent;
	private KMenuFragment mKMenuFragment;
	private SlidingMenu sm;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.responsive_content_frame);
		// check if the content frame contains the menu frame
		if (findViewById(R.id.menu_frame) == null)
		{
			setBehindContentView(R.layout.menu_frame);
			// show home as up so we can toggle
			sm = getSlidingMenu();
			sm.setShadowWidthRes(R.dimen.shadow_width);
			sm.setShadowDrawable(R.drawable.shadow);
			sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
			sm.setFadeDegree(0.35f);
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			// sm.setSlidingEnabled(true);
			// ActionBar bar=getSupportActionBar();
			// bar.setDisplayHomeAsUpEnabled(true);// by wmh
			// bar.setDisplayUseLogoEnabled(false);
			// bar.setDisplayShowTitleEnabled(false);

			m = ((LinearLayout) LayoutInflater.from(this).inflate(
					R.layout.custom_actionbar_layout, null));
			d = ((ImageView) m.findViewById(R.id.icon));
			d.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if(!sm.isMenuShowing())
					{
						sm.showMenu();
					}
					else
					{
						sm.showContent();
					}
				}
			});
			
			c = ((TextView) m.findViewById(R.id.ab_title));
			// b = ((BreadcrumbView)m.findViewById(2131427420));
			e = ((ActionListView) m.findViewById(R.id.action_list));
			ActionBar.LayoutParams localLayoutParams = new ActionBar.LayoutParams(
					-1, -2);
			getSupportActionBar().setIcon(R.drawable.logo);
			getSupportActionBar().setLogo(R.drawable.logo);
			getSupportActionBar().setCustomView(m, localLayoutParams);
			getSupportActionBar().setDisplayShowHomeEnabled(false);
			getSupportActionBar().setDisplayShowTitleEnabled(false);
			getSupportActionBar().setDisplayUseLogoEnabled(false);
			getSupportActionBar().setDisplayShowCustomEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);

			// 注销掉
			// k();
		} else
		{
			// add a dummy view
			View v = new View(this);
			setBehindContentView(v);
			getSlidingMenu().setSlidingEnabled(false);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}

		// set the Above View Fragment
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null)// 添加默认的fragment
		{
			LocalPage localPage = new LocalPage(SaveData.Read(this,
					Const.strDefaultPath, Const.getSDCard()));
			mContent = localPage;
			localPage.setTitle("SDcard");
		}
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();

		mKMenuFragment = new KMenuFragment();
		// set the Behind View Fragment KMenuFragment
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, mKMenuFragment).commit();
		Init();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		MobclickAgent.onResume(this);
		/**
		 * 此处调用baidu基本统计代码
		 */
		// StatService.onResume(this);
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		MobclickAgent.onPause(this);
		/**
		 * 此处调用baidu基本统计代码
		 */
		// StatService.onPause(this);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
		{
			System.out.println("现在是竖屏");
		}
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			System.out.println("现在是横屏");
		}
		super.onConfigurationChanged(newConfig);
		// Const.SW = getWindow().getWindowManager().getDefaultDisplay()
		// .getWidth();
		// Const.SH = getWindow().getWindowManager().getDefaultDisplay()
		// .getHeight();
		// if (pageManage != null)
		// pageManage.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		if (mContent != null && mContent instanceof AbsFragmentPage)
		{
			return ((AbsFragmentPage) mContent).onCreateOptionsMenu(
					getSupportMenuInflater(), menu);
		} else
		{
			return super.onCreateOptionsMenu(menu);
		}
	}

	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu)
	// {
	// // TODO Auto-generated method stub
	// if (mContent != null && mContent instanceof AbsFragmentPage)
	// {
	// return ((AbsFragmentPage) mContent).onCreateOptionsMenu(
	// getSupportMenuInflater(), menu);
	// } else
	// {
	// return super.onPrepareOptionsMenu(menu);
	// }
	// // return super.onPrepareOptionsMenu(menu);
	// }

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		boolean result = false;
		if (mContent != null && mContent instanceof AbsFragmentPage)
		{
			result = ((AbsFragmentPage) mContent).onOptionsItemSelected(item);
		}
		if (result)
		{
			return super.onOptionsItemSelected(item);
		} else
		{
			return true;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}

	private Handler h = new Handler();

	public void switchContent(final Fragment fragment)
	{
		mContent = fragment;
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.content_frame, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
		h.postDelayed(new Runnable()
		{
			public void run()
			{
				getSlidingMenu().showContent();
			}
		}, 50);
	}
	public void backFragment()
	{
		getSupportFragmentManager().popBackStack();
	}
	public void addFragment(Fragment paramFragment)
	{
		getSupportFragmentManager().beginTransaction()
				.add(R.id.content_frame, paramFragment).commit();
	}

	public void removeFragment(Fragment paramFragment)
	{
		getSupportFragmentManager().beginTransaction().remove(paramFragment)
				.commit();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		// TODO Auto-generated method stub
		boolean result = false;
		mContent=getSupportFragmentManager().findFragmentById(R.id.content_frame);
		if (getSlidingMenu().isMenuShowing())
		{
			result = mKMenuFragment.onKeyDown(keyCode, event);
		}
		else if (mContent != null && mContent instanceof AbsFragmentPage)
		{
			result = ((AbsFragmentPage) mContent).onKeyDown(keyCode, event);
		}
		if (!result)
		{
			return super.onKeyUp(keyCode, event);
		}
		return true;
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event)
	// {
	// boolean result = false;
	// if (getSlidingMenu().isMenuShowing())
	// {
	// result = mKMenuFragment.onKeyDown(keyCode, event);
	// }
	// else
	// if (mContent != null && mContent instanceof AbsFragmentPage)
	// {
	// result = ((AbsFragmentPage) mContent).onKeyDown(keyCode, event);
	// }
	// // if (!result)
	// // {
	// // return super.onKeyDown(keyCode, event);
	// // }
	// return false;
	// }

	// class cf implements View.OnClickListener
	// {
	// cf(MainUIActivity paramMainActivity)
	// {
	// }
	//
	// public void onClick(View paramView)
	// {
	// MainUIActivity.this.onBackPressed();
	// }
	// }

	// public void onBackPressed()
	// {
	// Fragment localFragment = getSupportFragmentManager().findFragmentById(
	// 2131427416);
	// if ((localFragment != null) && ((localFragment instanceof Backable)))
	// ;
	// for (boolean bool = ((Backable) localFragment).back();; bool = false)
	// {
	// if (!bool)
	// d();
	// return;
	// }
	// }

	private LinearLayout m;
	private TextView c;
	private ImageView d;
	private ActionListView e;

	// private BreadcrumbView b;
	private void k()
	{
		m = ((LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.custom_actionbar_layout, null));

		d = ((ImageView) m.findViewById(R.id.icon));
		c = ((TextView) m.findViewById(R.id.ab_title));
		// b = ((BreadcrumbView)m.findViewById(2131427420));
		e = ((ActionListView) m.findViewById(R.id.action_list));
		ActionBar.LayoutParams localLayoutParams = new ActionBar.LayoutParams(
				-1, -2);
		getSupportActionBar().setCustomView(m, localLayoutParams);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
	}

	// public BreadcrumbView getCreadcrumbView()
	// {
	// return this.b;
	// }
	// public void setBreadcrumb(FileBase paramFileBase)
	// {
	//
	// this.b.setVisibility(0);
	// this.c.setVisibility(View.GONE);
	// this.b.changeBreadcrumbPath(paramFileBase, false);
	// }

	public void setMenuContentView(int paramInt)
	{
		// this.a.setContentView(paramInt);
	}

	public void setTitle(int paramInt)
	{
		setTitle(getString(paramInt));
	}

	public void setTitle(String paramString)
	{
		this.c.setText(paramString);
	}

	public void setTitleIcon(int paramInt)
	{
		this.d.setImageDrawable(getResources().getDrawable(paramInt));
	}

	public void setTitleIcon(Drawable paramDrawable)
	{
		this.d.setImageDrawable(paramDrawable);
	}

	public void Init()
	{
		BaiduInit();
		UMInit();
		SysEng.getInstance().addHandlerEvent(new InitEvent(this));
		// Resources resources = getResources();//获得res资源对象
		// Configuration config = resources.getConfiguration();//获得设置对象
		// DisplayMetrics dm = resources
		// .getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
		// config.locale = Locale.SIMPLIFIED_CHINESE; //简体中文
		// resources.updateConfiguration(config, dm);
		// try {
		// zypushInit();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	// public void zypushInit() {
	//
	// // 14.设置是否上传经纬度
	// // 最好调用的地方是：连接推送之前进行调用
	// PushManager.getInstance().isAllowUploadGpsMessage(
	// getApplicationContext(), false, 24);
	//
	// // 7.启动push
	// // 设置是否自动重连
	// //
	// //
	// // 7.1 连接自动重连
	// // 如果出现连接失败的情况，推送服务会进行自动重连，不需要再广播接收器中进行手动重连，减少开发者业务逻辑
	// // import com.joboevan.push.tool.PushManager;
	// try {
	// boolean result = PushManager.getInstance().connect(
	// getApplicationContext(), false); // true表示自动重连，false表示不自动重连，
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// //
	// //
	// // result:表示推送开启的基本条件是否满足(appkey是否填写,当前网络是否正常)
	// //
	// // 7.2 连接(无参数)
	// // try {
	// // boolean result = PushManager.getInstance().connect(
	// // getApplicationContext()); // 默认自动重连
	// // } catch (Exception e) {
	// // e.printStackTrace();
	// // }
	// //
	// // 8.关闭推送服务
	// // PushManager.getInstance().disconnect(getApplicationContext());
	//
	// // 为了能够及时将消息推送到客户端，没有特殊需求请不要调用此接口
	// // 9.暂停接收消息/恢复接收
	// //
	// PushManager.getInstance().isStopMessage(getApplicationContext(),true/false);
	// //
	// // 第二个参数传递true，则表示暂停接收，false表示恢复接收
	// //
	// //
	// // 10.设置推送接收时间
	// // PushManager.getInstance().setPushReceviceTime(this,
	// // strWeek, strStartTime, strEndTime);
	// // 参数分别为：Context对象
	// // 第二个参数为：一周之内接收的天数，如strWeek = “1,2,3,4,5,6”;意思为周一到周6接收推送消息
	// // 第三个参数：一天之内开始接收时间
	// // 第四个参数：一天之内结束接收时间
	// //
	// // 11.设置别名
	// PushManager.getInstance().bindAlias(getApplicationContext(),
	// this.getString(R.string.app_name));
	//
	// // 12.清除别名
	// // PushManager.getInstance().clearAlias(getApplicationContext());
	// // 如果取消成功，广播接收器中会显示“设置别名成功”
	// //
	// // 13.设置标签
	// List<String> tag = new ArrayList<String>();
	// tag.add("1");
	// PushManager.getInstance().setTags(getApplicationContext(), tag);
	// // 限制：每个tag命名长度限制为40，并且不能为空
	// //
	//
	// // 第一个参数：Context对象
	// // 第二个参数：true为允许上传经纬度
	// // 第三个参数：上传间隔时间，单位是小时（int）
	// // 15.上传经纬度
	// // PushManager.getInstance().UploadGpsMessage(context,
	// // "113.631768592180", "34.752936240280", "1");
	// // 第一个参数：Context对象
	// // 第二个参数：经度
	// // 第三个参数：纬度
	// // 第四个参数：通过何种方式获取的经纬度（1：百度，2：google,3:GPS）
	// //
	// // 16.是否获取通知信息内容
	// PushManager.getInstance().isShowNotificationMessage(true);
	// //
	// // 默认是false，开发者获取不到通知的信息内容，如果设置为true，开发者可以接收到通知的信息内容
	// //
	// // 17.获取设备唯一标识
	// PushManager.getInstance().getDeviceId(getApplicationContext());
	// // 18.客户端与服务端的连接状态
	// // 在广播接收器里，可以接收客户端推送的连接状态，如果接收到客户端推送服务已经断开了，在推送服务里面会自动进行重连，减少开发者的维护
	// //
	// //
	// // 19.日志信息
	// //
	// PushManager.getInstance().setLogDisplay(false);
	// //
	// // true :显示日志信息
	// // false:不显示日志信息
	// registerBroadCast();
	// }
	//
	// public static String RECEIVER_MESSAGE_ACTION = "RECEIVER_MESSAGE_ACTION";
	//
	// /** 注册广播接收器 **/
	// private void registerBroadCast() {
	// RegistBroastBean registBean = new RegistBroastBean(this, mReceiver,
	// RECEIVER_MESSAGE_ACTION);
	// Tool.registReceiver(registBean);
	// }

	/**
	 * 卸载广播接收器
	 */
	private void unRegistReceiver()
	{
		if (mReceiver != null)
			unregisterReceiver(mReceiver);
	}

	/**
	 * 广播接收器
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
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

	private void UMInit()
	{
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

	private void BaiduInit()
	{
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

}
