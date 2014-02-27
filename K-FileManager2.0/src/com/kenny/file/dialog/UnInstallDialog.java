package com.kenny.file.dialog;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.framework.event.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.t.R;
import com.kenny.file.bean.AppBean;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.tools.ShellCommand;
import com.kenny.file.tools.ShellCommand.CommandResult;

public class UnInstallDialog extends AbsEvent {
	private Activity m_context;
	private ProgressDialog mProgressDialog;
	private boolean mProgress = false;
	private ArrayList<AppBean> appList;
	private INotifyDataSetChanged mNotifyDataSetChanged;
	private boolean isAllHideDialog = false;// 全部显示备份对话框
	private boolean bBackApp = true; // 是否备份
	private ShellCommand mShellCommand = null;

	public void ShowDialog(Activity context, ArrayList<AppBean> appList,
			INotifyDataSetChanged notifyDataSetChanged) {
		isAllHideDialog = false;
		bBackApp = true;
		mProgress = true;
		this.appList = appList;
		mNotifyDataSetChanged = notifyDataSetChanged;
		m_context = context;
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setTitle("正在卸载");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(appList.size());
		mProgressDialog.setButton(context.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mProgress = false;
					}
				});
		mProgressDialog.setProgress(0);
		mProgressDialog.show();
		SysEng.getInstance().addEvent(this);
	}
	public void ok() {
		try {
			mShellCommand = new ShellCommand();
			boolean isRoot = mShellCommand.canSU();
			// boolean isRoot = LinuxShell.isRoot();
			for (int i = 0; i < appList.size() && mProgress; i++) {
				AppBean tmpInfo = appList.get(i);
				if (isRoot) {
					if (unistallrootsys(tmpInfo)) {
						mProgressDialog.incrementProgressBy(1);
						continue;
					}
				}
				unInstallApk(tmpInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Message msg = new Message();
			msg.what = 103;
			msg.obj = e.getMessage();
			myHandler.sendMessage(msg);
		}
		Message msg = new Message();
		msg.what = 100;
		if (mProgress) {
			msg.obj = "卸载完成";
		} else {
			msg.obj = "卸载取消";
		}
		myHandler.sendMessage(msg);
	}

	public void unInstallApk(AppBean tmpInfo) {
		try {
			Uri uninstallUri = Uri.fromParts("package",
					tmpInfo.getPackageName(), null);
			Intent intent = new Intent(Intent.ACTION_DELETE, uninstallUri);
			m_context.startActivity(intent);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mProgressDialog.incrementProgressBy(1);
		}
	}

	private boolean unistallrootsys(AppBean tmpInfo) {
		// Process process = null;
		// DataOutputStream out = null;
		// InputStream in = null;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append("chmod 777 " + m_context.getPackageCodePath()+"\n");
			buffer.append("mount -o remount /dev/block/mtdblock0 /system  \n");
			String apkPath = tmpInfo.getFilePath();
			String odexPath = apkPath.substring(0, apkPath.length() - 3)
					+ "odex";
			buffer.append("pm uninstall  " + tmpInfo.getPackageName() + "\n");
			buffer.append("rm -rf " + apkPath + "\n");
			buffer.append("rm -rf " + odexPath + "\n");
			buffer.append("rm -rf data\\data\\" + tmpInfo.getPackageName()
					+ "\n");
			buffer.append("exit\n");
			CommandResult result = mShellCommand.suOrSH().runWaitFor(
					buffer.toString());
			Log.v("wmh", "cmd=" + buffer.toString());
			Log.v("wmh", "result.stderr=" + result.stderr);

			// ----------PACKAGE_REMOVED----start:intent.getDataString()=package:com.example.android.apis
//			 Intent intent=new Intent(Intent.ACTION_PACKAGE_REMOVED);
//			 Uri packageURI = Uri.parse("package:"+ tmpInfo.getPackageName());
//			 intent.setData(packageURI);
//			 m_context.sendBroadcast(intent);

			// 请求root
			// process = Runtime.getRuntime().exec("su");
			// out = new DataOutputStream(process.getOutputStream());
			// 调用安装
			// out.write(("mount -o remount /dev/block/mtdblock0 /system " +
			// " \n")
			// .getBytes());
			// out.write(("rm -rf " + dir + "\n").getBytes());
			// out.writeBytes("exit\n");
			// out.flush();
			// process.waitFor();
			// in = process.getInputStream();
			if (result.success()) {
				Message msg = new Message();
				msg.what = 99;
				msg.obj = tmpInfo.getAppName() + "卸载完成";
				myHandler.sendMessage(msg);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Message msg = new Message();
		msg.what = 99;
		msg.obj = tmpInfo.getAppName() + "卸载失败!";
		myHandler.sendMessage(msg);
		return false;
	}

	// class MyPakcageInstallObserver extends IPackageInstallObserver.Stub {
	// Context cxt;
	// String appName;
	// String filename;
	// String pkname;
	//
	// public MyPakcageInstallObserver(Context c, String appName,
	// String filename,String packagename) {
	// this.cxt = c;
	// this.appName = appName;
	// this.filename = filename;
	// this.pkname = packagename;
	// }
	//
	// @Override
	// public void packageInstalled(String packageName, int returnCode) {
	// Log.i(TAG, "returnCode = " + returnCode);// 返回1代表安装成功
	// if (returnCode == 1) {
	// //TODO
	// }
	// Intent it = new Intent();
	// it.setAction(CustomAction.INSTALL_ACTION);
	// it.putExtra("install_returnCode", returnCode);
	// it.putExtra("install_packageName", packageName);
	// it.putExtra("install_appName", appName); cxt.sendBroadcast(it);
	// }
	// }
	Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			int status = 0;
			if (msg.what == 99)// 备份完成
			{
				Toast.makeText(m_context, (String) msg.obj, Toast.LENGTH_SHORT)
						.show();
			} else if (msg.what == 100)// 备份完成
			{
				mProgressDialog.dismiss();
				// String message = msg.obj.toString();
				// Toast.makeText(m_context, message,
				// Toast.LENGTH_SHORT).show();
				if (mNotifyDataSetChanged != null) {
					mNotifyDataSetChanged.NotifyDataSetChanged(msg.what, null);
				}
				mProgressDialog.dismiss();
				return;
			} else if (msg.what == 101)// 备份失败
			{
				status = msg.arg1;
				String message = "卸载" + status + "软件包失败";
				Toast.makeText(m_context, message, Toast.LENGTH_SHORT).show();
				mProgressDialog.dismiss();
			} else if (msg.what == 103)// 备份异常失败
			{
				String message = "错误:" + (String) msg.obj;
				Toast.makeText(m_context, message, Toast.LENGTH_SHORT).show();
				mProgressDialog.dismiss();
				if (mNotifyDataSetChanged != null) {
					mNotifyDataSetChanged.NotifyDataSetChanged(msg.what, null);
				}
			}
		}
	};
}
