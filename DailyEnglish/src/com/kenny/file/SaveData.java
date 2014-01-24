package com.kenny.file;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SaveData
{
    /**
     * 读取配置文件中的某个字段
     * */
    public static boolean readPreferencesBoolean(Context context, String filename,boolean defaultValue)
    {
        try
        {
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    "KJokeConfig", 0);
            // 得到共享区"ITEM"的接口引用
            boolean show = sharedPreferences.getBoolean(filename,defaultValue);
            return show;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return defaultValue;
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
    public static void writePhonumList(Context context, String filename,
            Vector<String> phonumlist)
    {
        try
        {
            ByteArrayOutputStream bou = new ByteArrayOutputStream();
            DataOutputStream dou = new DataOutputStream(bou);
            dou.writeInt(phonumlist.size());
            for (int i = 0; i < phonumlist.size(); i++)
            {
                dou.writeUTF(phonumlist.elementAt(i));
            }
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
    public static void addPhoneNum(Context context, String filename,
            String phonenum)
    {
        try
        {
            Vector<String> vec = readPhoNumlistvec(context, filename);
            vec.add("" + phonenum);
            writePhonumList(context, filename, vec);
        }
        catch (Exception e)
        {
            // e.printStackTrace();
        }
    }
    public static Vector<String> readPhoNumlistvec(Context context,
            String filename)
    {
        Vector<String> vec = new Vector<String>();
        try
        {
            FileInputStream stream = context.openFileInput(filename);
            DataInputStream din = new DataInputStream(stream);
            int num = din.readInt();
            for (int i = 0; i < num; i++)
            {
                String s = "" + din.readUTF();
                vec.add(s);
            }
            stream.close();
            din.close();
        }
        catch (Exception e)
        {
            // e.printStackTrace();
        }
        return vec;
    }
    // 只用于电话号码
    public static String[] readPhoNumlist(Context context, String filename)
    {
        String[] phonum = null;
        try
        {
            FileInputStream stream = context.openFileInput(filename);
            DataInputStream din = new DataInputStream(stream);
            int num = din.readInt();
            phonum = new String[num];
            for (int i = 0; i < num; i++)
            {
                phonum[i] = new String();
                phonum[i] = din.readUTF();
            }
            stream.close();
            din.close();
        }
        catch (Exception e)
        {
            // e.printStackTrace();
            return null;
        }
        return phonum;
    }
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
    public static boolean delDatabase(Context context, String filename)
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
    
    /**
     * 用配置文件的模式存储
     * */
    public static void writePreferencesInt(Context context, String filename,
            int content)
    {
        try
        {
            Editor passfileEditor = context.getSharedPreferences("KJokeConfig", 0)
                    .edit();
            passfileEditor.putInt(filename, content);
            passfileEditor.commit();// 委托，存入数据
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 用配置文件的模式存储
     * */
    public static void writePreferencesBoolean(Context context, String filename,
            boolean content)
    {
        try
        {
            Editor passfileEditor = context.getSharedPreferences("KJokeConfig", 0)
                    .edit();
            passfileEditor.putBoolean(filename, content);
            passfileEditor.commit();// 委托，存入数据
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }    
    /**
     * 用配置文件的模式存储
     * */
    public static void writePreferencesString(Context context, String filename,
            String content)
    {
        try
        {
            Editor passfileEditor = context.getSharedPreferences("KJokeConfig", 0)
                    .edit();
            passfileEditor.putString(filename, content);
            passfileEditor.commit();// 委托，存入数据
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * 读取配置文件中的某个字段
     * */
    public static int readPreferencesInt(Context context, String filename)
    {
        try
        {
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    "KJokeConfig", 0);
            // 得到共享区"ITEM"的接口引用
            int show = sharedPreferences.getInt(filename, -1);
            return show;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return -1;
    }
    /**
     * 读取配置文件中的某个字段
     * */
    public static int readPreferencesInt(Context context, String filename,int defint)
    {
        try
        {
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    "KJokeConfig", 0);
            // 得到共享区"ITEM"的接口引用
            int show = sharedPreferences.getInt(filename,defint);
            return show;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return defint;
    }
    public static String readPreferencesString(Context context, String filename)
    {
        return readPreferencesString(context, filename,"");
    }
    /**
     * 读取配置文件中的某个字段
     * */
    public static String readPreferencesString(Context context, String filename,String def)
    {
        try
        {
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    "KJokeConfig", 0);
            // 得到共享区"ITEM"的接口引用
            String show = sharedPreferences.getString(filename,def);
            return show;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 读取配置文件中的某个字段
     * */
    public static boolean readPreferencesBoolean(Context context, String filename)
    {
        try
        {
            SharedPreferences sharedPreferences = context.getSharedPreferences(
                    "KJokeConfig", 0);
            // 得到共享区"ITEM"的接口引用
            boolean show = sharedPreferences.getBoolean(filename,false);
            return show;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
