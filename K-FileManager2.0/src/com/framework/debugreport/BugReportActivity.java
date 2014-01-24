package com.framework.debugreport;
import com.kenny.KFileManager.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;
public class BugReportActivity extends Activity {
	static final String STACKTRACE = "fbreader.stacktrace";

	private String getVersionName() {
		try {
			return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (Exception e) {
			return "";
		}
	}

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.bug_report_view);
		final String stackTrace = getIntent().getStringExtra(STACKTRACE);
		final TextView reportTextView = (TextView)findViewById(R.id.report_text);
		reportTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
		reportTextView.setClickable(false);
		reportTextView.setLongClickable(false);

		final String versionName = getVersionName();
		//reportTextView.append("文件管理器 " + versionName + " has been crached, sorry. You can help to fix this bug by sending the report below to FBReader developers. The report will be sent by e-mail. Thank you in advance!\n\n");
		reportTextView.append("文件管理器 " + versionName + " 截获异常错误.\n由此给您带来的不便, 非常抱歉. 您能否将错误发送给我们,以便我们更好的为您服务!\n\n");
		reportTextView.append(stackTrace);

		findViewById(R.id.send_report).setOnClickListener(
			new View.OnClickListener() {
				public void onClick(View view) {
					Intent sendIntent = new Intent(Intent.ACTION_SEND);
					sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "68711120@126.com" });
					sendIntent.putExtra(Intent.EXTRA_TEXT, stackTrace);
					sendIntent.putExtra(Intent.EXTRA_SUBJECT, "FBReader " + versionName + " exception report");
					sendIntent.setType("message/rfc822");
					startActivity(sendIntent);
					finish();
				}
			}
		);

		findViewById(R.id.cancel_report).setOnClickListener(
			new View.OnClickListener() {
				public void onClick(View view) {
					finish();
				}
			}
		);
	}
}
