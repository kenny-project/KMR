package com.kenny.event;

import android.content.Context;
import android.util.Log;

import com.kenny.activity.MainOld;
import com.kenny.file.SaveData;
import com.kenny.struct.AbsEvent;
import com.kenny.util.Const;
import com.kenny.util.T;
import com.kenny.util.Utils;

public class InitEvent extends AbsEvent
{
	Context context;
	public InitEvent(MainOld main)
	{
		super(main);
		// TODO Auto-generated constructor stub
	}

	public InitEvent(Context main){
		super(null);
		context = main;
	}
	
	@Override
	public void ok()
	{	
		//new SubscribeInitEvent(context).run();
		Utils.save(context, "bSubscribe", true);
//		T.ResourceAssetsFile(main,"css", "news.css");
//		T.ResourceAssetsFile(main,"css", "iphone.css");
//		T.ResourceAssetsFile(main,"js", "iphone.js");
		
	}
}
