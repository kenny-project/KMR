package com.kenny.file.page;

import java.util.ArrayList;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.framework.interfaces.OnConfigurationChangedAble;
import com.framework.log.P;
import com.framework.page.AbsPage;
import com.framework.page.MultiItemPage;
import com.kenny.KFileManager.R;
import com.kenny.file.tools.T;
import com.kenny.file.util.Theme;

/**
 * 主界面切换
 * 
 * @author WangMinghui
 * 
 */
public class KMainPage extends AbsPage implements OnConfigurationChangedAble
{
   private LocalFilePage m_localPage = null;
   private NetworkPage m_RemotePage = null;
   private AppsPage m_AppsPage = null;
   private TaskPage m_TaskPage = null;
   private FavoritePage m_FavoritePage = null;
   /**
    * 本地
    */
   public static final int Local = 0;
   public static final int NetWork = 1;
   public static final int Favorite = 2;
   public static final int apps = 3;
   public static final int task = 4;
   // private static Workspace mWorkspace;
   private ArrayList<AbsPage> mListViews;
   private View lvTabs;
   private MultiItemPage m_curPage = null;
   private View btapps, btFavorite, bttask;
   private View rbLocal;
   private View btRemote;
   private ViewPager myViewPager;
   private MyPagerAdapter mAbsPageAdapter = new MyPagerAdapter();
   private View btOldButton = null;
   private int nLastKey = Local;
   public static KMainPage mKMainPage;
   
   // public static Workspace getWorkspace()
   // {
   // return mWorkspace;
   // }
   
   public KMainPage(Activity context)
   {
      super(context);
      mKMainPage = this;
   }
   
   OnClickListener toolsOnClickListener = new OnClickListener()
   {
      
      public void onClick(View v)
      {
         switch (v.getId())
         {
         case R.id.tab_local:
	  // SwitchPage(KMainPage.Local);
	  // mWorkspace.setPosScreen(Local);
	  // MobclickAgent.onEvent(m_act, "KMainPage", "localPage");
	  myViewPager.setCurrentItem(Local);
	  break;
         case R.id.tab_network:
	  // SwitchPage(KMainPage.Remote);
	  // mWorkspace.setPosScreen(Remote);
	  // MobclickAgent.onEvent(m_act, "KMainPage", "networkPage");
	  myViewPager.setCurrentItem(NetWork);
	  break;
         case R.id.tab_Favorite:
	  // SwitchPage(KMainPage.Favorite);
	  // mWorkspace.setPosScreen(Favorite);
	  // MobclickAgent.onEvent(m_act, "KMainPage", "favoritePage");
	  myViewPager.setCurrentItem(Favorite);
	  break;
         case R.id.tab_apps:
	  // SwitchPage(KMainPage.apps);
	  // mWorkspace.setPosScreen(apps);
	  // MobclickAgent.onEvent(m_act, "KMainPage", "appPage");
	  myViewPager.setCurrentItem(apps);
	  break;
         case R.id.tab_task:
	  // SwitchPage(KMainPage.task);
	  // mWorkspace.setPosScreen(task);
	  // MobclickAgent.onEvent(m_act, "KMainPage", "taskPage");
	  myViewPager.setCurrentItem(task);
	  break;
         }
      }
   };
   
   public int ChangePage(int key, Object obj)
   {
      SwitchPage(key, obj);
      myViewPager.setCurrentItem(key);
      return 1;
   }
   
   public int SwitchPage(int key)
   {
      return SwitchPage(key, null);
   }
   
