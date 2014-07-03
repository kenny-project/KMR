package com.work.market.server;

public interface PosetnetMonClick {
	public void PostContentType(String Type);
	public void Postfinish(Boolean alog);
	public void HandHttpStartnum(long anum);
	public void HandHttpEndnum(int anum,int count);
	public void HandHttpError();
}

