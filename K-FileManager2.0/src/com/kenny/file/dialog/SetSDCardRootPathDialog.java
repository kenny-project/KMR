package com.kenny.file.dialog;
import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.kenny.KFileManager.R;
import com.kenny.file.tools.SaveData;
import com.kenny.file.util.Const;
public class SetSDCardRootPathDialog
{
    public void ShowDialog(final Activity context,final List<File> Roots )
    {
    	String[] list=new String[Roots.size()];
    	for(int i=0;i<Roots.size();i++)
    	{
    		list[i]=Roots.get(i).getName();
    	}
        AlertDialog.Builder test = new AlertDialog.Builder(context);
        test.setTitle(context.getString(R.string.SetSDcardRootPathDialog_title));
        test.setSingleChoiceItems(list, 0, 
                new OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                    	File file=Roots.get(which);
                    	SaveData.Write(context, Const.strSDRootPath, file.getAbsolutePath());
                    	Const.setSDCard(file.getAbsolutePath());
                        dialog.cancel();
                    }
                });
        test.setNegativeButton(context.getString(R.string.cancel), null);
        test.create();
        test.show();
    }
}
