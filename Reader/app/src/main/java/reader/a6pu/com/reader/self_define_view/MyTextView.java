package reader.a6pu.com.reader.self_define_view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by 王燕杰 on 2016/6/11.
 * 自定义textview实现在GridView实现跑马灯的效果，复写view里面的isFocused()方法，默认情况下是不会有效果的，
 * 而且gridview也不可点击
 */
public class MyTextView extends TextView
{

    public MyTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public MyTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean isFocused()
    {
        return true;
    }

}
