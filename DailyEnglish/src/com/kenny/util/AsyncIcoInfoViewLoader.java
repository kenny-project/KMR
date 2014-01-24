package com.kenny.util;



import java.io.InputStream;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.kenny.Interface.ImageCallback;
import com.kenny.file.SDFile;
//下载网络图标并读取本地图标
public class AsyncIcoInfoViewLoader
{
    private static AsyncIcoInfoViewLoader m_object=new AsyncIcoInfoViewLoader();
    private Context m_ctx;
    private AsyncIcoInfoViewLoader()
    {
        
    }
    public static AsyncIcoInfoViewLoader GetObject(Context ctx)
    {
        m_object.m_ctx=ctx;
        return m_object;
    }
    public boolean loadDrawable( final String imageUrl,
            final ImageCallback callback)
    {
    	
        if (imageUrl==null||SDFile.getSDWebViewLogoFileExits(imageUrl))
        {
            return true;
        }
        if(!KCommand.isNetConnectNoMsg(m_ctx))return false;
        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                if(msg.obj!=null)
                {
                    callback.imageLoaded((Drawable) msg.obj, imageUrl);
                }
            }
        };
        new Thread()
        {
            @Override
			public void run()
            {
                Drawable drawable = loadImageFromUrl(imageUrl);
                if(drawable!=null)
                {
                    handler.sendMessage(handler.obtainMessage(0, drawable));
                }
            };
        }.start();
        return false;
    }
    
    protected Drawable loadImageFromUrl(String imageUrl)
    {
        try
        {
            InputStream imageStream=new URL(imageUrl).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            if(T.checkSDCard())
            SDFile.WriteSDWebViewLogoFile(m_ctx,imageUrl,bitmap);
            Drawable result=T.bitmap2drawable(bitmap);
            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
