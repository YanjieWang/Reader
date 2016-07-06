package reader.a6pu.com.reader.fragment;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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


public class Home_Fragment extends Fragment implements DataUpdatedListener,AdapterView.OnItemLongClickListener{
    private float startY=-1;
    private AbsoluteLayout al;
    private TextView tv_top,tv_botom;
    private LinearLayout ll_top,ll_botom;
    private float topHight;
    private float delatY;
    private int speed=4;
    private int speed_first=3;
    private int speed_temp=-1;
    private ValueAnimator valueAnimator;
    private ListView lv_content;
    private MyOnTouchListener myOnTouchListener;
    private ImageView iv_top,iv_botom;
    private RotateAnimation rotateAnimation,rotateAnimationBotom;
    private Picture_Scroll_Fragment picture_scroll_fragment;
    private DataUpdateResuestListener dataUpdateResuestListener;
    private View view;
    private ArrayList <Book> bookList;
    private LayoutInflater layoutInflater;
    private View firstRow;
    private MyAdapter myAdapter;
    private int total_book_page;
    private int current_book_page=0;
    private String[] refresh={"三国","大秦","青春","风云","计"};
    private int refreshIndex=0;
    private boolean hasMove;//标志位用于判断是否向下移动，进而控制长按事件是否需要响应
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layoutInflater=inflater;
        if(view!=null) {
            return view;
        }
        view = inflater.inflate(R.layout.home, container, false);
        al= (AbsoluteLayout) view.findViewById(R.id.home);
        tv_top= (TextView) view.findViewById(R.id.tv_top);
        tv_botom= (TextView) view.findViewById(R.id.tv_botom);
        iv_top= (ImageView) view.findViewById(R.id.iv_top);
        iv_botom= (ImageView) view.findViewById(R.id.iv_botom);
        ll_top= (LinearLayout) view.findViewById(R.id.ll_top);
        ll_botom= (LinearLayout) view.findViewById(R.id.ll_botom);
        lv_content= (ListView) view.findViewById(R.id.lv_content);


