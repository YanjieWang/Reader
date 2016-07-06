package reader.a6pu.com.reader.self_define_view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;


import java.security.PrivateKey;
import java.text.BreakIterator;
import java.util.ArrayList;

import reader.a6pu.com.reader.R;

/**
 * Created by 王燕杰 on 2016/5/25.
 */
public class ReaderView extends View {
    private Bitmap current_bitmap;//当前页内容
    private Bitmap next_bitmap;//下一页内容
    private Bitmap blank_bitmap;//空白页
    private int next_page; //下页页码
    private Canvas my_canvas;//生成下一页
    private Paint my_paint;//绘制当前页
    private TextPaint textPaint;//绘制文字用
    private Paint.FontMetrics fontMetrics;//计算行高度需用到
    private ArrayList<ArrayList<String>> pageList;  //存放每页内容
    private int totalPageCount;//总共页数
    private int currentPageIndex; //当前页码
    private float startX;//按下时x坐标
    private float startY;//按下时y坐标
    private float deltX;//移动时x相对于startX差值
    private float deltY;//移动时y相对于startY差值
    private int height;//绘制高度
    private int width;//绘制宽度
    private boolean isAutoScroll;//标记是否在自由滚动
    private boolean isTouchWhenAutoScroll;//标记该次点击是否是在自动滚动时
    private MyAnimatorListener myAnimatorListener;//自动滚动动画
    private Paint paintPath;//用于绘制下一页未被遮挡部分，与pathNextPageFoot配合
    private Paint paintShader;//用于绘制阴影，与pathShader配合
    private Matrix matrix;//用于对当前页进行旋转与对称
    private Path pathNextPageFoot;//用于绘制下一页未被遮挡部分，与paintPath配合
    private Path pathShader;//用于绘制阴影，与paintShader配合
    private int moveFlag=0;//移动状态0未移动，1，upLeft，2.upRight，3.downLeft，4.downRight
    private final int FLAG_NO_MOVE=0;
    private final int FLAG_UP_LEFT=1;
    private final int FLAG_UP_RIGHT=2;
    private final int FLAG_DOWN_LEFT=3;
    private final int FLAG_DOWN_RIGHT=4;
    private int textSize=40;//字体大小，默认40
    private int textColor=0xff000000;//默认黑色字体
    private int backGround= R.drawable.bg01;//默认背景
    private String text="";
    private OnChapterNeedChangListener onChapterNeedChangListener;
    public void setOnChapterNeedChangListener(OnChapterNeedChangListener onChapterNeedChangListener){
        this.onChapterNeedChangListener=onChapterNeedChangListener;
    }
    public interface OnChapterNeedChangListener{
        void onNeedNextChapter();
        void onNeedPreChapter();
    }

