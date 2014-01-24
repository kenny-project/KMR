package com.kenny.util;

import java.io.File;

public class Const {
	
	public final static int VOA_SUBSCRIBE_COUNT = 10;
	
	public final static int Net_WebbyFile_Run = 492; // 页面下载
	public final static int Net_WebbyFile_Error = 493; // 页面下载
	public final static int Net_WebbyFile_Finish = 494; // 页面下载
	
	public final static int Net_WebPagebyFile_Run = 392; // 页面下载
	public final static int Net_WebPagebyFile_Error = 393; // 页面下载
	public final static int Net_WebPagebyFile_Finish = 394; // 页面下载
	public final static int Net_VOAPagebyFile_Finish = 394; // 页面下载
	
	public final static int Dict_KDMAdapter_Spread_true = 1394; // 页面下载
	public final static int Dict_KDMAdapter_Spread_false = 1395; // 页面下载
	
	public final static String CHANNEL_NUM = "529"; // 渠道号
	
	public final static int Dailysentence = 1;
	
	public final static int Bilingual = 2;
	
	public final static int VOA_Page = 4;
    public static int SHOW_ANIM = 0;
    public static int NOT_SHOW_ANIM = 1;
    public static int INDEX_NOT_SHOW = 2;
	public static final String SDCard=android.os.Environment
	.getExternalStorageDirectory().getAbsolutePath();
	
    public static String CATCH_DIRECTORY = SDCard + File.separator + "DailyEnglish" + File.separator;
    
    public static String TUIJIAN_IMAGE = CATCH_DIRECTORY + "tuijian"+ File.separator; 
    
    public static String DICT_DIRECTORY=CATCH_DIRECTORY+"dict" + File.separator;       // 词典缓存目录
    
    public static String NET_DIRECTORY = CATCH_DIRECTORY + "cache" + File.separator;   // 网络解释缓存目录
    
    public static String VOICE_DIRECTORY = CATCH_DIRECTORY + "voice" + File.separator; // 声音缓存目录
    
    public static String MP3_DIRECTORY = CATCH_DIRECTORY + "mp3" + File.separator; // 声音缓存目录
    
    public static String LOGO_DIRECTORY = NET_DIRECTORY + "logo" + File.separator; // 声音缓存目录
    
    public static String RECOMMENDATION_DIRECTORY = CATCH_DIRECTORY + "recommendation" + File.separator; // 推荐目录
    
    public static String RECOMMENDATION_ICON_DIRECTORY = CATCH_DIRECTORY + "recommendation" + File.separator + "icon" + File.separator; // 推荐icon目录
    
    public static String SKIN_DIRECTORY = CATCH_DIRECTORY + "skin" + File.separator + "icon" + File.separator; // 推荐icon目录
    
    public static final String LOADING_AD_IMAGE_DIRECTORY = CATCH_DIRECTORY + "ad" + File.separator;
    
    public static final int Net_Dailysentence_Data = 16;// 每日一句
    public static final int Net_Bilingual_Data = 17;// 双语资询
    public static final int Net_btVOA_Data = 85;// VOA
    public static final int Net_btSetting_Data = 86;// VOA
    
    public static final int Net_Conversation_Data = 18;// 
    public static final int Net_ConversationItem_Data = 21;// 
    
    public static final int Net_Not_Data = 19;// 
    public static final int Net_Favorite_Data = 20;// 收藏夹内容
    
    public static final int RandomWordListEvent = 2030;// 获取随机数
    public static final int ADD_NEWWORD_BOOK = 2031;// 增加生词本
    public static int REGISTER_PAGE_ID_NETTEST = 0x101;
    public static int REGISTER_PAGE_ID_FEEDBACK = 0x102;
    public static int REGISTER_PAGE_ID_WAVE = 0x103;
    public static int REGISTER_LearnPAGE_ID_WAVE = 0x104;
    public static int NEWWORD_OPEAR_ADD = 1;
    public static int NEWWORD_OPERA_DELETE = 2;
    public static int NEWWORD_OPERA_MODIFY = 3;
    public static int NEWWORD_OPERA_NONE = 0;
    public static int NEWWORD_DEFAULT_WORDID = 0;
    public static String NEWWORD_SYNC_URL = "http://scb.iciba.com/api/api.php";
    public static String SEARCH_NET_WORD_URL = "http://dict-mobile.iciba.com/date/2013-04-23/index.php";
    public static String TRANSTION_URL = "http://dict-mobile.iciba.com/fy/2012-12-14/api_2013_04_02.php";
    public static String RECOMMENDATION_URL = "http://api.iciba.com/client/recommend/?os=android&versionid=1&xmlid={0}";
    public final static String PUSH_URL = "http://dict-mobile.iciba.com/msg/index.php";
    public final static String LOADING_AD_URL = "http://dict-mobile.iciba.com/new/index.php?mod=load";
    // versionid=1为不加市场版，为2为加入市场版
    
    public static String CONFIG_SETTING_LEARNINGREMIND_SWITCH = "CONFIG_SETTING_LEARNINGREMIND_SWITCH";
    
    public static String CONFIG_SETTING_LEARNINGREMIND_HOUR = "CONFIG_SETTING_LEARNINGREMIND_HOUR";
    
