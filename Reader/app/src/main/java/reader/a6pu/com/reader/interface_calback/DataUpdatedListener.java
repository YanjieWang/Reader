package reader.a6pu.com.reader.interface_calback;
import java.util.ArrayList;

import reader.a6pu.com.reader.entity.Book;
import reader.a6pu.com.reader.entity.BookChapter;
import reader.a6pu.com.reader.entity.BookContent;
import reader.a6pu.com.reader.entity.BookType;

public interface DataUpdatedListener{
    void onTypeListUpdated(ArrayList<BookType> typeList);
    void onBookListUpdated(int total_book_num,int total_book_page,int current_book_page,ArrayList<Book> bookList);
    void onChapterUpdated(ArrayList<BookChapter> chapterList);
    void onContentUpdated(BookContent content);
}