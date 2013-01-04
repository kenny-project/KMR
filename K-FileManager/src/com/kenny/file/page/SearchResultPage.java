package com.kenny.file.page;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.framework.event.ParamEvent;
import com.framework.log.P;
import com.framework.page.AbsPage;
import com.framework.syseng.SysEng;
import com.kenny.KFileManager.R;
import com.kenny.file.Adapter.FavorFileAdapter;
import com.kenny.file.Adapter.FileAdapter;
import com.kenny.file.Event.FavoriteFileEvent;
import com.kenny.file.Event.openDefFileEvent;
import com.kenny.file.bean.FileBean;
import com.kenny.file.bean.ScarchParam;
import com.kenny.file.dialog.SearchFileDialog;
import com.kenny.file.struct.INotifyDataSetChanged;
import com.kenny.file.util.Const;
import com.kenny.file.util.FileManager;

public class SearchResultPage extends AbsPage implements OnItemClickListener,
      INotifyDataSetChanged
{
   private Button btnBack,btSearch;
   private ListView m_locallist;
   private FavorFileAdapter fileAdapter;
   private ScarchParam param=new ScarchParam();
   public ScarchParam getParam()
   {
      return param;
   }

   private ArrayList<FileBean> mFileList=new ArrayList<FileBean>();

   public SearchResultPage(Activity context)
   {
      super(context);
      param.setSearchItems(mFileList);
   }
   public int Size()
   {
      return param.getSearchItems().size();
   }
   public void onCreate()
   {
      setContentView(R.layout.searchresultpage);
      
      btSearch = (Button) findViewById(R.id.btSearch);
      btSearch.setOnClickListener(new OnClickListener()
      {
         
         public void onClick(View v)
         {
	  new SearchFileDialog().Show(m_act,  FileManager.GetHandler().getCurrentPath(),param,SearchResultPage.this);
         }
      });

      btnBack = (Button) findViewById(R.id.btBack);
      btnBack.setOnClickListener(new OnClickListener()
      {
         
         public void onClick(View v)
         {
	  backKey();
         }
      });
      m_locallist = (ListView) findViewById(R.id.lvLocallist);
      fileAdapter = new FavorFileAdapter(m_act, 1, mFileList);
      m_locallist.setAdapter(fileAdapter);
      m_locallist.setOnScrollListener(m_localOnScrollListener);
      m_locallist.setOnItemClickListener(this);
      if(mFileList.size()==0)
      {
         new SearchFileDialog().Show(m_act,  FileManager.GetHandler().getCurrentPath(),param,this);
      }
   }
   
   
   public boolean onTouchEvent(MotionEvent event)
   {
      // TODO Auto-generated method stub
      return false;
   }
   
   private OnScrollListener m_localOnScrollListener = new OnScrollListener()
   {
      
      
      public void onScrollStateChanged(AbsListView view, int scrollState)
      {
         switch (scrollState)
         {
         case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
	  P.debug("SCROLL_STATE_FLING");
	  if (fileAdapter != null) fileAdapter.setShowLogo(false);
	  break;
         case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
	  P.debug("SCROLL_STATE_IDLE");
	  if (fileAdapter != null)
	  {
	     fileAdapter.setShowLogo(true);
	     fileAdapter.notifyDataSetChanged();
	  }
	  break;
         case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
	  P.debug("SCROLL_STATE_TOUCH_SCROLL");
	  if (fileAdapter != null) fileAdapter.setShowLogo(false);
	  break;
         default:
	  break;
         }
      }
      
      
      public void onScroll(AbsListView view, int firstVisibleItem,
	  int visibleItemCount, int totalItemCount)
      {
      }
   };
   
   
   public void onResume()
   {
      // TODO Auto-generated method stub
      
   }
   
   
   public void onPause()
   {
      // TODO Auto-generated method stub
      
   }
   
   
   public void onDestroy()
   {
      // TODO Auto-generated method stub
      
   }
   
   
   public void clear()
   {
      // TODO Auto-generated method stub
      
   }
   
   
   public void onItemClick(AdapterView<?> parent, View view, int position,
         long id)
   {
      FileBean temp = mFileList.get(position);
      if (temp != null)
      {
         final File mFile = temp.getFile();
         SysEng.getInstance().addEvent(new FavoriteFileEvent(m_act, temp, 1));
         // 如果该文件是可读的，我们进去查看文件
         if (mFile.isDirectory())
         {
	  if (mFile.canRead())
	  {
	     FileManager.GetHandler().setFilePath(mFile.getPath());
	     backKey();
	  }
	  else
	  {// 如果该文件不可读，我们给出提示不能访问，防止用户操作系统文件造成系统崩溃等
	     Toast.makeText(m_act, "该文件夹不存在或权限不够!", Toast.LENGTH_SHORT)
		 .show();
	  }
         }
         else
         {
	  if (mFile.canRead())
	  {
	     if (!mFile.exists())
	     {
	        Toast.makeText(m_act, "未找到该文件!", Toast.LENGTH_SHORT).show();
	        return;
	     }
	     SysEng.getInstance().addHandlerEvent(
		 new openDefFileEvent(m_act, mFile.getPath()));
	  }
	  else
	  {// 如果该文件不可读，我们给出提示不能访问，防止用户操作系统文件造成系统崩溃等
	     Toast.makeText(m_act, "对不起，访问权限不够!", Toast.LENGTH_SHORT).show();
	  }
         }
      }
   }
   
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu)
   {
      // TODO Auto-generated method stub
      menu.clear();
      return super.onCreateOptionsMenu(menu);
   }
   public void NotifyDataSetChanged(int cmd, Object value)
   {
      mNotifyData.setKey(cmd);
      mNotifyData.setValue(value);
      SysEng.getInstance().addHandlerEvent(mNotifyData);
   }
   
   private ParamEvent mNotifyData = new ParamEvent()
   {
      
      public void ok()
      {
         P.v("SearchResult getKey()=" + getKey());
         
         switch (getKey())
         {
         case Const.cmd_LoadSDFile_Error:
	  // pbLoading.setVisibility(View.GONE);
	  break;
         case Const.cmd_LoadSDFile_Init:
	  // Long value=(Long)getValue();
	  // pbSDFileStatus.setMax(value.intValue());
	  // pbSDFileStatus.setProgress(0);
	  // tvMessage.setText("正在遍历文件...");
	  // pbSDFileMsg.setVisibility(View.GONE);
	  break;
         case Const.cmd_LoadSDFile_Start:
	  // pbLoading.setVisibility(View.VISIBLE);
	  // tvMessage.setText("正在获取文件数,请耐心等待!\n根据SD卡空间大小的不同遍历时间不等");
	  // mListView.setVisibility(View.GONE);
	  // mGridView.setVisibility(View.GONE);
	  // btRefresh.setVisibility(View.GONE);
	  // pbSDFileMsg.setVisibility(View.GONE);
	  break;
         case Const.cmd_LoadSDFile_State:
	  fileAdapter.notifyDataSetChanged();
	  // LoadSDFileEvent.LoadSDFile_State staValue =
	  // (LoadSDFileEvent.LoadSDFile_State) getValue();
	  // pbSDFileStatus.setProgress(staValue.Progress);
	  // pbSDFileStatus.setMax(staValue.count);
	  // tvMessage.setText(staValue.strPath);
	  break;
         case Const.cmd_LoadSDFile_Finish:
	  // pbLoading.setVisibility(View.GONE);
	  // SwitchStyle(bFlag, bStyle);
	  fileAdapter.notifyDataSetChanged();
//	  Toast.makeText(m_act, m_act.getString(R.string.msg_Scan_Finish),
//	        Toast.LENGTH_SHORT).show();
	  break;
         }
      }
   };
}
