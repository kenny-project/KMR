package com.work.market.net;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.protocol.HTTP;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


// ��һ��ʵ�������첽���أ�����ʾ Android ���첽��Ϣ������ Handler �ķ�ʽ��
public class DownloadManagerAsync
{
    // DownloadManagerAsync manager = new DownloadManagerAsync();
    // manager.setOnDownloadCompleteListener(this);
    // manager.setOnDownloadUpdateListener(this);
    // manager.download("http://files.cnblogs.com/webabcd/Android.rar",
    // "/sdcard/Android.rar");
    public DownloadManagerAsync()
    {
        
    }
    // ʵ�����Զ���� Handler
    EventHandler mHandler = new EventHandler(this);
    
    // ��ָ�� url ��ַ�����ļ���ָ��·��
    public void download(final String url, final String savePath)
    {
        new Thread(new Runnable()
        {
            @Override
			public void run()
            {
                try
                {
                    sendMessage(FILE_DOWNLOAD_CONNECT);
                    URL sourceUrl = new URL(url);
                    URLConnection conn = sourceUrl.openConnection();
                    InputStream inputStream = conn.getInputStream();
                    
                    int fileSize = conn.getContentLength();
                 
                    File savefile = new File(savePath);
                    if (savefile.exists())
                    {
                        savefile.delete();
                    }
                    savefile.createNewFile();
                    
                    FileOutputStream outputStream = new FileOutputStream(
                            savePath, true);
                    
                    byte[] buffer = new byte[5024];
                    int readCount = 0;
                    int readNum = 0;
                    int prevPercent = 0;
                    while (readCount < fileSize && readNum != -1)
                    {
                        readNum = inputStream.read(buffer);
                        if (readNum > -1)
                        {
                            outputStream.write(buffer,0,readNum);
                            readCount = readCount + readNum;
                            
                            int percent = (readCount * 100 / fileSize);
                            if (percent > prevPercent)
                            {
                                // �������ؽ�����Ϣ
                                sendMessage(FILE_DOWNLOAD_UPDATE, percent,
                                        readCount);
                                Log.v("package",String.valueOf(percent));
                                prevPercent = percent+8;
                            }
                        }
                    }
                    outputStream.close();
                    sendMessage(FILE_DOWNLOAD_COMPLETE, savePath);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    sendMessage(FILE_DOWNLOAD_ERROR, e);
                }
            }
        }).start();
    }
    
    // ��ȡָ�� url ��ַ����Ӧ����
    public void download(final String url)
    {
        new Thread(new Runnable()
        {
            @Override
			public void run()
            {
                try
                {
                    sendMessage(FILE_DOWNLOAD_CONNECT);
                    URL sourceUrl = new URL(url);
                    URLConnection conn = sourceUrl.openConnection();
                    conn.setConnectTimeout(3000);
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(),
                                    HTTP.UTF_8));
                    String line = null;
                    StringBuffer content = new StringBuffer();
                    while ((line = reader.readLine()) != null)
                    {
                        content.append(line);
                    }
                    reader.close();
                    
                    sendMessage(FILE_DOWNLOAD_COMPLETE, content.toString());
                    
                }
                catch (Exception e)
                {
                    sendMessage(FILE_DOWNLOAD_ERROR, e);
                    Log.v("MyError", e.toString());
                }
            }
        }).start();
    }
    
    // �� Handler ������Ϣ
    private void sendMessage(int what, Object obj)
    {
        // ������Ҫ�� Handler ���͵���Ϣ
        Message msg = mHandler.obtainMessage(what, obj);
        // ������Ϣ
        mHandler.sendMessage(msg);
    }
    
    private void sendMessage(int what)
    {
        Message msg = mHandler.obtainMessage(what);
        mHandler.sendMessage(msg);
    }
    
    private void sendMessage(int what, int arg1, int arg2)
    {
        Message msg = mHandler.obtainMessage(what, arg1, arg2);
        mHandler.sendMessage(msg);
    }
    
    private static final int FILE_DOWNLOAD_CONNECT = 0;
    private static final int FILE_DOWNLOAD_UPDATE = 1;
    private static final int FILE_DOWNLOAD_COMPLETE = 2;
    private static final int FILE_DOWNLOAD_ERROR = -1;
    
    // �Զ���� Handler
    private class EventHandler extends Handler
    {
        private DownloadManagerAsync mManager;
        
        public EventHandler(DownloadManagerAsync manager)
        {
            mManager = manager;
        }
        
        // ������յ�����Ϣ
        @Override
        public void handleMessage(Message msg)
        {
            
            switch (msg.what)
            {
                case FILE_DOWNLOAD_CONNECT :
                    if (mOnDownloadConnectListener != null)
                    {
                        mOnDownloadConnectListener

                        .onDownloadConnect(mManager);
                    }
                    break;
                case FILE_DOWNLOAD_UPDATE :// ���ؽ�����
                    if (mOnDownloadUpdateListener != null)
                    {
                        mOnDownloadUpdateListener.onDownloadUpdate(mManager,
                                msg.arg1);
                    }
                    break;
                case FILE_DOWNLOAD_COMPLETE :// �������
                    if (mOnDownloadCompleteListener != null)
                    {
                        mOnDownloadCompleteListener.onDownloadComplete(
                                mManager, msg.obj);
                    }
                    break;
                case FILE_DOWNLOAD_ERROR :// ����ʧ��
                    if (mOnDownloadErrorListener != null)
                    {
                        mOnDownloadErrorListener

                        .onDownloadError(mManager, (Exception) msg.obj);
                    }
                    break;
                default :
                    break;
            }
        }
    }
    
    // ���������¼�
    private OnDownloadConnectListener mOnDownloadConnectListener;
    public interface OnDownloadConnectListener
    {
        void onDownloadConnect(DownloadManagerAsync manager);
    }
    public void setOnDownloadConnectListener(OnDownloadConnectListener listener)
    {
        mOnDownloadConnectListener = listener;
    }
    
    // �������ؽ��ȸ����¼�
    private OnDownloadUpdateListener mOnDownloadUpdateListener;
    public interface OnDownloadUpdateListener
    {
        void onDownloadUpdate(DownloadManagerAsync manager, int percent);
    }
    public void setOnDownloadUpdateListener(OnDownloadUpdateListener listener)
    {
        mOnDownloadUpdateListener = listener;
    }
    
    // ������������¼�
    private OnDownloadCompleteListener mOnDownloadCompleteListener;
    public interface OnDownloadCompleteListener
    {
        void onDownloadComplete(DownloadManagerAsync manager, Object result);
    }
    public void setOnDownloadCompleteListener(
            OnDownloadCompleteListener listener)
    {
        mOnDownloadCompleteListener = listener;
    }
    
    // ���������쳣�¼�
    private OnDownloadErrorListener mOnDownloadErrorListener;
    public interface OnDownloadErrorListener
    {
        void onDownloadError(DownloadManagerAsync manager, Exception e);
    }
    public void setOnDownloadErrorListener(OnDownloadErrorListener listener)
    {
        mOnDownloadErrorListener = listener;
    }
}