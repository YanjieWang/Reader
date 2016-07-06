package reader.a6pu.com.reader.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import reader.a6pu.com.reader.R;


/**
 * Created by 王燕杰 on 2016/6/10.
 */
public class Reader_Setting_Fragment extends Fragment {
    private RadioGroup readerseting_rg_bg;
    private RadioGroup readerseting_rg_txt_color;
    private RadioGroup readerseting_rg_txt_size;


    private int[] rbs_bg_img={
            R.id.readerseting_rb_bg01,
            R.id.readerseting_rb_bg02,
            R.id.readerseting_rb_bg03,
            R.id.readerseting_rb_bg04,
    };
    private int[] rbs_txt_color={
            R.id.readerseting_rb_txtcolor_red,
            R.id.readerseting_rb_txtcolor_green,
            R.id.readerseting_rb_txtcolor_blue,
            R.id.readerseting_rb_txtcolor_black,
    };
    private int[] rbs_txt_size={
            R.id.readerseting_rb_txtsize01,
            R.id.readerseting_rb_txtsize02,
            R.id.readerseting_rb_txtsize03,
            R.id.readerseting_rb_txtsize04,
            R.id.readerseting_rb_txtsize05,
    };
    private int[]bg_img={R.drawable.seting_bg01,R.drawable.seting_bg02,R.drawable.seting_bg03,R.drawable.seting_bg04};
    private int[] txt_color={0xffff0000, 0xff00ff00, 0xff0000ff, 0xff000000};
    private int[] txt_size={30, 50, 70, 90, 110};




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.reader_setting,container,false);
        SharedPreferences readerSeting = getActivity().getSharedPreferences("readerSeting", Context.MODE_PRIVATE);
        readerseting_rg_bg= (RadioGroup) view.findViewById(R.id.readerseting_rg_bg);
        readerseting_rg_txt_color= (RadioGroup) view.findViewById(R.id.readerseting_rg_txt_color);
        readerseting_rg_txt_size= (RadioGroup) view.findViewById(R.id.readerseting_rg_txt_size);
        Button readerseting_btn_save= (Button) view.findViewById(R.id.readerseting_btn_save);
        Button readerseting_btn_cancel= (Button) view.findViewById(R.id.readerseting_btn_cancel);
        readerseting_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        readerseting_btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editer = getActivity().getSharedPreferences("readerSeting", Context.MODE_PRIVATE).edit();
                for(int i=0;i<rbs_bg_img.length;i++){
                    if(readerseting_rg_bg.getCheckedRadioButtonId()==rbs_bg_img[i]){
                        editer.putInt("readerseting_rg_bg",i);
                    }
                }
                for(int i=0;i<rbs_txt_color.length;i++){
                    if(readerseting_rg_txt_color.getCheckedRadioButtonId()==rbs_txt_color[i]){
                        editer.putInt("readerseting_rg_txt_color",i);
                    }
                }
                for(int i=0;i<rbs_txt_size.length;i++){
                    if(readerseting_rg_txt_size.getCheckedRadioButtonId()==rbs_txt_size[i]){
                        editer.putInt("readerseting_rg_txt_size",i);
                    }
                }
                editer.commit();
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });



        int rg_bg_value = readerSeting.getInt("readerseting_rg_bg", 0);
        int rg_txt_color_value = readerSeting.getInt("readerseting_rg_txt_color", 3);
        int rg_txt_size_value = readerSeting.getInt("readerseting_rg_txt_size", 3);
        readerseting_rg_bg.check(rbs_bg_img[rg_bg_value]);
        readerseting_rg_txt_color.check(rbs_txt_color[rg_txt_color_value]);
        readerseting_rg_txt_size.check(rbs_txt_size[rg_txt_size_value]);
        return view;
    }

    @Override
    public void onDestroy() {
        readerseting_rg_bg=null;
        readerseting_rg_txt_color=null;
        readerseting_rg_txt_size=null;


        rbs_bg_img=null;
        rbs_txt_color=null;
        rbs_txt_size=null;
        bg_img=null;
        txt_color=null;
        txt_size=null;
        super.onDestroy();
    }
}
