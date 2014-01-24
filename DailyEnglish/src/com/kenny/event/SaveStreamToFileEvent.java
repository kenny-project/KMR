package com.kenny.event;

import java.io.IOException;
import java.io.InputStream;

import com.kenny.activity.MainOld;
import com.kenny.struct.AbsEvent;
import com.kenny.util.NetCatch;

/**
 * 保存流到文件中
 * 
 * @author chenjiangang
 * 
 * */
public class SaveStreamToFileEvent extends AbsEvent{

	private InputStream stream;
	private String fileName;
	
	public SaveStreamToFileEvent(MainOld main, InputStream stream, String fileName) {
		super(main);
		this.fileName = fileName;
		this.stream = stream;
	}

	@Override
	public void ok() {
		// TODO Auto-generated method stub
		try {
			NetCatch.saveStreamToFile(stream, fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
