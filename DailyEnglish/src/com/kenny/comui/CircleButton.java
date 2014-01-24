package com.kenny.comui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.kenny.Application.KApp;
import com.kenny.util.ColorFactory;

public class CircleButton extends Button implements KsView{

	Bitmap bmp;
	Bitmap bmp1;
	BitmapDrawable bd;
	BitmapDrawable bd1;
	int oldColor = 0;
	int touchAction = MotionEvent.ACTION_UP;
	private Context context;
	public CircleButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
	}

	public CircleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		KApp app = (KApp) context.getApplicationContext();
		oldColor = app.colorFactory.getColor();
		init();
	}
	
	public CircleButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
	}
	
	private void init(){
		setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Log.e("chenjg", "action is " + event.getAction());
				switch (event.getAction()){
				case MotionEvent.ACTION_DOWN:
					setBackgroundDrawable(bd1);
					touchAction = MotionEvent.ACTION_DOWN;
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_OUTSIDE:
				case MotionEvent.ACTION_CANCEL:
					handler.postDelayed(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							touchAction = MotionEvent.ACTION_UP;
							setBackgroundDrawable(bd);
						}
						
					}, 100);
					break;
				}
				return false;
			}
		});
	}
	
	Handler handler = new Handler();
	
	@Override
	public void changeBg(){
		KApp app = (KApp) context.getApplicationContext();
		ColorFactory factory = app.colorFactory;
//		if (oldColor == factory.getColor()){
//			return;
//		}
		int height = getMeasuredHeight();
		int width = getMeasuredWidth();
		if (height > 500 || height <= 0){
			height = 100;
		}
		if (width > 500 || width <= 0){
			width = 100;
		}
		bmp = factory.createCirclecBitmap(width, height);
		bmp1 = factory.createCirclecBitmap(width, height, factory.getPressColor());
		bd = new BitmapDrawable(bmp);
		bd1 = new BitmapDrawable(bmp1);
		if (touchAction == MotionEvent.ACTION_DOWN){
			// 当前是按下态
			setBackgroundDrawable(bd1);
		} else {
			// 当前是正常态
			setBackgroundDrawable(bd);
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		KApp app = (KApp) context.getApplicationContext();
		ColorFactory factory = app.colorFactory;;
		int height = getMeasuredHeight();
		int width = getMeasuredWidth();
		if (height > 500 && height <= 0){
			height = 100;
		}
		if (width > 500 && width <= 0){
			width = 100;
		}
		if (height < 500){
			bmp = factory.createCirclecBitmap(width, height);
			bmp1 = factory.createCirclecBitmap(width, height, factory.getPressColor());
		}
		bd = new BitmapDrawable(bmp);
		bd1 = new BitmapDrawable(bmp1);
		if (touchAction == MotionEvent.ACTION_DOWN){
			// 当前是按下态
			setBackgroundDrawable(bd1);
		} else {
			// 当前是正常态
			setBackgroundDrawable(bd);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		changeBg();
		super.onDraw(canvas);
	}

}
