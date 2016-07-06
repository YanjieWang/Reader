package reader.a6pu.com.reader.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import reader.a6pu.com.reader.R;
import reader.a6pu.com.reader.activity.MainActivity;
import reader.a6pu.com.reader.fragment.Login_Fragment;
import reader.a6pu.com.reader.fragment.Reg_Fragment;

/**
 * Created by 王燕杰 on 2016/5/26.
 */
public class Welcome_Fragment_End extends Fragment implements View.OnClickListener{
    private Button btn_reg,btn_login,btn_later;
    private Reg_Fragment reg_fragment;
    private Login_Fragment login_fragment;
    private OnItemStateChangeListener onItemStateChangeListener;
    //回调接口，子fragment添加时让activity禁用viewpager滑动功能，删除时启用viewpager滑动功能。
    public interface OnItemStateChangeListener{
        void OnItemShow();
        void OnItemDismiss();
    }
    public void setOnItemStateChangeListener(OnItemStateChangeListener onItemStateChangeListener){
        this.onItemStateChangeListener=onItemStateChangeListener;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.welcome_end,container,false);
        btn_reg= (Button) view.findViewById(R.id.btn_reg);
        btn_login= (Button) view.findViewById(R.id.btn_login);
        btn_later= (Button) view.findViewById(R.id.btn_later);
        btn_reg.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_later.setOnClickListener(this);
        reg_fragment=new Reg_Fragment();
        login_fragment=new Login_Fragment();



        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(onItemStateChangeListener==null){
                    return false;
                }
               //因为每次onTouchDown 后viewpager会对DisallowInterceptTouchEvent重置，所以每次onTouchDown 需重新设置
                if (event.getAction() == MotionEvent.ACTION_DOWN&&(reg_fragment.isAdded()||login_fragment.isAdded())) {
                    onItemStateChangeListener.OnItemShow();
                    return true;
                }else if(event.getAction() == MotionEvent.ACTION_DOWN&&!reg_fragment.isAdded()) {
                    onItemStateChangeListener.OnItemDismiss();
                }
                //屏蔽touch事件向上传递
                return true;
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        //reg_fragment被添加后屏蔽Welcome_Fragment_End按键
        if(reg_fragment.isAdded()){
           return;
        }


        switch (v.getId()){
            case R.id.btn_reg:
                getActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.frameLayout_welcome,reg_fragment,"reg_fragment")
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
                if(onItemStateChangeListener!=null) {
                    onItemStateChangeListener.OnItemShow();
                }
                break;
            case R.id.btn_login:
                login_fragment.setOnLoginListenerSucessListener(new Login_Fragment.OnLoginListenerSucessListener() {
                    @Override
                    public void onLoginListenerSucess(String uName, String uPsw,boolean rember_psw) {
                        SharedPreferences userdb = getActivity().getSharedPreferences("userdb", Context.MODE_PRIVATE);
                        userdb.edit().putString("uName", uName).putBoolean("isAutoLogin",rember_psw).putString("uPsw", uPsw).commit();
                        //启动MainActivity
                        Intent intent =new Intent(getActivity(),MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                getActivity().getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .add(R.id.frameLayout_welcome,login_fragment,"reg_fragment")
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
                if(onItemStateChangeListener!=null) {
                    onItemStateChangeListener.OnItemShow();
                }
                break;
            case R.id.btn_later:
                SharedPreferences userdb = getActivity().getSharedPreferences("userdb", Context.MODE_PRIVATE);
                userdb.edit().putBoolean("isAutoLogin",false).remove("uName").remove("uPsw").commit();
                Intent intent=new Intent(getActivity(),MainActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
                break;
        }
    }
}
