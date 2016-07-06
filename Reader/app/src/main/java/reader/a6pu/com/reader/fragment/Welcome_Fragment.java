package reader.a6pu.com.reader.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import reader.a6pu.com.reader.R;

/**
 * Created by 王燕杰 on 2016/5/26.
 */
public class Welcome_Fragment extends Fragment{
    private ImageView iv_welcome_page;
    private int imageRes;
    @Nullable
    @Override
 public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.welcome_page,container,false);
        iv_welcome_page= (ImageView) view.findViewById(R.id.iv_welcome_page);


        //首次进入获取用户设置值 ，恢复时恢复图片显示
        if(savedInstanceState!=null) {
            int temp=savedInstanceState.getInt("imageRes");
            //设置图片后恢复图片显示，未设置不用恢复
            if(temp!=0) {
                imageRes=temp;
            }
        }else{
            imageRes=getArguments().getInt("imageRes");
        }

        //判断是否设置有效图片，如已设置则显示，未设置不显示
            try {
                iv_welcome_page.setImageResource(imageRes);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getActivity(),"资源id无效或未设置",Toast.LENGTH_SHORT).show();
            }
        return view;
    }


//  保存状态
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("imageRes",imageRes);
    }
}
