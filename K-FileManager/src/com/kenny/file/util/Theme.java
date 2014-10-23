package com.kenny.file.util;

import android.content.Context;
import android.graphics.Color;

import com.kenny.KFileManager.R;
import com.kenny.file.tools.SaveData;

public class Theme
{
   private static String StyleType = "styleType";
   private static int ThemeMode;// 应用程序主题
   private static int BGSelectedResID;
   /**
    * 获得背景色值
    */
   private static int BGResID = Color.WHITE;
   /**
    * 获得文字色值
    */
   private static int TextColor = 0xff000000;
   /**
    * 文字字体大小
    */
   private static int TextFontSize;
   
   private static int nStyleType = 0;// 0:list 1:grid 
   private static int nSortMode = 10;// 0:list 1:grid  大于等于10 正序 小于10为倒序 
   /**
    * 显示工具栏,TAB栏
    */
   private static boolean bToolsVisible;
   private static boolean bTabsVisible;
   
   public static boolean getTabsVisible()
   {
      return bTabsVisible;
   }
   
   public static void setTabsVisible(boolean bTabsVisible)
   {
      Theme.bTabsVisible = bTabsVisible;
   }
   
   public static boolean getToolsVisible()
   {
      return bToolsVisible;
   }
   
   public static void setToolsVisible(boolean bToolsVisible)
   {
      Theme.bToolsVisible = bToolsVisible;
   }
   

   
   public static int getSortMode()
   {
      return nSortMode;
   }
   
   public static void setSortMode(int sortMode)
   {
      nSortMode = sortMode;
   }
   
   // 用户自己定义的分类内容
   public static int getStyleMode()
   {
      return nStyleType;
   }
 /**
  * 
  * 1:Grid: 2:List
  */
   public static void setStyleMode(int styleType)
   {
      nStyleType = styleType;
   }
   
   public static int getTextFontSize()
   {
      return TextFontSize;
   }
   
   public static void setTextFontSize(int textFontSize)
   {
      TextFontSize = textFontSize;
   }
   
   /**
    * 获得文字色值
    */
   public static int getTextColor()
   {
      return TextColor;
   }
   /**
    * 获得文字色值
    */
   public static int getTextColor(int ThemeMode)
   {
	   int TextColor;
	      switch (ThemeMode)
	      {
	      case 0:// 默认
	    	 TextColor=R.color._0TextColor;
	         break;
	      case 1:// 黑色
	     	 TextColor=R.color._1TextColor;
	         break;
	      case 2:// 纸浆色
	     	 TextColor=R.color._2TextColor;
	         break;
	      case 3:// 豆沙绿
	     	 TextColor=R.color._3TextColor;
	         break;
	      case 4:// 粉红色
	     	 TextColor=R.color._4TextColor;
	         break;
	      case 5:// 黄色
	     	 TextColor=R.color._5TextColor;
	         break;
	      case 6:// 蓝色
	     	 TextColor=R.color._6TextColor;
	         break;
	      case 7:// 米黄色
	     	 TextColor=R.color._7TextColor;
	         break;
	      default:
	     	 TextColor=R.color._0TextColor;
	         break;
	      }
	      TextColor=m_ctx.getResources().getColor(TextColor);
      return TextColor;
   }
   /**
    * 获得背景色值
    */
   public static int getBackgroundResource()
   {
      return BGResID;
   }
   
   /**
    * 获得列表选中后的背影色
    */
   public static int getSelBackgroundResource()
   {
      return BGSelectedResID;
   }
   
   public static int getThemeMode()
   {
      return ThemeMode;
   }
   
   private static boolean bTaskVisible = false;
   
   public static void setTaskVisible(boolean value)
   {
      bTaskVisible = value;
   }
   
   public static boolean getTaskVisible()
   {
      return bTaskVisible;
   }
   
   public static void setThemeMode(int themeMode)
   {
      ThemeMode = themeMode;
      ThemeMode(ThemeMode);
   }
   
   private static Context m_ctx;
   /**
    * 初始化
    * 
    * @param inActivity
    */
   public static void setContext(Context inActivity)
   {
	   m_ctx=inActivity.getApplicationContext();
   }
   /**
    * 初始化
    * 
    * @param inActivity
    */
   public static void Init(Context inActivity)
   {
	   m_ctx=inActivity.getApplicationContext();
      TextFontSize = SaveData.Read(inActivity, "HCFontSize", 21);
      setThemeMode(SaveData.Read(inActivity, "ThemeMode", 0));// 显示主题
      setStyleMode(SaveData.Read(inActivity, StyleType, 0));
      setSortMode(SaveData.Read(inActivity, "sortMode", 10));
      setTaskVisible(SaveData.Read(inActivity, "TaskVisible", false));
      setToolsVisible(SaveData.Read(inActivity, Const.strToolsVisible, true));
      setTabsVisible(SaveData.Read(inActivity, Const.strTabVisible, true));
      setSensorOrientation(SaveData.Read(inActivity, Const.strSensorEnable, false));// 输入自动化
      setShowHideFile(SaveData.Read(inActivity, Const.strShowHideFile, false)); // 显示隐含文件
   }
   
