package com.kenny.file.dialog;


import com.kenny.KFileManager.t.R;
import com.kenny.file.interfaces.KActivityStatus;
import com.kenny.file.tools.SaveData;
import com.kenny.file.util.Theme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
public class FontSizeDialog implements OnClickListener
{
    private Activity m_context;
    private int FontMixSize = 12;
    private int FontMAxSize = 35;
    private SeekBar mSeekBar;
    private TextView tvDemo;
    private AlertDialog.Builder alertDialog;
    public void ShowDialog(Activity context, final KActivityStatus result,
            final boolean ktag)
    {
        m_context = context;
        
        int fontSize = SaveData.Read(context, "HCFontSize",
                FontMixSize);
        if (fontSize < FontMixSize)
        {
            fontSize = FontMixSize;
        }
        alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle("设置字体字号");
        LayoutInflater factory = LayoutInflater.from(context);
        final View alertSeekView = factory.inflate(
                R.layout.alert_dialog_fontsize, null);
        alertDialog.setView(alertSeekView);
        
        tvDemo=(TextView) alertSeekView
        .findViewById(R.id.tvDemo);
        final TextView tvFontSize=(TextView) alertSeekView
        .findViewById(R.id.tvFontSize);
        
        tvDemo.setVisibility(View.VISIBLE);
        tvDemo.setTextSize(fontSize);
        mSeekBar = (SeekBar) alertSeekView
                .findViewById(R.id.alert_sbDisplayLuminosity);
        mSeekBar.setMax(FontMAxSize-FontMixSize);
        mSeekBar.setProgress(fontSize-FontMixSize);
        // 滚动条滑动效果
        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromTouch)
            {
                int valuse=progress+FontMixSize;
                tvDemo.setTextSize(valuse);
                tvFontSize.setText("设置字体为"+valuse+"像素");
            }
            
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                // TODO Auto-generated method stub
                
            }
            
            
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                // TODO Auto-generated method stub
                
            }
        }

        );
        // int fontindex=(fontSize-fontSizedef)/3;
        // test.setSingleChoiceItems(R.array.select_dialog_FontSize, fontindex,
        // this);
        
        alertDialog.setNegativeButton("取消", this);
        alertDialog.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                int value=mSeekBar.getProgress();
                int writefontSize = value + FontMixSize;
                Theme.setTextFontSize(writefontSize);
                SaveData.Write(m_context, "HCFontSize",
                        writefontSize);
                if (result != null)
                {
                    result.KActivityResult(1,1, 0, "");
                }
            }
        });
        alertDialog.create();
        alertDialog.show();
    }
    
    public void onClick(DialogInterface dialog, int which)
    {
        // TODO Auto-generated method stub
       // FontSize = which;
    }
}
