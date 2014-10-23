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

import java.io.PrintWriter;
import java.io.StringWriter;

import android.content.Context;
import android.content.Intent;
import android.os.Process;

import com.umeng.analytics.MobclickAgent;

public class UncaughtExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
	private final Context myContext;

	public UncaughtExceptionHandler(Context context) {
		myContext = context;
	}

	public void uncaughtException(Thread thread, Throwable exception) {
		StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		System.err.println(stackTrace);
		MobclickAgent.reportError(myContext,stackTrace.toString()); //手动发送错误报告
		Intent intent = new Intent(myContext, BugReportActivity.class);
		intent.putExtra(BugReportActivity.STACKTRACE, stackTrace.toString());
		myContext.startActivity(intent);
		Process.killProcess(Process.myPid());
		System.exit(10);
	}
}
