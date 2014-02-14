package com.framework.page;

import android.app.Activity;
import android.view.MenuItem;

import com.framework.event.NextPageEvent;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Activity.SettingPage;
import com.kenny.file.Event.ExitEvent;
import com.kenny.file.dialog.AboutDialog;
import com.kenny.file.tools.ApkTools;

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
		if(!ApkTools.isPackageName(m_act))
		{
			System.exit(0);
		}
   }
   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {
      switch (item.getItemId())
      {
      case R.id.muSetting:
         SettingPage.actionSettingPage();
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