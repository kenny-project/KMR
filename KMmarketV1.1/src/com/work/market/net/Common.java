package com.work.market.net;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Environment;

import com.work.market.util.MD5Calculator;

public class Common {
	private static DecimalFormat myformat = new DecimalFormat("#####0.00");
	
	public static final String user_key = "mark_user_key";
	public static final String user_RunListData = "RunList_user_key";
	
	public static String    type_url ="";
	public static int       new_num = 0;
	
	//判断手机格式是否正确
		public static boolean isMobileNO(String mobiles) {
			Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
			Matcher m = p.matcher(mobiles);
			return m.matches();
			}
			
		//判断email格式是否正确
			public static boolean isEmail(String email) {
			String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
			Pattern p = Pattern.compile(str);
			Matcher m = p.matcher(email);


			return m.matches();
			}
			
			public boolean isNumeric(String str) 
			{ 
				Pattern pattern = Pattern.compile("[0-9]*"); 
				Matcher isNum = pattern.matcher(str); 
				return isNum.matches(); 
			} 
			
			//对数据进行MD5编码
			public static String getMd5Code(String aDta) {
				 
				return MD5Calculator.calculateMD5(aDta);
//		 		MessageDigest md5 = null; 
//		        try { 
//		            md5 = MessageDigest.getInstance("MD5"); 
//		        } catch (Exception e) { 
//		            e.printStackTrace(); 
//		            return ""; 
//		        } 
//		        char[] charArray = aDta.toCharArray(); 
//		        byte[] byteArray = new byte[charArray.length]; 
//		        for (int i = 0; i < charArray.length; i++) { 
//		            byteArray[i] = (byte) charArray[i]; 
//		        } 
//		        byte[] md5Bytes = md5.digest(byteArray); 
//		        StringBuffer hexValue = new StringBuffer(); 
//		        for (int i = 0; i < md5Bytes.length; i++) { 
//		            int val = ((int) md5Bytes[i]) & 0xff; 
//		            if (val < 16) { 
//		                hexValue.append("0"); 
//		            } 
//		            hexValue.append(Integer.toHexString(val)); 
//		        } 
//		        return hexValue.toString(); 
		 	}
			
/**
 * 检查sd卡
 * @return
 */
	public static boolean sdCardCheck() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}else {
			return false;
		} 
	}
	
	public static String getLength(String length) {
		String szLength = null;
			double totalsize = Double.parseDouble(length);
			if (totalsize > 1024) {
				totalsize = totalsize / 1024.0;
				if (totalsize > 1024) {
					totalsize = totalsize / 1024.0;
					szLength = myformat.format(totalsize) + "M";
				} else {
					szLength = myformat.format(totalsize) + "K";
				}
			} else {
				if (totalsize == 0) {
					return "0.00B";
				} else {
					szLength = myformat.format(totalsize) + "B";
				}
			}
	
		return szLength;
	}
	
	
	public static String getUTF8XMLString(String xml) {  
	    // A StringBuffer Object  
	    StringBuffer sb = new StringBuffer();  
	    sb.append(xml);  
	    String xmString = "";  
	    String xmlUTF8="";  
	    try {  
	    xmString = new String(sb.toString().getBytes("UTF-8"));  
	    xmlUTF8 = URLEncoder.encode(xmString, "UTF-8");  
	    System.out.println("utf-8 编码：" + xmlUTF8) ;  
	    } catch (UnsupportedEncodingException e) {  
	    // TODO Auto-generated catch block  
	    e.printStackTrace();  
	    }  
	    // return to String Formed  
	    return xmlUTF8;  
	    } 
	
	

}
