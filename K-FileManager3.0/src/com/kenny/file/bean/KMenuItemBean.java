package com.kenny.file.bean;
//词库信息
public class KMenuItemBean  implements CharSequence
{
	private int ID = 0;
	private String title = "";// 词典名称
	private int ImgID;
	public static final int TYPE_LOCALFILE=0x1001;
	public static final int TYPE_LOCAL_STATUS=0x1002;
	public static final int TYPE_CLASSIFY_FILE=0x1003;
	public static final int TYPE_FAVORITE_FILE=0x1004;
	public static final int TYPE_APPLICATION_PAGE=0x1005;
	public static final int TYPE_FTP_PAGE=0x1006;
	public static final int TYPE_KUAIPAN_PAGE=0x1007;
	private int type;
	private Object obj;
	/**
	 * 窗口类型
	 * @return
	 */
	public int getType()
	{
		return type;
	}
	public KMenuItemBean(int iD,String title,int type,Object obj,int imgID)
	{
		ID = iD;
		this.title = title;
		this.type = type;
		this.obj=obj;
		ImgID = imgID;
	}
	
	public int getID()
	{
		return ID;
	}
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public int getImgID()
	{
		return ImgID;
	}
	public Object getObj()
	{
		return obj;
	}

	public void setObj(Object obj)
	{
		this.obj = obj;
	}

	@Override
	public int length()
	{
		// TODO Auto-generated method stub
		return title.length();
	}
	@Override
	public char charAt(int index)
	{
		// TODO Auto-generated method stub
		return title.charAt(index);
	}
	@Override
	public CharSequence subSequence(int start, int end)
	{
		// TODO Auto-generated method stub
		return title.subSequence(start, end);
	}

}
