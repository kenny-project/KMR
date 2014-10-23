package com.kenny.Slidingmenu.Fragment;

import java.util.ArrayList;

import android.support.v4.app.Fragment;

import com.framework.page.AbsPage;

public class KPageFragment {

	private int mPos = -1;
	private int mImgRes;
	private LocalPage mContent ;	
	/**
	 * 本地
	 */
	public static final int Search = 0;
	public static final int Local = 1;
	public static final int NetWork = -1;// 2
	public static final int Favorite = 2;
	public static final int apps = 3;
	//public static final int task = 4;
	public static final int tools = 4;
	//private static Workspace mWorkspace;
	private ArrayList<AbsPage> mListViews;
	
	public  Fragment getFragment(int id)
	{
		return null;
	}
}
