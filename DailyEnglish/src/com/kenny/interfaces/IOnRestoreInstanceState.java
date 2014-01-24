package com.kenny.interfaces;

import android.os.Bundle;

/**
 * 恢复onSaveInstanceState保存的信息
 * 在start（）和resume（）之间
 * @author chenjiangang
 * */
public interface IOnRestoreInstanceState
{
	/**
	 * 恢复onSaveInstanceState保存的信息
	 * 在start（）和resume（）之间
	 * */
   public void onRestoreInstanceState(Bundle savedInstanceState);
}
