package reader.a6pu.com.reader.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import reader.a6pu.com.reader.R;

/**
 * Created by 王燕杰 on 2016/5/26.
 *
 */
public class Picture_Scroll_Fragment extends Fragment {
    private ViewPager vp_picture_scroll;
    private ArrayList<Fragment>picture_fragment_list;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private View view;
    private Timer timer;
    private BeforePictureChangeListener beforePictureChangeListener;
    private int [] imageResS={
            R.drawable.ad1,
            R.drawable.ad2,
            R.drawable.ad3,
            R.drawable.ad4,
    };

    //返回ture，移动图片，false不移动，此接口是为了解决fragment在listview可见区域中更新图片异常。
    public interface BeforePictureChangeListener{
        boolean ChangPicture();
    }
    public void setBeforePictureChangeListener(BeforePictureChangeListener beforePictureChangeListener){
        this.beforePictureChangeListener=beforePictureChangeListener;
    }


    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view =inflater.inflate(R.layout.picture_scroll,container,false);
            init(view);
        return view;
    }

    //初始化
    private void init(View view) {
        vp_picture_scroll = (ViewPager) view.findViewById(R.id.vp_picture_scroll);
        if(picture_fragment_list==null) {
            picture_fragment_list = new ArrayList<Fragment>();



            //初始化列表,复用welcome_Fragment
            for (int i = 0; i < imageResS.length; i++) {

                if(i==0){
                    Welcome_Fragment welcome_fragment1 = new Welcome_Fragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putInt("imageRes", imageResS[imageResS.length-1]);
                    welcome_fragment1.setArguments(bundle1);
                    picture_fragment_list.add(welcome_fragment1);
                }

                Welcome_Fragment welcome_fragment = new Welcome_Fragment();
                Bundle bundle = new Bundle();
                bundle.putInt("imageRes", imageResS[i]);
                welcome_fragment.setArguments(bundle);
                picture_fragment_list.add(welcome_fragment);


                if(i==(imageResS.length-1)){
                    Welcome_Fragment welcome_fragment1 = new Welcome_Fragment();
                    Bundle bundle1 = new Bundle();
                    bundle1.putInt("imageRes", imageResS[0]);
                    welcome_fragment1.setArguments(bundle1);
                    picture_fragment_list.add(welcome_fragment1);
                }
            }




            fragmentPagerAdapter=new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    return picture_fragment_list.get(position);
                }

                @Override
                public int getCount() {
                    return picture_fragment_list.size();
                }
            };
            vp_picture_scroll.setAdapter(fragmentPagerAdapter);
            vp_picture_scroll.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                    //实现无限循环滑动
                    if(state==0){
                        if(vp_picture_scroll.getCurrentItem()==0){
                            vp_picture_scroll.setCurrentItem(imageResS.length,false);
                        }
                        if(vp_picture_scroll.getCurrentItem()==imageResS.length+1){
                            vp_picture_scroll.setCurrentItem(1,false);
                        }
                    }
                }
            });


            //接受timer message更新vp_picture_scroll当前项
            if(handler==null) {
                handler = new Handler() {
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case 1:
                                if (beforePictureChangeListener == null||beforePictureChangeListener.ChangPicture()){
                                    vp_picture_scroll.setCurrentItem(vp_picture_scroll.getCurrentItem() + 1);
                                }
                                break;
                        }

                        //有此语句listview滑到底部时会崩
//                        super.handleMessage(msg);
                    }

                };
            }
            //定时发送消息
            if(timer==null) {
                timer = new Timer(true);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(1);
                    }
                }, 4000, 4000);
            }
        }
    }

    @Override
    public void onPause() {
        Log.i("aaa","Picture_Scroll_Fragment:onPause");
        if(timer!=null) {
            timer.cancel();
            timer = null;
        }
        if(handler!=null){
            handler=null;
        }
        super.onPause();
    }
}