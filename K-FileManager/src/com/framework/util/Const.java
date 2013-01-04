package com.framework.util;

/**
 * 甯搁噺
 * 
 * @author aimery
 * */
public class Const
{

	public static final int UNSHOWANIM = 0;// 涓嶆樉绀哄垏鎹㈠姩鐢�
	public static final int SHOWANIM = 1;// 鏄剧ず鍒囨崲鍔ㄧ敾
	/**
	 * 
	 * INetRegister 缃戠粶閫氶亾娉ㄥ唽
	 */

	public static final int NetAddTotal = 10;// 鍙戦�佹暟鎹�
	public static final int NR_ItemComment = 12;// 缃戠粶鍛戒护娉ㄥ唽鐮�
	public static final int NetAddItemComment = 13;// 鍙戦�佹暟鎹�
	public static final int NetAddMsgCommnet = 15;// 鍙戦�佺暀瑷�
	public static final int Net_Item = 14;// 鑾峰緱鍐呭
	public static final int Net_WebPagebyFileEvent = 15;// 缃戠粶鏁版嵁鍐欐枃浠�

	public static final int Msg_UpdateGroup = 0x105;// 骞挎挱鎺ユ敹_鏇存柊Group鏁版嵁

	public static final int NR_NETCOMMAND = 12;// 缃戠粶鍛戒护娉ㄥ唽鐮�
	public static final int NR_CLUSTERCASE = 13;// 浼氳鍒楄〃娉ㄥ唽鐮�
	public static final int NR_LOGINPAGE = 14;// 浼氳鍒楄〃娉ㄥ唽鐮�

	public static final int ER_COMMLAYER = 1843;// 鍏叡灞傚鐞嗙綉缁滃紓甯镐簨浠�

	public static int SW;// 灞忓箷瀹�
	public static int SH;// 灞忓箷楂�
	// 褰撳墠灞忓箷瀵嗗害鐩稿叧鍊�
	public static int densityDpi;
	public static float density;

	/**
	 * 鍒囨崲鐣岄潰闇�瑕佸叧闂殑Dialog鐨凪ARK锛岄渶瑕佸湪鍚勪釜寮瑰嚭绫诲瀷鐨刣ialogdata 涓粰DIALOG_MARK杩涜璇ラ」鏍囪
	 * */
	public static final String CHANGE_PAGE_CLOSEDIA = "CD";

	// 0-鍦ㄧ嚎銆�蹇欑銆�鍕挎壈銆�绂诲紑銆�闅愯韩
	public static final byte S_OFFLINE = -1;// 绂荤嚎
	public static final byte S_ONLINE = 0;// 鍦ㄧ嚎
	public static final byte S_BUSY = 1;// 蹇欑
	public static final byte S_NOT_DISTURB = 2;// 璇峰嬁鎵撴壈
	public static final byte S_AWAY = 3;// 绂诲紑
	public static final byte S_HIDE = 4;// 闅愯韩

	public static long clicktime = 0;// 寮�濮嬬偣鍑荤殑鏃堕棿
	public static long de_time = 500;
	// 涓嶆帴鏀剁兢娑堟伅鐨勭兢id鍒嗗壊绗�
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
