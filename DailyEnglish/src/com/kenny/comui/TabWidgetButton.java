package com.kenny.comui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.kenny.Application.KApp;
import com.kenny.util.Utils;

public class TabWidgetButton extends TextView{
	Bitmap bmp;
	Bitmap bmp1;
	BitmapDrawable bd;
	BitmapDrawable bd1;
	public TabWidgetButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public TabWidgetButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}
	
	public TabWidgetButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}
	
	private void init(){
		setTextColor(newColorSelector());
	}
	



    /** 设置Selector。 */  
    public StateListDrawable newSelector(Drawable normal, Drawable selected) {  
        StateListDrawable bg = new StateListDrawable();  
        // View.ENABLED_STATE_SET   
        bg.addState(new int[] { android.R.attr.state_selected }, normal);   
        // View.EMPTY_STATE_SET   
        bg.addState(new int[] {}, selected);  
        return bg;  
    }  
    
    /** 设置Selector。 */  
    public ColorStateList newColorSelector() {  

		KApp app = (KApp) getContext().getApplicationContext();
		int color = app.colorFactory.getColor();
    	ColorStateList colors = new ColorStateList(
    			  new int[][] {{ android.R.attr.state_selected}, {0}},
    			  new int[] { Color.WHITE , color});  
        return colors;  
    }
    
    Bitmap createArcBitmap(int width, int height, int color){
		Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paintD = new Paint();
		paintD.setAntiAlias(true);
		paintD.setDither(true);
		paintD.setStyle(Paint.Style.FILL_AND_STROKE);
		paintD.setStrokeCap(Cap.ROUND);
		paintD.setStrokeJoin(Join.ROUND);
		paintD.setColor(color);
		canvas.drawRect(new RectF(0, 0, width, height), paintD);
		return bmp;
	}
    
    @Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		KApp app = (KApp) getContext().getApplicationContext();
		int color = app.colorFactory.getColor();
		bmp = createArcBitmap(getMeasuredWidth(), getMeasuredHeight(), Color.WHITE);
		bmp1 = createArcBitmap(getMeasuredWidth(), getMeasuredHeight(), color);
		bd = new BitmapDrawable(bmp);
		bd1 = new BitmapDrawable(bmp1);
		setBackgroundDrawable(newSelector(bd1, bd));
		setTextColor(newColorSelector());
	}
    
    public void changeBg(){
    	if (bmp != null){
			bmp.recycle();
		}
		if (bmp1 != null){
			bmp1.recycle();
		}
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		if (width <= 0){
			width = Utils.dip2px(getContext(), 70);
		}
		if (height <= 0){
			height = Utils.dip2px(getContext(), 70);
		}
    	bmp = createArcBitmap(width, height, Color.WHITE);
    	KApp app = (KApp) getContext().getApplicationContext();
		int color = app.colorFactory.getColor();
		bmp1 = createArcBitmap(width, height, color);
		bd = new BitmapDrawable(bmp);
		bd1 = new BitmapDrawable(bmp1);
		setBackgroundDrawable(newSelector(bd1, bd));
		setTextColor(newColorSelector());
    }
}
