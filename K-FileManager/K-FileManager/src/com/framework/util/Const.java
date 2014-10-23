package com.framework.util;

/**
 * 常量
 * 
 * @author aimery
 * */
public class Const
{

	public static final int UNSHOWANIM = 0;// 不显示切换动甄1�7
	public static final int SHOWANIM = 1;// 显示切换动画
	/**
	 * 
	 * INetRegister 网络通道注册
	 */

	public static final int NetAddTotal = 10;// 发�1�7�数捄1�7
	public static final int NR_ItemComment = 12;// 网络命令注册砄1�7
	public static final int NetAddItemComment = 13;// 发�1�7�数捄1�7
	public static final int NetAddMsgCommnet = 15;// 发�1�7�留訄1�7
	public static final int Net_Item = 14;// 获得内容
	public static final int Net_WebPagebyFileEvent = 15;// 网络数据写文仄1�7

	public static final int Msg_UpdateGroup = 0x105;// 广播接收_更新Group数据

	public static final int NR_NETCOMMAND = 12;// 网络命令注册砄1�7
	public static final int NR_CLUSTERCASE = 13;// 会议列表注册砄1�7
	public static final int NR_LOGINPAGE = 14;// 会议列表注册砄1�7

	public static final int ER_COMMLAYER = 1843;// 公共层处理网络异常事仄1�7

	public static int SW;// 屏幕宄1�7
	public static int SH;// 屏幕髄1�7
	// 当前屏幕密度相关倄1�7
	public static int densityDpi;
	public static float density;

	/**
	 * 切换界面霄1�7要关闭的Dialog的MARK，需要在各个弹出类型的dialogdata 中给DIALOG_MARK进行该项标记
	 * */
	public static final String CHANGE_PAGE_CLOSEDIA = "CD";

	// 0-在线〄1�7忙碌〄1�7勿扰〄1�7离开〄1�7隐身
	public static final byte S_OFFLINE = -1;// 离线
	public static final byte S_ONLINE = 0;// 在线
	public static final byte S_BUSY = 1;// 忙碌
	public static final byte S_NOT_DISTURB = 2;// 请勿打扰
	public static final byte S_AWAY = 3;// 离开
	public static final byte S_HIDE = 4;// 隐身

	public static long clicktime = 0;// 弄1�7始点击的时间
	public static long de_time = 500;
	// 不接收群消息的群id分割笄1�7
	public static final String SPLIT_CHAR = ",";

	public static final byte CS_AUTO_POP = 0;
	public static final byte CS_RECEIVE_POP = 1;
	public static final byte CS_RECEIVE_NOT_POP = 2;
	public static final byte CS_RECEIVE_SHOW_NUM = 3;
	public static final byte CS_NO_RECEIVE = 4;

	public static final int CostomHeadidx = 0xffffffff;

	public static final byte BLOCK = 0;
	public static final byte UNBLOCK = 1;

}
