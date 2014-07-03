package com.work.market.view;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.byfen.market.R;
import com.work.market.net.Common;
import com.work.market.net.HttpUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;




public class ProductaboutView extends LinearLayout {

    private Context  mContext;
    private Activity m_MainActivity;
  
    
    private  ProgressDialog   pd;
    
   
    private ListView lv1;
    private String m_url;
    private ArrayList<String>  m_list_url;
    
    
   
    private  TextView    m_product_content;//
    private  RelativeLayout  m_RelativeLayout;
    private  SoftlistView  m_SoftlistView_command  ;
    
    
    
    
   
    
    
    
    
   
    
    

 
	
	public ProductaboutView(Context context,Activity aActivity) {
        this(context, null,aActivity);
        mContext = context;
    }

    public ProductaboutView(Context context, AttributeSet attrs,Activity aActivity) {
        super(context, attrs);
        mContext = context;
        m_MainActivity = aActivity;
        LayoutInflater.from(context).inflate(R.layout.product_about, this, true);
        m_RelativeLayout = (RelativeLayout)findViewById(R.id.product_about_last_view);
        m_SoftlistView_command = new SoftlistView(mContext);
//		m_SoftlistView_command.SetUrl("http://api.byfen.com/list/recommand?" + "kind="+ m_type+"&");
        
        m_RelativeLayout.addView(m_SoftlistView_command);
		
	


}
    
   
  	
  	
  	
  	
}
