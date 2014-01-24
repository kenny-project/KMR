package com.kenny.comui;

import com.kenny.util.Utils;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;
public class MyLayout extends FrameLayout {

	private LinearLayout mainView;
	private LinearLayout leftView;
	private LinearLayout rightView;
	private Scroller mScroller;
	private int flingDirect = 0;
	public static final int MOVE_LEFT = 1;
	public static final int MOVE_RIGHT = 2;
//	private int currOffset;
	public int mainState;
	public static final int STATE_MIDDLE=0;
	private static int WIDTH = 0;
	
	
	public MyLayout(Context context) {
		super(context);
		WIDTH = Utils.dip2px(context, 250);
		init(context);
	}

	private void init(Context context) {
		mainView = new LinearLayout(context);
		leftView = new LinearLayout(context);
		rightView = new LinearLayout(context);

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(WIDTH,
				LayoutParams.FILL_PARENT);
		this.addView(leftView, params);

		FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(WIDTH,
				LayoutParams.FILL_PARENT);
		params2.gravity = Gravity.RIGHT;
		this.addView(rightView, params2);

		this.addView(mainView, new FrameLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mScroller = new Scroller(context);
		mainState = 0;
	}

//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		switch (ev.getAction()) {
//		case MotionEvent.ACTION_DOWN: {
//			startX = (int) ev.getRawX();
//			flingDirect = 0;
//			break;
//		}
//		case MotionEvent.ACTION_MOVE: {
//			final int dx = (int) (ev.getRawX() - startX);
//			if (Math.abs(dx) > 10) {
//				return true;
//			}
//			break;
//		}
//
//		}
//		return super.onInterceptTouchEvent(ev);
//	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_MOVE: {
//			int dx = (int) (event.getRawX() - startX);
//
//			
//			if (dx > MOVE_DISTANCE) {
//				flingDirect = MOVE_LEFT;
//			} 
//			else if (dx < -MOVE_DISTANCE) {
//				flingDirect = MOVE_RIGHT;
//			} 
//			else {
//				flingDirect = 0;
//			}
//			if (currOffset - dx <- WIDTH) {
//				mainView.scrollTo(-WIDTH, 0);
//			} 
////			else if (currOffset - dx > 80) {
////				mainView.scrollTo(80, 0);
////			}
//			else if (currOffset - dx < 0){
//				mainView.scrollTo(currOffset - dx, 0);
//			}
//
//			break;
//		}
//		case MotionEvent.ACTION_UP: {
//			
//			
//			judgeDirection();
//			
//			
//			
//			invalidate();
//			break;
//		}
//		}
//		return true;
//	}
	
	public void judgeDirection(){
		
		switch(mainState){
		case MOVE_LEFT:{
			if(flingDirect == MOVE_RIGHT){
				mScroller.startScroll(mainView.getScrollX(), 0, -mainView.getScrollX(), 0);
				mainState= STATE_MIDDLE;
			}
			else {
				mScroller.startScroll(mainView.getScrollX(), 0,
						-WIDTH-mainView.getScrollX(), 0);
			}
			break;
		}
		case MOVE_RIGHT:{
			if(flingDirect == MOVE_LEFT){
				mScroller.startScroll(mainView.getScrollX(), 0, -mainView.getScrollX(), 0);
				mainState= STATE_MIDDLE;
			}
			else {
				mScroller.startScroll(mainView.getScrollX(), 0,
						80-mainView.getScrollX(), 0);
			}
			break;
		}
		case STATE_MIDDLE:{
			if (flingDirect == MOVE_LEFT) {
				mScroller.startScroll(mainView.getScrollX(), 0, -WIDTH-mainView.getScrollX(), 0);
				mainState= MOVE_LEFT;
			} 
			else if (flingDirect == MOVE_RIGHT) {
				mScroller.startScroll(mainView.getScrollX(), 0, WIDTH-mainView.getScrollX(), 0);
				mainState = MOVE_RIGHT;
			} 
//			else {
//				mScroller.startScroll(mainView.getScrollX(), 0,
//						-mainView.getScrollX(), 0);
//				mainState=STATE_MIDDLE;
//			}
			break;
		}
		}
		
		
	}
	
	
	public void setLeftMenu(View view) {
		if (view != null)
			leftView.addView(view, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	}

	public void setMainView(View view) {
		if (view != null) {
			mainView.addView(view, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		}
	}

	public void setRightMenu(View view) {
		if (view != null) {
			rightView.addView(view, new LinearLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		}
	}
	
	
//	public void moveToLeft(){
//		if(mainState==STATE_MIDDLE){
//			mScroller.startScroll(mainView.getScrollX(), 0, -WIDTH-mainView.getScrollX(), 0);
//			mainState= MOVE_LEFT;
//		}
//	}
	
	public void moveToRight(){
		if(mainState==STATE_MIDDLE){
			mScroller.startScroll(mainView.getScrollX(), 0, WIDTH-mainView.getScrollX(), 0);
			mainState = MOVE_RIGHT;
		}
	}
	
	
	public void moveToMain(){
//		if(mainState==MOVE_LEFT){
//			mScroller.startScroll(mainView.getScrollX(), 0, -mainView.getScrollX(), 0);
//			mainState= STATE_MIDDLE;
//			currOffset = 0;
//		}
//		else if(mainState==MOVE_RIGHT){
//			mScroller.startScroll(mainView.getScrollX(), 0, -mainView.getScrollX(), 0);
//			mainState= STATE_MIDDLE;
//			currOffset = 0;
//		}
		mScroller.startScroll(mainView.getScrollX(), 0, -mainView.getScrollX(), 0);
		mainState= STATE_MIDDLE;
	}
		
	public int getMoveState(){
		return mainState;
	}
	
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			mainView.scrollTo(mScroller.getCurrX(), 0);
			postInvalidate();
		}
		super.computeScroll();
	}

}
