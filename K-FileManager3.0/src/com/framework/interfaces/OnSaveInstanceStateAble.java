package com.framework.interfaces;


import android.os.Bundle;

/**
 * 资源不足终止调用，用于保存activity信息
 * @author aimery
 * */
public interface OnSaveInstanceStateAble
{
  /**
   * 资源不足终止调用，用于保存activity信息
   * */
  public void  onSaveInstanceState(Bundle outState);
//  super.onSaveInstanceState(outState); 
//  int c= adapter.getCount();
//  Story[] s=new Story[c];
//  for(int i=0;i<c;i++)
//  {
//  	s[i]=adapter.getItem(i).story;
//  }
//  outState.putParcelableArray("com.story", s);
}