    public ReaderView(Context context) {
        super(context);
        Log.i("aaaa","ReaderView");
        if(pageList==null){
            pageList=new ArrayList<ArrayList<String>>();
        }
    }
    public ReaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("aaaa","ReaderView");
        if(pageList==null){
            pageList=new ArrayList<ArrayList<String>>();
        }

    }


    public void setTxtColor(int color){
        this.textColor=color;
    }
    public void setTxtSize(int size){
        this.textSize=size;
    }
    public void setBackGround(int backGround){
        this.backGround=backGround;
    }

    //获取可绘制宽度、高度
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i("aaaa","onMeasure");
        width=MeasureSpec.getSize(widthMeasureSpec);
        height=MeasureSpec.getSize(heightMeasureSpec);
        init();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init() {
        paintPath=new Paint();
        paintShader=new Paint();
        pathNextPageFoot=new Path();
        pathShader=new Path();
        matrix=new Matrix();
        my_paint=new Paint();
        textPaint=new TextPaint();
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        fontMetrics = textPaint.getFontMetrics();
        myAnimatorListener=new MyAnimatorListener();
        blank_bitmap=initBitmap(BitmapFactory.decodeResource(getResources(),backGround),width,height);
        next_bitmap=Bitmap.createBitmap(blank_bitmap);
        current_bitmap=Bitmap.createBitmap(blank_bitmap);
    }


    //重新调整bitmap大小
    private Bitmap initBitmap(Bitmap bitmap,int toWith,int toHeight){
        Bitmap temp= Bitmap.createBitmap(toWith,toHeight, Bitmap.Config.ARGB_8888);
        Rect rectFrom = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect rectTo = new Rect(0, 0, toWith, toHeight);
        if(my_canvas==null){
            my_canvas=new Canvas(temp);
        }else {
            my_canvas.setBitmap(temp);
        }
        my_canvas.drawBitmap(bitmap,rectFrom,rectTo,my_paint);
        return temp;
    }


    private void drawText(Bitmap bitmap,ArrayList<String> page){
        //绘制文字
        if(my_canvas==null){
            my_canvas=new Canvas(bitmap);
        }else {
            my_canvas.setBitmap(bitmap);
        }
        for (int i = 0; i < page.size(); i++) {
            my_canvas.drawText(page.get(i), 0, (fontMetrics.bottom - fontMetrics.top) * (i + 1), textPaint);
        }
        Shader shader2= new BitmapShader(bitmap,Shader.TileMode.REPEAT,Shader.TileMode.MIRROR);
        paintPath.setShader(shader2);
    }




    //字符串分页
    @NonNull
    public void setContent(String text) {
        this.text = text;
        if (pageList == null) {
            pageList = new ArrayList<ArrayList<String>>();
        } else {
            pageList.clear();
        }
        if (text == null) {
            return;
        }
        //分割字符串,分行
        ArrayList<String> strList = new ArrayList<String>();
        while (text.length() > 0) {
            int i = 0;
            int index = text.indexOf("\n");
            while (text.length() > i && textPaint.measureText(text, 0, i) < width && ((index >= 0 && i <= index) || index < 0)) {
                i++;
            }
            if (i == 1) {
                if (text.indexOf("\n") == 0) {
                    text.trim();
                    if (text.lastIndexOf("\n") == 0) {
                        text = "";
                    } else {
                        text = text.substring(1, text.length());
                    }
                } else {
                    strList.add(text.substring(0, 0));
                    text = "";
                }
            } else {
                strList.add(text.substring(0, i - 1));
                text = text.substring(i - 1, text.length());
            }
        }


        //分割字符串，分页

        int row_per_page= (int)(height/(fontMetrics.bottom-fontMetrics.top));
        for(int pageIndex=0;pageIndex*row_per_page<strList.size();pageIndex++){
            ArrayList<String> tempPage=new ArrayList<String>();
            for(int i=pageIndex*row_per_page;i<strList.size()&&(i<(pageIndex+1)*row_per_page);i++){
                tempPage.add(strList.get(i));
            }
            pageList.add(tempPage);
        }
        totalPageCount=pageList.size();
        //绘制第一页文本
        if(pageList.size()<=currentPageIndex){
            currentPageIndex=pageList.size()-1;
        }
        current_bitmap=Bitmap.createBitmap(blank_bitmap);
        drawText(current_bitmap,pageList.get(currentPageIndex));
        invalidate();
    }

    public long  getCharacterIndexOfCurrentPage() {
        String txt=this.text;
        int row_per_page= (int)(height/(fontMetrics.bottom-fontMetrics.top));
        int now_line=0;
        int now_page=0;
        long now_index=0;
        if(currentPageIndex==0){
            return 0;
        }
        //分割字符串,分行
        ArrayList<String> strList=new ArrayList<String>();
        while(txt.length()>0){
            int i=0;
            int index=txt.indexOf("\n");
            while(txt.length()>i&&textPaint.measureText(txt,0,i)<width&&((index>=0&&i<=index)||index<0)){
                i++;
                now_index++;
            }

            if(i==1){
                if(txt.indexOf("\n")==0) {
                    txt.trim();
                    if(txt.lastIndexOf("\n")==0){
                        txt="";
                    }else{
                        txt = txt.substring(1, txt.length());
                    }
                }else {
                    now_line++;
                    if(now_line%row_per_page==0){
                        now_page++;
                        if(now_page==currentPageIndex){
                            return now_index;
                        }
                    }
                    txt = "";
                }
            }else{
                now_line++;
                if(now_line%row_per_page==0){
                    now_page++;
                    if(now_page==currentPageIndex){
                        return now_index-i+1;
                    }
                }
                txt=txt.substring(i-1,txt.length());
            }
        }
        return -1;

    }



    public int  setPageByCharacterIndex(long oldCharacterIndex,String txt) {
        int row_per_page= (int)(height/(fontMetrics.bottom-fontMetrics.top));
        int now_line=0;
        int now_page=0;
        double now_index=0;
        if(oldCharacterIndex==0){
            currentPageIndex=0;
            return 0;
        }
        //分割字符串,分行
        while(txt.length()>0){
            int i=0;
            int index=txt.indexOf("\n");
            while(txt.length()>i&&textPaint.measureText(txt,0,i)<width&&((index>=0&&i<=index)||index<0)){
                i++;
                now_index++;
                if(now_index==oldCharacterIndex){
                    currentPageIndex=now_page+1;
                    return now_page;
                }
            }

            if(i==1){
                if(txt.indexOf("\n")==0) {
                    txt.trim();
                    if(txt.lastIndexOf("\n")==0){
                        txt="";
                    }else{
                        txt = txt.substring(1, txt.length());
                    }
                }else {
                    now_line++;
                    if(now_line%row_per_page==0){
                        now_page++;
                    }
                    txt = "";
                }
            }else{
                now_line++;
                if(now_line%row_per_page==0){
                    now_page++;
                }
                txt=txt.substring(i-1,txt.length());
            }
        }
        return -1;
        //分割字符串，分页

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制本页
        canvas.drawBitmap(current_bitmap,0,0,my_paint);
        // 绘制本页折叠部分
        canvas.drawBitmap(current_bitmap,matrix,my_paint);

        // 绘制下页页脚
        canvas.drawPath(pathNextPageFoot,paintPath);
        //绘制阴影
        canvas.drawPath(pathShader,paintShader);
        super.onDraw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(isAutoScroll){
                    isTouchWhenAutoScroll=true;
                }else{
                    isTouchWhenAutoScroll=false;
                    startX=event.getRawX();
                    startY=event.getRawY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(isTouchWhenAutoScroll){
                    return true;
                }
                deltX=event.getRawX()-startX;
                deltY=event.getRawY()-startY;
                //移动距离小于10时无效
                if (Math.sqrt(deltX*deltX+deltY*deltY)<10||isTouchWhenAutoScroll){
                    return true;
                }



                if(deltX>0&&deltY>0&&currentPageIndex>0){
                    if(next_page!=currentPageIndex-1){
                        next_page=currentPageIndex-1;
                        initNextPage();
                    }
                    moveFlag=FLAG_DOWN_RIGHT;
                    Log.i("aaaa","FLAG_DOWN_RIGHT");
                    drawDownRight();
                    break;
                }

                if(deltX>0&&deltY<0&&currentPageIndex>0){
                    if(next_page!=currentPageIndex-1){
                        next_page=currentPageIndex-1;
                        initNextPage();
                    }
                    moveFlag=FLAG_UP_RIGHT;

                    Log.i("aaaa","FLAG_UP_RIGHT");
                    drawUpRight();
                    break;
                }
                if(deltX<0&&deltY>0&&currentPageIndex<pageList.size()-1){
                    if(next_page!=currentPageIndex+1){
                        next_page=currentPageIndex+1;
                        initNextPage();
                    }
                    moveFlag=FLAG_DOWN_LEFT;
                    Log.i("aaaa","FLAG_DOWN_LEFT");
                    drawDownLeft();
                    break;
                }

                if(deltX<0&&deltY<0&&currentPageIndex<pageList.size()-1){
                    if(next_page!=currentPageIndex+1){
                        next_page=currentPageIndex+1;
                        initNextPage();
                    }
                    moveFlag=FLAG_UP_LEFT;
                    Log.i("aaaa","FLAG_UP_LEFT");
                    drawUpLeft();
                    break;
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.i("aaaa","ACTION_UP:");

                autoMove();
                break;
        }
        return true;
    }

    //自动移动
    private void autoMove() {
        //移动距离小于10时无效
        if (Math.sqrt(deltX*deltX+deltY*deltY)<10){
            return ;
        }

        //downRight
//        Log.i("aaa","deltX:"+deltX+"-deltY:"+deltY+"-currentPageIndex:"+currentPageIndex+"-isTouchWhenAutoScroll:"+isTouchWhenAutoScroll);
        if(deltX>0&&currentPageIndex==0&&!isTouchWhenAutoScroll){
            //有上一章节需求时回调
            if(onChapterNeedChangListener!=null) {
                Log.i("aaa","onNeedPreChapter()");
                onChapterNeedChangListener.onNeedPreChapter();
            }
        }
        if(deltX<0&&currentPageIndex==pageList.size()-1&&!isTouchWhenAutoScroll){
            //有上一章节需求时回调
            if(onChapterNeedChangListener!=null) {
                Log.i("aaa","onNeedNextChapter");
                onChapterNeedChangListener.onNeedNextChapter();
            }
        }

        if(deltX>0&&deltY>0&&currentPageIndex>0&&!isTouchWhenAutoScroll){

            double tan=deltY/deltX;
            double cos =deltX/Math.sqrt(deltY*deltY+deltX*deltX);
            double sin =deltY/Math.sqrt(deltY*deltY+deltX*deltX);
            double a =width*tan;
            double b =height-a;
            double e =b*cos*sin;

            isAutoScroll=true;
            ValueAnimator valueAnimator = ValueAnimator.ofFloat( deltX, (float)( 2*(width+e)));
            valueAnimator.setDuration(600);
            valueAnimator.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    return input;
                }
            });
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Float res = (Float) animation.getAnimatedValue();
                    deltY = res * deltY / deltX;
                    deltX = res;
                    drawDownRight();
                }
            });
            valueAnimator.addListener(myAnimatorListener);
            valueAnimator.start();
            currentPageIndex--;
            return;
        }


        //downLeft

        if(deltX<0&&deltY>0&&currentPageIndex<pageList.size()-1&&!isTouchWhenAutoScroll){
            double tan=-deltY/deltX;
            double cos =-deltX/Math.sqrt(deltY*deltY+deltX*deltX);
            double sin =deltY/Math.sqrt(deltY*deltY+deltX*deltX);
            double a =width*tan;
            double b =height-a;
            double e =b*cos*sin;
            isAutoScroll=true;
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(deltX, -(float)( 2*(width+e)));
            valueAnimator.setDuration(600);
            valueAnimator.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    return input;
                }
            });
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Float res = (Float) animation.getAnimatedValue();
                    deltY = res * deltY / deltX;
                    deltX = res;
                    drawDownLeft();
                }
            });
            valueAnimator.addListener(myAnimatorListener);
            valueAnimator.start();
            currentPageIndex++;
            return;
        }
        //upLeft


        if(deltX<0&&deltY<0&&currentPageIndex<pageList.size()-1&&!isTouchWhenAutoScroll) {
            double tan = deltY / deltX;
            double cos = -deltX / Math.sqrt(deltY * deltY + deltX * deltX);
            double sin = -deltY / Math.sqrt(deltY * deltY + deltX * deltX);
            double a = width * tan;
            double b = height - a;
            double e = b * cos * sin;
            isAutoScroll = true;
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(deltX, -(float) (2 * (width + e)));
                valueAnimator.setDuration(900);
                valueAnimator.setInterpolator(new Interpolator() {
                    @Override
                    public float getInterpolation(float input) {
                        return input;
                    }
                });
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float res = (Float) animation.getAnimatedValue();
                        deltY = res * deltY / deltX;
                        deltX = res;
                        drawUpLeft();
                    }
                });
                valueAnimator.addListener(myAnimatorListener);
                valueAnimator.start();
                currentPageIndex++;
                return;
        }

            //upRight

            if(deltX>0&&deltY<0&&currentPageIndex>0&&!isTouchWhenAutoScroll){
                double tan=-deltY/deltX;
                double cos =deltX/Math.sqrt(deltY*deltY+deltX*deltX);
                double sin =-deltY/Math.sqrt(deltY*deltY+deltX*deltX);
                double a =width*tan;
                double b =height-a;
                double e =b*cos*sin;
                isAutoScroll=true;
                ValueAnimator valueAnimator = ValueAnimator.ofFloat( deltX, (float) (2 * (width + e)));
                valueAnimator.setDuration(900);
                valueAnimator.setInterpolator(new Interpolator() {
                    @Override
                    public float getInterpolation(float input) {
                        return input;
                    }
                });
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        Float res = (Float) animation.getAnimatedValue();
                        deltY = res * deltY / deltX;
                        deltX = res;
                        drawUpRight();
                    }
                });
                valueAnimator.addListener(myAnimatorListener);
                valueAnimator.start();
                currentPageIndex--;
                return;
            }

            if(deltY>0){}
        }



    private void drawDownRight() {
        //求y轴与touch点到
//Log.i("aaa",deltX+"---"+deltY);
        float dDeltX=deltX;
        float dDeltY=deltY;
        double l=Math.sqrt((dDeltX*dDeltX+dDeltY*dDeltY));
        double sin=dDeltX/l;
        double cos=dDeltY/l;

        //求交点
        double a=l/2/sin;//与y=hight交点
        double b=l/2/cos;//与x=width交点

//        Log.e("aaa",a+"---b"+b);

        //设置下页页脚path与阴影Path
        pathNextPageFoot.reset();
        pathShader.reset();
        pathNextPageFoot.moveTo(0,0);//****************
        pathShader.moveTo(0,0);//****************
        if(height-b<0) {

            double e=(b-height)*a/b;////中线与x=0交点
            pathNextPageFoot.lineTo(0, height);
            pathNextPageFoot.lineTo((float) e, height);

            double f = Math.tan((Math.PI / 2 - Math.asin(sin)) * 2) * (b - height);//阴影边界与x=0交点
            pathShader.lineTo(0, height);
            pathShader.lineTo((float) f, height);
        }else{
            pathNextPageFoot.lineTo(0, (float) ( b));
            pathShader.lineTo(0, (float) ( b));
        }
        pathShader.lineTo(deltX,deltY);
        if(width-a<0) {
            double g=(a-width)*b/a;////中线与y=0交点
            pathNextPageFoot.lineTo(width,(float)g);
            pathNextPageFoot.lineTo(width,0);

            double h=Math.tan((Math.asin(sin))*2)*(a-width);//阴影边界与y=0交点

            pathShader.lineTo(width,(float)h);
            pathShader.lineTo(width,0);


        }else{
            pathNextPageFoot.lineTo((float)(a),0);
            pathShader.lineTo((float)(a),0);
        }
        pathNextPageFoot.close();
        pathShader.close();

        //设置本页页脚旋转矩阵
        matrix.reset();
        matrix.postScale(1, -1);
        matrix.postTranslate(0,0);
        matrix.postRotate((float)(-180*2*Math.asin(sin)/ Math.PI));
        matrix.postTranslate(deltX,deltY);
        int[]coloes={0x22222222,0xbb000000,0x22222222};
        float[]p={0f,0.5f,1f};
        Shader shader=new LinearGradient((float)((deltX)/2+160*sin),(float)((deltY)/2+160*cos),(float)((deltX)/2-160*sin),(float)((deltY)/2-160*cos),coloes,p,Shader.TileMode.CLAMP);
        paintShader.setShader(shader);
        invalidate();
    }
    private void drawDownLeft() {
        //坐标转换
        float deltX=this.deltX+width;


        //求y轴与touch点到
        float dDeltX=width-deltX;
        float dDeltY=deltY;
        double l=Math.sqrt((dDeltX*dDeltX+dDeltY*dDeltY));
        double sin=dDeltX/l;
        double cos=dDeltY/l;

        //求交点
        double a=l/2/sin;//与y=hight交点
        double b=l/2/cos;//与x=width交点

//        Log.e("aaa",a+"---b"+b);

        //设置下页页脚path与阴影Path
        pathNextPageFoot.reset();
        pathShader.reset();
        pathNextPageFoot.moveTo(width,0);//****************
        pathShader.moveTo(width,0);//****************
        if(height-b<0) {
            double e=(b-height)*a/b;////中线与x=0交点
            pathNextPageFoot.lineTo(width, height);
            pathNextPageFoot.lineTo(width-(float)e, height);

            double f=Math.tan((Math.PI/2-Math.asin(sin))*2)*(b-height);//阴影边界与x=0交点
            pathShader.lineTo(width,height);
            pathShader.lineTo(width-(float)f, height);
        }else{
            pathNextPageFoot.lineTo(width, (float) ( b));
            pathShader.lineTo(width, (float) ( b));
        }
        pathShader.lineTo(deltX,deltY);
        if(width-a<0) {
            double g=(a-width)*b/a;////中线与y=0交点
            pathNextPageFoot.lineTo(0,(float)g);
            pathNextPageFoot.lineTo(0,0);

            double h=Math.tan((Math.asin(sin))*2)*(a-width);//阴影边界与y=0交点

            pathShader.lineTo(0,(float)h);
            pathShader.lineTo(0,0);


        }else{
            pathNextPageFoot.lineTo((float)(width-a),0);
            pathShader.lineTo((float)(width-a),0);
        }
        pathNextPageFoot.close();
        pathShader.close();

        //设置本页页脚旋转矩阵
        matrix.reset();
        matrix.postScale(1, -1);
        matrix.postTranslate(-width,0);
        matrix.postRotate((float)(180*2*Math.asin(sin)/ Math.PI));
        matrix.postTranslate(deltX,deltY);
        int[]coloes={0x22222222,0xbb000000,0x22222222};
        float[]p={0f,0.5f,1f};
        Shader shader=new LinearGradient((float)((deltX+width)/2-160*sin),(float)((deltY)/2+160*cos),(float)((deltX+width)/2+160*sin),(float)((deltY)/2-160*cos),coloes,p,Shader.TileMode.CLAMP);
        paintShader.setShader(shader);
        invalidate();
    }

    private void drawUpRight() {
        float deltY=this.deltY+height;
        //求y轴与touch点到
        float dDeltX=deltX-0;
        float dDeltY=height-deltY;
        double l=Math.sqrt((dDeltX*dDeltX+dDeltY*dDeltY));
        double sin=dDeltX/l;
        double cos=dDeltY/l;

        //求交点
        double a=l/2/sin;//与y=hight交点
        double b=l/2/cos;//与x=width交点

//        Log.e("aaa",a+"---b"+b);

        //设置下页页脚path与阴影Path
        pathNextPageFoot.reset();
        pathShader.reset();
        pathNextPageFoot.moveTo(0,height);
        pathShader.moveTo(0,height);
        if(height-b<0) {
            double e=(b-height)*a/b;////中线与x=0交点
            pathNextPageFoot.lineTo(0,0);
            pathNextPageFoot.lineTo((float)e, 0);
            double f=Math.tan((Math.PI/2-Math.asin(sin))*2)*(b-height);//阴影边界与x=0交点
            pathShader.lineTo(0,0);
            pathShader.lineTo((float)f, 0);
        }else{
            pathNextPageFoot.lineTo(0, (float) (height - b));
            pathShader.lineTo(0, (float) (height - b));
        }
        pathShader.lineTo(deltX,deltY);
        if(width-a<0) {
            double g=(a-width)*b/a;////中线与y=0交点
            pathNextPageFoot.lineTo(width,height-(float)g);
            pathNextPageFoot.lineTo(width,height);

            double h=Math.tan((Math.asin(sin))*2)*(a-width);//阴影边界与y=0交点
            pathShader.lineTo(width,height-(float)h);
            pathShader.lineTo(width,height);

        }else{
            pathNextPageFoot.lineTo((float)a,height);
            pathShader.lineTo((float)a,height);
        }
        pathNextPageFoot.close();
        pathShader.close();

        //设置本页页脚旋转矩阵
        matrix.reset();
        matrix.postScale(1, -1);
        matrix.postTranslate(0,height);
        matrix.postRotate((float)(180*2*Math.asin(sin)/ Math.PI));
        matrix.postTranslate(deltX,deltY);
        int[]coloes={0x22222222,0xbb000000,0x22222222};
        float[]p={0f,0.5f,1f};
        Shader shader=new LinearGradient((float)((deltX)/2-160*sin),(float)((deltY+height)/2+160*cos),(float)((deltX)/2+160*sin),(float)((deltY+height)/2-160*cos),coloes,p,Shader.TileMode.CLAMP);        paintShader.setShader(shader);
        invalidate();
    }


    private void drawUpLeft() {
        float deltY=this.deltY+height;
        float deltX=this.deltX+width;
        //求y轴与touch点到
        float dDeltX=width-deltX;
        float dDeltY=height-deltY;
        double l=Math.sqrt((dDeltX*dDeltX+dDeltY*dDeltY));
        double sin=dDeltX/l;
        double cos=dDeltY/l;

        //求交点
        double a=l/2/sin;//与y=hight交点
        double b=l/2/cos;//与x=width交点

//        Log.e("aaa",a+"---b"+b);

        //设置下页页脚path与阴影Path
        pathNextPageFoot.reset();
        pathShader.reset();
        pathNextPageFoot.moveTo(width,height);
        pathShader.moveTo(width,height);
        if(height-b<0) {
            double e=(b-height)*a/b;////中线与x=0交点
            pathNextPageFoot.lineTo(width, 0);
            pathNextPageFoot.lineTo(width-(float)e, 0);

            double f=Math.tan((Math.PI/2-Math.asin(sin))*2)*(b-height);//阴影边界与x=0交点
            pathShader.lineTo(width,0);
            pathShader.lineTo(width-(float)f, 0);
        }else{
            pathNextPageFoot.lineTo(width, (float) (height - b));
            pathShader.lineTo(width, (float) (height - b));
        }
        pathShader.lineTo(deltX,deltY);
        if(width-a<0) {
            double g=(a-width)*b/a;////中线与y=0交点
            pathNextPageFoot.lineTo(0,height-(float)g);
            pathNextPageFoot.lineTo(0,height);

            double h=Math.tan((Math.asin(sin))*2)*(a-width);//阴影边界与y=0交点
            pathShader.lineTo(0,height-(float)h);
            pathShader.lineTo(0,height);

        }else{
            pathNextPageFoot.lineTo((float)(width-a),height);
            pathShader.lineTo((float)(width-a),height);
        }
        pathNextPageFoot.close();
        pathShader.close();

        //设置本页页脚旋转矩阵
        matrix.reset();
        matrix.postScale(1, -1);
        matrix.postTranslate(-width,height);
        matrix.postRotate(-(float)(180*2*Math.asin(sin)/ Math.PI));
        matrix.postTranslate(deltX,deltY);
        int[]coloes={0x22222222,0xbb000000,0x22222222};
        float[]p={0f,0.5f,1f};
        Shader shader=new LinearGradient((float)((deltX+width)/2+160*sin),(float)((deltY+height)/2+160*cos),(float)((deltX+width)/2-160*sin),(float)((deltY+height)/2-160*cos),coloes,p,Shader.TileMode.CLAMP);
        paintShader.setShader(shader);
        invalidate();
    }


    //初始化下张图片
    private void initNextPage(){
        next_bitmap=Bitmap.createBitmap(blank_bitmap);
        drawText(next_bitmap,pageList.get(next_page));
    }


    //直接切换至第current_page页；
    public void setCurrentPage(int currentPageIndex){
        if(currentPageIndex>=0&&currentPageIndex<totalPageCount) {
            this.currentPageIndex = currentPageIndex;
            current_bitmap=Bitmap.createBitmap(blank_bitmap);
            drawText(current_bitmap,pageList.get(currentPageIndex));
            invalidate();
        }
    }

    public int getTotalPage(){
        return totalPageCount;
    }


    //切换到下一页
    public void showNextPageIfHas(boolean withEffect){
        if(currentPageIndex+1<totalPageCount) {
            currentPageIndex = currentPageIndex+1;
            drawText(next_bitmap,pageList.get(currentPageIndex+1));
            if(withEffect){
                deltX=-11;
                autoMove();
            }else {
                invalidate();
            }
        }
    }
    //切换到上一页
    public void showPrePageIfHas(boolean withEffect){
        if(currentPageIndex-1>=0) {
            currentPageIndex = currentPageIndex-1;
            drawText(next_bitmap,pageList.get(currentPageIndex+1));
            if(withEffect){
                deltX=11;
                autoMove();
            }else {
                invalidate();
            }
        }
    }

    //返回当前页码
    public int getCurrentPageIndex(){
        return currentPageIndex;
    }



    //动画结束监听器
    private class MyAnimatorListener implements Animator.AnimatorListener {
        @Override
        public void onAnimationStart(Animator animation) {}
        @Override
        public void onAnimationEnd(Animator animation) {
            //动画执行完交换next_bitmap；
            Bitmap temp=next_bitmap;
            current_bitmap=next_bitmap;
            next_bitmap=temp;
            isAutoScroll=false;
            deltX=0;
            deltY=0;
        }
        @Override
        public void onAnimationCancel(Animator animation) {}
        @Override
        public void onAnimationRepeat(Animator animation) {}
    }
}
