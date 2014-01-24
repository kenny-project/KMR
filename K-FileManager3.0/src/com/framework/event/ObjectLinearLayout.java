package com.framework.event;

import com.framework.interfaces.InputpadShowOrHideListener;
import com.framework.log.P;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

/**
 * @author aimery 结构所需的LinearLayout
 * */
public class ObjectLinearLayout extends LinearLayout
{

	InputpadShowOrHideListener listener;
	protected LayoutInflater mInflater = null;

	public void setInputPadShoworHideListener(InputpadShowOrHideListener l)
	{
		listener = l;
	}

	public ObjectLinearLayout(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ObjectLinearLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 菜单menu end
	 */
	/**
	 * 将指定View加入到当前窗体
	 * 
	 * @param rid
	 */
	protected void setContentView(int rid)
	{
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		if (mInflater == null)
		{
			mInflater = LayoutInflater.from(getContext());
		}
		this.addView(mInflater.inflate(rid, null), params);
	}

	@Override
	protected final void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		P.v("onSizeChanged== w=" + w + " h=" + h + " oldw=" + oldw + " oldh="
				+ oldh + " keypad ac=");
		if (listener != null)
		{
			if (oldh > h)
			{
				listener.show();
			}
			if (oldh < h || oldh == 0)
			{
				listener.hide();
			}
			listener.onSizeChanged(w, h, oldw, oldh);
		}
	}
}
