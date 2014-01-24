package com.kenny.interfaces;

import android.content.Intent;

/**
 * 调用其它activity需要返回结果时执行
 * @author chenjiangang
 * 
 * */
public interface IOnActivityResult {
	
	public void onActivityResult(int requestCode, int resultCode, Intent data);
}
