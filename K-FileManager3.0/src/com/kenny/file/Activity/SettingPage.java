package com.kenny.file.Activity;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.Slidingmenu.Fragment.ContentFragment;
import com.kenny.file.Event.delFileEvent;
import com.kenny.file.bean.FileBean;
import com.kenny.file.dialog.AboutDialog;
import com.kenny.file.dialog.FileRelevanceAppListDialog;
import com.kenny.file.dialog.ResumeFavoriteDialog;
import com.kenny.file.dialog.ThemeDialog;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.interfaces.KActivityStatus;
import com.kenny.file.manager.FileManager;
import com.kenny.file.tools.SaveData;
import com.kenny.file.tools.T;
import com.kenny.file.util.Config;
import com.kenny.file.util.Const;
import com.kenny.file.util.Theme;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public class SettingPage extends ContentFragment implements OnClickListener,
		OnCheckedChangeListener, KActivityStatus, INotifyDataSetChanged
{
	private TextView tvSetDefaultPath, tvSetSDRootPath;
	private CheckBox cbSetSensor, cbSetTools,  cbLastPath,
			cbSetShowHideFile;
			//cbSetShowTask;cbSetTab,
	private String mStrDefaultPath, mStrSDRootPath;
	private CheckBox btSetDefPicFileButton, btSetDefAudioFileButton,
			btSetDefTxtFileButton, btSetDefZipFileButton;
//    public static void actionSelectAccountType(Activity fromActivity, SetupData setupData) {
//        final Intent i = new ForwardingIntent(fromActivity, SettingPage.class);
//        i.putExtra(SetupData.EXTRA_SETUP_DATA, setupData);
//        fromActivity.startActivity(i);
//    }
//    public static void actionSettingPage(Activity fromActivity) 
//    {
//        final Intent i = new ForwardingIntent(fromActivity, SettingPage.class);
//        fromActivity.startActivity(i);
//    }
  public static void actionSettingPage() 
  {
	  SettingPage newContent=new SettingPage();
	  newContent.setTitle(R.string.setting_Title);
	  newContent.switchFragment(newContent);
  }
	@Override
	public void onCreate(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		setContentView(R.layout.settingpage,inflater);
		setTitle(R.string.setting_Title);
		MobclickAgent.onEvent(m_act, "KMainPage", "settingPage");
		mView.findViewById(R.id.btBack).setOnClickListener(this);
		cbSetSensor = (CheckBox) mView.findViewById(R.id.cbSetSensor);
		cbSetTools = (CheckBox) mView.findViewById(R.id.cbSetTools);
//		cbSetTab = (CheckBox) mView.findViewById(R.id.cbSetTab);
//		cbSetShowTask = (CheckBox) mView.findViewById(R.id.cbSetShowTask);
		cbSetShowHideFile = (CheckBox) mView.findViewById(R.id.cbSetShowHideFile);
		Button btSetDefaultPath = (Button) mView.findViewById(R.id.btSetDefaultPath);
		tvSetDefaultPath = (TextView) mView.findViewById(R.id.tvSetDefaultPath);
		tvSetSDRootPath = (TextView) mView.findViewById(R.id.tvSetSDRootPath);
		cbLastPath = (CheckBox) mView.findViewById(R.id.cbLastPath);

		btSetDefPicFileButton = (CheckBox) mView.findViewById(R.id.btSetDefPicFileButton);
		btSetDefAudioFileButton = (CheckBox) mView.findViewById(R.id.btSetDefAudioFileButton);
		btSetDefTxtFileButton = (CheckBox) mView.findViewById(R.id.btSetDefTxtFileButton);
		btSetDefZipFileButton = (CheckBox) mView.findViewById(R.id.btSetDefZipFileButton);
		// Button temp =(Button) findViewById(R.id.btSetViewButton);
		// temp.setOnClickListener(this);
		// temp =(Button) findViewById(R.id.btSetSortButton);
		// temp.setOnClickListener(this);
		Button btSetResumeFavorite = (Button) mView.findViewById(R.id.btSetResumeFavorite);
		Button btSetCommentScore = (Button) mView.findViewById(R.id.btSetCommentScore);
		Button btSetAboutTitle = (Button) mView.findViewById(R.id.btSetAboutTitle);
		View btSetCurrentPath = mView.findViewById(R.id.btSetCurrentPath);
		Button btSetFeedback = (Button) mView.findViewById(R.id.btSetFeedback);
		Button btSetThemeButton = (Button) mView.findViewById(R.id.btSetThemeButton);
		Button btSetDefFileIcoButton = (Button) mView.findViewById(R.id.btSetDefFileIcoButton);
		Button btSetCheckUpdate = (Button) mView.findViewById(R.id.btSetCheckUpdate);
		
		View btSetSDPath = mView.findViewById(R.id.btSetSDPath);
		btSetSDPath.setOnClickListener(this);
		btSetDefFileIcoButton.setOnClickListener(this);
		btSetDefaultPath.setOnClickListener(this);
		btSetCommentScore.setOnClickListener(this);
		btSetCurrentPath.setOnClickListener(this);
		btSetResumeFavorite.setOnClickListener(this);
		btSetAboutTitle.setOnClickListener(this);
		btSetFeedback.setOnClickListener(this);
		btSetCheckUpdate.setOnClickListener(this);
		btSetThemeButton.setOnClickListener(this);

		btSetDefPicFileButton.setChecked(Config.isOpenDefPicFile());
		btSetDefAudioFileButton.setChecked(Config.isOpenDefAudioFile());
		btSetDefTxtFileButton.setChecked(Config.isOpenDefTxtFile());
		btSetDefZipFileButton.setChecked(Config.isbOpenDefZipFile());

		cbSetShowHideFile.setChecked(Theme.getShowHideFile());
//		cbSetShowTask.setChecked(Theme.getTaskVisible());
		cbSetSensor.setChecked(Theme.getScreenOrientation());
		cbSetTools.setChecked(Theme.getToolsVisible());
//		cbSetTab.setChecked(Theme.getTabsVisible());

		// cbSetShowHideFile.setOnCheckedChangeListener(this);
		// cbSetShowTask.setOnCheckedChangeListener(this);
		// cbSetSensor.setOnCheckedChangeListener(this);
		// cbSetTab.setOnCheckedChangeListener(this);
		// cbSetTools.setOnCheckedChangeListener(this);
		// cbLastPath.setOnCheckedChangeListener(this);
	}

	@Override
	public void onPause()
	{
		T.SetScreenOrientation(m_act, cbSetSensor.isChecked());
		Theme.setSensorOrientation(cbSetSensor.isChecked());
		Theme.setToolsVisible(cbSetTools.isChecked());
//		Theme.setTaskVisible(cbSetShowTask.isChecked());
//		Theme.setTabsVisible(cbSetTab.isChecked());
		Theme.setShowHideFile(cbSetShowHideFile.isChecked());
		Theme.Save(m_act);
		Config.setbOpenDefZipFile(btSetDefZipFileButton.isChecked());
		Config.setbOpenDefPicFile(btSetDefPicFileButton.isChecked());
		Config.setOpenDefAudioFile(btSetDefAudioFileButton.isChecked());
		Config.setOpenDefTxtFile(btSetDefTxtFileButton.isChecked());
		Config.Save(m_act);
		super.onPause();
	}
	
	@Override
	public void onResume()
	{
		mStrDefaultPath = SaveData.Read(m_act, Const.strDefaultPath,
				Const.getSDCard());
		tvSetDefaultPath.setText(mStrDefaultPath);

		mStrSDRootPath = SaveData.Read(m_act, Const.strSDRootPath,
				Const.getSDCard());
		tvSetSDRootPath.setText(mStrSDRootPath);
		super.onResume();
	}
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btSetDefPicFileButton:
			FileRelevanceAppListDialog dailog = new FileRelevanceAppListDialog();
			dailog.ShowDialog(m_act, 2, this);
			break;
		case R.id.btSetDefTxtFileButton:
			dailog = new FileRelevanceAppListDialog();
			dailog.ShowDialog(m_act, 1, this);
			break;
		case R.id.btSetDefAudioFileButton:
			dailog = new FileRelevanceAppListDialog();
			dailog.ShowDialog(m_act, 3, this);
			break;
		case R.id.btSetSDPath:
			mStrSDRootPath = FileManager.getInstance().getCurrentPath();
			Const.setSDCard(mStrSDRootPath);
			SaveData.Write(m_act, Const.strSDRootPath, mStrSDRootPath);// 输入自动化
			tvSetSDRootPath.setText(mStrSDRootPath);
			Toast.makeText(m_act, "设置成功", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btSetCheckUpdate:
			CheckUpdate();
			break;
		case R.id.btBack:
			finish();
			break;
		// case R.id.btSetDefAudioFileButton:
		// dailog=new FileRelevanceAppListDialog();
		// dailog.ShowDialog(getActivity(),3, this);
		// break;
		// case R.id.btSetViewButton:
		// new ViewStyleDialog().ShowDialog(m_act,null);
		// break;
		// case R.id.btSetSortButton:
		// new ViewSortDialog().ShowDialog(m_act,null);
		// break;
		case R.id.btSetThemeButton:
			new ThemeDialog().ShowDialog(m_act, null);
			break;
		case R.id.btSetFeedback:// 反馈
			// UMFeedbackService.openUmengFeedbackSDK(m_act);
			FeedbackAgent agent = new FeedbackAgent(m_act);
			agent.startFeedbackActivity();
			break;
		case R.id.btSetCommentScore:
			MobclickAgent.onEvent(m_act, "CommentScore", "SettingScore");
			T.DetailsIntent(m_act);
			break;
		case R.id.btSetDefFileIcoButton:// 设置默认图标
			Intent intent = new Intent(m_act, FileRelevanceManager.class);
			m_act.startActivity(intent);
			break;
		case R.id.btSetDefaultPath:// 设置默认路径
			// EditDialog.Show(m_act, mStrDefaultPath);
			break;
		case R.id.btSetCurrentPath:
			mStrDefaultPath = FileManager.getInstance().getCurrentPath();
			SaveData.Write(m_act, Const.strDefaultPath, mStrDefaultPath);// 输入自动化
			tvSetDefaultPath.setText(mStrDefaultPath);
			Toast.makeText(m_act, "设置成功", Toast.LENGTH_SHORT).show();
			break;
		case R.id.btSetResumeFavorite:// 恢复数据库
			ResumeFavoriteDialog.showdialog(m_act, "您确定要恢复到初始状态吗？");
			break;
		case R.id.btSetAboutTitle:
			new AboutDialog().showDialog(m_act);
			break;
		case R.id.btSetClearCache:
			new AlertDialog.Builder(m_act)
					.setTitle("提示!")
					.setMessage("确定要清空缓存吗?")
					.setPositiveButton(m_act.getString(R.string.ok),
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int which)
								{
									SysEng.getInstance().addEvent(
											new delFileEvent(m_act,
													new FileBean(new File(
															Const.szAppTempPath),
															null)));
								}
							})
					.setNegativeButton(m_act.getString(R.string.cancel), null)
					.show();
			break;
		default:
			break;
		}
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)// checkbox的事件
	{
		// TODO Auto-generated method stub
		switch (buttonView.getId())
		{
		case R.id.cbSetSensor:
			T.SetScreenOrientation(m_act, isChecked);
			Theme.setSensorOrientation(isChecked);
			break;
		case R.id.cbLastPath:
			// SaveData.writePreferencesBoolean(m_act, Const.strLastPathEnable,
			// isChecked);// 输入自动化
			break;
		case R.id.cbSetTools:
			// SaveData.Write(m_act, Const.strToolsVisible,
			// isChecked);// 输入自动化
			Theme.setToolsVisible(isChecked);
			break;
//		case R.id.cbSetShowTask:
//			Theme.setTaskVisible(isChecked);
//			break;
//		case R.id.cbSetTab:
//			// SaveData
//			// .Write(m_act, Const.strTabVisible, isChecked);// 输入自动化
//			Theme.setTabsVisible(isChecked);
//			break;
		case R.id.cbSetShowHideFile:
			Theme.setShowHideFile(isChecked);
			// SaveData
			// .Write(m_act, Const.strShowHideFile, isChecked);//显示隐含文件
			break;
		default:
			break;
		}
	}
	private void CheckUpdate()
	{
		UmengUpdateAgent.update(m_act);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
		        @Override
		        public void onUpdateReturned(int updateStatus,UpdateResponse updateInfo) {
		            switch (updateStatus) {
		            case 0: // has update
		                UmengUpdateAgent.showUpdateDialog(m_act, updateInfo);
		                break;
		            case 1: // has no update
		                Toast.makeText(m_act, "没有更新,当前版本已经是最新的", Toast.LENGTH_SHORT)
		                        .show();
		                break;
		            case 2: // none wifi
		                Toast.makeText(m_act, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT)
		                        .show();
		                break;
		            case 3: // time out
		                Toast.makeText(m_act, "网络连接超时，请确认网络是否连接", Toast.LENGTH_SHORT)
		                        .show();
		                break;
		            }
		        }
		});
		UmengUpdateAgent.forceUpdate(m_act);
	}
	@Override
	public boolean KActivityResult(int cmd, int msg, int arg1, String arg2)
	{
		// TODO Auto-generated method stub
		onResume();
		return true;
	}
	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void NotifyDataSetChanged(int cmd, Object value)
	{
		// TODO Auto-generated method stub

	}
}
