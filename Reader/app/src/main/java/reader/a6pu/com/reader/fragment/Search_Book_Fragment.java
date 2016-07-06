package reader.a6pu.com.reader.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import reader.a6pu.com.reader.utils.DbUtilsUserData;

/**
 * Created by 王燕杰 on 2016/5/30.
 */
public class Search_Book_Fragment extends Fragment implements DataUpdatedListener,View.OnClickListener,AdapterView.OnItemLongClickListener,AdapterView.OnItemClickListener{
    private ArrayList<Book> bookList;
    private EditText et_keyword;
    private Button btn_search_searchbook,btn_refresh_searchbook;
    private ListView lv_book;
    private String keyWord;
    private LinearLayout ll_hint_searchbook,ll_botom;
    private ImageView iv_botom;
    TextView tv1_searchbook,tv2_searchbook,tv3_searchbook;
    private DataUpdateResuestListener dataUpdateResuestListener;
    private LayoutInflater layoutInflater;
    private Animation rotateAnimationBotom;
    private int total_book_page,current_book_page;
    private Search_Book_Fragment.MyAdapter myAdapter;
    private String[] hotWord={"三国","修仙传","修仙传","春秋","大秦","帝国","幻想"};
    private int index=0;



    @Override
    public void onAttach(Activity activity) {
       dataUpdateResuestListener=(DataUpdateResuestListener)activity;
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.search_book_name,container,false);
        layoutInflater=inflater;
        findview(view);
        bindListener(view);
        return view;
    }

    private void findview(View view) {
        et_keyword= (EditText) view.findViewById(R.id.et_keyword);
        btn_search_searchbook= (Button) view.findViewById(R.id.btn_search_searchbook);
        btn_refresh_searchbook= (Button) view.findViewById(R.id.btn_refresh_searchbook);
        ll_hint_searchbook= (LinearLayout) view.findViewById(R.id.ll_hint_searchbook);
        ll_botom= (LinearLayout) view.findViewById(R.id.ll_botom);
        iv_botom= (ImageView) view.findViewById(R.id.iv_botom);
        lv_book= (ListView) view.findViewById(R.id.lv_book);
        tv1_searchbook= (TextView) view.findViewById(R.id.tv1_searchbook);
        tv2_searchbook= (TextView) view.findViewById(R.id.tv2_searchbook);
        tv3_searchbook= (TextView) view.findViewById(R.id.tv3_searchbook);
        et_keyword.setOnClickListener(this);
        btn_refresh_searchbook.setOnClickListener(this);
        btn_search_searchbook.setOnClickListener(this);
        myAdapter=new MyAdapter();


    }

    private void bindListener(View view) {
        //屏蔽下层响应touch
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        lv_book.setOnItemLongClickListener(this);
        lv_book.setOnItemClickListener(this);
        tv1_searchbook.setOnClickListener(this);
        tv2_searchbook.setOnClickListener(this);
        tv3_searchbook.setOnClickListener(this);
        //上拉加载
        lv_book.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState){
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 判断是否滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        //初始化动画
                        if(rotateAnimationBotom==null) {
                            rotateAnimationBotom = new RotateAnimation(0, 3600, iv_botom.getPivotX(), iv_botom.getPivotY());
                            rotateAnimationBotom.setInterpolator(new Interpolator() {
                                @Override
                                public float getInterpolation(float input) {
                                    return input;
                                }
                            });
                            rotateAnimationBotom.setDuration(15000);
                            rotateAnimationBotom.setRepeatCount(Animation.INFINITE);
                        }

                        if(current_book_page<total_book_page) {
                            ll_botom.setVisibility(View.VISIBLE);
                            iv_botom.startAnimation(rotateAnimationBotom);
                            dataUpdateResuestListener.onBookListUpdateResuest(Search_Book_Fragment.this, keyWord, "", (current_book_page + 1) + "", false);
                        }else{
                            Toast.makeText(getActivity(),"没有更多内容了",Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }


    @Override
    public void onTypeListUpdated(ArrayList<BookType> typeList) {
    }

    @Override
    public void onBookListUpdated(int total_book_num,int total_book_page,int current_book_page,ArrayList<Book> bookList) {

        Search_Book_Fragment.this.bookList=bookList;
        this.total_book_page=total_book_page;
        this.current_book_page=current_book_page;
        if(current_book_page==1){
            lv_book.setAdapter(myAdapter);
        }else{
            myAdapter.notifyDataSetChanged();
        }
        onUpLoadCompleted();
    }

    @Override
    public void onChapterUpdated(ArrayList<BookChapter> chapterList) {
    }

    @Override
    public void onContentUpdated(BookContent content) {}

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_search_searchbook:
                String str =et_keyword.getText().toString().trim();
                if(!"".equals(str)){
                    keyWord=str;
                    bookList=dataUpdateResuestListener.onBookListUpdateResuest(Search_Book_Fragment.this,str,"","",true);
                    ll_hint_searchbook.setVisibility(View.GONE);
                }
                break;
            case R.id.et_keyword:
                ll_hint_searchbook.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_refresh_searchbook:
                if(index>=hotWord.length){
                    index=0;
                }
                tv1_searchbook.setText(hotWord[index]);
                index++;
                if(index>=hotWord.length){
                    index=0;
                }
                tv2_searchbook.setText(hotWord[index]);
                index++;
                if(index>=hotWord.length){
                    index=0;
                }
                tv3_searchbook.setText(hotWord[index]);
                index++;
                break;
            case R.id.tv1_searchbook:
                et_keyword.setText(tv1_searchbook.getText()+"");
                break;
            case R.id.tv2_searchbook:
                et_keyword.setText(tv2_searchbook.getText()+"");
                break;
            case R.id.tv3_searchbook:
                et_keyword.setText(tv3_searchbook.getText()+"");
                break;

        }
    }


    //上拉加载完成调用此方法恢复状态
    public void onUpLoadCompleted(){
        ll_botom.setVisibility(View.INVISIBLE);
        if(rotateAnimationBotom!=null&&rotateAnimationBotom.hasStarted()){
            rotateAnimationBotom.cancel();
        }
    }

    //listview长按监听
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        final int pos=position;
        new AlertDialog.Builder(getActivity())
                .setTitle("添加至书架")
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==-2){
                            DbUtilsUserData dbUtilsUserData=new DbUtilsUserData();
                            long l = dbUtilsUserData.insertBookIntoBookStoreIfNotInserted(bookList.get(pos));
                            if(l==-2){
                                Toast.makeText(getActivity(),"已经添加过了，不需要再添加了",Toast.LENGTH_LONG).show();
                            }
                            if(l==-1){
                                Toast.makeText(getActivity(),"添加失败",Toast.LENGTH_SHORT).show();
                            }
                            if(l>=0){
                                Toast.makeText(getActivity(),"添加成功",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setPositiveButton("取消",null)
                .show();
        return true;
    }


    //listview 点击监听
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Chapter_Fragment chapter_Fragment=new Chapter_Fragment();
        chapter_Fragment.setBook(bookList.get(position));
        getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_continer_without_title, chapter_Fragment, "chapter_Fragment")
                    .addToBackStack("chapter_Fragment")
                    .commitAllowingStateLoss();
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if(bookList==null){
                return 0;
            }else {
                return bookList.size();
            }
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
                convertView = layoutInflater.inflate(R.layout.home_row, parent, false);
                TextView tv_book_name = (TextView) convertView.findViewById(R.id.tv_book_name);
                TextView tv_book_latest_chapter = (TextView) convertView.findViewById(R.id.tv_book_latest_chapter);
                TextView tv_book_update_time = (TextView) convertView.findViewById(R.id.tv_book_update_time);
                TextView tv_book_type = (TextView) convertView.findViewById(R.id.tv_book_type);
                tv_book_name.setText(book.getName());
                tv_book_latest_chapter.setText(book.getNewChapter());
                tv_book_update_time.setText(book.getUpdateTime());
                tv_book_type.setText(book.getTypeName());
                convertView.setTag(new Holder(tv_book_name,tv_book_latest_chapter,tv_book_update_time,tv_book_type));
            } else {
                Holder holder = (Holder) convertView.getTag();
                holder.tv_book_name.setText(book.getName());
                holder.tv_book_latest_chapter.setText(book.getNewChapter());
                holder.tv_book_update_time.setText(book.getUpdateTime());
                holder.tv_book_type.setText(book.getTypeName());
            }

            return convertView;
        }

    }
    class Holder {
        TextView tv_book_name;
        TextView tv_book_latest_chapter;
        TextView tv_book_update_time;
        TextView tv_book_type;

        public Holder(TextView tv_book_name, TextView tv_book_latest_chapter, TextView tv_book_update_time, TextView tv_book_type) {
            this.tv_book_name = tv_book_name;
            this.tv_book_latest_chapter = tv_book_latest_chapter;
            this.tv_book_update_time = tv_book_update_time;
            this.tv_book_type = tv_book_type;
        }
    }

}

