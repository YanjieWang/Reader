package reader.a6pu.com.reader.interface_calback;



import java.util.ArrayList;

import reader.a6pu.com.reader.entity.Book;
import reader.a6pu.com.reader.entity.BookChapter;
import reader.a6pu.com.reader.entity.BookContent;
import reader.a6pu.com.reader.entity.BookType;
import reader.a6pu.com.reader.interface_calback.DataUpdatedListener;

public interface DataUpdateResuestListener {
    ArrayList<BookType> onTypeListUpdateResuest(DataUpdatedListener dataUpdatedListener, boolean withOutAppend);
    ArrayList<Book> onBookListUpdateResuest(DataUpdatedListener dataUpdatedListener, String keyword, String typeId, String page, boolean withOutAppend);
    ArrayList<BookChapter> onChapterUpdateResuest(DataUpdatedListener dataUpdatedListener, String bookId, boolean withOutAppend);
    BookContent onContentUpdateResuest(DataUpdatedListener dataUpdatedListener, String bookId, String chapterId, boolean withOutAppend);
}