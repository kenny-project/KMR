package com.kenny.interfaces;


import android.os.Bundle;

/**
 * 资源不足终止调用，用于保存activity信息
 * @author chenjiangang
 * */
public interface IOnSaveInstanceState
{
  /**
   * 资源不足终止调用，用于保存activity信息
   * */
  public void  onSaveInstanceState(Bundle outState);
}
