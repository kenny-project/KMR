
package com.work.market.server;

import com.work.Interface.INotifyDataSetChanged;
import com.work.market.bean.AppListBean;

/**
 * 显示内容更新处理接口
 * 
 * @author wmh
 * 
 */
public interface IMService
{
  /**
   * //获取当前访问页码
   * 
   * @return
   */
  public int getPagePos();

  /**
   * 切换到指定页
   * 
   * @param pos
   *          页数
   * @param index
   *          序号
   */
  public void SetPagePos(int pos, int index);

  public AppListBean getItem(int pos);// 获取Item内容
  
  public void setNotifItem(INotifyDataSetChanged mNotifItem);
  /**
   * 下一条内容
   * @return
   */
  public int NextItem();
  /**
   * 上一条内容
   * @return
   */
  public int PreItem();

  // public int NextPageData();
  // public int prevPageData();
}
