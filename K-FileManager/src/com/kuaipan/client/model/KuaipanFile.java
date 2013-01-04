package com.kuaipan.client.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.widget.Toast;

import com.framework.event.AbsEvent;
import com.framework.log.P;
import com.framework.syseng.SysEng;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.bean.FileBean;
import com.kenny.file.util.Const;
import com.kuaipan.client.KuaipanAPI;
import com.kuaipan.client.ProgressListener;
import com.kuaipan.client.exception.KuaipanAuthExpiredException;
import com.kuaipan.client.exception.KuaipanIOException;
import com.kuaipan.client.exception.KuaipanServerException;

public class KuaipanFile extends FileBean implements Runnable
{
   public String file_id;
   public String hash;
   public String root;
   public String rev;
   public Date create_time = null;
   public Date modify_time = null;
   public boolean is_deleted = false;
   public String type = "file";
   public List<KuaipanFile> files = null;
   private Context mContext = null;
   protected String KuaiPanPath; // 文件路径
   private String msg = "";
   public int size = 0;
   
   public String getKuaiPanFolderPath()
   {
      return KuaiPanPath;
   }
   
   public void setKuaiPanFolderPath(String path)
   {
      if (!path.endsWith("/"))
      {
         KuaiPanPath = path + "/";
      }
      else
      {
         KuaiPanPath = path;
      }
      this.setFolderPath(Const.szKuaiPanPath + KuaiPanPath);
      this.setFilePath(Const.szKuaiPanPath + KuaiPanPath + getFileName());
      mFile = new File(this.getFilePath());
   }
   
   public void setContext(Context mContext)
   {
      this.mContext = mContext;
   }
   
   public KuaipanFile()
   {
      
   }
   
   public KuaipanFile(Map<String, Object> map)
   {
      parseFromMap(map);
   }
   
   protected void parseFromMap(Map<String, Object> map)
   {
      this.type = (String) map.get("type");
      this.size = convert2Int(map.get("size"));
      
      this.setFileName((String) map.get("name"));
      // this.hash = (String) map.get("hash");
      // this.root = (String) map.get("root");
      this.setFilePath((String) map.get("path"));
      // P.v("wmh", "parseFromMap:path=" + getFilePath());
      // this.rev = (String) map.get("rev");
      
      // this.create_time = convert2Date(map.get("create_time"));
      // this.modify_time = convert2Date(map.get("modify_time"));
      this.setLength(convert2Log(map.get("size")));
      this.is_deleted = convert2Boolean(map.get("is_deleted"));
      this.type = (String) map.get("type");
      if (type == null || type.equals("folder"))
      {
         this.setDirectory(true);
      }
      else
      {
         this.setDirectory(false);
      }
      
      @SuppressWarnings("unchecked")
      Collection<Map<String, Object>> files = (Collection<Map<String, Object>>) map
	  .get("files");
      if (files != null)
      {
         Iterator<Map<String, Object>> it = files.iterator();
         this.files = new LinkedList<KuaipanFile>();
         while (it.hasNext())
         {
	  KuaipanFile temp_file = new KuaipanFile(it.next());
	  this.files.add(temp_file);
         }
      }
      
   }
   
   protected void parseFromMap1(Map<String, Object> map)
   {
      // this.file_id = (String) map.get("file_id");
      this.setFileName((String) map.get("name"));
      // this.hash = (String) map.get("hash");
      // this.root = (String) map.get("root");
      this.setFilePath((String) map.get("path"));
      // P.v("wmh", "parseFromMap:path=" + getFilePath());
      // this.rev = (String) map.get("rev");
      
      // this.create_time = convert2Date(map.get("create_time"));
      // this.modify_time = convert2Date(map.get("modify_time"));
      this.setLength(convert2Log(map.get("size")));
      this.is_deleted = convert2Boolean(map.get("is_deleted"));
      this.type = (String) map.get("type");
      if (type == null || type.equals("folder"))
      {
         this.setDirectory(true);
      }
      else
      {
         this.setDirectory(false);
      }
      
      @SuppressWarnings("unchecked")
      Collection<Map<String, Object>> files = (Collection<Map<String, Object>>) map
	  .get("files");
      if (files != null)
      {
         this.setItemCount(files.size());
         Iterator<Map<String, Object>> it = files.iterator();
         this.files = new LinkedList<KuaipanFile>();
         while (it.hasNext())
         {
	  KuaipanFile temp_file = new KuaipanFile(it.next());
	  if (!temp_file.is_deleted)
	  {
	     this.files.add(temp_file);
	  }
         }
      }
   }
   
   public boolean exists()
   {
      if (super.exists())
      {
         if (mFile.length() == length) { return true; }
      }
      return false;
   }
   
   public Date convert2Date(Object obj)
   {
      if (obj == null) return null;
      
      DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      Date date = null;
      
      try
      {
         date = format.parse((String) obj);
      }
      catch (ParseException e)
      {
         e.printStackTrace();
      }
      return date;
   }
   
   public boolean convert2Boolean(Object obj)
   {
      if (obj == null) return false;
      if (obj instanceof Boolean) return ((Boolean) obj).booleanValue();
      return ((String) obj).toLowerCase().equals("true");
   }
   
   public int convert2Int(Object obj)
   {
      int ret = 0;
      if (obj != null)
      {
         if (obj instanceof Number)
         {
	  ret = ((Number) obj).intValue();
         }
         else if (obj instanceof String)
         {
	  ret = Integer.parseInt((String) obj);
         }
      }
      return ret;
   }
   
