package com.kenny.comui;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.kenny.Application.KApp;
import com.kenny.util.ColorFactory;

public class SquareButton extends Button implements KsView{
	int color = 0;
	int pressColor = 0;
	public SquareButton(Context context) {
		super(context);
		KApp app = (KApp) context.getApplicationContext();
		color = app.colorFactory.getColor();
		pressColor = app.colorFactory.getPressColor();
		// TODO Auto-generated constructor stub
		init();
	}

	public SquareButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		KApp app = (KApp) context.getApplicationContext();
		color = app.colorFactory.getColor();
		pressColor = app.colorFactory.getPressColor();
		// TODO Auto-generated constructor stub
		init();
	}
	
	public SquareButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		KApp app = (KApp) context.getApplicationContext();
		color = app.colorFactory.getColor();
		pressColor = app.colorFactory.getPressColor();
		// TODO Auto-generated constructor stub
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
					setBackgroundColor(pressColor);
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_OUTSIDE:
				case MotionEvent.ACTION_CANCEL:
					handler.postDelayed(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							setBackgroundColor(color);
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

		KApp app = (KApp) getContext().getApplicationContext();
		color = app.colorFactory.getColor();
		pressColor = app.colorFactory.getPressColor();
		setBackgroundColor(color);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		KApp app = (KApp) getContext().getApplicationContext();
		color = app.colorFactory.getColor();
		pressColor = app.colorFactory.getPressColor();
		setBackgroundColor(color);
	}
}
