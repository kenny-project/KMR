package com.kenny.file.commui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.framework.log.P;
import com.kenny.file.page.KMainPage;

public class Workspace extends ViewGroup
{
   private static final int FLING_MIN_DISTANCE = 120;
   private static final int SPACE_MIN_DISTANCE = 30;
   private Scroller scroller = null;
   private float mActionDownX = 0;
   private float mLastMotionX = 0;
   private float mActionDownY = 0;
   //private float mLastMotionY = 0;
   private int whichScreen = 1;
   private static int MAX_SCREEN_COUNT = 1;
   
   public Workspace(Context context)
   {
      super(context);
   }
   
   public Workspace(Context context, AttributeSet attrs)
   {
      this(context, attrs, 0);
   }
   
   public Workspace(Context context, AttributeSet attrs, int defStyle)
   {
      // TODO Auto-generated constructor stub
      super(context, attrs, defStyle);
      scroller = new Scroller(getContext());
      this.setClickable(true);
   }
   
   @Override
   protected void onLayout(boolean changed, int l, int t, int r, int b)
   {
      // TODO Auto-generated method stub
      int childLeft = 0;
      
      final int count = getChildCount();
      //P.e("chenjg", "onLayout bottom is " + b);
      for (int i = 0; i < count; i++)
      {
         final View child = getChildAt(i);
         if (child.getVisibility() != View.GONE)
         {
	  final int childWidth = child.getMeasuredWidth();
	  child.layout(childLeft, 0, childLeft + r, b);
	  childLeft += childWidth;
         }
      }
   }
   
   @Override
   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
   {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
      final int count = getChildCount();
      for (int i = 0; i < count; i++)
      {
         getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
      }
   }
   
   @Override
   public void computeScroll()
   {
      super.computeScroll();
      if (scroller.computeScrollOffset())
      {
         scrollTo(scroller.getCurrX(), 0);
         postInvalidate();
      }
   }
   private static final int SPACEX=100;
   private static final int SPACEY=20;
   @Override
   public boolean onTouchEvent(MotionEvent event)
   {
      // TODO Auto-generated method stub
      final float x = event.getRawX();
      final float y = event.getRawY();
      
      switch (event.getAction())
      {
      case MotionEvent.ACTION_DOWN:
         if (scroller.isFinished())
         {
	  scroller.abortAnimation();
         }
         mLastMotionX = x;
         mActionDownX = x;
         //mLastMotionY = y;
         mActionDownY = y;
         return false;
      case MotionEvent.ACTION_MOVE:
         final int scrollX = getScrollX();
         final int deltaX = (int) (mLastMotionX - x);
         int spaceX = (int) (mActionDownX - x);
         int spaceY = (int) (mActionDownY - y);
         mLastMotionX = x;
         //mLastMotionY = y;
         if ((spaceX > SPACEX || spaceX < -SPACEX) && (spaceY < SPACEY || spaceY > -SPACEY))
         {
	  if (deltaX < 0)
	  {
	     if (scrollX > 0)
	     {
	        scrollBy(Math.max(-scrollX, deltaX), 0);
	     }
	  }
	  else if (deltaX > 0)
	  {
	     final int availableToScroll = getChildAt(getChildCount() - 1)
		 .getRight() - scrollX - getWidth();
	     if (availableToScroll > 0)
	     {
	        scrollBy(Math.min(availableToScroll, deltaX), 0);
	     }
	  }
	  return true;
         }
         return false;
      case MotionEvent.ACTION_UP:
         spaceX = (int) (mActionDownX - x);
         spaceY = (int) (mActionDownY - y);
         if ((spaceX > SPACEX || spaceX < -SPACEX) && (spaceY < SPACEY || spaceY > -SPACEY))
         {
	  // P.v("chenjg", "x is " + event.getRawX());
	  // P.v("chenjg", "getScrollX() is " + getScrollX());
	  if (Math.abs(mActionDownX - event.getRawX()) > (getWidth() / 4))
	  {
	     if (event.getX() < mActionDownX)
	     {
	        if (whichScreen < MAX_SCREEN_COUNT)
	        {
		 whichScreen++;
	        }
	     }
	     else
	     {
	        if (whichScreen > 1)
	        {
		 whichScreen--;
	        }
	     }
	     if (page != null)
	     {
	        page.SwitchPage(whichScreen);
	     }
	  }
	  swapToScreen();
	  return true;
         }
         return false;
      }
      return false;
   }
   
   private KMainPage page;
   
   public void setKMainPage(KMainPage page)
   {
      this.page = page;
   }
   
   //private int nWhichScreen = -1;
   
   // 切换指定屏幕.
   private void swapToScreen()
   {
     // if (whichScreen != nWhichScreen)
      {
         //nWhichScreen=whichScreen;
         final int newX = whichScreen * getWidth();
         final int delta = newX - getScrollX();
         scroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta));
         invalidate();
      }
   }
   
   /**
    * 
    * 
    * @param width
    */
   public void setDefaultScreen(int width)
   {
      whichScreen = 1;
     // nWhichScreen=whichScreen;
      scroller.startScroll(0, 0, whichScreen * width, 0, 0);
      MAX_SCREEN_COUNT = this.getChildCount() - 2;
      invalidate();
   }
   
   /**
    * 
    * 
    * @param width
    */
   public void RefreshScreen()
   {
      scroller.startScroll(0, 0, whichScreen * getWidth(), 0, 0);
      invalidate();
      P.e("chenjg", "whichScreen" + whichScreen);
   }
   /**
    * 
    * 
    * @param width
    */
   public void setPosScreen(int whichScreen)
   {
      this.whichScreen = whichScreen;
      //nWhichScreen=whichScreen;
      scroller.startScroll(0, 0, whichScreen * getWidth(), 0, 0);
      MAX_SCREEN_COUNT = this.getChildCount() - 2;
      invalidate();
      P.e("chenjg", "whichScreen" + whichScreen);
   }
}
