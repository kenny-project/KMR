package com.kenny.file.commui;

public class KString implements CharSequence
{
   private int key;
   private String value;
   
   public KString(int key, String value)
   {
      this.key = key;
      this.value = value;
   }
   
   public int getKey()
   {
      return key;
   }
   
   
   public int length()
   {
      // TODO Auto-generated method stub
      return value.length();
   }
   
   
   public char charAt(int index)
   {
      // TODO Auto-generated method stub
      return value.charAt(index);
   }
   
   
   public CharSequence subSequence(int start, int end)
   {
      // TODO Auto-generated method stub
      return value.subSequence(start, end);
   }

   
   public String toString()
   {
      // TODO Auto-generated method stub
      return value;
   }
}
