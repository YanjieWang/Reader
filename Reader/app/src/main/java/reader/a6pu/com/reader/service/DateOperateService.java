package reader.a6pu.com.reader.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import reader.a6pu.com.reader.R;
import reader.a6pu.com.reader.activity.MainActivity;
import reader.a6pu.com.reader.entity.Book;
import reader.a6pu.com.reader.entity.BookChapter;
import reader.a6pu.com.reader.entity.BookContent;
import reader.a6pu.com.reader.entity.BookType;
import reader.a6pu.com.reader.entity.Elecment;
import reader.a6pu.com.reader.interface_calback.DataUpdatedListener;
import reader.a6pu.com.reader.interface_calback.OnNotificationReceived;
import reader.a6pu.com.reader.utils.DbUtilsBookDate;

/**
 * Created by 王燕杰 on 2016/5/30.
 */
public class DateOperateService extends Service{
    private  final String TAG = "Date";

    //type查询
    private final String TYPE_DONLOAD_URL="https://route.showapi.com/211-3?showapi_appid=18576&showapi_timestamp=&showapi_sign=abdca2df6eff4783aa19ef9b69720cc8";

    //book查询
    private final String BOOK_DONLOAD_FIRST="https://route.showapi.com/211-2?keyword=";
    private String keyword="";
    private final String BOOK_DONLOAD_PAGE="&page=";
    private String page="";
    private final String BOOK_DONLOAD_TYPE="&pre_match=false&showapi_appid=18576&showapi_timestamp=&typeId=";
    private String typeId="";
    private final String BOOK_DONLOAD_END="&showapi_sign=abdca2df6eff4783aa19ef9b69720cc8";

    //章节查询
    private final String CHAPTER_DONLOAD_FIRST="https://route.showapi.com/211-1?bookId=";
    private String bookId="";
    private final String CHAPTER_DONLOAD_END="&showapi_appid=18576&showapi_timestamp=&showapi_sign=abdca2df6eff4783aa19ef9b69720cc8";

//内容查询
    private final String CONTENT_DONLOAD_FIRST="https://route.showapi.com/211-4?bookId=";
    private final String CONTENT_DONLOAD_CID="&cid=";
    private String chapterId="";
    private final String CONTENT_DONLOAD_END="&showapi_appid=18576&showapi_timestamp=&showapi_sign=abdca2df6eff4783aa19ef9b69720cc8";


    //get模式选择
    private final int MODE_GET_TYPE_LIST=1;
    private final int MODE_GET_BOOK_LIST=2;
    private final int MODE_GET_CHAPTER_LIST=3;
    private final int MODE_GET_CCONTENT=4;
    private final int MODE_ERROR=5;

    //标志位，标记用户设置

    //是否将所有已浏览过的内容保存在本地
    private boolean isAutoSaveToLocal=true;



    private IBinder dateOperateIBinder;
    private OnNotificationReceived onNotificationReceived;
    private ArrayList<BookType> typeList;
    private ArrayList<Book> bookList;
    private ArrayList<BookChapter> chapterList;
    private BookContent content;
    private boolean runFlag;
    private boolean withOutAppend;
    private MyAsyncTask myAsyncTask;
    private int runMode;
    private int total_book_num;
    private int total_book_page;
    private int current_book_page;
    private DbUtilsBookDate dbUtilsBookDate;
    private ArrayList<Elecment>elecmentList;


    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        if(elecmentList==null){
            elecmentList=new ArrayList<Elecment>();
        }
        if(dateOperateIBinder==null){
            dateOperateIBinder=new DateOperateIBinder();
        }
        if(dbUtilsBookDate==null){
            dbUtilsBookDate=new DbUtilsBookDate();
        }
        if(myAsyncTask==null){
            myAsyncTask=new MyAsyncTask();
            runFlag=true;
            myAsyncTask.execute();
        }


        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                //模拟推送
                int tempNum=(int)(Math.random()*20);

                if(bookList!=null&&bookList.size()>tempNum) {
                    NotificationManager nmss = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);

