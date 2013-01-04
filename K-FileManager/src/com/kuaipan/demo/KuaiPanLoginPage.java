package com.kuaipan.demo;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.framework.page.AbsPage;
import com.kenny.KFileManager.R;

public class KuaiPanLoginPage extends AbsPage

{
   private Button btnBack;
   private WebView tvStatus;
   private String defurl = "http://joke.km530.com/e/m/index.php?file=gbook";
   private SimpleCommandConsole cli;
   public KuaiPanLoginPage(Activity context, String url)
   {
      super(context);
      defurl=url;
   }
   
   public void onCreate()
   {
      setContentView(R.layout.kuaipanloginpage);
      btnBack = (Button) findViewById(R.id.btBack);
      btnBack.setOnClickListener(new OnClickListener()
      {
         
         public void onClick(View v)
         {
	  backKey();
         }
      });
      tvStatus = (WebView) findViewById(R.id.wvWebSide);
      tvStatus.setSelected(false);
      WebSettings webSettings = tvStatus.getSettings();
      webSettings.setJavaScriptEnabled(true);
      tvStatus.setWebChromeClient(new WebChromeClient());
      tvStatus.loadUrl(defurl);
      cli = SimpleCommandConsole.getHandler(m_act);
   }
   public boolean backKey()
   {
      cli.execute("access","");
      return super.backKey();
   }
   
   public boolean onTouchEvent(MotionEvent event)
   {
      // TODO Auto-generated method stub
      return false;
   }
   
   
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
      cli.execute("access","");
   }
   
   
   public void clear()
   {
      // TODO Auto-generated method stub
      
   }
}
