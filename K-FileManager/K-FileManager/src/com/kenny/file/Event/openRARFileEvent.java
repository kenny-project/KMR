package com.kenny.file.Event;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.framework.event.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.RAR.UnRAR;
import com.kenny.file.util.Const;

/**
 * @author kenny 初始化event
 * */
public class openRARFileEvent extends openDefFileEvent {
	private Activity act;
	private String nameContains;
	private String zipFile;
	private static final String ZipTmpPath=Const.szZipPath;
	public openRARFileEvent(Activity act, String zipFile, 
			String nameContains) {
		super(act, ZipTmpPath + nameContains);
		
		this.act = act;
		this.nameContains = nameContains;
		this.zipFile = zipFile;
		Log.v("wmh", ZipTmpPath + nameContains);
	}

	private ProgressDialog myDialog = null;

	@Override
	public void ok() {
		SysEng.getInstance().addHandlerEvent(new AbsEvent() {
			@Override
			public void ok() {
				myDialog = ProgressDialog
						.show(act, "", "正在解压数据...", true, true);
				myDialog.show();
			}
		});
		try {
			boolean result = UnRAR.UpSelectedFile(zipFile, ZipTmpPath,
					nameContains);
			if (result) {
				super.ok();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SysEng.getInstance().addHandlerEvent(new AbsEvent() {
			@Override
			public void ok() {
				if (myDialog != null)
					myDialog.dismiss();

			}
		});
	}
}
