package com.work.market.bean;

import java.io.InputStream;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.alibaba.fastjson.annotation.JSONField;
import com.byfen.app.KApp;
import com.work.market.net.Common;
import com.work.market.net.DictBean;
import com.work.market.net.Downloader;
import com.work.market.server.DownLoadService;

/**
 * AppList列表的属性
 * 
 * @author kenny
 * 
 */
public class AppListBean {

	
	protected int id = 1;// id
	protected String title = "";// 名称
	private String lang;
	private int type;
	private String en_name;// 英文名称
	protected String pn = "com.kenny.test";// 包名
	protected String logo = "";// 图标地址
	protected String size = "11K"; // 文件大小
	protected float score = 4;// 软件评分
	protected String dc = "0";// 下载次数
	protected String appurl = "";// 包地址
	protected String vername = "";// 版本号
	protected int vercode = 0;// 版本名称
	
	protected int downprogress = 0;
	// protected int downing = 0;// 0---无状态 1----等待下载 2-----下载中 3---暂停中
	protected int Percentage = 0;
	protected String AppFileExt = "*";// 文件保存路径

	protected boolean bSpread = false;


	protected String Desc = "";// 简介
	private String EditorComments;// 编辑点评
	private String UpdateDesc;// 更新说明
	private String TagDesc;// 更新说明

	private String update_time;
	private String dev_name;
	private String[]imgs;
	private String []other_download;
	//不需要实例化代码
	private Bitmap imgLogo = null;
	private DictBean downloading = null;
	private boolean bExplainVisible = false;// 显示折叠
	
	public String[] getImgs() {
		return imgs;
	}

	public String[] getOther_download() {
		return other_download;
	}

	public void setOther_download(String[] other_download) {
		this.other_download = other_download;
	}

	public void setImgs(String[] imgs) {
		this.imgs = imgs;
	}

	public String getDev_name() {
		return dev_name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setDev_name(String dev_name) {
		this.dev_name = dev_name;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String times) {
		if (times.indexOf(" ") > 0) {
			int x = times.indexOf(" ");
			this.update_time = times.substring(0, x);
		} else {
			this.update_time = times;
		}
	}

	public String getEn_name() {
		return en_name;
	}

	public void setEn_name(String en_name) {
		this.en_name = en_name;
	}

	public int getPercentage() {
		return Percentage;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		if (lang.equals("cn")) {
			this.lang = "中文";
		} else {
			this.lang = "英文";
		}
	}

	public boolean isbExplainVisible() {
		return bExplainVisible;
	}

	public void setbExplainVisible(boolean bExplainVisible) {
		this.bExplainVisible = bExplainVisible;
	}

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String desc) {
		this.Desc = desc;
	}

	public boolean iSpread() {
		return bSpread;
	}

	public void setSpread(boolean bSpread) {
		this.bSpread = bSpread;
	}

	// -1:未知错误 2:已经启动 1:启动成功 -2:创建文件或网络失败
	public int Start(Context mContext) {
		downloading = getDownloading();
		if (downloading == null) {
			downloading = new DictBean(mContext, getId(), getAppurl(),
					AppFileExt);
		}
		if (downloading.isdownloading()) {
			// Toast.makeText(context, text, duration)
		}
		// P.v("getAppurl()"+getAppurl()+",AppFileExt="+AppFileExt);
		// downloading.setHandler(mHandler);
		downloading.setState(Downloader.WAIT);
		KApp app = ((KApp) mContext.getApplicationContext());
		app.getDownLoadService().addDLEvent(this);
		return 1;
	}

	public void Delete() {

	}

	public DictBean getDictBean() {
		return downloading;
	}

	public String getAppFileExt() {
		return AppFileExt;
	}

	public void setAppFileExt(String appFileExt) {
		AppFileExt = appFileExt;
	}

	public String getFileName() {
		if (downloading != null) {
			return downloading.getFileName();
		}
		return null;
	}

	public DictBean getDownloading() {
		if (downloading == null) {
			downloading = DownLoadService.getDictBean(getId());
		}
		return downloading;
	}

	public void setDownloading(DictBean downloading) {
		this.downloading = downloading;
	}

	public void setPercentage(int downingsize) {
		this.Percentage = downingsize;
	}
	public Bitmap getImgLogo() {
		return imgLogo;
	}


	public void setImgLogo(Bitmap logo) {
		this.imgLogo = logo;
	}

	public void setImgLogo(Drawable logo) {
		this.imgLogo = ((BitmapDrawable) logo).getBitmap();
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

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logourl) {
		this.logo = logourl;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = Common.getLength(size);
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
		// if (strDC == null) {
		// if (dc > 10000) {
		// dc = dc / 10000;
		// strDC = dc + "万";
		// } else {
		// strDC = String.valueOf(dc);
		// }
		// }
		return dc;
	}

	public void setDowntiems(String dc) {
		this.dc = dc;
	}

	public String getEditorComments() {
		return EditorComments;
	}

	public void setEditorComments(String editorComments) {
		EditorComments = editorComments;
	}

	public String getUpdateDesc() {
		return UpdateDesc;
	}

	public void setUpdateDesc(String updateDesc) {
		UpdateDesc = updateDesc;
	}

	public String getTagDesc() {
		return TagDesc;
	}

	public void setTagDesc(String tagDesc) {
		TagDesc = tagDesc;
	}
}
