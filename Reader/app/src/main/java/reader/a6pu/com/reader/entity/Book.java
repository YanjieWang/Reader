package reader.a6pu.com.reader.entity;

/**
 * Created by 王燕杰 on 2016/5/29.
 */
public class Book {
    String author;
    String bookId;
    String name;
    String newChapter;
    String size;
    String typeId;
    String typeName;
    String updateTime;

    public Book() {
    }

    public Book(String author, String bookId, String name, String newChapter, String size, String typeId, String typeName, String updateTime) {
        this.author = author;
        this.bookId = bookId;
        this.name = name;
        this.newChapter = newChapter;
        this.size = size;
        this.typeId = typeId;
        this.typeName = typeName;
        this.updateTime = updateTime;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNewChapter() {
        return newChapter;
    }

    public void setNewChapter(String newChapter) {
        this.newChapter = newChapter;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Book{" +
                "author='" + author + '\'' +
                ", bookId='" + bookId + '\'' +
                ", name='" + name + '\'' +
                ", newChapter='" + newChapter + '\'' +
                ", size='" + size + '\'' +
                ", typeId='" + typeId + '\'' +
                ", typeName='" + typeName + '\'' +
                ", updateTime='" + updateTime + '\'' +
                '}';
    }
}
