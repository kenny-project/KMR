package com.kenny.file.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.kenny.KFileManager.R;
import com.kenny.file.commui.KString;

/**
 * 文件对话框菜单
 * 
 * @author WangMinghui
 * 
 */
public abstract class PopMenu
{
   /**
    * 创建文件夹的方法:当用户点击软件下面的创建菜单的时候，是在当前目录下创建的一个文件夹 静态变量mCurrentFilePath存储的就是当前路径
    * java.io.File.separator是JAVA给我们提供的一个File类中的静态成员，它会根据系统的不同来创建分隔符
    * mNewFolderName正是我们要创建的新文件的名称，从EditText组件上得到的
    */
//   public static void ShowList(final Context context, String Title,
//         final String mCurrentFilePath)
//   {
//      // mChecked = 1;
//      LayoutInflater mLI = (LayoutInflater) context
//	  .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//      final LinearLayout mLL = (LinearLayout) mLI.inflate(
//	  R.layout.alert_dialog_list, null);
//      
//      ListView lvList = (ListView) mLL.findViewById(R.id.lvList);
//      
//      Builder mBuilder = new AlertDialog.Builder(context).setTitle(Title)
//	  .setView(mLL)
//	  .setPositiveButton(context.getString(R.string.cancel), null);
//      mBuilder.show();
//   }
//   
   private KString[] mMenu;
   
   /**
    * 长按文件或文件夹时弹出的带ListView效果的功能菜单
    * -1:全部类型
    * case 0:// 文件夹 
    * return CreateFolderMenu(); 
    * case 1:// 音频 return
    * CreateMusicMenu(); 
    * case 2:// 视频 
    * return CreateVieoMenu(); 
    * case 3:// 图片
    * return CreatePictureMenu(); 
    * case 255: // 收藏夹里面的文件类型
    * */
   public void ShowFile(final Context context, int fileType, String Title)
   {
      mMenu = CreateMenu(fileType);
      // String[] mMenu =
      // { "打开", "复制", "重命名", "删除", "发送", "收藏", "压缩", "属性" };// "压缩" ,
      new AlertDialog.Builder(context).setTitle(Title)
	  // "请选择操作!"
	  .setItems(mMenu, listener)
	  .setPositiveButton(context.getString(R.string.cancel), null).show();
   }
   
   protected KString[] CreateMenu(int type)
   {
      switch (type)
      {
      case 0:// 文件夹
         return CreateFolderMenu();
      case 1:// 音频
         return CreateMusicMenu();
      case 2:// 视频
         return CreateVieoMenu();
      case 3:// 图片
         return CreatePictureMenu();
      case 255: // 收藏夹里面的文件类型
         return CreateFavorMenu();
      default:
         return CreateOtherMenu();
      }
   }
   
   protected abstract void OnItemClickListener(int pos, KString key);
   
   DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
   {
      public void onClick(DialogInterface dialog, int item)
      {
         dialog.cancel();
         if (mMenu.length > item)
         {
	  OnItemClickListener(item, mMenu[item]);
         }
      }
   };
   private List<KString> mAllMenu = null;
   private List<KString> mFolderMenu = null;
   private List<KString> mOtherFileMenu = null;
   private List<KString> mFavorMenu = null;
   private List<KString> mPictureFileMenu = null;
   private List<KString> mMusicFileMenu = null;
   
   private void InitMenu()
   {
      String[] mStrMenu =
      { "打开", "复制", "重命名", "删除", "收藏夹", "压缩", "属性", "发送", "打开所在文件夹", "设置成铃音",
	  "设置成壁纸","上传到网盘","剪切" };
      mAllMenu = new ArrayList<KString>();
      for (int i = 0; i < mStrMenu.length; i++)
      {
         mAllMenu.add(new KString(i, mStrMenu[i]));
      }
   }
   
   public static final int open = 0;
   public static final int copy = 1;
   public static final int rename = 2;
   public static final int delete = 3;
   public static final int favor = 4;
   public static final int zip = 5;
   public static final int attribute = 6;
   public static final int send = 7;
   public static final int openfolder = 8;//打开所在文件夹
   public static final int setring = 9;//设置成铃音
   public static final int setdesk = 10;//设置桌面
   public static final int uploadcloud = 11;//上传云端
   public static final int cut = 12;//剪切
   private KString[] CreateFolderMenu()
   {
      
      if (mFolderMenu == null)
      {
         if (mAllMenu == null)
         {
	  InitMenu();
         }
         mFolderMenu = new Vector<KString>();
         // mFolderMenu.add(mAllMenu.get(open));
         mFolderMenu.add(mAllMenu.get(copy));
         mFolderMenu.add(mAllMenu.get(cut));
         mFolderMenu.add(mAllMenu.get(rename));
         mFolderMenu.add(mAllMenu.get(delete));
//  	 mFolderMenu.add(mAllMenu.get(uploadcloud));
         mFolderMenu.add(mAllMenu.get(favor));
         mFolderMenu.add(mAllMenu.get(zip));
         mFolderMenu.add(mAllMenu.get(attribute));
      }
      return (KString[]) mFolderMenu.toArray(new KString[0]);
   }
   
