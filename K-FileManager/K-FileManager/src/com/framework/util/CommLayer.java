package com.framework.util;

import com.framework.interfaces.IPageManage;

public class CommLayer 
{
	private static IPageManage pageManage;

	public static void setPMG(IPageManage page)
	{
		pageManage = page;
	}
	public static IPageManage getPMG() 
	{
		return pageManage;
	}
}
