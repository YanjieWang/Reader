package reader.a6pu.com.reader.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import reader.a6pu.com.reader.activity.MainActivity;
import reader.a6pu.com.reader.utils.DbUtilsExtenel;


/**
 * Created by 王燕杰 on 2016/5/26.
 */
public class Reg_Fragment extends Fragment implements View.OnClickListener{
    private Button btn_reg,btn_clear;
    private EditText et_uname,et_upsw1,et_upsw2;
    private CheckBox cb_rember_psw;
    private int imageRes;
    DbUtilsExtenel dbUtilsExtenel;
    @Nullable
    @Override
 public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.reg,container,false);
        et_uname= (EditText) view.findViewById(R.id.et_uname);
        et_upsw1= (EditText) view.findViewById(R.id.et_upsw1);
        et_upsw2= (EditText) view.findViewById(R.id.et_upsw2);
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
                et_upsw2.setText("");
                break;

        }
    }

    private void reg() {
        String uname = et_uname.getText().toString().trim();
        String upsw1 = et_upsw1.getText().toString().trim();
        String upsw2 = et_upsw2.getText().toString().trim();

        dbUtilsExtenel =new DbUtilsExtenel();


        if (uname.equals("")  || uname.contains("'")) {
            Toast.makeText(getActivity(), "未输入有效用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        //判断用户是否被注册

        if (dbUtilsExtenel.isReg(uname)) {
            Toast.makeText(getActivity(), "用户名已被注册", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!upsw1.equals(upsw2)) {
            Toast.makeText(getActivity(), "两次输入密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        if(upsw1.length()<6){
            Toast.makeText(getActivity(), "密码长度太短，请输入不少于6位的密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (uname.equals("")||upsw1.contains("'")) {
            Toast.makeText(getActivity(), "未输入有效密码", Toast.LENGTH_SHORT).show();
            return;
        }

        //数据库存储用户名，密码，
        //

        //判断用户是否被注册
        dbUtilsExtenel.insertUser(uname,upsw1);


        //SharedPreferences 储用户名，密码，注册状态，以便后续自动登录使用
        SharedPreferences userdb = getActivity().getSharedPreferences("userdb", Context.MODE_PRIVATE);
        userdb.edit().putString("uName", uname).putBoolean("isAutoLogin",cb_rember_psw.isChecked()).putString("uPsw", upsw1).putBoolean("isReg", true).commit();
        //启动MainActivity
        Intent intent =new Intent(getActivity(),MainActivity.class);
        startActivity(intent);
        getActivity().finish();

    }


}
