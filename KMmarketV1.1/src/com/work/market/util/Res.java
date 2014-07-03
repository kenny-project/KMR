package com.work.market.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.byfen.market.bean.BanBean;
import com.work.market.net.Dao;

import android.app.Activity;

public class Res
{

	private static Res m_Res = null;
	private static Activity _context;


	private Res()
	{
		
	}

	private int mVoiceID = 1;

	public void setVoiceID(int id)
	{
		mVoiceID = id;
	}

	public int getVoiceID()
	{
		return 1;
	}


	public static void setActivity(Activity context)
	{
		_context = context;
	}

	public static Res getInstance()
	{
		if (m_Res == null)
		{
			m_Res = new Res();
		}
		return m_Res;
	}
}
