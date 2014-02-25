package com.kenny.file.Event;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.bean.FileBean;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.tools.FileOperation;
import com.kenny.file.tools.LinuxFileCommand;
import com.kenny.file.util.Const;
import com.kenny.file.util.SDFile;

public class cutFileCmdEvent extends AbsEvent {
	private Context mContext;
	private ProgressDialog mProgressDialog;
	private boolean mProgress = false;
	private List<FileBean> mFileList; // 内存里面的数据
	private String mCurrentPath;// 需要粘贴的位置
	private boolean bflag = false;// 标记是否全部粘贴
	private int nDialogResult = 1;
	private INotifyDataSetChanged notif = null;
	
	public cutFileCmdEvent(Context context, String mCurrentPath,
			List<FileBean> mFileList, INotifyDataSetChanged notif)// bCut:true:剪切:
	{
		mContext = context;
		this.notif = notif;
		this.mCurrentPath = mCurrentPath;
		this.mFileList = mFileList;
		ShowDialog(mFileList.size());
	}

	@SuppressWarnings("deprecation")
	private void ShowDialog(int count) {
		mProgress = true;
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setTitle(R.string.palseFileEvent_Title);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(count);
		mProgressDialog.setButton(mContext.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mProgress = false;
					}
				});
		mProgressDialog.setProgress(0);
		mProgressDialog.show();
	}

	public void ok() {
		try {
			P.debug("copyFileCmdEvent.Start");
			File directory=new File(mCurrentPath);
			if(!directory.exists())
			{
				directory.mkdirs();
			}
			for (int i = 0; i < mFileList.size() && mProgress; i++) {
				final FileBean mSrcFile = mFileList.get(i);

				String mStrSrcPath = mSrcFile.getFile().getPath();
				if (mCurrentPath.startsWith(mStrSrcPath)) {
					Log.v("wmh", "true:mStrSrcPath=" + "mCurrentPath="
							+ mCurrentPath);
					Message msg = new Message();
					msg.what = 100;
					msg.obj = mCurrentPath + "包括子目录,粘贴失败";
					myHandler.sendMessage(msg);
					return;
				} else {
					Log.v("wmh", "false:mStrSrcPath=" + "mCurrentPath="
							+ mCurrentPath);
				}
				// 将mSrcFile复制到mCurrentPath目录

				int result = cutFile(mCurrentPath, mSrcFile.getFile());
				if(result<0)
				{
					Log.v("wmh", "Palse");
					result=Palse(mCurrentPath, mSrcFile.getFile());
				}
				P.v("result=" + result);
				if (result > 0) {
					mProgressDialog.incrementProgressBy(1);
				} else if (result < 0) {
					
					Message msg = new Message();
					msg.what = 101;
					msg.obj = mSrcFile.getFileName();
					myHandler.sendMessage(msg);
					return;
				} else {
					Message msg = new Message();
					msg.what = 100;
					msg.obj = "粘贴取消";
					myHandler.sendMessage(msg);
					return;
				}
			}
			P.debug("copyFileCmdEvent.end");
			Message msg = new Message();
			msg.what = 100;
			if (mProgress) {
				msg.obj = "粘贴完成";
			} else {
				msg.obj = "粘贴取消";
			}
			myHandler.sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
			Message msg = new Message();
			msg.what = 103;
			msg.obj = e.getMessage();
			myHandler.sendMessage(msg);
			return;
		}

	}

	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 100)// 拷贝完成
			{
				String message = msg.obj.toString();
				Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
				mProgressDialog.dismiss();
			} else if (msg.what == 101)// 拷贝失败
			{
				String message = "拷贝文件失败!";
				Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
				mProgressDialog.dismiss();
			} else if (msg.what == 103)// 拷贝异常失败
			{
				String message = mContext.getString(R.string.error_lable)
						+ (String) msg.obj;
				Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
				mProgressDialog.dismiss();
			}
			if (notif != null)
			{
				notif.NotifyDataSetChanged(
						Const.cmd_CutFileEvent_Finish, null);
			}
		}
	};
	private LinuxFileCommand comm = new LinuxFileCommand();

	/**
	 * 粘贴 path:目地地址 file:源文件
	 * 
	 * @param mSaveFilePaths
	 */
	private int cutFile(String strDirectory, File file) {
		try {
			if (file.exists()) 
			{

				String newPath = strDirectory + "/" + file.getName();
				Process process = comm.moveFile(file.getPath(), newPath);

				int ret = process.waitFor();
				if (ret != 0) 
				{
					BufferedReader br = new BufferedReader(new InputStreamReader(
							process.getErrorStream()));
					final String errorMsg = br.readLine();
					P.d(tag, "Error(code = " + ret + "): " + errorMsg);
					return -1;
				}
				return 1;
			} else {
				return -2;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -2;

	}

	/**
	 * 粘贴 path:目地地址 file:源文件
	 * 
	 * @param mSaveFilePaths
	 */
	private int Palse(String newPath, File file) 
	{
		newPath = newPath + "/" + file.getName();
		if (file.exists()) {
			if (file.isFile()) { // 文件处理
				return PalseFileDialog(file, new File(newPath));

			} else { // 文件夹处理
				return PalseFolderDialog(newPath, file);
			}
		} else {
			return -2;
		}
	}

	/**
	 * 粘贴文件 return 大于0:成功 -1:失败
	 * 
	 * @param path
	 * @param file
	 */
	private int PalseFolderDialog(final String path, final File file) {
		File outFile = new File(path);
		int count = 0;
		if (!outFile.exists())
			outFile.mkdir();

		File[] fileArray = file.listFiles();
		for (File currentFile : fileArray) {// 遍历该目录
			if (currentFile.isFile()) { // 文件则直接粘贴
				int result = PalseFileDialog(currentFile, new File(path + "/"
						+ currentFile.getName()));
				if (result > 0) {
					count = result + count;
				} else {
					return result;
				}
				count++;
			} else {
				int result = PalseFolderDialog(
						path + "/" + currentFile.getName(), currentFile);// 回调
				if (result >= 0) {
					count = result + count;
				} else {
					return result;
				}
			}
		}
		if (!file.delete()) {
			return -4;
		}
		return count;
	}

	/**
	 * 粘贴文件 -4:删除失败
	 * 
	 * inFile:目地地址 outFile:新文件
	 */
	private int PalseFile(final File sourceFile, final File newFile) {
		int result = SDFile.CopyFile(sourceFile, newFile);
		if (result > 0) {
			if (!sourceFile.delete()) {
				return -4;
			}
		}
		return result;
	}

	/**
	 * outFile:目地地址 inFile:源文件
	 * 
	 * @param outFile
	 * @param inFile
	 * @return 0:取消 1:成功 >0:失败
	 */
	private int PalseFileDialog(File sourceFile, File newFile) {
		final String name = newFile.getName();

		if (newFile.exists() && !bflag) 
		{
			SysEng.getInstance().addHandlerEvent(new AbsEvent() {
				public void ok() {
					AlertDialog.Builder test = new AlertDialog.Builder(mContext);
					test.setTitle(name + "文件冲突!");
					test.setSingleChoiceItems(R.array.select_dialog_PalseMode,
							-1, new OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									switch (which) {
									case 0:// 覆盖
										nDialogResult = 1;
										SysEng.getInstance().ThreadNotify();

										break;
									case 1:// 自动命名
										nDialogResult = 2;
										SysEng.getInstance().ThreadNotify();

										break;
									}
									dialog.cancel();
								}
							});
					CheckBox all = new CheckBox(mContext);
					all.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							bflag = isChecked;
						}
					});
					all.setText("全部");
					test.setView(all);
					test.setNegativeButton(mContext.getString(R.string.cancel),
							new OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									nDialogResult = 0;
									mProgress = false;
									SysEng.getInstance().ThreadNotify();
								}
							});
					test.create();
					test.show();
				}
			});
			SysEng.getInstance().ThreadWait();
		}
		P.v("nDialogResult=" + nDialogResult + "newFile.getAbsolutePath():"
				+ newFile.getAbsolutePath());
		switch (nDialogResult) {
		case 1:// 覆盖
			return PalseFile(sourceFile, newFile);
		case 2:// 自动命名
			String strFilePath = newFile.getAbsolutePath();
			String temp;
			int index = 1;
			while (newFile.exists()) {
				temp = FileOperation.pathNameAppend(strFilePath, "(" + index
						+ ")");
				newFile = new File(temp);
				index++;
			}
			return PalseFile(sourceFile, newFile);
		default:// -1://取消
			return nDialogResult;
		}
	}
}
