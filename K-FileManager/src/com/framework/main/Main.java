package com.framework.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ViewFlipper;

import com.framework.event.NextPageEvent;
import com.framework.interfaces.IPageManage;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.framework.util.CommLayer;
import com.framework.util.Const;
import com.kenny.KFileManager.R;
import com.kenny.file.Event.InitEvent;
import com.kenny.file.page.KMainPage;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.ReportPolicy;
import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;
import com.umeng.update.UmengUpdateAgent;

/**
 * Main
 * 
 * @author aimery
 * 
 * */
public class Main extends Activity
{
   /** Called when the activity is first created. */
   public Context context;
   private IPageManage pageManage;
   public void Init()
   {
      //Resources resources = getResources();//获得res资源对象
      //Configuration config = resources.getConfiguration();//获得设置对象
      //DisplayMetrics dm = resources .getDisplayMetrics();//获得屏幕参数：主要是分辨率，像素等。
      //config.locale = Locale.SIMPLIFIED_CHINESE; //简体中文
      //resources.updateConfiguration(config, dm);
   }
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      P.v("Main:Oncreate");
//       Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(
//       this));//获取错误
      this.setContentView(R.layout.framework_main);
      Const.SW = getWindow().getWindowManager().getDefaultDisplay().getWidth();
      Const.SH = getWindow().getWindowManager().getDefaultDisplay().getHeight();
      context = this.getApplicationContext();
      ViewFlipper flipper = (ViewFlipper) findViewById(R.id.mainflip);
      pageManage = new KPageManage();
      pageManage.Init(this, flipper);
      CommLayer.setPMG(pageManage);
      Init();
      SysEng.getInstance().addHandlerEvent(new InitEvent(this));
      SysEng.getInstance().addHandlerEvent(
	  new NextPageEvent(this, new KMainPage(this), Const.SHOWANIM, null));
      // 友盟统计数据
      UmengUpdateAgent.update(this);
      MobclickAgent.updateOnlineConfig(this);//在线参数配置
      MobclickAgent.onError(this);
      MobclickAgent.setSessionContinueMillis(10*60*1000);
      MobclickAgent.setAutoLocation(true);// collect location info,
      MobclickAgent.setDebugMode(false); // set debug mode ,will print
      MobclickAgent.setDefaultReportPolicy(this, ReportPolicy.BATCH_AT_LAUNCH);
      UMFeedbackService.enableNewReplyNotification(this, NotificationType.AlertDialog);
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
      Const.SW = getWindow().getWindowManager().getDefaultDisplay().getWidth();
      Const.SH = getWindow().getWindowManager().getDefaultDisplay().getHeight();
      if (pageManage != null) pageManage.onConfigurationChanged(newConfig);
   }
   
   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event)
   {
      if (pageManage != null && pageManage.onKeyDown(keyCode, event)) { return true; }
      return super.onKeyDown(keyCode, event);
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      if (pageManage != null && pageManage.onCreateOptionsMenu(menu)) { return true; }
      return super.onCreateOptionsMenu(menu);
   }
   
   @Override
   public boolean onPrepareOptionsMenu(Menu menu)
   {
      if (pageManage != null && pageManage.onPrepareOptionsMenu(menu)) { return true; }
      return super.onPrepareOptionsMenu(menu);
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      // TODO Auto-generated method stub
      if (pageManage != null && pageManage.onOptionsItemSelected(item)) { return true; }
      return super.onOptionsItemSelected(item);
   }
   
   // ////life cycle////////
   @Override
   protected void onStart()
   {
      super.onStart();
      if (pageManage != null)
      {
         pageManage.onStart();
      }
   }
   
   @Override
   protected void onRestart()
   {
      super.onRestart();
      if (pageManage != null) pageManage.onRestart();
   }
   
   @Override
   protected void onResume()
   {
      super.onResume();
      MobclickAgent.onResume(this);
      if (pageManage != null) pageManage.onResume();
   }
   
   @Override
   protected void onPause()
   {
      super.onPause();
      MobclickAgent.onPause(this);
      if (pageManage != null) pageManage.onPause();
      
   }
   
   @Override
   protected void onStop()
   {
      super.onStop();
      pageManage.onStop();
   }
   
   @Override
   protected void onDestroy()
   {
      super.onDestroy();
      pageManage.onDestroy();
      // 彻底关闭程序
      // android.os.Process.killProcess(android.os.Process.myPid());
   }
   
   @Override
   protected void onRestoreInstanceState(Bundle savedInstanceState)
   {
      // TODO Auto-generated method stub
      super.onRestoreInstanceState(savedInstanceState);
      pageManage.onRestoreInstanceState(savedInstanceState);
   }
   
   @Override
   protected void onSaveInstanceState(Bundle outState)
   {
      // TODO Auto-generated method stub
      super.onSaveInstanceState(outState);
      pageManage.onSaveInstanceState(outState);
   }
   
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data)
   {
      // TODO Auto-generated method stub
      super.onActivityResult(requestCode, resultCode, data);
      pageManage.onActivityResult(requestCode, resultCode, data);
   }
}