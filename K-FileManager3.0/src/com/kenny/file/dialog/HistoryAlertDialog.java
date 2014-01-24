package com.kenny.file.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kenny.KFileManager.R;
import com.kenny.file.manager.FileManager;
import com.kenny.file.util.Const;

public class HistoryAlertDialog implements OnClickListener,
      android.view.View.OnClickListener
{
   private Activity m_context;
   private TextView tvDemo;
   // private AlertDialog.Builder alertDialog;
   private Dialog alertDialog;
   private View alertView;
   private ListView lvHistorylist;
   
   public void ShowDialog(Activity context)
   {
      m_context = context;

      alertDialog = new Dialog(context);
      alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// Window.FEATURE_CUSTOM_TITLE
      LayoutInflater factory = LayoutInflater.from(context);
      alertView = factory.inflate(R.layout.alert_dialog_history, null);
      alertDialog.setContentView(alertView);
      
      // tvDemo = (TextView) alertView.findViewById(R.id.tvDemo);
      final Button btSDCard = (Button) alertView.findViewById(R.id.btSDCard);
      btSDCard.setOnClickListener(this);
      final Button btRoot = (Button) alertView.findViewById(R.id.btRoot);
      btRoot.setOnClickListener(this);
      
      final Button btClearHistory = (Button) alertView
	  .findViewById(R.id.btClearHistory);
      btClearHistory.setOnClickListener(this);
      lvHistorylist = (ListView) alertView.findViewById(R.id.lvHistorylist);
//      KHistoryManage mHistory = KHistoryManage.GetHandler();
//      HistoryAdapter mHistoryAdapter = KHistoryManage.GetHandler().ReadRAMFile(
//	  context);
//      lvHistorylist.setAdapter(mHistoryAdapter);
//      lvHistorylist.setOnItemClickListener(mHistory);
      //builder.setNegativeButton(m_context.getString(R.string.btCancel), this);
      alertDialog.show();
      //builder.show();
   }
   
   
   public void onClick(DialogInterface dialog, int which)
   {
      
   }
   
   
   public void onClick(View v)
   {
      switch(v.getId())
      {
      case R.id.btRoot:
         FileManager.getInstance().setFilePath(Const.Root);
         break;
      case R.id.btSDCard:
         FileManager.getInstance().setFilePath(Const.getSDCard());
         break;
      case R.id.btClearHistory:
//         KHistoryManage.GetHandler().Clear();
//         KHistoryManage.GetHandler().SaveFile();
         break;
      }
      if (alertDialog != null)
      {
         alertDialog.dismiss();
      }
   }
}
