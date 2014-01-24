package com.kenny.interfaces;

import com.kenny.net.RequestEntry;
/**
 * 网络返回结果接口
 * 
 * @author chenjiangang
 * */
public interface INetResult {
	
	public void netResult(RequestEntry entry, Object object);
	
}
