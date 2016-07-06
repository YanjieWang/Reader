package reader.a6pu.com.reader.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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


public class Sort_Fragment extends Fragment implements RadioGroup.OnCheckedChangeListener ,DataUpdatedListener,AdapterView.OnItemLongClickListener,AdapterView.OnItemClickListener{
    private RadioGroup rg_sort;
    private ArrayList <RadioButton> buttonList;
    private ArrayList <BookType> typeList;
    private ArrayList <Book> bookList;
    private ListView lv_sort;
    private Animation rotateAnimationBotom;
    private LinearLayout ll_botom;
    private ImageView iv_botom;
    private String keyWord="";
    private Sort_Fragment.MyAdapter myAdapter;
    private int total_book_page,current_book_page;
    private RadioGroup.LayoutParams params;
    private DataUpdateResuestListener dataUpdateResuestListener;
    private LayoutInflater layoutInflater;
    private int[] res={R.drawable.sort_xuanhuanmofa,R.drawable.sort_wuxiaxiuzhen,R.drawable.sort_xiandaidushi,R.drawable.sort_yanqingxiaoshuo,
            R.drawable.sort_lishijunshi,R.drawable.sort_youxijingji,R.drawable.sort_kehuanlingyi,R.drawable.sort_danmeixiaoshuo,R.drawable.sort_qitaxiaoshuo};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layoutInflater=inflater;
        View view= inflater.inflate(R.layout.sort,container,false);
        rg_sort= (RadioGroup) view.findViewById(R.id.rg_sort);
        lv_sort= (ListView) view.findViewById(R.id.lv_sort);
        ll_botom= (LinearLayout) view.findViewById(R.id.ll_botom);
        iv_botom= (ImageView) view.findViewById(R.id.iv_botom);
        lv_sort.setOnItemClickListener(this);
        lv_sort.setOnItemLongClickListener(this);
        dataUpdateResuestListener= (DataUpdateResuestListener) getActivity();
        myAdapter=new MyAdapter();
        dataUpdateResuestListener.onTypeListUpdateResuest(this,true);
        //上拉加载
        lv_sort.setOnScrollListener(new AbsListView.OnScrollListener(){
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
                            dataUpdateResuestListener.onBookListUpdateResuest(Sort_Fragment.this, keyWord, "", (current_book_page + 1) + "", false);
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


        return view;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        dataUpdateResuestListener.onBookListUpdateResuest(this,"",typeList.get(checkedId).getTypeId(),"",true);
    }

    @Override
    public void onTypeListUpdated(ArrayList<BookType> typeList) {
        this.typeList=typeList;
        //获取屏幕像素密度
        final float scale = getActivity().getResources().getDisplayMetrics().density;
        int height = (int) (110 * scale + 0.5f); // 110dp
        int with = (int) (100 * scale + 0.5f); // 100dp
        int img = (int) (80 * scale + 0.5f); // 80dp
        params = new RadioGroup.LayoutParams(with, height);
        params.gravity= Gravity.CENTER;
        params.leftMargin=(int)(2 * scale + 0.5f);
        params.rightMargin=(int)(2 * scale + 0.5f);
        params.bottomMargin=(int)(2 * scale + 0.5f);
        params.topMargin=(int)(2 * scale + 0.5f);
        buttonList=new ArrayList<>();
        for(int i=0;i<typeList.size();i++){
            Drawable bg = getResources().getDrawable(R.drawable.sort_bg);
            RadioButton rb=new RadioButton(getActivity());
            Drawable top = getResources().getDrawable(res[i%res.length]);
            top.setBounds(0, 0, img,img);
            rb.setCompoundDrawables(null, top,null, null);
            rb.setButtonDrawable(null);
            rb.setBackground(bg);
            rb.setText(typeList.get(i).getTypeName());
            rb.setId(i);
            rb.setTextSize(6.5f* scale + 0.5f);
            rg_sort.addView(rb,params);
            buttonList.add(rb);
        }
        rg_sort.setOnCheckedChangeListener(this);
        rg_sort.check(0);
    }

    @Override
    public void onBookListUpdated(int total_book_num, int total_book_page, int current_book_page, ArrayList<Book> bookList) {
        Sort_Fragment.this.bookList=bookList;
        this.total_book_page=total_book_page;
        this.current_book_page=current_book_page;
        if(current_book_page==1){
            lv_sort.setAdapter(myAdapter);
        }else{
            myAdapter.notifyDataSetChanged();
        }
        onUpLoadCompleted();
    }

    @Override
    public void onChapterUpdated(ArrayList<BookChapter> chapterList) {

    }

    @Override
    public void onContentUpdated(BookContent content) {

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
            return position-1;
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