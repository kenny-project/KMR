package com.framework.interfaces;

import android.view.View;
import android.widget.AdapterView;

/**
 * @author aimery
 * OptionDialog 的点击监听
 * */
public interface OptionDialogClickListener
{
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id,String mark);
}
