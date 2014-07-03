package com.work.market.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.byfen.app.KApp;
import com.work.market.net.DictBean;
import com.work.market.net.Downloader;
import com.work.market.server.DownLoadService;

/**
 * AppList�б������
 * 
 * @author kenny
 * 
 */
public class AppListBean {

	private DictBean downloading = null;
	protected int id = 1;// id
	protected String title = "test";// ����
	protected String pn = "com.kenny.test";// ����
	protected String logo = "";// ͼ���ַ
	protected String size = "11K"; // �ļ���С
	protected float score = 4;// �������
	protected String dc = "0";// ���ش���
	protected String appurl = "";// ����ַ
	protected String vername = "";// �汾��
	protected int vercode = 0;// �汾����
	protected Bitmap image = null;
	protected int downprogress = 0;
//	protected int downing = 0;// 0---��״̬ 1----�ȴ����� 2-----������ 3---��ͣ��
	protected int Percentage = 0;
	protected String AppFileExt = "*";// �ļ�����·��
	protected String desc = "";// ����
	protected boolean bSpread=false;
	private boolean bExplainVisible=false;//��ʾ�۵�
	public int getPercentage(){
		return Percentage;
	}
	
	public boolean isbExplainVisible()
	{
		return bExplainVisible;
	}

	public void setbExplainVisible(boolean bExplainVisible)
	{
		this.bExplainVisible = bExplainVisible;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	public boolean iSpread()
	{
		return bSpread;
	}
	public void setSpread(boolean bSpread)
	{
		this.bSpread = bSpread;
	}
	// -1:δ֪���� 2:�Ѿ����� 1:�����ɹ� -2:�����ļ�������ʧ��
	public int Start(Context mContext) 
	{
		downloading=getDownloading() ;
		if(downloading==null)
		{
			downloading=new DictBean(mContext, getId(), getAppurl(),AppFileExt);
		}
		if(downloading.isdownloading())
		{
			//Toast.makeText(context, text, duration)
		}
//		P.v("getAppurl()"+getAppurl()+",AppFileExt="+AppFileExt);
		//downloading.setHandler(mHandler);
		downloading.setState(Downloader.WAIT);
		KApp app=((KApp)mContext.getApplicationContext());
		app.getDownLoadService().addDLEvent(this);
		return 1;
	}
	public void Delete()
	{
		
	}
	public DictBean getDictBean()
	{
		return downloading;	
	}

	public String getAppFileExt() {
		return AppFileExt;
	}

	public void setAppFileExt(String appFileExt) {
		AppFileExt = appFileExt;
	}

	public String getFileName()
	{
		if (downloading != null) 
		{
			return downloading.getFileName();
		}
		return null;
	}
	public DictBean getDownloading() 
	{
		if (downloading == null) 
		{
			downloading=DownLoadService.getDictBean(getId());
		}
		return downloading;
	}

	public void setDownloading(DictBean downloading) {
		this.downloading = downloading;
	}

	public void setPercentage(int downingsize) {
		this.Percentage = downingsize;
	}
	public Bitmap getLogo() {
		return image;
	}

	public void setLogo(Bitmap logo) {
		this.image =logo;
	}
	
	public void setLogo(Drawable logo) {
		this.image =((BitmapDrawable) logo).getBitmap();
	}

	public AppListBean() {

	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setId(String id) {
		this.id = Integer.valueOf(id);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPn() {
		return pn;
	}

	public void setPn(String pn) {
		this.pn = pn;
	}

	public String getLogourl() {
		return logo;
	}

	public void setLogourl(String logourl) {
		this.logo = logourl;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getVername() {
		return vername;
	}

	public void setVername(String vername) {
		this.vername = vername;
	}

	public int getVercode() {
		return vercode;
	}

	public void setVercode(String vercode) {
		try {
			this.vercode = Integer.valueOf(vercode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public float getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = Float.valueOf(score);
	}

	public String getAppurl() {
		return appurl;
	}

	public void setAppurl(String appurl) {
		this.appurl = appurl;
	}

	public int getDnum() {
		return downprogress;
	}

	public void setDnum(int dnum) {
		this.downprogress = dnum;
	}

	private String strDC = null;

	public String getDowntiems() {
//		if (strDC == null) {
//			if (dc > 10000) {
//				dc = dc / 10000;
//				strDC = dc + "��";
//			} else {
//				strDC = String.valueOf(dc);
//			}
//		}
		return dc;
	}

	public void setDowntiems(String dc) {
		this.dc = dc;
	}
}