   public int SwitchPage(final int key, Object obj)
   {
      // P.v("SwitchPage(final int key="+key);
      if (m_curPage != null)
      {
         nLastKey = key;
         m_curPage.onPause();
         m_curPage.onExit();
      }
      if (btOldButton != null)
      {
         btOldButton.setBackgroundResource(R.drawable.tab_normal);
         // btOldButton.setTextColor(m_act.getResources().getColor(
         // R.color.tab_TextColor_normal));
      }
      MultiItemPage tempPage = m_localPage;
      View btTemp = rbLocal;
      switch (key)
      {
      case Local:
         btTemp = rbLocal;
         // MobclickAgent.onEvent(m_act,"KMainPage","localPage");
         tempPage = m_localPage;
         break;
      case NetWork:
         btTemp = btRemote;
         // MobclickAgent.onEvent(m_act,"KMainPage","networkPage");
         tempPage = m_RemotePage;
         break;
      case Favorite:
         btTemp = btFavorite;
         // MobclickAgent.onEvent(m_act,"KMainPage","favoritePage");
         tempPage = m_FavoritePage;
         break;
      case apps:
         btTemp = btapps;
         // MobclickAgent.onEvent(m_act,"KMainPage","appPage");
         tempPage = m_AppsPage;
         break;
      case task:
         btTemp = bttask;
         tempPage = m_TaskPage;
         // MobclickAgent.onEvent(m_act,"KMainPage","taskPage");
         break;
      }
      btTemp.setBackgroundResource(R.drawable.tab_select);
      // btTemp.setTextColor(m_act.getResources().getColor(
      // R.color.tab_TextColor_selected));
      btOldButton = btTemp;
      if (obj != null)
      {
         tempPage.setObj(obj);
      }
      if (tempPage != null)
      {
         if (!tempPage.isCreate())
         {
	  tempPage.onCreate();
	  tempPage.onLoad();
         }
         m_curPage = tempPage;
         tempPage.onResume();
         tempPage.onReload();
         // temp.onCreateOptionsMenu(menu);
         this.postInvalidate();
         return 1;
      }
      else
      {
         P.v(key + ":未找到相应的窗体");
      }
      return 0;
   }
   
   public void onCreate()
   {
      setContentView(R.layout.kmain);
      lvTabs = (View) findViewById(R.id.lvTabs);
      rbLocal = findViewById(R.id.tab_local);
      btRemote = findViewById(R.id.tab_network);
      btapps = findViewById(R.id.tab_apps);
      btFavorite = findViewById(R.id.tab_Favorite);
      // btFavorite.setVisibility(View.GONE);
      bttask = findViewById(R.id.tab_task);
      rbLocal.setOnClickListener(toolsOnClickListener);
      btRemote.setOnClickListener(toolsOnClickListener);
      btFavorite.setOnClickListener(toolsOnClickListener);
      btapps.setOnClickListener(toolsOnClickListener);
      bttask.setOnClickListener(toolsOnClickListener);
      myViewPager = (ViewPager) findViewById(R.id.viewpagerLayout);
      
      m_localPage = new LocalFilePage(m_act);
      m_RemotePage = new NetworkPage(m_act);
      m_FavoritePage = new FavoritePage(m_act);
      m_AppsPage = new AppsPage(m_act);
      m_TaskPage = new TaskPage(m_act);
      
      // m_localPage.setObj(this);
      // m_RemotePage.setObj(this);
      // m_FavoritePage.setObj(this);
      // m_AppsPage.setObj(this);
      // m_TaskPage.setObj(this);
      // m_localPage.onCreate();
      // m_localPage.onLoad();
      // m_RemotePage.onCreate();
      // m_RemotePage.onLoad();
      // m_AppsPage.onCreate();
      // m_AppsPage.onLoad();
      // m_FavoritePage.onCreate();
      // m_FavoritePage.onLoad();
      // m_TaskPage.onCreate();
      // m_TaskPage.onLoad();
      
      // TextView tview = new TextView(m_act);
      // tview.setHeight(150);
      // tview.setWidth(-1);
      // tview.setVisibility(View.VISIBLE);
      // tview.setBackgroundColor(color.green);
      
      myViewPager.setAdapter(mAbsPageAdapter);
      mListViews = new ArrayList<AbsPage>();
      
      mListViews.add(m_localPage);
      mListViews.add(m_RemotePage);
      mListViews.add(m_FavoritePage);
      mListViews.add(m_AppsPage);
      mListViews.add(m_TaskPage);
      SwitchPage(Local);
      
      myViewPager.setOnPageChangeListener(new OnPageChangeListener()
      {
         
         public void onPageSelected(int arg0)
         {
	  // P.v("king", "onPageSelected - " + arg0);
	  // activity从1到2滑动，2被加载后掉用此方法
	  // View v = mListViews.get(arg0);
	  SwitchPage(arg0);
         }
         
         public void onPageScrolled(int arg0, float arg1, int arg2)
         {
	  // P.v("king", "onPageScrolled:arg0=" + arg0);//+",arg1=" +
	  // arg1+",arg2=" + arg2);
	  // 从1到2滑动，在1滑动前调用
         }
         
         public void onPageScrollStateChanged(int arg0)
         {
	  // P.v("king", "onPageScrollStateChanged - " + arg0);
	  // 状态有三个0空闲，1是增在滑行中，2目标加载完毕
	  /**
	   * Indicates that the pager is in an idle, settled state. The
	   * current page is fully in view and no animation is in progress.
	   */
	  // public static final int SCROLL_STATE_IDLE = 0;
	  /**
	   * Indicates that the pager is currently being dragged by the user.
	   */
	  // public static final int SCROLL_STATE_DRAGGING = 1;
	  /**
	   * Indicates that the pager is in the process of settling to a final
	   * position.
	   */
	  // public static final int SCROLL_STATE_SETTLING = 2;
         }
      });
      myViewPager.setCurrentItem(Local);
      T.SetScreenOrientation(m_act, Theme.getScreenOrientation());
   }
   