                    intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.putExtra("bookId", bookList.get(tempNum).getBookId());
                    intent1.putExtra("name", bookList.get(tempNum).getName());
                    intent1.putExtra("newChapter", bookList.get(tempNum).getNewChapter());
                    intent1.putExtra("updateTime", bookList.get(tempNum).getUpdateTime());
                    intent1.putExtra("typeName", bookList.get(tempNum).getTypeName());
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
                    Notification notification = new Notification.Builder(getApplicationContext())
                            .setTicker("小说有更新")
                            .setContentTitle(bookList.get(tempNum).getName())
                            .setContentInfo("最新章节:"+bookList.get(tempNum).getNewChapter())
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setSmallIcon(R.drawable.icon)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.enter1))
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true)
                            .build();
                    nmss.notify(111, notification);
                }
            }
        },60000,60000);
        return dateOperateIBinder;
    }





    public class DateOperateIBinder extends Binder {
        public ArrayList<BookType> getTypeList(DataUpdatedListener dataUpdatedListener, boolean withOutAppend){
            if(typeList==null){
                typeList=new ArrayList<BookType>();
            }
            //判断是要获取当前list还是要更新list
            elecmentList.add(new Elecment(dataUpdatedListener,"", "", "", "", "", withOutAppend,MODE_GET_TYPE_LIST));
        return typeList;
        }


        public ArrayList<Book> getBookList(DataUpdatedListener dataUpdatedListener, String keyword,String typeId ,String page,boolean withOutAppend){
            if(bookList==null){
                bookList=new ArrayList<Book>();
            }
            //判断是要获取当前list还是要更新list
            Log.i(TAG,withOutAppend+"withOutAppend");
            elecmentList.add(new Elecment(dataUpdatedListener, keyword, typeId, page, "", "", withOutAppend,MODE_GET_BOOK_LIST));
            return bookList;
        }


        public ArrayList<BookChapter> getChapterList(DataUpdatedListener dataUpdatedListener, String bookId, boolean withOutAppend){
            if(chapterList==null){
                chapterList=new ArrayList<BookChapter>();
            }
            //判断是要获取当前list还是要更新list
            elecmentList.add(new Elecment(dataUpdatedListener, "", "", "", bookId, "", withOutAppend,MODE_GET_CHAPTER_LIST));
            return chapterList;
        }

        public BookContent getBookContent(DataUpdatedListener dataUpdatedListener, String bookId, String chapterId,boolean withOutAppend){
            Log.i("aaa"," DateOperateService:getBookContent");
            if(content==null){
                content=new BookContent();
            }
            //判断是要获取当前list还是要更新list
            elecmentList.add(new Elecment(dataUpdatedListener, "", "", "", bookId, chapterId, withOutAppend,MODE_GET_CCONTENT));
            return content;
        }

        public void setOnNotificationReceived(OnNotificationReceived onNotificationReceived){
            DateOperateService.this.onNotificationReceived=onNotificationReceived;
        }

    }

    //加载数据
    class MyAsyncTask extends AsyncTask{
        private boolean publishOver=true;

        @Override
        protected void onProgressUpdate(Object[] values) {
            Log.i(TAG,"onProgressUpdate");
            int temp=(Integer)values[0];
            switch (temp){
                case MODE_GET_TYPE_LIST:
                    elecmentList.get(0).dataUpdatedListener.onTypeListUpdated(typeList);
                    Log.i(TAG,"onProgressUpdate：MODE_GET_TYPE_LIST");
                    elecmentList.remove(0);
                    publishOver=true;
                    break;
                case MODE_GET_BOOK_LIST:
                    Log.i(TAG,"onProgressUpdate：MODE_GET_BOOK_LIST");
                    elecmentList.get(0).dataUpdatedListener.onBookListUpdated(total_book_num,total_book_page,current_book_page,bookList);
                    elecmentList.remove(0);
                    publishOver=true;
                    break;
                case MODE_GET_CHAPTER_LIST:
                    Log.i(TAG,"onProgressUpdate：MODE_GET_CHAPTER_LIST");
                    elecmentList.get(0).dataUpdatedListener.onChapterUpdated(chapterList);
                    elecmentList.remove(0);
                    publishOver=true;
                    break;
                case MODE_GET_CCONTENT:
                    Log.i(TAG,"onProgressUpdate：MODE_GET_CCONTENT");
                    elecmentList.get(0).dataUpdatedListener.onContentUpdated(content);
                    elecmentList.remove(0);
                    publishOver=true;
                    break;
                case MODE_ERROR:
                    Log.i(TAG,"onProgressUpdate：MODE_ERROR");
                    elecmentList.remove(0);
                    Toast.makeText(getApplicationContext(),"更新失败，请连接网络",Toast.LENGTH_SHORT).show();
                    publishOver=true;
                    break;
            }
        }

        private void refreshFlag() {
            if(elecmentList.size()>0) {
                Log.i(TAG," RefreshFlag");
                Elecment elecment = elecmentList.get(0);
                DateOperateService.this.keyword = elecment.keyword;
                DateOperateService.this.typeId = elecment.typeId;
                DateOperateService.this.page = elecment.page;
                DateOperateService.this.bookId = elecment.bookId;
                DateOperateService.this.chapterId = elecment.chapterId;
                DateOperateService.this.withOutAppend = elecment.withOutAppend;
                DateOperateService.this.runMode = elecment.runMode;
            }
        }

        @Override
        protected Object doInBackground(Object[] params) {
            //循环运行
            while (runFlag){
//                Log.i(TAG,"doInBackground----isLooping");
                if(elecmentList.size()>0&&runMode==0&& publishOver){
                    Log.i(TAG,"request RefreshFlag");
                    refreshFlag();
                }
                switch (runMode){
                    case MODE_GET_TYPE_LIST:
                        Log.i(TAG,"MODE_GET_TYPE_LIST");
                        publishOver=false;
                        refreshTypeList();
                        runMode=0;
                        break;
                    case MODE_GET_BOOK_LIST:
                        Log.i(TAG,"MODE_GET_BOOK_LIST");
                        publishOver=false;
                        refreshBookList();
                        runMode=0;
                        break;
                    case MODE_GET_CHAPTER_LIST:
                        Log.i(TAG,"MODE_GET_CHAPTER_LIST");
                        publishOver=false;
                        refreshChapterList();
                        runMode=0;
                        break;
                    case MODE_GET_CCONTENT:
                        Log.i(TAG,"数据获取失败，本地无缓存且网络未连接、或获取json异常");
                        Log.i(TAG,"MODE_GET_CCONTENT");
                        publishOver=false;
                        refreshContent();
                        runMode=0;
                        break;



                }

                try {
                    Thread.currentThread().sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        private void refreshContent() {
            String urlTemp=CONTENT_DONLOAD_FIRST+bookId+CONTENT_DONLOAD_CID+chapterId+CONTENT_DONLOAD_END;
            //本地无缓存时联网获取数据
            String res=dbUtilsBookDate.getBookContentJsonContent(bookId,chapterId);
            if(res==null){
                res = getNetString(urlTemp);
                //自动保存浏览过的内容
                if(isAutoSaveToLocal&&res!=null){
                    dbUtilsBookDate.insertBookContentJsonContent(bookId,chapterId,res);
                }

            }

            if(res==null){
                Log.i(TAG,"数据获取失败，本地无缓存且网络未连接");
                publishProgress(MODE_ERROR);
                return;
            }
            //解析字符串
            try {
                JSONObject json=new JSONObject(res);
                if(json.getInt("showapi_res_code")!=0){
                    Log.i(TAG,"错误:"+json.getString("showapi_res_error"));
                    Log.i(TAG,"refreshContent send Error Message:MODE_ERROR");;
                    publishProgress(MODE_ERROR);
                    return;
                }
                Log.i(TAG,"JSONO获取成功");
                Log.i(TAG,"JSONO获取成功");

                json=json.getJSONObject( "showapi_res_body");
                if(json.getInt("ret_code")!=0){
                    Log.i(TAG,"错误:JSONObody解析失败");
                    return;
                }
                Log.i(TAG,"JSONObody解析成功，开始解析chapter");


                String txt=null;
                if(json.has("txt")){
                    txt=json.getString("txt");
                }
                //<br /><br />替换为换行。
                txt=txt.replaceAll("<br /><br />","\n\r　　");
                content.setContent("　　"+txt);

                Log.i(TAG,"章节内容解析成功");
                publishProgress(MODE_GET_CCONTENT);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        private void refreshChapterList() {
            Log.i(TAG,"refreshChapterList");
            String urlTemp=CHAPTER_DONLOAD_FIRST+bookId+CHAPTER_DONLOAD_END;
            //本地无缓存时联网获取数据
            String res=dbUtilsBookDate.getBookChapterJsonContent(bookId);
            if(res==null){
                res = getNetString(urlTemp);
                if(isAutoSaveToLocal&&res!=null){
                    dbUtilsBookDate.insertBookChapterJsonContent(bookId,res);
                }
            }

            if(res==null){
                Log.i(TAG,"数据获取失败，本地无缓存且网络未连接、或获取json异常");
                publishProgress(MODE_ERROR);
                return;
            }

//解析字符串
            try {
                JSONObject json=new JSONObject(res);
                if(json.getInt("showapi_res_code")!=0){
                    Log.i(TAG,"错误:"+json.getString("showapi_res_error"));
                    Log.i(TAG,"refreshChapterList send Error Message:MODE_ERROR");
                    publishProgress(MODE_ERROR);
                    return;
                }
                Log.i(TAG,"JSONO获取成功");

                json=json.getJSONObject( "showapi_res_body");
                if(json.getInt("ret_code")!=0){
                    Log.i(TAG,"错误:JSONObody解析失败");
                    return;
                }
                Log.i(TAG,"JSONObody解析成功，开始解析chapter");
                json=json.getJSONObject( "book");

                if(withOutAppend) {
                    chapterList = new ArrayList<BookChapter>();
                }

                JSONArray jsonArray=json.getJSONArray( "chapterList");
                for(int i=0;i<jsonArray.length();i++){

                    String bookId=null;
                    String chapterId=null;
                    String chapterName=null;

                    json= (JSONObject) jsonArray.get(i);
                    if(json.has("bookId")){
                        bookId=json.getString("bookId");
                    }
                    if(json.has("cid")){
                        chapterId=json.getInt("cid")+"";
                    }
                    if(json.has("name")){
                        chapterName=json.getString("name");
                    }
                    chapterList.add(new  BookChapter(bookId, chapterId, chapterName) );
                }
                Log.i(TAG,"JSONObook列表解析成功");
                publishProgress(MODE_GET_CHAPTER_LIST);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        //获取bookList算法
        private void refreshBookList() {
            String tempKey="";
            try {
                //中文转码，解决部分机型不支持问题
                tempKey = URLEncoder.encode(keyword, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.i(TAG,"onProgressUpdate：MODE_GET_BOOK_LIST：refreshBookList");
            String urlTemp=BOOK_DONLOAD_FIRST+tempKey+BOOK_DONLOAD_PAGE+page+BOOK_DONLOAD_TYPE+typeId+BOOK_DONLOAD_END;
            Log.i(TAG,urlTemp);
            String res=dbUtilsBookDate.getBookJsonContent(keyword,page,typeId);
            if(res==null){
                res = getNetString(urlTemp);
                if(isAutoSaveToLocal&&res!=null){
                    dbUtilsBookDate.insertBookJsonContent(keyword,page,typeId,res);
                }
            }

            Log.i(TAG,"refreshBookList：res="+res);
            if(res==null){
                Log.i(TAG,"数据获取失败，本地无缓存且网络未连接");
                publishProgress(MODE_ERROR);
                return;
            }

            //解析字符串
            try {
                JSONObject json=new JSONObject(res);
                if(json.getInt("showapi_res_code")!=0){
                    Log.i(TAG,"错误:"+json.getString("showapi_res_error"));
                    Log.i(TAG,"refreshBookList send Error Message:MODE_ERROR");
                    publishProgress(MODE_ERROR);
                    return;
                }
                Log.i(TAG,"JSONO获取成功");
                json=json.getJSONObject( "showapi_res_body");
                json=json.getJSONObject( "pagebean");
                //总数本书
                total_book_num=json.getInt("allNum");
                total_book_page=json.getInt("allPages");
                current_book_page=json.getInt("currentPage");
                Log.i(TAG,"开始解析book");
//                            typeList不为null时清空typeList；
                Log.i(TAG,withOutAppend+"withOutAppend");
                if(withOutAppend) {
                    bookList = new ArrayList<Book>();
                }

                JSONArray jsonArray=json.getJSONArray( "contentlist");
                for(int i=0;i<jsonArray.length();i++){

                    String author=null;
                    String bookId=null;
                    String name=null;
                    String newChapter=null;
                    String size=null;
                    String typeId=null;
                    String typeName=null;
                    String updateTime=null;


                    json= (JSONObject) jsonArray.get(i);
                    if(json.has("author")){
                        author=json.getString("author");
                    }
                    if(json.has("id")){
                        bookId=json.getInt("id")+"";
                    }
                    if(json.has("name")){
                        name=json.getString("name");
                        name=name.replace("下载","");
                    }
                    if(json.has("newChapter")){
                        newChapter=json.getString("newChapter");
                    }
                    if(json.has("size")){
                        size=json.getString("size");
                    }
                    if(json.has("type")){
                        typeId=json.getString("type");
                    }
                    if(json.has("typeName")){
                        typeName=json.getString("typeName");
                    }
                    if(json.has("updateTime")){
                        updateTime=json.getString("updateTime");
                    }
                    bookList.add(new Book(author,bookId, name,  newChapter,size, typeId, typeName, updateTime));
                }
                Log.i(TAG,"JSONObook列表解析成功");
                publishProgress(MODE_GET_BOOK_LIST);
                Log.i(TAG,bookList.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        //获取TypeList算法
        private void refreshTypeList() {
            String res=dbUtilsBookDate.getBookTypeJsonContent();
            if(res==null){
                res = getNetString(TYPE_DONLOAD_URL);
                if(isAutoSaveToLocal&&res!=null){
                    dbUtilsBookDate.insertBookTypeJsonContent(res);
                }
            }

            if(res==null){
                Log.i(TAG,"数据获取失败，本地无缓存且网络未连接");
                publishProgress(MODE_ERROR);
                return;
            }

            //解析字符串
            try {
                JSONObject json=new JSONObject(res);
                if(json.getInt("showapi_res_code")!=0){
                    Log.i(TAG,"showapi_res_error错误:"+json.getString("showapi_res_error"));
                    Log.i(TAG,"refreshTypeList send Error Message:MODE_ERROR");
                    publishProgress(MODE_ERROR);
                    return;
                }
                Log.i(TAG,"JSONO获取成功");
                json=json.getJSONObject( "showapi_res_body");
                if(json.getInt("ret_code")!=0){
                    Log.i(TAG,"错误:JSONObody解析失败");
                    return;
                }
                Log.i(TAG,"JSONObody解析成功，开始解析item");


                if(withOutAppend) {
                    typeList = new ArrayList<BookType>();
                }

                JSONArray jsonArray=json.getJSONArray( "typeList");
                for(int i=0;i<jsonArray.length();i++){

                    String typeName=null;
                    String typeId=null;
                    json= (JSONObject) jsonArray.get(i);
                    if(json.has("id")){
                        typeId=""+json.getInt("id");
                    }
                    if(json.has("name")){
                        typeName=json.getString("name");
                    }

                    typeList.add(new BookType(typeName,typeId));
                }
                Log.i(TAG,"JSONOitem解析成功");
                publishProgress(MODE_GET_TYPE_LIST);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        @Nullable
        private String getNetString(String urlTemp) {
            Log.i(TAG,"getNetString");
            Log.i(TAG,"urlTemp:"+urlTemp);
            //获取字符串
            String res=null;
            URL url=null;
            HttpURLConnection httpURLConnectionU =null;
            InputStream inputStream =null;
            ByteArrayOutputStream baos=null;

            //判断当前网络状态
            ConnectivityManager cm=(ConnectivityManager) getBaseContext().getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo info = cm.getActiveNetworkInfo();
            if(info==null){
                Log.i(TAG,"当前网络不可用");
                return null;
            }
            Log.i(TAG,info.getExtraInfo()+"当前网络"+info.getTypeName());

            try {
                url=new URL(urlTemp);
                httpURLConnectionU = (HttpURLConnection)url.openConnection();
                httpURLConnectionU.setReadTimeout(50000);
                httpURLConnectionU.setConnectTimeout(5*1000);
                httpURLConnectionU.setRequestMethod("GET");
//              解决部分三星机型偶尔联网失败
               httpURLConnectionU.setDoInput(true);

                inputStream = httpURLConnectionU.getInputStream();
                baos=new ByteArrayOutputStream();
                byte[]by=new byte[1024];
                int index=-1;
                while((index=inputStream.read(by))!=-1){
                    baos.write(by,0,index);
                }
                res=baos.toString();
                Log.i(TAG, "baos"+res);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                if(baos!=null){
                    try {
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(inputStream!=null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            return res;
        }
    }



    @Override
    public void onDestroy() {
        runFlag=false;
        dateOperateIBinder=null;
        typeList=null;
        bookList=null;
        chapterList=null;
        content=null;
        if (myAsyncTask!=null&&!myAsyncTask.isCancelled()){
            myAsyncTask.cancel(true) ;
            myAsyncTask=null;
        }
        super.onDestroy();
    }
}
