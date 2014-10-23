package com.framework.page;

import java.util.Hashtable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.framework.event.BackViewEvent;
import com.framework.event.ObjectLinearLayout;
import com.framework.interfaces.MenuAble;
import com.framework.interfaces.OnDestroyAble;
import com.framework.interfaces.OnPauseAble;
import com.framework.interfaces.OnResumeAble;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.umeng.analytics.MobclickAgent;

/**
 * 界面基类
 * 
 * @author aimery
 * */
public abstract class AbsPage extends ObjectLinearLayout implements MenuAble,
      OnResumeAble, OnPauseAble, OnDestroyAble
{
   // **
   // * 词友、群、会话、设置为一级界面，其余都为二级界面
   // * 一级界面再内存中只会存在一个,并且一级界面issave只能为true；
   // * */
   public static final int FIRST_PAGE = 0;// 一级界面，
   public static final int SECOND_PAGE = 1;// 二级级界面
   protected Activity m_act;
   protected LayoutInflater mInflater = null;
   protected boolean bViewCache = false;// 是否缓存View页
   // public View view;
   protected Object viewobj=null;// 上一个界面节点传入的数据结构体
   protected int pageID = 0;// 页面Id号
   public AbsPage parent;// 父界面指的是从那个界面做的跳转
   private static Hashtable<Integer, View> viewcache = new Hashtable<Integer, View>();
   private int Level = SECOND_PAGE;
   protected boolean issave = true;// 当前界面是否放入缓存
   
   public boolean isIssave()
   {
      return issave;
   }
   
   /**
    * 返回当前要操作的窗体指针
    * 
    * @return
    */
   public AbsPage getThis()
   {
      return this;
   }
   public Activity getActivity()
   {
	  return m_act; 
   }
   public AbsPage(Activity context)
   {
      super(context);
      m_act = context;
      if (mInflater == null) mInflater = LayoutInflater.from(m_act);
   }
//   public AbsPage(Context context)
//   {
//      super(context);
//      m_act = null;
//      if (mInflater == null) mInflater = LayoutInflater.from(m_act);
//   }
   
   public AbsPage(Context context, AttributeSet attrs)
   {
      super(context, attrs);
      if (mInflater == null) mInflater = LayoutInflater.from(m_act);
   }
   
   public void setCache(boolean iscache)
   {
      this.issave = iscache;
      if (this.Level == FIRST_PAGE)
      {
         issave = true;
      }
   }
   
   public boolean getCache()
   {
      return issave;
   }
   
   // public View getView()
   // {
   // return this.view;
   // }
   
   public void setLevel(int level)
   {
      this.Level = level;
      issave = true;
   }
   
   public int getLevel()
   {
      return this.Level;
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
   }
   
   /**
    * 获取上一个节点页面传递的数据
    * 
    * @return Object 上一个节点页面传递的数据
    * */
   public Object getObj()
   {
      return viewobj;
   }
   
   /**
    * 添加带有menu的view
    * 
    * */
   public void addHaveMenuView(View view, LinearLayout.LayoutParams params,
         final int pageId)
   {
      this.removeAllViews();
      this.addView(view);
      
      // updataSessionNum();
   }
   
   public static enum LoadType
   {
      NEED_CREAT_LAYOUT, /** 需要创建layout */
      NOT_CREAT_LAYOUT
      /** 不需要创建layout */
      
   }
   
   /**
    * 界面清理 彻底删除界面时调用，可以做一些垃圾清理、存储逻辑等
    * */
   public abstract void clear();
   
   /**
    * true:处理过 false:未处理 返回
    * */
   public boolean backKey()
   {
      
      SysEng.getInstance().addHandlerEvent(new BackViewEvent());
      return true;
   }
   
   public boolean onKeyDown(int keyCode, KeyEvent event)
   {
      if (keyCode == KeyEvent.KEYCODE_BACK)
      {
         SysEng.getInstance().addHandlerEvent(new BackViewEvent());
         return true;
      }
      else
      {
         return false;
      }
   }
   
   /**
    * touch 事件
    * */
  // public abstract boolean onTouchEvent(MotionEvent event);
   
   public void OnActivityResult(int requestCode, int resultCode, Intent data)
   {
   };
  
   
   /**
    * activity
    */
   /**
    * 创建
    */
   public abstract void onCreate();
   
   
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // TODO Auto-generated method stub
      return false;
   }
   

   /**
    * 加载menu
    * 
    * @param menu
    * @param menuid
    *           menu的xml
    * */
   protected boolean onCreateOptionsMenu(Menu menu, int menuid)
   {
      menu.clear();
      MenuInflater inflater = m_act.getMenuInflater();
      inflater.inflate(menuid, menu);
      return true;
   }
   
   
   public boolean onPrepareOptionsMenu(Menu menu)
   {
      onCreateOptionsMenu(menu);
      MobclickAgent.onEvent(m_act, "Menu","OnClick");
      // menu.clear();
      return false;
   }
   
   
   public boolean onOptionsItemSelected(MenuItem item)
   {
      return false;
   }
   
   /**
    * 菜单menu end
    */
   /**
    * 将指定View加入到当前窗体
    * 
    * @param rid
    */
   protected void setContentView(int rid)
   {
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
	  LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
      this.addView(getView(rid), params);
   }
   
   /**
    * 如果内存吃紧，建议所有界面的view获取方式都用该方法 根据layout的id获取Layou的View
    * 
    * @param resource
    *           layout的id
    * @param root
    *           ViewGroup 直接传null
    * @return View 对应layoutid的View
    * */
   public View getView(int resource)
   {
      if (!bViewCache)
      {
         return mInflater.inflate(resource, null);
      }
      else
      {
         View view = viewcache.get(resource);
         P.v("comm", "resourceid=" + resource + " " + view);
         if (view == null)
         {
	  view = mInflater.inflate(resource, null);
	  viewcache.put(resource, view);
         }
         if (view.getParent() == null)
         {
	  return view;
         }
         else
         {
        	 	return view;
         }
      }
   }
   
   protected void finish()
   {
      SysEng.getInstance().addHandlerEvent(new BackViewEvent());
   }
}
