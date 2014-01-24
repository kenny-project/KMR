package com.kenny.activity;

import java.util.Calendar;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kenny.Application.KApp;
import com.kenny.comui.KDialog;
import com.kenny.dailyenglish.R;
import com.kenny.data.DialogData;
import com.kenny.event.DoNothingEvent;
import com.kenny.file.SDFile;
import com.kenny.struct.AbsEvent;
import com.kenny.util.Config;
import com.kenny.util.Const;
import com.kenny.util.NetCatch;
import com.kenny.util.Utils;
import com.umeng.analytics.MobclickAgent;

public class V6Setting extends LinearLayout {
    private int mHour;
    private int mMinute;
    
	private Button btnClearCache;
	private Button btnFeedback;
	private Button btnAbout;
	private Button btnHelp;
	private ProgressDialog loaddialog;


	private TextView btLearningRemindTime;
	private CheckBox cbLearningRemindSwitch;
	private Utils utils;
	private Context context;
	private Config config;

	public V6Setting(Context context) {
		super(context);
		init(context);
	}

	public V6Setting(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public void init(Context context) {
		LayoutInflater.from(context).inflate(R.layout.v6setting_page, this,
				true);
		utils = new Utils();
		this.context = context;
		KApp app = (KApp) context.getApplicationContext();
		config = app.config;
		onLoad();
	}

	public void onLoad() 
	{
        
        mHour = Utils.get(context, Const.CONFIG_SETTING_LEARNINGREMIND_HOUR, 9);
        mMinute = Utils.get(context, Const.CONFIG_SETTING_LEARNINGREMIND_MINUTE, 0);
		btLearningRemindTime = (TextView) findViewById(R.id.btLearningRemindTime);
		btLearningRemindTime.setText(((mHour<10)?"0"+mHour:mHour) +":"+((mMinute<10)?"0"+mMinute:mMinute));
		btLearningRemindTime.setOnClickListener(onLearningRemindTimeClicked);
		findViewById(R.id.btLearningRemindTimeBackground).setOnClickListener(onLearningRemindTimeClicked);
		
		cbLearningRemindSwitch = (CheckBox) findViewById(R.id.cbLearningRemindSwitch);
//		cbLearningRemindSwitch.setOnClickListener(OnLearningRemindSwitchClicked);
		cbLearningRemindSwitch.setOnCheckedChangeListener(OnLearningRemindSwitchClicked);
		cbLearningRemindSwitch.setChecked(Utils.get(getContext(), Const.CONFIG_SETTING_LEARNINGREMIND_SWITCH, false));
		
		Button test = (Button) findViewById(R.id.btEncouragement);
		test.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) {
				DetailsIntent(v.getContext());
				MobclickAgent.onEvent(context, "Click-vote");
			}
		});
		btnClearCache = (Button) findViewById(R.id.btnClearCache);
		btnClearCache.setOnClickListener(new OnClickListener() {
			// 清空缓存
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String title = context.getString(R.string.app_name);
				String content = context.getString(R.string.dialog_data_clear_content);
				String leftText = context.getString(R.string.ok);
				String RightText = context.getString(R.string.cancel);
				AbsEvent leftEvent = new AbsEvent(context) {

					@Override
					public void ok() {
						NetCatch.clearCache(context);
						String FileName = Const.Dailysentence + "StoreData";
						SDFile.DeleteRAMFile(context, FileName);
						FileName = Const.VOA_Page + "StoreData";
						SDFile.DeleteRAMFile(context, FileName);
						Toast.makeText(context, R.string.clear_cache_success,
								1000).show();
					}
				};
				DialogData data = new DialogData(title, content, leftText,
						RightText, leftEvent, new DoNothingEvent());
				data.tyte = DialogData.BUT_1_TYPE;
				KDialog dialog = new KDialog(context);
				dialog.showdialog(data);
			}
		});



		btnFeedback = (Button) findViewById(R.id.sp_feedback);
		btnFeedback.setOnClickListener(new OnClickListener() {
			// 反馈
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context, V6Feedback.class);
				context.startActivity(intent);
			}
		});

		btnHelp = (Button) findViewById(R.id.sp_help);
		btnHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Intent intent = new Intent(getApplicationContext(),
				// Help.class);
				// startActivity(intent);
			}
		});

		btnAbout = (Button) findViewById(R.id.sp_about);
		btnAbout.setOnClickListener(new OnClickListener() {
			// 关于
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context.getApplicationContext(),
						V6About.class);
				context.startActivity(intent);
			}
		});
	}
	OnCheckedChangeListener OnLearningRemindSwitchClicked=new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			// TODO Auto-generated method stub
			Log.v("wmh", "OnLearningRemindSwitchClicked:"+arg1);
			boolean result=arg1;
			Utils.save(getContext(), Const.CONFIG_SETTING_LEARNINGREMIND_SWITCH, result);
			
			if(result)
			{
				Utils.setAlerm(getContext(), mHour, mMinute);
				MobclickAgent.onEvent(context, "AlarmON");
			}
			else
			{
				Utils.CleanAlerm(getContext());	
			}
		}
	};
	OnClickListener onLearningRemindTimeClicked=new OnClickListener()
	{
		@Override
		public void onClick(View v) {
			 Log.v("wmh", "onLearningRemindTimeClicked");
				TimePickerDialog dialog=new TimePickerDialog(context, mTimeSetListener, mHour, mMinute, true);
				dialog.show();			
		}
		
	};
	Handler handler = new Handler();

	private void startLoad(final String con) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (loaddialog != null) {
					loaddialog.dismiss();
					loaddialog = null;
				}
				try {
					loaddialog = ProgressDialog.show(context, "", "", true);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				loaddialog.setCancelable(false);
				loaddialog.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						// currentPage.onLoadingCancel();
					}
				});
				loaddialog.setContentView(R.layout.load_type1);
				TextView txt = (TextView) loaddialog
						.findViewById(R.id.loadtxt1);
				txt.setText("" + con);
			}
		});
	}

	private void stopLoad() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (loaddialog != null) {
					loaddialog.dismiss();
					loaddialog = null;
				}
			}
		});
	}
	/**
	 * 更新日期显示
	 */
	private void updateDateDisplay(){
//       showDate.setText(new StringBuilder().append(mYear).append("-")
//    		   .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)).append("-")
//               .append((mDay < 10) ? "0" + mDay : mDay)); 
	}
	/**
	 * 设置时间
	 */
	private void setTimeOfDay(){
	   final Calendar c = Calendar.getInstance(); 
       mHour = c.get(Calendar.HOUR_OF_DAY);
       mMinute = c.get(Calendar.MINUTE);
	}
	
    
    /**
     * 时间控件事件
     */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mHour = hourOfDay;
			mMinute = minute;
	        Utils.save(context, Const.CONFIG_SETTING_LEARNINGREMIND_HOUR, mHour);
	        Utils.save(context,Const.CONFIG_SETTING_LEARNINGREMIND_MINUTE, mMinute);
	        if(cbLearningRemindSwitch.isChecked())
	        {
	        	Utils.setAlerm(getContext(), mHour, mMinute);//设定闹钟
	        }
	        btLearningRemindTime.setText(((mHour<10)?"0"+mHour:mHour) +":"+((mMinute<10)?"0"+mMinute:mMinute));
		}
	};
	/**
	 * 获取当前应用的详细信息
	 * 
	 * @param context
	 * @param title
	 * @param imageUri
	 * @return
	 */
	public static int DetailsIntent(Context context)
	{
		try
		{
			Uri uri = Uri.parse("market://details?id="
					+ context.getPackageName());
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return 1;
	}

}
