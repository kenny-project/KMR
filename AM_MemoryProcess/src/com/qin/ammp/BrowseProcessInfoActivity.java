package com.qin.ammp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class BrowseProcessInfoActivity extends Activity  implements OnItemClickListener{

	private static String TAG = "ProcessInfo";
	private static final int KILL_PORCESS = 1;
	private static final int SEARCH_RUNNING_APP = 2;

	private ActivityManager mActivityManager = null;
	// ProcessInfo Model�� �����������н�����Ϣ
	private List<ProcessInfo> processInfoList = null;

	private ListView listviewProcess;
    private TextView tvTotalProcessNo ; 
	
    private String [] dialogItems  = new String[] {"ɱ���ý���","�鿴�����ڸý��̵�Ӧ�ó���"} ;
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.browse_process_list);

		listviewProcess = (ListView) findViewById(R.id.listviewProcess);
		listviewProcess.setOnItemClickListener(this);
		
		tvTotalProcessNo =(TextView)findViewById(R.id.tvTotalProcessNo);
		
		// ���ActivityManager����Ķ���
		mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		// ���ϵͳ������Ϣ
		getRunningAppProcessInfo();
		// ΪListView��������������
		BrowseProcessInfoAdapter mprocessInfoAdapter = new BrowseProcessInfoAdapter(this, processInfoList);
		listviewProcess.setAdapter(mprocessInfoAdapter);
	
		tvTotalProcessNo.setText("��ǰϵͳ���̹��У�"+processInfoList.size());
	}
    //ɱ���ý��̣�����ˢ��
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1,  final int position, long arg3) {
		// TODO Auto-generated method stub
	    new AlertDialog.Builder(this).setItems(dialogItems, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//ɱ������
				if(which == 0) {
				   //ɱ���ý��̣��ͷŽ���ռ�õĿռ�
				   mActivityManager.killBackgroundProcesses(processInfoList.get(position).getProcessName());
		           //ˢ�½���
				   getRunningAppProcessInfo() ;
				   BrowseProcessInfoAdapter mprocessInfoAdapter = new BrowseProcessInfoAdapter(
				   BrowseProcessInfoActivity.this, processInfoList);
				   listviewProcess.setAdapter(mprocessInfoAdapter);
				   tvTotalProcessNo.setText("��ǰϵͳ���̹��У�"+processInfoList.size());
				}
				//�鿴�����ڸý��̵�Ӧ�ó���
				else if(which ==1){   
					ProcessInfo processInfo = processInfoList.get(position);
					
					Intent intent = new Intent() ;
				    intent.putExtra("EXTRA_PKGNAMELIST", processInfo.pkgnameList) ;
				    intent.putExtra("EXTRA_PROCESS_ID", processInfo.getPid());
				    intent.putExtra("EXTRA_PROCESS_NAME", processInfo.getProcessName());
				    intent.setClass(BrowseProcessInfoActivity.this, BrowseRunningAppActivity.class);
				    startActivity(intent);
				}
		   }
	    }).create().show() ;
	}
	// ���ϵͳ������Ϣ
	private void getRunningAppProcessInfo() {
		// ProcessInfo Model��   �����������н�����Ϣ
	    processInfoList = new ArrayList<ProcessInfo>();

		// ͨ������ActivityManager��getRunningAppProcesses()�������ϵͳ�������������еĽ���
		List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager
				.getRunningAppProcesses();

		for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
			// ����ID��
			int pid = appProcessInfo.pid;
			// �û�ID ������Linux��Ȩ�޲�ͬ��IDҲ�Ͳ�ͬ ���� root��
			int uid = appProcessInfo.uid;
			// ��������Ĭ���ǰ�������������android��process=""ָ��
			String processName = appProcessInfo.processName;
			// ��øý���ռ�õ��ڴ�
			int[] myMempid = new int[] { pid };
			// ��MemoryInfoλ��android.os.Debug.MemoryInfo���У�����ͳ�ƽ��̵��ڴ���Ϣ
			Debug.MemoryInfo[] memoryInfo = mActivityManager
					.getProcessMemoryInfo(myMempid);
			// ��ȡ����ռ�ڴ�����Ϣ kb��λ
			int memSize = memoryInfo[0].dalvikPrivateDirty;

			Log.i(TAG, "processName: " + processName + "  pid: " + pid
					+ " uid:" + uid + " memorySize is -->" + memSize + "kb");
			// ����һ��ProcessInfo����
			ProcessInfo processInfo = new ProcessInfo();
			processInfo.setPid(pid);
			processInfo.setUid(uid);
			processInfo.setMemSize(memSize);
			processInfo.setPocessName(processName);
			//�������������ڸ�Ӧ�ó���İ���
			processInfo.pkgnameList = appProcessInfo.pkgList ;
			processInfoList.add(processInfo);

			// ���ÿ�����������е�Ӧ�ó���(��),��ÿ��Ӧ�ó���İ���
			String[] packageList = appProcessInfo.pkgList;
			
			Log.i(TAG, "process id is " + pid + "has " + packageList.length);
			for (String pkg : packageList) {
				Log.i(TAG, "packageName " + pkg + " in process id is -->"+ pid);
			}
		}
	}
//	// ���ϵͳ������Ϣ
//	private void getRunningTaskInfo() {
//		// ProcessInfo Model��   �����������н�����Ϣ
//	    processInfoList = new ArrayList<ProcessInfo>();
//
//		// ͨ������ActivityManager��getRunningAppProcesses()�������ϵͳ�������������еĽ���
//		List<ActivityManager.RunningTaskInfo> appProcessList = mActivityManager
//				.getRunningTasks(1024*1000);
//
//		for (ActivityManager.RunningTaskInfo appProcessInfo : appProcessList) {
//			// ����ID��
//			int pid = appProcessInfo.pid;
//			// �û�ID ������Linux��Ȩ�޲�ͬ��IDҲ�Ͳ�ͬ ���� root��
//			int uid = appProcessInfo.uid;
//			// ��������Ĭ���ǰ�������������android��process=""ָ��
//			String processName = appProcessInfo.topActivity.getPackageName();
//			// ��øý���ռ�õ��ڴ�
//			int[] myMempid = new int[] { pid };
//			// ��MemoryInfoλ��android.os.Debug.MemoryInfo���У�����ͳ�ƽ��̵��ڴ���Ϣ
//			Debug.MemoryInfo[] memoryInfo = mActivityManager
//					.getProcessMemoryInfo(myMempid);
//			// ��ȡ����ռ�ڴ�����Ϣ kb��λ
//			int memSize = memoryInfo[0].dalvikPrivateDirty;
//
//			Log.i(TAG, "processName: " + processName + "  pid: " + pid
//					+ " uid:" + uid + " memorySize is -->" + memSize + "kb");
//			// ����һ��ProcessInfo����
//			ProcessInfo processInfo = new ProcessInfo();
//			processInfo.setPid(pid);
//			processInfo.setUid(uid);
//			processInfo.setMemSize(memSize);
//			processInfo.setPocessName(processName);
//			//�������������ڸ�Ӧ�ó���İ���
//			processInfo.pkgnameList = appProcessInfo.pkgList ;
//			processInfoList.add(processInfo);
//
//			// ���ÿ�����������е�Ӧ�ó���(��),��ÿ��Ӧ�ó���İ���
//			String[] packageList = appProcessInfo.pkgList;
//			
//			Log.i(TAG, "process id is " + pid + "has " + packageList.length);
//			for (String pkg : packageList) {
//				Log.i(TAG, "packageName " + pkg + " in process id is -->"+ pid);
//			}
//		}
//	}

}
