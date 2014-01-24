package com.kenny.syseng;

import java.util.Calendar;
import java.util.Timer;
import java.util.Vector;

import com.kenny.struct.AbsEvent;

/**
 * 事件引擎
 * 
 * @author chenjiangang
 * @date   2011-12-14
 * 
 * */
public class SysEng extends Thread{
    private static SysEng mInstance = null;
    private Vector<AbsEvent> mVecEvent = null;
    private Timer mTimer = null;
    private Thread mThread = null;
    private boolean isRun = false;
    private long opera_time = 0;
    /**
     * 爱保护的构造函数，单态模式
     * 
     * */
    private SysEng() {
    	mVecEvent = new Vector<AbsEvent>();
    	mTimer = new Timer();
    }
    
    public synchronized static SysEng getInstance(){
    	if (mInstance == null){
    		mInstance = new SysEng();
    		mInstance.setPriority(Thread.MAX_PRIORITY);
    		mInstance.start();
    	}
    	return mInstance;
    }
    
    @Override
	public void start(){
    	if (mThread == null){
    		mThread = new Thread(this);
    	}
    	isRun = true;
    	mThread.start();
    }
    
    public void cancel(){
    	isRun = false;
    	mInstance = null;
    	synchronized (mVecEvent) {
        	mVecEvent.removeAllElements();	
		}
    	
    }
    
    public void runEvent(AbsEvent event){
    	synchronized (mVecEvent) {
//    		if (event instanceof NextPageEvent || event instanceof BackPageEvent)
//    		{
//        		if (Math.abs(Calendar.getInstance().getTime().getTime() - opera_time) < 500){
//        			return;
//        		}
//        		opera_time = Calendar.getInstance().getTime().getTime();
//    		}
    		mVecEvent.add(event);
    		mVecEvent.notify();
		}
    }
    
    public void runDelayEvent(AbsEvent event, long delayEvent){
    	mTimer.schedule(event, delayEvent);
    }
    
    @Override
    public void run() {
    	// TODO Auto-generated method stub
    	while (isRun){
    		synchronized (mVecEvent) {
    			if (mVecEvent.size() <= 0){
    				try {
						mVecEvent.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					continue;
    			}
				AbsEvent event = mVecEvent.remove(0);
				event.ok();
			}
    	}
    }
}
