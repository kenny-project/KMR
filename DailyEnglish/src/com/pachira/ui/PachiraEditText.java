package com.pachira.ui;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.EditText;

public class PachiraEditText extends EditText
{
	public PachiraEditText(Context context)
	{
		super(context);
	}
	public PachiraEditText(Context context,AttributeSet attrs)
	{
		super(context,attrs);
	}
	public PachiraEditText(Context context,AttributeSet attrs,int defStyle)
	{
		super(context,attrs,defStyle);
	}
	@Override
	public Editable getText()
	{
		Editable text=super.getText();
		String s=text.toString();
		if(s.length()!=0)
		{
			String userBehavior="<pachira><lists>"+"<list resultId="+"\""+"-1"+"\""+" listId="+"\""+"-1"+"\""+" selected ="+"\""+"0"+"\""+
				" content="+"\""+s+"\""+" newContent="+"\""+""+"\"/>"+"</lists></pachira>";
			byte[] data=userBehavior.getBytes();
        //	HttpMultipartRequest hmr = new HttpMultipartRequest(null,SharedConstants.VALUE_PARAM_UPLOAD_RESULT,data,-1,Common.appId);
        //	Thread t=new Thread(hmr);
    	//	t.start();	
		}
		return text;
	}
}
