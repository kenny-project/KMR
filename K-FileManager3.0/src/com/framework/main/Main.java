package com.framework.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.kenny.KFileManager.R;
import com.kenny.file.page.KMainPage;
import com.umeng.analytics.MobclickAgent;

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
		

		P.v("Main:KMainPage");
		SysEng.getInstance().addHandlerEvent(
				new NextPageEvent(this, new KMainPage(this), Const.SHOWANIM,
						null));
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