package reader.a6pu.com.reader.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


import reader.a6pu.com.reader.R;
import reader.a6pu.com.reader.utils.DbUtilsExtenel;


/**
 * Created by 王燕杰 on 2016/5/26.
 */
public class Login_Fragment extends Fragment implements View.OnClickListener{
    private Button btn_reg,btn_clear;
    private EditText et_uname,et_upsw1;
    private CheckBox cb_rember_psw;
    private int imageRes;


    private OnLoginListenerSucessListener onLoginListenerSucessListener;
    //回调接口，子fragment添加时让登录成功时调用此接口
    public interface OnLoginListenerSucessListener{
        void onLoginListenerSucess(String uName,String uPsw,boolean rember_psw);
    }
    public void setOnLoginListenerSucessListener(OnLoginListenerSucessListener onLoginListenerSucessListener){
        this.onLoginListenerSucessListener=onLoginListenerSucessListener;
    }

    @Nullable
    @Override
 public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.login,container,false);
        et_uname= (EditText) view.findViewById(R.id.et_uname);
        et_upsw1= (EditText) view.findViewById(R.id.et_upsw1);
        cb_rember_psw= (CheckBox) view.findViewById(R.id.cb_rember_psw);
        btn_reg= (Button) view.findViewById(R.id.btn_reg);
        btn_clear= (Button) view.findViewById(R.id.btn_clear);
        btn_reg.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        return view;
    }


//  保存状态
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("imageRes",imageRes);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_reg:
                reg();
                break;
            case R.id.btn_clear:
                et_uname.setText("");
                et_upsw1.setText("");
                break;

        }
    }

    private void reg() {
        String uname = et_uname.getText().toString().trim();
        String upsw1 = et_upsw1.getText().toString().trim();

        if (uname.equals("") || uname.contains("'")) {
            Toast.makeText(getActivity(), "未输入有效用户名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (uname.equals("") || upsw1.contains("'")) {
            Toast.makeText(getActivity(), "未输入有效密码", Toast.LENGTH_SHORT).show();
            return;
        }
        DbUtilsExtenel dbUtilsExtenel =new DbUtilsExtenel();
        //如果已注册，就登陆，并在SharedPreferences存储用户名，密码，注册状态，以便后续自动登录使用
        if (dbUtilsExtenel.QueryUser(uname, upsw1).getCount() > 0) {
            if(onLoginListenerSucessListener!=null){
                onLoginListenerSucessListener.onLoginListenerSucess(uname,upsw1,cb_rember_psw.isChecked());
            }
        } else {
            Toast.makeText(getActivity(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
        }
        dbUtilsExtenel.closeDataBase();
    }
}
