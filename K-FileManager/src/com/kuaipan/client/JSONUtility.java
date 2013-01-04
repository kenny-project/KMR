package com.kuaipan.client;

import java.util.Map;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONUtility
{
   private JSONUtility()
   {
   }
   
   @SuppressWarnings("unchecked")
   public static Map<String, Object> parse(String str)
   {
      if (str == null) return null;
      
      Map<String, Object> result = null;
      JSONParser parser = new JSONParser();
      try
      {
         result = (Map<String, Object>) parser.parse(str);
      }
      catch (ParseException e)
      {
         return null;
      }
      return result;
   }
   
   public static void main(String[] args)
   {
      System.out.println(parse("{\"name\": \"\\u65b0\\u5efa\\u6587\\u4ef6\"}"));
      System.out.println(parse("{'a': '23'}"));
   }
}
