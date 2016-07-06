package reader.a6pu.com.reader.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;



import java.util.ArrayList;

import reader.a6pu.com.reader.R;
import reader.a6pu.com.reader.fragment.Welcome_Fragment;
import reader.a6pu.com.reader.fragment.Welcome_Fragment_End;

/**
 * Created by 王燕杰 on 2016/5/26.
 *
 */
public class Welcome_Activity extends FragmentActivity {
    private ViewPager vp_welcome;
    private ArrayList<Fragment>welcome_fragment_list;
    private int [] imageResS={
            R.drawable.enter3,
            R.drawable.enter4,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        init();
    }


    //判断是否第一次启动
    private boolean isFirst() {
        SharedPreferences judgeFirst = getSharedPreferences("userdb", Context.MODE_PRIVATE);
        if(judgeFirst.getBoolean("isFirst",true)) {
            judgeFirst.edit().putBoolean("isFirst",false).commit();
            return true;
        }else{
            return false;
        }
    }

    //判断是否注册
    private boolean isAutoLogin() {
        SharedPreferences judgeFirst = getSharedPreferences("userdb", Context.MODE_PRIVATE);
        return judgeFirst.getBoolean("isAutoLogin",false);
    }

    //初始化
    private void init() {
        //        不是第一次并且已设置自动登录，跳过此界面
        boolean isFirst =isFirst();
        if(!isFirst&&isAutoLogin()){
            Intent intent =new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        vp_welcome = (ViewPager) findViewById(R.id.vp_welcome);
        welcome_fragment_list=new ArrayList<Fragment>();

        //初始化列表
        if(isFirst) {
            for (int i = 0; i < imageResS.length; i++) {
                Log.i("aaa", "" + i);
                Welcome_Fragment welcome_fragment = new Welcome_Fragment();
                Bundle bundle = new Bundle();
                bundle.putInt("imageRes", imageResS[i]);
                welcome_fragment.setArguments(bundle);
                welcome_fragment_list.add(welcome_fragment);
            }
        }
        Welcome_Fragment_End welcome_Fragment_End=new  Welcome_Fragment_End();



        welcome_Fragment_End.setOnItemStateChangeListener(new Welcome_Fragment_End.OnItemStateChangeListener() {
            @Override
            public void OnItemShow() {
                vp_welcome.requestDisallowInterceptTouchEvent(true);
            }
            @Override
            public void OnItemDismiss() {
                vp_welcome.requestDisallowInterceptTouchEvent(false);
            }
        });

        welcome_fragment_list.add(welcome_Fragment_End);
        vp_welcome.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return welcome_fragment_list.get(position);
            }

            @Override
            public int getCount() {
                return welcome_fragment_list.size();
            }
        });
    }
}