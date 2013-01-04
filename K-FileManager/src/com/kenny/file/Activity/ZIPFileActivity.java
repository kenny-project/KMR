package com.kenny.file.Activity;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Adapter.ZipFileAdapter;
import com.kenny.file.Event.openZipFileEvent;
import com.kenny.file.bean.ZipFileBean;
import com.kenny.file.sort.FileSort;
import com.kenny.file.tools.ZIP;
import com.kenny.file.util.Const;

/**
 * ZIP文件操作
 * 
 * @author WangMinghui
 * 
 */
public class ZIPFileActivity extends Activity implements OnItemClickListener
{
   private TextView txtTextTitle;
   private ListView lvlist;
   private String strZipPath = null;
   // private boolean mSaveFlag = false; // false: 未保存 true:保存
   private LoadData mLoadData = new LoadData();
   private HashMap<String, ZipFileBean> mAllist = new HashMap<String, ZipFileBean>();
   private Vector<ZipFileBean> mlist = new Vector<ZipFileBean>();
   private ZipFileAdapter mFileAdapter;
   
   private class LoadData extends AbsEvent
   {
      private ProgressDialog mProgressDialog = null;
      private File inFile;
      
      public void setPath(String path)
      {
         inFile = new File(path);
         Long len = inFile.length() / 1024;
         if (len > 16)
         {
	  ShowDialog(len);
         }
         P.v("len=" + len);
         SysEng.getInstance().addEvent(this);
      }
      
      private void ShowDialog(Long count)
      {
         if (count < 1)
         {
	  count = 1l;
         }
         mProgressDialog = ProgressDialog.show(ZIPFileActivity.this, "",
	     "正在加载数据...", true, true);
      }
      
      public void ok()
      {
         // TODO Auto-generated method stub
         try
         {
	  
	  mAllist.putAll(ZIP.getZipFileBeans(inFile));
         }
         catch (Exception e1)
         {
	  // TODO Auto-generated catch block
	  e1.printStackTrace();
         }
         
         SysEng.getInstance().addHandlerEvent(new AbsEvent()
         {
	  
	  public void ok()
	  {
	     try
	     {
	        mlist.clear();
	        mlist.addAll(mAllist.values());
	        Collections.sort(mlist, new FileSort());
	        mFileAdapter.notifyDataSetChanged();
	     }
	     catch (Exception e)
	     {
	        e.printStackTrace();
	        Toast.makeText(ZIPFileActivity.this, "内存不足加载失败",
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
      setContentView(R.layout.zf_manager);
      initContentView();
      Intent intent = getIntent();
      Uri uri = (Uri) intent.getData();
      strZipPath = null;
      if (uri != null)
      {
         strZipPath = uri.getPath();
      }
      if (strZipPath != null)
      {
         mLoadData.setPath(strZipPath);
         // //将字节流转换为String,操作String就好。
         txtTextTitle.setText(strZipPath);
      }
      else
      {
         txtTextTitle.setText("未找到相应的文件");
      }
   }
   
   /** 初始化 */
   private void initContentView()
   {
      txtTextTitle = (TextView) findViewById(R.id.TextViewTitle);
      lvlist = (ListView) findViewById(R.id.lvlist);
      mlist.addAll(mAllist.values());
      mFileAdapter = new ZipFileAdapter(this, 1, mlist);
      lvlist.setAdapter(mFileAdapter);
      lvlist.setOnItemClickListener(this);
   }
   
   public void onItemClick(AdapterView<?> parent, View view, int position,
         long id)
   {
      ZipFileBean temp = mlist.get(position);
      if (temp.isDirectory())
      {
         mlist.clear();
         mlist.addAll(temp.getCollectionItem());
         mFileAdapter.notifyDataSetChanged();
         return;
      }
      else
      {
//         SysEng.getInstance().addEvent(
//	     new openZipFileEvent(this, strZipPath, Const.szZipPath, temp
//	           .getFilePath()));
         SysEng.getInstance().addEvent(
	     new openZipFileEvent(this, strZipPath, strZipPath.substring(0,strZipPath.length()-4)+File.separator, temp
	           .getFilePath()));
      }
   }
}
