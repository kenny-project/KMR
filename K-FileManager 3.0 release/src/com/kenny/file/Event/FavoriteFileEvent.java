package com.kenny.file.Event;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;
import com.framework.event.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.bean.FileBean;
import com.kenny.file.db.Dao;
import com.kenny.file.util.FileManager;

/**
 * @author kenny 初始化event
 * */
public class FavoriteFileEvent extends AbsEvent {
	private List<FileBean> list;
	private Context m_context;
	private ProgressDialog mProgressDialog;
	private boolean mProgress = false;
	private int flag = 0;// 0:收藏 1:历史

	public FavoriteFileEvent(Context context, ArrayList<FileBean> list) {
		this.list = list;
		this.m_context = context;
		flag = 0;
		ShowDialog(list);
	}

	public FavoriteFileEvent(Context context, FileBean fileBean, int flag) {
		this.flag = flag;
		this.list = new ArrayList<FileBean>();
		list.add(fileBean);
		this.m_context = context;
		mProgress = true;
		if (flag == 0) {
			ShowDialog(list);
		}
	}

	@Override
	public void ok() {
		Favorite(list);

		if (flag == 0) {
			SysEng.getInstance().addHandlerEvent(new AbsEvent()

			{
				@Override
				public void ok() {
					FileManager.GetHandler().Refresh();
					mProgressDialog.dismiss();
					Toast.makeText(m_context, "添加收藏成功!", Toast.LENGTH_SHORT)
							.show();
				}
			});
		}
	}

	private void ShowDialog(List<FileBean> mFileList) {
		mProgress = true;
		mProgressDialog = new ProgressDialog(m_context);
		mProgressDialog.setTitle("正在处理:");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(mFileList.size());
		mProgressDialog.setButton(m_context.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mProgress = false;
					}
				});
		mProgressDialog.setProgress(0);
		mProgressDialog.show();
	}

	/** 删除文件夹的方法（删除该文件夹下的所有文件） */
	private void Favorite(List<FileBean> list) {
		Dao dao = Dao.getInstance(m_context);
		try {
			for (int i = 0; i < list.size() && mProgress; i++) {
				File temp = list.get(i).getFile();
				// dao.InsertFavorites(flag, temp.getPath(), temp.length());
				dao.InsertHistory(flag, temp.getPath(), temp.length());
				if (mProgressDialog != null) {
					mProgressDialog.incrementProgressBy(1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		dao.closeDb();
	}

}
