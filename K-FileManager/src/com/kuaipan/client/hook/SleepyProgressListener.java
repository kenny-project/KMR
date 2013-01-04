package com.kuaipan.client.hook;

import com.kuaipan.client.ProgressListener;

/**
 * a sample listener for debugging.
 * @author Ilcwd
 *
 */
public class SleepyProgressListener implements ProgressListener {

	
	public void started() {
		System.out.println("INFO - Started.");
	}

	
	public int getUpdateInterval() {
		return 500;
	}

	
	public void processing(long bytes, long total) {
		System.out.println("INFO - "+bytes+" / "+total+" bytes are done.");
		try {
			Thread.sleep(1000);
		} catch (Exception e) {}
	}

	
	public void completed() {
		System.out.println("INFO - Ended.");
	}

         
         public boolean cancel()
         {
	  // TODO Auto-generated method stub
	  return false;
         }

}
