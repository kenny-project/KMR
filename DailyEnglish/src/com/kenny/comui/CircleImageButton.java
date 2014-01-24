package com.kenny.comui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.kenny.Application.KApp;
import com.kenny.util.ColorFactory;

public class CircleImageButton extends ImageButton implements KsView {
	Bitmap bmp;
	Bitmap bmp1;
	BitmapDrawable bd;
	BitmapDrawable bd1;
	int reversedColor = Color.GRAY;
	ColorFactory factory = null;
	int oldColor = 0;
	private boolean oldReversed = false;
	private boolean touchable = true;
	private boolean reversed = false;
	private Context context;

	int touchAction = MotionEvent.ACTION_UP;

	public boolean isTouchable() {
		return touchable;
	}

	public void setTouchable(boolean touchable) {
		this.touchable = touchable;
	}

	public boolean isReversed() {
		return reversed;
	}

	public void setReversed(boolean reversed) {
		this.reversed = reversed;
		// postInvalidate();
	}

	public CircleImageButton(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
		init();
	}

	public CircleImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		// TODO Auto-generated constructor stub
		init();
	}

	public CircleImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		// TODO Auto-generated constructor stub
		init();
	}

	private void init() {
		KApp app = (KApp) context.getApplicationContext();
		factory = app.colorFactory;
		oldColor = factory.getColor();
		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Log.e("chenjg", "action is " + event.getAction());
				if (touchable == false) {
					return false;
				}
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					setBackgroundDrawable(bd1);
					touchAction = MotionEvent.ACTION_DOWN;
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_OUTSIDE:
				case MotionEvent.ACTION_CANCEL:
					handler.postDelayed(new Runnable() {

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
	public void changeBg() {
		// if (oldColor == factory.getColor() && reversed == oldReversed){
		// return;
		// }
		// if (reversed == oldReversed){
		// return;
		// }
		int height = getMeasuredHeight();
		int width = getMeasuredWidth();
		if (height > 500 || height <= 0) {
			height = 100;
		}
		if (width > 500 || width <= 0) {
			width = 100;
		}
		if (height < 500) {
			if (reversed == true) {
				// 反态
				bmp = factory.createArcBitmap(width, height, reversedColor);
				bmp1 = factory.createArcBitmap(width, height, factory.getPressColor());
			} else {
				// 常态
				bmp = factory.createArcBitmap(width, height, factory.getColor());
				bmp1 = factory.createArcBitmap(width, height, factory.getPressColor());
			}
		}
		oldReversed = reversed;
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
		int height = getMeasuredHeight();
		int width = getMeasuredWidth();
		if (height > 500 || height <= 0) {
			height = 100;
		}
		if (width > 500 || width <= 0) {
			width = 100;
		}
		if (height < 500) {
			if (reversed == true) {
				// 反态
				bmp = factory.createArcBitmap(width, height, reversedColor);
				bmp1 = factory.createArcBitmap(width, height, factory.getPressColor());
			} else {
				// 常态
				bmp = factory.createArcBitmap(width, height, factory.getColor());
				bmp1 = factory.createArcBitmap(width, height, factory.getPressColor());
			}
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