   private KString[] CreateMusicMenu()
   {
      if (mMusicFileMenu == null)
      {
         if (mAllMenu == null)
         {
	  InitMenu();
         }
         mMusicFileMenu = new ArrayList<KString>();
         mMusicFileMenu.add(mAllMenu.get(open));
         mMusicFileMenu.add(mAllMenu.get(copy));
         mMusicFileMenu.add(mAllMenu.get(cut));
         mMusicFileMenu.add(mAllMenu.get(rename));
         mMusicFileMenu.add(mAllMenu.get(delete));
         mMusicFileMenu.add(mAllMenu.get(send));
//         mMusicFileMenu.add(mAllMenu.get(uploadcloud));
         mMusicFileMenu.add(mAllMenu.get(setring));
         mMusicFileMenu.add(mAllMenu.get(favor));
         mMusicFileMenu.add(mAllMenu.get(zip));
         mMusicFileMenu.add(mAllMenu.get(attribute));
      }
      return (KString[]) mMusicFileMenu.toArray(new KString[0]);
   }
   
   private KString[] CreateVieoMenu()
   {
      return CreateOtherMenu();
   }
   
   private KString[] CreatePictureMenu()
   {
      if (mPictureFileMenu == null)
      {
         if (mAllMenu == null)
         {
	  InitMenu();
         }
         mPictureFileMenu = new ArrayList<KString>();
         mPictureFileMenu.add(mAllMenu.get(open));
         mPictureFileMenu.add(mAllMenu.get(copy));
         mPictureFileMenu.add(mAllMenu.get(cut));
         mPictureFileMenu.add(mAllMenu.get(rename));
         mPictureFileMenu.add(mAllMenu.get(delete));
         mPictureFileMenu.add(mAllMenu.get(send));
//         mPictureFileMenu.add(mAllMenu.get(uploadcloud));
         //mPictureFileMenu.add(mAllMenu.get(setdesk));
         mPictureFileMenu.add(mAllMenu.get(favor));
         mPictureFileMenu.add(mAllMenu.get(zip));
         mPictureFileMenu.add(mAllMenu.get(attribute));
      }
      return (KString[]) mPictureFileMenu.toArray(new KString[0]);
   }
   
   private KString[] CreateOtherMenu()
   {
      if (mOtherFileMenu == null)
      {
         if (mAllMenu == null)
         {
	  InitMenu();
         }
         mOtherFileMenu = new ArrayList<KString>();
         mOtherFileMenu.add(mAllMenu.get(open));
         mOtherFileMenu.add(mAllMenu.get(copy));
         mOtherFileMenu.add(mAllMenu.get(cut));
         mOtherFileMenu.add(mAllMenu.get(rename));
         mOtherFileMenu.add(mAllMenu.get(delete));
         mOtherFileMenu.add(mAllMenu.get(send));
//         mOtherFileMenu.add(mAllMenu.get(uploadcloud));
         mOtherFileMenu.add(mAllMenu.get(favor));
         mOtherFileMenu.add(mAllMenu.get(zip));
         mOtherFileMenu.add(mAllMenu.get(attribute));
      }
      return (KString[]) mOtherFileMenu.toArray(new KString[0]);
   }
   
   private KString[] CreateFavorMenu()
   {
      if (mFavorMenu == null)
      {
         if (mAllMenu == null)
         {
	  InitMenu();
         }
         mFavorMenu = new ArrayList<KString>();
         mFavorMenu.add(mAllMenu.get(open));
         mFavorMenu.add(mAllMenu.get(openfolder));
         mFavorMenu.add(mAllMenu.get(rename));
         mFavorMenu.add(mAllMenu.get(delete));
         mFavorMenu.add(mAllMenu.get(send));
//         mFavorMenu.add(mAllMenu.get(uploadcloud));
         mFavorMenu.add(mAllMenu.get(favor));
         mFavorMenu.add(mAllMenu.get(zip));
         mFavorMenu.add(mAllMenu.get(attribute));
      }
      return (KString[]) mFavorMenu.toArray(new KString[0]);
   }
   
}