    public static String CONFIG_SETTING_LEARNINGREMIND_MINUTE = "CONFIG_SETTING_LEARNINGREMIND_MINUTE";
    
    public final static String NEW_PUSH_URL = "http://api.client.iciba.com/recommend/android/";
    public static String TRANSTION_KEY = "fdagdgdfu897hki&*&^ihh";
    public static String NET_PK = "^EnjoyMyLifeInIciba$";
    public static String CONFIG_NEWWORD_SAVE = "CONFIG_NEWWORD_SAVE";
    public static String CONFIG_NEWWORD_USERNAME = "CONFIG_NEWWORD_USERNAME";
    public static String CONFIG_NEWWORD_PASSWORD = "CONFIG_NEWWORD_PASSWORD";
    
    public static String CONFIG_SETTING_VOICE_INPUT_TYPE = "CONFIG_SETTING_VOICE_INPUT_TYPE";
    public static String CONFIG_SETTING_VOICE_INPUT_ENGLISH = "1";
    public static String CONFIG_SETTING_VOICE_INPUT_CHINESE = "2";
    public static String CONFIG_SETTING_VOICE_INPUT_WORD = "3";
    public static String CONFIG_SETTING_IS_AUTO_CONNECTION = "1"; // 总是自动联网
    public static String CONFIG_SETTING_IS_UNAUTO_CONNECTION = "0"; // 不自动联网, 手动联网
    public static String CONFIG_SETTING_IS_WIFI_CONNECTION = "2"; // Wifi下自动联网
    public static String CONFIG_SETTING_IS_USE_TTS = "1";
    public static String CONFIG_SETTING_IS_NOT_USE_TTS = "0";
    public static String CONFIG_FIRST = "CONFIG_FIRST";
    public static String CONFIG_IS_FIRST = "1";
    public static String CONFIG_NOT_FIRST = "2";
    public static String CONFIG_SHOW_HELP = "CONFIG_SHOW_HELP";
    public static String CONFIG_IS_SHOW_HELP = "1";
    public static String CONFIG_NOT_SHOW_HELP = "2";
    public static String CONFIG_DEFAULT_TL = "CONFIG_DEFAULT_TL";
    public final static String CONFIG_DEFAULT_TL_BASIC = "1";
    public final static String CONFIG_DEFAULT_TL_COLLINS = "2";
    public final static String CONFIG_DEFAULT_TL_WIKI = "3";
    public final static String CONFIG_DEFALUT_TL_EE = "4";
    
    public static int NEWWORD_MAX_SIZE = 200000;
    public static int HISTORY_MAX_SIZE = 100;
    public static String WIKI_DIC_NAME = "维基词典";
    public static String TONGFAN_DIC_NAME = "同反义词";
    public static String CIZU_DIC_NAME = "词组词典";
    public static String EC_DIC_NAME = "英汉词典";
    public static String CE_DIC_NAME = "汉英词典";
    public static String EE_DIC_NAME = "英英词典";
    public static String CY_DIC_NAME = "成语词典";
    public static String HY_DIC_NAME = "汉语词典1";
    public static String CC_DIC_NAME = "汉语词典";
    public static String CE_LJ_NAME = "汉英例句";
    public static String EC_LJ_NAME = "英汉例句";
    public static String EC_ZQ_NAME = "英汉词典增强版";
    public static String CE_ZQ_NAME = "汉英词典增强版";
    public static String EC_HH_NAME = "英汉词典豪华版";
    public static String CE_HH_NAME = "汉英词典豪华版";
    public static String CONFIG_HISTORY_MODE = "CONFIG_HISTORY_MODE";
    public static String CONFIG_NEWWORD_MODE = "CONFIG_NEWWORD_MODE";
    public static String CONFIG_USE_TTS = "CONFIG_USE_TTS";
    public static String CONFIG_DEFAULT_NEWWORD_BOOK = "CONFIG_DEFAULT_NEWWORD_BOOK";
    public static String CONFIG_DEFAULT_NEWWORD_ID = "CONFIG_DEFAULT_NEWWORD_ID";
    public static String CONFIG_DEFAULT_NEWWORD_PAGE = "CONFIG_DEFAULT_NEWWORD_PAGE";
    public static String ORDER_DATA = "ORDER_MODE";
    public static String ORDER_WORD = "ORDER_WORD";
	public final static String DIRNAME_DATA_CORE = "CoreData";
	public final static String FILENAME_NEWWORD = "NewWord";
	public final static String FILENAME_NEWNEWWORD = "NewNewWord";
	public final static String FILENAME_NEWS = "News";
	public final static String FILENAME_HISTORY = "History";
	
	public final static String SOFTWARE_VERSION_ID = "A500";
	

	public final static int MSG_ON_UPDATE_FAILURE = 0x601;      
	public final static int MSG_ON_UPDATE_SHOW_APK_EXIST = 0x602;
	public final static int MSG_ON_UPDATE_SHOW_PROGRESS = 0x603;
	public final static int MSG_ON_UPDATE_HIDE_PROGRESS = 0x604;
	public final static int MSG_ON_SHOW_AND_HIDE_AD = 0x553;
	public final static int MSG_ON_UPDATE_FAILURE_PROGRESS = 0x605;
	
	public final static int EN_TYPE = 31;
	public final static int EN_AM_TYPE = 32;
}
