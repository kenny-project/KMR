package com.kenny.util;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.RectF;

public class ColorFactory {
	private int color = 0xff07c2f1;
	private int pressColor = Color.RED;
	private boolean isChange = false;
	private HashMap<String, Bitmap> colorMap = new HashMap<String, Bitmap>();
	private HashMap<String, Bitmap> circleColorMap = new HashMap<String, Bitmap>();
	
	public boolean isChange() {
		return isChange;
	}

	public void setChange(boolean isChange) {
		this.isChange = isChange;
	}
	
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
	}
	public int getPressColor() {
		return pressColor;
	}
	public void setPressColor(int pressColor) {
		this.pressColor = pressColor;
	}
	
	public Bitmap createArcBitmap(int width, int height, int color){
		Bitmap bmp = colorMap.get(width + "" + height + "" + color);
		if (bmp != null){
			return bmp;
		}
		bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paintD = new Paint();
		paintD.setAntiAlias(true);
		paintD.setDither(true);
		paintD.setStyle(Paint.Style.FILL_AND_STROKE);
		paintD.setStrokeCap(Cap.ROUND);
		paintD.setStrokeJoin(Join.ROUND);
		paintD.setColor(color);
		canvas.drawArc(new RectF(0, 0, width, height), 0, 360, false, paintD);
		colorMap.put(width + "" + height + "" + color, bmp);
		return bmp;
	}  
	
	public Bitmap createArcBitmap(int width, int height){
		Bitmap bmp = colorMap.get(width + "" + height + "" + color);
		if (bmp != null){
			return bmp;
		}
		bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paintD = new Paint();
		paintD.setAntiAlias(true);
		paintD.setDither(true);
		paintD.setStyle(Paint.Style.FILL_AND_STROKE);
		paintD.setStrokeCap(Cap.ROUND);
		paintD.setStrokeJoin(Join.ROUND);
		paintD.setColor(color);
		canvas.drawArc(new RectF(0, 0, width, height), 0, 360, false, paintD);
		colorMap.put(width + "" + height + "" + color, bmp);
		return bmp;
	}
	
	public Bitmap createCirclecBitmap(int width, int height){
		Bitmap bmp = circleColorMap.get(width + "" + height + "" + color);
		if (bmp != null){
			return bmp;
		}
		bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paintD = new Paint();
		paintD.setAntiAlias(true);
		paintD.setDither(true);
		paintD.setStyle(Paint.Style.FILL_AND_STROKE);
		paintD.setStrokeCap(Cap.ROUND);
		paintD.setStrokeJoin(Join.ROUND);
		paintD.setColor(color);
		canvas.drawArc(new RectF(0, 0, height, height), 90, 180, false, paintD);
		canvas.drawRect(new RectF(height - height / 2 - 1, 0, width - height / 2, height), paintD);
		canvas.drawArc(new RectF(width - height, 0, width, height), 270, 180, false, paintD);
		circleColorMap.put(width + "" + height + "" + color, bmp);
		return bmp;
	} 
	
	public Bitmap createCirclecBitmap(int width, int height, int color){
		Bitmap bmp = circleColorMap.get(width + "" + height + "" + color);
		if (bmp != null){
			return bmp;
		}
		bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		Paint paintD = new Paint();
		paintD.setAntiAlias(true);
		paintD.setDither(true);
		paintD.setStyle(Paint.Style.FILL_AND_STROKE);
		paintD.setStrokeCap(Cap.ROUND);
		paintD.setStrokeJoin(Join.ROUND);
		paintD.setColor(color);
		canvas.drawArc(new RectF(0, 0, height, height), 90, 180, false, paintD);
		canvas.drawRect(new RectF(height - height / 2 - 1, 0, width - height / 2, height), paintD);
		canvas.drawArc(new RectF(width - height, 0, width, height), 270, 180, false, paintD);
		circleColorMap.put(width + "" + height + "" + color, bmp);
		return bmp;
	} 
}
