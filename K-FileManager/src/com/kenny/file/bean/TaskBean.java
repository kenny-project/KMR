package com.kenny.file.bean;

import android.graphics.drawable.Drawable;

public class TaskBean extends AppBean
{
   /*
    * 定义应用程序的简要信息部分
    */
   private Drawable icon; // 程序图标
   private String programName; // 程序名称
   private String processName;
   private String cpuMemString;
   private int procId;
   public int getProcId()
   {
      return procId;
   }

   public TaskBean(int procId,String sourceDir, String appName)
   {
      super(sourceDir,appName);
      icon = null;
      this.procId=procId;
      programName = "";
      processName = "";
      cpuMemString = "";
   }
   
   public Drawable getIcon()
   {
      return icon;
   }
   
   public void setIcon(Drawable icon)
   {
      this.icon = icon;
   }
   
   public String getProgramName()
   {
      return programName;
   }
   
   public void setProgramName(String programName)
   {
      this.programName = programName;
      setAppName(programName);
   }
   
   public String getProcessName()
   {
      return processName;
   }
   
   public void setProcessName(String processName)
   {
      this.processName = processName;
   }
   
   public String getCpuMemString()
   {
      return cpuMemString;
   }
   
   public void setCpuMemString(String cpuMemString)
   {
      this.cpuMemString = cpuMemString;
   }
   
}
