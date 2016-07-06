package reader.a6pu.com.reader.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



import java.util.ArrayList;

import reader.a6pu.com.reader.R;
import reader.a6pu.com.reader.entity.Book;
import reader.a6pu.com.reader.entity.BookChapter;
import reader.a6pu.com.reader.entity.BookContent;
import reader.a6pu.com.reader.entity.BookType;
import reader.a6pu.com.reader.interface_calback.DataUpdateResuestListener;
import reader.a6pu.com.reader.interface_calback.DataUpdatedListener;

/**
 * Created by 王燕杰 on 2016/5/30.
 */
public class Search_Fragment extends Fragment implements DataUpdatedListener {
    private ArrayList<BookType> typeList;
    private ArrayList<Book> bookList;
    private ArrayList<BookChapter> chapterList;
    private BookContent content;
    private EditText et_keyword;
    private Button btn_search,btn_pre_page,btn_next_page;
    private ListView lv_type;
    private ListView lv_book;
    private ListView lv_chapter;
    private TextView tv_content,tv_book_date;
    private int total_book_page;
    private int current_book_page;
    private String keyword_now;
    private String typeid_now;
    private LayoutInflater layoutInflater;
    private DataUpdateResuestListener dataUpdateResuestListener;


    @Override
    public void onAttach(Activity activity) {
        dataUpdateResuestListener=(DataUpdateResuestListener)activity;
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.search,container,false);
        layoutInflater=inflater;
        findview(view);
        return view;
    }

    private void findview(View view) {
        et_keyword= (EditText) view.findViewById(R.id.et_keyword);
        btn_search= (Button) view.findViewById(R.id.btn_search);
        btn_pre_page= (Button) view.findViewById(R.id.btn_pre_page);
        btn_next_page= (Button) view.findViewById(R.id.btn_next_page);
        lv_type= (ListView) view.findViewById(R.id.lv_type);
        lv_book= (ListView) view.findViewById(R.id.lv_book);
        lv_chapter= (ListView) view.findViewById(R.id.lv_chapter);
        tv_content= (TextView) view.findViewById(R.id.tv_content);
        tv_book_date= (TextView) view.findViewById(R.id.tv_book_date);
        if(dataUpdateResuestListener!=null){
            typeList=dataUpdateResuestListener.onTypeListUpdateResuest(this,true);
        }
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str =et_keyword.getText().toString().trim();
                if(!"".equals(str)){
                    keyword_now=str;
                    typeid_now="";
                    bookList=dataUpdateResuestListener.onBookListUpdateResuest(Search_Fragment.this,str,"","",true);
                }
            }
        });
        btn_pre_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current_book_page>1){
                    bookList=dataUpdateResuestListener.onBookListUpdateResuest(Search_Fragment.this,keyword_now,typeid_now,(current_book_page-1)+"",true);
                }else{
                    Toast.makeText(getActivity(),"已到第一页了",Toast.LENGTH_SHORT).show();
                }

            }
        });
        btn_next_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(current_book_page<total_book_page){
                    bookList=dataUpdateResuestListener.onBookListUpdateResuest(Search_Fragment.this,keyword_now,typeid_now,(current_book_page+1)+"",true);
                }else{
                    Toast.makeText(getActivity(),"已是第一页了",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    @Override
    public void onTypeListUpdated(ArrayList<BookType> typeList) {
        Search_Fragment.this.typeList=typeList;
        lv_type.setAdapter(new MyTypeListAdapter());
        if(lv_type.getOnItemClickListener()!=null){
            return;
        }
        lv_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                keyword_now="";
                typeid_now=Search_Fragment.this.typeList.get(position).getTypeId();
                bookList=dataUpdateResuestListener.onBookListUpdateResuest(Search_Fragment.this,"",Search_Fragment.this.typeList.get(position).getTypeId(),"",true);
            }
        } );
    }

    @Override
    public void onBookListUpdated(int total_book_num,int total_book_page,int current_book_page,ArrayList<Book> bookList) {
        Search_Fragment.this.bookList=bookList;
        this.total_book_page=total_book_page;
        this.current_book_page=current_book_page;
        tv_book_date.setText("共"+total_book_num+"本，"+total_book_page+"页，当前在"+current_book_page+"页");
        lv_book.setAdapter(new MyBookListAdapter());
        lv_book.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chapterList=dataUpdateResuestListener.onChapterUpdateResuest(Search_Fragment.this,Search_Fragment.this.bookList.get(position).getBookId(),true);
            }
        } );
    }

    @Override
    public void onChapterUpdated(ArrayList<BookChapter> chapterList) {
        Search_Fragment.this.chapterList=chapterList;
        lv_chapter.setAdapter(new MyChapterListAdapter());
        lv_chapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                content=dataUpdateResuestListener.onContentUpdateResuest(Search_Fragment.this,Search_Fragment.this.chapterList.get(position).getBookId(),Search_Fragment.this.chapterList.get(position).getChapterId(),true);
            }
        } );
    }

    @Override
    public void onContentUpdated(BookContent content) {
        tv_content.setText(content.getContent());
    }
    class MyTypeListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return typeList.size();
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


            BookType type = typeList.get(position);
            if (convertView == null||convertView.getTag()==null ) {
                convertView = layoutInflater.inflate(R.layout.spiner_row, parent, false);
                TextView textView= (TextView) convertView.findViewById(R.id.textView);
                textView.setText(type.getTypeId()+":"+type.getTypeName() );
                convertView.setTag(new Holder(textView));
            } else {
                Holder holder = (Holder) convertView.getTag();
                holder.getTextView().setText(type.getTypeId()+":"+type.getTypeName() );
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








    class MyBookListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return bookList.size();
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


            Book book = bookList.get(position);
            if (convertView == null||convertView.getTag()==null ) {
                convertView = layoutInflater.inflate(R.layout.spiner_row, parent, false);
                TextView textView= (TextView) convertView.findViewById(R.id.textView);
                textView.setText(book.getName());
                convertView.setTag(new Holder(textView));
            } else {
                Holder holder = (Holder) convertView.getTag();
                holder.getTextView().setText(book.getName() );
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
                convertView = layoutInflater.inflate(R.layout.spiner_row, parent, false);
                TextView textView= (TextView) convertView.findViewById(R.id.textView);
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
    }