        dataUpdateResuestListener=(DataUpdateResuestListener)getActivity();
        this.bookList=dataUpdateResuestListener.onBookListUpdateResuest(this,"三国","","",true);
        myOnTouchListener=new MyOnTouchListener();
        myAdapter=new MyAdapter();
        lv_content.setAdapter(myAdapter);
        //下拉刷新
        lv_content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(lv_content.getFirstVisiblePosition()==0&&lv_content.getChildAt(0).getY()==0){
                    return myOnTouchListener.onTouch(v,event);
                }
                //恢复listview按监听
                hasMove=false;
                return false;
            }
        });
        lv_content.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Chapter_Fragment chapter_Fragment=new Chapter_Fragment();
                chapter_Fragment.setBook(bookList.get(position-1));
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_continer_without_title, chapter_Fragment, "chapter_Fragment")
                        .addToBackStack("chapter_Fragment")
                        .commitAllowingStateLoss();
            }
        });
        lv_content.setOnItemLongClickListener(this);
        //上拉加载
        lv_content.setOnScrollListener(new AbsListView.OnScrollListener(){
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
                            dataUpdateResuestListener.onBookListUpdateResuest(Home_Fragment.this, refresh[refreshIndex], "", (current_book_page + 1) + "", false);
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


        valueAnimator=new ValueAnimator();

     return view;
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
                            long l = dbUtilsUserData.insertBookIntoBookStoreIfNotInserted(bookList.get(pos - 1));
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


    class MyOnTouchListener implements View.OnTouchListener {


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //初始化动画
            if(rotateAnimation==null) {
                rotateAnimation = new RotateAnimation(0, 3600, iv_top.getPivotX(), iv_top.getPivotY());
                rotateAnimation.setInterpolator(new Interpolator() {
                    @Override
                    public float getInterpolation(float input) {
                        return input;
                    }
                });
                rotateAnimation.setDuration(15000);
                rotateAnimation.setRepeatCount(Animation.INFINITE);
            }





            topHight=ll_top.getMeasuredHeight();
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if(delatY==speed_first*topHight){
                        //当处于刷新状态再次点击
                        startY=event.getRawY()-speed_first*topHight;
                    }else {
                        startY = event.getRawY();
                    }
                    return false;
                case MotionEvent.ACTION_MOVE:

                    if(startY==-1){
                        if(delatY==speed_first*topHight){
                            //当处于刷新状态再次点击
                            startY=event.getRawY()-speed_first*topHight;
                        }else {
                            startY = event.getRawY();
                        }
                    }
                    delatY=event.getRawY()-startY;
                    if(delatY>=0) {
                        if (delatY> speed_first*topHight) {
                            tv_top.setText("释放立即刷新");
                            if(rotateAnimation.hasStarted()){
                                iv_top.clearAnimation();
                            }
                            iv_top.setImageResource(R.drawable.arrow_up_32x32);
                            al.scrollTo(0, -(int) ((delatY-speed_first*topHight)/speed+topHight));
                        }else{
                            tv_top.setText("下拉刷新");
                            if(rotateAnimation.hasStarted()){
                                iv_top.clearAnimation();
                            }
                            iv_top.setImageResource(R.drawable.arrow_down_32x32);
                            al.scrollTo(0, -(int) (delatY/speed_first));
                        }
                        if(hasMove==false&&delatY>10){
                            event.setLocation(event.getX(),event.getY()+45);
                            hasMove=true;
                            return false;
                        }


                        return true;
                    }else{
                        al.scrollTo(0, 0);
                        if(al.getY()==0){
                            if(speed_temp!=-1) {
                                speed_first = speed_temp;
                                speed_temp = -1;
                            }
                            startY=-1;
                            return false;
                        }
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    return touchUp();
            }
            return false;
        }

        private boolean touchUp() {
            //恢复listview按监听
            hasMove=false;
            startY=-1;

            //delatY<0处理
            if(delatY<0){
                if((al.getY())!=0) {
                    al.scrollTo(0, 0);
                    if(speed_temp!=-1) {
                        speed_first = speed_temp;
                        speed_temp = -1;
                    }
                    return true;
                }
                if(speed_temp!=-1) {
                    speed_first = speed_temp;
                    speed_temp = -1;
                }
                startY=-1;
                return false;
            }

            //speed*topHight>delatY>=0处理
            if(delatY<=speed_first*topHight) {
                valueAnimator.setObjectValues(delatY/speed_first,0);
                valueAnimator.setDuration((long)(delatY/speed_first*3));
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float tempY= (float) animation.getAnimatedValue();
                        al.scrollTo(0, -(int) tempY);
                    }
                });
                valueAnimator.start();
                if(delatY==0){
                    //用户未移动，响应listview点击事件
                    if(speed_temp!=-1) {
                        speed_first = speed_temp;
                        speed_temp = -1;
                    }
                    return false;
                }else {
                    if(speed_temp!=-1) {
                        speed_first = speed_temp;
                        speed_temp = -1;
                    }
                    delatY = 0;
                    //用户移动，屏蔽listview点击事件
                    return true;
                }
            }

            //delatY>speed*topHight处理
            valueAnimator.setObjectValues(((delatY -speed_first*topHight)/speed+topHight),topHight);
            valueAnimator.setDuration((long)(delatY -speed_first*topHight)/speed*2);
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    valueAnimator.removeAllListeners();
                    //下拉刷新所执行的语句放这里
                    refreshIndex++;
                    if (refreshIndex >= refresh.length) {
                        refreshIndex = 0;
                    }
                    dataUpdateResuestListener.onBookListUpdateResuest(Home_Fragment.this, refresh[refreshIndex], "", "", true);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float tempY= (float) animation.getAnimatedValue();
                    al.scrollTo(0, -(int) tempY);


                }
            });
            valueAnimator.start();

            tv_top.setText("正在刷新");
            iv_top.setImageResource(R.drawable.refresh_32x32);
            //如果没有播放就开始播放
            if(!rotateAnimation.hasStarted()||rotateAnimation.hasEnded()){
                iv_top.startAnimation(rotateAnimation);
            }

            if(delatY==speed_first*topHight){
                //用户未移动，响应listview点击事件
                if(speed_temp==-1) {
                    speed_temp= speed_first;
                    speed_first = 1;
                }
                return false;
            }else {
                if(speed_temp==-1) {
                    speed_temp= speed_first;
                    speed_first = 1;
                }
                delatY=speed_first*topHight;
                //用户移动，屏蔽listview点击事件
                return true;
            }
        }
    }
    //下拉刷新完成调用此方法恢复状态
    public void onDownSlideRefreshCompleted(){
        if( "正在刷新".equals(tv_top.getText().toString())) {
            tv_top.setText("刷新完成");
            if(rotateAnimation.hasStarted()){
                iv_top.clearAnimation();
            }
            valueAnimator.setObjectValues(topHight,0);
            valueAnimator.setDuration(300);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float tempY = (float) animation.getAnimatedValue();
                    al.scrollTo(0, -(int) tempY);
                }
            });
            speed_first=3;
            speed_temp=-1;
            startY=-1;
            delatY=0;
            valueAnimator.start();
        }
    }
    //上拉加载完成调用此方法恢复状态
    public void onUpLoadCompleted(){
        ll_botom.setVisibility(View.INVISIBLE);
        if(rotateAnimationBotom!=null&&rotateAnimationBotom.hasStarted()){
            rotateAnimationBotom.cancel();
        }
    }

    @Override
    public void onTypeListUpdated(ArrayList<BookType> typeList) {}
    @Override
    public void onBookListUpdated(int total_book_num,int total_book_page,int current_book_page,ArrayList<Book> bookList) {
        this.bookList=bookList;
        this.current_book_page=current_book_page;
        this.total_book_page=total_book_page;

        myAdapter.notifyDataSetChanged();
        //
        onUpLoadCompleted();
        onDownSlideRefreshCompleted();


        //下拉刷新更新状态



    }


    @Override
    public void onChapterUpdated(ArrayList<BookChapter> chapterList) {}

    @Override
    public void onContentUpdated(BookContent content) { }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return bookList.size()+1;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position-1;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position-1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(position==0){
                if(firstRow==null) {
                    firstRow=layoutInflater.inflate(R.layout.row_picture_scroll_continer, parent, false);
                    picture_scroll_fragment = new Picture_Scroll_Fragment();
                    picture_scroll_fragment.setBeforePictureChangeListener(new Picture_Scroll_Fragment.BeforePictureChangeListener() {
                        @Override
                        public boolean ChangPicture() {
                            return lv_content.getFirstVisiblePosition()==0;
                        }
                    });
                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fl_picture_scroll,picture_scroll_fragment).commitAllowingStateLoss();
                }
                return firstRow;
            }

            Book book = bookList.get(position-1);
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