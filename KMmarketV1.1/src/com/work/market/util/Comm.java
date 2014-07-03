package com.work.market.util;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;

import com.work.market.bean.AppListBean;
import com.work.market.db.DBAdapter;
import com.work.market.db.DBdatafinishModel;
import com.work.market.net.Common;
import com.work.market.server.DownLoadService;

public class Comm 
{

	public static void CreatPath() {
		if (Common.sdCardCheck()) {
			String status = Environment.getExternalStorageState();
			if (status.equals(Environment.MEDIA_MOUNTED)) {
				String path = Environment.getExternalStorageDirectory()
						+ "/baifen";
				File file = new File(path);
				if (!file.exists()) {
					file.mkdir();
				}
				//
				// baifen/dowsload
				// baifen/img
				// baifen/temp

				File file1 = new File(path + "/dowsload");
				if (!file1.exists()) {
					file1.mkdir();
				}
				File file2 = new File(path + "/img");
				if (file2.exists()) {
					file2.mkdir();
				}
			}
		}
	}


}
