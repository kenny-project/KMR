package com.kenny.event;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import android.util.Log;
import android.widget.Toast;

import com.kenny.struct.AbsEvent;
import com.kenny.util.LinuxFileCommand;

/**
 * @author kenny 初始化event
 * */
public class delFileEvent extends AbsEvent {
	private File mFolder;

	public delFileEvent(File mFolder) {
		super(null);
		this.mFolder = mFolder;
	}

	@Override
	public void ok() {
		deleteFolder(mFolder);
	}

	/** 删除文件夹的方法（删除该文件夹下的所有文件） */
	private void deleteFolder(final File folder) {

		if (!folder.canWrite()) {
			return;
		}
		try {
			LinuxFileCommand command = new LinuxFileCommand();
			Process deleProgress = command.deleteDirectory(folder.getPath());
			BufferedReader br = new BufferedReader(new InputStreamReader(
					deleProgress.getErrorStream()));
			int ret = 1;
			ret = deleProgress.waitFor();
			if (ret == 0) {
				Log.v("wmh", "delete Folder OK");
				return;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (folder.isFile()) {
			folder.delete();// 是文件则直接删除
			return;
		} else {
			File[] fileArray = folder.listFiles();
			if (fileArray.length == 0) { // 空文件夹则直接删除
				folder.delete();
			} else {
				for (File currentFile : fileArray) {// 遍历该目录
					if (currentFile.exists() && currentFile.isFile()) {// 文件则直接删除
						currentFile.delete();
					} else {
						deleteFolder(currentFile);// 回调
					}
				}
				folder.delete();
			}
		}
	}

}
