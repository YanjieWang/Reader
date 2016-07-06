package reader.a6pu.com.reader.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import reader.a6pu.com.reader.R;
import reader.a6pu.com.reader.entity.Book;
import reader.a6pu.com.reader.entity.BookChapter;
import reader.a6pu.com.reader.entity.BookContent;
import reader.a6pu.com.reader.entity.BookType;
import reader.a6pu.com.reader.fragment.BookStore_Fragment;
import reader.a6pu.com.reader.fragment.Chapter_Fragment;
import reader.a6pu.com.reader.fragment.Home_Fragment;
import reader.a6pu.com.reader.fragment.Reader_Setting_Fragment;
import reader.a6pu.com.reader.fragment.Search_Book_Fragment;
import reader.a6pu.com.reader.fragment.Sort_Fragment;
import reader.a6pu.com.reader.fragment.Welcome_Fragment_End;
import reader.a6pu.com.reader.interface_calback.DataUpdateResuestListener;
import reader.a6pu.com.reader.interface_calback.DataUpdatedListener;
import reader.a6pu.com.reader.service.DateOperateService;

public class MainActivity extends FragmentActivity implements DataUpdateResuestListener,View.OnClickListener{
;
    private DrawerLayout draw_layout;
    private CheckBox cb_draw;
    private TextView tv_left_uname;
    private Button btn_reset,setting,left_btn_about,btn_search_main,btn_left_home,btn_left_sort,btn_left_book_store;
    private TabWidget tabs;
    private TabHost tabhost;
    private MyTabContentFactory myTabContentFactory;
    private ViewPager vp;
    private ArrayList<Fragment> viewList;
    private BookStore_Fragment bookStore_fragment;
    private Sort_Fragment sort_Fragment;
    private DateOperateService.DateOperateIBinder dateOperateIBinder;
    private Intent intent;
    private LinearLayout ll_draw_left;



    private ServiceConnection sc=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            dateOperateIBinder=(DateOperateService.DateOperateIBinder)service;
            init();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

     //   动态设置全屏代码
//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        getWindow().setAttributes(params);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        //   动态取消全屏代码
//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setAttributes(params);
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(Window.FEATURE_NO_TITLE);
        autoLogin();
        bindSer();

    }


    private void bindSer() {
        intent=new Intent(MainActivity.this, DateOperateService.class);
        startService(intent);
        bindService(intent,sc ,Context.BIND_AUTO_CREATE);
    }

    private void autoLogin() {
        SharedPreferences userdb = getSharedPreferences("userdb", Context.MODE_PRIVATE);
            String uName=userdb.getString("uName",null);
        tv_left_uname= (TextView) findViewById(R.id.tv_left_uname);
        if(uName!=null) {
            tv_left_uname.setText(uName);
        }else{
            tv_left_uname.setOnClickListener(this);
        }
        btn_left_home= (Button) findViewById(R.id.btn_left_home);
        btn_left_sort= (Button) findViewById(R.id.btn_left_sort);
        btn_search_main= (Button) findViewById(R.id.btn_search_main);
        btn_left_book_store= (Button) findViewById(R.id.btn_left_book_store);
        btn_left_home.setOnClickListener(this);
        btn_left_sort.setOnClickListener(this);
        btn_left_book_store.setOnClickListener(this);
        btn_search_main.setOnClickListener(this);

    }


    private void init() {
        //findView
        myTabContentFactory=new MyTabContentFactory();
        tabs= (TabWidget) findViewById(android.R.id.tabs);
        tabhost= (TabHost) findViewById(R.id.tabhost);
        vp= (ViewPager) findViewById(R.id.vp);
        tv_left_uname= (TextView) findViewById(R.id.tv_left_uname);
        draw_layout= (DrawerLayout) findViewById(R.id.draw_layout);
        cb_draw= (CheckBox) findViewById(R.id.cb_draw);
        btn_reset= (Button) findViewById(R.id.btn_reset);
        setting= (Button) findViewById(R.id.left_btn_setting);
        left_btn_about= (Button) findViewById(R.id.left_btn_about);
        ll_draw_left= (LinearLayout) findViewById(R.id.ll_draw_left);
        setting.setOnClickListener(this);
        left_btn_about.setOnClickListener(this);
        //初始化viewList
        viewList=new ArrayList<Fragment>();
        viewList.add(new Home_Fragment());
        sort_Fragment = new Sort_Fragment();
        viewList.add(sort_Fragment);
        bookStore_fragment = new BookStore_Fragment();
        viewList.add(bookStore_fragment);
        //屏蔽left下面的控件响应touch事件
        ll_draw_left.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        //viewPager设置adapter
        vp.setAdapter(new FragmentPagerAdapter( getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return viewList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return viewList.get(position);
            }
        });

        //viewPager设置监听实现tabhost标签与viewPager同步
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabhost.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


