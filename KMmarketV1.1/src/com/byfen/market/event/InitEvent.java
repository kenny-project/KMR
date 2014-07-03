package com.byfen.market.event;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.app.Activity;

import com.framework.log.P;
import com.framework.syseng.AbsEvent;
import com.work.market.util.CONST;
import com.work.market.util.LinuxFileCommand;
import com.work.market.util.SaveData;
import com.work.market.util.T;

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
		if (7> oldVersionCode)
		{
			SaveData.Write(ctx, "versionCode", versionCode);// 赋值初始化
			LinuxFileCommand command = new LinuxFileCommand();
			try
			{
				Process deleProgress = command.delete(CONST.DownLoad);

				BufferedReader br = new BufferedReader(new InputStreamReader(
						deleProgress.getErrorStream()));
				int ret = deleProgress.waitFor();
				if (ret != 0)
				{
					final String errorMsg = br.readLine();
					P.d(tag, "Error(code = " + ret + "): " + errorMsg);

				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		P.Init(ctx);
		// 1 隐藏文件
	}

}
