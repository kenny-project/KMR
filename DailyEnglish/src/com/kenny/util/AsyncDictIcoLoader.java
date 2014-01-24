package com.kenny.util;



import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.kenny.Interface.ImageCallback;
import com.kenny.file.SDFile;
//下载网络图标并读取本地图标
public class AsyncDictIcoLoader
{
    private static AsyncDictIcoLoader m_object=new AsyncDictIcoLoader();
    private Map<String, SoftReference<Drawable>> imageCache = new HashMap<String, SoftReference<Drawable>>();
    private Context m_ctx;
    private AsyncDictIcoLoader()
    {
        
    }
    public static AsyncDictIcoLoader GetObject(Context ctx)
    {
        m_object.m_ctx=ctx;
        return m_object;
    }
    /**
     * 清除全部数据
     */
    public void RemoveAll()
    {
    	imageCache.clear();
    }
    public Drawable loadDrawable( final String imageUrl,
            final ImageCallback callback)
    {
        Drawable image=null;
        if (imageCache.containsKey(imageUrl))//从列表中获取数据
        {
            SoftReference<Drawable> softReference = imageCache.get(imageUrl);
            if (softReference!= null)
            {
                image=softReference.get();
                if(image!=null)
                {
                    return image;
                }
                imageCache.remove(imageUrl);
            }
            else
            {
                return null;
            }
        }
        image = SDFile.ReadRAMImageFile(m_ctx, imageUrl);
        if (image != null)
        {
            imageCache.put(imageUrl, new SoftReference<Drawable>(image));
            return image;
        }
        if(!KCommand.isNetConnectNoMsg(m_ctx))return null;
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
        return null;
    }
    
    protected Drawable loadImageFromUrl(String imageUrl)
    {
        try
        {
            InputStream imageStream=new URL(imageUrl).openStream();
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            if(T.checkSDCard())
            SDFile.WriteRAMImageFile(m_ctx,imageUrl,bitmap);
            Drawable result=T.bitmap2drawable(bitmap);
            imageCache.put(imageUrl, new SoftReference<Drawable>(result));
            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
