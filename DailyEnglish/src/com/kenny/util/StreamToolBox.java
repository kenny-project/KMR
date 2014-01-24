package com.kenny.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import android.content.Context;

public class StreamToolBox
{ 
    public static String loadStringFromFile(String filePathName) throws FileNotFoundException, IOException
    {
        return loadStringFromStream(new FileInputStream(filePathName));
    }
    
    public static String loadStringFromGZIP1Stream(InputStream in) throws IOException
    {
        return loadStringFromStream(new GZIPInputStream(in));
    }
    
    public static String loadStringFromStream(InputStream in) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(16384);
        copyStream(in, baos);
        baos.close();
        return baos.toString("UTF-8");
    }

    public static void saveGZipStreamToFile(InputStream in, String fileNamePath) throws IOException
    {
        saveStreamToFile(new GZIPInputStream(in), fileNamePath);
    }
    
    public static String extractGZipStreamToString(InputStream in) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copyStream(new GZIPInputStream(in), baos);
        return baos.toString("UTF-8");
    }
    
    public static void saveStringToFile(String str, String fileNamePath) throws IOException
    {
        saveStreamToFile(new ByteArrayInputStream(str.getBytes("UTF-8")), fileNamePath);
    }
    
    public static boolean saveStreamToFile(InputStream in, String fileNamePath) throws IOException
    {
    	boolean result = true;

		File f = null;
    	try{
    		f = new File(fileNamePath);
	        if (f.exists())
	        {
	            f.delete();
	        }
	        else
	        {
	            f.createNewFile();
	        }
	        
	        FileOutputStream fos = new FileOutputStream(f);
	        copyStream(in, fos);
	        fos.close();
        }catch(Exception e){
        	if (f != null && f.exists()){
        		f.delete();
        	}
        	e.printStackTrace();
        	result = false;
        }
    	return result;
    }

    public static void copyStream(InputStream in, OutputStream out) throws IOException
    {
        BufferedInputStream bin = new BufferedInputStream(in);
        BufferedOutputStream bout = new BufferedOutputStream(out);
        
        byte[] buffer = new byte[4096];

        while (true)
        {
            int doneLength = bin.read(buffer);
            if (doneLength == -1)
                break;
            bout.write(buffer, 0, doneLength);
        }
        bin.close();
        bout.flush();
        bout.close();
    }
    
    public static void dumpAssetToDataFile(Context ctx, String assetName, String storePath)
    {
        try
        {
            InputStream is = ctx.getAssets().open(assetName);
            StreamToolBox.saveStreamToFile(is, storePath + File.separator + assetName);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch(Exception e){
        	e.printStackTrace();
        }
    }
}
