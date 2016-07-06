package reader.a6pu.com.reader.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;



import java.util.ArrayList;

import reader.a6pu.com.reader.R;
import reader.a6pu.com.reader.entity.Book;
import reader.a6pu.com.reader.entity.BookChapter;
import reader.a6pu.com.reader.entity.BookContent;
import reader.a6pu.com.reader.entity.BookType;
import reader.a6pu.com.reader.entity.ReadingRecord;
import reader.a6pu.com.reader.interface_calback.DataUpdateResuestListener;
import reader.a6pu.com.reader.interface_calback.DataUpdatedListener;
import reader.a6pu.com.reader.self_define_view.ReaderView;
import reader.a6pu.com.reader.utils.DbUtilsUserData;

public class Content_Reader_Fragment extends Fragment implements DataUpdatedListener,View.OnTouchListener ,ReaderView.OnChapterNeedChangListener{

    private ReaderView readerView;
    private BookChapter bookChapter;
    private DataUpdateResuestListener dataUpdateResuestListener;
    private ReadingRecord readingRecordOfBook;
    private boolean isAutoStart;
    private int[]bg_img={R.drawable.bg01,R.drawable.bg02,R.drawable.bg03,R.drawable.bg04};
    private int[] txt_color={0xffff0000, 0xff00ff00, 0xff0000ff, 0xff000000};
    private int[] txt_size={30, 50, 70, 90, 110};
    private ArrayList<BookChapter> chapterList;
    private int needMode=0;//1.请求上一章，2.请求下一章，0.无请求
    private int chapterIndex=-1;

    @Override
    public void onAttach(Context context) {
        dataUpdateResuestListener= (DataUpdateResuestListener) getActivity();
        if(isAutoStart){
            readingRecordOfBook=new DbUtilsUserData().getReadingRecordOfBook(bookChapter.getBookId());
            dataUpdateResuestListener.onContentUpdateResuest(this, readingRecordOfBook.getBookId(), readingRecordOfBook.getChapterId(), true);
        }else {
            if (bookChapter != null) {
                dataUpdateResuestListener.onContentUpdateResuest(this, bookChapter.getBookId(), bookChapter.getChapterId(), true);
            }
        }
        super.onAttach(context);
    }

    public void setAutoStartFlag(boolean autoStartFlag){
        this.isAutoStart=autoStartFlag;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_reader, container, false);
        view.setOnTouchListener(this);
        readerView= (ReaderView) view.findViewById(R.id.reader_view);
        SharedPreferences readerSeting = getActivity().getSharedPreferences("readerSeting", Context.MODE_PRIVATE);
        int rg_bg_value = readerSeting.getInt("readerseting_rg_bg", 2);
        int rg_txt_color_value = readerSeting.getInt("readerseting_rg_txt_color", 3);
        int rg_txt_size_value = readerSeting.getInt("readerseting_rg_txt_size", 2);
        readerView.setBackGround(bg_img[rg_bg_value]);
        readerView.setTxtColor(txt_color[rg_txt_color_value]);
        readerView.setTxtSize(txt_size[rg_txt_size_value]);

