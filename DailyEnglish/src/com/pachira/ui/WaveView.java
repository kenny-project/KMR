/**   
 * java (__FILE__, "$Revision: 1.2 $")
 * @ WaveView.java Create on 2012-1-18   
 * Copyright (c) 2012 by april_pu@pachiratech.com.   
 */  

package com.pachira.ui;

import java.util.ArrayList;

import com.pachira.common.Common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class WaveView extends View
{
    private Object[] audioData;
 //   private Rect mRect = new Rect();

    private Paint mPaint = new Paint();

    public WaveView(Context context, AttributeSet a)
    {
        super(context,a);
        init();
    }

    private void init()
    {
        audioData = null;
        mPaint.setStrokeWidth(3f);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setARGB(255,120, 120, 120);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setShadowLayer(2.0f, 1.0f, 1.0f, 10);
    }

    public void updateWave(ArrayList<Integer> audioData1) 
    {
        if(audioData1!=null)
        {
            Object[] tmpAudioData=audioData1.toArray();
            int voiceOutDataSize=(int)(1024.0*7*Common.silenceTimeout/1000.0/128.0);
            System.out.println("voiceOut data size="+voiceOutDataSize+" total="+tmpAudioData.length);
            if((tmpAudioData.length -voiceOutDataSize)>0)
            {
        	audioData=new Integer[tmpAudioData.length-voiceOutDataSize];
        	System.arraycopy(tmpAudioData, 0,audioData, 0, (tmpAudioData.length-voiceOutDataSize));;
        
            }
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) 
    {
        super.onDraw(canvas);

        if (audioData == null) 
        {    
            return;
        }
 
        int height=getHeight();
        int width=getWidth();
        
        
        int sampleRate=1;
        int totalDrawPoint=width;
        
        //remove silence data
        
        ArrayList<Integer> sampleData=new ArrayList<Integer>();
        if(width<audioData.length*2)
        {
            sampleRate=audioData.length*2/width;
        }
        else
        {
            totalDrawPoint=audioData.length*2; 
        }
        int maxInt=5000;
        for(int i=0;i<audioData.length;i=i+sampleRate)
        {
            
            int tmp= ((Integer)audioData[i]).intValue();
            if(tmp>maxInt)
        	    tmp=maxInt;
            sampleData.add(new Integer(tmp));
    	    sampleData.add(new Integer(tmp*-1));
        }
        
        
       float x=0.0f;
        float y=height/2;
        for (int i = 0; i < totalDrawPoint - 1; i++) 
        {
            float x1 = (width*1.0f/totalDrawPoint*1.0f)*i;
            float y1 = height/2- (int)(sampleData.get(i).intValue()*1.0/(1.0*maxInt) * (height / 2.0))+9;
            canvas.drawLine(x,y,x1,y1, mPaint);
            x=x1;
            y=y1;
        }
        if(totalDrawPoint<width)
        {
            canvas.drawLine(x,y,(width-1)*1.0f,(height/2.0f)+9.0f, mPaint);
        }
    
   /*     Path path=new Path();
       path.moveTo(1,height/2+9);
        for (int i = 0; i < totalDrawPoint - 1; i++) 
        {
            float x1 = width/totalDrawPoint*i;
            float y1 = height/2+ (int)(sampleData.get(i).intValue()*1.0/1.0 * (height / 2))+9;
            path.moveTo(x1, y1); 
        }
        canvas.drawPath(path, mPaint);*/
        
      
    }
}
