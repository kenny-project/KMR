package com.kenny.data;

import com.kenny.event.DoNothingEvent;
import com.kenny.struct.AbsEvent;

/**
 * @author aimery 对话框结构
 * */
public class DialogData
{
	public static final byte BUT_1_TYPE = 4;// 1个but类型
    public static final byte BUT_2_TYPE = 0;// 2个but类型
    public static final byte BUT_3_TYPE = 1;// 3个but类型
    public static final byte BUT_4_TYPE = 2;// 2个but类型  by wmh
    public static final byte BUT_5_TYPE = 3;// 2个but类型  by wmh
    public static final byte BUT_6_TYPE=6;//3按钮竖向排列方式
    
    public byte tyte = BUT_2_TYPE;
    public String title = "";// 对话框标题
    public String content = "";// 对话框内容
    public String leftcmd = "";// 左按钮
    public String middlecmd = "";// 中间按钮
    public String rightcmd = "";// 右按键
    public String blank = "                        ";
    
    public AbsEvent leftevent = null;// 左按键事件
    public AbsEvent middleevent = null;// 左按键事件
    public AbsEvent rightevent = null;// 右按键事件
    
    public DialogData(String title, String content, String leftcmd,
            String rightcmd, AbsEvent leftevent, AbsEvent rightevent)
    {
        this.tyte = DialogData.BUT_2_TYPE;
        this.title = title;
        this.content = content + blank;
        this.leftcmd = leftcmd;
        this.rightcmd = rightcmd;
        this.leftevent = leftevent;
        this.rightevent = rightevent;
    }
    public DialogData()
    {
    	
    }
    public void AddLeftCmd(String cmd,AbsEvent event)
    {
        this.leftcmd = cmd;
        if(event==null)
        {
        	this.leftevent=	new DoNothingEvent(null);
        }
        else
        {
        this.leftevent = event;
        }
    }
    public DialogData(String title, String content, String cmd,AbsEvent event)
    {
        this.tyte = DialogData.BUT_1_TYPE;
        this.title = title;
        this.content = content + blank;
        this.leftcmd = cmd;
        this.leftevent = event;
    }
    
    /**
     * type:显示的DiaLog类型  by wmh
     */
    public DialogData(byte type,String title, String content, String leftcmd,
            AbsEvent leftevent, String rightcmd, AbsEvent rightevent,String midcmd,AbsEvent midevt)
    {
        this.tyte = type;
        this.title = title;
        this.content = content + blank;
        this.leftcmd = leftcmd;
        this.rightcmd = rightcmd;
        this.middlecmd = midcmd;
        
        this.leftevent = leftevent;
        this.rightevent = rightevent;
        this.middleevent = midevt;
    }
    public DialogData(byte type, String title, String content, String leftcmd,
            String midcmd, String rightcmd, AbsEvent leftevent,
            AbsEvent midevt, AbsEvent rightevent)
    {
        this.tyte = type;
        this.title = title;
        this.content = content + blank;
        this.leftcmd = leftcmd;
        this.middlecmd = midcmd;
        this.rightcmd = rightcmd;
        this.leftevent = leftevent;
        this.middleevent = midevt;
        this.rightevent = rightevent;
    }
    
}
