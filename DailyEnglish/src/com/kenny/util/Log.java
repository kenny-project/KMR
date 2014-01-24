package com.kenny.util;

/**
 * 自定义Log，方便管理Log
 * 
 * @author chenjiangang
 * 
 * */
public class Log {
	
	public static void e(String tag, String message){
		android.util.Log.e(tag, message);
	}
	
	public static void d(String tag, String message){
		android.util.Log.d(tag, message);
	}
	
	public static void v(String tag, String message){
		android.util.Log.v(tag, message);
	}
	
	public static void i(String tag, String message){
		android.util.Log.i(tag, message);
	}
	
	public static void w(String tag, String message){
		android.util.Log.w(tag, message);
	}
}
