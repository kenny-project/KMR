package com.work.market.net;

public class DownloadInfo {

    private int startPos;// 开始点
    private int endPos;// 结束点
    private int compeleteSize;// 完成度
    private String url;// 下载器网络标识
   
    public DownloadInfo(int startPos, int endPos,
                    int compeleteSize, String url) {
            super();
            this.startPos = startPos;
            this.endPos = endPos;
            this.compeleteSize = compeleteSize;
            this.url = url;
    }
    public int getStartPos() {
            return startPos;
    }
    public void setStartPos(int startPos) {
            this.startPos = startPos;
    }
    public int getEndPos() {
            return endPos;
    }
    public void setEndPos(int endPos) {
            this.endPos = endPos;
    }
    public int getCompeleteSize() {
            return compeleteSize;
    }
    public void setCompeleteSize(int compeleteSize) {
            this.compeleteSize = compeleteSize;
    }
    public void setCompeleteSize(Long compeleteSize) {
        this.compeleteSize =compeleteSize.intValue();
}
    /**
     * 获得下载进度百分比
     * @return
     */
    public int getCompeletePercentage()
    {
    	float temp=compeleteSize;
    	float tempEnd=endPos;
    	temp= temp/tempEnd*100;
    	return (int)temp;
    }
    public String getUrl() {
            return url;
    }
    public void setUrl(String url) {
            this.url = url;
    }
   
    @Override
         public String toString() {
             return "DownloadInfo ["
                     + " startPos=" + startPos + ", endPos=" + endPos
                     + ", compeleteSize=" + compeleteSize +"]";
 }


}