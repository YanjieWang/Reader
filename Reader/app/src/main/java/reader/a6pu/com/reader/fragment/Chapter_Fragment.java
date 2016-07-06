package reader.a6pu.com.reader.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reader.a6pu.com.reader.R;
import reader.a6pu.com.reader.entity.Book;
import reader.a6pu.com.reader.entity.BookChapter;
import reader.a6pu.com.reader.entity.BookContent;
import reader.a6pu.com.reader.entity.BookType;
import reader.a6pu.com.reader.entity.ReadingRecord;
import reader.a6pu.com.reader.interface_calback.DataUpdateResuestListener;
import reader.a6pu.com.reader.interface_calback.DataUpdatedListener;
import reader.a6pu.com.reader.utils.DbUtilsUserData;

public class Chapter_Fragment extends Fragment implements DataUpdatedListener,View.OnTouchListener{
        private ListView listView;
        private ArrayList<BookChapter> chapterList;
        private MyChapterListAdapter myChapterListAdapter;
        private LayoutInflater layoutInflater;
        private DataUpdateResuestListener dataUpdateResuestListener;
        private TextView tv_book_name,tv_book_type,tv_book_update_time,tv_book_latest_chapter;
        private Book book;


    @Override
    public void onAttach(Context context) {
        dataUpdateResuestListener= (DataUpdateResuestListener) getActivity();
        if(book!=null){
            dataUpdateResuestListener.onChapterUpdateResuest(this,book.getBookId(),true);
        }
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chapter, container, false);
        tv_book_name= (TextView) view.findViewById(R.id.tv_book_name);
        tv_book_type= (TextView) view.findViewById(R.id.tv_book_type);
        tv_book_update_time= (TextView) view.findViewById(R.id.tv_book_update_time);
        tv_book_latest_chapter= (TextView) view.findViewById(R.id.tv_book_latest_chapter);
        if(book!=null){
            tv_book_name.setText(book.getName());
            tv_book_type.setText(book.getTypeName());
            tv_book_update_time.setText(book.getUpdateTime());
            tv_book_latest_chapter.setText(book.getNewChapter());
        }
        layoutInflater=inflater;
        myChapterListAdapter=new MyChapterListAdapter();
        listView = (ListView) view.findViewById(R.id.lv_chapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Content_Reader_Fragment content_Reader_Fragment = new Content_Reader_Fragment();
                content_Reader_Fragment.setBookChapter(chapterList.get(position));
                content_Reader_Fragment.setAutoStartFlag(false);
                //全屏阅读
                WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
                params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                getActivity().getWindow().setAttributes(params);
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_continer_without_title, content_Reader_Fragment, "content_Reader_Fragment")
                        .addToBackStack("chapter_Fragment")
                        .commitAllowingStateLoss();
            }
        });
        view.setOnTouchListener(this);
        ReadingRecord readingRecordOfBook = new DbUtilsUserData().getReadingRecordOfBook(book.getBookId());
        if(readingRecordOfBook!=null){
            Content_Reader_Fragment content_Reader_Fragment = new Content_Reader_Fragment();
                content_Reader_Fragment.setBookChapter(new BookChapter(readingRecordOfBook.getBookId(),readingRecordOfBook.getChapterId(),null));
                content_Reader_Fragment.setAutoStartFlag(true);
                //全屏阅读
                WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
                params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                getActivity().getWindow().setAttributes(params);
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_continer_without_title, content_Reader_Fragment, "content_Reader_Fragment")
                        .addToBackStack("chapter_Fragment")
                        .commitAllowingStateLoss();
        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void setBook(Book book){
        this.book=book;
    }
    @Override
    public void onTypeListUpdated(ArrayList<BookType> typeList) {}

    @Override
    public void onBookListUpdated(int total_book_num, int total_book_page, int current_book_page, ArrayList<Book> bookList) {}

    @Override
    public void onChapterUpdated(ArrayList<BookChapter> chapterList) {
        this.chapterList=chapterList;
        if(listView!=null) {
            listView.setAdapter(myChapterListAdapter);
        }
    }

    @Override
    public void onContentUpdated(BookContent content) {}

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }


    class MyChapterListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return chapterList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            BookChapter chapter = chapterList.get(position);
            if (convertView == null||convertView.getTag()==null ) {
                convertView = layoutInflater.inflate(R.layout.chapter_row, parent, false);
                TextView textView= (TextView) convertView.findViewById(R.id.tv_chapter);
                textView.setText(chapter.getChapterName());
                convertView.setTag(new Holder(textView));
            } else {
                Holder holder = (Holder) convertView.getTag();
                holder.getTextView().setText(chapter.getChapterName() );
            }

            return convertView;
        }
        class Holder {
            private TextView textView;
            //
            public Holder(TextView textView) {
                super();
                this.textView = textView;
            }
            public TextView getTextView() {
                return textView;
            }
        }
    }

    @Override
    public void onDestroy() {
        listView=null;
        chapterList=null;
        myChapterListAdapter=null;
        layoutInflater=null;
        dataUpdateResuestListener=null;
        tv_book_name=null;
        tv_book_type=null;
        tv_book_update_time=null;
        tv_book_latest_chapter=null;
        super.onDestroy();
    }
}
