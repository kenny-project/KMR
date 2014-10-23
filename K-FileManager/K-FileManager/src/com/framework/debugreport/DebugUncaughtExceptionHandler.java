/*
 * Copyright (C) 2007-2011 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package com.framework.debugreport;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import android.content.Context;
import android.os.Process;

import com.kenny.file.util.Const;
/**
 * 
 * @author minghui.wang
 *
 */
public class DebugUncaughtExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
	private final Context myContext;

	public DebugUncaughtExceptionHandler(Context context) {
		myContext = context;
	}
	public static void LogWirte(String value)
	{
		try
		{
			FileWriter file=new FileWriter(Const.szAppTempPath+"error.txt");
			file.write("-----------------------------\r\n");
			file.write(value);
			file.flush();
			file.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void uncaughtException(Thread thread, Throwable exception) {
		StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		System.err.println(stackTrace);
		LogWirte(stackTrace.toString());
		Process.killProcess(Process.myPid());
		System.exit(10);
	}
}