   private static boolean SensorEnable;
   private static boolean ShowHideFile;
   public static boolean getShowHideFile()
   {
      return ShowHideFile;
   }

   public static void setShowHideFile(boolean showHideFile)
   {
      ShowHideFile = showHideFile;
   }

   public static boolean getScreenOrientation()
   {
      return SensorEnable;
   }
   
   public static void setSensorOrientation(boolean sensorEnable)
   {
      SensorEnable = sensorEnable;
   }
   
   public static void Save(Context inActivity)
   {
      SaveData.Write(inActivity, StyleType, getStyleMode());//格式
      SaveData.Write(inActivity, "sortMode", getSortMode());//排序
      SaveData.Write(inActivity, "TaskVisible", getTaskVisible());//任务管理器
      SaveData.Write(inActivity, Const.strSensorEnable, getScreenOrientation());//旋转
      SaveData.Write(inActivity, Const.strTabVisible, getTabsVisible());
      SaveData.Write(inActivity, Const.strShowHideFile, getShowHideFile());//旋转
      SaveData.Write(inActivity, Const.strToolsVisible, getToolsVisible());//工具栏
   }
   
   // 主题模式切换
   private static void ThemeMode(int ThemeMode)
   {
//      BackgroundColor = Color.WHITE;
//      TextColor = 0xff000000;
      switch (ThemeMode)
      {
      case 0:// 默认
    	 TextColor=R.color._0TextColor;
         BGResID = R.color._0BackgroundColor;
         BGSelectedResID=R.color._0SelBackgroundColor;
         break;
      case 1:// 黑色
     	 TextColor=R.color._1TextColor;
         BGResID = R.color._1BackgroundColor;
         BGSelectedResID=R.color._1SelBackgroundColor;
         //@drawable/abs__item_background_holo_dark
         //nListSelectedBackgroundColor=R.drawable.abs__item_background_holo_dark;
         break;
      case 2:// 纸浆色
//         BackgroundColor = 0xffe7dfce;
//         SelBackgroundColor=0xffc6dfc6;
     	 TextColor=R.color._2TextColor;
         BGResID = R.color._2BackgroundColor;
         BGSelectedResID=R.color._2SelBackgroundColor;

         break;
      case 3:// 豆沙绿
//         BackgroundColor = 0xffc6dfc6;
//         SelBackgroundColor=0xffe7dfce;
     	 TextColor=R.color._3TextColor;
         BGResID = R.color._3BackgroundColor;
         BGSelectedResID=R.color._3SelBackgroundColor;

         break;
      case 4:// 粉红色
//         BackgroundColor = 0xffefc7c6;
//         SelBackgroundColor=0xffc6dfc6;
     	 TextColor=R.color._4TextColor;
         BGResID = R.color._4BackgroundColor;
         BGSelectedResID=R.color._4SelBackgroundColor;

         break;
      case 5:// 黄色
//         BackgroundColor = 0xffefd79c;
//         SelBackgroundColor=0xffc6dfc6;
     	 TextColor=R.color._5TextColor;
         BGResID = R.color._5BackgroundColor;
         BGSelectedResID=R.color._5SelBackgroundColor;

         break;
      case 6:// 蓝色
//         BackgroundColor = 0xffc6cfe7;
//         SelBackgroundColor=0xffc6dfc6;
     	 TextColor=R.color._6TextColor;
         BGResID = R.color._6BackgroundColor;
         BGSelectedResID=R.color._6SelBackgroundColor;

         break;
      case 7:// 米黄色
//         BackgroundColor = 0xffe7ebd6;
//         SelBackgroundColor=0xffc6dfc6;
     	 TextColor=R.color._7TextColor;
         BGResID = R.color._7BackgroundColor;
         BGSelectedResID=R.color._7SelBackgroundColor;

         break;
      default:
//         BackgroundColor = 0xfff0f0f0;
//         SelBackgroundColor=0xffc6dfc6;
     	 TextColor=R.color._0TextColor;
         BGResID = R.color._0BackgroundColor;
         BGSelectedResID=R.color._0SelBackgroundColor;
         break;
      }
      TextColor=m_ctx.getResources().getColor(TextColor);
   }
}
