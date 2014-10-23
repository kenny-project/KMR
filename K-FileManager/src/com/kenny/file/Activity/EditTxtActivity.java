package com.kenny.file.Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import org.textmining.text.extraction.WordExtractor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.dialog.TextEncodeDialog;
import com.kenny.file.interfaces.INotifyDataSetChanged;
import com.kenny.file.tools.ChangeCharset;
import com.umeng.analytics.MobclickAgent;

/**
 * ZIP文件操作
 * 
 * @author WangMinghui
 * 
 */
public class EditTxtActivity extends Activity implements OnClickListener,
      INotifyDataSetChanged
{
   private EditText txtEditText;
   private TextView textViewDetail;
   private WebView webViewDetail;
   private TextView txtTextTitle;
   private Button txtEncodeButton;
   private Button txtEditButton;
   private Button txtSaveButton;
   
   
   protected void onActivityResult(int requestCode, int resultCode, Intent data)
   {
      // TODO Auto-generated method stub
      super.onActivityResult(requestCode, resultCode, data);
      
      // if (requestCode == 1 && resultCode == RESULT_OK)
      // {
      // ArrayList<String> results = data
      // .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
      // Toast.makeText(this, results.get(0), Toast.LENGTH_LONG).show();
      // // to do sth
      // }
   }
   
   private Button txtCancleButton;
   private String txtPath = null;
   // private boolean mSaveFlag = false; // false: 未保存 true:保存
   private LoadData mLoadData = new LoadData();
   
   private class LoadData extends AbsEvent
   {
      String encode;
      private ProgressDialog mProgressDialog = null;
      private boolean mProgress = false;
      private File inFile;
      
      public void setPath(String path, String encode)
      {
         this.encode = encode;
         mProgress = true;
         inFile = new File(path);
         Long len = inFile.length() / 1024;
         if (len > 8)
         {
	  ShowDialog(len);
         }
         P.v("len=" + len);
         SysEng.getInstance().addEvent(this);
      }
      
      /** 复制文件的方法 */
      private String ReadStreamFile(File inFile, String encode)
      {
         try
         {
	  StringBuffer strBuffer = new StringBuffer();
	  FileInputStream fis = new FileInputStream(inFile);
	  InputStreamReader isr = new InputStreamReader(fis, encode);
	  char[] buffer = new char[8000];
	  int len = 0;
	  do
	  {
	     len = isr.read(buffer);
	     if (len != -1)
	     {
	        strBuffer.append(buffer, 0, len);
	     }
	     // if (mProgressDialog != null)
	     // mProgressDialog
	     // .incrementProgressBy(1);
	     
	  } while (len != -1 && mProgress);
	  if (fis != null)
	  {
	     fis.close();
	  }
	  return strBuffer.toString();
         }
         catch (Exception e)
         {
	  e.printStackTrace();
	  return "";
         }
      }
      
      private void ShowDialog(Long count)
      {
         if (count < 1)
         {
	  count = 1l;
         }
         mProgress = true;
         
         mProgressDialog = ProgressDialog.show(EditTxtActivity.this, "",
	     "正在加载数据...", true, true);
      }
      
      public String readWord(File file)
      {
         // 创建输入流读取doc文件
         FileInputStream in;
         String text = "";
         try
         {
	  in = new FileInputStream(file);
	  WordExtractor extractor = null;
	  // 创建WordExtractor
	  extractor = new WordExtractor();
	  // 对doc文件进行提取
	  text = extractor.extractText(in);
         }
         catch (FileNotFoundException e)
         {
	  e.printStackTrace();
         }
         catch (Exception e)
         {
	  e.printStackTrace();
         }
         return text;
      }
      
      private String data = "";
      
      
      public void ok()
      {
         final String fileEnds = txtPath.substring(
	     txtPath.lastIndexOf(".") + 1, txtPath.length()).toLowerCase();// 取出文件后缀名并转成小写
         
         if (fileEnds.equals("doc"))
         {
	  data = readWord(inFile);
         }
         else
         {
	  data = ReadStreamFile(inFile, encode);
         }
         SysEng.getInstance().addHandlerEvent(new AbsEvent()
         {
	  
	  public void ok()
	  {
	     try
	     {
	        
	        if (fileEnds.equals("html") || fileEnds.equals("htm")
		    || fileEnds.equals("xml") || fileEnds.equals("mht"))
	        {
		 webViewDetail.loadDataWithBaseURL("", data, "text/html",
		       encode, "");
		 // .loadData(data,
		 // "text/html",
		 // "utf-8");
		 textViewDetail.setVisibility(View.GONE);
		 webViewDetail.setVisibility(View.VISIBLE);
	        }
	        else if (fileEnds.equals("doc"))
	        {
		 
	        }
	        else
	        {
		 
		 textViewDetail.setText(data.toString());
		 textViewDetail.setVisibility(View.VISIBLE);
		 webViewDetail.setVisibility(View.GONE);
	        }
	     }
	     catch (Exception e)
	     {
	        e.printStackTrace();
	        Toast.makeText(EditTxtActivity.this, "内存不足加载失败",
		    Toast.LENGTH_SHORT);
	     }
	     if (mProgressDialog != null) mProgressDialog.dismiss();
	  }
         });
         
      }
   }
   
   
   protected void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.edit_txt);
      initContentView();
      Intent intent = getIntent();
      Uri uri = (Uri) intent.getData();
      txtPath = null;
      MobclickAgent.onEvent(this, "KMainPage", "EditTxt");
      if (uri != null)
      {
         txtPath = uri.getPath();
      }
      if (txtPath != null)
      {
         String encode = ChangeCharset.getEnCode(txtPath);
         mLoadData.setPath(txtPath, encode);
         // //将字节流转换为String,操作String就好。
         txtTextTitle.setText(txtPath);
         txtEncodeButton.setText(encode);
      }
      else
      {
         txtTextTitle.setText("未找到相应的文件");
      }
   }
   
   // TextWatcher mTextWatcher1 = new TextWatcher()
   // {
   // 
   // public void beforeTextChanged(
   // CharSequence s,
   // int arg1, int arg2,
   // int arg3)
   // {
   // }
   //
   // 
   // public void onTextChanged(
   // CharSequence s,
   // int arg1, int arg2,
   // int arg3)
   // {
   //
   // }
   //
   // 
   // public void afterTextChanged(
   // Editable s)
   // {
   // // if(!mSaveFlag)
   // // {
   // // mSaveFlag=true;
   // // txtTextTitle.setText(txtPath+" *");
   // // }
   // }
   // };
   
   /** 初始化 */
   private void initContentView()
   {
      txtEditText = (EditText) findViewById(R.id.EditTextDetail);
      // txtEditText.addTextChangedListener(mTextWatcher1);
      textViewDetail = (TextView) findViewById(R.id.TextViewDetail);
      txtTextTitle = (TextView) findViewById(R.id.TextViewTitle);
      webViewDetail = (WebView) findViewById(R.id.webViewDetail);
      txtEncodeButton = (Button) findViewById(R.id.ButtonEncode);
      txtEditButton = (Button) findViewById(R.id.ButtonEdit);
      txtSaveButton = (Button) findViewById(R.id.ButtonRefer);
      txtCancleButton = (Button) findViewById(R.id.ButtonExit);
      txtSaveButton.setOnClickListener(this);
      txtCancleButton.setOnClickListener(this);
      txtEditButton.setOnClickListener(this);
      txtEncodeButton.setOnClickListener(this);
   }
   
   public void onClick(View view)
   {
      switch (view.getId())
      {
      case R.id.ButtonEncode:
         new TextEncodeDialog().ShowDialog(this, 0, this);
         break;
      case R.id.ButtonEdit:
         LoadEdit();
         break;
      case R.id.ButtonRefer:
         saveTxt();
         break;
      case R.id.ButtonExit:
         // Intent intent = new
         // Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
         // intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
         // RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
         // intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "您的语音将转化为文字");
         // startActivityForResult(intent, 1);
         EditTxtActivity.this.finish();
         break;
      }
   }
   
   private void LoadEdit()
   {
      txtEncodeButton.setVisibility(View.GONE);
      txtSaveButton.setVisibility(View.VISIBLE);
      txtEditButton.setVisibility(View.GONE);
      txtEditText.setVisibility(View.VISIBLE);
      textViewDetail.setVisibility(View.GONE);
      txtEditText.setText(textViewDetail.getText());
      // mSaveFlag = false;
   }
   
   /** 保存文本文件 */
   private void saveTxt()
   {
      String newData = txtEditText.getText().toString();
      if (ChangeCharset.Write(txtPath, newData) == 1)
      {
         Toast.makeText(EditTxtActivity.this, "保存成功", Toast.LENGTH_SHORT)
	     .show();
      }
      else
      {
         Toast.makeText(EditTxtActivity.this, "保存失败!", Toast.LENGTH_SHORT)
	     .show();
      }
      this.finish();
   }
   
   
   public void NotifyDataSetChanged(int cmd, Object value)
   {
      // TODO Auto-generated method stub
      mLoadData.setPath(txtPath, (String) value);
      txtEncodeButton.setText((String) value);
      // String data = ChangeCharset.Read(txtPath, (String) value);
      // textViewDetail.setText(data.toString());
      // txtEncodeButton.setText((String) value);
   }
}
