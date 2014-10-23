package com.framework.event;

import android.content.Context;
import com.framework.log.P;
import com.framework.page.AbsPage;
import com.framework.util.CommLayer;

/**
 * 页面跳转事件
 * 
 * @author aimery
 * */
public class NextPageEvent extends AbsEvent
{
   private int anim;
   private Object obj;
   private AbsPage nextpage = null;
   
   public NextPageEvent(Context act, AbsPage nextpage, int anim, Object obj)
   {
      this.obj = obj;
      this.anim = anim;
      this.nextpage = nextpage;
   }
   
   @Override
   public void ok()
   {
      if (nextpage != null)
      {
         nextpage.setObj(obj);
         P.v(anim + " create page suc =" + nextpage.getClass().getName());
         CommLayer.getPMG().NextView(nextpage, anim);
      }
   }
}
