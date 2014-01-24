package com.kenny.file.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.framework.log.P;
import com.kenny.KFileManager.R;
import com.kenny.file.bean.NetClientBean;
import com.kenny.file.util.FTPClientManager;

/**
 * 创建FTP客户端界面
 * 
 * @author WangMinghui
 * 
 */
public class FTPClientDialog implements OnCheckedChangeListener,
		OnClickListener
{
	EditText etTitle;
	EditText etFTPAddr;
	EditText etPort;
	EditText etUserName;
	EditText etPassWord;
	CheckBox cbAnonymousSetting;
	CheckBox cbAdvancedSetting;
	LinearLayout lyAnonymous, lyAdvanced;
	private NetClientBean mNetClientBean;
	private Spinner spEncode;
	Context context;
	AlertDialog mAlertDialog;
	public void Show(final Context context, final NetClientBean netClientBean)
	{
		this.mNetClientBean = netClientBean;
		this.context = context;
		// mChecked = 1;
		LayoutInflater mLI = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View mLL = mLI.inflate(
				R.layout.ftp_client_dialog, null);

		lyAnonymous = (LinearLayout) mLL.findViewById(R.id.lyAnonymous);
		lyAdvanced = (LinearLayout) mLL.findViewById(R.id.lyAdvanced);
		lyAdvanced.setVisibility(View.GONE);
		etTitle = (EditText) mLL.findViewById(R.id.etTitle);

		etFTPAddr = (EditText) mLL.findViewById(R.id.etFTPAddr);

		etPort = (EditText) mLL.findViewById(R.id.etPort);

		etUserName = (EditText) mLL.findViewById(R.id.etUserName);
		etPassWord = (EditText) mLL.findViewById(R.id.etPassWord);

		cbAnonymousSetting = (CheckBox) mLL
				.findViewById(R.id.cbAnonymousSetting);
		cbAnonymousSetting.setChecked(false);
		cbAnonymousSetting.setOnCheckedChangeListener(this);
		cbAdvancedSetting = (CheckBox) mLL.findViewById(R.id.cbAdvancedSetting);
		cbAdvancedSetting.setChecked(false);
		cbAdvancedSetting.setOnCheckedChangeListener(this);

		Button btSubmit = (Button) mLL.findViewById(R.id.btSubmit);
		btSubmit.setOnClickListener(this);

		Button btCancel = (Button) mLL.findViewById(R.id.btCancel);
		btCancel.setOnClickListener(this);

		spEncode = (Spinner) mLL.findViewById(R.id.spEncode);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				context, R.array.TextEncode,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spEncode.setAdapter(adapter);
		spEncode.setSelection(2);
		spEncode.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id)
			{
				P.v("Spinner1: position=" + position + " id=" + id);
			}

			public void onNothingSelected(AdapterView<?> parent)
			{
				P.v("Spinner1: unselected");

			}
		});
		String title;
		if (mNetClientBean == null)
		{
			title = "创建";
			mNetClientBean = new NetClientBean();
		} else
		{
			title = "修改";
			etTitle.setText(mNetClientBean.getTitle());
			etFTPAddr.setText(mNetClientBean.getHost());
			etPort.setText(mNetClientBean.getPort());
			etUserName.setText(mNetClientBean.getUserName());
			etPassWord.setText(mNetClientBean.getPassWord());
			cbAnonymousSetting.setChecked(mNetClientBean.isAnonymous());
			spEncode.setSelection(mNetClientBean.getEncode());
		}

//		Builder mBuilder = new AlertDialog.Builder(context).setTitle(title)
//				.setView(mLL);
//		AlertDialog aa=new AlertDialog.Builder(context).create();

		mAlertDialog=new AlertDialog.Builder(context).create();
		mAlertDialog.setTitle(title);
		mAlertDialog.setView(mLL);
		mAlertDialog.show();

	}

	
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		switch (buttonView.getId())
		{
		case R.id.cbAnonymousSetting:// 匿名
			if (isChecked)
			{
				lyAnonymous.setVisibility(View.GONE);
			} else
			{
				lyAnonymous.setVisibility(View.VISIBLE);
			}

			break;
		case R.id.cbAdvancedSetting:

			if (!isChecked)
			{
				lyAdvanced.setVisibility(View.GONE);
			} else
			{
				lyAdvanced.setVisibility(View.VISIBLE);
			}
			break;
		}
	}

	private void save()
	{
		if (etFTPAddr.length() <= 0)
		{
			Toast.makeText(context, "服务器地址不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if (etUserName.length() <= 0)
		{
			Toast.makeText(context, "用户名不能为空!", Toast.LENGTH_SHORT).show();
			return;
		}
		if (etPort.length() <= 0)
		{
			Toast.makeText(context, "服务器端口不能为空!", Toast.LENGTH_SHORT).show();
			return;
		}
		mNetClientBean.setTitle(etTitle.getText().toString());
		mNetClientBean.setHost(etFTPAddr.getText().toString());
		mNetClientBean.setUserName(etUserName.getText().toString());
		mNetClientBean.setPassWord(etPassWord.getText().toString());
		mNetClientBean.setAnonymous(cbAnonymousSetting.isChecked());
		mNetClientBean.setType(1);
		mNetClientBean.setPort(etPort.getText().toString());
		mNetClientBean.setEncode(spEncode.getSelectedItemPosition());
		FTPClientManager.GetHandler().Add(mNetClientBean);
		mAlertDialog.dismiss();
		FTPClientManager.GetHandler().SendNotifyData(1,null);
	}

	
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.btSubmit:
			save();
			break;
		case R.id.btCancel:
			mAlertDialog.dismiss();
			break;
		}
	}
}
