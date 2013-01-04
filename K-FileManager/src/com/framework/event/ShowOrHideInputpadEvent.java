package com.framework.event;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.framework.log.P;

public class ShowOrHideInputpadEvent extends AbsEvent
{

	boolean isshow = true;// true=显示 false=隐藏
	View view;
	private InputMethodManager inputmanager;
	private Activity act;

	/**
	 * 切换键盘
	 * @param m
	 * @param view
	 * @param b 
	 */
	public ShowOrHideInputpadEvent(Activity m, View view, boolean b)
	{
		isshow = b;
		this.view = view;
		this.act = m;
	}

	@Override
	public void ok()
	{
		// TODO Auto-generated method stub
		if (isshow)
		{
			showInputpad(this.view);
		} else
		{
			hideInputPad(this.view);
		}
	}

	/**
	 * 隐藏软键盘
	 * */
	public void hideInputPad(View edit)
	{

		inputmanager = (InputMethodManager) act
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		P.debug("hideInputPad=" + inputmanager.isActive());
		if (inputmanager.isActive())
		{
			inputmanager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 显示软键盘
	 * */
	public void showInputpad(View edit)
	{
		inputmanager = (InputMethodManager) act
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputmanager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
				InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
