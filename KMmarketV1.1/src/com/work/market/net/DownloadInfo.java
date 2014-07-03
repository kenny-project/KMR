package com.work.market.net;

public class DownloadInfo {

    private int startPos;// ��ʼ��
    private int endPos;// ������
    private int compeleteSize;// ��ɶ�
    private String url;// �����������ʶ
   
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
     * ������ؽ��Ȱٷֱ�
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