package reader.a6pu.com.reader.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import reader.a6pu.com.reader.R;


/**
 * Created by 王燕杰 on 2016/5/26.
 */
public class Fragment_test extends android.support.v4.app.Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.left,container,false);
       return view;
    }

}
