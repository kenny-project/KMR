package com.kenny.file.bean;
/**
 * FTP客户端的列表
 * @author WangMinghui
 *
 */
public class NetClientBean
{
	private int id=0;
	private String Title="";//昵称
	private String Desc=""; //解释
	private String UserName=""; //用户名
	private String PassWord=""; //密码
	private boolean check = false; // 是否选择
	private boolean Anonymous=false;//是否匿名访问
	//private boolean Anonymous=false;//是否匿名访问
	private boolean visible=true; //是否显示
	private String date="";   //创建日期
	private String host="";   //服务器地址
	private int port=21;   //服务器端口
	private int type;//类型
	private String LocalPath = "/";
	public String getLocalPath()
	{
		return LocalPath;
	}

	public void setLocalPath(String localPath)
	{
		LocalPath = localPath;
	}

	public boolean isAnonymous()
	{
		return Anonymous;
	}

	public void setAnonymous(boolean anonymous)
	{
		Anonymous = anonymous;
	}

	
	public String getTitle()
	{
		return Title;
	}

	public void setTitle(String title)
	{
		Title = title;
	}

	public String getUserName()
	{
		return UserName;
	}

	public void setUserName(String userName)
	{
		UserName = userName;
	}

	public String getPassWord()
	{
		return PassWord;
	}

	public void setPassWord(String passWord)
	{
		PassWord = passWord;
	}
	
	public int getID()
	{
		return id;
	}
	public void setID(int id)
	{
		this.id =id;
	}
	public void setID(String id)
	{
		this.id =Integer.valueOf(id);
	}
	public String getDesc()
	{
		return Desc;
	}

	public void setDesc(String desc)
	{
		Desc = desc;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public int getPort()
	{
		return port;
	}
	public void setPort(int port)
	{
		this.port = port;
	}
	public void setPort(String port)
	{
		this.port =Integer.valueOf(port);
	}

	public int getType()
	{
		return type;
	}
	public void setType(int type)
	{
		this.type =type;
	}

	public void setType(String type)
	{
		this.type =Integer.valueOf(type);
	}

	public int getEncode()
	{
		return Encode;
	}

	public void setEncode(int encode)
	{
		Encode = encode;
	}
	private int Encode;//类型
	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public boolean isVisible()
	{
		return visible;
	}

	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}

	public boolean isChecked()
	{
		return check;
	}

	public void setChecked(boolean check)
	{
		this.check = check;
	}

	public NetClientBean()
	{
		
	}
}