//初始化tabhost
        tabhost.setup();
        tabhost.addTab(tabhost.newTabSpec("首页").setIndicator("首页").setContent(myTabContentFactory));
        tabhost.addTab(tabhost.newTabSpec("分类").setIndicator("分类").setContent(myTabContentFactory));
        tabhost.addTab(tabhost.newTabSpec("书架").setIndicator("书架").setContent(myTabContentFactory));

//      设置tabwiget字体、宽度、高度
        for (int i =0; i < tabs.getChildCount(); i++) {
            tabs.getChildAt(i).getLayoutParams().height = 60;
//            tabs.getChildAt(i).getLayoutParams().width = 65;
            TextView tv = (TextView) tabs.getChildAt(i).findViewById(android.R.id.title);
            tv.setTextSize(15);
            tv.setTextColor(this.getResources().getColorStateList(android.R.color.white));
        }

        //      设置tabhost监听 实现tabhost标签与viewpager同步
        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabId){
                    case "首页":
                        vp.setCurrentItem(0,true);
                        break;
                    case "分类":
                        vp.setCurrentItem(1,true);
                        break;
                    case "书架":
                        vp.setCurrentItem(2,true);
                        break;
                }
                bookStore_fragment.setDelFlag(false);
            }
        });


        //重置登录状态监听
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences userdb = getSharedPreferences("userdb", Context.MODE_PRIVATE);
                userdb.edit().remove("uName").remove("uPsw").remove("isAutoLogin").remove("isFirst").commit();
            }
        });


        //cb_drawcheckBox监听实现侧滑栏手动拉出
        cb_draw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    draw_layout.openDrawer(Gravity.LEFT);
                }else{
                    draw_layout.closeDrawers();
                }

            }
        });

        //draw_layout监听实现cb_drawcheckBox状态同步
        draw_layout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                cb_draw.setChecked(true);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                cb_draw.setChecked(false);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        //判断是否是服务的intent启动了activity
        Intent intent=getIntent();
        if(intent.getStringExtra("bookId")!=null){
            String bookId=intent.getStringExtra("bookId");
            String name=intent.getStringExtra("name");
            String newChapter=intent.getStringExtra("newChapter");
            String updateTime=intent.getStringExtra("updateTime");
            String typeName=intent.getStringExtra("typeName");
            Chapter_Fragment chapter_Fragment=new Chapter_Fragment();
            chapter_Fragment.setBook(new Book(null,bookId,name,newChapter,null,null,typeName,updateTime));
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_continer_without_title, chapter_Fragment, "chapter_Fragment")
                    .addToBackStack("chapter_Fragment")
                    .commitAllowingStateLoss();
        }

    }





  //监听返回键
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(draw_layout.isDrawerOpen(Gravity.LEFT)){
                draw_layout.closeDrawers();
                return true;
            }

            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            //如果按下为返回键时，找到BookStore_Fragment，若处于删除状态，恢复为不可删除状态
            for (Fragment fr:fragments){
                if(fr instanceof BookStore_Fragment){
                    BookStore_Fragment tem=(BookStore_Fragment)fr;
                    if(tem.getDelFlag()){
                        tem.setDelFlag(false);
                        return true;
                    }
                }
            }
        }
        return super.onKeyUp(keyCode,event);
    }


    @Override
    protected void onDestroy() {
        stopService(intent);
        unbindService(sc);
        super.onDestroy();
    }

    @Override
    public ArrayList<BookType> onTypeListUpdateResuest(DataUpdatedListener dataUpdatedListener, boolean withOutAppend) {
        if(dateOperateIBinder!=null){

            return dateOperateIBinder.getTypeList(dataUpdatedListener,withOutAppend);
        }
        return null;
    }

    @Override
    public ArrayList<Book> onBookListUpdateResuest(DataUpdatedListener dataUpdatedListener, String keyword, String typeId, String page, boolean withOutAppend) {
        if(dateOperateIBinder!=null){

            return dateOperateIBinder.getBookList(dataUpdatedListener,  keyword,  typeId, page, withOutAppend);
        }
        return null;
    }

    @Override
    public ArrayList<BookChapter> onChapterUpdateResuest(DataUpdatedListener dataUpdatedListener, String bookId, boolean withOutAppend) {
        if(dateOperateIBinder!=null){

            return dateOperateIBinder.getChapterList(dataUpdatedListener,  bookId, withOutAppend);
        }
        return null;
    }

    @Override
    public BookContent onContentUpdateResuest(DataUpdatedListener dataUpdatedListener, String bookId, String chapterId, boolean withOutAppend) {
        if(dateOperateIBinder!=null){

            return dateOperateIBinder.getBookContent(dataUpdatedListener,  bookId,chapterId, withOutAppend);
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.left_btn_setting:
                //绑定设置按钮
                Reader_Setting_Fragment reader_Setting_Fragment=new Reader_Setting_Fragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_continer_without_title,reader_Setting_Fragment,"reader_Setting_Fragment")
                        .addToBackStack("reader_Setting_Fragment")
                        .commitAllowingStateLoss();
                if(draw_layout.isDrawerOpen(Gravity.LEFT)){
                    draw_layout.closeDrawers();
                }
                break;

            case R.id.left_btn_about:
                View view=getLayoutInflater().inflate(R.layout.about,null,true);
                new AlertDialog.Builder(MainActivity.this)
                        .setView(view)
                        .show();
                if(draw_layout.isDrawerOpen(Gravity.LEFT)){
                    draw_layout.closeDrawers();
                }
                break;
            case R.id.tv_left_uname:
                Welcome_Fragment_End welcome_Fragment_End=new Welcome_Fragment_End();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_continer_without_title,welcome_Fragment_End,"welcome_Fragment_End")
                        .addToBackStack("welcome_Fragment_End")
                        .commitAllowingStateLoss();
                if(draw_layout.isDrawerOpen(Gravity.LEFT)){
                    draw_layout.closeDrawers();
                }
                break;
            case R.id.btn_left_home:
                vp.setCurrentItem(0,true);
                if(draw_layout.isDrawerOpen(Gravity.LEFT)){
                    draw_layout.closeDrawers();
                }
                break;
            case R.id.btn_left_sort:
                vp.setCurrentItem(1,true);
                if(draw_layout.isDrawerOpen(Gravity.LEFT)){
                    draw_layout.closeDrawers();
                }
                break;
            case R.id.btn_left_book_store:
                vp.setCurrentItem(2,true);
                if(draw_layout.isDrawerOpen(Gravity.LEFT)){
                    draw_layout.closeDrawers();
                }
                break;
            case R.id.btn_search_main:
                Search_Book_Fragment search_Book_Fragment=new Search_Book_Fragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_continer_without_title,search_Book_Fragment,"search_Book_Fragment")
                        .addToBackStack("search_Book_Fragment")
                        .commitAllowingStateLoss();
                if(draw_layout.isDrawerOpen(Gravity.LEFT)){
                    draw_layout.closeDrawers();
                }
                break;
        }


    }


    //
    private class MyTabContentFactory implements TabHost.TabContentFactory {

        @Override
        public View createTabContent(String tag) {
            return new View(MainActivity.this);
        }
    }
}