   public boolean onKeyDown(int keyCode, KeyEvent msg)
   {
      if (m_curPage != null) { return m_curPage.onKeyDown(keyCode, msg); }
      return false;
   }
   
   public boolean onCreateOptionsMenu(Menu menu)
   {
      if (m_curPage != null)
      {
         m_curPage.onCreateOptionsMenu(menu);
      }
      return true;
   }
   
   public boolean onPrepareOptionsMenu(Menu menu)
   {
      // // return super.onCreateOptionsMenu(menu, R.menu.favoritepagemenu);
      // onCreateOptionsMenu(menu);
      super.onPrepareOptionsMenu(menu);
      if (m_curPage != null)
      {
         m_curPage.onPrepareOptionsMenu(menu);
      }
      return true;
   }
   
   public boolean onOptionsItemSelected(MenuItem item)
   {
      if (m_curPage != null)
      {
         m_curPage.onOptionsItemSelected(item);
      }
      return false;
   }
   
   public void onResume()
   {
      if (Theme.getTaskVisible())
      {
         if (!mListViews.contains(m_TaskPage))
         {
	  mListViews.add(m_TaskPage);
	  bttask.setVisibility(View.VISIBLE);
	  
         }
      }
      else
      {
         if (mListViews.contains(m_TaskPage))
         {
	  if (nLastKey != task)
	  {
	     myViewPager.setCurrentItem(Local);
	  }
	  mListViews.remove(m_TaskPage);
	  bttask.setVisibility(View.GONE);
         }
      }
      if (Theme.getTabsVisible())
      {
         lvTabs.setVisibility(View.VISIBLE);
      }
      else
      {
         lvTabs.setVisibility(View.GONE);
      }
      if (m_curPage != null)
      {
         m_curPage.onResume();
      }
   }
   
