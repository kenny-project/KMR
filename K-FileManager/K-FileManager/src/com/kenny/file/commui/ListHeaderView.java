package com.kenny.file.commui;


import android.content.Context;   
import android.util.AttributeSet;   
import android.view.View;   
import android.widget.LinearLayout;   
  
public class ListHeaderView extends LinearLayout 
{   
    public ListHeaderView(Context context, AttributeSet attrs,View view) {   
        super(context, attrs);   
        initialize(context,view);   
    }   
  
    public ListHeaderView(Context context,View view) {   
        super(context);   
        initialize(context,view);   
    }   
  
    private void initialize(Context context,View view) {   
//        this.context = context;   
//        LayoutInflater inflater =LayoutInflater.from(context);
//        
//        View view = inflater.inflate(R.layout.demo_list_item_header_view, null);   
        //textView = (TextView) view.findViewById(R.id.headerTextView);   
        addView(view);   
    }   
}  

