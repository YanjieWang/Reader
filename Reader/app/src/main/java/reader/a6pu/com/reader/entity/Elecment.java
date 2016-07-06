package reader.a6pu.com.reader.entity;


import reader.a6pu.com.reader.interface_calback.DataUpdatedListener;

/**用于存放运行参数
 * Created by 王燕杰 on 2016/6/1.
 */
public class Elecment {
    public String keyword;
    public String typeId;
    public String page;
    public String bookId;
    public String chapterId;
    public boolean withOutAppend;
    public DataUpdatedListener dataUpdatedListener;
    public int runMode;

    public Elecment(DataUpdatedListener dataUpdatedListener,String keyword, String typeId, String page, String bookId,String chapterId,boolean withOutAppend,int runMode) {
        this.keyword = keyword;
        this.dataUpdatedListener = dataUpdatedListener;
        this.chapterId = chapterId;
        this.bookId = bookId;
        this.page = page;
        this.typeId = typeId;
        this.withOutAppend = withOutAppend;
        this.runMode = runMode;
    }

    @Override
    public String toString() {
        return "Elecment{" +
                "keyword='" + keyword + '\'' +
                ", typeId='" + typeId + '\'' +
                ", page='" + page + '\'' +
                ", bookId='" + bookId + '\'' +
                ", chapterId='" + chapterId + '\'' +
                ", withOutAppend=" + withOutAppend +
                ", dataUpdatedListener=" + dataUpdatedListener +
                ", runMode=" + runMode +
                '}';
    }
}
