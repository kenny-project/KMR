package com.kenny.event;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.kenny.activity.MainOld;
import com.kenny.struct.AbsEvent;

/**
 * 显示或隐藏软键盘
 * 
 * @author chenjiangang
 * */
public class ShowOrHideInputpadEvent extends AbsEvent {

	boolean isshow = true;// true=显示 false=隐藏
	View view;
	private InputMethodManager inputmanager;

	public ShowOrHideInputpadEvent(Context m, View view, boolean b) {
		super(m);
		isshow = b;
		this.view = view;
	}

	@Override
	public void ok() {
		// TODO Auto-generated method stub
		if (isshow) {
			showInputpad(this.view);
		} else {
			hideInputPad(this.view);
		}
	}

	/**
	 * 隐藏软键盘
	 * */
	public void hideInputPad(final View edit) {
		inputmanager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputmanager.hideSoftInputFromWindow(edit.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 显示软键盘
	 * */
	public void showInputpad(final View edit) {
		inputmanager = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputmanager.showSoftInput(edit,
				InputMethodManager.RESULT_UNCHANGED_SHOWN);
	}
}
