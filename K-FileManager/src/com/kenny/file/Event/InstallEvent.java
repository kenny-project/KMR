package com.kenny.file.Event;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.framework.event.AbsEvent;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.bean.FileBean;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.tools.T;
import com.kenny.file.util.FileManager;

/**
 * @author kenny 初始化event
 * */
public class InstallEvent extends AbsEvent
{
   private List<FileBean> list;
   private Context m_context;
   private ProgressDialog mProgressDialog;
   private boolean mProgress = false;
   private INotifyDataSetChanged notifySetChanged = null;
   
   public InstallEvent(Context context, List<FileBean> list,
         INotifyDataSetChanged notifySetChanged)
   {
      this.list = list;
      this.notifySetChanged = notifySetChanged;
      this.m_context = context;
      ShowDialog(list);
   }
   
   public InstallEvent(Context context, List<FileBean> list)
   {
      this.list = list;
      this.m_context = context;
      ShowDialog(list);
   }
   
   public InstallEvent(Context context, FileBean fileBean)
   {
      this.list = new ArrayList<FileBean>();
      list.add(fileBean);
      this.m_context = context;
      ShowDialog(list);
   }
   
   @Override
   public void ok()
   {
      Long count = 0l;
      for (int i = 0; i < list.size(); i++)
      {
         count += T.FileCount(list.get(i).getFilePath());
      }
      mProgressDialog.setMax(count.intValue());
      InstallFile(list);
      SysEng.getInstance().addHandlerEvent(new AbsEvent()
      {
         @Override
         public void ok()
         {
	  FileManager.GetHandler().Refresh();
	  if (notifySetChanged != null)
	  {
	     notifySetChanged.NotifyDataSetChanged(0, null);
	  }
	  mProgressDialog.dismiss();
         }
      });
   }
   
   private void ShowDialog(List<FileBean> mFileList)
   {
      mProgress = true;
      mProgressDialog = new ProgressDialog(m_context);
      mProgressDialog.setTitle(m_context
	  .getString(R.string.installFileEvent_Title));
      // TextView text=new TextView(m_context);
      // text.setText("fdsafdsafdsa");
      // WindowManager.LayoutParams params=new WindowManager.LayoutParams();
      // params.width=WindowManager.LayoutParams.FILL_PARENT;
      // mProgressDialog.addContentView(text, params);
      mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      mProgressDialog.setMax(mFileList.size());
      mProgressDialog.setButton(m_context.getString(R.string.cancel),
	  new DialogInterface.OnClickListener()
	  {
	     public void onClick(DialogInterface dialog, int whichButton)
	     {
	        mProgress = false;
	     }
	  });
      mProgressDialog.setProgress(0);
      mProgressDialog.show();
   }
   
   /** 按装已选择的应用 */
   private void InstallFile(List<FileBean> list)
   {
      for (int i = 0; i < list.size() && mProgress; i++)
      {
         File temp = list.get(i).getFile();
         mProgressDialog.incrementProgressBy(1);

         new openDefFileEvent(m_context,temp.getAbsolutePath()).run();
      }
   }
}
