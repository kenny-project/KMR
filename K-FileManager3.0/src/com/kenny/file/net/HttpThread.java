package com.kenny.file.net;

import java.net.URL;

public class HttpThread extends Thread
{
   private String url;
   public HttpThread(String url)
    {
        this.url=url;
        setPriority(Thread.MIN_PRIORITY);
        start();
    }
    @Override
    public void run()
    {
        try
        {
            URL urlStream = new URL(url);
            urlStream.openStream();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