   public void RCreate(Configuration newConfig)
   {
      if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
      {
         setContentView(R.layout.kmain);
      }
      
      if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
      {
         setContentView(R.layout.kmain_land);
      }
      
      rbLocal = findViewById(R.id.tab_local);
      btRemote = (Button) findViewById(R.id.tab_network);
      btapps = (Button) findViewById(R.id.tab_apps);
      btFavorite = (Button) findViewById(R.id.tab_Favorite);
      // btFavorite.setVisibility(View.GONE);
      bttask = (Button) findViewById(R.id.tab_task);
      rbLocal.setOnClickListener(toolsOnClickListener);
      btRemote.setOnClickListener(toolsOnClickListener);
      btFavorite.setOnClickListener(toolsOnClickListener);
      btapps.setOnClickListener(toolsOnClickListener);
      bttask.setOnClickListener(toolsOnClickListener);
      myViewPager = (ViewPager) findViewById(R.id.viewpagerLayout);
      
      myViewPager.setAdapter(mAbsPageAdapter);
      
      myViewPager.setOnPageChangeListener(new OnPageChangeListener()
      {
         
         public void onPageSelected(int arg0)
         {
	  // P.v("king", "onPageSelected - " + arg0);
	  // activity从1到2滑动，2被加载后掉用此方法
	  // View v = mListViews.get(arg0);
	  SwitchPage(arg0);
         }
         
         public void onPageScrolled(int arg0, float arg1, int arg2)
         {
	  // P.v("king", "onPageScrolled:arg0=" + arg0);//+",arg1=" +
	  // arg1+",arg2=" + arg2);
	  // 从1到2滑动，在1滑动前调用
         }
         
         public void onPageScrollStateChanged(int arg0)
         {
	  // P.v("king", "onPageScrollStateChanged - " + arg0);
	  // 状态有三个0空闲，1是增在滑行中，2目标加载完毕
	  /**
	   * Indicates that the pager is in an idle, settled state. The
	   * current page is fully in view and no animation is in progress.
	   */
	  // public static final int SCROLL_STATE_IDLE = 0;
	  /**
	   * Indicates that the pager is currently being dragged by the user.
	   */
	  // public static final int SCROLL_STATE_DRAGGING = 1;
	  /**
	   * Indicates that the pager is in the process of settling to a final
	   * position.
	   */
	  // public static final int SCROLL_STATE_SETTLING = 2;
         }
      });
      myViewPager.setCurrentItem(Local);
   }
   
   public void onConfigurationChanged(Configuration newConfig)
   {
      // P.v("KMain:onConfigurationChanged");
      // RCreate(newConfig);
   }
   
   public void onPause()
   {
      if (m_curPage != null)
      {
         m_curPage.onPause();
      }
   }
   
   public void clear()
   {
      // TODO Auto-generated method stub
      if (m_curPage != null)
      {
         m_curPage.clear();
      }
   }
   
   public boolean onTouchEvent(MotionEvent event)
   {
      // TODO Auto-generated method stub
      return false;
   }
   
   public void onDestroy()
   {
      // TODO Auto-generated method stub
      try
      {
         if (m_AppsPage != null)
         {
	  m_AppsPage.onDestroy();
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      // for (AbsPage view: mListViews)
      // {
      // view.onDestroy();
      // }
   }
   
   private class MyPagerAdapter extends PagerAdapter
   {
      
      public void destroyItem(View arg0, int arg1, Object arg2)
      {
         // P.v("k", "destroyItem");
         if (mListViews.size() > arg1)
         {
	  ((ViewPager) arg0).removeView(mListViews.get(arg1));
         }
      }
      
      public void finishUpdate(View arg0)
      {
         // P.v("k", "finishUpdate");
      }
      
      public int getCount()
      {
         // P.v("k", "getCount");
         return mListViews.size();
      }
      
      public Object instantiateItem(View arg0, int arg1)
      {
         // P.v("k", "instantiateItem");
         ((ViewPager) arg0).addView(mListViews.get(arg1), 0);
         return mListViews.get(arg1);
      }
      
      public boolean isViewFromObject(View arg0, Object arg1)
      {
         // P.v("k", "isViewFromObject");
         return arg0 == (arg1);
      }
      
      public void restoreState(Parcelable arg0, ClassLoader arg1)
      {
         // P.v("k", "restoreState");
      }
      
      public Parcelable saveState()
      {
         // P.v("k", "saveState");
         return null;
      }
      
      public void startUpdate(View arg0)
      {
         // P.v("k", "startUpdate");
      }
      
   }
}