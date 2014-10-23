package com.kenny.file.page;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.log.P;
import com.framework.page.AbsPage;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Event.delFileEvent;
import com.kenny.file.bean.FileBean;
import com.kenny.file.dialog.AboutDialog;
import com.kenny.file.dialog.ResumeFavoriteDialog;
import com.kenny.file.dialog.ThemeDialog;
import com.kenny.file.dialog.ViewSortDialog;
import com.kenny.file.dialog.ViewStyleDialog;
import com.kenny.file.interfaces.KActivityStatus;
import com.kenny.file.tools.SaveData;
import com.kenny.file.tools.T;
import com.kenny.file.util.Const;
import com.kenny.file.util.FileManager;
import com.kenny.file.util.Theme;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.UMFeedbackService;

public class SettingPage extends AbsPage implements OnClickListener,
      OnCheckedChangeListener, KActivityStatus
{
   public SettingPage(Activity context)
   {
      super(context);
   }
   private TextView tvSetDefaultPath;
   private CheckBox cbSetSensor, cbSetTools, cbSetTab, cbLastPath,cbSetShowHideFile,cbSetShowTask;
   private String mStrDefaultPath;
   
   public void onCreate()
   {
      setContentView(R.layout.settingpage);
      MobclickAgent.onEvent(m_act,"KMainPage","settingPage");
      cbSetSensor = (CheckBox) findViewById(R.id.cbSetSensor);
      cbSetTools = (CheckBox) findViewById(R.id.cbSetTools);
      cbSetTab = (CheckBox) findViewById(R.id.cbSetTab);
      cbSetShowTask= (CheckBox) findViewById(R.id.cbSetShowTask);
      cbSetShowHideFile= (CheckBox) findViewById(R.id.cbSetShowHideFile);
      Button btSetDefaultPath = (Button) findViewById(R.id.btSetDefaultPath);
      tvSetDefaultPath = (TextView) findViewById(R.id.tvSetDefaultPath);
      cbLastPath = (CheckBox) findViewById(R.id.cbLastPath);
      
      Button temp =(Button) findViewById(R.id.btSetViewButton);
      temp.setOnClickListener(this);
      temp =(Button) findViewById(R.id.btSetSortButton);
      temp.setOnClickListener(this);
      Button btSetResumeFavorite = (Button) findViewById(R.id.btSetResumeFavorite);
      Button btSetCommentScore = (Button) findViewById(R.id.btSetCommentScore);
      Button btSetAboutTitle = (Button) findViewById(R.id.btSetAboutTitle);
      Button btSetCurrentPath = (Button) findViewById(R.id.btSetCurrentPath);
      Button btSetFeedback= (Button) findViewById(R.id.btSetFeedback);
      Button btSetThemeButton= (Button) findViewById(R.id.btSetThemeButton);
      btSetDefaultPath.setOnClickListener(this);
      btSetCommentScore.setOnClickListener(this);
      btSetCurrentPath.setOnClickListener(this);
      btSetResumeFavorite.setOnClickListener(this);
      btSetAboutTitle.setOnClickListener(this);
      btSetFeedback.setOnClickListener(this);
      
      btSetThemeButton.setOnClickListener(this);
      
      cbSetShowHideFile.setChecked(Theme.getShowHideFile());
      cbSetShowTask.setChecked(Theme.getTaskVisible());
      cbSetSensor.setChecked(Theme.getScreenOrientation());
      cbSetTools.setChecked(Theme.getToolsVisible());
      cbSetTab.setChecked(Theme.getTabsVisible());
      
//      cbSetShowHideFile.setOnCheckedChangeListener(this);
//      cbSetShowTask.setOnCheckedChangeListener(this);
//      cbSetSensor.setOnCheckedChangeListener(this);
//      cbSetTab.setOnCheckedChangeListener(this);
//      cbSetTools.setOnCheckedChangeListener(this);
//      cbLastPath.setOnCheckedChangeListener(this);
   }
   
   
   public void onPause()
   {
      T.SetScreenOrientation(m_act,cbSetSensor.isChecked());
      Theme.setSensorOrientation(cbSetSensor.isChecked());
      Theme.setToolsVisible(cbSetTools.isChecked());
      Theme.setTaskVisible(cbSetShowTask.isChecked());
      Theme.setTabsVisible(cbSetTab.isChecked());
      Theme.setShowHideFile(cbSetShowHideFile.isChecked());
      Theme.Save(m_act);
   }
   
   
   public void onResume()
   {
      mStrDefaultPath = SaveData.Read(m_act,
	  Const.strDefaultPath, Const.SDCard);
      tvSetDefaultPath.setText(mStrDefaultPath);
   }
   
   
   public void onClick(View v)
   {
      switch (v.getId())
      {
      case R.id.btSetViewButton:
         new ViewStyleDialog().ShowDialog(m_act,null);
         break;
      case R.id.btSetSortButton:
         new ViewSortDialog().ShowDialog(m_act,null);
         break;
      case R.id.btSetThemeButton:
         new ThemeDialog().ShowDialog(m_act, null);
         break;
      case R.id.btSetFeedback://反馈
         UMFeedbackService.openUmengFeedbackSDK(m_act);
//	  SysEng.getInstance().addHandlerEvent(
//	        new NextPageEvent(m_act, new FeedBackPage(m_act), 1, null));
         break;
      case R.id.btSetCommentScore:
         MobclickAgent.onEvent(m_act, "CommentScore","SettingScore");
         T.DetailsIntent(m_act);
         break;
      case R.id.btSetDefaultPath:// 设置默认路径
        // EditDialog.Show(m_act, mStrDefaultPath);
         break;
      case R.id.btSetCurrentPath:
         mStrDefaultPath = FileManager.GetHandler().getCurrentPath();
         SaveData.Write(m_act, Const.strDefaultPath,
	     mStrDefaultPath);// 输入自动化
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
		    public void onClick(DialogInterface dialog, int which)
		    {
		       SysEng.getInstance().addEvent(
		             new delFileEvent(m_act, new FileBean(new File(
		                   Const.szAppPath), null)));
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
         T.SetScreenOrientation(m_act,isChecked);
         Theme.setSensorOrientation(isChecked);
         break;
      case R.id.cbLastPath:
         // SaveData.writePreferencesBoolean(m_act, Const.strLastPathEnable,
         // isChecked);// 输入自动化
         break;
      case R.id.cbSetTools:
//         SaveData.Write(m_act, Const.strToolsVisible,
//	     isChecked);// 输入自动化
         Theme.setToolsVisible(isChecked);
         break;
      case R.id.cbSetShowTask:
         Theme.setTaskVisible(isChecked);
         break;
      case R.id.cbSetTab:
//         SaveData
//	     .Write(m_act, Const.strTabVisible, isChecked);// 输入自动化
         Theme.setTabsVisible(isChecked);
         break;
      case R.id.cbSetShowHideFile:
         Theme.setShowHideFile(isChecked);
//         SaveData
//	     .Write(m_act, Const.strShowHideFile, isChecked);//显示隐含文件
         break;

      default:
         break;
      }
   }
   
   
   public boolean KActivityResult(int cmd, int msg, int arg1, String arg2)
   {
      // TODO Auto-generated method stub
      onResume();
      return true;
   }
   
   
   public void clear()
   {
      
   }
   public boolean onCreateOptionsMenu(Menu menu)
   {
      P.debug("onCreateOptionsMenu");
      menu.clear();
      return false;
   }
   
   public boolean backKey()
   {
      // TODO Auto-generated method stub
      return false;
   }
   
   
   public boolean onTouchEvent(MotionEvent event)
   {
      // TODO Auto-generated method stub
      return false;
   }
   
   
   public void onDestroy()
   {
      // TODO Auto-generated method stub
      
   }
}
