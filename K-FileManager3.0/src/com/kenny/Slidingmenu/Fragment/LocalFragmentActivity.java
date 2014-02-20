package com.kenny.Slidingmenu.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.actionbarsherlock.app.ActionBar;
import com.framework.page.AbsFragmentPage;
import com.kenny.KFileManager.R;
import com.kenny.file.manager.FileManager;
import com.kenny.file.tools.SaveData;
import com.kenny.file.util.Const;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

public class LocalFragmentActivity extends SlidingFragmentActivity 

{
	public static void actionSettingPage(Activity m_act,String path)
	{
		Intent intent = new Intent(m_act, LocalFragmentActivity.class);
		m_act.startActivity(intent);
		mPath=path;
	}
	private static String mPath;
	private SlidingMenu sm;
	private Fragment mContent;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.responsive_content_frame);
		if (findViewById(R.id.menu_frame) == null)
		{
			setBehindContentView(R.layout.menu_frame);
			// show home as up so we can toggle
			sm = getSlidingMenu();
			sm.setShadowWidthRes(R.dimen.shadow_width);
			sm.setShadowDrawable(R.drawable.shadow);
			sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
			sm.setFadeDegree(0.35f);
			sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			// sm.setSlidingEnabled(true);
		}
		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);// by wmh
		bar.setDisplayUseLogoEnabled(false);
		bar.setDisplayShowTitleEnabled(true);
		bar.setDisplayShowHomeEnabled(false);
		// 其中setHomeButtonEnabled和setDisplayShowHomeEnabled共同起作用
		// bar.setHomeButtonEnabled(true);
		// bar.setDisplayShowHomeEnabled(true);
		bar.setTitle("R.string.setting_Title");
		setTitle(R.string.setting_Title);
		
		// set the Above View Fragment
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null)// 添加默认的fragment
		{
			LocalPage localPage = new LocalPage(SaveData.Read(this,
					Const.strDefaultPath, mPath));
			localPage.setLocalManage(new FileManager());
			mContent = localPage;
			localPage.setTitle("SDcard");
		}
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();
	}
	
	@Override
	public void setTitle(CharSequence title)
	{
		// TODO Auto-generated method stub
		getSupportActionBar().setTitle(title);
		super.setTitle(title);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		// TODO Auto-generated method stub
		boolean result = false;
		if (mContent != null && mContent instanceof AbsFragmentPage)
		{
			result = ((AbsFragmentPage) mContent).onKeyDown(keyCode, event);
		}
		if (!result)
		{
			return super.onKeyUp(keyCode, event);
		}
		return true;
	}
}
