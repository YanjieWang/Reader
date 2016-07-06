package reader.a6pu.com.reader.entity;

/**
 * Created by 王燕杰 on 2016/6/5.
 */
public class ReadingRecord {
    private  String bookId;
    private  String chapterId;
    private  long characterIndex;

    public ReadingRecord(String bookId, String chapterId, long characterIndex) {
        this.bookId = bookId;
        this.chapterId = chapterId;
        this.characterIndex = characterIndex;
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

    public long getCharacterIndex() {
        return characterIndex;
    }

    public void setCharacterIndex(long characterIndex) {
        this.characterIndex = characterIndex;
    }

    @Override
    public String toString() {
        return "ReadingRecord{" +
                "bookId='" + bookId + '\'' +
                ", chapterId='" + chapterId + '\'' +
                ", characterIndex=" + characterIndex +
                '}';
    }
}
