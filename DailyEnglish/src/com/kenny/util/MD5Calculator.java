package com.kenny.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Calculator
{
    private static MessageDigest _digest;
    
    public static String calculateMD5(String s)
    {
        try
        {
            return calculateMD5(s.getBytes("UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch(Exception e){
        	e.printStackTrace();
        }
        return "";
    }
    
    public synchronized static String calculateMD5(byte[] input)
    {
        try
        {
            _digest = MessageDigest.getInstance("MD5");
            _digest.reset();
            _digest.update(input);
            byte[] hash = _digest.digest();
            return StringToolBox.getHexString(hash);
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch(Exception e){
        	e.printStackTrace();
        }
        return "";
    }
}