        readerView.setOnChapterNeedChangListener(this);
        return view;
    }


    @Override
    public void onDetach() {
        WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getActivity().getWindow().setAttributes(params);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        chapterIndex=-1;
        isAutoStart=false;
        chapterList=null;
        bookChapter=null;
        needMode=0;
        super.onDetach();
    }

    public void setBookChapter(BookChapter bookChapter){
        Log.i("aaa","setBookChapter"+bookChapter.toString());
        this.bookChapter=bookChapter;
    }
    @Override
    public void onTypeListUpdated(ArrayList<BookType> typeList) {}

    @Override
    public void onBookListUpdated(int total_book_num, int total_book_page, int current_book_page, ArrayList<Book> bookList) {}

    @Override
    public void onChapterUpdated(ArrayList<BookChapter> chapterList) {
        Log.i("aaa","Content_Reader_Fragment:onChapterUpdated():needMode=="+needMode);
        this.chapterList=chapterList;
        if(needMode==2){
            nextChapter();
        }
        if(needMode==1){
            PreChapter();
        }
    }

    @Override
    public void onContentUpdated(BookContent content) {
        Log.i("aaa","Content_Reader_Fragment:onContentUpdated");
        if(isAutoStart){
            readingRecordOfBook=new DbUtilsUserData().getReadingRecordOfBook(bookChapter.getBookId());
            long characterIndex = readingRecordOfBook.getCharacterIndex();
            readerView.setPageByCharacterIndex(characterIndex,content.getContent());
            isAutoStart=false;
        }
        readerView.setContent(content.getContent());
        if(needMode==2){
            Toast.makeText(getActivity(),bookChapter.getChapterName(),Toast.LENGTH_LONG).show();
            readerView.setCurrentPage(0);
            needMode=0;
        }

        if(needMode==1){
            Toast.makeText(getActivity(),bookChapter.getChapterName(),Toast.LENGTH_LONG).show();
            readerView.setCurrentPage(readerView.getTotalPage()-1);
            needMode=0;
        }

    }

    //存储用户读书进度
    @Override
    public void onPause() {
        new DbUtilsUserData().insertOrReplaceBookRecord(new ReadingRecord(bookChapter.getBookId(),bookChapter.getChapterId(),readerView.getCharacterIndexOfCurrentPage()));
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.i("aaa","Content_Reader_Fragment:nextChapter():onDestroy");
        readerView=null;
        bookChapter=null;
        dataUpdateResuestListener=null;
        readingRecordOfBook=null;
        bg_img=null;
        txt_color=null;
        txt_size=null;
        chapterList=null;








        super.onDestroy();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }


    //处理下一章节请求
    @Override
    public void onNeedNextChapter() {
        //还未响应完时阻止操作
        if(needMode!=0){
            return;
        }
        needMode=2;
        Log.i("aaa","Content_Reader_Fragment:onNeedNextChapter():chapterIndex:" +chapterIndex+"chapterList"+chapterList);
        if(chapterList==null){
            dataUpdateResuestListener.onChapterUpdateResuest(this,bookChapter.getBookId(),true);
            return;
        }
        nextChapter();


    }

    private void nextChapter() {
        Log.i("aaa","Content_Reader_Fragment:nextChapter():chapterIndex:" +chapterIndex+"chapterList.size"+chapterList.size());
        //chapterIndex==-1,说明该值未初始化
        if(chapterIndex==-1){
            for(int i=0;i<chapterList.size();i++){
                if(chapterList.get(i).getChapterId().equals(bookChapter.getChapterId())){
                    chapterIndex=i;
                }
            }
        }
        Log.i("aaa","Content_Reader_Fragment:nextChapter():chapterIndex:" +chapterIndex+"chapterList.size"+chapterList.size());
        //此时提醒用户已经是第一章了
        if(chapterIndex==chapterList.size()-1){
            Toast.makeText(getActivity(),"已经是最后一章了",Toast.LENGTH_LONG).show();
            needMode=0;
        }
        if(chapterIndex<chapterList.size()-1){
            chapterIndex++;
            bookChapter=chapterList.get(chapterIndex);
            Log.i("aaa","Content_Reader_Fragment:nextChapter():chapterIndex:" +chapterIndex+"chapterList.size"+chapterList.size());
            Log.i("aaa"," dataUpdateResuestListener.onContentUpdateResuest");
            dataUpdateResuestListener.onContentUpdateResuest(this, bookChapter.getBookId(), bookChapter.getChapterId(),true);
        }
    }

    //处理上一章节请求
    @Override
    public void onNeedPreChapter() {
        //还未响应完时阻止操作
        if(needMode!=0){
            return;
        }
        needMode=1;
        Log.i("aaa","Content_Reader_Fragment:onNeedNextChapter():chapterIndex:" +chapterIndex+"chapterList"+chapterList);
        if(chapterList==null){
            dataUpdateResuestListener.onChapterUpdateResuest(this,bookChapter.getBookId(),true);
            return;
        }
        PreChapter();


    }
    private void PreChapter() {
        //chapterIndex==-1,说明该值未初始化
        if(chapterIndex==-1){
            for(int i=0;i<chapterList.size();i++){
                if(chapterList.get(i).getChapterId().equals(bookChapter.getChapterId())){
                    chapterIndex=i;
                }
            }
        }
        //此时提醒用户已经是第一章了
        if(chapterIndex==0){
            Toast.makeText(getActivity(),"已经是第一章了",Toast.LENGTH_LONG).show();
            needMode=0;
        }
        if(chapterIndex>0){
            chapterIndex--;
            bookChapter=chapterList.get(chapterIndex);
            dataUpdateResuestListener.onContentUpdateResuest(this, bookChapter.getBookId(), bookChapter.getChapterId(),true);
        }
    }

}
