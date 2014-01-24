package com.kenny.comui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.kenny.activity.MainOld;
import com.kenny.dailyenglish.R;
import com.kenny.data.DialogData;
import com.kenny.syseng.SysEng;

/**
 * 对话框
 * 
 * @author chenjg
 * */

public class KDialog
{
	private Context main;
	public KDialog(Context main)
	{
		this.main=main;
	}
	public void showdialog(final DialogData dialogdata)
	{
		final Dialog dialog = new Dialog(main);
		switch (dialogdata.tyte){
		case DialogData.BUT_1_TYPE:
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.dialog);
			TextView tvTitle = (TextView) dialog.findViewById(R.id.diatitle);
			tvTitle.setText(dialogdata.title);
			TextView tvMessage = (TextView) dialog.findViewById(R.id.diacon);
			tvMessage.setText(dialogdata.content);
			Button btnLeft = (Button) dialog.findViewById(R.id.yes);
			btnLeft.setText(dialogdata.leftcmd);
			btnLeft.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialogdata.leftevent.ok();
					dialog.dismiss();
				}});
			Button btnRight = (Button) dialog.findViewById(R.id.no);
			btnRight.setText(dialogdata.rightcmd);
			btnRight.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialogdata.rightevent.ok();
					dialog.dismiss();
				}});
			dialog.show();
			break;
		case DialogData.BUT_2_TYPE:
			
			break;
		}
	}
	/**
	 * 网络连接失败Dialog
	 * @param context
	 * @param dialogdata
	 */
	public static void ShowNetWorkErrorDialog(Context context ,final DialogData dialogdata)
	{
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.dialog);
		TextView tvTitle = (TextView) dialog.findViewById(R.id.diatitle);
		tvTitle.setText(dialogdata.title);
		TextView tvMessage = (TextView) dialog.findViewById(R.id.diacon);
		tvMessage.setText(dialogdata.content);
		Button btnLeft = (Button) dialog.findViewById(R.id.yes);
		btnLeft.setText(dialogdata.leftcmd);
		btnLeft.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SysEng.getInstance().runEvent(dialogdata.leftevent);
				dialog.dismiss();
			}});
		
		Button btnRight = (Button) dialog.findViewById(R.id.no);
		btnRight.setVisibility(View.GONE);
		btnRight.setText(dialogdata.rightcmd);
		btnRight.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SysEng.getInstance().runEvent(dialogdata.rightevent);
				dialog.dismiss();
			}});
		dialog.show();
	}
}
