package com.kenny.file.Event;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.kenny.KImageBrowser.GalleryActivity;
import com.kenny.LyricPlayer.xwg.MediaPlayActivity;
import com.kenny.RAR.RARFileActivity;
import com.kenny.Zip.ZIPFileActivity;
import com.kenny.file.Activity.EditTxtActivity;
import com.kenny.file.util.Config;

/**
 * @author aimery 初始化event
 * */
public class openDefFileEvent extends openFileEvent
{
	public openDefFileEvent(Context m_act, String path)
	{
		super(m_act, path);
	}
	@Override
	public void ok()
	{
		if (fileEnds.equals("txt") || fileEnds.equals("ini")
				|| fileEnds.equals("xml") || fileEnds.equals("html")
				|| fileEnds.equals("mht") || fileEnds.equals("htm")
				// || fileEnds.equals("doc")
				|| fileEnds.equals("xml") || fileEnds.equals("log"))// 默认执行
		{ // 打开网页
			if (Config.isOpenDefTxtFile())
			{
				Intent intent = new Intent(m_act, EditTxtActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setData(Uri.fromFile(file));
				m_act.startActivity(intent);
			} else
			{
				super.ok();
			}
		} else if (fileEnds.equals("m4a") || fileEnds.equals("mp3")
				|| fileEnds.equals("mid") || fileEnds.equals("xmf")
				|| fileEnds.equals("ogg") || fileEnds.equals("wav")
				|| fileEnds.equals("3gp"))
		{
			if (Config.isOpenDefAudioFile())
			{
				Intent intent = new Intent(m_act, MediaPlayActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setData(Uri.fromFile(file));
				m_act.startActivity(intent);
			} else
			{
				super.ok();
			}
		} else if (fileEnds.equals("zip"))
		{
			if (Config.isbOpenDefZipFile())
			{
				Intent intent = new Intent(m_act, ZIPFileActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setData(Uri.fromFile(file));
				m_act.startActivity(intent);
			} else
			{
				super.ok();
			}
		}else if (fileEnds.equals("rar"))
		{
			if (Config.isbOpenDefZipFile())
			{
				Intent intent = new Intent(m_act, RARFileActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setData(Uri.fromFile(file));
				m_act.startActivity(intent);
			} else
			{
				super.ok();
			}
		} else if (fileEnds.equals("jpg") || fileEnds.equals("gif")
				|| fileEnds.equals("png") || fileEnds.equals("jpeg")
				|| fileEnds.equals("bmp"))
		{
			if (Config.isOpenDefPicFile())
			{
				Intent intent = new Intent(m_act, GalleryActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setData(Uri.fromFile(file));
				m_act.startActivity(intent);
//				Intent intent = new Intent();
//				intent.setClassName("com.kenny.imgviewer", "com.kenny.Image.ImageGalleryPagerActivity");
//				intent.setData(Uri.fromFile(file));
//				m_act.startActivity(intent);
			} else
			{
				super.ok();
			}
		} else
		{ // 调用系统
			super.ok();
		}
	}
}
