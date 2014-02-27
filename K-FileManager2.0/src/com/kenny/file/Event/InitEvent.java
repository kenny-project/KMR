package com.kenny.file.Event;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.AssetManager;
import android.util.Log;

import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.kenny.KFileManager.t.R;
import com.kenny.file.dialog.KDialog;
import com.kenny.file.dialog.SetSDCardRootPathDialog;
import com.kenny.file.tools.SaveData;
import com.kenny.file.tools.T;
import com.kenny.file.util.Config;
import com.kenny.file.util.Const;
import com.kenny.file.util.KCommand;
import com.kenny.file.util.NetConst;
import com.kenny.file.util.Res;
import com.kenny.file.util.SDFile;
import com.kenny.file.util.Theme;
import com.umeng.analytics.MobclickAgent;

/**
 * @author wangminghui 初始化event
 * */
public class InitEvent extends AbsEvent
{
	private Activity ctx;

	public InitEvent(Activity mCtx)
	{
		ctx = mCtx;
	}

	public void ok()
	{
		int oldVersionCode = SaveData.Read(ctx, "versionCode", -1);// 显示主题
		int versionCode = T.GetVersionCode(ctx);
		if (versionCode != oldVersionCode)
		{
			SaveData.Write(ctx, "versionCode", versionCode);// 赋值初始化
			RestorationInit(ctx);
			Res.getInstance(ctx).setFirstRun(true);
		}
		else
		{
			Res.getInstance(ctx).setFirstRun(false);
		}
		String SDRoot = android.os.Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		String path = SaveData.Read(ctx, Const.strDefaultPath, "");
		if(path.length()<=0)
		{
			SaveData.Write(ctx, Const.strDefaultPath, SDRoot);
		}

		String mStrSDRootPath = SaveData.Read(ctx, Const.strSDRootPath, "");
		if(mStrSDRootPath.length()<=0)
		{
			File mFile = new File("/mnt/");
			File[] mFiles = mFile.listFiles();// 遍历出该文件夹路径下的所有文件/文件夹
			List<File> Roots=new ArrayList<File>();
			for(int i=0;i<mFiles.length;i++)
			{
				if(mFiles[i].canWrite())
				{
					Roots.add(mFiles[i]);
					Log.d("wmh", mFiles[i].getAbsolutePath());
				}
			}
			if(Roots.size()>1)
			{
				new SetSDCardRootPathDialog().ShowDialog(ctx, Roots);
			}
			Const.setSDCard(SDRoot);	
		}
		else
		{
			Const.setSDCard(mStrSDRootPath);
		}
		P.Init(ctx);
		NetConst.SetContext(ctx);
		Theme.Init(ctx);
		Config.Init(ctx);
	}

	// 恢复初始化/系统初始化
	private static boolean RestorationInit(Context context)
	{
		Theme.setSensorOrientation(false);
		Theme.setToolsVisible(true);
		Theme.setTaskVisible(true);
		Theme.setTabsVisible(true);
		Theme.setShowHideFile(true);
		Theme.setThemeMode(0);// 显示主题
		Theme.setStyleMode(0);
		Theme.setSortMode(10);
		Theme.Save(context);
		AssetManager assetManager = context.getAssets();
		String[] files = null;
		try
		{
			files = assetManager.list("filetype");
			if (files != null)
			{
				for (String file : files)
				{
					T.ResourceAssetsFile(context, "filetype/", file);
				}
			}
		} catch (IOException e)
		{
			P.e("tag", e.getMessage());
		}
		T.ResourceAssetsFile(context, "fileType.xml");
		String FileName = context.getString(R.string.FavoriteType);
		if (!SDFile.CheckRAMFile(context, FileName))
		{
			T.ResourceAssetsFile(context, FileName);
		}
		return true;
	}

	private void ShowCommentScore()
	{
		boolean bCommentScore = SaveData.Read(ctx, "CommentScore", false); // 评分
		if (!bCommentScore)
		{
			int nCommentScore = SaveData.Read(ctx, "CommentScorePos", 0); // 评分
			nCommentScore++;
			// nCommentScore=11;//test by wmh
			if (nCommentScore > 100)
			{
				if (KCommand.isNetConnectNoMsg(ctx))
				{
					SaveData.Write(ctx, "CommentScorePos", 3); // 评分
					KDialog.ShowDialog(
							ctx,
							ctx.getString(R.string.app_name),
							ctx.getString(R.string.commentContent),
							ctx.getString(R.string.msg_init_CommentScore),
							clPositive,
							ctx.getString(R.string.msg_init_CommentScore_button2),
							clNegative);
				}
			} else
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
			MobclickAgent.onEvent(ctx, "CommentScore", "yes");
		}
	};
	OnClickListener clNegative = new OnClickListener()
	{

		public void onClick(DialogInterface dialog, int which)
		{
			// TODO Auto-generated method stub
			SaveData.Write(ctx, "CommentScorePos", 0); // 评分
			MobclickAgent.onEvent(ctx, "CommentScore", "no");
		}
	};
}
