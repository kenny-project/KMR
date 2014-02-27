package com.kenny.file.dialog;


import com.kenny.KFileManager.t.R;
import com.kenny.file.interfaces.KActivityStatus;
import com.kenny.file.tools.SaveData;
import com.kenny.file.util.Theme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
public class ThemeDialog
{
    private Activity m_context;
    private int SelectItemPos = -1;
    private int ThemeModeDef=0;//默认的主题模式
    public void ShowDialog(final Activity context, final KActivityStatus result)
    {
        m_context = context;
        AlertDialog.Builder test = new AlertDialog.Builder(context);
        test.setTitle(context.getString(R.string.msg_themedialog));
        int ThemeMode=SaveData.Read(context, "ThemeMode",ThemeModeDef);
        test.setSingleChoiceItems(R.array.select_dialog_ThemeMode, ThemeMode, 
                new OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        SelectItemPos = which;
                        SaveData.Write(m_context, "ThemeMode",SelectItemPos);
                        Theme.setThemeMode(SelectItemPos);
                        if (result != null)
                        {
                            result.KActivityResult(1001,1, 0, "");
                        }
                        dialog.cancel();
                    }
                });
        test.setNegativeButton(m_context.getString(R.string.cancel), null);
        test.create();
        test.show();
    }
}
