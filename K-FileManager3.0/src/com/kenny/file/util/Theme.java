package com.kenny.file.util;

import com.kenny.KFileManager.R;
import com.kenny.file.tools.SaveData;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

public class Theme
{
   private static String StyleType = "styleType";
   private static int ThemeMode;// 应用程序主题
   private static int nListSelectedBackgroundColor;
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
   
   /**
    * 获得背景色值
    */
   private static int BackgroundColor = Color.WHITE;
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
    * 获得背景色值
    */
   public static int getBackgroundColor()
   {
      return BackgroundColor;
   }
   
   /**
    * 获得列表选中后的背影色
    */
   public static int getListSelectedBackgroundColor()
   {
      return nListSelectedBackgroundColor;
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
   
   /**
    * 初始化
    * 
    * @param inActivity
    */
   public static void Init(Activity inActivity)
   {
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
      BackgroundColor = Color.WHITE;
      TextColor = 0xff000000;
      switch (ThemeMode)
      {
      case 0:// 默认
         BackgroundColor = 0xffe7f3f7;
         nListSelectedBackgroundColor=0xffc6dfc6;
         break;
      case 1:// 黑色
         TextColor = 0xffc6c3c6;
         BackgroundColor = Color.BLACK;
         
//         @drawable/abs__item_background_holo_dark
         //nListSelectedBackgroundColor=R.drawable.abs__item_background_holo_dark;
         break;
      case 2:// 纸浆色
         BackgroundColor = 0xffe7dfce;
         nListSelectedBackgroundColor=0xffc6dfc6;
         break;
      case 3:// 豆沙绿
         BackgroundColor = 0xffc6dfc6;
         nListSelectedBackgroundColor=0xffe7dfce;
         break;
      case 4:// 粉红色
         BackgroundColor = 0xffefc7c6;
         nListSelectedBackgroundColor=0xffc6dfc6;
         break;
      case 5:// 黄色
         BackgroundColor = 0xffefd79c;
         nListSelectedBackgroundColor=0xffc6dfc6;
         break;
      case 6:// 蓝色
         BackgroundColor = 0xffc6cfe7;
         nListSelectedBackgroundColor=0xffc6dfc6;
         break;
      case 7:// 米黄色
         BackgroundColor = 0xffe7ebd6;
         nListSelectedBackgroundColor=0xffc6dfc6;
         break;
      default:
         BackgroundColor = 0xfff0f0f0;
         nListSelectedBackgroundColor=0xffc6dfc6;
         break;
      }
     // nListSelectedBackgroundColor=R.drawable.abs__item_background_holo_dark;
   }
}
