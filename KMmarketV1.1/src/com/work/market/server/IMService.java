
package com.work.market.server;

import com.work.Interface.INotifyDataSetChanged;
import com.work.market.bean.AppListBean;

/**
 * ��ʾ���ݸ��´���ӿ�
 * 
 * @author wmh
 * 
 */
public interface IMService
{
  /**
   * //��ȡ��ǰ����ҳ��
   * 
   * @return
   */
  public int getPagePos();

  /**
   * �л���ָ��ҳ
   * 
   * @param pos
   *          ҳ��
   * @param index
   *          ���
   */
  public void SetPagePos(int pos, int index);

  public AppListBean getItem(int pos);// ��ȡItem����
  
  public void setNotifItem(INotifyDataSetChanged mNotifItem);
  /**
   * ��һ������
   * @return
   */
  public int NextItem();
  /**
   * ��һ������
   * @return
   */
  public int PreItem();

  // public int NextPageData();
  // public int prevPageData();
}