   public Long convert2Log(Object obj)
   {
      Long ret = 0l;
      if (obj != null)
      {
         if (obj instanceof Number)
         {
	  ret = ((Number) obj).longValue();
         }
         else if (obj instanceof String)
         {
	  ret = Long.valueOf((String) obj);
         }
      }
      return ret;
   }
   
   private ProgressDialog mProgressDialog = null;
   
   private void ShowDialog()
   {
      // LayoutInflater factory = LayoutInflater.from(mContext);
      // final View view =
      // factory.inflate(R.layout.alert_dialog_load_sdcard_file,
      // null);
      // Builder mBuilder = new AlertDialog.Builder(mContext);
      // mBuilder.setTitle("正在打开文件");
      // mBuilder.setView(view);
      // TextView tvMessage = (TextView) view.findViewById(R.id.tvMessage);
      // mBuilder.setNegativeButton(mContext.getString(R.string.cancel),
      // new DialogInterface.OnClickListener()
      // {
      //
      // public void onClick(DialogInterface dialog, int which)
      // {
      //
      // }
      // });
      // mProgressDialog = mBuilder.create();
      // mProgressDialog.show();
      
      // mProgressDialog = ProgressDialog.show(mContext, "", "正在下载文件...", true,
      // true);
      // mProgressDialog.setCancelable(false);
      // mProgressDialog.show();
      progress();
   }
   
   private boolean bCancel = false;
   
   public void progress()
   {
      mProgressDialog = new ProgressDialog(mContext);
      mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      // progressDialog.setMessage(message);
      mProgressDialog.setTitle("正在下载文件");
      mProgressDialog.setProgress(0);
      mProgressDialog.setMax(1000);
      mProgressDialog.show();
      bCancel = false;
      mProgressDialog.setOnCancelListener(new OnCancelListener()
      {
         
         public void onCancel(DialogInterface dialog)
         {
	  bCancel = true;
         }
      });
   }
   
   private String TAG = "kuaipan";
   private FileOutputStream os = null;
   
   public void run()
   {
      try
      {
         if (exists())
         {
	  SysEng.getInstance().addHandlerEvent(
	        new openDefFileEvent(mContext, getFilePath()));
         }
         else
         {
	  SysEng.getInstance().addHandlerEvent(new AbsEvent()
	  {
	     
	     public void ok()
	     {
	        ShowDialog();
	     }
	  });
	  new File(getFolderPath()).mkdirs();
	  try
	  {
	     if (mFile == null)
	     {
	        mFile = new File(this.getFilePath());
	     }
	     if (mFile.exists())
	     {
	        mFile.delete();
	     }
	     mFile.createNewFile();
	     os = new FileOutputStream(mFile);
	  }
	  catch (FileNotFoundException e)
	  {
	     e.printStackTrace();
	  }
	  KuaipanHTTPResponse resp = KuaipanAPI.downloadFile(
	        getKuaiPanFolderPath() + getFileName(), os,
	        new ProgressListener()
	        {
		 public void started()
		 {
		    P.v(TAG, "started");
		 }
		 public void processing(final long bytes, final long total)
		 {
		    SysEng.getInstance().addHandlerEvent(new AbsEvent()
		    {
		       public void ok()
		       {
			double per = bytes * 1000.0 / total;
			P.v(TAG, "per=" + per);
			mProgressDialog.setProgress((int) (per));
			// /mProgressDialog.setMax((int) (1000));
		       }
		    });
		 }
		 
		 public int getUpdateInterval()
		 {
		    // TODO Auto-generated method stub
		    P.v(TAG, "getUpdateInterval");
		    return 100;
		 }
		 
		 public void completed()
		 {
		    try
		    {
		       if (os != null) os.close();
		    }
		    catch (IOException e)
		    {
		       // TODO Auto-generated catch block
		       e.printStackTrace();
		    }
		    P.v(TAG, "completed");
		    if (!bCancel) SysEng.getInstance().addHandlerEvent(
		          new openDefFileEvent(mContext, getFilePath()));
		 }
		 
		 public boolean cancel()
		 {
		    return bCancel;
		 }
	        });
	  os.close();
         }
      }
      catch (KuaipanServerException e)
      {
         bCancel = true;
         switch (e.code)
         {
         case 302:
	  msg = "文件在另外的服务器上，请处理好跳转";
	  break;
         case 401:
	  msg = "授权失败，参考";
	  break;
         case 403:
	  msg = "文件不存在或者无权访问";
	  break;
         case 404:
	  msg = "文件不存在";
	  break;
         case 500:
         case 507:
         default:
	  msg = "	文件不存在或者服务器内部错误";
	  break;
         }
         e.printStackTrace();
      }
      catch (KuaipanIOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
         bCancel = true;
      }
      catch (KuaipanAuthExpiredException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
         bCancel = true;
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
         bCancel = true;
      }
      
      if (bCancel)
      {
         mFile.delete();
      }
      SysEng.getInstance().addHandlerEvent(new AbsEvent()
      {
         
         public void ok()
         {
	  if (mProgressDialog != null) mProgressDialog.dismiss();
	  if (msg.length() > 0) Toast.makeText(mContext, msg,
	        Toast.LENGTH_SHORT).show();
         }
      });
   }
}
