package com.kenny.file.Event;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.kenny.KFileManager.R;
import com.kenny.file.dialog.KDialog;
import com.kenny.file.tools.SaveData;
import com.kenny.file.tools.T;
import com.kenny.file.util.AppConst;
import com.kenny.file.util.Const;
import com.kenny.file.util.KCommand;
import com.kenny.file.util.NetConst;
import com.kenny.file.util.SDFile;
import com.kenny.file.util.Theme;
import com.umeng.analytics.MobclickAgent;

/**
 * @author aimery 初始化event
 * */
public class InitEvent extends AbsEvent
{
   private Activity ctx;
   public InitEvent(Activity m)
   {
      ctx = m;
   }
   
   
   public void ok()
   {
      int oldVersionCode = SaveData.Read(ctx, "versionCode", -1);// 显示主题
      int versionCode = T.GetVersionCode(ctx);
      if (versionCode != oldVersionCode)
      {
         SaveData.Write(ctx, "versionCode", versionCode);// 赋值初始化
         RestorationInit(ctx);
      }
      P.Init(ctx);
      NetConst.SetContext(ctx);
      AppConst.Init(ctx);
      Theme.Init(ctx);
      //1 隐藏文件
      ShowCommentScore();
   }
   // 恢复初始化/系统初始化
   public static boolean RestorationInit(Context context)
   {
      SaveData.Write(context,Const.strShowHideFile, true);//屏幕自动切换
      SaveData.Write(context,Const.strSensorEnable, false);//屏幕自动切换
      SaveData.Write(context,Const.strToolsVisible, true);//显示工具栏
      SaveData.Write(context, Const.strTabVisible, true);//显示TAB栏
      String FileName = context.getString(R.string.FavoriteType);
      if (!SDFile.CheckRAMFile(context, FileName))
      {
	T.ResourceAssetsFile(context, FileName);
      }
      return true;
   }
   public void ShowCommentScore()
   {
      boolean bCommentScore = SaveData.Read(ctx,
	  "CommentScore",false); // 评分
      if (!bCommentScore)
      {
         int nCommentScore = SaveData.Read(ctx,
	     "CommentScorePos", 0); // 评分
         nCommentScore++;
         //nCommentScore=11;//test by wmh
         if (nCommentScore > 30)
         {
	  if (KCommand.isNetConnectNoMsg(ctx))
	  {
	     SaveData.Write(ctx, "CommentScorePos", 3); // 评分
	     KDialog
		 .ShowDialog(
		       ctx,
		      ctx.getString(R.string.app_name),
		       ctx.getString(R.string.commentContent),
		       ctx.getString(R.string.msg_init_CommentScore), clPositive, ctx.getString(R.string.msg_init_CommentScore_button2), clNegative);
	  }
         }
         else
         {
	  SaveData.Write(ctx, "CommentScorePos", nCommentScore); // 评分
         }
      }
   }
   OnClickListener clPositive = new OnClickListener()
   {
      
      
      public void onClick(DialogInterface dialog, int which)
      {
         SaveData.Write(ctx, "CommentScorePos", 0); // 评分
         SaveData.Write(ctx, "CommentScore", true); // 评分
         T.DetailsIntent(ctx);
         MobclickAgent.onEvent(ctx, "CommentScore","yes");
      }
   };
   OnClickListener clNegative = new OnClickListener()
   {
      
      
      public void onClick(DialogInterface dialog, int which)
      {
         // TODO Auto-generated method stub
         SaveData.Write(ctx, "CommentScorePos", 0); // 评分
         MobclickAgent.onEvent(ctx, "CommentScore","no");
      }
   };
}
