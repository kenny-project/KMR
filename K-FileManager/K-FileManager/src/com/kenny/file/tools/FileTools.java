package com.kenny.file.tools;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;

public class FileTools
{
   public static byte[] read(Context context, String filename)
   {
       try
       {
           FileInputStream stream = context.openFileInput(filename);
           DataInputStream din = new DataInputStream(stream);
           byte[] data = new byte[stream.available()];
           din.readFully(data);
           din.close();
           stream.close();
           return data;
       }
       catch (Exception e)
       {
           // e.printStackTrace();
       }
       return null;
   }

   public static void write(Context context, String filename, byte[] data)
   {
       try
       {
           FileOutputStream stream = context.openFileOutput(filename,
                   Context.MODE_WORLD_WRITEABLE);// MODE_WORLD_WRITEABLE
           stream.write(data);
           stream.flush();
           stream.close();
       }
       catch (Exception e)
       {
           // e.printStackTrace();
       }
   }
   public static void writeString(Context context, String filename, String data)
   {
       try
       {
           ByteArrayOutputStream bou = new ByteArrayOutputStream();
           DataOutputStream dou = new DataOutputStream(bou);
           dou.writeUTF(data);
           byte[] da = bou.toByteArray();
           FileOutputStream stream = context.openFileOutput(filename,
                   Context.MODE_WORLD_WRITEABLE);// MODE_WORLD_WRITEABLE
           stream.write(da);
           stream.flush();
           stream.close();
           bou.close();
           dou.close();
       }
       catch (Exception e)
       {
           // e.printStackTrace();
       }
   }
   public static boolean delete(Context context, String filename)
   {
       boolean suc = false;
       try
       {
           context.deleteFile(filename);
           suc = true;
       }
       catch (Exception e)
       {
           suc = false;
           // e.printStackTrace();
       }
       return suc;
   }
  
}
