//package com.kenny.file.page;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import android.app.Activity;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.framework.page.AbsPage;
//import com.kenny.KFileManager.R;
//
//public class FeedBackPage extends AbsPage
//{
//   public FeedBackPage(Activity context)
//   {
//      super(context);
//   }
//   
//   public void onCreate()
//   {
//      setContentView(R.layout.umeng_fb_send_feedback);
//      
//
//      //
//      // btnBack = (Button) findViewById(R.id.btBack);
//      // btnBack.setOnClickListener(new OnClickListener()
//      // {
//      //
//      // public void onClick(View v)
//      // {
//      // backKey();
//      // }
//      // });
//      final FeedBackListener listener = new FeedBackListener()
//      {
//         public void onSubmitFB(Activity activity)
//         {
//	  EditText phoneText = (EditText) activity
//	        .findViewById(R.id.feedback_phone);
//	  EditText qqText = (EditText) activity
//	        .findViewById(R.id.feedback_qq);
//	  EditText nameText = (EditText) activity
//	        .findViewById(R.id.feedback_name);
//	  EditText emailText = (EditText) activity
//	        .findViewById(R.id.feedback_email);
//	  Map<String, String> contactMap = new HashMap<String, String>();
//	  contactMap.put("phone", phoneText.getText().toString());
//	  contactMap.put("qq", qqText.getText().toString());
//	  UMFeedbackService.setContactMap(contactMap);
//	  Map<String, String> remarkMap = new HashMap<String, String>();
//	  remarkMap.put("name", nameText.getText().toString());
//	  remarkMap.put("email", emailText.getText().toString());
//	  UMFeedbackService.setRemarkMap(remarkMap);
//         }
//         public void onResetFB(Activity activity,
//	     Map<String, String> contactMap, Map<String, String> remarkMap)
//         {
//	  // TODO Auto-generated method stub`
//	  // FB initialize itself,load other attribute
//	  // from local storage and set them
//	  EditText phoneText = (EditText) activity
//	        .findViewById(R.id.feedback_phone);
//	  EditText qqText = (EditText) activity
//	        .findViewById(R.id.feedback_qq);
//	  EditText nameText = (EditText) activity
//	        .findViewById(R.id.feedback_name);
//	  EditText emailText = (EditText) activity
//	        .findViewById(R.id.feedback_email);
//	  if (remarkMap != null)
//	  {
//	     nameText.setText(remarkMap.get("name"));
//	     emailText.setText(remarkMap.get("email"));
//	  }
//	  if (contactMap != null)
//	  {
//	     phoneText.setText(contactMap.get("phone"));
//	     qqText.setText(contactMap.get("qq"));
//	  }
//         }
//      };
//      UMFeedbackService.setFeedBackListener(listener);
//      TextView btSearch = (TextView) findViewById(R.id.umeng_fb_submit);
//      btSearch.setOnClickListener(new OnClickListener()
//      {
//     
//      public void onClick(View v)
//      {
//         listener.onSubmitFB(m_act);
//      }
//      });
//   }
//   
//   public boolean onTouchEvent(MotionEvent event)
//   {
//      // TODO Auto-generated method stub
//      return false;
//   }
//   
//   public void onResume()
//   {
//      // TODO Auto-generated method stub
//      
//   }
//   
//   public void onPause()
//   {
//      // TODO Auto-generated method stub
//      
//   }
//   
//   public void onDestroy()
//   {
//      // TODO Auto-generated method stub
//      
//   }
//   
//   public void clear()
//   {
//      // TODO Auto-generated method stub
//      
//   }
//}
