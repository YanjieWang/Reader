package reader.a6pu.com.reader.entity;

/**
 * Created by 王燕杰 on 2016/5/30.
 */
public class BookContent {
    private String content;

    public BookContent() {
    }

    public BookContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "BookContent{" +
                "content='" + content + '\'' +
                '}';
    }
}
