package com.kenny.file.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.kenny.KFileManager.R;
import com.kenny.file.tools.LinuxFileCommand;

public class AppConst
{
   private static LinuxFileCommand linux = null;
   
   public static LinuxFileCommand getLinuxCmd()
   {
      return linux;
   }
   
   public static void Init(Context ctx)
   {
      linux = new LinuxFileCommand(Runtime.getRuntime());
   }
   
}
