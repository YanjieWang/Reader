package reader.a6pu.com.reader.fragment;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reader.a6pu.com.reader.R;
import reader.a6pu.com.reader.entity.Book;
import reader.a6pu.com.reader.utils.DbUtilsUserData;

public class BookStore_Fragment extends Fragment {
        private GridView gview;
        private ArrayList<Book> bookList;
        private MyAdapter myAdapter;
        private Chapter_Fragment chapter_Fragment;
        private boolean  delFlag;
        private boolean  lastDelCompleted=true;
        private LayoutInflater  layoutInflater;
        private LinearLayout ll_ringt_leter_index;
        private TextView tv_center_index_leter;
    private HanyuPinyinOutputFormat hanyuPinyinOutputFormat;

        // 图片封装为一个数组
        private int[] icon = {R.drawable.book_face1, R.drawable.book_face2,R.drawable.book_face3,R.drawable.book_face4};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layoutInflater=inflater;
        View view =inflater.inflate(R.layout.bookstore_graidview,container,false);
        //初始化数据源
        initData();
        //初始化gridview并绑定监听
        initGridView(view);
        //初始化侧边字母导航栏
        init_touch_slider(view);

        //设置返回键监听，若按返回键时
//        view.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if(keyCode==KeyEvent.KEYCODE_BACK){
//                    delFlag=false;
//                    myAdapter.notifyDataSetChanged();
//                }
//                Toast.makeText(getActivity(),keyCode+"",Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });
        return view;
    }

    private void initData() {
        DbUtilsUserData dbUtilsUserData=new DbUtilsUserData();
        //获取数据
        bookList = dbUtilsUserData.getBooksInBookStore();

        //初始化汉字转拼音Format
        hanyuPinyinOutputFormat=new HanyuPinyinOutputFormat();
        hanyuPinyinOutputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        hanyuPinyinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        //按拼音首字母排序
        Collections.sort(bookList, new Comparator<Book>() {
            @Override
            public int compare(Book lhs, Book rhs) {
                char c1=lhs.getName().charAt(0);
                char c2=rhs.getName().charAt(0);

                try {
                    String []str1= PinyinHelper.toHanyuPinyinStringArray(c1,hanyuPinyinOutputFormat);
                    String []str2=PinyinHelper.toHanyuPinyinStringArray(c2,hanyuPinyinOutputFormat);
                    if(str1!=null){
                        c1=str1[0].charAt(0);
                    }
                    if(str2!=null){
                        c2=str2[0].charAt(0);
                    }

                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                }

                return c1-c2;
            }
        });
    }

    private void initGridView(View view) {
        gview = (GridView) view.findViewById(R.id.gview);
        //配置适配器
        myAdapter=new MyAdapter();
        gview.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        gview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                if(delFlag&&lastDelCompleted){
                    lastDelCompleted=false;
                    //设置删粗动画
                    AlphaAnimation alphaAnimation=new AlphaAnimation(1,0);
                    RotateAnimation rotateAnimation=new RotateAnimation(0,360,view.getPivotX(),view.getPivotY());
                    ScaleAnimation scaleAnimation=new ScaleAnimation(1,0,1,0,view.getPivotX(),view.getPivotY());
                    scaleAnimation.setAnimationListener(new Animation.AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            //动画完成后删除对象
                            int i = new DbUtilsUserData().deleteBookFromBookStore(bookList.get(position));
                            if(i==0){
                                Toast.makeText(getActivity(),"删除失败，数据库异常",Toast.LENGTH_SHORT).show();
                            }else{
                                bookList.remove(position);
                                myAdapter.notifyDataSetChanged();
                                Toast.makeText(getActivity(),"删除成功",Toast.LENGTH_SHORT).show();
                            }
                            lastDelCompleted=true;


                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    AnimationSet animationSet=new AnimationSet(false);
                    animationSet.setDuration(700);
                    animationSet.addAnimation(alphaAnimation);
                    animationSet.addAnimation(rotateAnimation);
                    animationSet.addAnimation(scaleAnimation);
                    view.startAnimation(animationSet);
                }
                //不在删除状态时执行打开书籍
                if(!delFlag){
                    chapter_Fragment= new Chapter_Fragment();
                        chapter_Fragment.setBook(bookList.get(position));
                        getActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.fragment_continer_without_title, chapter_Fragment, "chapter_Fragment")
                                .addToBackStack("chapter_Fragment")
                                .commitAllowingStateLoss();
                }
            }
        });

        gview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(delFlag){
                    delFlag=false;
                }else{
                    delFlag=true;
                }
                myAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void init_touch_slider(View view) {
        ll_ringt_leter_index= (LinearLayout) view.findViewById(R.id.ll_ringt_leter_index);

        //b绑定textview用于屏幕中间显示选中字母
        tv_center_index_leter= (TextView) view.findViewById(R.id.tv_center_index_leter);


        //初始化右侧字符列表
        for(int i='A';i<='Z';i++){
            TextView tv=new TextView(getActivity());
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,0,1);
            tv.setText((char)i+"");
            ll_ringt_leter_index.addView(tv,i-'A',lp);
        }

        //设置监听
        ll_ringt_leter_index.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float height=v.getHeight();
                float perHeight=height/26;
                float currentY=event.getY();

                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    tv_center_index_leter.setVisibility(View.VISIBLE);
                    ll_ringt_leter_index.setAlpha(1);
                }
                for(int i='A';i<='Z';i++){
                    if((i-'A')*perHeight<currentY&&currentY<perHeight*(i-'A'+1)){
                        if(!tv_center_index_leter.getText().toString().equals(""+(char) i)) {

                            //遍历list，设置选中第一个显示在listview中
                            for(int j=0;j<bookList.size();j++){
                                char c1=bookList.get(j).getName().charAt(0);
                                try {
                                    String [] str=PinyinHelper.toHanyuPinyinStringArray(c1,hanyuPinyinOutputFormat);
                                    if(str!=null){
                                        c1=str[0].charAt(0);
                                    }
                                } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                                    badHanyuPinyinOutputFormatCombination.printStackTrace();
                                }
                                if(i<=c1){
                                    gview.setSelection(j);
                                    break;
                                }
                            }
                            tv_center_index_leter.setText((char) i + "");
                        }
                    }
                }
                if (event.getAction()==MotionEvent.ACTION_UP){
                    tv_center_index_leter.setVisibility(View.GONE);
                    ll_ringt_leter_index.setAlpha(0.2f);
                }
                return true;
            }
        });
    }




    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return bookList.size();
        }

        @Override
        public Object getItem(int position) {
            return bookList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Book book = bookList.get(position);
            Holder holder=null;
            if (convertView == null||convertView.getTag()==null ) {
                convertView = layoutInflater.inflate(R.layout.bookstore_row, parent, false);
                TextView text = (TextView) convertView.findViewById(R.id.text);
                ImageView image = (ImageView) convertView.findViewById(R.id.image);
                FrameLayout fl_del= (FrameLayout) convertView.findViewById(R.id.fl_del);
                holder=new Holder(text,image,fl_del);
                convertView.setTag(holder);
            }
            holder=(Holder)convertView.getTag();
            holder.text.setText(book.getName());
            holder.image.setImageResource(icon[position%4]);
            if(delFlag){
                holder.fl_del.setVisibility(View.VISIBLE);
            }else{
                holder.fl_del.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

        class Holder {
            TextView text;
            ImageView image;
            FrameLayout fl_del;

            public Holder(TextView text, ImageView image, FrameLayout fl_del) {
                this.text = text;
                this.image = image;
                this.fl_del = fl_del;
            }
        }
    }



    public void setDelFlag(boolean delFlag){
        this.delFlag=delFlag;
        if(myAdapter!=null){
            myAdapter.notifyDataSetChanged();
        }
    }
    public boolean getDelFlag(){
        return delFlag;
    }

}
