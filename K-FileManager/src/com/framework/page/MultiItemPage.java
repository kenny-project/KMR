package com.framework.page;

import com.framework.event.NextPageEvent;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Event.ExitEvent;
import com.kenny.file.dialog.AboutDialog;
import com.kenny.file.page.SettingPage;
import com.umeng.fb.UMFeedbackService;

import android.app.Activity;
import android.view.MenuItem;

/**
 * 主界面切换
 * 
 * @author WangMinghui
 * 
 */
public abstract class MultiItemPage extends AbsPage // implements
{
   private boolean bCreate = false; // 是否已经初始化
   
   public MultiItemPage(Activity context)
   {
      super(context);
   }
   public boolean isCreate()
   {
      return bCreate;
   }
   @Override
   public void onCreate()
   {
      bCreate = true;
   }
   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      switch (item.getItemId())
      {
      case R.id.muSetting:
         SysEng.getInstance().addHandlerEvent(
	     new NextPageEvent(m_act, new SettingPage(m_act), 1, null));
         break;
      case R.id.muAboutDialog:
         new AboutDialog().showDialog(m_act);
         break;
//      case R.id.muFeedback:
         //UMFeedbackService.openUmengFeedbackSDK(m_act);
       //  break;
      case R.id.muExit:
         SysEng.getInstance().addHandlerEvent(new ExitEvent(m_act, false));
         break;
      }
      return false;
   }
   /**
    * 界面加载时调用
    * 
    * */
   public abstract void onLoad();
   
   /**
    * 界面返回加载时调用
    * 
    * */
   public abstract void onReload();
   
   /**
    * 界面离开时调用
    * 
    * */
   public abstract void onExit();
   /**
    * 屏幕旋转
    */
   // public abstract void onConfigurationChanged(Configuration newConfig);
}