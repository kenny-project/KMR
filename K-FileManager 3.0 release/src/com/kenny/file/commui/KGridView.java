package com.kenny.file.commui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ListView;
import com.framework.log.P;
import com.kenny.file.page.KMainPage;

public class KGridView extends GridView
{

   
   public KGridView(Context context, AttributeSet attrs, View view)
   {
      super(context, attrs);
   }
   
   public KGridView(Context context)
   {
      super(context);
      // TODO Auto-generated constructor stub
   }
   
   public KGridView(Context context, AttributeSet attrs)
   {
      this(context, attrs, 0);
   }
   
   public KGridView(Context context, AttributeSet attrs, int defStyle)
   {
      super(context, attrs, defStyle);
   }
   
   @Override
   public boolean onTouchEvent(MotionEvent event)
   {
      return super.onTouchEvent(event);
//      boolean result=KMainPage.getWorkspace().onTouchEvent(event);;
//     // P.v("Touch", "Action = " + event.getAction()+",x = " + event.getRawX() + ", y = " + event.getRawY()+"result="+result);
//      if(!result)
//      {
//        return super.onTouchEvent(event);
//      }
//      else
//      {
//         event.setAction(MotionEvent.ACTION_CANCEL);
//         return super.onTouchEvent(event);  
//      }
   }
 
}
