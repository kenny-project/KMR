package com.kenny.util;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.kenny.Application.KApp;

public class TextToBitmap {

	public String textToBitmap(Context context, String bitmapPath ,String b, String s, String c) {
		Bitmap bitmap = null;
		String path = "";
		try {
			KApp app = (KApp) context.getApplicationContext();
			int color = app.colorFactory.getColor();
			TextPaint paint = new TextPaint();
			paint.setTextSize(Utils.sp2px(context, 25));
			paint.setAntiAlias(true);
			paint.setDither(true);
			paint.setColor(color);
			TextPaint spaint = new TextPaint();
			spaint.setTextSize(Utils.sp2px(context, 18));
			spaint.setAntiAlias(true);
			spaint.setDither(true);
			spaint.setColor(color);
			TextPaint cpaint = new TextPaint();
			cpaint.setTextSize(Utils.sp2px(context, 18));
			cpaint.setAntiAlias(true);
			cpaint.setDither(true);
			cpaint.setColor(Color.argb(0xff, 0x64, 0x64, 0x64));
			Bitmap bmp = BitmapFactory.decodeFile(bitmapPath);
			StaticLayout bLayout = new StaticLayout(b, paint,
					bmp.getWidth() - 40, Alignment.ALIGN_NORMAL, 1.0F, 0.0F,
					true);
			StaticLayout sLayout = new StaticLayout(s, spaint,
					bmp.getWidth() - 40, Alignment.ALIGN_NORMAL, 1.0F, 0.0F,
					true);
			StaticLayout cLayout = new StaticLayout(c + "\n\n@金山词霸", cpaint,
					bmp.getWidth() - 40, Alignment.ALIGN_NORMAL, 1.0F, 0.0F,
					true);

//			int bitmapHeight = bLayout.getHeight() + Utils.dip2px(context, 13) + sLayout.getHeight() + cLayout.getHeight() + 20 + bmp.getHeight();
			int bitmapHeight = bLayout.getHeight() + sLayout.getHeight() + cLayout.getHeight() + 40 + bmp.getHeight();
			bitmap = Bitmap.createBitmap(bmp.getWidth(),
					bitmapHeight, Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			canvas.drawARGB(0xff, 0xff, 0xff, 0xff);
			canvas.translate(20, 0);
			bLayout.draw(canvas);
			canvas.translate(0, bLayout.getHeight());
			sLayout.draw(canvas);
			canvas.translate(0, sLayout.getHeight() + Utils.dip2px(context, 10));
			cLayout.draw(canvas);
			canvas.drawBitmap(bmp, -20, cLayout.getHeight() + 20, null);;
			path = Const.CATCH_DIRECTORY + File.separator + "share.png";
			File file = new File(path);
			if (!file.exists()){
				file.createNewFile();
			}
			
			bitmap.compress(CompressFormat.PNG, 100, new FileOutputStream(file));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path;
	}
}
