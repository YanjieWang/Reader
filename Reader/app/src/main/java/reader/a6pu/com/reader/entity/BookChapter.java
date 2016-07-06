package reader.a6pu.com.reader.entity;

/**
 * Created by 王燕杰 on 2016/5/30.
 */
public class BookChapter {
    String bookId=null;
    String chapterId=null;
    String chapterName=null;

    public BookChapter() {
    }

    public BookChapter(String bookId, String chapterId, String chapterName) {
        this.bookId = bookId;
        this.chapterId = chapterId;
        this.chapterName = chapterName;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    @Override
    public String toString() {
        return "BookChapter{" +
                "bookId='" + bookId + '\'' +
                ", chapterId='" + chapterId + '\'' +
                ", chapterName='" + chapterName + '\'' +
                '}';
    }
}
