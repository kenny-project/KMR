package com.kenny.file.page;

import android.app.Activity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.framework.event.NextPageEvent;
import com.framework.page.MultiItemPage;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.Slidingmenu.Fragment.SettingPage;
import com.kenny.file.Event.ExitEvent;
import com.kenny.file.dialog.AboutDialog;

/**
 * 主界面切换
 * 
 * @author WangMinghui
 * 
 */
public abstract class ObjectMenuPage extends MultiItemPage 
{
   protected View tools;
   protected KMainPage m_basepage = null;
   private int type; // 1:Local.2net.3app.4task
   
   
   public ObjectMenuPage(Activity context, int type)
   {
      super(context);
      this.type = type;
   }
   
   /**
    * 设置下一个节点页面需要传递的数据
    * 
    * @param obj
    *           传递的数据结构
    * */
   public void setObj(Object obj)
   {
      this.viewobj = obj;
      m_basepage = (KMainPage) viewobj;
   }
   

   
   @Override
   public void onCreate()
   {
      tools = mInflater.inflate(R.layout.main_tools, null);
      // Button btLocal = (Button) tools.findViewById(R.id.tab_local);
      // Button btRemote = (Button) tools.findViewById(R.id.tab_network);
      // Button btapps = (Button) tools.findViewById(R.id.tab_apps);
      // Button btFavorite = (Button) tools
      // .findViewById(R.id.tab_Favorite);
      // // btFavorite.setVisibility(View.GONE);
      // Button bttask = (Button) tools.findViewById(R.id.tab_task);
      // btLocal.setOnClickListener(toolsOnClickListener);
      // btRemote.setOnClickListener(toolsOnClickListener);
      // btFavorite.setOnClickListener(toolsOnClickListener);
      // btapps.setOnClickListener(toolsOnClickListener);
      // bttask.setOnClickListener(toolsOnClickListener);
      // bttask.setVisibility(View.GONE);
      // LinearLayout lyTools = (LinearLayout) findViewById(R.id.lyTools);
      // lyTools.setVisibility(View.GONE);
      // lyTools.addView(tools, new LinearLayout.LayoutParams(
      // LayoutParams.FILL_PARENT,
      // LayoutParams.WRAP_CONTENT));
      // // android:background="@drawable/tab_select"
      // Button btTemp = btLocal;
      // switch (type)
      // {
      // case 1:
      // btTemp = btLocal;
      // // btLocal.setCompoundDrawables(null,
      // // this.getResources().getDrawable(R.drawable.tab_local_selected),
      // // null, null);
      // break;
      // case 2:
      // btTemp = btRemote;
      // break;
      // case 3:
      // btTemp = btFavorite;
      // break;
      // case 4:
      // btTemp = btapps;
      // break;
      // case 5:
      // btTemp = bttask;
      // break;
      // }
      // btTemp.setBackgroundResource(R.drawable.tab_select);
      // btTemp.setTextColor(m_act.getResources().getColor(
      // R.color.tab_TextColor_selected));
   }
   
   // OnClickListener toolsOnClickListener = new OnClickListener()
   // {
   // @Override
   // public void onClick(View v)
   // {
   // if (m_basepage == null) m_basepage = (MultiListPage) viewobj;
   // // TODO
   // // Auto-generated
   // // method stub
   // switch (v.getId())
   // {
   // case R.id.tab_local:
   // m_basepage.SwitchPage(WinMainPage.Local);
   // break;
   // case R.id.tab_network:
   // m_basepage.SwitchPage(WinMainPage.Remote);
   // break;
   // case R.id.tab_Favorite:
   // m_basepage.SwitchPage(WinMainPage.Favorite);
   // break;
   // case R.id.tab_apps:
   // m_basepage.SwitchPage(WinMainPage.apps);
   // break;
   // case R.id.tab_task:
   // m_basepage.SwitchPage(WinMainPage.task);
   // break;
   // }
   // }
   // };
   
   @Override
   public void clear()
   {
      // TODO Auto-generated method stub
      
   }
   
   @Override
   public boolean onTouchEvent(MotionEvent event)
   {
      // TODO Auto-generated method stub
      return false;
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
/*      case R.id.muFeedback:
         UMFeedbackService.openUmengFeedbackSDK(m_act);
         break;*/
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
}