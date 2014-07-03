package com.work.Interface;

public interface INotifyDataSetChanged
{
    public void NotifyDataSetChanged(int cmd ,Object value);
    public void NotifyDataSetChanged(int cmd ,Object value,int arg1,int arg2);
}
